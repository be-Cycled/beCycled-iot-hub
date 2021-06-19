package me.becycled.iothub.server.protocol.adm;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import me.becycled.iothub.server.protocol.Telemetry;
import me.becycled.iothub.sql.SqlDataSourceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.List;

import static org.apache.commons.lang3.math.NumberUtils.DOUBLE_ZERO;

/**
 * @author Suren Kalaychyan
 */
@SuppressWarnings("PMD.SingularField")
public final class AdmHandler extends ChannelInboundHandlerAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(AdmHandler.class);

    private int trackerId = -1;
    private boolean isAuthorized;
    private AdmUtils.AdmReplyType replyType;

    @Override
    @SuppressWarnings("unchecked")
    public void channelRead(final ChannelHandlerContext ctx, final Object msg) {
        final List<AdmPacket> packets = (List<AdmPacket>) msg;
        handle(packets);
        ctx.writeAndFlush(packets);
    }

    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) {
        LOGGER.error(cause.getMessage(), cause);
        ctx.close();
    }

    private void handle(final List<AdmPacket> packets) {
        for (final AdmPacket packet : packets) {
            LOGGER.info("Handle a packet: {}", packet);

            switch (packet.getType()) {
                case AUTH:
                    if (!authenticate(packet.getIdentifier())) {
                        throw new IllegalStateException(MessageFormat.format("Unregistered device connected with IMEI: {0}.",
                            packet.getIdentifier()));
                    }
                    replyType = packet.getReplyType();
                    break;

                case DATA_ADM5:
                case DATA_ADM6:
                    if (!isAuthorized) {
                        throw new IllegalStateException("Received packet from unauthorized device.");
                    }
                    packet.setReplyType(replyType);
                    writeTelemetries(trackerId, packet.getRecords());
                    break;

                default:
                    throw new IllegalStateException(MessageFormat.format("Unsupported packet type: {0}.",
                        packet.getType()));
            }
        }
    }

    private boolean authenticate(final String identifier) {
        if (identifier == null || identifier.isEmpty()) {
            throw new IllegalArgumentException("Identifier cannot be null or empty.");
        }

        trackerId = findTrackerIdByImei(identifier);
        isAuthorized = trackerId > 0;
        return isAuthorized;
    }

    private static void writeTelemetries(final int trackerId, final List<Telemetry> telemetries) {
        telemetries.stream()
            .filter(t -> !DOUBLE_ZERO.equals(t.getLatitude()) && !DOUBLE_ZERO.equals(t.getLongitude()))
            .forEach(t -> {
                t.setTrackerId(trackerId);
                try (var conn = SqlDataSourceManager.INSTANCE.getConnection();
                     var stmt = conn.prepareStatement("INSERT INTO telemetries (tracker_id, fix_time, latitude, longitude, altitude, speed, course) VALUES (?, ?, ?, ?, ?, ?, ?) ON CONFLICT DO NOTHING")) {
                    stmt.setInt(1, t.getTrackerId());
                    stmt.setTimestamp(2, Timestamp.from(t.getFixTime()));
                    stmt.setDouble(3, t.getLatitude());
                    stmt.setDouble(4, t.getLongitude());
                    stmt.setDouble(5, t.getAltitude());
                    stmt.setInt(6, t.getSpeed());
                    stmt.setInt(7, t.getCourse());

                    stmt.executeUpdate();
                } catch (Exception ex) {
                    LOGGER.error(ex.getMessage(), ex);
                }
            });
    }

    private static int findTrackerIdByImei(final String imei) {
        try (var conn = SqlDataSourceManager.INSTANCE.getConnection();
             var stmt = conn.prepareStatement("SELECT id FROM trackers WHERE imei = ?::TEXT")) {
            stmt.setString(1, imei.strip());

            try (var rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
        }

        return -1;
    }
}
