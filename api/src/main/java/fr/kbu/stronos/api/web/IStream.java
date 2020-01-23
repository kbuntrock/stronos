package fr.kbu.stronos.api.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import fr.kbu.stronos.api.dto.ServerInfoDto;

/**
 *
 * @author : Kevin Buntrock
 */
@RequestMapping(value = "/stream")
public interface IStream {

  @GetMapping("/info")
  ServerInfoDto info();

}
