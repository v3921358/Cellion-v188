/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package handling.game;

import client.MapleClient;
import net.InPacket;
import netty.ProcessPacket;
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
        int nSkillID = iPacket.DecodeInteger();
        int nSLV = iPacket.DecodeInteger();
        int nParentPsychicAreaKey = iPacket.DecodeInteger();
        int unk = iPacket.DecodeInteger();
        int nStuffID = iPacket.DecodeInteger();
        // 8 bytes, pos?

        c.write(JobPacket.Kinesis.OnReleasePsychicLock(c.getPlayer().getId(), nParentPsychicAreaKey));
        c.getPlayer().getMap().broadcastMessage(c.getPlayer(), JobPacket.Kinesis.OnReleasePsychicLock(c.getPlayer().getId(), nParentPsychicAreaKey), false);
    }

}
