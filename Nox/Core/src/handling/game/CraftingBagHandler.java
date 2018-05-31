package handling.game;

import java.util.ArrayList;
import java.util.List;

import client.ClientSocket;
import client.inventory.Item;
import enums.ItemFlag;
import enums.InventoryType;
import client.inventory.ModifyInventory;
import enums.ModifyInventoryOperation;
import server.MapleItemInformationProvider;
import server.StatEffect;
import server.maps.objects.User;
import net.InPacket;
import tools.packet.CField;
import tools.packet.WvsContext;
import net.ProcessPacket;

public final class CraftingBagHandler implements ProcessPacket<ClientSocket> {

    @Override
    public boolean ValidateState(ClientSocket c) {
        return true;
    }

    @Override
    public void Process(ClientSocket c, InPacket iPacket) {
        final User chr = c.getPlayer();

        c.getPlayer().updateTick(iPacket.DecodeInt());
        final byte slot = (byte) iPacket.DecodeShort();
        final int itemId = iPacket.DecodeInt();
        final InventoryType inventoryType = InventoryType.getByType(iPacket.DecodeByte());

        final Item toUse = chr.getInventory(inventoryType).getItem(slot);

        if (!chr.isAlive() || chr.getMap() == null
                || chr.hasBlockedInventory() || toUse == null || toUse.getQuantity() < 1 || toUse.getItemId() != itemId || inventoryType == InventoryType.UNDEFINED) {
            c.SendPacket(WvsContext.enableActions());
            return;
        }
        final StatEffect effect = MapleItemInformationProvider.getInstance().getItemEffect(itemId);
        if (effect == null || effect.getSlotCount() == 0 || effect.getSlotPerLine() == 0) {
            /*-<imgdir name="03080000">
            <imgdir name="spec">
                <int name="slotCount" value="4"/>
                <int name="slotPerLine" value="4"/>
                <int name="type" value="1"/>
            </imgdir>*/
            // this might not be a bag item..
            c.SendPacket(WvsContext.enableActions());
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
            c.SendPacket(WvsContext.inventoryOperation(true, mod));
        }
        c.SendPacket(CField.openBag(chr.getExtendedSlots().indexOf(itemId), itemId, firstTime));
        c.SendPacket(WvsContext.enableActions());
    }

}
