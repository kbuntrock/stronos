package fr.pandorea.stronos;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.apache.catalina.connector.ClientAbortException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

@RestController
@RequestMapping(value = "/stream")
public class StreamingService implements WebMvcConfigurer {

  private static final NumberFormat decimalFormat = new DecimalFormat("#0.00");

  @Override
  public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
    configurer.setDefaultTimeout(100000000); // in milliseconds (20 hours)
  }

  @ExceptionHandler(ClientAbortException.class)
  @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
  public Object exceptionHandler(ClientAbortException e, HttpServletRequest request) {
    return null;
  }

  @GetMapping(value = "", produces = "audio/mp3")
  public ResponseEntity<StreamingResponseBody> stream() {

    Mp3Stream stream = new Mp3Stream();
    final HttpHeaders headers = new HttpHeaders();
    headers.setContentDispositionFormData("filename", "stream.mp3");

    return ResponseEntity.ok().headers(headers).contentType(MediaType.valueOf("audio/mp3"))
        .body(stream);

  }

  @GetMapping(value = "test")
  public String test() {

    return "test";

  }

  @GetMapping(value = "stop")
  public String stop() {
    AudioLineReader.get().closeStreams();
    return "OK";
  }

  @GetMapping(value = "info")
  public String getTotalSample() {
    StringBuilder builder = new StringBuilder();

    List<Mp3Stream> streams = AudioLineReader.get().getStreams();
    builder.append(streams.size());
    builder.append(" streams<br>");
    for (int i = 1; i <= streams.size(); i++) {
      builder.append(i);
      builder.append(" - streamed ");
      builder.append(decimalFormat.format(streams.get(i - 1).streamSince()));
      builder.append("s<br>");
    }

    return builder.toString();
  }

}
