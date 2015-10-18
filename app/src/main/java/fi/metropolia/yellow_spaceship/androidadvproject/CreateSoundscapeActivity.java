package fi.metropolia.yellow_spaceship.androidadvproject;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

import fi.metropolia.yellow_spaceship.androidadvproject.adapters.SoundCardViewAdapter;
import fi.metropolia.yellow_spaceship.androidadvproject.database.DAMSoundContract.DAMSoundEntry;
import fi.metropolia.yellow_spaceship.androidadvproject.models.DAMSound;
import fi.metropolia.yellow_spaceship.androidadvproject.models.SoundCategory;
import fi.metropolia.yellow_spaceship.androidadvproject.models.SoundType;
import fi.metropolia.yellow_spaceship.androidadvproject.providers.SoundContentProvider;

public class CreateSoundscapeActivity extends AppCompatActivity {

    private ArrayList<DAMSound> mData;
    private RecyclerView recyclerView;
    private SoundCardViewAdapter adapter;
    private GridLayoutManager layoutManager;

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

        // TODO: replace data with actual content
        this.mData = new ArrayList<>();
        Cursor cursor = getApplicationContext().getContentResolver().query(
                SoundContentProvider.CONTENT_URI,
                new String[]{
                        DAMSoundEntry.COLUMN_NAME_TITLE,
                        DAMSoundEntry.COLUMN_NAME_CATEGORY,
                        DAMSoundEntry.COLUMN_NAME_TYPE,
                        DAMSoundEntry.COLUMN_NAME_FILE_NAME
                },
                null,
                null,
                null
        );
        if (cursor != null) {
            while (cursor.moveToNext()) {
                DAMSound s = new DAMSound();
                s.setTitle(cursor.getString(0));
                s.setCategory(SoundCategory.fromApi(cursor.getString(1)));
                s.setSoundType(SoundType.fromApi(cursor.getString(2)));
                s.setFileName(cursor.getString(3));
                this.mData.add(s);
                this.mData.add(s);
                this.mData.add(s);
            }

            cursor.close();
        }

        this.layoutManager = new GridLayoutManager(this, 2);
        this.recyclerView.setLayoutManager(this.layoutManager);

        this.adapter = new SoundCardViewAdapter(this.mData);
        this.recyclerView.setAdapter(adapter);


        // Set FAB listeners
        findViewById(R.id.menu_item_record).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: open library with Intent for Recordings
                Toast.makeText(getApplicationContext(), "Record sound clicked", Toast.LENGTH_SHORT).show();
            }
        });
        findViewById(R.id.menu_item_library).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: open library with Intent for sounds
                Toast.makeText(getApplicationContext(), "Sound library clicked", Toast.LENGTH_SHORT).show();
            }
        });

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

}
