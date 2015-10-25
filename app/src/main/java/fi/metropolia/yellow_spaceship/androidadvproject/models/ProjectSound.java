package fi.metropolia.yellow_spaceship.androidadvproject.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * A simplified representation of a DAMSound in a SoundScapeProject.
 * Can be serialized using GSON to JSON format.
 */
public class ProjectSound implements Parcelable {
    private String id;
    private String title;
    private SoundCategory category;
    private SoundType soundType;
    private String fileName;
    private boolean isOnLoop;
    private boolean isRandom;
    private float volume;

    /**
     * Constructor, at least an ID is required
     * @param id
     */
    public ProjectSound(String id) {
        this(id, null, null, null, null, false, false, 1.0f);
    }

    public ProjectSound(Parcel in) {
        this(
                in.readString(),
                in.readString(),
                SoundCategory.fromApi(in.readString()),
                SoundType.fromApi(in.readString()),
                in.readString(),
                in.readByte() == 1,
                in.readByte() == 1,
                in.readFloat()
        );
    }

    /**
     * Full constructor
     * @param id
     * @param title
     * @param category
     * @param soundType
     * @param fileName
     * @param isOnLoop
     * @param isRandom
     */
    public ProjectSound(String id, String title, SoundCategory category, SoundType soundType,
                        String fileName, boolean isOnLoop, boolean isRandom, float volume) {
        this.id = id;
        this.title = title;
        this.category = category;
        this.soundType = soundType;
        this.fileName = fileName;
        this.isOnLoop = isOnLoop;
        this.isRandom = isRandom;
        this.volume = volume;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public SoundCategory getCategory() {
        return category;
    }

    public void setCategory(SoundCategory category) {
        this.category = category;
    }

    public SoundType getSoundType() {
        return soundType;
    }

    public void setSoundType(SoundType soundType) {
        this.soundType = soundType;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public boolean getIsOnLoop() {
        return isOnLoop;
    }

    public void setIsOnLoop(boolean isOnLoop) {
        this.isOnLoop = isOnLoop;
    }

    public boolean getIsRandom() {
        return isRandom;
    }

    public void setIsRandom(boolean isRandom) {
        this.isRandom = isRandom;
    }

    public float getVolume() {
        return volume;
    }

    public void setVolume(float volume) {
        // Constrain volume to be between 0 and 1
        if (volume < 0.0f) {
            volume = 0.0f;
        } else if (volume > 1.0f) {
            volume = 1.0f;
        }

        this.volume = volume;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.getId());
        dest.writeString(this.getTitle());
        dest.writeString(this.getCategory().toString());
        dest.writeString(this.getSoundType().toString());
        dest.writeString(this.getFileName());
        dest.writeByte((byte) (this.getIsOnLoop() ? 1 : 0));
        dest.writeByte((byte) (this.getIsRandom() ? 1 : 0));
        dest.writeFloat(this.getVolume());
    }

    public static final Parcelable.Creator<ProjectSound> CREATOR
            = new Parcelable.Creator<ProjectSound>() {

        public ProjectSound createFromParcel(Parcel in) {
            return new ProjectSound(in);
        }

        public ProjectSound[] newArray(int size) {
            return new ProjectSound[size];
        }
    };
}
