package club.p6e.coat.message.center.repository;

import club.p6e.coat.message.center.MessageType;
import club.p6e.coat.message.center.model.ConfigModel;
import club.p6e.coat.message.center.model.LauncherModel;
import club.p6e.coat.message.center.model.TemplateModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author lidashuang
 * @version 1.0
 */
@Component
@ConditionalOnMissingBean(
        value = DataSourceRepository.class,
        ignored = DataSourceRepository.class
)
public class DataSourceRepository {

    /**
     * 注入日志对象
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(DataSourceRepository.class);

    /**
     * 配置的查询 SQL
     */
    @SuppressWarnings("ALL")
    private static final String QUERY_ID_CONFIG_SOURCE = "" +
            "SELECT\n" +
            "  \"mcc\".\"id\" AS \"id\",  " +
            "  \"mcc\".\"rule\" AS \"rule\",  " +
            "  \"mcc\".\"type\" AS \"type\",  " +
            "  \"mcc\".\"enable\" AS \"enable\",  " +
            "  \"mcc\".\"name\" AS \"name\",  " +
            "  \"mcc\".\"content\" AS \"content\",  " +
            "  \"mcc\".\"description\" AS \"description\",  " +
            "  \"mcc\".\"parser\" AS \"parser\",  " +
            "  \"mcc\".\"parser_source\" AS \"parser_source\"   " +
            "FROM  " +
            "  \"p6e_mc_config\" AS \"mcc\"" +
            "WHERE " +
            "  \"mcc\".\"id\" = ?  " +
            ";";

    /**
     * 模板的查询 SQL
     */
    @SuppressWarnings("ALL")
    private static final String QUERY_ID_TEMPLATE_SOURCE = "" +
            "SELECT  " +
            "  \"mct\".\"id\" AS \"id\",  " +
            "  \"mct\".\"type\" AS \"type\",  " +
            "  \"mct\".\"mark\" AS \"mark\",  " +
            "  \"mct\".\"name\" AS \"name\",  " +
            "  \"mct\".\"title\" AS \"title\",  " +
            "  \"mct\".\"content\" AS \"content\",  " +
            "  \"mct\".\"description\" AS \"description\",  " +
            "  \"mct\".\"parser\" AS \"parser\",  " +
            "  \"mct\".\"parser_source\" AS \"parser_source\"  " +
            "FROM  " +
            "  \"p6e_mc_template\" AS \"mct\"" +
            "WHERE " +
            "  \"mct\".\"mark\" = ?  " +
            ";";

    /**
     * 发射器的查询 SQL
     */
    @SuppressWarnings("ALL")
    private static final String QUERY_MARK_LAUNCHER_SOURCE = "" +
            "SELECT  " +
            "  \"mcl\".\"id\" AS \"id\",  " +
            "  \"mcl\".\"type\" AS \"type\",  " +
            "  \"mcl\".\"mark\" AS \"mark\",   " +
            "  \"mcl\".\"name\" AS \"name\",  " +
            "  \"mcl\".\"enable\" AS \"enable\",  " +
            "  \"mcl\".\"template\" AS \"template\",  " +
            "  \"mcl\".\"description\" AS \"description\",  " +
            "  \"mcl\".\"pattern\" AS \"pattern\",  " +
            "  \"mcl\".\"pattern_source\" AS \"pattern_source\"  " +
            "FROM  " +
            "  \"p6e_mc_launcher\" AS \"mcl\"" +
            "WHERE " +
            "  \"mcl\".\"mark\" = ?  " +
            ";";

    /**
     * 发射器的映射查询 SQL
     */
    @SuppressWarnings("ALL")
    private static final String QUERY_MARK_LAUNCHER_MAPPER_SOURCE = "" +
            "SELECT  " +
            "  \"mcp\".\"lid\" AS \"lid\",  " +
            "  \"mcp\".\"cid\" AS \"cid\",  " +
            "  \"mcp\".\"attribute\" AS \"attribute\"  " +
            "FROM  " +
            "  \"p6e_mc_mapper\" AS \"mcp\"  " +
            "WHERE " +
            "  \"mcp\".\"lid\" = ?  " +
            ";";

    /**
     * BLOB 转 BYTES
     *
     * @param blob BLOB 对象
     * @return 字节数组
     */
    private static byte[] blobToBytes(Blob blob) {
        if (blob == null) {
            return null;
        }
        try (
                InputStream inputStream = blob.getBinaryStream();
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream()
        ) {
            int read;
            byte[] buffer = new byte[1024];
            while ((read = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, read);
            }
            return outputStream.toByteArray();
        } catch (SQLException | IOException e) {
            LOGGER.info("[DATA SOURCE BLOB TO BYTES ERROR]", e);
            return null;
        }
    }

    /**
     * 数据源对象
     */
    private final DataSource dataSource;

    /**
     * 构造方法初始化
     *
     * @param dataSource 数据源对象
     */
    public DataSourceRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * 查询配置源
     * @return 配置源对象
     */
    public ConfigModel getConfigData(int id) {
        try (final Connection connection = dataSource.getConnection()) {
            final PreparedStatement preparedStatement = connection.prepareStatement(QUERY_ID_CONFIG_SOURCE);
            preparedStatement.setInt(1, id);
            final ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                return new ConfigModel() {

                    final Integer id = rs.getInt("id");
                    final String rule = rs.getString("rule");
                    final String name = rs.getString("name");
                    final String type = rs.getString("type");
                    final Integer enable = rs.getInt("enable");
                    final String content = rs.getString("content");
                    final String description = rs.getString("description");
                    final String parser = rs.getString("parser");
                    final byte[] parserSource = blobToBytes(rs.getBlob("parser_source"));

                    @Override
                    public int id() {
                        return id;
                    }

                    @Override
                    public boolean enable() {
                        return "1".equals(String.valueOf(enable));
                    }

                    @Override
                    public String name() {
                        return name;
                    }

                    @Override
                    public MessageType type() {
                        return switch (type) {
                            case "SMS" -> MessageType.SMS;
                            case "MAIL" -> MessageType.MAIL;
                            case "MOBILE" -> MessageType.MOBILE;
                            default -> null;
                        };
                    }

                    @Override
                    public String content() {
                        return content;
                    }

                    @Override
                    public String description() {
                        return description;
                    }

                    @Override
                    public String parser() {
                        return parser;
                    }

                    @Override
                    public byte[] parserSource() {
                        return parserSource;
                    }

                    @Override
                    public String rule() {
                        return rule;
                    }
                };
            }
        } catch (Exception e) {
            LOGGER.info("[DATA SOURCE ERROR]", e);
        }
        return null;
    }

    /**
     * 查询模板源
     * @return 模板源对象
     */
    public TemplateModel getTemplateData(int id) {
        try (final Connection connection = dataSource.getConnection()) {
            final PreparedStatement preparedStatement = connection.prepareStatement(QUERY_ID_TEMPLATE_SOURCE);
            preparedStatement.setInt(1, id);
            final ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                return new TemplateModel() {
                    final Integer id = rs.getInt("id");
                    final String type = rs.getString("type");
                    final String mark = rs.getString("mark");
                    final String name = rs.getString("name");
                    final String title = rs.getString("title");
                    final String content = rs.getString("content");
                    final String description = rs.getString("description");
                    final String parser = rs.getString("parser");
                    final byte[] parserSource = blobToBytes(rs.getBlob("parser_source"));

                    @Override
                    public Integer id() {
                        return id;
                    }

                    @Override
                    public String type() {
                        return type;
                    }

                    @Override
                    public String mark() {
                        return mark;
                    }

                    @Override
                    public String name() {
                        return name;
                    }

                    @Override
                    public String title() {
                        return title;
                    }

                    @Override
                    public String content() {
                        return content;
                    }

                    @Override
                    public String description() {
                        return description;
                    }

                    @Override
                    public String parser() {
                        return parser;
                    }

                    @Override
                    public byte[] parserSource() {
                        return parserSource;
                    }
                };
            }
        } catch (Exception e) {
            LOGGER.info("[DATA SOURCE ERROR]", e);
        }
        return null;
    }

    /**
     * 查询模板源
     *
     * @return 模板源对象
     */
    public TemplateModel getTemplateData(String key, String language) {
        try (final Connection connection = dataSource.getConnection()) {
            final PreparedStatement preparedStatement = connection.prepareStatement(QUERY_ID_TEMPLATE_SOURCE);
            preparedStatement.setString(1, key);
            preparedStatement.setString(2, language);
            final ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                return new TemplateModel() {
                    final Integer id = rs.getInt("id");
                    final String type = rs.getString("type");
                    final String mark = rs.getString("mark");
                    final String name = rs.getString("name");
                    final String title = rs.getString("title");
                    final String content = rs.getString("content");
                    final String description = rs.getString("description");
                    final String parser = rs.getString("parser");
                    final byte[] parserSource = blobToBytes(rs.getBlob("parser_source"));

                    @Override
                    public Integer id() {
                        return id;
                    }

                    @Override
                    public String type() {
                        return type;
                    }

                    @Override
                    public String mark() {
                        return mark;
                    }

                    @Override
                    public String name() {
                        return name;
                    }

                    @Override
                    public String title() {
                        return title;
                    }

                    @Override
                    public String content() {
                        return content;
                    }

                    @Override
                    public String description() {
                        return description;
                    }

                    @Override
                    public String parser() {
                        return parser;
                    }

                    @Override
                    public byte[] parserSource() {
                        return parserSource;
                    }
                };
            }
        } catch (Exception e) {
            LOGGER.info("[DATA SOURCE ERROR]", e);
        }
        return null;
    }

    /**
     * 查询发射器源
     *
     * @return 发射器源对象
     */
    public LauncherModel getLauncherData(int id) {
        try (final Connection connection = dataSource.getConnection()) {
            final PreparedStatement preparedStatement = connection.prepareStatement(QUERY_MARK_LAUNCHER_SOURCE);
            preparedStatement.setInt(1, id);
            final ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                return new LauncherModel() {
                    final Integer id = rs.getInt("id");
                    final String type = rs.getString("type");
                    final String mark = rs.getString("mark");
                    final String name = rs.getString("name");
                    final Integer enable = rs.getInt("enable");
                    final String template = rs.getString("template");
                    final String description = rs.getString("description");
                    final String pattern = rs.getString("pattern");
                    final byte[] patternSource = blobToBytes(rs.getBlob("pattern_source"));

                    final List<ConfigMapperModel> configs = new ArrayList<>();

                    @Override
                    public Integer id() {
                        return id;
                    }

                    @Override
                    public MessageType type() {
                        return switch (type) {
                            case "SMS" -> MessageType.SMS;
                            case "MAIL" -> MessageType.MAIL;
                            case "MOBILE" -> MessageType.MOBILE;
                            default -> null;
                        };
                    }

                    @Override
                    public String name() {
                        return name;
                    }

                    @Override
                    public boolean enable() {
                        return "1".equals(String.valueOf(enable));
                    }

                    @Override
                    public String template() {
                        return template;
                    }

                    @Override
                    public String description() {
                        return description;
                    }

                    @Override
                    public String route() {
                        return null;
                    }

                    @Override
                    public byte[] routeSource() {
                        return new byte[0];
                    }

                    @Override
                    public List<ConfigMapperModel> configs() {
                        return configs;
                    }
                };
            }
        } catch (Exception e) {
            LOGGER.info("[DATA SOURCE ERROR]", e);
        }
        return null;
    }

    /**
     * 查询发射器映射源列表
     * @return 发射器映射源列表
     */
    public List<LauncherModel.ConfigMapperModel> getLauncherConfigMappers(int id) {
        final List<LauncherModel.ConfigMapperModel> result = new ArrayList<>();
        try (final Connection connection = dataSource.getConnection()) {
            final PreparedStatement preparedStatement = connection.prepareStatement(QUERY_MARK_LAUNCHER_MAPPER_SOURCE);
            preparedStatement.setInt(1, id);
            final ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                result.add(new LauncherModel.ConfigMapperModel() {
                    private final Integer lid = rs.getInt("lid");
                    private final String attribute = rs.getString("attribute");

                    @Override
                    public Integer id() {
                        return lid;
                    }

                    @Override
                    public String attribute() {
                        return attribute;
                    }
                });
            }
        } catch (Exception e) {
            LOGGER.info("[DATA SOURCE ERROR]", e);
        }
        return result;
    }

}
