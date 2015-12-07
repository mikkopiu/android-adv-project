package fi.metropolia.yellow_spaceship.androidadvproject.api;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import fi.metropolia.yellow_spaceship.androidadvproject.database.DAMSoundContract.DAMSoundEntry;
import fi.metropolia.yellow_spaceship.androidadvproject.models.DAMSound;
import fi.metropolia.yellow_spaceship.androidadvproject.providers.SoundContentProvider;
import javazoom.jl.converter.Converter;

/**
 * Downloads a sound asynchronously
 */
public class AsyncDownloader extends AsyncTask<Void, Long, Boolean> {

    public static final String SOUNDS_FOLDER = "sounds";

    private final DAMSound mDAMSound;
    private final Context mContext;
    private final Fragment mContextFragment;
    private File mFile;

    /**
     * Constructor, sets the file url.
     *
     * @param damSound DAMSound for the file we are downloading
     * @param context  Activity context
     */
    public AsyncDownloader(DAMSound damSound, Context context, Fragment contextFragment) {

        mDAMSound = damSound;
        mContext = context;
        mContextFragment = contextFragment;

    }

    @Override
    protected Boolean doInBackground(Void... params) {

        OkHttpClient httpClient = new OkHttpClient();

        Call call = httpClient.newCall(new Request.Builder().url(mDAMSound.getDownloadLink()).get().build());

        try {

            Response response = call.execute();

            if (response.code() == 200) {

                InputStream inputStream = null;
                OutputStream outputStream = null;

                try {

                    File folder = new File(mContext.getFilesDir() + "/" + SOUNDS_FOLDER);
                    boolean folderExists = folder.exists();
                    // Create a folder if it doesn't exist.
                    if (!folderExists) {
                        folderExists = folder.mkdirs();
                    }

                    if (folderExists) {
                        mFile = new File(folder, mDAMSound.getFormattedSoundId() + "." + mDAMSound.getFileExtension());
                        inputStream = response.body().byteStream();
                        outputStream = new FileOutputStream(mFile);

                        byte[] buffer = new byte[1024 * 4];
                        long downloaded = 0;
                        long totalSize = response.body().contentLength();

                        int len;
                        while ((len = inputStream.read(buffer)) > 0) {

                            // Write the buffer to the file.
                            outputStream.write(buffer, 0, len);

                            downloaded += len;

                            // Stop the task and return false, if cancel() is called.
                            if (isCancelled())
                                return false;

                        }

                        boolean downloadSuccess = downloaded == totalSize;
                        if (downloadSuccess) {
                            if (mFile.getName().toLowerCase().contains(".mp3")) {

                                // Convert to wav
                                try {
                                    Converter converter = new Converter();
                                    String withoutExtension = mFile.getName().substring(0, mFile.getName().lastIndexOf('.'));
                                    mDAMSound.setFileName(withoutExtension + ".wav");
                                    converter.convert(
                                            mContext.getFilesDir().getAbsolutePath() + "/" + SOUNDS_FOLDER +
                                                    "/" + mFile.getName(),
                                            mContext.getFilesDir().getAbsolutePath() + "/" + SOUNDS_FOLDER +
                                                    "/" + withoutExtension + ".wav"
                                    );
                                    mFile.delete();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            } else if (mFile.getName().toLowerCase().contains(".wav")) {
                                String withoutExtension = mFile.getName().substring(0, mFile.getName().lastIndexOf('.'));
                                mDAMSound.setFileName(withoutExtension + ".wav");
                            }
                        }

                        return downloadSuccess;
                    } else {
                        throw new IOException("Folder was not successfully created.");
                    }

                } catch (IOException ioE) {

                    return false;

                } finally {

                    if (inputStream != null) {

                        inputStream.close();

                    }

                    if (outputStream != null) {

                        outputStream.close();

                    }

                }

            } else {

                return false;

            }

        } catch (IOException e) {

            e.printStackTrace();
            return false;

        }

    }

    @Override
    protected void onPostExecute(Boolean result) {

        // Playing the sound after download (for now)
        if (result) {
            setFileMetaData();

            ((AsyncDownloaderListener) mContextFragment).onDownloadFinished(mDAMSound);

        }

    }

    @Override
    protected void onPreExecute() {

        if (mDAMSound != null) {

            String filename = mDAMSound.getFileName();

            if (filename != null) {
                Log.d("AsyncDownloader DEBUG", "File has been already downloaded!");
                ((AsyncDownloaderListener) mContextFragment).onDownloadFinished(mDAMSound);
                this.cancel(true);
            }

        }

    }

    /**
     * Set meta data for the sound file.
     * Saves the state in our ContentProvider
     */
    private void setFileMetaData() {

        if (mDAMSound != null) {

            ContentValues values = new ContentValues();
            values.put(DAMSoundEntry.COLUMN_NAME_SOUND_ID, mDAMSound.getFormattedSoundId());
            values.put(DAMSoundEntry.COLUMN_NAME_TITLE, mDAMSound.getTitle());
            values.put(DAMSoundEntry.COLUMN_NAME_CATEGORY, mDAMSound.getCategory().toString());
            values.put(DAMSoundEntry.COLUMN_NAME_TYPE, mDAMSound.getSoundType().toString());
            values.put(DAMSoundEntry.COLUMN_NAME_LENGTH_SEC, mDAMSound.getLengthSec());
            values.put(DAMSoundEntry.COLUMN_NAME_IS_FAVORITE, mDAMSound.getIsFavorite());
            values.put(DAMSoundEntry.COLUMN_NAME_FILE_NAME, mDAMSound.getFileName());
            values.put(DAMSoundEntry.COLUMN_NAME_URL, mDAMSound.getDownloadLink());
            values.put(DAMSoundEntry.COLUMN_NAME_FILE_EXT, mDAMSound.getFileExtension());

            // Insert or update meta data, depending on whether the sound already exists
            // in the database.
            mContext.getContentResolver().insert(SoundContentProvider.CONTENT_URI, values);
        }
    }

}
