package me.becycled.iothub.server.protocol;

import me.becycled.iothub.sql.SqlDataSourceManager;
import org.testcontainers.containers.PostgreSQLContainer;

import java.sql.Connection;
import java.sql.PreparedStatement;

import static me.becycled.iothub.sql.SqlDataSourceManager.DATASOURCE_DATABASE_NAME;
import static me.becycled.iothub.sql.SqlDataSourceManager.DATASOURCE_PASSWORD;
import static me.becycled.iothub.sql.SqlDataSourceManager.DATASOURCE_PORT_NUMBER;
import static me.becycled.iothub.sql.SqlDataSourceManager.DATASOURCE_SERVER_NAME;
import static me.becycled.iothub.sql.SqlDataSourceManager.DATASOURCE_USER;

/**
 * @author binakot
 */
public abstract class BaseProtocolTest {

    private static final PostgreSQLContainer<?> TEST_POSTGRES = new PostgreSQLContainer<>("postgres:13");

    public BaseProtocolTest() {
        if (!TEST_POSTGRES.isRunning()) {
            TEST_POSTGRES.start();

            System.setProperty(DATASOURCE_SERVER_NAME, TEST_POSTGRES.getHost());
            System.setProperty(DATASOURCE_PORT_NUMBER, TEST_POSTGRES.getFirstMappedPort().toString());
            System.setProperty(DATASOURCE_DATABASE_NAME, TEST_POSTGRES.getDatabaseName());
            System.setProperty(DATASOURCE_USER, TEST_POSTGRES.getUsername());
            System.setProperty(DATASOURCE_PASSWORD, TEST_POSTGRES.getPassword());

            SqlDataSourceManager.INSTANCE.initConnection();

            prepareTestSchema();
        }

        resetTestDatabase();
    }

    private static final String TRACKER_TABLE_CREATE_SQL =
        "CREATE TABLE trackers ( " +
            "id SERIAL PRIMARY KEY, " +
            "user_id INTEGER NOT NULL, " +
            "imei TEXT NOT NULL UNIQUE)";

    private static final String TELEMETRY_TABLE_CREATE_SQL =
        "CREATE TABLE telemetries ( " +
            "tracker_id INTEGER NOT NULL, " +
            "fix_time TIMESTAMPTZ, " +
            "server_time TIMESTAMPTZ NOT NULL DEFAULT now(), " +
            "latitude DOUBLE PRECISION, " +
            "longitude DOUBLE PRECISION, " +
            "altitude DOUBLE PRECISION, " +
            "speed SMALLINT, " +
            "course SMALLINT, " +
            "CONSTRAINT telemetries_pkey PRIMARY KEY (tracker_id, fix_time))";

    private void prepareTestSchema() {
        try (final Connection conn = SqlDataSourceManager.INSTANCE.getConnection();
             final PreparedStatement stmt = conn.prepareStatement(TRACKER_TABLE_CREATE_SQL)) {
            stmt.execute();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        try (final Connection conn = SqlDataSourceManager.INSTANCE.getConnection();
             final PreparedStatement stmt = conn.prepareStatement(TELEMETRY_TABLE_CREATE_SQL)) {
            stmt.execute();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void resetTestDatabase() {
        try (final Connection conn = SqlDataSourceManager.INSTANCE.getConnection();
             final PreparedStatement stmt = conn.prepareStatement("TRUNCATE TABLE trackers")) {
            stmt.execute();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        try (final Connection conn = SqlDataSourceManager.INSTANCE.getConnection();
             final PreparedStatement stmt = conn.prepareStatement("TRUNCATE TABLE telemetries")) {
            stmt.execute();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        try (final Connection conn = SqlDataSourceManager.INSTANCE.getConnection();
             final PreparedStatement stmt = conn.prepareStatement(
                 "INSERT INTO trackers (user_id, imei) " +
                     "VALUES (1, 100000000000000)")) {
            stmt.execute();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
