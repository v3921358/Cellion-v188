/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import constants.ServerConstants;
import enums.ServerMode;
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
import java.net.InetSocketAddress;
import net.PacketDecoder;
import net.PacketEncoder;

/**
 *
 * @author William
 */
public class AuctionServer extends Thread {
    private static AuctionServer instance;
    private static String ip;
    private static InetSocketAddress InetSocketadd;
    private static final int PORT = 8612;
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
                c.pipeline().addLast(new PacketDecoder(), new ServerHandler(ServerMode.MapleServerMode.AUCTION), new PacketEncoder());
            }
        });

        //sb.option(ChannelOption.SO_BACKLOG, Configuration.MAXIMUM_CONNECTIONS);
        sb.childOption(ChannelOption.TCP_NODELAY, true);
        sb.childOption(ChannelOption.SO_KEEPALIVE, true);

        players = new PlayerStorage(-60);
        try {
            ChannelFuture f = sb.bind(PORT).sync();
            Channel serverChannel = f.channel();
            System.out.println("[Info] Auction Server is listening on port " + PORT + ".");
            serverChannel.closeFuture().sync();
        } catch (Exception e) {
            System.err.println("[Info] Auction Could not bind to port " + PORT + ": " + e);
            throw new RuntimeException("Binding failed.", e);
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
            System.out.printf("[Info] Auction Server has been unbound from port %s.%n", PORT);
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
        System.out.println("Saving all connected clients (Auction)...");
        players.disconnectAll();
        System.out.println("Shutting down Auction...");
        workerGroup.shutdownGracefully();
        bossGroup.shutdownGracefully();

        finishedShutdown = true;
    }

    public static boolean isShutdown() {
        return finishedShutdown;
    }

    public static AuctionServer getInstance() {
        if (instance == null) {
            instance = new AuctionServer();
        }
        return instance;
    }
}
