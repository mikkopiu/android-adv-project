package fi.metropolia.yellow_spaceship.androidadvproject.adapters;

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
import fi.metropolia.yellow_spaceship.androidadvproject.models.ListRowData;

/**
 * RecyclerView.Adapter for sound library's category list
 */
public class SoundCategoryListAdapter extends RecyclerView.Adapter<SoundCategoryListAdapter.ViewHolder> {

    private final ArrayList<ListRowData> dataSet;
    private final View.OnClickListener listener;

    /**
     * ViewHolder for categories.
     * Contains special styling for categories with icons (favourites & recordings).
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView mTextView;
        private final ImageButton mListIcon;

        public ViewHolder(final View itemView, final View.OnClickListener listener) {
            super(itemView);

            // Find elements for click listeners & data binding
            this.mTextView = (TextView) itemView.findViewById(R.id.sound_library_list_text);
            this.mListIcon = (ImageButton) itemView.findViewById(R.id.sound_library_preview_button);

            if (listener != null) {
                itemView.setOnClickListener(listener);
            }
        }

        /**
         * Bind ListRowData to this ViewHolder.
         * Sets proper icons and text content.
         *
         * @param data ListRowData to set
         */
        public void bindData(ListRowData data) {

            this.mTextView.setText(data.getCaption());

            // If the row has an icon, that means it is one of the pre-defined categories,
            // i.e. it needs a different styling.
            if (data.getIcon() != null) {
                Drawable icon = ContextCompat.getDrawable(
                        this.itemView.getContext(),
                        data.getIcon()
                );

                this.itemView
                        .setBackgroundResource(R.drawable.sound_library_select_ripple);

                // The ImageButtons are just icons here, not meant to be clicked
                this.mListIcon.setImageDrawable(icon);
                this.mListIcon.setClickable(false);
                this.mListIcon.setFocusable(false);

                // In order to show the parent view's ripple effect properly,
                // the button's background needs to be transparent.
                int color = ContextCompat.getColor(
                        this.itemView.getContext(),
                        android.R.color.transparent
                );
                this.mListIcon.setBackgroundColor(color);
            } else {

                // This item has no icon => fake the ImageButton as left padding
                this.mTextView.setPadding(
                        this.mListIcon.getMaxWidth(),
                        this.mTextView.getPaddingTop(),
                        this.mTextView.getPaddingRight(),
                        this.mTextView.getPaddingBottom()
                );
            }
        }
    }

    /**
     * Constructor
     *
     * @param listener Click listener for the adapter's items
     * @param dataSet  Data for the adapter.
     */
    public SoundCategoryListAdapter(View.OnClickListener listener, ArrayList<ListRowData> dataSet) {

        this.dataSet = dataSet;
        this.listener = listener;

    }

    /**
     * Get a ListRowData-object with position
     *
     * @param position index position in the dataSet
     * @return ListRowData object
     */
    public ListRowData getDataWithPosition(int position) {
        if (dataSet.size() > position)
            return dataSet.get(position);
        else
            return null;
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
        ListRowData d = dataSet.get(position);
        holder.bindData(d);
    }

    @Override
    public int getItemCount() {
        return dataSet != null ? dataSet.size() : 0;
    }

}
