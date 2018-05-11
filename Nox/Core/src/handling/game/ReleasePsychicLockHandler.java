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
public class ReleasePsychicLockHandler implements ProcessPacket<Client> {

    @Override
    public boolean ValidateState(Client c) {
        return true;
    }

    @Override
    public void Process(Client c, InPacket iPacket) {
        int nSkillID = iPacket.DecodeInt();
        int nSLV = iPacket.DecodeInt();
        int nParentPsychicAreaKey = iPacket.DecodeInt();
        int unk = iPacket.DecodeInt();
        int nStuffID = iPacket.DecodeInt();
        // 8 bytes, pos?

        c.SendPacket(JobPacket.Kinesis.OnReleasePsychicLock(c.getPlayer().getId(), nParentPsychicAreaKey));
        c.getPlayer().getMap().broadcastMessage(c.getPlayer(), JobPacket.Kinesis.OnReleasePsychicLock(c.getPlayer().getId(), nParentPsychicAreaKey), false);
    }

}
