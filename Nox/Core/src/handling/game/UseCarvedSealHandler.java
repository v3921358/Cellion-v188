package handling.game;

import client.Client;
import client.inventory.Equip;
import client.inventory.Item;
import client.inventory.MapleInventoryType;
import client.inventory.ModifyInventory;
import client.inventory.ModifyInventoryOperation;
import java.util.ArrayList;
import java.util.List;
import net.InPacket;
import server.MapleInventoryManipulator;
import server.MapleItemInformationProvider;
import server.Randomizer;
import server.potentials.ItemPotentialProvider;
import tools.packet.CField;
import tools.packet.CWvsContext;
import net.ProcessPacket;

/**
 *
 * @author
 */
public class UseCarvedSealHandler implements ProcessPacket<Client> {

    @Override
    public boolean ValidateState(Client c) {
        return true;
    }

    @Override
    public void Process(Client c, InPacket iPacket) {
        //iPacket: [90 64 C8 14] [04 00] [0F 00]
        c.getPlayer().updateTick(iPacket.DecodeInt());
        final Item toUse = c.getPlayer().getInventory(MapleInventoryType.USE).getItem(iPacket.DecodeShort());
        final Equip itemEq = (Equip) c.getPlayer().getInventory(MapleInventoryType.EQUIP).getItem(iPacket.DecodeShort());

        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();

        if (toUse == null || itemEq == null
                || toUse.getItemId() / 100 != 20495
                || !ii.getEquipStats(toUse.getItemId()).containsKey("success")) {
            c.SendPacket(CWvsContext.enableActions());
            return;
        }

        boolean used = false;

        final int successRate = ii.getEquipStats(toUse.getItemId()).get("success");
        if (Randomizer.nextInt(100) <= successRate) {
            final boolean success = ItemPotentialProvider.useAwakeningStamp(itemEq);

            if (success) {
                used = true;

                List<ModifyInventory> modifications = new ArrayList<>();
                modifications.add(new ModifyInventory(ModifyInventoryOperation.AddItem, itemEq));
                c.SendPacket(CWvsContext.inventoryOperation(true, modifications));

                c.getPlayer().getMap().broadcastMessage(CField.showPotentialReset(c.getPlayer().getId(), true, toUse.getItemId()));
            }
        } else {
            used = true;
            c.getPlayer().getMap().broadcastMessage(CField.showPotentialReset(c.getPlayer().getId(), false, toUse.getItemId()));
        }

        if (used) {
            MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, toUse.getPosition(), (short) 1, false);
        }

        c.SendPacket(CWvsContext.enableActions());
    }

}
