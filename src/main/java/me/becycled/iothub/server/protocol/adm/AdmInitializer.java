package me.becycled.iothub.server.protocol.adm;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;

/**
 * @author Suren Kalaychyan
 */
public class AdmInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(final SocketChannel ch) {
        ch.pipeline().addLast(
            new AdmFrameDecoder(),
            new AdmPacketEncoder(),
            new AdmPacketDecoder(),
            new AdmHandler());
    }
}
