package service;

import java.io.IOException;
import java.net.InetSocketAddress;

import constants.ServerConstants;
import handling.MapleServerHandler;
import handling.game.PlayerStorage;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import static service.LoginServer.PORT;
import service.ServerMode.MapleServerMode;
import net.PacketDecoder;
import net.PacketEncoder;

/**
 *
 * @author Itzik
 */
public class FarmServer extends Thread {

    private static FarmServer instance;
    private static String ip;
    private static InetSocketAddress InetSocketadd;
    private static final int PORT = 8611;
    private static PlayerStorage players;
    private static boolean finishedShutdown = false;
    private NioEventLoopGroup bossGroup;
    private NioEventLoopGroup workerGroup;
    private ServerBootstrap sb;

    @Override
    public void run() {
        ip = ServerConstants.HOST + ":" + PORT;

        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();
        sb = new ServerBootstrap();

        sb.group(bossGroup, workerGroup);
        sb.channel(NioServerSocketChannel.class);
        sb.childHandler(new ChannelInitializer<SocketChannel>() {

            @Override
            protected void initChannel(SocketChannel c) throws Exception {
                c.pipeline().addLast(new PacketDecoder(), new MapleServerHandler(ServerMode.MapleServerMode.FARM), new PacketEncoder());
            }
        });

        //sb.option(ChannelOption.SO_BACKLOG, Configuration.MAXIMUM_CONNECTIONS);
        sb.childOption(ChannelOption.TCP_NODELAY, true);
        sb.childOption(ChannelOption.SO_KEEPALIVE, true);

        players = new PlayerStorage(-30);
        try {
            ChannelFuture f = sb.bind(PORT).sync();
            Channel serverChannel = f.channel();
            System.out.println("[Info] Farm Server is listening on port " + PORT + ".");
            serverChannel.closeFuture().sync();
        } catch (Exception e) {
            System.err.println("[Info] Farm Could not bind to port " + PORT + ": " + e);
            throw new RuntimeException("Binding failed.", e);
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
            System.out.printf("[Info] Login Server has been unbound from port %s.%n", PORT);
        }
    }

    public static String getIP() {
        return ip;
    }

    public static PlayerStorage getPlayerStorage() {
        return players;
    }

    public void shutdown() {
        if (finishedShutdown) {
            return;
        }
        System.out.println("Saving all connected clients (Farm)...");
        players.disconnectAll();
        System.out.println("Shutting down Farm...");
        workerGroup.shutdownGracefully();
        bossGroup.shutdownGracefully();

        finishedShutdown = true;
    }

    public static boolean isShutdown() {
        return finishedShutdown;
    }

    public static FarmServer getInstance() {
        if (instance == null) {
            instance = new FarmServer();
        }
        return instance;
    }
}
