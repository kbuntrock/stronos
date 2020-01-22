package fr.kbu.stronos.api.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import fr.kbu.stronos.api.dto.StreamDto;

/**
 *
 * @author : Kevin Buntrock
 */
@RequestMapping(value = "/game")
public interface IStream {

  @GetMapping("/steam")
  String stream();

  @GetMapping("/info")
  StreamDto info();

}
