package fi.metropolia.yellow_spaceship.androidadvproject.sounds;

import android.os.Handler;

import java.util.ArrayList;
import java.util.Random;

import fi.metropolia.yellow_spaceship.androidadvproject.models.ProjectSound;

/**
 * Handles the random playback of sounds.
 */
public class RandomEngine {

    private SoundPlayer mSoundPlayer;
    private ArrayList<RandomRunnable> mRandomList;
    private boolean mIsRunning = false;

    // Minimum time in milliseconds before next playback.
    private final static short LOWER_LIMIT = 4000;
    // Maximum time in milliseconds before next playback.
    private final static short UPPER_LIMIT = 12000;

    private final Handler randomHandler = new Handler();

    public RandomEngine(SoundPlayer player) {
        mSoundPlayer = player;
        mRandomList = new ArrayList<RandomRunnable>();
    }

    public void start() {
        mIsRunning = true;

        for(RandomRunnable rr : mRandomList) {
            rr.setNextPlayback(generateNextPlayback());
            randomHandler.postDelayed(rr, rr.nextPlayback);
        }

    }

    public void stop() {
        mIsRunning = false;

        for(RandomRunnable rr : mRandomList) {
            randomHandler.removeCallbacks(rr);
        }

    }

    public void addRandom(int index) {

        short nextPlayback = generateNextPlayback();
        mRandomList.add(new RandomRunnable(nextPlayback, index));
        if(mIsRunning) {
            int localIndex = getLocalIndex(index);
            //randomHandler.postDelayed(mRandomList.get(localIndex), nextPlayback);
        }

    }

    public void removeRandom(int index) {

        int localIndex = getLocalIndex(index);

        if(localIndex != -1) {
            randomHandler.removeCallbacks(mRandomList.get(localIndex));
            mRandomList.remove(localIndex);

            mSoundPlayer.play(index);
        }
        /*
        if(index < mRandomList.size()) {
            randomHandler.removeCallbacks(mRandomList.get(index));
            mRandomList.remove(index);

            for (RandomRunnable rr : mRandomList) {
                if (rr.index > index) {
                    rr.index -= 1;
                }
            }

            mSoundPlayer.play(index);
        }
        */

    }

    public int getLocalIndex(int spIndex) {
        int position = 0;
        for(RandomRunnable rr : mRandomList) {
            if(rr.index == spIndex) {
                return position;
            }
            position++;
        }
        return -1;
    }

    private short generateNextPlayback() {

        return (short)(LOWER_LIMIT + (Math.random() * ((UPPER_LIMIT - LOWER_LIMIT) + 1)));

    }

    public void refresh(ProjectSound sound) {
        if(mIsRunning) {
            int index = mSoundPlayer.getSoundIndex(sound);

            int localIndex = getLocalIndex(index);
            if(localIndex != -1) {
                short nextPlayback = generateNextPlayback();
                randomHandler.postDelayed(mRandomList.get(localIndex), nextPlayback);
            }
        }
    }

    private class RandomRunnable implements Runnable {

        private int index;
        private short nextPlayback;

        public void setNextPlayback(short np) {
            nextPlayback = np;
        }

        public RandomRunnable(short nextPlayback, int index) {
            this.nextPlayback = nextPlayback;
            this.index = index;
        }

        @Override
        public void run() {
            mSoundPlayer.play(index);
        }
    }

}
