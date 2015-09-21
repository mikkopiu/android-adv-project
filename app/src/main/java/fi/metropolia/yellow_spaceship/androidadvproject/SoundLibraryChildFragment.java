package fi.metropolia.yellow_spaceship.androidadvproject;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.List;

import fi.metropolia.yellow_spaceship.androidadvproject.api.ApiClient;
import fi.metropolia.yellow_spaceship.androidadvproject.models.DAMSound;
import fi.metropolia.yellow_spaceship.androidadvproject.models.SoundCategory;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Petri on 19.9.2015.
 */
public class SoundLibraryChildFragment extends Fragment {

    public static SoundLibraryChildFragment newInstance() {
        SoundLibraryChildFragment fragment = new SoundLibraryChildFragment();
        return fragment;
    }

    public SoundLibraryChildFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        loadData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        ((AppCompatActivity)getActivity()).getSupportActionBar().setHomeAsUpIndicator(null);

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.sound_library_child_fragment, container, false);

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
        ApiClient.getDAMApiClient().getCategory("M4B-lnwO3clT-MGJmnMM1NGOpJF4q4YNxaBoQzLTjMx9dit4w1QoUZxO3LuVJeQWO03fxaNfdX38tMN1oJ_2ViQq7h_2e1hKcv_h_jAhYXPJJnMayzS-Ih6FcgwvBVaB",
                SoundCategory.HUMAN,
                true,
                new Callback<List<List<DAMSound>>>() {
                    @Override
                    public void success(List<List<DAMSound>> lists, Response response) {
                        for (List<DAMSound> d : lists) {
                            System.out.println(d.get(0).getTitle());
                        }
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
