package fr.kbu.stronos.audio;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.TargetDataLine;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import fr.kbu.stronos.web.Mp3Stream;

/**
 * Read the audio line. Must exist in only one instance.
 *
 * @author Kevin Buntrock
 *
 */
public enum AudioLineReader {
  INSTANCE;

  public static AudioLineReader get() {
    return INSTANCE;
  }

  /**
   * Format used for recording : - 44100 Hz - 16 bits per sample - stéréo
   */
  public static final float SAMPLE_RATE = 44100;
  public static final int SAMPLE_SIZE_IN_BITS = 16;
  // 1 = mono, 2 = stéréo
  public static final int NB_CHANNELS = 2;

  private static final AudioFormat compressionFormat =
      new AudioFormat(SAMPLE_RATE, SAMPLE_SIZE_IN_BITS, NB_CHANNELS, true, false);

  // Reading buffer size
  public static final int BUFFER_SIZE = 1024 * 64;

  private static final Logger logger = LogManager.getLogger(AudioLineReader.class);

  private final AtomicBoolean reading = new AtomicBoolean(false);

  private TargetDataLine inputLine;

  private final List<Mp3Stream> streams = new ArrayList<>();

  /**
   * Recorded volume
   */
  private float volume = 1.0f;

  private Mp3Encoder mp3Encoder;

  private AudioInputStream inputStream;

  private String currentRecordingDevice;

  /**
   * Private constuctor
   */
  private AudioLineReader() {
    // Nothing to do
  }

  public synchronized void openAudioLine(final String captureDeviceName)
      throws NoCaptureDeviceAvailable, LineUnavailableException {

    AudioLineInfo info = getSupportedDataLineInfo(captureDeviceName);

    // New encoder needed, to purge potential remaining data from the current one
    mp3Encoder = new Mp3Encoder(compressionFormat);

    inputLine = (TargetDataLine) AudioSystem.getLine(info.getDataLineInfo());

    inputLine.open(compressionFormat);
    inputLine.start();

    inputStream = new AudioInputStream(inputLine);

    currentRecordingDevice = info.getDeviceName();

    logger.info("AudioInputStream created");
    logger.info("Input format : {}", inputStream.getFormat());

  }

  public void read() {

    if (reading.get()) {
      logger.error("Already reading audio line");
      return;
    } else if (inputStream == null) {
      logger.error("There is no input to read.");
      return;
    }

    try {

      reading.set(true);

      logger.info("Audio line read started");

      int nBytesRead = 0;
      byte[] abData = new byte[BUFFER_SIZE];

      while (nBytesRead != -1 && reading.get()) {
        nBytesRead = inputStream.read(abData, 0, abData.length);
        logger.info(nBytesRead);
        if (nBytesRead >= 0 && !streams.isEmpty()) {
          abData = VolumeUtils.adjustVolume(abData, volume);
          var mp3 = mp3Encoder.encodePcmToMp3(abData);
          writeToStream(mp3, nBytesRead);
        }
      }

    } catch (IOException e) {
      logger.error("Error while reading audio line data", e);
    }
    return;
  }

  private void writeToStream(byte[] mp3, long nBytesRead) {
    for (Mp3Stream stream : streams) {
      stream.put(mp3, nBytesRead);
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

  public synchronized void stop() {


    logger.info("Stopping reading audio line.");

    if (inputLine != null && inputLine.isOpen()) {
      inputLine.stop();
      inputLine.close();
      inputLine = null;
    }

    if (inputStream != null) {
      try {
        inputStream.close();
      } catch (IOException e) {
        logger.error("Cannot close audio line input stream", e);
      }
    }
    streams.clear();

    currentRecordingDevice = null;

    reading.set(false);
  }

  /**
   * Return the name of the current recording device
   *
   * @return a mixer name
   */
  public String getCurrentRecordingDevice() {
    return currentRecordingDevice;
  }

  /**
   * Try to access to a wanted device data line. If not possible, try to retrieve the first
   * compatible one.
   *
   * @param wantedDeviceName
   * @return DataLine.Info
   * @throws NoCaptureDeviceAvailable
   */
  private AudioLineInfo getSupportedDataLineInfo(String wantedDeviceName)
      throws NoCaptureDeviceAvailable {

    List<Mixer.Info> mixers = Arrays.asList(AudioSystem.getMixerInfo());
    if (wantedDeviceName != null && !wantedDeviceName.isBlank()) {
      List<Mixer.Info> wantedList = mixers.stream()
          .filter(m -> wantedDeviceName.equals(m.getName())).collect(Collectors.toList());

      if (!wantedList.isEmpty()) {
        if (wantedList.size() > 1) {
          logger.warn(
              "There is more than one device corresponding to this name : {}. We are picking the first one.",
              wantedDeviceName);
        }
        try (Mixer targetMixer = AudioSystem.getMixer(wantedList.get(0))) {
          targetMixer.open();
          // Check if it supports the desired format
          DataLine.Info info = new DataLine.Info(TargetDataLine.class, compressionFormat);
          if (targetMixer.isLineSupported(info)) {
            logger.info("selected device {} supports recording @ {}", wantedDeviceName,
                compressionFormat);
            return new AudioLineInfo(info, wantedDeviceName);
          } else {
            logger.warn("selected device {} DOES NOT supports recording @ {}", wantedDeviceName,
                compressionFormat);
          }
        } catch (LineUnavailableException e) {
          logger.info("Selected device {} line unavailable.", wantedDeviceName);
        }

      }
    }

    logger.warn("Fallback, we select the first available device");

    for (Mixer.Info mi : AudioSystem.getMixerInfo()) {
      logger.info("Available mixer : {}", mi.getName());

      try (Mixer targetMixer = AudioSystem.getMixer(mi)) {
        targetMixer.open();
        // Check if it supports the desired format
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, compressionFormat);
        if (targetMixer.isLineSupported(info)) {
          logger.info("{} supports recording @ {}", mi.getName(), compressionFormat);
          return new AudioLineInfo(info, mi.getName());
        }
      } catch (LineUnavailableException e) {
        logger.error("Error while finding a supported format.", e);
      }
    }
    throw new NoCaptureDeviceAvailable();

  }

  public List<String> getCompatibleCaptureMixer() {

    List<String> mixerList = new ArrayList<>();

    for (Mixer.Info mi : AudioSystem.getMixerInfo()) {

      try (Mixer targetMixer = AudioSystem.getMixer(mi)) {
        targetMixer.open();
        // Check if it supports the desired format
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, compressionFormat);
        if (targetMixer.isLineSupported(info)) {
          mixerList.add(mi.getName());
        }
      } catch (LineUnavailableException e) {
        // Noting to do, that is unfortunate
      }
    }
    return mixerList;
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

  public float getVolume() {
    return volume;
  }

}
