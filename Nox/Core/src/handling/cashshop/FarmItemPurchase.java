/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package handling.cashshop;

import client.MapleClient;
import client.inventory.Item;
import server.CashItem;
import server.CashItemFactory;
import server.maps.objects.MapleCharacter;
import net.InPacket;
import tools.packet.CSPacket;

/**
 *
 * @author Novak
 */
public class FarmItemPurchase {

    public static void gems(InPacket iPacket, MapleClient c, MapleCharacter chr) {
        iPacket.Skip(1);
        int type = iPacket.DecodeInteger();//type again? #astral?
        int sn = iPacket.DecodeInteger();
        CashItem item = CashItemFactory.getInstance().getAllItem(sn);
        if (item == null) {
            c.write(CSPacket.sendCSFail(0));
        }
        Item changedItem = chr.getCashInventory().toItem(item);
        if (changedItem != null) {
            chr.getCashInventory().addToInventory(changedItem);
            c.write(CSPacket.showBoughtCSItem(changedItem, item.getSN(), c.getAccID()));
        } else {
            c.write(CSPacket.sendCSFail(0));
        }
    }
}
