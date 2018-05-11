package handling.game;

import java.util.ArrayList;
import java.util.List;

import client.Client;
import client.inventory.Item;
import client.inventory.ItemFlag;
import client.inventory.MapleInventoryType;
import client.inventory.ModifyInventory;
import client.inventory.ModifyInventoryOperation;
import server.MapleItemInformationProvider;
import server.MapleStatEffect;
import server.maps.objects.User;
import net.InPacket;
import tools.packet.CField;
import tools.packet.CWvsContext;
import net.ProcessPacket;

public final class CraftingBagHandler implements ProcessPacket<Client> {

    @Override
    public boolean ValidateState(Client c) {
        return true;
    }

    @Override
    public void Process(Client c, InPacket iPacket) {
        final User chr = c.getPlayer();

        c.getPlayer().updateTick(iPacket.DecodeInt());
        final byte slot = (byte) iPacket.DecodeShort();
        final int itemId = iPacket.DecodeInt();
        final MapleInventoryType inventoryType = MapleInventoryType.getByType(iPacket.DecodeByte());

        final Item toUse = chr.getInventory(inventoryType).getItem(slot);

        if (!chr.isAlive() || chr.getMap() == null
                || chr.hasBlockedInventory() || toUse == null || toUse.getQuantity() < 1 || toUse.getItemId() != itemId || inventoryType == MapleInventoryType.UNDEFINED) {
            c.SendPacket(CWvsContext.enableActions());
            return;
        }
        final MapleStatEffect effect = MapleItemInformationProvider.getInstance().getItemEffect(itemId);
        if (effect == null || effect.getSlotCount() == 0 || effect.getSlotPerLine() == 0) {
            /*-<imgdir name="03080000">
            <imgdir name="spec">
                <int name="slotCount" value="4"/>
                <int name="slotPerLine" value="4"/>
                <int name="type" value="1"/>
            </imgdir>*/
            // this might not be a bag item..
            c.SendPacket(CWvsContext.enableActions());
            return;
        }

        boolean firstTime = !chr.getExtendedSlots().contains(itemId);
        if (firstTime) {
            chr.getExtendedSlots().add(itemId);
            chr.changedExtended();

            // Set this bag item to locked, disallowing the player from trading
            short flag = toUse.getFlag();
            flag |= ItemFlag.LOCK.getValue();
            flag |= ItemFlag.UNTRADABLE.getValue();
            toUse.setFlag(flag);

            // Update bag item response to the client
            List<ModifyInventory> mod = new ArrayList<>();
            mod.add(new ModifyInventory(ModifyInventoryOperation.UpdateQuantity, toUse));
            c.SendPacket(CWvsContext.inventoryOperation(true, mod));
        }
        c.SendPacket(CField.openBag(chr.getExtendedSlots().indexOf(itemId), itemId, firstTime));
        c.SendPacket(CWvsContext.enableActions());
    }

}
