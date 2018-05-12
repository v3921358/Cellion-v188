package handling.game;

import client.ClientSocket;
import server.maps.objects.User;
import net.InPacket;
import tools.packet.CField;
import net.ProcessPacket;

/**
 * This is where the zero assist state is changed
 *
 * @author Steven
 *
 */
public class ZeroAssistState implements ProcessPacket<ClientSocket> {

    @Override
    public boolean ValidateState(ClientSocket c) {
        return true;
    }

    @Override
    public void Process(ClientSocket c, InPacket iPacket) {
        User chr = c.getPlayer();
        if (chr == null) {
            return;
        }
        c.getPlayer().getMap().broadcastPacket(chr, CField.zeroTagState(chr), chr.getPosition());
    }

}
