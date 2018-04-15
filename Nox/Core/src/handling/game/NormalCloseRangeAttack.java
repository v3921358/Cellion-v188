package handling.game;

import client.MapleClient;
import net.InPacket;
import net.ProcessPacket;

public final class NormalCloseRangeAttack implements ProcessPacket<MapleClient> {

    @Override
    public boolean ValidateState(MapleClient c) {
        return true;
    }

    @Override
    public void Process(MapleClient c, InPacket iPacket) {
        CloseRangeAttack.closeRangeAttack(iPacket, c, c.getPlayer(), false);
    }

}
