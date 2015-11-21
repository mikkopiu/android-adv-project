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
public class SoundLibraryListAdapter extends RecyclerView.Adapter<SoundLibraryListAdapter.ViewHolder> {

    private final ArrayList<ListRowData> dataSet;
    private final View.OnClickListener listener;

    /**
     * Basic ViewHolder inner class
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {

        public final View view;
        public final LinearLayout listItemContainerView;

        public ViewHolder(View v, View.OnClickListener listener) {
            super(v);
            if (listener != null)
                v.setOnClickListener(listener);
            this.view = v;
            this.listItemContainerView = (LinearLayout) v.findViewById(R.id.list_item_container);
        }

    }

    /**
     * Constructor
     *
     * @param listener Click listener for the adapter's items
     * @param dataSet Data for the adapter.
     */
    public SoundLibraryListAdapter(View.OnClickListener listener, ArrayList<ListRowData> dataSet) {

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
    public SoundLibraryListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        // Inflate the view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.sound_library_list_item, parent, false);

        // Assign the view to ViewHolder and return it
        return new ViewHolder(v, this.listener);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        // Find the views in the layout
        TextView textView = (TextView) holder.view.findViewById(R.id.sound_library_list_text);
        ImageButton listIconView = (ImageButton) holder.view.findViewById(R.id.sound_library_preview_button);

        // And set data to the views
        textView.setText(dataSet.get(position).getCaption());
        if (dataSet.get(position).getIcon() != null) {
            Drawable icon = ContextCompat.getDrawable(
                    holder.itemView.getContext(), dataSet.get(position).getIcon()
            );

            // If the row has an icon, that means it is one of the pre-defined categories,
            // i.e. it needs a different styling.
            holder.listItemContainerView.setBackgroundResource(R.drawable.sound_library_select_ripple);

            // The ImageButtons are just icons here, not meant to be clicked
            listIconView.setImageDrawable(icon);
            listIconView.setClickable(false);
            listIconView.setFocusable(false);

            // In order to show the parent view's ripple effect properly, the button's background
            // needs to be transparent.
            int color = ContextCompat.getColor(
                    holder.itemView.getContext(),
                    android.R.color.transparent
            );
            listIconView.setBackgroundColor(color);
        } else {
            textView.setPadding(
                    listIconView.getMaxWidth(), // Fake the ImageButton as left padding
                    textView.getPaddingTop(),
                    textView.getPaddingRight(),
                    textView.getPaddingBottom()
            );
        }
    }

    @Override
    public int getItemCount() {
        return dataSet != null ? dataSet.size() : 0;
    }

}
