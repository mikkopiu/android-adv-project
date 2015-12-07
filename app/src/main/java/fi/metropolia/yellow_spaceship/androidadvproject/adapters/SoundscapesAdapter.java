package fi.metropolia.yellow_spaceship.androidadvproject.adapters;

import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

import fi.metropolia.yellow_spaceship.androidadvproject.R;
import fi.metropolia.yellow_spaceship.androidadvproject.models.SoundScapeProject;

/**
 * RecyclerView.Adapter for saved soundscapes in "Your soundscapes"
 */
public class SoundscapesAdapter extends RecyclerView.Adapter<SoundscapesAdapter.ViewHolder> {

    private final ArrayList<SoundScapeProject> mDataSet;
    private final ISoundscapeViewHolderClicks mClickListener;

    /**
     * ViewHolder for a single soundscape in list.
     * Displays a title and a simple context-menu.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            android.support.v7.widget.PopupMenu.OnMenuItemClickListener {

        private final ImageButton mContextMenuBtn;
        private final ISoundscapeViewHolderClicks mListener;
        private final TextView mTextView;

        public ViewHolder(final View itemView, final ISoundscapeViewHolderClicks listener) {
            super(itemView);

            // Find the layout's buttons (for click listeners & data binding)
            this.mContextMenuBtn = (ImageButton) itemView.findViewById(R.id.sound_library_fav_button);
            this.mTextView = (TextView) itemView.findViewById(R.id.sound_library_list_text);

            this.mListener = listener;

            // The view itself will handle the click listening
            // and only pass the necessary actions to the listener.
            this.itemView.setOnClickListener(this);
            this.mContextMenuBtn.setOnClickListener(this);
        }

        /**
         * Bind SoundScapeProject to this ViewHolder.
         * Updates layout to match (titles etc.)
         *
         * @param project SoundScapeProject to bind
         */
        public void bindProject(SoundScapeProject project) {
            this.mTextView.setText(project.getName());
            this.mContextMenuBtn.setImageResource(R.drawable.ic_more_vert_24dp); // Context-menu-icon
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.sound_library_fav_button) {
                // The ViewHolder uses a shared layout: fav_button is the context-menu button
                // in this ViewHolder.
                PopupMenu popup = new PopupMenu(v.getContext(), v);
                popup.inflate(R.menu.soundscape_context);
                popup.setOnMenuItemClickListener(this);
                popup.show();
            } else {
                // Pass the selection event to the listener to handle
                this.mListener.onRowSelect(getLayoutPosition());
            }
        }

        /**
         * Context menu item click handling
         *
         * @param item Clicked menu item
         * @return Was event handled (/passed through)
         */
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            // Pass the action on to the listener
            if (item.getItemId() == R.id.rename_soundscape) {
                this.mListener.onRowRename(getLayoutPosition());
                return true;
            } else if (item.getItemId() == R.id.delete_soundscape) {
                this.mListener.onRowDelete(getLayoutPosition());
                return true;
            }
            return false;
        }
    }

    /**
     * Constructor
     *
     * @param dataSet  Data for this adapter
     * @param listener Click handler for the ViewHolders
     */
    public SoundscapesAdapter(ArrayList<SoundScapeProject> dataSet,
                              ISoundscapeViewHolderClicks listener) {
        this.mDataSet = dataSet;
        this.mClickListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate the view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.sound_library_list_item, parent, false);

        // Assign the view to ViewHolder and return it
        return new ViewHolder(v, this.mClickListener);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        SoundScapeProject item = mDataSet.get(position);
        holder.bindProject(item);
    }

    @Override
    public int getItemCount() {
        if (this.mDataSet != null) {
            return this.mDataSet.size();
        }
        return 0;
    }
}
