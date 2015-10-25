package fi.metropolia.yellow_spaceship.androidadvproject.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import fi.metropolia.yellow_spaceship.androidadvproject.R;
import fi.metropolia.yellow_spaceship.androidadvproject.models.SoundScapeProject;

public class SoundscapesAdapter extends RecyclerView.Adapter<SoundscapesAdapter.ViewHolder> {

    private ArrayList<SoundScapeProject> mDataSet;
    private View.OnClickListener listener;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private View itemView;

        public ViewHolder(View v, View.OnClickListener listener) {
            super(v);
            if(listener != null) {
                v.setOnClickListener(listener);
            }
            this.itemView = v;
        }
    }

    public SoundscapesAdapter(ArrayList<SoundScapeProject> dataSet,
                              View.OnClickListener listener) {
        this.mDataSet = dataSet;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate the view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.sound_library_list_item, parent, false);

        // Assign the view to ViewHolder and return it
        return new ViewHolder(v, this.listener);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // Find the views in the layout
        TextView textView = (TextView) holder.itemView.findViewById(R.id.sound_library_list_text);

        // And set data to the views
        textView.setText(mDataSet.get(position).getName());
    }

    @Override
    public int getItemCount() {
        if (this.mDataSet != null) {
            return this.mDataSet.size();
        }
        return 0;
    }
}
