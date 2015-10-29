package fi.metropolia.yellow_spaceship.androidadvproject.api;

import fi.metropolia.yellow_spaceship.androidadvproject.models.DAMSound;

public interface AsyncDownloaderListener {
    void onDownloadFinished(DAMSound damSOund);
}
