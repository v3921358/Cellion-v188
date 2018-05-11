package handling.game;

import client.ClientSocket;
import net.InPacket;
import net.ProcessPacket;

public final class NormalCloseRangeAttack implements ProcessPacket<ClientSocket> {

    @Override
    public boolean ValidateState(ClientSocket c) {
        return true;
    }

    @Override
    public void Process(ClientSocket c, InPacket iPacket) {
        CloseRangeAttack.closeRangeAttack(iPacket, c, c.getPlayer(), false);
    }

}
