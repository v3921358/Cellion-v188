package service;

import enums.ServerMode;
import java.net.InetSocketAddress;

import constants.ServerConstants;
import handling.ServerHandler;
import handling.game.PlayerStorage;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import net.PacketDecoder;
import net.PacketEncoder;

public class CashShopServer extends Thread {

    private static CashShopServer instance;
    private static String ip;
    private static InetSocketAddress InetSocketadd;
    private final static int PORT = 8610;
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
                c.pipeline().addLast(new PacketDecoder(), new ServerHandler(ServerMode.MapleServerMode.CS), new PacketEncoder());
            }
        });

        //sb.option(ChannelOption.SO_BACKLOG, Configuration.MAXIMUM_CONNECTIONS);
        sb.childOption(ChannelOption.TCP_NODELAY, true);
        sb.childOption(ChannelOption.SO_KEEPALIVE, true);

        players = new PlayerStorage(-10);

        try {
            ChannelFuture f = sb.bind(PORT).sync();
            Channel serverChannel = f.channel();
            System.out.println("[Info] CashShop Server is listening on port " + PORT + ".");
            serverChannel.closeFuture().sync();
        } catch (Exception e) {
            System.err.println("[Info] CashShop Could not bind to port " + PORT + ": " + e);
            throw new RuntimeException("Binding failed.", e);
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
            System.out.printf("[Info] CashShop Server has been unbound from port %s.%n", PORT);
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
        System.out.println("Saving all connected clients (CS)...");
        players.disconnectAll();
        System.out.println("Shutting down CS...");
        workerGroup.shutdownGracefully();
        bossGroup.shutdownGracefully();
        finishedShutdown = true;
    }

    public static boolean isShutdown() {
        return finishedShutdown;
    }

    public static CashShopServer getInstance() {
        if (instance == null) {
            instance = new CashShopServer();
        }
        return instance;
    }
}
