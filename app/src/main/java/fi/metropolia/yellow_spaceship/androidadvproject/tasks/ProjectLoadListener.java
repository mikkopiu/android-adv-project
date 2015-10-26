package fi.metropolia.yellow_spaceship.androidadvproject.tasks;

import java.util.ArrayList;

import fi.metropolia.yellow_spaceship.androidadvproject.models.SoundScapeProject;

public interface ProjectLoadListener {
    void onLoadFinished(ArrayList<SoundScapeProject> data);
}
