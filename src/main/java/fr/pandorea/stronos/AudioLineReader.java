package fr.pandorea.stronos;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.TargetDataLine;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public enum AudioLineReader {
  INSTANCE;

  public static AudioLineReader get() {
    return INSTANCE;
  }

  private static final float SAMPLE_RATE = 44100;
  private static final int SAMPLE_SIZE_IN_BITS = 16;
  // 1 = mono, 2 = stéréo
  private static final int NB_CHANNELS = 2;

  private static AudioFormat compressionFormat =
      new AudioFormat(SAMPLE_RATE, SAMPLE_SIZE_IN_BITS, NB_CHANNELS, true, false);

  private static final Logger logger = LogManager.getLogger(AudioLineReader.class);

  private final AtomicBoolean reading = new AtomicBoolean(false);

  private TargetDataLine line;

  private final List<Mp3Stream> streams = new ArrayList<>();

  private AudioLineReader() {
    // Nothing to do
  }

  public synchronized void read() {

    if (!reading.get()) {
      try {
        DataLine.Info info = getSupportedDataLineInfo();
        if (info != null) {

          Mp3Encoder mp3Encoder = new Mp3Encoder(compressionFormat);

          line = (TargetDataLine) AudioSystem.getLine(info);

          line.open(compressionFormat);
          line.start();
          reading.set(true);

          // SourceDataLine sourceDataLine = AudioSystem.getSourceDataLine(compressionFormat);
          // clip.start();

          var tab = new byte[2048];
          while (reading.get()) {
            int read = 0;
            while (read != 2048) {
              read += line.read(tab, read, 2048);
            }
            var mp3 = mp3Encoder.encodePcmToMp3(tab);
            writeToStream(mp3);
          }

        }
      } catch (LineUnavailableException e) {
        logger.error("Cannot read data", e);
      }
      logger.info("Audio line read started");
    } else {
      logger.error("Already reading audio line");
    }
  }

  private void writeToStream(byte[] mp3) {
    for (Mp3Stream stream : streams) {
      stream.put(mp3);
    }
  }

  public void subscribe(Mp3Stream stream) {
    streams.add(stream);
  }

  public synchronized void stop() {

    if (reading.get()) {

      logger.info("Stopping reading audio line.");

      line.stop();
      line = null;
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

}
