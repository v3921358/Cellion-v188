package tools.packet;

import java.util.List;

import client.MapleClient;
import client.inventory.Item;
import handling.game.PlayerInteractionHandler;
import service.SendPacketOpcode;
import net.OutPacket;

import server.MerchItemPackage;
import server.maps.objects.User;
import server.stores.AbstractPlayerStore.BoughtItem;
import server.stores.HiredMerchant;
import server.stores.IMaplePlayerShop;
import server.stores.MapleMiniGame;
import server.stores.MaplePlayerShop;
import server.stores.MaplePlayerShopItem;
import tools.Pair;

public class PlayerShopPacket {

    public static OutPacket sendTitleBox() {
        return sendTitleBox(7); // SendOpenShopRequest
    }

    public static OutPacket sendTitleBox(int mode) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.EntrustedShopCheckResult.getValue());
        oPacket.EncodeByte(mode);
        if ((mode == 8) || (mode == 16)) {
            oPacket.EncodeInt(0);
            oPacket.EncodeByte(0);
        } else if (mode == 13) {
            oPacket.EncodeInt(0);
        } else if (mode == 14) {
            oPacket.EncodeByte(0);
        } else if (mode == 18) {
            oPacket.EncodeByte(1);
            oPacket.EncodeString("");
        }

        return oPacket;
    }

    public static OutPacket requestShopPic(final int oid) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.EntrustedShopCheckResult.getValue());
        oPacket.EncodeByte(17);
        oPacket.EncodeInt(oid);
        oPacket.EncodeShort(0);
        oPacket.EncodeLong(0L);

        return oPacket;
    }

    public static final OutPacket addCharBox(final User c, final int type) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.UserMiniRoomBalloon.getValue());
        oPacket.EncodeInt(c.getId());
        PacketHelper.addAnnounceBox(oPacket, c);

        return oPacket;
    }

    public static final OutPacket removeCharBox(final User c) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.UserMiniRoomBalloon.getValue());
        oPacket.EncodeInt(c.getId());
        oPacket.EncodeByte(0);

        return oPacket;
    }

    public static final OutPacket sendPlayerShopBox(final User c) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.UserMiniRoomBalloon.getValue());
        oPacket.EncodeInt(c.getId());
        PacketHelper.addAnnounceBox(oPacket, c);

        return oPacket;
    }

    public static OutPacket getHiredMerch(User chr, HiredMerchant merch, boolean firstTime) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.MiniRoom.getValue());
        oPacket.EncodeByte(20);//was11
        oPacket.EncodeByte(6);
        oPacket.EncodeByte(7);
        oPacket.EncodeShort(merch.getVisitorSlot(chr));
        oPacket.EncodeInt(merch.getItemId());
        oPacket.EncodeString("Hired Merchant");
        for (Pair storechr : merch.getVisitors()) {
            oPacket.EncodeByte(((Byte) storechr.left).byteValue());
            CField.writeCharacterLook(oPacket, (User) storechr.right, false);//MapleCharacterLook
            oPacket.EncodeString(((User) storechr.right).getName());
            oPacket.EncodeShort(((User) storechr.right).getJob());
        }
        oPacket.EncodeByte(-1);
        oPacket.EncodeShort(0);
        oPacket.EncodeString(merch.getOwnerName());
        if (merch.isOwner(chr)) {
            oPacket.EncodeInt(merch.getTimeLeft());
            oPacket.EncodeByte(firstTime ? 1 : 0);
            oPacket.EncodeByte(merch.getBoughtItems().size());
            for (final BoughtItem SoldItem : merch.getBoughtItems()) {
                oPacket.EncodeInt(SoldItem.id);
                oPacket.EncodeShort(SoldItem.quantity);
                oPacket.EncodeLong(SoldItem.totalPrice);
                oPacket.EncodeString(SoldItem.buyer);
            }
            oPacket.EncodeLong(merch.getMeso());
        }
        oPacket.EncodeInt(263);
        oPacket.EncodeString(merch.getDescription());
        oPacket.EncodeByte(16);
        oPacket.EncodeLong(merch.getMeso());
        oPacket.EncodeByte(merch.getItems().size());
        for (MaplePlayerShopItem item : merch.getItems()) {
            oPacket.EncodeShort(item.bundles);
            oPacket.EncodeShort(item.item.getQuantity());
            oPacket.EncodeLong(item.price);
            PacketHelper.addItemInfo(oPacket, item.item);
        }
        oPacket.EncodeShort(0);

        return oPacket;
    }

    public static final OutPacket getPlayerStore(final User chr, final boolean firstTime) {
        final OutPacket oPacket = new OutPacket(SendPacketOpcode.MiniRoom.getValue());
        IMaplePlayerShop ips = chr.getPlayerShop();
        oPacket.EncodeByte(11);
        switch (ips.getShopType()) {
            case 2:
                oPacket.EncodeByte(4);
                oPacket.EncodeByte(4);
                break;
            case 3:
                oPacket.EncodeByte(2);
                oPacket.EncodeByte(2);
                break;
            case 4:
                oPacket.EncodeByte(1);
                oPacket.EncodeByte(2);
                break;
        }
        oPacket.EncodeShort(ips.getVisitorSlot(chr));
        CField.writeCharacterLook(oPacket, ((MaplePlayerShop) ips).getMCOwner(), false);
        oPacket.EncodeString(ips.getOwnerName());
        oPacket.EncodeShort(((MaplePlayerShop) ips).getMCOwner().getJob());
        for (final Pair<Byte, User> storechr : ips.getVisitors()) {
            oPacket.EncodeByte(storechr.left);
            CField.writeCharacterLook(oPacket, storechr.right, false);
            oPacket.EncodeString(storechr.right.getName());
            oPacket.EncodeShort(storechr.right.getJob());
        }
        oPacket.EncodeByte(255);
        oPacket.EncodeString(ips.getDescription());
        oPacket.EncodeByte(10);
        oPacket.EncodeByte(ips.getItems().size());

        for (final MaplePlayerShopItem item : ips.getItems()) {
            oPacket.EncodeShort(item.bundles);
            oPacket.EncodeShort(item.item.getQuantity());
            oPacket.EncodeInt(item.price);
            PacketHelper.addItemInfo(oPacket, item.item);
        }
        return oPacket;
    }

    public static final OutPacket shopChat(final String message, final int slot) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.MiniRoom.getValue());
        oPacket.EncodeByte(24);//was15
        oPacket.EncodeByte(25);//was15
        oPacket.EncodeByte(slot);
        oPacket.EncodeString(message);

        return oPacket;
    }

    public static final OutPacket shopErrorMessage(final int error, final int type) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.MiniRoom.getValue());
        oPacket.EncodeByte(28);//was18
        oPacket.EncodeByte(type);
        oPacket.EncodeByte(error);

        return oPacket;
    }

    public static final OutPacket spawnHiredMerchant(final HiredMerchant hm) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.EmployeeEnterField.getValue());
        oPacket.EncodeInt(hm.getOwnerId());
        oPacket.EncodeInt(hm.getItemId());
        oPacket.EncodePosition(hm.getTruePosition());
        oPacket.EncodeShort(0);
        oPacket.EncodeString(hm.getOwnerName());
        PacketHelper.addInteraction(oPacket, hm);
//        System.err.println(hm.getItemId());
        return oPacket;
    }

    public static final OutPacket destroyHiredMerchant(final int id) {
        OutPacket oPacket = new OutPacket(SendPacketOpcode.EmployeeLeaveField.getValue());
        oPacket.EncodeInt(id);

        return oPacket;
    }

    public static final OutPacket shopItemUpdate(final IMaplePlayerShop shop) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.MiniRoom.getValue());
        oPacket.EncodeByte(77);//was50
        if (shop.getShopType() == 1) {
            oPacket.EncodeLong(0L);
        }
        oPacket.EncodeByte(shop.getItems().size());
        for (final MaplePlayerShopItem item : shop.getItems()) {
            oPacket.EncodeShort(item.bundles);
            oPacket.EncodeShort(item.item.getQuantity());
            oPacket.EncodeLong(item.price);
            PacketHelper.addItemInfo(oPacket, item.item);
        }
        oPacket.EncodeShort(0);

        return oPacket;
    }

    public static final OutPacket shopVisitorAdd(final User chr, final int slot) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.MiniRoom.getValue());
        oPacket.EncodeByte(PlayerInteractionHandler.Interaction.VISIT.action);
//        oPacket.encode(19);//was10
        oPacket.EncodeByte(slot);
        CField.writeCharacterLook(oPacket, chr, false);
        oPacket.EncodeString(chr.getName());
        oPacket.EncodeShort(chr.getJob());

        return oPacket;
    }

    public static final OutPacket shopVisitorLeave(final byte slot) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.MiniRoom.getValue());
        oPacket.EncodeByte(PlayerInteractionHandler.Interaction.EXIT.action);
        oPacket.EncodeByte(slot);

        return oPacket;
    } // Fix from RZ

    public static final OutPacket Merchant_Buy_Error(final byte message) {

        // 2 = You have not enough meso
        OutPacket oPacket = new OutPacket(SendPacketOpcode.MiniRoom.getValue());
        oPacket.EncodeByte(44);
        oPacket.EncodeByte(message);

        return oPacket;
    }

    public static final OutPacket updateHiredMerchant(final HiredMerchant shop) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.EmployeeMiniRoomBalloon.getValue());
        oPacket.EncodeInt(shop.getOwnerId());
        PacketHelper.addInteraction(oPacket, shop);

        return oPacket;
    }

    public static final OutPacket merchItem_Message(final int op) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.StoreBankGetAllResult.getValue());
        oPacket.EncodeByte(op);

        return oPacket;
    }

    public static final OutPacket merchItemStore(final byte op, final int days, final int fees) {

        // 40: This is currently unavailable.\r\nPlease try again later
        OutPacket oPacket = new OutPacket(SendPacketOpcode.StoreBankResult.getValue());
        oPacket.EncodeByte(op);
        switch (op) {
            case 39:
                oPacket.EncodeInt(999999999); // ? 
                oPacket.EncodeInt(999999999); // mapid
                oPacket.EncodeByte(0); // >= -2 channel
                // if cc -1 or map = 999,999,999 : I don't think you have any items or money to retrieve here. This is where you retrieve the items and mesos that you couldn't get from your Hired Merchant. You'll also need to see me as the character that opened the Personal Store.
                //Your Personal Store is open #bin Channel %s, Free Market %d#k.\r\nIf you need me, then please close your personal store first before seeing me.
                break;
            case 38:
                oPacket.EncodeInt(days); // % tax or days, 1 day = 1%
                oPacket.EncodeInt(fees); // feees
                break;
        }

        return oPacket;
    }

    public static final OutPacket merchItemStore_ItemData(final MerchItemPackage pack) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.StoreBankResult.getValue());
        oPacket.EncodeByte(38);
        oPacket.EncodeInt(9030000); // Fredrick
        oPacket.EncodeByte(16); // max items?
        oPacket.EncodeLong(126L); // ?
        oPacket.EncodeLong(pack.getMesos());
        oPacket.EncodeByte(0);
        oPacket.EncodeByte(pack.getItems().size());
        for (final Item item : pack.getItems()) {
            PacketHelper.addItemInfo(oPacket, item);
        }
        oPacket.Fill(0, 3);

        return oPacket;
    }

    public static OutPacket getMiniGame(MapleClient c, MapleMiniGame minigame) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.MiniRoom.getValue());
        oPacket.EncodeByte(10);
        oPacket.EncodeByte(minigame.getGameType());
        oPacket.EncodeByte(minigame.getMaxSize());
        oPacket.EncodeShort(minigame.getVisitorSlot(c.getPlayer()));
        CField.writeCharacterLook(oPacket, minigame.getMCOwner(), false);
        oPacket.EncodeString(minigame.getOwnerName());
        oPacket.EncodeShort(minigame.getMCOwner().getJob());
        for (Pair visitorz : minigame.getVisitors()) {
            oPacket.EncodeByte(((Byte) visitorz.getLeft()).byteValue());
            CField.writeCharacterLook(oPacket, (User) visitorz.getRight(), false);//MapleCharacterLook
            oPacket.EncodeString(((User) visitorz.getRight()).getName());
            oPacket.EncodeShort(((User) visitorz.getRight()).getJob());
        }
        oPacket.EncodeByte(-1);
        oPacket.EncodeByte(0);
        addGameInfo(oPacket, minigame.getMCOwner(), minigame);
        for (Pair visitorz : minigame.getVisitors()) {
            oPacket.EncodeByte(((Byte) visitorz.getLeft()).byteValue());
            addGameInfo(oPacket, (User) visitorz.getRight(), minigame);
        }
        oPacket.EncodeByte(-1);
        oPacket.EncodeString(minigame.getDescription());
        oPacket.EncodeShort(minigame.getPieceType());
        return oPacket;
    }

    public static OutPacket getMiniGameReady(boolean ready) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.MiniRoom.getValue());
        oPacket.EncodeByte(ready ? 56 : 60);
        return oPacket;
    }

    public static OutPacket getMiniGameExitAfter(boolean ready) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.MiniRoom.getValue());
        oPacket.EncodeByte(ready ? 54 : 58);
        return oPacket;
    }

    public static OutPacket getMiniGameStart(int loser) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.MiniRoom.getValue());
        oPacket.EncodeByte(62);
        oPacket.EncodeByte(loser == 1 ? 0 : 1);
        return oPacket;
    }

    public static OutPacket getMiniGameSkip(int slot) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.MiniRoom.getValue());
        oPacket.EncodeByte(64);

        oPacket.EncodeByte(slot);
        return oPacket;
    }

    public static OutPacket getMiniGameRequestTie() {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.MiniRoom.getValue());
        oPacket.EncodeByte(51);
        return oPacket;
    }

    public static OutPacket getMiniGameDenyTie() {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.MiniRoom.getValue());
        oPacket.EncodeByte(50);
        return oPacket;
    }

    public static OutPacket getMiniGameFull() {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.MiniRoom.getValue());
        oPacket.EncodeShort(10);
        oPacket.EncodeByte(2);
        return oPacket;
    }

    public static OutPacket getMiniGameMoveOmok(int move1, int move2, int move3) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.MiniRoom.getValue());
        oPacket.EncodeByte(65);
        oPacket.EncodeInt(move1);
        oPacket.EncodeInt(move2);
        oPacket.EncodeByte(move3);
        return oPacket;
    }

    public static OutPacket getMiniGameNewVisitor(User c, int slot, MapleMiniGame game) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.MiniRoom.getValue());
        oPacket.EncodeByte(9);
        oPacket.EncodeByte(slot);
        CField.writeCharacterLook(oPacket, c, false);
        oPacket.EncodeString(c.getName());
        oPacket.EncodeShort(c.getJob());
        addGameInfo(oPacket, c, game);
        return oPacket;
    }

    public static void addGameInfo(OutPacket oPacket, User chr, MapleMiniGame game) {
        oPacket.EncodeInt(game.getGameType());
        oPacket.EncodeInt(game.getWins(chr));
        oPacket.EncodeInt(game.getTies(chr));
        oPacket.EncodeInt(game.getLosses(chr));
        oPacket.EncodeInt(game.getScore(chr));
    }

    public static OutPacket getMiniGameClose(byte number) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.MiniRoom.getValue());
        oPacket.EncodeByte(18);
        oPacket.EncodeByte(1);
        oPacket.EncodeByte(number);
        return oPacket;
    }

    public static OutPacket getMatchCardStart(MapleMiniGame game, int loser) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.MiniRoom.getValue());
        oPacket.EncodeByte(62);
        oPacket.EncodeByte(loser == 1 ? 0 : 1);
        int times = game.getPieceType() == 2 ? 30 : game.getPieceType() == 1 ? 20 : 12;
        oPacket.EncodeByte(times);
        for (int i = 1; i <= times; i++) {
            oPacket.EncodeInt(game.getCardId(i));
        }
        return oPacket;
    }

    public static OutPacket getMatchCardSelect(int turn, int slot, int firstslot, int type) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.MiniRoom.getValue());
        oPacket.EncodeByte(69);
        oPacket.EncodeByte(turn);
        oPacket.EncodeByte(slot);
        if (turn == 0) {
            oPacket.EncodeByte(firstslot);
            oPacket.EncodeByte(type);
        }
        return oPacket;
    }

    public static OutPacket getMiniGameResult(MapleMiniGame game, int type, int x) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.MiniRoom.getValue());
        oPacket.EncodeByte(63);
        oPacket.EncodeByte(type);
        game.setPoints(x, type);
        if (type != 0) {
            game.setPoints(x == 1 ? 0 : 1, type == 2 ? 0 : 1);
        }
        if (type != 1) {
            if (type == 0) {
                oPacket.EncodeByte(x == 1 ? 0 : 1);
            } else {
                oPacket.EncodeByte(x);
            }
        }
        addGameInfo(oPacket, game.getMCOwner(), game);
        for (Pair visitorz : game.getVisitors()) {
            addGameInfo(oPacket, (User) visitorz.right, game);
        }

        return oPacket;

    }

    public static final OutPacket MerchantBlackListView(final List<String> blackList) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.MiniRoom.getValue());
        oPacket.EncodeByte(39);
        oPacket.EncodeShort(blackList.size());
        for (String visit : blackList) {
            oPacket.EncodeString(visit);
        }
        return oPacket;
    }

    public static final OutPacket MerchantVisitorView(List<String> visitor) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.MiniRoom.getValue());
        oPacket.EncodeByte(38);
        oPacket.EncodeShort(visitor.size());
        for (String visit : visitor) {
            oPacket.EncodeString(visit);
            oPacket.EncodeInt(1);
        }
        return oPacket;
    }
}
