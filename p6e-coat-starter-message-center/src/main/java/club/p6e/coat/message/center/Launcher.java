package club.p6e.coat.message.center;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * @author lidashuang
 * @version 1.0
 */
public interface Launcher {

    List<String> push(Integer id, String language, List<String> recipients, Map<String, String> data, List<File> attachments);

}
