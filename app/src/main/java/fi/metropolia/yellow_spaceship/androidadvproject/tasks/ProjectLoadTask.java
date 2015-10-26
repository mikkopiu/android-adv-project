package fi.metropolia.yellow_spaceship.androidadvproject.tasks;

import android.os.AsyncTask;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import fi.metropolia.yellow_spaceship.androidadvproject.models.SoundScapeProject;

public class ProjectLoadTask extends AsyncTask<String, Void, ArrayList<SoundScapeProject>> {
    private ProjectLoadListener listener;
    private ArrayList<SoundScapeProject> projects = new ArrayList<>();

    public ProjectLoadTask(ProjectLoadListener listener) {
        this.listener = listener;
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
                    this.projects.add(p);
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

        return this.projects;
    }

    @Override
    protected void onPostExecute(ArrayList<SoundScapeProject> data) {
        this.listener.onLoadFinished(data);
    }
}
