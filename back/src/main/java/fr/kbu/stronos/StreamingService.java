package fr.kbu.stronos;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.apache.catalina.connector.ClientAbortException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import fr.kbu.stronos.api.dto.ServerInfoDto;
import fr.kbu.stronos.api.dto.StreamDto;
import fr.kbu.stronos.api.web.IStream;

@RestController
public class StreamingService implements IStream, WebMvcConfigurer {

  private static final Logger logger = LogManager.getLogger(StreamingService.class);

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
  public ResponseEntity<StreamingResponseBody> stream(HttpServletRequest request) {

    Mp3Stream stream = new Mp3Stream();
    stream.setIpAddress(request.getRemoteAddr());
    stream.setUserAgent(request.getHeader("User-Agent"));

    final HttpHeaders headers = new HttpHeaders();
    headers.setContentDispositionFormData("filename", "stream.mp3");

    return ResponseEntity.ok().headers(headers).contentType(MediaType.valueOf("audio/mp3"))
        .body(stream);

  }

  @GetMapping(value = "stop")
  public String stop() {
    AudioLineReader.get().closeStreams();
    return "OK";
  }

  // @GetMapping(value = "info")
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

  @Override
  public ServerInfoDto info() {
    ServerInfoDto response = new ServerInfoDto();

    AudioLineReader.get().getStreams().forEach(stream -> {
      StreamDto strDto = new StreamDto();
      strDto.setStreamedSeconds((long) stream.streamSince());
      strDto.setIpAdress(stream.getIpAddress());
      strDto.setUserAgent(stream.getUserAgent());
      response.getStreamList().add(strDto);
    });
    response.setAwakeSince((System.currentTimeMillis() - StronosApplication.AWAKE_SINCE) / 1000);
    return response;
  }

  @Override
  public float setVolume(float volume) {
    logger.info("setVolume {}", volume);
    return AudioLineReader.get().adjusVolume(volume);
  }

  @Override
  public float getVolume() {
    return AudioLineReader.get().getVolume();
  }

}
