package fi.metropolia.yellow_spaceship.androidadvproject;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionMenu;

import java.io.File;
import java.util.ArrayList;

import fi.metropolia.yellow_spaceship.androidadvproject.adapters.SoundCardViewAdapter;
import fi.metropolia.yellow_spaceship.androidadvproject.models.DAMSound;
import fi.metropolia.yellow_spaceship.androidadvproject.models.ProjectSound;
import fi.metropolia.yellow_spaceship.androidadvproject.models.SoundScapeProject;
import fi.metropolia.yellow_spaceship.androidadvproject.sounds.SoundPlayer;
import fi.metropolia.yellow_spaceship.androidadvproject.tasks.ProjectSaveTask;
import fi.metropolia.yellow_spaceship.androidadvproject.tasks.SaveListener;

public class CreateSoundscapeActivity extends AppCompatActivity {

    public final static int GET_LIBRARY_SOUND = 1;
    public final static int RECORD_SOUND = 2;

    private SoundScapeProject mProject;
    private RecyclerView recyclerView;
    private SoundCardViewAdapter adapter;
    private GridLayoutManager layoutManager;
    private Dialog mDialog;
    private EditText mDialogEditText;
    private ProgressDialog mProgress;

    private FloatingActionMenu fabMenu;

    private boolean mIsSaving = false;
    private boolean mIsPlaying = false;

    private SoundPlayer soundPlayer;

    View.OnClickListener clickListener = new View.OnClickListener() {
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.dialog_cancel_btn:
                    mDialog.dismiss();
                    break;
                case R.id.dialog_save_btn:
                    save();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_soundscape);

        soundPlayer = new SoundPlayer(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getResources().getString(R.string.create_soundscape_title));

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateSoundscapeActivity.this.onBackPressed();
            }
        });

        // RecyclerView setup
        this.recyclerView = (RecyclerView) findViewById(R.id.create_recycler_view);
        this.recyclerView.setHasFixedSize(false);

        // TODO: load previous project automatically?
        if (getIntent().getParcelableExtra("loadedSoundscape") != null) {
            this.mProject = getIntent().getParcelableExtra("loadedSoundscape");
            for(ProjectSound ps : mProject.getSounds()) {
                ps.setFile(new File(getFilesDir().getAbsolutePath() + "/sounds/" + ps.getFileName()));
                soundPlayer.addSound(ps);
            }
        } else {
            this.mProject = new SoundScapeProject();
        }

        this.layoutManager = new GridLayoutManager(this, 2);
        this.recyclerView.setLayoutManager(this.layoutManager);

        this.adapter = new SoundCardViewAdapter(this.mProject.getSounds(), new SoundCardViewAdapter.ViewHolder.IProjectSoundViewHolderClicks() {

            @Override
            public void onCloseClicked(View view, int layoutPosition) {
                try {
                    mProject.removeSound(layoutPosition);
                    soundPlayer.removeSound(layoutPosition);
                    recyclerView.getAdapter().notifyDataSetChanged();
                } catch (IndexOutOfBoundsException e) {
                    e.printStackTrace();
                    Toast.makeText(
                            getApplicationContext(),
                            "Something went wrong, please try again",
                            Toast.LENGTH_SHORT
                    ).show();
                }
            }
        });
        this.recyclerView.setAdapter(adapter);

        // Set FAB listeners
        findViewById(R.id.menu_item_record).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Record sound clicked", Toast.LENGTH_SHORT).show();
                addRecording();
            }
        });
        findViewById(R.id.menu_item_library).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Sound library clicked", Toast.LENGTH_SHORT).show();
                addLibrarySound();
            }
        });

        this.fabMenu = (FloatingActionMenu) findViewById(R.id.add_menu);

        // Play/pause button
        findViewById(R.id.create_play_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: playback toggle
                Toast.makeText(getApplicationContext(), "Play button clicked", Toast.LENGTH_SHORT).show();

                if(mIsPlaying)
                    soundPlayer.stopAll();
                else
                    soundPlayer.playAll();

            }
        });

        // Save button
        findViewById(R.id.create_save_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDialog == null) {
                    setupDialog();
                }
                mDialog.show();
            }
        });
    }

    /**
     * Start an Intent to add a new sound from the Sound Library
     */
    private void addLibrarySound() {
        Intent intent = new Intent(getApplicationContext(), SoundLibraryActivity.class);
        intent.putExtra("requestCode", GET_LIBRARY_SOUND);
        startActivityForResult(intent, GET_LIBRARY_SOUND);
    }

    /**
     * Start an Intent to add a new recording from the Recording view
     */
    private void addRecording() {
        Intent intent = new Intent(getApplicationContext(), RecordActivity.class);
        intent.putExtra("requestCode", RECORD_SOUND);
        startActivityForResult(intent, RECORD_SOUND);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == GET_LIBRARY_SOUND) {
            if(resultCode == Activity.RESULT_OK){
                // TODO: handle multi-select
                DAMSound result = data.getExtras().getParcelable("result");
                try {
                    Toast.makeText(
                            getApplicationContext(),
                            result.getTitle() + " selected",
                            Toast.LENGTH_SHORT
                    ).show();

                    ProjectSound ps = new ProjectSound(
                            result.getFormattedSoundId(),
                            result.getTitle(),
                            result.getCategory(),
                            result.getSoundType(),
                            result.getFileName(),
                            true,       // By default on loop
                            false,
                            1.0f        // By default on full volume
                    );

                    ps.setFile(new File(getFilesDir().getAbsolutePath() + "/sounds/" + ps.getFileName()));

                    this.mProject.addSound(ps);

                    soundPlayer.addSound(ps);

                    // Refresh card view list
                    this.recyclerView.getAdapter().notifyDataSetChanged();

                } catch (NullPointerException e) {
                    e.printStackTrace();
                    Toast.makeText(
                            getApplicationContext(),
                            "Something went wrong, please try another sound",
                            Toast.LENGTH_SHORT
                    ).show();
                }
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
                Toast.makeText(
                        getApplicationContext(),
                        "Sound Library Activity cancelled",
                        Toast.LENGTH_SHORT
                ).show();
            }

            fabMenu.close(false);
        } else if (requestCode == RECORD_SOUND) {
            // TODO: react to new sound recordings
        }
    }

    /**
     * Save the current project to file
     */
    private void save() {
        if(!mIsSaving) {
            String fileName = mDialogEditText.getText().toString().trim();

            mIsSaving = true;
            this.mProgress = new ProgressDialog(this);
            this.mProgress.setMessage("Saving...");
            this.mProgress.show();
            this.mProject.setName(fileName);
            new ProjectSaveTask(this.getApplicationContext(), new SaveListener() {
                @Override
                public void onSaveComplete() {
                    Toast.makeText(getApplicationContext(), "Project saved successfully", Toast.LENGTH_SHORT)
                            .show();
                    mIsSaving = false;
                    if (mProgress.isShowing()) {
                        mProgress.cancel();
                    }

                    mDialog.dismiss();
                }
            }).execute(this.mProject);
        }
    }

    private void setupDialog() {
        mDialog = new Dialog(CreateSoundscapeActivity.this);
        mDialog.setContentView(R.layout.create_save_dialog);
        mDialog.setTitle("Save");
        Button mDialogSaveBtn = (Button)mDialog.findViewById(R.id.dialog_save_btn);
        Button mDialogCancelBtn = (Button)mDialog.findViewById(R.id.dialog_cancel_btn);
        mDialogEditText = (EditText)mDialog.findViewById(R.id.input_name);
        mDialogSaveBtn.setOnClickListener(clickListener);
        mDialogCancelBtn.setOnClickListener(clickListener);

        mDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

}
