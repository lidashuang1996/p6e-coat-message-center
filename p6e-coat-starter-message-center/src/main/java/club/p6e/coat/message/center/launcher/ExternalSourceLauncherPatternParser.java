package club.p6e.coat.message.center.launcher;

import club.p6e.coat.message.center.ExternalSourceClassLoader;
import club.p6e.coat.message.center.config.ConfigSource;

/**
 * 外部数据源发射模式解析器
 *
 * @author lidashuang
 * @version 1.0
 */
public class ExternalSourceLauncherPatternParser implements LauncherPatternParser {

    @Override
    public ConfigSource execute(LauncherSource launcherSource) {
        if (launcherSource != null
                && launcherSource.pattern() != null
                && launcherSource.patternSource() != null) {
            final ExternalSourceClassLoader loader = ExternalSourceClassLoader.getInstance();
            final LauncherPatternParser externalSourceLauncherPatternParser = loader.newClassInstance(
                    launcherSource.pattern(),
                    launcherSource.patternSource(),
                    LauncherPatternParser.class
            );
            externalSourceLauncherPatternParser.execute(launcherSource);
        }
        throw new NullPointerException("launcher source parameters [pattern/source] is null");
    }
}
