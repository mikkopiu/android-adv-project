package fi.metropolia.yellow_spaceship.androidadvproject.managers;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class AlertDialogManager {

    public static void showAlertDialog(Context context, String title, String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();

        alertDialog.setTitle(title);
        alertDialog.setMessage(message);

        String btnTxt = context.getResources().getString(android.R.string.ok);
        alertDialog.setButton(DialogInterface.BUTTON_NEUTRAL, btnTxt, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        alertDialog.show();
    }
}
