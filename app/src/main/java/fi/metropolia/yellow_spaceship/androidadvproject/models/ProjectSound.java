package fi.metropolia.yellow_spaceship.androidadvproject.models;

/**
 * A simplified representation of a DAMSound in a SoundScapeProject.
 * Can be serialized using GSON to JSON format.
 */
public class ProjectSound {
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

    public boolean isOnLoop() {
        return isOnLoop;
    }

    public void setIsOnLoop(boolean isOnLoop) {
        this.isOnLoop = isOnLoop;
    }

    public boolean isRandom() {
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
}
