package handling.game;

import client.ClientSocket;
import net.InPacket;
import tools.packet.CField;
import tools.packet.WvsContext;
import net.ProcessPacket;

public final class EnterAzwanEventHandler implements ProcessPacket<ClientSocket> {

    @Override
    public boolean ValidateState(ClientSocket c) {
        return true;
    }

    @Override
    public void Process(ClientSocket c, InPacket iPacket) {
        if (c.getPlayer() == null || c.getPlayer().getMap() == null) {
            //c.SendPacket(CField.pvpBlocked(1));
            c.SendPacket(WvsContext.enableActions());
            return;
        }
        int mapid = iPacket.DecodeInt();
        c.getPlayer().changeMap(c.getChannelServer().getMapFactory().getMap(mapid));
    }

}
