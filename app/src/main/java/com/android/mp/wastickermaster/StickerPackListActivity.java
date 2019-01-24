package com.android.mp.wastickermaster;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class StickerPackListActivity extends AppCompatActivity {

    private static final int STORAGE_PERMISSION_CODE = 1;
    public static String NO_INTERNET_CONNECTION = "No internet connection";
    public static String SLOW_INTERNET_CONNECTION = "Slow internet connection";
    public static String NO_INTERNET_CONNCTION_MESSAGE = "Please turn on mobile data or wifi.";
    public static String SLOW_INTERNET_CONNECTION_MESSAGE = "It's takes too much time in loading.";
    final long PROGRESS_DIALOG_SHOW_TIME = 100000;
    private RecyclerView stickerPackListView;
    private DatabaseReference databasePack;
    private ProgressDialog packListLoader;
    private StickerListAdapter packAdapter;
    private List<StickerPack> stickerPackList;
    private LaunchPlayStoreApp launchPlayStoreApp;
    private AdView adView;

    private Dialog dialog = new Dialog(StickerPackListActivity.this);
    private Connection connection = new Connection(StickerPackListActivity.this);
    private WhiteListCheckAsyncTask whiteListCheckAsyncTask;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_sticker_pack_list);
        MobileAds.initialize(this, "ca-app-pub-3717587344238324~6384148633");
        Objects.requireNonNull(getSupportActionBar()).setTitle(getApplicationContext().getString(R.string.sticker_packs));

        adView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        databasePack = FirebaseDatabase.getInstance().getReference("sticker_packs");
        databasePack.keepSynced(true);
        stickerPackListView = (RecyclerView) findViewById(R.id.pack_list);
        stickerPackListView.setLayoutManager(new LinearLayoutManager(this));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        stickerPackListView.addItemDecoration(dividerItemDecoration);

        stickerPackList = new ArrayList<>();
        packListLoader = new ProgressDialog(this, R.style.PackLoaderDialog);
        packListLoader.setMessage(getString(R.string.loading));
        packListLoader.setCancelable(false);


        showPacks();
        showMessage();
    }

    private void showPacks() {
        if (!connection.connectionCheck.isConnected()) {
            Toast.makeText(StickerPackListActivity.this, NO_INTERNET_CONNECTION, Toast.LENGTH_SHORT).show();
            showPackList();
        } else {
            showProgressDialog();
            showPackList();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!connection.connectionCheck.isConnected())
            Toast.makeText(StickerPackListActivity.this, NO_INTERNET_CONNECTION, Toast.LENGTH_SHORT).show();
        Log.d("Activity", "Start");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("Activity", "Resumed");
        if (stickerPackList.size() != 0) {
            whiteListCheckAsyncTask = new WhiteListCheckAsyncTask(this);
            whiteListCheckAsyncTask.execute(stickerPackList.toArray(new StickerPack[0]));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("Activity", "Paused");
        if (whiteListCheckAsyncTask != null && !whiteListCheckAsyncTask.isCancelled()) {
            whiteListCheckAsyncTask.cancel(true);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("Activity", "Stop");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.tool_bar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refresh:
                if (!connection.connectionCheck.isConnected())
                    dialog.DialogShow(NO_INTERNET_CONNECTION, NO_INTERNET_CONNCTION_MESSAGE).show();
                else {
                    showProgressDialog();
                    showPackList();
                }
                return true;
            case R.id.rate_us:
                launchPlayStoreApp = new LaunchPlayStoreApp(StickerPackListActivity.this);
                launchPlayStoreApp.launchPlayStore.launchPlayStore();
                return true;
            case R.id.about:
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://sites.google.com/view/wastickermaster-about"));
                startActivity(intent);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    protected void showPackList() {
        databasePack.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                stickerPackList.clear();
                packListLoader.dismiss();
                for (DataSnapshot packSnapshot : dataSnapshot.getChildren()) {
                    int noOfStickers = (int) packSnapshot.child("stickers").getChildrenCount();
                    StickerPack stickerPack = packSnapshot.getValue(StickerPack.class);
                    Objects.requireNonNull(stickerPack).setNoOfSticker(noOfStickers);
                    stickerPackList.add(stickerPack);
                }
                packAdapter = new StickerListAdapter(StickerPackListActivity.this, stickerPackList);
                stickerPackListView.setAdapter(packAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(StickerPackListActivity.this, R.string.somthing_wrong, Toast.LENGTH_LONG).show();
            }
        });
    }

    public void showProgressDialog() {
        packListLoader.show();
        Runnable progressRunnable = new Runnable() {
            @Override
            public void run() {
                if (packListLoader.isShowing()) {
                    packListLoader.dismiss();
                    dialog.DialogShow(SLOW_INTERNET_CONNECTION, SLOW_INTERNET_CONNECTION_MESSAGE).show();
                }
            }
        };
        Handler packListLoaderCanceller = new Handler();
        packListLoaderCanceller.postDelayed(progressRunnable, PROGRESS_DIALOG_SHOW_TIME);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, R.string.permission_granted, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, R.string.permission_denided, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showMessage() {
        DatabaseReference messageRef = FirebaseDatabase.getInstance().getReference("message");
        messageRef.keepSynced(true);
        messageRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Message message = snapshot.getValue(Message.class);
                    setMessageLayout(Objects.requireNonNull(message));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void setMessageLayout(Message message) {
        if (!message.content.equals("no message")) {
            AlertDialog.Builder dialogue = new AlertDialog.Builder(StickerPackListActivity.this, R.style.MessageAlertDialog);
            dialogue.setTitle(message.title);
            dialogue.setMessage(message.content);
            dialogue.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    launchPlayStoreApp = new LaunchPlayStoreApp(StickerPackListActivity.this);
                    launchPlayStoreApp.launchPlayStore.launchPlayStore();
                }
            });
            dialogue.setNegativeButton("Update later", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int i) {
                    dialog.dismiss();
                }
            });
            dialogue.show();
        }
    }

    public interface ConnectionCheck {
        boolean isConnected();
    }

    public interface StoragePermission {
        void requestStoragePermission();

        boolean checkStoragePermission();
    }

    public interface LaunchPlayStore {
        void launchPlayStore();
    }

    public interface AddToWhatsApp {
        void addStickerPackToWhatsApp(String identifier, String stickerPackName);
    }

    static class WhiteListCheckAsyncTask extends AsyncTask<StickerPack, Void, List<StickerPack>> {
        private final WeakReference<StickerPackListActivity> stickerPackListActivityWeakReference;

        WhiteListCheckAsyncTask(StickerPackListActivity stickerPackListActivity) {
            this.stickerPackListActivityWeakReference = new WeakReference<>(stickerPackListActivity);
        }

        @Override
        protected final List<StickerPack> doInBackground(StickerPack... stickerPackArray) {
            final StickerPackListActivity stickerPackListActivity = stickerPackListActivityWeakReference.get();
            if (stickerPackListActivity == null) {
                return Arrays.asList(stickerPackArray);
            }
            for (StickerPack stickerPack : stickerPackArray) {
                stickerPack.setIsWhitelisted(WhitelistCheck.isWhitelisted(stickerPackListActivity, stickerPack.identifier));
            }
            return Arrays.asList(stickerPackArray);
        }

        @Override
        protected void onPostExecute(List<StickerPack> stickerPackList) {
            final StickerPackListActivity stickerPackListActivity = stickerPackListActivityWeakReference.get();
            if (stickerPackListActivity != null) {
                stickerPackListActivity.packAdapter.setStickerPackList(stickerPackList);
                stickerPackListActivity.packAdapter.notifyDataSetChanged();
            }
        }
    }

}


