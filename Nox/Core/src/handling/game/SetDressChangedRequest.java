package handling.game;

import client.ClientSocket;
import constants.GameConstants;
import net.InPacket;
import net.ProcessPacket;
import tools.packet.CField;

public class SetDressChangedRequest implements ProcessPacket<ClientSocket> {

    @Override
    public boolean ValidateState(ClientSocket c) {
        return true;
    }

    @Override
    public void Process(ClientSocket c, InPacket iPacket) {
        byte bDressChanged = iPacket.DecodeByte();

        System.out.printf("Dress Changed (%s) \r\n", bDressChanged);

        if (bDressChanged < 0 || bDressChanged > 1) {
            return;
        }

        if (!GameConstants.isAngelicBuster(c.getPlayer().getJob())) {
            return;
        }

        if (!c.getPlayer().isAngelicDressupState()) {
            c.getPlayer().setAngelicDressupState(true);
            c.SendPacket(CField.setAngelicBusterTransformation(bDressChanged, 1));
        } else {
            c.getPlayer().setAngelicDressupState(false);
            // c.write(CField.setAngelicBusterTransformation(0, 0));
        }

    }

}
