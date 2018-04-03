package handling.world;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import client.MapleClient;
import client.inventory.Item;
import client.inventory.ItemLoader;
import client.inventory.MapleInventoryType;
import constants.GameConstants;
import database.DatabaseConnection;
import scripting.provider.NPCChatByType;
import scripting.provider.NPCChatType;
import server.MapleInventoryManipulator;
import server.MapleItemInformationProvider;
import server.MerchItemPackage;
import server.maps.objects.User;
import tools.Pair;
import tools.StringUtil;
import net.InPacket;
import server.maps.objects.User.MapleCharacterConversationType;
import tools.packet.CField.NPCPacket;
import tools.packet.CWvsContext;
import tools.packet.PlayerShopPacket;

public class HiredMerchantHandler {

    public static final boolean UseHiredMerchant(final MapleClient c, final boolean packet) {
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
                            c.write(PlayerShopPacket.sendTitleBox());
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
            c.close();
        }
        return false;
    }

    private static byte checkExistance(final int accid, final int cid) {
        Connection con = DatabaseConnection.getConnection();
        try {
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
            return -1;
        }
    }

    public static void displayMerch(MapleClient c) {
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
                c.write(NPCPacket.getNPCTalk(9030000, NPCChatType.OK, "I don't think you have any items or money to retrieve here.\r\nThis is where you retrieve the items and mesos that you couldn't get from your Hired Merchant. You'll also need to see me as the character that opened the Personal Store.", NPCChatByType.NPC_Cancellable));
                c.getPlayer().setConversation(MapleCharacterConversationType.None);
            } else if (pack.getItems().size() <= 0) { //error fix for complainers.
                if (!check(c.getPlayer(), pack)) {
                    c.write(PlayerShopPacket.merchItem_Message((byte) 0x21));
                    return;
                }
                if (deletePackage(c.getPlayer().getAccountID(), pack.getPackageid(), c.getPlayer().getId())) {
                    //c.getPlayer().fakeRelog();
                    c.getPlayer().gainMeso(pack.getMesos(), false);
                    c.write(PlayerShopPacket.merchItem_Message((byte) 0x1d));
                    c.write(NPCPacket.getNPCTalk(9030000, NPCChatType.OK, "I see that you forgot something here right?\r\nHere is your money sir " + pack.getMesos(), NPCChatByType.NPC_Cancellable));
                    c.getPlayer().setConversation(MapleCharacterConversationType.None);
                } else {
                    c.getPlayer().dropMessage(1, "An unknown error occured.");
                }
                c.getPlayer().setConversation(MapleCharacterConversationType.None);
            } else {
                c.write(PlayerShopPacket.merchItemStore_ItemData(pack));
                //MapleInventoryManipulator.checkSpace(c, conv, conv, null);
                for (final Item item : pack.getItems()) {
                    if (c.getPlayer().getInventory(GameConstants.getInventoryType(item.getItemId())).isFull()) {
                        c.write(NPCPacket.getNPCTalk(9030000, NPCChatType.OK, "Sir, if you want your items back please clean up your inventory before you come here!", NPCChatByType.NPC_Cancellable));
                        c.getPlayer().setConversation(MapleCharacterConversationType.None);
                        break;
                    }
                    MapleInventoryManipulator.addFromDrop(c, item, true);
                    deletePackage(c.getPlayer().getAccountID(), pack.getPackageid(), c.getPlayer().getId());
                    c.write(NPCPacket.getNPCTalk(9030000, NPCChatType.OK, "I saved your items sir, next time don't forget them, have a nice day.", NPCChatByType.NPC_Cancellable));
                    c.getPlayer().setConversation(MapleCharacterConversationType.None);
                }

            }
        }
        c.write(CWvsContext.enableActions());
    }

    public static void displayMerch2(MapleClient c) {
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
                c.write(NPCPacket.getNPCTalk(9030000, NPCChatType.OK, "I don't think you have any items or money to retrive here. This is where you retrieve the items and mesos that you couldn't get from your Hired Merchant. You'll also need to see me as the character that opened the Personal Store.", NPCChatByType.NPC_Cancellable));
                c.getPlayer().setConversation(MapleCharacterConversationType.None);
            } else if (pack.getItems().size() <= 0) { //error fix for complainers.
                if (!check(c.getPlayer(), pack)) {
                    c.write(PlayerShopPacket.merchItem_Message((byte) 0x21));
                    return;
                }
                if (deletePackage(c.getPlayer().getAccountID(), pack.getPackageid(), c.getPlayer().getId())) {
                    c.getPlayer().fakeRelog();
                    c.getPlayer().gainMeso(pack.getMesos(), true);
                    c.write(PlayerShopPacket.merchItem_Message((byte) 0x1d));
                    c.getPlayer().setConversation(MapleCharacterConversationType.None);
                } else {
                    c.getPlayer().dropMessage(1, "An unknown error occured.");
                }
                c.getPlayer().setConversation(MapleCharacterConversationType.None);
            } else {
                c.write(PlayerShopPacket.merchItemStore_ItemData(pack));
                //MapleInventoryManipulator.checkSpace(c, conv, conv, null);
                for (final Item item : pack.getItems()) {
                    if (c.getPlayer().getInventory(GameConstants.getInventoryType(item.getItemId())).isFull()) {
                        c.write(NPCPacket.getNPCTalk(9030000, NPCChatType.OK, "Please clean up your inventory.", NPCChatByType.NPC_Cancellable));
                        c.getPlayer().setConversation(MapleCharacterConversationType.None);
                        break;
                    }
                    MapleInventoryManipulator.addFromDrop(c, item, true);
                    deletePackage(c.getPlayer().getAccountID(), pack.getPackageid(), c.getPlayer().getId());
                    //c.getPlayer().fakeRelog();
                    c.write(NPCPacket.getNPCTalk(9030000, NPCChatType.OK, "Your items have been claimed.", NPCChatByType.NPC_Cancellable));
                    c.getPlayer().setConversation(MapleCharacterConversationType.None);
                }

            }
        }
        c.write(CWvsContext.enableActions());
    }

    public static final void MerchantItemStore(final InPacket iPacket, final MapleClient c) {
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

    private static void requestItems(final MapleClient c, final boolean request) {
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
            c.write(PlayerShopPacket.merchItemStore((byte) 38, days, fee));
            return;
        }
        if (fee < 0) { // impossible
            c.write(PlayerShopPacket.merchItem_Message(33));
            return;
        }
        if (c.getPlayer().getMeso() < fee) {
            c.write(PlayerShopPacket.merchItem_Message(35));
            return;
        }
        if (!check(c.getPlayer(), pack)) {
            c.write(PlayerShopPacket.merchItem_Message(36));
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
            c.write(PlayerShopPacket.merchItem_Message(32));
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
            final MapleInventoryType invtype = GameConstants.getInventoryType(item.getItemId());
            if (invtype == MapleInventoryType.EQUIP) {
                eq++;
            } else if (invtype == MapleInventoryType.USE) {
                use++;
            } else if (invtype == MapleInventoryType.SETUP) {
                setup++;
            } else if (invtype == MapleInventoryType.ETC) {
                etc++;
            } else if (invtype == MapleInventoryType.CASH) {
                cash++;
            }
            if (MapleItemInformationProvider.getInstance().isPickupRestricted(item.getItemId()) && chr.haveItem(item.getItemId(), 1)) {
                return false;
            }
        }
        return chr.getInventory(MapleInventoryType.EQUIP).getNumFreeSlot() >= eq && chr.getInventory(MapleInventoryType.USE).getNumFreeSlot() >= use && chr.getInventory(MapleInventoryType.SETUP).getNumFreeSlot() >= setup && chr.getInventory(MapleInventoryType.ETC).getNumFreeSlot() >= etc && chr.getInventory(MapleInventoryType.CASH).getNumFreeSlot() >= cash;
    }

    private static boolean deletePackage(final int accid, final int packageid, final int chrId) {
        final Connection con = DatabaseConnection.getConnection();

        try {
            try (PreparedStatement ps = con.prepareStatement("DELETE from hiredmerch where accountid = ? OR packageid = ? OR characterid = ?")) {
                ps.setInt(1, accid);
                ps.setInt(2, packageid);
                ps.setInt(3, chrId);
                ps.executeUpdate();
            }
            ItemLoader.HIRED_MERCHANT.saveItems(null, packageid);
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public static final void showFredrick(MapleClient c) {
        final MerchItemPackage pack = HiredMerchantHandler.loadItemFrom_Database(c.getPlayer().getAccountID());
        c.write(PlayerShopPacket.merchItemStore_ItemData(pack));
    }

    private static MerchItemPackage loadItemFrom_Database(final int accountid) {
        final Connection con = DatabaseConnection.getConnection();

        try {
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

            Map<Long, Pair<Item, MapleInventoryType>> items = ItemLoader.HIRED_MERCHANT.loadItems(false, packageid);
            if (items != null) {
                List<Item> iters = new ArrayList<>();
                for (Pair<Item, MapleInventoryType> z : items.values()) {
                    iters.add(z.left);
                }
                pack.setItems(iters);
            }

            return pack;
        } catch (SQLException e) {
            return null;
        }
    }
}
