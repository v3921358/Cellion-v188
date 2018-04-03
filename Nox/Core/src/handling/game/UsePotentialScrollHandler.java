package handling.game;

import client.MapleClient;
import client.inventory.Equip;
import client.inventory.Equip.ScrollResult;
import client.inventory.EquipSlotType;
import client.inventory.Item;
import client.inventory.MapleInventoryType;
import client.inventory.ModifyInventory;
import client.inventory.ModifyInventoryOperation;
import constants.ItemConstants;
import constants.ServerConstants;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.InPacket;
import server.MapleInventoryManipulator;
import server.MapleItemInformationProvider;
import server.Randomizer;
import server.maps.objects.User;
import server.potentials.ItemPotentialProvider;
import server.potentials.ItemPotentialTierType;
import tools.packet.CField;
import tools.packet.CWvsContext;
import netty.ProcessPacket;

/**
 *
 * @author Lloyd Korn
 */
public class UsePotentialScrollHandler implements ProcessPacket<MapleClient> {

    @Override
    public boolean ValidateState(MapleClient c) {
        return true;
    }

    @Override
    public void Process(MapleClient c, InPacket iPacket) {
        //A2 C5 41 30 
        //03 00 
        //01 00 
        //00
        User chr = c.getPlayer();

        chr.updateTick(iPacket.DecodeInteger());
        //iPacket.decode();
        short slot = iPacket.DecodeShort();
        short dst = iPacket.DecodeShort();

        // Scroll and target eq
        Item scroll = chr.getInventory(MapleInventoryType.USE).getItem(slot);
        Equip toScroll = (Equip) chr.getInventory(dst < 0 ? MapleInventoryType.EQUIPPED : MapleInventoryType.EQUIP).getItem(dst);

        if (scroll == null || scroll.getQuantity() <= 0 || toScroll == null
                || !ItemConstants.isPotentialScroll(scroll.getItemId())) {
            c.write(CWvsContext.enableActions());
            return;
        }

        MapleItemInformationProvider mi = MapleItemInformationProvider.getInstance();
        final Map<String, Integer> scrollStats = mi.getEquipStats(scroll.getItemId());
        final Map<String, Integer> eqStats = mi.getEquipStats(toScroll.getItemId());

        // Check for the equipment if its able to be potential'd
        final EquipSlotType slotType = mi.getSlotType(toScroll.getItemId());
        final int totalUpgradeSlot = eqStats.containsKey("tuc") ? eqStats.get("tuc") : 0;

        if (totalUpgradeSlot == 0) {
            if (slotType != EquipSlotType.Shield_OrDualBlade && slotType != EquipSlotType.Si_Emblem) {
                c.write(CWvsContext.enableActions());
                return;
            }
        }

        final int successRate;
        if (scrollStats != null && scrollStats.containsKey("success")) {
            successRate = scrollStats.get("success");
        } else { // Some inconsistency in WZ. Certain potential scrolls doesnt have success rate data
            switch (scroll.getItemId()) {
                case ItemConstants.POTENTIAL_SCROLL:
                case ItemConstants.POTENTIAL_SCROLL_2:
                case ItemConstants.POTENTIAL_SCROLL_FOR_PURPLE_SOUL_RINGS:
                case ItemConstants.POTENTIAL_SCROLL_FOR_BLUE_SOUL_RINGS:
                case ItemConstants.POTENTIAL_SCROLL_FOR_CALTON_MUSTACHE:
                case ItemConstants.POTENTIAL_SCROLL_4:
                    successRate = 70;
                    break;
                case ItemConstants.ADVANCED_POTENTIAL_SCROLL_4:
                case ItemConstants.ADVANCED_POTENTIAL_SCROLL:
                case ItemConstants.ADVANCED_POTENTIAL_SCROLL_2:
                case ItemConstants.ADVANCED_POTENTIAL_SCROLL_3:
                    successRate = 90;
                    break;
                default:
                    successRate = 60; //Just give em a default.
                    break;
            }
        }

        int curseRate = 0;
        if (scrollStats != null && scrollStats.containsKey("cursed")) {
            curseRate = scrollStats.get("cursed") != null ? scrollStats.get("cursed") : 0;
        }

        ScrollResult result;
        boolean completed = true;
        List<ModifyInventory> modifications = new ArrayList<>();

        // Attempt scrolling here
        if (Randomizer.nextInt(100) < successRate) {
            ItemPotentialTierType potentialTier = ItemConstants.getPotentialTierFromScrollId(scroll.getItemId());

            if (ServerConstants.DEVELOPER_DEBUG_MODE) {
                System.out.println(potentialTier == null ? "[Debug] Scroll Error" : "[Debug] Scroll Success");
            }

            boolean res = ItemPotentialProvider.usePotentialScroll(toScroll, potentialTier);

            if (!res) {
                completed = false;
            } else {
                modifications.add(new ModifyInventory(ModifyInventoryOperation.AddItem, toScroll));
            }
            result = ScrollResult.SUCCESS;
        } else if (Randomizer.nextInt(100) < curseRate) {
            result = ScrollResult.CURSE;

            modifications.add(new ModifyInventory(ModifyInventoryOperation.Remove, toScroll));
            if (dst < 0) {
                chr.getInventory(MapleInventoryType.EQUIPPED).removeItem(toScroll.getPosition());
            } else {
                chr.getInventory(MapleInventoryType.EQUIP).removeItem(toScroll.getPosition());
            }
        } else {
            result = ScrollResult.FAIL;
        }

        // Remove item
        if (completed) {
            MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, scroll.getPosition(), (short) 1, false);
        }
        c.write(CField.enchantResult(result == Equip.ScrollResult.SUCCESS ? 1 : result == ScrollResult.CURSE ? 2 : 0));
        chr.getMap().broadcastMessage(chr, CField.getScrollEffect(c.getPlayer().getId(), result, false, toScroll.getItemId(), scroll.getItemId()), true);

        c.write(CWvsContext.inventoryOperation(true, modifications));
    }
}
