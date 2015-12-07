package fi.metropolia.yellow_spaceship.androidadvproject.adapters;

/**
 * General interface for ViewHolder clicks
 */
public interface IViewHolderClicks {
    void onRowSelect(int layoutPosition);

    void onRowDelete(int layoutPosition);
}
