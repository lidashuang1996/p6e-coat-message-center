package club.p6e.coat.message.center;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * @author lidashuang
 * @version 1.0
 */
@Data
@Component
@ConfigurationProperties(prefix = "p6e.cloud.index")
public class Properties implements Serializable {

    private Mail mail = new Mail();

    @Data
    public static class Mail implements Serializable {
        private Attachment attachment;
        private Integer maxRecipientLength = 20;
    }

    @Data
    public static class Attachment implements Serializable {
        private String path;
    }



}
