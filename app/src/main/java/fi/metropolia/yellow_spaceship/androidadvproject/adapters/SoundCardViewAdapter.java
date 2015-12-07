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
 * RecyclerView.Adapter for CardViews for Sounds (e.g. in create-view)
 */
public class SoundCardViewAdapter extends RecyclerView.Adapter<SoundCardViewAdapter.ViewHolder> {

    private final ArrayList<ProjectSound> mDataSet;
    private final IProjectSoundViewHolderClicks listener;

    /**
     * ViewHolder for a sound in the create-view
     */
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            SeekBar.OnSeekBarChangeListener,
            SwitchCompat.OnCheckedChangeListener {

        private final IProjectSoundViewHolderClicks mListener;
        private int mPrevProgress;

        private final ImageButton mVolBtn;
        private final SeekBar mVolBar;
        private final CardView mCardView;
        private final SwitchCompat mRandomSwitch;
        private final TextView mTitleTv;

        public ViewHolder(final View itemView, IProjectSoundViewHolderClicks listener) {
            super(itemView);

            this.mListener = listener;

            // Find elements for click listeners & data binding
            this.mTitleTv = (TextView) itemView.findViewById(R.id.sound_title);
            this.mVolBar = (SeekBar) itemView.findViewById(R.id.volume_slider);
            this.mCardView = (CardView) itemView.findViewById(R.id.create_card_view);
            this.mRandomSwitch = (SwitchCompat) itemView.findViewById(R.id.randomize_switch);
            this.mVolBtn = (ImageButton) itemView.findViewById(R.id.volume_slider_btn);

            itemView.findViewById(R.id.close_btn).setOnClickListener(this);
            this.mVolBtn.setOnClickListener(this);
            this.mVolBar.setOnSeekBarChangeListener(this);
            this.mRandomSwitch.setOnCheckedChangeListener(this);

            this.mPrevProgress = 100;
        }

        /**
         * Bind a ProjectSound's data to this ViewHolder
         *
         * @param sound Sound to bind
         */
        public void bindSound(ProjectSound sound) {
            this.mTitleTv.setText(sound.getTitle());
            this.mTitleTv.setAllCaps(true); // XML value for capitalization is overridden when using setText
            this.mVolBar.setProgress((int) (sound.getVolume() * 100));
            this.mRandomSwitch.setChecked(sound.getIsRandom());

            // Get colours for view elements
            Context context = this.itemView.getContext();
            int color = ContextCompat.getColor(context, getCardBackgroundColorId(sound));
            int volBarColor = ContextCompat.getColor(context, R.color.volbar_white);

            // Update card's colour based on our colouring rules
            this.mCardView.setCardBackgroundColor(color);

            // Fix volume SeekBar's (and its thumb's) and Randomize-switch's colours
            this.mVolBar.getProgressDrawable().setColorFilter(volBarColor, PorterDuff.Mode.SRC_ATOP);
            this.mVolBar.getThumb().setColorFilter(volBarColor, PorterDuff.Mode.SRC_ATOP);
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.close_btn) {
                this.mListener.onCloseClick(getLayoutPosition());
            } else if (v.getId() == R.id.volume_slider_btn) {
                this.mVolBar.setProgress(this.mPrevProgress == 0 ? 100 : 0);
            }
        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            // Update icon when volume hits zero
            if (progress == 0) {
                this.mVolBtn.setImageResource(R.drawable.ic_volume_mute_white_24dp);
            } else if (this.mPrevProgress == 0) { // Prevent unnecessary reload of image resources
                this.mVolBtn.setImageResource(R.drawable.ic_volume_up_white_24dp);
            }
            this.mPrevProgress = progress;

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

    /**
     * Constructor
     *
     * @param dataSet  Data for this adapter
     * @param listener Click handler for ViewHolders
     */
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
