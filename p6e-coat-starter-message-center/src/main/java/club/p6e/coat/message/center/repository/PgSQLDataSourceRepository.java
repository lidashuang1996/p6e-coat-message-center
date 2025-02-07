package club.p6e.coat.message.center.repository;

import club.p6e.DatabaseConfig;
import club.p6e.coat.message.center.MessageCenterType;
import club.p6e.coat.message.center.config.ConfigModel;
import club.p6e.coat.message.center.launcher.LauncherModel;
import club.p6e.coat.message.center.template.TemplateModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lidashuang
 * @version 1.0
 */
@Component
@ConditionalOnMissingBean(
        value = PgSQLDataSourceRepository.class,
        ignored = PgSQLDataSourceRepository.class
)
public class PgSQLDataSourceRepository {

    /**
     * 注入日志对象
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(PgSQLDataSourceRepository.class);

    /**
     * 配置的查询 SQL
     */
    @SuppressWarnings("ALL")
    private static final String QUERY_ID_CONFIG_SQL = "" +
            "    SELECT    " +
            "      \"mcc\".\"id\" AS \"id\",    " +
            "      \"mcc\".\"rule\" AS \"rule\",    " +
            "      \"mcc\".\"type\" AS \"type\",    " +
            "      \"mcc\".\"enable\" AS \"enable\",    " +
            "      \"mcc\".\"name\" AS \"name\",    " +
            "      \"mcc\".\"content\" AS \"content\",    " +
            "      \"mcc\".\"description\" AS \"description\",    " +
            "      \"mcc\".\"parser\" AS \"parser\",    " +
            "      \"mcc\".\"parser_source\" AS \"parser_source\"    " +
            "    FROM    " +
            "      \"" + DatabaseConfig.TABLE_PREFIX + "message_center_config\" AS \"mcc\"    " +
            "    WHERE    " +
            "      \"mcc\".\"id\"  =  ?    " +
            "    ;    ";

    /**
     * 创建日志的 SQL
     */
    @SuppressWarnings("ALL")
    private static final String UPDATE_CONFIG_SQL = "" +
            "    UPDATE " +
            "      \"" + DatabaseConfig.TABLE_PREFIX + "message_center_config\"     " +
            "    SET     " +
            "      \"content\"  =  ?    " +
            "    WHERE    " +
            "      \"id\" = ?    " +
            "    ;    ";

    /**
     * 模板的查询 SQL
     */
    @SuppressWarnings("ALL")
    private static final String QUERY_KEY_LANGUAGE_TEMPLATE_SQL = "" +
            "    SELECT    " +
            "      \"mct\".\"id\" AS \"id\",    " +
            "      \"mct\".\"key\" AS \"key\",    " +
            "      \"mct\".\"type\" AS \"type\",    " +
            "      \"mct\".\"name\" AS \"name\",    " +
            "      \"mct\".\"language\" AS \"language\",    " +
            "      \"mct\".\"title\" AS \"title\",    " +
            "      \"mct\".\"content\" AS \"content\",    " +
            "      \"mct\".\"description\" AS \"description\",    " +
            "      \"mct\".\"parser\" AS \"parser\",    " +
            "      \"mct\".\"parser_source\" AS \"parser_source\"    " +
            "    FROM    " +
            "      \"" + DatabaseConfig.TABLE_PREFIX + "message_center_template\" AS \"mct\"    " +
            "    WHERE    " +
            "      \"mct\".\"key\"  =  ?    " +
            "      AND \"mct\".\"language\"  =  ?    " +
            "    ;    ";

    /**
     * 发射器的查询 SQL
     */
    @SuppressWarnings("ALL")
    private static final String QUERY_ID_LAUNCHER_SQL = "" +
            "    SELECT    " +
            "      \"mcl\".\"id\" AS \"id\",    " +
            "      \"mcl\".\"enable\" AS \"enable\",    " +
            "      \"mcl\".\"type\" AS \"type\",    " +
            "      \"mcl\".\"name\" AS \"name\",    " +
            "      \"mcl\".\"route\" AS \"route\",    " +
            "      \"mcl\".\"route_source\" AS \"route_source\",    " +
            "      \"mcl\".\"parser\" AS \"parser\",    " +
            "      \"mcl\".\"parser_source\" AS \"parser_source\",    " +
            "      \"mcl\".\"template\" AS \"template\",    " +
            "      \"mcl\".\"description\" AS \"description\"    " +
            "    FROM    " +
            "      \"" + DatabaseConfig.TABLE_PREFIX + "message_center_launcher\" AS \"mcl\"    " +
            "    WHERE    " +
            "      \"mcl\".\"id\"  =  ?    " +
            "    ;    ";

    /**
     * 发射器的映射查询 SQL
     */
    @SuppressWarnings("ALL")
    private static final String QUERY_LAUNCHER_CONFIG_MAPPER_SQL = "" +
            "    SELECT    " +
            "      \"mcp\".\"lid\" AS \"lid\",    " +
            "      \"mcp\".\"cid\" AS \"cid\",    " +
            "      \"mcp\".\"attribute\" AS \"attribute\"    " +
            "    FROM    " +
            "      \"" + DatabaseConfig.TABLE_PREFIX + "message_center_launcher_config_mapper\" AS \"mcp\"    " +
            "    WHERE    " +
            "      \"mcp\".\"lid\"  =  ?    " +
            "    ;    ";

    /**
     * 配置的查询 SQL
     */
    @SuppressWarnings("ALL")
    private static final String QUERY_DICTIONARY_LIST_SQL = "" +
            "    SELECT    " +
            "      \"mcd\".\"id\" AS \"id\",    " +
            "      \"mcd\".\"key\" AS \"key\",    " +
            "      \"mcd\".\"value\" AS \"value\",    " +
            "      \"mcd\".\"language\" AS \"language\"    " +
            "    FROM    " +
            "      \"" + DatabaseConfig.TABLE_PREFIX + "message_center_dictionary\" AS \"mcd\"    " +
            "    ;    ";

    /**
     * 创建日志的 SQL
     */
    @SuppressWarnings("ALL")
    private static final String CREATE_LOG_SQL = "" +
            "    INSERT INTO " +
            "      \"" + DatabaseConfig.TABLE_PREFIX + "message_center_log\" (    " +
            "        \"no\",    " +
            "        \"parent\",    " +
            "        \"params\",    " +
            "        \"lid\",    " +
            "        \"tid\",    " +
            "        \"cid\",    " +
            "        \"date_time\",    " +
            "        \"creator\",    " +
            "        \"modifier\",    " +
            "        \"creation_date_time\",    " +
            "        \"modification_date_time\",    " +
            "        \"version\"    " +
            "      )    " +
            "    VALUES  (    " +
            "       ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? " +
            "    )    " +
            "    ;    ";

    /**
     * 创建日志的 SQL
     */
    @SuppressWarnings("ALL")
    private static final String UPDATE_LOG_SQL = "" +
            "    UPDATE " +
            "      \"" + DatabaseConfig.TABLE_PREFIX + "message_center_log\"     " +
            "    SET     " +
            "      \"result\"  =  ?,     " +
            "      \"result_date_time\"  =  ?    " +
            "    WHERE    " +
            "      \"no\" = ?    " +
            "    ;    ";

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
            LOGGER.error("DATA SOURCE BLOB TO BYTES ERROR", e);
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
    @SuppressWarnings("ALL")
    public PgSQLDataSourceRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * 查询配置源
     *
     * @param configId 配置 ID
     * @return 配置源对象
     */
    public ConfigModel getConfigData(int configId) {
        try (final Connection connection = dataSource.getConnection()) {
            final PreparedStatement preparedStatement = connection.prepareStatement(QUERY_ID_CONFIG_SQL);
            preparedStatement.setInt(1, configId);
            final ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                final Integer id = rs.getInt("id");
                final String rule = rs.getString("rule");
                final String type = rs.getString("type");
                final Integer enable = rs.getInt("enable");
                final String name = rs.getString("name");
                final String content = rs.getString("content");
                final String description = rs.getString("description");
                final String parser = rs.getString("parser");
                final byte[] parserSource = blobToBytes(rs.getBlob("parser_source"));
                return new ConfigModel() {
                    @Override
                    public int id() {
                        return id;
                    }

                    @Override
                    public String rule() {
                        return rule;
                    }

                    @Override
                    public MessageCenterType type() {
                        return switch (type) {
                            case "SMS" -> MessageCenterType.SMS;
                            case "MAIL" -> MessageCenterType.MAIL;
                            case "MOBILE" -> MessageCenterType.MOBILE;
                            default -> null;
                        };
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
            LOGGER.error("DATA SOURCE REPOSITORY ERROR", e);
        }
        return null;
    }

    /**
     * 查询模板源
     *
     * @param templateKey      模板 KEY
     * @param templateLanguage 模板 LANGUAGE
     * @return 模板源对象
     */
    public TemplateModel getTemplateData(String templateKey, String templateLanguage) {
        try (final Connection connection = dataSource.getConnection()) {
            final PreparedStatement preparedStatement = connection.prepareStatement(QUERY_KEY_LANGUAGE_TEMPLATE_SQL);
            preparedStatement.setString(1, templateKey);
            preparedStatement.setString(2, templateLanguage);
            final ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                final Integer id = rs.getInt("id");
                final String key = rs.getString("key");
                final String type = rs.getString("type");
                final String name = rs.getString("name");
                final String language = rs.getString("language");
                final String title = rs.getString("title");
                final String content = rs.getString("content");
                final String description = rs.getString("description");
                final String parser = rs.getString("parser");
                final byte[] parserSource = blobToBytes(rs.getBlob("parser_source"));
                return new TemplateModel() {

                    @Override
                    public Integer id() {
                        return id;
                    }

                    @Override
                    public String key() {
                        return key;
                    }

                    @Override
                    public String type() {
                        return type;
                    }

                    @Override
                    public String name() {
                        return name;
                    }

                    @Override
                    public String language() {
                        return language;
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
            LOGGER.error("DATA SOURCE REPOSITORY ERROR", e);
        }
        return null;
    }

    /**
     * 查询发射器源
     *
     * @param launcherId 发射器 ID
     * @return 发射器源对象
     */
    public LauncherModel getLauncherData(int launcherId) {
        try (final Connection connection = dataSource.getConnection()) {
            final PreparedStatement preparedStatement = connection.prepareStatement(QUERY_ID_LAUNCHER_SQL);
            preparedStatement.setInt(1, launcherId);
            final ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                final List<LauncherModel.ConfigMapperModel> configs = getLauncherConfigMappers(launcherId);
                final Integer id = rs.getInt("id");
                final Integer enable = rs.getInt("enable");
                final String type = rs.getString("type");
                final String name = rs.getString("name");
                final String template = rs.getString("template");
                final String route = rs.getString("route");
                final byte[] routeSource = blobToBytes(rs.getBlob("route_source"));
                final String parser = rs.getString("parser");
                final byte[] parserSource = blobToBytes(rs.getBlob("parser_source"));
                final String description = rs.getString("description");
                return new LauncherModel() {

                    @Override
                    public Integer id() {
                        return id;
                    }

                    @Override
                    public boolean enable() {
                        return "1".equals(String.valueOf(enable));
                    }

                    @Override
                    public MessageCenterType type() {
                        return switch (type) {
                            case "SMS" -> MessageCenterType.SMS;
                            case "MAIL" -> MessageCenterType.MAIL;
                            case "MOBILE" -> MessageCenterType.MOBILE;
                            case "TELEGRAM" -> MessageCenterType.TELEGRAM;
                            default -> null;
                        };
                    }

                    @Override
                    public String name() {
                        return name;
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
                        return route;
                    }

                    @Override
                    public byte[] routeSource() {
                        return routeSource;
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
                    public List<ConfigMapperModel> configs() {
                        return configs;
                    }
                };
            }
        } catch (Exception e) {
            LOGGER.error("DATA SOURCE REPOSITORY ERROR", e);
        }
        return null;
    }

    /**
     * 查询发射器映射源列表
     *
     * @param launcherId 发射器 ID
     * @return 发射器映射源列表
     */
    private List<LauncherModel.ConfigMapperModel> getLauncherConfigMappers(int launcherId) {
        final List<LauncherModel.ConfigMapperModel> result = new ArrayList<>();
        try (final Connection connection = dataSource.getConnection()) {
            final PreparedStatement preparedStatement = connection.prepareStatement(QUERY_LAUNCHER_CONFIG_MAPPER_SQL);
            preparedStatement.setInt(1, launcherId);
            final ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                final Integer lid = rs.getInt("lid");
                final String attribute = rs.getString("attribute");
                result.add(new LauncherModel.ConfigMapperModel() {
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
            LOGGER.error("DATA SOURCE REPOSITORY ERROR", e);
        }
        return result;
    }


    /**
     * 查询字典数据列表
     *
     * @return 字典数据列表
     */
    @SuppressWarnings("ALL")
    public List<Map<String, Object>> getDictionary() {
        final List<Map<String, Object>> result = new ArrayList<>();
        try (final Connection connection = dataSource.getConnection()) {
            final PreparedStatement preparedStatement = connection.prepareStatement(QUERY_DICTIONARY_LIST_SQL);
            final ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                final Integer id = rs.getInt("id");
                final String key = rs.getString("key");
                final String value = rs.getString("value");
                final String language = rs.getString("language");
                result.add(new HashMap<>() {{
                    put("id", id);
                    put("key", key);
                    put("value", value);
                    put("language", language);
                }});
            }
        } catch (Exception e) {
            LOGGER.error("DATA SOURCE REPOSITORY ERROR", e);
        }
        return result;
    }

    /**
     * 创建日志
     *
     * @param no       日志编号
     * @param parent   父日志编号
     * @param params   请求参数
     * @param lid      发射 ID
     * @param tid      模板 ID
     * @param cid      配置 ID
     * @param dateTime 创建时间
     * @return 是否创建日志成功
     */
    public boolean createLog(String no, String parent, String params, int lid, int tid, int cid, LocalDateTime dateTime) {
        try (final Connection connection = dataSource.getConnection()) {
            final PreparedStatement preparedStatement = connection.prepareStatement(CREATE_LOG_SQL);
            preparedStatement.setString(1, no);
            preparedStatement.setString(2, parent);
            preparedStatement.setString(3, params);
            preparedStatement.setInt(4, lid);
            preparedStatement.setInt(5, tid);
            preparedStatement.setInt(6, cid);
            preparedStatement.setTimestamp(7, Timestamp.valueOf(dateTime));
            preparedStatement.setString(8, "sys");
            preparedStatement.setString(9, "sys");
            preparedStatement.setTimestamp(10, Timestamp.valueOf(LocalDateTime.now()));
            preparedStatement.setTimestamp(11, Timestamp.valueOf(LocalDateTime.now()));
            preparedStatement.setInt(12, 0);
            return preparedStatement.executeUpdate() > 0;
        } catch (Exception e) {
            LOGGER.error("DATA SOURCE REPOSITORY ERROR", e);
        }
        return false;
    }

    /**
     * 更新日志
     *
     * @param no             日志编号
     * @param result         日志结果
     * @param resultDateTime 日志结果时间
     */
    public void updateLog(String no, String result, LocalDateTime resultDateTime) {
        try (final Connection connection = dataSource.getConnection()) {
            final PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_LOG_SQL);
            preparedStatement.setString(1, result);
            preparedStatement.setTimestamp(2, Timestamp.valueOf(resultDateTime));
            preparedStatement.setString(3, no);
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            LOGGER.error("DATA SOURCE REPOSITORY ERROR", e);
        }
    }

    public void updateConfigContent(int id, String content) {
        try (final Connection connection = dataSource.getConnection()) {
            final PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_CONFIG_SQL);
            preparedStatement.setString(1, content);
            preparedStatement.setInt(2, id);
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            LOGGER.error("DATA SOURCE REPOSITORY ERROR", e);
        }
    }
}
