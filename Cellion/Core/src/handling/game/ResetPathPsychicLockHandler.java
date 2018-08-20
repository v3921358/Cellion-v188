/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package handling.game;

import client.ClientSocket;
import constants.GameConstants;
import static client.jobs.Kinesis.KinesisHandler.handlePsychicPoint;
import net.InPacket;
import net.ProcessPacket;

/**
 * ResetPathPsychicLock
 * @author Mazen Massoud
 */
public class ResetPathPsychicLockHandler implements ProcessPacket<ClientSocket> {

    @Override
    public boolean ValidateState(ClientSocket c) {
        return true;
    }

    @Override
    public void Process(ClientSocket c, InPacket iPacket) {
        int nSkillID = iPacket.DecodeInt();
        int nSLV = iPacket.DecodeShort();
        int nAction = iPacket.DecodeInt();
        int nActionSpeed = iPacket.DecodeInt();
        byte bData = iPacket.DecodeByte(); // guess
        if (bData == 1) { // guess
            int unk = iPacket.DecodeInt(); // always 01 00 00 00 ?
            if (nSkillID != 142120002) {
                int nParentSkillID = iPacket.DecodeInt();
                int nParentSLV = iPacket.DecodeInt();
            }
            int nCount = iPacket.DecodeInt();
            for (int i = 0; i < nCount; i++) {
                long nPsychicLockKey = iPacket.DecodeLong(); // if i wanna do this.. just get all 5 from previous psychiclock but no send packet so who cares
            }
        }

        if (GameConstants.isKinesis(c.getPlayer().getJob())) {
            handlePsychicPoint(c.getPlayer(), nSkillID);
        }
    }

}
