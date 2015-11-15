package fi.metropolia.yellow_spaceship.androidadvproject;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.nio.channels.FileChannel;
import java.util.Date;

import fi.metropolia.yellow_spaceship.androidadvproject.database.DAMSoundContract.DAMSoundEntry;
import fi.metropolia.yellow_spaceship.androidadvproject.managers.SessionManager;
import fi.metropolia.yellow_spaceship.androidadvproject.models.DAMSound;
import fi.metropolia.yellow_spaceship.androidadvproject.models.SoundCategory;
import fi.metropolia.yellow_spaceship.androidadvproject.models.SoundType;
import fi.metropolia.yellow_spaceship.androidadvproject.providers.SoundContentProvider;
import fi.metropolia.yellow_spaceship.androidadvproject.sounds.SoundRecorder;

/**
 * Activity for recording sounds.
 */
public class RecordActivity extends AppCompatActivity {

    private TextView mRecordTimer;
    private ImageButton mRecordButton;
    private Button mPlayButton;
    private long mStartTime = 0;
    private boolean mRecording = false;
    private boolean mPlaying = false;
    private MediaRecorder mRecorder = null;
    private SoundRecorder mSoundRecorder = null;
    private MediaPlayer mPlayer = null;
    private SessionManager session;

    private Dialog mDialog = null;
    private AppCompatSpinner mDialogSpinner = null;
    private EditText mDialogEditText = null;

    private final static String DEFAULT_FILE_NAME = "untitled-recording.wav";
    private final static String DEFAULT_FOLDER = "sounds";
    private boolean mIsSaving = false;
    private boolean mTempFileExists = false;
    private int mLatestSeconds = 0;

    private MessageHandler msgHandler;

    /**
     * Handler and runnable for recording timer
     */
    private final Handler timerHandler = new Handler();
    private final Runnable timerRunnable = new Runnable() {

        @Override
        public void run() {

            long millis = System.currentTimeMillis() - mStartTime;
            int seconds = (int) (millis / 1000);
            mLatestSeconds = seconds;
            int minutes = seconds / 60;
            seconds = seconds % 60;

            mRecordTimer.setText(String.format("%02d:%02d", minutes, seconds));

            timerHandler.postDelayed(this, 500);

        }

    };

    /**
     * Copies temp file in another thread into a new file with a proper filename
     */
    private final Runnable copyFileRunnable = new Runnable() {

        @Override
        public void run() {

            /**
             * File copy
             */

            DAMSound damSound = new DAMSound();
            damSound.setTitle(mDialogEditText.getText().toString());
            damSound.setCategory(SoundCategory.fromApi(mDialogSpinner.getSelectedItem().toString().toLowerCase()));
            damSound.setLengthSec(mLatestSeconds);
            damSound.setSoundType(SoundType.EFFECT);
            damSound.setIsFavorite(false);
            damSound.setIsRecording(true);
            damSound.setFileExtension("wav");
            damSound.setCollectionID(session.getCollectionID());
            damSound.setCreationDate(new Date());
            damSound.setFileName(damSound.getFormattedSoundId() + "." + damSound.getFileExtension());

            File tempFile = new File(getFilesDir() + "/" + DEFAULT_FOLDER + "/" + DEFAULT_FILE_NAME);
            if (!tempFile.exists()) {
                return;
            }

            File newFile = new File(getFilesDir() + "/" + DEFAULT_FOLDER + "/" + damSound.getFormattedSoundId() + "." + damSound.getFileExtension());

            try {
                FileInputStream is = new FileInputStream(tempFile);
                FileOutputStream os = new FileOutputStream(newFile);
                FileChannel ic = is.getChannel();
                FileChannel oc = os.getChannel();
                ic.transferTo(0, ic.size(), oc);
                is.close();
                os.close();
                mDialog.dismiss();
            } catch (IOException e) {
                e.printStackTrace();
            }

            /**
             * Database entry
             */

            ContentValues values = new ContentValues();
            values.put(DAMSoundEntry.COLUMN_NAME_TITLE, damSound.getTitle());
            values.put(DAMSoundEntry.COLUMN_NAME_CATEGORY, damSound.getCategory().toString());
            values.put(DAMSoundEntry.COLUMN_NAME_LENGTH_SEC, damSound.getLengthSec());
            values.put(DAMSoundEntry.COLUMN_NAME_IS_FAVORITE, damSound.getIsFavorite());
            values.put(DAMSoundEntry.COLUMN_NAME_TYPE, damSound.getSoundType().toString());
            values.put(DAMSoundEntry.COLUMN_NAME_IS_RECORDING, damSound.getIsRecording());
            values.put(DAMSoundEntry.COLUMN_NAME_FILE_NAME, damSound.getFileName());
            values.put(DAMSoundEntry.COLUMN_NAME_SOUND_ID, damSound.getFormattedSoundId());

            RecordActivity.this.getContentResolver().insert(SoundContentProvider.CONTENT_URI, values);

            mIsSaving = false;

            Message msg = msgHandler.obtainMessage();
            msg.obj = "File saved";
            msgHandler.sendMessage(msg);

            deleteTempFile();

            // Return damSound if we have a intent from CreateSoundScapeActivity.
            Intent intent = RecordActivity.this.getIntent();
            if (intent.getIntExtra("requestCode", 0) == CreateSoundscapeActivity.RECORD_SOUND) {

                // Return intent
                Intent returnIntent = new Intent();
                returnIntent.putExtra("result", damSound);
                RecordActivity.this.setResult(Activity.RESULT_OK, returnIntent);
                RecordActivity.this.finish();

            }

        }

    };

    private void deleteTempFile() {
        File file = new File(RecordActivity.this.getFilesDir().getAbsolutePath() + "/" + DEFAULT_FOLDER + "/" + DEFAULT_FILE_NAME);
        if (file.exists()) {
            file.delete();
            mTempFileExists = false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        session = new SessionManager(this);

        deleteTempFile();

        setContentView(R.layout.activity_record);

        mRecordTimer = (TextView) findViewById(R.id.record_timer);
        mRecordButton = (ImageButton) findViewById(R.id.record_button);
        mPlayButton = (Button) findViewById(R.id.play_btn);
        Button mSaveButton = (Button) findViewById(R.id.save_btn);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getResources().getString(R.string.record_title));
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecordActivity.this.onBackPressed();
            }
        });

        mRecordButton.setOnClickListener(clickListener);
        mPlayButton.setOnClickListener(clickListener);
        mSaveButton.setOnClickListener(clickListener);

        msgHandler = new MessageHandler(this);
    }

    private final View.OnClickListener clickListener = new View.OnClickListener() {
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.record_button:
                    if (!mRecording && !mPlaying) {
                        mRecordButton.setImageResource(R.drawable.ic_stop_white_64dp);
                        mRecording = true;
                        startRecording();
                        mStartTime = System.currentTimeMillis();
                        timerHandler.postDelayed(timerRunnable, 0);
                    } else if (!mPlaying) {
                        mRecordButton.setImageResource(R.drawable.ic_mic_white_64dp);
                        mRecording = false;
                        stopRecording();
                        timerHandler.removeCallbacks(timerRunnable);
                    }
                    break;
                case R.id.play_btn:
                    if (mRecording || !mTempFileExists)
                        break;
                    if (!mPlaying)
                        startPlaying();
                    else
                        stopPlaying();
                    break;
                case R.id.save_btn:
                    if (mDialog == null) {
                        setupDialog();
                    }
                    mDialog.show();
                    break;
                case R.id.dialog_cancel_btn:
                    mDialog.dismiss();
                    break;
                case R.id.dialog_save_btn:
                    saveRecording();
                    break;
                default:
                    break;
            }
        }
    };

    private void saveRecording() {

        if (!mIsSaving) {
            mIsSaving = true;
            new Thread(copyFileRunnable).start();
        }

    }

    /**
     * Setups the save dialog view.
     */
    private void setupDialog() {

        mDialog = new Dialog(RecordActivity.this);
        mDialog.setContentView(R.layout.recording_save_dialog);
        mDialog.setTitle(getResources().getString(R.string.record_dialog_title));
        Button mDialogSaveBtn = (Button) mDialog.findViewById(R.id.dialog_save_btn);
        Button mDialogCancelBtn = (Button) mDialog.findViewById(R.id.dialog_cancel_btn);
        mDialogSpinner = (AppCompatSpinner) mDialog.findViewById(R.id.spinner_category);
        mDialogEditText = (EditText) mDialog.findViewById(R.id.input_filename);
        mDialogSaveBtn.setOnClickListener(clickListener);
        mDialogCancelBtn.setOnClickListener(clickListener);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.categories_array, R.layout.spinner_item);
        mDialogSpinner.setAdapter(adapter);

        mDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

    }

    private void startPlaying() {

        mPlayButton.setText(R.string.record_stop_btn);
        mPlaying = true;
        mPlayer = new MediaPlayer();
        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                stopPlaying();
            }
        });
        try {
            mPlayer.setDataSource(getFilesDir().getAbsolutePath() + "/" + DEFAULT_FOLDER + "/" + DEFAULT_FILE_NAME);
            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void stopPlaying() {

        mPlayButton.setText(R.string.record_play_btn);
        mPlaying = false;
        mPlayer.release();
        mPlayer = null;

    }

    private void startRecording() {

        if (mSoundRecorder != null) {
            mSoundRecorder = null;
        }

        mLatestSeconds = 0;
        mTempFileExists = true;

        File folder = new File(getFilesDir() + "/" + DEFAULT_FOLDER);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        File defaultFile = new File(getFilesDir().getAbsolutePath() + "/" + DEFAULT_FOLDER + "/" + DEFAULT_FILE_NAME);
        if (defaultFile.exists()) {
            defaultFile.delete();
        }

        mSoundRecorder = new SoundRecorder(defaultFile);

        mSoundRecorder.startRecording();

        /*
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
        mRecorder.setOutputFile(folder.getAbsolutePath() + "/" + DEFAULT_FILE_NAME);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mRecorder.start();
        */

    }

    private void stopRecording() {

        mSoundRecorder.stopRecording();
        mSoundRecorder = null;

    }

    @Override
    public void onPause() {

        super.onPause();

        if (mRecorder != null) {
            mRecorder.release();
            mRecorder = null;
        }

        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }


    }

    private static class MessageHandler extends Handler {
        private final WeakReference<RecordActivity> mTarget;

        public MessageHandler(RecordActivity context) {
            mTarget = new WeakReference<>(context);

        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String str = (String) msg.obj;
            Toast toast = Toast.makeText(mTarget.get(), str, Toast.LENGTH_SHORT);
            toast.show();

            mTarget.get().mRecordTimer.setText(R.string.record_default_timer);
        }

    }

}
