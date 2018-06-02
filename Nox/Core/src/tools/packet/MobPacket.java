package tools.packet;

import java.awt.Rectangle;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import client.MonsterStatus;
import client.MonsterStatusEffect;
import service.SendPacketOpcode;
import net.OutPacket;

import server.life.mob.BurnedInfo;
import server.life.LifeFactory;
import server.life.Mob;
import server.life.MobSkill;
import enums.MobStat;
import server.life.mob.MobTemporaryStat;
import server.life.MultiTarget;
import server.maps.MapleMap;
import server.maps.SharedMapResources.MapleNodeInfo;
import server.movement.LifeMovementFragment;

public class MobPacket {

    public static OutPacket mobStatSet(Mob mob, short delay) {
        //(OutHeader.MOB_STAT_SET);
        OutPacket oPacket = new OutPacket(SendPacketOpcode.MobStatSet.getValue());
        MobTemporaryStat mts = mob.getTemporaryStat();
        boolean hasMovementStat = mts.hasNewMovementAffectingStat();
        oPacket.EncodeInt(mob.getObjectId());
        mts.Encode(oPacket);
        oPacket.EncodeShort(delay);
        oPacket.EncodeByte(1); // nCalcDamageStatIndex
        if (hasMovementStat) {
            oPacket.EncodeByte(0); // ?
        }

        oPacket.Fill(0, 29);

        return oPacket;
    }

    public static OutPacket mobStatReset(Mob mob, byte byteCalcDamageStatIndex, boolean sn) {
        return mobStatReset(mob, byteCalcDamageStatIndex, sn, null);
    }

    public synchronized static OutPacket mobStatReset(Mob mob, byte calcDamageStatIndex, boolean sn, List<BurnedInfo> biList) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.MobStatReset.getValue());
        MobTemporaryStat resetStats = mob.getTemporaryStat();
        int[] mask = resetStats.getRemovedMask();
        oPacket.EncodeInt(mob.getObjectId());
        for (int i = 0; i < 3; i++) {
            oPacket.EncodeInt(mask[i]);
        }
        if (resetStats.hasRemovedMobStat(MobStat.BurnedInfo)) {
            if (biList == null) {
                oPacket.EncodeInt(0);
                oPacket.EncodeInt(0);
            } else {
                int dotCount = biList.stream().mapToInt(BurnedInfo::getDotCount).sum();
                oPacket.EncodeInt(dotCount);
                oPacket.EncodeInt(biList.size());
                for (BurnedInfo bi : biList) {
                    oPacket.EncodeInt(bi.getCharacterId());
                    oPacket.EncodeInt(bi.getSuperPos());
                }
            }
            resetStats.getBurnedInfos().clear();
        }
        oPacket.EncodeByte(calcDamageStatIndex);
        if (resetStats.hasRemovedMovementAffectingStat()) {
            oPacket.EncodeBool(sn);
        }
        resetStats.getRemovedStatVals().clear();
        return oPacket;
    }

    public static OutPacket mobSpecialEffectBySkill(Mob mob, int skillID, int charId, short hit) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.MobSpecialEffectBySkill.getValue());

        oPacket.EncodeInt(mob.getObjectId());
        oPacket.EncodeInt(skillID);
        oPacket.EncodeInt(charId);
        oPacket.EncodeShort(hit);

        return oPacket;
    }

    public static OutPacket mobAffected(Mob mob, int skillID, int slv, boolean userSkill, short delay) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.MobAffected.getValue());

        oPacket.EncodeInt(mob.getObjectId());
        oPacket.EncodeInt(skillID);
        oPacket.EncodeShort(delay);
        oPacket.EncodeBool(userSkill);
        oPacket.EncodeInt(slv);

        return oPacket;
    }

    public static OutPacket damageMonster(int oid, long damage) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.MobDamaged.getValue());
        oPacket.EncodeInt(oid);
        oPacket.EncodeInt(0); //nSkillID
        oPacket.EncodeShort(0); //tDelay
        oPacket.EncodeByte(0); //bUser
        oPacket.EncodeInt((int) damage); //nSLV

        return oPacket;
    }

    public static OutPacket damageFriendlyMob(Mob mob, long damage, boolean display) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.MobDamaged.getValue());
        oPacket.EncodeInt(mob.getObjectId());
        oPacket.EncodeInt(0); //nSkillID
        oPacket.EncodeShort(0); //tDelay
        oPacket.EncodeByte(display ? 1 : 2);
        if (damage > Integer.MAX_VALUE) {
            damage = Integer.MAX_VALUE;
        }
        oPacket.EncodeInt((int) damage);
        return oPacket;
    }

    public static OutPacket killMonster(int oid, int animation, boolean azwan) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.MobLeaveField.getValue());
        boolean a = false; //idk
        boolean b = false; //idk
        if (azwan) {
            oPacket.EncodeByte(a ? 1 : 0);
            oPacket.EncodeByte(b ? 1 : 0);
        }
        oPacket.EncodeInt(oid);
        if (azwan) {
            if (a) {
                oPacket.EncodeByte(0);
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
            return oPacket;
        }
        oPacket.EncodeByte(animation);
        if (animation == 4) {
            oPacket.EncodeInt(-1);
        }

        return oPacket;
    }

    public static OutPacket suckMonster(int oid, int chr) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.MobLeaveField.getValue());
        oPacket.EncodeInt(oid);
        oPacket.EncodeByte(4);
        oPacket.EncodeInt(chr);

        return oPacket;
    }

    public static OutPacket healMonster(int oid, int heal) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.MobDamaged.getValue());
        oPacket.EncodeInt(oid);
        oPacket.EncodeInt(0); //nSkillID
        oPacket.EncodeShort(0); //tDelay
        oPacket.EncodeByte(0); //bUser
        oPacket.EncodeInt(-heal);

        return oPacket;
    }

    public static OutPacket MobToMobDamage(int oid, int dmg, int mobid, boolean azwan) {

        OutPacket oPacket;
        if (azwan) {
            oPacket = new OutPacket(SendPacketOpcode.MobAttackedByMob.getValue());
        } else {
            oPacket = new OutPacket(SendPacketOpcode.MobAttackedByMob.getValue());
        }
        oPacket.EncodeInt(oid);
        oPacket.EncodeByte(0);
        oPacket.EncodeInt(dmg);
        oPacket.EncodeInt(mobid);
        oPacket.EncodeByte(1);

        return oPacket;
    }

    public static OutPacket getMobSkillEffect(int oid, int skillid, int cid, int skilllevel) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.MobSpecialEffectBySkill.getValue());
        oPacket.EncodeInt(oid);
        oPacket.EncodeInt(skillid);
        oPacket.EncodeInt(cid);
        oPacket.EncodeShort(skilllevel);

        return oPacket;
    }

    public static OutPacket getMobCoolEffect(int oid, int itemid) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.MobEffectByItem.getValue());
        oPacket.EncodeInt(oid);
        oPacket.EncodeInt(itemid);

        return oPacket;
    }

    public static OutPacket showMonsterHP(int oid, int remhppercentage) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.MobHPIndicator.getValue());
        oPacket.EncodeInt(oid);
        oPacket.EncodeByte(remhppercentage);

        return oPacket;
    }

    public static OutPacket showCygnusAttack(int oid) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.MobForcedAction.getValue());
        oPacket.EncodeInt(oid);

        return oPacket;
    }

    public static OutPacket showMonsterResist(int oid) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.MobAttackBlock.getValue());
        oPacket.EncodeInt(oid);
        oPacket.EncodeInt(0);
        oPacket.EncodeShort(1);
        oPacket.EncodeInt(0);

        return oPacket;
    }

    public static OutPacket showBossHP(Mob mob) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.FieldEffect.getValue());
        oPacket.EncodeByte(6);
        oPacket.EncodeInt(mob.getId() == 9400589 ? 9300184 : mob.getId());
        oPacket.EncodeLong(mob.getHp()); // Version 180 
        oPacket.EncodeLong(mob.getMobMaxHp()); // Version 180 
        oPacket.EncodeByte(mob.getStats().getTagColor());
        oPacket.EncodeByte(mob.getStats().getTagBgColor());
        return oPacket;
    }

    public static OutPacket showBossHP(int monsterId, long currentHp, long maxHp) {
        Mob mob = LifeFactory.getMonster(monsterId);

        OutPacket oPacket = new OutPacket(SendPacketOpcode.FieldEffect.getValue());
        oPacket.EncodeByte(6);
        oPacket.EncodeInt(monsterId);
        oPacket.EncodeLong(currentHp <= 0 ? -1 : currentHp); // Version 180 
        oPacket.EncodeLong(maxHp); // Version 180 
        oPacket.EncodeByte(mob.getStats().getTagColor());
        oPacket.EncodeByte(mob.getStats().getTagBgColor());
        //oPacket.Encode(6);
        //oPacket.Encode(5);

        /*if (currentHp > Integer.MAX_VALUE) {
            oPacket.EncodeInt((int) (currentHp / maxHp * Integer.MAX_VALUE));
        } else {
            oPacket.EncodeInt((int) (currentHp <= 0 ? -1 : currentHp));
        }
        if (maxHp > Integer.MAX_VALUE) {
            oPacket.EncodeInt(Integer.MAX_VALUE);
        } else {
            oPacket.EncodeInt((int) maxHp);
        }
        oPacket.Encode(6);
        oPacket.Encode(5);*/
        return oPacket;
    }

    public static OutPacket moveMonster(Mob monster, boolean useskill, int teleportEnd, int skillId, int skillLv, short option, int oid, List<LifeMovementFragment> moves, List<MultiTarget> multiTarget, List<Short> randomTime) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.MobMove.getValue());
        oPacket.EncodeInt(oid);
        oPacket.EncodeBool(useskill);
        oPacket.EncodeByte(teleportEnd);
        oPacket.EncodeByte(skillId);
        oPacket.EncodeByte(skillLv);
        oPacket.EncodeShort(option);
        oPacket.EncodeByte(multiTarget.size()); //m_aMultiTargetForBall
        for (MultiTarget multi : multiTarget) {
            oPacket.EncodePosition(multi.getPosition());
        }
        oPacket.EncodeByte(randomTime.size()); //m_aRandTimeforAreaAttack
        for (Short time : randomTime) {
            oPacket.EncodeShort(time);
        }
        PacketHelper.serializeMovementList(oPacket, monster, moves, 0);
        oPacket.EncodeByte(0);
        return oPacket;
    }

    public static OutPacket MobSkillDelay(int objectId, int skillID, int skillLv, int skillAfter, short sequenceDelay, List<Rectangle> skillRectInfo) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.MobSkillDelay.getValue());
        oPacket.EncodeInt(objectId);
        oPacket.EncodeInt(skillAfter);
        oPacket.EncodeInt(skillID);
        oPacket.EncodeInt(skillLv);
        oPacket.EncodeInt(sequenceDelay);
        oPacket.EncodeInt(skillRectInfo.size());
        for (Rectangle rect : skillRectInfo) {
            oPacket.EncodeInt(rect.x);
            oPacket.EncodeInt(rect.y);
            oPacket.EncodeInt(rect.x + rect.width);
            oPacket.EncodeInt(rect.y + rect.height);
        }
        return oPacket;
    }

    public static OutPacket spawnMonster(Mob life, int spawnType, int link, boolean azwan) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.MobEnterField.getValue());

        oPacket.EncodeByte(0);//bSealedInsteadDead
        oPacket.EncodeInt(life.getObjectId());
        oPacket.EncodeByte(1); //nCalcDamageIndex
        oPacket.EncodeInt(life.getId());
        SetMobStat(oPacket, life);
        MobInit(oPacket, life, spawnType, link, true, false);

        return oPacket;
    }

    public static void SetMobStat(OutPacket oPacket, Mob life) {

        // ForcedMobStat::Decode
        oPacket.EncodeBool(life.getChangedStats() != null);
        if (life.getChangedStats() != null) {
            oPacket.EncodeInt(life.getChangedStats().getHp() > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) life.getChangedStats().getHp());
            oPacket.EncodeInt((int) life.getChangedStats().getMp());
            oPacket.EncodeInt(life.getChangedStats().getExp());
            oPacket.EncodeInt(life.getChangedStats().getWatk());
            oPacket.EncodeInt(life.getChangedStats().getMatk());
            oPacket.EncodeInt(life.getChangedStats().getPDRate());
            oPacket.EncodeInt(life.getChangedStats().getMDRate());
            oPacket.EncodeInt(life.getChangedStats().getAcc());
            oPacket.EncodeInt(life.getChangedStats().getEva());
            oPacket.EncodeInt(life.getChangedStats().getPushed());
            oPacket.EncodeInt(life.getChangedStats().getSpeed());
            oPacket.EncodeInt(life.getChangedStats().getLevel());
            oPacket.EncodeInt(life.getChangedStats().getnUserCount());
        }

        // CMob::SetTemporaryStat
        life.getTemporaryStat().Encode(oPacket);
    }

    public static void MobInit(OutPacket oPacket, Mob life, int spawnType, int link, boolean summon, boolean newSpawn) {
        oPacket.EncodePosition(life.getTruePosition());
        oPacket.EncodeByte(life.getStance());
        if (life.getId() == 8910000 || life.getId() == 8910100) { // Von Bon
            oPacket.EncodeBool(false);//If true: random action
        }
        oPacket.EncodeShort(life.getFh()); // pfhCur
        oPacket.EncodeShort(life.getFh()); // nHomeFoothold

        oPacket.EncodeShort(spawnType); // nAppearType
        if (spawnType == -3 || spawnType >= 0) {
            oPacket.EncodeInt(link);
        }
        /*if (summon) {
            oPacket.EncodeShort(spawnType); // nAppearType
            if (spawnType == -3 || spawnType >= 0) {
                oPacket.EncodeInt(link);
            }
        } else {
            oPacket.Encode(newSpawn ? -2 : life.isFake() ? -4 : -1);
        }*/
        oPacket.EncodeByte(life.getCarnivalTeam()); // nTeamForMCarnival
        oPacket.EncodeLong(life.getHp());
        oPacket.EncodeInt(0); // nEffectItemID
        oPacket.EncodeInt(0); // m_nPhase
        oPacket.EncodeInt(0); // m_nCurZoneDataType
        oPacket.EncodeInt(0); // m_dwRefImgMobID
        oPacket.EncodeInt(0); // ?
        oPacket.EncodeByte(0); // ?
        oPacket.EncodeInt(-1); // nAfterAttack
        oPacket.EncodeInt(-1); // nCurrentAction
        oPacket.EncodeBool(life.isFacingLeft()); // bIsLeft
        oPacket.EncodeInt(0);
        oPacket.EncodeInt(0x64); // m_nScale
        oPacket.EncodeInt(-1); // m_nEliteGrade
        oPacket.EncodeByte(0);
        oPacket.EncodeByte(0);
        oPacket.EncodeInt(0); // New v176
        oPacket.EncodeInt(0); // New v176
        oPacket.EncodeInt(0);
    }

    public static OutPacket controlMonster(Mob life, boolean newSpawn, boolean aggro, boolean azwan) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.MobChangeController.getValue());
        oPacket.EncodeByte(aggro ? 2 : 1); // 0 = not moving at all, 1 = only attack when nearby, 2 = chasing + attack
        oPacket.EncodeInt(life.getObjectId());
        oPacket.EncodeByte(1);// 1 = Control normal, 5 = Control none?
        oPacket.EncodeInt(life.getId());
        SetMobStat(oPacket, life);
        oPacket.Fill(0, 149);

        return oPacket;
    }

    public static OutPacket stopControllingMonster(Mob life, boolean azwan) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.MobChangeController.getValue());
        oPacket.EncodeByte(0);

        oPacket.EncodeInt(life.getObjectId());
        oPacket.EncodeByte(0);

        if (azwan) {
            oPacket.EncodeByte(0);
            oPacket.EncodeInt(0);
            oPacket.EncodeByte(0);
            SetMobStat(oPacket, life);
            MobInit(oPacket, life, 0, 0, false, false);
        }

        oPacket.Fill(0, 69);
        return oPacket;
    }

    public static OutPacket makeMonsterReal(Mob life, boolean azwan) {
        return spawnMonster(life, -1, 0, azwan);
    }

    public static OutPacket makeMonsterFake(Mob life, boolean azwan) {
        return spawnMonster(life, -4, 0, azwan);
    }

    public static OutPacket makeMonsterEffect(Mob life, int effect, boolean azwan) {
        return spawnMonster(life, effect, 0, azwan);
    }

    public static OutPacket moveMonsterResponse(int objectid, short moveid, int currentMp, boolean useSkills, int skillId, int skillLevel, int forcedAttack) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.MobCtrlAck.getValue());
        oPacket.EncodeInt(objectid);
        oPacket.EncodeShort(moveid);//nMobCtrlSN
        oPacket.EncodeBool(useSkills);//bNextAttackPossible
        oPacket.EncodeInt(currentMp);//m_nMP
        oPacket.EncodeInt(skillId);//m_nSkillCommand  [Changed from int8 to int32 on v176]
        oPacket.EncodeByte(skillLevel);//m_nSLV
        oPacket.EncodeInt(forcedAttack);//nForcedAttackIdx

        return oPacket;
    }

    public static OutPacket SmartMobNotice(int nMessageType, int nMobID, int nMessageOpt, int nKey, String sMessage) {
        OutPacket oPacket = new OutPacket(SendPacketOpcode.SmartMobNoticeMsg.getValue());
        oPacket.EncodeInt(nMessageType); //0: white (Normal), 1: yellow (Aggro), 2: blue (Warning)
        oPacket.EncodeInt(nMobID);
        oPacket.EncodeInt(nMessageOpt);//1:attack, 2:skill, 3:change controller, 5:mobzone?
        oPacket.EncodeInt(nKey); //0: skill, 1: attack
        oPacket.EncodeString(sMessage);
        return oPacket;
    }

    public static OutPacket getMonsterSkill(int objectid) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.MobForcedSkillAction.getValue());
        oPacket.EncodeInt(objectid);
        oPacket.EncodeLong(0);

        return oPacket;
    }

    public static OutPacket getMonsterTeleport(int objectid, int x, int y) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.MobTeleport.getValue());
        oPacket.EncodeInt(objectid);
        oPacket.EncodeInt(x);
        oPacket.EncodeInt(y);

        return oPacket;
    }

    public static OutPacket applyMonsterStatus(int oid, MonsterStatus mse, int x, MobSkill skil) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.MobStatSet.getValue());
        oPacket.EncodeInt(oid);
        PacketHelper.writeSingleMask(oPacket, mse);

        oPacket.EncodeInt(x);
        oPacket.EncodeShort(skil.getSkillId());
        oPacket.EncodeShort(skil.getSkillLevel());
        oPacket.EncodeShort(mse.isEmpty() ? 1 : 0);

        oPacket.EncodeShort(0);
        oPacket.EncodeByte(2);//was 1
        oPacket.Fill(0, 0x69);

        return oPacket;
    }

    public static OutPacket applyMonsterStatus(Mob mons, MonsterStatusEffect ms) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.MobStatSet.getValue());
        oPacket.EncodeInt(mons.getObjectId());
        PacketHelper.writeSingleMask(oPacket, ms.getStati());

        oPacket.EncodeInt(ms.getX().intValue());
        if (ms.isMonsterSkill()) {
            oPacket.EncodeShort(ms.getMobSkill().getSkillId());
            oPacket.EncodeShort(ms.getMobSkill().getSkillLevel());
        } else if (ms.getSkill() > 0) {
            oPacket.EncodeInt(ms.getSkill());
        }
        oPacket.EncodeShort((short) ((ms.getCancelTask() - System.currentTimeMillis()) / 1000));

        oPacket.EncodeLong(0L); // I assume this is for encodetemporary but lol what a meme
        oPacket.EncodeShort(0); // tDelay
        oPacket.EncodeByte(1); // nCalcDamageStatIndex
        /*
        if (MobStat.IsMovementAffectingStat(uFlag)) {
            oPacket.Encode(0);
        }
         */

        return oPacket;
    }

    public static OutPacket applyMonsterStatus(Mob mons, List<MonsterStatusEffect> mse) { // WHY MY VERSION forces me into adding this funtion "EncodeTemporary" into the spawnmonster/controlmonstrer packets.. It handles "Monster Buffs" there hmmm
        if ((mse.size() <= 0) || (mse.get(0) == null)) {
            return WvsContext.enableActions();
        }

        OutPacket oPacket = new OutPacket(SendPacketOpcode.MobStatSet.getValue());
        oPacket.EncodeInt(mons.getObjectId());
        MonsterStatusEffect ms = (MonsterStatusEffect) mse.get(0);
        if (ms.getStati() == MonsterStatus.POISON) {
            PacketHelper.writeSingleMask(oPacket, MonsterStatus.EMPTY);
            oPacket.EncodeByte(mse.size());
            for (MonsterStatusEffect m : mse) {
                oPacket.EncodeInt(m.getFromID());
                if (m.isMonsterSkill()) {
                    oPacket.EncodeShort(m.getMobSkill().getSkillId());
                    oPacket.EncodeShort(m.getMobSkill().getSkillLevel());
                } else if (m.getSkill() > 0) {
                    oPacket.EncodeInt(m.getSkill());
                }
                oPacket.EncodeInt(m.getX().intValue());
                oPacket.EncodeInt(1000);
                oPacket.EncodeInt(0);
                oPacket.EncodeInt(8000);//new v141
                oPacket.EncodeInt(6);
                oPacket.EncodeInt(0);
            }
            oPacket.EncodeShort(1000);//was 300
            oPacket.EncodeByte(2);//was 1
            //oPacket.encode(1);
        } else {
            PacketHelper.writeSingleMask(oPacket, ms.getStati());

            oPacket.EncodeInt(ms.getX().intValue());
            if (ms.isMonsterSkill()) {
                oPacket.EncodeShort(ms.getMobSkill().getSkillId());
                oPacket.EncodeShort(ms.getMobSkill().getSkillLevel());
            } else if (ms.getSkill() > 0) {
                oPacket.EncodeInt(ms.getSkill());
            }
            oPacket.EncodeShort((short) ((ms.getCancelTask() - System.currentTimeMillis()) / 500));
            oPacket.EncodeLong(0L);
            oPacket.EncodeShort(0);
            oPacket.EncodeByte(1);
        }
//System.out.println("Monsterstatus3");
        return oPacket;
    }

    public static OutPacket applyMonsterStatus(int oid, Map<MonsterStatus, Integer> stati, List<Integer> reflection, MobSkill skil) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.MobStatSet.getValue());
        oPacket.EncodeInt(oid);
        PacketHelper.writeMask(oPacket, stati.keySet());

        for (Entry<MonsterStatus, Integer> mse : stati.entrySet()) {
            oPacket.EncodeInt(mse.getValue().intValue());
            oPacket.EncodeInt(skil.getSkillId());
            oPacket.EncodeShort((short) skil.getDuration() / 500);
        }

        for (Integer ref : reflection) {
            oPacket.EncodeInt(ref.intValue());
        }
        oPacket.EncodeInt(0);
        oPacket.EncodeInt(0);
        oPacket.EncodeShort(0);//effectDelay 

        int size = stati.size();
        if (reflection.size() > 0) {
            size /= 2;
        }
        oPacket.EncodeByte(size);
        return oPacket;
    }

    public static OutPacket applyPoison(Mob mons, List<MonsterStatusEffect> mse) {
        if ((mse.size() <= 0) || (mse.get(0) == null)) {
            return WvsContext.enableActions();
        }

        OutPacket oPacket = new OutPacket(SendPacketOpcode.MobStatSet.getValue());
        oPacket.EncodeInt(mons.getObjectId());
        PacketHelper.writeSingleMask(oPacket, MonsterStatus.EMPTY);
        oPacket.EncodeByte(mse.size());
        for (MonsterStatusEffect m : mse) {
            oPacket.EncodeInt(m.getFromID());
            if (m.isMonsterSkill()) {
                oPacket.EncodeShort(m.getMobSkill().getSkillId());
                oPacket.EncodeShort(m.getMobSkill().getSkillLevel());
            } else if (m.getSkill() > 0) {
                oPacket.EncodeInt(m.getSkill());
            }
            oPacket.EncodeInt(m.getX().intValue());
            oPacket.EncodeInt(1000);
            oPacket.EncodeInt(0);//600574518?
            oPacket.EncodeInt(8000);//war 7000
            oPacket.EncodeInt(6);//was 5
            oPacket.EncodeInt(0);
        }
        oPacket.EncodeShort(1000);//was 300
        oPacket.EncodeByte(2);//was 1
        //oPacket.encode(1);
//System.out.println("Monsterstatus5");
        return oPacket;
    }

    public static OutPacket cancelMonsterStatus(int oid, MonsterStatus stat) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.MobStatReset.getValue());
        oPacket.EncodeInt(oid);
        PacketHelper.writeSingleMask(oPacket, stat);
        oPacket.EncodeByte(5);
        oPacket.Fill(0, 5);  // v145+
        oPacket.EncodeByte(2);
        oPacket.Fill(0, 30); // v145+

        return oPacket;
    }

    public static OutPacket cancelPoison(int oid, MonsterStatusEffect m) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.MobStatReset.getValue());
        oPacket.EncodeInt(oid);
        PacketHelper.writeSingleMask(oPacket, MonsterStatus.EMPTY);
        oPacket.EncodeInt(0);
        oPacket.EncodeInt(1);
        oPacket.EncodeInt(m.getFromID());
        if (m.isMonsterSkill()) {
            oPacket.EncodeShort(m.getMobSkill().getSkillId());
            oPacket.EncodeShort(m.getMobSkill().getSkillLevel());
        } else if (m.getSkill() > 0) {
            oPacket.EncodeInt(m.getSkill());
        }
        oPacket.EncodeByte(3);

        return oPacket;
    }

    public static OutPacket talkMonster(int oid, int itemId, String msg) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.MobSpeaking.getValue());
        oPacket.EncodeInt(oid);
        oPacket.EncodeInt(500);
        oPacket.EncodeInt(itemId);
        oPacket.EncodeByte(itemId <= 0 ? 0 : 1);
        oPacket.EncodeByte((msg == null) || (msg.length() <= 0) ? 0 : 1);
        if ((msg != null) && (msg.length() > 0)) {
            oPacket.EncodeString(msg);
        }
        oPacket.EncodeInt(1);

        return oPacket;
    }

    public static OutPacket removeTalkMonster(int oid) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.MobMessaging.getValue());
        oPacket.EncodeInt(oid);

        return oPacket;
    }

    public static final OutPacket getNodeProperties(Mob objectid, MapleMap map) {
        if (objectid.getNodePacket() != null) {
            return objectid.getNodePacket();
        }

        OutPacket oPacket = new OutPacket(SendPacketOpcode.MobMessaging.getValue());
        oPacket.EncodeInt(objectid.getObjectId());
        oPacket.EncodeInt(map.getSharedMapResources().getNodes().size());
        oPacket.EncodeInt(objectid.getPosition().x);
        oPacket.EncodeInt(objectid.getPosition().y);
        for (MapleNodeInfo mni : map.getSharedMapResources().getNodes()) {
            oPacket.EncodeInt(mni.x);
            oPacket.EncodeInt(mni.y);
            oPacket.EncodeInt(mni.attr);
            if (mni.attr == 2) {
                oPacket.EncodeInt(500);
            }
        }
        oPacket.EncodeInt(0);
        oPacket.EncodeByte(0);
        oPacket.EncodeByte(0);

        objectid.setNodePacket(oPacket);
        return objectid.getNodePacket();
    }

    public static OutPacket showMagnet(int mobid, boolean success) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.MobFlyTarget.getValue());
        oPacket.EncodeInt(mobid);
        oPacket.EncodeByte(success ? 1 : 0);
        oPacket.EncodeByte(0);

        return oPacket;
    }

    public static OutPacket catchMonster(int mobid, int itemid, byte success) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.MobCatchEffect.getValue());
        oPacket.EncodeInt(mobid);
        oPacket.EncodeInt(itemid);
        oPacket.EncodeByte(success);

        return oPacket;
    }
}
