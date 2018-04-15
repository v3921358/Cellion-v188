/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package handling.game;

import client.MapleClient;
import net.InPacket;
import net.OutPacket;
import net.ProcessPacket;
import service.SendPacketOpcode;

/**
 *
 * @author Mazen
 */
public final class KinesisCancelPsychicRequest implements ProcessPacket<MapleClient> {

    @Override
    public boolean ValidateState(MapleClient c) {
        return true;
    }

    @Override
    public void Process(MapleClient c, InPacket iPacket) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.UserLeaveFieldPsychicInfo.getValue());
        oPacket.EncodeInt(c.getPlayer().getId());
        oPacket.EncodeInt(iPacket.DecodeInt());

        c.SendPacket(oPacket);

    }
}
