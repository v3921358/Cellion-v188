/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package handling.cashshop;

import client.ClientSocket;
import client.inventory.Item;
import client.inventory.MapleInventoryType;
import server.MapleInventoryManipulator;
import server.maps.objects.User;
import net.InPacket;
import tools.packet.CSPacket;

/**
 *
 * @author Novak
 */
public class CSInventoryAction {

    public static void RetrieveItem(InPacket iPacket, ClientSocket c, User chr) {
        Item changedItem = c.getPlayer().getCashInventory().findByCashId((int) iPacket.DecodeLong());
        if (changedItem != null && changedItem.getQuantity() > 0 && MapleInventoryManipulator.checkSpace(c, changedItem.getItemId(), changedItem.getQuantity(), changedItem.getOwner())) {
            Item item_ = changedItem.copy();
            short pos = MapleInventoryManipulator.addbyItem(c, item_, true);
            if (pos >= 0) {
                if (item_.getPet() != null) {
                    item_.getPet().getItem().setPosition(pos);
                    c.getPlayer().addPet(item_.getPet());
                }
                c.getPlayer().getCashInventory().removeFromInventory(changedItem);
                c.SendPacket(CSPacket.confirmFromCSInventory(item_, pos));
            } else {
                c.SendPacket(CSPacket.sendCSFail(0xB1));
            }
        } else {
            c.SendPacket(CSPacket.sendCSFail(0xB1));
        }
    }

    public static void AddItem(InPacket iPacket, ClientSocket c, User chr) {
        int uniqueid = (int) iPacket.DecodeLong();
        MapleInventoryType inventoryType = MapleInventoryType.getByType(iPacket.DecodeByte());
        Item changedItem = c.getPlayer().getInventory(inventoryType).findByUniqueId(uniqueid);
        if (changedItem != null && changedItem.getQuantity() > 0 && changedItem.getUniqueId() > 0 && c.getPlayer().getCashInventory().getItemsSize() < 100) {
            Item item_ = changedItem.copy();
            MapleInventoryManipulator.removeFromSlot(c, inventoryType, changedItem.getPosition(), changedItem.getQuantity(), false);
            if (item_.getPet() != null) {
                c.getPlayer().removePetCS(item_.getPet());
            }
            item_.setPosition((byte) 0);
            c.getPlayer().getCashInventory().addToInventory(item_);
            c.SendPacket(CSPacket.showBoughtCSItem(changedItem, changedItem.getUniqueId(), c.getAccID())); // Meme, but updates the inventory right away.
            //c.write(CSPacket.confirmToCSInventory(changedItem, c.getAccID(), uniqueid)); // TODO: Fix the param
        } else {
            c.SendPacket(CSPacket.sendCSFail(0xB1));
        }
    }
}
