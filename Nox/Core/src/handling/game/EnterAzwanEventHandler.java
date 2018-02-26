package handling.game;

import client.MapleClient;
import net.InPacket;
import tools.packet.CField;
import tools.packet.CWvsContext;
import netty.ProcessPacket;

public final class EnterAzwanEventHandler implements ProcessPacket<MapleClient> {

    @Override
    public boolean ValidateState(MapleClient c) {
        return true;
    }

    @Override
    public void Process(MapleClient c, InPacket iPacket) {
        if (c.getPlayer() == null || c.getPlayer().getMap() == null) {
            c.write(CField.pvpBlocked(1));
            c.write(CWvsContext.enableActions());
            return;
        }
        int mapid = iPacket.DecodeInteger();
        c.getPlayer().changeMap(c.getChannelServer().getMapFactory().getMap(mapid));
    }

}
