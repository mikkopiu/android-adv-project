package fi.metropolia.yellow_spaceship.androidadvproject.adapters;

import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import fi.metropolia.yellow_spaceship.androidadvproject.R;
import fi.metropolia.yellow_spaceship.androidadvproject.models.DAMSound;


public class SoundListAdapter extends RecyclerView.Adapter<SoundListAdapter.ViewHolder> {
    private ArrayList<DAMSound> mDataset;

    /**
     * Basic ViewHolder inner class
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageButton imgButton;

        public ViewHolder(final View itemView) {
            super(itemView);
            imgButton = (ImageButton) itemView.findViewById(R.id.sound_library_fav_button);
        }

    }

    /**
     * Constructor
     * @param dataset A reference to the data for the adapter
     */
    public SoundListAdapter(ArrayList<DAMSound> dataset) {
        this.mDataset = dataset;
    }

    /**
     * Create new views
     * @param parent The ViewGroup into which the new View will be added after it is bound to
     *               an adapter position.
     * @param viewType The view type of the new View.
     * @return New ViewHolder
     */
    @Override
    public SoundListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate the view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.sound_library_list_item, parent, false);

        // Assign the view to ViewHolder and return it
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final SoundListAdapter.ViewHolder holder, int position) {
        final DAMSound item = mDataset.get(position);

        // Find the views in the layout
        TextView textView = (TextView) holder.itemView.findViewById(R.id.sound_library_list_text);
        final ImageButton imgButton = (ImageButton) holder.itemView.findViewById(R.id.sound_library_fav_button);

        // And set data to the views
        textView.setText(item.getTitle());

        // Set favorite-button's image based on favorite-status
        if (item.getIsFavorite()) {
            holder.imgButton.setImageResource(R.drawable.ic_favorite_48dp);
        } else {
            holder.imgButton.setImageResource(R.drawable.ic_favorite_outline_48dp);
        }

        holder.imgButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.sound_library_fav_button) {
                    boolean isFavorite = item.getIsFavorite();
                    if (!isFavorite) {
                        item.setIsFavorite(true);
                        imgButton.setImageResource(R.drawable.ic_favorite_48dp);
                    } else {
                        item.setIsFavorite(false);
                        imgButton.setImageResource(R.drawable.ic_favorite_outline_48dp);
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if (mDataset != null) {
            return mDataset.size();
        } else {
            return 0;
        }
    }
}
