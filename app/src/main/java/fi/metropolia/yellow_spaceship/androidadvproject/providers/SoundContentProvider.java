package fi.metropolia.yellow_spaceship.androidadvproject.providers;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.util.Arrays;
import java.util.HashSet;

import fi.metropolia.yellow_spaceship.androidadvproject.database.DAMSoundContract.DAMSoundEntry;
import fi.metropolia.yellow_spaceship.androidadvproject.database.DAMSoundDbHelper;

public class SoundContentProvider extends ContentProvider {

    // Reference to our database
    private DAMSoundDbHelper database;

    // Used for the UriMacher
    private static final int SOUNDS = 10;
    private static final int SOUND_ID = 20;

    private static final String AUTHORITY = "fi.metropolia.yellow_spaceship.androidadvproject.providers";

    private static final String BASE_PATH = "damsounds";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
            + "/" + BASE_PATH);

    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
            + "/sounds";
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
            + "/sound";

    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        sURIMatcher.addURI(AUTHORITY, BASE_PATH, SOUNDS);
        sURIMatcher.addURI(AUTHORITY, BASE_PATH + "/#", SOUND_ID);
    }

    @Override
    public boolean onCreate() {
        this.database = DAMSoundDbHelper.getInstance(getContext());
        return false;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        // Validate that all requested columns exist (throws on error)
        checkColumns(projection);

        // Set the table
        queryBuilder.setTables(DAMSoundEntry.TABLE_NAME);

        int uriType = sURIMatcher.match(uri);
        switch (uriType) {
            case SOUNDS:
                break;
            case SOUND_ID:
                // Adding the ID to the original query
                queryBuilder.appendWhere(DAMSoundEntry._ID + "="
                        + uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        SQLiteDatabase db = database.getWritableDatabase();
        Cursor cursor = queryBuilder.query(db, projection, selection,
                selectionArgs, null, null, sortOrder);

        // Make sure that potential listeners are getting notified
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) throws IllegalArgumentException {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = database.getWritableDatabase();
        long id;

        // Verify that the proper URI is being accessed
        switch (uriType) {
            case SOUNDS:
                id = sqlDB.insert(DAMSoundEntry.TABLE_NAME, null, values);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        // Notify any possible ContentResolvers of the change
        getContext().getContentResolver().notifyChange(uri, null);

        // Return a URI for the newly created item
        return Uri.parse(BASE_PATH + "/" + id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int uriType = sURIMatcher.match(uri);

        // Get access to the writable DB
        SQLiteDatabase sqlDB = database.getWritableDatabase();

        int rowsDeleted;
        switch (uriType) {
            case SOUNDS:
                rowsDeleted = sqlDB.delete(DAMSoundEntry.TABLE_NAME, selection,
                        selectionArgs);
                break;
            case SOUND_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsDeleted = sqlDB.delete(DAMSoundEntry.TABLE_NAME,
                            DAMSoundEntry._ID + "=" + id,
                            null);
                } else {
                    // In case there are some additional selections, add them to the delete-call
                    rowsDeleted = sqlDB.delete(DAMSoundEntry.TABLE_NAME,
                            DAMSoundEntry._ID + "=" + id
                                    + " and " + selection,
                            selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = database.getWritableDatabase();
        int rowsUpdated;
        switch (uriType) {
            case SOUNDS:
                rowsUpdated = sqlDB.update(DAMSoundEntry.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);
                break;
            case SOUND_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated = sqlDB.update(DAMSoundEntry.TABLE_NAME,
                            values,
                            DAMSoundEntry._ID + "=" + id,
                            null);
                } else {
                    rowsUpdated = sqlDB.update(DAMSoundEntry.TABLE_NAME,
                            values,
                            DAMSoundEntry._ID + "=" + id
                                    + " and "
                                    + selection,
                            selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }

    @Override
    public void shutdown() {
        this.database.close();
        super.shutdown();
    }

    /**
     * Validate that a query only requests valid columns
     * @param projection Projection to validate
     * @throws IllegalArgumentException In case there are unknown columns in the project
     */
    private void checkColumns(String[] projection) throws IllegalArgumentException {
        String[] available = {
                DAMSoundEntry._ID,
                DAMSoundEntry.COLUMN_NAME_SOUND_ID,
                DAMSoundEntry.COLUMN_NAME_TITLE,
                DAMSoundEntry.COLUMN_NAME_CATEGORY,
                DAMSoundEntry.COLUMN_NAME_TYPE,
                DAMSoundEntry.COLUMN_NAME_LENGTH_SEC,
                DAMSoundEntry.COLUMN_NAME_IS_FAVORITE,
                DAMSoundEntry.COLUMN_NAME_FILE_NAME,
        };

        if (projection != null) {
            HashSet<String> requestedColumns = new HashSet<>(Arrays.asList(projection));
            HashSet<String> availableColumns = new HashSet<>(Arrays.asList(available));

            // Check if all columns which are requested are available
            if (!availableColumns.containsAll(requestedColumns)) {
                throw new IllegalArgumentException("Unknown columns in projection");
            }
        }
    }
}