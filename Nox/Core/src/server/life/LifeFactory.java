package server.life;

import constants.NPCConstants;
import constants.ServerConstants;
import database.Database;
import provider.MapleData;
import provider.MapleDataProvider;
import provider.MapleDataTool;
import provider.wz.cache.WzDataStorage;
import provider.wz.nox.NoxBinaryReader;
import server.maps.SharedMapResources;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import tools.LogHelper;

public class LifeFactory {

    private static final MapleDataProvider etcDataWZ = WzDataStorage.getEtcWZ();
    private static final MapleData npclocData = etcDataWZ.getData("NpcLocation.img");

    private static final Map<Integer, MonsterStats> MONSTER_STATS = new HashMap<>();
    private static final Map<Integer, List<Integer>> QUEST_COUNT_GROUP = new HashMap<>();

    private static final Map<Integer, NPCStats> NPC_STATS = new HashMap<>();
    private static final Map<Integer, Integer> NPC_LOCATION = new HashMap<>();

    public static void initialize() {
        loadAllNPCsFromBinWZ();
        loadAllMobsFromBinWZ();
        loadAllCustomLifeFromDatabase();
        if (ServerConstants.CUSTOM_LIFE) OnLoadCustomLife();
    }
    
    /**
     * Java - Load Custom Life
     * @author Mazen Massoud
     * 
     * @purpose This method is used to load in certain NPCs or Mobs into the server that are required for server structure to be functional.
     * Instead of loading from a database which can be tedious during development, the data for these Life objects is defined here.
     */
    private static void OnLoadCustomLife() {
        
        /*Tutorial Intro NPCs*/
        int[] pTutorial = {331003400, NPCConstants.Tutorial_NPC, 165, 57, 20, 57, 165, 165, 0, 0, -1, 0, 0};
        
        /*FM NPCs*/
        int[] pJobAdvance = {910000000, NPCConstants.JobAdvance_NPC, -193, 34, 67, 34, -193, -193, 0, 0, -1, 0, 0};
        int[] aVMatrixAdvance = {910000000, NPCConstants.VMatrixAdvance_NPC, -839, 34, 76, 34, -839, -839, 0, 0, -1, 0, 0};
        int[] pEventShop = {910000000, NPCConstants.EventShop_NPC, -10, 34, 66, 34 ,-10, -10, 0, 0, -1, 0, 0};
        int[] pDonorShop = {910000000, NPCConstants.DonorShop_NPC, 102, 34, 63, 34, 102, 102, 0, 0, -1, 0, 0};
        int[] pVoteShop = {910000000, NPCConstants.VoteShop_NPC, 207, 34, 60, 34, 207, 207, 0, 0, -1, 0, 0};
        int[] aEquipmentShop = {910000000, 9201082, 580, 4, 30, 4, 580, 580, 0, 0, -1, 0, 1};
        int[] aCustomCashShop = {910000000, NPCConstants.CashItem_NPC, 762, -266, 44, -266, 812, 712, 0, 0, -1, 0, 0};
        int[] aMerchantShop = {910000000, 1500028, 611, -266, 87, -266, 611, 611, 0, 0, -1, 0, 0};
        int[] aGuildBoard = {910000000, 9040008, 1578, 34, 47, 34, 1578, 1578, 0, 0, -1, 0, 0};
        int[] aPerryNPC = {910000000, 9110008, -1014, 34, 77, 34, -1014, -1014, 0, 0, -1, 0, 0};
        int[] aMasteryBookNPC = {910000000, 2080008, 723, 34, 56, 34, 723, 723, 0, 0, -1, 0, 0};
        int[] aSecondaryWeaponNPC = {910000000, NPCConstants.Secondary_NPC, 1036, -266, 42, -266, 1036, 1036, 0, 0, -1, 0, 0};
        int[] aCubingNPC = {910000000, NPCConstants.Cubing_NPC, 873, 34, 55, 34, 873, 873, 0, 0, -1, 0, 0};
        
        List<int[]> aCustomLifeData = Arrays.asList(pJobAdvance, aVMatrixAdvance, pTutorial, aCustomCashShop, pDonorShop, pVoteShop, 
                                                    pEventShop, aEquipmentShop, aMerchantShop, aGuildBoard, aPerryNPC, aMasteryBookNPC,
                                                    aSecondaryWeaponNPC, aCubingNPC);
        
        for (int nIndex = 0; nIndex < aCustomLifeData.size(); nIndex++) {
            int[] aData = aCustomLifeData.get(nIndex);
            
            int nMapID = aData[0];
            int nID = aData[1];
            int nX = aData[2];
            int nY = aData[3];
            int nFH = aData[4];
            int nCY = aData[5];
            int nRX0 = aData[6];
            int nRX1 = aData[7];
            int tMob = aData[8];
            int nF = aData[9];
            int nTeam = aData[10];
            String sType = (aData[11] > 0) ? "m" : "n";
            boolean bHide = (aData[12] > 0);

            final SharedMapResources pResources = SharedMapResources.getAndGetSMR(nMapID);
            pResources.LifeStorage.add(new SharedMapResources.TemporaryStorage_Life(nID, nX, nY, nFH, nCY, nRX0, nRX1, tMob, nF, nTeam, sType, "", bHide));
        }
    }

    private static void loadAllNPCsFromBinWZ() {
        try {
            final NoxBinaryReader data = WzDataStorage.getBinaryNPCData();

            int NpcSize = data.readInt();
            for (int i = 0; i < NpcSize; i++) {
                int npcid = data.readInt();
                String quest = data.readAsciiString();
                short trunk = data.readShort();
                boolean floating = data.readBoolean();
                boolean mapleTV = data.readBoolean();
                boolean hideName = data.readBoolean();
                boolean hide = data.readBoolean();
                boolean skeleton = data.readBoolean();
                boolean componentNPC = data.readBoolean();
                boolean talkMouseOnly = data.readBoolean();
                boolean shop = data.readBoolean();
                int linkId = data.readInt();
                String script = data.readAsciiString();
                boolean jsonLoad = data.readBoolean();
                boolean forceMove = data.readBoolean();

                NPCStats npcStats = new NPCStats();
                npcStats.setNpcid(npcid);
                npcStats.setQuest(quest);
                npcStats.setFloating(floating);
                npcStats.setMapletv(mapleTV);
                npcStats.setTrunk(trunk);
                npcStats.setHideName(hideName);
                npcStats.setHide(hide);
                npcStats.setSkeleton(skeleton);
                npcStats.setComponentNPC(componentNPC);
                npcStats.setTalkMouseOnly(talkMouseOnly);
                npcStats.setShop(shop);
                npcStats.setLinkId(linkId);
                npcStats.setScript(script);
                npcStats.setJsonLoad(jsonLoad);
                npcStats.setForceMove(forceMove);

                NPC_STATS.put(npcid, npcStats);
            }
            System.out.println(String.format("[Info] Loaded %d NPCs.", NPC_STATS.size()));
            data.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void loadAllMobsFromBinWZ() {
        try {
            final NoxBinaryReader data = WzDataStorage.getBinaryMobsData();

            final int MobQuestCount = data.readInt();
            for (int i = 0; i < MobQuestCount; i++) {
                final short MobQuestImgCount = data.readShort();
                final int ImageName = data.readInt();

                final List<Integer> mobs = new LinkedList();

                for (int z = 0; z < MobQuestImgCount; z++) {
                    mobs.add(data.readInt());
                }
                QUEST_COUNT_GROUP.put(ImageName, mobs);
            }

            for (int mobwzFiles = 0; mobwzFiles < 2; mobwzFiles++) {
                final int MobCount = data.readInt();
                for (int i = 0; i < MobCount; i++) {
                    final int MobId = data.readInt();
                    MonsterStats stats = new MonsterStats(MobId, data);

                    MONSTER_STATS.put(MobId, stats);
                }
            }
            data.close();

            System.out.println(String.format("[Info] Loaded %d Mobs.", MONSTER_STATS.size()));
            data.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void loadAllCustomLifeFromDatabase() {

        try (Connection con = Database.GetConnection()) {

            PreparedStatement ps = con.prepareStatement("SELECT * FROM wz_customlife");
            ResultSet rs = ps.executeQuery();
            short mobs = 0;
            short npcs = 0;
            while (rs.next()) {
                int mapid = rs.getInt("mid");

                int id = rs.getInt("idd");

                int x = rs.getInt("x");
                int y = rs.getInt("y");
                int fh_ = rs.getInt("fh");
                int cy = rs.getInt("cy");
                int rx0 = rs.getInt("rx0");
                int rx1 = rs.getInt("rx1");
                int mobTime = rs.getInt("mobtime");
                int f = rs.getInt("f");
                int team = rs.getInt("team");
                String type = rs.getString("type");
                boolean hide = rs.getBoolean("hide");

                if (type.equals("m")) {
                    mobs++;
                } else if (type.equals("n")) {
                    npcs++;
                }

                final SharedMapResources smr = SharedMapResources.getAndGetSMR(mapid);

                smr.LifeStorage.add(new SharedMapResources.TemporaryStorage_Life(id, x, y, fh_, cy, rx0, rx1, mobTime, f, team, type, "", hide));
            }
            ps.close();
            System.out.printf("[Info] Loaded %s NPCs and %s Mobs from the database.\n", mobs, npcs);
        } catch (SQLException e) {
            LogHelper.SQL.get().info("[SQL] There was an issue with something from the database:\n", e);
        }

    }

    /**
     * Gets the loaded NPC stats from the ID of the NPC.
     *
     * @param npcid
     * @return MapleNPCStats (null if not found)
     */
    public static NPCStats getNPCStats(int npcid) {
        if (NPC_STATS.containsKey(npcid)) {
            return NPC_STATS.get(npcid);
        }
        return null;
    }

    public static AbstractLoadedMapleLife getLife(int id, LifeType type) {
        switch (type) {
            case Npcs:
                return getNPC(id);
            case Monster:
                return getMonster(id);
            default:
                System.err.println("Unknown Life type: " + type);
                return null;
        }
    }

    public static int getNPCLocation(int npcid) {
        if (NPC_LOCATION.containsKey(npcid)) {
            return NPC_LOCATION.get(npcid);
        }
        final int map = MapleDataTool.getIntConvert(Integer.toString(npcid) + "/0", npclocData, -1);
        NPC_LOCATION.put(npcid, map);
        return map;
    }

    public static List<Integer> getQuestCount(final int id) {
        return QUEST_COUNT_GROUP.get(id);
    }

    public static Mob getMonster(int mid) {
        MonsterStats stats = getMonsterStats(mid);
        if (stats == null) {
            return null;
        }
        return new Mob(mid, stats);
    }

    public static NPCLife getNPC(final int nid) {
        return new NPCLife(nid);
    }

    public static MonsterStats getMonsterStats(int mid) {
        MonsterStats stats = MONSTER_STATS.get(mid);
        return stats;

        /*  for (int i = 0; true; i++) { // TODO: Check and reprint all available values again..doing like below is a ridiculous way
                final MapleData monsterAtt = monsterInfoData.getChildByPath("attack/" + i);
                final MapleData attackData = monsterData.getChildByPath("attack" + (i + 1) + "/info");
                if (attackData == null || monsterAtt == null) {
                    break;
                }
                final MobAttackInfo ret = new MobAttackInfo();

                boolean deadlyAttack = monsterAtt.getChildByPath("deadlyAttack") != null;
                if (!deadlyAttack) {
                    deadlyAttack = attackData.getChildByPath("deadlyAttack") != null;
                }
                ret.setDeadlyAttack(deadlyAttack);

                int mpBurn = MapleDataTool.getInt("mpBurn", monsterAtt, 0);
                if (mpBurn == 0) {
                    mpBurn = MapleDataTool.getInt("mpBurn", attackData, 0);
                }
                ret.setMpBurn(mpBurn);

                int disease = MapleDataTool.getInt("disease", monsterAtt, 0);
                if (disease == 0) {
                    disease = MapleDataTool.getInt("disease", attackData, 0);
                }
                ret.setDiseaseSkill(disease);

                int level = MapleDataTool.getInt("level", monsterAtt, 0);
                if (level == 0) {
                    level = MapleDataTool.getInt("level", attackData, 0);
                }
                ret.setDiseaseLevel(level);

                int conMP = MapleDataTool.getInt("conMP", monsterAtt, 0);
                if (conMP == 0) {
                    conMP = MapleDataTool.getInt("conMP", attackData, 0);
                }
                ret.setMpCon(conMP);

                int attackAfter = MapleDataTool.getInt("attackAfter", monsterAtt, 0);
                if (attackAfter == 0) {
                    attackAfter = MapleDataTool.getInt("attackAfter", attackData, 0);
                }
                ret.attackAfter = attackAfter;

                int PADamage = MapleDataTool.getInt("PADamage", monsterAtt, 0);
                if (PADamage == 0) {
                    PADamage = MapleDataTool.getInt("PADamage", attackData, 0);
                }
                ret.PADamage = PADamage;

                int MADamage = MapleDataTool.getInt("MADamage", monsterAtt, 0);
                if (MADamage == 0) {
                    MADamage = MapleDataTool.getInt("MADamage", attackData, 0);
                }
                ret.MADamage = MADamage;

                boolean magic = MapleDataTool.getInt("magic", monsterAtt, 0) > 0;
                if (!magic) {
                    magic = MapleDataTool.getInt("magic", attackData, 0) > 0;
                }
                ret.magic = magic;
                ret.isElement = monsterAtt.getChildByPath("elemAttr") != null; // we handle it like this, i don't know what it does

                if (attackData.getChildByPath("range") != null) { // Definitely in attackData
                    ret.range = MapleDataTool.getInt("range/r", attackData, 0);
                    if (attackData.getChildByPath("range/lt") != null && attackData.getChildByPath("range/rb") != null) {
                        ret.lt = (Point) attackData.getChildByPath("range/lt").getData();
                        ret.rb = (Point) attackData.getChildByPath("range/rb").getData();
                    }
                }
                stats.addMobAttack(ret);
            }*/
    }
}
