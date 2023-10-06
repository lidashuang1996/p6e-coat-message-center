package club.p6e.coat.message.center.config;

import club.p6e.coat.message.center.ExternalSourceClassLoader;

/**
 * 外部数据源配置解析器
 *
 * @author lidashuang
 * @version 1.0
 */
public class ExternalSourceConfigParser implements ConfigParser {

    @Override
    public ConfigData execute(ConfigSource configSource) {
        if (configSource != null
                && configSource.parser() != null
                && configSource.parserSource() != null) {
            final ExternalSourceClassLoader loader = ExternalSourceClassLoader.getInstance();
            final ConfigParser externalSourceConfigParser = loader.newClassInstance(
                    configSource.parser(),
                    configSource.parserSource(),
                    ConfigParser.class
            );
            externalSourceConfigParser.execute(configSource);
        }
        throw new NullPointerException("config source parameters [parser/source] is null");
    }
}
