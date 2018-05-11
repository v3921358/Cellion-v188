package service;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

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

/**
 *
 * @author Novak
 */
public class MapleTalkServer extends Thread {

    private static MapleTalkServer instance;
    private final int worldid;
    private static String ip, worldname;
    private static InetSocketAddress InetSocketadd;
    private static final int PORT = 8787;
    private static final Map<Integer, MapleTalkServer> instances = new HashMap<>();
    private static PlayerStorage players;
    private static boolean finishedShutdown = false;
    private NioEventLoopGroup bossGroup;
    private NioEventLoopGroup workerGroup;
    private ServerBootstrap sb;

    public static final MapleTalkServer newMapleTalkServer(final int worldid, String worldname) {
        return new MapleTalkServer(worldid, worldname);
    }

    private MapleTalkServer(final int worldid, String worldname) {
        this.worldid = worldid;
        this.worldname = worldname;
    }

    public final void setWorldId(final int worldid) {
        instances.put(worldid, this);
    }

    @Override
    public void run() {
        setWorldId(worldid);
        int serverPort = PORT + worldid;
        ip = ServerConstants.HOST + ":" + serverPort;

        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();
        sb = new ServerBootstrap();

        sb.group(bossGroup, workerGroup);
        sb.channel(NioServerSocketChannel.class);
        sb.childHandler(new ChannelInitializer<SocketChannel>() {

            @Override
            protected void initChannel(SocketChannel c) throws Exception {
                c.pipeline().addLast(new PacketDecoder(), new ServerHandler(ServerMode.MapleServerMode.MAPLETALK), new PacketEncoder());
            }
        });

        //sb.option(ChannelOption.SO_BACKLOG, Configuration.MAXIMUM_CONNECTIONS);
        sb.childOption(ChannelOption.TCP_NODELAY, true);
        sb.childOption(ChannelOption.SO_KEEPALIVE, true);

        players = new PlayerStorage(-40 - worldid);
        try {
            ChannelFuture f = sb.bind(serverPort).sync();
            Channel serverChannel = f.channel();
            System.out.println("[Info] MapleTalk Server " + worldname + " is listening on port " + PORT + ".");
            serverChannel.closeFuture().sync();
        } catch (Exception e) {
            System.err.println("[Info] MapleTalk Server " + worldname + " Could not bind to port " + PORT + ": " + e);
            throw new RuntimeException("Binding failed.", e);
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
            System.out.printf("[Info]  MapleTalk Server " + worldname + " has been unbound from port %s.%n", PORT);
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
        System.out.println("Saving all connected clients (MapleTalkServer)...");
        players.disconnectAll();
        System.out.println("Shutting down MapleTalkServer...");
        workerGroup.shutdownGracefully();
        bossGroup.shutdownGracefully();

        finishedShutdown = true;
    }

    public static boolean isShutdown() {
        return finishedShutdown;
    }

    public static MapleTalkServer getInstance(int worldid, String worldname) {
        if (instance == null) {
            instance = new MapleTalkServer(worldid, worldname);
        }
        return instance;
    }
}
