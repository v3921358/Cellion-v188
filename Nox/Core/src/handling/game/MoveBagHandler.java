package handling.game;

import client.MapleClient;
import client.inventory.MapleInventoryType;
import server.MapleInventoryManipulator;
import net.InPacket;
import tools.packet.CWvsContext;
import netty.ProcessPacket;

/**
 *
 * @author
 */
public class MoveBagHandler implements ProcessPacket<MapleClient> {

    @Override
    public boolean ValidateState(MapleClient c) {
        return true;
    }

    @Override
    public void Process(MapleClient c, InPacket iPacket) {
        if (c.getPlayer().hasBlockedInventory()) { //hack
            return;
        }
        c.getPlayer().setScrolledPosition((short) 0);
        c.getPlayer().updateTick(iPacket.DecodeInteger());
        final boolean srcFirst = iPacket.DecodeInteger() > 0;
        short dst = (short) iPacket.DecodeInteger();                                       //01 00
        if (iPacket.DecodeByte() != 4) { //must be etc
            c.write(CWvsContext.enableActions());
            return;
        }
        short src = iPacket.DecodeShort();                                             //00 00
        MapleInventoryManipulator.move(c, MapleInventoryType.ETC, srcFirst ? dst : src, srcFirst ? src : dst);
    }
}
