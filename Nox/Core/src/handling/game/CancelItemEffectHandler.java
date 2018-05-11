package handling.game;

import client.ClientSocket;
import server.MapleItemInformationProvider;
import net.InPacket;
import server.MapleStatEffect;
import net.ProcessPacket;

public final class CancelItemEffectHandler implements ProcessPacket<ClientSocket> {

    @Override
    public boolean ValidateState(ClientSocket c) {
        return true;
    }

    @Override
    public void Process(ClientSocket c, InPacket iPacket) {
        int itemId = iPacket.DecodeInt();

        MapleStatEffect effect = MapleItemInformationProvider.getInstance().getItemEffect(-itemId);
        if (effect != null) {
            c.getPlayer().cancelEffect(effect, false, -1L);
        }
    }
}
