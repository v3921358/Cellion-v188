package server.movement.types;

import java.awt.Point;
import java.util.List;

import client.MapleClient;
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
public class HakuMovement implements ProcessPacket<MapleClient> {

    @Override
    public boolean ValidateState(MapleClient c) {
        return c.isLoggedIn();
    }

    @Override
    public void Process(MapleClient c, InPacket iPacket) {
        User chr = c.getPlayer();
        if (chr == null) {
            return;
        }
        if (chr.getHaku() == null) {
            return;
        }
        chr.getHaku().settEncodedGatherDuration(iPacket.DecodeInt());
        chr.getHaku().setxCS(iPacket.DecodeShort());
        chr.getHaku().setyCS(iPacket.DecodeShort());
        chr.getHaku().setvXCS(iPacket.DecodeShort());
        chr.getHaku().setvYCS(iPacket.DecodeShort());
        List<LifeMovementFragment> res = MovementParse.parseMovement(iPacket);
        if (chr.getMap() != null) {
            Point pos = new Point(chr.getHaku().getPosition());
            MovementParse.updatePosition(res, chr.getHaku());
            //chr.getHaku().updatePosition(res);
            chr.getMap().broadcastMessage(chr, CField.moveHaku(chr.getHaku(), pos, res), false);
        }
    }

}
