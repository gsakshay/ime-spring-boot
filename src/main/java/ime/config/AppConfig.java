package ime.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ime.model.MIME;
import ime.model.MIMEImp;

@Configuration
public class AppConfig {

  @Bean
  public MIME mimeModel() {
    return new MIMEImp();
  }
}
