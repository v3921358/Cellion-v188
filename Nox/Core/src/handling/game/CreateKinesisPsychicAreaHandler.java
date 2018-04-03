/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package handling.game;

import client.MapleClient;
import constants.GameConstants;
import static client.jobs.Kinesis.KinesisHandler.handlePsychicPoint;
import java.awt.Point;
import net.InPacket;
import netty.ProcessPacket;
import tools.packet.JobPacket;

/**
 *
 * @author Mazen
 */
public class CreateKinesisPsychicAreaHandler implements ProcessPacket<MapleClient> {

    @Override
    public boolean ValidateState(MapleClient c) {
        return true;
    }

    @Override
    public void Process(MapleClient c, InPacket iPacket) {
        int nAction = iPacket.DecodeInteger();
        int nActionSpeed = iPacket.DecodeInteger();
        int unknown = iPacket.DecodeInteger();
        int nParentPsychicAreaKey = iPacket.DecodeInteger();
        int nPsychicAreaKey = Math.abs(nParentPsychicAreaKey);
        int nSkillID = iPacket.DecodeInteger();
        short nSLV = iPacket.DecodeShort();
        int unknown2 = iPacket.DecodeInteger();
        byte isLeft = iPacket.DecodeByte();
        short nSkeletonFilePathIdx = iPacket.DecodeShort();
        short nSkeletonAniIdx = iPacket.DecodeShort();
        short nSkeletonLoop = iPacket.DecodeShort();
        Point posStart = new Point(iPacket.DecodeInteger(), iPacket.DecodeInteger());
        int nDurationTime = 14000; // Not in packet?

        if (GameConstants.isKinesis(c.getPlayer().getJob())) {
            handlePsychicPoint(c.getPlayer(), nSkillID);
        }

        c.write(JobPacket.Kinesis.OnCreatePsychicArea(c.getPlayer().getId(), nAction, nActionSpeed, nParentPsychicAreaKey, nSkillID, nSLV, nPsychicAreaKey, nDurationTime, isLeft, nSkeletonFilePathIdx, nSkeletonAniIdx, nSkeletonLoop, posStart));
        c.getPlayer().getMap().broadcastMessage(c.getPlayer(), JobPacket.Kinesis.OnCreatePsychicArea(c.getPlayer().getId(), nAction, nActionSpeed, nParentPsychicAreaKey, nSkillID, nSLV, nPsychicAreaKey, nDurationTime, isLeft, nSkeletonFilePathIdx, nSkeletonAniIdx, nSkeletonLoop, posStart), false);
    }
}
