package constants;

import java.awt.Point;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import client.CharacterTemporaryStat;
import client.MonsterStatus;
import client.PlayerStats;
import client.Skill;
import client.SkillFactory;
import client.inventory.MapleInventoryType;
import client.inventory.MapleWeaponType;
import constants.skills.*;
import handling.login.Balloon;
import server.MapleItemInformationProvider;
import server.MapleStatEffect;
import server.MapleStringInformationProvider;
import server.NebuliteGrade;
import server.Randomizer;
import server.maps.MapleMapObjectType;
import server.maps.objects.MapleCharacter;
import tools.Pair;
import tools.Tuple;

public class GameConstants {

    // Login
    public static final int characterSlot = 8;
    public static final int characterSlotMax = 40;

    // Stats
    public static final int maxLevel = 250;
    public static final int maxHP = 500000;
    public static final int maxMP = 500000;
    public static final int maxAccAvoid = 9999;
    public static final int maxWdefMdef = 30000;

    // Quick Slot
    public static final int quickSlot = 123000;

    // Inventory
    public static final int maxInventorySlot = 96; // this may be increased in fifth job patch 

    // Skills
    public static final int damageCap = 50000000;//2_499_999;

    // Removes these NPCs from all maps.
    public static boolean isHiddenNpc(int npcId) {
        switch (npcId) {
            case 1500000:
            case 1500001:
            case 1500002:
                return true;
            default:
                return false;
        }
    }

    // Cash Weapon Item IDs, used for addCharLook packet. Hack fix for now. -Mazen
    public static final int aCashWeapons[] = {1702696, 1702716, 1702709, 1702701, 1702475, 1702469, 1702375, 1702687, 1702680, 1702682, 1702649, 1702651, 1702645, 1702643, 1702632, 1702634, 1702395, 1702399, 1702457, 1702467, 1702436, 1702480, 1702565, 1702613, 1702586, 1702624, 1702653, 1702224, 1702235, 1702374, 1702409, 1702556, 1702585, 1702526, 1702710};

    /**
     * Custom Map Spawn Rate Buffs
     * @note Requires ServerConstants boolean to be enabled.
     */
    public static boolean isCustomMapForBuffedSpawn(int nMapID) {
        switch (nMapID) {
            case 272000300: // Leafre in Flames 3 
            case 860000032: // Dangerous Deep Sea 3 
            case 240093100: // Inside the Stone Colossus 2
            case 273040100: // Forsaken Excavation Site 2
            case 211060830: // Very Tall Castle Walls
            case 106031504: // Galley    
            case 120040300: // Beachgrass Dunes 3 
            case 551030000: // Fantasy Theme Park 3
            case 105200900: // Neglected Garden
                return true;
            default:
                return false;
        }
    }

    // Non-Buff Stat Skills
    // This is just for skills we don't want spamming to console for missing buffstats.
    public static boolean isNotBuffSkill(int skillid) {
        switch (skillid) {
            case 10001254: // Elemental Shift Flash Jump (Cygnus)
                return true;
            default:
                return false;
        }
    }

    // Buffs
    // 17 Ints as of v175+ (maybe earlier)
    public static final int CFlagSize = 17;

    // Maps
    public static final int mapItemExpiration_ms = 120000;
    public static final int mapItemExpirationGlobalDrops_ms = 60000;

    public static final List<Balloon> lBalloon = new ArrayList<>();
    public static final List<MapleMapObjectType> rangedMapobjectTypes = Collections.unmodifiableList(Arrays.asList(
            MapleMapObjectType.ITEM,
            MapleMapObjectType.MONSTER,
            MapleMapObjectType.DOOR,
            MapleMapObjectType.REACTOR,
            MapleMapObjectType.SUMMON,
            MapleMapObjectType.NPC,
            MapleMapObjectType.MIST,
            MapleMapObjectType.FAMILIAR,
            MapleMapObjectType.EXTRACTOR,
            MapleMapObjectType.RUNE));
    private static final long[] exp = new long[251];
    private static final int[] closeness = {0, 1, 3, 6, 14, 31, 60, 108, 181, 287, 434, 632, 891, 1224, 1642, 2161, 2793,
        3557, 4467, 5542, 6801, 8263, 9950, 11882, 14084, 16578, 19391, 22547, 26074,
        30000};
    private static final int[] setScore = {0, 10, 100, 300, 600, 1000, 2000, 4000, 7000, 10000};
    private static final int[] cumulativeTraitExp = {0, 20, 46, 80, 124, 181, 255, 351, 476, 639, 851, 1084,
        1340, 1622, 1932, 2273, 2648, 3061, 3515, 4014, 4563, 5128,
        5710, 6309, 6926, 7562, 8217, 8892, 9587, 10303, 11040, 11788,
        12547, 13307, 14089, 14883, 15689, 16507, 17337, 18179, 19034, 19902,
        20783, 21677, 22584, 23505, 24440, 25399, 26362, 27339, 28331, 29338,
        30360, 31397, 32450, 33519, 34604, 35705, 36823, 37958, 39110, 40279,
        41466, 32671, 43894, 45135, 46395, 47674, 48972, 50289, 51626, 52967,
        54312, 55661, 57014, 58371, 59732, 61097, 62466, 63839, 65216, 66597,
        67982, 69371, 70764, 72161, 73562, 74967, 76376, 77789, 79206, 80627,
        82052, 83481, 84914, 86351, 87792, 89237, 90686, 92139, 93596, 96000};
    private static final int[] mobHpVal = {0, 15, 20, 25, 35, 50, 65, 80, 95, 110, 125, 150, 175, 200, 225, 250, 275, 300, 325, 350,
        375, 405, 435, 465, 495, 525, 580, 650, 720, 790, 900, 990, 1100, 1200, 1300, 1400, 1500, 1600, 1700, 1800,
        1900, 2000, 2100, 2200, 2300, 2400, 2520, 2640, 2760, 2880, 3000, 3200, 3400, 3600, 3800, 4000, 4300, 4600, 4900, 5200,
        5500, 5900, 6300, 6700, 7100, 7500, 8000, 8500, 9000, 9500, 10000, 11000, 12000, 13000, 14000, 15000, 17000, 19000, 21000, 23000,
        25000, 27000, 29000, 31000, 33000, 35000, 37000, 39000, 41000, 43000, 45000, 47000, 49000, 51000, 53000, 55000, 57000, 59000, 61000, 63000,
        65000, 67000, 69000, 71000, 73000, 75000, 77000, 79000, 81000, 83000, 85000, 89000, 91000, 93000, 95000, 97000, 99000, 101000, 103000,
        105000, 107000, 109000, 111000, 113000, 115000, 118000, 120000, 125000, 130000, 135000, 140000, 145000, 150000, 155000, 160000, 165000, 170000, 175000, 180000,
        185000, 190000, 195000, 200000, 205000, 210000, 215000, 220000, 225000, 230000, 235000, 240000, 250000, 260000, 270000, 280000, 290000, 300000, 310000, 320000,
        330000, 340000, 350000, 360000, 370000, 380000, 390000, 400000, 410000, 420000, 430000, 440000, 450000, 460000, 470000, 480000, 490000, 500000, 510000, 520000,
        530000, 550000, 570000, 590000, 610000, 630000, 650000, 670000, 690000, 710000, 730000, 750000, 770000, 790000, 810000, 830000, 850000, 870000, 890000, 910000};
    private static final int[] pvpExp = {0, 3000, 6000, 12000, 24000, 48000, 960000, 192000, 384000, 768000};
    public static final int[] guildexp = {0, 15000, 60000, 135000, 240000, 375000, 540000, 735000, 960000,
        1215000, 1500000, 1815000, 2160000, 2535000, 2940000, 3375000,
        3840000, 4335000, 4860000, 5415000, 6000000, 6615000, 7260000,
        7935000, 8640000, 9375000, 10050000, 10725000, 11400000, 12075000};
    private static final int[] mountexp = {0, 6, 25, 50, 105, 134, 196, 254, 263, 315, 367, 430, 543, 587, 679, 725, 897, 1146, 1394, 1701, 2247,
        2543, 2898, 3156, 3313, 3584, 3923, 4150, 4305, 4550};
    public static final int[] itemBlock = {5200000, 2290653, 4001168, 5220013, 3993003, 2340000, 2049100, 4001129, 2040037, 2040006, 2040007, 2040303, 2040403, 2040506, 2040507, 2040603, 2040709, 2040710, 2040711, 2040806, 2040903, 2041024, 2041025, 2043003, 2043103, 2043203, 2043303, 2043703, 2043803, 2044003, 2044103, 2044203, 2044303, 2044403, 2044503, 2044603, 2044908, 2044815, 2044019, 2044703};
    public static final int[] cashBlock = {5200000, 5062000, 5062001, 5062002, 5062003, 5062005, 5062500, 5610000, 5610001, 5640000, 2531000, 2530000,
        5534000, 5050000, 5510000, 5521000, 5062200, 5062201, 5133000, 5520001, 5030000, 5030001, 5030006,
        5470000, 1122121, 5155000, 5062400, 5700000, 1112909, 5450005, 5040004, 5220000, 5050000, 5062000,
        5062001, 5062002, 5062003, 5211046, 5360000, 5051001, 5590000};
    public static final int[] rankC = {70000000, 70000001, 70000002, 70000003, 70000004, 70000005, 70000006, 70000007, 70000008, 70000009, 70000010, 70000011, 70000012, 70000013};
    public static final int[] rankB = {70000014, 70000015, 70000016, 70000017, 70000018, 70000021, 70000022, 70000023, 70000024, 70000025, 70000026};
    public static final int[] rankA = {70000027, 70000028, 70000029, 70000030, 70000031, 70000032, 70000033, 70000034, 70000035, 70000036, 70000039, 70000040, 70000041, 70000042};
    public static final int[] rankS = {70000043, 70000044, 70000045, 70000047, 70000048, 70000049, 70000050, 70000051, 70000052, 70000053, 70000054, 70000055, 70000056, 70000057, 70000058, 70000059, 70000060, 70000061, 70000062};
    public static final int[] circulators = {2702000, 2700000, 2700100, 2700200, 2700300, 2700400, 2700500, 2700600, 2700700, 2700800, 2700900, 2701000};
    public static final int JAIL = 180000004;
    public static final int[] blockedSkills = {4341003, 36120045};
    public static final String[] stats = {"tuc", "reqLevel", "reqJob", "reqSTR", "reqDEX", "reqINT", "reqLUK", "reqPOP", "cash", "cursed", "success", "setItemID", "equipTradeBlock", "durability", "randOption", "randStat", "masterLevel", "reqSkillLevel", "elemDefault", "incRMAS", "incRMAF", "incRMAI", "incRMAL", "canLevel", "skill", "charmEXP"};
    public static final int[] hyperTele = {10000, 20000, 30000, 40000, 50000, 1000000, 1010000, 1020000, 2000000, //Maple Island
        104000000, 104010000, 104010100, 104010200, 104020000, 103010100, 103010000, 103000000, 103050000, 103020000, 103020020, 103020100, 103020200, 103020300, 103020310, 103020320, 103020400, 103020410, 103020420, 103030000, 103030100, 103030200, 103030300, 103030400, 102000000, 102010000, 102010100, 102020000, 102020100, 102020200, 102020300, 102020400, 102020500, 102040000, 102040100, 102040200, 102040300, 102040400, 102040500, 102040600, 102030000, 102030100, 102030200, 102030300, 102030400, 101000000, 101010000, 101010100, 101020000, 101020100, 101020200, 101020300, 101030000, 101030100, 101030200, 101030300, 101030400, 101030500, 101030101, 101030201, 101040000, 101040100, 101040200, 101040300, 101040310, 101040320, 101050000, 101050400, 100000000, 100010000, 100010100, 100020000, 100020100, 100020200, 100020300, 100020400, 100020500, 100020401, 100020301, 100040000, 100040100, 100040200, 100040300, 100040400, 100020101, 106020000, 120010100, 120010000, 120000000, 120020000, 120020100, 120020200, 120020300, 120020400, 120020500, 120020600, 120020700, 120030000, 120030100, 120030200, 120030300, 120030400, 120030500, //Victoria Island
        105000000, 105010000, 105010100, 105020000, 105020100, 105020200, 105020300, 105020400, 105020500, 105030000, 105030100, 105030200, 105030300, 105030400, 105030500, 105100000, 105100100, //Sleepy Wood
        120000100, 120000101, 120000102, 120000103, 120000104, 120000201, 120000202, 120000301, //Nautilus
        103040000, 103040100, 103040101, 103040102, 103040103, 103040200, 103040201, 103040202, 103040203, 103040300, 103040301, 103040302, 103040303, 103040400, //Kerning Square
        200000000, 200010000, 200010100, 200010110, 200010120, 200010130, 200010111, 200010121, 200010131, 200010200, 200010300, 200010301, 200010302, 200020000, 200030000, 200040000, 200050000, 200060000, 200070000, 200080000, 200000100, 200000200, 200000300, 200100000, 200080100, 200080200, 200081500, 200082200, 200082300, 211000000, 211000100, 211000200, 211010000, 211020000, 211030000, 211040000, 211050000, 211040100, 211040200, 921120000, //Orbis
        211040300, 211040400, 211040500, 211040600, 211040700, 211040800, 211040900, 211041000, 211041100, 211041200, 211041300, 211041400, 211041500, 211041600, 211041700, 211041800, 211041900, 211042000, 211042100, 211042200, 211042300, 211042400, 280030000, 211060000, //Dead Mine
        211060010, 211060100, 211060200, 211060201, 211060300, 211060400, 211060401, 211060410, 211060500, 211060600, 211060601, 211060610, 211060620, 211060700, 211060800, 211060801, 211060810, 211060820, 211060830, 211060900, 211061000, 211061001, 211070000, //Lion King's Castle
        220000000, 220000100, 220000300, 220000400, 220000500, 220010000, 220010100, 220010200, 220010300, 220010400, 220010500, 220010600, 220010700, 220010800, 220010900, 220011000, 220020000, 220020100, 220020200, 220020300, 220020400, 220020500, 220020600, 220030100, 220030200, 220030300, 220030400, 220030000, 220040000, 220040100, 220040200, 220040300, 220040400, 220050000, 220050100, 220050200, 221023200, 221022300, 221022200, 221021700, 221021600, 221021100, 221020000, 221000000, 221030000, 221030100, 221030200, 221030300, 221030400, 221030500, 221030600, 221040000, 221040100, 221040200, 221040300, 221040400, 222000000, 222010000, 222010001, 222010002, 222010100, 222010101, 222010102, 222010200, 222010201, 222010300, 222010400, 222020300, 222020200, 222020100, 222020000, //Ludas Lake
        220050300, 220060000, 220060100, 220060200, 220060300, 220060400, 220070000, 220070100, 220070200, 220070300, 220070400, 220080000, 220080001, //Clock Tower Lower Floor
        300000100, 300000000, 300010000, 300010100, 300010200, 300010400, 300020000, 300020100, 300020200, 300030000, 300030100, 300010410, 300020210, 300030200, 300030300, 300030310, //Ellin Forest
        230010000, 230010100, 230010200, 230010201, 230010300, 230010400, 230020000, 230020100, 230020200, 230020201, 230020300, 230030000, 230030100, 230030101, 230030200, 230040000, 230040100, 230040200, 230040300, 230040400, 230040410, 230040420, 230000000, //Aqua Road
        250000000, 250000100, 250010000, 250010100, 250010200, 250010300, 250010301, 250010302, 250010303, 250010304, 250010400, 250010500, 250010501, 250010502, 250010503, 250010600, 250010700, 250020000, 250020100, 250020200, 250020300, 251000000, 251000100, 251010000, 251010200, 251010300, 251010400, 251010401, 251010402, 251010403, 251010500, //Mu Lung Garden
        240010100, 240010200, 240010300, 240010400, 240010500, 240010600, 240010700, 240010800, 240010900, 240011000, 240020000, 240020100, 240020101, 240020200, 240020300, 240020400, 240020401, 240020500, 240030000, 240030100, 240030101, 240030102, 240030200, 240030300, 240040000, 240040100, 240040200, 240040300, 240040400, 240040500, 240040510, 240040511, 240040520, 240040521, 240040600, 240040700, 240050000, 240010000, 240000000, //Minar Forest
        240070000, 240070010, 240070100, 240070200, 240070300, 240070400, 240070500, 240070600, //Neo City
        260010000, 260010100, 260010200, 260010300, 260010400, 260010500, 260010600, 260010700, 260020000, 260020100, 260020200, 260020300, 260020400, 260020500, 260020600, 260020610, 260020620, 260020700, 261000000, 260000000, 926010000, 261010000, 261010001, 261010002, 261010003, 261010100, 261010101, 261010102, 261010103, 261020000, 261020100, 261020200, 261020300, 261020400, 261020500, 261020600, 261020700, 260000300, 260000200, //Nihal Desert
        270000000, 270000100, 270010000, 270010100, 270010110, 270010111, 270010200, 270010210, 270010300, 270010310, 270010400, 270010500, 270020000, 270020100, 270020200, 270020210, 270020211, 270020300, 270020310, 270020400, 270020410, 270020500, 270030000, 270030100, 270030110, 270030200, 270030210, 270030300, 270030310, 270030400, 270030410, 270030411, 270030500, 270040000, 270050000, //Temple of Time
        271000000, 271000100, 271000200, 271000210, 271000300, 271020000, 271020100, 271010000, 271010100, 271010200, 271010300, 271010301, 271010400, 271010500, 271030000, 271030100, 271030101, 271030102, 271030200, 271030201, 271030300, 271030310, 271030320, 271030400, 271030410, 271030500, 271030510, 271030520, 271030530, 271030540, 271030600, 271040000, 271040100, //Gate of Future
        130000000, 130000100, 130000110, 130000120, 130000200, 130000210, 130010000, 130010010, 130010020, 130010100, 130010110, 130010120, 130010200, 130010210, 130010220, 130020000, 130030005, 130030006, 130030000, //Ereve
        140000000, 140010000, 140010100, 140010200, 140020000, 140020100, 140020200, 140030000, 140090000, 140020300, //Rien
        310000000, 310000010, 310020000, 310020100, 310020200, 310030000, 310030100, 310030110, 310030200, 310030300, 310030310, 310040000, 310040100, 310040110, 310040200, 310040300, 310040400, 310050000, 310050100, 310050200, 310050300, 310050400, 310050500, 310050510, 310050520, 310050600, 310050700, 310050800, 310060000, 310060100, 310060110, 310060120, 310060200, 310060210, 310060220, 310060300, 310010000, //Edelstein
        600000000, 600010100, 600010200, 600010300, 600010400, 600010500, 600010600, 600010700, 600020000, 600020100, 600020200, 600020300, 600020400, 600020500, 600020600, 682000000, 610010000, 610010001, 610010002, 610010004, 610020000, 610020001, 610020006, 610040000, 610040100, 610040200, 610040210, 610040220, 610040230, 610040400//Masteria
    };
    public static final int[] unusedNpcs = {9201142, 9201254, 9201030, 9010037, 9010038, 9010039, 9010040, 9300010, 9070004, 9070006, 9000017, 2041017, 9270075, 9000069, 9201029, 9130024, 9330072, 9133080, 9201152, 9330189};
    //Unused npcs will be removed from map once you enter it.

    public static void LoadEXP() { // Version 180
        exp[1] = 15;
        exp[2] = 34;
        exp[3] = 57;
        exp[4] = 92;
        exp[5] = 135;
        exp[6] = 372;
        exp[7] = 560;
        exp[8] = 840;
        exp[9] = 1242;
        exp[10] = 1242;
        exp[11] = 1242;
        exp[12] = 1242;
        exp[13] = 1242;
        exp[14] = 1242;
        exp[15] = 1490;
        exp[16] = 1788;
        exp[17] = 2145;
        exp[18] = 2574;
        exp[19] = 3088;
        exp[20] = 3705;
        exp[21] = 4446;
        exp[22] = 5335;
        exp[23] = 6402;
        exp[24] = 7682;
        exp[25] = 9218;
        exp[26] = 11061;
        exp[27] = 13273;
        exp[28] = 15927;
        exp[29] = 19112;
        exp[30] = 19112;
        exp[31] = 19112;
        exp[32] = 19112;
        exp[33] = 19112;
        exp[34] = 19112;
        exp[35] = 22934;
        exp[36] = 27520;
        exp[37] = 33024;
        exp[38] = 39628;
        exp[39] = 47553;
        exp[40] = 51357;
        exp[41] = 55465;
        exp[42] = 59902;
        exp[43] = 64694;
        exp[44] = 69869;
        exp[45] = 75458;
        exp[46] = 81494;
        exp[47] = 88013;
        exp[48] = 95054;
        exp[49] = 102658;
        exp[50] = 110870;
        exp[51] = 119739;
        exp[52] = 129318;
        exp[53] = 139663;
        exp[54] = 150836;
        exp[55] = 162902;
        exp[56] = 175934;
        exp[57] = 190008;
        exp[58] = 205208;
        exp[59] = 221624;
        exp[60] = 221624;
        exp[61] = 221624;
        exp[62] = 221624;
        exp[63] = 221624;
        exp[64] = 221624;
        exp[65] = 238245;
        exp[66] = 256113;
        exp[67] = 275321;
        exp[68] = 295970;
        exp[69] = 318167;
        exp[70] = 342029;
        exp[71] = 367681;
        exp[72] = 395257;
        exp[73] = 424901;
        exp[74] = 456768;
        exp[75] = 488741;
        exp[76] = 522952;
        exp[77] = 559558;
        exp[78] = 598727;
        exp[79] = 640637;
        exp[80] = 685481;
        exp[81] = 733464;
        exp[82] = 784806;
        exp[83] = 839742;
        exp[84] = 898523;
        exp[85] = 961419;
        exp[86] = 1028718;
        exp[87] = 1100728;
        exp[88] = 1177778;
        exp[89] = 1260222;
        exp[90] = 1342136;
        exp[91] = 1429374;
        exp[92] = 1522283;
        exp[93] = 1621231;
        exp[94] = 1726611;
        exp[95] = 1838840;
        exp[96] = 1958364;
        exp[97] = 2085657;
        exp[98] = 2221224;
        exp[99] = 2365603;
        exp[100] = 2365603;
        exp[101] = 2365603;
        exp[102] = 2365603;
        exp[103] = 2365603;
        exp[104] = 2365603;
        exp[105] = 2519367;
        exp[106] = 2683125;
        exp[107] = 2857528;
        exp[108] = 3043267;
        exp[109] = 3241079;
        exp[110] = 3451749;
        exp[111] = 3676112;
        exp[112] = 3915059;
        exp[113] = 4169537;
        exp[114] = 4440556;
        exp[115] = 4729192;
        exp[116] = 5036589;
        exp[117] = 5363967;
        exp[118] = 5712624;
        exp[119] = 6083944;
        exp[120] = 6479400;
        exp[121] = 6900561;
        exp[122] = 7349097;
        exp[123] = 7826788;
        exp[124] = 8335529;
        exp[125] = 8877338;
        exp[126] = 9454364;
        exp[127] = 10068897;
        exp[128] = 10723375;
        exp[129] = 11420394;
        exp[130] = 12162719;
        exp[131] = 12953295;
        exp[132] = 13795259;
        exp[133] = 14691950;
        exp[134] = 15646926;
        exp[135] = 16663976;
        exp[136] = 17747134;
        exp[137] = 18900697;
        exp[138] = 20129242;
        exp[139] = 21437642;
        exp[140] = 22777494;
        exp[141] = 24201087;
        exp[142] = 25713654;
        exp[143] = 27320757;
        exp[144] = 29028304;
        exp[145] = 30842573;
        exp[146] = 32770233;
        exp[147] = 34818372;
        exp[148] = 36994520;
        exp[149] = 39306677;
        exp[150] = 41763344;
        exp[151] = 44373553;
        exp[152] = 47146900;
        exp[153] = 50093581;
        exp[154] = 53224429;
        exp[155] = 56550955;
        exp[156] = 60085389;
        exp[157] = 63840725;
        exp[158] = 67830770;
        exp[159] = 72070193;
        exp[160] = 76574580;
        exp[161] = 76574580;
        exp[162] = 86445521;
        exp[163] = 91848366;
        exp[164] = 97588888;
        exp[165] = 103688193;
        exp[166] = 110168705;
        exp[167] = 117054249;
        exp[168] = 124370139;
        exp[169] = 132143272;
        exp[170] = 140402226;
        exp[171] = 149177365;
        exp[172] = 158500950;
        exp[173] = 168407259;
        exp[174] = 178932712;
        exp[175] = 190116006;
        exp[176] = 201998256;
        exp[177] = 214623147;
        exp[178] = 228037093;
        exp[179] = 242289411;
        exp[180] = 256826775;
        exp[181] = 272236381;
        exp[182] = 288570563;
        exp[183] = 305884796;
        exp[184] = 324237883;
        exp[185] = 343692155;
        exp[186] = 364313684;
        exp[187] = 386172505;
        exp[188] = 409342855;
        exp[189] = 433903426;
        exp[190] = 459937631;
        exp[191] = 487533888;
        exp[192] = 516785921;
        exp[193] = 547793076;
        exp[194] = 580660660;
        exp[195] = 615500299;
        exp[196] = 652430316;
        exp[197] = 691576134;
        exp[198] = 733070702;
        exp[199] = 777054944;
        exp[200] = 2207026470L;
        exp[201] = 2648431764L;
        exp[202] = 3178118116L;
        exp[203] = 3813741739L;
        exp[204] = 4576490086L;
        exp[205] = 5491788103L;
        exp[206] = 6590145723L;
        exp[207] = 7908174867L;
        exp[208] = 9489809840L;
        exp[209] = 11387771808L;
        exp[210] = 24142076232L;
        exp[211] = 25590600805L;
        exp[212] = 27126036853L;
        exp[213] = 28753599064L;
        exp[214] = 30478815007L;
        exp[215] = 32307543907L;
        exp[216] = 34245996541L;
        exp[217] = 36300756333L;
        exp[218] = 38478801712L;
        exp[219] = 40787529814L;
        exp[220] = 84838062013L;
        exp[221] = 88231584493L;
        exp[222] = 91760847872L;
        exp[223] = 95431281786L;
        exp[224] = 99248533057L;
        exp[225] = 103218474379L;
        exp[226] = 107347213354L;
        exp[227] = 111641101888L;
        exp[228] = 116106745963L;
        exp[229] = 120751015801L;
        exp[230] = 246332072234L;
        exp[231] = 251258713678L;
        exp[232] = 256283887951L;
        exp[233] = 261409565710L;
        exp[234] = 266637757024L;
        exp[235] = 271970512164L;
        exp[236] = 277409922407L;
        exp[237] = 282958120855L;
        exp[238] = 288617283272L;
        exp[239] = 294389628937L;
        exp[240] = 594667050452L;
        exp[241] = 600613720956L;
        exp[242] = 606619858165L;
        exp[243] = 612686056746L;
        exp[244] = 618812917313L;
        exp[245] = 625001046486L;
        exp[246] = 631251056950L;
        exp[247] = 637563567519L;
        exp[248] = 643939203194L;
        exp[249] = 650378595225L;
        exp[250] = ServerConstants.PARAGON_SYSTEM ? 1 : 0; // Non-zero required for experience gain toward paragon levels.
    }

    /*public static void LoadEXP() { // Version 176
        exp[1] = 15;
        exp[2] = 34;
        exp[3] = 57;
        exp[4] = 92;
        exp[5] = 135;
        exp[6] = 372;
        exp[7] = 560;
        exp[8] = 840;
        exp[9] = 1242;

        // Level 10-15's EXP
        for (int level = 10; level < 15; level++) {
            exp[level] = (long) exp[level - 1];
        }

        // Level 15-29 EXP
        for (int level = 15; level < 30; level++) {
            exp[level] = (long) (exp[level - 1] * 1.2);
        }

        // Level 30-34's EXP
        for (int level = 30; level < 35; level++) {
            exp[level] = (long) exp[level - 1];
        }

        //Level 35-39 EXP
        for (int level = 35; level < 40; level++) {
            exp[level] = (long) (exp[level - 1] * 1.2);
        }

        //Level 40-59 EXP
        for (int level = 40; level < 60; level++) {
            exp[level] = (long) (exp[level - 1] * 1.08);
        }

        // Level 60-64's EXP
        for (int level = 60; level < 65; level++) {
            exp[level] = (long) exp[level - 1];
        }

        // Level 65 - 74 EXP
        for (int level = 65; level < 75; level++) {
            exp[level] = (long) (exp[level - 1] * 1.08);
        }

        // Level 75 - 99 EXP
        for (int level = 75; level < 100; level++) {
            exp[level] = (long) (exp[level - 1] * 1.07);
        }
        // Level 100 - 104 EXP
        for (int level = 100; level < 105; level++) {
            exp[level] = (long) exp[level - 1];
        }

        // Level 105 - 159 EXP
        for (int level = 105; level < 160; level++) {
            exp[level] = (long) (exp[level - 1] * 1.07);
        }

        // Level 160 - 199 EXP
        for (int level = 160; level < 200; level++) {
            exp[level] = (long) (exp[level - 1] * 1.06);
        }

        exp[200] = exp[199] * 2;

        // Level 201 - 209 EXP
        for (int level = 201; level < 210; level++) {
            exp[level] = (long) (exp[level - 1] * 1.2);
        }

        exp[210] = exp[209] * 2;

        // Level 210 - 219 EXP
        for (int level = 211; level < 220; level++) {
            exp[level] = (long) (exp[level - 1] * 1.06);
        }

        exp[220] = exp[219] * 2;

        // Level 220 - 229 EXP
        for (int level = 221; level < 230; level++) {
            exp[level] = (long) (exp[level - 1] * 1.04);
        }

        exp[230] = exp[229] * 2;

        // Level 230 - 239 EXP
        for (int level = 231; level < 240; level++) {
            exp[level] = (long) (exp[level - 1] * 1.02);
        }

        exp[240] = exp[239] * 2;

        // Level 240 - 249's EXP
        for (int level = 241; level < 250; level++) {
            exp[level] = (long) (exp[level - 1] * 1.01);
        }

        exp[250] = 0;
    }*/
    public static long getExpNeededForLevel(final int level) {
        if (level < 1 || level >= exp.length) {
            return Long.MAX_VALUE;
        }
        return exp[level];
    }

    public static int getGuildExpNeededForLevel(final int level) {
        if (level < 0 || level >= guildexp.length) {
            return Integer.MAX_VALUE;
        }
        return guildexp[level];
    }

    public static int getPVPExpNeededForLevel(final int level) {
        if (level < 0 || level >= pvpExp.length) {
            return Integer.MAX_VALUE;
        }
        return pvpExp[level];
    }

    public static int getClosenessNeededForLevel(final int level) {
        return closeness[level - 1];
    }

    public static int getMountExpNeededForLevel(final int level) {
        return mountexp[level - 1];
    }

    public static int getTraitExpNeededForLevel(final int level) {
        if (level < 0 || level >= cumulativeTraitExp.length) {
            return Integer.MAX_VALUE;
        }
        return cumulativeTraitExp[level];
    }

    public static int getSetExpNeededForLevel(final int level) {
        if (level < 0 || level >= setScore.length) {
            return Integer.MAX_VALUE;
        }
        return setScore[level];
    }

    public static boolean isSpecialForce(int type) {
        switch (type) {
            case 2:
            case 3:
            case 6:
            case 7:
            case 11:
            case 12:
            case 13:
            case 17:
            case 19:
                return true;
            default:
                return false;
        }
    }

    public static int getMonsterHP(final int level) {
        if (level < 0 || level >= mobHpVal.length) {
            return Integer.MAX_VALUE;
        }
        return mobHpVal[level];
    }

    public static int getTimelessRequiredEXP(final int level) {
        return 70 + (level * 10);
    }

    public static int getReverseRequiredEXP(final int level) {
        return 60 + (level * 5);
    }

    public static int getProfessionEXP(final int level) {
        return ((100 * level * level) + (level * 400)) / 2;
    }

    public static boolean isHarvesting(final int itemId) {
        return itemId >= 1500000 && itemId < 1520000;
    }

    /*These are the item IDs for coupons used for experience boosts*/
    public static int _150PercentExpBoost = 5211111;
    public static int _200PercentExpBoost = 5211122;

    /**
     * Required range to be within the visibility of ranged map objects
     *
     * @return
     */
    public static final int maxViewRangeSq() {
        //return 1000000; // 1024 * 768
        return 1700000; // 1366 * 768
    }

    public static boolean isEvanForceSkill(int skillid) {
        switch (skillid) {
            case 22141012: // Dragon Dive
            case 22140022: // Dragon Dive

            case 22110022: // Dragon Flash
            case 22110023: // Dragon Flash
            case 22111011: // Wind Circle
            case 22141011: // Thunder Circle
            case 22171062: // Earth Circle
            case 22171063: // Dragon Breath
                return true;
            default:
                return false;
        }
    }

    /**
     * Required range to be able to control monsters [mark final here for compiler optimization from maxViewRangeSq()]
     *
     * @return
     */
    public static final int maxViewRangeSq_Half() {
        return maxViewRangeSq() / 2;
    }

    public static boolean isJobFamily(final int baseJob, final int currentJob) {
        return currentJob >= baseJob && currentJob / 100 == baseJob / 100;
    }

    public static short getBeginnerJob(final short job) {
        if (job % 1000 < 10) {
            return job;
        }
        switch (job / 100) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
                return 0;
            case 10:
            case 11:
            case 12:
            case 13:
            case 14:
            case 15:
                return 1000;
            case 20:
                return 2000;
            case 21:
                return 2000;
            case 22:
                return 2001;
            case 23:
                return 2002;
            case 24:
                return 2003;
            case 27:
                return 2004;
            case 31:
                return 3001;
            case 36:
                return 3002;
            case 30:
            case 32:
            case 33:
            case 35:
                return 3000;
            case 41:
                return 4001;
            case 42:
                return 4002;
            case 50:
            case 51:
                return 5000;
            case 60:
            case 61:
                return 6000;
            case 65:
                return 6001;
            case 100:
            case 110:
                return 10000;
        }
        return 0;
    }

    public static boolean isExplorer(final int job) {
        return job >= 0 && job < 1000;
    }

    public static boolean isCygnusKnight(final int job) {
        return job >= 1000 && job < 2000;
    }

    public static boolean isResistance(final int job) {
        return job / 1000 == 3;
    }

    public static boolean isLegend(final int job) { // Refers to "Legend"/"Hero" classes.
        return job >= 2100 && job <= 2800;
    }

    public static boolean isAran(final int job) {
        return job >= 2000 && job <= 2113 && job != 2001 && job != 2002 && job != 2003;
    }

    public static boolean isEvan(final int job) {
        return job == 2001 || (job / 100 == 22);
    }

    public static boolean isMercedes(final int job) {
        return job == 2002 || (job / 100 == 23);
    }

    public static boolean isJett(final int job) {
        return job == 508 || (job / 10 == 57);
    }

    public static boolean isPhantom(final int job) {
        return job == 2003 || (job / 100 == 24);
    }

    public static boolean isDawnWarrior(final int job) {
        return job == 1000 || (job >= 1100 && job <= 1113);
    }

    public static boolean isWindArcher(final int job) { //woops xD:3
        return job == 1300 || (job >= 1310 && job <= 1313);
    }

    public static boolean isPinkBean(final int jobId) {
        return jobId == 13100;
    }

    public static boolean isGameMaster(final int jobId) {
        return jobId == 800 || jobId == 900 || jobId == 910;
    }

    public static int getTrueJobGrade(int job) {
        int result;
        int jobGrade = job % 1000 / 100;
        if (job / 100 == 27) {
            jobGrade = 2;
        }
        result = 4;
        if (job / 100 != 36) {
            result = jobGrade;
        }
        return result;
    }

    public static boolean isDualBladeNoSP(int job) {
        return job == 430 || job == 432;
    }

    public static boolean isDemon(final int job) {
        return (isDemonSlayer(job) || isDemonAvenger(job));
    }

    public static boolean isDemonSlayer(final int job) {
        return job == 3001 || (job >= 3100 && job <= 3113 && job != 3101);
    }

    public static boolean isDemonAvenger(final int job) {
        return job == 3001 || job == 3101 || (job >= 3120 && job <= 3123 && job != 3100);
    }

    public static boolean isCannoneer(final int job) {
        return job == 1 || job == 501 || (job >= 530 && job <= 533);
    }

    public static boolean isDualBlade(final int job) {
        return job >= 430 && job <= 435;
    }

    public static boolean isMihile(final int job) {
        return job == 5000 || (job >= 5100 && job <= 5113);
    }

    public static boolean isLuminous(final int job) {
        return job == 2004 || (job >= 2700 && job <= 2713);
    }

    public static boolean isKaiser(final int job) {
        return job == 6000 || (job >= 6100 && job <= 6113);
    }

    public static boolean isAngelicBuster(final int job) {
        return job == 6001 || (job >= 6500 && job <= 6513);
    }

    public static boolean isNova(final int job) {
        return job / 1000 == 6;
    }

    public static boolean isXenon(final int job) {
        return job == 3002 || (job >= 3600 && job <= 3613);
    }

    public static boolean isShade(final int job) {
        return job == 2005 || (job >= 2500 && job <= 2512);
    }

    public static boolean isHayato(int job) {
        return job == 4001 || (job >= 4100 && job <= 4113);
    }

    public static boolean isKanna(int job) {
        return job == 4002 || (job >= 4200 && job <= 4213);
    }

    public static boolean isSengoku(int job) {
        return job / 1000 == 4;
    }

    public static boolean isZero(int job) {
        return job == 10000 || (job >= 10100 && job <= 10113);
    }

    public static boolean isKinesis(int job) {
        return job == 14000 || (job >= 14200 && job <= 14212);
    }

    /*Cygnus Jobs*/
    public static boolean isDawnWarriorCygnus(int job) {
        return job >= 1100 && job <= 1112;
    }

    public static boolean isBlazeWizardCygnus(int job) {
        return job >= 1200 && job <= 1212;
    }

    public static boolean isWindArcherCygnus(int job) {
        return job >= 1300 && job <= 1312;
    }

    public static boolean isNightWalkerCygnus(int job) {
        return job >= 1400 && job <= 1412;
    }

    public static boolean isThunderBreakerCygnus(int job) {
        return job >= 1500 && job <= 1512;
    }

    /*Explorer Warrior Jobs*/
    public static boolean isWarriorHero(int job) {
        return job >= 110 && job <= 112;
    }

    public static boolean isWarriorPaladin(int job) {
        return job >= 120 && job <= 122;
    }

    public static boolean isWarriorDarkKnight(int job) {
        return job >= 130 && job <= 132;
    }

    /*Explorer Magician Jobs*/
    public static boolean isMagicianFirePoison(int job) {
        return job >= 210 && job <= 212;
    }

    public static boolean isMagicianIceLightning(int job) {
        return job >= 220 && job <= 222;
    }

    public static boolean isMagicianBishop(int job) {
        return job >= 230 && job <= 232;
    }

    /*Explorer Archer Jobs*/
    public static boolean isArcherBowmaster(int job) {
        return job >= 310 && job <= 312;
    }

    public static boolean isArcherMarksman(int job) {
        return job >= 320 && job <= 322;
    }

    /*Explorer Thief Jobs*/
    public static boolean isThiefNightLord(int job) {
        return job >= 410 && job <= 412;
    }

    public static boolean isThiefShadower(int job) {
        return job >= 420 && job <= 422;
    }

    /*Explorer Pirate Jobs*/
    public static boolean isPirateBuccaneer(int job) {
        return job >= 510 && job <= 512;
    }

    public static boolean isPirateCorsair(int job) {
        return job >= 520 && job <= 522;
    }

    /*Aran Job*/
    public static boolean isAranJob(int job) {
        return job == 2000 || job >= 2100 && job <= 2112;
    }

    /*Resistance Primary Jobs*/
    public static boolean isBattleMage(int job) {
        return job >= 3200 && job <= 3212;
    }

    public static boolean isWildHunter(final int job) {
        return job >= 3300 && job <= 3312;
    }

    public static boolean isMechanic(final int job) {
        return job / 100 == 35;
    }

    public static boolean isBlaster(final int job) {
        return job >= 3700 && job <= 3712;
    }

    public static boolean isExceedAttack(int id) {
        switch (id) {
            case 31011000:
            case 31011004:
            case 31011005:
            case 31011006:
            case 31011007:
            case 31201000:
            case 31201007:
            case 31201008:
            case 31201009:
            case 31201010:
            case 31211000:
            case 31211007:
            case 31211008:
            case 31211009:
            case 31211010:
            case 31221000:
            case 31221009:
            case 31221010:
            case 31221011:
            case 31221012:
                return true;
        }
        return false;
    }

    public static boolean isChangeable(int id) {
        switch (id) {
            case 9306000:
                return true;
        }
        return false;
    }

    /*
     *  Damage Corrections & Modfications
     *  @purpose This value is added to the damage lines for certain skills, used to fix or modify the result.
     */
    public static long damageCorrectRequest(MapleCharacter pPlayer, int nSkill, long nDamage) {
        long nDamageChange = 0;
        switch (nSkill) {
            case DemonAvenger.EXCEED_DEMON_STRIKE:
            case DemonAvenger.EXCEED_DEMON_STRIKE_1:
            case DemonAvenger.EXCEED_DEMON_STRIKE_2:
            case DemonAvenger.EXCEED_DEMON_STRIKE_3:
            case DemonAvenger.EXCEED_DEMON_STRIKE_4:
                nDamageChange = (long) (nDamage * (pPlayer.getSkillLevel(DemonAvenger.NETHER_SLICE) * 0.05));
                break;
        }
        return nDamageChange;
    }

    /*
    *   Checks if Superior Equip for Star Force.
    */
    public static boolean isSuperiorEquip(int nItemID) {
        switch (nItemID) {
            case 1122241:
            case 1122242:
            case 1122243:
            case 1122244:
            case 1122245:
            case 1132164:
            case 1132165:
            case 1132166:
            case 1132167:
            case 1132168:
            case 1132169:
            case 1132170:
            case 1132171:
            case 1132172:
            case 1132173:
            case 1132174:
            case 1132175:
            case 1132176:
            case 1132177:
            case 1132178:
            case 1102471:
            case 1102472:
            case 1102473:
            case 1102474:
            case 1102475:
            case 1102476:
            case 1102477:
            case 1102478:
            case 1102479:
            case 1102480:
            case 1102481:
            case 1102482:
            case 1102483:
            case 1102484:
            case 1102485:
            case 1102627:
            case 1102628:
            case 1082543:
            case 1082544:
            case 1082545:
            case 1082546:
            case 1082547:
            case 1072732:
            case 1072733:
            case 1072734:
            case 1072735:
            case 1072736:
            case 1072737:
            case 1072738:
            case 1072739:
            case 1072740:
            case 1072741:
            case 1072743:
            case 1072744:
            case 1072745:
            case 1072746:
            case 1072747:
                return true;
            default:
                return false;
        }
    }
    
    public short changeExp(short level, MapleCharacter chr) {
        switch (chr.getLevel()) {
            case 100:
                return 100;
            case 140:
                return 140;
        }
        return 1;
    }

    public static int getKaiserMode(int id) {
        switch (id) {
            case 61100005:
            case 61110005:
            case 61120010:
                return 60001216;
        }
        return 0;
    }

    public static int getLuminousSkillMode(int id) {
        switch (id) {
            case 27001100:
            case 27101100:
            case 27111100:
            case 27111101:
            case 27121100:
                return 20040216;//light
            case 27001201:
            case 27101202:
            case 27111202:
            case 27121201:
            case 27121202:
            case 27120211:
                return 20040217;//dark
            //           case 27111303:
            //           case 27121303:
            //               return 20040220;
        }
        return 0;
    }

    public static boolean isLightSkills(int skillid) {
        switch (skillid) {
            case 20041226: // ìŠ¤íŽ™íŠ¸ëŸ´ ë�¼ì�´íŠ¸ (ê¸°ë³¸ ì§�ì—…)
            case 27001100: // íŠ¸ìœ™í�´ í”Œëž˜ì‰¬
            case 27101100: // ì‹¤í”¼ë“œ ëžœì„œ
            case 27111100: // ìŠ¤íŽ™íŠ¸ëŸ´ ë�¼ì�´íŠ¸
            case 27121100: // ë�¼ì�´íŠ¸ ë¦¬í”Œë ‰ì…˜
                return true;
        }
        return false;
    }

    public static boolean isDarkSkills(int skillid) {
        switch (skillid) {
            case 27001201: // ë‹¤í�¬ í�´ë§�
            case 27101202: // ë³´ì�´ë“œ í”„ë ˆì…”
            case 27111202: // ë…¹ìŠ¤í”¼ì–´
            case 27121201: // ëª¨ë‹� ìŠ¤íƒ€í�´
            case 27121202: // ì•„í�¬ì¹¼ë¦½ìŠ¤
                return true;
        }
        return false;
    }

    public static int getLinkSkillByJob(final int job) {
        if (isCannoneer(job)) { //Pirate Blessing
            return 80000000;
        } else if (isCygnusKnight(job)) { //Cygnus Blessing
            return 80000070;
        } else if (isMercedes(job)) { //Elven Blessing
            return 80001040;
        } else if (isDemonSlayer(job)) { //Fury Unleashed
            return 80000001;
        } else if (isDemonAvenger(job)) { //Wild Rage
            return 80000050;
        } else if (isJett(job)) { //Core Aura
            return 80001151;
        } else if (isPhantom(job)) { //Phantom Instinct
            return 80000002;
        } else if (isMihile(job)) { //Knight's Watch
            return 80001140;
        } else if (isLuminous(job)) { //Light Wash
            return 80000005;
        } else if (isAngelicBuster(job)) { //Terms and Conditions   
            return 80001155;
        } else if (isHayato(job)) { //Keen Edge  
            return 80000003;
        } else if (isKanna(job)) { //Elementalism    
            return 40020002;
        } else if (isKaiser(job)) { //Iron Will
            return 80000006;
        } else if (isXenon(job)) { //Hybrid Logic    
            return 80000047;
        } else if (isBeastTamer(job)) { //Focus Spirit    
            return 80010006;
        }
        return 0;
    }

    /**
     * This is hard coded! We still need to look into WZ on other flip affected area skill
     *
     * ?is_flip_affected_area_skill@@YAHJ@Z
     *
     * @param sourceSkillId
     * @return
     */
    public static boolean isFlipAffectedAreaSkill(int sourceSkillId) {
        //if ( sub_D3B900(a1) )
        //return 1;
        if (sourceSkillId > 131001107) {
            return sourceSkillId == PinkBean.BREEZY;
        } else if (sourceSkillId == PinkBean.POSIE || sourceSkillId == NightLord.FRAILTY_CURSE) {
            return true;
        }
        return sourceSkillId == Mihile.RADIANT_CROSS_SPREAD;
    }

    public static boolean isRecoveryIncSkill(final int id) {
        switch (id) {
            case 1110000:
            case 2000000:
            case 1210000:
            case 11110000:
            case 4100002:
            case 4200001:
                return true;
        }
        return false;
    }

    public static boolean iskaiser_Transfiguration_Skill(int id) {
        Skill skill = SkillFactory.getSkill(getLinkedAttackSkill(id));
        if (skill == null) {
            return false;
        }
        Tuple<String, String, String> skillName_ = MapleStringInformationProvider.getSkillStringCache().get(id);
        String skillName = skillName_ != null ? skillName_.get_2() : "";

        if ((skillName.contains("(Transfiguration)"))) {
            return true;
        }
        switch (skill.getId()) {
            case 61001004:
            case 61001005:
            case 61110009:
            case 61111111: // Wing Beat
            case 61111113:
            case 61111114:
            case 61120008:
            case 61121015:
            case 61120018:
            case 61121116:
                return true;
        }
        return false;
    }

    /**
     * These buffs don't need to write foreign buff effects to other characters.
     * @param nSkillID
     * @return boolean
     */
    public static boolean nonForeignEffect(int nSkillID) {
        switch (nSkillID) {
            case NightWalker.DARK_SERVANT:
            case NightWalker.SHADOW_ILLUSION:
            case NightWalker.SHADOW_ILLUSION_1:
            case NightWalker.SHADOW_ILLUSION_2:
                return true;
            default:
                return false;
        }
    }
    
    public static boolean isJaguarSkill(int nSkillID) {
        switch (nSkillID) {
            case WildHunter.SUMMON_JAGUAR:
            case WildHunter.SUMMON_JAGUAR_1:
            case WildHunter.SUMMON_JAGUAR_2:
            case WildHunter.SUMMON_JAGUAR_3:
            case WildHunter.SUMMON_JAGUAR_4:
            case WildHunter.SUMMON_JAGUAR_5:
            case WildHunter.SUMMON_JAGUAR_6:
            case WildHunter.SUMMON_JAGUAR_7:
            case WildHunter.SUMMON_JAGUAR_8:
            case WildHunter.SUMMON_JAGUAR_9:
            case WildHunter.SUMMON_JAGUAR_10:
            case WildHunter.SWIPE:
            case WildHunter.SWIPE_1:
            case WildHunter.SWIPE_2:
            case WildHunter.WILD_LURE:
            case WildHunter.DASH_N_SLASH:
            case WildHunter.DASH_N_SLASH_1:
            case WildHunter.DASH_N_SLASH_2:
            case WildHunter.DASH_N_SLASH_3:
            case WildHunter.SONIC_ROAR:
            case WildHunter.SONIC_ROAR_1:
            case WildHunter.SONIC_ROAR_2:
            case WildHunter.JAGUAR_SOUL:
            case WildHunter.JAGUAR_SOUL_1:
            case WildHunter.JAGUAR_RAMPAGE:
            case WildHunter.JAGUAR_RAMPAGE_1:
            case WildHunter.JAGUAR_RAMPAGE_2:
                return true;
            default: 
                return false;
        }
    }
    
    //Literally useless function, but I cbf to check linked attack skills rn, its 5am.
    public static boolean bypassLinkedAttackCheck(int nSkillID) {
        switch (nSkillID) {
            case Blaster.RELOAD:
            case Blaster.REVOLVING_CANNON:
            case Blaster.REVOLVING_CANNON_1:
            case Blaster.REVOLVING_CANNON_2:
                return true;
            default: 
                return false;
        }
    }
    
    public static boolean isLinkedAttackSkill(final int id) {
        return getLinkedAttackSkill(id) != id;
    }

    public static int getLinkedAttackSkill(final int id) {
        switch (id) {
            case 37001001:
            case 37000010:
            case 37121004:
            case 37120023:
            case 37120019:
            case 37120018:
            case 37120017:
            case 37120016:
            case 37120015:
                return 37120014;
            case 27111100:
                return 20041226; // Spectral Light
            case 33121255:
            case 33121052:
                return 33120056; // Jaguar Rampage
            //case 400004077:
            //case 95001000:
            //case 3121013:
            //case Ranger.ARROW_BLASTER:
            //    return 3120019;
            case 24120043: // Tempest - Reinforce
            case 24120044: // Tempest - Cooldown Cutter
            case 24120045: // Tempest - Extra Target
                return 24121005; // Tempest
            case 14120048: // Dark Omen - Reinforce
            case 14120047: // Dark Omen - Spread
            case 14120046: // Dark Omen - Cooldown Cutter
                return 14121003; // Dark Omen
            case 4120045: // Showdown - Enhance
            case 4120044: // Showdown - Spread
            case 4120043: // Showdown - Reinforce
                return 4121017; // Showdown (Night Lord)
            case 21001009:
                return 21000004; // Smash Wave
            case 22140022: // Dragon Dive
            case 22141012: // Dragon Dive
            case 22110023: // Dragon Flash
            case 22110022: // Dragon Flash
                return 22111012; // Dragon Flash
            //case 400004077: // Arrow blaster?
            //case 3120019: // Hurricane
            //    return 3121013;
            case 36121013:
            case 36121014:
                return 36121002;
            case 21110015:
            case 21110007:
            case 21110008:
                return 21110002;
            case 21000006:
                return 21000002;
            case 12000026:
                return 12001020;
            case 12100028:
                return 12100020;
            case 12110028:
                return 12110020;
            case 12120010:
                return 12120006;
            case 21120015:
            case 21120009:
            case 21120010:
                return 21120002;
            case 4321001:
                return 4321000;
            case 33101008:
                return 33101004;
            case 35101009:
            case 35101010:
                return 35100008;
            case 35121013:
                return 35120013;
            case 35121011:
                return 35121009;
            case 35111009:
            case 35111010:
                return 35111001;
            case 35100004:
                return 35101004;
            case 32001007:
            case 32001008:
            case 32001009:
            case 32001010:
            case 32001011:
                return 32001001;
            case 5300007:
                return 5301001;
            case 5320011:
                return 5321004;
            case 23101007:
                return 23101001;
            case 23111010:
            case 23111009:
                return 23111008;
            case 31001006:
            case 31001007:
            case 31001008:
                return 31000004;
            case 27120211:
                return 27121201;
            case 61001004:
            case 61001005:
            case 61110212:
            case 61120219:
                return 61001000;
            case 61110211:
            case 61120007:
            case 61121217:
                return 61101002;
            case 61111215:
                return 61001101;
            case 61111217:
                return 61101101;
            case 61111216:
                return 61101100;
            case 61111219:
                return 61111101;
            case 61111113:
            case 61111218:
                return 61111100;
            case 61121201:
                return 61121100;
            case 61121203:
                return 61121102;
            case 61110009:
                return 61111003;
            /*case 61121217:
                return 61120007;*/
            case 61121116:
                return 61121104;
            case 61121223:
                return 61121221;
            case 61121221:
                return 61121104;
            case 65101006:
                return 65101106;
            case 65121007:
            case 65121008:
                return 65121101;
            case 61111220:
                return 61121105;
            //case 61120018:
            //      return 61121105;
            case 65111007:
                return 65111100;
            case 4100012:
                return 4100011;
            case 24121010:
                return 24121003;
            case 24111008:
                return 24111006;
            case 5001008:
                return 5001005;
            case 61121053://kaiser hyper
            case 61120008:
                return 61111008;
            case 51100006:
                return 51101006;
            case 31011004:
            case 31011005:
            case 31011006:
            case 31011007:
                return 31011000;
            case 31201007:
            case 31201008:
            case 31201009:
            case 31201010:
                return 31201000;
            case 31211007:
            case 31211008:
            case 31211009:
            case 31211010:
                return 31211000;
            /*case 31221009:
            case 31221010:
            case 31221011:
            case 31221012:
                return 31221000; // Demon Avenger - Exceed Execution*/
            case 31211011:
                return 31211002;
            case 31221014:
                return 31221001;
            case 25100010:
                return 25100009;
            case 25120115:
                return 25120110;
            case 36101008:
            case 36101009:
                return 36101000;
            case 36111010:
            case 36111009:
                return 36111000;
            /*case 36121011:
            case 36121012:
                return 36121001; // Xenon - Mecha Purge Snipe*/
            case 35100009:
                return 35100009;
            case 2121055:
                return 2121052;
            case 11121055:
                return 11121052;
            case 1120017:
                return 1121008;
            case 25000003:
                return 25001002;
            case 25000001:
                return 25001000;
            case 25100001:
                return 25101000;
            case 25110001:
            case 25110002:
            case 25110003:
                return 25111000;
            case 25120001:
            case 25120002:
            case 25120003:
                return 25121000;
            //case 95001000:
            //    return 3111013;
            case 4210014:
                return 4211006;
            case 101000102:
                return 101000101;
            case 14101021:
                return 14101020;
            case 14111021:
            case 14111005:
                return 14111020;
            case 14111023:
                return 14111022;
            case 14121002:
                return 14121001;
            case 12120011:
                return 12121001;// Quad Star, 3rd job
            case 12120012:
                return 12121003;
            //case 101000102:
            //return 101000101;
            case 101000202:
                return 101000201;
            case 101100202:
                return 101100201;
            case 101110201:
                return 101110200;
            case 101110204:
                return 101110203;
            case 101120101:
                return 101120100;
            case 101120103:
                return 101120102;
            case 101120105:
            case 101120106:
                return 101120104;
            case 101120203:
                return 101120202;
            case 101120205:
            case 101120206:
                return 101120204;
            case 101120200:
                return 101121200;
            case 41001005:
            case 41001004:
                return 41001000;
            case 41101009:
            case 41101008:
                return 41101000;
            case 41111012:
            case 41111011:
                return 41111000;
            case 41001000:
                return 41001002;
            // case 41120013:
            case 41001002:
            case 41121012:
            case 41121011:
                return 41121000;
            case 42001006:
            case 42001005:
                return 42001000;
            case 42001007:
                return 42001002;
            case 42100010:
                return 42101001;
            case 33101006:
            case 33101007:
                return 33101005;
            case 35001001:
                return 35101009;
            case 42111011:
                return 42111000;
            case 11101220:
                return 11101120;
            case 11101221:
                return 11101121;
            case 11111120:
                return 11111220;
            case 11111121:
                return 11111221;
            case 11121102:
            case 11121201:
                return 11121101; // Moon dancer 
            case 11121103:
                return 11121203; // Solar Pierce
            case 4120019: // Night Lord's Mark
                return 4120018;
            case 5710012:
                return 5711002;
            case 2101010:
                return 2100010;
            case 12121055:
                return 12121054;
            case 13120010:
                return 13120003;
            case 13110027:
                return 13110022;
            case 13101022:
            case 13100027:
                return 13100022;
            case 33121214:
                return 33121114;
            case 2121006: // Paralyze:
                return 2121052;
            case 11121202: // Speeding Sunset（In the air)
                return 11121101; // Moon Dancer
            case 21110003:// Final Toss
                return 21111013;// Final Toss
            case 21120005:// Final Blow
                return 21121013;// Final Blow
            case 21110006:// Rolling Spin
                return 21111014;// Rolling Spin
            case 30010183:
            case 30010184:
            case 30010186:
                return 30010110;
            case 31010004:
            case 31010005:
            case 31010006:
            case 31010007:
            case 4221016:
                return 4221014;
            case 5701012:
            case 5710020:
                return 5701011;
            case 31121010:
                return 31121000;
            case 5211015:
            case 5211016:
                return 5211011;
            case 5001009:
                return 5101004;
            case 41001006:
            case 41001007:
            case 41001008:
                return 41001002;
            case 41120013:
            case 41120012:
            case 41120011:
                return 41121000;
            case 112000001:
            case 112000002:
                return 112000000;
            case 112120001:
            case 112120002:
            case 112120003:
                return 112120000;
            case 131001001:
            case 131001002:
            case 131001003:
            case 131001013:
                return 131001000;
            case 14001004:// Lucky Seven (NW)
                return 14001020;
            case 14111022:
                return 14121001;
            case 4100011:
                return 4101011;
            case 25121055:
                return 25121030;
            // i want u to know the way u handle this shit is stupid af
            case 12120013:
            case 12120014:
                return 12121004;
            case 33001007:
            case 33001008:
            case 33001009:
            case 33001010:
            case 33001011:
            case 33001012:
            case 33001013:
            case 33001014:
            case 33001015:
                return 33000038;
            case 142100001:
                return 142100000;
            case 142110001:
                return 142110000;
        }
        return id;
    }

    public static boolean isSpecialBuff(final int skillid) {
        switch (skillid) {
            case 23101003://Spirit Surge
            case 65101002://Power Transfer
            case 4111002://Final Feint
            case 4211008://Shadow Partner
            case 14111000://Shadow Partner
            case 4331002://Mirror Image
            case 36111006://Manifest Projector
            case 15121004://Arc Charger
            case 31121054://Blue Blood
            case 65121004://Star Gazer
                return true;
        }
        return false;
    }

    public final static boolean isForceIncrease(int skillid) {
        switch (skillid) {
            case 24100003:
            case 24120002:
            case 31000004:
            case 31001006:
            case 31001007:
            case 31001008:

            case 30010166:
            case 30011167:
            case 30011168:
            case 30011169:
            case 30011170:
                return true;
        }
        return false;
    }

    public static int findSkillByName(String name, int job, int def) {
        int skillid = 0;
        for (Skill skill : SkillFactory.getAllSkills()) {
            Tuple<String, String, String> skillName_ = MapleStringInformationProvider.getSkillStringCache().get(skill.getId());

            if (skillName_ != null && skillName_.get_2().toLowerCase().contains(name.toLowerCase())) {
                if (skill.getId() / 10000 == job) {
                    skillid = skill.getId();
                }
            }
        }
        if (skillid != 0) {
            return skillid;
        } else {
            return def;
        }
    }

    public static int getBOF_ForJob(final int job) {
        return PlayerStats.getSkillByJob(12, job);
    }

    public static int getEmpress_ForJob(final int job) {
        return PlayerStats.getSkillByJob(73, job);
    }

    public static boolean isElementAmp_Skill(final int skill) {
        switch (skill) {
            case 2110001:
            case 2210001:
            case 12110001:
            case 22150000:
                return true;
        }
        return false;
    }

    public static int getMPEaterForJob(final int job) {
        switch (job) {
            case 210:
            case 211:
            case 212:
                return 2100000;
            case 220:
            case 221:
            case 222:
                return 2200000;
            case 230:
            case 231:
            case 232:
                return 2300000;
        }
        return 2100000; // Default, in case GM
    }

    public static int getJobShortValue(int job) {
        if (job >= 1000) {
            job -= (job / 1000) * 1000;
        }
        job /= 100;
        switch (job) {
            case 4:
                // For some reason dagger/ claw is 8.. IDK
                job *= 2;
                break;
            case 3:
                job += 1;
                break;
            case 5:
                job += 11; // 16
                break;
            default:
                break;
        }
        return job;
    }

    public static boolean isPyramidSkill(final int skill) {
        return isBeginnerJob(skill / 10000) && skill % 10000 == 1020;
    }

    public static boolean isInflationSkill(final int skill) {
        return isBeginnerJob(skill / 10000) && skill % 10000 == 1092;
    }

    public static boolean isMulungSkill(final int skill) {
        return isBeginnerJob(skill / 10000) && (skill % 10000 == 1009 || skill % 10000 == 1010 || skill % 10000 == 1011);
    }

    public static boolean isIceKnightSkill(final int skill) {
        return isBeginnerJob(skill / 10000) && (skill % 10000 == 1098 || skill % 10000 == 99 || skill % 10000 == 100 || skill % 10000 == 103 || skill % 10000 == 104 || skill % 10000 == 1105);
    }

    public static boolean isThrowingStar(final int itemId) {
        return itemId / 10000 == 207;
    }

    public static boolean isBullet(final int itemId) {
        return itemId / 10000 == 233;
    }

    public static boolean isRechargable(final int itemId) {
        return isThrowingStar(itemId) || isBullet(itemId);
    }

    public static boolean isArrowForCrossBow(final int itemId) {
        return itemId >= 2061000 && itemId < 2062000;
    }

    public static boolean isArrowForBow(final int itemId) {
        return itemId >= 2060000 && itemId < 2061000;
    }

    public static MapleInventoryType getInventoryType(final int itemId) {
        final byte type = (byte) (itemId / 1000000);
        if (type < 1 || type > 5) {
            return MapleInventoryType.UNDEFINED;
        }
        return MapleInventoryType.getByType(type);
    }

    public static MapleWeaponType getWeaponType(final int itemId) {
        int cat = itemId / 10000;
        cat = cat % 100;
        switch (cat) { // 39, 50, 51 ??
            case 21:
                return MapleWeaponType.ROD;
            case 30:
                return MapleWeaponType.SWORD1H;
            case 31:
                return MapleWeaponType.AXE1H;
            case 32:
                return MapleWeaponType.BLUNT1H;
            case 33:
                return MapleWeaponType.DAGGER;
            case 34:
                return MapleWeaponType.KATARA;
            case 35:
                return MapleWeaponType.MAGIC_ARROW; // can be magic arrow or cards
            case 36:
                return MapleWeaponType.CANE;
            case 37:
                return MapleWeaponType.WAND;
            case 38:
                return MapleWeaponType.STAFF;
            case 40:
                return MapleWeaponType.SWORD2H;
            case 41:
                return MapleWeaponType.AXE2H;
            case 42:
                return MapleWeaponType.BLUNT2H;
            case 43:
                return MapleWeaponType.SPEAR;
            case 44:
                return MapleWeaponType.POLE_ARM;
            case 45:
                return MapleWeaponType.BOW;
            case 46:
                return MapleWeaponType.CROSSBOW;
            case 47:
                return MapleWeaponType.CLAW;
            case 48:
                return MapleWeaponType.KNUCKLE;
            case 49:
                return MapleWeaponType.GUN;
            case 52:
                return MapleWeaponType.DUAL_BOW;
            case 53:
                return MapleWeaponType.CANNON;
            case 56:
                return MapleWeaponType.LAPIS;
            case 57:
                return MapleWeaponType.LAZULI;
        }
        //System.out.println("Found new Weapon: " + cat + ", ItemId: " + itemId);
        return MapleWeaponType.NOT_A_WEAPON;
    }

    public static boolean isEquip(final int itemId) {
        return itemId / 1000000 == 1;
    }

    public static boolean isCleanSlate(int itemId) {
        return itemId / 100 == 20490;
    }

    public static boolean isAccessoryScroll(int itemId) {
        return itemId / 100 == 20492;
    }

    public static boolean isChaosScroll(int itemId) {
        if (itemId >= 2049105 && itemId <= 2049110) {
            return false;
        }
        return itemId / 100 == 20491 || itemId == 2040126;
    }

    public static int getChaosNumber(int itemId) {
        return itemId == 2049116 ? 10 : 5;
    }

    public static boolean isEquipScroll(int scrollId) {
        return scrollId / 100 == 20493;
    }

    public static boolean isPotentialScroll(int scrollId) {
        return scrollId / 100 == 20494 || scrollId / 100 == 20497 || scrollId == 5534000 || scrollId == 2049700 || scrollId == 2049701 || scrollId == 2049702 || scrollId == 2049703;
    }

    public static boolean isAzwanScroll(int scrollId) {
        //return MapleItemInformationProvider.getInstance().getEquipStats(scroll.getItemId()).containsKey("tuc");
        //should add this ^ too.
        return scrollId >= 2046060 && scrollId <= 2046069 || scrollId >= 2046141 && scrollId <= 2046145 || scrollId >= 2046519 && scrollId <= 2046530 || scrollId >= 2046701 && scrollId <= 2046712;
    }

    public static boolean isSpecialScroll(final int scrollId) {
        switch (scrollId) {
            case 2040727: // Spikes on show
            case 2041058: // Cape for Cold protection
            case 2530000:
            case 2530001:
            case 2531000:
            case 5063000:
            case 5064000:
                return true;
        }
        return false;
    }

    public static boolean isTwoHanded(final int itemId) {
        switch (getWeaponType(itemId)) {
            case AXE2H:
            case GUN:
            case KNUCKLE:
            case BLUNT2H:
            case BOW:
            case CLAW:
            case CROSSBOW:
            case POLE_ARM:
            case SPEAR:
            case SWORD2H:
            case CANNON:
            case DUAL_BOW:
            case LAPIS:
            case LAZULI:
                return true;
            default:
                return false;
        }
    }

    public static boolean isTownScroll(final int id) {
        return id >= 2030000 && id < 2040000;
    }

    public static boolean isUpgradeScroll(final int id) {
        return id >= 2040000 && id < 2050000;
    }

    public static boolean isUse(final int id) {
        return id >= 2000000 && id < 3000000;
    }

    public static boolean isSummonSack(final int id) {
        return id / 10000 == 210;
    }

    public static boolean isMonsterCard(final int id) {
        return id / 10000 == 238;
    }

    public static boolean isSpecialCard(final int id) {
        return id / 1000 >= 2388;
    }

    public static int getCardShortId(final int id) {
        return id % 10000;
    }

    public static boolean isGem(final int id) {
        return id >= 4250000 && id <= 4251402;
    }

    public static boolean isOtherGem(final int id) {
        switch (id) {
            case 4001174:
            case 4001175:
            case 4001176:
            case 4001177:
            case 4001178:
            case 4001179:
            case 4001180:
            case 4001181:
            case 4001182:
            case 4001183:
            case 4001184:
            case 4001185:
            case 4001186:
            case 4031980:
            case 2041058:
            case 2040727:
            case 1032062:
            case 4032334:
            case 4032312:
            case 1142156:
            case 1142157:
                return true; //mostly quest items
        }
        return false;
    }

    public static int getTaxAmount(long meso) {
        if (meso >= 100000000) {
            return (int) Math.round(0.06 * meso);
        } else if (meso >= 25000000) {
            return (int) Math.round(0.05 * meso);
        } else if (meso >= 10000000) {
            return (int) Math.round(0.04 * meso);
        } else if (meso >= 5000000) {
            return (int) Math.round(0.03 * meso);
        } else if (meso >= 1000000) {
            return (int) Math.round(0.018 * meso);
        } else if (meso >= 100000) {
            return (int) Math.round(0.008 * meso);
        }
        return 0;
    }

    public static int storeTax(final int meso) {
        if (meso >= 100000000) {
            return (int) Math.round(0.03 * meso);
        } else if (meso >= 25000000) {
            return (int) Math.round(0.025 * meso);
        } else if (meso >= 10000000) {
            return (int) Math.round(0.02 * meso);
        } else if (meso >= 5000000) {
            return (int) Math.round(0.015 * meso);
        } else if (meso >= 1000000) {
            return (int) Math.round(0.009 * meso);
        } else if (meso >= 100000) {
            return (int) Math.round(0.004 * meso);
        }
        return 0;
    }

    public static int getAttackDelay(final int id, final Skill skill) {
        switch (id) { // Assume it's faster(2)
            case 3121004: // Storm of Arrow
            case 24121000:
            //case 24121005:
            case 23121000:
            case 33121009:
            case 13111002: // Storm of Arrow
            case 5221004: // Rapidfire
            case 5721001: // Rapidfire
            case 5201006: // Recoil shot/ Back stab shot
            case 35121005:
            case 35111004:
            case 35121013:
            case 31121005:
            case 24120002:
            case 24100003:
                return 40; //reason being you can spam with final assaulter
            case 14111005:
            case 4121013:
            case 4121007:
            case 5221007:
            case 112100000: // Leopard's Paw
            case 112100002: // Leopard's Pounce
            case 112100003: // Leopard's Roar   
            case 112001004: // Deep Breath
            case 112001005: // Really Deep Breath
            case 112001006: // Majestic Trumpet
            case 112121004: // Fire Kitty
            case 112121057: // Cat's Cradle Blitzkrieg
            case 112121005: // Purr Zone
            case 112121013: // Meow Heal  
            case 112001008: // Fishy Slap
            case 22111012: // Dragon Flash (hopefully this fixes)
                return 99; //skip duh chek
            case 0: // Normal Attack, TODO delay for each weapon type
                return 570;
        }
        if (skill != null && skill.getSkillType() == 3) {
            return 0; //final attack
        }
        if (skill != null && skill.getDelay() > 0 && !isNoDelaySkill(id)) {
            return skill.getDelay();
        }
        // TODO delay for final attack, weapon type, swing,stab etc
        return 330; // Default usually
    }

    public static byte gachaponRareItem(final int id) {
        switch (id) {
            case 2340000: // White Scroll
            case 2049100: // Chaos Scroll
            case 3010014: // Moon Star Chair
            case 3010043: // Halloween Brromstick
            case 3010073: // Giant Pink bean Cushion
            case 3010072: // Miwok Chief's Chair
            case 3010068: // Lotus Leaf Chair
            case 3010085: // Olivia's Chair
            case 3010118: // Musical Note Chair
            case 3010124: // Dunas Jet Char
            case 3010125: // Nibelung Battleship
            case 3010131: //chewing panda chair
            case 3010137: // Dragon lord Chair
            case 3010156: // Visitor Representative
            case 3010615: // Nao Resting
            case 3010592: //Black Bean Chair
            case 3010602: // Heart Cloud Chair
            case 3010670: // absolute Ring chair
            case 3010728: // ilove Maplestory
            case 1342033: // VIP Katara
            case 1372078: // VIP wand
            case 1382099: // Staff
            case 1402090: // Two handed Sword
            case 1412062: // Two Handed Axe
            case 1422063: // Two handed Blunt Weapon
            case 1432081: // Spear
            case 1442111: // Polearm
            case 1452106: // Bow
            case 1462091: // Crossbow
            case 1472117: // Claw
            case 1482079: // Knuckle
            case 1492079: // Gun
            case 1302147: // one sword
            case 1312062: // One handed Axe
            case 1322090: // One Handed Blunt Weapon
            case 1332120: // Dagger(LUK)
            case 1332125: // Dagger (STR)< end of VIP
            case 1102041: // Pink Adventure Cape
            case 1022082: // Spectrum Goog
            case 1072238: // Violet snow shoes
            case 5062002: // Super Miracle
            case 5062003: // Miracle
            case 5062005: // Miracle
            case 2040834: // Scroll for gloves for att 50%^
            case 1102042: // Purple adventure cape
                return 2;
            //1 = wedding msg o.o
        }
        return 0;
    }
    public final static int[] goldrewards = {
        2049400, 1,
        2049401, 2,
        2049301, 2,
        2340000, 1, // white scroll
        2070007, 2,
        2070016, 1,
        2330007, 1,
        2070018, 1, // balance fury
        1402037, 1, // Rigbol Sword
        2290096, 1, // Maple Warrior 20
        2290049, 1, // Genesis 30
        2290041, 1, // Meteo 30
        2290047, 1, // Blizzard 30
        2290095, 1, // Smoke 30
        2290017, 1, // Enrage 30
        2290075, 1, // Snipe 30
        2290085, 1, // Triple Throw 30
        2290116, 1, // Areal Strike
        1302059, 3, // Dragon Carabella
        2049100, 1, // Chaos Scroll
        1092049, 1, // Dragon Kanjar
        1102041, 1, // Pink Cape
        1432018, 3, // Sky Ski
        1022047, 3, // Owl Mask
        3010051, 1, // Chair
        3010020, 1, // Portable meal table
        2040914, 1, // Shield for Weapon Atk

        1432011, 3, // Fair Frozen
        1442020, 3, // HellSlayer
        1382035, 3, // Blue Marine
        1372010, 3, // Dimon Wand
        1332027, 3, // Varkit
        1302056, 3, // Sparta
        1402005, 3, // Bezerker
        1472053, 3, // Red Craven
        1462018, 3, // Casa Crow
        1452017, 3, // Metus
        1422013, 3, // Lemonite
        1322029, 3, // Ruin Hammer
        1412010, 3, // Colonian Axe

        1472051, 1, // Green Dragon Sleeve
        1482013, 1, // Emperor's Claw
        1492013, 1, // Dragon fire Revlover

        1382049, 1,
        1382050, 1, // Blue Dragon Staff
        1382051, 1,
        1382052, 1,
        1382045, 1, // Fire Staff, Level 105
        1382047, 1, // Ice Staff, Level 105
        1382048, 1, // Thunder Staff
        1382046, 1, // Poison Staff

        1372035, 1,
        1372036, 1,
        1372037, 1,
        1372038, 1,
        1372039, 1,
        1372040, 1,
        1372041, 1,
        1372042, 1,
        1332032, 8, // Christmas Tree
        1482025, 7, // Flowery Tube

        4001011, 8, // Lupin Eraser
        4001010, 8, // Mushmom Eraser
        4001009, 8, // Stump Eraser

        2047000, 1,
        2047001, 1,
        2047002, 1,
        2047100, 1,
        2047101, 1,
        2047102, 1,
        2047200, 1,
        2047201, 1,
        2047202, 1,
        2047203, 1,
        2047204, 1,
        2047205, 1,
        2047206, 1,
        2047207, 1,
        2047208, 1,
        2047300, 1,
        2047301, 1,
        2047302, 1,
        2047303, 1,
        2047304, 1,
        2047305, 1,
        2047306, 1,
        2047307, 1,
        2047308, 1,
        2047309, 1,
        2046004, 1,
        2046005, 1,
        2046104, 1,
        2046105, 1,
        2046208, 1,
        2046209, 1,
        2046210, 1,
        2046211, 1,
        2046212, 1,
        //list
        1132014, 3,
        1132015, 2,
        1132016, 1,
        1002801, 2,
        1102205, 2,
        1332079, 2,
        1332080, 2,
        1402048, 2,
        1402049, 2,
        1402050, 2,
        1402051, 2,
        1462052, 2,
        1462054, 2,
        1462055, 2,
        1472074, 2,
        1472075, 2,
        //pro raven
        1332077, 1,
        1382082, 1,
        1432063, 1,
        1452087, 1,
        1462053, 1,
        1472072, 1,
        1482048, 1,
        1492047, 1,
        2030008, 5, // Bottle, return scroll
        1442018, 3, // Frozen Tuna
        2040900, 4, // Shield for DEF
        2049100, 10,
        2000005, 10, // Power Elixir
        2000004, 10, // Elixir
        4280000, 8,
        2430144, 10,
        2290285, 10,
        2028061, 10,
        2028062, 10,
        2530000, 5,
        2531000, 5}; // Gold Box
    public final static int[] silverrewards = {
        2049401, 2,
        2049301, 2,
        3010041, 1, // skull throne
        1002452, 6, // Starry Bandana
        1002455, 6, // Starry Bandana
        2290084, 1, // Triple Throw 20
        2290048, 1, // Genesis 20
        2290040, 1, // Meteo 20
        2290046, 1, // Blizzard 20
        2290074, 1, // Sniping 20
        2290064, 1, // Concentration 20
        2290094, 1, // Smoke 20
        2290022, 1, // Berserk 20
        2290056, 1, // Bow Expert 30
        2290066, 1, // xBow Expert 30
        2290020, 1, // Sanc 20
        1102082, 1, // Black Raggdey Cape
        1302049, 1, // Glowing Whip
        2340000, 1, // White Scroll
        1102041, 1, // Pink Cape
        1452019, 2, // White Nisrock
        4001116, 3, // Hexagon Pend
        4001012, 3, // Wraith Eraser
        1022060, 2, // Foxy Racoon Eye
        2430144, 5,
        2290285, 5,
        2028062, 5,
        2028061, 5,
        2530000, 1,
        2531000, 1,
        2041100, 1,
        2041101, 1,
        2041102, 1,
        2041103, 1,
        2041104, 1,
        2041105, 1,
        2041106, 1,
        2041107, 1,
        2041108, 1,
        2041109, 1,
        2041110, 1,
        2041111, 1,
        2041112, 1,
        2041113, 1,
        2041114, 1,
        2041115, 1,
        2041116, 1,
        2041117, 1,
        2041118, 1,
        2041119, 1,
        2041300, 1,
        2041301, 1,
        2041302, 1,
        2041303, 1,
        2041304, 1,
        2041305, 1,
        2041306, 1,
        2041307, 1,
        2041308, 1,
        2041309, 1,
        2041310, 1,
        2041311, 1,
        2041312, 1,
        2041313, 1,
        2041314, 1,
        2041315, 1,
        2041316, 1,
        2041317, 1,
        2041318, 1,
        2041319, 1,
        2049200, 1,
        2049201, 1,
        2049202, 1,
        2049203, 1,
        2049204, 1,
        2049205, 1,
        2049206, 1,
        2049207, 1,
        2049208, 1,
        2049209, 1,
        2049210, 1,
        2049211, 1,
        1432011, 3, // Fair Frozen
        1442020, 3, // HellSlayer
        1382035, 3, // Blue Marine
        1372010, 3, // Dimon Wand
        1332027, 3, // Varkit
        1302056, 3, // Sparta
        1402005, 3, // Bezerker
        1472053, 3, // Red Craven
        1462018, 3, // Casa Crow
        1452017, 3, // Metus
        1422013, 3, // Lemonite
        1322029, 3, // Ruin Hammer
        1412010, 3, // Colonian Axe

        1002587, 3, // Black Wisconsin
        1402044, 1, // Pumpkin lantern
        2101013, 4, // Summoning Showa boss
        1442046, 1, // Super Snowboard
        1422031, 1, // Blue Seal Cushion
        1332054, 3, // Lonzege Dagger
        1012056, 3, // Dog Nose
        1022047, 3, // Owl Mask
        3012002, 1, // Bathtub
        1442012, 3, // Sky snowboard
        1442018, 3, // Frozen Tuna
        1432010, 3, // Omega Spear
        1432036, 1, // Fishing Pole
        2000005, 10, // Power Elixir
        2049100, 10,
        2000004, 10, // Elixir
        4280001, 8}; // Silver Box
    public final static int[] peanuts = {2430091, 200, 2430092, 200, 2430093, 200, 2430101, 200, 2430102, 200, 2430136, 200, 2430149, 200,//mounts 
        2340000, 1, //rares
        1152000, 5, 1152001, 5, 1152004, 5, 1152005, 5, 1152006, 5, 1152007, 5, 1152008, 5, //toenail only comes when db is out.
        1152064, 5, 1152065, 5, 1152066, 5, 1152067, 5, 1152070, 5, 1152071, 5, 1152072, 5, 1152073, 5,
        3010019, 2, //chairs
        1001060, 10, 1002391, 10, 1102004, 10, 1050039, 10, 1102040, 10, 1102041, 10, 1102042, 10, 1102043, 10, //equips
        1082145, 5, 1082146, 5, 1082147, 5, 1082148, 5, 1082149, 5, 1082150, 5, //wg
        2043704, 10, 2040904, 10, 2040409, 10, 2040307, 10, 2041030, 10, 2040015, 10, 2040109, 10, 2041035, 10, 2041036, 10, 2040009, 10, 2040511, 10, 2040408, 10, 2043804, 10, 2044105, 10, 2044903, 10, 2044804, 10, 2043009, 10, 2043305, 10, 2040610, 10, 2040716, 10, 2041037, 10, 2043005, 10, 2041032, 10, 2040305, 10, //scrolls
        2040211, 5, 2040212, 5, 1022097, 10, //dragon glasses
        2049000, 10, 2049001, 10, 2049002, 10, 2049003, 10, //clean slate
        1012058, 5, 1012059, 5, 1012060, 5, 1012061, 5,//pinocchio nose msea only.
        1332100, 10, 1382058, 10, 1402073, 10, 1432066, 10, 1442090, 10, 1452058, 10, 1462076, 10, 1472069, 10, 1482051, 10, 1492024, 10, 1342009, 10, //durability weapons level 105
        2049400, 1, 2049401, 2, 2049301, 2,
        2049100, 10,
        2430144, 10,
        2290285, 10,
        2028062, 10,
        2028061, 10,
        2530000, 5,
        2531000, 5,
        1032080, 5,
        1032081, 4,
        1032082, 3,
        1032083, 2,
        1032084, 1,
        1112435, 5,
        1112436, 4,
        1112437, 3,
        1112438, 2,
        1112439, 1,
        1122081, 5,
        1122082, 4,
        1122083, 3,
        1122084, 2,
        1122085, 1,
        1132036, 5,
        1132037, 4,
        1132038, 3,
        1132039, 2,
        1132040, 1,
        //source
        1092070, 5,
        1092071, 4,
        1092072, 3,
        1092073, 2,
        1092074, 1,
        1092075, 5,
        1092076, 4,
        1092077, 3,
        1092078, 2,
        1092079, 1,
        1092080, 5,
        1092081, 4,
        1092082, 3,
        1092083, 2,
        1092084, 1,
        1092087, 1,
        1092088, 1,
        1092089, 1,
        1302143, 5,
        1302144, 4,
        1302145, 3,
        1302146, 2,
        1302147, 1,
        1312058, 5,
        1312059, 4,
        1312060, 3,
        1312061, 2,
        1312062, 1,
        1322086, 5,
        1322087, 4,
        1322088, 3,
        1322089, 2,
        1322090, 1,
        1332116, 5,
        1332117, 4,
        1332118, 3,
        1332119, 2,
        1332120, 1,
        1332121, 5,
        1332122, 4,
        1332123, 3,
        1332124, 2,
        1332125, 1,
        1342029, 5,
        1342030, 4,
        1342031, 3,
        1342032, 2,
        1342033, 1,
        1372074, 5,
        1372075, 4,
        1372076, 3,
        1372077, 2,
        1372078, 1,
        1382095, 5,
        1382096, 4,
        1382097, 3,
        1382098, 2,
        1392099, 1,
        1402086, 5,
        1402087, 4,
        1402088, 3,
        1402089, 2,
        1402090, 1,
        1412058, 5,
        1412059, 4,
        1412060, 3,
        1412061, 2,
        1412062, 1,
        1422059, 5,
        1422060, 4,
        1422061, 3,
        1422062, 2,
        1422063, 1,
        1432077, 5,
        1432078, 4,
        1432079, 3,
        1432080, 2,
        1432081, 1,
        1442107, 5,
        1442108, 4,
        1442109, 3,
        1442110, 2,
        1442111, 1,
        1452102, 5,
        1452103, 4,
        1452104, 3,
        1452105, 2,
        1452106, 1,
        1462087, 5,
        1462088, 4,
        1462089, 3,
        1462090, 2,
        1462091, 1,
        1472113, 5,
        1472114, 4,
        1472115, 3,
        1472116, 2,
        1472117, 1,
        1482075, 5,
        1482076, 4,
        1482077, 3,
        1482078, 2,
        1482079, 1,
        1492075, 5,
        1492076, 4,
        1492077, 3,
        1492078, 2,
        1492079, 1,
        1132012, 2,
        1132013, 1,
        1942002, 2,
        1952002, 2,
        1962002, 2,
        1972002, 2,
        1612004, 2,
        1622004, 2,
        1632004, 2,
        1642004, 2,
        1652004, 2,
        2047000, 1,
        2047001, 1,
        2047002, 1,
        2047100, 1,
        2047101, 1,
        2047102, 1,
        2047200, 1,
        2047201, 1,
        2047202, 1,
        2047203, 1,
        2047204, 1,
        2047205, 1,
        2047206, 1,
        2047207, 1,
        2047208, 1,
        2047300, 1,
        2047301, 1,
        2047302, 1,
        2047303, 1,
        2047304, 1,
        2047305, 1,
        2047306, 1,
        2047307, 1,
        2047308, 1,
        2047309, 1,
        2046004, 1,
        2046005, 1,
        2046104, 1,
        2046105, 1,
        2046208, 1,
        2046209, 1,
        2046210, 1,
        2046211, 1,
        2046212, 1,
        2049200, 1,
        2049201, 1,
        2049202, 1,
        2049203, 1,
        2049204, 1,
        2049205, 1,
        2049206, 1,
        2049207, 1,
        2049208, 1,
        2049209, 1,
        2049210, 1,
        2049211, 1,
        //ele wand
        1372035, 1,
        1372036, 1,
        1372037, 1,
        1372038, 1,
        //ele staff
        1382045, 1,
        1382046, 1,
        1382047, 1,
        1382048, 1,
        1382049, 1,
        1382050, 1, // Blue Dragon Staff
        1382051, 1,
        1382052, 1,
        1372039, 1,
        1372040, 1,
        1372041, 1,
        1372042, 1,
        2070016, 1,
        2070007, 2,
        2330007, 1,
        2070018, 1,
        2330008, 1,
        2070023, 1,
        2070024, 1,
        2028062, 5,
        2028061, 5};
    public static int[] eventCommonReward = {
        0, 10,
        1, 10,
        4, 5,
        5060004, 25,
        4170024, 25,
        4280000, 5,
        4280001, 6,
        5490000, 5,
        5490001, 6
    };
    public static int[] eventUncommonReward = {
        1, 4,
        2, 8,
        3, 8,
        2022179, 5,
        5062000, 20,
        2430082, 20,
        2430092, 20,
        2022459, 2,
        2022460, 1,
        2022462, 1,
        2430103, 2,
        2430117, 2,
        2430118, 2,
        2430201, 4,
        2430228, 4,
        2430229, 4,
        2430283, 4,
        2430136, 4,
        2430476, 4,
        2430511, 4,
        2430206, 4,
        2430199, 1,
        1032062, 5,
        5220000, 28,
        2022459, 5,
        2022460, 5,
        2022461, 5,
        2022462, 5,
        2022463, 5,
        5050000, 2,
        4080100, 10,
        4080000, 10,
        2049100, 10,
        2430144, 10,
        2290285, 10,
        2028062, 10,
        2028061, 10,
        2530000, 5,
        2531000, 5,
        2041100, 1,
        2041101, 1,
        2041102, 1,
        2041103, 1,
        2041104, 1,
        2041105, 1,
        2041106, 1,
        2041107, 1,
        2041108, 1,
        2041109, 1,
        2041110, 1,
        2041111, 1,
        2041112, 1,
        2041113, 1,
        2041114, 1,
        2041115, 1,
        2041116, 1,
        2041117, 1,
        2041118, 1,
        2041119, 1,
        2041300, 1,
        2041301, 1,
        2041302, 1,
        2041303, 1,
        2041304, 1,
        2041305, 1,
        2041306, 1,
        2041307, 1,
        2041308, 1,
        2041309, 1,
        2041310, 1,
        2041311, 1,
        2041312, 1,
        2041313, 1,
        2041314, 1,
        2041315, 1,
        2041316, 1,
        2041317, 1,
        2041318, 1,
        2041319, 1,
        2049200, 1,
        2049201, 1,
        2049202, 1,
        2049203, 1,
        2049204, 1,
        2049205, 1,
        2049206, 1,
        2049207, 1,
        2049208, 1,
        2049209, 1,
        2049210, 1,
        2049211, 1
    };
    public static int[] eventRareReward = {
        2049100, 5,
        2430144, 5,
        2290285, 5,
        2028062, 5,
        2028061, 5,
        2530000, 2,
        2531000, 2,
        2049116, 1,
        2049401, 10,
        2049301, 20,
        2049400, 3,
        2340000, 1,
        3010130, 5,
        3010131, 5,
        3010132, 5,
        3010133, 5,
        3010136, 5,
        3010116, 5,
        3010117, 5,
        3010118, 5,
        1112405, 1,
        1112445, 1,
        1022097, 1,
        2040211, 1,
        2040212, 1,
        2049000, 2,
        2049001, 2,
        2049002, 2,
        2049003, 2,
        1012058, 2,
        1012059, 2,
        1012060, 2,
        1012061, 2,
        2022460, 4,
        2022461, 3,
        2022462, 4,
        2022463, 3,
        2040041, 1,
        2040042, 1,
        2040334, 1,
        2040430, 1,
        2040538, 1,
        2040539, 1,
        2040630, 1,
        2040740, 1,
        2040741, 1,
        2040742, 1,
        2040829, 1,
        2040830, 1,
        2040936, 1,
        2041066, 1,
        2041067, 1,
        2043023, 1,
        2043117, 1,
        2043217, 1,
        2043312, 1,
        2043712, 1,
        2043812, 1,
        2044025, 1,
        2044117, 1,
        2044217, 1,
        2044317, 1,
        2044417, 1,
        2044512, 1,
        2044612, 1,
        2044712, 1,
        2046000, 1,
        2046001, 1,
        2046004, 1,
        2046005, 1,
        2046100, 1,
        2046101, 1,
        2046104, 1,
        2046105, 1,
        2046200, 1,
        2046201, 1,
        2046202, 1,
        2046203, 1,
        2046208, 1,
        2046209, 1,
        2046210, 1,
        2046211, 1,
        2046212, 1,
        2046300, 1,
        2046301, 1,
        2046302, 1,
        2046303, 1,
        2047000, 1,
        2047001, 1,
        2047002, 1,
        2047100, 1,
        2047101, 1,
        2047102, 1,
        2047200, 1,
        2047201, 1,
        2047202, 1,
        2047203, 1,
        2047204, 1,
        2047205, 1,
        2047206, 1,
        2047207, 1,
        2047208, 1,
        2047300, 1,
        2047301, 1,
        2047302, 1,
        2047303, 1,
        2047304, 1,
        2047305, 1,
        2047306, 1,
        2047307, 1,
        2047308, 1,
        2047309, 1,
        1112427, 5,
        1112428, 5,
        1112429, 5,
        1012240, 10,
        1022117, 10,
        1032095, 10,
        1112659, 10,
        2070007, 10,
        2330007, 5,
        2070016, 5,
        2070018, 5,
        1152038, 1,
        1152039, 1,
        1152040, 1,
        1152041, 1,
        1122090, 1,
        1122094, 1,
        1122098, 1,
        1122102, 1,
        1012213, 1,
        1012219, 1,
        1012225, 1,
        1012231, 1,
        1012237, 1,
        2070023, 5,
        2070024, 5,
        2330008, 5,
        2003516, 5,
        2003517, 1,
        1132052, 1,
        1132062, 1,
        1132072, 1,
        1132082, 1,
        1112585, 1,
        //walker
        1072502, 1,
        1072503, 1,
        1072504, 1,
        1072505, 1,
        1072506, 1,
        1052333, 1,
        1052334, 1,
        1052335, 1,
        1052336, 1,
        1052337, 1,
        1082305, 1,
        1082306, 1,
        1082307, 1,
        1082308, 1,
        1082309, 1,
        1003197, 1,
        1003198, 1,
        1003199, 1,
        1003200, 1,
        1003201, 1,
        1662000, 1,
        1662001, 1,
        1672000, 1,
        1672001, 1,
        1672002, 1,
        //crescent moon
        1112583, 1,
        1032092, 1,
        1132084, 1,
        //mounts, 90 day
        2430290, 1,
        2430292, 1,
        2430294, 1,
        2430296, 1,
        2430298, 1,
        2430300, 1,
        2430302, 1,
        2430304, 1,
        2430306, 1,
        2430308, 1,
        2430310, 1,
        2430312, 1,
        2430314, 1,
        2430316, 1,
        2430318, 1,
        2430320, 1,
        2430322, 1,
        2430324, 1,
        2430326, 1,
        2430328, 1,
        2430330, 1,
        2430332, 1,
        2430334, 1,
        2430336, 1,
        2430338, 1,
        2430340, 1,
        2430342, 1,
        2430344, 1,
        2430347, 1,
        2430349, 1,
        2430351, 1,
        2430353, 1,
        2430355, 1,
        2430357, 1,
        2430359, 1,
        2430361, 1,
        2430392, 1,
        2430512, 1,
        2430536, 1,
        2430477, 1,
        2430146, 1,
        2430148, 1,
        2430137, 1,};
    public static int[] eventSuperReward = {
        2022121, 10,
        4031307, 50,
        3010127, 10,
        3010128, 10,
        3010137, 10,
        3010157, 10,
        2049300, 10,
        2040758, 10,
        1442057, 10,
        2049402, 10,
        2049304, 1,
        2049305, 1,
        2040759, 7,
        2040760, 5,
        2040125, 10,
        2040126, 10,
        1012191, 5,
        1112514, 1, //untradable/tradable
        1112531, 1,
        1112629, 1,
        1112646, 1,
        1112515, 1, //untradable/tradable
        1112532, 1,
        1112630, 1,
        1112647, 1,
        1112516, 1, //untradable/tradable
        1112533, 1,
        1112631, 1,
        1112648, 1,
        2040045, 10,
        2040046, 10,
        2040333, 10,
        2040429, 10,
        2040542, 10,
        2040543, 10,
        2040629, 10,
        2040755, 10,
        2040756, 10,
        2040757, 10,
        2040833, 10,
        2040834, 10,
        2041068, 10,
        2041069, 10,
        2043022, 12,
        2043120, 12,
        2043220, 12,
        2043313, 12,
        2043713, 12,
        2043813, 12,
        2044028, 12,
        2044120, 12,
        2044220, 12,
        2044320, 12,
        2044520, 12,
        2044513, 12,
        2044613, 12,
        2044713, 12,
        2044817, 12,
        2044910, 12,
        2046002, 5,
        2046003, 5,
        2046102, 5,
        2046103, 5,
        2046204, 10,
        2046205, 10,
        2046206, 10,
        2046207, 10,
        2046304, 10,
        2046305, 10,
        2046306, 10,
        2046307, 10,
        2040006, 2,
        2040007, 2,
        2040303, 2,
        2040403, 2,
        2040506, 2,
        2040507, 2,
        2040603, 2,
        2040709, 2,
        2040710, 2,
        2040711, 2,
        2040806, 2,
        2040903, 2,
        2040913, 2,
        2041024, 2,
        2041025, 2,
        2044815, 2,
        2044908, 2,
        1152046, 1,
        1152047, 1,
        1152048, 1,
        1152049, 1,
        1122091, 1,
        1122095, 1,
        1122099, 1,
        1122103, 1,
        1012214, 1,
        1012220, 1,
        1012226, 1,
        1012232, 1,
        1012238, 1,
        1032088, 1,
        1032089, 1,
        1032090, 1,
        1032091, 1,
        1132053, 1,
        1132063, 1,
        1132073, 1,
        1132083, 1,
        1112586, 1,
        1112593, 1,
        1112597, 1,
        1662002, 1,
        1662003, 1,
        1672003, 1,
        1672004, 1,
        1672005, 1,
        //130, 140 weapons
        1092088, 1,
        1092089, 1,
        1092087, 1,
        1102275, 1,
        1102276, 1,
        1102277, 1,
        1102278, 1,
        1102279, 1,
        1102280, 1,
        1102281, 1,
        1102282, 1,
        1102283, 1,
        1102284, 1,
        1082295, 1,
        1082296, 1,
        1082297, 1,
        1082298, 1,
        1082299, 1,
        1082300, 1,
        1082301, 1,
        1082302, 1,
        1082303, 1,
        1082304, 1,
        1072485, 1,
        1072486, 1,
        1072487, 1,
        1072488, 1,
        1072489, 1,
        1072490, 1,
        1072491, 1,
        1072492, 1,
        1072493, 1,
        1072494, 1,
        1052314, 1,
        1052315, 1,
        1052316, 1,
        1052317, 1,
        1052318, 1,
        1052319, 1,
        1052329, 1,
        1052321, 1,
        1052322, 1,
        1052323, 1,
        1003172, 1,
        1003173, 1,
        1003174, 1,
        1003175, 1,
        1003176, 1,
        1003177, 1,
        1003178, 1,
        1003179, 1,
        1003180, 1,
        1003181, 1,
        1302152, 1,
        1302153, 1,
        1312065, 1,
        1312066, 1,
        1322096, 1,
        1322097, 1,
        1332130, 1,
        1332131, 1,
        1342035, 1,
        1342036, 1,
        1372084, 1,
        1372085, 1,
        1382104, 1,
        1382105, 1,
        1402095, 1,
        1402096, 1,
        1412065, 1,
        1412066, 1,
        1422066, 1,
        1422067, 1,
        1432086, 1,
        1432087, 1,
        1442116, 1,
        1442117, 1,
        1452111, 1,
        1452112, 1,
        1462099, 1,
        1462100, 1,
        1472122, 1,
        1472123, 1,
        1482084, 1,
        1482085, 1,
        1492085, 1,
        1492086, 1,
        1532017, 1,
        1532018, 1,
        //mounts
        2430291, 1,
        2430293, 1,
        2430295, 1,
        2430297, 1,
        2430299, 1,
        2430301, 1,
        2430303, 1,
        2430305, 1,
        2430307, 1,
        2430309, 1,
        2430311, 1,
        2430313, 1,
        2430315, 1,
        2430317, 1,
        2430319, 1,
        2430321, 1,
        2430323, 1,
        2430325, 1,
        2430327, 1,
        2430329, 1,
        2430331, 1,
        2430333, 1,
        2430335, 1,
        2430337, 1,
        2430339, 1,
        2430341, 1,
        2430343, 1,
        2430345, 1,
        2430348, 1,
        2430350, 1,
        2430352, 1,
        2430354, 1,
        2430356, 1,
        2430358, 1,
        2430360, 1,
        2430362, 1,
        //rising sun
        1012239, 1,
        1122104, 1,
        1112584, 1,
        1032093, 1,
        1132085, 1
    };
    public static int[] tenPercent = {
        //10% scrolls
        2040002,
        2040005,
        2040026,
        2040031,
        2040100,
        2040105,
        2040200,
        2040205,
        2040302,
        2040310,
        2040318,
        2040323,
        2040328,
        2040329,
        2040330,
        2040331,
        2040402,
        2040412,
        2040419,
        2040422,
        2040427,
        2040502,
        2040505,
        2040514,
        2040517,
        2040534,
        2040602,
        2040612,
        2040619,
        2040622,
        2040627,
        2040702,
        2040705,
        2040708,
        2040727,
        2040802,
        2040805,
        2040816,
        2040825,
        2040902,
        2040915,
        2040920,
        2040925,
        2040928,
        2040933,
        2041002,
        2041005,
        2041008,
        2041011,
        2041014,
        2041017,
        2041020,
        2041023,
        2041058,
        2041102,
        2041105,
        2041108,
        2041111,
        2041302,
        2041305,
        2041308,
        2041311,
        2043002,
        2043008,
        2043019,
        2043102,
        2043114,
        2043202,
        2043214,
        2043302,
        2043402,
        2043702,
        2043802,
        2044002,
        2044014,
        2044015,
        2044102,
        2044114,
        2044202,
        2044214,
        2044302,
        2044314,
        2044402,
        2044414,
        2044502,
        2044602,
        2044702,
        2044802,
        2044809,
        2044902,
        2045302,
        2048002,
        2048005
    };
    public static int[] fishingReward = {
        0, 100, // Meso
        1, 100, // EXP
        2022179, 1, // Onyx Apple
        1302021, 5, // Pico Pico Hammer
        1072238, 1, // Voilet Snowshoe
        1072239, 1, // Yellow Snowshoe
        2049100, 2, // Chaos Scroll
        2430144, 1,
        2290285, 1,
        2028062, 1,
        2028061, 1,
        2049301, 1, // Equip Enhancer Scroll
        2049401, 1, // Potential Scroll
        1302000, 3, // Sword
        1442011, 1, // Surfboard
        4000517, 8, // Golden Fish
        4000518, 10, // Golden Fish Egg
        4031627, 2, // White Bait (3cm)
        4031628, 1, // Sailfish (120cm)
        4031630, 1, // Carp (30cm)
        4031631, 1, // Salmon(150cm)
        4031632, 1, // Shovel
        4031633, 2, // Whitebait (3.6cm)
        4031634, 1, // Whitebait (5cm)
        4031635, 1, // Whitebait (6.5cm)
        4031636, 1, // Whitebait (10cm)
        4031637, 2, // Carp (53cm)
        4031638, 2, // Carp (60cm)
        4031639, 1, // Carp (100cm)
        4031640, 1, // Carp (113cm)
        4031641, 2, // Sailfish (128cm)
        4031642, 2, // Sailfish (131cm)
        4031643, 1, // Sailfish (140cm)
        4031644, 1, // Sailfish (148cm)
        4031645, 2, // Salmon (166cm)
        4031646, 2, // Salmon (183cm)
        4031647, 1, // Salmon (227cm)
        4031648, 1, // Salmon (288cm)
        4001187, 20,
        4001188, 20,
        4001189, 20,
        4031629, 1 // Pot
    };

    public static boolean isReverseItem(int itemId) {
        switch (itemId) {
            case 1002790:
            case 1002791:
            case 1002792:
            case 1002793:
            case 1002794:
            case 1082239:
            case 1082240:
            case 1082241:
            case 1082242:
            case 1082243:
            case 1052160:
            case 1052161:
            case 1052162:
            case 1052163:
            case 1052164:
            case 1072361:
            case 1072362:
            case 1072363:
            case 1072364:
            case 1072365:

            case 1302086:
            case 1312038:
            case 1322061:
            case 1332075:
            case 1332076:
            case 1372045:
            case 1382059:
            case 1402047:
            case 1412034:
            case 1422038:
            case 1432049:
            case 1442067:
            case 1452059:
            case 1462051:
            case 1472071:
            case 1482024:
            case 1492025:

            case 1342012:
            case 1942002:
            case 1952002:
            case 1962002:
            case 1972002:
            case 1532016:
            case 1522017:
                return true;
            default:
                return false;
        }
    }

    public static boolean isTimelessItem(int itemId) {
        switch (itemId) {
            case 1032031: //shield earring, but technically
            case 1102172:
            case 1002776:
            case 1002777:
            case 1002778:
            case 1002779:
            case 1002780:
            case 1082234:
            case 1082235:
            case 1082236:
            case 1082237:
            case 1082238:
            case 1052155:
            case 1052156:
            case 1052157:
            case 1052158:
            case 1052159:
            case 1072355:
            case 1072356:
            case 1072357:
            case 1072358:
            case 1072359:
            case 1092057:
            case 1092058:
            case 1092059:

            case 1122011:
            case 1122012:

            case 1302081:
            case 1312037:
            case 1322060:
            case 1332073:
            case 1332074:
            case 1372044:
            case 1382057:
            case 1402046:
            case 1412033:
            case 1422037:
            case 1432047:
            case 1442063:
            case 1452057:
            case 1462050:
            case 1472068:
            case 1482023:
            case 1492023:
            case 1342011:
            case 1532015:
            case 1522016:
                //raven.
                return true;
            default:
                return false;
        }
    }

    public static boolean icsog(int itemId) {
        return itemId == 2049122;
    }

    //if only there was a way to find in wz files -.-
    public static boolean isEffectRing(int itemid) {
        return isFriendshipRing(itemid) || isCrushRing(itemid) || isMarriageRing(itemid);
    }

    public static boolean isMarriageRing(int itemId) {
        switch (itemId) {
            case 1112803:
            case 1112806:
            case 1112807:
            case 1112809:
                return true;
        }
        return false;
    }

    public static boolean isFriendshipRing(int itemId) {
        switch (itemId) {
            case 1112800:
            case 1112801:
            case 1112802:
            case 1112810: //new
            case 1112811: //new, doesnt work in friendship?
            case 1112812: //new, im ASSUMING it's friendship cuz of itemID, not sure.
            case 1112816: //new, i'm also assuming
            case 1112817:

            case 1049000:
                return true;
        }
        return false;
    }

    public static boolean isCrushRing(int itemId) {
        switch (itemId) {
            case 1112001:
            case 1112002:
            case 1112003:
            case 1112005: //new
            case 1112006: //new
            case 1112007:
            case 1112012:
            case 1112015: //new

            case 1048000:
            case 1048001:
            case 1048002:
                return true;
        }
        return false;
    }

    public static int[] Equipments_Bonus = {1122017};

    public static int[] blockedMaps = {109050000, 280030000, 240060200, 280090000, 280030001, 240060201, 950101100, 950101010};
    //If you can think of any maps that could be exploitable via npc, block them here.

    public static int getExpForLevel(int i, int itemId) {
        if (isReverseItem(itemId)) {
            return getReverseRequiredEXP(i);
        } else if (getMaxLevel(itemId) > 0) {
            return getTimelessRequiredEXP(i);
        }
        return 0;
    }

    public static int getMaxLevel(final int itemId) {
        Map<Integer, Map<String, Integer>> inc = MapleItemInformationProvider.getInstance().getEquipIncrements(itemId);
        return inc != null ? (inc.size()) : 0;
    }

    public static int getStatChance() {
        return 25;
    }

    public static MonsterStatus getStatFromWeapon(final int itemid) {
        switch (itemid) {
            case 1302109:
            case 1312041:
            case 1322067:
            case 1332083:
            case 1372048:
            case 1382064:
            case 1402055:
            case 1412037:
            case 1422041:
            case 1432052:
            case 1442073:
            case 1452064:
            case 1462058:
            case 1472079:
            case 1482035:
                return MonsterStatus.DARKNESS;
            case 1302108:
            case 1312040:
            case 1322066:
            case 1332082:
            case 1372047:
            case 1382063:
            case 1402054:
            case 1412036:
            case 1422040:
            case 1432051:
            case 1442072:
            case 1452063:
            case 1462057:
            case 1472078:
            case 1482036:
                return MonsterStatus.SPEED;
        }
        return null;
    }

    public static int getXForStat(MonsterStatus stat) {
        switch (stat) {
            case DARKNESS:
                return -70;
            case SPEED:
                return -50;
        }
        return 0;
    }

    public static boolean isInBag(final int slot, final byte type) {
        return ((slot >= 101 && slot <= 512) && type == MapleInventoryType.ETC.getType());
    }

    public static int getSkillForStat(MonsterStatus stat) {
        switch (stat) {
            case DARKNESS:
                return 1111003;
            case SPEED:
                return 3121007;
        }
        return 0;
    }
    public final static int[] normalDrops = {
        4001009, //real
        4001010,
        4001011,
        4001012,
        4001013,
        4001014, //real
        4001021,
        4001038, //fake
        4001039,
        4001040,
        4001041,
        4001042,
        4001043, //fake
        4001038, //fake
        4001039,
        4001040,
        4001041,
        4001042,
        4001043, //fake
        4001038, //fake
        4001039,
        4001040,
        4001041,
        4001042,
        4001043, //fake
        4000164, //start
        2000000,
        2000003,
        2000004,
        2000005,
        4000019,
        4000000,
        4000016,
        4000006,
        2100121,
        4000029,
        4000064,
        5110000,
        4000306,
        4032181,
        4006001,
        4006000,
        2050004,
        3994102,
        3994103,
        3994104,
        3994105,
        2430007, //end
        4000164, //start
        2000000,
        2000003,
        2000004,
        2000005,
        4000019,
        4000000,
        4000016,
        4000006,
        2100121,
        4000029,
        4000064,
        5110000,
        4000306,
        4032181,
        4006001,
        4006000,
        2050004,
        3994102,
        3994103,
        3994104,
        3994105,
        2430007, //end
        4000164, //start
        2000000,
        2000003,
        2000004,
        2000005,
        4000019,
        4000000,
        4000016,
        4000006,
        2100121,
        4000029,
        4000064,
        5110000,
        4000306,
        4032181,
        4006001,
        4006000,
        2050004,
        3994102,
        3994103,
        3994104,
        3994105,
        2430007}; //end
    public final static int[] rareDrops = {
        2022179,
        2049100,
        2049100,
        2430144,
        2028062,
        2028061,
        2290285,
        2049301,
        2049401,
        2022326,
        2022193,
        2049000,
        2049001,
        2049002};
    public final static int[] superDrops = {
        2040804,
        2049400,
        2028062,
        2028061,
        2430144,
        2430144,
        2430144,
        2430144,
        2290285,
        2049100,
        2049100,
        2049100,
        2049100};

    //SP Tables
    public static int getSkillBook(final int job, final int skill) {
        if (isBeastTamer(job)) {
            return 0;
        }
        if (isEvan(job)) { // Evan
            switch (job) {
                case 2210:
                    return 0;
                case 2212:
                    return 1;
                case 2214:
                    return 2;
                case 2216:
                case 2217:
                case 2218:
                    return 3;
                case 2219:
                    return 0;
            }
        }
        if (isDualBlade(job)) {
            switch (job) {
                case 430:
                    return 1;
                case 431:
                    return 2;
                case 432:
                    return 3;
                case 433:
                    return 4;
                case 434:
                    return 5;
                case 435:
                    return 6;
                case 436:
                    return 7;
            }
        }
        if (isZero(job)) {
            if (skill > 0) {
                int type = (skill % 1000) / 100; //1 beta 2 alpha
                return type == 1 ? 1 : 0;
            } else {
                return 0;
            }
        } //
        switch (job) {
            //Special Case First Job Table
            case 501:
            case 3101:
            case 11200:
            case 14200:
                return 0;
            //Second Job Table
            case 110:
            case 120:
            case 130:
            case 210:
            case 220:
            case 230:
            case 310:
            case 320:
            case 330:
            case 410:
            case 420:
            case 510:
            case 520:
            case 530:
            case 570:
            case 1110:
            case 1210:
            case 1310:
            case 1410:
            case 1510:
            case 2110:
            case 2212:
            case 2310:
            case 2410:
            case 2510:
            case 2710:
            case 3110:
            case 3120:
            case 3210:
            case 3310:
            case 3510:
            case 3610:
            case 3710:
            case 4110:
            case 4210:
            case 5110:
            case 6110:
            case 6510:
            case 11210:
            case 14210:
                return 1;
            //Third Job Table
            case 111:
            case 121:
            case 131:
            case 211:
            case 221:
            case 231:
            case 311:
            case 321:
            case 411:
            case 421:
            case 511:
            case 521:
            case 531:
            case 571:
            case 1111:
            case 1211:
            case 1311:
            case 1411:
            case 1511:
            case 2111:
            case 2214:
            case 2311:
            case 2411:
            case 2511:
            case 2711:
            case 3111:
            case 3121:
            case 3211:
            case 3311:
            case 3511:
            case 3611:
            case 3711:
            case 4111:
            case 4211:
            case 5111:
            case 6111:
            case 6511:
            case 11211:
            case 14211:
                return 2;
            //Fourth Job Table
            case 112:
            case 122:
            case 132:
            case 212:
            case 222:
            case 232:
            case 312:
            case 322:
            case 412:
            case 422:
            case 512:
            case 522:
            case 532:
            case 572:
            case 1112:
            case 1212:
            case 1312:
            case 1412:
            case 1512:
            case 2112:
            case 2218:
            case 2312:
            case 2412:
            case 2512:
            case 2712:
            case 3112:
            case 3122:
            case 3212:
            case 3312:
            case 3512:
            case 3612:
            case 3712:
            case 4112:
            case 4212:
            case 5112:
            case 6112:
            case 6512:
            case 11212:
            case 14212:
                return 3;
            //? Job Table
            case 113:
            case 123:
            case 133:
            case 213:
            case 223:
            case 233:
            case 313:
            case 323:
            case 413:
            case 423:
            case 513:
            case 523:
            case 533:
            case 573:
            case 1113:
            case 1313:
            case 1513:
            case 2219:
            case 2313:
            case 2413:
            case 2513:
            case 2713:
            case 3113:
            case 3123:
            case 3213:
            case 3313:
            case 3513:
            case 3613:
            case 3713:
            case 4113:
            case 4213:
            case 5113:
            case 6113:
            case 6513:
            case 11213:
            case 14213:
                return 4;
        }
        if (isExtendedSpJob(job)) {
            if (job % 10 > 4) {
                return 0;
            }
            return (job % 10);
        }
        return 0;
    }

    public static boolean isExtendedSpJob(int job) {
        if (isBeastTamer(job) || isPinkBean(job) || isGameMaster(job)) {
            return false;
        }
        return true;
    }

    public static int getSkillBookForSkill(final int skillid) {
        return getSkillBook(skillid / 10000, skillid);
    }

    public static boolean isKatara(int itemId) {
        return itemId / 10000 == 134;
    }

    public static boolean isDagger(int itemId) {
        return itemId / 10000 == 133;
    }

    public static boolean isApplicableSkill(int skil) {
        return ((skil < 80000000 || skil >= 100000000) && (skil % 10000 < 8000 || skil % 10000 > 8006) && !isAngel(skil)) || skil >= 92000000 || (skil >= 80000000 && skil < 80010000); //no additional/decent skills
    }

    public static boolean isApplicableSkill_(int skil) { //not applicable to saving but is more of temporary
        for (int i : PlayerStats.pvpSkills) {
            if (skil == i) {
                return true;
            }
        }
        return (skil >= 90000000 && skil < 92000000) || (skil % 10000 >= 8000 && skil % 10000 <= 8006) || isAngel(skil);
    }

    public static boolean isTablet(int itemId) {
        return itemId / 1000 == 2047;
    }

    public static boolean isGeneralScroll(int itemId) {
        return itemId / 1000 == 2046;
    }

    public static int getSuccessTablet(final int scrollId, final int level) {
        switch (scrollId % 1000 / 100) {
            case 2:
                //2047_2_00 = armor, 2047_3_00 = accessory
                switch (level) {
                    case 0:
                        return 70;
                    case 1:
                        return 55;
                    case 2:
                        return 43;
                    case 3:
                        return 33;
                    case 4:
                        return 26;
                    case 5:
                        return 20;
                    case 6:
                        return 16;
                    case 7:
                        return 12;
                    case 8:
                        return 10;
                    default:
                        return 7;
                }
            case 3:
                switch (level) {
                    case 0:
                        return 70;
                    case 1:
                        return 35;
                    case 2:
                        return 18;
                    case 3:
                        return 12;
                    default:
                        return 7;
                }
            default:
                switch (level) {
                    case 0:
                        return 70;
                    case 1:
                        return 50; //-20
                    case 2:
                        return 36; //-14
                    case 3:
                        return 26; //-10
                    case 4:
                        return 19; //-7
                    case 5:
                        return 14; //-5
                    case 6:
                        return 10; //-4
                    default:
                        return 7;  //-3
                }
        }
    }

    public static int getCurseTablet(final int scrollId, final int level) {
        switch (scrollId % 1000 / 100) {
            case 2:
                //2047_2_00 = armor, 2047_3_00 = accessory
                switch (level) {
                    case 0:
                        return 10;
                    case 1:
                        return 12;
                    case 2:
                        return 16;
                    case 3:
                        return 20;
                    case 4:
                        return 26;
                    case 5:
                        return 33;
                    case 6:
                        return 43;
                    case 7:
                        return 55;
                    case 8:
                        return 70;
                    default:
                        return 100;
                }
            case 3:
                switch (level) {
                    case 0:
                        return 12;
                    case 1:
                        return 18;
                    case 2:
                        return 35;
                    case 3:
                        return 70;
                    default:
                        return 100;
                }
            default:
                switch (level) {
                    case 0:
                        return 10;
                    case 1:
                        return 14; //+4
                    case 2:
                        return 19; //+5
                    case 3:
                        return 26; //+7
                    case 4:
                        return 36; //+10
                    case 5:
                        return 50; //+14
                    case 6:
                        return 70; //+20
                    default:
                        return 100;  //+30
                }
        }
    }

    public static boolean isAccessory(final int itemId) {
        return (itemId >= 1010000 && itemId < 1040000) || (itemId >= 1122000 && itemId < 1153000) || (itemId >= 1112000 && itemId < 1113000) || (itemId >= 1670000 && itemId < 1680000);
    }

    public static boolean potentialIDFits(final int potentialID, final int newstate, final int i) {
        //first line is always the best
        //but, sometimes it is possible to get second/third line as well
        //may seem like big chance, but it's not as it grabs random potential ID anyway
        switch (newstate) {
            case 20:
                return (i == 0 || Randomizer.nextInt(10) == 0 ? potentialID >= 40000 : potentialID >= 30000 && potentialID < 60004); // xml say so
            case 19:
                return (i == 0 || Randomizer.nextInt(10) == 0 ? potentialID >= 30000 : potentialID >= 20000 && potentialID < 30000);
            case 18:
                return (i == 0 || Randomizer.nextInt(10) == 0 ? potentialID >= 20000 && potentialID < 30000 : potentialID >= 10000 && potentialID < 20000);
            case 17:
                return (i == 0 || Randomizer.nextInt(10) == 0 ? potentialID >= 10000 && potentialID < 20000 : potentialID < 10000);
            default:
                return false;
        }
    }

    public static boolean optionTypeFits(final int optionType, final int itemId) {
        switch (optionType) {
            case 10: // weapons
                return InventoryConstants.isWeapon(itemId);
            case 11: // all equipment except weapons
                return !InventoryConstants.isWeapon(itemId);
            case 20: // all armors
                return !isAccessory(itemId) && !InventoryConstants.isWeapon(itemId);
            case 40: // accessories
                return isAccessory(itemId);
            case 51: // hat
                return itemId / 10000 == 100;
            case 52: // top and overall
                return itemId / 10000 == 104 || itemId / 10000 == 105;
            case 53: // bottom and overall
                return itemId / 10000 == 106 || itemId / 10000 == 105;
            case 54: // glove
                return itemId / 10000 == 108;
            case 55: // shoe
                return itemId / 10000 == 107;
            default:
                return true;
        }
    }

    public static NebuliteGrade getNebuliteGrade(final int id) {
        if (id / 10000 != 306) {
            return NebuliteGrade.None;
        }
        if (id >= 3060000 && id < 3061000) {
            return NebuliteGrade.GradeD;
        } else if (id >= 3061000 && id < 3062000) {
            return NebuliteGrade.GradeC;
        } else if (id >= 3062000 && id < 3063000) {
            return NebuliteGrade.GradeB;
        } else if (id >= 3063000 && id < 3064000) {
            return NebuliteGrade.GradeA;
        }
        return NebuliteGrade.GradeS;
    }

    public static final boolean isMountItemAvailable(final int mountid, final int jobid) {
        if (jobid != 900 && mountid / 10000 == 190) {
            switch (mountid) {
                case 1902000:
                case 1902001:
                case 1902002:
                    return isExplorer(jobid);
                case 1902005:
                case 1902006:
                case 1902007:
                    return isCygnusKnight(jobid);
                case 1902015:
                case 1902016:
                case 1902017:
                case 1902018:
                    return isAran(jobid);
                case 1902040:
                case 1902041:
                case 1902042:
                    return isEvan(jobid);
            }

            if (isResistance(jobid)) {
                return false; //none lolol
            }
        }
        return mountid / 10000 == 190;
    }

    public static boolean isMechanicItem(final int itemId) {
        return itemId >= 1610000 && itemId < 1660000;
    }

    public static boolean isEvanDragonItem(final int itemId) {
        return itemId >= 1940000 && itemId < 1980000; //194 = mask, 195 = pendant, 196 = wings, 197 = tail
    }

    public static boolean canScroll(final int itemId) {
        return itemId / 100000 != 19 && itemId / 100000 != 16; //no mech/taming/dragon
    }

    public static boolean isFastAttackSkill(int skillId) {
        switch (skillId) {
            case 30021238: //BeamDance
            case 36121000:
            case 36120044:
            case 36120043:
            case 36120045:
            case 13121001: //Song of Heaven
                return true;
            default:
                return false;
        }
    }

    public static boolean canHammer(final int itemId) {
        switch (itemId) {
            case 1122000:
            case 1122076: //ht, chaos ht
                return false;
        }
        return canScroll(itemId);
    }
    public static int[] owlItems = new int[]{
        1082002, // work gloves
        2070005,
        2070006,
        1022047,
        1102041,
        2044705,
        2340000, // white scroll
        2040017,
        1092030,
        2040804};

    public static int getMasterySkill(final int job) {
        if (job >= 1410 && job <= 1413) {
            return 14100000;
        } else if (job >= 410 && job <= 413) {
            return 4100000;
        } else if (job >= 520 && job <= 523) {
            return 5200000;
        }
        return 0;
    }

    public static int getExpRate_Quest(final int level) {
        return (level >= 30 ? (level >= 70 ? (level >= 120 ? 10 : 5) : 2) : 1);
    }

    public static String getCommandBlockedMsg() {
        return "You may not use this command here.";
    }

    public static int getJobNumber(int jobz) {
        int job = (jobz % 1000);
        if (job / 100 == 0 || isBeginnerJob(jobz)) {
            return 0; //beginner
        } else if ((job / 10) % 10 == 0 || job == 501) {
            return 1;
        } else {
            return 2 + (job % 10);
        }
    }

    public static boolean isBeginnerJob(final int job) {
        return job == 0 || job == 1 || job == 1000 || job == 2000 || job == 2001 || job == 3000 || job == 3001 || job == 2002 || job == 2003 || job == 5000 || job == 2004 || job == 4001 || job == 4002 || job == 6000 || job == 6001 || job == 3002;
    }

    public static boolean isAzwanMap(int mapId) {
        return mapId >= 262020000 && mapId < 262023000;
    }

    public static boolean isForceRespawn(int mapid) {
        switch (mapid) {
            case 103000800: //kerning PQ crocs
            case 925100100: //crocs and stuff
                return true;
            default:
                return mapid / 100000 == 9800 && (mapid % 10 == 1 || mapid % 1000 == 100);
        }
    }

    public static int getFishingTime(boolean vip, boolean gm) {
        return gm ? 1000 : (vip ? 30000 : 60000);
    }

    public static int getCustomSpawnID(int summoner, int def) {
        switch (summoner) {
            case 9400589:
            case 9400748: //MV
                return 9400706; //jr
            default:
                return def;
        }
    }

    public static boolean canForfeit(int questid) {
        switch (questid) {
            case 20000:
            case 20010:
            case 20015: //cygnus quests
            case 20020:
                return false;
            default:
                return true;
        }
    }

    public static double getAttackRange(MapleStatEffect def, int rangeInc) {
        double defRange = ((400.0 + rangeInc) * (400.0 + rangeInc));
        if (def != null) {
            defRange += def.getMaxDistanceSq() + (def.getRange() * def.getRange());
        }
        //rangeInc adds to X
        //400 is approximate, screen is 600.. may be too much
        //200 for y is also too much
        //default 200000
        return defRange + 120000.0;
    }

    public static double getAttackRange(Point lt, Point rb) {
        double defRange = (400.0 * 400.0);
        final int maxX = Math.max(Math.abs(lt == null ? 0 : lt.x), Math.abs(rb == null ? 0 : rb.x));
        final int maxY = Math.max(Math.abs(lt == null ? 0 : lt.y), Math.abs(rb == null ? 0 : rb.y));
        defRange += (maxX * maxX) + (maxY * maxY);
        //rangeInc adds to X
        //400 is approximate, screen is 600.. may be too much
        //200 for y is also too much
        //default 200000
        return defRange + 120000.0;
    }

    public static int getLowestPrice(int itemId) {
        switch (itemId) {
            case 2340000: //ws
            case 2531000:
            case 2530000:
                return 50000000;
        }
        return -1;
    }

    public static boolean isNoDelaySkill(int skillId) {
        return skillId == 5110001 || skillId == 21101003 || skillId == 15100004 || skillId == 33101004 || skillId == 32111010 || skillId == 2111007 || skillId == 2211007 || skillId == 2311007 || skillId == 32121003 || skillId == 35121005 || skillId == 35111004 || skillId == 35121013 || skillId == 35121003 || skillId == 22150004 || skillId == 22181004 || skillId == 11101002 || skillId == 51100002 || skillId == 13101002 || skillId == 24121000 || skillId == 22161005 || skillId == 22161005;
    }

    public static boolean isNoSpawn(int mapID) {
        return mapID == 809040100 || mapID == 925020010 || mapID == 925020011 || mapID == 925020012 || mapID == 925020013 || mapID == 925020014 || mapID == 682020000 || mapID == 980010000 || mapID == 980010100 || mapID == 980010200 || mapID == 980010300 || mapID == 980010020;
    }

    public static int getExpRate(int job, int def) {
        return def;
    }

    public static int getModifier(int itemId, int up) {
        if (up <= 0) {
            return 0;
        }
        switch (itemId) {
            case 2022459:
            case 2860179:
            case 2860193:
            case 2860207:
                return 130;
            case 2022460:
            case 2022462:
            case 2022730:
                return 150;
            case 2860181:
            case 2860195:
            case 2860209:
                return 200;
        }
        if (itemId / 10000 == 286) { //familiars
            return 150;
        }
        return 200;
    }

    public static short getSlotMax(int itemId) {
        switch (itemId) {
            case 4030003:
            case 4030004:
            case 4030005:
                return 1;
            case 4001168:
            case 4031306:
            case 4031307:
            case 3993000:
            case 3993002:
            case 3993003:
                return 100;
            case 5220010:
            case 5220013:
                return 1000;
            case 5220020:
                return 2000;
        }
        return 0;
    }

    public static boolean isDropRestricted(int itemId) {
        return itemId == 3012000 || itemId == 4030004 || itemId == 1052098 || itemId == 1052202;
    }

    public static boolean isPickupRestricted(int itemId) {
        return itemId == 4030003 || itemId == 4030004;
    }

    public static short getStat(int itemId, int def) {
        switch (itemId) {
            //case 1002419:
            //    return 5;
            case 1002959:
                return 25;
            case 1142002:
                return 10;
            case 1122121:
                return 7;
        }
        return (short) def;
    }

    public static short getHpMp(int itemId, int def) {
        switch (itemId) {
            case 1122121:
                return 500;
            case 1142002:
            case 1002959:
                return 1000;
        }
        return (short) def;
    }

    public static short getATK(int itemId, int def) {
        switch (itemId) {
            case 1122121:
                return 3;
            case 1002959:
                return 4;
            case 1142002:
                return 9;
        }
        return (short) def;
    }

    public static short getDEF(int itemId, int def) {
        switch (itemId) {
            case 1122121:
                return 250;
            case 1002959:
                return 500;
        }
        return (short) def;
    }

    public static boolean isDojo(int mapId) {
        return mapId >= 925020100 && mapId <= 925023814;
    }

    public static boolean isHyperTeleMap(int mapId) {
        for (int i : hyperTele) {
            if (i == mapId) {
                return true;
            }
        }
        return false;
    }

    public static boolean isAngel(int sourceid) {
        return isBeginnerJob(sourceid / 10000) && (sourceid % 10000 == 1085 || sourceid % 10000 == 1087 || sourceid % 10000 == 1090 || sourceid % 10000 == 1179 || sourceid % 10000 == 1154);
    }

    public static boolean isFishingMap(int mapid) {
        return mapid == 749050500 || mapid == 749050501 || mapid == 749050502 || mapid == 970020000 || mapid == 970020005;
    }

    public static int getRewardPot(int itemid, int closeness) {
        switch (itemid) {
            case 2440000:
                switch (closeness / 10) {
                    case 0:
                    case 1:
                    case 2:
                        return 2028041 + (closeness / 10);
                    case 3:
                    case 4:
                    case 5:
                        return 2028046 + (closeness / 10);
                    case 6:
                    case 7:
                    case 8:
                        return 2028049 + (closeness / 10);
                }
                return 2028057;
            case 2440001:
                switch (closeness / 10) {
                    case 0:
                    case 1:
                    case 2:
                        return 2028044 + (closeness / 10);
                    case 3:
                    case 4:
                    case 5:
                        return 2028049 + (closeness / 10);
                    case 6:
                    case 7:
                    case 8:
                        return 2028052 + (closeness / 10);
                }
                return 2028060;
            case 2440002:
                return 2028069;
            case 2440003:
                return 2430278;
            case 2440004:
                return 2430381;
            case 2440005:
                return 2430393;
        }
        return 0;
    }

    public static boolean isEventMap(final int mapid) {
        return (mapid >= 109010000 && mapid < 109050000) || (mapid > 109050001 && mapid < 109090000) || (mapid >= 809040000 && mapid <= 809040100);
    }

    public static boolean isCoconutMap(final int mapid) {
        return mapid == 109080000 || mapid == 109080001 || mapid == 109080002 || mapid == 109080003 || mapid == 109080010 || mapid == 109080011 || mapid == 109080012 || mapid == 109090300 || mapid == 109090301 || mapid == 109090302 || mapid == 109090303 || mapid == 109090304 || mapid == 910040100;
    }

    public static boolean isMismatchingBulletSkill(final int nSkillId) { // Just anti-cheat exceptions.
        switch (nSkillId) {
            case constants.skills.Hero.RAGING_BLOW:
            case 21120025: // Aran Skill
            case Aran.AERO_SWING_1:
            case IceLightningArchMage.FROZEN_ORB:
            case DualBlade.BLADE_FURY:
            case DualBlade.BLOODY_STORM:
            case WindArcher.SPIRALING_VORTEX:
            case Bowmaster.ARROW_STREAM:
            case Buccaneer.BUCCANEER_BLAST:
            case DawnWarrior.SOLAR_PIERCE:
            case Shadower.BOOMERANG_STAB:
            case Xenon.BEAM_DANCE:
            case ThunderBreaker.THUNDERBOLT:
            case Phantom.TEMPEST:
            case Phantom.MILLE_AIGUILLES:
            case Phantom.MILLE_AIGUILLES_EXTRA_TARGET:
            case IceLightningArchMage.CHAIN_LIGHTNING:
            case Paladin.ADVANCED_CHARGE:
            case Evan.WIND_FLASH_1:
            case Evan.WIND_FLASH_2:
            case Evan.WIND_FLASH_3:
            case Evan.THUNDER_DIVE:
            case Evan.WIND_BREATH:
            case Evan.EARTH_BREATH:
            case Evan.EARTH_DIVE:
            case Evan.DRAGON_BREATH_1:
                return true;
        }
        return false;
    }

    public static boolean isMagicChargeSkill(final int skillid) {
        switch (skillid) {
            case 2121001: // Big Bang
            case 2221001:
            case 2321001:
            case 42121000:
            case 80001836:
                return true;
        }
        return false;
    }

    public static boolean isTeamMap(final int mapid) {
        return mapid == 109080000 || mapid == 109080001 || mapid == 109080002 || mapid == 109080003 || mapid == 109080010 || mapid == 109080011 || mapid == 109080012 || mapid == 109090300 || mapid == 109090301 || mapid == 109090302 || mapid == 109090303 || mapid == 109090304 || mapid == 910040100 || mapid == 960020100 || mapid == 960020101 || mapid == 960020102 || mapid == 960020103 || mapid == 960030100 || mapid == 689000000 || mapid == 689000010;
    }

    public static int getStatDice(int stat) {
        switch (stat) {
            case 2:
                return 30;
            case 3:
                return 20;
            case 4:
                return 15;
            case 5:
                return 20;
            case 6:
                return 30;
        }
        return 0;
    }

    public static boolean isAuraBuff(CharacterTemporaryStat stat) {
        return (stat == CharacterTemporaryStat.BMageAura);
    }

    public static boolean isValuelessBuff(int buffid) {
        switch (buffid) {
            case 23101003: //Spirit Surge
                return true;
        }
        return false;
    }

    public static boolean isAnyDropMap(int mapId) {
        switch (mapId) {
            case 180000000:
            case 180000001:
                return true;
        }
        return false;
    }

    public static int getSpecialMapTarget(int mapId) {
        if (ServerConstants.OLD_MAPS) {
            switch (mapId) {
                case 690000029:
                    return 100010000;
                case 100000000:
                    return 690000025;
            }
        }
        return mapId;
    }

    public static String getSpecialPortalTarget(int mapId, String portalId) {
        if (ServerConstants.OLD_MAPS) {
            if (mapId == 100010000 && "old_lith".equals(portalId)) {
                return "east00";
            } else if (mapId == 100000000 && "west00".equals(portalId)) {
                return "old_lith";
            } else if (mapId == 100000000 && "east00".equals(portalId)) {
                return "old_portalnpc_2";
            }
        }
        return portalId;
    }

    public static int getDiceStat(int buffid, int stat) {
        if (buffid == stat || buffid % 10 == stat || buffid / 10 == stat) {
            return getStatDice(stat);
        } else if (buffid == (stat * 100)) {
            return getStatDice(stat) + 10;
        }
        return 0;
    }

    public static boolean isEnergyBuff(int skill) { //body pressure, tele mastery, twister spin. etc
        switch (skill) {
            case 32121003:
            case 21101003:
            case 2311007:
            case 22161005:
            case 2211007:
            case 2111007:
            case 32111010:
            case 12111007:
                return true;
        }
        return false;
    }

    public static int getMPByJob(int job) {
        switch (job) {
            case 3100:
                return 30;
            case 3110:
                return 60;
            case 3111:
                return 100;
            case 3112:
                return 120;
        }
        return 30; // beginner or 3100
    }

    public static int getHpApByJob(short job) {
        if ((job % 1000) / 100 > 5) {
            job -= 500;
        }
        if ((job % 1000) / 100 == 5) {
            switch (job / 10) {
                case 51:
                    return 68;
                case 53:
                    return 28;
            }
        }
        switch (job / 100) {
            case 21:
                return 30;
            case 22:
                return 12;
            case 31:
                return 38;
            case 32:
                return 20;
        }
        switch ((job % 1000) / 100) {
            case 0:
                return 8;
            case 1:
                return 50;
            case 2:
                return 6;
            case 3:
            case 4:
                return 16;
            case 5:
                return 18;
            default:
                return 8;
        }
    }

    public static int getMpApByJob(short job) {
        if (job / 100 == 31 || job / 100 == 65) {
            return 0;
        }
        if ((job % 1000) / 100 > 5) {
            job -= 500;
        }
        switch (job / 100) {
            case 22:
                return 72;
            case 32:
                return 69;
        }
        switch ((job % 1000) / 100) {
            case 0:
                return 57;
            case 1:
                return 53;
            case 2:
                return 79;
            case 3:
            case 4:
                return 61;
            case 5:
                return 65;
            default:
                return 57;
        }
    }

    public static int getSkillLevel(final int level) {
        if (level >= 70 && level < 120) {
            return 2;
        } else if (level >= 120 && level < 200) {
            return 3;
        } else if (level == 200) {
            return 4;
        }
        return 1;
    }

    public static int[] getInnerSkillbyRank(int rank) {
        switch (rank) {
            case 0:
                return rankC;
            case 1:
                return rankB;
            case 2:
                return rankA;
            case 3:
                return rankS;
            default:
                return null;
        }
    }
    private static final int[] azwanRecipes = {2510483, 2510484, 2510485, 2510486, 2510487, 2510488, 2510489, 2510490, 2510491, 2510492, 2510493, 2510494, 2510495, 2510496, 2510497, 2510498, 2510499, 2510500, 2510501, 2510502, 2510503, 2510504, 2510505, 2510506, 2510507, 2510508, 2510509, 2510510, 2510511, 2510512, 2510513, 2510514, 2510515, 2510516, 2510517, 2510518, 2510519, 2510520, 2510521, 2510522, 2510523, 2510524, 2510525, 2510526, 2510527, 2511153, 2511154, 2511155};
    private static final int[] azwanScrolls = {2046060, 2046061, 2046062, 2046063, 2046064, 2046065, 2046066, 2046067, 2046068, 2046069, 2046141, 2046142, 2046143, 2046144, 2046145, 2046519, 2046520, 2046521, 2046522, 2046523, 2046524, 2046525, 2046526, 2046527, 2046528, 2046529, 2046530, 2046701, 2046702, 2046703, 2046704, 2046705, 2046706, 2046707, 2046708, 2046709, 2046710, 2046711, 2046712};
    private static final Pair[] useItems = {new Pair<>(2002010, 500), new Pair<>(2002006, 600), new Pair<>(2002007, 600), new Pair<>(2002008, 600), new Pair<>(2002009, 600), new Pair<>(2022003, 770), new Pair<>(2022000, 1155), new Pair<>(2001001, 2300), new Pair<>(2001002, 4000), new Pair<>(2020012, 4680), new Pair<>(2020013, 5824), new Pair<>(2020014, 8100), new Pair<>(2020015, 10200), new Pair<>(2000007, 5), new Pair<>(2000000, 5), new Pair<>(2000008, 48), new Pair<>(2000001, 48), new Pair<>(2000009, 96), new Pair<>(2000002, 96), new Pair<>(2000010, 20), new Pair<>(2000003, 20), new Pair<>(2000011, 186), new Pair<>(2000006, 186), new Pair<>(2050000, 200), new Pair<>(2050001, 200), new Pair<>(2050002, 300), new Pair<>(2050003, 500)};

    public static int[] getAzwanRecipes() {
        return azwanRecipes;
    }

    public static int[] getAzwanScrolls() {
        return azwanScrolls;
    }

    public static Pair[] getUseItems() {
        return useItems;
    }

    public static int[] getCirculators() {
        return circulators;
    }
    private static final int[] wheelRewardsA = {2512139, 2512159, 2512179, 2512199, 2512219, 2512239, 2512249, 2000000, 2000001, 2000002, 2000003, 2000007, 2000008, 2000009, 2000010, 2002000, 2002001, 2002002, 2000018, 2000019, 2020012, 2020014, 2001003, 2001515, 2001516, 2001517, 2001518, 2001519, 2001520, 2001521, 2001522, 2001523, 2001524, 2001525, 2003503, 2003504, 2003505, 2003506, 2003507, 2003508, 2004003, 2004023, 2004043, 2004063, 2030000, 2030001, 2030002, 2030003, 2030004, 2030005, 2030006, 4000014, 4000030, 4000073, 4000082, 4000085, 4000103, 4000118, 4000235, 4000296, 4000327, 4000352, 4000445, 4000446, 4000600};
    //recipes, alchemy potions, potions, town scrolls, etc items
    private static final int[] wheelRewardsB = {};
    //10%, 60%, 100% scrolls, pvp level 70 equips, blitz helm, power mane, arcana crown, elemental wands, mastery books, other rare equipments
    private static final int[] wheelRewardsC = {};
    //10%, 60% scrolls, pvp level 130 equips, mastery books

    public static void loadWheelRewards(List<Integer> items, int token) {
        int rank = token % 10;
        int[] rewards = rank == 2 ? wheelRewardsC : rank == 1 ? wheelRewardsB : wheelRewardsA;
        for (int i = 0; i < 10; i++) {
            if (Randomizer.nextInt(100) < 15 && rank == 0 && !items.contains(4031349)) {
                items.add(4031349);
            } else {
                int item = rewards[Randomizer.nextInt(rewards.length)];
                while (items.contains(item)) {
                    item = rewards[Randomizer.nextInt(rewards.length)];
                }
                items.add(item);
            }
        }
    }

    public static List<Integer> getSealedBoxItems(int itemId) {
        List<Integer> list = new LinkedList();
        int[] items = {};
        switch (itemId) {
            case 2028155:
                items = new int[]{2510028, 1050104, 1052131, 1050106, 1050099,
                    1050107, 1052072, 1050098, 1050096, 1052076, 1051101,
                    1041122, 1052071, 2510035, 1061123, 1051106, 1050103,
                    1040122, 2510023, 1052075, 2510022};
                break;
            case 2028156:
                items = new int[]{1082151, 1082153, 1082213, 1072223, 1072272,
                    1072269, 1072226, 1082168, 1082167, 1072222, 2510050,
                    1082159, 2510072, 1082139, 1082154, 1082140, 1072321,
                    1072273, 2510066, 1072215, 2510068};
                break;
        }
        for (int i : items) {
            list.add(i);
        }
        return list;
    }

    public static List<Integer> getMasteryBook(int itemId) {
        List<Integer> list = new LinkedList();
        int[] items = {};
        switch (itemId) {
            case 2290868:
                items = new int[]{2510028, 1050104, 1052131, 1050106, 1050099,
                    1050107, 1052072, 1050098, 1050096, 1052076, 1051101,
                    1041122, 1052071, 2510035, 1061123, 1051106, 1050103,
                    1040122, 2510023, 1052075, 2510022};
                break;
            case 2290869:
                items = new int[]{1082151, 1082153, 1082213, 1072223, 1072272,
                    1072269, 1072226, 1082168, 1082167, 1072222, 2510050,
                    1082159, 2510072, 1082139, 1082154, 1082140, 1072321,
                    1072273, 2510066, 1072215, 2510068};
                break;
        }
        for (int i : items) {
            list.add(i);
        }
        return list;
    }

    public static boolean isStealSkill(int skillId) {
        switch (skillId) {
            case 24001001:
            case 24101001:
            case 24111001:
            case 24121001:
                return true;
        }
        return false;
    }

    public static int getStealSkill(int job) {
        switch (job) {
            case 1:
                return 24001001;
            case 2:
                return 24101001;
            case 3:
                return 24111001;
            case 4:
                return 24121001;
            case 5:
                return 24121054;
        }
        return 0;
    }

    public static int getNumSteal(int jobNum) {
        switch (jobNum) {
            case 1:
            case 2:
                return 4;
            case 3:
                return 3;
            case 4:
            case 5:
                return 2;
        }
        return 0;
    }

    public static boolean canSteal(Skill skil) {
        return skil != null && !skil.isMovement() && !isLinkedAttackSkill(skil.getId()) && skil.getId() % 10000 >= 1000 && getJobNumber(skil.getId() / 10000) > 0 && !isDualBlade(skil.getId() / 10000) && !isCannoneer(skil.getId() / 10000) && !isJett(skil.getId() / 10000) && skil.getId() < 8000000 && skil.getEffect(1) != null && skil.getEffect(1).getSummonMovementType() == null && !skil.getEffect(1).isUnstealable();
    }

    /**
     * Determines if the following skill used is a return to HQ skill
     *
     * @param skill
     * @return
     */
    public static boolean isReturnHQSkill(final int skill) {
        switch (skill) {
            case Citizen.SECRET_ASSEMBLY:
            case Beginner.RETURN_TO_SPACESHIP: // jett
            case Beginner.MAPLE_RETURN:
            case Noblesse.IMPERIAL_RECALL:
            case Xenon.PROMESSA_ESCAPE:
            case Kinesis.RETURN_1:
            case Zero.TEMPLE_RECALL:
            case Phantom.TO_THE_SKIES:
            case Mercedes.ELVEN_BLESSING_1:
                return true;
        }
        return false;
    }

    public static boolean isHyperSkill(Skill skill) {
        /*if (skill.isHyper() || skill.getHyper() > 0) {
            return true;
        }
        if (skill.isBeginnerSkill()) {
            return false;
        }
        return skill.getId() % 1000 >= 30;*/
        return skill.isHyper();
    }

    public static boolean isDispellableMorph(int sourceid) {
        switch (sourceid) {
            case 5111005:
            case 5121003:
            case 15111002:
            case 13111005:
            case 61111008:
            case 61120008:
            case 61121053:
                return false; // Since we can't have more than 1, save up on loops
        }
        return true;
    }

    //questID; FAMILY USES 19000x, MARRIAGE USES 16000x, EXPED USES 16010x
    //dojo = 150000, bpq = 150001, master monster portals: 122600
    //compensate evan = 170000, compensate sp = 170001
    public static final int OMOK_SCORE = 122200;
    public static final int MATCH_SCORE = 122210;
    public static final int HP_ITEM = 122221;
    public static final int MP_ITEM = 122222;
    public static final int BUFF_ITEM = 122223;
    public static final int PART_JOB = 122750;
    public static final int PART_JOB_REWARD = 122751;
    public static final int JAIL_TIME = 123455;
    public static final int JAIL_QUEST = 123456;
    public static final int REPORT_QUEST = 123457;
    public static final int PLAYER_INFORMATION = 123568;
    public static final int ULT_EXPLORER = 111111;
    //codex = -55 slot
    //crafting/gathering are designated as skills(short exp then byte 0 then byte level), same with recipes(integer.max_value skill level)
    public static final int ENERGY_DRINK = 122500;
    public static final int HARVEST_TIME = 122501;
    public static final int PENDANT_SLOT = 122700;
    public static final int CURRENT_SET = 122800;
    public static final int BOSS_PQ = 150001;
    public static final int CUSTOM_BANK = 150002;
    public static final int JAGUAR = 111112;
    public static final int DOJO = 150100;
    public static final int DOJO_RECORD = 150101;
    public static final int PARTY_REQUEST = 122900;
    public static final int PARTY_INVITE = 122901;
    public static final int QUICK_SLOT = 123000;
    public static final int ITEM_TITLE = 124000;
    public static final int AUTO_PET_LOOT = 150004;

    private static int[] dmgskinitem = {2431965,
        2431966,
        2432084,
        2431967,
        2432131,
        2432153,
        2432638,
        2432659,
        2432154,
        2432637,
        2432658,
        2432207,
        2432354,
        2432355,
        2432972,
        2432465,
        2432479,
        2432526,
        2432639,
        2432660,
        2432532,
        2432592,
        2432640,
        2432661,
        2432710,
        2432836,
        2432973,
        2433063,
        2433178,
        2433456,
        2435960,
        2433715,
        2433804,
        5680343,
        2433913,
        2433980,
        2433981,
        2436229,
        2432659,
        2432526,
        2432710,
        2432355,
        2434248,
        2433362,
        2434274,
        2434289,
        2434390,
        2434391,
        5680395,
        2434528,
        2434529,
        2434654,
        2435326,
        2432749,
        2434710,
        2433777,
        2434530,
        2433571,
        2434574,
        2433828,
        2432804,
        2434824,
        2431966,
        2431967,
        2432154,
        2432354,
        2432532,
        2433715,
        2433063,
        2433913,
        2433980,
        2434248,
        2433362,
        2434274,
        2434390,
        5680395,
        2434528,
        2434529,
        2434530,
        2433571,
        2434574,
        2433828,
        2434662,
        2434664,
        2434868,
        2436041,
        2436042,
        2435046,
        2435047,
        2435836,
        2435141,
        2435179,
        2435162,
        2435157,
        2435835,
        2435159,
        2436044,
        2434663,
        2435182,
        2435850,
        2435184,
        2435222,
        2435293,
        2435313,
        2435331,
        2435332,
        2435333,
        2435334,
        2435316,
        2435408,
        2435427,
        2435428,
        2435429,
        2435456,
        2435493,
        2435331,
        2435334,
        2435959,
        2435958,
        2435431,
        2435430,
        2435432,
        2435433,
        2434601,
        2435521,
        2435196,
        2435523,
        2435524,
        2435538,
        2435832,
        2435833,
        2435839,
        2435840,
        2435841,
        2435849,
        2435972,
        2436023,
        2436024,
        2436026,
        2436027,
        2436028,
        2436029,
        2436045};

    private static int[] dmgskinnum = {0,
        1,
        1,
        2,
        3,
        4,
        4,
        4,
        5,
        5,
        5,
        6,
        7,
        8,
        8,
        9,
        10,
        11,
        11,
        11,
        12,
        13,
        14,
        14,
        15,
        16,
        17,
        18,
        20,
        21,
        22,
        23,
        24,
        25,
        26,
        27,
        28,
        29,
        4,
        11,
        15,
        8,
        34,
        35,
        36,
        37,
        38,
        39,
        40,
        41,
        42,
        48,
        49,
        50,
        51,
        52,
        43,
        44,
        45,
        46,
        47,
        53,
        1,
        2,
        5,
        7,
        12,
        23,
        25,
        26,
        27,
        34,
        35,
        36,
        38,
        40,
        41,
        42,
        43,
        44,
        45,
        46,
        74,
        75,
        76,
        77,
        78,
        79,
        80,
        81,
        82,
        83,
        84,
        85,
        86,
        87,
        88,
        89,
        90,
        91,
        92,
        93,
        94,
        95,
        96,
        97,
        98,
        99,
        100,
        101,
        102,
        103,
        104,
        105,
        106,
        96,
        99,
        109,
        110,
        111,
        112,
        113,
        114,
        115,
        116,
        117,
        118,
        119,
        120,
        121,
        122,
        123,
        124,
        125,
        126,
        127,
        128,
        129,
        130,
        131,
        132,
        133,
        134};

    public static int getDamageSkinNumberByItem(int itemid) {
        for (int i = 0; i < dmgskinitem.length; i++) {
            if (dmgskinitem[i] == itemid) {
                return dmgskinnum[i];
            }
        }
        return -1;
    }

    public static int getDamageSkinItemByNumber(int num) {
        for (int i = 0; i < dmgskinnum.length; i++) {
            if (dmgskinnum[i] == num) {
                return dmgskinitem[i];
            }
        }
        return -1;
    }

    public static Integer[] getDamageSkinsTradeBlock() {
        ArrayList<Integer> skins = new ArrayList<>();
        for (int i = 0; i < dmgskinitem.length; i++) {
            if (MapleItemInformationProvider.getInstance().isOnlyTradeBlock(dmgskinitem[i])) {
                skins.add(dmgskinitem[i]);

            }
        }
//        System.out.println(skins.size());
        Integer list[] = new Integer[skins.size()];
        return skins.toArray(list);
    }

    public static boolean isBeastTamer(int job) {
        return job <= 11213 &&job >= 11000;
    }

    public static List<Balloon> getBalloons() {
        return lBalloon;
    }

    /**
     * This method returns back the position of the beta cash inventory equvialent of what is equppied in alpha
     *
     * @param short position - The position of the shared cash item
     * @return short pos - The position of it in beta's inventory
     */
    public static short getBetaCashPosition(short position) {
        short pos = 0;
        switch (position) {
            case -101:
                pos = -1501;
                break;
            case -102:
                pos = -1502;
                break;
            case -103:
                pos = -1500;
                break;
            case -104:
                pos = -1503;
                break;
            case -105:
                pos = -1505;
                break;
            case -106:
                pos = -1508;
                break;
            case -107:
                pos = -1509;
                break;
            case -108:
                pos = -1506;
                break;
            case -109:
                pos = -1504;
                break;
            case -110:
                //has to be pendant
                break;
            case -111:
                pos = -1507;
                break;
            case -112:
                pos = -1510;
                break;
            case -113:
                pos = -1511;
                break;
        }
        return pos;
    }

    /**
     * This method returns back the position of the alpha cash inventory equvialent of what is equppied in beta
     *
     * @param short position - The position of the shared cash item
     * @return short pos - The position of it in alpha's inventory
     */
    public static short getAlphaCashPosition(short position) {
        short pos = 0;
        switch (position) {
            case -1501:
                pos = -101;
                break;
            case -1502:
                pos = -102;
                break;
            case -1500:
                pos = -103;
                break;
            case -1503:
                pos = -104;
                break;
            case -1505:
                pos = -105;
                break;
            case -1508:
                pos = -106;
                break;
            case -1509:
                pos = -107;
                break;
            case -1506:
                pos = -108;
                break;
            case -1504:
                pos = -109;
                break;
            case -1507:
                pos = -111;
                break;
            case -1510:
                pos = -112;
                break;
            case -1511:
                pos = -113;
                break;
        }
        return pos;
    }

    /**
     * This method will return true or false if the position of the item is in the beta slots
     *
     * @return boolean
     */
    public static boolean isBetaSlot(short position) {
        return position <= -1500 && position >= -1512;
    }

    /**
     * Returns back the current date in int format year month date
     *
     * @return int
     */
    public static int getTimeAsInt() {
        LocalDateTime date = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        return Integer.parseInt(date.format(formatter));
    }

    public static boolean isMage(int jobId) {
        return jobId >= 200 && jobId <= 233 || jobId >= 1200 && jobId <= 1213
                || jobId >= 2200 && jobId <= 2219 || jobId >= 3200 && jobId <= 3213
                || jobId >= 4200 && jobId <= 4213 || jobId >= 14200 && jobId <= 14213;
    }

    public static boolean SUB_ADCBA0(int nSkillID) { // TODO: WHAT IS THIS
        if (nSkillID > 131003017) {
            if (nSkillID == 400011005 || nSkillID == 400031007) {
                return true;
            }
            if (nSkillID != 400041028) {
                return IsSpiritOfElluel(nSkillID);
            }
            return true;
        }
        if (nSkillID == 131003017) {
            return true;
        }
        if (nSkillID > 131001017) {
            if (nSkillID != 131002017) {
                return IsSpiritOfElluel(nSkillID);
            }
            return true;
        }
        if (nSkillID == 131001017 || nSkillID == 14111024 || nSkillID > 14121053 && nSkillID <= 14121056) {
            return true;
        }
        return IsSpiritOfElluel(nSkillID);
    }

    public static boolean IsSpiritOfElluel(int nSkillID) {
        return nSkillID >= 400031007 && nSkillID <= 400031009;
    }

    /*
     *  Hyper Skill Required Level Provider
     *  @purpose Return the required level for a specific hyper skill.
     */
    public static int getHyperSkillRequiredLevel(int nSkillID) {
        switch (nSkillID) {
            //Hero 112
            //Advanced Combo Attack - Reinforce
            case 1120043:
                return 183;
            //Advanced Combo Attack - Opportunity
            case 1120044:
                return 162;
            //Advanced Combo Attack - Boss Rush
            case 1120045:
                return 143;
            //Advanced Final Attack - Reinforce
            case 1120046:
                return 168;
            //Advanced Final Attack - Ferocity
            case 1120047:
                return 189;
            //Advanced Final Attack - Opportunity
            case 1120048:
                return 149;
            //Raging Blow - Reinforce
            case 1120049:
                return 195;
            //Raging Blow - Spread
            case 1120050:
                return 177;
            //Raging Blow - Extra Strike
            case 1120051:
                return 155;

            //Rising Rage
            case 1121052:
                return 170;
            //Epic Adventure
            case 1121053:
                return 200;
            //Cry Valhalla
            case 1121054:
                return 150;

            //Paladin 122
            //Threaten - Persist
            case 1220043:
                return 183;
            //Threaten - Opportunity
            case 1220044:
                return 162;
            //Threaten - Enhance
            case 1220045:
                return 143;
            //Blast - Reinforce
            case 1220046:
                return 168;
            //Blast - Critical Chance
            case 1220047:
                return 189;
            //Blast - Extra Strike
            case 1220048:
                return 149;
            //Heaven's Hammer - Reinforce
            case 1220049:
                return 195;
            //Heaven's Hammer - Extra Strike
            case 1220050:
                return 177;
            //Heaven's Hammer - Cooldown Cutter
            case 1220051:
                return 155;

            //Smite Shield
            case 1221052:
                return 170;
            //Epic Adventure
            case 1221053:
                return 200;
            //Sacrosanctity
            case 1221054:
                return 150;

            //Dark Knight 132
            //Evil Eye - Reinforce
            case 1320043:
                return 183;
            //Evil Eye - Hex Reinforce
            case 1320044:
                return 162;
            //Evil Eye - Aura Reinforce
            case 1320045:
                return 143;
            //Final Pact - Damage
            case 1320046:
                return 168;
            //Final Pact - Reduce Target
            case 1320047:
                return 189;
            //Final Pact - Critical Chance
            case 1320048:
                return 149;
            //Gungnir's Descent - Reinforce
            case 1320049:
                return 195;
            //Gungnir's Descent - Guardbreak
            case 1320050:
                return 177;
            //Gungnir's Descent - Boss Rush
            case 1320051:
                return 155;

            //Nightshade Explosion
            case 1321052:
                return 170;
            //Epic Adventure
            case 1321053:
                return 200;
            //Dark Thrist
            case 1321054:
                return 150;

            //Arch Mage(Fire,Poison) 212
            //Advanced Poison Mist - Reinforce
            case 2120043:
                return 183;
            //Advanced Poison Mist - Aftermath
            case 2120044:
                return 162;
            //Advanced Poison Mist - Cripple
            case 2120045:
                return 143;
            //Paralyze - Reinforce
            case 2120046:
                return 168;
            //Paralyze - Cripple
            case 2120047:
                return 189;
            //Paralyze - Extra Strike
            case 2120048:
                return 149;
            //Mist Eruption - Reinforce
            case 2120049:
                return 195;
            //Mist Eruption - Guardbreak
            case 2120050:
                return 177;
            //Mist Eruption - Cooldown Cutter
            case 2120051:
                return 155;

            //Megiddo Flame
            case 2121052:
                return 170;
            //Epic Adventure
            case 2121053:
                return 200;
            //Inferno Aura
            case 2121054:
                return 150;

            //Arch Mage(Ice,Lightning) 222
            //Teleport Mastery - Reinforce
            case 2220043:
                return 183;
            //Teleport Mastery - Spread
            case 2220044:
                return 162;
            //Teleport Mastery - Range
            case 2220045:
                return 183;
            //Chain Lightning - Reinforce
            case 2220046:
                return 168;
            //Chain Lightning - Spread
            case 2220047:
                return 189;
            //Chain Lightning - Critical Chance
            case 2220048:
                return 149;
            //Frozen Orb - Reinforce
            case 2220049:
                return 195;
            //Frozen Orb - Spread
            case 2220050:
                return 177;
            //Frozen Orb - Critical Chance
            case 2220051:
                return 155;

            //Lightning Orb
            case 2221052:
                return 170;
            //Epic Adventure
            case 2221053:
                return 200;
            //Absolute Zero Aura
            case 2221054:
                return 150;

            //Bishop 232
            //Holy Magic Shell - Extra Guard
            case 2320043:
                return 183;
            //Holy Magic Shell - Persist
            case 2320044:
                return 162;
            //Holy Magic Shell - Cooldown Cutter
            case 2320045:
                return 143;
            //Holy Symbol - Experience
            case 2320046:
                return 168;
            //Holy Symbol - Persist
            case 2320047:
                return 189;
            //Holy Symbol - Item Drop
            case 2320048:
                return 149;
            //Advanced Blessing - Ferocity
            case 2320049:
                return 195;
            //Advanced Blessing - Boss Rush
            case 2320050:
                return 177;
            //Advanced Blessing - Extra Point
            case 2320051:
                return 155;

            //Heaven's Door
            case 2321052:
                return 170;
            //Epic Adventure
            case 2321053:
                return 200;
            //Righteous Indignant
            case 2321054:
                return 150;

            //Bowmaster 312
            //Sharp Eyes - Persist
            case 3120043:
                return 183;
            //Sharp Eyes - Guardbreak
            case 3120044:
                return 162;
            //Sharp Eyes - Critical Chance
            case 3120045:
                return 143;
            //Arrow Stream - Reinforce
            case 3120046:
                return 168;
            //Arrow Stream - Spread
            case 3120047:
                return 189;
            //Arrow Stream - Extra Strike
            case 3120048:
                return 149;
            //Hurricane - Reinforce
            case 3120049:
                return 155;
            //Hurricane - Boss Rush
            case 3120050:
                return 177;
            //Hurricane - Split Attack
            case 3120051:
                return 195;

            //Gritty Gust
            case 3121052:
                return 170;
            //Epic Adventure
            case 3121053:
                return 200;
            //Concentration
            case 3121054:
                return 150;

            //Marksman 322
            //Sharp Eyes - Persist
            case 3220043:
                return 183;
            //Sharp Eyes - Guardbreak
            case 3220044:
                return 162;
            //Sharp Eyes - Critical Chance
            case 3220045:
                return 143;
            //Piercing Arrow - Reinforce
            case 3220046:
                return 168;
            //Piercing Arrow - Spread
            case 3220047:
                return 189;
            //Piercing Arrow - Extra Strike
            case 3220048:
                return 149;
            //Snipe - Reinforce
            case 3220049:
                return 195;
            //Snipe - Boss Rush
            case 3220050:
                return 195;
            //Snipe - Cooldown Cutter
            case 3220051:
                return 143;

            //High Speed Shot
            case 3221052:
                return 170;
            //Epic Adventure
            case 3221053:
                return 200;
            //Bullseye Shot
            case 3221054:
                return 150;

            //Night Lord 412
            //Showdown - Reinforce
            case 4120043:
                return 183;
            //Showdown - Spread
            case 4120044:
                return 162;
            //Showdown - Enhance
            case 4120045:
                return 143;
            //Frailty Curse - Enhance
            case 4120046:
                return 168;
            //Frailty Curse - Slow
            case 4120047:
                return 189;
            //Frailty Curse - Boss Rush
            case 4120048:
                return 149;
            //Quad Star - Reinforce
            case 4120049:
                return 195;
            //Quad Star - Boss Rush
            case 4120050:
                return 177;
            //Quad Star - Extra Strike
            case 4120051:
                return 155;

            //Death Star
            case 4121052:
                return 170;
            //Epic Adventure
            case 4121053:
                return 200;
            //Bleed Dart
            case 4121054:
                return 150;

            //Shadower 422
            //Meso Explosion - Reinforce
            case 4220043:
                return 183;
            //Meso Explosion - Guardbreak
            case 4220044:
                return 162;
            //Meso Explosion - Enhance
            case 4220045:
                return 143;
            //Boomerang Stab - Reinforce
            case 4220046:
                return 168;
            //Boomerang Stab - Spread
            case 4220047:
                return 189;
            //Boomerang Stab - Extra Strike
            case 4220048:
                return 149;
            //Assassinate - Reinforce
            case 4220049:
                return 155;
            //Assassinate - Boss Rush
            case 4220050:
                return 177;
            //Assassinate - Guardbreak
            case 4220051:
                return 195;

            //Shadow Veil
            case 4221052:
                return 170;
            //Epic Adventure
            case 4221053:
                return 200;
            //Flip of the Coin
            case 4221054:
                return 150;

            //Blade Specialist 432
            //Bloody Storm - Reinforce
            case 434043:
                return 143;
            //Bloody Storm - Spread
            case 4340044:
                return 162;
            //Bloody Storm - Extra Strike
            case 4340045:
                return 183;
            //Phantom Blow - Reinforce
            case 4340046:
                return 148;
            //Phantom Blow - Guardbreak
            case 4340047:
                return 168;
            //Phantom Blow - Extra Strike
            case 4340048:
                return 189;
            //Blade Fury - Reinforce
            case 4340055:
                return 155;
            //Blade Fury - Guardbreak
            case 4340056:
                return 177;
            //Blade Fury - Spread
            case 4340057:
                return 195;

            //Asura's Anger
            case 4341052:
                return 170;
            //Epic Adventure
            case 4341053:
                return 200;
            //Blade Clone
            case 4341054:
                return 150;

            //Buccaneer 512
            //Double Down - Saving Grace
            case 5120043:
                return 183;
            //Double Down - Addition
            case 5120044:
                return 162;
            //Double Down - Enhance
            case 5120045:
                return 143;
            //Octopunch - Reinforce
            case 5120046:
                return 168;
            //Octopunch - Boss Rush
            case 5120047:
                return 189;
            //Octopunch - Extra Strike
            case 5120048:
                return 149;
            //Buccaneer Blast - Reinforce
            case 5120049:
                return 195;
            //Buccaneer Blast - Spread
            case 5120050:
                return 177;
            //Buccaneer Blast - Extra Strike
            case 5120051:
                return 155;

            //Power Unity
            case 5121052:
                return 170;
            //Epic Adventure
            case 5121053:
                return 200;
            //Stimulating Conversation
            case 5121054:
                return 150;

            //Corsair 522
            //Double Down - Saving Grace
            case 5220043:
                return 183;
            //Double Down - Addition
            case 5220044:
                return 162;
            //Double Down - Enhance
            case 5220045:
                return 143;
            //Brain Scrambler - Reinforce
            case 5220046:
                return 168;
            //Brain Scrambler - Extra Strike
            case 5220047:
                return 189;
            //Brain Scrambler - Boss Rush
            case 5220048:
                return 149;
            //Rapid Fire - Reinforce
            case 5220049:
                return 195;
            //Rapid Fire - Add Range
            case 5220050:
                return 177;
            //Rapid Fire - Boss Rush
            case 5220051:
                return 155;

            //Ugly bomb
            case 5221052:
                return 170;
            //Epic Adventure
            case 5221053:
                return 200;
            //Whaler's Potion
            case 5221054:
                return 150;

            //Cannon Master 532
            //Monkey Militia - Splitter
            case 5320043:
                return 183;
            //Monkey Militia - Persist
            case 5320044:
                return 162;
            //Monkey Militia - Enhance
            case 5320045:
                return 143;
            //Cannon Bazooka - Reinforce
            case 5320046:
                return 149;
            //Cannon Bazooka - Spread
            case 5320047:
                return 168;
            //Cannon Bazooka - Extra Strike
            case 5320048:
                return 189;
            //Cannon Barrage - Reinforce
            case 5320049:
                return 155;
            //Cannon Barrage - Critical Chance
            case 5320050:
                return 177;
            //Cannon Barrage - Extra Strike
            case 5320051:
                return 195;

            //Rolling Rainbow
            case 5321052:
                return 170;
            //Epic Adventure
            case 5321053:
                return 200;
            //Buckshot
            case 5321054:
                return 150;

            //Jett 572
            //Backup Beatdown - Reinforce
            case 5720043:
                return 183;
            //Backup Beatdown - Spread
            case 5720044:
                return 162;
            //Backup Beatdown - Extra Strike
            case 5720045:
                return 143;
            //Planet Buster - Critical Chance
            case 5720046:
                return 168;
            //Planet Buster - Guardbreak
            case 5720047:
                return 189;
            //Planet Buster - Extra Strike
            case 5720048:
                return 149;
            //Starforce Salvo - Reinforce
            case 5720049:
                return 195;
            //Starforce Salvo - Range
            case 5720050:
                return 177;
            //Starforce Salvo - Boss Rush
            case 5720051:
                return 155;

            //Singularity Shock
            case 5721052:
                return 170;
            //Epic Adventure
            case 5721053:
                return 200;
            //Bionic Maximizer
            case 5721054:
                return 150;

            //Dawn Warrior 1112
            //True Sight - Persist
            case 11120043:
                return 143;
            //True Sight - Enhance
            case 11120044:
                return 162;
            //True Sight - Guardbreak
            case 11120045:
                return 183;
            //Divide and Pierce - Reinforce
            case 11120046:
                return 149;
            //Divide and Pierce - Spread
            case 11120047:
                return 1628;
            //Divide and Pierce - Extra Strike
            case 11120048:
                return 189;
            //Careening Dance - Reinforce
            case 11120049:
                return 155;
            //Careening Dance - Guardbreak
            case 11120050:
                return 177;
            //Careening Dance - Boss Rush
            case 11120051:
                return 195;

            //Styx Crossing
            case 11121052:
                return 170;
            //Glory of the Guardians
            case 11121053:
                return 200;
            //Soul Forge
            case 11121054:
                return 150;

            //Blaze Wizard 1212
            //Orbital Flame - Range
            case 12120043:
                return 143;
            //Orbital Flame - Guardbreak
            case 12120044:
                return 162;
            //Orbital Flame - Split Attack
            case 12120045:
                return 183;
            //Blazing Extinction - Add Attack
            case 12120046:
                return 149;
            //Blazing Extinction - Reinforce
            case 12120047:
                return 168;
            //Blazing Extinction - Spread
            case 12120048:
                return 189;
            //Ignition - Max Ignition
            case 12120049:
                return 155;
            //Ignition - Reinforce
            case 12120050:
                return 177;
            //Ignition - Max Explosion
            case 12120051:
                return 195;

            //Cataclysm
            case 12121052:
                return 170;
            //Glory of the Guardians
            case 12121053:
                return 200;
            //Dragon Blaze
            case 12121054:
                return 150;

            //Wind Archer 1312
            //Trifling Wind - Reinforce
            case 13120043:
                return 143;
            //Trifling Wind - Enhance
            case 13120044:
                return 162;
            //Trifling Wind - Double Chance
            case 13120045:
                return 183;
            //Spiraling Vortex - Reinforce
            case 13120046:
                return 149;
            //Spiraling Vortex - Spread
            case 13120047:
                return 168;
            //Spiraling Vortex - Extra Strike
            case 13120048:
                return 189;
            //Song of Heaven - Reinforce
            case 13120049:
                return 155;
            //Song of Heaven - Guardbreak
            case 13120050:
                return 177;
            //Song of Heaven - Boss Rush
            case 13120051:
                return 195;

            //Monsoon
            case 13121052:
                return 170;
            //Glory of the Guardians
            case 13121053:
                return 200;
            //Storm Bringer
            case 13121054:
                return 150;

            //Night Watcher 1412
            //Quintuple Star - Reinforce
            case 14120043:
                return 143;
            //Quintuple Star - Boss Rush
            case 14120044:
                return 162;
            //Quintuple Star - Critical Chance
            case 14120045:
                return 183;
            //Dark Omen - Cooldown Cutter
            case 14120046:
                return 149;
            //Dark Omen - Spread
            case 14120047:
                return 168;
            //Dark Omen - Reinforce
            case 14120048:
                return 189;
            //Vitality Siphon - Extra Point
            case 14120049:
                return 155;
            //Vitality Siphon - Steel Skin
            case 14120050:
                return 177;
            //Vitality Siphon - Preparation
            case 14120051:
                return 195;

            //Dominion
            case 14121052:
                return 170;
            //Glory of the Guardians
            case 14121053:
                return 200;
            //Shadow Illusion
            case 14121054:
                return 150;

            //Thunder Breaker 1512
            //Gale - Reinforce
            case 15120043:
                return 143;
            //Gale - Spread
            case 15120044:
                return 162;
            //Gale - Extra Strike
            case 15120045:
                return 183;
            //Thunderbolt - Reinforce
            case 15120046:
                return 149;
            //Thunderbolt - Spread
            case 15120047:
                return 168;
            //Thunderbolt - Extra Strike
            case 15120048:
                return 189;
            //Annihilate - Reinforce
            case 15120049:
                return 155;
            //Annihilate - Guardbreak
            case 15120050:
                return 177;
            //Annihilate - Boss Rush
            case 15120051:
                return 195;

            //Deep Rising
            case 15121052:
                return 170;
            //Glory of the Guardians
            case 15121053:
                return 200;
            //Primal Bolt
            case 15121054:
                return 150;

            //Aran 2112
            //Heavy Swing
            case 21120059:
                return 143;
            //Frenzied Swing
            case 21120060:
                return 162;
            //Rebounding Swing
            case 21120061:
                return 183;
            //Storming Terror
            case 21120062:
                return 149;
            //Merciless Hunt
            case 21120063:
                return 168;
            //Surging Adrenaline
            case 21120064:
                return 189;
            //Beyond-er Blade
            case 21120065:
                return 155;
            //Beyond Blade Barrage
            case 21120066:
                return 177;
            //Piercing Beyond Blade
            case 21120067:
                return 195;

            //Maha's Domain
            case 21121057:
                return 170;
            //Heroic Memories
            case 21121053:
                return 200;
            //Adrenaline Burst
            case 21121058:
                return 150;

            //Evan 2217
            //Speedy Dragon Flash
            case 22170084:
                return 143;
            //Howling Wind
            case 22170085:
                return 162;
            //Rolling Thunder
            case 22170086:
                return 183;
            //Speedy Dragon Dive
            case 22170087:
                return 149;
            //Thunder Overload
            case 22170088:
                return 168;
            //Earth-shattering Dive
            case 22170089:
                return 189;
            //Speedy Dragon Breath
            case 22170090:
                return 155;
            //Lungs of Stone
            case 22170091:
                return 177;
            //Wind Breath - Opportunity
            case 22170092:
                return 195;

            //Dragon Master
            case 22171080:
                return 170;
            //Heroic Memories
            case 22171082:
                return 200;
            //Summon Onyx Dragon
            case 22171081:
                return 150;

            //Mercedes 2312
            //Ishtar's Ring - Reinforce
            case 23120043:
                return 143;
            //Ishtar's Ring - Guardbreak
            case 23120044:
                return 162;
            //Ishtar's Ring - Boss Rush
            case 23120045:
                return 183;
            //Water Shield - Reinforce
            case 23120046:
                return 149;
            //Water Shield - True Immunity 1
            case 23120047:
                return 168;
            //Water Shield - True Immunity 2
            case 23120048:
                return 189;
            //Spikes Royale - Reinforce
            case 23120049:
                return 155;
            //Spikes Royale - Armorbreak
            case 23120050:
                return 177;
            //Spikes Royale - Temper Link
            case 23120051:
                return 195;

            //Wrath of Enlil
            case 23121052:
                return 170;
            //Heroic Memories
            case 23121053:
                return 200;
            //Elvish Blessing
            case 23121054:
                return 150;

            //Phantom 2412
            //Tempest - Reinforce
            case 24120043:
                return 183;
            //Tempest - Cooldown Cutter
            case 24120044:
                return 143;
            //Tempest - Extra Target
            case 24120045:
                return 162;
            //Mille Aiguilles - Reinforce
            case 24120046:
                return 189;
            //Mille Aiguilles - Extra Target
            case 24120047:
                return 149;
            //Mille Aiguilles - Guardbreak
            case 24120048:
                return 168;
            //Bad Luck Ward - Immunity Enhance
            case 24120049:
                return 155;
            //Bad Luck Ward - Hyper Health
            case 24120050:
                return 177;
            //Bad Luck Ward - Hyper Mana
            case 24120051:
                return 195;

            //Carte Rose Finale
            case 24121052:
                return 170;
            //Heroic Memories
            case 24121053:
                return 200;
            //Impeccable Memory H
            case 24121054:
                return 150;

            //Shade 2512
            //Spirit Claw - Reinforce
            case 25120146:
                return 183;
            //Spirit Claw - Boss Rush
            case 25120147:
                return 143;
            //Spirit Claw - Extra Strike
            case 25120148:
                return 162;
            //Bomb Punch - Reinforce
            case 25120149:
                return 189;
            //Bomb Punch - Spread
            case 25120150:
                return 149;
            //Bomb Punch - Critical Chance
            case 25120151:
                return 168;
            //Fire Fox Spirits - Reinforce
            case 25120152:
                return 155;
            //Fire Fox Spirits - Repeated Attack bonus
            case 25120153:
                return 177;
            //Fire Fox Spirits - Summon Chance
            case 25120154:
                return 195;

            //Spirit Incarnation
            case 25121030:
                return 170;
            //Heroic Memories
            case 25121032:
                return 200;
            //Spirit Bond Max
            case 25121031:
                return 150;

            //Luminous 2712
            //Reflection - Range
            case 27120043:
                return 183;
            //Reflection - Spread
            case 27120044:
                return 162;
            //Reflection - Range up
            case 27120045:
                return 143;
            //Apocalypse - Reinforce
            case 27120046:
                return 189;
            //Apocalypse - Recharge
            case 27120047:
                return 149;
            //Apocalypse - Extra Target
            case 27120048:
                return 168;
            //Ender - Reinforce
            case 27120049:
                return 195;
            //Ender - Extra Target
            case 27120050:
                return 177;
            //Ender - Range Up
            case 27120051:
                return 155;

            //Armageddon
            case 27121052:
                return 170;
            //Heroic Memories
            case 27121053:
                return 200;
            //Equalize
            case 27121054:
                return 150;

            //Demon Slayer 3112
            //Demon Lash - Fury
            case 31120043:
                return 143;
            //Demon Lash - Reinforce
            case 31120044:
                return 149;
            //Demon Lash - Reinforce Duration
            case 31120045:
                return 183;
            //Dark Metamorphosis - Enhance
            case 31120046:
                return 168;
            //Dark Metamorphosis - Reinforce
            case 31120047:
                return 189;
            //Dark Metamorphosis - Reduce Fury
            case 31120048:
                return 149;
            //Demon Impact - Reinforce
            case 31120049:
                return 195;
            //Demon Impact - Extra Strike
            case 31120050:
                return 177;
            //Demon Impact - Reduce Fury
            case 31120051:
                return 155;

            //Cerberus Chomp
            case 31121052:
                return 170;
            //Demonic Fortitude
            case 31121053:
                return 200;
            //Blue Blood
            case 31121054:
                return 150;

            //Demon Avenger 3122
            //Exceed - Reinforce
            case 31220043:
                return 143;
            //Exceed - Reduce Overload
            case 31220044:
                return 162;
            //Exceed - Opportunity
            case 31220045:
                return 183;
            //Ward Evil - Harden
            case 31220046:
                return 149;
            //Ward Evil - Immunity Enhance 1
            case 31220047:
                return 168;
            //Ward Evil - Immunity Enhance 2
            case 31220048:
                return 189;
            //Nether Shield - Reinforce
            case 31220049:
                return 155;
            //Nether Shield - Spread
            case 31220050:
                return 177;
            //Nether Shield - Range
            case 31220051:
                return 195;

            //Thousand Swords
            case 31221052:
                return 170;
            //Demonic Fortitude
            case 31221053:
                return 200;
            //Forbidden Contract
            case 31221054:
                return 150;

            //Battle Mage 3212
            //Dark Genesis - Cooldown Cutter
            case 32120057:
                return 143;
            //Dark Genesis - Reinforce
            case 32120058:
                return 162;
            //Dark Genesis - Additional Reinforce
            case 32120059:
                return 183;
            //Dark Aura - Boss Rush
            case 32120060:
                return 149;
            //Weakening  Aura - Elemental Decrease
            case 32120061:
                return 168;
            //Blue Aura - Dispel Magic
            case 32120062:
                return 189;
            //Party Shield - Cooldown Cutter
            case 32120063:
                return 155;
            //Party Shield - Persist
            case 32120064:
                return 177;
            //Party Shield - Enhance
            case 32120065:
                return 195;

            //Sweeping Staff
            case 32121052:
                return 170;
            //For Liberty
            case 32121053:
                return 200;
            //Master of Death
            case 32121056:
                return 150;

            //Wild Hunter 3312
            //Feline Berserk - Reinforce
            case 33120043:
                return 143;
            //Feline Berserk - Vitality
            case 33120044:
                return 162;
            //Feline Berserk - Rapid Attack
            case 33120045:
                return 183;
            //Summon Jaguar - Enhance
            case 33120046:
                return 149;
            //Summon Jaguar - Reinforce
            case 33120047:
                return 168;
            //Summon Jaguar - Cooldown Cutter
            case 33120048:
                return 189;
            //Wild Arrow Blast - Reinforce
            case 33120049:
                return 155;
            //Wild Arrow Blast - Guardbreak
            case 33120050:
                return 177;
            //Wild Arrow Blast - Boss Rush
            case 33120051:
                return 195;

            //Exploding Arrow
            case 33121052:
                return 170;
            //For Liberty
            case 33121053:
                return 200;
            //Silent Rampage
            case 33121056:
                return 150;

            //Mechanic 3512
            //Rock 'n Shock - Reinforce
            case 35120043:
                return 143;
            //Rock 'n Shock - Persist
            case 35120044:
                return 162;
            //Rock 'n Shock - Cooldown Cutter
            case 35120045:
                return 183;
            //Support Unit: H-EX - Reinforce
            case 35120046:
                return 149;
            //Support Unit: H-EX - Party Reinforce
            case 35120047:
                return 168;
            //Support Unit: H-EX - Persist
            case 35120048:
                return 189;
            //Salvo - Reinforce
            case 35120049:
                return 155;
            //Heavy Salvo Plus - Spread
            case 35120050:
                return 177;
            //AP Salvo Plus - Extra Strike
            case 35120051:
                return 195;

            //Distortion Bomb
            case 35121052:
                return 170;
            //For Liberty
            case 35121053:
                return 200;
            //Full Spread
            case 35121055:
                return 150;

            //Xenon 3612
            //Beam Dance - Blur
            case 36120043:
                return 143;
            //Beam Dance - Reinforce
            case 36120044:
                return 162;
            //Beam Dance - Spread
            case 36120045:
                return 183;
            //Mecha Purge - Reinforce
            case 36120046:
                return 149;
            //Mecha Purge - Guardbreak
            case 36120047:
                return 168;
            //Mecha Purge - Spread
            case 36120048:
                return 189;
            //Hypogram Field - Speed
            case 36120049:
                return 155;
            //Hypogram Field - Reinforce
            case 36120050:
                return 177;
            //Hypogram Field - Persist
            case 36120051:
                return 195;

            //Entangling Lash
            case 36121053:
                return 170;
            //Orbital Cataclysm
            case 36121052:
                return 200;
            //Amaranth Generator
            case 36121054:
                return 150;

            //Blaster 3712
            //Power Punch
            case 37120043:
                return 162;
            //Piercing Punch
            case 37120044:
                return 183;
            //Aftershock Punch
            case 37120045:
                return 143;
            //Improved Bunker Explosion
            case 37120046:
                return 149;
            //Improved Bunker Shockwave
            case 37120047:
                return 168;
            //Bunker Explosion Guard Bonus
            case 37120048:
                return 189;
            //Blast Shield Recovery
            case 37120049:
                return 155;
            //Speedy Vitality Shield
            case 37120050:
                return 177;
            //Speedy Ballistic Hurricane
            case 37120051:
                return 195;

            //Hyper Magnum Punch
            case 37121052:
                return 170;
            //For Liberty
            case 37121053:
                return 200;
            //Cannon Overdrive
            case 37121054:
                return 150;

            //Hayato 4112
            //Shinsoku - Reinforce
            case 41120043:
                return 183;
            //Shinsoku - Extra Strike
            case 41120044:
                return 143;
            //Shinsoku - Boss Rush
            case 41120045:
                return 162;
            //Sudden Strike - Reinforce
            case 41120046:
                return 189;
            //Sudden Strike - Spread
            case 41120047:
                return 168;
            //Sudden Strike - Extra Strike
            case 41120048:
                return 149;
            //Hitokiri Strike - Spread
            case 41120049:
                return 195;
            //Hitokiri Strike - Extra Strike
            case 41120050:
                return 155;
            //Hitokiri Strike - Cooldown Cutter
            case 41120051:
                return 177;

            //Falcon's Honor
            case 41121052:
                return 170;
            //Princess's Vow
            case 41121053:
                return 200;
            //God of Blades
            case 41121054:
                return 150;

            //Kanna 4212
            //Vanquisher's Charm - Reinforce
            case 42120043:
                return 183;
            //Vanquisher's Charm - Spread
            case 42120044:
                return 162;
            //Vanquisher's Charm - Extra Strike
            case 42120045:
                return 143;
            //Falling Sakura - Reinforce
            case 42120046:
                return 168;
            //Falling Sakura - Spread
            case 42120047:
                return 189;
            //Falling Sakura - Vitality
            case 42120048:
                return 149;
            //Bellflower Barrier - Persist
            case 42120049:
                return 195;
            //Bellflower Barrier - Cooldown Cutter
            case 42120050:
                return 177;
            //Bellflower Barrier - Boss Rush
            case 42120051:
                return 155;

            //Veritable Pandemonium
            case 42121052:
                return 170;
            //Princess's Vow
            case 42121053:
                return 200;
            //Blackhearted Curse
            case 42121054:
                return 150;

            //Mihile 5112
            //Enduring Spirit - Persist
            case 51120043:
                return 183;
            //Enduring Spirit - Steel Skin
            case 51120044:
                return 162;
            //Enduring Spirit - Preparation
            case 51120045:
                return 143;
            //Radiant Cross - Reinforce
            case 51120056:
                return 149;
            //Radiant Cross - Spread
            case 51120057:
                return 168;
            //Radiant Cross - Extra Strike
            case 51120058:
                return 189;
            //Four-Point Assault - Reinforce
            case 51120049:
                return 195;
            //Four-Point Assault - Spread
            case 51120050:
                return 177;
            //Four-Point Assault - Extra Strike
            case 51120051:
                return 155;

            //Charging Light
            case 51121052:
                return 170;
            //Queen of Tomorrow
            case 51121053:
                return 200;
            //Sacred Cube
            case 51121054:
                return 150;

            //Kaiser 6112
            //Gigas Wave - Reinforce
            case 61120043:
                return 143;
            //Gigas Wave - Burden
            case 61120044:
                return 162;
            //Gigas Wave - Bonus Attack
            case 61120045:
                return 183;
            //Inferno Breath - Reinforce
            case 61120046:
                return 149;
            //Inferno Breath - Burn
            case 61120047:
                return 168;
            //Inferno Breath - Blaze
            case 61120048:
                return 189;
            //Wing Beat - Reinforce
            case 61120049:
                return 155;
            //Wing Beat - Pummel
            case 61120050:
                return 177;
            //Wing Beat - Extra Attack
            case 61120051:
                return 195;

            //Ancestral Prominence
            case 61121052:
                return 170;
            //Final Trance
            case 61121053:
                return 200;
            //Kaiser's Majesty
            case 61121054:
                return 150;

            //Angelic Buster 6512
            //Soul Seeker - Reinforce
            case 65120043:
                return 143;
            //Soul Seeker - Make Up
            case 65120044:
                return 162;
            //Piercing Soul Seeker
            case 65120045:
                return 183;
            //Finale Ribbon - Reinforce
            case 65120046:
                return 149;
            //Finale Ribbon - Enhance
            case 65120047:
                return 168;
            //Finale Ribbon - Cooldown Cutter
            case 65120048:
                return 189;
            //Trinity - Reinforce
            case 65120049:
                return 155;
            //Piercing Trinity
            case 65120050:
                return 177;
            //Trinity - Extra Strike
            case 65120051:
                return 195;

            //Supreme Supernova
            case 65121052:
                return 170;
            //Final Contract
            case 65121053:
                return 200;
            //Pretty Exaltation
            case 65121054:
                return 150;

            //Kinesis 14212
            //Psychic Grab - Boss Point
            case 142120043:
                return 143;
            //Psychic Grab - Reinforce
            case 142120044:
                return 162;
            //Psychic Grab - Steel Skin
            case 142120045:
                return 183;
            //Mind Tremor - Overwhelm
            case 142120046:
                return 149;
            //Mind Tremor - Reinforce
            case 142120047:
                return 168;
            //Mind Tremor - Persist
            case 142120048:
                return 189;
            //Mind Break - Reinforce
            case 142120049:
                return 155;
            //Mind Break - Cooldown Cutter
            case 142120050:
                return 177;
            //Mind Break - Enhance
            case 142120051:
                return 195;

            //Mental Tempest
            case 142121030:
                return 170;
            //Mental Shock
            case 142121031:
                return 200;
            //Mental Overdrive
            case 142121032:
                return 150;

            //Beast Tamer 11212
            //Furious Strike - Reinforce
            case 112120043:
                return 168;
            //Furious Strike - Critical Chance
            case 112120044:
                return 189;
            //Furious Strike - Boss Rush
            case 112120045:
                return 149;
            //Three-Point Pounce - Reinforce
            case 112120046:
                return 195;
            //Three-Point Pounce - Spread
            case 112120047:
                return 177;
            //Three-Point Pounce - Extra Strike
            case 112120048:
                return 155;
            //Formation Attack - Reinforce
            case 112120049:
                return 189;
            //Formation Attack - Spread
            case 112120050:
                return 149;
            //Formation Attack - Guardbreak
            case 112120051:
                return 168;
            //Friend Launcher - Reinforce
            case 112120052:
                return 183;
            //Friend Launcher - Spread
            case 112120053:
                return 162;
            //Friend Launcher - Range
            case 112120054:
                return 143;

            //Group Bear Blaster
            case 112121030:
                return 170;
            //Team Roar
            case 112121032:
                return 150;
        }
        return 0;
    }
}
