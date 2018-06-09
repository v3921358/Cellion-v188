package constants;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * Nox Project
 * Cellion Development

 * MapleStory Server Emulator
 * GMS Version 1.88.4
 * 
 * Project Lead Developer
 * @author Mazen Massoud
 *
 * Credits to all previous Nox Developers.
 */
public class ServerConstants {

    public static final String SOURCE_REVISION = "1.0";
    
    /*Server IP Address*/
    public static final String HOST = "127.0.0.1"; // Server IP Address "10.147.17.88"; //
    public static final byte[] NEXON_IP = new byte[]{(byte) 8, (byte) 31, (byte) 99, (byte) 141}; // 8.31.99.141
    public static final byte[] NEXON_CHAT_IP = new byte[]{(byte) 8, (byte) 31, (byte) 99, (byte) 133}; // Chat Server
    
    /*Standard Login or API Configuration*/
   /*These values will be overwritten by the configuration.ini file.*/
    public static boolean USE_API = false; // If false, use the standard login methods and database communications.
    public static boolean DEVMODE = false; // Dev mode for API stuff?
    public static boolean GM_ONLY = false; // Only allow GMs to login.
    
    /*Server Debug*/
   /*These values will be overwritten by the configuration.ini file.*/
    public static boolean DEVELOPER_DEBUG_MODE = false;
    public static boolean REDUCED_DEBUG_SPAM = true;

    /*General Rate Configuration*/
   /*These values will be overwritten by the configuration.ini file.*/
    public static float EXP_RATE = 1;
    public static float MESO_RATE = 1;
    public static float DROP_RATE = 1;

    /*Client Information*/
    public static final boolean TESPIA = false; // Used for activating GMST.
    public static final short MAPLE_VERSION = (short) 188;
    public static final String SERVER_NAME = "Cellion"; //Shorter reference of just the server name.
    public static final String MAPLE_PATCH = "4";
    public static final int MAPLE_LOCALE = 8;
    public static final boolean ENABLE_PIC = true;
    public static boolean FARM = false; //Enable or disable farm entry.
    public static boolean PART_TIME_JOB = false; //Enable or disable Part Time Jobs at character select.
    public static boolean HIDE_STAR_PLANET_WORLD_UI = true; //Hides the star planet ui on world select.
    public static final boolean SHOW_LOADING_MESSAGE = true; //Display message to inform players that data is loading.
    public static final int LOGIN_ATTEMPTS = 5; //Amount of login attempts before game will close.
    public static boolean GLOBAL_GM_ACC = true; //True = Shares the account GM level on new characters.
    public static boolean DEV_DEFAULT_CD = true; //True = Accounts GM Level 5+ have skill cooldowns toggled off by default.
    public static boolean GM_TRADING = false; //If false, GMs can no longer drop items or trade with regular players.
    
    /*Extra-Client Configuration*/
    public static boolean ANTI_CHEAT = true; // Enables Cellion Anti-Cheat. (THIS HAS NOT BEEN CODED YET)
    public static boolean ADMIN_MODE = false;
    public static final boolean LOCALHOST = false; //true = packets are logged, false = others can connect to server
    public static final boolean LOG_SHARK = false;  //true = enable shark to log 
    
    /*Server Configuration*/
    public static final int FLAGS = 3;
    public static final int CHANNEL_COUNT = 20;
    public static final int USER_LIMIT = 500;
    public static final int CHARACTER_LIMIT = 16;
    public static final String SERVER_MESSAGE = "";
    public static final String EVENT_MESSAGE = "Cellion MapleStory (Beta)";
    public static final String RANKING_URL = "https://cellion.org/rankings";
    public static final String CS_BANNER_URL = "3F 00 68 74 74 70 73 3a 2f 2f 73 74 61 74 69 63 2e 63 65 6c 6c 69 6f 6e 2e 6f 72 67 2f 63 65 6c 6c 69 6f 6e 2f 69 6e 67 61 6d 65 2f 63 73 5f 62 61 6e 6e 65 72 2f 77 65 6c 63 6f 6d 65 2e 6a 70 67".toUpperCase(); // public static final String CS_BANNER_URL = HexTool.toString("https://static.cellion.org/cellion/ingame/cs_banner/welcome.jpg".getBytes(ASCII)).toUpperCase(); 
    private static final Charset ASCII = Charset.forName("US-ASCII");
    
    /*Event Script Configuration*/
    public static final String events = "" + "EvolutionLab,PinkZakumEntrance,PVP,CygnusBattle,ScarTarBattle,BossBalrog_EASY,BossBalrog_NORMAL,HorntailBattle,Nibergen,PinkBeanBattle,ZakumBattle,NamelessMagicMonster,Dunas,Dunas2,2095_tokyo,ZakumPQ,LudiPQ,KerningPQ,ProtectTylus,WitchTower_EASY,WitchTower_Med,WitchTower_Hard,Vergamot,ChaosHorntail,ChaosZakum,CoreBlaze,BossQuestEASY,BossQuestMed,BossQuestHARD,BossQuestHELL,BossQuestCHAOS,Ravana_EASY,Ravana_HARD,Ravana_MED,GuildQuest,Aufhaven,Dragonica,Rex,MonsterPark,KentaPQ,ArkariumBattle,AswanOffSeason,HillaBattle,The Dragon Shout,VonLeonBattle,Ghost,OrbisPQ,Romeo,Juliet,Pirate,Amoria,Ellin,CWKPQ,DollHouse,Kenta,Prison,Azwan,HenesysPQ,jett2ndjob,cpq,cpq2,Rex,Trains,Boats,Flight,Visitor,AirPlane,Ghost,PinkBeanBattle,Aswan,AswanOffSeason,Subway,MagnusBattle,MagnusMed,mirrorD_328_2_, DimensionInvasion,lolcastle,MiniDungeon,RanmaruBattle,RanmaruNorm,DarkHillaBattle,RootPierre,RootQueen,RootVellum,RootVonBon,ChaosQueen,ChaosVellum,ChaosPierre,ChaosVonBon,ChaosMagnus,ChaosPinkBeanBattle";
    //Scripts TODO: Amoria,CWKPQ,BossBalrog_EASY,BossBalrog_NORMAL,ZakumPQ,ProtectTylus,GuildQuest,Ravana_EASY,Ravna_MED,Ravana_HARD (untested or not working)k

    /*Server Structure Configuration*/
    public static final boolean CUSTOM_LIFE = true; //If true, automatically loads the scripted NPCs for the tutorial and FM, without the need for updating the database.
    public static final boolean BUFFED_CHANNELS = true; //If true, enables the buffed channels system which makes some channels spawn harder, more rewarding monsters.
    public static final boolean UNIVERSAL_START = true; //If true, all classes start in the custom tutorial intro.
    public static final int UNIVERSAL_START_MAP = 331003400; //All classes start on this map if the boolean above is true.
    public static final int JAIL_MAP = 693000006; //Map ID used for the server jail, a map where characters may not leave from.
    public static final boolean INCREASED_HP_GAIN = true; //Players gain increased HP per level.
    
    /*Miscellaneous Configuration*/
    public static final long MAX_MESOS = Long.MAX_VALUE;
    public static boolean MULTI_LEVEL = true; //true = enable multi leveling
    public static final byte SHOP_DISCOUNT = 0; //Discount Rate (%) Higher than 100 or lower than 1 means no discount
    public static boolean CS_COUPONS = false; //Disable the purchase of exp/drop rate coupons from cash shop.
    public static final boolean OLD_MAPS = false; //example: it will use old maple event's henesys instead of current one
    public static boolean FEVER_TIME = false;
    public static final boolean AUTO_NX_CONVERT = true; //Convert NXCredit to MaplePoints upon EnterCS.
    public static final boolean DAMAGE_CORRECTION = true; //Enables damage modifications for some skills, used to correct or change damage lines.
    
    /*Cubing Configuration*/
    public static final boolean CONTROLLED_POTENTIAL_RESULTS = true; // Uses our customizable potential tables instead, this is configured to be GMS-like.
    public static final float MIRACLE_CUBE_RATE = 1; //cube tier up rateng range
    
    /*Monster Configuration*/
    public static final boolean MONSTER_CASH_DROP = true; // NX Gain
    public static final boolean BUFFED_BOSSES = true; //Buffs the damage resistance on certain bosses to make them more challenging.
    
    /*Spawn Rate Configuration*/
    public static final boolean MODIFY_GLOBAL_SPAWN_RATE = true; //Enables spawn rate multiplier.
    public static final float SPAWN_RATE_MULTIPLIER = 12.0F; //Multipy the spawn rate globally by this variable.

    /*Pet Configuration*/
    public static final boolean AUTO_PET_LOOT = true; //Enables automatic pet loot whenever a pet is equipped.
    public static final boolean STRICT_PET_LOOT = true; //Only picks up USE and ETC item types.
    
    /*Events Configuration*/
    public static boolean BURNING_CHARACTER_EVENT = false;
    public static boolean RED_EVENT_10 = false; //Makes cassandra popup when you login at lvl<10 (maple island)
    public static boolean RED_EVENT = false; //Makes red even notification popup (cassandra) When you login at level 11+
    public static boolean QUICKMOVE_EVERYWHERE = true;

    /*Experience Configuration*/
    public static final int LEECH_LEVEL_RANGE = 10;
    public static final int EXP_WITHIN_MONSTER = 40;
    public static final float ATTACKER_EXP_RATIO = 0.8f;
    public static final float LEECHER_EXP_RATIO = 0.2f;

    /*Miracle Time Config*/
    public static boolean DOUBLE_MIRACLE_TIME = false;
    public static boolean DOUBLE_TIME = false;
    public static boolean HOT_TIME = true;

    public static final boolean APPLY_HOTFIX = false;
    
    /*GM Level & Command Configuration*/
    public static enum PlayerGMRank {

        NORMAL('@', 0),                 // Normal Player
        DONOR('$', 1),                  // Donor Player
        INTERN('!', 2),                 // Intern           (Minor Staff Permissions)
        GM('!', 3),                     // Game Master      (Staff Permissions)
        ADMIN('!', 4),                  // Administrator    (Full Staff Permissions)
        INTERNAL_DEVELOPER('!', 5),     // Developer        (Complete Permissions)
        
        COMMAND_VAULT_ACCESS('!', 10);  // Additional Command Access (Developer Toggle)
        
        private final char cCommandPrefix;
        private final int nGMLevel;

        PlayerGMRank(char ch, int level) {
            cCommandPrefix = ch;
            this.nGMLevel = level;
        }

        public String getCommandPrefix() {
            return String.valueOf(cCommandPrefix);
        }

        public int getLevel() {
            return nGMLevel;
        }
    }

    public static enum CommandType {

        NORMAL(0),
        TRADE(1);
        private final int nType;

        CommandType(int type) {
            this.nType = type;
        }

        public int getType() {
            return nType;
        }
    }
    
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
        private final String sName;

        Events(String name) {
            this.sName = name;
        }

        public String getName() {
            return sName;
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
