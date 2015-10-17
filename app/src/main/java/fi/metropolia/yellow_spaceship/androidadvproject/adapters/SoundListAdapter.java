package fi.metropolia.yellow_spaceship.androidadvproject.adapters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

import fi.metropolia.yellow_spaceship.androidadvproject.R;
import fi.metropolia.yellow_spaceship.androidadvproject.database.DAMSoundContract;
import fi.metropolia.yellow_spaceship.androidadvproject.models.DAMSound;
import fi.metropolia.yellow_spaceship.androidadvproject.providers.SoundContentProvider;


public class SoundListAdapter extends RecyclerView.Adapter<SoundListAdapter.ViewHolder> {
    private ArrayList<DAMSound> mDataSet;
    private ViewHolder.ISoundViewHolderClicks listener;
    private Context context;

    /**
     * Basic ViewHolder inner class
     */
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ISoundViewHolderClicks mListener;

        public TextView tvTitle;
        public ImageButton favBtn;
        public ImageButton previewBtn;

        public ViewHolder(final View itemView, ISoundViewHolderClicks listener) {
            super(itemView);

            this.mListener = listener;

            this.tvTitle = (TextView) itemView.findViewById(R.id.sound_library_list_text);
            this.favBtn = (ImageButton) itemView.findViewById(R.id.sound_library_fav_button);
            this.previewBtn = (ImageButton) itemView.findViewById(R.id.sound_library_preview_button);

            this.previewBtn.setImageResource(R.drawable.ic_play_arrow_48dp);

            itemView.setOnClickListener(this);
            this.favBtn.setOnClickListener(this);
            this.previewBtn.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.sound_library_fav_button) {
                this.mListener.onFavorite(v, getLayoutPosition());
            } else if (v.getId() == R.id.sound_library_preview_button) {
                this.mListener.onPlayPauseToggle(v, getLayoutPosition());
            } else {
                this.mListener.onRowSelect(v, getLayoutPosition());
            }
        }

        public interface ISoundViewHolderClicks {
            void onRowSelect(View view, int layoutPosition);
            void onFavorite(View view, int layoutPosition);
            void onPlayPauseToggle(View view, int layoutPosition);
        }

    }

    /**
     * Constructor
     * @param dataSet A reference to the data for the adapter
     * @param listener
     * @param context
     */
    public SoundListAdapter(ArrayList<DAMSound> dataSet,
                            SoundListAdapter.ViewHolder.ISoundViewHolderClicks listener,
                            Context context) {
        this.mDataSet = dataSet;
        this.listener = listener;
        this.context = context;
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
        return new ViewHolder(v, this.listener);
    }

    @Override
    public void onBindViewHolder(final SoundListAdapter.ViewHolder holder, int position) {
        final DAMSound item = mDataSet.get(position);

        // Find the views in the layout
        TextView textView = (TextView) holder.itemView.findViewById(R.id.sound_library_list_text);

        // And set data to the views
        textView.setText(item.getTitle());

        Cursor cursor = this.context.getContentResolver().query(
                SoundContentProvider.CONTENT_URI,
                new String[] {
                        DAMSoundContract.DAMSoundEntry.COLUMN_NAME_IS_FAVORITE,
                        DAMSoundContract.DAMSoundEntry.COLUMN_NAME_FILE_NAME
                },
                DAMSoundContract.DAMSoundEntry.COLUMN_NAME_SOUND_ID + "=?",
                new String[] {item.getFormattedSoundId()},
                null
        );

        boolean isFavorite = false;
        String fileName = null;
        if (cursor != null) {
            if (cursor.moveToNext()) {
                isFavorite = cursor.getInt(0) == 1;
                fileName = cursor.getString(1);

                // Set favorite-button's image based on favorite-status
                item.setIsFavorite(isFavorite);
                // Set file name for the DAMSound, null if there is no local copy of the file
                item.setFileName(fileName);
            }

            cursor.close();
        }

        if (isFavorite) {
            holder.favBtn.setImageResource(R.drawable.ic_favorite_48dp);
        } else {
            holder.favBtn.setImageResource(R.drawable.ic_favorite_outline_48dp);
        }
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
