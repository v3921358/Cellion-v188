package service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import constants.ServerConstants;
import handling.MapleServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import service.ServerMode.MapleServerMode;
import netty.PacketDecoder;
import netty.PacketEncoder;
import server.LoginAuthorization;

public class LoginServer extends Thread {

    public static final int PORT = 8484;
    private static LoginServer instance;
    private Map<Integer, Integer> load = new HashMap<>();
    private String serverName, eventMessage;
    private byte flag;
    private int maxCharacters;
    private int userLimit, usersOn = 0;
    private static final HashMap<Integer, LoginAuthorization> loginAuth = new HashMap<>();
    private static final HashSet<String> loginIPAuth = new HashSet<>();
    private NioEventLoopGroup bossGroup;
    private NioEventLoopGroup workerGroup;
    private ServerBootstrap sb;
    private Channel serverChannel;

    public LoginServer() {
        userLimit = ServerConstants.USER_LIMIT;
        serverName = ServerConstants.SERVER_NAME;
        eventMessage = ServerConstants.EVENT_MESSAGE;
        flag = ServerConstants.FLAGS;
        maxCharacters = ServerConstants.CHARACTER_LIMIT;
    }

    public static void putLoginAuth(int chrid, String ip, String tempIP, int channel, long randomLoginAuthCookie) {
        LoginAuthorization put = loginAuth.put(chrid, new LoginAuthorization(ip, tempIP, channel, randomLoginAuthCookie));
        loginIPAuth.add(ip);
    }

    public static LoginAuthorization getLoginAuth(int chrid) {
        return loginAuth.remove(chrid);
    }

    public static boolean containsIPAuth(String ip) {
        return loginIPAuth.contains(ip);
    }

    public static void removeIPAuth(String ip) {
        loginIPAuth.remove(ip);
    }

    public static void addIPAuth(String ip) {
        loginIPAuth.add(ip);
    }

    public final void addChannel(final int channel) {
        load.put(channel, 0);
    }

    public final void removeChannel(final int channel) {
        load.remove(channel);
    }

    @Override
    public void run() {
        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();
        sb = new ServerBootstrap();

        sb.group(bossGroup, workerGroup);
        sb.channel(NioServerSocketChannel.class);
        sb.childHandler(new ChannelInitializer<SocketChannel>() {

            @Override
            protected void initChannel(SocketChannel c) throws Exception {
                c.pipeline().addLast(new PacketDecoder(), new MapleServerHandler(MapleServerMode.LOGIN), new PacketEncoder());
            }
        });

        sb.option(ChannelOption.SO_BACKLOG, 1000);
        sb.childOption(ChannelOption.TCP_NODELAY, true);
        sb.childOption(ChannelOption.SO_KEEPALIVE, true);

        try {
            ChannelFuture f = sb.bind(PORT).sync();
            serverChannel = f.channel();
            System.out.println("[Info] Login Server is listening on port " + PORT + ".");
            serverChannel.closeFuture().sync();
        } catch (Exception e) {
            System.err.println("[Info] LoginServer Could not bind to port " + PORT + ": " + e);
            throw new RuntimeException(e);
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
            System.out.printf("[Info] Login Server has been unbound from port %s.%n", PORT);
        }
    }

    public void shutdown() {
        System.out.println("Shutting down login...");
        ChannelFuture sf = serverChannel.close();
        sf.awaitUninterruptibly();
        workerGroup.shutdownGracefully();
        bossGroup.shutdownGracefully();
    }

    public final String getServerName() {
        return serverName;
    }

    public final String getTrueServerName() {
        return serverName.substring(0, serverName.length() - 2);
    }

    public String getEventMessage() {
        return eventMessage;
    }

    public int getMaxCharacters() {
        return maxCharacters;
    }

    public Map<Integer, Integer> getLoad() {
        return load;
    }

    public void setLoad(final Map<Integer, Integer> load, final int usersOn) {
        this.load = load;
        this.usersOn = usersOn;
    }

    public static String getEventMessage(int world) { //TODO: Finish this
        switch (world) {
            case 0:
                return null;
        }
        return null;
    }

    public void setFlag(byte newflag) {
        flag = newflag;
    }

    public int getUserLimit() {
        return userLimit;
    }

    public int getUsersOn() {
        return usersOn;
    }

    public void setUserLimit(int newLimit) {
        userLimit = newLimit;
    }

    public int getNumberOfSessions() {
        return workerGroup.executorCount();
    }

    public static LoginServer getInstance() {
        if (instance == null) {
            instance = new LoginServer();
        }
        return instance;
    }
}
