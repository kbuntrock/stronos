package fr.kbu.stronos.api.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import fr.kbu.stronos.api.dto.ServerInfoDto;

/**
 *
 * @author : Kevin Buntrock
 */
@RequestMapping(value = "/stream")
public interface IStream {

  @GetMapping("/info")
  ServerInfoDto info();

  @GetMapping(value = "", produces = "audio/mp3")
  ResponseEntity<StreamingResponseBody> stream();

}
