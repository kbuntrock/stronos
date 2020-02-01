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
public enum ConfigurationManager {
  INSTANCE;

  private static final Logger logger = LogManager.getLogger(ConfigurationManager.class);

  private static final String CONFIG_FILENAME = "stronos.config";
  private static final String CONFIG_FILE_PATH = System.getProperty("user.home") + File.separator
      + ".stronos" + File.separator + CONFIG_FILENAME;

  public static ConfigurationManager get() {
    return INSTANCE;
  }

  private Configuration configuration = null;

  /**
   * Private constructor
   */
  private ConfigurationManager() {
    // Nothing to do
  }

  public void saveVolume(float volume) {
    getConfiguration().setVolume(String.valueOf(volume));
    saveConfiguration();
  }

  public void saveRecordingDevice(String recordingDevice) {
    getConfiguration().setRecordingDevice(recordingDevice);
    saveConfiguration();
  }

  public float getVolume() {
    return Float.valueOf(getConfiguration().getVolume());
  }

  public String getRecordingDevice() {
    return getConfiguration().getRecordingDevice();
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

  private Configuration getConfiguration() {
    if (configuration != null) {
      return configuration;
    }
    File file = new File(CONFIG_FILE_PATH);
    if (file.exists()) {
      try (BufferedReader br = new BufferedReader(new FileReader(file))) {

        StringBuilder sb = new StringBuilder();
        String st;
        while ((st = br.readLine()) != null) {
          sb.append(st);
        }

        ObjectMapper mapper = new ObjectMapper();
        configuration = mapper.readValue(sb.toString(), Configuration.class);
        return configuration;

      } catch (IOException e) {
        logger.error("Impossible to read conf file", e);
      }

    }
    configuration = new Configuration();
    configuration.setVolume(String.valueOf(1.0f));
    return configuration;
  }

}

