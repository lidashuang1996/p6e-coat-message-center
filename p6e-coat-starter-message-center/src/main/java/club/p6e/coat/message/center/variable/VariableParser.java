package club.p6e.coat.message.center.variable;

/**
 * 变量解析器
 *
 * @author lidashuang
 * @version 1.0
 */
public interface VariableParser {

    /**
     * 获取排序
     *
     * @return 排序序号
     */
    public int order();

    /**
     * 执行解析变量
     *
     * @param key 变量的名称
     * @return 变量的值
     */
    public String execute(String key);

}
