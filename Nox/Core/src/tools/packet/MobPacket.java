package tools.packet;

import java.awt.Rectangle;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import client.MonsterStatus;
import client.MonsterStatusEffect;
import service.SendPacketOpcode;
import net.OutPacket;
import net.Packet;
import server.life.MapleLifeFactory;
import server.life.MapleMonster;
import server.life.MobSkill;
import server.life.MultiTarget;
import server.maps.MapleMap;
import server.maps.SharedMapResources.MapleNodeInfo;
import server.movement.LifeMovementFragment;

public class MobPacket {

    public static Packet damageMonster(int oid, long damage) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.MobDamaged.getValue());
        oPacket.EncodeInteger(oid);
        oPacket.EncodeInteger(0); //nSkillID
        oPacket.EncodeShort(0); //tDelay
        oPacket.Encode(0); //bUser
        oPacket.EncodeInteger((int) damage); //nSLV

        return oPacket.ToPacket();
    }

    public static Packet damageFriendlyMob(MapleMonster mob, long damage, boolean display) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.MobDamaged.getValue());
        oPacket.EncodeInteger(mob.getObjectId());
        oPacket.EncodeInteger(0); //nSkillID
        oPacket.EncodeShort(0); //tDelay
        oPacket.Encode(display ? 1 : 2);
        if (damage > Integer.MAX_VALUE) {
            damage = Integer.MAX_VALUE;
        }
        oPacket.EncodeInteger((int) damage);
        return oPacket.ToPacket();
    }

    public static Packet killMonster(int oid, int animation, boolean azwan) {
        OutPacket oPacket = new OutPacket(80);

        if (azwan) {
            oPacket.EncodeShort(SendPacketOpcode.MinionLeaveField.getValue());
        } else {
            oPacket.EncodeShort(SendPacketOpcode.MobLeaveField.getValue());
        }
        boolean a = false; //idk
        boolean b = false; //idk
        if (azwan) {
            oPacket.Encode(a ? 1 : 0);
            oPacket.Encode(b ? 1 : 0);
        }
        oPacket.EncodeInteger(oid);
        if (azwan) {
            if (a) {
                oPacket.Encode(0);
                if (b) {
                    //set mob temporary stat
                } else {
                    //set mob temporary stat
                }
            } else {
                if (b) {
                    //idk
                } else {
                    //idk
                }
            }
            return oPacket.ToPacket();
        }
        oPacket.Encode(animation);
        if (animation == 4) {
            oPacket.EncodeInteger(-1);
        }

        return oPacket.ToPacket();
    }

    public static Packet suckMonster(int oid, int chr) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.MobLeaveField.getValue());
        oPacket.EncodeInteger(oid);
        oPacket.Encode(4);
        oPacket.EncodeInteger(chr);

        return oPacket.ToPacket();
    }

    public static Packet healMonster(int oid, int heal) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.MobDamaged.getValue());
        oPacket.EncodeInteger(oid);
        oPacket.EncodeInteger(0); //nSkillID
        oPacket.EncodeShort(0); //tDelay
        oPacket.Encode(0); //bUser
        oPacket.EncodeInteger(-heal);

        return oPacket.ToPacket();
    }

    public static Packet MobToMobDamage(int oid, int dmg, int mobid, boolean azwan) {
        OutPacket oPacket = new OutPacket(80);

        if (azwan) {
            oPacket.EncodeShort(SendPacketOpcode.MobAttackedByMob.getValue());
        } else {
            oPacket.EncodeShort(SendPacketOpcode.MobAttackedByMob.getValue());
        }
        oPacket.EncodeInteger(oid);
        oPacket.Encode(0);
        oPacket.EncodeInteger(dmg);
        oPacket.EncodeInteger(mobid);
        oPacket.Encode(1);

        return oPacket.ToPacket();
    }

    public static Packet getMobSkillEffect(int oid, int skillid, int cid, int skilllevel) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.MobSpecialEffectBySkill.getValue());
        oPacket.EncodeInteger(oid);
        oPacket.EncodeInteger(skillid);
        oPacket.EncodeInteger(cid);
        oPacket.EncodeShort(skilllevel);

        return oPacket.ToPacket();
    }

    public static Packet getMobCoolEffect(int oid, int itemid) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.MobEffectByItem.getValue());
        oPacket.EncodeInteger(oid);
        oPacket.EncodeInteger(itemid);

        return oPacket.ToPacket();
    }

    public static Packet showMonsterHP(int oid, int remhppercentage) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.MobHPIndicator.getValue());
        oPacket.EncodeInteger(oid);
        oPacket.Encode(remhppercentage);

        return oPacket.ToPacket();
    }

    public static Packet showCygnusAttack(int oid) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.MobForcedAction.getValue());
        oPacket.EncodeInteger(oid);

        return oPacket.ToPacket();
    }

    public static Packet showMonsterResist(int oid) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.MobAttackBlock.getValue());
        oPacket.EncodeInteger(oid);
        oPacket.EncodeInteger(0);
        oPacket.EncodeShort(1);
        oPacket.EncodeInteger(0);

        return oPacket.ToPacket();
    }

    public static Packet showBossHP(MapleMonster mob) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.BossEnvironment.getValue());
        oPacket.Encode(6);
        oPacket.EncodeInteger(mob.getId() == 9400589 ? 9300184 : mob.getId());
        oPacket.EncodeLong(mob.getHp()); // Version 180 
        oPacket.EncodeLong(mob.getMobMaxHp()); // Version 180 
        oPacket.Encode(mob.getStats().getTagColor());
        oPacket.Encode(mob.getStats().getTagBgColor());
        return oPacket.ToPacket();
    }

    public static Packet showBossHP(int monsterId, long currentHp, long maxHp) {
        MapleMonster mob = MapleLifeFactory.getMonster(monsterId);

        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.BossEnvironment.getValue());
        oPacket.Encode(6);
        oPacket.EncodeInteger(monsterId);
        oPacket.EncodeLong(currentHp <= 0 ? -1 : currentHp); // Version 180 
        oPacket.EncodeLong(maxHp); // Version 180 
        oPacket.Encode(mob.getStats().getTagColor());
        oPacket.Encode(mob.getStats().getTagBgColor());
        //oPacket.Encode(6);
        //oPacket.Encode(5);

        /*if (currentHp > Integer.MAX_VALUE) {
            oPacket.EncodeInteger((int) (currentHp / maxHp * Integer.MAX_VALUE));
        } else {
            oPacket.EncodeInteger((int) (currentHp <= 0 ? -1 : currentHp));
        }
        if (maxHp > Integer.MAX_VALUE) {
            oPacket.EncodeInteger(Integer.MAX_VALUE);
        } else {
            oPacket.EncodeInteger((int) maxHp);
        }
        oPacket.Encode(6);
        oPacket.Encode(5);*/
        return oPacket.ToPacket();
    }

    public static Packet moveMonster(MapleMonster monster, boolean useskill, int teleportEnd, int skillId, int skillLv, short option, int oid, List<LifeMovementFragment> moves, List<MultiTarget> multiTarget, List<Short> randomTime) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.MobMove.getValue());
        oPacket.EncodeInteger(oid);
        oPacket.Encode(useskill);
        oPacket.Encode(teleportEnd);
        oPacket.Encode(skillId);
        oPacket.Encode(skillLv);
        oPacket.EncodeShort(option);
        oPacket.Encode(multiTarget.size()); //m_aMultiTargetForBall
        for (MultiTarget multi : multiTarget) {
            oPacket.EncodePosition(multi.getPosition());
        }
        oPacket.Encode(randomTime.size()); //m_aRandTimeforAreaAttack
        for (Short time : randomTime) {
            oPacket.EncodeShort(time);
        }
        PacketHelper.serializeMovementList(oPacket, monster, moves, 0);
        oPacket.Encode(0);
        return oPacket.ToPacket();
    }

    public static Packet MobSkillDelay(int objectId, int skillID, int skillLv, int skillAfter, short sequenceDelay, List<Rectangle> skillRectInfo) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.MobSkillDelay.getValue());
        oPacket.EncodeInteger(objectId);
        oPacket.EncodeInteger(skillAfter);
        oPacket.EncodeInteger(skillID);
        oPacket.EncodeInteger(skillLv);
        oPacket.EncodeInteger(sequenceDelay);
        oPacket.EncodeInteger(skillRectInfo.size());
        for (Rectangle rect : skillRectInfo) {
            oPacket.EncodeInteger(rect.x);
            oPacket.EncodeInteger(rect.y);
            oPacket.EncodeInteger(rect.x + rect.width);
            oPacket.EncodeInteger(rect.y + rect.height);
        }
        return oPacket.ToPacket();
    }

    public static Packet spawnMonster(MapleMonster life, int spawnType, int link, boolean azwan) {
        OutPacket oPacket = new OutPacket(80);
        oPacket.EncodeShort(SendPacketOpcode.MobEnterField.getValue());

        oPacket.Encode(0);//bSealedInsteadDead
        oPacket.EncodeInteger(life.getObjectId());
        oPacket.Encode(1); //nCalcDamageIndex
        oPacket.EncodeInteger(life.getId());
        addMonsterStatus(oPacket, life);
        addMonsterInformation(oPacket, life, spawnType, link, true, false);

        return oPacket.ToPacket();
    }

    public static void addMonsterStatus(OutPacket oPacket, MapleMonster life) {

        // ForcedMobStat::Decode
        oPacket.Encode(life.getChangedStats() != null);
        if (life.getChangedStats() != null) {
            oPacket.EncodeInteger(life.getChangedStats().getHp() > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) life.getChangedStats().getHp());
            oPacket.EncodeInteger((int) life.getChangedStats().getMp());
            oPacket.EncodeInteger(life.getChangedStats().getExp());
            oPacket.EncodeInteger(life.getChangedStats().getWatk());
            oPacket.EncodeInteger(life.getChangedStats().getMatk());
            oPacket.EncodeInteger(life.getChangedStats().getPDRate());
            oPacket.EncodeInteger(life.getChangedStats().getMDRate());
            oPacket.EncodeInteger(life.getChangedStats().getAcc());
            oPacket.EncodeInteger(life.getChangedStats().getEva());
            oPacket.EncodeInteger(life.getChangedStats().getPushed());
            oPacket.EncodeInteger(life.getChangedStats().getSpeed());
            oPacket.EncodeInteger(life.getChangedStats().getLevel());
            oPacket.EncodeInteger(life.getChangedStats().getnUserCount());
        }

        // CMob::SetTemporaryStat
        oPacket.Fill(0, 12); //mask
    }

    public static void addMonsterInformation(OutPacket oPacket, MapleMonster life, int spawnType, int link, boolean summon, boolean newSpawn) {
        oPacket.EncodePosition(life.getTruePosition());
        oPacket.Encode(life.getStance());
        if (life.getId() == 8910000 || life.getId() == 8910100) { // Von Bon
            oPacket.Encode(0);
        }
        oPacket.EncodeShort(life.getFh()); // pfhCur
        oPacket.EncodeShort(life.getFh()); // nHomeFoothold

        oPacket.EncodeShort(spawnType); // nAppearType
        if (spawnType == -3 || spawnType >= 0) {
            oPacket.EncodeInteger(link);
        }
        /*if (summon) {
            oPacket.EncodeShort(spawnType); // nAppearType
            if (spawnType == -3 || spawnType >= 0) {
                oPacket.EncodeInteger(link);
            }
        } else {
            oPacket.Encode(newSpawn ? -2 : life.isFake() ? -4 : -1);
        }*/
        oPacket.Encode(life.getCarnivalTeam()); // nTeamForMCarnival
        oPacket.EncodeLong(life.getHp());
        oPacket.EncodeInteger(0); // nEffectItemID
        oPacket.EncodeInteger(0); // m_nPhase
        oPacket.EncodeInteger(0); // m_nCurZoneDataType
        oPacket.EncodeInteger(0); // m_dwRefImgMobID
        oPacket.EncodeInteger(0); // ?
        oPacket.Encode(0); // ?
        oPacket.EncodeInteger(-1); // nAfterAttack
        oPacket.EncodeInteger(-1); // nCurrentAction
        oPacket.Encode(life.isFacingLeft()); // bIsLeft
        oPacket.EncodeInteger(0);
        oPacket.EncodeInteger(0x64); // m_nScale
        oPacket.EncodeInteger(-1); // m_nEliteGrade
        oPacket.Encode(0);
        oPacket.Encode(0);
        oPacket.EncodeInteger(0); // New v176
        oPacket.EncodeInteger(0); // New v176
        oPacket.EncodeInteger(0);
    }

    public static Packet controlMonster(MapleMonster life, boolean newSpawn, boolean aggro, boolean azwan) {
        OutPacket oPacket = new OutPacket(80);

        if (azwan) {
            oPacket.EncodeShort(SendPacketOpcode.MinionChangeController.getValue());
        } else {
            oPacket.EncodeShort(SendPacketOpcode.MobChangeController.getValue());
            oPacket.Encode(aggro ? 2 : 1); // 0 = not moving at all, 1 = only attack when nearby, 2 = chasing + attack
        }
        oPacket.EncodeInteger(life.getObjectId());
        oPacket.Encode(1);// 1 = Control normal, 5 = Control none?
        oPacket.EncodeInteger(life.getId()); // idk?
        addMonsterStatus(oPacket, life);
        addMonsterInformation(oPacket, life, 0, 0, false, newSpawn);

        return oPacket.ToPacket();
    }

    public static Packet stopControllingMonster(MapleMonster life, boolean azwan) {
        OutPacket oPacket = new OutPacket(80);

        if (azwan) {
            oPacket.EncodeShort(SendPacketOpcode.MinionChangeController.getValue());
        } else {
            oPacket.EncodeShort(SendPacketOpcode.MobChangeController.getValue());
            oPacket.Encode(0);
        }

        oPacket.EncodeInteger(life.getObjectId());
        oPacket.Encode(0);

        if (azwan) {
            oPacket.Encode(0);
            oPacket.EncodeInteger(0);
            oPacket.Encode(0);
            addMonsterStatus(oPacket, life);
            addMonsterInformation(oPacket, life, 0, 0, false, false);
        }
        return oPacket.ToPacket();
    }

    public static Packet makeMonsterReal(MapleMonster life, boolean azwan) {
        return spawnMonster(life, -1, 0, azwan);
    }

    public static Packet makeMonsterFake(MapleMonster life, boolean azwan) {
        return spawnMonster(life, -4, 0, azwan);
    }

    public static Packet makeMonsterEffect(MapleMonster life, int effect, boolean azwan) {
        return spawnMonster(life, effect, 0, azwan);
    }

    public static Packet moveMonsterResponse(int objectid, short moveid, int currentMp, boolean useSkills, int skillId, int skillLevel, int forcedAttack) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.MobCtrlAck.getValue());
        oPacket.EncodeInteger(objectid);
        oPacket.EncodeShort(moveid);//nMobCtrlSN
        oPacket.Encode(useSkills);//bNextAttackPossible
        oPacket.EncodeInteger(currentMp);//m_nMP
        oPacket.EncodeInteger(skillId);//m_nSkillCommand  [Changed from int8 to int32 on v176]
        oPacket.Encode(skillLevel);//m_nSLV
        oPacket.EncodeInteger(forcedAttack);//nForcedAttackIdx

        return oPacket.ToPacket();
    }

    public static Packet getMonsterSkill(int objectid) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.MobForcedSkillAction.getValue());
        oPacket.EncodeInteger(objectid);
        oPacket.EncodeLong(0);

        return oPacket.ToPacket();
    }

    public static Packet getMonsterTeleport(int objectid, int x, int y) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.MobTeleport.getValue());
        oPacket.EncodeInteger(objectid);
        oPacket.EncodeInteger(x);
        oPacket.EncodeInteger(y);

        return oPacket.ToPacket();
    }

    public static Packet applyMonsterStatus(int oid, MonsterStatus mse, int x, MobSkill skil) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.MobStatSet.getValue());
        oPacket.EncodeInteger(oid);
        PacketHelper.writeSingleMask(oPacket, mse);

        oPacket.EncodeInteger(x);
        oPacket.EncodeShort(skil.getSkillId());
        oPacket.EncodeShort(skil.getSkillLevel());
        oPacket.EncodeShort(mse.isEmpty() ? 1 : 0);

        oPacket.EncodeShort(0);
        oPacket.Encode(2);//was 1
        oPacket.Fill(0, 30);

        return oPacket.ToPacket();
    }

    public static Packet applyMonsterStatus(MapleMonster mons, MonsterStatusEffect ms) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.MobStatSet.getValue());
        oPacket.EncodeInteger(mons.getObjectId());
        PacketHelper.writeSingleMask(oPacket, ms.getStati());

        oPacket.EncodeInteger(ms.getX().intValue());
        if (ms.isMonsterSkill()) {
            oPacket.EncodeShort(ms.getMobSkill().getSkillId());
            oPacket.EncodeShort(ms.getMobSkill().getSkillLevel());
        } else if (ms.getSkill() > 0) {
            oPacket.EncodeInteger(ms.getSkill());
        }
        oPacket.EncodeShort((short) ((ms.getCancelTask() - System.currentTimeMillis()) / 1000));

        oPacket.EncodeLong(0L); // I assume this is for encodetemporary but lol what a meme
        oPacket.EncodeShort(0); // tDelay
        oPacket.Encode(1); // nCalcDamageStatIndex
        /*
        if (MobStat.IsMovementAffectingStat(uFlag)) {
            oPacket.Encode(0);
        }
        */

        return oPacket.ToPacket();
    }

    public static Packet applyMonsterStatus(MapleMonster mons, List<MonsterStatusEffect> mse) { // WHY MY VERSION forces me into adding this funtion "EncodeTemporary" into the spawnmonster/controlmonstrer packets.. It handles "Monster Buffs" there hmmm
        if ((mse.size() <= 0) || (mse.get(0) == null)) {
            return CWvsContext.enableActions();
        }
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.MobStatSet.getValue());
        oPacket.EncodeInteger(mons.getObjectId());
        MonsterStatusEffect ms = (MonsterStatusEffect) mse.get(0);
        if (ms.getStati() == MonsterStatus.POISON) {
            PacketHelper.writeSingleMask(oPacket, MonsterStatus.EMPTY);
            oPacket.Encode(mse.size());
            for (MonsterStatusEffect m : mse) {
                oPacket.EncodeInteger(m.getFromID());
                if (m.isMonsterSkill()) {
                    oPacket.EncodeShort(m.getMobSkill().getSkillId());
                    oPacket.EncodeShort(m.getMobSkill().getSkillLevel());
                } else if (m.getSkill() > 0) {
                    oPacket.EncodeInteger(m.getSkill());
                }
                oPacket.EncodeInteger(m.getX().intValue());
                oPacket.EncodeInteger(1000);
                oPacket.EncodeInteger(0);
                oPacket.EncodeInteger(8000);//new v141
                oPacket.EncodeInteger(6);
                oPacket.EncodeInteger(0);
            }
            oPacket.EncodeShort(1000);//was 300
            oPacket.Encode(2);//was 1
            //oPacket.encode(1);
        } else {
            PacketHelper.writeSingleMask(oPacket, ms.getStati());

            oPacket.EncodeInteger(ms.getX().intValue());
            if (ms.isMonsterSkill()) {
                oPacket.EncodeShort(ms.getMobSkill().getSkillId());
                oPacket.EncodeShort(ms.getMobSkill().getSkillLevel());
            } else if (ms.getSkill() > 0) {
                oPacket.EncodeInteger(ms.getSkill());
            }
            oPacket.EncodeShort((short) ((ms.getCancelTask() - System.currentTimeMillis()) / 500));
            oPacket.EncodeLong(0L);
            oPacket.EncodeShort(0);
            oPacket.Encode(1);
        }
//System.out.println("Monsterstatus3");
        return oPacket.ToPacket();
    }

    public static Packet applyMonsterStatus(int oid, Map<MonsterStatus, Integer> stati, List<Integer> reflection, MobSkill skil) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.MobStatSet.getValue());
        oPacket.EncodeInteger(oid);
        PacketHelper.writeMask(oPacket, stati.keySet());

        for (Entry<MonsterStatus, Integer> mse : stati.entrySet()) {
            oPacket.EncodeInteger(mse.getValue().intValue());
            oPacket.EncodeInteger(skil.getSkillId());
            oPacket.EncodeShort((short) skil.getDuration() / 500);
        }

        for (Integer ref : reflection) {
            oPacket.EncodeInteger(ref.intValue());
        }
        oPacket.EncodeInteger(0);
        oPacket.EncodeInteger(0);
        oPacket.EncodeShort(0);//effectDelay 

        int size = stati.size();
        if (reflection.size() > 0) {
            size /= 2;
        }
        oPacket.Encode(size);
        return oPacket.ToPacket();
    }

    public static Packet applyPoison(MapleMonster mons, List<MonsterStatusEffect> mse) {
        if ((mse.size() <= 0) || (mse.get(0) == null)) {
            return CWvsContext.enableActions();
        }
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.MobStatSet.getValue());
        oPacket.EncodeInteger(mons.getObjectId());
        PacketHelper.writeSingleMask(oPacket, MonsterStatus.EMPTY);
        oPacket.Encode(mse.size());
        for (MonsterStatusEffect m : mse) {
            oPacket.EncodeInteger(m.getFromID());
            if (m.isMonsterSkill()) {
                oPacket.EncodeShort(m.getMobSkill().getSkillId());
                oPacket.EncodeShort(m.getMobSkill().getSkillLevel());
            } else if (m.getSkill() > 0) {
                oPacket.EncodeInteger(m.getSkill());
            }
            oPacket.EncodeInteger(m.getX().intValue());
            oPacket.EncodeInteger(1000);
            oPacket.EncodeInteger(0);//600574518?
            oPacket.EncodeInteger(8000);//war 7000
            oPacket.EncodeInteger(6);//was 5
            oPacket.EncodeInteger(0);
        }
        oPacket.EncodeShort(1000);//was 300
        oPacket.Encode(2);//was 1
        //oPacket.encode(1);
//System.out.println("Monsterstatus5");
        return oPacket.ToPacket();
    }

    public static Packet cancelMonsterStatus(int oid, MonsterStatus stat) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.MobStatReset.getValue());
        oPacket.EncodeInteger(oid);
        PacketHelper.writeSingleMask(oPacket, stat);
        oPacket.Encode(5);
        oPacket.Fill(0, 5);  // v145+
        oPacket.Encode(2);
        oPacket.Fill(0, 30); // v145+

        return oPacket.ToPacket();
    }

    public static Packet cancelPoison(int oid, MonsterStatusEffect m) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.MobStatReset.getValue());
        oPacket.EncodeInteger(oid);
        PacketHelper.writeSingleMask(oPacket, MonsterStatus.EMPTY);
        oPacket.EncodeInteger(0);
        oPacket.EncodeInteger(1);
        oPacket.EncodeInteger(m.getFromID());
        if (m.isMonsterSkill()) {
            oPacket.EncodeShort(m.getMobSkill().getSkillId());
            oPacket.EncodeShort(m.getMobSkill().getSkillLevel());
        } else if (m.getSkill() > 0) {
            oPacket.EncodeInteger(m.getSkill());
        }
        oPacket.Encode(3);

        return oPacket.ToPacket();
    }

    public static Packet talkMonster(int oid, int itemId, String msg) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.MobSpeaking.getValue());
        oPacket.EncodeInteger(oid);
        oPacket.EncodeInteger(500);
        oPacket.EncodeInteger(itemId);
        oPacket.Encode(itemId <= 0 ? 0 : 1);
        oPacket.Encode((msg == null) || (msg.length() <= 0) ? 0 : 1);
        if ((msg != null) && (msg.length() > 0)) {
            oPacket.EncodeString(msg);
        }
        oPacket.EncodeInteger(1);

        return oPacket.ToPacket();
    }

    public static Packet removeTalkMonster(int oid) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.MobMessaging.getValue());
        oPacket.EncodeInteger(oid);

        return oPacket.ToPacket();
    }

    public static final Packet getNodeProperties(MapleMonster objectid, MapleMap map) {
        if (objectid.getNodePacket() != null) {
            return objectid.getNodePacket();
        }
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.MobMessaging.getValue());
        oPacket.EncodeInteger(objectid.getObjectId());
        oPacket.EncodeInteger(map.getSharedMapResources().getNodes().size());
        oPacket.EncodeInteger(objectid.getPosition().x);
        oPacket.EncodeInteger(objectid.getPosition().y);
        for (MapleNodeInfo mni : map.getSharedMapResources().getNodes()) {
            oPacket.EncodeInteger(mni.x);
            oPacket.EncodeInteger(mni.y);
            oPacket.EncodeInteger(mni.attr);
            if (mni.attr == 2) {
                oPacket.EncodeInteger(500);
            }
        }
        oPacket.EncodeInteger(0);
        oPacket.Encode(0);
        oPacket.Encode(0);

        objectid.setNodePacket(oPacket.ToPacket());
        return objectid.getNodePacket();
    }

    public static Packet showMagnet(int mobid, boolean success) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.MobFlyTarget.getValue());
        oPacket.EncodeInteger(mobid);
        oPacket.Encode(success ? 1 : 0);
        oPacket.Encode(0);

        return oPacket.ToPacket();
    }

    public static Packet catchMonster(int mobid, int itemid, byte success) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.MobCatchEffect.getValue());
        oPacket.EncodeInteger(mobid);
        oPacket.EncodeInteger(itemid);
        oPacket.Encode(success);

        return oPacket.ToPacket();
    }
}
