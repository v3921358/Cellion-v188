package handling.game;

import client.ClientSocket;
import net.InPacket;
import tools.packet.WvsContext;
import net.ProcessPacket;

/**
 *
 * @author
 */
public class UseOwlHandler implements ProcessPacket<ClientSocket> {

    @Override
    public boolean ValidateState(ClientSocket c) {
        return true;
    }

    @Override
    public void Process(ClientSocket c, InPacket iPacket) {
        if (c.getPlayer().haveItem(5230000, 1, true, false) || c.getPlayer().haveItem(2310000, 1, true, false)) {
            if (c.getPlayer().getMapId() >= 910000000 && c.getPlayer().getMapId() <= 910000022) {
                c.SendPacket(WvsContext.getOwlOpen());
            } else {
                c.getPlayer().dropMessage(5, "This can only be used inside the Free Market.");
                c.SendPacket(WvsContext.enableActions());
            }
        }
    }
}
