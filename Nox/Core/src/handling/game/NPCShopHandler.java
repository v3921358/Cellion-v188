package handling.game;

import client.ClientSocket;
import enums.InventoryType;
import client.inventory.ModifyInventory;
import constants.GameConstants;
import constants.InventoryConstants;
import java.util.ArrayList;
import server.maps.objects.User;
import server.shops.Shop;
import net.InPacket;
import scripting.provider.NPCScriptManager;
import tools.packet.WvsContext;
import net.ProcessPacket;
import server.MapleInventoryManipulator;
import server.shops.ShopFactory;

/**
 *
 * @author Mazen Massoud
 */
public class NPCShopHandler implements ProcessPacket<ClientSocket> {

    @Override
    public boolean ValidateState(ClientSocket c) {
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
    public void Process(ClientSocket c, InPacket iPacket) {
        User pPlayer = c.getPlayer();

        NPCShopOperation bmode = NPCShopOperation.getFromValue(iPacket.DecodeByte());

        if (pPlayer == null || bmode == null) {
            return;
        }

        switch (bmode) {
            case NPC_SHOP_ACTION_BUY: {
                Shop oShop = pPlayer.getShop();
                if (oShop == null) {
                    return;
                }
                byte nSlot = (byte) iPacket.DecodeShort();
                int nItem = iPacket.DecodeInt();
                short nQuantity = iPacket.DecodeShort();


                if (nItem == 2000005) { // Hack fix for Power Elixer.
                    pPlayer.gainItem(2000005, nQuantity);
                    pPlayer.gainMeso(-(5000 * nQuantity), false);
                } else if (InventoryConstants.isAmmo(nItem)) {
                    oShop.buy(c, nSlot, nItem, nQuantity);
                    pPlayer.sortInventory((byte) 2); // Sort players 'USE' inventory for ammo.
                } else {
                    nSlot++; // Increase this by one to match the index of our shops.
                    oShop.buy(c, nSlot, nItem, nQuantity);
                }
                c.SendPacket(WvsContext.enableActions());
                if (pPlayer.isIntern()) {
                    pPlayer.dropMessage(5, "[Shop Debug] Item ID : " + nItem + " (Purchased)");
                }
                break;
            }
            case NPC_SHOP_ACTION_SELL: {
                Shop shop = pPlayer.getShop();
                if (shop == null) {
                    return;
                }
                byte slot = (byte) iPacket.DecodeShort();
                int itemId = iPacket.DecodeInt();
                short quantity = iPacket.DecodeShort();
                shop.sell(c, GameConstants.getInventoryType(itemId), slot, quantity);
                c.SendPacket(WvsContext.enableActions());

                if (pPlayer.isIntern()) {
                    pPlayer.dropMessage(5, "[Shop Debug] Item ID : " + itemId + " (Sold)");
                }
                break;
            }
            case NPC_SHOP_ACTION_RECHARGE: {
                Shop shop = pPlayer.getShop();
                if (shop == null) {
                    return;
                }
                byte slot = (byte) iPacket.DecodeShort();
                shop.recharge(c, slot);
                c.SendPacket(WvsContext.enableActions());
                break;
            }
            case NPC_SHOP_ACTION_CLOSE: {
                c.removeClickedNPC();
                NPCScriptManager.getInstance().dispose(c);
                c.SendPacket(WvsContext.enableActions());
                break;
            }
            case NOT_FOUND:
                c.SendPacket(WvsContext.enableActions());
                break;
            default:
                pPlayer.setConversation(User.MapleCharacterConversationType.None);
                c.SendPacket(WvsContext.enableActions());
                break;
        }
    }
}
