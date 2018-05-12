package server.movement.types;

import java.util.ArrayList;
import java.util.List;

import client.ClientSocket;
import handling.AbstractMaplePacketHandler;
import handling.world.MovementParse;
import java.awt.Point;
import java.util.Random;
import net.InPacket;
import server.Randomizer;
import server.life.Mob;
import server.life.MonsterSkill;
import server.life.MobSkill;
import server.life.MultiTarget;
import server.maps.MapleMap;
import server.maps.objects.User;
import server.movement.LifeMovementFragment;
import tools.packet.MobPacket;
import net.ProcessPacket;
import server.life.LifeFactory;
import server.life.MonsterStats;

/**
 * @author Steven
 *
 */
public class MobMovement implements ProcessPacket<ClientSocket> {

    private static Random rand = new Random();

    @Override
    public boolean ValidateState(ClientSocket c) {
        return c.isLoggedIn();
    }

    @Override
    public void Process(ClientSocket c, InPacket iPacket) {
        int oid = iPacket.DecodeInt();
        User chr = c.getPlayer();
        if (chr == null) {
            return;
        }
        Mob pMob = chr.getMap().getMonsterByOid(oid);
        if (pMob == null) {
            return;
        }
        iPacket.DecodeByte(); //v122 = CMobTemplate::_ZtlSecureGet_dwTemplateID(v121) / 10000 == 250 || CMobTemplate::_ZtlSecureGet_dwTemplateID(v121) / 10000 == 251;

        short nMobControlSN = iPacket.DecodeShort();
        boolean bNextAcctackPossible = iPacket.DecodeBool();
        byte nSkillID = iPacket.DecodeByte(); //bTeleportEnd

        // lol this is an int all 3 of these combined
        int nSkill1 = iPacket.DecodeByte() & 0xFF;//skill_1
        int nSkill2 = iPacket.DecodeByte() & 0xFF;//skill_2
        short nSkillOpt = iPacket.DecodeShort();//skill_3,skill_4  

        int nAction = nSkillID >> 1;//or also called 'action'
        int nAttackIdx = nAction - 13; //this number might be different per version
        int nForcedAttackIdx = 0, nForcedSkillIdx = 0;

        boolean bSmartMob = false;
        String sMobNotice = "";
        switch (pMob.getId()) {
            case 8880000: //magnus
            case 8881000: //ursus
            case 8930000: //chaos vellum
                bSmartMob = true;
                nForcedSkillIdx = 0;
                nForcedAttackIdx = rand.nextInt(13);
                if (nForcedAttackIdx == nAttackIdx) {
                    nForcedAttackIdx = 0;
                }
                sMobNotice = "Magnus is preparing a dangerous move!";
                break;
            case 8920000: //chaos queen
            case 8920001: //chaos queen
            case 8920002: //chaos queen
            case 8920003: //chaos queen
                bSmartMob = true;
                if (pMob.getHPPercent() < 99 && rand.nextInt(100) < 15) {
                    int nTemplateID = 8920000 + rand.nextInt(4);
                    if (nTemplateID != pMob.getId()) {
                        Mob pNewMob = LifeFactory.getMonster(nTemplateID);
                        pNewMob.setHp(pMob.getHp());
                        pMob.getMap().replaceQueen(c.getPlayer(), pMob, pNewMob);
                    }
                }
                break;
        }

        if (chr.isAdmin()) {
            chr.yellowMessage(String.format("[Mob Movement Debug] nAction: %s | BaseNum: %s | nForcedAttackIdx: %s", nAttackIdx, nSkillID, nForcedAttackIdx));
        }

        if (bNextAcctackPossible) {
            List<MonsterSkill> skills = pMob.getStats().getSkills();
            int size = skills.size();
            if (size > 0) {
                MonsterSkill nextSkill = skills.get(nForcedSkillIdx > 0 ? nForcedSkillIdx : Randomizer.nextInt(size));

                if (chr.isAdmin()) {
                    chr.yellowMessage(String.format("[Mob Movement Debug] nSkill: %s", nextSkill.getSkillId()));
                }

                if (bSmartMob) {
                    c.SendPacket(MobPacket.SmartMobNotice(1, oid, 2, nextSkill.getSkillId(), sMobNotice));
                    c.getPlayer().getMap().broadcastPacket(MobPacket.SmartMobNotice(2, oid, 2, nextSkill.getSkillId(), sMobNotice));
                }

                final long tNow = System.currentTimeMillis();
                long tLastUsed = pMob.getLastSkillUsed(nSkill1);
                MobSkill skill = nextSkill.getSkill();

                if (tLastUsed == 0 || ((tNow - tLastUsed) > skill.getCoolTime())) {
                    pMob.setLastSkillUsed(nSkill1, tNow, skill.getCoolTime());

                    int reqHp = (int) (((float) pMob.getHp() / pMob.getMobMaxHp()) * 100); // In case this monster have 2.1b and above HP
                    if (reqHp <= skill.getHP()) {
                        if (skill.getCoolTime() == 0) {
                            skill.applyEffect(chr, pMob, true);
                        }
                    }
                }
            }
        }

        List<MultiTarget> multiTarget = new ArrayList<>();
        //for (int i = 0; i < iPacket.DecodeByte(); i++) {//aMultiTargetForBall
        //    multiTarget.add(new MultiTarget(iPacket.DecodeShort(), iPacket.DecodeShort()));
        //}

        List<Short> randomTime = new ArrayList<>();
        //for (int i = 0; i < iPacket.DecodeByte(); i++) {//aRandTimeforAreaAttack
        //    randomTime.add(iPacket.DecodeShort());
        //}
/*
        iPacket.DecodeByte(); //IsCheatMobMoveRand
        iPacket.DecodeInt(); //GetHackedCode
        iPacket.DecodeInt(); //nMoveAction?
        iPacket.DecodeInt(); //nMoveAction?
        iPacket.DecodeInt(); //tHitExpire
        iPacket.DecodeByte(); //fucking pointer.. (v20->vfptr[4].Update)(v20);
         */

        iPacket.DecodeLong(); // Padding 20 bytes
        iPacket.DecodeLong(); // Padding 20 bytes
        iPacket.DecodeInt(); // Padding 20 bytes

        pMob.settEncodedGatherDuration(iPacket.DecodeInt());
        pMob.setxCS(iPacket.DecodeShort());
        pMob.setyCS(iPacket.DecodeShort());
        pMob.setvXCS(iPacket.DecodeShort());
        pMob.setvYCS(iPacket.DecodeShort());
        List<LifeMovementFragment> res = MovementParse.parseMovement(iPacket);
        c.SendPacket(MobPacket.moveMonsterResponse(oid, nMobControlSN, (int) pMob.getMp(), pMob.isControllerHasAggro(), nSkill1, nSkill2, nForcedAttackIdx));
        MapleMap map = c.getPlayer().getMap();
        MovementParse.updatePosition(res, pMob);
        map.moveMonster(pMob, pMob.getPosition());
        map.broadcastPacket(chr, MobPacket.moveMonster(pMob, bNextAcctackPossible, nSkillID, nSkill1, nSkill2, nSkillOpt, oid, res, multiTarget, randomTime), pMob.getPosition());
    }
}
