package constants;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * Nox Project
 * Rexion Development

 * MapleStory Server Emulator
 * GMS Version 1.80.2
 * 
 * Project Lead Developer
 * @author Mazen Massoud
 *
 * Credits to all previous Nox Developers.
 */
public class ServerConstants {

    /*Server IP Address*/
    public static final String HOST = "rexion.mywire.org";

    /*Server Database Configuration*/
    public static String SQL_HOST = "127.0.0.1";
    public static String SQL_PORT = "3306";
    public static String SQL_DATABASE = "nox";
    public static String SQL_USER = "root";
    public static String SQL_PASSWORD = "?r3xionism3me!";

    /*Server Debug*/
 /*These values will be overwritten by the configuration.ini file.*/
    public static boolean DEVELOPER_DEBUG_MODE = true;
    public static boolean DEVELOPER_PACKET_DEBUG_MODE = true;
 /*This boolean filters out debug spam, not configured by the INI file.*/
    public static boolean REDUCED_DEBUG_SPAM = true;

    /*Server Maintenance*/
 /*These values will be overwritten by the configuration.ini file.*/
    public static boolean MAINTENANCE = false; //If true, only specified gm accounts can login.
    public static int MAINTENANCE_LEVEL = 3; //GM level needed to access server during maintenance.

    /*General Rate Configuration*/
 /*These values will be overwritten by the configuration.ini file.*/
    public static float EXP_RATE = 1;
    public static float MESO_RATE = 1;
    public static float DROP_RATE = 1;
    
    /*Server Save Intervals*/
 /*The server will automatically save all character data every n minutes.*/
    public static int SAVE_INTERVAL = 15;

    /*Master Password Configuration*/
    public static final boolean ENABLE_MASTER = false; //Leave disabled to avoid potential security leaks, feature works though.
    public static final String MASTER_PASSWORD = "";
    public static final String MASTER_PIC = "";

    /*Client Information*/
    public static final boolean TESPIA = false; // Used for activating GMST.
    public static final short MAPLE_VERSION = (short) 180;
    public static final String SERVER_NAME = "REXION"; //Shorter reference of just the server name.
    public static final String MAPLE_PATCH = "2";
    public static final int MAPLE_LOCALE = 8;

    /*General Configuration*/
    public static final String SOURCE_REVISION = "0.9";
    public static final boolean OLD_MAPS = false; //example: it will use old maple event's henesys instead of current one
    public static final boolean LOCALHOST = false; //true = packets are logged, false = others can connect to server
    public static final boolean LOG_SHARK = false;  //true = enable shark to log 
    public static boolean MULTI_LEVEL = true; //true = enable multi leveling
    public static final byte SHOP_DISCOUNT = 0; //Discount Rate (%) Higher than 100 or lower than 1 means no discount
    public static boolean CS_COUPONS = false; //Disable the purchase of exp/drop rate coupons from cash shop.
    public static boolean HIDE_STAR_PLANET_WORLD_UI = true; //Hides the star planet ui on world select.
    public static boolean FARM = false; //Enable or disable farm entry.
    public static boolean PART_TIME_JOB = false; //Enable or disable Part Time Jobs at character select.
    public static boolean ADMIN_MODE = false;
    public static boolean GLOBAL_GM_ACC = true; //True = Shares the account GM level on new characters.
    public static boolean DEV_DEFAULT_CD = true; //True = Accounts GM Level 5+ have skill cooldowns toggled off by default.
    public static final int LOGIN_ATTEMPTS = 5; //Amount of login attempts before game will close.
    public static final int FLAGS = 3;
    public static final int USER_LIMIT = 150;
    public static final String EVENT_MESSAGE = "REXION MapleStory GMS 1.80 (Early Access)";
    public static final String RANKING_URL = "https://playrexion.net/rankings";
    public static final String CS_BANNER_URL = "http://mazenmassoud.com/rexion/CashShop.jpg";
    public static final byte[] NEXON_IP = new byte[]{(byte) 8, (byte) 31, (byte) 99, (byte) 141};//8.31.99.141
    public static final byte[] NEXON_CHAT_IP = new byte[]{(byte) 8, (byte) 31, (byte) 99, (byte) 133};
    public static final int CHARACTER_LIMIT = 16;
    public static final String SERVER_MESSAGE = "";
    public static final int CHANNEL_COUNT = 10;
    public static final boolean ENABLE_PIC = true;
    public static final long MAX_MESOS = Long.MAX_VALUE;
    public static boolean FEVER_TIME = false;
    public static final String events = "" + "EvolutionLab,PinkZakumEntrance,PVP,CygnusBattle,ScarTarBattle,BossBalrog_EASY,BossBalrog_NORMAL,HorntailBattle,Nibergen,PinkBeanBattle,ZakumBattle,NamelessMagicMonster,Dunas,Dunas2,2095_tokyo,ZakumPQ,LudiPQ,KerningPQ,ProtectTylus,WitchTower_EASY,WitchTower_Med,WitchTower_Hard,Vergamot,ChaosHorntail,ChaosZakum,CoreBlaze,BossQuestEASY,BossQuestMed,BossQuestHARD,BossQuestHELL,BossQuestCHAOS,Ravana_EASY,Ravana_HARD,Ravana_MED,GuildQuest,Aufhaven,Dragonica,Rex,MonsterPark,KentaPQ,ArkariumBattle,AswanOffSeason,HillaBattle,The Dragon Shout,VonLeonBattle,Ghost,OrbisPQ,Romeo,Juliet,Pirate,Amoria,Ellin,CWKPQ,DollHouse,Kenta,Prison,Azwan,HenesysPQ,jett2ndjob,cpq,cpq2,Rex,Trains,Boats,Flight,Visitor,AirPlane,Ghost,PinkBeanBattle,Aswan,AswanOffSeason,Subway,MagnusBattle,MagnusMed,mirrorD_328_2_, DimensionInvasion,lolcastle,MiniDungeon,RanmaruBattle,RanmaruNorm,DarkHillaBattle,RootPierre,RootQueen,RootVellum,RootVonBon,ChaosQueen,ChaosVellum,ChaosPierre,ChaosVonBon,ChaosMagnus,ChaosPinkBeanBattle";
    //Scripts TODO: Amoria,CWKPQ,BossBalrog_EASY,BossBalrog_NORMAL,ZakumPQ,ProtectTylus,GuildQuest,Ravana_EASY,Ravna_MED,Ravana_HARD (untested or not working)

    /*Miscellaneous Configuration*/
    public static final boolean CONTROLLED_POTENTIAL_RESULTS = true; // Uses our customizable potential tables instead, this is configured to be GMS-like.
    public static final float MIRACLE_CUBE_RATE = 1; //cube tier up rateng range
    public static final float CASH_DROP_RATE = 30; //out of 100
    public static final boolean BUFFED_BOSSES = true; //Buffs the damage resistance on certain bosses to make them more challenging.
    public static final boolean BUFFED_HP_GAIN = true; //Players gain increased HP per level.
    public static final boolean AUTO_NX_CONVERT = true; //Convert NXCredit to MaplePoints upon EnterCS.
    public static final boolean SHOW_LOADING_MESSAGE = true; //Display message to inform players that data is loading.
    public static final boolean AUTO_PET_LOOT = true; //Enables automatic pet loot whenever a pet is equipped.
    public static final boolean STRICT_PET_LOOT = true; //Only picks up USE and ETC item types.
    public static final boolean MODIFY_SPAWN_RATE = true; //Enables spawn rate multiplier.
    public static final float SPAWN_RATE_MULTIPLIER = 1.25F; //Multipy the spawn rate globally by this variable.

    /*Tutorial Configuration*/
 /*NPCs used for the tutorial (9201095, 9270087, 9270082, 9270086)*/
 /*Maps used for the tutorial (552000010, 552000021, 552000022, 552000020)*/
    public static final boolean UNIVERSAL_START = true; //If true, all classes start in the custom tutorial intro.
    public static final int UNIVERSAL_START_MAP = 552000010; //All classes start on this map if the boolean above is true.

    /*Buffed Channel System*/
    public static final boolean BUFFED_SYSTEM = true; //Enables the buffed channel system.
    public static final boolean BUFFED_NX_GAIN = true; //Enables the buffed NX gain formula in buffed channels.
    public static final int START_RANGE = 6; //Buffed channel that the buffs the mobs and nx gain. (Starting range)
    public static final int END_RANGE = 10; //Buffed channel that the buffs the mobs and nx gain. (Ending range)
    public static final int BUFFED_EXP_PERCENTAGE = 70; //Percentage of bonus exp received in buffed channels.(ie. 50 = 50% bonus exp)
    public static final double DAMAGE_DEALT_PERCENTAGE = 10; //Percentage of damage applied to monsters in buffed channels. (ie. 30 = 30% damage)
    public static final double DAMAGE_TAKEN_MULTIPLIER = 3; //Multiplier of damage applied to players in buffed channels. (ie. 200 = 200% damage)

    /*Buffed Channel Bloodless Event*/
 /*Description: Bloodless Event takes place in Buffed Channels and has a chance to level up twice or not level at all.*/
    public static boolean BLOODLESS_EVENT = true; //Enables the Bloodless Event in the buffed channels.
    public static boolean BLOODLESS_PARTY_BONUS = true; //Enables buffed party exp in bloodless channels.
    public static int BLOODLESS_DOWNGRADE_CHANCE = 25; //Chance to have experience set to zero on level up. (ie. 20 = 1 in 20 chance)
    public static int BLOODLESS_UPGRADE_CHANCE = 20; //Chance to level up twice instead of once. (ie. 20 = 1 in 20 chance)

    /*Paragon System*/
 /*Level Bonus Index
    1. +5% Damage Reduction
    2. +5% Increased Damage
    3. +10% Increased All Stats
    4. +10% Increased Meso Gain
    5. +10% Increased NX Gain
    6. +1% Life Leech
    7. +1% Mana Leech
    8. +5% Maximum Mana
    9. +5% Maximum Life
    10. +5% Increased Damage
    SS. Access to Additional Content*/
 /*Reworked approach on a rebirth system, the player obtains perks for reaching each next rank.*/
    public static boolean PARAGON_SYSTEM = true; //Enables the skill rank system, levels accessed after Level 250.
    public static int MAX_PARAGON = 11; //Maximum Paragon Level.
    public static long PARAGON_NEEDED_EXP[] = {15000000000000L, // Paragon Level 1 Required Experience
                                                20000000000000L, // Paragon Level 2 Required Experience
                                                25000000000000L, // Paragon Level 3 Required Experience
                                                30000000000000L, // Paragon Level 4 Required Experience
                                                35000000000000L, // Paragon Level 5 Required Experience
                                                40000000000000L, // Paragon Level 6 Required Experience
                                                45000000000000L, // Paragon Level 7 Required Experience
                                                50000000000000L, // Paragon Level 8 Required Experience
                                                55000000000000L, // Paragon Level 9 Required Experience
                                                60000000000000L, // Paragon Level 10 Required Experience
                                                100000000000000L}; // Paragon Max Level Required Experience

    /*Events*/
    public static boolean BURNING_CHARACTER_EVENT = false;
    public static boolean RED_EVENT_10 = false; //Makes cassandra popup when you login at lvl<10 (maple island)
    public static boolean RED_EVENT = false; //Makes red even notification popup (cassandra) When you login at level 11+
    public static boolean QUICKMOVE_EVERYWHERE = true;

    /*Anti-Sniff*/
    public static boolean USE_FIXED_IV = false;
    public static final byte[] STATIC_LOCAL_IV = new byte[]{71, 113, 26, 44};
    public static final byte[] STATIC_REMOTE_IV = new byte[]{70, 112, 25, 43};

    /*Experience Configuration*/
    public static final int LEECH_LEVEL_RANGE = 5;
    public static final int EXP_WITHIN_MONSTER = 40;
    public static final float ATTACKER_EXP_RATIO = 0.8f;
    public static final float LEECHER_EXP_RATIO = 0.2f;

    public static final boolean APPLY_HOTFIX = false;

    public static final byte[] hotfix() {
        if (APPLY_HOTFIX) {
            try {
                return Files.readAllBytes(Paths.get(System.getProperty("wzpath") + "/data.wz"));
            } catch (IOException ex) {
                Logger.getLogger(ServerConstants.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;
    }

    public static byte classExpBonus(final int job) {
        switch (job) {
            case 501:
            case 530:
            case 531:
            case 532:
            case 2300:
            case 2310:
            case 2311:
            case 2312:
            case 3100:
            case 3110:
            case 3111:
            case 3112:
            case 800:
            case 900:
            case 910:
                return 10;
        }
        return 0;
    }

    public static boolean getEventTime() {
        int time = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        switch (Calendar.DAY_OF_WEEK) {
            case 1:
                return time >= 1 && time <= 5;
            case 2:
                return time >= 4 && time <= 9;
            case 3:
                return time >= 7 && time <= 12;
            case 4:
                return time >= 10 && time <= 15;
            case 5:
                return time >= 13 && time <= 18;
            case 6:
                return time >= 16 && time <= 21;
        }
        return time >= 19 && time <= 24;
    }

    /*GM Level & Command Configuration*/
    public static enum PlayerGMRank {

        NORMAL('@', 0),
        DONATOR('$', 1),
        INTERN('!', 2),
        GM('!', 3),
        ADMIN('!', 4),
        INTERNAL_DEVELOPER('!', 5),;
        private final char commandPrefix;
        private final int level;

        PlayerGMRank(char ch, int level) {
            commandPrefix = ch;
            this.level = level;
        }

        public String getCommandPrefix() {
            return String.valueOf(commandPrefix);
        }

        public int getLevel() {
            return level;
        }
    }

    public static enum CommandType {

        NORMAL(0),
        TRADE(1);
        private final int level;

        CommandType(int level) {
            this.level = level;
        }

        public int getType() {
            return level;
        }
    }

    public static enum Events {

        EVENT1("PinkZakumEntrance"),
        EVENT2("PVP"),
        EVENT3("CygnusBattle"),
        EVENT4("ScarTarBattle"),
        EVENT5("BossBalrog_EASY"),
        EVENT6("BossBalrog_NORMAL"),
        EVENT7("HorntailBattle"),
        EVENT8("Nibergen"),
        EVENT9("PinkBeanBattle"),
        EVENT10("ZakumBattle"),
        EVENT11("NamelessMagicMonster"),
        EVENT12("Dunas"),
        EVENT13("Dunas2"),
        EVENT14("2095_tokyo"),
        EVENT15("ZakumPQ"),
        EVENT16("LudiPQ"),
        EVENT17("KerningPQ"),
        EVENT18("ProtectTylus"),
        EVENT19("WitchTower_EASY"),
        EVENT20("WitchTower_Med"),
        EVENT21("WitchTower_Hard"),
        EVENT22("Vergamot"),
        EVENT23("ChaosHorntail"),
        EVENT24("ChaosZakum"),
        EVENT25("CoreBlaze"),
        EVENT26("BossQuestEASY"),
        EVENT27("BossQuestMed"),
        EVENT28("BossQuestHARD"),
        EVENT29("BossQuestHELL"),
        EVENT30("Ravana_EASY"),
        EVENT31("Ravana_HARD"),
        EVENT32("Ravana_MED"),
        EVENT33("GuildQuest"),
        EVENT34("Aufhaven"),
        EVENT35("Dragonica"),
        EVENT36("Rex"),
        EVENT37("MonsterPark"),
        EVENT38("KentaPQ"),
        EVENT39("ArkariumBattle"),
        EVENT40("AswanOffSeason"),
        EVENT41("HillaBattle"),
        EVENT42("The Dragon Shout"),
        EVENT43("VonLeonBattle"),
        EVENT44("Ghost"),
        EVENT45("OrbisPQ"),
        EVENT46("Romeo"),
        EVENT47("Juliet"),
        EVENT48("Pirate"),
        EVENT49("Amoria"),
        EVENT50("Ellin"),
        EVENT51("CWKPQ"),
        EVENT52("DollHouse"),
        EVENT53("Kenta"),
        EVENT54("Prison"),
        EVENT55("Azwan"),
        EVENT56("cpq"),
        EVENT57("cpq2"),
        EVENT58("Rex"),
        EVENT59("Trains"),
        EVENT60("Boats"),
        EVENT61("Flight"),
        EVENT62("Visitor"),
        EVENT63("AirPlane"),
        EVENT64("Ghost"),
        EVENT65("PinkBeanBattle"),
        EVENT66("Aswan"),
        EVENT67("AswanOffSeason"),
        EVENT68("Subway"),
        EVENT69("MagnusBattle"),
        EVENT70("MagnusMed"),
        EVENT71("mirrorD_328_2_"),
        EVENT72("DimensionInvasion"),
        EVENT73("lolcastle"),
        EVENT74("MiniDungeon"),
        EVENT75("RanmaruBattle"),
        EVENT76("RanmaruNorm"),
        EVENT77("DarkHillaBattle"),
        EVENT78("RootPierre"),
        EVENT79("RootQueen"),
        EVENT80("RootVellum"),
        EVENT81("RootVonBon"),
        EVENT82("ChaosQueen"),
        EVENT83("ChaosVellum"),
        EVENT84("ChaosPierre"),
        EVENT85("ChaosVonBon"),
        EVENT86("ChaosMagnus"),
        EVENT87("ChaosPinkBeanBattle");
        private final String name;

        Events(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    public static String[] getEvents() {
        String[] eventlist = new String[Events.values().length];
        int arrayLocation = 0;
        for (Events event : Events.values()) {
            eventlist[arrayLocation] += event.getName();
            arrayLocation++;
        }
        return eventlist;
    }

    public static String getEventList() {
        String eventlist = new String();
        for (Events event : Events.values()) {
            eventlist += event.getName();
            eventlist += ", ";
        }
        eventlist += "@";
        eventlist = eventlist.replaceAll(", @", "");
        return eventlist;
    }
}
