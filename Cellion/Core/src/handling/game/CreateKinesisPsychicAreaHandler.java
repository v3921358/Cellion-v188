/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package handling.game;

import client.ClientSocket;
import constants.GameConstants;
import static client.jobs.Kinesis.KinesisHandler.handlePsychicPoint;
import java.awt.Point;
import net.InPacket;
import net.ProcessPacket;
import tools.packet.JobPacket;

/**
 * CreateKinesisPsychicArea
 * @author Mazen Massoud
 */
public class CreateKinesisPsychicAreaHandler implements ProcessPacket<ClientSocket> {

    @Override
    public boolean ValidateState(ClientSocket c) {
        return true;
    }

    @Override
    public void Process(ClientSocket c, InPacket iPacket) {
        int nAction = iPacket.DecodeInt();
        int nActionSpeed = iPacket.DecodeInt();
        int unknown = iPacket.DecodeInt();
        int nParentPsychicAreaKey = iPacket.DecodeInt();
        int nPsychicAreaKey = Math.abs(nParentPsychicAreaKey);
        int nSkillID = iPacket.DecodeInt();
        short nSLV = iPacket.DecodeShort();
        int unknown2 = iPacket.DecodeInt();
        byte isLeft = iPacket.DecodeByte();
        short nSkeletonFilePathIdx = iPacket.DecodeShort();
        short nSkeletonAniIdx = iPacket.DecodeShort();
        short nSkeletonLoop = iPacket.DecodeShort();
        Point posStart = new Point(iPacket.DecodeInt(), iPacket.DecodeInt());
        int nDurationTime = 14000; // Not in packet?

        if (GameConstants.isKinesis(c.getPlayer().getJob())) {
            handlePsychicPoint(c.getPlayer(), nSkillID);
        }

        c.SendPacket(JobPacket.Kinesis.OnCreatePsychicArea(c.getPlayer().getId(), nAction, nActionSpeed, nParentPsychicAreaKey, nSkillID, nSLV, nPsychicAreaKey, nDurationTime, isLeft, nSkeletonFilePathIdx, nSkeletonAniIdx, nSkeletonLoop, posStart));
        c.getPlayer().getMap().broadcastPacket(c.getPlayer(), JobPacket.Kinesis.OnCreatePsychicArea(c.getPlayer().getId(), nAction, nActionSpeed, nParentPsychicAreaKey, nSkillID, nSLV, nPsychicAreaKey, nDurationTime, isLeft, nSkeletonFilePathIdx, nSkeletonAniIdx, nSkeletonLoop, posStart), false);
    }
}
