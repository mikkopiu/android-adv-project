package fi.metropolia.yellow_spaceship.androidadvproject.menu;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import fi.metropolia.yellow_spaceship.androidadvproject.R;

/**
 * Adapter for SoundLibrary RecyclerView
 */
public class SoundLibraryListAdapter extends RecyclerView.Adapter<SoundLibraryListAdapter.ViewHolder> {

    private Context context;
    private ArrayList<ListRowData> dataSet;
    private View.OnClickListener listener;

    /**
     * Basic ViewHolder inner class
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {

        public View view;

        public ViewHolder(View v, View.OnClickListener listener) {
            super(v);
            v.setOnClickListener(listener);
            this.view = v;
        }

    }

    /**
     * Constructor
     * @param context The activity
     * @param dataSet Data for the adapter.
     */
    public SoundLibraryListAdapter(Context context, View.OnClickListener listener, ArrayList<ListRowData> dataSet) {

        this.dataSet = dataSet;
        this.context = context;
        this.listener = listener;

    }

    /**
     * Get a ListRowData-object with position
     * @param position index position in the dataSet
     * @return ListRowData object
     */
    public ListRowData getDataWithPosition(int position) {
        if(dataSet.size() > position)
            return dataSet.get(position);
        else
            return null;
    }

    @Override
    public SoundLibraryListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        // Inflate the view
        View v = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.sound_library_list_item, parent, false);

        // Assign the view to ViewHolder and return it
        ViewHolder vh = new ViewHolder(v, this.listener);

        return vh;

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        // Find the views in the layout
        TextView textView = (TextView)holder.view.findViewById(R.id.sound_library_list_text);
        ImageView imageView = (ImageView)holder.view.findViewById(R.id.sound_library_list_button);

        // And set data to the views
        textView.setText(dataSet.get(position).getCaption());
        if(dataSet.get(position).getIcon() != null){
            imageView.setImageResource(dataSet.get(position).getIcon());
        } else {
            imageView.setImageResource(R.drawable.ic_audiotrack_black_48dp);
            imageView.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

}
