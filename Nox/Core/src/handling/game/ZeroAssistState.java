package handling.game;

import client.MapleClient;
import server.maps.objects.MapleCharacter;
import net.InPacket;
import tools.packet.CField;
import netty.ProcessPacket;

/**
 * This is where the zero assist state is changed
 *
 * @author Steven
 *
 */
public class ZeroAssistState implements ProcessPacket<MapleClient> {

    @Override
    public boolean ValidateState(MapleClient c) {
        return true;
    }

    @Override
    public void Process(MapleClient c, InPacket iPacket) {
        MapleCharacter chr = c.getPlayer();
        if (chr == null) {
            return;
        }
        c.getPlayer().getMap().broadcastMessage(chr, CField.zeroTagState(chr), chr.getPosition());
    }

}
