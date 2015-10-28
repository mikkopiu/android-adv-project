package fi.metropolia.yellow_spaceship.androidadvproject;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

public class SoundLibraryActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sound_library);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getResources().getString(R.string.sound_library_title));
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SoundLibraryActivity.this.onBackPressed();
            }
        });

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

    // http://stackoverflow.com/a/27482902
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.search_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);

        SearchManager searchManager = (SearchManager)SoundLibraryActivity.this.getSystemService(Context.SEARCH_SERVICE);

        SearchView searchView = null;
        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();
        }
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(SoundLibraryActivity.this.getComponentName()));
            searchView.setOnQueryTextListener(this);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        if (query.length() > 0) {

            Fragment f = getSupportFragmentManager().findFragmentById(R.id.frame_layout);
            if(f == null || !(f instanceof SoundLibraryChildFragment)) {

                SoundLibraryChildFragment fragment = SoundLibraryChildFragment.newInstance();

                Bundle bundle = new Bundle();
                bundle.putString("search-query", query);
                fragment.setArguments(bundle);
                swapFragment(fragment);

            } else {
                ((SoundLibraryChildFragment) f).setSearchQuery(query);
                ((SoundLibraryChildFragment) f).loadSearchData();
            }

        }
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

}
