package fr.kbu.stronos.utils;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Tous les appels qui ne sont pas sur mappés redirigent vers l'application angular (mise à part
 * ceux allant sur "assets")
 * https://blog.impulsebyingeniance.io/spring-boot-angular-gerer-url-html-5/
 * 
 * @author Kevin Buntrock
 *
 */
@Controller
public class AngularRedirectionController {

  @RequestMapping(value = {"{path:(?:(?!assets|\\.).)*}/**"})
  public String redirect() {
    return "forward:/";
  }

}
