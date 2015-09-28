package fi.metropolia.yellow_spaceship.androidadvproject.models;

/**
 * Simple wrapper for retrieving the DAM API key with Retrofit/GSON parsing
 */
public class DAMApiKey {
    private String api_key;

    public void setApi_key(String api_key) {
        this.api_key = api_key;
    }

    public String getApi_key() {
        return this.api_key;
    }
}
