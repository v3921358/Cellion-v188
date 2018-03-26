package server;

import server.potentials.ItemPotentialProvider;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicInteger;

import client.SkillFactory;
import client.inventory.MapleInventoryIdentifier;
import constants.GameConstants;
import constants.JobConstants;
import constants.ServerConstants;
import constants.WorldConstants;
import constants.WorldConstants.TespiaWorldOption;
import constants.WorldConstants.WorldOption;
import crypto.CAESCipher;
import database.DatabaseConnection;
import handling.world.MapleDojoRanking;
import handling.world.MapleGuildRanking;
import handling.login.LoginInformationProvider;
import handling.world.World;
import handling.world.MapleFamily;
import handling.world.MapleGuild;
import java.io.FileInputStream;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import service.CashShopServer;
import service.ChannelServer;
import service.FarmServer;
import service.LoginServer;
import service.MapleTalkServer;
import provider.wz.cache.WzDataStorage;
import server.Timer.BuffTimer;
import server.Timer.CloneTimer;
import server.Timer.EtcTimer;
import server.Timer.EventTimer;
import server.Timer.MapTimer;
import server.Timer.PingTimer;
import server.Timer.WorldTimer;
import server.skills.effects.manager.EffectManager;
import server.events.MapleOxQuizFactory;
import server.life.MapleLifeFactory;
import server.life.MapleMonsterInformationProvider;
import server.life.PlayerNPC;
import server.maps.MapleMapFactory;
import server.maps.objects.MaplePet;
import server.quest.MapleQuest;
import server.skills.VCore;

public class Start {

    public static long startTime = System.currentTimeMillis();
    public static final Start instance = new Start();
    public static AtomicInteger CompletedLoadingThreads = new AtomicInteger(0);

    public void run() throws InterruptedException, IOException {
        long start = System.currentTimeMillis();

        /*Configuration Start*/
        Properties config = new Properties();
        try {
            config.load(new FileInputStream("configuration.ini"));
            System.out.println("\nConfiguration Initialized");
        } catch (IOException ex) {
            System.out.println("\nFailed to load data from configuration.ini");
            System.exit(0);
        }

        /*Setting Server Rates*/
        ServerConstants.EXP_RATE = Float.valueOf(config.getProperty("EXP_RATE"));
        ServerConstants.MESO_RATE = Float.valueOf(config.getProperty("MESO_RATE"));
        ServerConstants.DROP_RATE = Float.valueOf(config.getProperty("DROP_RATE"));

        /*Setting Debug Configuration*/
        ServerConstants.DEVELOPER_DEBUG_MODE = Boolean.valueOf(config.getProperty("DEBUG"));
        ServerConstants.DEVELOPER_PACKET_DEBUG_MODE = Boolean.valueOf(config.getProperty("PACKET_DEBUG"));

        /*Setting Maintenance Configuration*/
        ServerConstants.MAINTENANCE = Boolean.valueOf(config.getProperty("MAINTENANCE"));
        ServerConstants.MAINTENANCE_LEVEL = Integer.valueOf(config.getProperty("LOGIN_REQUIRED_GM_LEVEL"));

        /*Charcter Creation Configuration*/
        JobConstants.enableResistance = Boolean.valueOf(config.getProperty("RESISTANCE"));
        JobConstants.enableAdventurer = Boolean.valueOf(config.getProperty("EXPLORERS"));
        JobConstants.enableCygnus = Boolean.valueOf(config.getProperty("CYGNUS"));
        JobConstants.enableAran = Boolean.valueOf(config.getProperty("ARAN"));
        JobConstants.enableEvan = Boolean.valueOf(config.getProperty("EVAN"));
        JobConstants.enableMercedes = Boolean.valueOf(config.getProperty("MERCEDES"));
        JobConstants.enableDemon = Boolean.valueOf(config.getProperty("DEMON"));
        JobConstants.enablePhantom = Boolean.valueOf(config.getProperty("PHANTOM"));
        JobConstants.enableDualBlade = Boolean.valueOf(config.getProperty("DUALBLADE"));
        JobConstants.enableMihile = Boolean.valueOf(config.getProperty("MIHILE"));
        JobConstants.enableLuminous = Boolean.valueOf(config.getProperty("LUMINOUS"));
        JobConstants.enableKaiser = Boolean.valueOf(config.getProperty("KAISER"));
        JobConstants.enableAngelicBuster = Boolean.valueOf(config.getProperty("ANGELICBUSTER"));
        JobConstants.enableCannoneer = Boolean.valueOf(config.getProperty("CANNONEER"));
        JobConstants.enableXenon = Boolean.valueOf(config.getProperty("XENON"));
        JobConstants.enableZero = Boolean.valueOf(config.getProperty("ZERO"));
        JobConstants.enableShade = Boolean.valueOf(config.getProperty("SHADE"));
        JobConstants.enableJett = Boolean.valueOf(config.getProperty("JETT"));
        JobConstants.enableHayato = Boolean.valueOf(config.getProperty("HAYATO"));
        JobConstants.enableKanna = Boolean.valueOf(config.getProperty("KANNA"));
        JobConstants.enableBeastTamer = Boolean.valueOf(config.getProperty("CHASE"));
        JobConstants.enableKinesis = Boolean.valueOf(config.getProperty("KINESIS"));
        /*Configuration End*/

        System.setProperty("wzpath", "wz");
        try {
            try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement("UPDATE accounts SET loggedin = 0")) {
                ps.executeUpdate();
                System.out.println("Database Connection Established");
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Runtime Exception - Could not connect to MySql Server.");
        }

        System.out.println("\nLoading " + ServerConstants.SERVER_NAME + ": MAPLESTORY VERSION " + ServerConstants.MAPLE_VERSION);

        if (ServerConstants.MAINTENANCE) {
            System.out.println("Server Maintenance Enabled: GM Level " + ServerConstants.MAINTENANCE_LEVEL + " Required");
        }

        CAESCipher.Initialize(ServerConstants.MAPLE_VERSION);
        World.init();
        System.out.println("\nHost IP: " + ServerConstants.HOST);
        System.out.println("Port: " + LoginServer.PORT);

        int servers = 0;
        if (ServerConstants.TESPIA) {
            for (TespiaWorldOption server : TespiaWorldOption.values()) {
                if (server.show()) {
                    servers++;
                }
            }
        } else {
            for (WorldOption server : WorldOption.values()) {
                if (server.show()) {
                    servers++;
                }
            }
        }
        System.out.println("\nWorlds Loaded: " + servers + "/" + (ServerConstants.TESPIA ? TespiaWorldOption.values().length : WorldOption.values().length));
        System.out.println("Experience Multiplier: " + ServerConstants.EXP_RATE);
        System.out.println("Meso Multiplier: " + ServerConstants.MESO_RATE);
        System.out.println("Drop Multiplier: " + ServerConstants.DROP_RATE + "\r\n");
        boolean encryptionfound = false;

        // Load WZ files
        WzDataStorage.load();

        /////////////////////////////////////////////////////
        //////////////// Setup an executor for server startup
        final ExecutorService es = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors(), (Runnable r) -> {
            final Thread t = new Thread(r);
            t.setName("Server startup executors worker");
            return t;
        });
        final AtomicInteger atomicInteger = new AtomicInteger(0);

        ServerStartupRunnable r = new ServerStartupRunnable(atomicInteger, ()
                -> {
            WorldTimer.getInstance().start();
            EtcTimer.getInstance().start();
            MapTimer.getInstance().start();
            CloneTimer.getInstance().start();
            EventTimer.getInstance().start();
            BuffTimer.getInstance().start();
            PingTimer.getInstance().start();
        }, "All Timers");
        es.submit(r);

        r = new ServerStartupRunnable(atomicInteger, ()
                -> {
            GameConstants.LoadEXP();
        }, "EXP Curve");
        es.submit(r);

        r = new ServerStartupRunnable(atomicInteger, ()
                -> {
            MapleDojoRanking.getInstance().load();
            MapleGuildRanking.getInstance().load();
        }, "Rankings");
        es.submit(r);

        r = new ServerStartupRunnable(atomicInteger, ()
                -> {
            ItemPotentialProvider.initialize();

        }, "Potential");
        es.submit(r);

        r = new ServerStartupRunnable(atomicInteger, ()
                -> {
            MapleGuild.loadAll();
            MapleFamily.loadAll();
        }, "Guilds");
        es.submit(r);

        r = new ServerStartupRunnable(atomicInteger, ()
                -> {
            MapleQuest.initQuests();
            MapleItemInformationProvider.getInstance().runEtc();

        }, "Quests");
        es.submit(r);

        r = new ServerStartupRunnable(atomicInteger, ()
                -> {
            EffectManager.loadBuffEffectProviders();
            SkillFactory.load();
            VCore.Load();
        }, "Skill Information");
        es.submit(r);

        r = new ServerStartupRunnable(atomicInteger, ()
                -> {
            MapleItemInformationProvider.getInstance().runItems();
        }, "Maple Item Information");
        es.submit(r);

        r = new ServerStartupRunnable(atomicInteger, ()
                -> {
            LoginInformationProvider.getInstance();
        }, "Login Information");
        es.submit(r);

        r = new ServerStartupRunnable(atomicInteger, ()
                -> {
            RandomRewards.load();
        }, "Random Rewards");
        es.submit(r);

        r = new ServerStartupRunnable(atomicInteger, ()
                -> {
            MapleOxQuizFactory.getInstance();
        }, "Maple OX Quiz");
        es.submit(r);

        r = new ServerStartupRunnable(atomicInteger, ()
                -> {
            MapleCarnivalFactory.getInstance();
        }, "Maple Carnival Factory");
        es.submit(r);

        r = new ServerStartupRunnable(atomicInteger, ()
                -> {
            CharacterCardFactory.getInstance().initialize();
        }, "Character Card Factory");
        es.submit(r);

        r = new ServerStartupRunnable(atomicInteger, ()
                -> {
            SpeedRunner.loadSpeedRuns();
            MapleInventoryIdentifier.getInstance();
        }, "Events");
        es.submit(r);

        r = new ServerStartupRunnable(atomicInteger, ()
                -> {
            MapleLifeFactory.initialize();
            MapleMonsterInformationProvider.getInstance().load();
        }, "Maple NPCs & Mobs");
        es.submit(r);

        r = new ServerStartupRunnable(atomicInteger, ()
                -> {
            MapleStringInformationProvider.initialize();

            MapleMapFactory.initialize();
        }, "Maple Maps & String Pool");
        es.submit(r);

        r = new ServerStartupRunnable(atomicInteger, ()
                -> {
            MapleTamingMobProvider.initialize();
        }, "Taming Mobs");
        es.submit(r);

        r = new ServerStartupRunnable(atomicInteger, ()
                -> {
            CashItemFactory.getInstance().initialize();
        }, "Cash Item Factory");
        es.submit(r);

        r = new ServerStartupRunnable(atomicInteger, ()
                -> {
            LoginServer.getInstance().start();
            ChannelServer.startChannel_Main();
            CashShopServer.getInstance().start();
            FarmServer.getInstance().start();
            for (WorldOption server : WorldOption.values()) {
                if (server.show()) {
                    MapleTalkServer.getInstance(server.getWorld(), server.name()).start();
                }
            }
        }, "Servers");
        es.submit(r);

        while (atomicInteger.get() > 0) {
            Thread.sleep(200);
        }

        //Clear Broken Pets
        MaplePet.clearPet();
        System.out.println("[Info] Pets Cleaned.");

        ////////////////////// Multithread loader ends here.
        Runtime.getRuntime().addShutdownHook(new Thread(new Shutdown()));
        World.registerRespawn();
        ShutdownServer.registerMBean();
        PlayerNPC.loadAll();
        //MapleMonsterInformationProvider.getInstance().addExtra();
        RankingWorker.run();

        if (ServerConstants.DEVELOPER_PACKET_DEBUG_MODE) {
            System.out.println("[Info] Developer Packet debug mode is enabled.");
        }

        long now = System.currentTimeMillis() - start;
        long seconds = now / 1000;
        long ms = now % 1000;
        System.out.println("\n" + ServerConstants.SERVER_NAME + " started successfully in " + seconds + " seconds and " + ms + " milliseconds.\n");
    }

    public static class Shutdown implements Runnable {

        @Override
        public void run() {
            ShutdownServer.getInstance().run();
            //ShutdownServer.getInstance().run();
        }
    }

    private static class ServerStartupRunnable implements Runnable {

        private final AtomicInteger ai;
        private final Runnable r;
        private final String threadName;

        public ServerStartupRunnable(AtomicInteger ai, Runnable r, String threadName) {
            this.ai = ai;
            this.r = r;
            this.threadName = threadName;
        }

        @Override
        public void run() {
            ai.incrementAndGet(); // flag for thread count

            try {
                r.run();
            } catch (Throwable t) {
                t.printStackTrace();
            }
            System.out.println("[Info] " + threadName + " loaded.");

            ai.decrementAndGet(); // completed.
        }
    }

    public static void main(final String args[]) throws InterruptedException, IOException {
        if (args.length > 0) {
            if (args[0].equals("DEBUG_OPCODES")) {
            }
        } else {
            instance.run();
        }
    }
}
