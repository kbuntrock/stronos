package fr.kbu.stronos.api.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import fr.kbu.stronos.api.dto.ServerInfoDto;

/**
 *
 * @author : Kevin Buntrock
 */
@RequestMapping(value = "/stream")
public interface IStream {

  @GetMapping("/info")
  ServerInfoDto info();

  @PostMapping("volume")
  float setVolume(@RequestParam float volume);

  @GetMapping("volume")
  float getVolume();

}
