package server;

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

import database.Database;
import java.util.concurrent.locks.ReentrantLock;
import provider.MapleData;
import provider.MapleDataProvider;
import provider.MapleDataTool;
import provider.wz.cache.WzDataStorage;
import server.CashItemInfo.CashModInfo;
import tools.LogHelper;

public class CashItemFactory {

    private ReentrantLock m_Lock = new ReentrantLock(true);
    private final static CashItemFactory instance = new CashItemFactory();
    private final static int[] bestItems = new int[]{10003055, 10003090, 10103464, 10002960, 10103363};
    private final Map<Integer, CashItemInfo> itemStats = new HashMap<>();
    private final Map<Integer, List<Integer>> itemPackage = new HashMap<>();
    private final Map<Integer, CashModInfo> itemMods = new HashMap<>();
    private final Map<Integer, List<Integer>> openBox = new HashMap<>();
    private final MapleDataProvider data = WzDataStorage.getEtcWZ();
    private final List<CashCategory> categories = new LinkedList<>();
    private final Map<Integer, CashItem> menuItems = new HashMap<>();
    private final Map<Integer, CashItem> categoryItems = new HashMap<>();

    public static CashItemFactory getInstance() {
        return instance;
    }

    public static void reload() {
        CashItemFactory pCashItemFactory = getInstance();
        pCashItemFactory.m_Lock.lock();
        try {
            pCashItemFactory.itemStats.clear();
            pCashItemFactory.itemPackage.clear();
            pCashItemFactory.itemMods.clear();
            pCashItemFactory.openBox.clear();
            pCashItemFactory.categories.clear();
            pCashItemFactory.menuItems.clear();
            pCashItemFactory.categoryItems.clear();
            pCashItemFactory.initialize();
        } finally {
            pCashItemFactory.m_Lock.unlock();
        }
    }

    public void initialize() {
        final List<MapleData> cccc = data.getData("Commodity.img").getChildren();
        for (MapleData field : cccc) {
            final int SN = MapleDataTool.getIntConvert("SN", field, 0);

            final CashItemInfo stats = new CashItemInfo(MapleDataTool.getIntConvert("ItemId", field, 0),
                    MapleDataTool.getIntConvert("Count", field, 1),
                    MapleDataTool.getIntConvert("Price", field, 0), SN,
                    MapleDataTool.getIntConvert("Period", field, 0),
                    MapleDataTool.getIntConvert("Gender", field, 2),
                    MapleDataTool.getIntConvert("OnSale", field, 0) > 0 && MapleDataTool.getIntConvert("Price", field, 0) > 0,
                    0);

            if (SN > 0) {
                itemStats.put(SN, stats);
            }
        }

        final MapleData b = data.getData("CashPackage.img");
        for (MapleData c : b.getChildren()) {
            if (c.getChildByPath("SN") == null) {
                continue;
            }
            final List<Integer> packageItems = new ArrayList<>();
            for (MapleData d : c.getChildByPath("SN").getChildren()) {
                packageItems.add(MapleDataTool.getIntConvert(d));
            }
            itemPackage.put(Integer.parseInt(c.getName()), packageItems);
        }

        try (Connection con = Database.GetConnection()) {

            try (PreparedStatement ps = con.prepareStatement("SELECT * FROM cashshop_modified_items"); ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    CashModInfo ret = new CashModInfo(rs.getInt("serial"), rs.getInt("discount_price"), rs.getInt("mark"), rs.getInt("showup") > 0, rs.getInt("itemid"), rs.getInt("priority"), rs.getInt("package") > 0, rs.getInt("period"), rs.getInt("gender"), rs.getInt("count"), rs.getInt("meso"), rs.getInt("unk_1"), rs.getInt("unk_2"), rs.getInt("unk_3"), rs.getInt("extra_flags"));
                    itemMods.put(ret.sn, ret);
                    if (ret.showUp) {
                        final CashItemInfo cc = itemStats.get(Integer.valueOf(ret.sn));
                        if (cc != null) {
                            ret.toCItem(cc); //init
                        }
                    }
                }
            }
        } catch (SQLException e) {
            LogHelper.SQL.get().info("[SQL] There was an issue with something from the database:\n", e);
        }

        List<Integer> availableSN = new LinkedList<>();
        availableSN.add(20001141);
        availableSN.add(20001142);
        availableSN.add(20001143);
        availableSN.add(20001144);
        availableSN.add(20001145);
        availableSN.add(20001146);
        availableSN.add(20001147);
        openBox.put(5533003, availableSN); // Rainbow Visor Box

        availableSN = new LinkedList<>();
        availableSN.add(20000462);
        availableSN.add(20000463);
        availableSN.add(20000464);
        availableSN.add(20000465);
        availableSN.add(20000466);
        availableSN.add(20000467);
        availableSN.add(20000468);
        availableSN.add(20000469);
        openBox.put(5533000, availableSN); // Korean stuffs..

        availableSN = new LinkedList<>();
        availableSN.add(20800259);
        availableSN.add(20800260);
        availableSN.add(20800263);
        availableSN.add(20800264);
        availableSN.add(20800265);
        availableSN.add(20800267);
        openBox.put(5533001, availableSN); // Angelic Beam Weapon Box

        availableSN = new LinkedList<>();
        availableSN.add(20800270);
        availableSN.add(20800271);
        availableSN.add(20800272);
        availableSN.add(20800273);
        availableSN.add(20800274);
        openBox.put(5533002, availableSN); // Chief Knight Weapon Box

        try (Connection con = Database.GetConnection()) {

            try (PreparedStatement ps = con.prepareStatement("SELECT * FROM cashshop_categories"); ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    CashCategory cat = new CashCategory(rs.getInt("categoryid"), rs.getString("name"), rs.getInt("parent"), rs.getInt("flag"), rs.getInt("sold"));
                    if (!(rs.getInt("sold") > 0)) categories.add(cat); // Hides sold out categories.
                }
            }

            try (PreparedStatement ps = con.prepareStatement("SELECT * FROM cashshop_menuitems"); ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    CashItem item = new CashItem(rs.getInt("category"),rs.getInt("subcategory"), rs.getInt("parent"), rs.getString("image"), rs.getInt("sn"), rs.getInt("itemid"), rs.getInt("flag"), rs.getInt("price"), rs.getInt("discountPrice"), rs.getInt("quantity"), rs.getInt("expire"), rs.getInt("gender"), rs.getInt("likes"));
                    if (!(rs.getInt("sold") > 0)) menuItems.put(item.getSN(), item); // Hides sold out sub categories.
                }
            }

            try (PreparedStatement ps = con.prepareStatement("SELECT * FROM cashshop_items"); ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    CashItem item = new CashItem(rs.getInt("category"),rs.getInt("subcategory"), rs.getInt("parent"), rs.getString("image"), rs.getInt("sn"), rs.getInt("itemid"), rs.getInt("flag"), rs.getInt("price"), rs.getInt("discountPrice"), rs.getInt("quantity"), rs.getInt("expire"), rs.getInt("gender"), rs.getInt("likes"));
                    if (rs.getInt("price") > 0) categoryItems.put(item.getSN(), item); // Hides items when price is set to zero.
                }
            }
        } catch (SQLException e) {
            LogHelper.SQL.get().info("Failed to load cash shop categories. ", e);
        }

    }

    public final CashItemInfo getSimpleItem(int sn) {
        return itemStats.get(sn);
    }

    public final CashItemInfo getItem(int sn) {
        final CashItemInfo stats = itemStats.get(Integer.valueOf(sn));
        final CashModInfo z = getModInfo(sn);
        if (z != null && z.showUp) {
            return z.toCItem(stats); //null doesnt matter
        }
        if (stats == null || !stats.onSale()) {
            return null;
        }
        return stats;
    }

    public final CashItem getMenuItem(int sn) {
        for (CashItem ci : getMenuItems()) {
            if (ci.getSN() == sn) {
                return ci;
            }
        }
        return null;
    }

    public final CashItem getAllItem(int sn) {
        for (CashItem ci : getAllItems()) {
            if (ci.getSN() == sn) {
                return ci;
            }
        }
        return null;
    }

    public final List<Integer> getPackageItems(int itemId) {
        return itemPackage.get(itemId);
    }

    public final CashModInfo getModInfo(int sn) {
        return itemMods.get(sn);
    }

    public final Collection<CashModInfo> getAllModInfo() {
        return itemMods.values();
    }

    public final Map<Integer, List<Integer>> getRandomItemInfo() {
        return openBox;
    }

    public final int[] getBestItems() {
        return bestItems;
    }

    public final List<CashCategory> getCategories() {
        return categories;
    }

    public final List<CashItem> getMenuItems(int type) {
        List<CashItem> items = new LinkedList<>();
        for (CashItem ci : menuItems.values()) {
            if (ci.getSubCategory() / 10000 == type) {
                items.add(ci);
            }
        }
        return items;
    }

    public final List<CashItem> getMenuItems() {
        List<CashItem> items = new LinkedList<>();
        for (CashItem ci : menuItems.values()) {
            items.add(ci);
        }
        return items;
    }

    public final List<CashItem> getAllItems(int type) {
        List<CashItem> items = new LinkedList<>();
        for (CashItem ci : categoryItems.values()) {
            if (ci.getSubCategory() / 10000 == type) {
                items.add(ci);
            }
        }
        return items;
    }

    public final List<CashItem> getAllItems() {
        List<CashItem> items = new LinkedList<>();
        for (CashItem ci : categoryItems.values()) {
            items.add(ci);
        }
        return items;
    }

    public final List<CashItem> getCategoryItems(int category) {
        List<CashItem> items = new LinkedList<>();
        for (CashItem ci : categoryItems.values()) {
            //Other servers don't do the child cats (+1~9) lmao
            if (ci.getSubCategory() == category || ci.getParent() == category) {
                items.add(ci);
            }
        }
        return items;
    }
}
