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
import fi.metropolia.yellow_spaceship.androidadvproject.models.DAMSound;


public class SoundListAdapter extends RecyclerView.Adapter<SoundListAdapter.ViewHolder> {
    private final ArrayList<DAMSound> mDataSet;
    private final ISoundLibraryViewHolderClicks listener;
    private final boolean showContextMenu;

    /**
     * Basic ViewHolder inner class
     */
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            android.support.v7.widget.PopupMenu.OnMenuItemClickListener {

        private final ISoundLibraryViewHolderClicks mListener;

        private final TextView tvTitle;
        private final ImageButton favBtn;
        private final ImageButton previewBtn;

        private final boolean showContextMenu;

        public ViewHolder(final View itemView, ISoundLibraryViewHolderClicks listener,
                          boolean showContextMenu) {
            super(itemView);

            this.mListener = listener;
            this.showContextMenu = showContextMenu;

            this.tvTitle = (TextView) itemView.findViewById(R.id.sound_library_list_text);
            this.favBtn = (ImageButton) itemView.findViewById(R.id.sound_library_fav_button);
            this.previewBtn = (ImageButton) itemView.findViewById(R.id.sound_library_preview_button);

            this.previewBtn.setImageResource(R.drawable.ic_play_arrow_24dp);

            itemView.setOnClickListener(this);
            this.favBtn.setOnClickListener(this);
            this.previewBtn.setOnClickListener(this);
        }

        /**
         * Bind DAMSound to this ViewHolder.
         * Sets icons and text content.
         * @param sound DAMSound to bind
         */
        public void bindSound(DAMSound sound) {

            this.tvTitle.setText(sound.getTitle());

            // Set play/pause icon
            if(sound.getIsPlaying()) {
                this.previewBtn.setImageResource(R.drawable.ic_pause_24dp);
            } else {
                this.previewBtn.setImageResource(R.drawable.ic_play_arrow_24dp);
            }

            // Context-menu replaces the favorite-button
            if (showContextMenu) {
                this.favBtn.setImageResource(R.drawable.ic_more_vert_24dp);
            } else {
                if (sound.getIsFavorite()) {
                    this.favBtn.setImageResource(R.drawable.ic_star_24dp);
                } else {
                    this.favBtn.setImageResource(R.drawable.ic_star_outline_24dp);
                }
            }
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.sound_library_fav_button) {
                // Context-menu replaces the fav-button
                if (this.showContextMenu) {
                    PopupMenu popup = new PopupMenu(v.getContext(), v);
                    popup.inflate(R.menu.recordings_context);
                    popup.setOnMenuItemClickListener(this);
                    popup.show();
                } else {
                    this.mListener.onFavorite(this.getAdapterPosition());
                }
            } else if (v.getId() == R.id.sound_library_preview_button) {
                this.mListener.onPlayPauseToggle(this.getAdapterPosition());
            } else {
                this.mListener.onRowSelect(this.getAdapterPosition());
            }
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            if (item.getItemId() == R.id.upload_sound) {
                this.mListener.onRowUpload(this.getAdapterPosition());
                return true;
            } else if (item.getItemId() == R.id.delete_sound) {
                this.mListener.onRowDelete(this.getAdapterPosition());
                return true;
            }
            return false;
        }
    }

    /**
     * Constructor
     *
     * @param dataSet  A reference to the data for the adapter
     * @param listener Listener for ViewHolder's click events
     * @param showContextMenu True to show context menu instead of the favorite-button
     */
    public SoundListAdapter(ArrayList<DAMSound> dataSet,
                            ISoundLibraryViewHolderClicks listener,
                            boolean showContextMenu) {
        this.mDataSet = dataSet;
        this.listener = listener;
        this.showContextMenu = showContextMenu;
    }

    /**
     * Create new views
     *
     * @param parent   The ViewGroup into which the new View will be added after it is bound to
     *                 an adapter position.
     * @param viewType The view type of the new View.
     * @return New ViewHolder
     */
    @Override
    public SoundListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate the view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.sound_library_list_item, parent, false);

        // Assign the view to ViewHolder and return it
        return new ViewHolder(v, this.listener, this.showContextMenu);
    }

    @Override
    public void onBindViewHolder(final SoundListAdapter.ViewHolder holder, int position) {
        DAMSound item = mDataSet.get(position);
        holder.bindSound(item);
    }

    @Override
    public int getItemCount() {
        if (mDataSet != null) {
            return mDataSet.size();
        } else {
            return 0;
        }
    }
}
