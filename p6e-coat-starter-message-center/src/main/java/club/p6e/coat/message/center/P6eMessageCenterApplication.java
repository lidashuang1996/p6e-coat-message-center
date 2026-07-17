//package club.p6e.coat.message.center;
//
//import club.p6e.coat.common.utils.SpringUtil;
//import club.p6e.coat.message.center.launcher.LauncherStartingModel;
//import org.springframework.boot.SpringApplication;
//import org.springframework.boot.autoconfigure.SpringBootApplication;
//
//import java.io.File;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//@SpringBootApplication
//public class P6eMessageCenterApplication {
//
//    public static void main(String[] args) {
//        SpringApplication.run(P6eMessageCenterApplication.class, args);
//        SpringUtil.getBean(MessageCenterService.class).execute(new LauncherStartingModel() {
//
//            @Override
//            public Integer id() {
//                return 30008;
//            }
//
//            @Override
//            public String language() {
//                return "zh-hk";
//            }
//
//            @Override
//            public Map<String, String> param() {
//                return new HashMap<>() {{
//                    put("chat", "that");
//                }};
//            }
//
//            @Override
//            public List<String> recipients() {
//                return List.of();
//            }
//
//            @Override
//            public List<File> attachment() {
//                return new ArrayList<>() {{
//                    add(new File("C:\\Users\\a1294\\Pictures\\1.jpg"));
//                }};
//            }
//
//        });
//    }
//}
