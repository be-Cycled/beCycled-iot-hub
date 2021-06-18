package me.becycled.iothub.server.protocol.ping;

import me.becycled.iothub.server.TcpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Suren Kalaychyan
 */
public class PingProtocolTest {

    private static final int PORT = 10000;
    private static final TcpServer server = new TcpServer(PORT, new PingInitializer());

    @BeforeEach
    void setUp() throws Exception {
        server.start();
    }

    @AfterEach
    void tearDown() throws Exception {
        server.stop();
    }

    @Test
    void ping() throws Exception {
        try (Socket socket = new Socket("localhost", PORT);
             InputStream in = socket.getInputStream();
             OutputStream out = socket.getOutputStream()) {
            out.write("ping".getBytes(StandardCharsets.UTF_8));
            out.flush();
            final byte[] resp = new byte[4];
            assertEquals(resp.length, in.read(resp));
            assertArrayEquals("pong".getBytes(StandardCharsets.UTF_8), resp);
        }
    }
}
