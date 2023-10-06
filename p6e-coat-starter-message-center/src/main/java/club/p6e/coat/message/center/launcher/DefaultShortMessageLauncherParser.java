package club.p6e.coat.message.center.launcher;

import club.p6e.coat.message.center.MessageCenterThreadPool;
import club.p6e.coat.message.center.config.ShortMessageConfigData;
import club.p6e.coat.message.center.template.TemplateData;
import club.p6e.coat.message.center.utils.GeneratorUtil;
import club.p6e.coat.message.center.utils.JsonUtil;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lidashuang
 * @version 1.0
 */
@Component
@ConditionalOnMissingBean(
        value = ShortMessageLauncherParser.class,
        ignored = DefaultShortMessageLauncherParser.class
)
public class DefaultShortMessageLauncherParser implements ShortMessageLauncherParser {

    /**
     * 最多的收件人长度
     */
    private static final int MAX_RECIPIENT_LENGTH = 20;

    /**
     * 注入日志对象
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultShortMessageLauncherParser.class);

    /**
     * Rest Template 对象
     */
    private final RestTemplate restTemplate;

    /**
     * 消息中心线程池对象
     */
    private final MessageCenterThreadPool threadPool;

    /**
     * 构造方法初始化
     *
     * @param restTemplate REST TEMPLATE 对象
     * @param threadPool   消息中心线程池对象
     */
    public DefaultShortMessageLauncherParser(RestTemplate restTemplate, MessageCenterThreadPool threadPool) {
        this.threadPool = threadPool;
        this.restTemplate = restTemplate;
    }

    @Override
    public LauncherData execute(ShortMessageConfigData configData, TemplateData templateData, List<String> recipients) {
        if (configData == null) {
            throw new NullPointerException(
                    "when performing the send SMS operation, it was found config data is null");
        }
        if (templateData == null) {
            throw new NullPointerException(
                    "when performing the send SMS operation, it was found template data is null");
        }
        if (recipients == null) {
            throw new NullPointerException(
                    "when performing the send SMS operation, it was found recipients is null");
        }
        if (templateData.title() == null) {
            throw new NullPointerException(
                    "when performing the send SMS operation, it was found that the title in template data is null");
        }
        // submit send mail task
        final int size = recipients.size();
        final int stride = MAX_RECIPIENT_LENGTH;
        for (int i = 0; i < size; i = i + stride) {
            final List<String> rs = recipients.subList(i, i + stride);
            threadPool.submit(() -> send(rs, configData, templateData));
        }
        return new LauncherData() {
            @Override
            public String id() {
                return GeneratorUtil.uuid();
            }

            @Override
            public String type() {
                return configData.type();
            }
        };
    }


    /**
     * 发送短信
     *
     * @param recipients 收件人
     * @param config     配置对象
     * @param template   模板对象
     */
    private void send(List<String> recipients, ShortMessageConfigData config, TemplateData template) {
        // SMS CODE
        final String title = template.title();
        // SMS PARAMS
        final Map<String, String> variable = template.variable();
        // Sending SMS messages is typically implemented by sending HTTP requests
        // SDK implementation also exists
        // Now use HTTP requests as a demonstration implementation
        // SMS CONFIG URL
        final String url = config.applicationUrl();
        // SMS CONFIG ID
        final String id = config.applicationId();
        // SMS CONFIG KEY
        final String key = config.applicationKey();
        // SMS CONFIG SECRET
        final String secret = config.applicationSecret();
        // HTTP PARAMS
        final Map<String, Object> data = new HashMap<>(5);
        data.put("id", id);
        data.put("key", key);
        data.put("secret", secret);
        data.put("template", title);
        data.put("recipients", recipients);
        data.put("params", variable == null ? new HashMap<>(0) : variable);
        // HTTP HEADER
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        final HttpEntity<String> entity = new HttpEntity<>(JsonUtil.toJson(data), headers);
        final ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
        if (response.getStatusCode().is2xxSuccessful()) {
            try {
                final String rc = response.getBody();
                final ResultModel rm = JsonUtil.fromJson(rc, ResultModel.class);
                if (rm != null && rm.getCode() == 0) {
                    LOGGER.info(rc);
                } else {
                    LOGGER.error("[SMS HTTP ERROR RESULT] >>> " + rc);
                }
            } catch (Exception e) {
                LOGGER.error("[SMS HTTP ERROR RESULT]", e);
            }
        } else {
            LOGGER.error("[SMS HTTP ERROR RESULT] >>> " + response.getBody());
        }
    }

    @Data
    private static class ResultModel {
        private Integer code;
        private String message;
    }

}
