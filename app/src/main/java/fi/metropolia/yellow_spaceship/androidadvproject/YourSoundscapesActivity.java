package fi.metropolia.yellow_spaceship.androidadvproject;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.io.File;
import java.util.ArrayList;

import fi.metropolia.yellow_spaceship.androidadvproject.adapters.SoundscapesAdapter;
import fi.metropolia.yellow_spaceship.androidadvproject.models.SoundScapeProject;

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
        System.out.println("Selected project: " + d.getName());
    }

    private void loadData() {
        if (this.mData != null) {
            this.mData.clear();
        } else {
            this.mData = new ArrayList<>();
        }

        String path = getFilesDir() + "/" + "projects";
        File f = new File(path);
        File file[] = f.listFiles();
        for (File aFile : file) {
            String fname = aFile.getName();
            int pos = fname.lastIndexOf(".");
            if (pos > 0) {
                fname = fname.substring(0, pos);
            }
            this.mData.add(new SoundScapeProject(fname));
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
