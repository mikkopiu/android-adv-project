package fi.metropolia.yellow_spaceship.androidadvproject;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionMenu;
import com.github.clans.fab.FloatingActionButton;

public class CreateSoundscapeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_soundscape);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getResources().getString(R.string.create_soundscape_title));

        Toolbar bottomToolbar = (Toolbar) findViewById(R.id.bottom_toolbar);
        ViewCompat.setElevation(bottomToolbar, 4.0f);

        FloatingActionMenu menu = (FloatingActionMenu) findViewById(R.id.add_menu);
        ViewCompat.setElevation(menu, 6.0f);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateSoundscapeActivity.this.onBackPressed();
            }
        });

        FloatingActionButton fabRecBtn = (FloatingActionButton) findViewById(R.id.menu_item_record);
        FloatingActionButton fabLibBtn = (FloatingActionButton) findViewById(R.id.menu_item_library);

        fabRecBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: open library with Intent for Recordings
                Toast.makeText(getApplicationContext(), "Record sound clicked", Toast.LENGTH_SHORT).show();
            }
        });

        fabLibBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: open library with Intent for sounds
                Toast.makeText(getApplicationContext(), "Sound library clicked", Toast.LENGTH_SHORT).show();
            }
        });

    }

}
