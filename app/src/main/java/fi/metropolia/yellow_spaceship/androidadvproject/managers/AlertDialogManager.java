package fi.metropolia.yellow_spaceship.androidadvproject.managers;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

/**
 * Unified manager for creating simple AlertDialogs
 */
public class AlertDialogManager {

    /**
     * Create and show a simple AlertDialog with a title & message
     *
     * @param context The context which this Dialog should be opened in
     * @param title   Title for the Dialog
     * @param message A short message for the Dialog
     */
    public static void showAlertDialog(Context context, String title, String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();

        alertDialog.setTitle(title);
        alertDialog.setMessage(message);

        String btnTxt = context.getResources().getString(android.R.string.ok);

        // There's no reason to handle any clicks, so we create an empty handler
        alertDialog.setButton(DialogInterface.BUTTON_NEUTRAL, btnTxt, clickListener);

        alertDialog.show();
    }

    /**
     * No need to re-create an empty listener every time, so make it static
     */
    private final static DialogInterface.OnClickListener clickListener =
            new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                }
            };
}
