package fr.kbu.stronos.audio;

import javax.sound.sampled.DataLine;
import javax.sound.sampled.DataLine.Info;

/**
 * Wrap the audio line informations
 *
 * @author Kevin Buntrock
 *
 */
public class AudioLineInfo {

  private final DataLine.Info dataLineInfo;

  private final String deviceName;


  /**
   * @param dataLineInfo
   * @param deviceName
   */
  public AudioLineInfo(Info dataLineInfo, String deviceName) {
    super();
    this.dataLineInfo = dataLineInfo;
    this.deviceName = deviceName;
  }

  /**
   * @return the dataLineInfo
   */
  public DataLine.Info getDataLineInfo() {
    return dataLineInfo;
  }

  /**
   * @return the deviceName
   */
  public String getDeviceName() {
    return deviceName;
  }



}
