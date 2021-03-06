package fi.metropolia.yellow_spaceship.androidadvproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import fi.metropolia.yellow_spaceship.androidadvproject.managers.SessionManager;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final SessionManager session = new SessionManager(this);

        // Check login status and redirect to LoginActivity if necessary
        session.checkLogin();

        // Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getResources().getString(R.string.toolbar_title));
        toolbar.inflateMenu(R.menu.toolbar_menu);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch(item.getItemId()){
                    case R.id.action_logout:
                        session.logoutUser();
                        break;
                }
                return true;
            }
        });

        // Incontext navigation click events
        View incontextCreateSoundscape = findViewById(R.id.incontext_create_soundscape);
        View incontextSoundLibrary = findViewById(R.id.incontext_sound_library);
        View incontextYourSoundscapes = findViewById(R.id.incontext_your_soundscapes);
        View incontextMuseumTour = findViewById(R.id.incontext_museum_tour);

        incontextCreateSoundscape.setOnClickListener(incontextButtonListener);
        incontextSoundLibrary.setOnClickListener(incontextButtonListener);
        incontextYourSoundscapes.setOnClickListener(incontextButtonListener);
        incontextMuseumTour.setOnClickListener(incontextButtonListener);

    }

    // OnClickListener for incontext navigation buttons
    private final View.OnClickListener incontextButtonListener = new View.OnClickListener() {
        public void onClick(View v) {

            Intent intent = null;

            switch (v.getId()) {
                case R.id.incontext_create_soundscape:
                    intent = new Intent(MainActivity.this, CreateSoundscapeActivity.class);
                    break;
                case R.id.incontext_sound_library:
                    intent = new Intent(MainActivity.this, SoundLibraryActivity.class);
                    break;
                case R.id.incontext_your_soundscapes:
                    intent = new Intent(MainActivity.this, YourSoundscapesActivity.class);
                    break;
                case R.id.incontext_museum_tour:
//                    intent = new Intent(MainActivity.this, MuseumTourActivity.class);
//                    break;
                    Toast.makeText(getApplicationContext(), "Coming soon", Toast.LENGTH_SHORT).show();
                    return;
            }

            if (intent != null) {
                startActivity(intent);
            }
        }
    };

}
