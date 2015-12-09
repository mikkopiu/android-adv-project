package fi.metropolia.yellow_spaceship.androidadvproject.sounds;

/**
 * RandomRunnables are run in a handler to play sounds at random intervals.
 */
public class RandomRunnable implements Runnable {

    private int mIndex;
    private short mNextPlayback;
    private final SoundPlayer mSoundPlayer;

    /**
     * Constructor.
     *
     * @param index        Position in the data structure.
     * @param player       Reference to the SoundPlayer object.
     * @param nextPlayback Time until next playback in milliseconds.
     */
    public RandomRunnable(int index, SoundPlayer player, short nextPlayback) {
        mIndex = index;
        mSoundPlayer = player;
        mNextPlayback = nextPlayback;
    }

    /**
     * Sets the next playback time.
     *
     * @param np Time until next playback in milliseconds.
     */
    public void setNextPlayback(short np) {
        mNextPlayback = np;
    }

    /**
     * Get the next playback time.
     *
     * @return Time until next playback in milliseconds.
     */
    public short getNextPlayback() {
        return mNextPlayback;
    }

    /**
     * Set the position of the sound in the data structure.
     *
     * @param index Position of the sound in the data structure
     */
    public void setIndex(int index) {
        mIndex = index;
    }

    /**
     * Get the position of the sound in the data structure.
     *
     * @return index
     */
    public int getIndex() {
        return mIndex;
    }

    @Override
    public void run() {
        mSoundPlayer.play(mIndex);
    }

}
