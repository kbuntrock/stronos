package fr.kbu.stronos;

/**
 * Based on
 * https://stackoverflow.com/questions/14485873/audio-change-volume-of-samples-in-byte-array
 *
 * @author kbuntrock
 *
 */
public class VolumeControler {

  private static int N_SHORTS = 0xffff;
  private static final short[] VOLUME_NORM_LUT = new short[N_SHORTS];
  private static int MAX_NEGATIVE_AMPLITUDE = 0x8000;

  // static {
  // precomputeVolumeNormLUT();
  // }
  //
  // private static boolean withNormalization = false;
  //
  // public static boolean toogleNormalization() {
  // withNormalization = !withNormalization;
  // return withNormalization;
  // }

  public static byte[] adjustVolume(byte[] audioSamples, float volume) {
    if (volume == 1.0f) {
      return audioSamples;
    }
    var array = adjustVolumeInternal(audioSamples, volume);
    // if (withNormalization) {
    // normalizeVolume(audioSamples, 0, audioSamples.length);
    // }
    return array;
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

  private static void normalizeVolume(byte[] audioSamples, int start, int len) {
    for (int i = start; i < start + len; i += 2) {
      // convert byte pair to int
      short s1 = audioSamples[i + 1];
      short s2 = audioSamples[i];

      s1 = (short) ((s1 & 0xff) << 8);
      s2 = (short) (s2 & 0xff);

      short res = (short) (s1 | s2);

      res = VOLUME_NORM_LUT[res + MAX_NEGATIVE_AMPLITUDE];
      audioSamples[i] = (byte) res;
      audioSamples[i + 1] = (byte) (res >> 8);
    }
  }

  private static void precomputeVolumeNormLUT() {
    for (int s = 0; s < N_SHORTS; s++) {
      double v = s - MAX_NEGATIVE_AMPLITUDE;
      double sign = Math.signum(v);
      // Non-linear volume boost function
      // fitted exponential through (0,0), (10000, 25000), (32767, 32767)
      VOLUME_NORM_LUT[s] = (short) (sign
          * (1.240769e-22 - (-4.66022 / 0.0001408133) * (1 - Math.exp(-0.0001408133 * v * sign))));
    }
  }

}
