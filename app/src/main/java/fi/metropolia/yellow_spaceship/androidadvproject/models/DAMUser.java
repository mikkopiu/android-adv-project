package fi.metropolia.yellow_spaceship.androidadvproject.models;

/**
 * Simple user class for logging into the DAM.
 * Not meant to store user credentials, just for retrofit's @Body.
 */
public class DAMUser {
    private final String username;
    private final String password;
    private final int collectionId;

    public DAMUser(String username, String password, int collectionId) {
        this.username = username;
        this.password = password;
        this.collectionId = collectionId;
    }
}
