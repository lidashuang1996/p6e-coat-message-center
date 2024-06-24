package club.p6e.coat.message.center.service.impl;

import club.p6e.coat.common.utils.JsonUtil;
import club.p6e.coat.common.utils.SnowflakeIdUtil;
import club.p6e.coat.message.center.SnowflakeId;
import club.p6e.coat.message.center.model.TemplateMessageModel;
import club.p6e.coat.message.center.repository.DataSourceRepository;
import club.p6e.coat.message.center.service.LogService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 日志服务
 *
 * @author lidashuang
 * @version 1.0
 */
@Component
@ConditionalOnMissingBean(
        value = LogServiceImpl.class,
        ignored = LogServiceImpl.class
)
public class LogServiceImpl implements LogService {

    /**
     * 雪花算法对象
     */
    private final SnowflakeId snowflakeId;

    /**
     * 数据源存储库对象
     */
    private final DataSourceRepository repository;

    /**
     * 构造方法初始化
     *
     * @param repository  数据源存储库对象
     * @param snowflakeId 雪花算法对象
     */
    public LogServiceImpl(SnowflakeId snowflakeId, DataSourceRepository repository) {
        this.repository = repository;
        this.snowflakeId = snowflakeId;
    }

    @Override
    public Map<String, List<String>> create(List<String> recipients, TemplateMessageModel message) {
        final LocalDateTime now = LocalDateTime.now();
        final Map<String, List<String>> result = new HashMap<>(16);
        final String parent = String.valueOf(snowflakeId.generate());
        final int config = Integer.parseInt(message.getLogData().get("config"));
        final int template = Integer.parseInt(message.getLogData().get("template"));
        final int launcher = Integer.parseInt(message.getLogData().get("launcher"));
        final Map<String, String> params = message.getMessageParam();
        if (message.getLogData().get("attachment") != null) {
            params.put("__attachment__", message.getLogData().get("attachment"));
        }
        if (repository.createLog(parent, null, JsonUtil.toJson(params), launcher, template, config, now)) {
            result.put(parent, new ArrayList<>());
        }
        for (final String recipient : recipients) {
            params.put("__recipient__", recipient);
            final String no = String.valueOf(SnowflakeIdUtil.getInstance(SnowflakeId.MESSAGE_CENTER_LOG_SNOWFLAKE_NAME).nextId());
            if (repository.createLog(no, parent, JsonUtil.toJson(params), launcher, template, config, now)) {
                result.get(parent).add(no);
            }
        }
        return result;
    }

    @Override
    public void update(Map<String, List<String>> list, String result) {
        final LocalDateTime now = LocalDateTime.now();
        for (final Map.Entry<String, List<String>> entry : list.entrySet()) {
            repository.updateLog(entry.getKey(), result, now);
            for (final String item : entry.getValue()) {
                repository.updateLog(item, result, now);
            }
        }
    }

}
