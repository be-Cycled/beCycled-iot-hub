package me.becycled.iothub.server;

import me.becycled.iothub.server.protocol.adm.AdmInitializer;
import me.becycled.iothub.server.protocol.ping.PingInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author Suren Kalaychyan
 */
public final class ServerManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServerManager.class);

    private final List<TcpServer> servers = List.of(
        new TcpServer(10_000, new PingInitializer()),
        new TcpServer(10_001, new AdmInitializer())
    );

    public void startAll() {
        for (final TcpServer server : servers) {
            try {
                server.start();
            } catch (InterruptedException ex) {
                LOGGER.error("Error on starting a server {}: {}", server, ex.getMessage());
            }
        }
    }

    public void stopAll() {
        for (final TcpServer server : servers) {
            try {
                server.stop();
            } catch (InterruptedException ex) {
                LOGGER.error("Error on stopping a server {}: {}", server, ex.getMessage());
            }
        }
    }
}
