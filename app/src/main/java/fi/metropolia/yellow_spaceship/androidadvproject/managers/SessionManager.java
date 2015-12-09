package fi.metropolia.yellow_spaceship.androidadvproject.managers;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;

import fi.metropolia.yellow_spaceship.androidadvproject.LoginActivity;

/**
 * SessionManager is used to store and manage DAM API key/login
 */
public class SessionManager {

    private final SharedPreferences preferences;
    private final SharedPreferences.Editor editor;

    private final Context mContext;

    // Preference's file name
    private static final String PREF_NAME = "DamApiLoginPref";

    private static final String IS_LOGGED_IN = "userLoggedIn";
    private static final String API_KEY = "damApiKey";
    private static final String COLLECTION_ID = "collectionId";

    public SessionManager(Context context) {
        this.mContext = context;
        preferences = mContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = preferences.edit();
    }

    /**
     * Get the current API key
     *
     * @return ?String API key for DAM
     */
    public String getApiKey() {
        return preferences.getString(API_KEY, null);
    }

    /**
     * Create a new login session
     *
     * @param apiKey       New API key for DAM
     * @param collectionId The predefined collection ID for this user
     */
    public void createLoginSession(String apiKey, int collectionId) {
        editor.putBoolean(IS_LOGGED_IN, true); // True for logged in
        editor.putString(API_KEY, apiKey);
        editor.putInt(COLLECTION_ID, collectionId);
        editor.apply();
    }

    /**
     * Logout the user.
     * Clears the current API key and login status from SharedPreferences
     */
    public void logoutUser() {
        editor.clear();
        editor.apply();

        // After logout redirect user to Login Activity
        Intent i = new Intent(mContext, LoginActivity.class);
        // Closing all the Activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

        // Staring Login Activity
        mContext.startActivity(i);
        ((AppCompatActivity)this.mContext).finish();
    }

    /**
     * Public method for checking login status and opening the LoginActivity in case of
     * a logged out session.
     */
    public void checkLogin() {
        // Check login status
        if (!this.isLoggedIn()) {
            // user is not logged in redirect him to Login Activity
            Intent i = new Intent(mContext, LoginActivity.class);

            // Closing all the Activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

            // Staring Login Activity
            mContext.startActivity(i);
            ((AppCompatActivity)this.mContext).finish();
        }

        // Invalidate old SessionManager versions
        if (preferences.getInt(COLLECTION_ID, -1) == -1) {
            logoutUser();
        }
    }

    /**
     * Get the collection ID setting for this login-session
     *
     * @return Current collection ID
     */
    public int getCollectionID() {
        return preferences.getInt(COLLECTION_ID, -1);
    }

    /**
     * Quick check for login status
     *
     * @return boolean Is user logged in
     */
    private boolean isLoggedIn() {
        return preferences.getBoolean(IS_LOGGED_IN, false);
    }
}
