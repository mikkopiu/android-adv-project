package fi.metropolia.yellow_spaceship.androidadvproject;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import fi.metropolia.yellow_spaceship.androidadvproject.adapters.SoundLibraryListAdapter;
import fi.metropolia.yellow_spaceship.androidadvproject.models.ListRowData;
import fi.metropolia.yellow_spaceship.androidadvproject.models.SoundCategory;

public class SoundLibraryMainFragment extends Fragment implements View.OnClickListener {

    private ArrayList<ListRowData> data;

    private RecyclerView recyclerView;
    private SoundLibraryListAdapter adapter;

    public static SoundLibraryMainFragment newInstance() {
        return new SoundLibraryMainFragment();
    }

    private static final String RECORDINGS_CAPTION = "Recordings";
    private static final String FAVOURITES_CAPTION = "Favourite Sounds";

    public SoundLibraryMainFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.sound_library_main_fragment, container, false);

        // Data for RecycleView
        this.data = new ArrayList<>();
        data.add(new ListRowData(RECORDINGS_CAPTION, R.drawable.ic_mic_yellow_24dp, null));
        data.add(new ListRowData(FAVOURITES_CAPTION, R.drawable.ic_star_24dp, null));

        for (SoundCategory cat : SoundCategory.values()) {
            // No need to show the unknown category in the list
            if (cat != SoundCategory.UNKNOWN) {
                data.add(new ListRowData(cat.menuCaption(), null, cat));
            }
        }

        // Adapter for RecyclerView
        adapter = new SoundLibraryListAdapter(getActivity(), this, data);
        recyclerView = (RecyclerView) fragmentView.findViewById(R.id.recycler_view);

        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setAdapter(adapter);

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

    }

    /**
     * OnClickListener for RecyclerView views.
     *
     * @param v View object
     */
    @Override
    public void onClick(View v) {
        SoundLibraryChildFragment fragment = SoundLibraryChildFragment.newInstance();
        int itemPosition = recyclerView.getChildAdapterPosition(v);
        ListRowData d = this.data.get(itemPosition);
        Bundle bundle = new Bundle();

        switch (d.getCaption()) {
            case RECORDINGS_CAPTION:
                bundle.putBoolean("isRecordings", true);
                break;
            case FAVOURITES_CAPTION:
                bundle.putBoolean("isFavorites", true);
                break;
            default:
                ListRowData data = adapter.getDataWithPosition(itemPosition);
                bundle.putBoolean("isFavorites", false);
                bundle.putBoolean("isRecordings", false);
                bundle.putString("category", data.getCategory().toString());
        }

        fragment.setArguments(bundle);
        ((SoundLibraryActivity) getActivity()).swapFragment(fragment);
    }

}
