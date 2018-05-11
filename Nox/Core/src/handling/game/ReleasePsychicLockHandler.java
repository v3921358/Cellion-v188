/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package handling.game;

import client.ClientSocket;
import net.InPacket;
import net.ProcessPacket;
import tools.packet.JobPacket;

/**
 * ReleasePsychicLock
 * @author Mazen Massoud
 */
public class ReleasePsychicLockHandler implements ProcessPacket<ClientSocket> {

    @Override
    public boolean ValidateState(ClientSocket c) {
        return true;
    }

    @Override
    public void Process(ClientSocket c, InPacket iPacket) {
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
