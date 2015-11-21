package fi.metropolia.yellow_spaceship.androidadvproject.adapters;

/**
 * Interface for actions on ProjectSounds in the Create-view
 */
public interface IProjectSoundViewHolderClicks {
    void onCloseClick(int layoutPosition);

    void onVolumeChange(int layoutPosition, int progress);

    void onRandomizeCheckedChange(int layoutPosition, boolean checked);
}
