package fi.metropolia.yellow_spaceship.androidadvproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import fi.metropolia.yellow_spaceship.androidadvproject.adapters.SoundscapesAdapter;
import fi.metropolia.yellow_spaceship.androidadvproject.models.SoundScapeProject;
import fi.metropolia.yellow_spaceship.androidadvproject.tasks.ProjectSaveTask;

public class YourSoundscapesActivity extends AppCompatActivity implements View.OnClickListener {

    private ArrayList<SoundScapeProject> mData;
    private RecyclerView recyclerView;
    private SoundscapesAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;

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

        loadData();

        initRecyclerView();
    }

    @Override
    public void onClick(View v) {
        int itemPosition = recyclerView.getChildAdapterPosition(v);
        SoundScapeProject d = this.mData.get(itemPosition);

        Intent intent = new Intent(getApplicationContext(), CreateSoundscapeActivity.class);
        intent.putExtra("loadedSoundscape", d);
        startActivity(intent);
    }

    private void loadData() {
        // TODO: move this out of the UI thread
        if (this.mData != null) {
            this.mData.clear();
        } else {
            this.mData = new ArrayList<>();
        }

        String path = getFilesDir() + "/" + ProjectSaveTask.PROJECT_FOLDER;
        File dir = new File(path);
        File file[] = dir.listFiles();
        for (File aFile : file) {
            Gson gson = new GsonBuilder().create();
            BufferedReader br = null;
            try {
                br = new BufferedReader(new FileReader(aFile.getAbsolutePath()));
                SoundScapeProject p = gson.fromJson(br, SoundScapeProject.class);
                this.mData.add(p);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (br != null) {
                        br.close();
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

            }

        }
    }

    private void initRecyclerView() {
        adapter = new SoundscapesAdapter(mData, this);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setAdapter(adapter);
    }
}
