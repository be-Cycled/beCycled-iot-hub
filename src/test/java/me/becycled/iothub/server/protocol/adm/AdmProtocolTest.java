package me.becycled.iothub.server.protocol.adm;

import me.becycled.iothub.server.TcpServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Suren Kalaychyan
 */
@Disabled
public class AdmProtocolTest {

    private static final int PORT = 10000;
    private static final TcpServer server = new TcpServer(PORT, new AdmInitializer());
    private static boolean inited = false;

    @BeforeAll
    static void beforeAll() throws Exception {
        if (!inited) {
            server.start();
            inited = true;
        }
    }

    @AfterAll
    static void afterAll() throws Exception {
        server.stop();
    }

    @Test
    void testAuthWithSinglePacket() throws Exception {
        try (Socket socket = new Socket("localhost", PORT);
             InputStream in = socket.getInputStream();
             OutputStream out = socket.getOutputStream()) {

            out.write(AdmProtocolTestUtils.VALID_AUTH_PACKET);
            out.flush();
            byte[] resp = new byte[AdmProtocolTestUtils.VALID_AUTH_RESPONSE.length];
            assertEquals(resp.length, in.read(resp));
            assertArrayEquals(AdmProtocolTestUtils.VALID_AUTH_RESPONSE, resp);

            out.write(AdmProtocolTestUtils.VALID_ADM6_PACKET_WITH_SINGLE_DATA_RECORD);
            out.flush();
            resp = new byte[AdmProtocolTestUtils.VALID_ADM6_PACKET_WITH_SINGLE_DATA_RECORD_RESPONSE.length];
            assertEquals(resp.length, in.read(resp));
            assertArrayEquals(AdmProtocolTestUtils.VALID_ADM6_PACKET_WITH_SINGLE_DATA_RECORD_RESPONSE, resp);
        }
    }

    @Test
    public void testAuthWithSeveralPackets() throws Exception {
        try (Socket socket = new Socket("localhost", PORT);
             InputStream in = socket.getInputStream();
             OutputStream out = socket.getOutputStream()) {

            out.write(AdmProtocolTestUtils.VALID_AUTH_PACKET);
            out.flush();
            byte[] resp = new byte[AdmProtocolTestUtils.VALID_AUTH_RESPONSE.length];
            assertEquals(resp.length, in.read(resp));
            assertArrayEquals(AdmProtocolTestUtils.VALID_AUTH_RESPONSE, resp);

            out.write(AdmProtocolTestUtils.VALID_ADM6_PACKET_WITH_SEVERAL_DATA_RECORDS);
            out.flush();

            resp = new byte[AdmProtocolTestUtils.VALID_ADM6_PACKET_WITH_SEVERAL_DATA_RECORDS_RESPONSE.length];
            assertEquals(resp.length, in.read(resp));
            assertArrayEquals(AdmProtocolTestUtils.VALID_ADM6_PACKET_WITH_SEVERAL_DATA_RECORDS_RESPONSE, resp);

            resp = new byte[AdmProtocolTestUtils.VALID_ADM6_PACKET_WITH_SEVERAL_DATA_RECORDS_RESPONSE.length];
            assertEquals(resp.length, in.read(resp));
            assertArrayEquals(AdmProtocolTestUtils.VALID_ADM6_PACKET_WITH_SEVERAL_DATA_RECORDS_RESPONSE, resp);

            resp = new byte[AdmProtocolTestUtils.VALID_ADM6_PACKET_WITH_SEVERAL_DATA_RECORDS_RESPONSE.length];
            assertEquals(resp.length, in.read(resp));
            assertArrayEquals(AdmProtocolTestUtils.VALID_ADM6_PACKET_WITH_SEVERAL_DATA_RECORDS_RESPONSE, resp);
        }
    }
}
