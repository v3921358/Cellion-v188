package handling.game;

import client.MapleClient;
import server.MapleItemInformationProvider;
import net.InPacket;
import server.MapleStatEffect;
import net.ProcessPacket;

public final class CancelItemEffectHandler implements ProcessPacket<MapleClient> {

    @Override
    public boolean ValidateState(MapleClient c) {
        return true;
    }

    @Override
    public void Process(MapleClient c, InPacket iPacket) {
        int itemId = iPacket.DecodeInt();

        MapleStatEffect effect = MapleItemInformationProvider.getInstance().getItemEffect(-itemId);
        if (effect != null) {
            c.getPlayer().cancelEffect(effect, false, -1L);
        }
    }
}
