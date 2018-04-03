package server.movement.types;

import java.util.List;

import client.MapleClient;
import handling.AbstractMaplePacketHandler;
import handling.world.MovementParse;
import net.InPacket;
import server.maps.objects.User;
import server.maps.objects.Summon;
import server.movement.LifeMovementFragment;
import tools.packet.CField.SummonPacket;
import netty.ProcessPacket;

/**
 * @author Steven
 *
 */
public class SummonMovement implements ProcessPacket<MapleClient> {

    @Override
    public boolean ValidateState(MapleClient c) {
        return c.isLoggedIn();
    }

    @Override
    public void Process(MapleClient c, InPacket iPacket) {
        User chr = c.getPlayer();
        int oid = iPacket.DecodeInteger();
        Summon sum = null;
        List<Summon> summons = chr.getSummonsReadLock();
        try {
            for (Summon summon : summons) {
                if (summon.getObjectId() == oid) {
                    sum = summon;
                }
            }
        } finally {
            chr.unlockSummonsReadLock();
        }
        if (sum != null) {
            sum.settEncodedGatherDuration(iPacket.DecodeInteger());
            sum.setxCS(iPacket.DecodeShort());
            sum.setyCS(iPacket.DecodeShort());
            sum.setvXCS(iPacket.DecodeShort());
            sum.setvYCS(iPacket.DecodeShort());
            List<LifeMovementFragment> res = MovementParse.parseMovement(iPacket);
            MovementParse.updatePosition(res, sum);
            chr.getMap().broadcastMessage(chr, SummonPacket.moveSummon(sum, res), sum.getTruePosition());
        }
    }
}
