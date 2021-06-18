package me.becycled.iothub.server.protocol.ping;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Suren Kalaychyan
 */
public class PingHandler extends ChannelInboundHandlerAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(PingHandler.class);

    @Override
    public void channelRead(final ChannelHandlerContext ctx, final Object msg) {
        final String message;
        if (msg instanceof String) {
            message = (String) msg;
        } else {
            ctx.close();
            return;
        }

        if ("ping".equals(message)) {
            ctx.write("pong");
        } else {
            ctx.close();
        }
    }

    @Override
    public void channelReadComplete(final ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) {
        LOGGER.error(cause.getMessage());
        ctx.close();
    }
}
