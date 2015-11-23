package fi.metropolia.yellow_spaceship.androidadvproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
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
import fi.metropolia.yellow_spaceship.androidadvproject.tasks.ProjectSaveTask;
import fi.metropolia.yellow_spaceship.androidadvproject.tasks.SaveListener;

public class YourSoundscapesActivity extends AppCompatActivity
        implements ISoundscapeViewHolderClicks,
        ProjectLoadListener,
        SaveDialogListener {

    private ArrayList<SoundScapeProject> mData;
    private RecyclerView recyclerView;

    private SoundScapeProject mEditedProject;

    private SaveDialogManager saveDialogManager;

    private TextView mEmptyView;
    private ProgressBar mSpinner;
    private CoordinatorLayout coordinatorLayout;

    /**
     * SaveListener for renaming projects (they need to be re-saved in order to
     * serialize the updated name).
     */
    private final SaveListener saveListener = new SaveListener() {
        @Override
        public void onSaveComplete(boolean success) {
            if (success) {
                Snackbar.make(
                        coordinatorLayout,
                        R.string.your_soundscapes_rename_success,
                        Snackbar.LENGTH_SHORT
                ).show();

                saveDialogManager.dismiss();

                // Reload data
                loadData();
            } else {
                Snackbar.make(
                        coordinatorLayout,
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

        this.coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator_layout);

        initRecyclerView();

        loadData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
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
        this.recyclerView.getAdapter().notifyDataSetChanged();

        if (this.recyclerView.getAdapter().getItemCount() == 0) {
            this.recyclerView.setVisibility(View.GONE);
            this.mEmptyView.setVisibility(View.VISIBLE);
        } else {
            this.recyclerView.setVisibility(View.VISIBLE);
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
        intent.putExtra("loadedSoundscape", d);
        startActivity(intent);
    }

    @Override
    public void onRowRename(int layoutPosition) {
        if (this.saveDialogManager == null) {
            this.saveDialogManager = new SaveDialogManager(
                    this,
                    getResources().getString(R.string.soundscape_rename_dialog_title),
                    null,
                    this
            );
            this.saveDialogManager.setCounterMaxLength(
                    getResources().getInteger(R.integer.soundscape_name_max_length)
            );
        }
        this.mEditedProject = this.mData.get(layoutPosition);
        this.saveDialogManager.show();
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
        if (this.saveDialogManager != null) {
            this.saveDialogManager.dismiss();
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
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        initRecyclerViewAdapter();
    }

    /**
     * Initialize the adapter for the RecyclerView
     */
    private void initRecyclerViewAdapter() {
        mData = new ArrayList<>();
        SoundscapesAdapter adapter = new SoundscapesAdapter(mData, this);
        recyclerView.setAdapter(adapter);
    }

    /**
     * Rename an existing project (by moving the file).
     * Reloads data on success.
     * @param project Existing SoundScapeProject
     * @param name New name for the project
     */
    private void renameProject(SoundScapeProject project, String name) {
        if (project != null && !name.trim().isEmpty() && !name.equals(project.getName())) {
            String dir = getFilesDir() + "/" + ProjectSaveTask.PROJECT_FOLDER + "/";
            File newFile = new File(dir + name + ProjectSaveTask.FILE_EXT);

            // Bail out if a project with the same name already exists
            if (newFile.exists()) {
                this.saveDialogManager.setTextInputLayoutError(
                        getResources().getString(R.string.your_soundscapes_rename_error_name_taken)
                );
                return;
            }

            // Delete the old file (synchronous, relatively quick)
            deleteProject(project);

            // Star a ProjectSaveTask for saving the renamed project (to serialize the new name)
            project.setName(name);
            new ProjectSaveTask(this.getApplicationContext(), saveListener).execute(project);
        }
    }

    /**
     * Delete an existing project from the file systems.
     * Reloads data on success.
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
                        this.coordinatorLayout,
                        R.string.your_soundscapes_delete_error,
                        Snackbar.LENGTH_LONG
                ).show();
            }
        }
    }
}
