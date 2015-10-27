package fi.metropolia.yellow_spaceship.androidadvproject.api;

import fi.metropolia.yellow_spaceship.androidadvproject.models.DAMSound;

/**
 * Created by Petri on 27.10.2015.
 */
public interface AsyncDownloaderListener {
    public void onDownloadFinished(DAMSound damSOund);
}
