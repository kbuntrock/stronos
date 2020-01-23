package fr.kbu.stronos.api.swagger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.reflections.Reflections;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.WebApplicationContext;

/**
 *
 * @author : Kevin Buntrock
 */
@SpringBootApplication
@ComponentScan("fr.kbu")
@SuppressWarnings("rawtypes")
public class SwaggerApplication {

  private static Logger LOGGER = LogManager.getLogger(SwaggerApplication.class);

  public static void main(String[] args) throws Exception {
    LOGGER.info("Starting Swagger gen App");
    ConfigurableApplicationContext context = SpringApplication.run(SwaggerApplication.class, args);

    Class[] classArray = new Reflections("fr.kbu").getTypesAnnotatedWith(RestController.class)
        .toArray(new Class[0]);
    LOGGER.info("Found {} classes to document.", classArray.length);
    for (Class clazz : classArray) {
      LOGGER.info("- {}", clazz.getSimpleName());
    }

    // Request APO
    MockMvc mockMvc = MockMvcBuilders.webAppContextSetup((WebApplicationContext) context).build();
    mockMvc.perform(MockMvcRequestBuilders.get("/v2/api-docs").accept(MediaType.APPLICATION_JSON))
        .andDo((result) -> {
          if (args.length > 1) {
            writeSwaggerFile(args[0], args[1], result.getResponse().getContentAsString());
          } else {
            System.out.println(result.getResponse().getContentAsString());
          }

        });

    SpringApplication.exit(context);
    LOGGER.info("Stopping Swagger gen App");
  }

  private static void writeSwaggerFile(final String destination, final String fileName,
      final String content) throws IOException {
    File destinationDirectory = new File(destination);
    FileUtils.deleteDirectory(destinationDirectory);
    destinationDirectory.mkdirs();
    File apiFile = new File(destinationDirectory, fileName);

    FileWriter fileWriter = new FileWriter(apiFile);
    PrintWriter printWriter = new PrintWriter(fileWriter);
    printWriter.print(content);
    printWriter.close();

  }
}
