/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package handling.game;

import client.Client;
import net.InPacket;
import net.OutPacket;
import net.ProcessPacket;
import service.SendPacketOpcode;

/**
 *
 * @author Mazen Massoud
 */
public final class KinesisCancelPsychicRequest implements ProcessPacket<Client> {

    @Override
    public boolean ValidateState(Client c) {
        return true;
    }

    @Override
    public void Process(Client c, InPacket iPacket) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.UserLeaveFieldPsychicInfo.getValue());
        oPacket.EncodeInt(c.getPlayer().getId());
        oPacket.EncodeInt(iPacket.DecodeInt());

        c.SendPacket(oPacket);

    }
}
