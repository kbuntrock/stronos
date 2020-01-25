package fr.kbu.stronos;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class StronosApplication {

  private static final Logger logger = LogManager.getLogger(StronosApplication.class);

  private static ConfigurableApplicationContext ctx;

  public static final long AWAKE_SINCE = System.currentTimeMillis();

  public static void main(String[] args) {

    ctx = SpringApplication.run(StronosApplication.class, args);

  }

  @PostConstruct
  private void launchApp() {
    logger.info("launchApp");
    ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(2);
    executor.submit(() -> AudioLineReader.get().read());
  }

  @PreDestroy
  private void onExit() {
    AudioLineReader.get().stop();
  }

  @Bean
  public WebMvcConfigurer corsConfigurer() {
    return new WebMvcConfigurer() {
      @Override
      public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").allowedOrigins("*");
      }
    };
  }

  public static void stop() {
    int exitCode = SpringApplication.exit(ctx, () -> 0);
    System.exit(exitCode);
  }

}
