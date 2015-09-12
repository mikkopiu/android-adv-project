package fi.metropolia.yellow_spaceship.androidadvproject;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Petri on 12.9.2015.
 */
public class ScanAndListenFragment extends Fragment {
    public static ScanAndListenFragment newInstance() {
        ScanAndListenFragment fragment = new ScanAndListenFragment();
        return fragment;
    }

    public ScanAndListenFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.scan_and_listen_fragment, container, false);

    }
}
