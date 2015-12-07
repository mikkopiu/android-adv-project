package fi.metropolia.yellow_spaceship.androidadvproject.providers;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import java.util.Arrays;
import java.util.HashSet;

import fi.metropolia.yellow_spaceship.androidadvproject.database.DAMSoundContract.DAMSoundEntry;
import fi.metropolia.yellow_spaceship.androidadvproject.database.DAMSoundDbHelper;

/**
 * ContentProvider for DAMSounds saved locally (or more specifically their metadata
 * in a local SQLite database).
 * <p/>
 * This ContentProvider should not be interacted with directly, but through the
 * ContentResolver API.
 */
public class SoundContentProvider extends ContentProvider {

    // Reference to our database
    private DAMSoundDbHelper dbHelper;
    private SQLiteDatabase database;

    // Used for the UriMatcher
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
        Context context = getContext();
        this.dbHelper = DAMSoundDbHelper.getInstance(context);
        this.database = this.dbHelper.getWritableDatabase();

        return this.database != null;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
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

        Cursor cursor = queryBuilder.query(this.database, projection, selection,
                selectionArgs, null, null, sortOrder);

        // Make sure that potential listeners are getting notified
        try {
            cursor.setNotificationUri(getContext().getContentResolver(), uri);
        } catch (NullPointerException e) {
            Log.e("SoundContentProvider", "Could not set notification URI, context is null!");
        }

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) throws IllegalArgumentException {
        int uriType = sURIMatcher.match(uri);
        long id = 0;

        // Verify that the proper URI is being accessed
        if (uriType == SOUNDS) {
            String selection = DAMSoundEntry.COLUMN_NAME_SOUND_ID + "=?";
            String[] selectionArgs = new String[]{
                    values.getAsString(DAMSoundEntry.COLUMN_NAME_SOUND_ID)
            };

            // Do an update if the constraints match
            int rowsAffected = this.database.update(
                    DAMSoundEntry.TABLE_NAME,
                    values,
                    selection,
                    selectionArgs
            );

            if (rowsAffected == 0) {
                // No such row already existed, do an actual insert
                id = this.database.insert(DAMSoundEntry.TABLE_NAME, null, values);
            } else {
                // Find the ID of the already existing sound (to return)
                Cursor cursor = this.database.query(
                        DAMSoundEntry.TABLE_NAME,
                        new String[]{DAMSoundEntry._ID},
                        selection,
                        selectionArgs,
                        null, null, null
                );

                if (cursor.moveToFirst()) {
                    id = cursor.getLong(0);
                }

                cursor.close();
            }

            try {
                getContext().getContentResolver().notifyChange(uri, null);
            } catch (NullPointerException e) {
                Log.e("SoundContentProvider", "Could notify ContentResolver, context is null!");
            }

            return ContentUris.withAppendedId(uri, id);
        } else {
            throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        int uriType = sURIMatcher.match(uri);

        int rowsDeleted;
        switch (uriType) {
            case SOUNDS:
                rowsDeleted = this.database.delete(DAMSoundEntry.TABLE_NAME, selection,
                        selectionArgs);
                break;
            case SOUND_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsDeleted = this.database.delete(DAMSoundEntry.TABLE_NAME,
                            DAMSoundEntry._ID + "=" + id,
                            null);
                } else {
                    // In case there are some additional selections, add them to the delete-call
                    rowsDeleted = this.database.delete(DAMSoundEntry.TABLE_NAME,
                            DAMSoundEntry._ID + "=" + id
                                    + " and " + selection,
                            selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        try {
            getContext().getContentResolver().notifyChange(uri, null);
        } catch (NullPointerException e) {
            Log.e("SoundContentProvider", "Could notify ContentResolver, context is null!");
        }

        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int uriType = sURIMatcher.match(uri);

        int rowsUpdated;
        switch (uriType) {
            case SOUNDS:
                rowsUpdated = this.database.update(DAMSoundEntry.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);
                break;
            case SOUND_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated = this.database.update(DAMSoundEntry.TABLE_NAME,
                            values,
                            DAMSoundEntry._ID + "=" + id,
                            null);
                } else {
                    rowsUpdated = this.database.update(DAMSoundEntry.TABLE_NAME,
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

        try {
            getContext().getContentResolver().notifyChange(uri, null);
        } catch (NullPointerException e) {
            Log.e("SoundContentProvider", "Could notify ContentResolver, context is null!");
        }

        return rowsUpdated;
    }

    @Override
    public void shutdown() {
        this.database.close();
        this.dbHelper.close();
        super.shutdown();
    }

    /**
     * Validate that a query only requests valid columns
     *
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
                DAMSoundEntry.COLUMN_NAME_IS_RECORDING,
                DAMSoundEntry.COLUMN_NAME_FILE_NAME,
                DAMSoundEntry.COLUMN_NAME_URL,
                DAMSoundEntry.COLUMN_NAME_FILE_EXT
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
