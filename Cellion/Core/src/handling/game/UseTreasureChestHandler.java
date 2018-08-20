package handling.game;

import client.ClientSocket;
import client.inventory.Item;
import enums.InventoryType;
import constants.GameConstants;
import handling.world.World;
import server.MapleInventoryManipulator;
import server.RandomRewards;
import server.maps.objects.User;
import net.InPacket;
import tools.packet.WvsContext;
import net.ProcessPacket;

/**
 *
 * @author
 */
public class UseTreasureChestHandler implements ProcessPacket<ClientSocket> {

    @Override
    public boolean ValidateState(ClientSocket c) {
        return true;
    }

    @Override
    public void Process(ClientSocket c, InPacket iPacket) {
        final short slot = iPacket.DecodeShort();
        final int itemid = iPacket.DecodeInt();

        User chr = c.getPlayer();

        final Item toUse = chr.getInventory(InventoryType.ETC).getItem((byte) slot);
        if (toUse == null || toUse.getQuantity() <= 0 || toUse.getItemId() != itemid || chr.hasBlockedInventory()) {
            c.SendPacket(WvsContext.enableActions());
            return;
        }
        int reward;
        int keyIDforRemoval = 0;
        String box;

        switch (toUse.getItemId()) {
            case 4280000: // Gold box
                reward = RandomRewards.getGoldBoxReward();
                keyIDforRemoval = 5490000;
                box = "Gold";
                break;
            case 4280001: // Silver box
                reward = RandomRewards.getSilverBoxReward();
                keyIDforRemoval = 5490001;
                box = "Silver";
                break;
            default: // Up to no good
                return;
        }

        // Get the quantity
        int amount = 1;
        switch (reward) {
            case 2000004:
                amount = 200; // Elixir
                break;
            case 2000005:
                amount = 100; // Power Elixir
                break;
        }
        if (chr.getInventory(InventoryType.CASH).countById(keyIDforRemoval) > 0) {
            final Item item = MapleInventoryManipulator.addbyIdGachapon(c, reward, (short) amount);

            if (item == null) {
                chr.dropMessage(5, "Please check your item inventory and see if you have a Master Key, or if the inventory is full.");
                c.SendPacket(WvsContext.enableActions());
                return;
            }
            MapleInventoryManipulator.removeFromSlot(c, InventoryType.ETC, (byte) slot, (short) 1, true);
            MapleInventoryManipulator.removeById(c, InventoryType.CASH, keyIDforRemoval, 1, true, false);
            c.SendPacket(WvsContext.InfoPacket.getShowItemGain(reward, (short) amount, true));

            if (GameConstants.gachaponRareItem(item.getItemId()) > 0) {
                World.Broadcast.broadcastMessage(WvsContext.getGachaponMega(c.getPlayer().getName(), " : got a(n)", item, (byte) 2, "from a chest!"));
            }
        } else {
            chr.dropMessage(5, "Please check your item inventory and see if you have a Master Key, or if the inventory is full.");
            c.SendPacket(WvsContext.enableActions());
        }
    }
}
