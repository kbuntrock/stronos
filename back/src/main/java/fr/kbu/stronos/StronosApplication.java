package fr.kbu.stronos;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.sound.sampled.LineUnavailableException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import fr.kbu.stronos.audio.AudioLineReader;
import fr.kbu.stronos.audio.NoCaptureDeviceAvailable;
import fr.kbu.stronos.utils.ConfigurationManager;
import fr.kbu.stronos.utils.StartupMp3Stream;

@SpringBootApplication
public class StronosApplication {

  private static final Logger logger = LogManager.getLogger(StronosApplication.class);

  private static ConfigurableApplicationContext ctx;

  public static final long AWAKE_SINCE = System.currentTimeMillis();

  private static boolean warmupComplete = false;

  private static ThreadPoolExecutor thPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(2);

  /**
   * Launche the app
   * 
   * @param args
   */
  public static void main(String[] args) {

    ctx = SpringApplication.run(StronosApplication.class, args);

  }

  /**
   * Set the warmup state to true
   */
  public static void completeWarmup() {
    logger.info("Warmup is complete!");
    warmupComplete = true;
  }

  /**
   * Indicate if the app warmup is complete
   *
   * @return true if it is
   */
  public static boolean isWarmupComplete() {
    return warmupComplete;
  }

  /**
   * Give access to the ThreadPool executor
   *
   * @return ThreadPoolExecutor
   */
  public static ThreadPoolExecutor getThreadPool() {
    return thPool;
  }

  @PostConstruct
  private void launchApp() {
    logger.info("Stronos App startup");
    AudioLineReader.get().adjusVolume(ConfigurationManager.getVolume());

    try {
      AudioLineReader.get().openAudioLine(null);
      // Start the read loop
      thPool.submit(() -> AudioLineReader.get().read());

      // Create a new startup autoclosable mp3Stream for warmup
      thPool.submit(() -> {
        StartupMp3Stream s = new StartupMp3Stream();
        s.warmup();
      });

    } catch (NoCaptureDeviceAvailable | LineUnavailableException e) {
      logger.error("Recording is not supported on this machine");
    }


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
