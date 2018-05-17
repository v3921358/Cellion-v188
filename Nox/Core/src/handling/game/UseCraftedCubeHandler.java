package handling.game;

import client.ClientSocket;
import client.inventory.Equip;
import client.inventory.Item;
import client.inventory.MapleInventoryType;
import constants.GameConstants;
import constants.ItemConstants;
import net.InPacket;
import server.MapleItemInformationProvider;
import server.maps.objects.User;
import tools.LogHelper;
import tools.packet.CField;
import net.ProcessPacket;
import server.MapleInventoryManipulator;
import server.potentials.Cube;

/**
 * Crafted Cube Handler
 * @author Mazen Massoud
 */
public class UseCraftedCubeHandler implements ProcessPacket<ClientSocket> {

    @Override
    public boolean ValidateState(ClientSocket c) {
        return true;
    }

    @Override
    public void Process(ClientSocket c, InPacket iPacket) {
        User pPlayer = c.getPlayer();
        pPlayer.updateTick(iPacket.DecodeInt());
        short nCubeSlot = iPacket.DecodeShort();
        short nEquipSlot = iPacket.DecodeShort();
        
        final Item pCube = pPlayer.getInventory(MapleInventoryType.USE).getItem(nCubeSlot);
        final Equip pEquip = (Equip) pPlayer.getInventory(MapleInventoryType.EQUIP).getItem(nEquipSlot);
        
        if (pCube == null || pEquip == null || pCube.getItemId() / 10000 != 271 || pCube.getQuantity() <= 0) {
            c.SendPacket(CField.enchantResult(0));
            return;
        }
        
        final MapleItemInformationProvider pItemProvider = MapleItemInformationProvider.getInstance();
        final int nReqLevel = pItemProvider.getReqLevel(pEquip.getItemId());
        int nEquipmentCutOffLevel = GameConstants.maxLevel;
        if (pCube.getItemId() == ItemConstants.HERMES_CUBE) nEquipmentCutOffLevel = 99;
        
        if (nReqLevel > nEquipmentCutOffLevel) {
            LogHelper.PACKET_EDIT_HACK.get().info(String.format("[UseCraftedCubeHandler] %s [ChrID: %d; AccId %d] has tried to use a crafted cube on an item beyond the level requirement. EquipmentID = %d, CubeID = %d", pPlayer.getName(), pPlayer.getId(), c.getAccID(), pEquip.getItemId(), pCube.getItemId()));
            c.Close();
            return;
        }
        
        boolean bUsed = Cube.OnCubeRequest(pPlayer, pEquip, pCube.getItemId()); // Handle the Cube
        if (bUsed) MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, nCubeSlot, (short) 1, false, true);
    }
}
