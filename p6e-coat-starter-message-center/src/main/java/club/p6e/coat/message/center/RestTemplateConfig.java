package club.p6e.coat.message.center;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * @author lidashuang
 * @version 1.0
 */
@Component
@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate injectRestTemplate() {
        return new RestTemplate();
    }

}
