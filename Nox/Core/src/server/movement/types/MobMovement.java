package server.movement.types;

import java.util.ArrayList;
import java.util.List;

import client.ClientSocket;
import handling.world.MovementParse;
import java.awt.Point;
import java.util.Arrays;
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
import server.maps.MapleMapObject;
import server.maps.MapleMapObjectType;

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

        String sMobNotice = "";
        switch (pMob.getId()) {
            case 8910100: //von bon
                if (pMob.getHPPercent() > 80) {
                    nForcedAttackIdx = rand.nextInt(2);
                    nForcedSkillIdx = 170; //Just shoots, no clocks.
                } else {
                    nForcedAttackIdx = rand.nextInt(3);
                }
                break;
            case 8910000: //chaos von bon
                //TODO: Inner world portal spawns
                if (pMob.getHPPercent() > 80) {
                    nForcedAttackIdx = rand.nextInt(2);
                    nForcedSkillIdx = 170; //Just shoots, no clocks.
                } else if (pMob.getHPPercent() > 10) {
                    nForcedAttackIdx = rand.nextInt(3);
                } else {
                    nForcedAttackIdx = rand.nextInt(9);//Unlocks his push moves.
                }
                break;
            case 8910101: //disembodied von bon
            case 8910001: //chaos disembodied von bon
                break;
            case 8920100: //queen
            case 8920101: //queen
            case 8920102: //queen
            case 8920103: //queen
            case 8920000: //chaos queen
            case 8920001: //chaos queen
            case 8920002: //chaos queen
            case 8920003: //chaos queen
                if (rand.nextInt(100) < 10) {
                    int nTemplateID = pMob.getId() - (pMob.getId() % 10) + rand.nextInt(4);
                    if (nTemplateID != pMob.getId()) {
                        Mob pNewMob = LifeFactory.getMonster(nTemplateID);
                        pNewMob.setHp(pMob.getHp());
                        pMob.getMap().ReplaceMobDelayed(c.getPlayer(), pMob, pNewMob);
                    }
                }
                break;
            case 8900100: //pierre
            case 8900101: //pierre
            case 8900102: //pierre twister
            case 8900000: //chaos pierre
            case 8900001: //chaos pierre
            case 8900002: //chaos pierre twister
                nForcedAttackIdx = rand.nextInt(2);
                if (rand.nextInt(100) < 10) {
                    int nTemplateID = pMob.getId() - (pMob.getId() % 10) + rand.nextInt(3);
                    if (nTemplateID != pMob.getId()) {
                        Mob pNewMob = LifeFactory.getMonster(nTemplateID);
                        pNewMob.setHp(pMob.getHp());
                        pMob.getMap().ReplaceMobDelayed(c.getPlayer(), pMob, pNewMob);
                    }
                }
                break;
        }

        if (chr.isDeveloper() && chr.usingExtraDebug()) {
            chr.yellowMessage(String.format("[Mob Movement Debug] nAction: %s | BaseNum: %s | nForcedAttackIdx: %s", nAttackIdx, nSkillID, nForcedAttackIdx));
        }

        if (bNextAcctackPossible) {
            List<MonsterSkill> skills = pMob.getStats().getSkills();
            int size = skills.size();
            if (size > 0) {
                MonsterSkill nextSkill = skills.get(Randomizer.nextInt(size));

                if (nForcedSkillIdx != 0) {
                    for (MonsterSkill SkillEntry : skills) {
                        if (SkillEntry.getSkillId() == nForcedSkillIdx) {
                            nextSkill = SkillEntry;
                            break;
                        }
                    }
                }

                if (!sMobNotice.isEmpty()) {
                    c.SendPacket(MobPacket.SmartMobNotice(1, oid, 2, nextSkill.getSkillId(), sMobNotice));
                    c.getPlayer().getMap().broadcastPacket(MobPacket.SmartMobNotice(2, oid, 2, nextSkill.getSkillId(), sMobNotice));
                }

                final long tNow = System.currentTimeMillis();
                long tLastUsed = pMob.getLastSkillUsed(nSkill1);
                MobSkill skill = nextSkill.getSkill();

                if (tLastUsed == 0 || ((tNow - tLastUsed) > skill.getCoolTime())) {

                    if (chr.isDeveloper() && chr.usingExtraDebug()) {
                        chr.yellowMessage(String.format("[Mob Movement Debug] nSkill: %s", nextSkill.getSkillId()));
                    }

                    pMob.setLastSkillUsed(nSkill1, tNow, skill.getCoolTime());
                    skill.applyEffect(chr, pMob, true);
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
        
        //if ((Math.abs(c.getPlayer().getPosition().x - pMob.getPosition().x) < 2000) 
        //    && (Math.abs(c.getPlayer().getPosition().y - pMob.getPosition().y) < 700) ) {
        
            for (LifeMovementFragment m : res) {
                Point nPOS = m.getPosition();
                pMob.setPosition(nPOS);
                pMob.setFh(m.getFoothold());
                if (res.size() > 0) c.SendPacket(MobPacket.moveMonsterResponse(oid, nMobControlSN, (int) pMob.getMp(), pMob.isControllerHasAggro(), nSkill1, nSkill2, nForcedAttackIdx));
            }
        //}
        
        MapleMap map = c.getPlayer().getMap();
        MovementParse.updatePosition(res, pMob);
        map.moveMonster(pMob, pMob.getPosition());
        map.broadcastPacket(chr, MobPacket.moveMonster(pMob, bNextAcctackPossible, nSkillID, nSkill1, nSkill2, nSkillOpt, oid, res, multiTarget, randomTime), pMob.getPosition());
    }
}
