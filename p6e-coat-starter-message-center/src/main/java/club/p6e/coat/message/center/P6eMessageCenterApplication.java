package club.p6e.coat.message.center;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

import java.util.List;

@ServletComponentScan
@SpringBootApplication
public class P6eMessageCenterApplication {

    public static void main(String[] args) {
        // 邮件测试
//        SpringApplication
//                .run(P6eMessageCenterApplication.class, args)
//                .getBean(MessageCenterLauncher.class)
//                .execute("sys.mark.aaa", List.of("1294935733@qq.com"));

        SpringApplication
                .run(P6eMessageCenterApplication.class, args);
    }

}
