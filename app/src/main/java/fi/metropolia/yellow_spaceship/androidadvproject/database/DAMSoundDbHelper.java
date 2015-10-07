package fi.metropolia.yellow_spaceship.androidadvproject.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import fi.metropolia.yellow_spaceship.androidadvproject.database.DAMSoundContract.DAMSoundEntry;

/**
 * DAMSoundDbHelper is used to manage the SQLite DB's creation/versioning etc.
 */
public class DAMSoundDbHelper extends SQLiteOpenHelper {
    private static DAMSoundDbHelper sInstance;

    // NOTE: Increment DATABASE_VERSION if changing the schema
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "DAMSound.db";

    private static final String TEXT_TYPE = " TEXT";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + DAMSoundEntry.TABLE_NAME + " (" +
                    DAMSoundEntry._ID + " INTEGER PRIMARY KEY," +
                    DAMSoundEntry.COLUMN_NAME_SOUND_ID + TEXT_TYPE + COMMA_SEP +
                    DAMSoundEntry.COLUMN_NAME_TITLE + TEXT_TYPE + COMMA_SEP +
                    DAMSoundEntry.COLUMN_NAME_CATEGORY + TEXT_TYPE + COMMA_SEP +
                    DAMSoundEntry.COLUMN_NAME_TYPE + TEXT_TYPE + COMMA_SEP +
                    DAMSoundEntry.COLUMN_NAME_LENGTH_SEC + INTEGER_TYPE + COMMA_SEP +   // TODO: check is this actually an int, or can it be a float?
                    DAMSoundEntry.COLUMN_NAME_IS_FAVORITE + INTEGER_TYPE + COMMA_SEP +  // SQLite doesn't have booleans, so this needs to be an int
                    DAMSoundEntry.COLUMN_NAME_FILE_NAME + TEXT_TYPE +
                    " )";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + DAMSoundEntry.TABLE_NAME;

    public static synchronized DAMSoundDbHelper getInstance(Context context) {

        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (sInstance == null) {
            sInstance = new DAMSoundDbHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    /**
     * Constructor should be private to prevent direct instantiation.
     * make call to static method "getInstance()" instead.
     */
    private DAMSoundDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO: implement data migration if necessary, not necessary for development
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    /**
     * Check if a single sound is favorited by the user
     * @param id Sound's ID
     * @return Favorite-status
     */
    public boolean isFavorite(String id) {
        boolean isFavorite = false;
        SQLiteDatabase db = this.getReadableDatabase();

        String[] projection = {
                DAMSoundEntry._ID,
                DAMSoundEntry.COLUMN_NAME_SOUND_ID,
                DAMSoundEntry.COLUMN_NAME_IS_FAVORITE
        };

        Cursor cursor = db.query(
                DAMSoundEntry.TABLE_NAME,
                projection,
                DAMSoundEntry.COLUMN_NAME_SOUND_ID + "=?",
                new String[] { String.valueOf(id) },
                null,
                null,
                null,
                null
        );

        // There should be max one sound found
        if (cursor.moveToFirst()) {
            // In any other case than 1, the sound is not a favorite
            if (cursor.getInt(2) == 1) {
                isFavorite = true;
            }
        }

        // Close the cursor to not leave it hanging in memory
        cursor.close();

        return isFavorite;
    }
}