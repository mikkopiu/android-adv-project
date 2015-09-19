package fi.metropolia.yellow_spaceship.androidadvproject.menu;

import android.graphics.drawable.Drawable;

/**
 * Created by Petri on 19.9.2015.
 */
public class ListRowData {

    private String caption;
    private Integer icon;

    public ListRowData() {
        this.caption = null;
        this.icon = null;
    }

    public ListRowData(String caption, Integer icon) {
        this.caption = caption;
        this.icon = icon;
    }

    public String getCaption() {
        return this.caption;
    }

    public Integer getIcon() {
        return this.icon;
    }

}
