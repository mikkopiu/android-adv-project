package fi.metropolia.yellow_spaceship.androidadvproject.adapters;

import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import fi.metropolia.yellow_spaceship.androidadvproject.R;
import fi.metropolia.yellow_spaceship.androidadvproject.models.ListRowData;

/**
 * Adapter for SoundLibrary RecyclerView
 */
public class SoundCategoryListAdapter extends RecyclerView.Adapter<SoundCategoryListAdapter.ViewHolder> {

    private final ArrayList<ListRowData> dataSet;
    private final View.OnClickListener listener;

    /**
     * Basic ViewHolder inner class
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView textView;
        private final ImageButton listIcon;
        private final LinearLayout listItemContainerView;

        public ViewHolder(final View itemView, View.OnClickListener listener) {
            super(itemView);

            this.textView = (TextView) itemView.findViewById(R.id.sound_library_list_text);
            this.listIcon = (ImageButton) itemView.findViewById(R.id.sound_library_preview_button);
            this.listItemContainerView = (LinearLayout) itemView.findViewById(R.id.list_item_container);

            if (listener != null) {
                itemView.setOnClickListener(listener);
            }
        }

        /**
         * Bind ListRowData to this ViewHolder.
         * Sets proper icons and text content.
         * @param data ListRowData to set
         */
        public void bindData(ListRowData data) {

            this.textView.setText(data.getCaption());

            if (data.getIcon() != null) {
                Drawable icon = ContextCompat.getDrawable(
                        this.itemView.getContext(),
                        data.getIcon()
                );

                // If the row has an icon, that means it is one of the pre-defined categories,
                // i.e. it needs a different styling.
                this.listItemContainerView
                        .setBackgroundResource(R.drawable.sound_library_select_ripple);

                // The ImageButtons are just icons here, not meant to be clicked
                this.listIcon.setImageDrawable(icon);
                this.listIcon.setClickable(false);
                this.listIcon.setFocusable(false);

                // In order to show the parent view's ripple effect properly,
                // the button's background needs to be transparent.
                int color = ContextCompat.getColor(
                        this.itemView.getContext(),
                        android.R.color.transparent
                );
                this.listIcon.setBackgroundColor(color);
            } else {

                // This item has no icon => fake the ImageButton as left padding
                this.textView.setPadding(
                        this.listIcon.getMaxWidth(),
                        this.textView.getPaddingTop(),
                        this.textView.getPaddingRight(),
                        this.textView.getPaddingBottom()
                );
            }
        }
    }

    /**
     * Constructor
     *
     * @param listener Click listener for the adapter's items
     * @param dataSet Data for the adapter.
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

    public void swap(ArrayList<ListRowData> data) {
        if (dataSet != null) {
            dataSet.clear();
            dataSet.addAll(data);
            notifyDataSetChanged();
        }
    }

    @Override
    public SoundCategoryListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

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
