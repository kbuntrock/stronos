package fr.kbu.stronos.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Kevin Buntrock
 *
 */
public class ConfigurationUtils {

  private static final Logger logger = LogManager.getLogger(ConfigurationUtils.class);

  private static final String CONFIG_FILENAME = "stronos-config.txt";
  private static final String CONFIG_FILE_PATH = System.getProperty("user.home") + File.separator
      + ".stronos" + File.separator + CONFIG_FILENAME;


  /**
   * Private constructor
   */
  private ConfigurationUtils() {
    // Nothing to do
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
        printWriter.printf("volume=%s\n", String.valueOf(volume));
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
        if (st.contains("volume=")) {
          br.close();
          return Float.valueOf(st.replaceAll("volume=", ""));
        }
      }
    } catch (IOException e) {
      logger.error("Cannot read configuration file", e);
    }

    return 1.0f;
  }
}

