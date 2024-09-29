package club.p6e.coat.message.center.service;

import club.p6e.coat.message.center.model.ConfigModel;
import club.p6e.coat.message.center.model.TelegramMessageConfigModel;

/**
 * @author lidashuang
 * @version 1.0
 */
public interface TelegramMessageConfigParserService extends ConfigParserService {

    @Override
    TelegramMessageConfigModel execute(ConfigModel config);

}
