package fi.metropolia.yellow_spaceship.androidadvproject.sounds;

import android.content.Context;
import android.media.AudioTrack;

import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

import fi.metropolia.yellow_spaceship.androidadvproject.models.ProjectSound;

/**
 * SoundPlayer
 */
public class SoundPlayer {

    private final Context mContext;
    private ArrayList<ProjectSound> mSounds;
    private RandomEngine mRandomEngine;

    private boolean mIsPlaying = false;

    private final static int HEADER_SIZE = 44;

    public SoundPlayer(Context context) {

        mContext = context;
        mSounds = new ArrayList<>();
        mRandomEngine = new RandomEngine(this);

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

            if(!projectSound.getIsOnLoop()) {
                mRandomEngine.addRandom(mSounds.indexOf(projectSound));
                projectSound.setRandomEngine(mRandomEngine);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void removeSound(int index) {
        stop(index);
        mRandomEngine.removeRandom(index);
        mSounds.remove(index);
    }

    public int getSoundIndex(ProjectSound sound) {
        return mSounds.indexOf(sound);
    }

    /**
     * Plays all sounds
     */
    public void playAll() {

        for (int i = 0; i < mSounds.size(); i++) {
            ProjectSound sound = mSounds.get(i);
            if(sound.getIsOnLoop()) {
                // Start looping sounds.
                sound.play();
            }
        }

        mRandomEngine.start();

    }

    /**
     * Plays a single sound.
     */
    public void play(int index) {
        try {
            mSounds.get(index).play();
        } catch(IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
    }

    /**
     * Stops all sounds.
     */
    public void stopAll() {

        for (int i = 0; i < mSounds.size(); i++) {
            mSounds.get(i).stop();
        }

        mRandomEngine.stop();

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
            mSounds.get(i).clear();
        }

        mSounds.clear();
        mSounds = null;

    }
}
