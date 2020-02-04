package fr.kbu.stronos.utils;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * All GET calls not mapped are redirected to the angular app (except thoses going to "assets")
 * https://blog.impulsebyingeniance.io/spring-boot-angular-gerer-url-html-5/
 * 
 * @author Kevin Buntrock
 *
 */
@Controller
public class AngularRedirectionController {

  @GetMapping(value = {"{path:(?:(?!assets|\\.).)*}/**"})
  public String redirect() {
    return "forward:/";
  }

}
