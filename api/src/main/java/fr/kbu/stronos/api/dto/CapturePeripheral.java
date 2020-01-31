package fr.kbu.stronos.api.dto;

/**
 * Audio capture peripheral informations
 *
 * @author Kevin Buntrock
 *
 */
public class CapturePeripheral {

  private String name;
  private String description;
  private Boolean active;

  /**
   * @param name
   * @param description
   * @param active
   */
  public CapturePeripheral(String name, String description, Boolean active) {
    super();
    this.name = name;
    this.description = description;
    this.active = active;
  }

  /**
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * @param name the name to set
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * @return the description
   */
  public String getDescription() {
    return description;
  }

  /**
   * @param description the description to set
   */
  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * @return the active
   */
  public Boolean getActive() {
    return active;
  }

  /**
   * @param active the active to set
   */
  public void setActive(Boolean active) {
    this.active = active;
  }



}
