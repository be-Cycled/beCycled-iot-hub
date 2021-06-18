package me.becycled.iothub.sql;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.sql.Connection;

/**
 * @author Suren Kalaychyan
 */
@SuppressFBWarnings("DMI_HARDCODED_ABSOLUTE_FILENAME")
public enum SqlDataSourceManager {

    INSTANCE;

    private static final String CONFIG_FILE = "/hikari.properties";

    private final HikariConfig config;
    private HikariDataSource dataSource;

    SqlDataSourceManager() {
        config = new HikariConfig(CONFIG_FILE);
    }

    public Connection getConnection() throws Exception {
        if (dataSource == null) {
            dataSource = new HikariDataSource(config);
        }

        return dataSource.getConnection();
    }
}
