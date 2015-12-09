package fi.metropolia.yellow_spaceship.androidadvproject.api;


import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Date;
import java.util.List;

import fi.metropolia.yellow_spaceship.androidadvproject.BuildConfig;
import fi.metropolia.yellow_spaceship.androidadvproject.api.deserializers.DAMSoundDeserializer;
import fi.metropolia.yellow_spaceship.androidadvproject.api.deserializers.DateDeserializer;
import fi.metropolia.yellow_spaceship.androidadvproject.api.deserializers.IntegerDeserializer;
import fi.metropolia.yellow_spaceship.androidadvproject.api.deserializers.SoundCategoryDeserializer;
import fi.metropolia.yellow_spaceship.androidadvproject.api.deserializers.SoundTypeDeserializer;
import fi.metropolia.yellow_spaceship.androidadvproject.models.DAMApiKey;
import fi.metropolia.yellow_spaceship.androidadvproject.models.DAMSound;
import fi.metropolia.yellow_spaceship.androidadvproject.models.DAMUser;
import fi.metropolia.yellow_spaceship.androidadvproject.models.SoundCategory;
import fi.metropolia.yellow_spaceship.androidadvproject.models.SoundType;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.http.Query;
import retrofit.mime.TypedFile;

/**
 * The API client for accessing the DAM.
 */
public class ApiClient {
    private static DAMApiInterface sDAMService;
    private static final String BASE_URL = BuildConfig.API_URL;

    public static DAMApiInterface getDAMApiClient() {
        if (sDAMService == null) {
            // Create a new RestAdapter instance to create a base for API calls
            RestAdapter restAdapter = new RestAdapter.Builder()
                    .setConverter(getConverter())
                    .setEndpoint(BASE_URL)
                    .build();

            sDAMService = restAdapter.create(DAMApiInterface.class);
        }

        return sDAMService;
    }

    private static GsonConverter getConverter() {
        // Create the builder responsible for creating the GSON used in parsing our response
        GsonBuilder builder = new GsonBuilder();

        // Register our Deserializer classes as type adapters for their classes
        // This will instruct retrofit to deserialize the JSON into the correct format
        builder.registerTypeAdapter(Date.class, new DateDeserializer());
        builder.registerTypeAdapter(SoundCategory.class, new SoundCategoryDeserializer());
        builder.registerTypeAdapter(SoundType.class, new SoundTypeDeserializer());
        builder.registerTypeAdapter(Integer.class, new IntegerDeserializer());
        builder.registerTypeAdapter(DAMSound.class, new DAMSoundDeserializer());

        Gson gson = builder.create();

        return new GsonConverter(gson);
    }

    /**
     * Interface for all necessary API calls.
     * Data parsing is done by defining a data class as the Callback.
     */
    public interface DAMApiInterface {

        /**
         * Login & get an API key to use for other API calls
         *
         * @param user     Temporary DAMUser that has the user's username and password
         * @param callback Callback for parsing
         */
        @POST("/api_auth/auth.php")
        void login(@NonNull @Body DAMUser user,
                   Callback<DAMApiKey> callback);

        /**
         * Simplified method for getting all sounds for a single category
         *
         * @param apiKey       API key
         * @param collectionId Current collection ID
         * @param category     SoundCategory
         * @param format       File format
         * @param link         Show download link?
         * @param callback     Callback for parsing
         */
        @GET("/api_audio_search/index.php/")
        void getCategory(@NonNull @Query("key") String apiKey,
                         @NonNull @Query("collection") Integer collectionId,
                         @Query("category") SoundCategory category,
                         @Query("format") String format,
                         @Query("link") boolean link,
                         Callback<List<List<DAMSound>>> callback);

        /**
         * Make a free text search on all available metadata fields
         *
         * @param apiKey   DAM API key
         * @param search   Free text search query
         * @param format   File format
         * @param link     Show download links?
         * @param callback Callback for parsing
         */
        @GET("/api_audio_search/index.php/")
        void getTextSearchResults(@NonNull @Query("key") String apiKey,
                                  @NonNull @Query("collection") Integer collectionId,
                                  @Query("search") String search,
                                  @Query("format") String format,
                                  @Query("link") boolean link,
                                  Callback<List<List<DAMSound>>> callback);

        /**
         * Upload a sound to the DAM
         *
         * @param apiKey       API key
         * @param collectionId Current collection ID
         * @param title        Title of the sound
         * @param description  Description of the sound
         * @param category     Category of the sound
         * @param soundType    Type of the sound
         * @param lengthInSecs Length in full seconds
         * @param file         Actual sound file
         * @param callback     Callback for responses
         */
        @Multipart
        @POST("/api_upload/?resourcetype=4")
        void uploadSound(@NonNull @Query("key") String apiKey,
                         @NonNull @Query("collection") Integer collectionId,
                         @Query("field8") String title,
                         @Query("field73") String description,
                         @Query("field75") SoundCategory category,
                         @Query("field76") SoundType soundType,
                         @Query("field79") int lengthInSecs,
                         @NonNull @Part("userfile") TypedFile file,
                         Callback<Object> callback);
    }
}
