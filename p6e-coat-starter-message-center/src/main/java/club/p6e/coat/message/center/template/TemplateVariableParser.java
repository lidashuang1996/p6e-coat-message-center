package club.p6e.coat.message.center.template;

import org.springframework.core.Ordered;

/**
 * 变量解析器
 *
 * @author lidashuang
 * @version 1.0
 */
public interface TemplateVariableParser extends Ordered {

    /**
     * 执行解析变量
     *
     * @param key 变量的名称
     * @return 解析的值
     */
    String execute(String key);

}
