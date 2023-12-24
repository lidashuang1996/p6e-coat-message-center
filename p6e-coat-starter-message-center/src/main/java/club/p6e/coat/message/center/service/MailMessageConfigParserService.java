package club.p6e.coat.message.center.service;

import club.p6e.coat.message.center.model.ConfigModel;
import club.p6e.coat.message.center.model.MailMessageConfigModel;

/**
 * @author lidashuang
 * @version 1.0
 */
public interface MailMessageConfigParserService extends ConfigParserService {

    public MailMessageConfigModel execute(ConfigModel config);

}
