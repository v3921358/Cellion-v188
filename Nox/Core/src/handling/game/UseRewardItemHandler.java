package handling.game;

import client.ClientSocket;
import client.inventory.Item;
import client.inventory.MapleInventoryType;
import constants.GameConstants;
import handling.world.World;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import server.MapleInventoryManipulator;
import server.MapleItemInformationProvider;
import server.Randomizer;
import server.StructRewardItem;
import server.maps.objects.User;
import tools.Pair;
import net.InPacket;
import tools.packet.CField;
import tools.packet.WvsContext;
import net.ProcessPacket;

/**
 *
 * @author
 */
public class UseRewardItemHandler implements ProcessPacket<ClientSocket> {

    @Override
    public boolean ValidateState(ClientSocket c) {
        return true;
    }

    @Override
    public void Process(ClientSocket c, InPacket iPacket) {
        //System.out.println("[Reward Item] " + iPacket.toString());
        final byte slot = (byte) iPacket.DecodeShort();
        final int itemId = iPacket.DecodeInt();
        final boolean unseal = iPacket.DecodeByte() > 0;

        UseRewardItem(slot, itemId, unseal, c, c.getPlayer());
    }

    public static boolean UseRewardItem(byte slot, int itemId, final boolean unseal, final ClientSocket c, final User chr) {
        final Item toUse = c.getPlayer().getInventory(GameConstants.getInventoryType(itemId)).getItem(slot);
        c.SendPacket(WvsContext.enableActions());
        if (toUse != null && toUse.getQuantity() >= 1 && toUse.getItemId() == itemId && !chr.hasBlockedInventory()) {
            if (chr.getInventory(MapleInventoryType.EQUIP).getNextFreeSlot() > -1 && chr.getInventory(MapleInventoryType.USE).getNextFreeSlot() > -1 && chr.getInventory(MapleInventoryType.SETUP).getNextFreeSlot() > -1 && chr.getInventory(MapleInventoryType.ETC).getNextFreeSlot() > -1) {
                final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
                final Pair<Integer, List<StructRewardItem>> rewards = ii.getRewardItem(itemId);

                switch (itemId) {
                    case 2290245:
                    case 2290285:
                    case 2290448:
                    case 2290449:
                    case 2290450:
                    case 2290451:
                    case 2290452:
                    case 2290454:
                    case 2290455:
                    case 2290456:
                    case 2290457:
                    case 2290458:
                    case 2290459:
                    case 2290460:
                    case 2290461:
                    case 2290462:
                    case 2290463:
                    case 2290464:
                    case 2290465:
                    case 2290466:
                    case 2290467:
                    case 2290468:
                    case 2290469:
                    case 2290571:
                    case 2290581:
                    case 2290602:
                    case 2290653:
                    case 2290714:
                    case 2290715:
                    case 2290721:
                    case 2290722:
                    case 2290723:
                    case 2290724:
                    case 2290803:
                    case 2290868:
                    case 2290869:
                    case 2290870:
                    case 2290871:
                    case 2290872:
                    case 2290873:
                    case 2290874:
                    case 2290875:
                    case 2290876:
                    case 2290877:
                    case 2290878:
                    case 2290879:
                    case 2290880:
                    case 2290881:
                    case 2290882:
                    case 2290883:
                    case 2290884:
                    case 2290885:
                    case 2290886:
                    case 2290887:
                    case 2290888:
                    case 2290889:
                    case 2290890:
                    case 2290891:
                    case 2290892:
                    case 2290893:
                    case 2290914:
                    case 2290915:
                    case 2291020:
                        //    case 2291021:
                        //  case 2430144: //smb
                        final int itemid = Randomizer.nextInt(999) + 2290000;
                        World.Broadcast.broadcastMessage(CField.getGameMessage("SMB.", (short) 8));
                        if (MapleItemInformationProvider.getInstance().itemExists(itemid) && !MapleItemInformationProvider.getInstance().getName(itemid).contains("Special") && !MapleItemInformationProvider.getInstance().getName(itemid).contains("Event")) {
                            MapleInventoryManipulator.addById(c, itemid, (short) 1, "Reward item: " + toUse.getItemId() + " on " + LocalDateTime.now());
                            MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (byte) 1, false);
                        }
                        break;
                }
                if (rewards != null && rewards.getLeft() > 0) {
                    while (true) {
                        for (StructRewardItem reward : rewards.getRight()) {
                            if (reward.prob > 0 && Randomizer.nextInt(rewards.getLeft()) < reward.prob) { // Total prob
                                if (GameConstants.getInventoryType(reward.itemid) == MapleInventoryType.EQUIP) {
                                    final Item item = ii.getEquipById(reward.itemid);
                                    if (reward.period > 0) {
                                        item.setExpiration(System.currentTimeMillis() + (reward.period * 60 * 60 * 10));
                                    }
                                    item.setGMLog("Reward item: " + itemId + " on " + LocalDateTime.now());
                                    MapleInventoryManipulator.addbyItem(c, item);
                                } else {
                                    MapleInventoryManipulator.addById(c, reward.itemid, reward.quantity, "Reward item: " + itemId + " on " + LocalDateTime.now());
                                }
                                MapleInventoryManipulator.removeById(c, GameConstants.getInventoryType(itemId), itemId, 1, false, false);

                                c.SendPacket(CField.EffectPacket.showRewardItemAnimation(reward.itemid, reward.effect));
                                chr.getMap().broadcastMessage(chr, CField.EffectPacket.showRewardItemAnimation(reward.itemid, reward.effect, chr.getId()), false);
                                return true;
                            }
                        }
                    }
                }
            } else {
                chr.dropMessage(6, "Insufficient inventory slot.");
            }
        }
        return false;
    }
}
