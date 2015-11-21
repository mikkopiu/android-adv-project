package fi.metropolia.yellow_spaceship.androidadvproject.api;


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
        void login(@Body DAMUser user,
                   Callback<DAMApiKey> callback);

        /**
         * Get a list of sounds matching given parameters.
         * Set parameter as null to ignore.
         * <p/>
         * String.valueOf(Object) is called for all Query objects, so adding a toString-method
         * to a custom class/enum should be enough.
         *
         * @param format     File format
         * @param size       File size, e.g. "<50KB"
         * @param collection Collection ID
         * @param category   SoundCategory
         * @param tag        Tag of the sound, e.g. "dog"
         * @param link       Show download link?
         * @param search     Search any metadata field for string
         * @param soundType  SoundType
         * @param createdBy  User that created the sound
         * @param callback   Callback for parsing
         */
        @GET("/api_audio_search/index.php/")
        void getSoundsWithParams(@Query("key") String apiKey,
                                 @Query("format") String format,
                                 @Query("size") String size,
                                 @Query("collection") Integer collection,
                                 @Query("category") SoundCategory category,
                                 @Query("tag") String tag,
                                 @Query("link") boolean link,
                                 @Query("search") String search,
                                 @Query("sound_type") SoundType soundType,
                                 @Query("resource_created_by") String createdBy,
                                 Callback<List<List<DAMSound>>> callback);

        /**
         * Simplified method for getting all sounds in a single collection
         *
         * @param collection Collection ID
         * @param link       Show download link?
         * @param callback   Callback for parsing
         */
        @GET("/api_audio_search/index.php/")
        void getCollection(@Query("key") String apiKey,
                           @Query("collection") int collection,
                           @Query("link") boolean link,
                           Callback<List<List<DAMSound>>> callback);

        /**
         * Simplified method for getting all sounds for a single category
         *
         * @param category SoundCategory
         * @param link     Show download link?
         * @param callback Callback for parsing
         */
        @GET("/api_audio_search/index.php/")
        void getCategory(@Query("key") String apiKey,
                         @Query("category") SoundCategory category,
                         @Query("format") String format,
                         @Query("link") boolean link,
                         Callback<List<List<DAMSound>>> callback);

        /**
         * Simplified method for getting all sounds of a single SoundType
         *
         * @param soundType SoundType
         * @param link      Show download link?
         * @param callback  Callback for parsing
         */
        @GET("/api_audio_search/index.php/")
        void getType(@Query("key") String apiKey,
                     @Query("sound_type") SoundType soundType,
                     @Query("link") boolean link,
                     Callback<List<List<DAMSound>>> callback);

        @Multipart
        @POST("/api_upload/?resourcetype=4")
        void uploadSound(@Query("key") String apiKey,
                         @Query("collection") int collectionId,
                         @Query("field8") String title,
                         @Query("field73") String description,
                         @Query("field74") String tags,
                         @Query("field75") SoundCategory category,
                         @Query("field76") SoundType soundType,
                         @Query("field78") int lengthInSecs,
                         @Part("filename") String filename,
                         @Part("userfile") TypedFile file,
                         Callback<Object> callback);

        /**
         * Make a free text search on all available metadata fields
         *
         * @param apiKey   DAM API key
         * @param search   Free text search query
         * @param link     Show download links?
         * @param callback Callback for parsing
         */
        @GET("/api_audio_search/index.php/")
        void getTextSearchResults(@Query("key") String apiKey,
                                  @Query("search") String search,
                                  @Query("format") String format,
                                  @Query("link") boolean link,
                                  Callback<List<List<DAMSound>>> callback);
    }
}
