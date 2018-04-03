package handling.game;

import client.MapleClient;
import client.inventory.MapleInventoryType;
import client.inventory.ModifyInventory;
import constants.GameConstants;
import constants.InventoryConstants;
import java.util.ArrayList;
import server.maps.objects.User;
import server.shops.MapleShop;
import net.InPacket;
import scripting.provider.NPCScriptManager;
import tools.packet.CWvsContext;
import netty.ProcessPacket;
import server.MapleInventoryManipulator;
import server.shops.MapleShopFactory;

/**
 *
 * @author Mazen Massoud
 */
public class NPCShopHandler implements ProcessPacket<MapleClient> {

    @Override
    public boolean ValidateState(MapleClient c) {
        return true;
    }

    private static enum NPCShopOperation {
        NPC_SHOP_ACTION_BUY(0),
        NPC_SHOP_ACTION_SELL(1),
        NPC_SHOP_ACTION_RECHARGE(2),
        NPC_SHOP_ACTION_CLOSE(3),
        NOT_FOUND(-1);

        private final int val;

        private NPCShopOperation(int val) {
            this.val = val;
        }

        public int getValue() {
            return val;
        }

        public static NPCShopOperation getFromValue(int val) {
            for (NPCShopOperation nso : values()) {
                if (nso.getValue() == val) {
                    return nso;
                }
            }
            return NOT_FOUND;
        }
    }

    @Override
    public void Process(MapleClient c, InPacket iPacket) {
        User pPlayer = c.getPlayer();

        NPCShopOperation bmode = NPCShopOperation.getFromValue(iPacket.DecodeByte());

        if (pPlayer == null || bmode == null) {
            return;
        }

        switch (bmode) {
            case NPC_SHOP_ACTION_BUY: {
                MapleShop oShop = pPlayer.getShop();
                if (oShop == null) {
                    return;
                }
                byte nSlot = (byte) iPacket.DecodeShort();
                int nItem = iPacket.DecodeInteger();
                short nQuantity = iPacket.DecodeShort();

                if (nItem == 2000005) { // Hack fix for Power Elixer.
                    pPlayer.gainItem(2000005, nQuantity);
                    pPlayer.gainMeso(-(5000 * nQuantity), false);
                } else if (InventoryConstants.isAmmo(nItem)) {
                    oShop.buy(c, nSlot, nItem, nQuantity);
                    pPlayer.sortInventory((byte) 2); // Sort players 'USE' inventory for ammo.
                } else {
                    nSlot++; // If item isn't ammo, increase slot value before continuing.
                    oShop.buy(c, nSlot, nItem, nQuantity);
                }

                if (pPlayer.isIntern()) {
                    pPlayer.dropMessage(5, "[Shop Debug] Item ID : " + nItem + " (Purchased)");
                }
                break;
            }
            case NPC_SHOP_ACTION_SELL: {
                MapleShop shop = pPlayer.getShop();
                if (shop == null) {
                    return;
                }
                byte slot = (byte) iPacket.DecodeShort();
                int itemId = iPacket.DecodeInteger();
                short quantity = iPacket.DecodeShort();
                shop.sell(c, GameConstants.getInventoryType(itemId), slot, quantity);
                c.write(CWvsContext.enableActions());

                if (pPlayer.isIntern()) {
                    pPlayer.dropMessage(5, "[Shop Debug] Item ID : " + itemId + " (Sold)");
                }
                break;
            }
            case NPC_SHOP_ACTION_RECHARGE: {
                MapleShop shop = pPlayer.getShop();
                if (shop == null) {
                    return;
                }
                byte slot = (byte) iPacket.DecodeShort();
                shop.recharge(c, slot);
                c.write(CWvsContext.enableActions());
                break;
            }
            case NPC_SHOP_ACTION_CLOSE: {
                c.removeClickedNPC();
                NPCScriptManager.getInstance().dispose(c);
                c.write(CWvsContext.enableActions());
                break;
            }
            case NOT_FOUND:
                c.write(CWvsContext.enableActions());
                break;
            default:
                pPlayer.setConversation(User.MapleCharacterConversationType.None);
                c.write(CWvsContext.enableActions());
                break;
        }
    }
}
