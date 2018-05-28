package server.life;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import client.inventory.MapleInventoryType;
import constants.GameConstants;
import constants.ServerConstants;
import database.Database;
import java.util.Arrays;
import server.MapleItemInformationProvider;
import server.MapleFamiliar;
import server.MapleStringInformationProvider;
import server.Randomizer;
import tools.LogHelper;
import tools.Pair;

public class MonsterInformationProvider {

    /**
     * bLoadFromFile
     * @purpose If enabled, drops will be loaded from the "monsterDrops.txt" file, rather than the database.
     */
    private static final boolean bLoadFromFile = true;
    private static final boolean bLoadFromDatabase = true;
    
    private static final MonsterInformationProvider instance = new MonsterInformationProvider();
    private final Map<Integer, ArrayList<MonsterDropEntry>> drops = new HashMap<>();
    private final List<MonsterGlobalDropEntry> globaldrops = new ArrayList<>();

    public static MonsterInformationProvider getInstance() {
        return instance;
    }

    public List<MonsterGlobalDropEntry> getGlobalDrop() {
        return globaldrops;
    }

    public List<MonsterDropEntry> retrieveDrop(int monsterId) {
        return drops.get(monsterId);
    }

    public void load() {

        if (bLoadFromDatabase) {
            try (Connection con = Database.GetConnection()) {
                try (PreparedStatement ps = con.prepareStatement("SELECT * FROM drop_data_global WHERE chance > 0")) {
                    ResultSet rs = ps.executeQuery();

                    while (rs.next()) {
                        globaldrops.add(
                                new MonsterGlobalDropEntry(
                                        rs.getInt("itemid"),
                                        rs.getInt("chance"),
                                        rs.getInt("continent"),
                                        rs.getByte("dropType"),
                                        rs.getInt("minimum_quantity"),
                                        rs.getInt("maximum_quantity"),
                                        rs.getInt("questid")));
                    }
                    rs.close();
                } catch (SQLException e) {
                    LogHelper.SQL.get().info("[SQL] There was an issue with something from the database:\n", e);
                }

                try (PreparedStatement ps = con.prepareStatement("SELECT dropperid FROM drop_data")) {
                    List<Integer> mobIds = new ArrayList<>();
                    ResultSet rs = ps.executeQuery();
                    while (rs.next()) {
                        if (!mobIds.contains(rs.getInt("dropperid"))) {
                            loadDrop(rs.getInt("dropperid"));
                            mobIds.add(rs.getInt("dropperid"));
                        }
                    }
                    rs.close();
                } catch (SQLException e) {
                    LogHelper.SQL.get().info("[SQL] There was an issue with something from the database:\n", e);
                }
            } catch (SQLException e) {
                LogHelper.SQL.get().info("[SQL] There was an issue with something from the database:\n", e);
            }
            loadCustomLevelDrops();
        } 
        
        if (bLoadFromFile) {
            OnLoadMonsterDrops();
        }

        loadCustom();
        System.out.println(String.format("[Info] Loaded %d Drops.", drops.size()));
    }

    /**
     * OnLoadMonsterDrops
     * @author aa
     * @author Mazen Massoud
     *
     * @purpose Retrieve monster drop data from the "monsterDrops.txt" file. 
     */
    public void OnLoadMonsterDrops() {
        try (BufferedReader buffRead = new BufferedReader(new FileReader("monsterDrops.txt"))) {
            String sLine;
            while ((sLine = buffRead.readLine()) != null) {
                String nMobID = null;
                String nDropID;
                String nDropChance;
                String nMinQuantity;
                String nMaxQuantity;
                String nRequiredQuestID;
                if (!sLine.contains(" ") && sLine.length() > 0) {
                    nMobID = sLine;
                    sLine = buffRead.readLine();
                }

                ArrayList<MonsterDropEntry> aMonsterDropData = new ArrayList<>();

                while(sLine.length() > 0) {
                    while (nMobID != null) {
                        if (sLine.contains(" ")) {

                            String[] dropData = sLine.split(" ");
                            nDropID = dropData[0];
                            nDropChance = dropData[1];
                            nMinQuantity = dropData[2];
                            nMaxQuantity = dropData[3];
                            nRequiredQuestID = dropData[4];

                            int nRawDropChance = (int) (Integer.parseInt(nDropChance) * (1000 / ServerConstants.DROP_RATE));
                            if (bLoadFromDatabase) nRawDropChance /= 1.5;

                            if (!ServerConstants.REDUCED_DEBUG_SPAM) System.err.printf("%s, %s, %s, %s, %s, %s \n", Integer.parseInt(nMobID), Integer.parseInt(nDropID), nRawDropChance, Integer.parseInt(nMinQuantity), Integer.parseInt(nMaxQuantity), Integer.parseInt(nRequiredQuestID));

                            aMonsterDropData.add(new MonsterDropEntry(Integer.parseInt(nDropID), nRawDropChance, Integer.parseInt(nMinQuantity), Integer.parseInt(nMaxQuantity), Integer.parseInt(nRequiredQuestID)));
                            sLine = buffRead.readLine();
                        } else {
                            break;
                        }
                    }
                    drops.put(Integer.parseInt(nMobID), aMonsterDropData);
                    nMobID = null;
                    sLine = "";
                }
                if (sLine.isEmpty() || sLine.contains("\n")) {
                    //do nothing
                }
            }
            buffRead.close();
        } catch (Exception e) {}
    }

    /**
     * loadDrop
     *
     * @purpose SQL method to load drop data.
     * @param monsterId
     */
    private void loadDrop(int monsterId) {
        final ArrayList<MonsterDropEntry> ret = new ArrayList<>();

        PreparedStatement ps = null;
        ResultSet rs = null;
        try (Connection con = Database.GetConnection()) {

            final MonsterStats mons = LifeFactory.getMonsterStats(monsterId);
            if (mons == null) {
                return;
            }
            ps = con.prepareStatement("SELECT * FROM drop_data WHERE dropperid = ?");
            ps.setInt(1, monsterId);
            rs = ps.executeQuery();
            int itemid;
            int chance;
            boolean doneMesos = false;
            while (rs.next()) {
                itemid = rs.getInt("itemid");
                chance = rs.getInt("chance");
                if (GameConstants.getInventoryType(itemid) == MapleInventoryType.EQUIP) {
                    chance *= 10; //in GMS/SEA it was raised
                }
                if (bLoadFromFile) chance /= 3; // Lower drop chance if we're using both our drop tables.
                ret.add(new MonsterDropEntry(
                        itemid,
                        chance,
                        rs.getInt("minimum_quantity"),
                        rs.getInt("maximum_quantity"),
                        rs.getInt("questid")));
                if (itemid == 0) {
                    doneMesos = true;
                }
            }
            if (!doneMesos) {
                addMeso(mons, ret);
            }

        } catch (SQLException e) {
            LogHelper.SQL.get().info("[SQL] There was an issue with something from the database:\n", e);
        } finally {

            try {
                if (ps != null) {
                    ps.close();
                }
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e) {
                LogHelper.SQL.get().info("[SQL] There was an issue with something from the database:\n", e);
                return;
            }
        }
        drops.put(monsterId, ret);
    }

    public void loadCustom() {
        globaldrops.add(new MonsterGlobalDropEntry(2435902, (int) (0.075 * 10000), -1, (byte) 0, 1, 1, 0)); // Nodes
        globaldrops.add(new MonsterGlobalDropEntry(5062009, (int) (0.4 * 10000), -1, (byte) 0, 1, 1, 0)); // Red cube
        globaldrops.add(new MonsterGlobalDropEntry(4001832, (int) (0.4 * 10000), -1, (byte) 0, 1, 1, 0)); // Spell trace
        globaldrops.add(new MonsterGlobalDropEntry(2049700, (int) (0.3 * 10000), -1, (byte) 0, 1, 1, 0)); // Epic potential scroll scroll
    }

    public void addExtra() {
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        for (Entry<Integer, ArrayList<MonsterDropEntry>> e : drops.entrySet()) {
            final MonsterStats mons = LifeFactory.getMonsterStats(e.getKey());
            Integer item = ii.getItemIdByMob(e.getKey());
            if (item != null && item > 0) {
                e.getValue().add(new MonsterDropEntry(item, mons.isBoss() ? 1000000 : 10000, 1, 1, 0));
            }
            MapleFamiliar f = ii.getFamiliarByMob(e.getKey());
            if (f != null) {
                e.getValue().add(new MonsterDropEntry(f.getItemid(), mons.isBoss() ? 10000 : 100, 1, 1, 0));
            }
        }
        for (Entry<Integer, Integer> i : ii.getMonsterBook().entrySet()) {
            if (!drops.containsKey(i.getKey())) {
                final MonsterStats mons = LifeFactory.getMonsterStats(i.getKey());
                ArrayList<MonsterDropEntry> e = new ArrayList<>();
                e.add(new MonsterDropEntry(i.getValue(), mons.isBoss() ? 1000000 : 10000, 1, 1, 0));
                MapleFamiliar f = ii.getFamiliarByMob(i.getKey());
                if (f != null) {
                    e.add(new MonsterDropEntry(f.getItemid(), mons.isBoss() ? 10000 : 100, 1, 1, 0));
                }
                addMeso(mons, e);
                drops.put(i.getKey(), e);
            }
        }
        for (MapleFamiliar f : ii.getFamiliars().values()) {
            if (!drops.containsKey(f.getMob())) {
                MonsterStats mons = LifeFactory.getMonsterStats(f.getMob());
                ArrayList<MonsterDropEntry> e = new ArrayList<>();
                e.add(new MonsterDropEntry(f.getItemid(), mons.isBoss() ? 10000 : 100, 1, 1, 0));
                addMeso(mons, e);
                drops.put(f.getMob(), e);
            }
        }
    }

    public void addMeso(MonsterStats mons, ArrayList<MonsterDropEntry> ret) {
        final double divided = (mons.getLevel() < 100 ? (mons.getLevel() < 10 ? (double) mons.getLevel() : 10.0) : (mons.getLevel() / 10.0));
        final int max = mons.isBoss() && !mons.isPartyBonus() ? (mons.getLevel() * mons.getLevel()) : (mons.getLevel() * (int) Math.ceil(mons.getLevel() / divided));
        for (int i = 0; i < mons.dropsMeso(); i++) {
            ret.add(new MonsterDropEntry(0, mons.isBoss() && !mons.isPartyBonus() ? 1000000 : (mons.isPartyBonus() ? 100000 : 200000), (int) Math.floor(0.66 * max), max, 0));
        }
    }

    public void clearDrops() {
        drops.clear();
        globaldrops.clear();
        load();
        addExtra();
    }

    public String getDrops(String itemName) {
        List<Integer> dropsfound = new LinkedList<>();

        for (Map.Entry<Integer, Pair<String, String>> item : MapleStringInformationProvider.getAllitemsStringCache().entrySet()) {
            if (item.getValue().getLeft().toLowerCase().contains(itemName.toLowerCase())) {
                int itemId = item.getKey();

                for (Pair<Integer, String> b : getAllMonsters()) {
                    for (MonsterDropEntry c : retrieveDrop(b.getLeft())) {
                        if (c.itemId == itemId) {
                            if (!dropsfound.contains(b.getLeft())) {
                                dropsfound.add(b.getLeft());
                            }
                        }
                    }
                }
            }
        }
        String droplist = "";
        for (int d : dropsfound) {
            droplist += "#o" + d + "#\r\n";
        }
        return droplist;
    }

    public String getDrops(int item) {
        List<Integer> dropsfound = new LinkedList<>();
        for (Pair<Integer, String> a : getAllMonsters()) {
            for (MonsterDropEntry b : retrieveDrop(a.getLeft())) {
                if (b.itemId == item) {
                    if (!dropsfound.contains(a.getLeft())) {
                        dropsfound.add(a.getLeft());
                    }
                }
            }
        }
        String droplist = "";
        for (int c : dropsfound) {
            droplist += "#o" + c + "#\r\n";
        }
        return droplist;
    }

    public List<Pair<Integer, String>> getAllMonsters() {
        List<Pair<Integer, String>> mobPairs = new ArrayList<>();

        for (Map.Entry<Integer, String> idNameEntry : MapleStringInformationProvider.getMobStringCache().entrySet()) {
            mobPairs.add(new Pair<>(idNameEntry.getKey(), idNameEntry.getValue()));
        }
        return mobPairs;
    }

    // Item Drops based off level
    public void loadCustomLevelDrops() {
        for (Entry<Integer, ArrayList<MonsterDropEntry>> e : drops.entrySet()) {
            final MonsterStats mons = LifeFactory.getMonsterStats(e.getKey());
            //for (Entry<Integer, ArrayList<MonsterDropEntry>> e : drops.entrySet()) {//???????

            if (mons.getLevel() > 1 && mons.getLevel() < 86) {
                e.getValue().add(new MonsterDropEntry(2028150, 50, 1, 1, 0));
            }
            if (mons.getLevel() > 49 && mons.getLevel() < 86) {
                e.getValue().add(new MonsterDropEntry(4001513, 80000, 1, 1, 0));
            }
            if (mons.getLevel() > 84 && mons.getLevel() < 131) {
                e.getValue().add(new MonsterDropEntry(4001515, 80000, 1, 1, 0));
                e.getValue().add(new MonsterDropEntry(2028180, 200, 1, 1, 0));
                e.getValue().add(new MonsterDropEntry(2028181, 300, 1, 1, 0));
            }
            if (mons.isBoss()) {
                e.getValue().add(new MonsterDropEntry(2049122, 1000, 1, 1, 0));
                e.getValue().add(new MonsterDropEntry(4310018, 5000, 1, 1, 0));
                e.getValue().add(new MonsterDropEntry(4310018, 3000, 1, 1, 0));
                e.getValue().add(new MonsterDropEntry(4310018, 2000, 1, 1, 0));
                //e.getValue().add(new MonsterDropEntry(2430028, 5000, 20, 50, 0)); 
                List<Integer> items;
                Integer[] itemArray = {3010000, 3010001, 3010002, 3010003, 3010004, 3010005, 3010006, 3010007, 3010008, 3010009, 3010010, 3010011, 3010012, 3010013, 3010014, 3010015, 3010016, 3010017, 3010018, 3010019, 3010021, 3010025, 3010035, 3010036, 3010038, 3010039, 3010040, 3010041, 3010043, 3010044, 3010045, 3010046, 3010047, 3010049, 3010052, 3010053, 3010054, 3010055, 3010057, 3010058,
                        3010196, 3010253, 3010255, 3010060, 3010061, 3010062, 3010063, 3010064, 3010065, 3010066, 3010067, 3010068, 3010069, 3010071, 3010072, 3010073, 3010075, 3010077, 3010080, 3010085, 3010092, 3010093, 3010095, 3010096, 3010098, 3010099, 3010101, 3010106, 3010107, 3010108, 3010109, 3010110, 3010111, 3010112, 3010113, 3010114, 3010115, 3010116, 3010117, 3010118, 3010119, 301020};
                items = Arrays.asList(itemArray);
                int item = Randomizer.nextInt(items.size());
                e.getValue().add(new MonsterDropEntry(item, 10000, 1, 1, 0));// 60%  
            }
            if (mons.getId() == 9390600 || mons.getId() == 8880000 || mons.getId() == 8920000 || mons.getId() == 8910000 || mons.getId() == 8900000 || mons.getId() == 8900001 || mons.getId() == 8240105 || mons.getId() == 9421589) {
                e.getValue().add(new MonsterDropEntry(2049135, 15000, 1, 1, 0)); // 20%, destruction upon failure
                e.getValue().add(new MonsterDropEntry(2049136, 10000, 1, 1, 0));// 20% no Destruction
                e.getValue().add(new MonsterDropEntry(2049137, 5000, 1, 1, 0)); // 40%
                e.getValue().add(new MonsterDropEntry(2049153, 3000, 1, 1, 0));// 60%  
                List<Integer> items;
                Integer[] itemArray = {3010183, 3010189, 3010249, 3010124, 3010125, 3010126, 3010127, 3010128, 3010129, 3010130, 3010131, 3010132, 3010133, 3010134, 3010135, 3010136, 3010137, 3010138, 3010139, 3010140, 3010141, 3010142, 3010145, 3010149, 3010151, 3010152, 3010154, 3010155, 3010156, 3010157, 3010161, 3010163, 3010165, 3010167, 3010168, 3010169, 3010170, 3010171, 3010172, 3010173, 3010174, 3010175, 3010177};
                items = Arrays.asList(itemArray);
                int item = Randomizer.nextInt(items.size());
                e.getValue().add(new MonsterDropEntry(item, 70000, 1, 1, 0));// 60%  
            }

            /*if (e.getKey() == 9800114 || e.getKey() == 9800115 || e.getKey() == 9800116 || e.getKey() == 9800117 || e.getKey() == 9800118 || e.getKey() == 9800119 || e.getKey() == 9800120 || e.getKey() == 9800121 || e.getKey() == 9800122) { 
                    e.getValue().add(new MonsterDropEntry(4310020, 10000, 1, 1, 0));// 60%  
                }*/
            if (mons.getLevel() > 131 && mons.getLevel() < 251) {
                e.getValue().add(new MonsterDropEntry(5680021, 500, 1, 1, 0));
                e.getValue().add(new MonsterDropEntry(2028126, 1000, 1, 1, 0));
                e.getValue().add(new MonsterDropEntry(4001521, 80000, 1, 1, 0));
            }
            /*if (mons.getLevel() > 1 && mons.getLevel() < 120) {
                    e.getValue().add(new MonsterDropEntry(4310014, 5000, 1, 1, 0, "Source Customization")); 
                }*/
        }
    }

    public int chanceLogic(int itemId) {
        if (null != GameConstants.getInventoryType(itemId)) //not much logic in here. most of the drops should already be there anyway.
        {
            switch (GameConstants.getInventoryType(itemId)) {
                case EQUIP:
                    return 50000; //with *10
                case SETUP:
                case CASH:
                    return 500;
                default:
                    switch (itemId / 10000) {
                        case 204:
                        case 207:
                        case 233:
                        case 229:
                            return 500;
                        case 401:
                        case 402:
                            return 5000;
                        case 403:
                            return 5000; //lol
                    }
                    return 20000;
            }
        }
        return 0;
    }
    //MESO DROP: level * (level / 10) = max, min = 0.66 * max
    //explosive Reward = 7 meso drops
    //boss, ffaloot = 2 meso drops
    //boss = level * level = max
    //no mesos if: mobid / 100000 == 97 or 95 or 93 or 91 or 90 or removeAfter > 0 or invincible or onlyNormalAttack or friendly or dropitemperiod > 0 or cp > 0 or point > 0 or fixeddamage > 0 or selfd > 0 or mobType != null and mobType.charat(0) == 7 or PDRate <= 0
}