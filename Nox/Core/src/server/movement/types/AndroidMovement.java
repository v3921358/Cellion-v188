package server.movement.types;

import java.util.List;

import client.Client;
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
public class AndroidMovement implements ProcessPacket<Client> {

    @Override
    public boolean ValidateState(Client c) {
        return c.isLoggedIn();
    }

    @Override
    public void Process(Client c, InPacket iPacket) {
        User chr = c.getPlayer();
        if (chr == null || chr.getAndroid() == null) {
            return;
        }
        chr.getAndroid().settEncodedGatherDuration(iPacket.DecodeInt()); //tEncodedGatherDuration
        chr.getAndroid().setxCS(iPacket.DecodeShort()); //x_CS
        chr.getAndroid().setyCS(iPacket.DecodeShort()); //y_CS
        chr.getAndroid().setvXCS(iPacket.DecodeShort());//vx_CS
        chr.getAndroid().setvYCS(iPacket.DecodeShort()); //vy_CS
        List<LifeMovementFragment> res = MovementParse.parseMovement(iPacket);
        if (chr.getMap() != null) {
            chr.getAndroid().updatePosition(res);
            chr.getMap().broadcastMessage(chr, CField.moveAndroid(chr, res), false);
        }
    }

}
