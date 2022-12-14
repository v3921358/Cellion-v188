package handling.world;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import client.ClientSocket;
import client.inventory.Item;
import client.inventory.ItemLoader;
import enums.InventoryType;
import constants.GameConstants;
import database.Database;
import enums.NPCInterfaceType;
import enums.NPCChatType;
import server.MapleInventoryManipulator;
import server.MapleItemInformationProvider;
import server.MerchItemPackage;
import server.maps.objects.User;
import tools.Pair;
import tools.StringUtil;
import net.InPacket;
import server.maps.objects.User.MapleCharacterConversationType;
import tools.LogHelper;
import tools.packet.CField.NPCPacket;
import tools.packet.WvsContext;
import tools.packet.PlayerShopPacket;

public class HiredMerchantHandler {

    public static final boolean UseHiredMerchant(final ClientSocket c, final boolean packet) {
        if (c.getPlayer().getMap() != null && c.getPlayer().getMap().getSharedMapResources().personalShop) {
            final byte state = checkExistance(c.getPlayer().getAccountID(), c.getPlayer().getId());

            switch (state) {
                case 1:
                    c.getPlayer().dropMessage(1, "Please claim your items from Fredrick first.");
                    break;
                case 0:
                    boolean merch = World.hasMerchant(c.getPlayer().getAccountID(), c.getPlayer().getId());
                    if (!merch) {
                        if (c.getChannelServer().isShutdown()) {
                            c.getPlayer().dropMessage(1, "The server is about to shut down.");
                            return false;
                        }
                        if (packet) {
                            c.SendPacket(PlayerShopPacket.sendTitleBox());
                        }
                        return true;
                    } else {
                        c.getPlayer().dropMessage(1, "Please close the existing store and try again.");
                    }
                    break;
                default:
                    c.getPlayer().dropMessage(1, "An unknown error occured.");
                    break;
            }
        } else {
            c.Close();
        }
        return false;
    }

    private static byte checkExistance(final int accid, final int cid) {
        try (Connection con = Database.GetConnection()) {

            try (PreparedStatement ps = con.prepareStatement("SELECT * from hiredmerch where accountid = ? OR characterid = ?")) {
                ps.setInt(1, accid);
                ps.setInt(2, cid);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        ps.close();
                        rs.close();
                        return 1;
                    }
                }
            }

            return 0;
        } catch (SQLException se) {

            LogHelper.SQL.get().info("[SQL] There was an issue with something from the database:\n", se);
            return -1;
        }
    }

    public static void displayMerch(ClientSocket c) {
        final MapleCharacterConversationType conv = c.getPlayer().getConversation();

        boolean merch = World.hasMerchant(c.getPlayer().getAccountID(), c.getPlayer().getId());
        if (merch) {
            c.getPlayer().dropMessage(1, "Please close the existing store and try again.");
            c.getPlayer().setConversation(MapleCharacterConversationType.None);

        } else if (c.getChannelServer().isShutdown()) {
            c.getPlayer().dropMessage(1, "The world is going to shut down.");
            c.getPlayer().setConversation(MapleCharacterConversationType.None);

        } else if (conv == MapleCharacterConversationType.HiredMerchant) { // Hired Merch
            final MerchItemPackage pack = loadItemFrom_Database(c.getPlayer().getAccountID());

            if (pack == null) {
                c.SendPacket(NPCPacket.getNPCTalk(9030000, NPCChatType.OK, "I don't think you have any items or money to retrieve here.\r\nThis is where you retrieve the items and mesos that you couldn't get from your Hired Merchant. You'll also need to see me as the character that opened the Personal Store.", NPCInterfaceType.NPC_Cancellable));
                c.getPlayer().setConversation(MapleCharacterConversationType.None);
            } else if (pack.getItems().size() <= 0) { //error fix for complainers.
                if (!check(c.getPlayer(), pack)) {
                    c.SendPacket(PlayerShopPacket.merchItem_Message((byte) 0x21));
                    return;
                }
                if (deletePackage(c.getPlayer().getAccountID(), pack.getPackageid(), c.getPlayer().getId())) {
                    //c.getPlayer().fakeRelog();
                    c.getPlayer().gainMeso(pack.getMesos(), false);
                    c.SendPacket(PlayerShopPacket.merchItem_Message((byte) 0x1d));
                    c.SendPacket(NPCPacket.getNPCTalk(9030000, NPCChatType.OK, "I see that you forgot something here right?\r\nHere is your money sir " + pack.getMesos(), NPCInterfaceType.NPC_Cancellable));
                    c.getPlayer().setConversation(MapleCharacterConversationType.None);
                } else {
                    c.getPlayer().dropMessage(1, "An unknown error occured.");
                }
                c.getPlayer().setConversation(MapleCharacterConversationType.None);
            } else {
                c.SendPacket(PlayerShopPacket.merchItemStore_ItemData(pack));
                //MapleInventoryManipulator.checkSpace(c, conv, conv, null);
                for (final Item item : pack.getItems()) {
                    if (c.getPlayer().getInventory(GameConstants.getInventoryType(item.getItemId())).isFull()) {
                        c.SendPacket(NPCPacket.getNPCTalk(9030000, NPCChatType.OK, "Sir, if you want your items back please clean up your inventory before you come here!", NPCInterfaceType.NPC_Cancellable));
                        c.getPlayer().setConversation(MapleCharacterConversationType.None);
                        break;
                    }
                    MapleInventoryManipulator.addFromDrop(c, item, true);
                    deletePackage(c.getPlayer().getAccountID(), pack.getPackageid(), c.getPlayer().getId());
                    c.SendPacket(NPCPacket.getNPCTalk(9030000, NPCChatType.OK, "I saved your items sir, next time don't forget them, have a nice day.", NPCInterfaceType.NPC_Cancellable));
                    c.getPlayer().setConversation(MapleCharacterConversationType.None);
                }

            }
        }
        c.SendPacket(WvsContext.enableActions());
    }

    public static void displayMerch2(ClientSocket c) {
        final MapleCharacterConversationType conv = c.getPlayer().getConversation();
        boolean merch = World.hasMerchant(c.getPlayer().getAccountID(), c.getPlayer().getId());

        if (merch) {
            c.getPlayer().dropMessage(1, "Please close the existing store and try again.");
            c.getPlayer().setConversation(MapleCharacterConversationType.None);

        } else if (c.getChannelServer().isShutdown()) {
            c.getPlayer().dropMessage(1, "The world is going to shut down.");
            c.getPlayer().setConversation(MapleCharacterConversationType.None);

        } else if (conv == MapleCharacterConversationType.HiredMerchant) { // Hired Merch
            final MerchItemPackage pack = loadItemFrom_Database(c.getPlayer().getAccountID());

            if (pack == null) {
                c.SendPacket(NPCPacket.getNPCTalk(9030000, NPCChatType.OK, "I don't think you have any items or money to retrive here. This is where you retrieve the items and mesos that you couldn't get from your Hired Merchant. You'll also need to see me as the character that opened the Personal Store.", NPCInterfaceType.NPC_Cancellable));
                c.getPlayer().setConversation(MapleCharacterConversationType.None);
            } else if (pack.getItems().size() <= 0) { //error fix for complainers.
                if (!check(c.getPlayer(), pack)) {
                    c.SendPacket(PlayerShopPacket.merchItem_Message((byte) 0x21));
                    return;
                }
                if (deletePackage(c.getPlayer().getAccountID(), pack.getPackageid(), c.getPlayer().getId())) {
                    c.getPlayer().reloadUser();
                    c.getPlayer().gainMeso(pack.getMesos(), true);
                    c.SendPacket(PlayerShopPacket.merchItem_Message((byte) 0x1d));
                    c.getPlayer().setConversation(MapleCharacterConversationType.None);
                } else {
                    c.getPlayer().dropMessage(1, "An unknown error occured.");
                }
                c.getPlayer().setConversation(MapleCharacterConversationType.None);
            } else {
                c.SendPacket(PlayerShopPacket.merchItemStore_ItemData(pack));
                //MapleInventoryManipulator.checkSpace(c, conv, conv, null);
                for (final Item item : pack.getItems()) {
                    if (c.getPlayer().getInventory(GameConstants.getInventoryType(item.getItemId())).isFull()) {
                        c.SendPacket(NPCPacket.getNPCTalk(9030000, NPCChatType.OK, "Please clean up your inventory.", NPCInterfaceType.NPC_Cancellable));
                        c.getPlayer().setConversation(MapleCharacterConversationType.None);
                        break;
                    }
                    MapleInventoryManipulator.addFromDrop(c, item, true);
                    deletePackage(c.getPlayer().getAccountID(), pack.getPackageid(), c.getPlayer().getId());
                    //c.getPlayer().fakeRelog();
                    c.SendPacket(NPCPacket.getNPCTalk(9030000, NPCChatType.OK, "Your items have been claimed.", NPCInterfaceType.NPC_Cancellable));
                    c.getPlayer().setConversation(MapleCharacterConversationType.None);
                }

            }
        }
        c.SendPacket(WvsContext.enableActions());
    }

    public static final void MerchantItemStore(final InPacket iPacket, final ClientSocket c) {
        if (c.getPlayer() == null) {
            return;
        }
        final byte operation = iPacket.DecodeByte();
        if (operation == 27 || operation == 28) { // Request, Take out
            requestItems(c, operation == 27);
        } else if (operation == 30) { // Exit
            c.getPlayer().setConversation(MapleCharacterConversationType.None);
        }
    }

    private static void requestItems(final ClientSocket c, final boolean request) {
        if (c.getPlayer().getConversation() != MapleCharacterConversationType.HiredMerchant) {
            return;
        }
        boolean merch = World.hasMerchant(c.getPlayer().getAccountID(), c.getPlayer().getId());
        if (merch) {
            c.getPlayer().dropMessage(1, "Please close the existing store and try again.");
            c.getPlayer().setConversation(MapleCharacterConversationType.None);
            return;
        }
        final MerchItemPackage pack = loadItemFrom_Database(c.getPlayer().getAccountID());
        if (pack == null) {
            c.getPlayer().dropMessage(1, "An unknown error occured.");
            return;
        } else if (c.getChannelServer().isShutdown()) {
            c.getPlayer().dropMessage(1, "The world is going to shut down.");
            c.getPlayer().setConversation(MapleCharacterConversationType.None);
            return;
        }
        final int days = StringUtil.getDaysAmount(pack.getSavedTime(), System.currentTimeMillis()); // max 100%
        final double percentage = days / 100.0;
        final int fee = (int) Math.ceil(percentage * pack.getMesos()); // if no mesos = no tax
        if (request && days > 0 && percentage > 0 && pack.getMesos() > 0 && fee > 0) {
            c.SendPacket(PlayerShopPacket.merchItemStore((byte) 38, days, fee));
            return;
        }
        if (fee < 0) { // impossible
            c.SendPacket(PlayerShopPacket.merchItem_Message(33));
            return;
        }
        if (c.getPlayer().getMeso() < fee) {
            c.SendPacket(PlayerShopPacket.merchItem_Message(35));
            return;
        }
        if (!check(c.getPlayer(), pack)) {
            c.SendPacket(PlayerShopPacket.merchItem_Message(36));
            return;
        }
        if (deletePackage(c.getPlayer().getAccountID(), pack.getPackageid(), c.getPlayer().getId())) {
            if (fee > 0) {
                c.getPlayer().gainMeso(-fee, true);
            }
            c.getPlayer().gainMeso(pack.getMesos(), false);
            for (Item item : pack.getItems()) {
                MapleInventoryManipulator.addFromDrop(c, item, false);
            }
            c.SendPacket(PlayerShopPacket.merchItem_Message(32));
        } else {
            c.getPlayer().dropMessage(1, "An unknown error occured.");
        }
    }

    private static boolean check(final User chr, final MerchItemPackage pack) {
        if (chr.getMeso() + pack.getMesos() < 0) {
            return false;
        }
        byte eq = 0, use = 0, setup = 0, etc = 0, cash = 0;
        for (Item item : pack.getItems()) {
            final InventoryType invtype = GameConstants.getInventoryType(item.getItemId());
            if (invtype == InventoryType.EQUIP) {
                eq++;
            } else if (invtype == InventoryType.USE) {
                use++;
            } else if (invtype == InventoryType.SETUP) {
                setup++;
            } else if (invtype == InventoryType.ETC) {
                etc++;
            } else if (invtype == InventoryType.CASH) {
                cash++;
            }
            if (MapleItemInformationProvider.getInstance().isPickupRestricted(item.getItemId()) && chr.haveItem(item.getItemId(), 1)) {
                return false;
            }
        }
        return chr.getInventory(InventoryType.EQUIP).getNumFreeSlot() >= eq && chr.getInventory(InventoryType.USE).getNumFreeSlot() >= use && chr.getInventory(InventoryType.SETUP).getNumFreeSlot() >= setup && chr.getInventory(InventoryType.ETC).getNumFreeSlot() >= etc && chr.getInventory(InventoryType.CASH).getNumFreeSlot() >= cash;
    }

    private static boolean deletePackage(final int accid, final int packageid, final int chrId) {
        try (Connection con = Database.GetConnection()) {

            try (PreparedStatement ps = con.prepareStatement("DELETE from hiredmerch where accountid = ? OR packageid = ? OR characterid = ?")) {
                ps.setInt(1, accid);
                ps.setInt(2, packageid);
                ps.setInt(3, chrId);
                ps.executeUpdate();
            }
            ItemLoader.HIRED_MERCHANT.saveItems(null, packageid, con);

            return true;
        } catch (SQLException e) {

            LogHelper.SQL.get().info("[SQL] There was an issue with something from the database:\n", e);
            return false;
        }
    }

    public static final void showFredrick(ClientSocket c) {
        final MerchItemPackage pack = HiredMerchantHandler.loadItemFrom_Database(c.getPlayer().getAccountID());
        c.SendPacket(PlayerShopPacket.merchItemStore_ItemData(pack));
    }

    private static MerchItemPackage loadItemFrom_Database(final int accountid) {
        try (Connection con = Database.GetConnection()) {

            ResultSet rs;

            final int packageid;
            final MerchItemPackage pack;
            try (PreparedStatement ps = con.prepareStatement("SELECT * from hiredmerch where accountid = ?")) {
                ps.setInt(1, accountid);
                rs = ps.executeQuery();
                if (!rs.next()) {
                    ps.close();
                    rs.close();
                    return null;
                }
                packageid = rs.getInt("PackageId");
                pack = new MerchItemPackage();
                pack.setPackageid(packageid);
                pack.setMesos(rs.getInt("Mesos"));
                pack.setSavedTime(rs.getLong("time"));
            }
            rs.close();

            Map<Long, Pair<Item, InventoryType>> items = ItemLoader.HIRED_MERCHANT.loadItems(false, packageid, con);
            if (items != null) {
                List<Item> iters = new ArrayList<>();
                for (Pair<Item, InventoryType> z : items.values()) {
                    iters.add(z.left);
                }
                pack.setItems(iters);
            }

            return pack;
        } catch (SQLException e) {

            LogHelper.SQL.get().info("[SQL] There was an issue with something from the database:\n", e);
            return null;
        }
    }
}
