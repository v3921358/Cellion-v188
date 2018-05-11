package handling.game;

import client.Client;
import net.InPacket;
import net.ProcessPacket;

public final class PassiveEnergyCloseRangeAttack implements ProcessPacket<Client> {

    @Override
    public boolean ValidateState(Client c) {
        return true;
    }

    @Override
    public void Process(Client c, InPacket iPacket) {
        CloseRangeAttack.closeRangeAttack(iPacket, c, c.getPlayer(), true);
    }

}
