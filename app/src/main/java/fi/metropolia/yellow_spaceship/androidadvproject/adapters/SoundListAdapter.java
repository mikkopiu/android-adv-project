package fi.metropolia.yellow_spaceship.androidadvproject.adapters;

import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

import fi.metropolia.yellow_spaceship.androidadvproject.ActionModeToggleListener;
import fi.metropolia.yellow_spaceship.androidadvproject.R;
import fi.metropolia.yellow_spaceship.androidadvproject.models.DAMSound;

/**
 * RecyclerView.Adapter for sound library's sound lists (DAM results, favourites & recordings).
 * See SoundCategoryListAdapter for categories.
 */
public class SoundListAdapter extends RecyclerView.Adapter<SoundListAdapter.ViewHolder> {
    private final ArrayList<DAMSound> mDataSet;
    private final ISoundLibraryViewHolderClicks mClickListener;
    private final ActionModeToggleListener mToggleListener;
    private final ArrayList<Integer> mSelectedSounds = new ArrayList<>();

    private final boolean showContextMenu;
    private boolean mEditMode = false;

    /**
     * Basic ViewHolder inner class
     */
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            android.support.v7.widget.PopupMenu.OnMenuItemClickListener, View.OnLongClickListener {

        private final ISoundLibraryViewHolderClicks mListener;

        private final TextView mvTvTitle;
        private final ImageButton mFavBtn;
        private final ImageButton mPreviewBtn;

        public ViewHolder(final View itemView, ISoundLibraryViewHolderClicks listener) {
            super(itemView);

            this.mListener = listener;

            // Find buttons for click handling & data binding
            this.mvTvTitle = (TextView) itemView.findViewById(R.id.sound_library_list_text);
            this.mFavBtn = (ImageButton) itemView.findViewById(R.id.sound_library_fav_button);
            this.mPreviewBtn = (ImageButton) itemView.findViewById(R.id.sound_library_preview_button);

            this.mPreviewBtn.setImageResource(R.drawable.ic_play_arrow_24dp);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            this.mFavBtn.setOnClickListener(this);
            this.mPreviewBtn.setOnClickListener(this);
        }

        /**
         * Bind DAMSound to this ViewHolder.
         * Sets icons and text content.
         *
         * @param sound DAMSound to bind
         */
        public void bindSound(DAMSound sound) {

            this.mvTvTitle.setText(sound.getTitle());

            // Edit-mode removes the playback icons
            // and updates background colors for selected items.
            if (mEditMode) {
                if (mSelectedSounds.contains(this.getAdapterPosition())) {
                    // Selected sounds should use a darker background and
                    // the checked checkbox.
                    itemView.setBackgroundResource(R.drawable.sound_library_select_ripple);
                    this.mPreviewBtn.setImageResource(R.drawable.ic_check_box_black_24dp);
                } else {
                    // Non-selected items should return back to the default background and
                    // unchecked checkbox.
                    TypedValue outValue = new TypedValue();
                    itemView.getContext()
                            .getTheme()
                            .resolveAttribute(
                                    android.R.attr.selectableItemBackground,
                                    outValue,
                                    true
                            );
                    itemView.setBackgroundResource(outValue.resourceId);
                    this.mPreviewBtn
                            .setImageResource(R.drawable.ic_check_box_outline_blank_black_24dp);
                }
            } else {
                // Reset styles when no longer in edit-mode
                TypedValue outValue = new TypedValue();
                itemView.getContext()
                        .getTheme()
                        .resolveAttribute(
                                android.R.attr.selectableItemBackground,
                                outValue,
                                true
                        );
                itemView.setBackgroundResource(outValue.resourceId);

                // Set play/pause icon
                if (sound.getIsPlaying()) {
                    this.mPreviewBtn.setImageResource(R.drawable.ic_pause_24dp);
                } else {
                    this.mPreviewBtn.setImageResource(R.drawable.ic_play_arrow_24dp);
                }
            }

            // Context-menu replaces the favorite-button
            if (showContextMenu) {
                this.mFavBtn.setImageResource(R.drawable.ic_more_vert_24dp);
            } else {
                if (sound.getIsFavorite()) {
                    this.mFavBtn.setImageResource(R.drawable.ic_star_24dp);
                } else {
                    this.mFavBtn.setImageResource(R.drawable.ic_star_outline_24dp);
                }
            }
        }

        /**
         * Update the preview button to match play-state
         *
         * @param playing Is sound currently playing
         */
        public void setPlayingState(boolean playing) {
            this.mPreviewBtn.setImageResource(
                    playing ?
                            R.drawable.ic_pause_24dp :
                            R.drawable.ic_play_arrow_24dp
            );
        }

        @Override
        public void onClick(View v) {
            if (getInEditMode()) {
                // In edit-mode we can skip all playback functionality,
                // as the button has been switched to the selection checkbox.
                this.selectItem();
            } else {
                if (v.getId() == R.id.sound_library_fav_button) {
                    // Context-menu replaces the fav-button
                    if (showContextMenu) {
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
        }

        /**
         * Context-menu's item click handling (for favourites)
         *
         * @param item Clicked menu item
         * @return Was event handled (/passed through)
         */
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            boolean handled = false;
            if (item.getItemId() == R.id.upload_sound) {
                this.mListener.onRowUpload(this.getAdapterPosition());
                handled = true;
            } else if (item.getItemId() == R.id.delete_sound) {
                this.mListener.onRowDelete(this.getAdapterPosition());
                handled = true;
            }
            return handled;
        }

        /**
         * Long clicks start the multi-selection mode
         *
         * @param v Clicked view
         * @return Event handled
         */
        @Override
        public boolean onLongClick(View v) {
            // mToggleListener won't be defined if the caller isn't expecting any action modes
            if (mToggleListener != null) {
                this.selectItem();
                return true;
            }

            return false;
        }

        /**
         * Set this item as selected
         */
        private void selectItem() {
            int pos = this.getAdapterPosition();

            if (!getInEditMode()) {
                // Init edit-mode if it wasn't enabled already
                mSelectedSounds.add(pos);
                setInEditMode(true);
            } else {
                // If the sound exists in the selected sounds array,
                // it means the click is meant to remove the selection.
                int ind = mSelectedSounds.indexOf(pos);
                if (ind >= 0) {
                    mSelectedSounds.remove(ind);
                } else {
                    mSelectedSounds.add(pos);
                }

                // Notify to update layout (but only this one; no need to invalidate all items)
                notifyItemChanged(pos);

                // If the final item was removed, change the edit-mode to disabled
                if (mSelectedSounds.size() == 0) {
                    setInEditMode(false);
                } else {
                    // Otherwise just update the title to match the current selection count
                    mToggleListener.setActionModeTitle(mSelectedSounds.size() + " selected");
                }
            }
        }
    }

    /**
     * Constructor
     *
     * @param dataSet         A reference to the data for the adapter
     * @param listener        Listener for ViewHolder's click events
     * @param showContextMenu True to show context menu instead of the favorite-button
     */
    public SoundListAdapter(ArrayList<DAMSound> dataSet,
                            ISoundLibraryViewHolderClicks listener,
                            boolean showContextMenu,
                            ActionModeToggleListener toggleListener) {
        this.mDataSet = dataSet;
        this.mClickListener = listener;
        this.mToggleListener = toggleListener;
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
        return new ViewHolder(v, this.mClickListener);
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

    /**
     * Get the array of selected sounds
     *
     * @return ArrayList of selected DAMSounds
     */
    public ArrayList<DAMSound> getSelectedSounds() {
        ArrayList<DAMSound> sounds = new ArrayList<>();
        for (int pos : this.mSelectedSounds) {
            sounds.add(this.mDataSet.get(pos));
        }

        return sounds;
    }

    /**
     * Toggle edit-mode
     *
     * @param inEditMode Is edit-mode enabled
     */
    public void setInEditMode(boolean inEditMode) {
        this.mEditMode = inEditMode;

        if (this.mToggleListener != null) {
            // Let the toggle listener know about mode-changes
            this.mToggleListener.setActionMode(this.mEditMode);

            if (this.mEditMode) {
                // Set the initial title
                this.mToggleListener.setActionModeTitle(mSelectedSounds.size() + " selected");
            }
        }

        if (!this.mEditMode && this.mSelectedSounds.size() > 0) {
            this.mSelectedSounds.clear();
        }

        notifyDataSetChanged();
    }

    /**
     * Check if edit-mode is enabled
     *
     * @return Is edit-mode enabled
     */
    private boolean getInEditMode() {
        return this.mEditMode;
    }
}
