package handling.game;

import client.MapleClient;
import net.InPacket;
import netty.ProcessPacket;

public final class PassiveEnergyCloseRangeAttack implements ProcessPacket<MapleClient> {

    @Override
    public boolean ValidateState(MapleClient c) {
        return true;
    }

    @Override
    public void Process(MapleClient c, InPacket iPacket) {
        CloseRangeAttack.closeRangeAttack(iPacket, c, c.getPlayer(), true);
    }

}
