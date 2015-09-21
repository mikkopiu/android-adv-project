package fi.metropolia.yellow_spaceship.androidadvproject;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import fi.metropolia.yellow_spaceship.androidadvproject.menu.DrawerMenu;

/**
 * Created by Petri on 15.9.2015.
 */
public class SoundLibraryActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private RecyclerView.LayoutManager layoutManager;
    private DrawerMenu drawerMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sound_library);

        // Toolbar and menus + menu click events
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.getMenu().getItem(2).setChecked(true);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getResources().getString(R.string.sound_library_title));

        drawerMenu = new DrawerMenu(this, navigationView, drawerLayout, toolbar);
        drawerMenu.createMenu();

        Fragment f = getSupportFragmentManager().findFragmentById(R.id.frame_layout);
        if(f == null) {
            SoundLibraryMainFragment fragment = SoundLibraryMainFragment.newInstance();
            getSupportFragmentManager().beginTransaction().add(R.id.frame_layout, fragment).commit();
        }

    }

    public void swapFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_layout, fragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onBackPressed() {
        System.out.println("TEEEESTAT");
        drawerMenu.changeToDrawerMenu();
        super.onBackPressed();
    }

    public void changeToDrawerMenu() {
        drawerMenu.changeToDrawerMenu();
    }

    public void changeToBackButton() {
        drawerMenu.changeToBackButton();
    }

}
