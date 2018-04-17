package server;

import java.awt.Point;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import client.MapleTrait.MapleTraitType;
import client.inventory.Equip;
import client.inventory.EquipSlotType;
import client.inventory.Item;
import client.inventory.ItemFlag;
import client.inventory.MapleInventoryType;
import constants.GameConstants;
import constants.InventoryConstants;
import constants.ItemConstants;
import database.Database;
import provider.MapleData;
import provider.MapleDataDirectoryEntry;
import provider.MapleDataEntry;
import provider.MapleDataFileEntry;
import provider.MapleDataProvider;
import provider.MapleDataTool;
import provider.data.loaders.MapleAndroidBuilder;
import provider.wz.MapleDataType;
import provider.wz.cache.WzDataStorage;
import server.StructSetItem.SetItem;
import server.farm.inventory.FarmItemInformation;
import server.maps.objects.User;
import tools.Pair;
import tools.Triple;

public class MapleItemInformationProvider {

    private final static MapleItemInformationProvider instance = new MapleItemInformationProvider();

    protected final MapleDataProvider chrData = WzDataStorage.getCharacterWZ();
    protected final MapleDataProvider etcData = WzDataStorage.getEtcWZ();
    protected final MapleDataProvider itemData = WzDataStorage.getItemWZ();

    protected final Map<Integer, EquipSlotType> equipValidSlotCheck = new HashMap<>();
    protected final Map<Integer, ItemInformation> dataCache = new HashMap<>();
    protected final Map<Integer, FarmItemInformation> farmDataCache = new HashMap<>();
    protected final Map<String, List<Triple<String, Point, Point>>> afterImage = new HashMap<>();
    protected final Map<Integer, MapleStatEffect> itemEffects = new HashMap<>();
    protected final Map<Integer, MapleStatEffect> itemEffectsEx = new HashMap<>();
    protected final Map<Integer, Integer> mobIds = new HashMap<>();
    protected final Map<Integer, Pair<Integer, Integer>> potLife = new HashMap<>(); //itemid to lifeid, levels
    protected final Map<Integer, MapleFamiliar> familiars = new HashMap<>(); //by familiarID
    protected final Map<Integer, MapleFamiliar> familiarsItem = new HashMap<>(); //by cardID
    protected final Map<Integer, MapleFamiliar> familiarsMob = new HashMap<>(); //by mobID
    protected final ArrayList<MapleAndroidBuilder> androids = new ArrayList<>();
    protected final Map<Integer, Triple<Integer, List<Integer>, List<Integer>>> monsterBookSets = new HashMap<>();
    protected final Map<Integer, StructSetItem> setItems = new HashMap<>();

    public void runEtc() {
        if (!setItems.isEmpty()) {
            return;
        }
        final MapleData setsData = etcData.getData("SetItemInfo.img");
        StructSetItem itemz;
        SetItem itez;
        for (MapleData dat : setsData) {
            itemz = new StructSetItem();
            itemz.setItemID = Integer.parseInt(dat.getName());
            itemz.completeCount = (byte) MapleDataTool.getIntConvert("completeCount", dat, 0);
            for (MapleData level : dat.getChildByPath("ItemID")) {
                if (level.getType() != MapleDataType.INT) {
                    for (MapleData leve : level) {
                        if (!leve.getName().equals("representName") && !leve.getName().equals("typeName")) {
                            itemz.itemIDs.add(MapleDataTool.getInt(leve));
                        }
                    }
                } else {
                    itemz.itemIDs.add(MapleDataTool.getInt(level));
                }
            }
            for (MapleData level : dat.getChildByPath("Effect")) {
                itez = new SetItem();
                itez.incPDD = MapleDataTool.getIntConvert("incPDD", level, 0);
                itez.incMDD = MapleDataTool.getIntConvert("incMDD", level, 0);
                itez.incSTR = MapleDataTool.getIntConvert("incSTR", level, 0);
                itez.incDEX = MapleDataTool.getIntConvert("incDEX", level, 0);
                itez.incINT = MapleDataTool.getIntConvert("incINT", level, 0);
                itez.incLUK = MapleDataTool.getIntConvert("incLUK", level, 0);
                itez.incACC = MapleDataTool.getIntConvert("incACC", level, 0);
                itez.incPAD = MapleDataTool.getIntConvert("incPAD", level, 0);
                itez.incMAD = MapleDataTool.getIntConvert("incMAD", level, 0);
                itez.incSpeed = MapleDataTool.getIntConvert("incSpeed", level, 0);
                itez.incMHP = MapleDataTool.getIntConvert("incMHP", level, 0);
                itez.incMMP = MapleDataTool.getIntConvert("incMMP", level, 0);
                itez.incMHPr = MapleDataTool.getIntConvert("incMHPr", level, 0);
                itez.incMMPr = MapleDataTool.getIntConvert("incMMPr", level, 0);
                itez.incAllStat = MapleDataTool.getIntConvert("incAllStat", level, 0);
                itez.option1 = MapleDataTool.getIntConvert("Option/1/option", level, 0);
                itez.option2 = MapleDataTool.getIntConvert("Option/2/option", level, 0);
                itez.option1Level = MapleDataTool.getIntConvert("Option/1/level", level, 0);
                itez.option2Level = MapleDataTool.getIntConvert("Option/2/level", level, 0);
                itemz.items.put(Integer.parseInt(level.getName()), itez);
            }
            setItems.put(itemz.setItemID, itemz);
        }

        final MapleDataDirectoryEntry android = (MapleDataDirectoryEntry) etcData.getRoot().getEntry("Android");
        for (MapleDataEntry androidData : android.getFiles()) {
            final MapleData androidLooks = etcData.getData("Android/" + androidData.getName());
            final ArrayList<Integer> face = new ArrayList<>(), hair = new ArrayList<>(), skin = new ArrayList<>();
            for (MapleData faceChildren : androidLooks.getChildByPath("costume/face")) {
                face.add(MapleDataTool.getInt(faceChildren, 2000));
            }
            for (MapleData hairChildren : androidLooks.getChildByPath("costume/hair")) {
                hair.add(MapleDataTool.getInt(hairChildren, 30000));
            }
            for (MapleData skinChildren : androidLooks.getChildByPath("costume/skin")) {
                skin.add(MapleDataTool.getInt(skinChildren, 20000));
            }
            MapleAndroidBuilder androidBuilder = new MapleAndroidBuilder();
            androidBuilder.setId(Integer.parseInt(androidData.getName().substring(0, 4)));
            androidBuilder.setFace(face);
            androidBuilder.setHair(hair);
            androidBuilder.setSkin(skin);
            androids.add(androidBuilder);
        }

        final MapleData lifesData = etcData.getData("ItemPotLifeInfo.img");
        for (MapleData d : lifesData) {
            if (d.getChildByPath("info") != null && MapleDataTool.getInt("type", d.getChildByPath("info"), 0) == 1) {
                potLife.put(MapleDataTool.getInt("counsumeItem", d.getChildByPath("info"), 0), new Pair<>(Integer.parseInt(d.getName()), d.getChildByPath("level").getChildren().size()));
            }
        }
        List<Triple<String, Point, Point>> thePointK = new ArrayList<>();
        List<Triple<String, Point, Point>> thePointA = new ArrayList<>();

        final MapleDataDirectoryEntry a = (MapleDataDirectoryEntry) chrData.getRoot().getEntry("Afterimage");
        for (MapleDataEntry b : a.getFiles()) {
            final MapleData iz = chrData.getData("Afterimage/" + b.getName());
            List<Triple<String, Point, Point>> thePoint = new ArrayList<>();
            Map<String, Pair<Point, Point>> dummy = new HashMap<>();
            for (MapleData i : iz) {
                for (MapleData xD : i) {
                    if (xD.getName().contains("prone") || xD.getName().contains("double") || xD.getName().contains("triple")) {
                        continue;
                    }
                    if ((b.getName().contains("bow") || b.getName().contains("Bow")) && !xD.getName().contains("shoot")) {
                        continue;
                    }
                    if ((b.getName().contains("gun") || b.getName().contains("cannon")) && !xD.getName().contains("shot")) {
                        continue;
                    }
                    if (dummy.containsKey(xD.getName())) {
                        if (xD.getChildByPath("lt") != null) {
                            Point lt = (Point) xD.getChildByPath("lt").getData();
                            Point ourLt = dummy.get(xD.getName()).left;
                            if (lt.x < ourLt.x) {
                                ourLt.x = lt.x;
                            }
                            if (lt.y < ourLt.y) {
                                ourLt.y = lt.y;
                            }
                        }
                        if (xD.getChildByPath("rb") != null) {
                            Point rb = (Point) xD.getChildByPath("rb").getData();
                            Point ourRb = dummy.get(xD.getName()).right;
                            if (rb.x > ourRb.x) {
                                ourRb.x = rb.x;
                            }
                            if (rb.y > ourRb.y) {
                                ourRb.y = rb.y;
                            }
                        }
                    } else {
                        Point lt = null, rb = null;
                        if (xD.getChildByPath("lt") != null) {
                            lt = (Point) xD.getChildByPath("lt").getData();
                        }
                        if (xD.getChildByPath("rb") != null) {
                            rb = (Point) xD.getChildByPath("rb").getData();
                        }
                        dummy.put(xD.getName(), new Pair<>(lt, rb));
                    }
                }
            }
            for (Entry<String, Pair<Point, Point>> ez : dummy.entrySet()) {
                if (ez.getKey().length() > 2 && ez.getKey().substring(ez.getKey().length() - 2, ez.getKey().length() - 1).equals("D")) { //D = double weapon
                    thePointK.add(new Triple<>(ez.getKey(), ez.getValue().left, ez.getValue().right));
                } else if (ez.getKey().contains("PoleArm")) { //D = double weapon
                    thePointA.add(new Triple<>(ez.getKey(), ez.getValue().left, ez.getValue().right));
                } else {
                    thePoint.add(new Triple<>(ez.getKey(), ez.getValue().left, ez.getValue().right));
                }
            }
            afterImage.put(b.getName().substring(0, b.getName().length() - 4), thePoint);
        }
        afterImage.put("katara", thePointK); //hackish
        afterImage.put("aran", thePointA); //hackish
    }

    public void runItems() {
        final MapleData fData = etcData.getData("FamiliarInfo.img");
        for (MapleData d : fData) {
            MapleFamiliar f = new MapleFamiliar();
            f.setGrade((byte) 0);
            f.setMob(MapleDataTool.getInt("mob", d, 0));
            f.setPassive(MapleDataTool.getInt("passive", d, 0));
            f.setItemid(MapleDataTool.getInt("consume", d, 0));
            f.setFamiliar(Integer.parseInt(d.getName()));
            familiars.put(f.getFamiliar(), f);
            familiarsItem.put(f.getItemid(), f);
            familiarsMob.put(f.getMob(), f);
        }
        final MapleDataDirectoryEntry e = (MapleDataDirectoryEntry) chrData.getRoot().getEntry("Familiar");
        for (MapleDataEntry d : e.getFiles()) {
            final int id = Integer.parseInt(d.getName().substring(0, d.getName().length() - 4));
            if (familiars.containsKey(id)) {
                familiars.get(id).setGrade((byte) MapleDataTool.getInt("grade", chrData.getData("Familiar/" + d.getName()).getChildByPath("info"), 0));
            }
        }

        final MapleData mSetsData = etcData.getData("MonsterBookSet.img");
        for (MapleData d : mSetsData.getChildByPath("setList")) {
            if (MapleDataTool.getInt("deactivated", d, 0) > 0) {
                continue;
            }
            final List<Integer> set = new ArrayList<>(), potential = new ArrayList<>(3);
            for (MapleData ds : d.getChildByPath("stats/potential")) {
                if (ds.getType() != MapleDataType.STRING && MapleDataTool.getInt(ds, 0) > 0) {
                    potential.add(MapleDataTool.getInt(ds, 0));
                    if (potential.size() >= 5) {
                        break;
                    }
                }
            }
            for (MapleData ds : d.getChildByPath("cardList")) {
                set.add(MapleDataTool.getInt(ds, 0));
            }
            monsterBookSets.put(Integer.parseInt(d.getName()), new Triple<>(MapleDataTool.getInt("setScore", d, 0), set, potential));
        }

        try (Connection con = Database.GetConnection()) {
            System.out.println("[" + Thread.currentThread().getStackTrace()[2].getClassName() + "." + Thread.currentThread().getStackTrace()[2].getMethodName() + "] " + Database.GetPoolStats() + " Opening");

            // Load Item Data
            PreparedStatement ps = con.prepareStatement("SELECT * FROM wz_itemdata");

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                initItemInformation(rs);
            }
            rs.close();
            ps.close();

            // Load Item Equipment Data
            ps = con.prepareStatement("SELECT * FROM wz_itemequipdata ORDER BY itemid");
            rs = ps.executeQuery();
            while (rs.next()) {
                initItemEquipData(rs);
            }
            rs.close();
            ps.close();

            // Load Item Equipment slot Data
            ps = con.prepareStatement("SELECT * FROM wz_itemequip_slotdata");
            rs = ps.executeQuery();
            while (rs.next()) {
                int itemid = rs.getInt("itemid");
                equipValidSlotCheck.put(itemid, this.getSlotType_Enum(rs.getString("islot"), itemid));
            }
            rs.close();
            ps.close();

            // Load Item Addition Data
            ps = con.prepareStatement("SELECT * FROM wz_itemadddata ORDER BY itemid");
            rs = ps.executeQuery();
            while (rs.next()) {
                initItemAddData(rs);
            }
            rs.close();
            ps.close();

            // Load Item Reward Data
            ps = con.prepareStatement("SELECT * FROM wz_itemrewarddata ORDER BY itemid");
            rs = ps.executeQuery();
            while (rs.next()) {
                initItemRewardData(rs);
            }
            rs.close();
            ps.close();

            // Finalize all Equipments
            for (Entry<Integer, ItemInformation> entry : dataCache.entrySet()) {
                if (GameConstants.getInventoryType(entry.getKey()) == MapleInventoryType.EQUIP) {
                    finalizeEquipData(entry.getValue());
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        System.out.println("[" + Thread.currentThread().getStackTrace()[2].getClassName() + "." + Thread.currentThread().getStackTrace()[2].getMethodName() + "] " + Database.GetPoolStats() + " Closing");

    }

    public void finalizeEquipData(ItemInformation item) {
        int itemId = item.itemId;
        if (item.equipStats == null) {
            item.equipStats = new HashMap<>();
        }

        item.eq = new Equip(itemId, (byte) 0, -1, (byte) 0);
        item.eq.setReqLevel((short) getReqLevel(itemId));
        if (item.equipStats.size() > 0) {
            for (Entry<String, Integer> stat : item.equipStats.entrySet()) {
                final String key = stat.getKey();
                switch (key) {
                    case "STR":
                        item.eq.setStr(stat.getValue().shortValue());
                        break;
                    case "DEX":
                        item.eq.setDex(stat.getValue().shortValue());
                        break;
                    case "INT":
                        item.eq.setInt(stat.getValue().shortValue());
                        break;
                    case "LUK":
                        item.eq.setLuk(stat.getValue().shortValue());
                        break;
                    case "PAD":
                        item.eq.setWatk(stat.getValue().shortValue());
                        break;
                    case "PDD":
                        item.eq.setWdef(stat.getValue().shortValue());
                        break;
                    case "MAD":
                        item.eq.setMatk(stat.getValue().shortValue());
                        break;
                    case "MDD":
                        item.eq.setMdef(stat.getValue().shortValue());
                        break;
                    case "MHPr":
                        item.eq.setMHPr((short) stat.getValue().intValue());
                        break;
                    case "MMPr":
                        item.eq.setMMPr((short) stat.getValue().intValue());
                        break;
                    case "ACC":
                        item.eq.setAcc((short) stat.getValue().intValue());
                        break;
                    case "EVA":
                        item.eq.setAvoid((short) stat.getValue().intValue());
                        break;
                    case "Speed":
                        item.eq.setSpeed((short) stat.getValue().intValue());
                        break;
                    case "Jump":
                        item.eq.setJump((short) stat.getValue().intValue());
                        break;
                    case "MHP":
                        item.eq.setHp(stat.getValue().shortValue());
                        break;
                    case "MMP":
                        item.eq.setMp(stat.getValue().shortValue());
                        break;
                    case "tuc":
                        item.eq.setUpgradeSlots(stat.getValue().byteValue());
                        break;
                    case "Craft":
                        item.eq.setHands(stat.getValue().shortValue());
                        break;
                    case "durability":
                        item.eq.setDurability(stat.getValue());
                        break;
                    case "charmEXP":
                        item.eq.setCharmEXP(stat.getValue().shortValue());
                        break;
                    case "PVPDamage":
                        item.eq.setPVPDamage(stat.getValue().shortValue());
                        break;
                    case "imdR":
                        item.eq.setIgnorePDR(stat.getValue().byteValue());
                        break;
                    case "bdR":
                        item.eq.setBossDamage(stat.getValue().byteValue());
                    case "statR":
                        item.eq.setAllStat(stat.getValue().byteValue());
                        break;
                }
            }
            item.eq.setKarmaCount((byte) -1);
            if (item.equipStats.get("cash") != null && item.eq.getCharmEXP() <= 0) { //set the exp
                short exp = 0;
                int identifier = itemId / 10000;
                if (InventoryConstants.isWeapon(itemId) || identifier == 106) { //weapon overall
                    exp = 60;
                } else if (identifier == 100) { //hats
                    exp = 50;
                } else if (GameConstants.isAccessory(itemId) || identifier == 102 || identifier == 108 || identifier == 107) { //gloves shoes accessory
                    exp = 40;
                } else if (identifier == 104 || identifier == 105 || identifier == 110) { //top bottom cape
                    exp = 30;
                }
                item.eq.setCharmEXP(exp);
            }
        }
    }

    public final Collection<Integer> getMonsterBookList() {
        return mobIds.values();
    }

    public final Map<Integer, Integer> getMonsterBook() {
        return mobIds;
    }

    public final Pair<Integer, Integer> getPot(int f) {
        return potLife.get(f);
    }

    public final MapleFamiliar getFamiliar(int f) {
        return familiars.get(f);
    }

    public final Map<Integer, MapleFamiliar> getFamiliars() {
        return familiars;
    }

    public final MapleFamiliar getFamiliarByItem(int f) {
        return familiarsItem.get(f);
    }

    public final MapleFamiliar getFamiliarByMob(int f) {
        return familiarsMob.get(f);
    }

    public static final MapleItemInformationProvider getInstance() {
        return instance;
    }

    public final Collection<ItemInformation> getAllItems() {
        return dataCache.values();
    }

    public ArrayList<MapleAndroidBuilder> getAndroidInfo() {
        return androids;
    }

    public final Triple<Integer, List<Integer>, List<Integer>> getMonsterBookInfo(int i) {
        return monsterBookSets.get(i);
    }

    public final Map<Integer, Triple<Integer, List<Integer>, List<Integer>>> getAllMonsterBookInfo() {
        return monsterBookSets;
    }

    protected final MapleData getItemData(final int itemId) {
        MapleData ret = null;
        final String idStr = "0" + String.valueOf(itemId);
        MapleDataDirectoryEntry root = itemData.getRoot();
        for (final MapleDataDirectoryEntry topDir : root.getSubdirectories()) {
            // we should have .img files here beginning with the first 4 IID
            for (final MapleDataFileEntry iFile : topDir.getFiles()) {
                if (iFile.getName().equals(idStr.substring(0, 4) + ".img")) {
                    ret = itemData.getData(topDir.getName() + "/" + iFile.getName());
                    if (ret == null) {
                        return null;
                    }
                    ret = ret.getChildByPath(idStr);
                    return ret;
                } else if (iFile.getName().equals(idStr.substring(1) + ".img")) {
                    ret = itemData.getData(topDir.getName() + "/" + iFile.getName());
                    return ret;
                }
            }
        }
        //equips dont have item effects :)
        /*
         * root = equipData.getRoot(); for (final MapleDataDirectoryEntry topDir
         * : root.getSubdirectories()) { for (final MapleDataFileEntry iFile :
         * topDir.getFiles()) { if (iFile.getName().equals(idStr + ".img")) {
         * ret = equipData.getData(topDir.getName() + "/" + iFile.getName());
         * return ret; } }
         }
         */

        return ret;
    }

    public Integer getItemIdByMob(int mobId) {
        return mobIds.get(mobId);
    }

    public Integer getSetId(int itemId) {
        final ItemInformation i = getItemInformation(itemId);
        if (i == null) {
            return null;
        }
        return i.cardSet;
    }

    /**
     * returns the maximum of items in one slot
     *
     * @param itemId
     * @return
     */
    public final short getSlotMax(final int itemId) {
        final ItemInformation i = getItemInformation(itemId);
        if (i == null) {
            return 0;
        }
        return i.slotMax;
    }

    public final int getWholePrice(final int itemId) {
        final ItemInformation i = getItemInformation(itemId);
        if (i == null) {
            return 0;
        }
        return i.wholePrice;
    }

    public final double getPrice(final int itemId) {
        final ItemInformation i = getItemInformation(itemId);
        if (i == null) {
            return -1.0;
        }
        return i.price;
    }

    protected int rand(int min, int max) {
        return Math.abs((int) Randomizer.rand(min, max));
    }

    public Equip levelUpEquip(Equip equip, Map<String, Integer> sta) {
        Equip nEquip = (Equip) equip.copy();
        //is this all the stats?
        try {
            for (Entry<String, Integer> stat : sta.entrySet()) {
                switch (stat.getKey()) {
                    case "STRMin":
                        nEquip.setStr((short) (nEquip.getStr() + rand(stat.getValue().intValue(), sta.get("STRMax").intValue())));
                        break;
                    case "DEXMin":
                        nEquip.setDex((short) (nEquip.getDex() + rand(stat.getValue().intValue(), sta.get("DEXMax").intValue())));
                        break;
                    case "INTMin":
                        nEquip.setInt((short) (nEquip.getInt() + rand(stat.getValue().intValue(), sta.get("INTMax").intValue())));
                        break;
                    case "LUKMin":
                        nEquip.setLuk((short) (nEquip.getLuk() + rand(stat.getValue().intValue(), sta.get("LUKMax").intValue())));
                        break;
                    case "PADMin":
                        nEquip.setWatk((short) (nEquip.getWatk() + rand(stat.getValue().intValue(), sta.get("PADMax").intValue())));
                        break;
                    case "PDDMin":
                        nEquip.setWdef((short) (nEquip.getWdef() + rand(stat.getValue().intValue(), sta.get("PDDMax").intValue())));
                        break;
                    case "MADMin":
                        nEquip.setMatk((short) (nEquip.getMatk() + rand(stat.getValue().intValue(), sta.get("MADMax").intValue())));
                        break;
                    case "MDDMin":
                        nEquip.setMdef((short) (nEquip.getMdef() + rand(stat.getValue().intValue(), sta.get("MDDMax").intValue())));
                        break;
                    case "ACCMin":
                        nEquip.setAcc((short) (nEquip.getAcc() + rand(stat.getValue().intValue(), sta.get("ACCMax").intValue())));
                        break;
                    case "EVAMin":
                        nEquip.setAvoid((short) (nEquip.getAvoid() + rand(stat.getValue().intValue(), sta.get("EVAMax").intValue())));
                        break;
                    case "SpeedMin":
                        nEquip.setSpeed((short) (nEquip.getSpeed() + rand(stat.getValue().intValue(), sta.get("SpeedMax").intValue())));
                        break;
                    case "JumpMin":
                        nEquip.setJump((short) (nEquip.getJump() + rand(stat.getValue().intValue(), sta.get("JumpMax").intValue())));
                        break;
                    case "MHPMin":
                        nEquip.setHp((short) (nEquip.getHp() + rand(stat.getValue().intValue(), sta.get("MHPMax").intValue())));
                        break;
                    case "MMPMin":
                        nEquip.setMp((short) (nEquip.getMp() + rand(stat.getValue().intValue(), sta.get("MMPMax").intValue())));
                        break;
                    case "MaxHPMin":
                        nEquip.setHp((short) (nEquip.getHp() + rand(stat.getValue().intValue(), sta.get("MaxHPMax").intValue())));
                        break;
                    case "MaxMPMin":
                        nEquip.setMp((short) (nEquip.getMp() + rand(stat.getValue().intValue(), sta.get("MaxMPMax").intValue())));
                        break;
                }
            }
        } catch (NullPointerException e) {
        }
        return nEquip;
    }

    public final List<Triple<String, String, String>> getEquipAdditions(final int itemId) {
        final ItemInformation i = getItemInformation(itemId);
        if (i == null) {
            return null;
        }
        return i.equipAdditions;
    }

    public final String getEquipAddReqs(final int itemId, final String key, final String sub) {
        final ItemInformation i = getItemInformation(itemId);
        if (i == null) {
            return null;
        }
        for (Triple<String, String, String> data : i.equipAdditions) {
            if (data.getLeft().equals("key") && data.getMid().equals("con:" + sub)) {
                return data.getRight();
            }
        }
        return null;
    }

    public final Map<Integer, Map<String, Integer>> getEquipIncrements(final int itemId) {
        final ItemInformation i = getItemInformation(itemId);
        if (i == null) {
            return null;
        }
        return i.equipIncs;
    }

    public final List<Integer> getEquipSkills(final int itemId) {
        final ItemInformation i = getItemInformation(itemId);
        if (i == null) {
            return null;
        }
        return i.incSkill;
    }

    public final Map<String, Integer> getEquipStats(final int itemId) {
        final ItemInformation i = getItemInformation(itemId);
        if (i == null) {
            return null;
        }
        return i.equipStats;
    }

    public final boolean canEquip(final Map<String, Integer> stats, final int itemid, final int level, final int job, final int fame, final int str, final int dex, final int luk, final int int_, int supremacy, short reqLevel) {
        if (level + supremacy >= 0xFF) {
            supremacy = 0xFF - level;
        }
        if ((level + supremacy) >= (reqLevel != 0 ? reqLevel : stats.containsKey("reqLevel") ? stats.get("reqLevel") : 0) && str >= (stats.containsKey("reqSTR") ? stats.get("reqSTR") : 0) && dex >= (stats.containsKey("reqDEX") ? stats.get("reqDEX") : 0) && luk >= (stats.containsKey("reqLUK") ? stats.get("reqLUK") : 0) && int_ >= (stats.containsKey("reqINT") ? stats.get("reqINT") : 0)) {
            final Integer fameReq = stats.get("reqPOP");
            return fameReq == null || fame >= fameReq;
        }
        return false;
    }

    public final int getReqLevel(final int itemId) {
        if (getEquipStats(itemId) == null || !getEquipStats(itemId).containsKey("reqLevel")) {
            return 0;
        }
        return getEquipStats(itemId).get("reqLevel");
    }

    public final int getSlots(final int itemId) {
        if (getEquipStats(itemId) == null || !getEquipStats(itemId).containsKey("tuc")) {
            return 0;
        }
        return getEquipStats(itemId).get("tuc");
    }

    public final Integer getSetItemID(final int itemId) {
        if (getEquipStats(itemId) == null || !getEquipStats(itemId).containsKey("setItemID")) {
            return 0;
        }
        return getEquipStats(itemId).get("setItemID");
    }

    public final StructSetItem getSetItem(final int setItemId) {
        return setItems.get(setItemId);
    }

    public final List<Integer> getScrollReqs(final int itemId) {
        final ItemInformation i = getItemInformation(itemId);
        if (i == null) {
            return null;
        }
        return i.scrollReqs;
    }

    public final Item scrollEquipWithId(final Item equip, final Item scrollId, final boolean ws, final User chr, final int vegas) {
        final Equip nEquip = (Equip) equip;
        final Map<String, Integer> stats = getEquipStats(scrollId.getItemId());
        final Map<String, Integer> eqstats = getEquipStats(equip.getItemId());

        final int succRate, curseRate;
        if (GameConstants.isTablet(scrollId.getItemId())) {
            succRate = GameConstants.getSuccessTablet(scrollId.getItemId(), nEquip.getLevel());
            curseRate = GameConstants.getCurseTablet(scrollId.getItemId(), nEquip.getLevel());
        } else if (GameConstants.isEquipScroll(scrollId.getItemId()) || GameConstants.isSpecialScroll(scrollId.getItemId())) {
            succRate = 100;
            curseRate = 0;
        } else {
            succRate = stats.get("success");
            curseRate = stats.get("cursed");
        }

        final boolean isNonEquipmentSlotDeductableScroll = !GameConstants.isCleanSlate(scrollId.getItemId()) && !GameConstants.isSpecialScroll(scrollId.getItemId()) && !GameConstants.isEquipScroll(scrollId.getItemId());
        final int additionalSuccessRate = (ItemFlag.LUCKY_DAY.check(equip.getFlag()) ? 10 : 0) + (chr.getTrait(MapleTraitType.craft).getLevel() / 10);

        // Remove lucky day flag if any.
        if (ItemFlag.LUCKY_DAY.check(equip.getFlag()) && !GameConstants.isSpecialScroll(scrollId.getItemId())) {
            equip.modifyFlags(false, ItemFlag.LUCKY_DAY);
        }

        if (Randomizer.nextInt(100) <= succRate + additionalSuccessRate) {
            switch (scrollId.getItemId()) {
                case 2049000:
                case 2049001:
                case 2049002:
                case 2049003:
                case 2049004:
                case 2049005: {
                    if (eqstats.containsKey("tuc") && nEquip.getLevel() + nEquip.getUpgradeSlots() < eqstats.get("tuc")) {
                        nEquip.setUpgradeSlots((byte) (nEquip.getUpgradeSlots() + 1));
                    }
                    break;
                }
                case 2049006:
                case 2049007:
                case 2049008: {
                    if (eqstats.containsKey("tuc") && nEquip.getLevel() + nEquip.getUpgradeSlots() < eqstats.get("tuc")) {
                        nEquip.setUpgradeSlots((byte) (nEquip.getUpgradeSlots() + 2));
                    }
                    break;
                }
                case 2040727: { // Spikes on shoe, prevents slip
                    short flag = nEquip.getFlag();
                    flag |= ItemFlag.SPIKES.getValue();
                    nEquip.setFlag(flag);
                    break;
                }
                case 2041058: { // Cape for Cold protection
                    short flag = nEquip.getFlag();
                    flag |= ItemFlag.COLD.getValue();
                    nEquip.setFlag(flag);
                    break;
                }
                case 5063000:
                case 2530000:
                case 2530001: {
                    short flag = nEquip.getFlag();
                    flag |= ItemFlag.LUCKY_DAY.getValue();
                    nEquip.setFlag(flag);
                    break;
                }
                case 5064000:
                case 2531000: {
                    short flag = nEquip.getFlag();
                    flag |= ItemFlag.SHIELD_WARD.getValue();
                    nEquip.setFlag(flag);
                    break;
                }
                default: {
                    if (GameConstants.icsog(scrollId.getItemId())) {//Incredible chaos scroll of goodnes
                        final int z = GameConstants.getChaosNumber(scrollId.getItemId());
                        if (nEquip.getStr() > 0) {
                            nEquip.setStr((short) (nEquip.getStr() + Randomizer.nextInt(z)));
                        }
                        if (nEquip.getDex() > 0) {
                            nEquip.setDex((short) (nEquip.getDex() + Randomizer.nextInt(z)));
                        }
                        if (nEquip.getInt() > 0) {
                            nEquip.setInt((short) (nEquip.getInt() + Randomizer.nextInt(z)));
                        }
                        if (nEquip.getLuk() > 0) {
                            nEquip.setLuk((short) (nEquip.getLuk() + Randomizer.nextInt(z)));
                        }
                        if (nEquip.getWatk() > 0) {
                            nEquip.setWatk((short) (nEquip.getWatk() + Randomizer.nextInt(z)));
                        }
                        if (nEquip.getWdef() > 0) {
                            nEquip.setWdef((short) (nEquip.getWdef() + Randomizer.nextInt(z)));
                        }
                        if (nEquip.getMatk() > 0) {
                            nEquip.setMatk((short) (nEquip.getMatk() + Randomizer.nextInt(z)));
                        }
                        if (nEquip.getMdef() > 0) {
                            nEquip.setMdef((short) (nEquip.getMdef() + Randomizer.nextInt(z)));
                        }
                        if (nEquip.getAcc() > 0) {
                            nEquip.setAcc((short) (nEquip.getAcc() + Randomizer.nextInt(z)));
                        }
                        if (nEquip.getAvoid() > 0) {
                            nEquip.setAvoid((short) (nEquip.getAvoid() + Randomizer.nextInt(z)));
                        }
                        if (nEquip.getSpeed() > 0) {
                            nEquip.setSpeed((short) (nEquip.getSpeed() + Randomizer.nextInt(z)));
                        }
                        if (nEquip.getJump() > 0) {
                            nEquip.setJump((short) (nEquip.getJump() + Randomizer.nextInt(z)));
                        }
                        if (nEquip.getHp() > 0) {
                            nEquip.setHp((short) (nEquip.getHp() + Randomizer.nextInt(z)));
                        }
                        if (nEquip.getMp() > 0) {
                            nEquip.setMp((short) (nEquip.getMp() + Randomizer.nextInt(z)));
                        }
                    } else if (GameConstants.isChaosScroll(scrollId.getItemId())) {
                        final int z = GameConstants.getChaosNumber(scrollId.getItemId());
                        if (nEquip.getStr() > 0) {
                            nEquip.setStr((short) (nEquip.getStr() + Randomizer.nextInt(z) * (Randomizer.nextBoolean() ? 1 : -1)));
                        }
                        if (nEquip.getDex() > 0) {
                            nEquip.setDex((short) (nEquip.getDex() + Randomizer.nextInt(z) * (Randomizer.nextBoolean() ? 1 : -1)));
                        }
                        if (nEquip.getInt() > 0) {
                            nEquip.setInt((short) (nEquip.getInt() + Randomizer.nextInt(z) * (Randomizer.nextBoolean() ? 1 : -1)));
                        }
                        if (nEquip.getLuk() > 0) {
                            nEquip.setLuk((short) (nEquip.getLuk() + Randomizer.nextInt(z) * (Randomizer.nextBoolean() ? 1 : -1)));
                        }
                        if (nEquip.getWatk() > 0) {
                            nEquip.setWatk((short) (nEquip.getWatk() + Randomizer.nextInt(z) * (Randomizer.nextBoolean() ? 1 : -1)));
                        }
                        if (nEquip.getWdef() > 0) {
                            nEquip.setWdef((short) (nEquip.getWdef() + Randomizer.nextInt(z) * (Randomizer.nextBoolean() ? 1 : -1)));
                        }
                        if (nEquip.getMatk() > 0) {
                            nEquip.setMatk((short) (nEquip.getMatk() + Randomizer.nextInt(z) * (Randomizer.nextBoolean() ? 1 : -1)));
                        }
                        if (nEquip.getMdef() > 0) {
                            nEquip.setMdef((short) (nEquip.getMdef() + Randomizer.nextInt(z) * (Randomizer.nextBoolean() ? 1 : -1)));
                        }
                        if (nEquip.getAcc() > 0) {
                            nEquip.setAcc((short) (nEquip.getAcc() + Randomizer.nextInt(z) * (Randomizer.nextBoolean() ? 1 : -1)));
                        }
                        if (nEquip.getAvoid() > 0) {
                            nEquip.setAvoid((short) (nEquip.getAvoid() + Randomizer.nextInt(z) * (Randomizer.nextBoolean() ? 1 : -1)));
                        }
                        if (nEquip.getSpeed() > 0) {
                            nEquip.setSpeed((short) (nEquip.getSpeed() + Randomizer.nextInt(z) * (Randomizer.nextBoolean() ? 1 : -1)));
                        }
                        if (nEquip.getJump() > 0) {
                            nEquip.setJump((short) (nEquip.getJump() + Randomizer.nextInt(z) * (Randomizer.nextBoolean() ? 1 : -1)));
                        }
                        if (nEquip.getHp() > 0) {
                            nEquip.setHp((short) (nEquip.getHp() + Randomizer.nextInt(z) * (Randomizer.nextBoolean() ? 1 : -1)));
                        }
                        if (nEquip.getMp() > 0) {
                            nEquip.setMp((short) (nEquip.getMp() + Randomizer.nextInt(z) * (Randomizer.nextBoolean() ? 1 : -1)));
                        }
                        break;

                    } else if (GameConstants.isPotentialScroll(scrollId.getItemId())) {
                        final int chanc = Math.max((scrollId.getItemId() == 2049700 || scrollId.getItemId() == 2049702 || scrollId.getItemId() == 2049703 ? 100 : (scrollId.getItemId() == 2049708 ? 50 : scrollId.getItemId() == 2049705 ? 60 : 80)) - (nEquip.getEnhance() * 10), 10) + additionalSuccessRate;
                        if (Randomizer.nextInt(100) > chanc) {
                            return null;
                        }
                        for (int i = 0; i < (scrollId.getItemId() == 2049708 ? 5 : (scrollId.getItemId() == 2049705 ? 4 : (scrollId.getItemId() == 2049704 ? 3 : scrollId.getItemId() == 2049709 ? 2 : 1))); i++) {
                            if (nEquip.getStr() > 0 || Randomizer.nextInt(50) == 1) { //1/50
                                nEquip.setStr((short) (nEquip.getStr() + Randomizer.nextInt(5)));
                            }
                            if (nEquip.getStr() > 0 && (InventoryConstants.isTyrant(nEquip.getItemId()) || Randomizer.nextInt(1) == 1)) {
                                nEquip.setStr((short) (nEquip.getStr() + 22));
                            }
                            if (nEquip.getStr() > 0 && (InventoryConstants.isNovaGear(nEquip.getItemId()) || Randomizer.nextInt(1) == 1)) {
                                nEquip.setStr((short) (nEquip.getStr() + 12));
                            }
                            if (nEquip.getDex() > 0 || Randomizer.nextInt(50) == 1) { // 1/50
                                nEquip.setDex((short) (nEquip.getDex() + Randomizer.nextInt(5)));
                            }
                            if (nEquip.getDex() > 0 && (InventoryConstants.isTyrant(nEquip.getItemId()) || Randomizer.nextInt(1) == 1)) {
                                nEquip.setDex((short) (nEquip.getDex() + 22));
                            }
                            if (nEquip.getDex() > 0 && (InventoryConstants.isNovaGear(nEquip.getItemId()) || Randomizer.nextInt(1) == 1)) {
                                nEquip.setDex((short) (nEquip.getDex() + 12));
                            }
                            if (nEquip.getInt() > 0 || Randomizer.nextInt(50) == 1) { // 1/50
                                nEquip.setInt((short) (nEquip.getInt() + Randomizer.nextInt(5)));
                            }
                            if (nEquip.getInt() > 0 && (InventoryConstants.isTyrant(nEquip.getItemId()) || Randomizer.nextInt(1) == 1)) {
                                nEquip.setInt((short) (nEquip.getInt() + 22));
                            }
                            if (nEquip.getInt() > 0 && (InventoryConstants.isTyrant(nEquip.getItemId()) || Randomizer.nextInt(1) == 1)) {
                                nEquip.setInt((short) (nEquip.getInt() + 12));
                            }
                            if (nEquip.getLuk() > 0 || Randomizer.nextInt(50) == 1) { // 1/50
                                nEquip.setLuk((short) (nEquip.getLuk() + Randomizer.nextInt(5)));
                            }
                            if (nEquip.getLuk() > 0 && (InventoryConstants.isTyrant(nEquip.getItemId()) || Randomizer.nextInt(1) == 1)) {
                                nEquip.setLuk((short) (nEquip.getLuk() + 22));
                            }
                            if (nEquip.getLuk() > 0 && (InventoryConstants.isNovaGear(nEquip.getItemId()) || Randomizer.nextInt(1) == 1)) {
                                nEquip.setLuk((short) (nEquip.getLuk() + 12));
                            }
                            if (nEquip.getWatk() > 0 && InventoryConstants.isWeapon(nEquip.getItemId())) {
                                nEquip.setWatk((short) (nEquip.getWatk() + (nEquip.getWatk() / 50 + 1)));
                            }
                            if (nEquip.getWatk() > 0 && !InventoryConstants.isWeapon(nEquip.getItemId()) && Randomizer.nextInt(5) == 1 || Randomizer.nextInt(5) == 1 && !InventoryConstants.isWeapon(nEquip.getItemId())) {
                                nEquip.setWatk((short) (nEquip.getWatk() + Randomizer.nextInt(5)));
                            }
                            if (nEquip.getWatk() > 0 && InventoryConstants.isTyrant(nEquip.getItemId()) && nEquip.getEnhance() > 3) {
                                nEquip.setWatk((short) (nEquip.getWatk() + 5));
                            }
                            if (nEquip.getWatk() > 0 && InventoryConstants.isNovaGear(nEquip.getItemId()) && nEquip.getEnhance() > 5) {
                                nEquip.setWatk((short) (nEquip.getWatk() + 2));
                            }
                            if (nEquip.getWdef() > 0 || Randomizer.nextInt(40) == 1) { //1/40
                                nEquip.setWdef((short) (nEquip.getWdef() + Randomizer.nextInt(5)));
                            }
                            if (nEquip.getMatk() > 0 && InventoryConstants.isWeapon(nEquip.getItemId())) {
                                nEquip.setMatk((short) (nEquip.getMatk() + (nEquip.getMatk() / 50 + 1)));
                            }
                            if (nEquip.getMatk() > 0 && InventoryConstants.isTyrant(nEquip.getItemId()) && nEquip.getEnhance() > 3) {
                                nEquip.setMatk((short) (nEquip.getMatk() + 5));
                            }
                            if (nEquip.getMatk() > 0 && InventoryConstants.isNovaGear(nEquip.getItemId()) && nEquip.getEnhance() > 5) {
                                nEquip.setMatk((short) (nEquip.getMatk() + 2));
                            }
                            if (nEquip.getMdef() > 0 || Randomizer.nextInt(40) == 1) { //1/40
                                nEquip.setMdef((short) (nEquip.getMdef() + Randomizer.nextInt(5)));
                            }
                            if (nEquip.getAcc() > 0 || Randomizer.nextInt(20) == 1) { //1/20
                                nEquip.setAcc((short) (nEquip.getAcc() + Randomizer.nextInt(5)));
                            }
                            if (nEquip.getAvoid() > 0 || Randomizer.nextInt(20) == 1) { //1/20
                                nEquip.setAvoid((short) (nEquip.getAvoid() + Randomizer.nextInt(5)));
                            }
                            if (nEquip.getSpeed() > 0 || Randomizer.nextInt(10) == 1) { //1/10
                                nEquip.setSpeed((short) (nEquip.getSpeed() + Randomizer.nextInt(5)));
                            }
                            if (nEquip.getJump() > 0 || Randomizer.nextInt(10) == 1) { //1/10
                                nEquip.setJump((short) (nEquip.getJump() + Randomizer.nextInt(5)));
                            }
                            if (nEquip.getHp() > 0 || Randomizer.nextInt(5) == 1) { //1/5
                                nEquip.setHp((short) (nEquip.getHp() + Randomizer.nextInt(5)));
                            }
                            if (nEquip.getMp() > 0 || Randomizer.nextInt(5) == 1) { //1/5
                                nEquip.setMp((short) (nEquip.getMp() + Randomizer.nextInt(5)));
                            }
                            nEquip.setEnhance((byte) (nEquip.getEnhance() + 1));
                        }
                        break;

                    } else if (GameConstants.isEquipScroll(scrollId.getItemId())) {
                        final int chanc = Math.max((scrollId.getItemId() == 2049300 || scrollId.getItemId() == 2049303 || scrollId.getItemId() == 2049306 ? 100 : (scrollId.getItemId() == 2049308 ? 50 : scrollId.getItemId() == 2049305 ? 60 : 80)) - (nEquip.getEnhance() * 10), 10) + additionalSuccessRate;
                        if (Randomizer.nextInt(100) > chanc) {
                            return null; //destroyed, nib
                        }
                        for (int i = 0; i < (scrollId.getItemId() == 2049308 ? 5 : (scrollId.getItemId() == 2049305 ? 4 : (scrollId.getItemId() == 2049304 ? 3 : scrollId.getItemId() == 2049309 ? 2 : 1))); i++) {
                            if (nEquip.getStr() > 0 || Randomizer.nextInt(50) == 1) { //1/50
                                nEquip.setStr((short) (nEquip.getStr() + Randomizer.nextInt(5)));
                            }
                            if (nEquip.getStr() > 0 && (InventoryConstants.isTyrant(nEquip.getItemId()) || Randomizer.nextInt(1) == 1)) {
                                nEquip.setStr((short) (nEquip.getStr() + 22));
                            }
                            if (nEquip.getStr() > 0 && (InventoryConstants.isNovaGear(nEquip.getItemId()) || Randomizer.nextInt(1) == 1)) {
                                nEquip.setStr((short) (nEquip.getStr() + 12));
                            }
                            if (nEquip.getDex() > 0 || Randomizer.nextInt(50) == 1) { //1/50
                                nEquip.setDex((short) (nEquip.getDex() + Randomizer.nextInt(5)));
                            }
                            if (nEquip.getDex() > 0 && (InventoryConstants.isTyrant(nEquip.getItemId()) || Randomizer.nextInt(1) == 1)) {
                                nEquip.setDex((short) (nEquip.getDex() + 22));
                            }
                            if (nEquip.getDex() > 0 && (InventoryConstants.isNovaGear(nEquip.getItemId()) || Randomizer.nextInt(1) == 1)) {
                                nEquip.setDex((short) (nEquip.getDex() + 12));
                            }
                            if (nEquip.getInt() > 0 || Randomizer.nextInt(50) == 1) { //1/50
                                nEquip.setInt((short) (nEquip.getInt() + Randomizer.nextInt(5)));
                            }
                            if (nEquip.getInt() > 0 && (InventoryConstants.isTyrant(nEquip.getItemId()) || Randomizer.nextInt(1) == 1)) {
                                nEquip.setInt((short) (nEquip.getInt() + 22));
                            }
                            if (nEquip.getInt() > 0 && (InventoryConstants.isTyrant(nEquip.getItemId()) || Randomizer.nextInt(1) == 1)) {
                                nEquip.setInt((short) (nEquip.getInt() + 12));
                            }
                            if (nEquip.getLuk() > 0 || Randomizer.nextInt(50) == 1) { //1/50
                                nEquip.setLuk((short) (nEquip.getLuk() + Randomizer.nextInt(5)));
                            }
                            if (nEquip.getLuk() > 0 && (InventoryConstants.isTyrant(nEquip.getItemId()) || Randomizer.nextInt(1) == 1)) {
                                nEquip.setLuk((short) (nEquip.getLuk() + 22));
                            }
                            if (nEquip.getLuk() > 0 && (InventoryConstants.isNovaGear(nEquip.getItemId()) || Randomizer.nextInt(1) == 1)) {
                                nEquip.setLuk((short) (nEquip.getLuk() + 12));
                            }
                            if (nEquip.getWatk() > 0 && InventoryConstants.isWeapon(nEquip.getItemId())) {
                                nEquip.setWatk((short) (nEquip.getWatk() + (nEquip.getWatk() / 50 + 1)));
                            }
                            if (nEquip.getWatk() > 0 && !InventoryConstants.isWeapon(nEquip.getItemId()) && Randomizer.nextInt(5) == 1 || Randomizer.nextInt(5) == 1 && !InventoryConstants.isWeapon(nEquip.getItemId())) {
                                nEquip.setWatk((short) (nEquip.getWatk() + Randomizer.nextInt(5)));
                            }
                            if (nEquip.getWatk() > 0 && InventoryConstants.isTyrant(nEquip.getItemId()) && nEquip.getEnhance() > 3) {
                                nEquip.setWatk((short) (nEquip.getWatk() + 5));
                            }
                            if (nEquip.getWatk() > 0 && InventoryConstants.isNovaGear(nEquip.getItemId()) && nEquip.getEnhance() > 5) {
                                nEquip.setWatk((short) (nEquip.getWatk() + 2));
                            }
                            if (nEquip.getWdef() > 0 || Randomizer.nextInt(40) == 1) { //1/40
                                nEquip.setWdef((short) (nEquip.getWdef() + Randomizer.nextInt(5)));
                            }
                            if (nEquip.getMatk() > 0 && InventoryConstants.isWeapon(nEquip.getItemId())) {
                                nEquip.setMatk((short) (nEquip.getMatk() + (nEquip.getMatk() / 50 + 1)));
                            }
                            if (nEquip.getMatk() > 0 && InventoryConstants.isTyrant(nEquip.getItemId()) && nEquip.getEnhance() > 3) {
                                nEquip.setMatk((short) (nEquip.getMatk() + 5));
                            }
                            if (nEquip.getMatk() > 0 && InventoryConstants.isNovaGear(nEquip.getItemId()) && nEquip.getEnhance() > 5) {
                                nEquip.setMatk((short) (nEquip.getMatk() + 2));
                            }
                            if (nEquip.getMdef() > 0 || Randomizer.nextInt(40) == 1) { //1/40
                                nEquip.setMdef((short) (nEquip.getMdef() + Randomizer.nextInt(5)));
                            }
                            if (nEquip.getAcc() > 0 || Randomizer.nextInt(20) == 1) { //1/20
                                nEquip.setAcc((short) (nEquip.getAcc() + Randomizer.nextInt(5)));
                            }
                            if (nEquip.getAvoid() > 0 || Randomizer.nextInt(20) == 1) { //1/20
                                nEquip.setAvoid((short) (nEquip.getAvoid() + Randomizer.nextInt(5)));
                            }
                            if (nEquip.getSpeed() > 0 || Randomizer.nextInt(10) == 1) { //1/10
                                nEquip.setSpeed((short) (nEquip.getSpeed() + Randomizer.nextInt(5)));
                            }
                            if (nEquip.getJump() > 0 || Randomizer.nextInt(10) == 1) { //1/10
                                nEquip.setJump((short) (nEquip.getJump() + Randomizer.nextInt(5)));
                            }
                            if (nEquip.getHp() > 0 || Randomizer.nextInt(5) == 1) { //1/5
                                nEquip.setHp((short) (nEquip.getHp() + Randomizer.nextInt(5)));
                            }
                            if (nEquip.getMp() > 0 || Randomizer.nextInt(5) == 1) { //1/5
                                nEquip.setMp((short) (nEquip.getMp() + Randomizer.nextInt(5)));
                            }
                            nEquip.setEnhance((byte) (nEquip.getEnhance() + 1));
                        }
                        break;
                    } else {
                        for (Entry<String, Integer> stat : stats.entrySet()) {
                            final String key = stat.getKey();
                            switch (key) {
                                case "STR":
                                    nEquip.setStr((short) (nEquip.getStr() + stat.getValue()));
                                    break;
                                case "DEX":
                                    nEquip.setDex((short) (nEquip.getDex() + stat.getValue()));
                                    break;
                                case "INT":
                                    nEquip.setInt((short) (nEquip.getInt() + stat.getValue()));
                                    break;
                                case "LUK":
                                    nEquip.setLuk((short) (nEquip.getLuk() + stat.getValue()));
                                    break;
                                case "PAD":
                                    nEquip.setWatk((short) (nEquip.getWatk() + stat.getValue()));
                                    break;
                                case "PDD":
                                    nEquip.setWdef((short) (nEquip.getWdef() + stat.getValue()));
                                    break;
                                case "MAD":
                                    nEquip.setMatk((short) (nEquip.getMatk() + stat.getValue()));
                                    break;
                                case "MDD":
                                    nEquip.setMdef((short) (nEquip.getMdef() + stat.getValue()));
                                    break;
                                case "ACC":
                                    nEquip.setAcc((short) (nEquip.getAcc() + stat.getValue()));
                                    break;
                                case "EVA":
                                    nEquip.setAvoid((short) (nEquip.getAvoid() + stat.getValue()));
                                    break;
                                case "Speed":
                                    nEquip.setSpeed((short) (nEquip.getSpeed() + stat.getValue()));
                                    break;
                                case "Jump":
                                    nEquip.setJump((short) (nEquip.getJump() + stat.getValue()));
                                    break;
                                case "MHP":
                                    nEquip.setHp((short) (nEquip.getHp() + stat.getValue()));
                                    break;
                                case "MMP":
                                    nEquip.setMp((short) (nEquip.getMp() + stat.getValue()));
                                    break;
                            }
                        }
                        break;
                    }
                }
            }

            if (isNonEquipmentSlotDeductableScroll) {
                if (GameConstants.isAzwanScroll(scrollId.getItemId())) {
                    nEquip.setUpgradeSlots((byte) (nEquip.getUpgradeSlots() - stats.get("tuc")));
                } else {
                    nEquip.setUpgradeSlots((byte) (nEquip.getUpgradeSlots() - 1));
                }
                nEquip.setLevel((byte) (nEquip.getLevel() + 1));
            }
        } else {
            if (!ws && isNonEquipmentSlotDeductableScroll) {
                if (GameConstants.isAzwanScroll(scrollId.getItemId())) {
                    nEquip.setUpgradeSlots((byte) (nEquip.getUpgradeSlots() - stats.get("tuc")));
                } else {
                    nEquip.setUpgradeSlots((byte) (nEquip.getUpgradeSlots() - 1));
                }
            }
            if (Randomizer.nextInt(99) < curseRate) {
                return null;
            }
        }
        return equip;
    }

    public final Item getEquipById(final int equipId) {
        return getEquipById(equipId, -1);
    }

    public final Item getEquipById(final int equipId, final int ringId) {
        final ItemInformation i = getItemInformation(equipId);
        if (i == null) {
            return new Equip(equipId, (short) 0, ringId, (byte) 0);
        }
        final Item eq = i.eq.copy();
        eq.setUniqueId(ringId);
        return eq;
    }

    protected final short getRandStatFusion(final short defaultValue, final int value1, final int value2) {
        if (defaultValue == 0) {
            return 0;
        }
        final int range = ((value1 + value2) / 2) - defaultValue;
        final int rand = Randomizer.nextInt(Math.abs(range) + 1);
        return (short) (defaultValue + (range < 0 ? -rand : rand));
    }

    protected final short getRandStat(final short defaultValue, final int maxRange) {
        if (defaultValue == 0) {
            return 0;
        }
        // vary no more than ceil of 10% of stat
        final int lMaxRange = (int) Math.min(Math.ceil(defaultValue * 0.1), maxRange);

        return (short) ((defaultValue - lMaxRange) + Randomizer.nextInt(lMaxRange * 2 + 1));
    }

    protected final short getRandStatAbove(final short defaultValue, final int maxRange) {
        if (defaultValue <= 0) {
            return 0;
        }
        final int lMaxRange = (int) Math.min(Math.ceil(defaultValue * 0.1), maxRange);

        return (short) ((defaultValue) + Randomizer.nextInt(lMaxRange + 1));
    }

    public final Equip randomizeStats(final Equip equip) {
        equip.setStr(getRandStat(equip.getStr(), 5));
        equip.setDex(getRandStat(equip.getDex(), 5));
        equip.setInt(getRandStat(equip.getInt(), 5));
        equip.setLuk(getRandStat(equip.getLuk(), 5));
        equip.setMatk(getRandStat(equip.getMatk(), 5));
        equip.setWatk(getRandStat(equip.getWatk(), 5));
        equip.setAcc(getRandStat(equip.getAcc(), 5));
        equip.setAvoid(getRandStat(equip.getAvoid(), 5));
        equip.setJump(getRandStat(equip.getJump(), 5));
        equip.setHands(getRandStat(equip.getHands(), 5));
        equip.setSpeed(getRandStat(equip.getSpeed(), 5));
        equip.setWdef(getRandStat(equip.getWdef(), 10));
        equip.setMdef(getRandStat(equip.getMdef(), 10));
        equip.setHp(getRandStat(equip.getHp(), 10));
        equip.setMp(getRandStat(equip.getMp(), 10));
        return equip;
    }

    public final Equip randomizeStats_Above(final Equip equip) {
        equip.setStr(getRandStatAbove(equip.getStr(), 5));
        equip.setDex(getRandStatAbove(equip.getDex(), 5));
        equip.setInt(getRandStatAbove(equip.getInt(), 5));
        equip.setLuk(getRandStatAbove(equip.getLuk(), 5));
        equip.setMatk(getRandStatAbove(equip.getMatk(), 5));
        equip.setWatk(getRandStatAbove(equip.getWatk(), 5));
        equip.setAcc(getRandStatAbove(equip.getAcc(), 5));
        equip.setAvoid(getRandStatAbove(equip.getAvoid(), 5));
        equip.setJump(getRandStatAbove(equip.getJump(), 5));
        equip.setHands(getRandStatAbove(equip.getHands(), 5));
        equip.setSpeed(getRandStatAbove(equip.getSpeed(), 5));
        equip.setWdef(getRandStatAbove(equip.getWdef(), 10));
        equip.setMdef(getRandStatAbove(equip.getMdef(), 10));
        equip.setHp(getRandStatAbove(equip.getHp(), 10));
        equip.setMp(getRandStatAbove(equip.getMp(), 10));
        return equip;
    }

    public final Equip fuse(final Equip equip1, final Equip equip2) {
        if (equip1.getItemId() != equip2.getItemId()) {
            return equip1;
        }
        final Equip equip = (Equip) getEquipById(equip1.getItemId());
        equip.setStr(getRandStatFusion(equip.getStr(), equip1.getStr(), equip2.getStr()));
        equip.setDex(getRandStatFusion(equip.getDex(), equip1.getDex(), equip2.getDex()));
        equip.setInt(getRandStatFusion(equip.getInt(), equip1.getInt(), equip2.getInt()));
        equip.setLuk(getRandStatFusion(equip.getLuk(), equip1.getLuk(), equip2.getLuk()));
        equip.setMatk(getRandStatFusion(equip.getMatk(), equip1.getMatk(), equip2.getMatk()));
        equip.setWatk(getRandStatFusion(equip.getWatk(), equip1.getWatk(), equip2.getWatk()));
        equip.setAcc(getRandStatFusion(equip.getAcc(), equip1.getAcc(), equip2.getAcc()));
        equip.setAvoid(getRandStatFusion(equip.getAvoid(), equip1.getAvoid(), equip2.getAvoid()));
        equip.setJump(getRandStatFusion(equip.getJump(), equip1.getJump(), equip2.getJump()));
        equip.setHands(getRandStatFusion(equip.getHands(), equip1.getHands(), equip2.getHands()));
        equip.setSpeed(getRandStatFusion(equip.getSpeed(), equip1.getSpeed(), equip2.getSpeed()));
        equip.setWdef(getRandStatFusion(equip.getWdef(), equip1.getWdef(), equip2.getWdef()));
        equip.setMdef(getRandStatFusion(equip.getMdef(), equip1.getMdef(), equip2.getMdef()));
        equip.setHp(getRandStatFusion(equip.getHp(), equip1.getHp(), equip2.getHp()));
        equip.setMp(getRandStatFusion(equip.getMp(), equip1.getMp(), equip2.getMp()));
        return equip;
    }

    public final int getTotalStat(final Equip equip) { //i get COOL when my defense is higher on gms...
        return equip.getStr() + equip.getDex() + equip.getInt() + equip.getLuk() + equip.getMatk() + equip.getWatk() + equip.getAcc() + equip.getAvoid() + equip.getJump()
                + equip.getHands() + equip.getSpeed() + equip.getHp() + equip.getMp() + equip.getWdef() + equip.getMdef();
    }

    public final MapleStatEffect getItemEffect(final int itemId) {
        MapleStatEffect ret = itemEffects.get(itemId);
        if (ret == null) {
            final MapleData item = getItemData(itemId);
            if (item == null || item.getChildByPath("spec") == null) {
                return null;
            }
            ret = MapleStatEffect.loadItemEffectFromData(item.getChildByPath("spec"), itemId);
            itemEffects.put(itemId, ret);
        }
        return ret;
    }

    public final MapleStatEffect getItemEffectEX(final int itemId) {
        MapleStatEffect ret = itemEffectsEx.get(itemId);
        if (ret == null) {
            final MapleData item = getItemData(itemId);
            if (item == null || item.getChildByPath("specEx") == null) {
                return null;
            }
            ret = MapleStatEffect.loadItemEffectFromData(item.getChildByPath("specEx"), itemId);
            itemEffectsEx.put(itemId, ret);
        }
        return ret;
    }

    public final int getCreateId(final int id) {
        final ItemInformation i = getItemInformation(id);
        if (i == null) {
            return 0;
        }
        return i.create;
    }

    public final int getCardMobId(final int id) {
        final ItemInformation i = getItemInformation(id);
        if (i == null) {
            return 0;
        }
        return i.monsterBook;
    }

    public final int getBagType(final int id) {
        final ItemInformation i = getItemInformation(id);
        if (i == null) {
            return 0;
        }
        return i.flag & 0xF;
    }

    public int[][] getSummonMobs(int itemId) {
        MapleData data = getItemData(itemId);
        int theInt = data.getChildByPath("mob").getChildren().size();
        int[][] mobs2spawn = new int[theInt][2];
        for (int x = 0; x < theInt; x++) {
            mobs2spawn[x][0] = MapleDataTool.getIntConvert("mob/" + x + "/id", data);
            mobs2spawn[x][1] = MapleDataTool.getIntConvert("mob/" + x + "/prob", data);
        }
        return mobs2spawn;
    }

    public final int getWatkForProjectile(final int itemId) {
        final ItemInformation i = getItemInformation(itemId);
        if (i == null || i.equipStats == null || i.equipStats.get("incPAD") == null) {
            return 0;
        }
        return i.equipStats.get("incPAD");
    }

    public final boolean canScroll(final int scrollid, final int itemid) {
        return (scrollid / 100) % 100 == (itemid / 10000) % 100;
    }

    public final String getName(final int itemId) {
        final ItemInformation i = getItemInformation(itemId);
        if (i == null) {
            return null;
        }
        return i.name;
    }

    public final String getDesc(final int itemId) {
        final ItemInformation i = getItemInformation(itemId);
        if (i == null) {
            return null;
        }
        return i.desc;
    }

    public final String getMsg(final int itemId) {
        final ItemInformation i = getItemInformation(itemId);
        if (i == null) {
            return null;
        }
        return i.msg;
    }

    public final short getItemMakeLevel(final int itemId) {
        final ItemInformation i = getItemInformation(itemId);
        if (i == null) {
            return 0;
        }
        return i.itemMakeLevel;
    }

    public final boolean isDropRestricted(final int itemId) {
        final ItemInformation i = getItemInformation(itemId);
        if (i == null) {
            return false;
        }
        return ((i.flag & 0x200) != 0 || (i.flag & 0x400) != 0 || GameConstants.isDropRestricted(itemId)) && (itemId == 3012000 || itemId == 3012015 || itemId / 10000 != 301) && itemId != 2041200 && itemId != 5640000 && itemId != 4170023 && itemId != 2040124 && itemId != 2040125 && itemId != 2040126 && itemId != 2040211 && itemId != 2040212 && itemId != 2040227 && itemId != 2040228 && itemId != 2040229 && itemId != 2040230 && itemId != 1002926 && itemId != 1002906 && itemId != 1002927;
    }

    public final boolean isPickupRestricted(final int itemId) {
        final ItemInformation i = getItemInformation(itemId);
        if (i == null) {
            return false;
        }
        return ((i.flag & 0x80) != 0 || GameConstants.isPickupRestricted(itemId)) && itemId != 4001168 && itemId != 4031306 && itemId != 4031307;
    }

    public final boolean isAccountShared(final int itemId) {
        final ItemInformation i = getItemInformation(itemId);
        if (i == null) {
            return false;
        }
        return (i.flag & 0x100) != 0;
    }

    public final int getStateChangeItem(final int itemId) {
        final ItemInformation i = getItemInformation(itemId);
        if (i == null) {
            return 0;
        }
        return i.stateChange;
    }

    public final int getMeso(final int itemId) {
        final ItemInformation i = getItemInformation(itemId);
        if (i == null) {
            return 0;
        }
        return i.meso;
    }

    public final boolean isShareTagEnabled(final int itemId) {
        final ItemInformation i = getItemInformation(itemId);
        if (i == null) {
            return false;
        }
        return (i.flag & 0x800) != 0;
    }

    public final boolean isKarmaEnabled(final int itemId) {
        final ItemInformation i = getItemInformation(itemId);
        if (i == null) {
            return false;
        }
        return i.karmaEnabled == 1;
    }

    public final boolean isPKarmaEnabled(final int itemId) {
        final ItemInformation i = getItemInformation(itemId);
        if (i == null) {
            return false;
        }
        return i.karmaEnabled == 2;
    }

    public final boolean isPickupBlocked(final int itemId) {
        final ItemInformation i = getItemInformation(itemId);
        if (i == null) {
            return false;
        }
        return (i.flag & 0x40) != 0;
    }

    public final boolean isLogoutExpire(final int itemId) {
        final ItemInformation i = getItemInformation(itemId);
        if (i == null) {
            return false;
        }
        return (i.flag & 0x20) != 0;
    }

    public final boolean cantSell(final int itemId) { //true = cant sell, false = can sell
        final ItemInformation i = getItemInformation(itemId);
        if (i == null) {
            return false;
        }
        return (i.flag & 0x10) != 0;
    }

    public final Pair<Integer, List<StructRewardItem>> getRewardItem(final int itemid) {
        final ItemInformation i = getItemInformation(itemid);
        if (i == null) {
            return null;
        }
        return new Pair<>(i.totalprob, i.rewardItems);
    }

    public final boolean isMobHP(final int itemId) {
        final ItemInformation i = getItemInformation(itemId);
        if (i == null) {
            return false;
        }
        return (i.flag & 0x1000) != 0;
    }

    public final boolean isQuestItem(final int itemId) {
        final ItemInformation i = getItemInformation(itemId);
        if (i == null) {
            return false;
        }
        return (i.flag & 0x200) != 0 && itemId / 10000 != 301;
    }

    public final Pair<Integer, List<Integer>> questItemInfo(final int itemId) {
        final ItemInformation i = getItemInformation(itemId);
        if (i == null) {
            return null;
        }
        return new Pair<>(i.questId, i.questItems);
    }

    public final Pair<Integer, String> replaceItemInfo(final int itemId) {
        final ItemInformation i = getItemInformation(itemId);
        if (i == null) {
            return null;
        }
        return new Pair<>(i.replaceItem, i.replaceMsg);
    }

    public final List<Triple<String, Point, Point>> getAfterImage(final String after) {
        return afterImage.get(after);
    }

    public final String getAfterImage(final int itemId) {
        final ItemInformation i = getItemInformation(itemId);
        if (i == null) {
            return null;
        }
        return i.afterImage;
    }

    public final boolean itemExists(final int itemId) {
        if (GameConstants.getInventoryType(itemId) == MapleInventoryType.UNDEFINED) {
            return false;
        }
        return getItemInformation(itemId) != null;
    }

    public boolean isSuperior(int itemId) {
        return getEquipStats(itemId).containsKey("superiorEqp");
    }

    public final boolean isCash(final int itemId) {
        if (getEquipStats(itemId) == null) {
            return GameConstants.getInventoryType(itemId) == MapleInventoryType.CASH;
        }
        return GameConstants.getInventoryType(itemId) == MapleInventoryType.CASH || getEquipStats(itemId).get("cash") != null;
    }

    public final ItemInformation getItemInformation(final int itemId) {
        if (itemId <= 0) {
            return null;
        }
        return dataCache.get(itemId);
    }

    private ItemInformation tmpInfo = null;

    public void initItemRewardData(ResultSet sqlRewardData) throws SQLException {
        final int itemID = sqlRewardData.getInt("itemid");
        if (tmpInfo == null || tmpInfo.itemId != itemID) {
            if (!dataCache.containsKey(itemID)) {
                System.out.println("[initItemRewardData] Tried to load an item while this is not in the cache: " + itemID);
                return;
            }
            tmpInfo = dataCache.get(itemID);
        }

        if (tmpInfo.rewardItems == null) {
            tmpInfo.rewardItems = new ArrayList<>();
        }

        StructRewardItem add = new StructRewardItem();
        add.itemid = sqlRewardData.getInt("item");
        add.period = (add.itemid == 1122017 ? Math.max(sqlRewardData.getInt("period"), 7200) : sqlRewardData.getInt("period"));
        add.prob = sqlRewardData.getInt("prob");
        add.quantity = sqlRewardData.getShort("quantity");
        add.worldmsg = sqlRewardData.getString("worldMsg").length() <= 0 ? null : sqlRewardData.getString("worldMsg");
        add.effect = sqlRewardData.getString("effect");

        tmpInfo.rewardItems.add(add);
    }

    public void initItemAddData(ResultSet sqlAddData) throws SQLException {
        final int itemID = sqlAddData.getInt("itemid");
        if (tmpInfo == null || tmpInfo.itemId != itemID) {
            if (!dataCache.containsKey(itemID)) {
                System.out.println("[initItemAddData] Tried to load an item while this is not in the cache: " + itemID);
                return;
            }
            tmpInfo = dataCache.get(itemID);
        }

        if (tmpInfo.equipAdditions == null) {
            tmpInfo.equipAdditions = new LinkedList<>();
        }

        while (sqlAddData.next()) {
            tmpInfo.equipAdditions.add(new Triple<>(sqlAddData.getString("key"), sqlAddData.getString("subKey"), sqlAddData.getString("value")));
        }
    }

    public void initItemEquipData(ResultSet sqlEquipData) throws SQLException {
        final int itemID = sqlEquipData.getInt("itemid");
        if (tmpInfo == null || tmpInfo.itemId != itemID) {
            if (!dataCache.containsKey(itemID)) {
                System.out.println("[initItemEquipData] Tried to load an item while this is not in the cache: " + itemID);
                return;
            }
            tmpInfo = dataCache.get(itemID);
        }

        if (tmpInfo.equipStats == null) {
            tmpInfo.equipStats = new HashMap<>();
        }

        final int itemLevel = sqlEquipData.getInt("itemLevel");
        if (itemLevel == -1) {
            tmpInfo.equipStats.put(sqlEquipData.getString("key"), sqlEquipData.getInt("value"));
        } else {
            if (tmpInfo.equipIncs == null) {
                tmpInfo.equipIncs = new HashMap<>();
            }

            Map<String, Integer> toAdd = tmpInfo.equipIncs.get(itemLevel);
            if (toAdd == null) {
                toAdd = new HashMap<>();
                tmpInfo.equipIncs.put(itemLevel, toAdd);
            }
            toAdd.put(sqlEquipData.getString("key"), sqlEquipData.getInt("value"));
        }
    }

    public void initItemInformation(ResultSet sqlItemData) throws SQLException {
        final ItemInformation ret = new ItemInformation();
        final int itemId = sqlItemData.getInt("itemid");

        ret.itemId = itemId;
        ret.slotMax = GameConstants.getSlotMax(itemId) > 0 ? GameConstants.getSlotMax(itemId) : sqlItemData.getShort("slotMax");
        ret.price = Double.parseDouble(sqlItemData.getString("price"));
        ret.wholePrice = sqlItemData.getInt("wholePrice");
        ret.stateChange = sqlItemData.getInt("stateChange");
        ret.name = sqlItemData.getString("name");
        ret.desc = sqlItemData.getString("desc");
        ret.msg = sqlItemData.getString("msg");

        ret.flag = sqlItemData.getInt("flags");

        ret.karmaEnabled = sqlItemData.getByte("karma");
        ret.meso = sqlItemData.getInt("meso");
        ret.monsterBook = sqlItemData.getInt("monsterBook");
        ret.itemMakeLevel = sqlItemData.getShort("itemMakeLevel");
        ret.questId = sqlItemData.getInt("questId");
        ret.create = sqlItemData.getInt("create");
        ret.replaceItem = sqlItemData.getInt("replaceId");
        ret.replaceMsg = sqlItemData.getString("replaceMsg");
        ret.afterImage = sqlItemData.getString("afterImage");
        ret.cardSet = 0;
        if (ret.monsterBook > 0 && itemId / 10000 == 238) {
            mobIds.put(ret.monsterBook, itemId);
            for (Entry<Integer, Triple<Integer, List<Integer>, List<Integer>>> set : monsterBookSets.entrySet()) {
                if (set.getValue().mid.contains(itemId)) {
                    ret.cardSet = set.getKey();
                    break;
                }
            }
        }

        final String scrollRq = sqlItemData.getString("scrollReqs");
        if (scrollRq.length() > 0) {
            ret.scrollReqs = new ArrayList<>();
            final String[] scroll = scrollRq.split(",");
            for (String s : scroll) {
                if (s.length() > 1) {
                    ret.scrollReqs.add(Integer.parseInt(s));
                }
            }
        }
        final String consumeItem = sqlItemData.getString("consumeItem");
        if (consumeItem.length() > 0) {
            ret.questItems = new ArrayList<>();
            final String[] scroll = scrollRq.split(",");
            for (String s : scroll) {
                if (s.length() > 1) {
                    ret.questItems.add(Integer.parseInt(s));
                }
            }
        }

        ret.totalprob = sqlItemData.getInt("totalprob");
        final String incRq = sqlItemData.getString("incSkill");
        if (incRq.length() > 0) {
            ret.incSkill = new ArrayList<>();
            final String[] scroll = incRq.split(",");
            for (String s : scroll) {
                if (s.length() > 1) {
                    ret.incSkill.add(Integer.parseInt(s));
                }
            }
        }
        dataCache.put(itemId, ret);
    }

    public boolean isEquip(int itemId) {
        return itemId / 1000000 == 1;
    }

    public final FarmItemInformation getFarmItemInformation(final int itemId) {
        if (itemId <= 0) {
            return null;
        }
        return farmDataCache.get(itemId);
    }

    public void initFarmItemInformation(ResultSet sqlItemData) throws SQLException {
        final FarmItemInformation ret = new FarmItemInformation();
        final int itemId = sqlItemData.getInt("itemid");
        ret.itemId = itemId;
        ret.slotMax = GameConstants.getSlotMax(itemId) > 0 ? GameConstants.getSlotMax(itemId) : sqlItemData.getShort("slotMax");
        ret.price = Double.parseDouble(sqlItemData.getString("price"));
        ret.wholePrice = sqlItemData.getInt("wholePrice");
        ret.stateChange = sqlItemData.getInt("stateChange");
        ret.name = sqlItemData.getString("name");
        ret.desc = sqlItemData.getString("desc");
        ret.msg = sqlItemData.getString("msg");

        ret.flag = sqlItemData.getInt("flags");

        ret.karmaEnabled = sqlItemData.getByte("karma");
        ret.meso = sqlItemData.getInt("meso");
        ret.monsterBook = sqlItemData.getInt("monsterBook");
        ret.itemMakeLevel = sqlItemData.getShort("itemMakeLevel");
        ret.questId = sqlItemData.getInt("questId");
        ret.create = sqlItemData.getInt("create");
        ret.replaceItem = sqlItemData.getInt("replaceId");
        ret.replaceMsg = sqlItemData.getString("replaceMsg");
        ret.afterImage = sqlItemData.getString("afterImage");
        ret.cardSet = 0;
        if (ret.monsterBook > 0 && itemId / 10000 == 238) {
            mobIds.put(ret.monsterBook, itemId);
            for (Entry<Integer, Triple<Integer, List<Integer>, List<Integer>>> set : monsterBookSets.entrySet()) {
                if (set.getValue().mid.contains(itemId)) {
                    ret.cardSet = set.getKey();
                    break;
                }
            }
        }

        final String scrollRq = sqlItemData.getString("scrollReqs");
        if (scrollRq.length() > 0) {
            ret.scrollReqs = new ArrayList<>();
            final String[] scroll = scrollRq.split(",");
            for (String s : scroll) {
                if (s.length() > 1) {
                    ret.scrollReqs.add(Integer.parseInt(s));
                }
            }
        }
        final String consumeItem = sqlItemData.getString("consumeItem");
        if (consumeItem.length() > 0) {
            ret.questItems = new ArrayList<>();
            final String[] scroll = scrollRq.split(",");
            for (String s : scroll) {
                if (s.length() > 1) {
                    ret.questItems.add(Integer.parseInt(s));
                }
            }
        }

        ret.totalprob = sqlItemData.getInt("totalprob");
        final String incRq = sqlItemData.getString("incSkill");
        if (incRq.length() > 0) {
            ret.incSkill = new ArrayList<>();
            final String[] scroll = incRq.split(",");
            for (String s : scroll) {
                if (s.length() > 1) {
                    ret.incSkill.add(Integer.parseInt(s));
                }
            }
        }
        farmDataCache.put(itemId, ret);
    }

    public static MapleInventoryType getInventoryType(final int itemId) {
        final byte type = (byte) (itemId / 1000000);
        if (type < 1 || type > 5) {
            return MapleInventoryType.UNDEFINED;
        }
        return MapleInventoryType.getByType(type);
    }

    public final boolean isOnlyTradeBlock(final int itemId) {
        final MapleData data = getItemData(itemId);
        boolean tradeblock = false;
        if (MapleDataTool.getIntConvert("info/tradeBlock", data, 0) == 1) {
            tradeblock = true;
        }
        return tradeblock;
    }

    // <editor-fold defaultstate="visible" desc="Equipment slot type"> 
    public EquipSlotType getSlotType(final int itemid) {
        return equipValidSlotCheck.get(itemid);
    }

    /**
     * methodology : Assign a new enum only if an equipment uses its own slot.
     *
     * @param slot_wz
     * @param itemid
     * @return
     */
    private EquipSlotType getSlotType_Enum(final String slot_wz, final int itemid) {
        switch (slot_wz) {
            case "Wp":
                return EquipSlotType.Weapon;
            case "WpSi":
                return EquipSlotType.Weapon_TakingBothSlot_Shield;
            case "Po":
                if (ItemConstants.isTotem(itemid)) {
                    return EquipSlotType.Totem;
                }
                return EquipSlotType.Accessary_Pocket;
            case "Af":
                return EquipSlotType.Accessary_Face;
            case "Ay":
                return EquipSlotType.Accessary_Eye;
            case "Me":
                return EquipSlotType.Medal;
            case "Be":
                return EquipSlotType.Belt;
            case "Pe":
                return EquipSlotType.Pendant;
            case "Ae":
                return EquipSlotType.Earring;
            case "Sh":
                return EquipSlotType.Shoulder;
            case "Cp":
                return EquipSlotType.Cap;
            case "Sr":
                return EquipSlotType.Cape;
            case "Ma":
                return EquipSlotType.Coat;
            case "Gv":
                return EquipSlotType.Glove;
            case "MaPn":
                return EquipSlotType.Longcoat;
            case "Pn":
                return EquipSlotType.Pants;
            case "Ri":
                return EquipSlotType.Ring;
            case "Si":
                if (ItemConstants.isEmblem(itemid)) {
                    return EquipSlotType.Si_Emblem;
                }
                return EquipSlotType.Shield_OrDualBlade;
            case "So":
                return EquipSlotType.Shoes;
            case "Bi":
                return EquipSlotType.Bits;
            case "Mb":
                return EquipSlotType.MonsterBook;
            case "Ba":
                return EquipSlotType.Badge;
            case "HrCp":
                return EquipSlotType.CashCap;
            case "Tm":
                final int div = itemid / 1000;

                switch (div) {
                    case 1652: // 01652000.img
                        return EquipSlotType.TamingMob_MechanicTransistor;
                    case 1642:
                        return EquipSlotType.TamingMob_MechanicFrame;
                    case 1632:
                        return EquipSlotType.TamingMob_MechanicLeg;
                    case 1622:
                        return EquipSlotType.TamingMob_MechanicArm;
                    case 1612:
                        return EquipSlotType.TamingMob_MechanicEngine;
                    case 1662:
                        return EquipSlotType.TamingMob_Android;
                    case 1672:
                        return EquipSlotType.TamingMob_AndroidHeart;
                    case 1942:
                        return EquipSlotType.TamingMob_DragonHat;
                    case 1952:
                        return EquipSlotType.TamingMob_DragonPendant;
                    case 1962:
                        return EquipSlotType.TamingMob_DragonWingAccessory;
                    case 1972:
                        return EquipSlotType.TamingMob_DragonTailAccessory;
                }
                return EquipSlotType.TamingMob;
            case "Sd":
                return EquipSlotType.TamingMob_Saddle;
            default:
                switch (itemid) {
                    case 1812001: // item pounch
                        return EquipSlotType.Pet_ItemPounch;
                    case 1812000: // Meso Magnet
                        return EquipSlotType.Pet_MesoMagnet;
                }

                if (!slot_wz.equals("")) {
                    System.out.println("[MapleItemInformationProvider] New equipment slot found. Name = '" + slot_wz + "', itemid: '" + itemid + "'.");
                }
                return EquipSlotType.UNKNOWN;
        }
    }
    // </editor-fold> 
}
