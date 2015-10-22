package fi.metropolia.yellow_spaceship.androidadvproject;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import fi.metropolia.yellow_spaceship.androidadvproject.models.ListRowData;
import fi.metropolia.yellow_spaceship.androidadvproject.adapters.SoundLibraryListAdapter;
import fi.metropolia.yellow_spaceship.androidadvproject.models.SoundCategory;

public class SoundLibraryMainFragment extends Fragment implements View.OnClickListener {

    private RecyclerView.LayoutManager layoutManager;
    private View fragmentView;
    private RecyclerView recyclerView;
    private SoundLibraryListAdapter adapter;

    public static SoundLibraryMainFragment newInstance() {
        return new SoundLibraryMainFragment();
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
        ArrayList<ListRowData> data = new ArrayList<>();
        data.add(new ListRowData("Your Soundscapes", R.drawable.ic_audiotrack_black_48dp, null));
        data.add(new ListRowData("Recordings", R.drawable.ic_mic_black_48dp, null));
        data.add(new ListRowData("Favourite Sounds", R.drawable.ic_star_outline_48dp, null));

        for(SoundCategory cat : SoundCategory.values()) {
            // No need to show the unknown category in the list
            if (cat != SoundCategory.UNKNOWN) {
                data.add(new ListRowData(cat.menuCaption(), null, cat));
            }
        }

        // Adapter for RecyclerView
        adapter = new SoundLibraryListAdapter(getActivity(), this, data);
        recyclerView = (RecyclerView)fragmentView.findViewById(R.id.recycler_view);

        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setAdapter(adapter);

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

    }

    /**
     * OnClickListener for RecyclerView views.
     * @param v View object
     */
    @Override
    public void onClick(View v) {
        SoundLibraryChildFragment fragment = SoundLibraryChildFragment.newInstance();
        int itemPosition = recyclerView.getChildAdapterPosition(v);
        Bundle bundle = new Bundle();

        switch (itemPosition) {
            case 0:
                // TODO: implement your soundscapes view
                break;
            case 1:
                // TODO: implement recordings view
                bundle.putBoolean("isRecordings", true);
                break;
            case 2:
                // TODO: implement favorites view
                bundle.putBoolean("isFavorites", true);
                break;
            default:
                ListRowData data = adapter.getDataWithPosition(itemPosition);
                bundle.putBoolean("isFavorites", false);
                bundle.putBoolean("isRecordings", false);
                bundle.putString("category", data.getCategory().toString());
        }

        fragment.setArguments(bundle);
        ((SoundLibraryActivity)getActivity()).swapFragment(fragment);
    }

}
