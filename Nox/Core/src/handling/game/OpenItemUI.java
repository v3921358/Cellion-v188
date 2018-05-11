package handling.game;

import client.ClientSocket;
import server.maps.objects.User;
import net.InPacket;
import net.ProcessPacket;

/**
 *
 * @author Steven
 *
 */
public class OpenItemUI implements ProcessPacket<ClientSocket> {

    @Override
    public boolean ValidateState(ClientSocket c) {
        return true;
    }

    @Override
    public void Process(ClientSocket c, InPacket iPacket) {
        User chr = c.getPlayer();
        chr.updateTick(iPacket.DecodeInt());
        short pos = iPacket.DecodeShort();
        int itemId = iPacket.DecodeInt();
    }

}
