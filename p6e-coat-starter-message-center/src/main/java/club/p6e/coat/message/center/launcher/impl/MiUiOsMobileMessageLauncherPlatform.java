//package club.p6e.coat.message.center.launcher;
//
//import club.p6e.coat.message.center.config.MobileMessageConfigModel;
//import club.p6e.coat.message.center.template.TemplateModel;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
//import org.springframework.stereotype.Component;
//
//import java.util.List;
//import java.util.function.Function;
//
///**
// * MiUiOS
// * 移动消息发送平台
// *<a href="https://dev.mi.com/distribute/doc/details?pId=1558">...</a>
// * @author lidashuang
// * @version 1.0
// */
//@Component
//@ConditionalOnMissingBean(
//        value = MiUiOsMobileMessageLauncherPlatform.class,
//        ignored = MiUiOsMobileMessageLauncherPlatform.class
//)
//public class MiUiOsMobileMessageLauncherPlatform implements MobileMessageLauncherPlatform {
//
//    @Override
//    public String name() {
//        return null;
//    }
//
//    @Override
//    public List<Function<Void, String>> execute(MobileMessageConfigModel config, TemplateModel template, List<String> recipients) {
//        return List.of(u -> null, u -> null);
//    }
//
//}
