package handling.game;

import client.Client;
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
public class ZeroAssistState implements ProcessPacket<Client> {

    @Override
    public boolean ValidateState(Client c) {
        return true;
    }

    @Override
    public void Process(Client c, InPacket iPacket) {
        User chr = c.getPlayer();
        if (chr == null) {
            return;
        }
        c.getPlayer().getMap().broadcastMessage(chr, CField.zeroTagState(chr), chr.getPosition());
    }

}
