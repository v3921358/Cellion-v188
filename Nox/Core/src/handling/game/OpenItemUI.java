package handling.game;

import client.MapleClient;
import server.maps.objects.MapleCharacter;
import net.InPacket;
import netty.ProcessPacket;

/**
 *
 * @author Steven
 *
 */
public class OpenItemUI implements ProcessPacket<MapleClient> {

    @Override
    public boolean ValidateState(MapleClient c) {
        return true;
    }

    @Override
    public void Process(MapleClient c, InPacket iPacket) {
        MapleCharacter chr = c.getPlayer();
        chr.updateTick(iPacket.DecodeInteger());
        short pos = iPacket.DecodeShort();
        int itemId = iPacket.DecodeInteger();
    }

}
