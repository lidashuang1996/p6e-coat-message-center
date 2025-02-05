package club.p6e.coat.message.center.service;

import org.springframework.core.Ordered;

/**
 * TemplateVariableParserService
 *
 * @author lidashuang
 * @version 1.0
 */
public interface TemplateVariableParserService extends Ordered {

    /**
     * Execute Template Variable Parser
     *
     * @param key      Key
     * @param language Language
     * @return Result Value
     */
    String execute(String key, String language);

}
