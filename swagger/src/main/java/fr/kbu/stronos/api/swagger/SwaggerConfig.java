package fr.kbu.stronos.api.swagger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.WebApplicationContext;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 *
 * @author : Kevin Buntrock
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig {

  private static Logger LOGGER = LogManager.getLogger(SwaggerConfig.class);

  @Autowired
  WebApplicationContext context;

  @Bean
  public Docket api() {
    LOGGER.info("Create Smartfox Bean");
    return new Docket(DocumentationType.SWAGGER_2).select()
        .apis(RequestHandlerSelectors.basePackage("fr.kbu")).paths(PathSelectors.any())
        .build();
  }
}
