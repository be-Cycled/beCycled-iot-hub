package me.becycled.iothub.sql;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.util.Properties;

/**
 * @author Suren Kalaychyan
 */
@SuppressFBWarnings("DMI_HARDCODED_ABSOLUTE_FILENAME")
public enum SqlDataSourceManager {

    INSTANCE;

    private static final String CONFIG_FILE = "src/main/resources/hikari.properties";

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
    private static Properties loadProperties() throws Exception {
        try (InputStream input = Files.newInputStream(Paths.get(CONFIG_FILE))) {
            final Properties props = new Properties();
            props.load(input);

            final String host = System.getProperty("dataSource.serverName", props.getProperty("dataSource.serverName"));
            final String password = System.getProperty("dataSource.password", props.getProperty("dataSource.password"));
            props.setProperty("dataSource.serverName", host);
            props.setProperty("dataSource.password", password);

            return props;
        }
    }
}
