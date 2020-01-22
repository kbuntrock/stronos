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
   * Value in seconds
   */
  private long streamSince;


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
   * @return the streamSince
   */
  public long getStreamSince() {
    return streamSince;
  }

  /**
   * @param streamSince the streamSince to set
   */
  public void setStreamSince(long streamSince) {
    this.streamSince = streamSince;
  }



}
