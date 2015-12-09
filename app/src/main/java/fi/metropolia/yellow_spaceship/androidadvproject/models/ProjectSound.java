package fi.metropolia.yellow_spaceship.androidadvproject.models;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;

import fi.metropolia.yellow_spaceship.androidadvproject.sounds.RandomRunnable;
import fi.metropolia.yellow_spaceship.androidadvproject.sounds.SoundFinishedListener;
import fi.metropolia.yellow_spaceship.androidadvproject.sounds.SoundPlayer;

/**
 * A simplified representation of a DAMSound in a SoundScapeProject.
 * Can be serialized using GSON to JSON format.
 */
public class ProjectSound implements Parcelable {
    private String id;
    private String title;
    private SoundCategory category;
    private SoundType soundType;
    private String fileName;
    private boolean isOnLoop;
    private boolean isRandom;
    private float mVolume;

    private transient AudioTrack mAudioTrack;
    private transient boolean isPlaying = false;
    private transient int mChannels;
    private transient int mBits;
    private transient int mSampleRate;
    private transient File mFile;
    private transient Thread mTrackThread;
    private transient RandomRunnable mRandomRunnable;
    private transient SoundFinishedListener soundFinishedListener;

    /**
     * Full constructor
     *
     * @param id        ID
     * @param title     Human readable title
     * @param category  SoundCategory
     * @param soundType SoundType
     * @param fileName  Local filename
     * @param isOnLoop  Is sound playing on loop
     * @param isRandom  Is sound playing randomly
     */
    public ProjectSound(String id, String title, SoundCategory category, SoundType soundType,
                        String fileName, boolean isOnLoop, boolean isRandom, float volume) {
        this.id = id;
        this.title = title;
        this.category = category;
        this.soundType = soundType;
        this.fileName = fileName;
        this.isOnLoop = isOnLoop;
        this.isRandom = isRandom;
        this.mVolume = volume;

    }

    private ProjectSound(Parcel in) {
        this(
                in.readString(),
                in.readString(),
                SoundCategory.fromApi(in.readString()),
                SoundType.fromApi(in.readString()),
                in.readString(),
                in.readByte() == 1,
                in.readByte() == 1,
                in.readFloat()
        );
    }

    public void setSoundFinishedListener(SoundFinishedListener sfl) {
        this.soundFinishedListener = sfl;
    }

    public void setRandomRunnable(RandomRunnable randomRunnable) {
        mRandomRunnable = randomRunnable;
    }

    public RandomRunnable getRandomRunnable() {
        return mRandomRunnable;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public SoundCategory getCategory() {
        return category;
    }

    public void setCategory(SoundCategory category) {
        this.category = category;
    }

    public SoundType getSoundType() {
        return soundType;
    }

    public void setSoundType(SoundType soundType) {
        this.soundType = soundType;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public boolean getIsOnLoop() {
        return isOnLoop;
    }

    public void setIsOnLoop(boolean isOnLoop) {
        this.isOnLoop = isOnLoop;
    }

    public boolean getIsRandom() {
        return isRandom;
    }

    public void setIsRandom(boolean isRandom) {
        this.isRandom = isRandom;
    }

    public float getVolume() {
        return mVolume;
    }

    /**
     * Set sample rate
     *
     * @param sampleRate Sample rate as integer value e.g 44100
     */
    public void setSampleRate(int sampleRate) {
        if (sampleRate != 44100)
            sampleRate = 44100;
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
     * Get channel information for Audio Track
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
        isOnLoop = looping;
    }

    /**
     * Get looping status
     *
     * @return Is the sound looping as boolean
     */
    public boolean getLooping() {
        return isOnLoop;
    }

    /**
     * Start playing the sound
     */
    public void play() {
        if (!isPlaying) {
            isPlaying = true;

            mTrackThread = new Thread(new TrackRunnable());
            mTrackThread.start();
        }
    }

    /**
     * Stop playing the sound.
     */
    public void stop() {
        isPlaying = false;
        if (mAudioTrack != null && mAudioTrack.getState() != AudioTrack.STATE_UNINITIALIZED) {
            mAudioTrack.stop();
        }
        if (mTrackThread != null) {
            try {
                mTrackThread.interrupt();
                mTrackThread.join();
                mTrackThread = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Set the sound level of the sound.
     *
     * @param volume Volume as float (0f - 1.0f, other values get clamped)
     */
    public void setVolume(float volume) {
        if (volume > 1.0f)
            mVolume = 1.0f;
        else if (volume < 0.0f)
            mVolume = 0.0f;
        else
            mVolume = volume;

        // mAudioTrack is null until the playback actually starts
        if (isPlaying) {
            if (Build.VERSION.SDK_INT < 21) {
                mAudioTrack.setStereoVolume(mVolume, mVolume);
            } else {
                mAudioTrack.setVolume(mVolume);
            }
        }
    }

    /**
     * Cleans up the ProjectSound
     */
    public void clear() {
        try {
            if (mAudioTrack != null) {
                if (mAudioTrack.getState() != AudioTrack.STATE_UNINITIALIZED) {
                    mAudioTrack.stop();
                }
                mAudioTrack.release();
                mAudioTrack = null;
            }
            if (mTrackThread != null) {
                mTrackThread.interrupt();
                mTrackThread.join();
                mTrackThread = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Audio Track in a thread. Sound playing implementation.
     */
    private final class TrackRunnable implements Runnable {

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
            setVolume(mVolume);

            mAudioTrack.play();

            try {

                BufferedInputStream bis = new BufferedInputStream(new FileInputStream(mFile));
                // Skip header
                bis.skip(SoundPlayer.HEADER_SIZE);

                while (isPlaying) {

                    i = bis.read(buffer, 0, minBufferSize);
                    if (i == -1) {

                        if (isOnLoop) {
                            // Start in the beginning of the file if we are looping.
                            bis.close();
                            bis = new BufferedInputStream(new FileInputStream(mFile));
                            bis.skip(SoundPlayer.HEADER_SIZE);
                        } else {
                            // Stop playing if we are not looping
                            isPlaying = false;
                            // Tell SoundPlayer we are finished
                            soundFinishedListener.soundIsFinished(ProjectSound.this);
                        }

                    }

                    // Write to the Audio Tracks buffer
                    mAudioTrack.write(buffer, 0, i);
                }

                // Clean up.
                if (mAudioTrack != null && mAudioTrack.getState() != AudioTrack.STATE_UNINITIALIZED) {
                    try {
                        mAudioTrack.stop();
                    } catch (IllegalStateException e) {
                        e.printStackTrace();
                    }
                }

                mAudioTrack.release();
                bis.close();

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

    /**
     * Parcelable implementation
     */

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.getId());
        dest.writeString(this.getTitle());
        dest.writeString(this.getCategory().toString());
        dest.writeString(this.getSoundType().toString());
        dest.writeString(this.getFileName());
        dest.writeByte((byte) (this.getIsOnLoop() ? 1 : 0));
        dest.writeByte((byte) (this.getIsRandom() ? 1 : 0));
        dest.writeFloat(this.getVolume());
    }

    public static final Parcelable.Creator<ProjectSound> CREATOR
            = new Parcelable.Creator<ProjectSound>() {

        public ProjectSound createFromParcel(Parcel in) {
            return new ProjectSound(in);
        }

        public ProjectSound[] newArray(int size) {
            return new ProjectSound[size];
        }
    };

}
