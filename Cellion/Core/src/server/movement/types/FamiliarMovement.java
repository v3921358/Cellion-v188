package server.movement.types;

import java.util.List;

import client.ClientSocket;
import handling.AbstractMaplePacketHandler;
import handling.world.MovementParse;
import net.InPacket;
import server.maps.objects.User;
import server.movement.LifeMovementFragment;
import tools.packet.CField;
import net.ProcessPacket;

/**
 * @author Steven
 *
 */
public class FamiliarMovement implements ProcessPacket<ClientSocket> {

    @Override
    public boolean ValidateState(ClientSocket c) {
        return c.isLoggedIn();
    }

    @Override
    public void Process(ClientSocket c, InPacket iPacket) {
        User chr = c.getPlayer();
        if (chr == null || chr.getSummonedFamiliar() == null) {
            return;
        }
        iPacket.DecodeByte();
        iPacket.DecodeInt();
        chr.getSummonedFamiliar().settEncodedGatherDuration(iPacket.DecodeInt());
        chr.getSummonedFamiliar().setxCS(iPacket.DecodeShort());
        chr.getSummonedFamiliar().setyCS(iPacket.DecodeShort());
        chr.getSummonedFamiliar().setvXCS(iPacket.DecodeShort());
        chr.getSummonedFamiliar().setvYCS(iPacket.DecodeShort());
        List<LifeMovementFragment> res = MovementParse.parseMovement(iPacket);
        MovementParse.updatePosition(res, chr.getSummonedFamiliar());
        if (!chr.isHidden()) {
            chr.getMap().broadcastPacket(chr, CField.moveFamiliar(chr.getSummonedFamiliar(), res), chr.getTruePosition());
        } else {
            chr.getMap().broadcastGMMessage(chr, CField.moveFamiliar(chr.getSummonedFamiliar(), res), false);
        }
    }

}
