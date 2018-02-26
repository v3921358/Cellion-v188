package handling.game;

import java.util.Collections;

import client.MapleClient;
import client.inventory.Equip;
import client.inventory.MapleInventoryType;
import client.inventory.ModifyInventory;
import client.inventory.ModifyInventoryOperation;
import constants.GameConstants;
import server.maps.objects.MapleCharacter;
import net.InPacket;
import tools.packet.CWvsContext;
import netty.ProcessPacket;

/**
 * @author Steven
 *
 */
public class ZeroCashItemShare implements ProcessPacket<MapleClient> {

    @Override
    public boolean ValidateState(MapleClient c) {
        return true;
    }

    /**
     * This handler handles if the cash item is going to be shared between alpha and beta
     *
     * @see handling.MaplePacketHandler#handlePacket(tools.data.input.InPacket, client.MapleClient)
     */
    @Override
    public void Process(MapleClient c, InPacket iPacket) {
        MapleCharacter chr = c.getPlayer();
        if (chr == null) {
            return;
        }
        int position = iPacket.DecodeInteger();
        boolean isShared = iPacket.DecodeByte() > 0;
        byte pos = (byte) (-100 - position);
        Equip alpha = (Equip) chr.getInventory(MapleInventoryType.EQUIPPED).getItem(pos);
        if (alpha != null) {
            alpha.setBetaShare(isShared);
        }
        if (isShared) {
            Equip beta = (Equip) alpha.copy();
            beta.setPosition(GameConstants.getBetaCashPosition(alpha.getPosition()));
            c.write(CWvsContext.inventoryOperation(true, Collections.singletonList(new ModifyInventory(ModifyInventoryOperation.AddItem, beta, (short) 0)), true));
        }
    }

}
