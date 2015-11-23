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

public class SaveDialogManager implements View.OnClickListener {
    private SaveDialogListener listener;

    private Dialog dialog;
    private EditText editText;
    private TextInputLayout textInputLayout;
    private AppCompatSpinner mDialogSpinner;

    public SaveDialogManager(Context context, String title, ArrayAdapter<CharSequence> adapter, final SaveDialogListener listener) {
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

        final Button dialogSaveBtn = (Button) dialog.findViewById(R.id.dialog_save_btn);
        Button dialogCancelBtn = (Button) dialog.findViewById(R.id.dialog_cancel_btn);
        this.editText = (EditText) dialog.findViewById(R.id.input_name);
        this.textInputLayout = (TextInputLayout) dialog.findViewById(R.id.layout_input_name);

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

    public void show() {
        this.dialog.show();
    }

    public void dismiss() {
        this.dialog.dismiss();
    }

    public void setCounterMaxLength(int maxLength) {
        this.textInputLayout.setCounterMaxLength(maxLength);
    }

    public void setTextInputLayoutError(String str) {
        this.textInputLayout.setError(str);
    }

    public void setEditTextText(String str) {
        this.editText.setText(str);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialog_cancel_btn:
                this.listener.onDialogCancel();
                break;
            case R.id.dialog_save_btn:
                String str = this.editText.getText().toString();
                if (str.trim().equals("")) {
                    this.editText.setError("Name is required");
                    break;
                } else if (str.length() > this.textInputLayout.getCounterMaxLength()) {
                    this.editText.setError("Name is too long");
                    break;
                }

                SoundCategory category = null;
                if (this.mDialogSpinner != null) {
                    category = SoundCategory.fromApi(
                            this.mDialogSpinner.getSelectedItem().toString().toLowerCase()
                    );
                }

                this.listener.onDialogSave(str, category);
                break;
            default:
                break;
        }
    }
}
