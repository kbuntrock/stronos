package fr.pandorea.stronos;

import java.io.ByteArrayOutputStream;
import javax.sound.sampled.AudioFormat;
import de.sciss.jump3r.lowlevel.LameEncoder;

public class Mp3Encoder {

  // private final AudioFormat audioFormat;

  private final LameEncoder encoder;

  public Mp3Encoder(AudioFormat audioFormat) {
    // this.audioFormat = audioFormat;
    encoder = new LameEncoder(audioFormat);
  }

  public byte[] encodePcmToMp3(byte[] pcm) {

    // LameEncoder encoder = new LameEncoder(audioFormat, 256, LameEncoder.CHANNEL_MODE_STEREO,
    // LameEncoder.QUALITY_HIGHEST, false);

    ByteArrayOutputStream mp3 = new ByteArrayOutputStream();
    byte[] buffer = new byte[encoder.getPCMBufferSize()];

    int bytesToTransfer = Math.min(buffer.length, pcm.length);
    int bytesWritten;
    int currentPcmPosition = 0;
    while (0 < (bytesWritten =
        encoder.encodeBuffer(pcm, currentPcmPosition, bytesToTransfer, buffer))) {
      currentPcmPosition += bytesToTransfer;
      bytesToTransfer = Math.min(buffer.length, pcm.length - currentPcmPosition);

      mp3.write(buffer, 0, bytesWritten);
    }

    // encoder.close();
    return mp3.toByteArray();
  }

  public void close() {
    encoder.close();
  }

}
