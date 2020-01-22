package fr.kbu.stronos;

import java.io.File;
import java.io.IOException;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

public class Test {

  public static void main(String[] args)
      throws UnsupportedAudioFileException, IOException, LineUnavailableException {


    File sample = new File(Test.class.getClassLoader().getResource("sample.wav").getFile());
    AudioInputStream input = AudioSystem.getAudioInputStream(sample);

    AudioFormat format = input.getFormat();

    SourceDataLine sourceDataLine = AudioSystem.getSourceDataLine(format);
    sourceDataLine.open(format);
    sourceDataLine.start();

    int nBytesRead = 0;
    byte[] abData = new byte[2048];

    while (nBytesRead != -1) {
      nBytesRead = input.read(abData, 0, abData.length);
      if (nBytesRead >= 0) {
        sourceDataLine.write(abData, 0, nBytesRead);
      }
      System.out.println(nBytesRead);
    }
    sourceDataLine.drain();
    sourceDataLine.close();

  }

}
