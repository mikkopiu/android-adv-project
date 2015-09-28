package fi.metropolia.yellow_spaceship.androidadvproject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

/**
 * SessionManager is used to store and manage DAM API key/login
 */
public class SessionManager {

    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    Context mContext;

    // Preference's file name
    private static final String PREF_NAME = "DamApiLoginPref";

    private static final String IS_LOGGED_IN = "userLoggedIn";
    private static final String API_KEY = "damApiKey";

    public SessionManager(Context context) {
        this.mContext = context;
        preferences = mContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = preferences.edit();
    }

    /**
     * Get the current API key
     * @return ?String API key for DAM
     */
    public String getApiKey() {
        return preferences.getString(API_KEY, null);
    }

    /**
     * Create a new login session
     * @param apiKey New API key for DAM
     */
    public void createLoginSession(String apiKey) {
        editor.putBoolean(IS_LOGGED_IN, true); // True for logged in
        editor.putString(API_KEY, apiKey);
        editor.apply();
    }

    /**
     * Logout the user.
     * Clears the current API key and login status from SharedPreferences
     */
    public void logoutUser() {
        editor.clear();
        editor.apply();

        // TODO: Start login activity here?
        // After logout redirect user to Loing Activity
        Intent i = new Intent(mContext, LoginActivity.class);
        // Closing all the Activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Add new Flag to start new Activity
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Staring Login Activity
        mContext.startActivity(i);
    }

    // TODO: If using activity, create something like this?
    public void checkLogin(){
        // Check login status
        if(!this.isLoggedIn()){
            // user is not logged in redirect him to Login Activity
            Intent i = new Intent(mContext, LoginActivity.class);
            // Closing all the Activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // Staring Login Activity
            mContext.startActivity(i);
        }

    }

    /**
     * Quick check for login status
     * @return boolean Is user logged in
     */
    public boolean isLoggedIn() {
        return preferences.getBoolean(IS_LOGGED_IN, false);
    }
}
