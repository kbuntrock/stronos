package fr.kbu.stronos.api.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * Server info Dto
 *
 * @author Kevin Buntrock
 *
 */
public class ServerInfoDto {

  /**
   * Indicate if the app is ready to broadcast audio
   */
  private boolean warmupComplete;

  /**
   * Value in seconds
   */
  private long awakeSince;

  /**
   * True if there is at least one audio entry peripheral
   */
  private boolean recordingSupported;

  /**
   * List of streams
   */
  private List<StreamDto> streamList = new ArrayList<>();

  /**
   * @return the awakeSince
   */
  public long getAwakeSince() {
    return awakeSince;
  }

  /**
   * @param awakeSince the awakeSince to set
   */
  public void setAwakeSince(long awakeSince) {
    this.awakeSince = awakeSince;
  }

  /**
   * @return the streamList
   */
  public List<StreamDto> getStreamList() {
    return streamList;
  }

  /**
   * @param streamList the streamList to set
   */
  public void setStreamList(List<StreamDto> streamList) {
    this.streamList = streamList;
  }

  /**
   * @return the warmupComplete
   */
  public boolean isWarmupComplete() {
    return warmupComplete;
  }

  /**
   * @param warmupComplete the warmupComplete to set
   */
  public void setWarmupComplete(boolean warmupComplete) {
    this.warmupComplete = warmupComplete;
  }

  /**
   * @return the recordingSupported
   */
  public boolean isRecordingSupported() {
    return recordingSupported;
  }

  /**
   * @param recordingSupported the recordingSupported to set
   */
  public void setRecordingSupported(boolean recordingSupported) {
    this.recordingSupported = recordingSupported;
  }

}
