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
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionMenu;

import java.io.File;

import fi.metropolia.yellow_spaceship.androidadvproject.adapters.SoundCardViewAdapter;
import fi.metropolia.yellow_spaceship.androidadvproject.adapters.SoundCardViewAdapter.ViewHolder.IProjectSoundViewHolderClicks;
import fi.metropolia.yellow_spaceship.androidadvproject.models.DAMSound;
import fi.metropolia.yellow_spaceship.androidadvproject.models.ProjectSound;
import fi.metropolia.yellow_spaceship.androidadvproject.models.SoundScapeProject;
import fi.metropolia.yellow_spaceship.androidadvproject.sounds.SoundPlayer;
import fi.metropolia.yellow_spaceship.androidadvproject.tasks.ProjectSaveTask;
import fi.metropolia.yellow_spaceship.androidadvproject.tasks.SaveListener;

public class CreateSoundscapeActivity extends AppCompatActivity {

    public final static int GET_LIBRARY_SOUND = 1;
    public final static int RECORD_SOUND = 2;

    private final static String UNSAVED_PROJECT_BUNDLE_NAME = "unsaved_project";

    private SoundScapeProject mProject;
    private RecyclerView recyclerView;
    private Dialog mDialog;
    private EditText mDialogEditText;
    private ProgressDialog mProgress;

    private FloatingActionMenu fabMenu;

    private boolean mIsSaving = false;
    private boolean mIsPlaying = false;

    private SoundPlayer soundPlayer;

    /**
     * Unified click handler for the main UI-buttons
     */
    private final View.OnClickListener clickListener = new View.OnClickListener() {
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.dialog_cancel_btn:
                    mDialog.dismiss();
                    break;
                case R.id.dialog_save_btn:
                    String str = mDialogEditText.getText().toString().trim();
                    if (str.equals("")) {
                        mDialogEditText.setError("Name is required!");
                        break;
                    }
                    save(str);
                    break;
                case R.id.create_play_btn:
                    if (mProject.getSounds().size() > 0) {
                        if (mIsPlaying) {
                            stopPlayback();
                        } else {
                            startPlayback();
                        }
                    } else {
                        Toast.makeText(
                                getApplicationContext(),
                                "Add at least one sound to your soundscape",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                    break;
                case R.id.create_save_btn:
                    if (mDialog == null) {
                        setupDialog();
                    }
                    mDialog.show();
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * Listener for FAB menu-item clicks
     */
    private final View.OnClickListener fabItemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.menu_item_library:
                    addLibrarySound();
                    break;
                case R.id.menu_item_record:
                    addRecording();
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * Listener for ProjectSound ViewHolder clicks
     */
    private final IProjectSoundViewHolderClicks adapterOnClickListener =
            new IProjectSoundViewHolderClicks() {

                @Override
                public void onCloseClicked(View view, int layoutPosition) {
                    try {
                        mProject.removeSound(layoutPosition);
                        soundPlayer.removeSound(layoutPosition);
                        recyclerView.getAdapter().notifyDataSetChanged();

                        if (mProject.getSounds().size() == 0 && mIsPlaying) {
                            // No more sounds to play, stop playback
                            stopPlayback();
                        }

                    } catch (IndexOutOfBoundsException e) {
                        e.printStackTrace();
                        Toast.makeText(
                                getApplicationContext(),
                                "Something went wrong, please try again",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                }

                @Override
                public void onVolumeChange(SeekBar seekBar, int layoutPosition, int progress) {
                    float newVol = (float) progress / 100.0f; // SeekBar has values from 0-100 (int)

                    // soundPlayer, mProject and recyclerView should have matching indexes
                    // for the sound items.
                    soundPlayer.setVolume(layoutPosition, newVol);
                }

                @Override
                public void onRandomizeCheckedChange(CompoundButton button, int layoutPosition, boolean checked) {
                    ProjectSound sound = mProject.getSound(layoutPosition);

                    if (sound != null) {

                        if(!checked) {
                            soundPlayer.changeToLoop(layoutPosition);
                        } else if(checked) {
                            soundPlayer.changeToRandom(layoutPosition);
                        }

                        //sound.setIsOnLoop(!checked);
                        //sound.setIsRandom(checked);
                    } else {
                        Log.e(
                                "CreateSoundscape DEBUG",
                                "Lost reference to sound, cannot change random-state!"
                        );
                    }
                }
            };


    /**
     * Unified save-event handler
     */
    private final SaveListener saveListener = new SaveListener() {
        @Override
        public void onSaveComplete(boolean success) {
            if (success) {
                Toast.makeText(
                        getApplicationContext(),
                        "Project saved successfully",
                        Toast.LENGTH_SHORT
                ).show();
                mIsSaving = false;
                if (mProgress.isShowing()) {
                    mProgress.cancel();
                }

                mDialog.dismiss();
            } else {
                Toast.makeText(
                        getApplicationContext(),
                        "Couldn't save project, please try another name",
                        Toast.LENGTH_SHORT
                ).show();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_create_soundscape);

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

        soundPlayer = new SoundPlayer(this);

        if (savedInstanceState != null) {
            this.mProject = savedInstanceState.getParcelable(UNSAVED_PROJECT_BUNDLE_NAME);
        }

        if (this.mProject == null) {
            initProject();
        } else {
            // An existing project was found => reload the sounds to the SoundPlayer instance
            this.soundPlayer.addSounds(
                    this.mProject.getSounds().toArray(
                            new ProjectSound[this.mProject.getSounds().size()]
                    )
            );
        }
        initRecyclerView();

        // Set FAB listeners
        findViewById(R.id.menu_item_record).setOnClickListener(fabItemClickListener);
        findViewById(R.id.menu_item_library).setOnClickListener(fabItemClickListener);

        this.fabMenu = (FloatingActionMenu) findViewById(R.id.add_menu);

        // Play/pause button
        findViewById(R.id.create_play_btn).setOnClickListener(clickListener);

        // Save button
        findViewById(R.id.create_save_btn).setOnClickListener(clickListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mIsPlaying && soundPlayer != null) {
            stopPlayback();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(UNSAVED_PROJECT_BUNDLE_NAME, this.mProject);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mIsPlaying && soundPlayer != null) {
            stopPlayback();
            soundPlayer.clear();
        }
    }

    /**
     * Start project playback
     */
    private void startPlayback() {
        fabMenu.setKeepScreenOn(true);
        soundPlayer.playAll();
        mIsPlaying = true;
        ((ImageButton) findViewById(R.id.create_play_btn))
                .setImageResource(R.drawable.ic_stop_white_48dp);
    }

    /**
     * Stop project playback
     */
    private void stopPlayback() {
        fabMenu.setKeepScreenOn(false);
        soundPlayer.stopAll();
        mIsPlaying = false;
        ((ImageButton) findViewById(R.id.create_play_btn))
                .setImageResource(R.drawable.ic_play_arrow_white_48dp);
    }

    /**
     * Initialize the SoundScapeProject, either from a loaded SoundScapeProject or a new one
     * TODO: auto-load previous project
     */
    private void initProject() {
        if (getIntent().getParcelableExtra("loadedSoundscape") != null) {
            this.mProject = getIntent().getParcelableExtra("loadedSoundscape");

            for (ProjectSound ps : mProject.getSounds()) {
                ps.setFile(new File(getFilesDir().getAbsolutePath() + "/sounds/" + ps.getFileName()));
                soundPlayer.addSound(ps);
            }

        } else {
            this.mProject = new SoundScapeProject();
        }
    }

    /**
     * Initialize the RecyclerView with data.
     * Assumes mProject has been initialized.
     */
    private void initRecyclerView() {
        this.recyclerView = (RecyclerView) findViewById(R.id.create_recycler_view);
        this.recyclerView.setHasFixedSize(false); // Soundscapes can be deleted & renamed

        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        this.recyclerView.setLayoutManager(layoutManager);

        SoundCardViewAdapter adapter = new SoundCardViewAdapter(this.mProject.getSounds(), adapterOnClickListener);
        this.recyclerView.setAdapter(adapter);
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

        if (requestCode == GET_LIBRARY_SOUND || requestCode == RECORD_SOUND) {
            if (resultCode == Activity.RESULT_OK) {
                // TODO: handle multi-select
                DAMSound result = data.getExtras().getParcelable("result");
                addSelectedSound(result);
            }

            fabMenu.close(false);
        }
    }

    private void addSelectedSound(DAMSound result) {
        try {
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

    /**
     * Save the current project to file
     */
    private void save(String fileName) {
        if (!mIsSaving) {

            mIsSaving = true;
            this.mProgress = new ProgressDialog(this);
            this.mProgress.setMessage("Saving...");
            this.mProgress.show();
            this.mProject.setName(fileName);
            new ProjectSaveTask(this.getApplicationContext(), saveListener).execute(this.mProject);
        }
    }

    private void setupDialog() {
        mDialog = new Dialog(CreateSoundscapeActivity.this);
        mDialog.setContentView(R.layout.create_save_dialog);
        mDialog.setTitle("Save");
        Button mDialogSaveBtn = (Button) mDialog.findViewById(R.id.dialog_save_btn);
        Button mDialogCancelBtn = (Button) mDialog.findViewById(R.id.dialog_cancel_btn);
        mDialogEditText = (EditText) mDialog.findViewById(R.id.input_name);
        mDialogSaveBtn.setOnClickListener(clickListener);
        mDialogCancelBtn.setOnClickListener(clickListener);

        String prevName = this.mProject.getName();
        if (prevName != null && !prevName.equals("")) {
            mDialogEditText.setText(prevName);
        }

        mDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

}
