package tools.packet;

import java.util.List;

import client.MapleClient;
import client.inventory.Item;
import handling.game.PlayerInteractionHandler;
import service.SendPacketOpcode;
import net.OutPacket;
import net.Packet;
import server.MerchItemPackage;
import server.maps.objects.MapleCharacter;
import server.stores.AbstractPlayerStore.BoughtItem;
import server.stores.HiredMerchant;
import server.stores.IMaplePlayerShop;
import server.stores.MapleMiniGame;
import server.stores.MaplePlayerShop;
import server.stores.MaplePlayerShopItem;
import tools.Pair;

public class PlayerShopPacket {

    public static Packet sendTitleBox() {
        return sendTitleBox(7); // SendOpenShopRequest
    }

    public static Packet sendTitleBox(int mode) {
        OutPacket oPacket = new OutPacket(8);

        oPacket.EncodeShort(SendPacketOpcode.EntrustedShopCheckResult.getValue());
        oPacket.Encode(mode);
        if ((mode == 8) || (mode == 16)) {
            oPacket.EncodeInteger(0);
            oPacket.Encode(0);
        } else if (mode == 13) {
            oPacket.EncodeInteger(0);
        } else if (mode == 14) {
            oPacket.Encode(0);
        } else if (mode == 18) {
            oPacket.Encode(1);
            oPacket.EncodeString("");
        }

        return oPacket.ToPacket();
    }

    public static Packet requestShopPic(final int oid) {
        final OutPacket oPacket = new OutPacket(17);

        oPacket.EncodeShort(SendPacketOpcode.EntrustedShopCheckResult.getValue());
        oPacket.Encode(17);
        oPacket.EncodeInteger(oid);
        oPacket.EncodeShort(0);
        oPacket.EncodeLong(0L);

        return oPacket.ToPacket();
    }

    public static final Packet addCharBox(final MapleCharacter c, final int type) {
        final OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.UserMiniRoomBalloon.getValue());
        oPacket.EncodeInteger(c.getId());
        PacketHelper.addAnnounceBox(oPacket, c);

        return oPacket.ToPacket();
    }

    public static final Packet removeCharBox(final MapleCharacter c) {
        final OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.UserMiniRoomBalloon.getValue());
        oPacket.EncodeInteger(c.getId());
        oPacket.Encode(0);

        return oPacket.ToPacket();
    }

    public static final Packet sendPlayerShopBox(final MapleCharacter c) {
        final OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.UserMiniRoomBalloon.getValue());
        oPacket.EncodeInteger(c.getId());
        PacketHelper.addAnnounceBox(oPacket, c);

        return oPacket.ToPacket();
    }

    public static Packet getHiredMerch(MapleCharacter chr, HiredMerchant merch, boolean firstTime) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.MiniRoom.getValue());
        oPacket.Encode(20);//was11
        oPacket.Encode(6);
        oPacket.Encode(7);
        oPacket.EncodeShort(merch.getVisitorSlot(chr));
        oPacket.EncodeInteger(merch.getItemId());
        oPacket.EncodeString("Hired Merchant");
        for (Pair storechr : merch.getVisitors()) {
            oPacket.Encode(((Byte) storechr.left).byteValue());
            CField.writeCharacterLook(oPacket, (MapleCharacter) storechr.right, false);//MapleCharacterLook
            oPacket.EncodeString(((MapleCharacter) storechr.right).getName());
            oPacket.EncodeShort(((MapleCharacter) storechr.right).getJob());
        }
        oPacket.Encode(-1);
        oPacket.EncodeShort(0);
        oPacket.EncodeString(merch.getOwnerName());
        if (merch.isOwner(chr)) {
            oPacket.EncodeInteger(merch.getTimeLeft());
            oPacket.Encode(firstTime ? 1 : 0);
            oPacket.Encode(merch.getBoughtItems().size());
            for (final BoughtItem SoldItem : merch.getBoughtItems()) {
                oPacket.EncodeInteger(SoldItem.id);
                oPacket.EncodeShort(SoldItem.quantity);
                oPacket.EncodeLong(SoldItem.totalPrice);
                oPacket.EncodeString(SoldItem.buyer);
            }
            oPacket.EncodeLong(merch.getMeso());
        }
        oPacket.EncodeInteger(263);
        oPacket.EncodeString(merch.getDescription());
        oPacket.Encode(16);
        oPacket.EncodeLong(merch.getMeso());
        oPacket.Encode(merch.getItems().size());
        for (MaplePlayerShopItem item : merch.getItems()) {
            oPacket.EncodeShort(item.bundles);
            oPacket.EncodeShort(item.item.getQuantity());
            oPacket.EncodeLong(item.price);
            PacketHelper.addItemInfo(oPacket, item.item);
        }
        oPacket.EncodeShort(0);

        return oPacket.ToPacket();
    }

    public static final Packet getPlayerStore(final MapleCharacter chr, final boolean firstTime) {
        final OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.MiniRoom.getValue());
        IMaplePlayerShop ips = chr.getPlayerShop();
        oPacket.Encode(11);
        switch (ips.getShopType()) {
            case 2:
                oPacket.Encode(4);
                oPacket.Encode(4);
                break;
            case 3:
                oPacket.Encode(2);
                oPacket.Encode(2);
                break;
            case 4:
                oPacket.Encode(1);
                oPacket.Encode(2);
                break;
        }
        oPacket.EncodeShort(ips.getVisitorSlot(chr));
        CField.writeCharacterLook(oPacket, ((MaplePlayerShop) ips).getMCOwner(), false);
        oPacket.EncodeString(ips.getOwnerName());
        oPacket.EncodeShort(((MaplePlayerShop) ips).getMCOwner().getJob());
        for (final Pair<Byte, MapleCharacter> storechr : ips.getVisitors()) {
            oPacket.Encode(storechr.left);
            CField.writeCharacterLook(oPacket, storechr.right, false);
            oPacket.EncodeString(storechr.right.getName());
            oPacket.EncodeShort(storechr.right.getJob());
        }
        oPacket.Encode(255);
        oPacket.EncodeString(ips.getDescription());
        oPacket.Encode(10);
        oPacket.Encode(ips.getItems().size());

        for (final MaplePlayerShopItem item : ips.getItems()) {
            oPacket.EncodeShort(item.bundles);
            oPacket.EncodeShort(item.item.getQuantity());
            oPacket.EncodeInteger(item.price);
            PacketHelper.addItemInfo(oPacket, item.item);
        }
        return oPacket.ToPacket();
    }

    public static final Packet shopChat(final String message, final int slot) {
        final OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.MiniRoom.getValue());
        oPacket.Encode(24);//was15
        oPacket.Encode(25);//was15
        oPacket.Encode(slot);
        oPacket.EncodeString(message);

        return oPacket.ToPacket();
    }

    public static final Packet shopErrorMessage(final int error, final int type) {
        final OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.MiniRoom.getValue());
        oPacket.Encode(28);//was18
        oPacket.Encode(type);
        oPacket.Encode(error);

        return oPacket.ToPacket();
    }

    public static final Packet spawnHiredMerchant(final HiredMerchant hm) {
        final OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.EmployeeEnterField.getValue());
        oPacket.EncodeInteger(hm.getOwnerId());
        oPacket.EncodeInteger(hm.getItemId());
        oPacket.EncodePosition(hm.getTruePosition());
        oPacket.EncodeShort(0);
        oPacket.EncodeString(hm.getOwnerName());
        PacketHelper.addInteraction(oPacket, hm);
//        System.err.println(hm.getItemId());
        return oPacket.ToPacket();
    }

    public static final Packet destroyHiredMerchant(final int id) {
        final OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.EmployeeLeaveField.getValue());
        oPacket.EncodeInteger(id);

        return oPacket.ToPacket();
    }

    public static final Packet shopItemUpdate(final IMaplePlayerShop shop) {
        final OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.MiniRoom.getValue());
        oPacket.Encode(77);//was50
        if (shop.getShopType() == 1) {
            oPacket.EncodeLong(0L);
        }
        oPacket.Encode(shop.getItems().size());
        for (final MaplePlayerShopItem item : shop.getItems()) {
            oPacket.EncodeShort(item.bundles);
            oPacket.EncodeShort(item.item.getQuantity());
            oPacket.EncodeLong(item.price);
            PacketHelper.addItemInfo(oPacket, item.item);
        }
        oPacket.EncodeShort(0);

        return oPacket.ToPacket();
    }

    public static final Packet shopVisitorAdd(final MapleCharacter chr, final int slot) {
        final OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.MiniRoom.getValue());
        oPacket.Encode(PlayerInteractionHandler.Interaction.VISIT.action);
//        oPacket.encode(19);//was10
        oPacket.Encode(slot);
        CField.writeCharacterLook(oPacket, chr, false);
        oPacket.EncodeString(chr.getName());
        oPacket.EncodeShort(chr.getJob());

        return oPacket.ToPacket();
    }

    public static final Packet shopVisitorLeave(final byte slot) {
        final OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.MiniRoom.getValue());
        oPacket.Encode(PlayerInteractionHandler.Interaction.EXIT.action);
        oPacket.Encode(slot);

        return oPacket.ToPacket();
    } // Fix from RZ

    public static final Packet Merchant_Buy_Error(final byte message) {
        final OutPacket oPacket = new OutPacket(80);

        // 2 = You have not enough meso
        oPacket.EncodeShort(SendPacketOpcode.MiniRoom.getValue());
        oPacket.Encode(44);
        oPacket.Encode(message);

        return oPacket.ToPacket();
    }

    public static final Packet updateHiredMerchant(final HiredMerchant shop) {
        final OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.EmployeeMiniRoomBalloon.getValue());
        oPacket.EncodeInteger(shop.getOwnerId());
        PacketHelper.addInteraction(oPacket, shop);

        return oPacket.ToPacket();
    }

    public static final Packet merchItem_Message(final int op) {
        final OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.StoreBankGetAllResult.getValue());
        oPacket.Encode(op);

        return oPacket.ToPacket();
    }

    public static final Packet merchItemStore(final byte op, final int days, final int fees) {
        final OutPacket oPacket = new OutPacket(80);

        // 40: This is currently unavailable.\r\nPlease try again later
        oPacket.EncodeShort(SendPacketOpcode.StoreBankResult.getValue());
        oPacket.Encode(op);
        switch (op) {
            case 39:
                oPacket.EncodeInteger(999999999); // ? 
                oPacket.EncodeInteger(999999999); // mapid
                oPacket.Encode(0); // >= -2 channel
                // if cc -1 or map = 999,999,999 : I don't think you have any items or money to retrieve here. This is where you retrieve the items and mesos that you couldn't get from your Hired Merchant. You'll also need to see me as the character that opened the Personal Store.
                //Your Personal Store is open #bin Channel %s, Free Market %d#k.\r\nIf you need me, then please close your personal store first before seeing me.
                break;
            case 38:
                oPacket.EncodeInteger(days); // % tax or days, 1 day = 1%
                oPacket.EncodeInteger(fees); // feees
                break;
        }

        return oPacket.ToPacket();
    }

    public static final Packet merchItemStore_ItemData(final MerchItemPackage pack) {
        final OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.StoreBankResult.getValue());
        oPacket.Encode(38);
        oPacket.EncodeInteger(9030000); // Fredrick
        oPacket.Encode(16); // max items?
        oPacket.EncodeLong(126L); // ?
        oPacket.EncodeLong(pack.getMesos());
        oPacket.Encode(0);
        oPacket.Encode(pack.getItems().size());
        for (final Item item : pack.getItems()) {
            PacketHelper.addItemInfo(oPacket, item);
        }
        oPacket.Fill(0, 3);

        return oPacket.ToPacket();
    }

    public static Packet getMiniGame(MapleClient c, MapleMiniGame minigame) {
        OutPacket oPacket = new OutPacket(80);
        oPacket.EncodeShort(SendPacketOpcode.MiniRoom.getValue());
        oPacket.Encode(10);
        oPacket.Encode(minigame.getGameType());
        oPacket.Encode(minigame.getMaxSize());
        oPacket.EncodeShort(minigame.getVisitorSlot(c.getPlayer()));
        CField.writeCharacterLook(oPacket, minigame.getMCOwner(), false);
        oPacket.EncodeString(minigame.getOwnerName());
        oPacket.EncodeShort(minigame.getMCOwner().getJob());
        for (Pair visitorz : minigame.getVisitors()) {
            oPacket.Encode(((Byte) visitorz.getLeft()).byteValue());
            CField.writeCharacterLook(oPacket, (MapleCharacter) visitorz.getRight(), false);//MapleCharacterLook
            oPacket.EncodeString(((MapleCharacter) visitorz.getRight()).getName());
            oPacket.EncodeShort(((MapleCharacter) visitorz.getRight()).getJob());
        }
        oPacket.Encode(-1);
        oPacket.Encode(0);
        addGameInfo(oPacket, minigame.getMCOwner(), minigame);
        for (Pair visitorz : minigame.getVisitors()) {
            oPacket.Encode(((Byte) visitorz.getLeft()).byteValue());
            addGameInfo(oPacket, (MapleCharacter) visitorz.getRight(), minigame);
        }
        oPacket.Encode(-1);
        oPacket.EncodeString(minigame.getDescription());
        oPacket.EncodeShort(minigame.getPieceType());
        return oPacket.ToPacket();
    }

    public static Packet getMiniGameReady(boolean ready) {
        OutPacket oPacket = new OutPacket(80);
        oPacket.EncodeShort(SendPacketOpcode.MiniRoom.getValue());
        oPacket.Encode(ready ? 56 : 60);
        return oPacket.ToPacket();
    }

    public static Packet getMiniGameExitAfter(boolean ready) {
        OutPacket oPacket = new OutPacket(80);
        oPacket.EncodeShort(SendPacketOpcode.MiniRoom.getValue());
        oPacket.Encode(ready ? 54 : 58);
        return oPacket.ToPacket();
    }

    public static Packet getMiniGameStart(int loser) {
        OutPacket oPacket = new OutPacket(80);
        oPacket.EncodeShort(SendPacketOpcode.MiniRoom.getValue());
        oPacket.Encode(62);
        oPacket.Encode(loser == 1 ? 0 : 1);
        return oPacket.ToPacket();
    }

    public static Packet getMiniGameSkip(int slot) {
        OutPacket oPacket = new OutPacket(80);
        oPacket.EncodeShort(SendPacketOpcode.MiniRoom.getValue());
        oPacket.Encode(64);

        oPacket.Encode(slot);
        return oPacket.ToPacket();
    }

    public static Packet getMiniGameRequestTie() {
        OutPacket oPacket = new OutPacket(80);
        oPacket.EncodeShort(SendPacketOpcode.MiniRoom.getValue());
        oPacket.Encode(51);
        return oPacket.ToPacket();
    }

    public static Packet getMiniGameDenyTie() {
        OutPacket oPacket = new OutPacket(80);
        oPacket.EncodeShort(SendPacketOpcode.MiniRoom.getValue());
        oPacket.Encode(50);
        return oPacket.ToPacket();
    }

    public static Packet getMiniGameFull() {
        OutPacket oPacket = new OutPacket(80);
        oPacket.EncodeShort(SendPacketOpcode.MiniRoom.getValue());
        oPacket.EncodeShort(10);
        oPacket.Encode(2);
        return oPacket.ToPacket();
    }

    public static Packet getMiniGameMoveOmok(int move1, int move2, int move3) {
        OutPacket oPacket = new OutPacket(80);
        oPacket.EncodeShort(SendPacketOpcode.MiniRoom.getValue());
        oPacket.Encode(65);
        oPacket.EncodeInteger(move1);
        oPacket.EncodeInteger(move2);
        oPacket.Encode(move3);
        return oPacket.ToPacket();
    }

    public static Packet getMiniGameNewVisitor(MapleCharacter c, int slot, MapleMiniGame game) {
        OutPacket oPacket = new OutPacket(80);
        oPacket.EncodeShort(SendPacketOpcode.MiniRoom.getValue());
        oPacket.Encode(9);
        oPacket.Encode(slot);
        CField.writeCharacterLook(oPacket, c, false);
        oPacket.EncodeString(c.getName());
        oPacket.EncodeShort(c.getJob());
        addGameInfo(oPacket, c, game);
        return oPacket.ToPacket();
    }

    public static void addGameInfo(OutPacket oPacket, MapleCharacter chr, MapleMiniGame game) {
        oPacket.EncodeInteger(game.getGameType());
        oPacket.EncodeInteger(game.getWins(chr));
        oPacket.EncodeInteger(game.getTies(chr));
        oPacket.EncodeInteger(game.getLosses(chr));
        oPacket.EncodeInteger(game.getScore(chr));
    }

    public static Packet getMiniGameClose(byte number) {
        OutPacket oPacket = new OutPacket(80);
        oPacket.EncodeShort(SendPacketOpcode.MiniRoom.getValue());
        oPacket.Encode(18);
        oPacket.Encode(1);
        oPacket.Encode(number);
        return oPacket.ToPacket();
    }

    public static Packet getMatchCardStart(MapleMiniGame game, int loser) {
        OutPacket oPacket = new OutPacket(80);
        oPacket.EncodeShort(SendPacketOpcode.MiniRoom.getValue());
        oPacket.Encode(62);
        oPacket.Encode(loser == 1 ? 0 : 1);
        int times = game.getPieceType() == 2 ? 30 : game.getPieceType() == 1 ? 20 : 12;
        oPacket.Encode(times);
        for (int i = 1; i <= times; i++) {
            oPacket.EncodeInteger(game.getCardId(i));
        }
        return oPacket.ToPacket();
    }

    public static Packet getMatchCardSelect(int turn, int slot, int firstslot, int type) {
        OutPacket oPacket = new OutPacket(80);
        oPacket.EncodeShort(SendPacketOpcode.MiniRoom.getValue());
        oPacket.Encode(69);
        oPacket.Encode(turn);
        oPacket.Encode(slot);
        if (turn == 0) {
            oPacket.Encode(firstslot);
            oPacket.Encode(type);
        }
        return oPacket.ToPacket();
    }

    public static Packet getMiniGameResult(MapleMiniGame game, int type, int x) {
        OutPacket oPacket = new OutPacket(80);
        oPacket.EncodeShort(SendPacketOpcode.MiniRoom.getValue());
        oPacket.Encode(63);
        oPacket.Encode(type);
        game.setPoints(x, type);
        if (type != 0) {
            game.setPoints(x == 1 ? 0 : 1, type == 2 ? 0 : 1);
        }
        if (type != 1) {
            if (type == 0) {
                oPacket.Encode(x == 1 ? 0 : 1);
            } else {
                oPacket.Encode(x);
            }
        }
        addGameInfo(oPacket, game.getMCOwner(), game);
        for (Pair visitorz : game.getVisitors()) {
            addGameInfo(oPacket, (MapleCharacter) visitorz.right, game);
        }

        return oPacket.ToPacket();

    }

    public static final Packet MerchantBlackListView(final List<String> blackList) {
        final OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.MiniRoom.getValue());
        oPacket.Encode(39);
        oPacket.EncodeShort(blackList.size());
        for (String visit : blackList) {
            oPacket.EncodeString(visit);
        }
        return oPacket.ToPacket();
    }

    public static final Packet MerchantVisitorView(List<String> visitor) {
        final OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.MiniRoom.getValue());
        oPacket.Encode(38);
        oPacket.EncodeShort(visitor.size());
        for (String visit : visitor) {
            oPacket.EncodeString(visit);
            oPacket.EncodeInteger(1);
        }
        return oPacket.ToPacket();
    }
}
