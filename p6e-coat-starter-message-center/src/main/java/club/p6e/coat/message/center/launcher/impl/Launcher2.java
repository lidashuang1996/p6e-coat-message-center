//package club.p6e.coat.message.center.launcher;
//
//import club.p6e.coat.message.center.DataSourceFactory;
//import club.p6e.coat.message.center.config.model.ConfigModel;
//import club.p6e.coat.message.center.config.model.MailMessageConfigModel;
//import club.p6e.coat.message.center.config.model.MobileMessageConfigModel;
//import club.p6e.coat.message.center.config.model.ShortMessageConfigModel;
//import club.p6e.coat.message.center.config.parser.ConfigParser;
//import club.p6e.coat.message.center.template.TemplateModel;
//import club.p6e.coat.message.center.template.TemplateParser;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
//import org.springframework.stereotype.Component;
//
//import java.util.List;
//import java.util.Map;
//
///**
// * 发射器
// *
// * @author lidashuang
// * @version 1.0
// */
//@Component
//@ConditionalOnMissingBean(
//        value = Launcher.class,
//        ignored = Launcher.class
//)
//public class Launcher {
//
//    /**
//     * MMS 类型
//     */
//    private static final String MMS_TYPE = "MMS";
//
//    /**
//     * SMS 类型
//     */
//    private static final String SMS_TYPE = "SMS";
//
//    /**
//     * MAIL 类型
//     */
//    private static final String MAIL_TYPE = "MAIL";
//
//    /**
//     * 配置解析器对象
//     */
//    private final ConfigParser configParser;
//
//    /**
//     * 模板解析器对象
//     */
//    private final TemplateParser templateParser;
//
//    /**
//     * 数据源工厂对象
//     */
//    private final DataSourceFactory dataSourceFactory;
//
//    /**
//     * 发射器模式解析器对象
//     */
//    private final LauncherPatternParser launcherPatternParser;
//
//    /**
//     * 邮件发射解析器对象
//     */
//    private final MailLauncherParser mailLauncherParser;
//
//    /**
//     * 短信发射解析器对象
//     */
//    private final ShortMessageLauncherParser shortMessageLauncherParser;
//
//    /**
//     * 移动消息发射解析器对象
//     */
//    private final MobileMessageLauncherParser mobileMessageLauncherParser;
//
//    /**
//     * 构造方法初始化
//     *
//     * @param configParser                配置解析器对象
//     * @param templateParser              模板解析器对象
//     * @param dataSourceFactory           数据源工厂对象
//     * @param mailLauncherParser          邮件发射解析器对象
//     * @param launcherPatternParser       发射器模式解析器对象
//     * @param shortMessageLauncherParser  短信发射解析器对象
//     * @param mobileMessageLauncherParser 移动消息发射解析器对象
//     */
//    public Launcher(
//            ConfigParser configParser,
//            TemplateParser templateParser,
//            DataSourceFactory dataSourceFactory,
//            MailLauncherParser mailLauncherParser,
//            LauncherPatternParser launcherPatternParser,
//            ShortMessageLauncherParser shortMessageLauncherParser,
//            MobileMessageLauncherParser mobileMessageLauncherParser
//    ) {
//        this.configParser = configParser;
//        this.templateParser = templateParser;
//        this.dataSourceFactory = dataSourceFactory;
//        this.mailLauncherParser = mailLauncherParser;
//        this.launcherPatternParser = launcherPatternParser;
//        this.shortMessageLauncherParser = shortMessageLauncherParser;
//        this.mobileMessageLauncherParser = mobileMessageLauncherParser;
//    }
//
//    /**
//     * 发送消息
//     *
//     * @param mark       标记
//     * @param language   语言
//     * @param data       参数对象
//     * @param recipients 收件人
//     * @return 发射器回执数据对象
//     */
//    public LauncherData execute(String mark, String language, Map<String, String> data, List<String> recipients) {
//        return execute(dataSourceFactory.getLauncherSource(mark, language), data, recipients);
//    }
//
//    /**
//     * 发送消息
//     *
//     * @param source     发射器源对象
//     * @param data       参数对象
//     * @param recipients 收件人
//     * @return 发射器回执数据对象
//     */
//    private LauncherData execute(LauncherSource source, Map<String, String> data, List<String> recipients) {
//        if (source == null) {
//            throw new NullPointerException("launcher source is null");
//        }
//        final TemplateModel templateData = template(source, data);
//        final ConfigModel configData = configParser.execute(launcherPatternParser.execute(source));
//        if (source.type().equalsIgnoreCase(templateData.type())
//                && source.type().equalsIgnoreCase(configData.type())) {
//            if (MAIL_TYPE.equalsIgnoreCase(configData.type())) {
//                return mailLauncherParser.execute((MailMessageConfigModel) configData, templateData, recipients);
//            }
//            if (SMS_TYPE.equalsIgnoreCase(configData.type())) {
//                return shortMessageLauncherParser.execute((ShortMessageConfigModel) configData, templateData, recipients);
//            }
//            if (MMS_TYPE.equalsIgnoreCase(configData.type())) {
//                return mobileMessageLauncherParser.execute((MobileMessageConfigModel) configData, templateData, recipients);
//            }
//        }
//        throw new RuntimeException(
//                source.type() + "/" + templateData.type() + "/" + configData.type(),
//                new Throwable("launcher type and template type and config type mismatch exception")
//        );
//    }
//
//    /**
//     * 获取模板对象
//     *
//     * @param source 发射器源对象
//     * @param data   参数对象
//     * @return 模板数据对象
//     */
//    private TemplateModel template(LauncherSource source, Map<String, String> data) {
//        if (source == null) {
//            throw new NullPointerException("launcher source is null");
//        }
//        if (source.template() == null) {
//            throw new NullPointerException("launcher source parameters [template] is null");
//        }
//        final TemplateModel templateSource = dataSourceFactory.getTemplateSource(source.template());
//        if (templateSource == null) {
//            throw new NullPointerException("template source is null");
//        }
//        return templateParser.execute(templateSource, new BasicVariableParser(data));
//    }
//
//}
