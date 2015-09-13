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
    private String LengthSec;
    private Date CreationDate;
    private String FileExtension;
    private String FileSizeKB;
    private String CreatedBy;
    private String CollectionName;
    private String CollectionID;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     *
     * @return
     * The Title
     */
    public String getTitle() {
        return Title;
    }

    /**
     *
     * @param Title
     * The Title
     */
    public void setTitle(String Title) {
        this.Title = Title;
    }

    /**
     *
     * @return
     * The OriginalFilename
     */
    public String getOriginalFilename() {
        return OriginalFilename;
    }

    /**
     *
     * @param OriginalFilename
     * The Original filename
     */
    public void setOriginalFilename(String OriginalFilename) {
        this.OriginalFilename = OriginalFilename;
    }

    /**
     *
     * @return
     * The Category
     */
    public SoundCategory getCategory() {
        return Category;
    }

    /**
     *
     * @param Category
     * The Category
     */
    public void setCategory(SoundCategory Category) {
        this.Category = Category;
    }

    /**
     *
     * @return
     * The SoundType
     */
    public SoundType getSoundType() {
        return SoundType;
    }

    /**
     *
     * @param SoundType
     * The Sound Type
     */
    public void setSoundType(SoundType SoundType) {
        this.SoundType = SoundType;
    }

    /**
     *
     * @return
     * The LengthSec
     */
    public String getLengthSec() {
        return LengthSec;
    }

    /**
     *
     * @param LengthSec
     * The Length (sec)
     */
    public void setLengthSec(String LengthSec) {
        this.LengthSec = LengthSec;
    }

    /**
     *
     * @return
     * The CreationDate
     */
    public Date getCreationDate() {
        return CreationDate;
    }

    /**
     *
     * @param CreationDate
     * The Creation date
     */
    public void setCreationDate(Date CreationDate) {
        this.CreationDate = CreationDate;
    }

    /**
     *
     * @return
     * The FileExtension
     */
    public String getFileExtension() {
        return FileExtension;
    }

    /**
     *
     * @param FileExtension
     * The File extension
     */
    public void setFileExtension(String FileExtension) {
        this.FileExtension = FileExtension;
    }

    /**
     *
     * @return
     * The FileSizeKB
     */
    public String getFileSizeKB() {
        return FileSizeKB;
    }

    /**
     *
     * @param FileSizeKB
     * The File size(KB)
     */
    public void setFileSizeKB(String FileSizeKB) {
        this.FileSizeKB = FileSizeKB;
    }

    /**
     *
     * @return
     * The CreatedBy
     */
    public String getCreatedBy() {
        return CreatedBy;
    }

    /**
     *
     * @param CreatedBy
     * The Created by
     */
    public void setCreatedBy(String CreatedBy) {
        this.CreatedBy = CreatedBy;
    }

    /**
     *
     * @return
     * The CollectionName
     */
    public String getCollectionName() {
        return CollectionName;
    }

    /**
     *
     * @param CollectionName
     * The Collection name
     */
    public void setCollectionName(String CollectionName) {
        this.CollectionName = CollectionName;
    }

    /**
     *
     * @return
     * The CollectionID
     */
    public String getCollectionID() {
        return CollectionID;
    }

    /**
     *
     * @param CollectionID
     * The Collection ID
     */
    public void setCollectionID(String CollectionID) {
        this.CollectionID = CollectionID;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}