package fi.metropolia.yellow_spaceship.androidadvproject;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import fi.metropolia.yellow_spaceship.androidadvproject.menu.ListRowData;
import fi.metropolia.yellow_spaceship.androidadvproject.menu.SoundLibraryListAdapter;
import fi.metropolia.yellow_spaceship.androidadvproject.models.SoundCategory;

/**
 * Created by Petri on 19.9.2015.
 */
public class SoundLibraryMainFragment extends Fragment {

    private RecyclerView.LayoutManager layoutManager;
    private View fragmentView;

    public static SoundLibraryMainFragment newInstance() {
        SoundLibraryMainFragment fragment = new SoundLibraryMainFragment();
        return fragment;
    }

    public SoundLibraryMainFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        fragmentView = inflater.inflate(R.layout.sound_library_main_fragment, container, false);

        // Data for RecycleView
        ArrayList<ListRowData> data = new ArrayList<ListRowData>();
        data.add(new ListRowData("Your Souncdscapes", R.drawable.ic_audiotrack_black_48dp));
        data.add(new ListRowData("Recordings", R.drawable.ic_mic_black_48dp));
        data.add(new ListRowData("Favourite Sounds", R.drawable.ic_star_border_black_48dp));

        for(SoundCategory cat : SoundCategory.values()) {
            data.add(new ListRowData(cat.menuCaption(), null));
        }

        // Adapter for RecyclerView
        SoundLibraryListAdapter adapter = new SoundLibraryListAdapter(getActivity(), (View.OnClickListener)getActivity(), data);
        RecyclerView recyclerView = (RecyclerView)fragmentView.findViewById(R.id.recycler_view);

        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setAdapter(adapter);

        return fragmentView;

    }

}
