package club.p6e.coat.message.center.template;

/**
 * TemplateParserService
 *
 * @author lidashuang
 * @version 1.0
 */
public interface TemplateParserService<P, L> {

    /**
     * Get Name
     *
     * @return Name
     */
    String name();

    /**
     * Execute Param Model/Template Model Convert To Launcher Model
     *
     * @param param    Param Model
     * @param template Template Model
     * @return Launcher Model
     */
    L execute(P param, TemplateModel template);

}
