package handling.game;

import client.MapleClient;
import client.inventory.Equip;
import client.inventory.Item;
import client.inventory.MapleInventoryType;
import client.inventory.ModifyInventory;
import client.inventory.ModifyInventoryOperation;
import constants.GameConstants;
import constants.ItemConstants;
import java.util.ArrayList;
import java.util.List;
import server.MapleInventoryManipulator;
import net.InPacket;
import server.MapleItemInformationProvider;
import server.maps.objects.MapleCharacter;
import server.potentials.ItemPotentialProvider;
import server.potentials.ItemPotentialTierType;
import tools.LogHelper;
import tools.packet.CField;
import tools.packet.CWvsContext;
import tools.packet.MiracleCubePacket;
import netty.ProcessPacket;

/**
 *
 * @author Lloyd Korn
 */
public class UseCraftedCubeHandler implements ProcessPacket<MapleClient> {

    @Override
    public boolean ValidateState(MapleClient c) {
        return true;
    }

    @Override
    public void Process(MapleClient c, InPacket iPacket) {
        MapleCharacter chr = c.getPlayer();

        chr.updateTick(iPacket.DecodeInteger());
        final Item toUse = chr.getInventory(MapleInventoryType.USE).getItem(iPacket.DecodeShort());
        final Equip item = (Equip) chr.getInventory(MapleInventoryType.EQUIP).getItem(iPacket.DecodeShort());

        if (toUse == null || item == null
                || toUse.getItemId() / 10000 != 271 || toUse.getQuantity() <= 0) {
            c.write(CField.enchantResult(0));
            return;
        }

        int tierUpRate = ItemPotentialProvider.RATE_GAMECUBE_TIERUP;
        int tierDownRate = 0;
        int maxEquipmentCutoffLevel = GameConstants.maxLevel;
        ItemPotentialTierType maxTier = ItemPotentialTierType.Epic;

        switch (toUse.getItemId()) {
            case ItemConstants.HERMES_CUBE:
                maxTier = ItemPotentialTierType.Unique;
                maxEquipmentCutoffLevel = 99;
                break;
            case ItemConstants.OCCULT_CUBE:
            case ItemConstants.OCCULT_CUBE_UNTRADEABLE:
            case ItemConstants.OCCULT_CUBE_UNTRADEABLE2:
                maxTier = ItemPotentialTierType.Unique;
                break;
            case ItemConstants.TIMIC_CUBE:
                maxTier = ItemPotentialTierType.Unique;
                break;
            case ItemConstants.MASTER_CRAFTMANS_CUBE:
            case ItemConstants.MASTER_CRAFTMANS_CUBE2:
            case ItemConstants.MASTER_CRAFTMANS_CUBE_UNTRADEABLE:
                maxTier = ItemPotentialTierType.Unique;
                break;
            case ItemConstants.MEISTER_CUBE:
            case ItemConstants.MEISTER_CUBE2:
            case ItemConstants.MEISTER_CUBE_UNTRADEABLE:
                maxTier = ItemPotentialTierType.Legendary;
                break;
            case ItemConstants.MAPLE_SAINT_WEAPON_CUBE:
                maxTier = ItemPotentialTierType.Unique;
                break;
            case ItemConstants.UNK_CUBE:
            case ItemConstants.OCCULT_CUBE_UNTRADEABLE3:
                maxTier = ItemPotentialTierType.Epic;
                break;
            default: {
                c.write(CField.enchantResult(0));
                throw new RuntimeException(String.format("New crafted cube detected. Itemid: %d, Character: %d [%s], AccountID: %d", toUse.getItemId(), c.getPlayer().getId(), c.getPlayer().getName(), c.getAccID()));
            }
        }
        // Check level
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        final int reqLevel = ii.getReqLevel(item.getItemId());
        if (reqLevel > maxEquipmentCutoffLevel) {
            LogHelper.PACKET_EDIT_HACK.get().info(
                    String.format("[UseCraftedCubeHandler] %s [ChrID: %d; AccId %d] has tried to use a crafted cube on an item beyond the level requirement. EquipmentID = %d, CubeID = %d", chr.getName(), chr.getId(), c.getAccID(), item.getItemId(), toUse.getItemId())
            );
            c.close();
            return;
        }
        ItemPotentialTierType lastTierBeforeCube = item.getPotentialTier();
        boolean renewedPotential = ItemPotentialProvider.resetPotential(item, tierUpRate, tierDownRate, maxTier, false, 1);

        // Check Tier
        if (lastTierBeforeCube.getValue() > maxTier.getValue()) {
            LogHelper.PACKET_EDIT_HACK.get().info(
                    String.format("[UseCraftedCubeHandler] %s [ChrID: %d; AccId %d] has tried to use a crafted cube for %s tier on a %s tier equipment. EquipmentID = %d, CubeID = %d",
                            chr.getName(), chr.getId(), c.getAccID(),
                            maxTier.toString(),
                            lastTierBeforeCube.toString(),
                            item.getItemId(), toUse.getItemId())
            );
            c.close();
            return;
        }

        if (renewedPotential) {
            // Remove cube from inventory
            MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, toUse.getPosition(), (short) 1, false);

            // Update inventory equipment 
            List<ModifyInventory> modifications = new ArrayList<>();
            modifications.add(new ModifyInventory(ModifyInventoryOperation.AddItem, item));
            c.write(CWvsContext.inventoryOperation(true, modifications));

            c.write(MiracleCubePacket.onInGameCubeResult(chr.getId(), lastTierBeforeCube != item.getPotentialTier(), item.getPosition(), toUse.getItemId(), item));
        } else {
            chr.dropMessage(5, "This item's Potential cannot be reset.");
        }

        // Show to map
        chr.getMap().broadcastMessage(CField.showPotentialReset(chr.getId(), renewedPotential, item.getItemId()));
        c.write(CField.enchantResult(renewedPotential ? 0 : 0));
    }

}
