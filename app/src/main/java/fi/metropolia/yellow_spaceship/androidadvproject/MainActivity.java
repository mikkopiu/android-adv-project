package fi.metropolia.yellow_spaceship.androidadvproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import fi.metropolia.yellow_spaceship.androidadvproject.menu.DrawerMenu;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        //HomeFragment homeFragment = HomeFragment.newInstance();
        //getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, homeFragment).commit();

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

    private void swapFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    /*

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.findItem(R.id.home_nav_item).setVisible(false);
        return true;
    }

    /*

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    */

}
