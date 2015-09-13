package fi.metropolia.yellow_spaceship.androidadvproject.api;


import java.util.List;

import fi.metropolia.yellow_spaceship.androidadvproject.models.DAMSound;
import fi.metropolia.yellow_spaceship.androidadvproject.models.DAMUser;
import fi.metropolia.yellow_spaceship.androidadvproject.models.SoundCategory;
import fi.metropolia.yellow_spaceship.androidadvproject.models.SoundType;
import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;

/**
 * The API client for accessing the DAM.
 */
public class ApiClient {
    private static DAMApiInterface sDAMService;
    private static final String BASE_URL = "http://dev.mw.metropolia.fi/dianag/AudioResourceSpace/plugins";

    public static DAMApiInterface getDAMApiClient() {
        if (sDAMService == null) {
            // Create a new RestAdapter instance to create a base for API calls
            // TODO: How to manage the API key? Should ApiClient have a constructor? How to skip in case of login?
            RestAdapter restAdapter = new RestAdapter.Builder()
                    .setEndpoint(BASE_URL)
//                    .setRequestInterceptor(new RequestInterceptor() {
//                        @Override
//                        public void intercept(RequestFacade request) {
//                            request.addQueryParam("key", "API_KEY_HERE");
//                        }
//                    })
                    .build();

            sDAMService = restAdapter.create(DAMApiInterface.class);
        }

        return sDAMService;
    }

    /**
     * Interface for all necessary API calls.
     * Data parsing is done by defining a data class as the Callback.
     */
    public interface DAMApiInterface {

        /**
         * Login & get an API key to use for other API calls
         * @param user Temporary DAMUser that has the user's username and password
         * @param callback Callback for parsing
         */
        @POST("/api_auth/auth.php")
        void login(@Body DAMUser user,
                   Callback<String> callback);

        /**
         * Get a list of sounds matching given parameters.
         * Set parameter as null to ignore.
         *
         * TODO: Check if .toString() is called on values, i.e. can we use SoundType or does it need to be a String?
         *
         * @param format File format
         * @param size File size, e.g. "<50KB"
         * @param collection Collection ID
         * @param category SoundCategory
         * @param tag Tag of the sound, e.g. "dog"
         * @param link Show download link?
         * @param search Search any metadata field for string
         * @param soundType SoundType
         * @param createdBy User that created the sound
         * @param callback Callback for parsing
         */
        @GET("/api_audio_search/index.php/")
        void getSoundsWithParams(@Query("format") String format,
                                 @Query("size") String size,
                                 @Query("collection") int collection,
                                 @Query("category") SoundCategory category,
                                 @Query("tag") String tag,
                                 @Query("link") boolean link,
                                 @Query("search") String search,
                                 @Query("sound_type") SoundType soundType,
                                 @Query("resource_created_by") String createdBy,
                                 Callback<List<List<DAMSound>>> callback);

        /**
         * Simplified method for getting all sounds in a single collection
         * @param collection Collection ID
         * @param link Show download link?
         * @param callback Callback for parsing
         */
        @GET("/api_audio_search/index.php/")
        void getCollection(@Query("collection") int collection,
                           @Query("link") boolean link,
                           Callback<List<List<DAMSound>>> callback);

        /**
         * Simplified method for getting all sounds for a single category
         *
         * TODO: Does this search all collections available to the user, or for anyone?
         *
         * @param category SoundCategory
         * @param link Show download link?
         * @param callback Callback for parsing
         */
        @GET("/api_audio_search/index.php/")
        void getCategory(@Query("category") SoundCategory category,
                         @Query("link") boolean link,
                         Callback<List<List<DAMSound>>> callback);

        /**
         * Simplified method for getting all sounds of a single SoundType
         * @param soundType SoundType
         * @param link Show download link?
         * @param callback Callback for parsing
         */
        @GET("/api_audio_search/index.php/")
        void getType(@Query("sound_type") SoundType soundType,
                     @Query("link") boolean link,
                     Callback<List<List<DAMSound>>> callback);
    }
}
