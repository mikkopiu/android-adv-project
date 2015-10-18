package fi.metropolia.yellow_spaceship.androidadvproject;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;

/**
 * Created by Petri on 18.10.2015.
 */
public class RecordActivity extends AppCompatActivity {

    private TextView mRecordTimer;
    private ImageButton mRecordButton;
    private Button mPlayButton;
    private long mStartTime = 0;
    private boolean mRecording = false;
    private boolean mPlaying = false;
    private MediaRecorder mRecorder = null;
    private MediaPlayer mPlayer = null;

    private String mDefaultFileName = "untitled-recording.mp3";

    Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable() {

        @Override
        public void run() {

            long millis = System.currentTimeMillis() - mStartTime;
            int seconds = (int)(millis / 1000);
            int minutes = seconds / 60;
            seconds = seconds % 60;

            mRecordTimer.setText(String.format("%02d:%02d", minutes, seconds));

            timerHandler.postDelayed(this, 500);

        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_record);

        mRecordTimer = (TextView)findViewById(R.id.record_timer);
        mRecordButton = (ImageButton)findViewById(R.id.record_button);
        mPlayButton = (Button)findViewById(R.id.play_btn);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
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

    }

    View.OnClickListener clickListener = new View.OnClickListener() {
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.record_button:
                    if(mRecording == false && mPlaying == false) {
                        mRecording = true;
                        startRecording();
                        mStartTime = System.currentTimeMillis();
                        timerHandler.postDelayed(timerRunnable, 0);
                        mRecordButton.setImageResource(R.drawable.ic_stop_white_64dp);
                    } else if(mPlaying == false) {
                        mRecording = false;
                        stopRecording();
                        timerHandler.removeCallbacks(timerRunnable);
                        mRecordButton.setImageResource(R.drawable.ic_mic_white_64dp);
                    }
                    break;
                case R.id.play_btn:
                    if(mRecording == true)
                        break;
                    if(mPlaying == false)
                        startPlaying();
                    else
                        stopPlaying();
                    break;
                default:
                    break;
            }
        }
    };

    private void startPlaying() {

        mPlayButton.setText("STOP");
        mPlaying = true;
        mPlayer = new MediaPlayer();
        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                stopPlaying();
            }
        });
        try {
            mPlayer.setDataSource(getFilesDir().getAbsolutePath() + "/recordings/" + mDefaultFileName);
            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void stopPlaying() {

        mPlayButton.setText("PLAY");
        mPlaying = false;
        mPlayer.release();
        mPlayer = null;

    }

    private void startRecording() {

        File folder = new File(getFilesDir() + "/recordings");
        if(!folder.exists()) {
            folder.mkdirs();
        }

        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mRecorder.setOutputFile(folder.getAbsolutePath() + "/" + mDefaultFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mRecorder.start();

    }

    private void stopRecording() {

        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;

    }

    @Override
    public void onPause() {

        super.onPause();

        if(mRecorder != null) {
            mRecorder.release();
            mRecorder = null;
        }

        if(mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }


    }

}
