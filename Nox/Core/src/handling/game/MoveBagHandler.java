package handling.game;

import client.ClientSocket;
import client.inventory.MapleInventoryType;
import server.MapleInventoryManipulator;
import net.InPacket;
import tools.packet.WvsContext;
import net.ProcessPacket;

/**
 *
 * @author
 */
public class MoveBagHandler implements ProcessPacket<ClientSocket> {

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
        final boolean srcFirst = iPacket.DecodeInt() > 0;
        short dst = (short) iPacket.DecodeInt();                                       //01 00
        if (iPacket.DecodeByte() != 4) { //must be etc
            c.SendPacket(WvsContext.enableActions());
            return;
        }
        short src = iPacket.DecodeShort();                                             //00 00
        MapleInventoryManipulator.move(c, MapleInventoryType.ETC, srcFirst ? dst : src, srcFirst ? src : dst);
    }
}
