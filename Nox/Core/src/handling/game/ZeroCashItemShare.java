package handling.game;

import java.util.Collections;

import client.ClientSocket;
import client.inventory.Equip;
import client.inventory.MapleInventoryType;
import client.inventory.ModifyInventory;
import client.inventory.ModifyInventoryOperation;
import constants.GameConstants;
import server.maps.objects.User;
import net.InPacket;
import tools.packet.WvsContext;
import net.ProcessPacket;

/**
 * @author Steven
 *
 */
public class ZeroCashItemShare implements ProcessPacket<ClientSocket> {

    @Override
    public boolean ValidateState(ClientSocket c) {
        return true;
    }

    /**
     * This handler handles if the cash item is going to be shared between alpha and beta
     *
     * @see handling.MaplePacketHandler#handlePacket(tools.data.input.InPacket, client.Client)
     */
    @Override
    public void Process(ClientSocket c, InPacket iPacket) {
        User chr = c.getPlayer();
        if (chr == null) {
            return;
        }
        int position = iPacket.DecodeInt();
        boolean isShared = iPacket.DecodeByte() > 0;
        byte pos = (byte) (-100 - position);
        Equip alpha = (Equip) chr.getInventory(MapleInventoryType.EQUIPPED).getItem(pos);
        if (alpha != null) {
            alpha.setBetaShare(isShared);
        }
        if (isShared) {
            Equip beta = (Equip) alpha.copy();
            beta.setPosition(GameConstants.getBetaCashPosition(alpha.getPosition()));
            c.SendPacket(WvsContext.inventoryOperation(true, Collections.singletonList(new ModifyInventory(ModifyInventoryOperation.AddItem, beta, (short) 0)), true));
        }
    }

}
