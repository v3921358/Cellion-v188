package handling.game;

import client.MapleClient;
import net.InPacket;
import server.maps.objects.User;
import tools.packet.PetPacket;
import net.ProcessPacket;

/**
 *
 * @author Lloyd Korn
 */
public class PetChatHandler implements ProcessPacket<MapleClient> {

    @Override
    public boolean ValidateState(MapleClient c) {
        return true;
    }

    @Override
    public void Process(MapleClient c, InPacket iPacket) {
        //System.out.println("Pet chat: " + iPacket.toString());
        if (iPacket.GetRemainder() < 12) {
            return;
        }
        User chr = c.getPlayer();

        final int petid = chr.getPetIndex((int) iPacket.DecodeLong());
        c.getPlayer().updateTick(iPacket.DecodeInt());
        short command = iPacket.DecodeShort();
        String text = iPacket.DecodeString();

        if (chr.getMap() == null || chr.getPet(petid) == null) {
            return;
        }
        chr.getMap().broadcastMessage(chr, PetPacket.petChat(chr.getId(), command, text, (byte) petid), true);
    }
}
