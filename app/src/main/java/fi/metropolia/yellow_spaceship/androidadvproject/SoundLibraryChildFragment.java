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
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import fi.metropolia.yellow_spaceship.androidadvproject.adapters.SoundListAdapter;
import fi.metropolia.yellow_spaceship.androidadvproject.adapters.ISoundLibraryViewHolderClicks;
import fi.metropolia.yellow_spaceship.androidadvproject.api.ApiClient;
import fi.metropolia.yellow_spaceship.androidadvproject.tasks.AsyncDownloader;
import fi.metropolia.yellow_spaceship.androidadvproject.tasks.AsyncDownloaderListener;
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
import retrofit.mime.TypedFile;

public class SoundLibraryChildFragment extends Fragment implements AsyncDownloaderListener,
        ActionModeToggleListener {

    private ArrayList<DAMSound> data;
    private SoundCategory mCategory;
    private RecyclerView mRecyclerView;
    private SoundListAdapter mAdapter;
    private String mSearchQuery;
    private ProgressBar mSpinner;
    private TextView mEmptyView;
    private CoordinatorLayout coordinatorLayout;

    private boolean isFavoritesView;
    private boolean isRecordingsView;

    private SessionManager session;
    private MediaPlayer mediaPlayer;

    private boolean playingAudio;
    private boolean initialStage = true;
    private int playingInd = -1;

    private boolean downloadingFiles;
    private ActionMode mMode;
    private int mWantedCount = -1;
    private ArrayList<DAMSound> mIntentReturnData;

    private static final String LOCAL_SOUND_FOLDER = "/sounds";
    private static final String WANTED_FILETYPE = null;         // TODO: change to wav, when source
                                                                // file headers are fixed (there are
                                                                // some extra headers currently,
                                                                // messing up the audio).

    /**
     * Event handling for adapter's events
     */
    private final ISoundLibraryViewHolderClicks listEventHandler = new ISoundLibraryViewHolderClicks() {
        @Override
        public void onFavorite(int layoutPosition) {
            setItemFavorite(!data.get(layoutPosition).getIsFavorite(), layoutPosition);
        }

        @Override
        public void onRowSelect(int layoutPosition) {
            DAMSound selectedSound = data.get(layoutPosition);

            // Return the selection results if necessary
            Intent intent = getActivity().getIntent();
            if (intent.getIntExtra(SoundLibraryActivity.LIBRARY_REQUEST_KEY, 0) == CreateSoundscapeActivity.GET_LIBRARY_SOUND) {
                // No need to download any time the user clicks a row, just when getting a sound
                ((SoundLibraryActivity)getActivity()).showProgressDialog(
                        getResources().getString(R.string.library_downloading)
                );

                mWantedCount = 1;
                downloadingFiles = true;
                new AsyncDownloader(selectedSound, getActivity(), SoundLibraryChildFragment.this).execute();
            }
        }

        @Override
        public void onPlayPauseToggle(int layoutPosition) {
            playPauseToggle(layoutPosition);
        }

        @Override
        public void onRowUpload(int layoutPosition) {
            Log.d("SoundLibChild DEBUG", "UPLOADING SOUND: " + data.get(layoutPosition).getTitle());
            DAMSound s = data.get(layoutPosition);

            if (s != null) {
                uploadSound(s);
            }
        }

        @Override
        public void onRowDelete(int layoutPosition) {
            Log.d("SoundLibChild DEBUG", "DELETING SOUND: " + data.get(layoutPosition).getTitle());
            DAMSound s = data.get(layoutPosition);

            if (s != null) {
                int deletedRows = deleteSound(s);

                if (deletedRows > 0) {
                    Snackbar.make(
                            coordinatorLayout,
                            R.string.library_delete_success,
                            Snackbar.LENGTH_LONG
                    ).show();
                    loadRecordingsData();
                } else {
                    Snackbar.make(
                            coordinatorLayout,
                            R.string.library_delete_error,
                            Snackbar.LENGTH_LONG
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retain this fragment across configuration changes.
        setRetainInstance(true);

        this.session = new SessionManager(getActivity());

        // Are we in a category sound list
        if (getArguments().getString("category") != null) {
            this.mCategory = SoundCategory.fromApi(getArguments().getString("category"));
        } else {
            this.mCategory = null;
        }

        // Get other data about our current view
        this.mSearchQuery = getArguments().getString(SoundLibraryActivity.SEARCH_QUERY_KEY);
        this.isFavoritesView = getArguments().getBoolean("isFavorites");
        this.isRecordingsView = getArguments().getBoolean("isRecordings");

        this.coordinatorLayout = (CoordinatorLayout) getActivity()
                .findViewById(R.id.coordinator_layout);

        // Data for RecycleView
        this.data = new ArrayList<>();

        boolean inSelectMode = getActivity().getIntent()
                .getIntExtra(SoundLibraryActivity.LIBRARY_REQUEST_KEY, 0) == CreateSoundscapeActivity.GET_LIBRARY_SOUND;
        this.mAdapter = new SoundListAdapter(data, listEventHandler, isRecordingsView,
                inSelectMode ? this : null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        ActionBar toolbar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (toolbar != null) {
            toolbar.setHomeAsUpIndicator(null);
            toolbar.setTitle(getArguments().getString("title"));
        }

        // Inflate the layout for this fragment
        View fragmentView = inflater
                .inflate(R.layout.sound_library_child_fragment, container, false);

        this.mRecyclerView = (RecyclerView) fragmentView.findViewById(R.id.recycler_view);

        // Changes in content don't affect the layout size, so set as true to improve performance
        this.mRecyclerView.setHasFixedSize(!this.isFavoritesView);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity(),
                LinearLayoutManager.VERTICAL, false);
        this.mRecyclerView.setLayoutManager(mLayoutManager);

        this.mRecyclerView.setAdapter(this.mAdapter);

        return fragmentView;

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

        this.mSpinner = (ProgressBar) getActivity().findViewById(R.id.progressBar);
        this.mEmptyView = (TextView) getActivity().findViewById(R.id.empty_view);

        if (savedInstanceState == null) {
            this.mEmptyView.setVisibility(View.GONE);
            this.mSpinner.setVisibility(View.GONE);


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

        if (this.downloadingFiles) {
            if (!((SoundLibraryActivity)getActivity()).isProgressDialogShowing()) {
                ((SoundLibraryActivity)getActivity()).showProgressDialog(
                        getResources().getString(R.string.library_downloading)
                );
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        stopPreview(playingInd);
        clearMediaPlayer();
    }

    @Override
    public void onDownloadFinished(DAMSound damSound) {
        if (this.mIntentReturnData == null) {
            this.mIntentReturnData = new ArrayList<>();
        }

        this.mIntentReturnData.add(damSound);

        if (mIntentReturnData.size() == this.mWantedCount) {
            this.downloadingFiles = false;
            ((SoundLibraryActivity)getActivity()).dismissProgressDialog();

            if (getActivity() != null) {

                if (damSound != null && damSound.getFileName() != null) {
                    // We don't want to block we UI and make parcelable out of all selected sounds,
                    // instead pass their IDs as the result, and let the recipient handle actually
                    // loading them from the DB, asynchronously.
                    String[] ids = new String[this.mIntentReturnData.size()];
                    for (int i = 0; i < this.mIntentReturnData.size(); i++) {
                        ids[i] = this.mIntentReturnData.get(i).getFormattedSoundId();
                    }

                    // Create the return Intent to send the selected sound IDs
                    // to the create-view.
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra(SoundLibraryActivity.LIBRARY_RESULT_KEY, ids);
                    getActivity().setResult(Activity.RESULT_OK, returnIntent);
                    getActivity().finish();
                } else {
                    Snackbar.make(
                            this.coordinatorLayout,
                            R.string.library_download_error,
                            Snackbar.LENGTH_LONG
                    ).show();
                }
            }
        }
    }

    /**
     * Set ActionMode state.
     * Display/Hide the ActionMode overlay over the Toolbar when selecting multiple sounds.
     *
     * @param actionModeOn Should ActionMode menu be visible
     */
    @Override
    public void setActionMode(boolean actionModeOn) {
        if (actionModeOn && this.mMode == null) {
            this.mMode = ((AppCompatActivity) this.getActivity())
                    .startSupportActionMode(actionModeCb);
        } else if (this.mMode != null) {
            this.mMode.finish();
            this.mMode = null;
        }

    }

    /**
     * Update ActionMode overlay's title (here: selection count)
     *
     * @param title New title for ActionMode overlay
     */
    @Override
    public void setActionModeTitle(String title) {
        if (this.mMode != null) {
            this.mMode.setTitle(title);
        }
    }

    public void setSearchQuery(String query) {
        this.mSearchQuery = query;
    }

    /**
     * Callback for ActionMode events (create, item click etc).
     * Used to display an ActionMode overlay over the Toolbar when selecting multiple sounds.
     */
    private final ActionMode.Callback actionModeCb =
            new ActionMode.Callback() {

                @Override
                public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                    MenuInflater inflater = mode.getMenuInflater();
                    inflater.inflate(R.menu.library_multiselect_context, menu);
                    return true;
                }

                @Override
                public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                    return false;
                }

                @Override
                public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                    // Return the selection results if necessary
                    Intent intent = getActivity().getIntent();
                    if (intent.getIntExtra(SoundLibraryActivity.LIBRARY_REQUEST_KEY, 0) == CreateSoundscapeActivity.GET_LIBRARY_SOUND) {
                        ArrayList<DAMSound> selectedSounds =
                                ((SoundListAdapter) mRecyclerView.getAdapter()).getSelectedSounds();

                        mWantedCount = selectedSounds.size();
                        downloadingFiles = true;
                        ((SoundLibraryActivity)getActivity()).showProgressDialog(
                                getResources().getString(R.string.library_downloading)
                        );

                        for (DAMSound s : selectedSounds) {
                            new AsyncDownloader(s, getActivity(), SoundLibraryChildFragment.this)
                                    .execute();
                        }
                    }

                    return false;
                }

                @Override
                public void onDestroyActionMode(ActionMode mode) {
                    ((SoundListAdapter) mRecyclerView.getAdapter()).setInEditMode(false);
                }
            };

    /**
     * Load data for a search query
     */
    public void loadSearchData() {
        session.checkLogin();

        mSpinner.setVisibility(View.VISIBLE);

        ApiClient.getDAMApiClient().getTextSearchResults(
                session.getApiKey(),
                session.getCollectionID(),
                this.mSearchQuery,
                WANTED_FILETYPE,
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

            // Activity no longer exists or hasn't yet been attached,
            // no need to set the received data to a null context
            if (getActivity() != null) {
                setSoundData(d);
            }
        }

        @Override
        public void failure(RetrofitError error) {
            error.printStackTrace();

            mSpinner.setVisibility(View.GONE);
            Snackbar.make(
                    coordinatorLayout,
                    R.string.library_download_error,
                    Snackbar.LENGTH_LONG
            ).show();
        }
    };

    /**
     * Unified callback for uploading.
     * API should respond with a JSON object like: {Success:You have uploaded successfully.}
     */
    private final Callback<Object> uploadCallback = new Callback<Object>() {
        @Override
        public void success(Object stringResponse, Response response) {
            Snackbar.make(
                    coordinatorLayout,
                    R.string.library_upload_success,
                    Snackbar.LENGTH_LONG
            ).show();

            ((SoundLibraryActivity)getActivity()).dismissProgressDialog();
        }

        @Override
        public void failure(RetrofitError error) {
            Log.e("LibChildFrag", "Upload failed: " + error.getMessage());

            ((SoundLibraryActivity)getActivity()).dismissProgressDialog();

            Snackbar.make(
                    coordinatorLayout,
                    R.string.library_upload_error,
                    Snackbar.LENGTH_LONG
            ).show();
        }
    };

    /**
     * Load a sound category's data
     */
    private void loadData() {
        session.checkLogin();

        mSpinner.setVisibility(View.VISIBLE);

        ApiClient.getDAMApiClient().getCategory(
                session.getApiKey(),
                session.getCollectionID(),
                this.mCategory,
                WANTED_FILETYPE,
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
                        DAMSoundEntry.COLUMN_NAME_SOUND_ID,
                        DAMSoundEntry.COLUMN_NAME_URL,
                        DAMSoundEntry.COLUMN_NAME_FILE_EXT
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
     *
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
                new String[]{sound.getFormattedSoundId()}
        );
    }

    private void uploadSound(DAMSound sound) {

        ((SoundLibraryActivity)getActivity()).showProgressDialog(
                getResources().getString(R.string.library_uploading)
        );

        // Create a TypedFile of type octet-stream for the API upload
        File file = new File(getContext().getFilesDir() +
                "/" + AsyncDownloader.SOUNDS_FOLDER +
                "/" + sound.getFileName());
        TypedFile typedSound = new TypedFile("application/octet-stream", file);

        ApiClient.getDAMApiClient().uploadSound(
                session.getApiKey(),
                session.getCollectionID(),
                sound.getTitle(),
                sound.getDescription(),
                sound.getCategory(),
                sound.getSoundType(),
                sound.getLengthSec(),
                typedSound,
                uploadCallback
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
        s.setDownloadLink(cursor.getString(7));
        s.setFileExtension(cursor.getString(8));
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

        if (this.mRecyclerView.getAdapter().getItemCount() == 0) {
            this.mRecyclerView.setVisibility(View.GONE);
            this.mEmptyView.setVisibility(View.VISIBLE);

            if (this.isFavoritesView) {
                this.mEmptyView.setText(R.string.no_favorites_added);
            } else if (this.isRecordingsView) {
                this.mEmptyView.setText(R.string.no_recordings_added);
            } else {
                this.mEmptyView.setText(R.string.no_sounds_found);
            }

        } else {
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
            values.put(DAMSoundEntry.COLUMN_NAME_URL, sound.getDownloadLink());
            values.put(DAMSoundEntry.COLUMN_NAME_FILE_EXT, sound.getFileExtension());

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

    /**
     * Toggles between previews pause and play states.
     * If another sound is being played it will be stopped and the sound in layoutPosition will start.
     *
     * @param layoutPosition Index in layout
     */
    private void playPauseToggle(int layoutPosition) {
        DAMSound sound = data.get(layoutPosition);

        if (sound.getIsPlaying() && layoutPosition == playingInd && !initialStage && playingAudio) {
            // layout position is playing
            pausePreview(layoutPosition);
        } else if (!sound.getIsPlaying() && layoutPosition == playingInd && !initialStage && !playingAudio) {
            // layout position is paused
            continuePreview(layoutPosition);
        } else if (playingInd == -1) {
            // nothing is playing
            startPreview(layoutPosition);
        } else if (playingInd != layoutPosition) {
            // something else is playing
            stopPreview(playingInd);
            startPreview(layoutPosition);
        }
    }

    /**
     * Toggle between pause/play icon in ViewHolder.
     *
     * @param layoutPosition Index in layout
     * @param playing        Is sound currently playing
     */
    private void toggleViewHolderIcon(int layoutPosition, boolean playing) {
        SoundListAdapter.ViewHolder viewHolder =
                ((SoundListAdapter.ViewHolder) SoundLibraryChildFragment.this.mRecyclerView
                        .findViewHolderForAdapterPosition(layoutPosition));

        if (viewHolder != null) {
            viewHolder.setPlayingState(playing);
        }
    }

    /**
     * Start preview playback
     *
     * @param layoutPosition Index in layout
     */
    private void startPreview(int layoutPosition) {

        toggleViewHolderIcon(layoutPosition, true);

        if (mediaPlayer == null) {
            initMediaPlayer();
        }

        startPreviewPlayback(layoutPosition);

    }

    /**
     * Pause preview playback
     *
     * @param layoutPosition Index in layout
     */
    private void pausePreview(int layoutPosition) {
        if (mediaPlayer.isPlaying()) {

            mediaPlayer.pause();
            playingAudio = false;

            data.get(layoutPosition).setIsPlaying(false);

            toggleViewHolderIcon(layoutPosition, false);

        }
    }

    /**
     * Continue preview playback
     *
     * @param layoutPosition Index in layout
     */
    private void continuePreview(int layoutPosition) {
        if (!mediaPlayer.isPlaying()) {

            mediaPlayer.start();
            playingAudio = true;

            data.get(layoutPosition).setIsPlaying(true);

            toggleViewHolderIcon(layoutPosition, true);

        }
    }

    /**
     * Stop preview playback
     *
     * @param layoutPosition Index in layout
     */
    private void stopPreview(int layoutPosition) {
        toggleViewHolderIcon(layoutPosition, false);

        if (layoutPosition != -1) {
            data.get(layoutPosition).setIsPlaying(false);
        }

        playingInd = -1;
        initialStage = true;
        playingAudio = false;

        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.reset();
        }
    }

    /**
     * Start preview playback when nothing is currently playing/previous audio has finished
     *
     * @param layoutPosition Index in layout
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
            data.get(layoutPosition).setIsPlaying(true);
        } else {
            Snackbar.make(
                    this.coordinatorLayout,
                    R.string.library_preview_error,
                    Snackbar.LENGTH_LONG
            ).show();
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
                        stopPreview(playingInd);
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
                progress.dismiss();
            }

            if (result) {
                mediaPlayer.start();
                initialStage = false;
                playingAudio = true;
            } else {
                stopPreview(playingInd);
                Snackbar.make(
                        coordinatorLayout,
                        R.string.library_preview_error,
                        Snackbar.LENGTH_LONG
                ).show();
            }
        }

        public Player() {
            progress = new ProgressDialog(getActivity());
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.progress.setMessage(getResources().getString(R.string.library_buffering));
            this.progress.setCancelable(false);
            this.progress.show();
        }
    }

}
