package com.android.mp.wastickermaster;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.android.mp.wastickermaster.StickerPackListActivity.NO_INTERNET_CONNCTION_MESSAGE;
import static com.android.mp.wastickermaster.StickerPackListActivity.NO_INTERNET_CONNECTION;

public class StickerActivity extends AppCompatActivity {


    private GridView stickerGrid;
    private TextView pack_name;
    private TextView publishertv;
    private TextView noOfStickers;
    private ImageView trayimage;
    private FrameLayout addtowhatsapp;
    private View alreadyAddedText;

    private LaunchPlayStoreApp launchPlayStoreApp;
    private Dialog dialog = new Dialog(StickerActivity.this);
    private Connection connection = new Connection(StickerActivity.this);

    private StickerPack stickerPack;
    private List<Sticker> stickersList;
    private StickerDownloadTask stickerDownloadTask;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sticker);

        pack_name = (TextView) findViewById(R.id.sticker_pack_name);
        publishertv = (TextView) findViewById(R.id.spublisher);
        trayimage = (ImageView) findViewById(R.id.tray_image);
        noOfStickers = (TextView) findViewById(R.id.no_of_sticker_in_pack);
        stickerGrid = (GridView) findViewById(R.id.stickerList_gridview);
        addtowhatsapp = (FrameLayout) findViewById(R.id.add_to_whatsapp_button);
        alreadyAddedText = (View) findViewById(R.id.already_added_text);
        stickersList = new ArrayList<>();

        stickerPack = getIntent().getParcelableExtra("stickerPack");

        showStickers();

    }

    private void showStickers() {
        if (!connection.connectionCheck.isConnected()) {
            Toast.makeText(StickerActivity.this, NO_INTERNET_CONNECTION, Toast.LENGTH_SHORT).show();
            displaypackInfo();
            showStickerList();
        } else {
            displaypackInfo();
            showStickerList();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!connection.connectionCheck.isConnected())
            Toast.makeText(StickerActivity.this, NO_INTERNET_CONNECTION, Toast.LENGTH_SHORT).show();
        displaypackInfo();
        showStickerList();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setAddButtonAppearance(stickerPack);
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
                if (!connection.connectionCheck.isConnected()) {
                    dialog.DialogShow(NO_INTERNET_CONNECTION, NO_INTERNET_CONNCTION_MESSAGE).show();
                } else {
                    displaypackInfo();
                    showStickerList();
                }
                return true;
            case R.id.rate_us:
                launchPlayStoreApp = new LaunchPlayStoreApp(StickerActivity.this);
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

    private void showStickerList() {
        stickersList = stickerPack.getStickers();
        StickerPreviewAdapter stickerPreviewAdapter = new StickerPreviewAdapter(StickerActivity.this, stickersList);
        stickerGrid.setAdapter(stickerPreviewAdapter);
    }

    private void displaypackInfo() {
        pack_name.setText(stickerPack.getName());
        publishertv.setText(stickerPack.getPublisher());
        noOfStickers.setText(stickerPack.getNoOfSticker() + " Stickers");
        GlideApp
                .with(StickerActivity.this)
                .load(stickerPack.tray_image_file)
                .placeholder(R.drawable.image_before_stickerload)
                .error(R.drawable.stickerload_error)
                .dontAnimate()
                .into(trayimage);
    }


    private void setAddButtonAppearance(StickerPack pack) {
        if (pack.isWhitelisted) {
            addtowhatsapp.setVisibility(View.GONE);
            alreadyAddedText.setVisibility(View.VISIBLE);
        } else {
            addtowhatsapp.setVisibility(View.VISIBLE);
            onAddButtonClicked(addtowhatsapp, pack);
        }
    }

    private void onAddButtonClicked(View button, StickerPack stickerPack) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Permissions permissions = new Permissions(StickerActivity.this);
                if (!permissions.storagePermission.checkStoragePermission()) {
                    permissions.storagePermission.requestStoragePermission();
                } else {
                    if (!connection.connectionCheck.isConnected()) {
                        Dialog dialog = new Dialog(StickerActivity.this);
                        dialog.DialogShow(NO_INTERNET_CONNECTION, NO_INTERNET_CONNCTION_MESSAGE).show();
                    } else {
                        stickerDownloadTask = new StickerDownloadTask(stickerPack, StickerActivity.this);
                        stickerDownloadTask.download();
                    }
                }
            }
        });
    }
}
