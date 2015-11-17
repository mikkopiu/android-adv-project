package fi.metropolia.yellow_spaceship.androidadvproject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
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
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import fi.metropolia.yellow_spaceship.androidadvproject.adapters.SoundListAdapter;
import fi.metropolia.yellow_spaceship.androidadvproject.adapters.SoundListAdapter.ViewHolder.ISoundViewHolderClicks;
import fi.metropolia.yellow_spaceship.androidadvproject.api.ApiClient;
import fi.metropolia.yellow_spaceship.androidadvproject.api.AsyncDownloader;
import fi.metropolia.yellow_spaceship.androidadvproject.api.AsyncDownloaderListener;
import fi.metropolia.yellow_spaceship.androidadvproject.database.DAMSoundContract;
import fi.metropolia.yellow_spaceship.androidadvproject.database.DAMSoundContract.DAMSoundEntry;
import fi.metropolia.yellow_spaceship.androidadvproject.managers.SessionManager;
import fi.metropolia.yellow_spaceship.androidadvproject.models.DAMSound;
import fi.metropolia.yellow_spaceship.androidadvproject.models.SoundCategory;
import fi.metropolia.yellow_spaceship.androidadvproject.models.SoundType;
import fi.metropolia.yellow_spaceship.androidadvproject.providers.SoundContentProvider;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class SoundLibraryChildFragment extends Fragment implements AsyncDownloaderListener {

    private ArrayList<DAMSound> data;
    private SoundCategory mCategory;
    private RecyclerView mRecyclerView;
    private String mSearchQuery;
    private ProgressBar mSpinner;
    private TextView mEmptyView;

    private boolean isFavoritesView;
    private boolean isRecordingsView;

    private SessionManager session;
    private MediaPlayer mediaPlayer;

    private boolean playingAudio;
    private boolean initialStage = true;
    private int playingInd = -1;

    private static final String LOCAL_SOUND_FOLDER = "/sounds";

    /**
     * Event handling for adapter's events
     */
    private ISoundViewHolderClicks listEventHandler = new ISoundViewHolderClicks() {
        @Override
        public void onFavorite(View view, int layoutPosition) {
            setItemFavorite(!data.get(layoutPosition).getIsFavorite(), layoutPosition);
        }

        @Override
        public void onRowSelect(View view, int layoutPosition) {
            DAMSound selectedSound = data.get(layoutPosition);

            // Return the selection results if necessary
            Intent intent = getActivity().getIntent();
            if (intent.getIntExtra("requestCode", 0) == CreateSoundscapeActivity.GET_LIBRARY_SOUND) {
                // No need to download any time the user clicks a row, just when getting a sound
                mSpinner.setVisibility(View.VISIBLE);
                new AsyncDownloader(selectedSound, getActivity(), SoundLibraryChildFragment.this).execute();


            }
        }

        @Override
        public void onPlayPauseToggle(View view, int layoutPosition) {
            togglePlayPause(layoutPosition);
        }

        @Override
        public void onRowUpload(View view, int layoutPosition) {
            // TODO
            Log.d("SoundLibChild DEBUG", "UPLOADING SOUND: " + data.get(layoutPosition).getTitle());
        }

        @Override
        public void onRowDelete(View view, int layoutPosition) {
            Log.d("SoundLibChild DEBUG", "DELETING SOUND: " + data.get(layoutPosition).getTitle());
            DAMSound s = data.get(layoutPosition);

            if (s != null) {
                int deletedRows = deleteSound(s);

                if (deletedRows > 0) {
                    Toast.makeText(
                            getContext(),
                            "Sound deleted",
                            Toast.LENGTH_SHORT
                    ).show();
                    loadRecordingsData();
                } else {
                    Toast.makeText(
                            getContext(),
                            "Couldn't delete sound, something went wrong",
                            Toast.LENGTH_SHORT
                    ).show();
                }
            }
        }
    };

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeAsUpIndicator(null);

        session = new SessionManager(getActivity());

        if (getArguments().getString("category") != null) {
            this.mCategory = SoundCategory.fromApi(getArguments().getString("category"));
        } else {
            this.mCategory = null;
        }

        if (getArguments().getString("search-query") != null) {
            this.mSearchQuery = getArguments().getString("search-query");
        } else {
            this.mSearchQuery = null;
        }

        this.isFavoritesView = getArguments().getBoolean("isFavorites");
        this.isRecordingsView = getArguments().getBoolean("isRecordings");

        // Data for RecycleView
        data = new ArrayList<>();

        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.sound_library_child_fragment, container, false);

        // Adapter for RecyclerView
        SoundListAdapter mAdapter = new SoundListAdapter(data, listEventHandler, isRecordingsView);
        mRecyclerView = (RecyclerView) fragmentView.findViewById(R.id.recycler_view);

        // Changes in content don't affect the layout size, so set as true to improve performance
        mRecyclerView.setHasFixedSize(!this.isFavoritesView);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity(),
                LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mRecyclerView.setAdapter(mAdapter);

        return fragmentView;

    }

    @Override
    public void onDownloadFinished(DAMSound damSound) {

        mSpinner.setVisibility(View.GONE);

        if (damSound != null && damSound.getFileName() != null) {
            // Create the return Intent to send the selected sound
            // to the create-view.
            Intent returnIntent = new Intent();
            returnIntent.putExtra("result", damSound);
            getActivity().setResult(Activity.RESULT_OK, returnIntent);
            getActivity().finish();
        } else {
            Toast.makeText(
                    getActivity().getApplicationContext(),
                    "Something went wrong when downloading the sound, please select another sound",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

        // Set onClickListener for back button
        ((Toolbar) getActivity().findViewById(R.id.toolbar)).setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        this.mEmptyView = (TextView) getActivity().findViewById(R.id.empty_view);

        this.mSpinner = (ProgressBar) getActivity().findViewById(R.id.progressBar);
        mSpinner.setVisibility(View.GONE);


        if (this.mCategory != null)
            loadData();

        if (this.mSearchQuery != null) {
            loadSearchData();
        }

        if (this.isFavoritesView) {
            loadFavoritesData();
        }

        if (this.isRecordingsView) {
            loadRecordingsData();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        clearMediaPlayer();
    }

    /**
     * Load data for a search query
     */
    public void loadSearchData() {
        session.checkLogin();

        mSpinner.setVisibility(View.VISIBLE);

        ApiClient.getDAMApiClient().getTextSearchResults(session.getApiKey(),
                this.mSearchQuery,
                true,
                webDataCallback);
    }

    /**
     * Unified callback for retrieving data from the DAM API
     */
    private final Callback<List<List<DAMSound>>> webDataCallback = new Callback<List<List<DAMSound>>>() {
        @Override
        public void success(List<List<DAMSound>> lists, Response response) {
            ArrayList<DAMSound> d = new ArrayList<>();
            for (List<DAMSound> s : lists) {
                d.add(s.get(0));
            }
            setSoundData(d);
        }

        @Override
        public void failure(RetrofitError error) {
            error.printStackTrace();

            mSpinner.setVisibility(View.GONE);
            Toast.makeText(
                    getActivity().getApplicationContext(),
                    "Downloading sounds failed, please try again",
                    Toast.LENGTH_SHORT
            ).show();
        }
    };

    /**
     * Load a sound category's data
     */
    private void loadData() {
        session.checkLogin();

        mSpinner.setVisibility(View.VISIBLE);

        ApiClient.getDAMApiClient().getCategory(session.getApiKey(),
                this.mCategory,
                true,
                webDataCallback);
    }

    /**
     * Load data for the favorites-category
     */
    private void loadFavoritesData() {
        mSpinner.setVisibility(View.VISIBLE);

        ArrayList<DAMSound> d = getSoundContentWithSelection(
                DAMSoundEntry.COLUMN_NAME_IS_FAVORITE + "=?",
                new String[]{"1"}
        );

        setSoundData(d);
    }

    /**
     * Load data for the recordings-category
     */
    private void loadRecordingsData() {
        mSpinner.setVisibility(View.VISIBLE);

        ArrayList<DAMSound> d = getSoundContentWithSelection(
                DAMSoundEntry.COLUMN_NAME_IS_RECORDING + "=?",
                new String[]{"1"}
        );

        setSoundData(d);
    }

    /**
     * Get sounds from SoundContentProvider with a given selection.
     * Used with favorites & recordings.
     *
     * @param selection     Selection String
     * @param selectionArgs Arguments for the selection
     * @return Found ArrayList<DAMSound>
     */
    private ArrayList<DAMSound> getSoundContentWithSelection(String selection,
                                                             String[] selectionArgs) {
        ArrayList<DAMSound> d = new ArrayList<>();

        Cursor cursor = getActivity().getApplicationContext().getContentResolver().query(
                SoundContentProvider.CONTENT_URI,
                new String[]{
                        DAMSoundEntry.COLUMN_NAME_TITLE,
                        DAMSoundEntry.COLUMN_NAME_CATEGORY,
                        DAMSoundEntry.COLUMN_NAME_TYPE,
                        DAMSoundEntry.COLUMN_NAME_LENGTH_SEC,
                        DAMSoundEntry.COLUMN_NAME_IS_FAVORITE,
                        DAMSoundEntry.COLUMN_NAME_FILE_NAME,
                        DAMSoundEntry.COLUMN_NAME_SOUND_ID
                },
                selection,
                selectionArgs,
                null
        );

        if (cursor != null) {
            while (cursor.moveToNext()) {
                d.add(parseDamSoundFromCursor(cursor));
            }

            cursor.close();
        }

        return d;
    }

    /**
     * Delete a single sound.
     * Also deletes the accompanying file, if the sound has a fileName
     * @param sound Sound to be deleted
     * @return Amount of sounds deleted
     */
    private int deleteSound(DAMSound sound) {
        if (!sound.getFileName().isEmpty()) {
            File file = new File(getContext().getFilesDir() +
                    "/" + AsyncDownloader.SOUNDS_FOLDER +
                    "/" + sound.getFileName());
            boolean deleted = file.delete();

            if (!deleted) {
                return 0; // Bail if a fileName was found but couldn't be deleted
            }
        }

        return getActivity().getApplicationContext().getContentResolver().delete(
                SoundContentProvider.CONTENT_URI,
                DAMSoundEntry.COLUMN_NAME_SOUND_ID + "=?",
                new String[] {sound.getFormattedSoundId()}
        );
    }

    /**
     * Parse necessary data from a SoundContentProvider Cursor into a DAMSound
     *
     * @param cursor SoundContentProvider's Cursor
     * @return Parsed DAMSound
     */
    private DAMSound parseDamSoundFromCursor(Cursor cursor) {
        DAMSound s = new DAMSound();
        s.setTitle(cursor.getString(0));
        s.setCategory(SoundCategory.fromApi(cursor.getString(1)));
        s.setSoundType(SoundType.fromApi(cursor.getString(2)));
        s.setLengthSec(cursor.getInt(3));
        s.setIsFavorite(cursor.getInt(4) == 1);
        s.setFileName(cursor.getString(5));
        s.setFormattedSoundId(cursor.getString(6));
        return s;
    }

    /**
     * Set new data for sound list
     *
     * @param list New data
     */
    private void setSoundData(ArrayList<DAMSound> list) {
        this.data.clear();
        for (DAMSound s : list) {

            // We are only interested in sounds with working download links
            if (!TextUtils.isEmpty(s.getDownloadLink())) {
                Cursor cursor = getActivity().getApplicationContext().getContentResolver().query(
                        SoundContentProvider.CONTENT_URI,
                        new String[]{
                                DAMSoundContract.DAMSoundEntry.COLUMN_NAME_IS_FAVORITE,
                                DAMSoundContract.DAMSoundEntry.COLUMN_NAME_FILE_NAME
                        },
                        DAMSoundContract.DAMSoundEntry.COLUMN_NAME_SOUND_ID + "=?",
                        new String[]{s.getFormattedSoundId()},
                        null
                );

                if (cursor != null) {
                    if (cursor.moveToNext()) {
                        // Set favorite-button's image based on favorite-status
                        s.setIsFavorite(cursor.getInt(0) == 1);
                        // Set file name for the DAMSound, null if there is no local copy of the file
                        s.setFileName(cursor.getString(1));
                    }

                    cursor.close();
                }
            } else if (!this.isFavoritesView && !this.isRecordingsView) {
                // Skip items that don't have download links (and if we aren't in the favorites-
                // or recordings-view).
                continue;
            }

            // If in favorites- or recordings-view data has already been fetched from the
            // ContentProvider, no need to re-fetch it (and they don't have downloadLinks).

            if (s.getFormattedSoundId() == null) {
                s.setFormattedSoundId(s.generateFormattedSoundId());
            }

            this.data.add(s);
        }

        this.mRecyclerView.getAdapter().notifyDataSetChanged();
        this.mSpinner.setVisibility(View.GONE);

        if (this.data.isEmpty()) {
            this.mRecyclerView.setVisibility(View.GONE);
            this.mEmptyView.setVisibility(View.VISIBLE);
        }
        else {
            this.mRecyclerView.setVisibility(View.VISIBLE);
            this.mEmptyView.setVisibility(View.GONE);
        }
    }

    /**
     * Set item's favorite status.
     * Saves the state in our ContentProvider
     *
     * @param isFavorite     New favorite-status
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

            if (this.isFavoritesView) {
                loadFavoritesData();
            } else {
                mRecyclerView.getAdapter().notifyDataSetChanged();
            }
        }
    }

    private void togglePlayPause(int layoutPosition) {
        // No need to instantiate this on onCreate, only when we first need it
        if (mediaPlayer == null) {
            initMediaPlayer();
        }

        if (layoutPosition != playingInd) {
            // Play was clicked in some other position than which was playing earlier
            mediaPlayer.stop();
            mediaPlayer.reset();

            if (playingInd != -1) {
                setPlaybackStatus(false);
            }

            startPreviewPlayback(layoutPosition);
        } else {
            if (!playingAudio) {
                // No sound has played before, or the earlier one has already completed
                if (initialStage) {
                    startPreviewPlayback(layoutPosition);
                } else {
                    if (!mediaPlayer.isPlaying()) {
                        mediaPlayer.start();
                        setPlaybackStatus(true);
                    }
                }
            } else {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    setPlaybackStatus(false);
                }
            }
        }
    }

    /**
     * Start preview playback when nothing is currently playing/previous audio has finished
     *
     * @param layoutPosition
     */
    private void startPreviewPlayback(int layoutPosition) {
        String url = this.data.get(layoutPosition).getFileName();

        if (!TextUtils.isEmpty(url)) {
            url = getActivity().getApplicationContext().getFilesDir() + LOCAL_SOUND_FOLDER +
                    "/" + url;
        } else {
            // Stream from the DAM, if no local file is available
            url = this.data.get(layoutPosition).getDownloadLink();
        }

        if (!TextUtils.isEmpty(url)) {
            new Player().execute(url);
            this.playingInd = layoutPosition;
            this.setPlaybackStatus(true);
        } else {
            Toast.makeText(
                    getActivity(),
                    "Sorry, there is something wrong with this sound, please try another sound.",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    /**
     * Update playback status
     *
     * @param playing Is audio playing
     */
    private void setPlaybackStatus(boolean playing) {
        this.playingAudio = playing;

        if (!this.playingAudio) {
            ((SoundListAdapter.ViewHolder) this.mRecyclerView.getChildViewHolder(
                    this.mRecyclerView.getChildAt(this.playingInd))
            ).previewBtn.setImageResource(R.drawable.ic_play_arrow_24dp);
        } else {
            ((SoundListAdapter.ViewHolder) this.mRecyclerView.getChildViewHolder(
                    this.mRecyclerView.getChildAt(this.playingInd))
            ).previewBtn.setImageResource(R.drawable.ic_pause_24dp);
        }
    }

    /**
     * Initialize the MediaPlayer
     */
    private void initMediaPlayer() {
        if (this.mediaPlayer != null) {
            clearMediaPlayer();
        }

        this.mediaPlayer = new MediaPlayer();
        this.mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        this.mediaPlayer.setLooping(false);
    }

    /**
     * Stop and clear an existing MediaPlayer instance
     */
    private void clearMediaPlayer() {
        if (this.mediaPlayer != null) {
            this.mediaPlayer.reset();
            this.mediaPlayer.release();
            this.mediaPlayer = null;
        }
    }

    /**
     * Inner class Player is used to asynchronous audio playback when previewing sounds.
     * Player will also display a ProgressDialog when buffering an audio stream.
     */
    class Player extends AsyncTask<String, Void, Boolean> {

        // Display a progress dialog when buffering an audio stream
        private final ProgressDialog progress;

        @Override
        protected Boolean doInBackground(String... params) {
            Boolean prepared;
            try {
                mediaPlayer.setDataSource(params[0]);

                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        // This sound has completed, so clicking play should restart the sound,
                        // not unpause it.
                        initialStage = true;
                        setPlaybackStatus(false);

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
            super.onPostExecute(result);
            if (progress.isShowing()) {
                progress.cancel();
            }

            if (result) {
                mediaPlayer.start();
                initialStage = false;
            } else {
                setPlaybackStatus(false);
                Toast.makeText(
                        getActivity().getApplicationContext(),
                        "Something went wrong, please try another sound",
                        Toast.LENGTH_SHORT
                ).show();
            }
        }

        public Player() {
            progress = new ProgressDialog(getActivity());
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.progress.setMessage("Buffering...");
            this.progress.show();
        }
    }

}
