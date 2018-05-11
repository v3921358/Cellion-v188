package handling.game;

import client.ClientSocket;
import client.inventory.MapleInventoryType;
import server.MapleInventoryManipulator;
import net.InPacket;
import net.ProcessPacket;

/**
 *
 * @author
 */
public class SwitchBagHandler implements ProcessPacket<ClientSocket> {

    @Override
    public boolean ValidateState(ClientSocket c) {
        return true;
    }

    @Override
    public void Process(ClientSocket c, InPacket iPacket) {
        if (c.getPlayer().hasBlockedInventory()) { //hack
            return;
        }
        c.getPlayer().setScrolledPosition((short) 0);
        c.getPlayer().updateTick(iPacket.DecodeInt());
        final short src = (short) iPacket.DecodeInt();                                       //01 00
        final short dst = (short) iPacket.DecodeInt();                                       //00 00
        if (src < 100 || dst < 100) {
            return;
        }
        MapleInventoryManipulator.move(c, MapleInventoryType.ETC, src, dst);
    }

}
