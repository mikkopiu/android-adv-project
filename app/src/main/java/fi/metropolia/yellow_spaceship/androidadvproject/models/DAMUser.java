package fi.metropolia.yellow_spaceship.androidadvproject.models;

/**
 * Simple user class for logging into the DAM.
 * Not meant to store user credentials, just for retrofit's @Body.
 */
public class DAMUser {
    private String username;
    private String password;

    public DAMUser() {}
    public DAMUser(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
