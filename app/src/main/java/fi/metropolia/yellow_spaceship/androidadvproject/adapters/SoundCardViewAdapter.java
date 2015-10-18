package fi.metropolia.yellow_spaceship.androidadvproject.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import fi.metropolia.yellow_spaceship.androidadvproject.R;
import fi.metropolia.yellow_spaceship.androidadvproject.models.DAMSound;

public class SoundCardViewAdapter extends RecyclerView.Adapter<SoundCardViewAdapter.ViewHolder> {

    private ArrayList<DAMSound> mDataSet;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(final View itemView) {
            super(itemView);
        }
    }

    public SoundCardViewAdapter(ArrayList<DAMSound> dataSet) {
        this.mDataSet = dataSet;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate the view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.create_soundscape_list_item, parent, false);

        // Assign the view to ViewHolder and return it
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        DAMSound item = mDataSet.get(position);

        // Find the views in the layout
        TextView textView = (TextView) holder.itemView.findViewById(R.id.sound_title);

        // And set data to the views
        textView.setText(item.getTitle());
    }

    @Override
    public int getItemCount() {
        if (this.mDataSet != null) {
            return this.mDataSet.size();
        } else {
            return 0;
        }
    }
}
