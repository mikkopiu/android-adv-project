package fi.metropolia.yellow_spaceship.androidadvproject.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * A single sound from the DAM.
 */
public class DAMSound implements Parcelable {

    // Properties found in DAM
    private String Title;
    private String Description;
    private String OriginalFilename;
    private SoundCategory Category;
    private SoundType SoundType;
    private int LengthSec; // TODO: Can this be a float on the DAM side?
    private Date CreationDate;
    private String FileExtension;
    private float FileSizeKB;
    private String CreatedBy;
    private String CollectionName;
    private int CollectionID;
    private String DownloadLink;
    private Map<String, Object> additionalProperties = new HashMap<>();

    // Additional local properties
    private transient boolean isFavorite;
    private transient boolean isRecording;
    private transient String fileName;
    private transient String soundId;

    /**
     * Necessary empty constructor, to not conflict with the Parcelable implementation
     */
    public DAMSound() {
        // NOTE: Intentionally empty
    }

    /**
     * Constructor for the Parcelable interface
     *
     * @param in DAMSound as a Parcel
     */
    private DAMSound(Parcel in) {
        this.Title = in.readString();
        this.Category = SoundCategory.fromApi(in.readString());
        this.SoundType = fi.metropolia.yellow_spaceship.androidadvproject.models.SoundType.fromApi(in.readString());
        this.LengthSec = in.readInt();
        this.isFavorite = in.readByte() == (byte) 1;
        this.isRecording = in.readByte() == (byte) 1;
        this.fileName = in.readString();
        this.soundId = in.readString();
    }

    /**
     * Title of the sound
     *
     * @return The Title
     */
    public String getTitle() {
        return Title;
    }

    /**
     * @param Title The Title
     */
    public void setTitle(String Title) {
        this.Title = Title;
    }

    /**
     * Get a description of the sound
     *
     * @return A description of the sound
     */
    public String getDescription() {
        return Description;
    }

    /**
     * @param Description A description of the sound
     */
    public void setDescription(String Description) {
        this.Description = Description;
    }

    /**
     * Original file name
     *
     * @return The OriginalFilename
     */
    public String getOriginalFilename() {
        return OriginalFilename;
    }

    /**
     * @param OriginalFilename The Original filename
     */
    public void setOriginalFilename(String OriginalFilename) {
        this.OriginalFilename = OriginalFilename;
    }

    /**
     * Category of the sound, e.g. NATURE, HUMAN
     *
     * @return The Category
     */
    public SoundCategory getCategory() {
        return Category;
    }

    /**
     * @param Category The Category
     */
    public void setCategory(SoundCategory Category) {
        this.Category = Category;
    }

    /**
     * The sound's type, e.g. SOUNDSCAPE, EFFECT
     *
     * @return The SoundType
     */
    public SoundType getSoundType() {
        return SoundType;
    }

    /**
     * @param SoundType The Sound Type
     */
    public void setSoundType(SoundType SoundType) {
        this.SoundType = SoundType;
    }

    /**
     * Length of the sound in full seconds
     *
     * @return The LengthSec
     */
    public int getLengthSec() {
        return LengthSec;
    }

    /**
     * @param LengthSec The Length (sec)
     */
    public void setLengthSec(int LengthSec) {
        this.LengthSec = LengthSec;
    }

    /**
     * Date that the sound was created on
     *
     * @return The CreationDate
     */
    public Date getCreationDate() {
        return CreationDate;
    }

    /**
     * @param CreationDate The Creation date
     */
    public void setCreationDate(Date CreationDate) {
        this.CreationDate = CreationDate;
    }

    /**
     * File extension, without the dot, e.g. "mp3"
     *
     * @return The FileExtension
     */
    public String getFileExtension() {
        return FileExtension;
    }

    /**
     * @param FileExtension The File extension
     */
    public void setFileExtension(String FileExtension) {
        this.FileExtension = FileExtension;
    }

    /**
     * File size in kilobytes
     *
     * @return The FileSizeKB
     */
    public float getFileSizeKB() {
        return FileSizeKB;
    }

    /**
     * @param FileSizeKB The File size(KB)
     */
    public void setFileSizeKB(float FileSizeKB) {
        this.FileSizeKB = FileSizeKB;
    }

    /**
     * Username of the user that created this sound
     *
     * @return The CreatedBy
     */
    public String getCreatedBy() {
        return CreatedBy;
    }

    /**
     * @param CreatedBy The Created by
     */
    public void setCreatedBy(String CreatedBy) {
        this.CreatedBy = CreatedBy;
    }

    /**
     * Name of the collection where this sound is located
     *
     * @return The CollectionName
     */
    public String getCollectionName() {
        return CollectionName;
    }

    /**
     * @param CollectionName The Collection name
     */
    public void setCollectionName(String CollectionName) {
        this.CollectionName = CollectionName;
    }

    /**
     * The ID of the collection where this sound is located
     *
     * @return The CollectionID
     */
    public int getCollectionID() {
        return CollectionID;
    }

    /**
     * @param CollectionID The Collection ID
     */
    public void setCollectionID(int CollectionID) {
        this.CollectionID = CollectionID;
    }

    /**
     * URL to download the sound
     *
     * @return Download URL
     */
    public String getDownloadLink() {
        return DownloadLink;
    }

    /**
     * @param downloadLink New download URL
     */
    public void setDownloadLink(String downloadLink) {
        DownloadLink = downloadLink;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    /**
     * Has the local user favorited this sound
     *
     * @return Is this sound favorited
     */
    public boolean getIsFavorite() {
        return isFavorite;
    }

    /**
     * Set favorite-status for this sound
     *
     * @param favorite True if this is a favorite
     */
    public void setIsFavorite(boolean favorite) {
        this.isFavorite = favorite;
    }

    /**
     * Is this sound recorded by the user
     *
     * @return Is this sound a recording
     */
    public boolean getIsRecording() {
        return isRecording;
    }

    /**
     * Set recorded-status for this sound
     *
     * @param recording True if this is a recording
     */
    public void setIsRecording(boolean recording) {
        this.isRecording = recording;
    }

    /**
     * Get local file name
     *
     * @return {?String} Returns null if file is not saved locally
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * Set local file name
     *
     * @param fileName Local file name for sound
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * Get a unique identified for the sound
     *
     * @return Unique ID for this sound
     */
    public String getFormattedSoundId() {
        if (this.soundId == null) {
            this.setFormattedSoundId(this.generateFormattedSoundId());
        }
        return this.soundId;
    }

    public void setFormattedSoundId(String soundId) {
        this.soundId = soundId;
    }

    public String generateFormattedSoundId() {
        return String.valueOf(getCollectionID()) +
                getTitle() +
                String.valueOf(getCreationDate().getTime());
    }

    /**
     * PARCELABLE IMPLEMENTATIONS
     * See the constructor above
     */

    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Implementation for Parcelable interface.
     * Only write the necessary fields for SoundScapes,
     * no need for anything else (might not even exist, when handling favorites).
     *
     * @param dest
     * @param flags
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getTitle());
        dest.writeString(getCategory().toString());
        dest.writeString(getSoundType().toString());
        dest.writeInt(getLengthSec());
        dest.writeByte((byte) (getIsFavorite() ? 1 : 0));
        dest.writeByte((byte) (getIsRecording() ? 1 : 0));
        dest.writeString(getFileName());
        dest.writeString(getFormattedSoundId());
    }

    public static final Parcelable.Creator<DAMSound> CREATOR
            = new Parcelable.Creator<DAMSound>() {

        public DAMSound createFromParcel(Parcel in) {
            return new DAMSound(in);
        }

        public DAMSound[] newArray(int size) {
            return new DAMSound[size];
        }
    };
}