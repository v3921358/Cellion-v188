/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package handling.cashshop;

import client.ClientSocket;
import client.inventory.Item;
import server.CashItem;
import server.CashItemFactory;
import server.maps.objects.User;
import net.InPacket;
import tools.packet.CSPacket;

/**
 *
 * @author Novak
 */
public class FarmItemPurchase {

    public static void gems(InPacket iPacket, ClientSocket c, User chr) {
        iPacket.Skip(1);
        int type = iPacket.DecodeInt();//type again? #astral?
        int sn = iPacket.DecodeInt();
        CashItem item = CashItemFactory.getInstance().getAllItem(sn);
        if (item == null) {
            c.SendPacket(CSPacket.sendCSFail(0));
        }
        Item changedItem = chr.getCashInventory().toItem(item);
        if (changedItem != null) {
            chr.getCashInventory().addToInventory(changedItem);
            c.SendPacket(CSPacket.showBoughtCSItem(changedItem, item.getSN(), c.getAccID()));
        } else {
            c.SendPacket(CSPacket.sendCSFail(0));
        }
    }
}
