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
public class SoundLibraryMainFragment extends Fragment implements View.OnClickListener {

    private RecyclerView.LayoutManager layoutManager;
    private View fragmentView;
    private RecyclerView recyclerView;
    private SoundLibraryListAdapter adapter;

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
        data.add(new ListRowData("Your Souncdscapes", R.drawable.ic_audiotrack_black_48dp, null));
        data.add(new ListRowData("Recordings", R.drawable.ic_mic_black_48dp, null));
        data.add(new ListRowData("Favourite Sounds", R.drawable.ic_star_border_black_48dp, null));

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

    /**
     * OnClickListener for RecyclerView views.
     * @param v View object
     */
    @Override
    public void onClick(View v) {
        SoundLibraryChildFragment fragment = SoundLibraryChildFragment.newInstance();
        ((SoundLibraryActivity)getActivity()).changeToBackButton();
        int itemPosition = recyclerView.getChildAdapterPosition(v);
        ListRowData data = adapter.getDataWithPosition(itemPosition);
        Bundle bundle = new Bundle();
        bundle.putString("category", data.getCategory().toString());
        fragment.setArguments(bundle);
        ((SoundLibraryActivity)getActivity()).swapFragment(fragment);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

        ((SoundLibraryActivity)getActivity()).changeToDrawerMenu();

    }

}
