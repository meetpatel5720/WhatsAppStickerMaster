package com.android.mp.wastickermaster;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;

public class Connection {
    private Activity context;
    StickerPackListActivity.ConnectionCheck connectionCheck = new StickerPackListActivity.ConnectionCheck() {
        @Override
        public boolean isConnected() {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netinfo = cm.getActiveNetworkInfo();
            if (netinfo != null && netinfo.isConnectedOrConnecting()) {
                NetworkInfo wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                NetworkInfo mobile = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
                if ((mobile != null && mobile.isConnectedOrConnecting()) || (wifi != null && wifi.isConnectedOrConnecting()))
                    return true;
                else return false;
            } else
                return false;
        }
    };

    public Connection(Activity context) {
        this.context = context;
    }
}

