package fi.metropolia.yellow_spaceship.androidadvproject;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import fi.metropolia.yellow_spaceship.androidadvproject.adapters.SoundListAdapter;
import fi.metropolia.yellow_spaceship.androidadvproject.api.ApiClient;
import fi.metropolia.yellow_spaceship.androidadvproject.database.DAMSoundContract.DAMSoundEntry;
import fi.metropolia.yellow_spaceship.androidadvproject.managers.SessionManager;
import fi.metropolia.yellow_spaceship.androidadvproject.models.DAMSound;
import fi.metropolia.yellow_spaceship.androidadvproject.models.SoundCategory;
import fi.metropolia.yellow_spaceship.androidadvproject.providers.SoundContentProvider;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class SoundLibraryChildFragment extends Fragment {

    ArrayList<DAMSound> data;
    private SoundCategory mCategory;
    private RecyclerView.LayoutManager mLayoutManager;
    private SoundListAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private String mSearchQuery;
    private ProgressBar mSpinner;

    private SessionManager session;
    private MediaPlayer mediaPlayer;

    public static SoundLibraryChildFragment newInstance() {
        return new SoundLibraryChildFragment();
    }

    public SoundLibraryChildFragment() {
        // Required empty public constructor
    }

    public void setSearchQuery(String query) {
        this.mSearchQuery = query;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        ((AppCompatActivity)getActivity()).getSupportActionBar().setHomeAsUpIndicator(null);

        session = new SessionManager(getActivity());

        if(getArguments().getString("category") != null) {
            this.mCategory = SoundCategory.fromApi(getArguments().getString("category"));
        } else {
            this.mCategory = null;
        }

        if(getArguments().getString("search-query") != null) {
            this.mSearchQuery = getArguments().getString("search-query");
        } else {
            this.mSearchQuery = null;
        }

        // Data for RecycleView
        data = new ArrayList<>();

        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.sound_library_child_fragment, container, false);

        // Adapter for RecyclerView
        mAdapter = new SoundListAdapter(data, new SoundListAdapter.ViewHolder.ISoundViewHolderClicks() {
            @Override
            public void onFavorite(View view, int layoutPosition) {
                setItemFavorite(!data.get(layoutPosition).getIsFavorite(), layoutPosition);
            }

            @Override
            public void onRowSelect(View view, int layoutPosition) {
                // TODO: do something here
            }

            @Override
            public void onPlayPauseToggle(View view, int layoutPosition) {
//                downloadFile(""); // TODO: playback checks
            }
        }, getActivity().getApplicationContext());
        mRecyclerView = (RecyclerView)fragmentView.findViewById(R.id.recycler_view);

        // Changes in content don't affect the layout size, so set as true to improve performance
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mRecyclerView.setAdapter(mAdapter);

        return fragmentView;

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

        // Set onClickListener for back button
        ((Toolbar)getActivity().findViewById(R.id.toolbar)).setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        this.mSpinner = (ProgressBar)getActivity().findViewById(R.id.progressBar);
        mSpinner.setVisibility(View.GONE);


        if(this.mCategory != null)
            loadData();

        if(this.mSearchQuery != null) {
            loadSearchData();
        }
    }

    private void loadData() {
        session.checkLogin();

        mSpinner.setVisibility(View.VISIBLE);

        ApiClient.getDAMApiClient().getCategory(session.getApiKey(),
                this.mCategory,
                true,
                new Callback<List<List<DAMSound>>>() {
                    @Override
                    public void success(List<List<DAMSound>> lists, Response response) {

                        data.clear();
                        for (List<DAMSound> d : lists) {
                            data.add(d.get(0));
                        }
                        mRecyclerView.getAdapter().notifyDataSetChanged();
                        mSpinner.setVisibility(View.GONE);
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        error.printStackTrace();

                        mSpinner.setVisibility(View.GONE);
                        Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Downloading sounds failed, please try again", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                });
    }

    public void loadSearchData() {
        session.checkLogin();

        mSpinner.setVisibility(View.VISIBLE);

        ApiClient.getDAMApiClient().getTextSearchResults(session.getApiKey(),
                this.mSearchQuery,
                true,
                new Callback<List<List<DAMSound>>>() {
                    @Override
                    public void success(List<List<DAMSound>> lists, Response response) {
                        data.clear();
                        for (List<DAMSound> d : lists) {
                            data.add(d.get(0));
                        }
                        mRecyclerView.getAdapter().notifyDataSetChanged();
                        mSpinner.setVisibility(View.GONE);
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        error.printStackTrace();

                        mSpinner.setVisibility(View.GONE);
                        Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Downloading sounds failed, please try again", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                });
    }

    /**
     * Set item's favorite status.
     * Saves the state in our ContentProvider
     * @param isFavorite New favorite-status
     * @param layoutPosition Index of the item
     */
    private void setItemFavorite(boolean isFavorite, int layoutPosition) {
        DAMSound sound = data.get(layoutPosition);

        if (sound != null) {
            sound.setIsFavorite(isFavorite);

            ContentValues values = new ContentValues();
            values.put(DAMSoundEntry.COLUMN_NAME_SOUND_ID, sound.getFormattedSoundId());
            values.put(DAMSoundEntry.COLUMN_NAME_TITLE, sound.getTitle());
            values.put(DAMSoundEntry.COLUMN_NAME_CATEGORY, sound.getCategory().toString());
            values.put(DAMSoundEntry.COLUMN_NAME_TYPE, sound.getSoundType().toString());
            values.put(DAMSoundEntry.COLUMN_NAME_LENGTH_SEC, sound.getLengthSec());
            values.put(DAMSoundEntry.COLUMN_NAME_IS_FAVORITE, sound.getIsFavorite());

            // Insert or update favorite status, depending on whether the sound already exists
            // in the database.
            getActivity().getContentResolver().insert(SoundContentProvider.CONTENT_URI, values);

            mRecyclerView.getAdapter().notifyDataSetChanged();
        }
    }

    // TODO: everything below this is just an example, need to finalize

    private void downloadFile(String url) {
        url = "http://dev.mw.metropolia.fi/dianag/AudioResourceSpace/filestore/9_27b83a6bb5c6cea/9_f79eb4f22f03a3d.mp3?v=2015-09-01+13%3A00%3A05"; // TODO: remove

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        new Player()
                .execute(url);
    }

    class Player extends AsyncTask<String, Void, Boolean> {
        private ProgressDialog progress;

        @Override
        protected Boolean doInBackground(String... params) {
            // TODO Auto-generated method stub
            Boolean prepared;
            try {

                mediaPlayer.setDataSource(params[0]);

                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        mediaPlayer.stop();
                        mediaPlayer.reset();
                    }
                });
                mediaPlayer.prepare();
                prepared = true;
            } catch (IllegalArgumentException e) {
                Log.d("IllegalArgument", e.getMessage());
                prepared = false;
                e.printStackTrace();
            } catch (IOException e) {
                prepared = false;
                e.printStackTrace();
            }
            return prepared;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            if (progress.isShowing()) {
                progress.cancel();
            }
            Log.d("Prepared", "//" + result);
            mediaPlayer.start();
        }

        public Player() {
            progress = new ProgressDialog(getActivity());
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            this.progress.setMessage("Buffering...");
            this.progress.show();

        }
    }

}
