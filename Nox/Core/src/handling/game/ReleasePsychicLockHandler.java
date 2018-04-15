/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package handling.game;

import client.MapleClient;
import net.InPacket;
import net.ProcessPacket;
import tools.packet.JobPacket;

/**
 *
 * @author Mazen
 */
public class ReleasePsychicLockHandler implements ProcessPacket<MapleClient> {

    @Override
    public boolean ValidateState(MapleClient c) {
        return true;
    }

    @Override
    public void Process(MapleClient c, InPacket iPacket) {
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
