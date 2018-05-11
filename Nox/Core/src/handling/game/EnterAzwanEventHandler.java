package handling.game;

import client.Client;
import net.InPacket;
import tools.packet.CField;
import tools.packet.CWvsContext;
import net.ProcessPacket;

public final class EnterAzwanEventHandler implements ProcessPacket<Client> {

    @Override
    public boolean ValidateState(Client c) {
        return true;
    }

    @Override
    public void Process(Client c, InPacket iPacket) {
        if (c.getPlayer() == null || c.getPlayer().getMap() == null) {
            //c.SendPacket(CField.pvpBlocked(1));
            c.SendPacket(CWvsContext.enableActions());
            return;
        }
        int mapid = iPacket.DecodeInt();
        c.getPlayer().changeMap(c.getChannelServer().getMapFactory().getMap(mapid));
    }

}
