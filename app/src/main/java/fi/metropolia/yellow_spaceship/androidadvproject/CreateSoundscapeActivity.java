package fi.metropolia.yellow_spaceship.androidadvproject;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionMenu;

import java.util.ArrayList;

import fi.metropolia.yellow_spaceship.androidadvproject.adapters.SoundCardViewAdapter;
import fi.metropolia.yellow_spaceship.androidadvproject.database.DAMSoundContract.DAMSoundEntry;
import fi.metropolia.yellow_spaceship.androidadvproject.models.DAMSound;
import fi.metropolia.yellow_spaceship.androidadvproject.models.ProjectSound;
import fi.metropolia.yellow_spaceship.androidadvproject.models.SoundCategory;
import fi.metropolia.yellow_spaceship.androidadvproject.models.SoundScapeProject;
import fi.metropolia.yellow_spaceship.androidadvproject.models.SoundType;
import fi.metropolia.yellow_spaceship.androidadvproject.providers.SoundContentProvider;

public class CreateSoundscapeActivity extends AppCompatActivity {

    public final static int GET_LIBRARY_SOUND = 1;
    public final static int RECORD_SOUND = 2;

    private SoundScapeProject mProject;
    private RecyclerView recyclerView;
    private SoundCardViewAdapter adapter;
    private GridLayoutManager layoutManager;

    private FloatingActionMenu fabMenu;

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

        // RecyclerView setup
        this.recyclerView = (RecyclerView) findViewById(R.id.create_recycler_view);
        this.recyclerView.setHasFixedSize(false);

        // TODO: load previous project automatically?
        this.mProject = new SoundScapeProject();

        this.layoutManager = new GridLayoutManager(this, 2);
        this.recyclerView.setLayoutManager(this.layoutManager);

        this.adapter = new SoundCardViewAdapter(this.mProject.getSounds(), new SoundCardViewAdapter.ViewHolder.IProjectSoundViewHolderClicks() {

            @Override
            public void onCloseClicked(View view, int layoutPosition) {
                try {
                    mProject.removeSound(layoutPosition);
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
            }
        });

        // Save button
        findViewById(R.id.create_save_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: saving
                Toast.makeText(getApplicationContext(), "Save button clicked", Toast.LENGTH_SHORT).show();
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

                    this.mProject.addSound(new ProjectSound(
                            result.getFormattedSoundId(),
                            result.getTitle(),
                            result.getCategory(),
                            result.getSoundType(),
                            result.getFileName(),
                            true,       // By default on loop
                            false,
                            1.0f        // By default on full volume
                    ));

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

}
