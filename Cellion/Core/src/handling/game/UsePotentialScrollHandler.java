package handling.game;

import client.ClientSocket;
import client.inventory.Equip;
import client.inventory.Equip.ScrollResult;
import enums.EquipSlotType;
import client.inventory.Item;
import enums.InventoryType;
import client.inventory.ModifyInventory;
import enums.ModifyInventoryOperation;
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
import enums.ItemPotentialTierType;
import tools.packet.CField;
import tools.packet.WvsContext;
import net.ProcessPacket;

/**
 *
 * @author Lloyd Korn
 */
public class UsePotentialScrollHandler implements ProcessPacket<ClientSocket> {

    @Override
    public boolean ValidateState(ClientSocket c) {
        return true;
    }

    @Override
    public void Process(ClientSocket c, InPacket iPacket) {
        //A2 C5 41 30 
        //03 00 
        //01 00 
        //00
        User chr = c.getPlayer();

        chr.updateTick(iPacket.DecodeInt());
        //iPacket.decode();
        short slot = iPacket.DecodeShort();
        short dst = iPacket.DecodeShort();

        // Scroll and target eq
        Item scroll = chr.getInventory(InventoryType.USE).getItem(slot);
        Equip toScroll = (Equip) chr.getInventory(dst < 0 ? InventoryType.EQUIPPED : InventoryType.EQUIP).getItem(dst);

        if (scroll == null || scroll.getQuantity() <= 0 || toScroll == null
                || !ItemConstants.isPotentialScroll(scroll.getItemId())) {
            c.SendPacket(WvsContext.enableActions());
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
                c.SendPacket(WvsContext.enableActions());
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
                    successRate = 100; //Just give em a default.
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
                chr.getInventory(InventoryType.EQUIPPED).removeItem(toScroll.getPosition());
            } else {
                chr.getInventory(InventoryType.EQUIP).removeItem(toScroll.getPosition());
            }
        } else {
            result = ScrollResult.FAIL;
        }

        // Remove item
        if (completed) {
            MapleInventoryManipulator.removeFromSlot(c, InventoryType.USE, scroll.getPosition(), (short) 1, false);
        }
        c.SendPacket(CField.enchantResult(result == Equip.ScrollResult.SUCCESS ? 1 : result == ScrollResult.CURSE ? 2 : 0));
        chr.getMap().broadcastPacket(chr, CField.getScrollEffect(c.getPlayer().getId(), result, false, toScroll.getItemId(), scroll.getItemId()), true);

        c.SendPacket(WvsContext.inventoryOperation(true, modifications));
    }
}
