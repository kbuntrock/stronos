package fr.pandorea.stronos;

import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import org.apache.catalina.connector.ClientAbortException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

public class Mp3Stream implements StreamingResponseBody {

  private static final Logger logger = LogManager.getLogger(Mp3Stream.class);

  private boolean shouldRun = true;

  public BlockingQueue<byte[]> queue = new LinkedBlockingQueue<>();

  private long totalBytes = 0;

  public Mp3Stream() {
    AudioLineReader.get().subscribe(this);
  }

  public void put(byte[] buffer) {
    queue.add(buffer);
  }

  @Override
  public void writeTo(OutputStream out) throws IOException {
    try {
      int i = 0;
      while (shouldRun) { // && i < 800) {
        out.write(queue.take());
        out.flush();

        totalBytes += AudioLineReader.BUFFER_SIZE;

        i++;
        if (i % 100 == 0) {
          logger.info(i);
        }

      }

    } catch (IOException | InterruptedException e) {
      if (e instanceof ClientAbortException) {
        logger.error("Client aborted");
      } else {
        logger.error("Error while writing stream", e);
      }

    }
    AudioLineReader.get().removeStream(this);
  }

  public void close() {
    shouldRun = false;
  }

  public double streamSince() {
    return totalBytes / (AudioLineReader.SAMPLE_SIZE_IN_BITS / 8 * AudioLineReader.NB_CHANNELS)
        / AudioLineReader.SAMPLE_RATE;
  }

}
