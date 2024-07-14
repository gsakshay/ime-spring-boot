package ime.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ime.model.MIME;
import ime.model.MIMEImp;
import ime.service.ImageIOLoader;
import ime.service.ImageIOSaver;
import ime.service.ImageLoader;
import ime.service.ImageSaver;

@Configuration
public class AppConfig {

  @Bean
  public MIME mimeModel() {
    return new MIMEImp();
  }

  @Bean
  public ImageLoader imageLoader() {
    return new ImageIOLoader();
  }

  @Bean
  public ImageSaver imageSaver() {
    return new ImageIOSaver();
  }
}
