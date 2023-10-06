package club.p6e.coat.message.center;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

import java.util.List;

@ServletComponentScan
@SpringBootApplication
public class P6eMessageCenterApplication {

    public static void main(String[] args) {
        SpringApplication
                .run(P6eMessageCenterApplication.class, args)
                .getBean(MessageCenterLauncher.class)
                .execute("aaa", List.of("1294935733@qq.com"));
    }

}
