package fr.kbu.stronos.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import fr.kbu.stronos.StronosApplication;
import fr.kbu.stronos.audio.AudioLineReader;
import fr.kbu.stronos.web.Mp3Stream;

/**
 * Used with some restricted computers (ex : raspberry), the first batch of mp3 encoding are taking
 * to much time to unsure a continuous stream.
 *
 * This is probably due to a lot of first object instantiation. I don't know if raspberry use
 * Hotspot, but the JVM could also optimize the run by itself after a few loops of doing the same
 * thing. (https://en.wikipedia.org/wiki/Adaptive_optimization)
 *
 * The goal of this specialized stream is to close itself after a warmup time (2000 loops, = 23 sec)
 *
 * @author Kevin Buntrock
 *
 */
public class StartupMp3Stream extends Mp3Stream {

  private static final Logger logger = LogManager.getLogger(StartupMp3Stream.class);

  public void warmup() {
    logger.info("Warming up!");

    try {
      int i = 0;
      while (shouldRun) {
        queue.take();
        i++;
        if (logger.isDebugEnabled()) {
          if (i % 100 == 0) {
            logger.debug("warmup write buffer n°{}", i);
          }
        }
        if (i > 2000) {
          close();
          StronosApplication.completeWarmup();
        }

      }
    } catch (InterruptedException e) {
      logger.error("Error while writing stream", e);
    }

    AudioLineReader.get().removeStream(this);
  }

}
