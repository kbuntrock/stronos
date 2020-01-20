package fr.pandorea.stronos;

import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

public class Mp3Stream implements StreamingResponseBody {

  private static final Logger logger = LogManager.getLogger(Mp3Stream.class);

  public BlockingQueue<byte[]> queue = new LinkedBlockingQueue<>();

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
      while (AudioLineReader.get().isReading() && i < 1000) {
        out.write(queue.take());
        out.flush();
        i++;
        if (i % 100 == 0) {
          logger.info(i);
        }

      }
    } catch (IOException | InterruptedException e) {
      logger.error("Error while writing stream", e);
    }

  }

}
