package server.movement.types;

import java.util.List;

import client.MapleClient;
import handling.AbstractMaplePacketHandler;
import handling.world.MovementParse;
import net.InPacket;
import server.maps.objects.MapleCharacter;
import server.movement.LifeMovementFragment;
import tools.packet.CField;
import netty.ProcessPacket;

/**
 * @author Steven
 *
 */
public class FamiliarMovement implements ProcessPacket<MapleClient> {

    @Override
    public boolean ValidateState(MapleClient c) {
        return c.isLoggedIn();
    }

    @Override
    public void Process(MapleClient c, InPacket iPacket) {
        MapleCharacter chr = c.getPlayer();
        if (chr == null || chr.getSummonedFamiliar() == null) {
            return;
        }
        iPacket.DecodeByte();
        iPacket.DecodeInteger();
        chr.getSummonedFamiliar().settEncodedGatherDuration(iPacket.DecodeInteger());
        chr.getSummonedFamiliar().setxCS(iPacket.DecodeShort());
        chr.getSummonedFamiliar().setyCS(iPacket.DecodeShort());
        chr.getSummonedFamiliar().setvXCS(iPacket.DecodeShort());
        chr.getSummonedFamiliar().setvYCS(iPacket.DecodeShort());
        List<LifeMovementFragment> res = MovementParse.parseMovement(iPacket);
        MovementParse.updatePosition(res, chr.getSummonedFamiliar());
        if (!chr.isHidden()) {
            chr.getMap().broadcastMessage(chr, CField.moveFamiliar(chr.getSummonedFamiliar(), res), chr.getTruePosition());
        } else {
            chr.getMap().broadcastGMMessage(chr, CField.moveFamiliar(chr.getSummonedFamiliar(), res), false);
        }
    }

}
