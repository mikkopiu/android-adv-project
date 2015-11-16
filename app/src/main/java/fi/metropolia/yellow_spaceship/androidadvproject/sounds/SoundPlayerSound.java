package fi.metropolia.yellow_spaceship.androidadvproject.sounds;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Build;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;

/**
 * Handles sound playing for a specific sound.
 */
public class SoundPlayerSound {

    private int mSampleRate;
    private int mChannels;
    private int mBits;
    private File mFile;
    private Thread mTrackThread;
    private float mVolume = 1.0f;
    private boolean mPlaying = false;
    private boolean mLooping = false;
    private AudioTrack mAudioTrack;

    public SoundPlayerSound() {

    }

    /**
     * Set sample rate
     *
     * @param sampleRate Sample rate as integer value e.g 44100
     */
    public void setSampleRate(int sampleRate) {
        mSampleRate = sampleRate;
    }

    /**
     * Get sample rate
     *
     * @return Sample rate as integer.
     */
    public int getSampleRate() {
        return mSampleRate;
    }

    /**
     * Set number of channels (mono = 1, stereo = 2)
     *
     * @param channels Number of channels
     */
    public void setChannels(int channels) {
        mChannels = channels;
    }

    /**
     * Get channel informatio for Audio Track
     *
     * @return Channel information for Audio Track
     */
    public int getChannels() {
        if (mChannels == 1)
            return AudioFormat.CHANNEL_OUT_MONO;
        else
            return AudioFormat.CHANNEL_OUT_STEREO;
    }

    /**
     * Set bit depth
     *
     * @param bits Bit depth
     */
    public void setBits(int bits) {
        mBits = bits;
    }

    /**
     * Get encoding information for Audio Track
     *
     * @return Encoding information for Audio Track
     */
    public int getBits() {
        if (mBits == 8)
            return AudioFormat.ENCODING_PCM_8BIT;
        else
            return AudioFormat.ENCODING_PCM_16BIT;
    }

    /**
     * Set File
     *
     * @param file File handle for audio file.
     */
    public void setFile(File file) {
        mFile = file;
    }

    /**
     * Get file
     *
     * @return file handle for audio file.
     */
    public File getFile() {
        return mFile;
    }

    /**
     * Set looping status
     *
     * @param looping Set looping to true or false
     */
    public void setLooping(boolean looping) {
        mLooping = looping;
    }

    /**
     * Get looping status
     *
     * @return Is the sound looping as boolean
     */
    public boolean getLooping() {
        return mLooping;
    }

    /**
     * Start playing the sound
     */
    public void play() {
        if (!mPlaying) {
            mPlaying = true;
            mTrackThread = new Thread(new TrackRunnable());
            mTrackThread.start();
        }
    }

    /**
     * Stop playing the sound.
     */
    public void stop() {
        mPlaying = false;
        mTrackThread.interrupt();
        mTrackThread = null;
    }

    /**
     * Set the sound level of the sound.
     *
     * @param volume Volume as float (0f - 1.0f, other values get clamped)
     */
    public void setVolume(float volume) {
        mVolume = volume;
        if (Build.VERSION.SDK_INT < 21) {
            mAudioTrack.setStereoVolume(mVolume, mVolume);
        } else {
            mAudioTrack.setVolume(mVolume);
        }
    }

    /**
     * Cleans up the SoundPlayerSound.
     */
    public void clear() {
        mAudioTrack.stop();
        mAudioTrack.release();
        mAudioTrack = null;
        mTrackThread.interrupt();
        mTrackThread = null;
    }

    /**
     * Audio Track in a thread. Sound playing implementation.
     */
    class TrackRunnable implements Runnable {

        @Override
        public void run() {

            // Get the minimum buffer size.
            int minBufferSize = AudioTrack.getMinBufferSize(mSampleRate, getChannels(), getBits());
            // Create the Audio Track with proper settings.
            mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, mSampleRate, getChannels(), getBits(), minBufferSize, AudioTrack.MODE_STREAM);

            int i;
            // Byte buffer for streaming data into Audio Track.
            byte[] buffer = new byte[minBufferSize];

            // Set the volume
            if (Build.VERSION.SDK_INT < 21) {
                mAudioTrack.setStereoVolume(mVolume, mVolume);
            } else {
                mAudioTrack.setVolume(mVolume);
            }

            mAudioTrack.play();

            try {

                BufferedInputStream bis = new BufferedInputStream(new FileInputStream(mFile));
                // Skip header
                bis.skip(44);

                while (mPlaying) {

                    i = bis.read(buffer, 0, minBufferSize);
                    if (i == -1) {

                        if (mLooping) {
                            // Start in the beginning of the file if we are looping.
                            bis = new BufferedInputStream(new FileInputStream(mFile));
                            bis.skip(44);
                        } else {
                            // Stop playing if we are not looping
                            mPlaying = false;

                            // Tell RandomEngine we are finished
                        }

                    }

                    // Write to the Audio Tracks buffer
                    mAudioTrack.write(buffer, 0, i);
                }

                // Clean up.
                mAudioTrack.stop();
                mAudioTrack.release();
                bis.close();

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

}
