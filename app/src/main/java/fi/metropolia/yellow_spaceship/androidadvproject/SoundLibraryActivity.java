package fi.metropolia.yellow_spaceship.androidadvproject;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import java.util.ArrayList;

import fi.metropolia.yellow_spaceship.androidadvproject.menu.DrawerMenu;
import fi.metropolia.yellow_spaceship.androidadvproject.menu.ListRowData;
import fi.metropolia.yellow_spaceship.androidadvproject.menu.SoundLibraryListAdapter;
import fi.metropolia.yellow_spaceship.androidadvproject.models.SoundCategory;

/**
 * Created by Petri on 15.9.2015.
 */
public class SoundLibraryActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private RecyclerView.LayoutManager layoutManager;

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

        DrawerMenu drawerMenu = new DrawerMenu(this, navigationView, drawerLayout, toolbar);
        drawerMenu.createMenu();

        // Data for RecycleView
        ArrayList<ListRowData> data = new ArrayList<ListRowData>();
        data.add(new ListRowData("Your Souncdscapes", R.drawable.ic_audiotrack_black_48dp));
        data.add(new ListRowData("Recordings", R.drawable.ic_mic_black_48dp));
        data.add(new ListRowData("Favourite Sounds", R.drawable.ic_star_border_black_48dp));

        for(SoundCategory cat : SoundCategory.values()) {
            data.add(new ListRowData(cat.menuCaption(), null));
        }

        // Adapter for RecyclerView
        SoundLibraryListAdapter adapter = new SoundLibraryListAdapter(this, data);
        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.recycler_view);

        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setAdapter(adapter);
    }

}
