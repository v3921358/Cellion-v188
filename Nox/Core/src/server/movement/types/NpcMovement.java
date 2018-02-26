package server.movement.types;

import java.util.List;

import client.MapleClient;
import handling.AbstractMaplePacketHandler;
import handling.world.MovementParse;
import service.SendPacketOpcode;
import net.OutPacket;
import net.InPacket;
import server.movement.LifeMovementFragment;
import tools.packet.PacketHelper;
import netty.ProcessPacket;

/**
 * @author Steven
 *
 */
public class NpcMovement implements ProcessPacket<MapleClient> {

    @Override
    public boolean ValidateState(MapleClient c) {
        return c.isLoggedIn();
    }

    @Override
    public void Process(MapleClient c, InPacket iPacket) {
        OutPacket oPacket = new OutPacket(80);
        oPacket.EncodeShort(SendPacketOpcode.NpcMove.getValue());
        int length = (int) iPacket.Available();
        if (length == 10) {
            oPacket.EncodeInteger(iPacket.DecodeInteger()); //Npc Id
            oPacket.Encode(iPacket.DecodeByte());
            oPacket.Encode(iPacket.DecodeByte()); // nChatIdx
            oPacket.EncodeInteger(iPacket.DecodeInteger()); //tDuration
        } else if (length > 10) {
            oPacket.EncodeInteger(iPacket.DecodeInteger()); //Npc Id
            oPacket.Encode(iPacket.DecodeByte());
            oPacket.Encode(iPacket.DecodeByte()); // nChatIdx
            oPacket.EncodeInteger(iPacket.DecodeInteger()); //tDuration
            oPacket.EncodeInteger(iPacket.DecodeInteger()); //tEncodedGatherDuration
            oPacket.EncodeShort(iPacket.DecodeShort()); //x_CS
            oPacket.EncodeShort(iPacket.DecodeShort()); //y_CS
            oPacket.EncodeShort(iPacket.DecodeShort());//vx_CS
            oPacket.EncodeShort(iPacket.DecodeShort()); //vy_CS

            List<LifeMovementFragment> res = MovementParse.parseMovement(iPacket);
            PacketHelper.serializeMovementList(oPacket, null, res, 0);
        } else {
            return;
        }
        c.write(oPacket.ToPacket());
    }

}
