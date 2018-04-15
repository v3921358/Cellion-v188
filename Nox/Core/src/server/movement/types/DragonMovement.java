package server.movement.types;

import java.awt.Point;
import java.lang.ref.WeakReference;
import java.util.List;

import client.MapleClient;
import handling.AbstractMaplePacketHandler;
import handling.world.MovementParse;
import net.InPacket;
import server.Timer.CloneTimer;
import server.maps.MapleMap;
import server.maps.objects.User;
import server.movement.LifeMovementFragment;
import tools.packet.CField;
import net.ProcessPacket;

/**
 * @author Steven
 *
 */
public class DragonMovement implements ProcessPacket<MapleClient> {

    @Override
    public boolean ValidateState(MapleClient c) {
        return c.isLoggedIn();
    }

    @Override
    public void Process(MapleClient c, InPacket iPacket) {
        User chr = c.getPlayer();
        if (chr == null || chr.getDragon() == null) {
            return;
        }

        final Point pos = chr.getDragon().getPosition();
        chr.getDragon().settEncodedGatherDuration(iPacket.DecodeInt());
        chr.getDragon().setxCS(iPacket.DecodeShort());
        chr.getDragon().setyCS(iPacket.DecodeShort());
        chr.getDragon().setvXCS(iPacket.DecodeShort());
        chr.getDragon().setvYCS(iPacket.DecodeShort());
        final List<LifeMovementFragment> res = MovementParse.parseMovement(iPacket);
        MovementParse.updatePosition(res, chr.getDragon());

        if (!chr.isHidden()) {
            chr.getMap().broadcastMessage(chr, CField.moveDragon(chr.getDragon(), pos, res), chr.getTruePosition());
        }
    }
}
