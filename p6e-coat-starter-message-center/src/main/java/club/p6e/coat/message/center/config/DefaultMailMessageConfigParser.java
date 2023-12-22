package club.p6e.coat.message.center.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

/**
 * 配置解析器（默认）
 *
 * @author lidashuang
 * @version 1.0
 */
@Component
@ConditionalOnMissingBean(
        value = MailMessageConfigParser.class,
        ignored = DefaultMailMessageConfigParser.class
)
public abstract class DefaultMailMessageConfigParser extends AbstractConfigParser implements MailMessageConfigParser {

    @Override
    public MailMessageConfigModel execute(ConfigModel config) {
        return null;
    }

}
