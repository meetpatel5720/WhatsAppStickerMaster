package com.android.mp.wastickermaster;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

public class Dialog {
    private Activity context;

    public Dialog(Activity context) {
        this.context = context;
    }

    public AlertDialog.Builder DialogShow(String title,String message) {

        AlertDialog.Builder dialogbuilder = new AlertDialog.Builder(context,R.style.MessageAlertDialog);
        dialogbuilder.setTitle(title);
        dialogbuilder.setMessage(message);
        dialogbuilder.setPositiveButton(R.string.okText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        return dialogbuilder;
    }
}
