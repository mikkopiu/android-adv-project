package fi.metropolia.yellow_spaceship.androidadvproject;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;

public class CreateSoundscapeActivity extends AppCompatActivity {

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

        findViewById(R.id.create_play_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: playback toggle
                Toast.makeText(getApplicationContext(), "Play button clicked", Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.create_save_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: saving
                Toast.makeText(getApplicationContext(), "Save button clicked", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
