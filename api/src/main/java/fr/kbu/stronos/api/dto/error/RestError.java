package fr.kbu.stronos.api.dto.error;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class RestError {

  private String errorCode;
  private String errorMessage;
  private List<RestErrorArg> errorArgs = new ArrayList<>();

  public String getErrorCode() {
    return errorCode;
  }

  public void setErrorCode(String errorCode) {
    this.errorCode = errorCode;
  }

  public String getErrorMessage() {
    return errorMessage;
  }

  public void setErrorMessage(String errorMessage) {
    this.errorMessage = errorMessage;
  }

  public List<RestErrorArg> getErrorArgs() {
    return errorArgs;
  }

  public void setErrorArgs(List<RestErrorArg> errorArgs) {
    this.errorArgs = errorArgs;
  }

  public void addErrorArgs(RestErrorArg errorArgs) {
    this.errorArgs.add(errorArgs);
  }
}
