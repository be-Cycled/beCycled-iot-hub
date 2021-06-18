package me.becycled.iothub.server.protocol.adm;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

import static me.becycled.iothub.server.protocol.adm.AdmUtils.PACKET_HEADER_LENGTH;

/**
 * @author Suren Kalaychyan
 */
public final class AdmFrameDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(final ChannelHandlerContext ctx, final ByteBuf in, final List<Object> out) {
        if (in.readableBytes() < PACKET_HEADER_LENGTH) {
            return;
        }

        final int packetLength = in.getUnsignedByte(in.readerIndex() + 2);
        if (in.readableBytes() < packetLength) {
            return;
        }

        out.add(in.readBytes(packetLength));
    }
}
