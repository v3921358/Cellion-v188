/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package handling.game;

import client.MapleClient;
import net.InPacket;
import net.OutPacket;
import netty.ProcessPacket;
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
        OutPacket oPacket = new OutPacket(80);
        oPacket.EncodeShort(SendPacketOpcode.UserLeaveFieldPsychicInfo.getValue());
        oPacket.EncodeInteger(c.getPlayer().getId());
        oPacket.EncodeInteger(iPacket.DecodeInteger());

        c.write(oPacket.ToPacket());

    }
}
