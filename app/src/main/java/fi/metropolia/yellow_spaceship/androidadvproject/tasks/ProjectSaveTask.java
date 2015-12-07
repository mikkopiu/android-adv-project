package fi.metropolia.yellow_spaceship.androidadvproject.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import fi.metropolia.yellow_spaceship.androidadvproject.models.SoundScapeProject;

/**
 * AsyncTask for saving a soundscape's data to file
 */
public class ProjectSaveTask extends AsyncTask<SoundScapeProject, Void, Boolean> {
    public final static String PROJECT_FOLDER = "projects";
    public final static String FILE_EXT = ".json";

    private final ProjectSaveListener mListener;
    private final Context mContext;
    private Throwable mError;

    /**
     * Constructor
     *
     * @param context  Context for finding the proper file directory
     * @param listener Listener for save completion events
     */
    public ProjectSaveTask(Context context, @NonNull ProjectSaveListener listener) {
        this.mListener = listener;
        this.mContext = context;
    }

    @Override
    protected Boolean doInBackground(SoundScapeProject... params) {
        SoundScapeProject project = params[0];

        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(project);

        BufferedWriter bw = null;
        try {
            File outputFile = new File(this.mContext.getFilesDir() +
                    "/" + PROJECT_FOLDER +
                    "/" + project.getName() + FILE_EXT);

            File folder = new File(this.mContext.getFilesDir() + "/" + PROJECT_FOLDER);
            folder.mkdirs();

            // We simply allow overwriting of existing projects
            if (!outputFile.exists()) {
                outputFile.createNewFile();
            }

            if (!folder.exists() || !outputFile.exists()) {
                mError = new IOException("Failed to create folder or file for saving");
            } else {
                FileWriter writer = new FileWriter(outputFile);
                bw = new BufferedWriter(writer);
                bw.write(json);
            }

        } catch (IOException e) {
            mError = e;
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
        if (mError != null) {
            Log.e("ProjectSaveTask", "Failed to save project", mError);
        }

        mListener.onSaveComplete(mError == null);
    }
}
