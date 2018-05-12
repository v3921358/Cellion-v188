/*
 * Cellion Development
 */
package handling.game;

import client.ClientSocket;
import client.jobs.KinesisPsychicLock;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import net.InPacket;
import net.ProcessPacket;
import server.life.Mob;
import tools.packet.JobPacket;

/**
 * CreatePsychicLock
 * @author Mazen Massoud
 */
public class CreatePsychicLockHandler implements ProcessPacket<ClientSocket> {

    @Override
    public boolean ValidateState(ClientSocket c) {
        return true;
    }

    @Override
    public void Process(ClientSocket c, InPacket iPacket) {
        List<KinesisPsychicLock> PsychicLock = new ArrayList<>();
        int nSkillID = iPacket.DecodeInt();
        short nSLV = iPacket.DecodeShort();
        int nAction = iPacket.DecodeInt();
        int nActionSpeed = iPacket.DecodeInt();
        long nMobCurHP = 100;
        long nMobMaxHP = 100;
        int count = c.getPlayer().getTotalSkillLevel(142120000) > 0 ? 5 : 3;
        for (int i = 0; i < count; i++) {
            byte bData = iPacket.DecodeByte();
            if (bData == 1) {
                int nLocalPsychicLockKey = iPacket.DecodeInt();
                int nParentPsychicAreaKey = iPacket.DecodeInt();
                int dwMobID = iPacket.DecodeInt(); // Object Id
                int nStuffID = iPacket.DecodeShort();
                int nUsableCount = iPacket.DecodeShort();
                int posRelPosFirst = iPacket.DecodeByte();
                Point posStart = new Point(iPacket.DecodeInt(), iPacket.DecodeInt());
                Point posRelPosSecond = new Point(iPacket.DecodeInt(), iPacket.DecodeInt());
                if (dwMobID != 0) {
                    Mob monster = c.getPlayer().getMap().getMonsterByOid(dwMobID);
                    nMobCurHP = monster.getHp();
                    nMobMaxHP = monster.getMobMaxHp();
                }
                KinesisPsychicLock Lock = new KinesisPsychicLock(nLocalPsychicLockKey, nParentPsychicAreaKey, dwMobID, nStuffID, nMobMaxHP, nMobCurHP, nUsableCount, posRelPosFirst, posStart, posRelPosSecond);
                PsychicLock.add(Lock);
            }
        }
        byte end = iPacket.DecodeByte();
        c.SendPacket(JobPacket.Kinesis.OnCreatePsychicLock(c.getPlayer().getId(), nSkillID, nSLV, nAction, nActionSpeed, PsychicLock));
        c.getPlayer().getMap().broadcastPacket(c.getPlayer(), JobPacket.Kinesis.OnCreatePsychicLock(c.getPlayer().getId(), nSkillID, nSLV, nAction, nActionSpeed, PsychicLock), false);
    }

}
