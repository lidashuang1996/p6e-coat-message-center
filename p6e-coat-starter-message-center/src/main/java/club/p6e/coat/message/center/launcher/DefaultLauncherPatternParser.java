package club.p6e.coat.message.center.launcher;

import club.p6e.coat.message.center.DataSourceFactory;
import club.p6e.coat.message.center.config.ConfigSource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 发射模式解析器（默认）
 *
 * @author lidashuang
 * @version 1.0
 */
@Component
@ConditionalOnMissingBean(
        value = LauncherPatternParser.class,
        ignored = DefaultLauncherPatternParser.class
)
public class DefaultLauncherPatternParser implements LauncherPatternParser {

    /**
     * 默认模式
     */
    private static final String DEFAULT_PATTERN = "DEFAULT";

    /**
     * 轮询模式
     */
    private static final String POLLING_PATTERN = "POLLING";

    /**
     * 加权轮询模式
     */
    private static final String WEIGHT_POLLING_PATTERN = "WEIGHT_POLLING";

    /**
     * 随机模式
     */
    private static final String RANDOM_PATTERN = "RANDOM";

    /**
     * 加权随机模式
     */
    private static final String WEIGHT_RANDOM_PATTERN = "WEIGHT_RANDOM";

    /**
     * 计数器缓存
     */
    private static final Map<String, AtomicInteger> COUNTER = new ConcurrentHashMap<>();

    /**
     * 数据源工厂对象
     */
    private final DataSourceFactory dataSourceFactory;

    /**
     * 外部数据源解析器
     */
    private final ExternalSourceLauncherPatternParser externalSourceLauncherPatternParser = new ExternalSourceLauncherPatternParser();

    /**
     * 构造方法初始化
     *
     * @param dataSourceFactory 数据源工厂对象
     */
    public DefaultLauncherPatternParser(DataSourceFactory dataSourceFactory) {
        this.dataSourceFactory = dataSourceFactory;
    }

    @Override
    public ConfigSource execute(LauncherSource launcherSource) {
        if (launcherSource == null) {
            throw new NullPointerException("launcher source is null");
        }
        if (launcherSource.id() == null) {
            throw new NullPointerException("launcher source parameters [id] is null");
        }
        if (launcherSource.pattern() == null) {
            throw new NullPointerException("launcher source parameters [pattern] is null");
        }
        final int id = launcherSource.id();
        final String pattern = launcherSource.pattern();
        final List<LauncherMapperSource> mappers = dataSourceFactory.getLauncherMapperSourceList(id);
        final List<ConfigSource> configs = mappers.stream().map(i ->
                dataSourceFactory.getConfigSource(i.cid(), i.attribute())).filter(i -> i.enable() > 0).toList();
        if (!configs.isEmpty()) {
            return switch (pattern) {
                case DEFAULT_PATTERN, POLLING_PATTERN -> {
                    final AtomicInteger pAtomicInteger = COUNTER.computeIfAbsent(
                            String.valueOf(id), k -> new AtomicInteger(0));
                    yield configs.get(pAtomicInteger.getAndIncrement() % configs.size());
                }
                case WEIGHT_POLLING_PATTERN -> {
                    final AtomicInteger wpAtomicInteger = COUNTER.computeIfAbsent(
                            String.valueOf(id), k -> new AtomicInteger(0));
                    yield getIndexConfigSource(configs, wpAtomicInteger.getAndIncrement());
                }
                case RANDOM_PATTERN -> configs.get(ThreadLocalRandom.current().nextInt(0, configs.size()));
                case WEIGHT_RANDOM_PATTERN -> {
                    int wrCount = 0;
                    for (final ConfigSource config : configs) {
                        int weight = 100;
                        try {
                            weight = Integer.parseInt(config.attribute());
                        } catch (Exception e) {
                            // ...
                        }
                        wrCount += weight;
                    }
                    yield getIndexConfigSource(
                            configs,
                            ThreadLocalRandom.current().nextInt(0, wrCount)
                    );
                }
                default -> externalSourceLauncherPatternParser.execute(launcherSource);
            };
        }
        throw new RuntimeException("launcher does not have a corresponding available config");
    }

    /**
     * 通过索引获取当前的配置源对象
     *
     * @param configs 配置源列表
     * @param index   索引
     * @return 配置源对象
     */
    private ConfigSource getIndexConfigSource(List<ConfigSource> configs, int index) {
        int count = 0;
        for (final ConfigSource config : configs) {
            int weight = 0;
            try {
                weight = Integer.parseInt(config.attribute());
            } catch (Exception e) {
                // ...
            }
            count += weight;
            if (index < count) {
                return config;
            }
        }
        return configs.get(0);
    }

}
