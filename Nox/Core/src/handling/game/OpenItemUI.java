package handling.game;

import client.Client;
import server.maps.objects.User;
import net.InPacket;
import net.ProcessPacket;

/**
 *
 * @author Steven
 *
 */
public class OpenItemUI implements ProcessPacket<Client> {

    @Override
    public boolean ValidateState(Client c) {
        return true;
    }

    @Override
    public void Process(Client c, InPacket iPacket) {
        User chr = c.getPlayer();
        chr.updateTick(iPacket.DecodeInt());
        short pos = iPacket.DecodeShort();
        int itemId = iPacket.DecodeInt();
    }

}
