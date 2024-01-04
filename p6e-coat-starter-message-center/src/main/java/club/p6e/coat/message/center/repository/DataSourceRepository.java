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
import java.time.LocalDateTime;
import java.time.ZoneId;
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
            "      \"p6e_mc_config\" AS \"mcc\"    " +
            "    WHERE    " +
            "      \"mcc\".\"id\"  =  ?    " +
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
            "      \"p6e_mc_template\" AS \"mct\"    " +
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
            "      \"mcl\".\"parser\" AS \"route\",    " +
            "      \"mcl\".\"parser_source\" AS \"route_source\",    " +
            "      \"mcl\".\"template\" AS \"template\",    " +
            "      \"mcl\".\"description\" AS \"description\"    " +
            "    FROM    " +
            "      \"p6e_mc_launcher\" AS \"mcl\"    " +
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
            "      \"p6e_mc_launcher_config_mapper\" AS \"mcp\"    " +
            "    WHERE    " +
            "      \"mcp\".\"lid\"  =  ?    " +
            "    ;    ";

    /**
     * 创建日志的 SQL
     */
    @SuppressWarnings("ALL")
    private static final String CREATE_LOG_SQL = "" +
            "    INSERT INTO " +
            "      \"p6e_mc_log\" (    " +
            "        \"no\",    " +
            "        \"parent\",    " +
            "        \"params\",    " +
            "        \"lid\",    " +
            "        \"tid\",    " +
            "        \"cid\",    " +
            "        \"date\",    " +
            "        \"result\",    " +
            "        \"result_date\"    " +
            "      )    " +
            "    VALUES  (    " +
            "       ?, ?, ?, ?, ?, ?, ?, ?, ? " +
            "    )    " +
            "    ;    ";

    /**
     * 创建日志的 SQL
     */
    @SuppressWarnings("ALL")
    private static final String UPDATE_LOG_SQL = "" +
            "    UPDATE " +
            "      \"p6e_mc_log\"     " +
            "    SET     " +
            "      \"result\"  =  ?,     " +
            "      \"result_date\"  =  ?    " +
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
            LOGGER.info("DATA SOURCE BLOB TO BYTES ERROR", e);
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
     *
     * @return 配置源对象
     */
    public ConfigModel getConfigData(int id) {
        try (final Connection connection = dataSource.getConnection()) {
            final PreparedStatement preparedStatement = connection.prepareStatement(QUERY_ID_CONFIG_SQL);
            preparedStatement.setInt(1, id);
            final ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                return new ConfigModel() {
                    private final Integer id = rs.getInt("id");
                    private final String rule = rs.getString("rule");
                    private final String type = rs.getString("type");
                    private final Integer enable = rs.getInt("enable");
                    private final String name = rs.getString("name");
                    private final String content = rs.getString("content");
                    private final String description = rs.getString("description");
                    private final String parser = rs.getString("parser");
                    private final byte[] parserSource = blobToBytes(rs.getBlob("parser_source"));

                    @Override
                    public int id() {
                        return id;
                    }

                    @Override
                    public String rule() {
                        return rule;
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
            LOGGER.info("DATA SOURCE REPOSITORY ERROR", e);
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
            final PreparedStatement preparedStatement = connection.prepareStatement(QUERY_KEY_LANGUAGE_TEMPLATE_SQL);
            preparedStatement.setString(1, key);
            preparedStatement.setString(2, language);
            final ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                return new TemplateModel() {
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
            LOGGER.info("DATA SOURCE REPOSITORY ERROR", e);
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
            final PreparedStatement preparedStatement = connection.prepareStatement(QUERY_ID_LAUNCHER_SQL);
            preparedStatement.setInt(1, id);
            final ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                final List<LauncherModel.ConfigMapperModel> configs = getLauncherConfigMappers(id);
                return new LauncherModel() {
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

                    @Override
                    public Integer id() {
                        return id;
                    }

                    @Override
                    public boolean enable() {
                        return "1".equals(String.valueOf(enable));
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
            LOGGER.info("DATA SOURCE REPOSITORY ERROR", e);
        }
        return null;
    }

    /**
     * 查询发射器映射源列表
     *
     * @return 发射器映射源列表
     */
    private List<LauncherModel.ConfigMapperModel> getLauncherConfigMappers(int id) {
        final List<LauncherModel.ConfigMapperModel> result = new ArrayList<>();
        try (final Connection connection = dataSource.getConnection()) {
            final PreparedStatement preparedStatement = connection.prepareStatement(QUERY_LAUNCHER_CONFIG_MAPPER_SQL);
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
            LOGGER.info("DATA SOURCE REPOSITORY ERROR", e);
        }
        return result;
    }

    public boolean createLog(String no, String parent, String params, int lid, int tid, int cid, LocalDateTime createDate) {
        try (final Connection connection = dataSource.getConnection()) {
            final PreparedStatement preparedStatement = connection.prepareStatement(CREATE_LOG_SQL);
            final Date date = new Date(createDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
            preparedStatement.setString(1, no);
            preparedStatement.setString(2, parent);
            preparedStatement.setString(3, params);
            preparedStatement.setInt(4, lid);
            preparedStatement.setInt(5, tid);
            preparedStatement.setInt(6, cid);
            preparedStatement.setDate(7, date);
            return preparedStatement.executeUpdate() > 0;
        } catch (Exception e) {
            LOGGER.info("DATA SOURCE REPOSITORY ERROR", e);
        }
        return false;
    }

    public void updateLog(String no, String result, LocalDateTime resultDate) {
        try (final Connection connection = dataSource.getConnection()) {
            final PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_LOG_SQL);
            final Date date = new Date(resultDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
            preparedStatement.setString(1, result);
            preparedStatement.setDate(2, date);
            preparedStatement.setString(3, no);
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            LOGGER.info("DATA SOURCE REPOSITORY ERROR", e);
        }
    }

}
