package fr.kbu.stronos;

/**
 * Adjust the recording volume
 *
 * Based on
 * https://stackoverflow.com/questions/14485873/audio-change-volume-of-samples-in-byte-array
 *
 * @author kbuntrock
 *
 */
public final class VolumeUtils {

  /**
   * Private contructor
   */
  private VolumeUtils() {
    // Nothing to do
  }

  public static byte[] adjustVolume(byte[] audioSamples, float volume) {
    if (volume == 1.0f) {
      return audioSamples;
    }
    return adjustVolumeInternal(audioSamples, volume);
  }

  private static byte[] adjustVolumeInternal(byte[] audioSamples, float volume) {
    byte[] array = new byte[audioSamples.length];
    for (int i = 0; i < array.length; i += 2) {
      // convert byte pair to int
      short buf1 = audioSamples[i + 1];
      short buf2 = audioSamples[i];

      buf1 = (short) ((buf1 & 0xff) << 8);
      buf2 = (short) (buf2 & 0xff);

      short res = (short) (buf1 | buf2);
      res = (short) (res * volume);

      // convert back
      array[i] = (byte) res;
      array[i + 1] = (byte) (res >> 8);

    }
    return array;
  }
}
