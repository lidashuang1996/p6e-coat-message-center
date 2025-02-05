package club.p6e.coat.message.center.service.impl;

import club.p6e.coat.message.center.model.ConfigModel;
import club.p6e.coat.message.center.model.MailMessageConfigModel;
import club.p6e.coat.message.center.service.WeChatMessageConfigParserService;
import org.springframework.stereotype.Component;

/**
 * @author lidashuang
 * @version 1.0
 */
@Component
public class WeChatMessageConfigParserServiceImpl implements WeChatMessageConfigParserService {

    @Override
    public String name() {
        return "";
    }

    @Override
    public MailMessageConfigModel execute(ConfigModel cm) {
        return null;
    }

}
