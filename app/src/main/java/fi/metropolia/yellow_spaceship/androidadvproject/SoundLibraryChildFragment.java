package fi.metropolia.yellow_spaceship.androidadvproject;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Set onClickListener for back button
        ((Toolbar)((AppCompatActivity)getActivity()).findViewById(R.id.toolbar)).setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.sound_library_child_fragment, container, false);

    }

}
