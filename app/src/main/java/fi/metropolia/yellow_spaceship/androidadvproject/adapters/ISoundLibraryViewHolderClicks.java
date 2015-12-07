package fi.metropolia.yellow_spaceship.androidadvproject.adapters;

/**
 * Actions for items in the SoundLibrary.
 */
public interface ISoundLibraryViewHolderClicks extends IViewHolderClicks {
    void onFavorite(int layoutPosition);

    void onPlayPauseToggle(int layoutPosition);

    void onRowUpload(int layoutPosition);
}
