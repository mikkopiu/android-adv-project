package fi.metropolia.yellow_spaceship.androidadvproject.menu;

import android.graphics.drawable.Drawable;

/**
 * Holds data for a single RecyclerView row
 */
public class ListRowData {

    private String caption;
    private Integer icon;

    /**
     * Default constructor
     */
    public ListRowData() {
        this.caption = null;
        this.icon = null;
    }

    /**
     * Constructor
     * @param caption Caption for the row.
     * @param icon R integer for a drawable.
     */
    public ListRowData(String caption, Integer icon) {
        this.caption = caption;
        this.icon = icon;
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

}
