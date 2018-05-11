package service;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import constants.ServerConstants;
import constants.WorldConstants.WorldOption;
import handling.ServerHandler;
import handling.game.PlayerStorage;
import handling.world.CheaterData;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import net.OutPacket;
import net.PacketDecoder;
import net.PacketEncoder;

import scripting.provider.EventScriptManager;
import server.MapleSquad;
import server.MapleSquad.MapleSquadType;
import server.events.MapleCoconut;
import server.events.MapleEvent;
import server.events.MapleEventType;
import server.events.MapleFitness;
import server.events.MapleOla;
import server.events.MapleOxQuiz;
import server.events.MapleSnowball;
import server.events.MapleSurvival;
import server.life.PlayerNPC;
import server.maps.AramiaFireWorks;
import server.maps.MapleMapFactory;
import server.maps.MapleMapObject;
import server.maps.MapleMapObjectType;
import server.maps.objects.User;
import server.stores.HiredMerchant;
import tools.ConcurrentEnumMap;
import tools.packet.WvsContext;

public class ChannelServer extends Thread {

    public static long serverStartTime;
    private final float cashRate = 1, traitRate = 10, BossDropRate = 3;
    private short port = 8585;
    private static final short DEFAULT_PORT = 8584;
    private int channel, running_MerchantID = 0, flags = 0;
    private String serverMessage, ip, serverName;
    private boolean shutdown = false, finishedShutdown = false, MegaphoneMuteState = false;
    private PlayerStorage players;
    private final MapleMapFactory mapFactory;
    private EventScriptManager eventSM;
    private final AramiaFireWorks works = new AramiaFireWorks();
    private static final Map<Integer, ChannelServer> instances = new HashMap<>();
    private final Map<MapleSquadType, MapleSquad> mapleSquads = new ConcurrentEnumMap<>(MapleSquadType.class);
    private final Map<Integer, HiredMerchant> merchants = new HashMap<>();
    private final List<PlayerNPC> playerNPCs = new LinkedList<>();
    private final ReentrantReadWriteLock merchLock = new ReentrantReadWriteLock(); //merchant
    private int eventmap = -1;
    private final Map<MapleEventType, MapleEvent> events = new EnumMap<>(MapleEventType.class);
    public boolean eventOn = false;
    public int eventMap = 0;
    private boolean eventWarp;
    private String eventHost;
    private String eventName;
    private boolean manualEvent = false;
    private int manualEventMap = 0;
    private boolean bomberman = false;
    private int world = 1;
    private NioEventLoopGroup bossGroup;
    private NioEventLoopGroup workerGroup;
    private ServerBootstrap sb;

    private ChannelServer(final int channel) {
        this.channel = channel;
        //this.world = world; //fuck lithium with no multiworld (only fake multiworld)
        mapFactory = new MapleMapFactory(channel);
    }

    public static Set<Integer> getAllInstance() {
        return new HashSet<>(instances.keySet());
    }

    public final void loadEvents() {
        if (!events.isEmpty()) {
            return;
        }
        events.put(MapleEventType.CokePlay, new MapleCoconut(channel, MapleEventType.CokePlay)); //yep, coconut. same shit
        events.put(MapleEventType.Coconut, new MapleCoconut(channel, MapleEventType.Coconut));
        events.put(MapleEventType.Fitness, new MapleFitness(channel, MapleEventType.Fitness));
        events.put(MapleEventType.OlaOla, new MapleOla(channel, MapleEventType.OlaOla));
        events.put(MapleEventType.OxQuiz, new MapleOxQuiz(channel, MapleEventType.OxQuiz));
        events.put(MapleEventType.Snowball, new MapleSnowball(channel, MapleEventType.Snowball));
        events.put(MapleEventType.Survival, new MapleSurvival(channel, MapleEventType.Survival));
    }

    @Override
    public void run() {
        setChannel(channel); //instances.put
        try {
            serverMessage = ServerConstants.SERVER_MESSAGE;
            serverName = ServerConstants.SERVER_NAME;
            flags = ServerConstants.FLAGS;
            eventSM = new EventScriptManager(this, ServerConstants.events.split(","));
            port = (short) (DEFAULT_PORT + channel);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        ip = ServerConstants.HOST + ":" + port;

        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();
        sb = new ServerBootstrap();

        sb.group(bossGroup, workerGroup);
        sb.channel(NioServerSocketChannel.class);
        sb.childHandler(new ChannelInitializer<SocketChannel>() {

            @Override
            protected void initChannel(SocketChannel c) throws Exception {
                c.pipeline().addLast(new PacketDecoder(), new ServerHandler(ServerMode.MapleServerMode.GAME), new PacketEncoder());
            }
        });

        //sb.option(ChannelOption.SO_BACKLOG, Configuration.MAXIMUM_CONNECTIONS);
        sb.childOption(ChannelOption.TCP_NODELAY, true);
        sb.childOption(ChannelOption.SO_KEEPALIVE, true);
        players = new PlayerStorage(channel);
        loadEvents();

        try {
            ChannelFuture f = sb.bind(port).sync();
            Channel serverChannel = f.channel();
            System.out.println("[Info] Channel " + channel + " is listening on port " + port + ".");
            serverChannel.closeFuture().sync();
        } catch (Exception e) {
            System.err.println("[Info] Channel " + channel + "  Could not bind to port " + port + ": " + e);
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
            System.out.printf("[Info] Login Server has been unbound from port %s.%n", port);
        }
    }

    public final void shutdown() {
        if (finishedShutdown) {
            return;
        }
        broadcastPacket(WvsContext.broadcastMsg(0, "This channel will now shut down."));
        // dc all clients by hand so we get sessionClosed...
        shutdown = true;

        System.out.println("Channel " + channel + ", Saving characters...");

        getPlayerStorage().disconnectAll();

        System.out.println("Channel " + channel + ", Unbinding...");

        //temporary while we dont have !addchannel
        instances.remove(channel);
        workerGroup.shutdownGracefully();
        bossGroup.shutdownGracefully();
        setFinishShutdown();
    }

    public final boolean hasFinishedShutdown() {
        return finishedShutdown;
    }

    public final MapleMapFactory getMapFactory() {
        return mapFactory;
    }

    public static final ChannelServer getInstance(final int channel) {
        if (instances.get(channel) == null) {
            instances.put(channel, new ChannelServer(channel));
        }
        return instances.get(channel);
    }

    public final void addPlayer(final User chr) {
        getPlayerStorage().registerPlayer(chr);
    }

    public final PlayerStorage getPlayerStorage() {
        if (players == null) { //wth
            players = new PlayerStorage(channel); //wthhhh
        }
        return players;
    }

    public final void removePlayer(final User chr) {
        getPlayerStorage().deregisterPlayer(chr);

    }

    public final void removePlayer(final int idz, final String namez) {
        getPlayerStorage().deregisterPlayer(idz, namez);

    }

    public final String getServerMessage() {
        return serverMessage;
    }

    public final void setServerMessage(final String newMessage) {
        serverMessage = newMessage;
        broadcastPacket(WvsContext.broadcastMsg(serverMessage));
    }

    public final void broadcastPacket(final OutPacket data) {
        getPlayerStorage().broadcastPacket(data);
    }

    public final void broadcastSmegaPacket(final OutPacket data) {
        getPlayerStorage().broadcastSmegaPacket(data);
    }

    public final void broadcastGMPacket(final OutPacket data) {
        getPlayerStorage().broadcastGMPacket(data);
    }

    public final void broadcastWhisperPacket(final OutPacket data, String msgDestination) {
        getPlayerStorage().broadcastWhisperPacket(data, msgDestination);
    }

    public final float getExpRate(int world) {
        return WorldOption.getById(world).getExp();
    }

    public final float getMiracleCubeRate(int world) {
        return WorldOption.getById(world).getMiracleCube();
    }

    public float getCashRate() {
        return cashRate;
    }

    public int getChannel() {
        return channel;
    }

    public final void setChannel(final int channel) {
        instances.put(channel, this);
        LoginServer.getInstance().addChannel(channel);
    }

    public static ArrayList<ChannelServer> getAllInstances() {
        return new ArrayList<>(instances.values());
    }

    public final String getIP() {
        return ip;
    }

    public final boolean isShutdown() {
        return shutdown;
    }

    public final int getLoadedMaps() {
        return mapFactory.getLoadedMaps();
    }

    public final EventScriptManager getEventSM() {
        return eventSM;
    }

    public final void reloadEvents() {
        eventSM.cancel();
        eventSM = new EventScriptManager(this, ServerConstants.events.split(","));
        eventSM.init();
    }

    public final float getMesoRate(int world) {
        return WorldOption.getById(world).getMeso();
    }

    public final float getDropRate(int world) {
        return WorldOption.getById(world).getDrop();
    }

    public final float getBossDropRate() {
        return BossDropRate;
    }

    public static void startChannel_Main() {
        serverStartTime = System.currentTimeMillis();

        for (int i = 0; i < ServerConstants.CHANNEL_COUNT; i++) {
            getInstance(i + 1).start();
        }
    }

    public Map<MapleSquadType, MapleSquad> getAllSquads() {
        return Collections.unmodifiableMap(mapleSquads);
    }

    public final MapleSquad getMapleSquad(final String type) {
        return getMapleSquad(MapleSquadType.valueOf(type.toLowerCase()));
    }

    public final MapleSquad getMapleSquad(final MapleSquadType type) {
        return mapleSquads.get(type);
    }

    public final boolean addMapleSquad(final MapleSquad squad, final String type) {
        final MapleSquadType types = MapleSquadType.valueOf(type.toLowerCase());
        if (types != null && !mapleSquads.containsKey(types)) {
            mapleSquads.put(types, squad);
            squad.scheduleRemoval();
            return true;
        }
        return false;
    }

    public final boolean removeMapleSquad(final MapleSquadType types) {
        if (types != null && mapleSquads.containsKey(types)) {
            mapleSquads.remove(types);
            return true;
        }
        return false;
    }

    public final int closeAllMerchant() {
        int ret = 0;
        merchLock.writeLock().lock();
        try {
            final Iterator<Entry<Integer, HiredMerchant>> merchants_ = merchants.entrySet().iterator();
            while (merchants_.hasNext()) {
                HiredMerchant hm = merchants_.next().getValue();
                hm.closeShop(true, false);
                //HiredMerchantSave.QueueShopForSave(hm);
                hm.getMap().removeMapObject(hm);
                merchants_.remove();
                ret++;
            }
        } finally {
            merchLock.writeLock().unlock();
        }
        //hacky
        for (int i = 910000001; i <= 910000022; i++) {
            for (MapleMapObject mmo : mapFactory.getMap(i).getAllMapObjects(MapleMapObjectType.HIRED_MERCHANT)) {
                ((HiredMerchant) mmo).closeShop(true, false);
                //HiredMerchantSave.QueueShopForSave((HiredMerchant) mmo);
                ret++;
            }
        }
        return ret;
    }

    public final int addMerchant(final HiredMerchant hMerchant) {
        merchLock.writeLock().lock();
        try {
            running_MerchantID++;
            merchants.put(running_MerchantID, hMerchant);
            return running_MerchantID;
        } finally {
            merchLock.writeLock().unlock();
        }
    }

    public final void removeMerchant(final HiredMerchant hMerchant) {
        merchLock.writeLock().lock();

        try {
            merchants.remove(hMerchant.getStoreId());
        } finally {
            merchLock.writeLock().unlock();
        }
    }

    public final boolean containsMerchant(final int accid, int cid) {
        boolean contains = false;

        merchLock.readLock().lock();
        try {
            final Iterator itr = merchants.values().iterator();

            while (itr.hasNext()) {
                HiredMerchant hm = (HiredMerchant) itr.next();
                if (hm.getOwnerAccId() == accid || hm.getOwnerId() == cid) {
                    contains = true;
                    break;
                }
            }
        } finally {
            merchLock.readLock().unlock();
        }
        return contains;
    }

    public final List<HiredMerchant> searchMerchant(final int itemSearch) {
        final List<HiredMerchant> list = new LinkedList<>();
        merchLock.readLock().lock();
        try {
            final Iterator itr = merchants.values().iterator();

            while (itr.hasNext()) {
                HiredMerchant hm = (HiredMerchant) itr.next();
                if (hm.searchItem(itemSearch).size() > 0) {
                    list.add(hm);
                }
            }
        } finally {
            merchLock.readLock().unlock();
        }
        return list;
    }

    public final void toggleMegaphoneMuteState() {
        this.MegaphoneMuteState = !this.MegaphoneMuteState;
    }

    public final boolean getMegaphoneMuteState() {
        return MegaphoneMuteState;
    }

    public int getEvent() {
        return eventmap;
    }

    public final void setEvent(final int ze) {
        this.eventmap = ze;
    }

    public MapleEvent getEvent(final MapleEventType t) {
        return events.get(t);
    }

    public final Collection<PlayerNPC> getAllPlayerNPC() {
        return playerNPCs;
    }

    public final void addPlayerNPC(final PlayerNPC npc) {
        if (playerNPCs.contains(npc)) {
            return;
        }
        playerNPCs.add(npc);
        getMapFactory().getMap(npc.getMapId()).addMapObject(npc);
    }

    public final void removePlayerNPC(final PlayerNPC npc) {
        if (playerNPCs.contains(npc)) {
            playerNPCs.remove(npc);
            getMapFactory().getMap(npc.getMapId()).removeMapObject(npc);
        }
    }

    public final String getServerName() {
        return serverName;
    }

    public final void setServerName(final String sn) {
        this.serverName = sn;
    }

    public final String getTrueServerName() {
        return serverName.substring(0, serverName.length() - 2);
    }

    public final int getPort() {
        return port;
    }

    public static final Set<Integer> getChannelServer() {
        return new HashSet<>(instances.keySet());
    }

    public final void setShutdown() {
        this.shutdown = true;
        System.out.println("Channel " + channel + " has set to shutdown and is closing Hired Merchants...");
    }

    public final void setFinishShutdown() {
        this.finishedShutdown = true;
        System.out.println("Channel " + channel + " has finished shutdown.");
    }

    public final static int getChannelCount() {
        return instances.size();
    }

    public final int getTempFlag() {
        return flags;
    }

    public static Map<Integer, Integer> getChannelLoad() {
        Map<Integer, Integer> ret = new HashMap<>();
        for (ChannelServer cs : instances.values()) {
            ret.put(cs.getChannel(), cs.getConnectedClients());
        }
        return ret;
    }

    public int getConnectedClients() {
        return getPlayerStorage().getConnectedClients();
    }

    public List<CheaterData> getCheaters() {
        List<CheaterData> cheaters = getPlayerStorage().getCheaters();

        Collections.sort(cheaters);
        return cheaters;
    }

    public List<CheaterData> getReports() {
        List<CheaterData> cheaters = getPlayerStorage().getReports();

        Collections.sort(cheaters);
        return cheaters;
    }

    public void broadcastMessage(OutPacket message) {
        broadcastPacket(message);
    }

    public void broadcastSmega(OutPacket message) {
        broadcastSmegaPacket(message);
    }

    public void broadcastGMMessage(OutPacket message) {
        broadcastGMPacket(message);
    }

    public void broadcastWhisper(OutPacket message, String msgDestination) {
        broadcastWhisperPacket(message, msgDestination);
    }

    public AramiaFireWorks getFireWorks() {
        return works;
    }

    public float getTraitRate() {
        return traitRate;
    }

    public boolean manualEvent(User chr) {
        if (manualEvent) {
            manualEvent = false;
            manualEventMap = 0;
        } else {
            manualEvent = true;
            manualEventMap = chr.getMapId();
        }
        if (manualEvent) {
            chr.dropMessage(5, "Manual event has " + (manualEvent ? "began" : "begone") + ".");
        }
        return manualEvent;
    }

    public void warpToEvent(User chr) {
        if (!manualEvent || manualEventMap <= 0) {
            chr.dropMessage(5, "Sorry, there is currently no event being hosted.");
            return;
        }
        chr.dropMessage(5, "You are being warped into the event!");
        chr.changeMap(manualEventMap, 0);
    }

    public boolean bombermanActive() {
        return bomberman;
    }

    public void toggleBomberman(User chr) {
        bomberman = !bomberman;
        if (bomberman) {
            chr.dropMessage(5, "Bomberman Event is active.");
        } else {
            chr.dropMessage(5, "Bomberman Event is not active.");
        }
    }
}
