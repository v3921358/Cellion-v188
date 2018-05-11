/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package handling.cashshop;

import client.Client;
import constants.GameConstants;

import static handling.cashshop.CashShopOperation.playerCashShopInfo;
import java.time.LocalDateTime;
import server.CashItemFactory;
import server.CashItemInfo;
import server.MapleInventoryManipulator;
import server.MapleItemInformationProvider;
import server.maps.objects.User;
import net.InPacket;
import tools.packet.CSPacket;

/**
 *
 * @author Novak
 */
public class QuestPurchase {

    public static void buyItem(InPacket iPacket, Client c, User chr) {
        CashItemInfo iteminfo = CashItemFactory.getInstance().getItem(iPacket.DecodeInt());
        if (iteminfo == null || !MapleItemInformationProvider.getInstance().isQuestItem(iteminfo.getId())) {
            c.SendPacket(CSPacket.sendCSFail(0));
            playerCashShopInfo(c);
            return;
        } else if (c.getPlayer().getMeso() < iteminfo.getPrice()) {
            c.SendPacket(CSPacket.sendCSFail(0xB8));
            playerCashShopInfo(c);
            return;
        } else if (c.getPlayer().getInventory(GameConstants.getInventoryType(iteminfo.getId())).getNextFreeSlot() < 0) {
            c.SendPacket(CSPacket.sendCSFail(0xB1));
            playerCashShopInfo(c);
            return;
        }
        byte pos = MapleInventoryManipulator.addId(c, iteminfo.getId(), (short) iteminfo.getCount(), null, "Cash shop: quest item" + " on " + LocalDateTime.now());
        if (pos < 0) {
            c.SendPacket(CSPacket.sendCSFail(0xB1));
            playerCashShopInfo(c);
            return;
        }
        chr.gainMeso(-iteminfo.getPrice(), false);
        c.SendPacket(CSPacket.showBoughtCSQuestItem(iteminfo.getPrice(), (short) iteminfo.getCount(), pos, iteminfo.getId()));
    }
}
