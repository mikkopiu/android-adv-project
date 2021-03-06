package fi.metropolia.yellow_spaceship.androidadvproject;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

public class SoundLibraryActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    public static final String LIBRARY_REQUEST_KEY = "requestCode";
    public static final String LIBRARY_RESULT_KEY = "result";
    public static final String SEARCH_QUERY_KEY = "search-query";

    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sound_library);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.sound_library_title);
        setSupportActionBar(toolbar);

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SoundLibraryActivity.this.onBackPressed();
            }
        });

        Fragment f = getSupportFragmentManager().findFragmentById(R.id.frame_layout);
        if (f == null) {
            SoundLibraryMainFragment fragment = SoundLibraryMainFragment.newInstance();
            getSupportFragmentManager().beginTransaction().add(R.id.frame_layout, fragment).commit();
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.dismissProgressDialog();
    }

    /**
     * Show a simple ProgressDialog while doing tasks that shouldn't be
     * cancelled by the user.
     *
     * @param msg Message to display (e.g. "Loading...")
     */
    public void showProgressDialog(String msg) {
        mProgressDialog = ProgressDialog.show(
                SoundLibraryActivity.this,
                null,
                msg,
                true,
                false
        );
    }

    /**
     * Dismiss the ProgressDialog if is exists
     */
    public void dismissProgressDialog() {
        if (this.mProgressDialog != null) {
            this.mProgressDialog.dismiss();
            this.mProgressDialog = null;
        }
    }

    /**
     * Check if ProgressDialog is visible
     *
     * @return Is ProgressDialog currently visible
     */
    public boolean isProgressDialogShowing() {
        return this.mProgressDialog != null && this.mProgressDialog.isShowing();
    }

    /**
     * Swap the currently displayed Fragment to another one
     *
     * @param fragment Fragment to display
     */
    public void swapFragment(Fragment fragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(
                R.anim.fragment_slide_in_right,
                R.anim.fragment_slide_out_left,
                R.anim.fragment_slide_in_left,
                R.anim.fragment_slide_out_right
        );
        ft.replace(R.id.frame_layout, fragment)
                .addToBackStack(null)
                .commit();
    }

    // http://stackoverflow.com/a/27482902
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.search_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);

        SearchManager searchManager = (SearchManager) SoundLibraryActivity.this.getSystemService(Context.SEARCH_SERVICE);

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
            if (f == null || !(f instanceof SoundLibraryChildFragment)) {

                SoundLibraryChildFragment fragment = SoundLibraryChildFragment.newInstance();

                Bundle bundle = new Bundle();
                bundle.putString(SEARCH_QUERY_KEY, query);
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
