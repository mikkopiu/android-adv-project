package fi.metropolia.yellow_spaceship.androidadvproject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

import fi.metropolia.yellow_spaceship.androidadvproject.adapters.ISoundscapeViewHolderClicks;
import fi.metropolia.yellow_spaceship.androidadvproject.adapters.SoundscapesAdapter;
import fi.metropolia.yellow_spaceship.androidadvproject.managers.SaveDialogListener;
import fi.metropolia.yellow_spaceship.androidadvproject.managers.SaveDialogManager;
import fi.metropolia.yellow_spaceship.androidadvproject.models.SoundCategory;
import fi.metropolia.yellow_spaceship.androidadvproject.models.SoundScapeProject;
import fi.metropolia.yellow_spaceship.androidadvproject.tasks.ProjectLoadListener;
import fi.metropolia.yellow_spaceship.androidadvproject.tasks.ProjectLoadTask;
import fi.metropolia.yellow_spaceship.androidadvproject.tasks.ProjectSaveListener;
import fi.metropolia.yellow_spaceship.androidadvproject.tasks.ProjectSaveTask;

public class YourSoundscapesActivity extends AppCompatActivity
        implements ISoundscapeViewHolderClicks,
        ProjectLoadListener,
        SaveDialogListener {

    private ArrayList<SoundScapeProject> mData;
    private RecyclerView mRecyclerView;

    private SoundScapeProject mEditedProject;

    private SaveDialogManager mSaveDialogManager;

    private ProgressDialog mProgressDialog;
    private TextView mEmptyView;
    private ProgressBar mSpinner;
    private CoordinatorLayout mCoordinatorLayout;

    /**
     * ProjectSaveListener for renaming projects (they need to be re-saved in order to
     * serialize the updated name).
     */
    private final ProjectSaveListener projectSaveListener = new ProjectSaveListener() {
        @Override
        public void onSaveComplete(boolean success) {
            if (success) {
                Snackbar.make(
                        mCoordinatorLayout,
                        R.string.your_soundscapes_rename_success,
                        Snackbar.LENGTH_SHORT
                ).show();

                dismissProgressDialog();

                // Reload data
                loadData();
            } else {
                Snackbar.make(
                        mCoordinatorLayout,
                        R.string.your_soundscapes_rename_error,
                        Snackbar.LENGTH_LONG
                ).show();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_your_soundscapes); // Re-using the same layout

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getResources().getString(R.string.your_soundscapes_title));

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                YourSoundscapesActivity.this.onBackPressed();
            }
        });

        this.mSpinner = (ProgressBar) findViewById(R.id.progressBar);
        this.mSpinner.setVisibility(View.GONE);

        this.mEmptyView = (TextView) findViewById(R.id.empty_view);
        this.mEmptyView.setText(R.string.no_saved_soundscapes);
        this.mEmptyView.setVisibility(View.GONE);

        this.mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator_layout);

        initRecyclerView();

        loadData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.dismissProgressDialog();
    }

    private void dismissProgressDialog() {
        if (this.mProgressDialog != null) {
            this.mProgressDialog.dismiss();
            this.mProgressDialog = null;
        }
    }

    @Override
    public void onLoadFinished(ArrayList<SoundScapeProject> data) {
        if (this.mData != null) {
            this.mData.clear();
        } else {
            // A new adapter needs to be set if mData hasn't been initialized yet
            initRecyclerViewAdapter();
        }

        this.mData.addAll(data);
        this.mSpinner.setVisibility(View.GONE);
        this.mRecyclerView.getAdapter().notifyDataSetChanged();

        if (this.mRecyclerView.getAdapter().getItemCount() == 0) {
            this.mRecyclerView.setVisibility(View.GONE);
            this.mEmptyView.setVisibility(View.VISIBLE);
        } else {
            this.mRecyclerView.setVisibility(View.VISIBLE);
            this.mEmptyView.setVisibility(View.GONE);
        }
    }

    /**
     * ISoundscapeViewHolderClicks implementation
     */

    @Override
    public void onRowSelect(int layoutPosition) {
        SoundScapeProject d = this.mData.get(layoutPosition);

        Intent intent = new Intent(getApplicationContext(), CreateSoundscapeActivity.class);
        intent.putExtra(CreateSoundscapeActivity.LOADED_SOUNDSCAPE_KEY, d);
        startActivity(intent);
    }

    @Override
    public void onRowRename(int layoutPosition) {
        this.mEditedProject = this.mData.get(layoutPosition);
        if (mEditedProject != null) {
            if (this.mSaveDialogManager == null) {
                this.mSaveDialogManager = new SaveDialogManager(
                        this,
                        getResources().getString(R.string.soundscape_rename_dialog_title),
                        null,
                        this
                );
                this.mSaveDialogManager.setCounterMaxLength(
                        getResources().getInteger(R.integer.soundscape_name_max_length)
                );
                this.mSaveDialogManager.setEditTextText(mEditedProject.getName());
            }
            this.mSaveDialogManager.show();
        } else {
            Snackbar.make(
                    this.mCoordinatorLayout,
                    R.string.your_soundscapes_rename_error,
                    Snackbar.LENGTH_LONG
            ).show();
        }
    }

    @Override
    public void onRowDelete(int layoutPosition) {
        this.deleteProject(this.mData.get(layoutPosition));
    }

    /**
     * SaveDialogListener implementation
     */

    @Override
    public void onDialogSave(String title, SoundCategory category) {
        if (this.mEditedProject != null) {
            // Category is not relevant here, ignore in all cases
            renameProject(this.mEditedProject, title.trim());
        }
    }

    @Override
    public void onDialogCancel() {
        if (this.mSaveDialogManager != null) {
            this.mSaveDialogManager.dismiss();
        }
    }

    /**
     * Load existing soundscapes
     */
    private void loadData() {
        this.mSpinner.setVisibility(View.VISIBLE);

        new ProjectLoadTask(this).execute(getFilesDir() + "/" + ProjectSaveTask.PROJECT_FOLDER);
    }

    /**
     * Initialize the RecyclerView (list of soundscapes)
     */
    private void initRecyclerView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        mRecyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);

        initRecyclerViewAdapter();
    }

    /**
     * Initialize the adapter for the RecyclerView
     */
    private void initRecyclerViewAdapter() {
        mData = new ArrayList<>();
        SoundscapesAdapter adapter = new SoundscapesAdapter(mData, this);
        mRecyclerView.setAdapter(adapter);
    }

    /**
     * Rename an existing project (by moving the file).
     * Reloads data on success.
     *
     * @param project Existing SoundScapeProject
     * @param name    New name for the project
     */
    private void renameProject(SoundScapeProject project, String name) {
        if (project != null && !name.trim().isEmpty() && !name.equals(project.getName())) {
            String dir = getFilesDir() + "/" + ProjectSaveTask.PROJECT_FOLDER + "/";
            File newFile = new File(dir + name + ProjectSaveTask.FILE_EXT);

            // Bail out if a project with the same name already exists
            if (newFile.exists()) {
                this.mSaveDialogManager.setTextInputLayoutError(
                        getResources().getString(R.string.your_soundscapes_rename_error_name_taken)
                );
                return;
            }

            // Delete the old file (synchronous, relatively quick)
            deleteProject(project);

            // Dismiss the save dialog and show a spinner
            mSaveDialogManager.dismiss();
            this.mProgressDialog = ProgressDialog.show(
                    YourSoundscapesActivity.this,
                    null,
                    getResources().getString(R.string.saving_project),
                    true,
                    false
            );

            // Start a ProjectSaveTask for saving the renamed project (to serialize the new name)
            project.setName(name);
            new ProjectSaveTask(this.getApplicationContext(), projectSaveListener).execute(project);
        }
    }

    /**
     * Delete an existing project from the file systems.
     * Reloads data on success.
     *
     * @param project Existing SoundScapeProject
     */
    private void deleteProject(SoundScapeProject project) {
        if (project != null) {
            File file = new File(getFilesDir() +
                    "/" + ProjectSaveTask.PROJECT_FOLDER +
                    "/" + project.getName() + ProjectSaveTask.FILE_EXT);
            boolean deleted = file.delete();

            if (deleted) {
                // Reload data
                this.loadData();
            } else {
                Snackbar.make(
                        this.mCoordinatorLayout,
                        R.string.your_soundscapes_delete_error,
                        Snackbar.LENGTH_LONG
                ).show();
            }
        }
    }
}
