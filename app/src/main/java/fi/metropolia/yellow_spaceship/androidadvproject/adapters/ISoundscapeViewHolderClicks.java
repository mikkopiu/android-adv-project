package fi.metropolia.yellow_spaceship.androidadvproject.adapters;

/**
 * Actions for a list of soundscapes
 */
public interface ISoundscapeViewHolderClicks {
    void onRowSelect(int layoutPosition);

    void onRowRename(int layoutPosition);

    void onRowDelete(int layoutPosition);
}
