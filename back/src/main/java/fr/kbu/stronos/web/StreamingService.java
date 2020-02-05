package fr.kbu.stronos.web;

import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.sound.sampled.LineUnavailableException;
import org.apache.catalina.connector.ClientAbortException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import fr.kbu.stronos.StronosApplication;
import fr.kbu.stronos.api.dto.CapturePeripheral;
import fr.kbu.stronos.api.dto.ServerInfoDto;
import fr.kbu.stronos.api.dto.StreamDto;
import fr.kbu.stronos.api.web.IStream;
import fr.kbu.stronos.audio.AudioLineReader;
import fr.kbu.stronos.audio.NoCaptureDeviceAvailable;
import fr.kbu.stronos.utils.ConfigurationManager;
import fr.kbu.stronos.utils.StartupMp3Stream;

@RestController
public class StreamingService implements IStream, WebMvcConfigurer {

  private static final Logger logger = LogManager.getLogger(StreamingService.class);

  @Override
  public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
    configurer.setDefaultTimeout((long) 1000 * 60 * 60 * 48); // (48 hours)
  }

  @ExceptionHandler(ClientAbortException.class)
  @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
  public Object exceptionHandler(ClientAbortException e, HttpServletRequest request) {
    return null;
  }

  @GetMapping(value = "", produces = "audio/mp3")
  public ResponseEntity<StreamingResponseBody> stream(HttpServletRequest request) {

    Mp3Stream stream = new Mp3Stream();
    stream.setIpAddress(request.getRemoteAddr());
    stream.setUserAgent(request.getHeader("User-Agent"));

    final HttpHeaders headers = new HttpHeaders();
    headers.setContentDispositionFormData("filename", "stream.mp3");

    return ResponseEntity.ok().headers(headers).contentType(MediaType.valueOf("audio/mp3"))
        .body(stream);

  }

  @Override
  public ServerInfoDto info() {
    ServerInfoDto response = new ServerInfoDto();

    AudioLineReader.get().getStreams().forEach(stream -> {
      if (!(stream instanceof StartupMp3Stream)) {
        StreamDto strDto = new StreamDto();
        strDto.setStreamedSeconds((long) stream.streamSince());
        strDto.setIpAdress(stream.getIpAddress());
        strDto.setUserAgent(stream.getUserAgent());
        response.getStreamList().add(strDto);
      }
    });
    response.setAwakeSince((System.currentTimeMillis() - StronosApplication.AWAKE_SINCE) / 1000);
    response.setWarmupComplete(StronosApplication.isWarmupComplete());
    response.setRecordingSupported(!AudioLineReader.get().getRecordingDeviceList().isEmpty());
    // response.setRecordingSupported(false);
    return response;
  }

  @Override
  public float setVolume(float volume) {
    logger.info("setVolume {}", volume);
    volume = AudioLineReader.get().adjustVolume(volume);
    ConfigurationManager.get().saveVolume(volume);
    return volume;
  }

  @Override
  public float getVolume() {
    return AudioLineReader.get().getVolume();
  }

  @Override
  public List<CapturePeripheral> getAvailableCaptureDevices() {
    List<String> allDevices = AudioLineReader.get().getRecordingDeviceList();
    String selectedOne = AudioLineReader.get().getCurrentRecordingDevice();
    List<CapturePeripheral> result = new ArrayList<>();
    for (String d : allDevices) {
      result.add(new CapturePeripheral(d, null, d.equals(selectedOne)));
    }
    return result;
  }

  @Override
  public Boolean setCaptureDevice(String name) {

    ConfigurationManager.get().saveRecordingDevice(name);

    if (!AudioLineReader.get().getCurrentRecordingDevice().equals(name)
        && AudioLineReader.get().getRecordingDeviceList().contains(name)) {

      AudioLineReader.get().stop(false);
      try {
        AudioLineReader.get().openAudioLine(name);
        StronosApplication.getThreadPool().submit(() -> AudioLineReader.get().read());
      } catch (NoCaptureDeviceAvailable | LineUnavailableException e) {
        logger.error("Cannot switch capturing device", e);
        return false;
      }

    } else {
      logger.info("This recording device is already selected");
    }
    return true;

  }

}
