package handling.game;

import client.MapleClient;
import server.maps.objects.User;
import net.InPacket;
import tools.packet.CField;
import net.ProcessPacket;

public final class OnUserSitRequest implements ProcessPacket<MapleClient> {

    @Override
    public boolean ValidateState(MapleClient c) {
        return true;
    }

    @Override
    public void Process(MapleClient c, InPacket iPacket) {
        final User chr = c.getPlayer();

        if (chr.getMap().getSharedMapResources().noChair) {
            return;
        }

        int chairId = iPacket.DecodeShort();

        if (chairId == -1) {
            chr.cancelFishingTask();
            chr.setChair(0);
            c.SendPacket(CField.cancelChair(chr.getId(), -1));

            chr.getMap().broadcastMessage(chr, CField.showChair(chr.getId(), 0), false);
        } else {
            chr.setChair(chairId);
            c.SendPacket(CField.cancelChair(chr.getId(), chairId));
        }
    }
}
