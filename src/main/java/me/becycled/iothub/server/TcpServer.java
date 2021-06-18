package me.becycled.iothub.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.DefaultThreadFactory;

import java.net.InetSocketAddress;

/**
 * @author Suren Kalaychyan
 */
@SuppressWarnings("PMD.AvoidUsingHardCodedIP")
public final class TcpServer {

    private static final String ALL_ALLOWED_IP = "0.0.0.0";

    private final int port;

    private final EventLoopGroup bossGroup;
    private final EventLoopGroup workerGroup;
    private final ServerBootstrap bootstrap;

    public TcpServer(final int port, final ChannelInitializer<?> initializer) {
        this.port = port;

        if (Epoll.isAvailable()) {
            bossGroup = new EpollEventLoopGroup(0, new DefaultThreadFactory(port + "-TCP-ACCEPTOR-EPOLL-POOL"));
            workerGroup = new EpollEventLoopGroup(0, new DefaultThreadFactory(port + "-TCP-HANDLER-EPOLL-POOL"));
            bootstrap = new ServerBootstrap()
                .group(bossGroup, workerGroup)
                .channel(EpollServerSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.TRACE))
                .option(ChannelOption.SO_BACKLOG, 1024)
                .childHandler(initializer)
                .childOption(ChannelOption.SO_REUSEADDR, true)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childOption(ChannelOption.SO_LINGER, 0);
        } else {
            bossGroup = new NioEventLoopGroup(0, new DefaultThreadFactory(port + "-TCP-ACCEPTOR-NIO-POOL"));
            workerGroup = new NioEventLoopGroup(0, new DefaultThreadFactory(port + "-TCP-HANDLER-NIO-POOL"));
            bootstrap = new ServerBootstrap()
                .group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.TRACE))
                .option(ChannelOption.SO_BACKLOG, 1024)
                .childHandler(initializer)
                .childOption(ChannelOption.SO_REUSEADDR, true)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childOption(ChannelOption.SO_LINGER, 0);
        }
    }

    public void start() throws InterruptedException {
        bootstrap.bind(new InetSocketAddress(ALL_ALLOWED_IP, port)).sync();
    }

    public void stop() throws InterruptedException {
        bossGroup.shutdownGracefully().sync();
        workerGroup.shutdownGracefully().sync();
    }
}
