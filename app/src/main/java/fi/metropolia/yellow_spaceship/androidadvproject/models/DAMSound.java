package fi.metropolia.yellow_spaceship.androidadvproject.models;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * A single sound from the DAM.
 */
public class DAMSound {

    private String Title;
    private String OriginalFilename;
    private SoundCategory Category;
    private SoundType SoundType;
    private int LengthSec; // TODO: Can this be a float on the DAM side?
    private Date CreationDate;
    private String FileExtension;
    private String FileSizeKB;
    private String CreatedBy;
    private String CollectionName;
    private int CollectionID;
    private String DownloadLink;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * Title of the sound
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
     * Original file name
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
     * @return The FileSizeKB
     */
    public String getFileSizeKB() {
        return FileSizeKB;
    }

    /**
     * @param FileSizeKB The File size(KB)
     */
    public void setFileSizeKB(String FileSizeKB) {
        this.FileSizeKB = FileSizeKB;
    }

    /**
     * Username of the user that created this sound
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
}