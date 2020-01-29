package fr.kbu.stronos.api.dto.error;

/**
 *
 * @author Kevin Buntrock
 *
 */
public class RestErrorArg {

  private String label;
  private String value;

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

}
