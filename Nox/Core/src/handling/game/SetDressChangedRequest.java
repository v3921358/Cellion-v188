package handling.game;

import client.MapleClient;
import constants.GameConstants;
import net.InPacket;
import net.ProcessPacket;
import tools.packet.CField;

public class SetDressChangedRequest implements ProcessPacket<MapleClient> {

    @Override
    public boolean ValidateState(MapleClient c) {
        return true;
    }

    @Override
    public void Process(MapleClient c, InPacket iPacket) {
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
