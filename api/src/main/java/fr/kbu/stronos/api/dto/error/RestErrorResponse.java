package fr.kbu.stronos.api.dto.error;

import java.util.ArrayList;
import java.util.List;

public class RestErrorResponse {
  private List<RestError> errors = new ArrayList<>();

  public List<RestError> getErrors() {
    return errors;
  }

  public void setErrors(List<RestError> errors) {
    this.errors = errors;
  }

  public void addError(RestError error) {
    this.errors.add(error);
  }

}
