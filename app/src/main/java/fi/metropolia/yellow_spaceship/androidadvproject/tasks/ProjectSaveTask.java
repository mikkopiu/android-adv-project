package fi.metropolia.yellow_spaceship.androidadvproject.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import fi.metropolia.yellow_spaceship.androidadvproject.models.SoundScapeProject;

public class ProjectSaveTask extends AsyncTask<SoundScapeProject, Void, Boolean> {
    public final static String PROJECT_FOLDER = "projects";
    public final static String FILE_EXT = ".json";

    private final SaveListener listener;
    private final Context context;

    private Throwable error;

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
                    "/" + project.getName() + FILE_EXT);

            File folder = new File(this.context.getFilesDir() + "/" + PROJECT_FOLDER);
            folder.mkdirs();

            // We simply allow overwriting of existing projects
            if (!outputFile.exists()) {
                outputFile.createNewFile();
            }

            if (!folder.exists() || !outputFile.exists()) {
                error = new IOException("Failed to create folder or file for saving");
            } else {
                FileWriter writer = new FileWriter(outputFile);
                bw = new BufferedWriter(writer);
                bw.write(json);
            }

        } catch (IOException e) {
            error = e;
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
        if (error != null) {
            Log.e("ProjectSaveTask", "Failed to save project", error);
        }

        listener.onSaveComplete(error == null);
    }
}
