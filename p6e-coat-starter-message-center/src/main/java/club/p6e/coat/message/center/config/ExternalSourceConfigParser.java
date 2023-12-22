//package club.p6e.coat.message.center.config;
//
//import club.p6e.coat.message.center.ExternalSourceClassLoader;
//import club.p6e.coat.message.center.config.model.ConfigModel;
//import club.p6e.coat.message.center.config.parser.ConfigParser;
//
///**
// * 外部数据源配置解析器
// *
// * @author lidashuang
// * @version 1.0
// */
//public class ExternalSourceConfigParser implements ConfigParser {
//
//    @Override
//    public ConfigModel execute(ConfigModel model) {
//        if (model != null && model.parser() != null && model.parserSource() != null) {
//            final ExternalSourceClassLoader loader = ExternalSourceClassLoader.getInstance();
//            return loader.newClassInstance(model.parser(), model.parserSource(), ConfigParser.class).execute(model);
//        }
//        return new DefaultConfigParser();
//    }
//}
