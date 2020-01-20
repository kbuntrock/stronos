package fr.pandorea.stronos;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

@RestController
@RequestMapping(value = "/stream")
public class StreamingService {

  @GetMapping(value = "", produces = "audio/mp3")
  public ResponseEntity<StreamingResponseBody> stream() {

    final HttpHeaders headers = new HttpHeaders();
    headers.setContentDispositionFormData("filename", "stream.mp3");

    return ResponseEntity.ok().headers(headers).contentType(MediaType.valueOf("audio/mp3"))
        .body(new Mp3Stream());

  }

  @GetMapping(value = "test")
  public String test() {

    return "test";

  }

  @GetMapping(value = "stop")
  public String stop() {
    AudioLineReader.get().stop();
    return "OK";
  }

}
