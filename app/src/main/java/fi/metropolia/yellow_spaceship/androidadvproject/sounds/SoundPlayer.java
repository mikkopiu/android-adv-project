package fi.metropolia.yellow_spaceship.androidadvproject.sounds;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

/**
 * SoundPlayer
 */
public class SoundPlayer {

    private Context mContext;
    private ArrayList<SoundPlayerSound> mSounds;
    private AudioTrack mAudioTrack;

    private boolean mIsPlaying = false;

    private final static int HEADER_SIZE = 44;

    public SoundPlayer(Context context) {

        mContext = context;
        mSounds = new ArrayList<SoundPlayerSound>();

    }

    /**
     * Add a new sound to Sound Player
     * @param soundPlayerSound SoundPlayerSound object
     */
    public void addSound(SoundPlayerSound soundPlayerSound) {

        ByteBuffer buffer = ByteBuffer.allocate(HEADER_SIZE);
        buffer.order(ByteOrder.LITTLE_ENDIAN);

        int rate;
        int format;
        int channels;
        int bits;

        // TODO: Header reading should probably be done in another thread.

        try {

            FileInputStream fis = new FileInputStream(soundPlayerSound.getFile());
            fis.read(buffer.array(), buffer.arrayOffset(), buffer.capacity());
            buffer.rewind();
            buffer.position(buffer.position() + 20);
            format = buffer.getShort();
            channels = buffer.getShort();
            rate = buffer.getInt();
            buffer.position(buffer.position() + 6);
            bits = buffer.getShort();

            soundPlayerSound.setSampleRate(rate);
            soundPlayerSound.setChannels(channels);
            soundPlayerSound.setBits(bits);

            mSounds.add(soundPlayerSound);

        } catch(Exception e) {
            e.printStackTrace();
        }

    }

    public void removeSound(File soundFile) {
        //mSounds.remove(soundFile);
    }

    /**
     * Plays all sounds
     */
    public void playAll() {

        for(int i = 0; i < mSounds.size(); i++) {
            mSounds.get(i).play();
        }

    }

    /**
     * Stops all sounds.
     */
    public void stopAll() {

        for(int i = 0; i < mSounds.size(); i++) {
            mSounds.get(i).stop();
        }

    }

    /**
     * Set volume of a specific sound.
     * @param sps SoundPlayerSound object, which sounds volume you want to change.
     * @param volume volume as floating point (0f - 1.0f, other values get clamped)
     */
    public void setVolume(SoundPlayerSound sps, float volume) {
        mSounds.get(mSounds.indexOf(sps)).setVolume(volume);
    }

    /**
     * Stop a specific sound from playing.
     * @param sps SoundPlayerSound object, which sound to stop.
     */
    public void stop(SoundPlayerSound sps) {
        mSounds.get(mSounds.indexOf(sps)).stop();
    }

    /**
     * Clean up Sound Player
     */
    public void clear() {

        for(int i = 0; i < mSounds.size(); i++) {
            mSounds.get(i).clear();
        }

        mSounds.clear();
        mSounds = null;

    }

    // This runnable is just for testing. Combining multiple buffers into one and playing in one Audio Track.
    // Leaving the code here for now.
    class TestPlay implements Runnable {

        @Override
        public void run() {

            File soundFile = new File(mContext.getFilesDir().getAbsolutePath() + "/sounds/11rainforest1441186990000.wav");
            File soundFile2 = new File(mContext.getFilesDir().getAbsolutePath() + "/sounds/11pod1445691558905.wav");
            File test = new File(mContext.getFilesDir().getAbsolutePath() + "/sounds/11island1441187623000.wav");

            int minBufferSize = AudioTrack.getMinBufferSize(44100, AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_16BIT);
            mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, 44100, AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_16BIT, minBufferSize, AudioTrack.MODE_STREAM);

            int minBufferByteSize = minBufferSize * 1;
            byte[] monoData = new byte[minBufferByteSize];
            byte[] audioData = new byte[minBufferByteSize * 2];
            int frameByteSize = minBufferSize / 2;
            int sampleBytes = frameByteSize;
            int audioBytePtr = 0;

            try {

                DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(soundFile)));
                //DataInputStream dis2 = new DataInputStream(new BufferedInputStream(new FileInputStream(soundFile2)));

                mAudioTrack.play();
                while(dis.available() > 0) {

                    int reallySampledBytes = dis.read(monoData, audioBytePtr, sampleBytes);
                    //int reallySampledBytes2 = dis2.read(audioData2, audioBytePtr, sampleBytes);

                    int i = 0;
                    while(dis.available() > 0 && i < reallySampledBytes) {

                        float sample = (float) (monoData[audioBytePtr + i] & 0xFF
                                | monoData[audioBytePtr + i + 1] << 8);

                        //float sample2 = (float) (audioData2[audioBytePtr + i] & 0xFF
                        //        | audioData2[audioBytePtr + i + 1] << 8);

                        // Combining buffers
                        //sample += sample2;

                        // Volume
                        sample *= 2;

                        // Silent
                        //sample = 0;

                        // Avoid 16-bit integer overflow when writing back the manipulated data
                        if (sample >= 32767f) {
                            monoData[audioBytePtr + i] = (byte) 0xFF;
                            monoData[audioBytePtr + i + 1] = 0x7F;
                        } else if (sample <= -32768f) {
                            monoData[audioBytePtr + i] = 0x00;
                            monoData[audioBytePtr + i + 1] = (byte) 0x80;
                        } else {
                            int s = (int) (0.5f + sample);
                            monoData[audioBytePtr + i] = (byte) (s & 0xFF);
                            monoData[audioBytePtr + i + 1] = (byte) (s >> 8 & 0xFF);
                        }
                        i += 2;
                    }

                    // Mono to stereo
                    for (int j = 0; j < reallySampledBytes; j += 2) {
                        audioData[j*2+0] = monoData[j];
                        audioData[j*2+1] = monoData[j+1];
                        audioData[j*2+2] = monoData[j];
                        audioData[j*2+3] = monoData[j+1];
                    }

                    // Write the buffer to Audio Track
                    //mAudioTrack.write(audioData, audioBytePtr, reallySampledBytes);
                    mAudioTrack.write(monoData, audioBytePtr, audioData.length);

                    // Move the recording pointer to the next position in the recording buffer
                    audioBytePtr += reallySampledBytes;

                    if (audioBytePtr >= minBufferByteSize) {
                        audioBytePtr = 0;
                        sampleBytes = frameByteSize;
                    } else {
                        sampleBytes = minBufferByteSize - audioBytePtr;
                        if (sampleBytes > frameByteSize) {
                            sampleBytes = frameByteSize;
                        }
                    }
                }
                mAudioTrack.flush();
                mAudioTrack.stop();
                mAudioTrack.release();
                dis.close();

            } catch(Exception e) {
                e.printStackTrace();
            }

        }

    }

}
