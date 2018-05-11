package server.movement.types;

import java.util.List;

import client.Client;
import handling.AbstractMaplePacketHandler;
import handling.world.MovementParse;
import service.SendPacketOpcode;
import net.OutPacket;
import net.InPacket;
import server.movement.LifeMovementFragment;
import tools.packet.PacketHelper;
import net.ProcessPacket;

/**
 * @author Steven
 *
 */
public class NpcMovement implements ProcessPacket<Client> {

    @Override
    public boolean ValidateState(Client c) {
        return c.isLoggedIn();
    }

    @Override
    public void Process(Client c, InPacket iPacket) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.NpcMove.getValue());
        int length = (int) iPacket.GetRemainder();
        if (length == 10) {
            oPacket.EncodeInt(iPacket.DecodeInt()); //Npc Id
            oPacket.EncodeByte(iPacket.DecodeByte());
            oPacket.EncodeByte(iPacket.DecodeByte()); // nChatIdx
            oPacket.EncodeInt(iPacket.DecodeInt()); //tDuration
        } else if (length > 10) {
            oPacket.EncodeInt(iPacket.DecodeInt()); //Npc Id
            oPacket.EncodeByte(iPacket.DecodeByte());
            oPacket.EncodeByte(iPacket.DecodeByte()); // nChatIdx
            oPacket.EncodeInt(iPacket.DecodeInt()); //tDuration
            oPacket.EncodeInt(iPacket.DecodeInt()); //tEncodedGatherDuration
            oPacket.EncodeShort(iPacket.DecodeShort()); //x_CS
            oPacket.EncodeShort(iPacket.DecodeShort()); //y_CS
            oPacket.EncodeShort(iPacket.DecodeShort());//vx_CS
            oPacket.EncodeShort(iPacket.DecodeShort()); //vy_CS

            List<LifeMovementFragment> res = MovementParse.parseMovement(iPacket);
            PacketHelper.serializeMovementList(oPacket, null, res, 0);
        } else {
            return;
        }
        c.SendPacket(oPacket);
    }

}
