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

public class SoundscapesAdapter extends RecyclerView.Adapter<SoundscapesAdapter.ViewHolder> {

    private final ArrayList<SoundScapeProject> mDataSet;
    private final ViewHolder.ISoundscapeViewHolderClicks listener;

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            android.support.v7.widget.PopupMenu.OnMenuItemClickListener {

        private final View itemView;
        private final ImageButton contextMenuBtn;
        private final ISoundscapeViewHolderClicks mListener;

        public ViewHolder(View v, ISoundscapeViewHolderClicks listener) {
            super(v);
            this.itemView = v;
            this.contextMenuBtn = (ImageButton) v.findViewById(R.id.sound_library_fav_button);
            this.mListener = listener;
            v.setOnClickListener(this);
            this.contextMenuBtn.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.sound_library_fav_button) {
                PopupMenu popup = new PopupMenu(v.getContext(), v);
                popup.inflate(R.menu.soundscape_context);
                popup.setOnMenuItemClickListener(this);
                popup.show();
            } else {
                this.mListener.onRowSelect(v, getLayoutPosition());
            }
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            if (item.getItemId() == R.id.rename_soundscape) {
                this.mListener.onRowRename(itemView, getLayoutPosition());
                return true;
            } else if (item.getItemId() == R.id.delete_soundscape) {
                this.mListener.onRowDelete(itemView, getLayoutPosition());
                return true;
            }
            return false;
        }

        public interface ISoundscapeViewHolderClicks {
            void onRowSelect(View view, int layoutPosition);

            void onRowRename(View view, int layoutPosition);

            void onRowDelete(View view, int layoutPosition);
        }
    }

    public SoundscapesAdapter(ArrayList<SoundScapeProject> dataSet,
                              ViewHolder.ISoundscapeViewHolderClicks listener) {
        this.mDataSet = dataSet;
        this.listener = listener;
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
        // Find the views in the layout
        TextView textView = (TextView) holder.itemView.findViewById(R.id.sound_library_list_text);
        ImageButton contextMenuBtn = (ImageButton) holder.itemView
                .findViewById(R.id.sound_library_fav_button); // Re-use the fav-button as the menu-button

        // And set data to the views
        textView.setText(mDataSet.get(position).getName());

        contextMenuBtn.setImageResource(R.drawable.ic_more_vert_24dp);
    }

    @Override
    public int getItemCount() {
        if (this.mDataSet != null) {
            return this.mDataSet.size();
        }
        return 0;
    }
}
