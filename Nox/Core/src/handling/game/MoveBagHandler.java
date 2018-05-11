package handling.game;

import client.Client;
import client.inventory.MapleInventoryType;
import server.MapleInventoryManipulator;
import net.InPacket;
import tools.packet.CWvsContext;
import net.ProcessPacket;

/**
 *
 * @author
 */
public class MoveBagHandler implements ProcessPacket<Client> {

    @Override
    public boolean ValidateState(Client c) {
        return true;
    }

    @Override
    public void Process(Client c, InPacket iPacket) {
        if (c.getPlayer().hasBlockedInventory()) { //hack
            return;
        }
        c.getPlayer().setScrolledPosition((short) 0);
        c.getPlayer().updateTick(iPacket.DecodeInt());
        final boolean srcFirst = iPacket.DecodeInt() > 0;
        short dst = (short) iPacket.DecodeInt();                                       //01 00
        if (iPacket.DecodeByte() != 4) { //must be etc
            c.SendPacket(CWvsContext.enableActions());
            return;
        }
        short src = iPacket.DecodeShort();                                             //00 00
        MapleInventoryManipulator.move(c, MapleInventoryType.ETC, srcFirst ? dst : src, srcFirst ? src : dst);
    }
}
