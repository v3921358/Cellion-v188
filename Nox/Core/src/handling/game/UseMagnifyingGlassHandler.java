package handling.game;

import client.Client;
import client.MapleTrait;
import client.inventory.Equip;
import client.inventory.Item;
import client.inventory.MapleInventoryType;
import client.inventory.ModifyInventory;
import client.inventory.ModifyInventoryOperation;
import constants.GameConstants;
import java.util.ArrayList;
import java.util.List;
import server.MapleItemInformationProvider;
import net.InPacket;
import server.MapleInventoryManipulator;
import server.maps.objects.User;
import server.potentials.ItemPotentialProvider;
import tools.packet.CField;
import tools.packet.CWvsContext;
import net.ProcessPacket;

/**
 *
 * @author
 */
public class UseMagnifyingGlassHandler implements ProcessPacket<Client> {

    @Override
    public boolean ValidateState(Client c) {
        return true;
    }

    /**
     * Gets the magnifying glass to use from insight level http://pastebin.com/zhjx2QuA
     *
     * @param nInsight
     * @return
     */
    private static int getMagnifyingGlassIDFromInsight(int nInsight) {
        if (nInsight < 30) {
        } else if (nInsight < 60) {
            return 2460000;
        } else if (nInsight < 90) {
            return 2460001;
        } else if (nInsight <= 100) {
            return 2460002;
        }
        return -1;
    }

    /**
     * The item release cost if used with a glass item http://pastebin.com/HVipuHYA
     *
     * @param nReqLevel
     * @param nGlassItem
     * @return
     */
    private static int getItemReleaseCostAppliedGlass(int nReqLevel, int nGlassItem) {
        int v2 = getItemReleaseCost(nReqLevel);
        switch (nGlassItem) {
            case 2460000:
                if (nReqLevel > 30) {
                    return v2;
                }
                break;
            case 2460001:
                if (nReqLevel > 70) {
                    return v2;
                }
                break;
            case 2460002:
                if (nReqLevel > 120) {
                    return v2;
                }
            default:
                if (nGlassItem / 10000 != 246) {
                    if (nReqLevel > 30) {
                        return v2;
                    }
                }
                break;
        }
        return 0;
    }

    /**
     * The price of the release cost. http://pastebin.com/2r7YLkbF
     *
     * @param nReqLevel
     * @return
     */
    private static int getItemReleaseCost(int nReqLevel) {
        double v1 = 0.0; // base multiplier? lol
        //  if (nReqLevel > 30) {
        //       v1 = 0.5;
        //   }

        if (nReqLevel > 120) {
            v1 = 10;
        } else if (nReqLevel > 70) {
            v1 = 2.5;
        } else if (nReqLevel > 30) {
            v1 = 0.5;
        }

        double v2 = (double) nReqLevel;
        int loop = 2;
        double v4 = 1.0;
        while (true) {
            if ((loop & 1) != 0) {
                v4 *= v2;
            }
            //  System.out.println("v3 : " + loop);
            loop >>= 1;
            if (loop == 0) {
                break;
            }
            v2 *= v2;
        }
        //   System.out.println("v1 : " + v1 + ", v4: " + v4 + ", nReqLevel" + nReqLevel);

        int price = (int) (v1 * Math.ceil(v4));
        if (price <= 0) {
            return 0;
        }
        return price;
    }

    @Override
    public void Process(Client c, InPacket iPacket) {
        User chr = c.getPlayer();

        chr.setScrolledPosition((short) 0);

        chr.updateTick(iPacket.DecodeInt());
        final short srcPosition = iPacket.DecodeShort();
        final boolean insight = srcPosition == 127 && chr.getTrait(MapleTrait.MapleTraitType.sense).getLevel() >= 30;
        final Item magnifyingItem = chr.getInventory(MapleInventoryType.USE).getItem(srcPosition); // magnifying item could be null, since later versions dont need it
        if (magnifyingItem != null) { // If this item is not null, it means the player is using magnifying glass USE item instead of the inventory UI
            //      System.out.println(magnifyingItem.getItemId());
            if (magnifyingItem.getItemId() < 2460000 || magnifyingItem.getItemId() > 2460005) { // Check itemid
                c.SendPacket(CWvsContext.inventoryOperation(true, new ArrayList<>()));
                return;
            }
        }
        final short eqSlot = iPacket.DecodeShort();
        final Equip equipment = (Equip) chr.getInventory(eqSlot < 0 ? MapleInventoryType.EQUIPPED : MapleInventoryType.EQUIP).getItem(eqSlot);
        if (equipment == null || chr.hasBlockedInventory()) {
            c.SendPacket(CWvsContext.inventoryOperation(true, new ArrayList<>()));
            return;
        }

        // Check the cost
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        int reqLevel = 0;
        if (ii.getEquipStats(equipment.getItemId()).containsKey("reqLevel")) {
            reqLevel = ii.getEquipStats(equipment.getItemId()).get("reqLevel");
        } else {
            c.SendPacket(CWvsContext.inventoryOperation(true, new ArrayList<>()));
            throw new RuntimeException("No 'reqLevel' property available for the equipment id " + equipment.getItemId());
        }

        // Check the level requirement, if the player is using magnifying glass from USE 
        if (magnifyingItem != null) {
            int cutoffLevel = 0;
            switch (magnifyingItem.getItemId()) {
                case 2460000:
                    cutoffLevel = 30;
                    break;
                case 2460001:
                    cutoffLevel = 70;
                    break;
                case 2460002:
                    cutoffLevel = 120;
                    break;
                case 2460003:
                case 2460004:
                case 2460005:
                    cutoffLevel = GameConstants.maxLevel;
                    break;
            }
            if (reqLevel > cutoffLevel) {
                c.SendPacket(CWvsContext.inventoryOperation(true, new ArrayList<>()));
                return;
            }
        }

        int itemReleaseCost = magnifyingItem != null ? 0 : getItemReleaseCost(reqLevel);
        if (itemReleaseCost >= 0 && chr.getMeso() >= reqLevel) {
            boolean success = ItemPotentialProvider.magnifyEquipment(equipment);

            if (success) {
                if (itemReleaseCost > 0) {
                    chr.gainMeso(-itemReleaseCost, false);
                }
                if (magnifyingItem != null) {
                    MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, magnifyingItem.getPosition(), (short) 1, false);
                }

                // c.getPlayer().getTrait(MapleTrait.MapleTraitType.insight).addExp((src == 0x7F && price != -1 ? 10 : insight ? 10 : ((magnify.getItemId() + 2) - 2460000)) * 2, c.getPlayer());
                // Broadcast to map
                chr.getMap().broadcastMessage(CField.showMagnifyingEffect(chr.getId(), equipment.getPosition()));

                // Update inventory 
                List<ModifyInventory> modifications = new ArrayList<>();

                modifications.add(new ModifyInventory(ModifyInventoryOperation.AddItem, equipment));

                c.SendPacket(CWvsContext.inventoryOperation(true, modifications));
            }
        } else {
            // dont warn by dropping a message, client checks for meso anyway... 
        }
        c.SendPacket(CWvsContext.inventoryOperation(true, new ArrayList<>()));
    }
}
