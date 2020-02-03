package fr.kbu.stronos.audio;

import javax.sound.sampled.DataLine;
import javax.sound.sampled.DataLine.Info;
import javax.sound.sampled.Mixer;

/**
 * Wrap the audio line informations
 *
 * @author Kevin Buntrock
 *
 */
public class AudioLineInfo {

  private final DataLine.Info dataLineInfo;

  /**
   * Default or the name of the mixer
   */
  private final String deviceName;

  private final Mixer.Info mixerInfo;


  /**
   * @param dataLineInfo
   * @param deviceName
   */
  public AudioLineInfo(Info dataLineInfo, String deviceName, final Mixer.Info mixerInfo) {
    super();
    this.dataLineInfo = dataLineInfo;
    this.deviceName = deviceName;
    this.mixerInfo = mixerInfo;
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

  /**
   * @return the mixerInfo
   */
  public Mixer.Info getMixerInfo() {
    return mixerInfo;
  }

}
