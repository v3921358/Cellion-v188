/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package handling.game;

import client.MapleClient;
import constants.GameConstants;
import static handling.jobs.Kinesis.KinesisHandler.handlePsychicPoint;
import net.InPacket;
import netty.ProcessPacket;

/**
 *
 * @author Mazen
 */
public class ResetPathPsychicLockHandler implements ProcessPacket<MapleClient> {

    @Override
    public boolean ValidateState(MapleClient c) {
        return true;
    }

    @Override
    public void Process(MapleClient c, InPacket iPacket) {
        int nSkillID = iPacket.DecodeInteger();
        int nSLV = iPacket.DecodeShort();
        int nAction = iPacket.DecodeInteger();
        int nActionSpeed = iPacket.DecodeInteger();
        byte bData = iPacket.DecodeByte(); // guess
        if (bData == 1) { // guess
            int unk = iPacket.DecodeInteger(); // always 01 00 00 00 ?
            if (nSkillID != 142120002) {
                int nParentSkillID = iPacket.DecodeInteger();
                int nParentSLV = iPacket.DecodeInteger();
            }
            int nCount = iPacket.DecodeInteger();
            for (int i = 0; i < nCount; i++) {
                long nPsychicLockKey = iPacket.DecodeLong(); // if i wanna do this.. just get all 5 from previous psychiclock but no send packet so who cares
            }
        }

        if (GameConstants.isKinesis(c.getPlayer().getJob())) {
            handlePsychicPoint(c.getPlayer(), nSkillID);
        }
    }

}
