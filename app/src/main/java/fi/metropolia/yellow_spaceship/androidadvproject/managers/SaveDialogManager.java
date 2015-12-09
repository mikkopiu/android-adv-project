package fi.metropolia.yellow_spaceship.androidadvproject.managers;

import android.app.Dialog;
import android.content.Context;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.AppCompatSpinner;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import fi.metropolia.yellow_spaceship.androidadvproject.R;
import fi.metropolia.yellow_spaceship.androidadvproject.models.SoundCategory;

/**
 * SaveDialogManager creates a simple save-dialog with a single text input,
 * and optionally one dropdown/spinner.
 * 
 * Interfacing with the dialog is done through SaveDialogListener.
 */
public class SaveDialogManager implements View.OnClickListener {
    private final SaveDialogListener listener;

    private final Dialog dialog;
    private final EditText editText;
    private final TextInputLayout textInputLayout;
    private AppCompatSpinner mDialogSpinner;

    /**
     * Constructor.
     *
     * @param context  Context where the Dialog should be opened in
     * @param title    Title for the Dialog
     * @param adapter  Nullable adapter (if given value, a dropdown is drawn)
     * @param listener Listener for save/cancel-events
     */
    public SaveDialogManager(Context context, String title, ArrayAdapter<CharSequence> adapter,
                             final SaveDialogListener listener) {

        this.listener = listener;

        this.dialog = new Dialog(context);

        // Check for special case save dialog (Recordings dialog)
        if (adapter != null) {
            this.dialog.setContentView(R.layout.recording_save_dialog);
            this.mDialogSpinner = (AppCompatSpinner) this.dialog.findViewById(R.id.spinner_category);
            this.mDialogSpinner.setAdapter(adapter);
        } else {
            this.dialog.setContentView(R.layout.create_save_dialog);
        }

        this.dialog.setTitle(title);

        // Find all necessary elements
        final Button dialogSaveBtn = (Button) dialog.findViewById(R.id.dialog_save_btn);
        Button dialogCancelBtn = (Button) dialog.findViewById(R.id.dialog_cancel_btn);
        this.editText = (EditText) dialog.findViewById(R.id.input_name);
        this.textInputLayout = (TextInputLayout) dialog.findViewById(R.id.layout_input_name);

        // SaveDialogManager will handle the clicks itself, and only pass data through when
        // ready.
        dialogSaveBtn.setOnClickListener(this);
        dialogCancelBtn.setOnClickListener(this);

        // The spinner is after the EditText, so the EditText shouldn't have IME action DONE
        if (this.mDialogSpinner == null) {
            // React to IME actions the same way as on Save-button clicks
            this.editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    boolean handled = false;
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        onClick(dialogSaveBtn);
                        handled = true;
                    }
                    return handled;
                }
            });
        }

        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    /**
     * Show the Dialog
     */
    public void show() {
        this.dialog.show();
    }

    /**
     * Hide the dialog
     */
    public void dismiss() {
        this.dialog.dismiss();
    }

    /**
     * Set a maximum string length for the text input
     *
     * @param maxLength Maximum amount of characters in text input (will be validated)
     */
    public void setCounterMaxLength(int maxLength) {
        this.textInputLayout.setCounterMaxLength(maxLength);
    }

    /**
     * Set custom error message to text input (in case some additional validations failed)
     *
     * @param str Error message
     */
    public void setTextInputLayoutError(String str) {
        this.textInputLayout.setError(str);
    }

    /**
     * Set a predefined value to the text input (e.g. pre-filled project names)
     *
     * @param str Predefined text value
     */
    public void setEditTextText(String str) {
        this.editText.setText(str);
    }

    /**
     * Click-handling for Dialog's buttons
     *
     * @param v Button view
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialog_cancel_btn:
                // Nothing needs to be done on cancel, the listener will handle dismissal
                // on its own.
                this.listener.onDialogCancel();
                break;
            case R.id.dialog_save_btn:

                // Do some simple validation
                String str = this.editText.getText().toString();
                if (str.trim().equals("")) {
                    this.editText.setError("Name is required");
                    break;
                } else if (str.length() > this.textInputLayout.getCounterMaxLength()) {
                    this.editText.setError("Name is too long");
                    break;
                }

                // Only attempt to get the selected category if the dropdown actually
                // exists.
                SoundCategory category = null;
                if (this.mDialogSpinner != null) {
                    category = SoundCategory.fromApi(
                            this.mDialogSpinner.getSelectedItem().toString().toLowerCase()
                    );
                }

                // Only pass through the values, the listener should handle the dismissal
                this.listener.onDialogSave(str, category);
                break;
            default:
                break;
        }
    }
}
