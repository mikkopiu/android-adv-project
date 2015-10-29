package fi.metropolia.yellow_spaceship.androidadvproject.menu;

import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import fi.metropolia.yellow_spaceship.androidadvproject.CreateSoundscapeActivity;
import fi.metropolia.yellow_spaceship.androidadvproject.MainActivity;
import fi.metropolia.yellow_spaceship.androidadvproject.MuseumTourActivity;
import fi.metropolia.yellow_spaceship.androidadvproject.R;
import fi.metropolia.yellow_spaceship.androidadvproject.SoundLibraryActivity;

/**
 * Creates the drawer menu and onclick listener for its menu items
 */
public class DrawerMenu {

    private AppCompatActivity activity;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private ActionBarDrawerToggle drawerToggle;

    /**
     * Constructor
     *
     * @param activity       The activity that holds the side menu
     * @param navigationView NavigationView-object
     * @param drawerLayout   DrawerLayout-object
     * @param toolbar        Toolbar-object
     */
    public DrawerMenu(AppCompatActivity activity, NavigationView navigationView, DrawerLayout drawerLayout, Toolbar toolbar) {
        this.activity = activity;
        this.navigationView = navigationView;
        this.drawerLayout = drawerLayout;
        this.toolbar = toolbar;
    }

    /**
     * Creates the menu and onclick listener
     */
    public void createMenu() {
        this.activity.setSupportActionBar(toolbar);
        drawerToggle = new ActionBarDrawerToggle(this.activity, this.drawerLayout, this.toolbar, R.string.drawer_open, R.string.drawer_close);
        this.drawerLayout.setDrawerListener(drawerToggle);
        this.activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.activity.getSupportActionBar().setHomeButtonEnabled(true);
        drawerToggle.syncState();

        this.navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                drawerLayout.closeDrawers();

                // If menu item is for current activity, do nothing
                if (menuItem.isChecked())
                    return true;

                Object object = null;

                switch (menuItem.getItemId()) {
                    case R.id.side_nav_item1:
                        object = MainActivity.class;
                        break;
                    case R.id.side_nav_item2:
                        object = CreateSoundscapeActivity.class;
                        break;
                    case R.id.side_nav_item3:
                        object = SoundLibraryActivity.class;
                        break;
                    case R.id.side_nav_item4:
                        object = MuseumTourActivity.class;
                        break;
                }

                if (object != null) {
                    Intent intent = new Intent(DrawerMenu.this.activity, (Class) object);
                    DrawerMenu.this.activity.startActivity(intent);
                    return true;
                }

                return false;
            }
        });
    }

    /**
     * Changes hamburger to back arrow
     */
    public void changeToBackButton() {

        // Hide drawer menu button and disable swipe
        this.drawerToggle.setDrawerIndicatorEnabled(false);
        this.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        // This shows the back arrow
        activity.getSupportActionBar().setHomeAsUpIndicator(null);

    }

    /**
     * Changes back arrow back to hamburger
     */
    public void changeToDrawerMenu() {

        createMenu();
        this.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);

    }

}
