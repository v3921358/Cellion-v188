package server.shops;

import client.MapleClient;
import client.SkillFactory;
import client.inventory.*;
import constants.GameConstants;
import constants.InventoryConstants;
import database.Database;
import server.AutobanManager;
import server.MapleInventoryManipulator;
import server.MapleItemInformationProvider;
import server.maps.objects.User;
import server.maps.objects.Pet;
import tools.LogHelper;
import tools.Pair;
import tools.packet.CField;
import tools.packet.CWvsContext;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;

public class MapleShop {

    private static final Set<Integer> rechargeableItems = new LinkedHashSet<>();
    private final int id;
    private final int npcId;
    private final List<MapleShopItem> items = new LinkedList<>();
    private final List<Pair<Integer, String>> ranks = new ArrayList<>();

    static {
        rechargeableItems.add(Integer.valueOf(2070000));
        rechargeableItems.add(Integer.valueOf(2070001));
        rechargeableItems.add(Integer.valueOf(2070002));
        rechargeableItems.add(Integer.valueOf(2070003));
        rechargeableItems.add(Integer.valueOf(2070004));
        rechargeableItems.add(Integer.valueOf(2070005));
        rechargeableItems.add(Integer.valueOf(2070006));
        rechargeableItems.add(Integer.valueOf(2070007));
        rechargeableItems.add(Integer.valueOf(2070008));
        rechargeableItems.add(Integer.valueOf(2070009));
        rechargeableItems.add(Integer.valueOf(2070010));
        rechargeableItems.add(Integer.valueOf(2070011));
        rechargeableItems.add(Integer.valueOf(2070023));
        rechargeableItems.add(Integer.valueOf(2070024));
        rechargeableItems.add(Integer.valueOf(2330000));
        rechargeableItems.add(Integer.valueOf(2330001));
        rechargeableItems.add(Integer.valueOf(2330002));
        rechargeableItems.add(Integer.valueOf(2330003));
        rechargeableItems.add(Integer.valueOf(2330004));
        rechargeableItems.add(Integer.valueOf(2330005));
        rechargeableItems.add(Integer.valueOf(2330008));
        rechargeableItems.add(Integer.valueOf(2331000));
        rechargeableItems.add(Integer.valueOf(2332000));
    }

    public MapleShop(int id, int npcId) {
        this.id = id;
        this.npcId = npcId;
    }

    public void addItem(MapleShopItem item) {
        this.items.add(item);
    }

    public List<MapleShopItem> getItems() {
        return this.items;
    }

    public void sendShop(MapleClient c) {
        c.getPlayer().setShop(this);
        c.SendPacket(CField.NPCPacket.getNPCShop(getNpcId(), this, c));
    }

    public void sendShop(MapleClient c, int customNpc) {
        c.getPlayer().setShop(this);
        c.SendPacket(CField.NPCPacket.getNPCShop(customNpc, this, c));
    }

    public void buy(MapleClient c, short slot, int itemId, short quantity) {
        if (itemId / 10000 == 190 && !GameConstants.isMountItemAvailable(itemId, c.getPlayer().getJob())) {
            c.getPlayer().dropMessage(1, "You may not buy this item.");
            c.SendPacket(CWvsContext.enableActions());
            return;
        }

        // Item information
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        final MapleShopItem shopitem = findBySlot(slot);

        if (shopitem != null) {
            // Check if the itemid that the player sends is the same itemid on the server
            if (shopitem.getItemId() != itemId) {
                LogHelper.PACKET_EDIT_HACK.get().info(String.format("%s is attempting to buy [%d] shop item of itemid '%d' by sending an itemid of '%d' at map [%d] from NPC: %d",
                        c.getPlayer().getName(), quantity, shopitem.getItemId(), itemId, c.getPlayer().getMapId(), npcId));

                c.SendPacket(CWvsContext.enableActions());
                return;
            }

            short totalQuantity = (short) (quantity * shopitem.getQuantity()); // the shop item may be sold in a bundle
            if (GameConstants.isRechargable(itemId)) {
                totalQuantity = ii.getSlotMax(shopitem.getItemId());
            }
            if (totalQuantity <= 0) {
                LogHelper.PACKET_EDIT_HACK.get().info(String.format("%s attempting to buy item %d of negative quantity '%d' from NPC shop id '%d' at map ['%d'] from NPC: %d",
                        c.getPlayer().getName(), shopitem.getReqItem(), totalQuantity, id, c.getPlayer().getMapId(), npcId));

                c.SendPacket(CWvsContext.enableActions());
                return;
            }

            // This shop item is to be bought with meso 
            if (shopitem.getReqItem() == 0) {
                final boolean rankEligible = checkRankEligibility(c.getPlayer(), shopitem);
                final int price = GameConstants.isRechargable(itemId) ? shopitem.getPrice() : (shopitem.getPrice() * totalQuantity);

                if (!rankEligible) {
                    c.getPlayer().dropMessage(1, "You need a higher rank to purcahse this item.");

                } else if (shopitem.getPrice() <= 0 // basic error check, in case the guy doing NPC shop is drunk
                        || price <= 0 || c.getPlayer().getMeso() < price) {
                    c.getPlayer().dropMessage(1, "You do not have enough meso to purchase this item.");

                } else if (!MapleInventoryManipulator.checkSpace(c, itemId, totalQuantity, "")) {
                    c.getPlayer().dropMessage(1, "There is not enough space in your inventory to purchase this item.");

                } else {
                    // Always remove first, a.k.a incur cost to the player before giving
                    // in case an exception happened after it, so as to avoid exploits
                    c.getPlayer().gainMeso(-price, false);

                    // Give item
                    if (InventoryConstants.isPet(itemId)) {
                        MapleInventoryManipulator.addById(c, itemId, totalQuantity, "", Pet.createPet(itemId, MapleInventoryIdentifier.getInstance()), -1, false, "Bought from shop " + id + ", " + npcId + " on " + LocalDateTime.now());
                    } else {
                        MapleInventoryManipulator.addById(c, itemId, totalQuantity, "Bought from shop " + this.id + ", " + this.npcId + " on " + LocalDateTime.now());
                    }
                    c.SendPacket(CField.NPCPacket.confirmShopTransaction(ShopOperationType.Buy, this, c, -1));
                    return; // stop,
                }
            } else { // this shop item is to be bought with another item such as coin
                if (totalQuantity != 1 || !c.getPlayer().haveItem(shopitem.getReqItem(), shopitem.getReqItemQ(), false, true)) {
                    LogHelper.PACKET_EDIT_HACK.get().info(String.format("%s attempting to buy item %d of quantity %d from NPC shop id '%d' at map ['%d'] from NPC: %d, without having the required items of [%d X %d]",
                            c.getPlayer().getName(), shopitem.getReqItem(), totalQuantity, id, c.getPlayer().getMapId(), npcId, shopitem.getReqItem(), shopitem.getReqItemQ()));

                    c.SendPacket(CWvsContext.enableActions());
                    return;
                }

                if (!MapleInventoryManipulator.checkSpace(c, itemId, totalQuantity, "")) {
                    c.getPlayer().dropMessage(1, "There is not enough space in your inventory to purchase this item.");

                } else {
                    // Always remove first, a.k.a incur cost to the player before giving
                    // in case an exception happened after it, so as to avoid exploits
                    MapleInventoryManipulator.removeById(c, GameConstants.getInventoryType(shopitem.getReqItem()), shopitem.getReqItem(), shopitem.getReqItemQ(), false, false);

                    // Give item
                    if (InventoryConstants.isPet(itemId)) {
                        MapleInventoryManipulator.addById(c, itemId, totalQuantity, "", Pet.createPet(itemId, MapleInventoryIdentifier.getInstance()), -1, false, "Bought from shop " + id + ", " + npcId + " on " + LocalDateTime.now());
                    } else {
                        MapleInventoryManipulator.addById(c, itemId, totalQuantity, "Bought from shop " + this.id + ", " + this.npcId + " on " + LocalDateTime.now());
                    }
                    c.SendPacket(CField.NPCPacket.confirmShopTransaction(ShopOperationType.Buy, this, c, -1));
                    return; // stop,
                }
            }
        } else if (slot >= items.size()) { // Re-purchase sold item from NPC back.
            /**
             * TODO: Identify how the slot number are calculated by MapleStory client.. I really have no idea for now.
             *
             * [EL Nath Potion store] Store item size = 41 Re-buying from first slot = 67 Difference: 26
             *
             * [EL NATH Weapon store]\ Store item size = 27 Re-buying from first slot = 55 Difference: 28
             *
             * [Orbis potion store] Store item size = 29 Re-buying from first slot = 56
             *
             * Difference: 27
             */

            // Could this be a re-purchase item?
            // Create a copy of this array 
            final List<ShopRepurchase> RePurchases = c.getPlayer().getShopRepurchases();

            // List of items available to purchase back
            for (ShopRepurchase repurchase : RePurchases) {
                if (/*repurchase.getItem().getQuantity() == quantity && */repurchase.getItem().getItemId() == itemId) {
                    // Remove from the character's ArrayList first before giving.

                    Item item = repurchase.getItem();
                    int indexFrom = RePurchases.indexOf(repurchase);
                    int previousSalePrice = repurchase.getPreviousSalePrice();
                    int rePurchaseItemId = repurchase.getItem().getItemId();
                    short rePurchaseQuantity = repurchase.getItem().getQuantity();

                    // Attempt to repurchase this..
                    if (previousSalePrice >= 0 && c.getPlayer().getMeso() >= previousSalePrice) {
                        if (MapleInventoryManipulator.checkSpace(c, rePurchaseItemId, rePurchaseQuantity, "")) {

                            final boolean removedFromExistingRepurchaseList = c.getPlayer().removeShopRepurchase(repurchase);

                            if (removedFromExistingRepurchaseList) { // double check, as additional pre-caution to prevent exploits
                                c.getPlayer().gainMeso(-previousSalePrice, false);

                                if (item != null && item.getType() == ItemType.Equipment) {
                                    MapleInventoryManipulator.addbyItem(c, item);

                                } else if (InventoryConstants.isPet(rePurchaseItemId)) {
                                    MapleInventoryManipulator.addById(c, rePurchaseItemId, rePurchaseQuantity, "", Pet.createPet(rePurchaseItemId, MapleInventoryIdentifier.getInstance()), -1, false, "Bought from shop " + id + ", " + npcId + " on " + LocalDateTime.now());

                                } else {
                                    MapleInventoryManipulator.addById(c, rePurchaseItemId, rePurchaseQuantity, "Bought from shop " + this.id + ", " + this.npcId + " on " + LocalDateTime.now());
                                }
                                c.SendPacket(CField.NPCPacket.confirmShopTransaction(ShopOperationType.Buy, this, c, indexFrom));
                                return; // stop completely.
                            }
                        } else {
                            c.getPlayer().dropMessage(1, "There is not enough space in your inventory to purchase this item.");
                        }
                    }
                }
            }
        }
        // nothing is being re-bought, sent enableaction
        c.SendPacket(CWvsContext.enableActions());
    }

    private boolean checkRankEligibility(final User chr, final MapleShopItem shopitem) {
        if (shopitem.getRank() >= 0) {
            boolean passed = true;
            int y = 0;

            for (Pair i : getRanks()) {
                if (chr.haveItem((Integer) i.left, 1, true, true) && shopitem.getRank() >= y) {
                    passed = true;
                    break;
                }
                y++;
            }
            if (!passed) {
                return false;
            }
        }
        return true;
    }

    public void sell(MapleClient c, MapleInventoryType type, byte slot, short quantity) {
        switch (type) {
            case UNDEFINED:
            case EQUIPPED:
            case CASH:
                AutobanManager.getInstance().autoban(c, String.format("Selling equipped or cash item to NPC. Inventory type: %s, Slot: %d, Quantity: %d, Mapid: %d", type.toString(), slot, quantity, c.getPlayer().getMapId()));
                return;
        }
        final Item item_fromInventory = c.getPlayer().getInventory(type).getItem((short) slot);
        if (item_fromInventory == null) {
            c.SendPacket(CWvsContext.enableActions());
            return;
        }

        // Adjust selling quantity
        if ((quantity == 65535) || (quantity == 0)) {
            quantity = 1;
        }
        if ((GameConstants.isThrowingStar(item_fromInventory.getItemId())) || (GameConstants.isBullet(item_fromInventory.getItemId()))) {
            quantity = item_fromInventory.getQuantity();
        }
        short quantity_inventory = item_fromInventory.getQuantity();
        if (quantity_inventory == 65535) {
            quantity_inventory = 1;
        }

        // Item information
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        if ((ii.cantSell(item_fromInventory.getItemId())) || (InventoryConstants.isPet(item_fromInventory.getItemId()))) {
            c.SendPacket(CWvsContext.enableActions());
            return;
        }

        // Basic check, if the user is attempting to sell what they have
        if (quantity > quantity_inventory || quantity_inventory <= 0) {
            LogHelper.ANTI_HACK.get().info(String.format("%s attempting to sell item %d of quantity %d to NPC shop id '%d' at map ['%d'] from NPC: %d",
                    c.getPlayer().getName(), item_fromInventory.getItemId(), quantity, id, c.getPlayer().getMapId(), npcId));
            c.SendPacket(CWvsContext.enableActions());
            return;
        }

        MapleInventoryManipulator.removeFromSlot(c, type, (short) slot, quantity, false);
        c.SendPacket(CWvsContext.enableActions()); // Just incase, to prevent players from getting stuck inbetween selling items.

        double price;
        if ((GameConstants.isThrowingStar(item_fromInventory.getItemId())) || (GameConstants.isBullet(item_fromInventory.getItemId()))) {
            price = ii.getWholePrice(item_fromInventory.getItemId()) / ii.getSlotMax(item_fromInventory.getItemId());
        } else {
            price = ii.getPrice(item_fromInventory.getItemId());
        }

        final int recvMesos = (int) Math.max(Math.ceil(price * quantity), 0.0D);
        if (price != -1.0D && recvMesos > 0) {
            c.getPlayer().gainMeso(recvMesos, false);
        }

        // Add to shop repurchase
        addShopRepurchaseItem(c, item_fromInventory, quantity, recvMesos);

        // Sent packet to client
        c.SendPacket(CField.NPCPacket.confirmShopTransaction(ShopOperationType.Sell, this, c, -1)); // LOL?? wasn't me i promise :monkaS:
    }

    /**
     * Add to re-purchase list [NOTE: Only execute this code after the player have sold them to prevent future exceptions that opens an
     * exploit to item duplication
     *
     * @param c
     * @param item_fromInventory
     * @param quantity_sold
     * @param recvMesos
     */
    private static void addShopRepurchaseItem(MapleClient c, Item item_fromInventory, short quantity_sold, int recvMesos) {
        final Item item_cpy = item_fromInventory.copy(); // The reference that will be used for storing repurchase info. 
        if (item_fromInventory.getType() == ItemType.Equipment) {
            item_cpy.setQuantity(((short) 1));
        } else {
            // Make sure that we set the sell quantity to what the character is actually doing
            // and not the inventory quantity
            item_cpy.setQuantity(quantity_sold);
        }

        final ShopRepurchase repurchase = new ShopRepurchase(item_cpy, recvMesos);
        c.getPlayer().addShopRepurchase(repurchase);
    }

    public void recharge(MapleClient c, byte slot) {
        Item item = c.getPlayer().getInventory(MapleInventoryType.USE).getItem((short) slot);

        if (item == null || (!GameConstants.isThrowingStar(item.getItemId()) && !GameConstants.isBullet(item.getItemId()))) {
            return;
        }
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        short slotMax = ii.getSlotMax(item.getItemId());
        int skill = GameConstants.getMasterySkill(c.getPlayer().getJob());

        if (skill != 0) {
            slotMax = (short) (slotMax + c.getPlayer().getTotalSkillLevel(SkillFactory.getSkill(skill)) * 10);
        }
        if (item.getQuantity() < slotMax) {
            int price = (int) Math.round(ii.getPrice(item.getItemId()) * (slotMax - item.getQuantity()));
            if (c.getPlayer().getMeso() >= price) {
                c.getPlayer().gainMeso(-price, false, false);
                item.setQuantity(slotMax);

                List<ModifyInventory> mod = new ArrayList<>();
                mod.add(new ModifyInventory(ModifyInventoryOperation.UpdateQuantity, item));
                c.SendPacket(CWvsContext.inventoryOperation(true, mod));

                c.SendPacket(CField.NPCPacket.confirmShopTransaction(ShopOperationType.Buy, this, c, -1));
            }
        }
    }

    protected MapleShopItem findById(int itemId) {
        for (MapleShopItem item : this.items) {
            if (item.getItemId() == itemId) {
                return item;
            }
        }
        return null;
    }

    protected MapleShopItem findBySlot(short slot) {
        for (MapleShopItem item : this.items) {
            if (item.getSlot() == slot) {
                return item;
            }
        }
        return null;
    }

    public static MapleShop createFromDB(int id, boolean isShopId) {
        MapleShop ret = null;

        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        try (Connection con = Database.GetConnection()) {

            PreparedStatement ps = con.prepareStatement(isShopId ? "SELECT * FROM shops WHERE shopid = ?" : "SELECT * FROM shops WHERE npcid = ?");
            int shopId;
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                shopId = rs.getInt("shopid");
                ret = new MapleShop(shopId, rs.getInt("npcid"));
                rs.close();
                ps.close();
            } else {
                rs.close();
                ps.close();
                return null;
            }
            ps = con.prepareStatement("SELECT * FROM shopitems WHERE shopid = ? ORDER BY position ASC");
            ps.setInt(1, shopId);
            rs = ps.executeQuery();
            List<Integer> recharges = new ArrayList<>(rechargeableItems);
            while (rs.next()) {
                if (ii.itemExists(rs.getInt("itemid"))) {
                    if ((GameConstants.isThrowingStar(rs.getInt("itemid"))) || (GameConstants.isBullet(rs.getInt("itemid")))) {
                        MapleShopItem starItem = new MapleShopItem((short) rs.getShort("buyable"), ii.getSlotMax(rs.getInt("itemid")), rs.getInt("itemid"), rs.getInt("price"), (short) rs.getInt("position"), rs.getInt("reqitem"), rs.getInt("reqitemq"), rs.getByte("rank"), rs.getInt("category"), rs.getInt("minLevel"), rs.getInt("expiration"), rs.getInt("potential"));
                        starItem.setPointQuestId(rs.getInt("pointquestid"));
                        starItem.setPointQuestPrice(rs.getInt("pointquestprice"));
                        starItem.setStarCoin(rs.getInt("starcoin"));
                        starItem.setQuestExId(rs.getInt("questexid"));
                        starItem.setQuestExKey(rs.getString("questexkey"));
                        starItem.setQuestExValue(rs.getInt("questexvalue"));
                        starItem.setMaxLevel(rs.getInt("maxlevel"));
                        starItem.setQuestId(rs.getInt("questid"));
                        starItem.setSaleLimit(rs.getInt("salelimit"));
                        starItem.setLevelLimited(rs.getInt("levellimited"));
                        ret.addItem(starItem);
                    } else {
                        MapleShopItem shopItem = new MapleShopItem((short) rs.getShort("buyable"), rs.getShort("quantity"), rs.getInt("itemid"), rs.getInt("price"), (short) rs.getInt("position"), rs.getInt("reqitem"), rs.getInt("reqitemq"), rs.getByte("rank"), rs.getInt("category"), rs.getInt("minLevel"), rs.getInt("expiration"), rs.getInt("potential"));
                        shopItem.setPointQuestId(rs.getInt("pointquestid"));
                        shopItem.setPointQuestPrice(rs.getInt("pointquestprice"));
                        shopItem.setStarCoin(rs.getInt("starcoin"));
                        shopItem.setQuestExId(rs.getInt("questexid"));
                        shopItem.setQuestExKey(rs.getString("questexkey"));
                        shopItem.setQuestExValue(rs.getInt("questexvalue"));
                        shopItem.setMaxLevel(rs.getInt("maxlevel"));
                        shopItem.setQuestId(rs.getInt("questid"));
                        shopItem.setSaleLimit(rs.getInt("salelimit"));
                        shopItem.setLevelLimited(rs.getInt("levellimited"));
                        ret.addItem(shopItem);
                    }
                }
            }
            for (Integer recharge : recharges) {
                ret.addItem(new MapleShopItem((short) 1, ii.getSlotMax(recharge), recharge, 0, (short) 0, 0, 0, (byte) 0, 0, 0, 0, 0));
            }
            rs.close();
            ps.close();

            ps = con.prepareStatement("SELECT * FROM shopranks WHERE shopid = ? ORDER BY rank ASC");
            ps.setInt(1, shopId);
            rs = ps.executeQuery();
            while (rs.next()) {
                if (ii.itemExists(rs.getInt("itemid"))) {
                    ret.ranks.add(new Pair(rs.getInt("itemid"), rs.getString("name")));
                }
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Could not load shop");
        }

        return ret;
    }

    public int getNpcId() {
        return this.npcId;
    }

    public int getId() {
        return this.id;
    }

    public List<Pair<Integer, String>> getRanks() {
        return this.ranks;
    }
}
