package fi.metropolia.yellow_spaceship.androidadvproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import fi.metropolia.yellow_spaceship.androidadvproject.api.ApiClient;
import fi.metropolia.yellow_spaceship.androidadvproject.managers.AlertDialogManager;
import fi.metropolia.yellow_spaceship.androidadvproject.managers.SessionManager;
import fi.metropolia.yellow_spaceship.androidadvproject.models.DAMApiKey;
import fi.metropolia.yellow_spaceship.androidadvproject.models.DAMUser;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText txtUsername;
    private EditText txtPassword;
    private EditText txtCollectionId;

    private SessionManager sessionManager;

    private final static String LOG_TAG = "LoginActivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Create a new session manager
        sessionManager = new SessionManager(getApplicationContext());

        // Username & password inputs
        txtUsername = (EditText) findViewById(R.id.input_username);
        txtPassword = (EditText) findViewById(R.id.input_password);
        txtCollectionId = (EditText) findViewById(R.id.input_collection_id);

        // Login button
        Button btnLogin = (Button) findViewById(R.id.btn_login);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        txtCollectionId.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    login();
                    handled = true;
                }
                return handled;
            }
        });
    }

    private void login() {
        // Get user input
        String username = txtUsername.getText().toString();
        String password = txtPassword.getText().toString();
        final int collectionId;

        if (username.trim().isEmpty() && password.trim().isEmpty()) {
            showLoginFailMsg(getResources().getString(R.string.login_no_name_or_password));
            return;
        }

        try {
            collectionId = Integer.parseInt(txtCollectionId.getText().toString());
        } catch (NumberFormatException e) {
            showLoginFailMsg(getResources().getString(R.string.login_collection_id_not_int));
            return;
        }

        // NOTE: Probably safe to assume collection IDs are always positive integers
        if (collectionId >= 0) {

            DAMUser user = new DAMUser(username, password, collectionId);
            ApiClient.getDAMApiClient().login(user, new Callback<DAMApiKey>() {
                @Override
                public void success(DAMApiKey apiKey, Response response) {
                    String key = apiKey.getApi_key();
                    if (key != null &&
                            !key.equals("Incorrect credentials! Try again.") &&
                            !key.contains(" ")) {

                        sessionManager.createLoginSession(apiKey.getApi_key(), collectionId);

                        // Start the actual application
                        Intent i = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(i);
                        finish();
                    } else {
                        Log.e(
                                LOG_TAG,
                                "Incorrect credentials returned in HTTP 200 OK, key: " +
                                        apiKey.getApi_key()
                        );
                        showLoginFailMsg();
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    Log.e(LOG_TAG, error.getMessage());
                    showLoginFailMsg();
                }
            });

        } else {
            showLoginFailMsg(getResources().getString(R.string.login_collection_id_not_int));
        }
    }

    /**
     * Show alert dialog with a login failure message (default)
     */
    private void showLoginFailMsg() {
        showLoginFailMsg(null);
    }

    /**
     * Show alert dialog with a login failure message, with a custom message
     *
     * @param msg Nullable message
     */
    private void showLoginFailMsg(@Nullable String msg) {
        String message = msg != null ? msg : getResources()
                .getString(R.string.login_name_or_password_incorrect);

        AlertDialogManager.showAlertDialog(
                LoginActivity.this,
                getResources().getString(R.string.login_failed),
                message
        );
    }
}
