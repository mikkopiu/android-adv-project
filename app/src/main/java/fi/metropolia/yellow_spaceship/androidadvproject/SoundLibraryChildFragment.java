package fi.metropolia.yellow_spaceship.androidadvproject;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import fi.metropolia.yellow_spaceship.androidadvproject.api.ApiClient;
import fi.metropolia.yellow_spaceship.androidadvproject.menu.ListRowData;
import fi.metropolia.yellow_spaceship.androidadvproject.menu.SoundLibraryListAdapter;
import fi.metropolia.yellow_spaceship.androidadvproject.models.DAMSound;
import fi.metropolia.yellow_spaceship.androidadvproject.models.SoundCategory;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Petri on 19.9.2015.
 */
public class SoundLibraryChildFragment extends Fragment {

    private SoundCategory category;
    private RecyclerView.LayoutManager layoutManager;
    ArrayList<ListRowData> data;
    private SoundLibraryListAdapter adapter;
    private RecyclerView recyclerView;
    private String searchQuery;

    private SessionManager session;

    public static SoundLibraryChildFragment newInstance() {
        SoundLibraryChildFragment fragment = new SoundLibraryChildFragment();
        return fragment;
    }

    public SoundLibraryChildFragment() {
        // Required empty public constructor
    }

    public void setSearchQuery(String query) {
        this.searchQuery = query;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        ((AppCompatActivity)getActivity()).getSupportActionBar().setHomeAsUpIndicator(null);

        session = new SessionManager(getActivity());

        if(getArguments().getString("category") != null) {
            this.category = SoundCategory.fromApi(getArguments().getString("category"));
        } else {
            this.category = null;
        }

        if(getArguments().getString("search-query") != null) {
            this.searchQuery = getArguments().getString("search-query");
        } else {
            this.searchQuery = null;
        }

        // Data for RecycleView
        data = new ArrayList<ListRowData>();

        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.sound_library_child_fragment, container, false);

        // Adapter for RecyclerView
        adapter = new SoundLibraryListAdapter(getActivity(), null, data);
        recyclerView = (RecyclerView)fragmentView.findViewById(R.id.recycler_view);

        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setAdapter(adapter);

        if(this.category != null)
            loadData();

        if(this.searchQuery != null) {
            loadSearchData();
        }

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

    private void loadData() {
        session.checkLogin();

        ApiClient.getDAMApiClient().getCategory(session.getApiKey(),
                this.category,
                true,
                new Callback<List<List<DAMSound>>>() {
                    @Override
                    public void success(List<List<DAMSound>> lists, Response response) {
                        data.clear();
                        for (List<DAMSound> d : lists) {
                            data.add(new ListRowData(d.get(0).getTitle(), null, null));
                        }
                        recyclerView.getAdapter().notifyDataSetChanged();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        error.printStackTrace();

                        Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Downloading sounds failed, please try again", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                });
    }

    public void loadSearchData() {
        session.checkLogin();

        ApiClient.getDAMApiClient().getTextSearchResults(session.getApiKey(),
                this.searchQuery,
                true,
                new Callback<List<List<DAMSound>>>() {
                    @Override
                    public void success(List<List<DAMSound>> lists, Response response) {
                        data.clear();
                        for (List<DAMSound> d : lists) {
                            data.add(new ListRowData(d.get(0).getTitle(), null, null));
                        }
                        recyclerView.getAdapter().notifyDataSetChanged();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        error.printStackTrace();

                        Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Downloading sounds failed, please try again", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                });
    }

}
