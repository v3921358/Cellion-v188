package handling.game;

import client.MapleClient;
import net.InPacket;
import tools.packet.CField;
import tools.packet.CWvsContext;
import net.ProcessPacket;

public final class EnterAzwanEventHandler implements ProcessPacket<MapleClient> {

    @Override
    public boolean ValidateState(MapleClient c) {
        return true;
    }

    @Override
    public void Process(MapleClient c, InPacket iPacket) {
        if (c.getPlayer() == null || c.getPlayer().getMap() == null) {
            //c.SendPacket(CField.pvpBlocked(1));
            c.SendPacket(CWvsContext.enableActions());
            return;
        }
        int mapid = iPacket.DecodeInt();
        c.getPlayer().changeMap(c.getChannelServer().getMapFactory().getMap(mapid));
    }

}
