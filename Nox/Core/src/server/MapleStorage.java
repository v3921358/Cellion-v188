package server;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import client.ClientSocket;
import client.inventory.Item;
import client.inventory.ItemLoader;
import client.inventory.MapleInventoryType;
import constants.GameConstants;
import database.Database;
import static java.sql.Statement.RETURN_GENERATED_KEYS;
import tools.Pair;
import tools.packet.CField.NPCPacket;

public class MapleStorage implements Serializable {

    private static final long serialVersionUID = 9179541993413738569L;
    private final int id;
    private final int accountId;
    private final List<Item> items;
    private long meso;
    private int lastNPC = 0;
    private byte slots;
    private boolean changed = false;
    private final Map<MapleInventoryType, List<Item>> typeItems = new EnumMap<>(MapleInventoryType.class);

    private MapleStorage(int id, byte slots, long meso, int accountId) {
        this.id = id;
        this.slots = slots;
        this.items = new LinkedList<>();
        this.meso = meso;
        this.accountId = accountId;
    }

    public static int create(int id) throws SQLException {
        ResultSet rs;
        try (Connection con = Database.GetConnection()) {

            try (PreparedStatement ps = con.prepareStatement("INSERT INTO storages (accountid, slots, meso) VALUES (?, ?, ?)", RETURN_GENERATED_KEYS)) {
                ps.setInt(1, id);
                ps.setInt(2, 4);
                ps.setLong(3, 0);
                ps.executeUpdate();
                int storageid;
                rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    storageid = rs.getInt(1);
                    ps.close();
                    rs.close();
                    return storageid;
                }
            }
        }

        rs.close();
        throw new SQLException("Inserting char failed.");
    }

    public static MapleStorage loadStorage(int id) {
        MapleStorage ret = null;
        int storeId;
        try (Connection con = Database.GetConnection()) {

            PreparedStatement ps = con.prepareStatement("SELECT * FROM storages WHERE accountid = ?");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                storeId = rs.getInt("storageid");
                ret = new MapleStorage(storeId, rs.getByte("slots"), rs.getLong("meso"), id);
                rs.close();
                ps.close();

                for (Pair<Item, MapleInventoryType> mit : ItemLoader.STORAGE.loadItems(false, id, con).values()) {
                    ret.items.add(mit.getLeft());
                }
            } else {
                storeId = create(id);
                ret = new MapleStorage(storeId, (byte) 4, 0, id);
                rs.close();
                ps.close();
            }
        } catch (SQLException ex) {
            System.err.println("Error loading storage" + ex);
        }

        return ret;
    }

    public void saveToDB(Connection con) {
        if (!changed) {
            return;
        }
        try (PreparedStatement ps = con.prepareStatement("UPDATE storages SET slots = ?, meso = ? WHERE storageid = ?")) {
            ps.setInt(1, slots);
            ps.setLong(2, meso);
            ps.setInt(3, id);
            ps.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        List<Pair<Item, MapleInventoryType>> listing = new ArrayList<>();
        for (final Item item : items) {
            listing.add(new Pair<>(item, GameConstants.getInventoryType(item.getItemId())));
        }
        ItemLoader.STORAGE.saveItems(listing, accountId, con);
    }

    public Item takeOut(byte slot) {
        if (slot >= items.size() || slot < 0) {
            return null;
        }
        changed = true;
        Item ret = items.remove(slot);
        MapleInventoryType type = GameConstants.getInventoryType(ret.getItemId());
        typeItems.put(type, filterItems(type));
        return ret;
    }

    public void store(Item item) {
        changed = true;
        items.add(item);
        MapleInventoryType type = GameConstants.getInventoryType(item.getItemId());
        typeItems.put(type, filterItems(type));
    }

    public void arrange() { //i believe gms does by itemID
        Collections.sort(items, new Comparator<Item>() {

            @Override
            public int compare(Item o1, Item o2) {
                if (o1.getItemId() < o2.getItemId()) {
                    return -1;
                } else if (o1.getItemId() == o2.getItemId()) {
                    return 0;
                } else {
                    return 1;
                }
            }
        });
        for (MapleInventoryType type : MapleInventoryType.values()) {
            typeItems.put(type, items);
        }
    }

    public List<Item> getItems() {
        return Collections.unmodifiableList(items);
    }

    private List<Item> filterItems(MapleInventoryType type) {
        List<Item> ret = new ArrayList<>();

        for (Item item : items) {
            if (GameConstants.getInventoryType(item.getItemId()) == type) {
                ret.add(item);
            }
        }
        return ret;
    }

    public byte getSlot(MapleInventoryType type, byte slot) {
        // MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        byte ret = 0;
        final List<Item> it = typeItems.get(type);
        if (it == null || slot >= it.size() || slot < 0) {
            return -1;
        }
        for (Item item : items) {
            if (item == it.get(slot)) {
                return ret;
            }
            ret++;
        }
        return -1;
    }

    public void sendStorage(ClientSocket c, int npcId) {
        // sort by inventorytype to avoid confusion
        setLastNPC(npcId);
        Collections.sort(items, new Comparator<Item>() {

            @Override
            public int compare(Item o1, Item o2) {
                if (GameConstants.getInventoryType(o1.getItemId()).getType() < GameConstants.getInventoryType(o2.getItemId()).getType()) {
                    return -1;
                } else if (GameConstants.getInventoryType(o1.getItemId()) == GameConstants.getInventoryType(o2.getItemId())) {
                    return 0;
                } else {
                    return 1;
                }
            }
        });
        for (MapleInventoryType type : MapleInventoryType.values()) {
            typeItems.put(type, items);
        }
        c.SendPacket(NPCPacket.getStorage(npcId, slots, items, meso));
    }

    public void update(ClientSocket c) {
        c.SendPacket(NPCPacket.arrangeStorage(slots, items, true));
    }

    public void sendStored(ClientSocket c, MapleInventoryType type) {
        c.SendPacket(NPCPacket.storeStorage(slots, type, typeItems.get(type)));
    }

    public void sendTakenOut(ClientSocket c, MapleInventoryType type) {
        c.SendPacket(NPCPacket.takeOutStorage(slots, type, typeItems.get(type)));
    }

    public long getMeso() {
        return meso;
    }

    public Item findById(int itemId) {
        for (Item item : items) {
            if (item.getItemId() == itemId) {
                return item;
            }
        }
        return null;
    }

    public void setMeso(long meso) {
        if (meso < 0) {
            return;
        }
        changed = true;
        this.meso = meso;
    }

    public void sendMeso(ClientSocket c) {
        c.SendPacket(NPCPacket.mesoStorage(slots, meso));
    }

    public boolean isFull() {
        return items.size() >= slots;
    }

    public int getSlots() {
        return slots;
    }

    public void increaseSlots(byte gain) {
        changed = true;
        this.slots += gain;
    }

    public void setSlots(byte set) {
        changed = true;
        this.slots = set;
    }

    public void close() {
        typeItems.clear();
    }

    /**
     * @return the lastNPC
     */
    public int getLastNPC() {
        return lastNPC;
    }

    /**
     * @param lastNPC the lastNPC to set
     */
    public void setLastNPC(int lastNPC) {
        this.lastNPC = lastNPC;
    }
}
