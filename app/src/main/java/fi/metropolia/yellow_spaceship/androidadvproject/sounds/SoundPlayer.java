package fi.metropolia.yellow_spaceship.androidadvproject.sounds;

import android.os.Handler;

import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

import fi.metropolia.yellow_spaceship.androidadvproject.models.ProjectSound;

/**
 * SoundPlayer
 */
public class SoundPlayer implements SoundFinishedListener {

    private ArrayList<ProjectSound> mSounds;

    private boolean mIsPlaying = false;

    public final static int HEADER_SIZE = 44;
    // Minimum time in milliseconds before next playback.
    private final static short LOWER_LIMIT = 4000;
    // Maximum time in milliseconds before next playback.
    private final static short UPPER_LIMIT = 12000;

    private final Handler randomHandler = new Handler();

    public SoundPlayer() {
        mSounds = new ArrayList<>();
    }

    /**
     * Add multiple sounds to the Sound Player at once
     * @param projectSounds An array of ProjectSounds
     */
    public void addSounds(ProjectSound[] projectSounds) {
        for (ProjectSound p : projectSounds) {
            addSound(p);
        }
    }

    /**
     * Add a new sound to Sound Player
     *
     * @param projectSound ProjectSound object
     */
    public void addSound(ProjectSound projectSound) {

        ByteBuffer buffer = ByteBuffer.allocate(HEADER_SIZE);
        buffer.order(ByteOrder.LITTLE_ENDIAN);

        int rate;
        int format;
        int channels;
        int bits;

        // TODO: Header reading should probably be done in another thread.

        try {

            FileInputStream fis = new FileInputStream(projectSound.getFile());
            fis.read(buffer.array(), buffer.arrayOffset(), buffer.capacity());
            buffer.rewind();
            buffer.position(buffer.position() + 20);
            format = buffer.getShort();
            channels = buffer.getShort();
            rate = buffer.getInt();
            buffer.position(buffer.position() + 6);
            bits = buffer.getShort();

            projectSound.setSampleRate(rate);
            projectSound.setChannels(channels);
            projectSound.setBits(bits);

            mSounds.add(projectSound);

            int index = mSounds.size() - 1;
            projectSound.setRandomRunnable(new RandomRunnable(index, this, generateNextPlayback()));
            projectSound.setSoundFinishedListener(this);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void changeToLoop(int index) {
        ProjectSound sound = mSounds.get(index);
        if(!sound.getIsOnLoop()) {
            sound.setIsOnLoop(true);
            sound.setIsRandom(false);
            randomHandler.removeCallbacks(sound.getRandomRunnable());
            if(mIsPlaying) {
                sound.stop();
                sound.play();
            }
        }
    }

    public void changeToRandom(int index) {
        ProjectSound sound = mSounds.get(index);
        if(!sound.getIsRandom()) {
            sound.setIsOnLoop(false);
            sound.setIsRandom(true);
            sound.getRandomRunnable().setNextPlayback(generateNextPlayback());
            if(mIsPlaying) {
                sound.stop();
                randomHandler.postDelayed(sound.getRandomRunnable(), sound.getRandomRunnable().getNextPlayback());
            }
        }
    }

    public void removeSound(int index) {
        ProjectSound sound = mSounds.get(index);
        sound.stop();
        sound.clear();
        randomHandler.removeCallbacks(sound.getRandomRunnable());
        mSounds.remove(index);

        for(ProjectSound ps : mSounds) {
            RandomRunnable rr = ps.getRandomRunnable();
            if(rr.getIndex() > index) {
                rr.setIndex(rr.getIndex() - 1);
            }
        }
    }

    private short generateNextPlayback() {

        return (short)(LOWER_LIMIT + (Math.random() * ((UPPER_LIMIT - LOWER_LIMIT) + 1)));

    }

    @Override
    public void soundIsFinished(ProjectSound sound) {
        int index = mSounds.indexOf(sound);
        if(index != -1) {
            ProjectSound projectSound = mSounds.get(index);
            if(projectSound.getIsRandom()) {
                projectSound.getRandomRunnable().setNextPlayback(generateNextPlayback());
                randomHandler.postDelayed(projectSound.getRandomRunnable(), projectSound.getRandomRunnable().getNextPlayback());
            }
        }
    }

    /**
     * Plays all sounds
     */
    public void playAll() {

        mIsPlaying = true;

        // Start playing the first sound immediately if we have only randomly played sounds.
        boolean allRandom = true;
        for(ProjectSound ps : mSounds) {
            if(ps.getIsOnLoop()) {
                allRandom = false;
                break;
            }
        }

        for (int i = 0; i < mSounds.size(); i++) {
            ProjectSound sound = mSounds.get(i);
            if(sound.getIsOnLoop()) {
                sound.play();
            } else if(sound.getIsRandom()) {
                if(allRandom) {
                    if(i == 0) {
                        sound.play();
                    }
                } else {
                    sound.getRandomRunnable().setNextPlayback(generateNextPlayback());
                    randomHandler.postDelayed(sound.getRandomRunnable(), sound.getRandomRunnable().getNextPlayback());
                }
            }
        }

    }

    /**
     * Plays a single sound.
     */
    public void play(int index) {
        ProjectSound sound = mSounds.get(index);
        sound.play();
    }

    /**
     * Stops all sounds.
     */
    public void stopAll() {

        mIsPlaying = false;

        for (int i = 0; i < mSounds.size(); i++) {
            ProjectSound sound = mSounds.get(i);
            sound.stop();
            randomHandler.removeCallbacks(sound.getRandomRunnable());

        }

    }

    /**
     * Set volume of a specific sound.
     *
     * @param sps    ProjectSound object, which sounds volume you want to change.
     * @param volume volume as floating point (0f - 1.0f, other values get clamped)
     */
    public void setVolume(ProjectSound sps, float volume) {
        mSounds.get(mSounds.indexOf(sps)).setVolume(volume);
    }

    /**
     * Set volume of a specific sound at index n.
     *
     * @param index  Index of the sound
     * @param volume volume as floating point (0f - 1.0f, other values get clamped)
     */
    public void setVolume(int index, float volume) {
        mSounds.get(index).setVolume(volume);
    }

    /**
     * Stop a specific sound from playing.
     *
     * @param sps ProjectSound object, which sound to stop.
     */
    public void stop(ProjectSound sps) {
        mSounds.get(mSounds.indexOf(sps)).stop();
    }

    public void stop(int index) {
        mSounds.get(index).stop();
    }

    /**
     * Clean up Sound Player
     */
    public void clear() {

        for (int i = 0; i < mSounds.size(); i++) {
            ProjectSound sound = mSounds.get(i);
            sound.stop();
            sound.clear();
        }

        mSounds.clear();
        mSounds = null;

    }
}
