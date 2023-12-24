package club.p6e.coat.message.center.repository;

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

//    /**
//     * 查询配置源
//     *
//     * @param query     配置 ID
//     * @param attribute 属性
//     * @return 配置源对象
//     */
//    public ConfigSource getConfigSource(int query, String attribute) {
//        try (final Connection connection = dataSource.getConnection()) {
//            final PreparedStatement preparedStatement = connection.prepareStatement(QUERY_ID_CONFIG_SOURCE);
//            preparedStatement.setInt(1, query);
//            final ResultSet rs = preparedStatement.executeQuery();
//            if (rs.next()) {
//                final Integer id = rs.getInt("id");
//                final String rule = rs.getString("rule");
//                final String name = rs.getString("name");
//                final String type = rs.getString("type");
//                final Integer enable = rs.getInt("enable");
//                final String content = rs.getString("content");
//                final String description = rs.getString("description");
//                final String parser = rs.getString("parser");
//                final byte[] parserSource = blobToBytes(rs.getBlob("parser_source"));
//                return new ConfigSource() {
//
//                    @Override
//                    public Integer enable() {
//                        return enable;
//                    }
//
//                    @Override
//                    public String content() {
//                        return content;
//                    }
//
//                    @Override
//                    public String description() {
//                        return description;
//                    }
//
//                    @Override
//                    public String parser() {
//                        return parser;
//                    }
//
//                    @Override
//                    public byte[] parserSource() {
//                        return parserSource;
//                    }
//
//                    @Override
//                    public String attribute() {
//                        return attribute;
//                    }
//
//                    @Override
//                    public Integer id() {
//                        return id;
//                    }
//
//                    @Override
//                    public String rule() {
//                        return rule;
//                    }
//
//                    @Override
//                    public String name() {
//                        return name;
//                    }
//
//                    @Override
//                    public String type() {
//                        return type;
//                    }
//                };
//            }
//        } catch (Exception e) {
//            LOGGER.info("[DATA SOURCE ERROR]", e);
//        }
//        return null;
//    }

    public TemplateModel getTemplateData(Integer id) {
        return new TemplateModel() {
            @Override
            public Integer id() {
                return null;
            }

            @Override
            public String type() {
                return null;
            }

            @Override
            public String mark() {
                return null;
            }

            @Override
            public String name() {
                return null;
            }

            @Override
            public String title() {
                return null;
            }

            @Override
            public String content() {
                return null;
            }

            @Override
            public String description() {
                return null;
            }

            @Override
            public String parser() {
                return null;
            }

            @Override
            public byte[] parserSource() {
                return new byte[0];
            }
        };
    }

    /**
     * 查询模板源
     *
     * @param query 发射器的标记
     * @return 模板源对象
     */
    public TemplateModel getTemplateData(String query, String language) {
        try (final Connection connection = dataSource.getConnection()) {
            final PreparedStatement preparedStatement = connection.prepareStatement(QUERY_ID_TEMPLATE_SOURCE);
            preparedStatement.setString(1, query);
            final ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                final Integer id = rs.getInt("id");
                final String type = rs.getString("type");
                final String mark = rs.getString("mark");
                final String name = rs.getString("name");
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
     * @param query    发射器的标记
     * @param language 语言
     * @return 发射器源对象
     */
    public LauncherModel getLauncherData(Integer id) {
        try (final Connection connection = dataSource.getConnection()) {
            final PreparedStatement preparedStatement = connection.prepareStatement(QUERY_MARK_LAUNCHER_SOURCE);
            preparedStatement.setString(1, query);
            final ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                final Integer id = rs.getInt("id");
                final String type = rs.getString("type");
                final String mark = rs.getString("mark");
                final String name = rs.getString("name");
                final Integer enable = rs.getInt("enable");
                final String template = rs.getString("template");
                final String description = rs.getString("description");
                final String pattern = rs.getString("pattern");
                final byte[] patternSource = blobToBytes(rs.getBlob("pattern_source"));
                return new LauncherModel() {
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
                    public Integer enable() {
                        return enable;
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
                    public String pattern() {
                        return pattern;
                    }

                    @Override
                    public byte[] patternSource() {
                        return patternSource;
                    }

                    @Override
                    public String language() {
                        return language;
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
     *
     * @param query 发射器 ID
     * @return 发射器映射源列表
     */
    public List<LauncherMapperSource> getLauncherMapperSourceList(int query) {
        final List<LauncherMapperSource> result = new ArrayList<>();
        try (final Connection connection = dataSource.getConnection()) {
            final PreparedStatement preparedStatement = connection.prepareStatement(QUERY_MARK_LAUNCHER_MAPPER_SOURCE);
            preparedStatement.setInt(1, query);
            final ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                final Integer lid = rs.getInt("lid");
                final Integer cid = rs.getInt("cid");
                final String attribute = rs.getString("attribute");
                result.add(new LauncherMapperSource() {
                    @Override
                    public Integer lid() {
                        return lid;
                    }

                    @Override
                    public Integer cid() {
                        return cid;
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
