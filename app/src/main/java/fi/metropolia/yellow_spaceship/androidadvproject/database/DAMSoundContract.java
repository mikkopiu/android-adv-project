package fi.metropolia.yellow_spaceship.androidadvproject.database;

import android.provider.BaseColumns;

/**
 * A contract class for the SQLite database.
 * Used for save metadata for sounds that are favorited and/or downloaded.
 */
public final class DAMSoundContract {

    // Empty constructor to prevent accidental instantiation
    public DAMSoundContract() {}

    public static abstract class DAMSoundEntry implements BaseColumns {
        public static final String TABLE_NAME = "sounds";
        public static final String COLUMN_NAME_SOUND_ID = "sound_id";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_CATEGORY = "category";
        public static final String COLUMN_NAME_TYPE = "type";
        public static final String COLUMN_NAME_LENGTH_SEC = "length_sec";
        public static final String COLUMN_NAME_IS_FAVORITE = "is_favorite";
        public static final String COLUMN_NAME_FILE_NAME = "filename";
    }
}
