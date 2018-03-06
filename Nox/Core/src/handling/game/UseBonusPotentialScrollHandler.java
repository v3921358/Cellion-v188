/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package handling.game;

import client.MapleClient;
import client.inventory.Equip;
import client.inventory.Item;
import client.inventory.MapleInventoryType;
import client.inventory.ModifyInventory;
import client.inventory.ModifyInventoryOperation;
import constants.ItemConstants;
import constants.ServerConstants;
import java.util.ArrayList;
import java.util.List;
import net.InPacket;
import netty.ProcessPacket;
import server.MapleInventoryManipulator;
import server.Randomizer;
import server.maps.objects.MapleCharacter;
import server.potentials.ItemPotentialProvider;
import static server.potentials.ItemPotentialProvider.generateBonusPotential;
import server.potentials.ItemPotentialTierType;
import tools.packet.CField;
import tools.packet.CWvsContext;

/**
 *
 * @author Mazen
 */
public class UseBonusPotentialScrollHandler implements ProcessPacket<MapleClient> {

    @Override
    public boolean ValidateState(MapleClient c) {
        return true;
    }

    @Override
    public void Process(MapleClient c, InPacket iPacket) {
        MapleCharacter pPlayer = c.getPlayer();
        pPlayer.updateTick(iPacket.DecodeInteger());
        short nSlot = iPacket.DecodeShort();
        short nDestination = iPacket.DecodeShort();
        pPlayer.yellowMessage("Slot: " + nSlot + " / nDestination: " + nDestination);
        
        Item pScroll = pPlayer.getInventory(MapleInventoryType.USE).getItem(nSlot);
        Equip pEquip = (Equip) pPlayer.getInventory(MapleInventoryType.EQUIP).getItem(nDestination);
        ItemPotentialTierType pTier = pEquip.getPotentialBonusTier();

        if (pScroll == null || pScroll.getQuantity() <= 0 || pEquip == null/* || !ItemConstants.isPotentialScroll(pScroll.getItemId())*/) {
            c.write(CWvsContext.enableActions());
            return;
        }
        Equip.ScrollResult pResult;
        boolean bCompleted = true;
        List<ModifyInventory> aModifications = new ArrayList<>();
        
        //boolean bPotential = ItemPotentialProvider.useBonusPotentialScroll(pEquip, pTier);
        pEquip.setBonusPotential1(generateBonusPotential(pEquip));
        pEquip.setBonusPotential2(generateBonusPotential(pEquip));
        pEquip.setBonusPotential3(generateBonusPotential(pEquip));
        /*pEquip.setBonusPotential1(generateBonusPotential(pEquip, pTier));
        pEquip.setBonusPotential2(generateBonusPotential(pEquip, pTier));
        pEquip.setBonusPotential3(generateBonusPotential(pEquip, pTier));*/
        
        //if (bPotential) {
            pResult = Equip.ScrollResult.SUCCESS;
            aModifications.add(new ModifyInventory(ModifyInventoryOperation.AddItem, pEquip));
            MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, pScroll.getPosition(), (short) 1, false);
            c.write(CField.enchantResult(pResult == Equip.ScrollResult.SUCCESS ? 1 : pResult == Equip.ScrollResult.CURSE ? 2 : 0));
            pPlayer.getMap().broadcastMessage(pPlayer, CField.getScrollEffect(c.getPlayer().getId(), pResult, false, pEquip.getItemId(), pScroll.getItemId()), true);
            c.write(CWvsContext.inventoryOperation(true, aModifications));
        //}
    }
}
