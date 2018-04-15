package handling.game;

import client.MapleClient;
import server.maps.objects.User;
import net.InPacket;
import net.ProcessPacket;

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
        User chr = c.getPlayer();
        chr.updateTick(iPacket.DecodeInt());
        short pos = iPacket.DecodeShort();
        int itemId = iPacket.DecodeInt();
    }

}
