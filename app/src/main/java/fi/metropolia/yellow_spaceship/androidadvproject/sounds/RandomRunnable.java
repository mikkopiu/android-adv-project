package fi.metropolia.yellow_spaceship.androidadvproject.sounds;

public class RandomRunnable implements Runnable {

    private int mIndex;
    private short mNextPlayback;
    private final SoundPlayer mSoundPlayer;

    public void setNextPlayback(short np) {
        mNextPlayback = np;
    }

    public short getNextPlayback() {
        return mNextPlayback;
    }

    public void setIndex(int index) {
        mIndex = index;
    }

    public int getIndex() {
        return mIndex;
    }

    public RandomRunnable(int index, SoundPlayer player, short nextPlayback) {
        mIndex = index;
        mSoundPlayer = player;
        mNextPlayback = nextPlayback;
    }

    @Override
    public void run() {
        mSoundPlayer.play(mIndex);
    }

}
