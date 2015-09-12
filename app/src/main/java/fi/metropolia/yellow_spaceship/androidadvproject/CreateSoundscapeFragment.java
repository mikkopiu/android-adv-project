package fi.metropolia.yellow_spaceship.androidadvproject;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Petri on 12.9.2015.
 */
public class CreateSoundscapeFragment extends Fragment {
    public static CreateSoundscapeFragment newInstance() {
        CreateSoundscapeFragment fragment = new CreateSoundscapeFragment();
        return fragment;
    }

    public CreateSoundscapeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.create_soundscape_fragment, container, false);

    }
}
