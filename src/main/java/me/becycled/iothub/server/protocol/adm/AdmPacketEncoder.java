package me.becycled.iothub.server.protocol.adm;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.List;

/**
 * @author Suren Kalaychyan
 */
public final class AdmPacketEncoder extends MessageToByteEncoder<List<AdmPacket>> {

    @Override
    protected void encode(final ChannelHandlerContext ctx, final List<AdmPacket> msg, final ByteBuf out) {
        final AdmPacket firstPacket = msg.get(0);
        switch (firstPacket.getReplyType()) {
            case UNSUPPORTED:
            case TCP_ACK:
                break;

            case BY_RECORD_COUNT:
                if (firstPacket.getType() == AdmUtils.AdmPacketType.AUTH ||
                    firstPacket.getType() == AdmUtils.AdmPacketType.DATA_ADM5 ||
                    firstPacket.getType() == AdmUtils.AdmPacketType.DATA_ADM6) {
                    out.writeBytes(
                        MessageFormat.format("***{0}*\r\n", msg.size())
                            .getBytes(StandardCharsets.US_ASCII));
                }
                break;

            default:
                throw new IllegalStateException(MessageFormat.format("Unsupported reply type: {0} in packet: {1}.",
                    firstPacket.getReplyType(), firstPacket));
        }
    }
}
