package club.p6e.coat.message.center.config;

import club.p6e.coat.message.center.DataSourceFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author lidashuang
 * @version 1.0
 */
@Component
@ConditionalOnMissingBean(
        value = ConfigFactory.class,
        ignored = ConfigFactory.class
)
public class ConfigFactory {

    /**
     * 数据源工厂对象
     */
    private final DataSourceFactory dataSourceFactory;

    /**
     * 邮件消息配置解析器
     */
    private final MailMessageConfigParser mailMessageConfigParser;

    /**
     * 短信消息配置解析器
     */
    private final ShortMessageConfigParser shortMessageConfigParser;

    /**
     * 移动消息配置解析器
     */
    private final MobileMessageConfigParser mobileMessageConfigParser;

    /**
     * 缓存对象
     */
    private final Map<String, ConfigModel> CACHE = new ConcurrentHashMap<>();

    /**
     * 构造方法初始化
     *
     * @param dataSourceFactory         数据源工厂对象
     * @param mailMessageConfigParser   邮件消息配置解析器
     * @param shortMessageConfigParser  短信消息配置解析器
     * @param mobileMessageConfigParser 移动消息配置解析器
     */
    public ConfigFactory(
            DataSourceFactory dataSourceFactory,
            MailMessageConfigParser mailMessageConfigParser,
            ShortMessageConfigParser shortMessageConfigParser,
            MobileMessageConfigParser mobileMessageConfigParser
    ) {
        this.dataSourceFactory = dataSourceFactory;
        this.mailMessageConfigParser = mailMessageConfigParser;
        this.mobileMessageConfigParser = mobileMessageConfigParser;
        this.shortMessageConfigParser = shortMessageConfigParser;
    }

    /**
     * 执行读取数据
     *
     * @param id 配置模型对象的 ID
     * @return 配置模型对象
     */
    public ConfigModel executeReadData(Integer id) {
        ConfigModel result = executeReadCache(id);
        if (result == null) {
            result = executeReadDatabase(id);
            if (result != null) {
                CACHE.put(String.valueOf(id), result);
            }
        }
        return result;
    }

    /**
     * 执行读取缓存数据
     *
     * @param id 配置模型对象的 ID
     * @return 配置模型对象
     */
    public ConfigModel executeReadCache(Integer id) {
        return CACHE.get(String.valueOf(id));
    }

    /**
     * 执行读取数据库数据
     *
     * @param id 配置模型对象的 ID
     * @return 配置模型对象
     */
    public ConfigModel executeReadDatabase(Integer id) {
        return CACHE.get(String.valueOf(id));
    }

    /**
     * 根据 ID 读取配置模型对象
     *
     * @param id 配置模型对象的 ID
     * @return 配置模型对象
     */
    public ConfigModel getConfigModel(Integer id) {
        final ConfigModel model = executeReadData(id);
        if (model == null || model.type() == null) {
            return null;
        } else {
            return switch (model.type()) {
                case SMS -> shortMessageConfigParser.execute(model);
                case MAIL -> mailMessageConfigParser.execute(model);
                case MOBILE -> mobileMessageConfigParser.execute(model);
            };
        }
    }

    public MailMessageConfigModel getMailMessageConfigModel(Integer id) {
        final ConfigModel model = getConfigModel(id);
        if (model instanceof MailMessageConfigModel) {
            return (MailMessageConfigModel) model;
        } else {
            return null;
        }
    }

    public MobileMessageConfigModel getMobileMessageConfigModel(Integer id) {
        final ConfigModel model = getConfigModel(id);
        if (model instanceof MobileMessageConfigModel) {
            return (MobileMessageConfigModel) model;
        } else {
            return null;
        }
    }

    public ShortMessageConfigModel getShortMessageConfigModel(Integer id) {
        final ConfigModel model = getConfigModel(id);
        if (model instanceof ShortMessageConfigModel) {
            return (ShortMessageConfigModel) model;
        } else {
            return null;
        }
    }

}
