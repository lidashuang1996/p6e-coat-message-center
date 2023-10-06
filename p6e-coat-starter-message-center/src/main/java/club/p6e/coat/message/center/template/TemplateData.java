package club.p6e.coat.message.center.template;

import java.util.List;
import java.util.Map;

/**
 * 模板数据
 *
 * @author lidashuang
 * @version 1.0
 */
public interface TemplateData {

    /**
     * 获取 ID
     *
     * @return ID
     */
    public Integer id();

    /**
     * 获取类型
     *
     * @return 类型
     */
    public String type();

    /**
     * 获取标记
     *
     * @return 标记
     */
    public String mark();

    /**
     * 获取名称
     *
     * @return 名称
     */
    public String name();

    /**
     * 获取标题
     *
     * @return 标题
     */
    public String title();

    /**
     * 获取标题
     *
     * @return 标题
     */
    public String content();

    /**
     * 获取附件
     *
     * @return 附件
     */
    public List<String> attachments();

    /**
     * 获取变量
     *
     * @return 变量
     */
    public Map<String, String> variable();

}
