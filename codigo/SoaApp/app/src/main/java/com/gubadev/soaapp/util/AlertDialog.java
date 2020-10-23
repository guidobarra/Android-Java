package com.gubadev.soaapp.util;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;

public class AlertDialog {

    public static void displayAlertDialog(Activity activity, String title, String description, String labelButton) {
        androidx.appcompat.app.AlertDialog alertDialog = new androidx.appcompat.app.AlertDialog.Builder(activity).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(description);
        alertDialog.setButton(androidx.appcompat.app.AlertDialog.BUTTON_NEUTRAL, labelButton,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }


    public static void displayAlertDialogGame(final Activity activity, String title, String description, String labelButton) {
        androidx.appcompat.app.AlertDialog alertDialog = new androidx.appcompat.app.AlertDialog.Builder(activity).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(description);
        alertDialog.setButton(androidx.appcompat.app.AlertDialog.BUTTON_NEUTRAL, labelButton,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        activity.finish();
                    }
                });
        alertDialog.show();
    }

}