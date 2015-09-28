package fi.metropolia.yellow_spaceship.androidadvproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import fi.metropolia.yellow_spaceship.androidadvproject.menu.DrawerMenu;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        session = new SessionManager(getApplicationContext());

        // Check login status and redirect to LoginActivity if necessary
        // TODO: is this the best place?
        session.checkLogin();

        // Toolbar and menus + menu click events
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        navigationView = (NavigationView)findViewById(R.id.navigation_view);
        navigationView.getMenu().getItem(0).setChecked(true);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle(getResources().getString(R.string.toolbar_title));

        DrawerMenu drawerMenu = new DrawerMenu(this, navigationView, drawerLayout, toolbar);
        drawerMenu.createMenu();

        // Incontext navigation click events
        Button incontextCreateSoundscapeButton = (Button)findViewById(R.id.incontext_create_soundscape);
        incontextCreateSoundscapeButton.setOnClickListener(incontextButtonListener);

    }

    // OnClickListener for incontext navigation buttons
    View.OnClickListener incontextButtonListener = new View.OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.incontext_create_soundscape:
                    Intent intent = new Intent(MainActivity.this, CreateSoundscapeActivity.class);
                    startActivity(intent);
                    break;
            }
        }
    };

}
