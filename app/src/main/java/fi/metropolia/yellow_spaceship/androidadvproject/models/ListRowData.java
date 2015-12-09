package fi.metropolia.yellow_spaceship.androidadvproject.models;

/**
 * Holds data for a single RecyclerView row with an optional icon
 */
public class ListRowData {

    private final String caption;
    private final Integer icon;
    private final SoundCategory category;

    /**
     * Constructor
     *
     * @param caption Caption for the row.
     * @param icon    R integer for a drawable.
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
