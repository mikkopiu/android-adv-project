package fi.metropolia.yellow_spaceship.androidadvproject.adapters;

import android.content.Context;
import android.graphics.PorterDuff;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.support.v7.widget.SwitchCompat;
import android.widget.TextView;

import java.util.ArrayList;

import fi.metropolia.yellow_spaceship.androidadvproject.R;
import fi.metropolia.yellow_spaceship.androidadvproject.models.ProjectSound;

/**
 * Adapter for a RecyclerView of CardViews for Sounds (e.g. in create-view)
 */
public class SoundCardViewAdapter extends RecyclerView.Adapter<SoundCardViewAdapter.ViewHolder> {

    private final ArrayList<ProjectSound> mDataSet;
    private final IProjectSoundViewHolderClicks listener;

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            SeekBar.OnSeekBarChangeListener,
            SwitchCompat.OnCheckedChangeListener {

        private final IProjectSoundViewHolderClicks mListener;
        private int prevProgress;

        private final ImageButton volBtn;
        private final SeekBar volBar;
        private final CardView cardView;
        private final SwitchCompat randomSwitch;
        private final TextView textView;

        public ViewHolder(final View itemView, IProjectSoundViewHolderClicks listener) {
            super(itemView);

            this.mListener = listener;

            this.textView = (TextView) itemView.findViewById(R.id.sound_title);
            this.volBar = (SeekBar) itemView.findViewById(R.id.volume_slider);
            this.cardView = (CardView) itemView.findViewById(R.id.create_card_view);
            this.randomSwitch = (SwitchCompat) itemView.findViewById(R.id.randomize_switch);
            this.volBtn = (ImageButton) itemView.findViewById(R.id.volume_slider_btn);

            itemView.findViewById(R.id.close_btn).setOnClickListener(this);
            this.volBtn.setOnClickListener(this);
            this.volBar.setOnSeekBarChangeListener(this);
            this.randomSwitch.setOnCheckedChangeListener(this);

            this.prevProgress = 100;
        }

        /**
         * Bind a ProjectSound's data to this ViewHolder
         *
         * @param sound Sound to bind
         */
        public void bindSound(ProjectSound sound) {
            this.textView.setText(sound.getTitle());
            this.textView.setAllCaps(true); // XML value for capitalization is overridden when using setText
            this.volBar.setProgress((int) (sound.getVolume() * 100));
            this.randomSwitch.setChecked(sound.getIsRandom());

            // Get colours for view elements
            Context context = this.itemView.getContext();
            int color = ContextCompat.getColor(context, getCardBackgroundColorId(sound));
            int volBarColor = ContextCompat.getColor(context, R.color.volbar_white);

            // Update card's colour based on our colouring rules
            this.cardView.setCardBackgroundColor(color);

            // Fix volume SeekBar's (and its thumb's) and Randomize-switch's colours
            this.volBar.getProgressDrawable().setColorFilter(volBarColor, PorterDuff.Mode.SRC_ATOP);
            this.volBar.getThumb().setColorFilter(volBarColor, PorterDuff.Mode.SRC_ATOP);
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.close_btn) {
                this.mListener.onCloseClick(getLayoutPosition());
            } else if (v.getId() == R.id.volume_slider_btn) {
                this.volBar.setProgress(this.prevProgress == 0 ? 100 : 0);
            }
        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            // Update icon when volume hits zero
            if (progress == 0) {
                this.volBtn.setImageResource(R.drawable.ic_volume_mute_white_24dp);
            } else if (this.prevProgress == 0) { // Prevent unnecessary reload of image resources
                this.volBtn.setImageResource(R.drawable.ic_volume_up_white_24dp);
            }
            this.prevProgress = progress;

            this.mListener.onVolumeChange(getLayoutPosition(), progress);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            // Do nothing
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            // Do nothing
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            this.mListener.onRandomizeCheckedChange(getLayoutPosition(), isChecked);
        }
    }

    public SoundCardViewAdapter(ArrayList<ProjectSound> dataSet,
                                IProjectSoundViewHolderClicks listener) {
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
        holder.bindSound(item);
    }

    @Override
    public int getItemCount() {
        if (this.mDataSet != null) {
            return this.mDataSet.size();
        } else {
            return 0;
        }
    }

    /**
     * Select a colour to use for a sound cardview.
     * TODO: Replace with actual logic (maybe by index?)
     *
     * @param sound ProjectSound for the current ViewHolder
     * @return Background colour id
     */
    private static int getCardBackgroundColorId(ProjectSound sound) {
        int cardBackgroundColorId;

        switch (sound.getCategory()) {
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

        return cardBackgroundColorId;
    }
}
