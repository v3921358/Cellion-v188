package handling.game;

import client.Client;
import server.maps.objects.User;
import net.InPacket;
import server.maps.MapleMapObjectType;
import tools.packet.CField;
import net.ProcessPacket;

public final class CraftCreation implements ProcessPacket<Client> {

    @Override
    public boolean ValidateState(Client c) {
        return true;
    }

    @Override
    public void Process(Client c, InPacket iPacket) {
        final User chr = c.getPlayer();
        if (chr.getMapId() != 910001000 && chr.getMap().getAllMapObjectSize(MapleMapObjectType.EXTRACTOR) <= 0) {
            return; //ardent mill
        }
        final int something = iPacket.DecodeInt(); //no clue what it is, but its between 288 and 305..
        //if (something >= 280 && something <= 310) {
        int time = iPacket.DecodeInt();
        if (time > 6000 || time < 3000) {
            time = 4000;
        }
        chr.getMap().broadcastMessage(CField.craftMake(chr.getId(), something, time));
    }

}
