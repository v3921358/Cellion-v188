package handling.game;

import client.ClientSocket;
import net.InPacket;
import server.maps.objects.User;
import tools.packet.PetPacket;
import net.ProcessPacket;

/**
 *
 * @author Lloyd Korn
 */
public class PetChatHandler implements ProcessPacket<ClientSocket> {

    @Override
    public boolean ValidateState(ClientSocket c) {
        return true;
    }

    @Override
    public void Process(ClientSocket c, InPacket iPacket) {
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
        chr.getMap().broadcastPacket(chr, PetPacket.petChat(chr.getId(), command, text, (byte) petid), true);
    }
}
