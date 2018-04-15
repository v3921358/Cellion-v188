package handling.cashshop;

import java.util.ArrayList;

import client.MapleClient;
import client.MapleClient.MapleClientLoginState;
import client.buddy.Buddy;
import client.buddy.BuddyResult;
import client.inventory.Item;
import client.inventory.MapleInventoryType;
import constants.ServerConstants;
import handling.world.CharacterTransfer;
import handling.world.World;
import java.util.logging.Level;
import java.util.logging.Logger;
import server.CashShop;
import service.CashShopServer;
import service.ChannelServer;
import service.LoginServer;
import server.maps.objects.User;
import tools.packet.CField;
import tools.packet.CSPacket;
import tools.packet.CWvsContext;

public class CashShopOperation {

    public static void LeaveCS(final MapleClient c, final User chr) {
        CashShopServer.getPlayerStorage().deregisterPlayer(chr);
        c.updateLoginState(MapleClient.MapleClientLoginState.LOGIN_SERVER_TRANSITION, c.getSessionIPAddress());
        try {
            World.changeChannelData(new CharacterTransfer(chr), chr.getId(), c.getChannel());
        } catch (Exception ex) {
            Logger.getLogger(CashShopOperation.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            final String s = c.getSessionIPAddress();
            LoginServer.addIPAuth(s.substring(s.indexOf('/') + 1, s.length()));
            c.SendPacket(CField.getChannelChange(c, Integer.parseInt(ChannelServer.getInstance(c.getChannel()).getIP().split(":")[1])));
            c.disconnect(false, true);
        }
    }

    public static void EnterCS(final CharacterTransfer transfer, final MapleClient c) {
        if (transfer == null) {
            c.Close();
            return;
        }
        User chr = User.reconstructCharacter(transfer, c, false);

        c.setPlayer(chr);
        c.setAccID(chr.getAccountID());

        if (!c.CheckIPAddress()) { // Remote hack
            c.Close();
            return;
        }

        final MapleClientLoginState state = c.getLoginState();
        boolean allowLogin = false;

        switch (state) {
            case LOGIN_SERVER_TRANSITION:
            case CHANGE_CHANNEL:
                if (!World.isCharacterListConnected(c.loadCharacterNames(c.getWorld()))) {
                    allowLogin = true;
                }
                break;
        }

        if (!allowLogin) {
            c.setPlayer(null);
            c.Close();
            return;
        }

        // Convert All Types of NX to MaplePoints upon entering Cash Shop.
        if (ServerConstants.AUTO_NX_CONVERT) {
            c.getPlayer().modifyCSPoints(2, c.getPlayer().getCSPoints(1));
            c.getPlayer().modifyCSPoints(1, -c.getPlayer().getCSPoints(1));
        }

        c.updateLoginState(MapleClientLoginState.LOGIN_LOGGEDIN, c.getSessionIPAddress());
        CashShopServer.getPlayerStorage().registerPlayer(chr);
        c.SendPacket(CSPacket.warpCS(c));
        c.SendPacket(CSPacket.loadCategories());
        c.SendPacket(CSPacket.SetCashShopBannerPicture());
        c.SendPacket(CSPacket.CS_Top_Items());
        c.SendPacket(CSPacket.CS_Special_Item());
        c.SendPacket(CSPacket.CS_Featured_Item());
        c.SendPacket(CSPacket.showNXMapleTokens(c.getPlayer()));
        playerCashShopInfo(c);
        CashShop mci = c.getPlayer().getCashInventory();
        if (mci != null && mci.getItemsSize() > 0) {
            for (Item itemz : mci.getInventory()) {
                c.SendPacket(CSPacket.showBoughtCSItem(itemz, itemz.getUniqueId(), c.getAccID()));
            }
        }
    }

    private static MapleInventoryType getInventoryType(final int id) {
        switch (id) {
            case 50200093:
                return MapleInventoryType.EQUIP;
            case 50200094:
                return MapleInventoryType.USE;
            case 50200197:
                return MapleInventoryType.SETUP;
            case 50200095:
                return MapleInventoryType.ETC;
            default:
                return MapleInventoryType.UNDEFINED;
        }
    }

    public static void playerCashShopInfo(MapleClient c) {
        c.SendPacket(CSPacket.getCSInventory(c));
        c.SendPacket(CSPacket.doCSMagic());
        c.SendPacket(CSPacket.getCSGifts(c));
        Buddy buddy = new Buddy(BuddyResult.LOAD_FRIENDS);
        buddy.setEntries(new ArrayList<>(c.getPlayer().getBuddylist().getBuddies()));
        c.SendPacket(CWvsContext.buddylistMessage(buddy));
        c.SendPacket(CSPacket.showNXMapleTokens(c.getPlayer()));
        //c.write(CSPacket.sendWishList(c.getPlayer(), false));
        //c.write(CSPacket.showNXMapleTokens(c.getPlayer()));
        c.SendPacket(CSPacket.enableCSUse());
        c.getPlayer().getCashInventory().checkExpire(c);
    }
}
