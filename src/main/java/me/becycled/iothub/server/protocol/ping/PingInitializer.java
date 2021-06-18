package me.becycled.iothub.server.protocol.ping;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;

/**
 * @author Suren Kalaychyan
 */
public final class PingInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(final SocketChannel ch) {
        ch.pipeline().addLast(
            new StringDecoder(CharsetUtil.UTF_8),
            new StringEncoder(CharsetUtil.UTF_8),
            new PingHandler());
    }
}
