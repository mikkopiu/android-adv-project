package fi.metropolia.yellow_spaceship.androidadvproject.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.os.Build;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;

import fi.metropolia.yellow_spaceship.androidadvproject.R;
import fi.metropolia.yellow_spaceship.androidadvproject.models.ProjectSound;

/**
 * Adapter for a RecyclerView of CardViews for Sounds (e.g. in create-view)
 */
public class SoundCardViewAdapter extends RecyclerView.Adapter<SoundCardViewAdapter.ViewHolder> {

    private final ArrayList<ProjectSound> mDataSet;
    private final ViewHolder.IProjectSoundViewHolderClicks listener;

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            SeekBar.OnSeekBarChangeListener {
        private final IProjectSoundViewHolderClicks mListener;

        public final ImageButton closeBtn;
        public final SeekBar volBar;
        public final CardView cardView;

        public ViewHolder(View itemView, IProjectSoundViewHolderClicks listener) {
            super(itemView);

            this.mListener = listener;

            this.closeBtn = (ImageButton) itemView.findViewById(R.id.close_btn);
            this.volBar = (SeekBar) itemView.findViewById(R.id.volume_slider);
            this.cardView = (CardView) itemView.findViewById(R.id.create_card_view);

            this.closeBtn.setOnClickListener(this);
            this.volBar.setOnSeekBarChangeListener(this);
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.close_btn) {
                this.mListener.onCloseClicked(v, getLayoutPosition());
            }
        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            this.mListener.onVolumeChange(seekBar, getLayoutPosition(), progress);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            // Do nothing
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            // Do nothing
        }

        // TODO: add more (loop, random)
        public interface IProjectSoundViewHolderClicks {
            void onCloseClicked(View view, int layoutPosition);

            void onVolumeChange(SeekBar seekBar, int layoutPosition, int progress);
        }
    }

    public SoundCardViewAdapter(ArrayList<ProjectSound> dataSet,
                                SoundCardViewAdapter.ViewHolder.IProjectSoundViewHolderClicks listener) {
        this.mDataSet = dataSet;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate the view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.sound_cardview, parent, false);

        // Assign the view to ViewHolder and return it
        return new ViewHolder(v, this.listener);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ProjectSound item = mDataSet.get(position);

        // Find the views in the layout
        TextView textView = (TextView) holder.itemView.findViewById(R.id.sound_title);
        SeekBar volBar = (SeekBar) holder.itemView.findViewById(R.id.volume_slider);

        // And set data to the views
        textView.setText(item.getTitle());
        volBar.setProgress((int) (item.getVolume() * 100));

        // TODO: Replace with actual logic (maybe by index?)
        int cardBackgroundColorId;

        switch (item.getCategory()) {
            case HUMAN:
                cardBackgroundColorId = R.color.acc_card_bac_1;
                break;
            case STORY:
                cardBackgroundColorId = R.color.acc_card_bac_3;
                break;
            case NATURE:
                cardBackgroundColorId = R.color.acc_card_bac_2;
                break;
            case MACHINE:
                cardBackgroundColorId = R.color.acc_card_bac_4;
                break;
            default:
                cardBackgroundColorId = R.color.acc_card_bac_1;
        }

        int color;
        int volBarColor;
        Context context = holder.cardView.getContext();
        Resources res = context.getResources();

        if (Build.VERSION.SDK_INT < 23) {
            color = res.getColor(cardBackgroundColorId);
            volBarColor = res.getColor(R.color.volbar_white);
        } else {
            color = res.getColor(cardBackgroundColorId, context.getTheme());
            volBarColor = res.getColor(R.color.volbar_white, context.getTheme());
        }
        holder.cardView.setCardBackgroundColor(color);

        // Fix volume SeekBar's (and its thumb's) colour
        holder.volBar.getProgressDrawable().setColorFilter(volBarColor, PorterDuff.Mode.SRC_ATOP);
        holder.volBar.getThumb().setColorFilter(volBarColor, PorterDuff.Mode.SRC_ATOP);
    }

    @Override
    public int getItemCount() {
        if (this.mDataSet != null) {
            return this.mDataSet.size();
        } else {
            return 0;
        }
    }
}
