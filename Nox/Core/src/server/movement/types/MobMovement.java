package server.movement.types;

import java.util.ArrayList;
import java.util.List;

import client.MapleClient;
import handling.AbstractMaplePacketHandler;
import handling.world.MovementParse;
import net.InPacket;
import server.Randomizer;
import server.life.Mob;
import server.life.MapleMonsterSkill;
import server.life.MobSkill;
import server.life.MultiTarget;
import server.maps.MapleMap;
import server.maps.objects.User;
import server.movement.LifeMovementFragment;
import tools.packet.MobPacket;
import net.ProcessPacket;

/**
 * @author Steven
 *
 */
public class MobMovement implements ProcessPacket<MapleClient> {

    @Override
    public boolean ValidateState(MapleClient c) {
        return c.isLoggedIn();
    }

    @Override
    public void Process(MapleClient c, InPacket iPacket) {
        int oid = iPacket.DecodeInt();
        User chr = c.getPlayer();
        if (chr == null) {
            return;
        }
        Mob monster = chr.getMap().getMonsterByOid(oid);
        if (monster == null) {
            return;
        }
        iPacket.DecodeByte(); //v122 = CMobTemplate::_ZtlSecureGet_dwTemplateID(v121) / 10000 == 250 || CMobTemplate::_ZtlSecureGet_dwTemplateID(v121) / 10000 == 251;

        short moveid = iPacket.DecodeShort();
        boolean useSkill = iPacket.DecodeByte() == 1;
        byte teleportEnd = iPacket.DecodeByte(); //bTeleportEnd

        // lol this is an int all 3 of these combined
        int skillId = iPacket.DecodeByte() & 0xFF;//skill_1
        int skillLv = iPacket.DecodeByte() & 0xFF;//skill_2
        short option = iPacket.DecodeShort();//skill_3,skill_4  

        int nAction = teleportEnd >> 1;//or also called 'action'
        int skillIdx = nAction - 30;
        int forceAttack = 0;

        if (useSkill) {
            List<MapleMonsterSkill> skills = monster.getStats().getSkills();
            int size = skills.size();
            if (size > 0) {
                MapleMonsterSkill nextSkill = skills.get(Randomizer.nextInt(size));

                final long now = System.currentTimeMillis();
                long ls = monster.getLastSkillUsed(skillId);
                MobSkill skill = nextSkill.getSkill();

                if (ls == 0 || ((now - ls) > skill.getCoolTime())) {
                    monster.setLastSkillUsed(skillId, now, skill.getCoolTime());

                    int reqHp = (int) (((float) monster.getHp() / monster.getMobMaxHp()) * 100); // In case this monster have 2.1b and above HP
                    if (reqHp <= skill.getHP()) {
                        if (skill.getCoolTime() == 0) {
                            skill.applyEffect(chr, monster, true);
                        } else if (skillIdx >= 0 && skillIdx <= 16) {

                            MapleMonsterSkill msi = monster.getStats().getSkills().get(skillIdx);
                            if (msi != null) {
                                //     if (msi.getAfterAttack() != -1) {
                                //monster.getMap().broadcastMessage(MobPacket.setAfterAttack(monster.getObjectId(), msi.getAfterAttack(), nAction, (skill & 1) != 0));
                                //     }
                                /* if (msi.getNextSkill() > 0) {
                                    msi.getSkill().setDelay(c.getPlayer(), monster, msi.getNextSkill(), option);
                                    return;
                                }*/
                                //monster.resetSkillCommand();
                                msi.getSkill().applyEffect(c.getPlayer(), monster, true, option);
                            }
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

        monster.settEncodedGatherDuration(iPacket.DecodeInt());
        monster.setxCS(iPacket.DecodeShort());
        monster.setyCS(iPacket.DecodeShort());
        monster.setvXCS(iPacket.DecodeShort());
        monster.setvYCS(iPacket.DecodeShort());
        List<LifeMovementFragment> res = MovementParse.parseMovement(iPacket);
        c.SendPacket(MobPacket.moveMonsterResponse(oid, moveid, (int) monster.getMp(), monster.isControllerHasAggro(), skillId, skillLv, forceAttack));
        MapleMap map = c.getPlayer().getMap();
        MovementParse.updatePosition(res, monster);
        map.moveMonster(monster, monster.getPosition());
        map.broadcastMessage(chr, MobPacket.moveMonster(monster, useSkill, teleportEnd, skillId, skillLv, option, oid, res, multiTarget, randomTime), monster.getPosition());
    }
}
