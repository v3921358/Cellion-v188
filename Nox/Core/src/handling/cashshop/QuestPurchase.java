/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package handling.cashshop;

import client.MapleClient;
import constants.GameConstants;

import static handling.cashshop.CashShopOperation.playerCashShopInfo;
import java.time.LocalDateTime;
import server.CashItemFactory;
import server.CashItemInfo;
import server.MapleInventoryManipulator;
import server.MapleItemInformationProvider;
import server.maps.objects.MapleCharacter;
import net.InPacket;
import tools.packet.CSPacket;

/**
 *
 * @author Novak
 */
public class QuestPurchase {

    public static void buyItem(InPacket iPacket, MapleClient c, MapleCharacter chr) {
        CashItemInfo iteminfo = CashItemFactory.getInstance().getItem(iPacket.DecodeInteger());
        if (iteminfo == null || !MapleItemInformationProvider.getInstance().isQuestItem(iteminfo.getId())) {
            c.write(CSPacket.sendCSFail(0));
            playerCashShopInfo(c);
            return;
        } else if (c.getPlayer().getMeso() < iteminfo.getPrice()) {
            c.write(CSPacket.sendCSFail(0xB8));
            playerCashShopInfo(c);
            return;
        } else if (c.getPlayer().getInventory(GameConstants.getInventoryType(iteminfo.getId())).getNextFreeSlot() < 0) {
            c.write(CSPacket.sendCSFail(0xB1));
            playerCashShopInfo(c);
            return;
        }
        byte pos = MapleInventoryManipulator.addId(c, iteminfo.getId(), (short) iteminfo.getCount(), null, "Cash shop: quest item" + " on " + LocalDateTime.now());
        if (pos < 0) {
            c.write(CSPacket.sendCSFail(0xB1));
            playerCashShopInfo(c);
            return;
        }
        chr.gainMeso(-iteminfo.getPrice(), false);
        c.write(CSPacket.showBoughtCSQuestItem(iteminfo.getPrice(), (short) iteminfo.getCount(), pos, iteminfo.getId()));
    }
}
