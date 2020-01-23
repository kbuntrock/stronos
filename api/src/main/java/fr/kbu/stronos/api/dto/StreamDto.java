package fr.kbu.stronos.api.dto;

/**
 * StreamDto
 *
 * @author Kevin Buntrock
 */
public class StreamDto {

  private String id;
  private String ipAdress;

  /**
   * Number of seconds of sound which has been streamed
   */
  private long streamedSeconds;


  /**
   * @return the id
   */
  public String getId() {
    return id;
  }

  /**
   * @param id the id to set
   */
  public void setId(String id) {
    this.id = id;
  }

  /**
   * @return the ipAdress
   */
  public String getIpAdress() {
    return ipAdress;
  }

  /**
   * @param ipAdress the ipAdress to set
   */
  public void setIpAdress(String ipAdress) {
    this.ipAdress = ipAdress;
  }

  /**
   * @return the streamedSeconds
   */
  public long getStreamedSeconds() {
    return streamedSeconds;
  }

  /**
   * @param streamedSeconds the streamedSeconds to set
   */
  public void setStreamedSeconds(long streamedSeconds) {
    this.streamedSeconds = streamedSeconds;
  }

}
