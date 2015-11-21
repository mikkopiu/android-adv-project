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
    private final ISoundscapeViewHolderClicks listener;

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            android.support.v7.widget.PopupMenu.OnMenuItemClickListener {

        private final ImageButton contextMenuBtn;
        private final ISoundscapeViewHolderClicks mListener;
        private final TextView textView;

        public ViewHolder(final View itemView, ISoundscapeViewHolderClicks listener) {
            super(itemView);
            this.contextMenuBtn = (ImageButton) itemView.findViewById(R.id.sound_library_fav_button);
            this.textView = (TextView) itemView.findViewById(R.id.sound_library_list_text);

            this.mListener = listener;

            this.itemView.setOnClickListener(this);
            this.contextMenuBtn.setOnClickListener(this);
        }

        /**
         * Bind SoundScapeProject to this ViewHolder.
         * Updates layout to match (titles etc.)
         * @param project SoundScapeProject to bind
         */
        public void bindProject(SoundScapeProject project) {
            this.textView.setText(project.getName());

            this.contextMenuBtn.setImageResource(R.drawable.ic_more_vert_24dp);
        }
        
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.sound_library_fav_button) {
                PopupMenu popup = new PopupMenu(v.getContext(), v);
                popup.inflate(R.menu.soundscape_context);
                popup.setOnMenuItemClickListener(this);
                popup.show();
            } else {
                this.mListener.onRowSelect(getLayoutPosition());
            }
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
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

    public SoundscapesAdapter(ArrayList<SoundScapeProject> dataSet,
                              ISoundscapeViewHolderClicks listener) {
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
