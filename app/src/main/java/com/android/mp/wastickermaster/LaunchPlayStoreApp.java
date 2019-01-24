package com.android.mp.wastickermaster;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

public class LaunchPlayStoreApp {
    protected Activity context;

    public LaunchPlayStoreApp(Activity context) {
        this.context = context;
    }
    StickerPackListActivity.LaunchPlayStore launchPlayStore = new StickerPackListActivity.LaunchPlayStore() {
        @Override
        public void launchPlayStore() {
            String uriString = "http://play.google.com/store/apps/details?id=com.android.mp.wastickermaster";
            launchPlayStoreWithUri(uriString);
        }
    };
    private void launchPlayStoreWithUri(String uriString) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(uriString));
        intent.setPackage("com.android.vending");
        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, R.string.cannot_find_play_store, Toast.LENGTH_LONG).show();
        }
    }
}
