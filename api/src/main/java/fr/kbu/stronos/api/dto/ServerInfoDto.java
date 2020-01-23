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
   * Value in seconds
   */
  private long awakeSince;

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

}
