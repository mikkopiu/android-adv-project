package fi.metropolia.yellow_spaceship.androidadvproject.sounds;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * Class for handling sound recording.
 */
public class SoundRecorder {

    public static final int SAMPLE_RATE = 44100;
    public static final int CHANNELS = 1;

    private File mFile;
    private File mTempFile;
    private boolean mIsRecording = false;

    private Thread mBackgroundThread;

    /**
     * Constructor
     *
     * @param file File to write into
     */
    public SoundRecorder(File file) {

        mFile = file;
        mTempFile = new File(file.getParentFile().getAbsolutePath() + "/untitled-recording.raw");

    }

    /**
     * Set the file to write into
     *
     * @param file New file
     */
    public void setFile(File file) {

        mFile = file;
        mTempFile = new File(file.getParentFile().getAbsolutePath() + "/untitled-recording.raw");

    }

    /**
     * Start recording a new sound (and interrupt previous threads)
     */
    public void startRecording() {

        mIsRecording = true;
        if (mBackgroundThread != null) {
            mBackgroundThread.interrupt();
            mBackgroundThread = null;
        }
        mBackgroundThread = new Thread(new Recorder());
        mBackgroundThread.start();

    }

    /**
     * Stop the recording
     */
    public void stopRecording() {

        mIsRecording = false;

    }

    /**
     * Runnable for recording sound and saving it into a local file
     */
    class Recorder implements Runnable {

        /**
         * Writes WAV-headers to saved sound
         */
        private void writeFileWithHeaders() {

            try {

                long subChunk1Size = 16;
                int bitsPerSample = 16;
                int format = 1;
                long channels = (long) CHANNELS;
                long sampleRate = (long) SAMPLE_RATE;
                long byteRate = sampleRate * channels * bitsPerSample / 8;
                int blockAlign = (int) (channels * bitsPerSample / 8);

                DataInputStream dis = new DataInputStream((new BufferedInputStream(new FileInputStream(mTempFile))));

                long dataSize = mTempFile.length();
                long chunk2Size = dataSize * channels * bitsPerSample / 8;
                long chunkSize = 36 + chunk2Size;

                DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(mFile)));

                dos.writeBytes("RIFF");                                         // 00 - RIFF
                dos.write(intToByteArray((int) chunkSize), 0, 4);               // 04 - how big is the rest of this file?
                dos.writeBytes("WAVE");                                         // 08 - WAVE
                dos.writeBytes("fmt ");                                         // 12 - fmt
                dos.write(intToByteArray((int) subChunk1Size), 0, 4);           // 16 - size of this chunk
                dos.write(shortToByteArray((short) format), 0, 2);              // 20 - what is the audio format? 1 for PCM
                dos.write(shortToByteArray((short) channels), 0, 2);            // 22 - mono or stereo? 1 or 2?
                dos.write(intToByteArray((int) sampleRate), 0, 4);              // 24 - samples per second
                dos.write(intToByteArray((int) byteRate), 0, 4);                // 28 - bytes per second
                dos.write(shortToByteArray((short) blockAlign), 0, 2);          // 32 - # of bytes in one sample for all channels
                dos.write(shortToByteArray((short) bitsPerSample), 0, 2);       // 34 - how many bits in sample? Usually 16 or 24
                dos.writeBytes("data");                                         // 36 - data
                dos.write(intToByteArray((int) dataSize), 0, 4);                 // 40 - how big is the data chunk

                // Write the actual sound data
                byte[] dataBuffer = new byte[1024];

                while (dis.available() > 0) {
                    dis.read(dataBuffer);
                    dos.write(dataBuffer, 0, dataBuffer.length);
                }

                dos.flush();
                dos.close();
                dis.close();


            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        private byte[] intToByteArray(int i) {

            byte[] b = new byte[4];
            b[0] = (byte) (i & 0x00FF);
            b[1] = (byte) ((i >> 8) & 0x000000FF);
            b[2] = (byte) ((i >> 16) & 0x000000FF);
            b[3] = (byte) ((i >> 24) & 0x000000FF);

            return b;

        }

        private byte[] shortToByteArray(short s) {

            return new byte[]{(byte) (s & 0xff), (byte) ((s >>> 8) & 0xff)};

        }

        @Override
        public void run() {

            int minBufferSize = AudioRecord.getMinBufferSize(
                    SAMPLE_RATE,
                    AudioFormat.CHANNEL_IN_MONO,
                    AudioFormat.ENCODING_PCM_16BIT
            );
            AudioRecord audioRecord = new AudioRecord(
                    MediaRecorder.AudioSource.MIC,
                    SAMPLE_RATE,
                    AudioFormat.CHANNEL_IN_MONO,
                    AudioFormat.ENCODING_PCM_16BIT,
                    minBufferSize
            );
            int minBufferByteSize = minBufferSize * 2;
            byte[] recBuffer = new byte[minBufferByteSize];
            int frameByteSize = minBufferSize / 2;
            int sampleBytes = frameByteSize;
            int recBufferBytePtr = 0;

            try {

                if (mTempFile.exists()) {
                    mTempFile.delete();
                }

                if (mFile.exists()) {
                    mFile.delete();
                }

                DataOutputStream dos = new DataOutputStream(
                        new BufferedOutputStream(new FileOutputStream(mTempFile))
                );

                audioRecord.startRecording();

                while (mIsRecording) {

                    int reallySampledBytes = audioRecord.read(
                            recBuffer,
                            recBufferBytePtr,
                            sampleBytes
                    );

                    // Uncomment block if you want to do some manipulation on samples.
                    /*
                    int i = 0;
                    while (i < reallySampledBytes) {

                        float sample = (float) (recBuffer[recBufferBytePtr + i] & 0xFF
                                | recBuffer[recBufferBytePtr + i + 1] << 8);

                        // Create sample manipulations here

                        // Avoid 16-bit integer overflow when writing back the manipulated data
                        if (sample >= 32767f) {
                            recBuffer[recBufferBytePtr + i] = (byte) 0xFF;
                            recBuffer[recBufferBytePtr + i + 1] = 0x7F;
                        } else if (sample <= -32768f) {
                            recBuffer[recBufferBytePtr + i] = 0x00;
                            recBuffer[recBufferBytePtr + i + 1] = (byte) 0x80;
                        } else {
                            int s = (int) (0.5f + sample);
                            recBuffer[recBufferBytePtr + i] = (byte) (s & 0xFF);
                            recBuffer[recBufferBytePtr + i + 1] = (byte) (s >> 8 & 0xFF);
                        }
                        i += 2;

                    }
                    */

                    // Write the buffer to file
                    dos.write(recBuffer, recBufferBytePtr, reallySampledBytes);

                    // Move the recording pointer to the next position in the recording buffer
                    recBufferBytePtr += reallySampledBytes;

                    if (recBufferBytePtr >= minBufferByteSize) {
                        recBufferBytePtr = 0;
                        sampleBytes = frameByteSize;
                    } else {
                        sampleBytes = minBufferByteSize - recBufferBytePtr;
                        if (sampleBytes > frameByteSize) {
                            sampleBytes = frameByteSize;
                        }
                    }

                }

                audioRecord.stop();
                audioRecord.release();

                // Close output stream
                dos.close();

                // Write WAV-headers to file
                writeFileWithHeaders();

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

}
