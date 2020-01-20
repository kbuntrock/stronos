package fr.pandorea.stronos;

import java.io.ByteArrayOutputStream;
import javax.sound.sampled.AudioFormat;
import net.sourceforge.lame.lowlevel.LameEncoder;
import net.sourceforge.lame.mp3.Lame;
import net.sourceforge.lame.mp3.MPEGMode;

public class Mp3Encoder {

  private final AudioFormat audioFormat;

  public Mp3Encoder(AudioFormat audioFormat) {
    this.audioFormat = audioFormat;
  }

  public byte[] encodePcmToMp3(byte[] pcm) {

    LameEncoder encoder =
        new LameEncoder(audioFormat, 256, MPEGMode.STEREO, Lame.QUALITY_HIGHEST, false);

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

    encoder.close();
    return mp3.toByteArray();
  }

}
