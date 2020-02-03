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
 * Start / Read / Stop the audio line. Must exist in only one instance.
 *
 * @author Kevin Buntrock
 *
 */
public enum AudioLineReader {
  INSTANCE;

  /**
   * Get the singleton instance of the AudioLineReader
   * 
   * @return AudioLineReader
   */
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

  // Reading buffer size, dependant of the audio line buffer
  public static int bufferSize = 1024 * 16;

  private static final Logger logger = LogManager.getLogger(AudioLineReader.class);

  private static final String DEFAULT_MIXER_NAME = "Default";

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

  private List<String> recordingDeviceList;

  /**
   * Private constuctor
   */
  private AudioLineReader() {
    initCompatibleDeviceList();
  }

  /**
   * Open a Audio Line, in a state ready to be read.
   * 
   * @param captureDeviceName
   * @throws NoCaptureDeviceAvailable
   * @throws LineUnavailableException
   */
  public synchronized void openAudioLine(final String captureDeviceName)
      throws NoCaptureDeviceAvailable, LineUnavailableException {

    AudioLineInfo info = getSupportedDataLineInfo(captureDeviceName);

    // Possibly the current recording device is not the one given in parameter
    // if the wanted one is not available anymore (ex : usb peripheral has been disconnected)
    currentRecordingDevice = info.getDeviceName();

    // New encoder needed, to purge potential remaining data from the current one
    mp3Encoder = new Mp3Encoder(compressionFormat);

    if (DEFAULT_MIXER_NAME.equals(info.getDeviceName())) {
      // Find the default System mixer corresponding to the dataline info
      inputLine = AudioSystem.getTargetDataLine(compressionFormat);
      // inputLine = (TargetDataLine) AudioSystem.getLine(info.getDataLineInfo());
    } else {
      inputLine = AudioSystem.getTargetDataLine(compressionFormat, info.getMixerInfo());
    }

    // The buffer size is important :
    // "https://docs.oracle.com/javase/8/docs/technotes/guides/sound/programmer_guide/chapter5.html"

    // "If you make it as big as the line's buffer and try to read the entire buffer, you need to be
    // very exact in your timing, because data will be dumped if the mixer needs to deliver data to
    // the line while you are reading from it. By using some fraction of the line's buffer size, as
    // shown here, your application will be more successful in sharing access to the line's buffer
    // with the mixer".
    bufferSize = (inputLine.getBufferSize() / 5);
    // We want a multiple of 2, because each sample takes 2 bytes
    bufferSize += bufferSize % 2;

    logger.info("Target data line buffer size : {}", inputLine.getBufferSize());
    logger.info("App buffer size : {}", bufferSize);

    inputLine.addLineListener(event -> {
      logger.warn("Audio line event received : " + event.getType() + " - " + event.getSource());
    });

    inputLine.open(compressionFormat);
    inputLine.start();

    inputStream = new AudioInputStream(inputLine);

    logger.info("AudioInputStream created");
    logger.info("Input format : {}", inputStream.getFormat());

  }

  /**
   * Read the opened audio line. If one or more client has subscribed to the stream, converts the
   * wav buffer to a mp3 one and copy it to the output stream(s)
   * 
   * This method should be called from a dedicated thread
   */
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
      byte[] abData = new byte[bufferSize];

      while (nBytesRead != -1 && reading.get()) {
        nBytesRead = inputStream.read(abData, 0, abData.length);
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

  /**
   * Write the mp3 byte array to the clients output streams
   * 
   * @param mp3 byte array
   * @param nb of byte read from the audio line (pre-mp3 conversion, used to compute the time of
   *        audio broadcasting)
   */
  private void writeToStream(byte[] mp3, long nBytesRead) {
    for (Mp3Stream stream : streams) {
      stream.put(mp3, nBytesRead);
    }
  }

  /**
   * Subscribe a client Mp3Stream
   * 
   * @param stream
   */
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
    stop(true);
  }

  public synchronized void stop(boolean shutdownStreams) {


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
    if (shutdownStreams) {
      streams.clear();
    }

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
            return new AudioLineInfo(info, wantedDeviceName, targetMixer.getMixerInfo());
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
          logger.info("Default {} supports recording @ {}", mi.getName(), compressionFormat);
          return new AudioLineInfo(info, DEFAULT_MIXER_NAME, targetMixer.getMixerInfo());
        }
      } catch (LineUnavailableException e) {
        logger.error("Error while finding a supported format.", e);
      }
    }
    throw new NoCaptureDeviceAvailable();

  }

  private void initCompatibleDeviceList() {

    recordingDeviceList = new ArrayList<>();
    recordingDeviceList.add(DEFAULT_MIXER_NAME);

    for (Mixer.Info mi : AudioSystem.getMixerInfo()) {

      try (Mixer targetMixer = AudioSystem.getMixer(mi)) {
        targetMixer.open();
        // Check if it supports the desired format
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, compressionFormat);
        if (targetMixer.isLineSupported(info)) {
          recordingDeviceList.add(mi.getName());
        }
      } catch (LineUnavailableException e) {
        // Noting to do, that is unfortunate
      }
    }
  }

  public List<String> getRecordingDeviceList() {
    return recordingDeviceList;
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
