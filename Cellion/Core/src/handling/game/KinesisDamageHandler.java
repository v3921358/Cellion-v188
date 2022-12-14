/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package handling.game;

import client.ClientSocket;
import net.InPacket;
import net.OutPacket;
import net.ProcessPacket;
import server.maps.objects.User;
import service.SendPacketOpcode;

/**
 *
 * @author Mazen Massoud
 */
public final class KinesisDamageHandler implements ProcessPacket<ClientSocket> {

    @Override
    public boolean ValidateState(ClientSocket c) {
        return true;
    }

    @Override
    public void Process(ClientSocket c, InPacket iPacket) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.UserReleasePsychicArea.getValue());
        oPacket.EncodeInt(iPacket.DecodeInt());
        oPacket.EncodeInt(1);

        c.SendPacket(oPacket);
    }
}
