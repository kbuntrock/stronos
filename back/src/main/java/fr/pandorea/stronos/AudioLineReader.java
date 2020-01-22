package fr.pandorea.stronos;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public enum AudioLineReader {
  INSTANCE;

  public static AudioLineReader get() {
    return INSTANCE;
  }

  public static final int BUFFER_SIZE = 2048;

  public static final float SAMPLE_RATE = 44100;
  public static final int SAMPLE_SIZE_IN_BITS = 16;
  // 1 = mono, 2 = stéréo
  public static final int NB_CHANNELS = 2;

  private static AudioFormat compressionFormat =
      new AudioFormat(SAMPLE_RATE, SAMPLE_SIZE_IN_BITS, NB_CHANNELS, true, false);

  private static final Logger logger = LogManager.getLogger(AudioLineReader.class);

  private final AtomicBoolean reading = new AtomicBoolean(false);

  private TargetDataLine inputLine;

  private final List<Mp3Stream> streams = new ArrayList<>();

  private final boolean playSoundLocally = false;
  private SourceDataLine outputLine = null;

  private float volume = 1.0f;

  private AudioLineReader() {
    // Nothing to do
  }

  public synchronized boolean read() {

    if (!reading.get()) {
      try {
        DataLine.Info info = getSupportedDataLineInfo();
        if (info != null) {

          Mp3Encoder mp3Encoder = new Mp3Encoder(compressionFormat);

          inputLine = (TargetDataLine) AudioSystem.getLine(info);

          inputLine.open(compressionFormat);
          inputLine.start();
          reading.set(true);

          // Play sound on PC
          if (playSoundLocally) {
            outputLine = AudioSystem.getSourceDataLine(compressionFormat);
            outputLine.open(compressionFormat);
            outputLine.start();
          }
          //

          AudioInputStream inputStream = new AudioInputStream(inputLine);

          logger.info("Input format : {}", inputStream.getFormat());
          logger.info("Audio line read started");

          int nBytesRead = 0;
          byte[] abData = new byte[BUFFER_SIZE];

          while (nBytesRead != -1 && reading.get()) {
            nBytesRead = inputStream.read(abData, 0, abData.length);
            if (nBytesRead >= 0) {
              abData = VolumeControler.adjustVolume(abData, volume);
              var mp3 = mp3Encoder.encodePcmToMp3(abData);
              writeToStream(mp3);
              // outputLine.write(abData, 0, nBytesRead);
              // logger.info("output " + nBytesRead);
            }
          }
        }

        return true;
      } catch (LineUnavailableException | IOException e) {
        logger.error("Cannot read data", e);
        return false;
      }

    }
    logger.error("Already reading audio line");
    return false;
  }

  private void writeToStream(byte[] mp3) {
    for (Mp3Stream stream : streams) {
      stream.put(mp3);
    }
  }

  public void subscribe(Mp3Stream stream) {
    streams.add(stream);
  }

  public void closeStreams() {
    streams.forEach(s -> s.close());
  }

  public void removeStream(Object stream) {
    streams.remove(stream);
  }

  public void stop() {

    if (reading.get()) {

      logger.info("Stopping reading audio line.");

      inputLine.stop();
      inputLine.close();
      inputLine = null;

      if (playSoundLocally) {
        outputLine.stop();
        outputLine.close();
        outputLine = null;
      }

      streams.clear();

      reading.set(false);

    } else {
      logger.error("Audio line is not reading.");
    }
  }

  private DataLine.Info getSupportedDataLineInfo() {

    for (Mixer.Info mi : AudioSystem.getMixerInfo()) {
      logger.info("Available mixer : {}", mi.getName());
      Mixer targetMixer = AudioSystem.getMixer(mi);
      try {
        targetMixer.open();
        // Check if it supports the desired format
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, compressionFormat);
        if (targetMixer.isLineSupported(info)) {
          logger.info("{} supports recording @ {}", mi.getName(), compressionFormat);
          return info;
        } else {
          targetMixer.close();
        }
      } catch (LineUnavailableException e) {
        logger.error("Error while finding a supported format.", e);
      }
    }
    logger.error("Recording not supported!!!");
    return null;

  }

  public boolean isReading() {
    return reading.get();
  }

  public List<Mp3Stream> getStreams() {
    return streams;
  }

  public float adjusVolume(float v) {
    volume = v;
    return volume;
  }

}
