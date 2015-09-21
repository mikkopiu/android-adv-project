package fi.metropolia.yellow_spaceship.androidadvproject.menu;

import android.graphics.drawable.Drawable;

import fi.metropolia.yellow_spaceship.androidadvproject.models.SoundCategory;

/**
 * Holds data for a single RecyclerView row
 */
public class ListRowData {

    private String caption;
    private Integer icon;
    private SoundCategory category;

    /**
     * Default constructor
     */
    public ListRowData() {
        this.caption = null;
        this.icon = null;
        this.category = null;
    }

    /**
     * Constructor
     * @param caption Caption for the row.
     * @param icon R integer for a drawable.
     */
    public ListRowData(String caption, Integer icon, SoundCategory category) {
        this.caption = caption;
        this.icon = icon;
        this.category = category;
    }

    /**
     * @return Caption for the row.
     */
    public String getCaption() {
        return this.caption;
    }

    /**
     * @return R integer for the drawable.
     */
    public Integer getIcon() {
        return this.icon;
    }

    /**
     * @return SoundCategory Enum.
     */
    public SoundCategory getCategory() {
        return this.category;
    }

}
