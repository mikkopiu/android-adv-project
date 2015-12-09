package fi.metropolia.yellow_spaceship.androidadvproject.tasks;

import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import fi.metropolia.yellow_spaceship.androidadvproject.models.SoundScapeProject;

/**
 * AsyncTask for loading saved soundscape projects' data
 */
public class ProjectLoadTask extends AsyncTask<String, Void, ArrayList<SoundScapeProject>> {
    private final ProjectLoadListener mListener;
    private final ArrayList<SoundScapeProject> mProjects = new ArrayList<>();

    /**
     * Constructor
     *
     * @param listener Listener for handling load finishing events
     */
    public ProjectLoadTask(ProjectLoadListener listener) {
        this.mListener = listener;
    }

    @Override
    protected ArrayList<SoundScapeProject> doInBackground(String... params) {
        // Create a single GSON parser
        Gson gson = new GsonBuilder().create();

        // Project folder
        String path = params[0];

        BufferedReader bufferedReader = null;
        File folder = new File(path);

        // The projects-folder might not exist yet, so no need to look for files in it
        if (folder.isDirectory()) {
            for (File file : folder.listFiles()) {
                try {
                    // A single file contains a single SoundScapeProject object to be parsed
                    bufferedReader = new BufferedReader(new FileReader(file));
                    SoundScapeProject p = gson.fromJson(bufferedReader, SoundScapeProject.class);
                    this.mProjects.add(p);
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (bufferedReader != null) {
                        try {
                            // BufferedReader needs to be closed
                            bufferedReader.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

        return this.mProjects;
    }

    @Override
    protected void onPostExecute(ArrayList<SoundScapeProject> data) {
        // Let the listener know the loading has finished
        if (this.mListener != null) {
            this.mListener.onLoadFinished(data);
        }
    }
}
