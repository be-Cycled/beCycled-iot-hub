package me.becycled.iothub.sql;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.sql.Connection;
import java.util.Properties;

/**
 * @author Suren Kalaychyan
 */
@SuppressFBWarnings("DMI_HARDCODED_ABSOLUTE_FILENAME")
public enum SqlDataSourceManager {

    INSTANCE;

    private static final String DATASOURCE_CLASS_NAME = "dataSourceClassName";
    private static final String DATASOURCE_SERVER_NAME = "dataSource.serverName";
    private static final String DATASOURCE_PORT_NUMBER = "dataSource.portNumber";
    private static final String DATASOURCE_DATABASE_NAME = "dataSource.databaseName";
    private static final String DATASOURCE_USER = "dataSource.user";
    private static final String DATASOURCE_PASSWORD = "dataSource.password";

    private HikariDataSource dataSource;

    public Connection getConnection() throws Exception {
        if (dataSource == null) {
            final Properties props = loadProperties();
            final HikariConfig config = new HikariConfig(props);
            dataSource = new HikariDataSource(config);
        }

        return dataSource.getConnection();
    }

    @SuppressWarnings("MultipleStringLiterals")
    private static Properties loadProperties() {
        final Properties props = new Properties();
        props.setProperty(DATASOURCE_CLASS_NAME, System.getProperty(DATASOURCE_CLASS_NAME, "org.postgresql.ds.PGSimpleDataSource"));
        props.setProperty(DATASOURCE_SERVER_NAME, System.getProperty(DATASOURCE_SERVER_NAME, "localhost"));
        props.setProperty(DATASOURCE_PORT_NUMBER, System.getProperty(DATASOURCE_PORT_NUMBER, "5432"));
        props.setProperty(DATASOURCE_DATABASE_NAME, System.getProperty(DATASOURCE_DATABASE_NAME, "becycled"));
        props.setProperty(DATASOURCE_USER, System.getProperty(DATASOURCE_CLASS_NAME, "becycled"));
        props.setProperty(DATASOURCE_PASSWORD, System.getProperty(DATASOURCE_CLASS_NAME, "becycled"));
        return props;
    }
}
