package club.p6e.coat.message.center.service;

import org.springframework.core.Ordered;

/**
 * 模板变量解析器服务
 *
 * @author lidashuang
 * @version 1.0
 */
public interface TemplateVariableParserService extends Ordered {

    /**
     * 执行解析变量
     *
     * @param key      变量的名称
     * @param language 环境的语言
     * @return 解析的值
     */
    String execute(String key, String language);

}
