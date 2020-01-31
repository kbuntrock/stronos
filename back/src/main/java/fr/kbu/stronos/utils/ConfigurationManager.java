package fr.kbu.stronos.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 *
 * @author Kevin Buntrock
 *
 */
public class ConfigurationManager {

  private static final Logger logger = LogManager.getLogger(ConfigurationManager.class);

  private static final String CONFIG_FILENAME = "stronos-config.txt";
  private static final String CONFIG_FILE_PATH = System.getProperty("user.home") + File.separator
      + ".stronos" + File.separator + CONFIG_FILENAME;

  private static final String VOLUME_PROP_NAME = "volume";
  private static final String CAPTURE_DEVICE_PROP_NAME = "capture";

  private final Configuration configuration = new Configuration();

  /**
   * Private constructor
   */
  private ConfigurationManager() {
    // Nothing to do
  }

  public void saveVolumeNew(float volume) {
    configuration.setVolume(String.valueOf(volume));
    saveConfiguration();
  }

  public void saveRecordingDevice(String recordingDevice) {
    configuration.setRecordingDevice(recordingDevice);
    saveConfiguration();
  }

  private void saveConfiguration() {

    try {
      ObjectMapper mapper = new ObjectMapper();
      String configString = mapper.writeValueAsString(configuration);

      File file = new File(CONFIG_FILE_PATH);
      if (!file.exists()) {
        file.getParentFile().mkdirs();
        file.createNewFile();
      }
      if (file.exists()) {
        FileWriter fileWriter;
        try {

          fileWriter = new FileWriter(file);
          PrintWriter printWriter = new PrintWriter(fileWriter);
          printWriter.print(configString);
          printWriter.close();
        } catch (IOException e) {
          logger.error("Cannot write to configuration file", e);
        }
      }
    } catch (JsonProcessingException e1) {
      logger.error("Cannot write to configuration file", e1);
    } catch (IOException e) {
      logger.error("Connot create or write to configuration file", e);
    }

  }

  public static void saveVolume(float volume) {

    File file = new File(CONFIG_FILE_PATH);
    if (!file.exists()) {
      try {
        file.getParentFile().mkdirs();
        file.createNewFile();
      } catch (IOException e) {
        logger.error("Connot create configuration file", e);
      }
    }
    if (file.exists()) {
      FileWriter fileWriter;
      try {
        fileWriter = new FileWriter(file);
        PrintWriter printWriter = new PrintWriter(fileWriter);
        printWriter.printf(VOLUME_PROP_NAME + "=%s\n", String.valueOf(volume));
        printWriter.close();
      } catch (IOException e) {
        logger.error("Cannot write to configuration file", e);
      }
    }
  }

  public static float getVolume() {
    File file = new File(CONFIG_FILE_PATH);
    if (!file.exists()) {
      return 1.0f;
    }
    BufferedReader br;
    try {
      br = new BufferedReader(new FileReader(file));
      String st;
      while ((st = br.readLine()) != null) {
        if (st.contains(VOLUME_PROP_NAME + "=")) {
          br.close();
          return Float.valueOf(st.replaceAll(VOLUME_PROP_NAME + "=", ""));
        }
      }
    } catch (IOException e) {
      logger.error("Cannot read configuration file", e);
    }

    return 1.0f;
  }

  public static float getCaptureDevice() {
    File file = new File(CONFIG_FILE_PATH);
    if (!file.exists()) {
      return 1.0f;
    }
    BufferedReader br;
    try {
      br = new BufferedReader(new FileReader(file));
      String st;
      while ((st = br.readLine()) != null) {
        if (st.contains(CAPTURE_DEVICE_PROP_NAME + "=")) {
          br.close();
          return Float.valueOf(st.replaceAll(CAPTURE_DEVICE_PROP_NAME + "=", ""));
        }
      }
    } catch (IOException e) {
      logger.error("Cannot read configuration file", e);
    }

    return 1.0f;
  }
}

