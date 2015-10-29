package fi.metropolia.yellow_spaceship.androidadvproject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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

public class LoginActivity extends Activity {

    private EditText txtUsername;
    private EditText txtPassword;

    private SessionManager sessionManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Create a new session manager
        sessionManager = new SessionManager(getApplicationContext());

        // Username & password inputs
        txtUsername = (EditText) findViewById(R.id.input_username);
        txtPassword = (EditText) findViewById(R.id.input_password);

        // Login button
        Button btnLogin = (Button) findViewById(R.id.btn_login);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        txtPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
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

        if (username.trim().length() > 0 && password.trim().length() > 0) {

            DAMUser user = new DAMUser(username, password);
            ApiClient.getDAMApiClient().login(user, new Callback<DAMApiKey>() {
                @Override
                public void success(DAMApiKey apiKey, Response response) {
                    System.out.println("Fetched api key: " + apiKey.getApi_key());
                    sessionManager.createLoginSession(apiKey.getApi_key());

                    // Start the actual application
                    Intent i = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(i);
                    finish();
                }

                @Override
                public void failure(RetrofitError error) {
                    System.out.println(error.getMessage());
//                            error.printStackTrace();
                    // TODO: check actual error codes
                    AlertDialogManager.showAlertDialog(LoginActivity.this, "Login failed", "Username or password incorrect");
                }
            });

        } else {
            AlertDialogManager.showAlertDialog(LoginActivity.this, "Login failed", "Please enter a username and a password");
        }
    }
}
