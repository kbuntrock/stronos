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
 * 1 buffer = 16384 samples | 1 sec = 44100 samples | 1 sec = 44100 / 16384 = 2.69 buffers
 *
 * The goal of this specialized stream is to close itself after a warmup time (13 loops approx 4.82
 * sec)
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
          logger.debug("warmup write buffer n°{}", i);
        }
        if (i > 13) {
          close();
          StronosApplication.completeWarmup();
        }

      }
    } catch (@SuppressWarnings("squid:S2142") InterruptedException e) {
      // This is unfortunate, but there is no explicit need to re-throw the exception
      logger.error("Error while writing stream for warmup", e);
    }

    AudioLineReader.get().removeStream(this);
  }

}
