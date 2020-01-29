package fr.kbu.stronos.web;

import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import org.apache.catalina.connector.ClientAbortException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import fr.kbu.stronos.audio.AudioLineReader;

/**
 *
 * @author Kevin Buntrock
 *
 */
public class Mp3Stream implements StreamingResponseBody {

  private static final Logger logger = LogManager.getLogger(Mp3Stream.class);

  private static byte[] blankBuffer;

  static {
    try {
      blankBuffer =
          Mp3Stream.class.getClassLoader().getResourceAsStream("blank2sec.mp3").readAllBytes();
    } catch (IOException e) {
      logger.error("Cannot read blankBuffer mp3.", e);
      blankBuffer = new byte[0];
    }

  }

  protected boolean shouldRun = true;

  public BlockingQueue<byte[]> queue = new LinkedBlockingQueue<>();

  protected long totalBytes = 0;

  private String ipAddress;
  private String userAgent;

  public Mp3Stream() {
    AudioLineReader.get().subscribe(this);
  }

  public void put(byte[] buffer) {
    queue.add(buffer);
  }

  @Override
  public void writeTo(OutputStream out) throws IOException {

    // When the stream is starting up, we write 2 seconds of empty sound to quickly fill
    // the Sonos buffer and prevent an early disconnection.
    out.write(blankBuffer);
    out.flush();

    try {
      int i = 0;
      while (shouldRun) {
        out.write(queue.take());
        out.flush();

        totalBytes += AudioLineReader.BUFFER_SIZE;

        if (logger.isDebugEnabled()) {
          i++;
          if (i % 100 == 0) {
            logger.debug("mp3Stream write buffer n°{}", i);
          }
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

  /**
   * @return the ipAddress
   */
  public String getIpAddress() {
    return ipAddress;
  }

  /**
   * @param ipAddress the ipAddress to set
   */
  public void setIpAddress(String ipAddress) {
    this.ipAddress = ipAddress;
  }

  /**
   * @return the userAgent
   */
  public String getUserAgent() {
    return userAgent;
  }

  /**
   * @param userAgent the userAgent to set
   */
  public void setUserAgent(String userAgent) {
    this.userAgent = userAgent;
  }



}
