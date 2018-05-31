/*
 * Cellion Development
 */
package handling.game;

import client.ClientSocket;
import client.inventory.Equip;
import client.inventory.Item;
import enums.InventoryType;
import client.inventory.ModifyInventory;
import enums.ModifyInventoryOperation;
import java.util.ArrayList;
import java.util.List;
import net.InPacket;
import net.ProcessPacket;
import server.MapleInventoryManipulator;
import server.Randomizer;
import server.maps.objects.User;
import server.potentials.Cube;
import server.potentials.ItemPotentialProvider;
import enums.ItemPotentialTierType;
import tools.packet.CField;
import tools.packet.WvsContext;

/**
 * Bonus Potential Scroll Request
 * @author Mazen Massoud
 */
public class UseBonusPotentialScrollHandler implements ProcessPacket<ClientSocket> {

    @Override
    public boolean ValidateState(ClientSocket c) {
        return true;
    }

    @Override
    public void Process(ClientSocket c, InPacket iPacket) {
        User pPlayer = c.getPlayer();
        pPlayer.updateTick(iPacket.DecodeInt());
        short nSlot = iPacket.DecodeShort();
        short nDestination = iPacket.DecodeShort();
        
        Item pScroll = pPlayer.getInventory(InventoryType.USE).getItem(nSlot);
        Equip pEquip = (Equip) pPlayer.getInventory(InventoryType.EQUIP).getItem(nDestination);
        ItemPotentialTierType pTier = pEquip.getPotentialBonusTier();

        if (pScroll == null || pScroll.getQuantity() <= 0 || pEquip == null/* || !ItemConstants.isPotentialScroll(pScroll.getItemId())*/) {
            c.SendPacket(WvsContext.enableActions());
            return;
        }
        Equip.ScrollResult pResult;
        boolean bCompleted = true;
        List<ModifyInventory> aModifications = new ArrayList<>();

        //boolean bPotential = ItemPotentialProvider.useBonusPotentialScroll(pEquip, pTier);
        pEquip.setBonusPotential1(Cube.generateBonusPotential(pEquip));
        pEquip.setBonusPotential2(Cube.generateBonusPotential(pEquip));
        pEquip.setBonusPotential3(Cube.generateBonusPotential(pEquip));
        /*pEquip.setBonusPotential1(generateBonusPotential(pEquip, pTier));
        pEquip.setBonusPotential2(generateBonusPotential(pEquip, pTier));
        pEquip.setBonusPotential3(generateBonusPotential(pEquip, pTier));*/

        //if (bPotential) {
        pResult = Equip.ScrollResult.SUCCESS;
        aModifications.add(new ModifyInventory(ModifyInventoryOperation.AddItem, pEquip));
        MapleInventoryManipulator.removeFromSlot(c, InventoryType.USE, pScroll.getPosition(), (short) 1, false);
        c.SendPacket(CField.enchantResult(pResult == Equip.ScrollResult.SUCCESS ? 1 : pResult == Equip.ScrollResult.CURSE ? 2 : 0));
        pPlayer.getMap().broadcastPacket(pPlayer, CField.getScrollEffect(c.getPlayer().getId(), pResult, false, pEquip.getItemId(), pScroll.getItemId()), true);
        c.SendPacket(WvsContext.inventoryOperation(true, aModifications));
        //}
    }
}
