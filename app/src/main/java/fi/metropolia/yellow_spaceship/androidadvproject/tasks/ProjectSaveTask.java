package fi.metropolia.yellow_spaceship.androidadvproject.tasks;

import android.content.Context;
import android.os.AsyncTask;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import fi.metropolia.yellow_spaceship.androidadvproject.models.SoundScapeProject;

public class ProjectSaveTask extends AsyncTask<SoundScapeProject, Void, Boolean> {
    public final static String PROJECT_FOLDER = "projects";

    private SaveListener listener;
    private Context context;

    public ProjectSaveTask(Context context, SaveListener listener) {
        this.listener = listener;
        this.context = context;
    }

    @Override
    protected Boolean doInBackground(SoundScapeProject... params) {
        SoundScapeProject project = params[0];

        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(project);

        BufferedWriter bw = null;
        try {
            File outputFile = new File(this.context.getFilesDir() +
                    "/" + PROJECT_FOLDER +
                    "/" + project.getName() + ".json");

            File folder = new File(this.context.getFilesDir() + "/" + PROJECT_FOLDER);
            if(!folder.exists()) {
                folder.mkdirs();
            }

            // TODO: add logic for handling already existing names
            if (!outputFile.exists()) {
                outputFile.createNewFile();
            }

            FileWriter writer = new FileWriter(outputFile);
            bw = new BufferedWriter(writer);
            bw.write(json);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bw != null) {
                    bw.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        return null;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        listener.onSaveComplete();
    }
}
