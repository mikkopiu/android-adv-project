package fi.metropolia.yellow_spaceship.androidadvproject;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

import fi.metropolia.yellow_spaceship.androidadvproject.adapters.SoundscapesAdapter;
import fi.metropolia.yellow_spaceship.androidadvproject.models.SoundScapeProject;
import fi.metropolia.yellow_spaceship.androidadvproject.tasks.ProjectLoadListener;
import fi.metropolia.yellow_spaceship.androidadvproject.tasks.ProjectLoadTask;
import fi.metropolia.yellow_spaceship.androidadvproject.tasks.ProjectSaveTask;
import fi.metropolia.yellow_spaceship.androidadvproject.tasks.SaveListener;

public class YourSoundscapesActivity extends AppCompatActivity
        implements SoundscapesAdapter.ViewHolder.ISoundscapeViewHolderClicks,
        ProjectLoadListener {

    private ArrayList<SoundScapeProject> mData;
    private RecyclerView recyclerView;
    private TextView mEmptyView;

    private SoundScapeProject mEditedProject;

    private ProgressBar mSpinner;
    private Dialog mDialog;
    private EditText mDialogEditText;

    /**
     * Dialog's click listener
     */
    private final View.OnClickListener clickListener = new View.OnClickListener() {
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.dialog_cancel_btn:
                    mDialog.dismiss();
                    break;
                case R.id.dialog_save_btn:
                    renameProject(mEditedProject, mDialogEditText.getText().toString().trim());
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * SaveListener for renaming projects (they need to be re-saved in order to
     * serialize the updated name).
     */
    private final SaveListener saveListener = new SaveListener() {
        @Override
        public void onSaveComplete() {
            Toast.makeText(
                    getApplicationContext(),
                    "Project renamed successfully",
                    Toast.LENGTH_SHORT
            ).show();

            mDialog.dismiss();

            // Reload data
            loadData();
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
        this.mEmptyView.setVisibility(View.GONE);

        initRecyclerView();

        loadData();
    }

    private void loadData() {
        this.mSpinner.setVisibility(View.VISIBLE);

        new ProjectLoadTask(this).execute(getFilesDir() + "/" + ProjectSaveTask.PROJECT_FOLDER);
    }

    private void initRecyclerView() {
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        initRecyclerViewAdapter();
    }

    private void initRecyclerViewAdapter() {
        mData = new ArrayList<>();
        SoundscapesAdapter adapter = new SoundscapesAdapter(mData, this);
        recyclerView.setAdapter(adapter);
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

        if (this.mData.isEmpty()) {
            this.recyclerView.setVisibility(View.GONE);
            this.mEmptyView.setVisibility(View.VISIBLE);
        } else {
            this.recyclerView.setVisibility(View.VISIBLE);
            this.mEmptyView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onRowSelect(View view, int layoutPosition) {
        SoundScapeProject d = this.mData.get(layoutPosition);

        Intent intent = new Intent(getApplicationContext(), CreateSoundscapeActivity.class);
        intent.putExtra("loadedSoundscape", d);
        startActivity(intent);
    }

    @Override
    public void onRowRename(View view, int layoutPosition) {
        if (this.mDialog == null) {
            setupDialog();
        }
        this.mEditedProject = this.mData.get(layoutPosition);
        this.mDialog.show();
    }

    @Override
    public void onRowDelete(View view, int layoutPosition) {
        this.deleteProject(this.mData.get(layoutPosition));
    }

    /**
     * Rename an existing project (by moving the file).
     * Reloads data on success.
     * @param project Existing SoundScapeProject
     * @param name New name for the project
     */
    public void renameProject(SoundScapeProject project, String name) {
        if (project != null && !name.trim().isEmpty() && !name.equals(project.getName())) {
            String dir = getFilesDir() + "/" + ProjectSaveTask.PROJECT_FOLDER + "/";
            File newFile = new File(dir + name + ProjectSaveTask.FILE_EXT);

            // Bail out if a project with the same name already exists
            if (newFile.exists()) {
                Toast.makeText(
                        this.getApplicationContext(),
                        "A soundscape with that name already exists",
                        Toast.LENGTH_SHORT
                ).show();
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
    public void deleteProject(SoundScapeProject project) {
        if (project != null) {
            File file = new File(getFilesDir() +
                    "/" + ProjectSaveTask.PROJECT_FOLDER +
                    "/" + project.getName() + ProjectSaveTask.FILE_EXT);
            boolean deleted = file.delete();

            if (deleted) {
                // Reload data
                this.loadData();
            } else {
                Toast.makeText(
                        this.getApplicationContext(),
                        "Couldn't delete " + project.getName() + ". Something went wrong",
                        Toast.LENGTH_SHORT
                ).show();
            }
        }
    }

    /**
     * Setup a renaming dialog
     */
    private void setupDialog() {
        mDialog = new Dialog(YourSoundscapesActivity.this);
        mDialog.setContentView(R.layout.create_save_dialog);
        mDialog.setTitle(getResources().getString(R.string.soundscape_rename_dialog_title));
        Button mDialogSaveBtn = (Button) mDialog.findViewById(R.id.dialog_save_btn);
        Button mDialogCancelBtn = (Button) mDialog.findViewById(R.id.dialog_cancel_btn);
        mDialogEditText = (EditText) mDialog.findViewById(R.id.input_name);
        mDialogSaveBtn.setOnClickListener(clickListener);
        mDialogCancelBtn.setOnClickListener(clickListener);

        mDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }
}
