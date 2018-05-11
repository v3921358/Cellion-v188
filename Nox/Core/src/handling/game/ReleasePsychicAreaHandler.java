/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package handling.game;

import client.Client;
import net.InPacket;
import net.ProcessPacket;
import tools.packet.JobPacket;

/**
 *
 * @author Mazen Massoud
 */
public class ReleasePsychicAreaHandler implements ProcessPacket<Client> {

    @Override
    public boolean ValidateState(Client c) {
        return true;
    }

    @Override
    public void Process(Client c, InPacket iPacket) {
        int nPsychicAreaKey = iPacket.DecodeInt();

        c.SendPacket(JobPacket.Kinesis.OnReleasePsychicArea(c.getPlayer().getId(), nPsychicAreaKey));
        c.getPlayer().getMap().broadcastMessage(c.getPlayer(), JobPacket.Kinesis.OnReleasePsychicArea(c.getPlayer().getId(), nPsychicAreaKey), false);
    }

}
