/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.life.mob;

import client.Skill;
import client.SkillFactory;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ScheduledFuture;
import java.util.stream.Collectors;

import net.OutPacket;
import scripting.EventManager;
import server.MapleStatEffect;
import server.life.Mob;
import static server.MapleStatInfo.dot;
import static server.MapleStatInfo.dotInterval;
import static server.MapleStatInfo.dotSuperpos;
import static server.MapleStatInfo.dotTime;
import static server.life.mob.MobStat.*;
import static server.life.mob.MobStat.BurnedInfo;
import server.skills.Option;
import tools.Utility;
import tools.packet.MobPacket;

/**
 *
 * @author Sjonnie
 */
public class MobTemporaryStat {

    private List<BurnedInfo> burnedInfos = new ArrayList<>();
    private Map<Integer, ScheduledFuture> burnCancelSchedules = new HashMap<>();
    private Map<Integer, ScheduledFuture> burnSchedules = new HashMap<>();
    private String linkTeam;
    private Comparator mobStatComper = (o1, o2) -> {
        MobStat k1 = (MobStat) o1;
        MobStat k2 = (MobStat) o2;
        int res = 0;
        if (k1.getPosition() < k2.getPosition()) {
            res = -1;
        } else if (k1.getPosition() > k2.getPosition()) {
            res = 1;
        } else {
            if (k1.getVal() < k2.getVal()) {
                res = -1;
            } else if (k1.getVal() > k2.getVal()) {
                res = 1;
            }
        }
        return res;
    };
    private TreeMap<MobStat, Option> currentStatVals = new TreeMap<>(mobStatComper);
    private TreeMap<MobStat, Option> newStatVals = new TreeMap<>(mobStatComper);
    private TreeMap<MobStat, Option> removedStatVals = new TreeMap<>(mobStatComper);
    private Map<MobStat, ScheduledFuture> schedules = new HashMap<>();
    private Mob mob;

    public MobTemporaryStat(Mob mob) {
        this.mob = mob;
    }

    public MobTemporaryStat deepCopy() {
        MobTemporaryStat copy = new MobTemporaryStat(getMob());
        copy.setBurnedInfos(new ArrayList<>());
        for (BurnedInfo bi : getBurnedInfos()) {
            copy.getBurnedInfos().add(bi.deepCopy());
        }
        copy.setLinkTeam(getLinkTeam());
        copy.mobStatComper = getMobStatComper();
        for (MobStat ms : getCurrentStatVals().keySet()) {
            copy.addStatOptions(ms, getCurrentStatVals().get(ms).deepCopy());
        }
        return copy;
    }

    public Option getNewOptionsByMobStat(MobStat mobStat) {
        return getNewStatVals().getOrDefault(mobStat, null);
    }

    public Option getCurrentOptionsByMobStat(MobStat mobStat) {
        return getCurrentStatVals().getOrDefault(mobStat, null);
    }

    public Option getRemovedOptionsByMobStat(MobStat mobStat) {
        return getRemovedStatVals().getOrDefault(mobStat, null);
    }

    public void Encode(OutPacket oPacket) {
        synchronized (currentStatVals) {
            // DecodeBuffer(12) + MobStat::DecodeTemporary
            int[] mask = getNewMask();
            for (int i = 0; i < mask.length; i++) {
                oPacket.EncodeInt(mask[i]);
            }

            for (Map.Entry<MobStat, Option> entry : getNewStatVals().entrySet()) {
                MobStat mobStat = entry.getKey();
                Option option = entry.getValue();
                switch (mobStat) {
                    case PAD:
                    case PDR:
                    case MAD:
                    case MDR:
                    case ACC:
                    case EVA:
                    case Speed:
                    case Stun:
                    case Freeze:
                    case Poison:
                    case Seal:
                    case Darkness:
                    case PowerUp:
                    case MagicUp:
                    case PGuardUp:
                    case MGuardUp:
                    case PImmune:
                    case MImmune:
                    case Web:
                    case HardSkin:
                    case Ambush:
                    case Venom:
                    case Blind:
                    case SealSkill:
                    case Dazzle:
                    case PCounter:
                    case MCounter:
                    case RiseByToss:
                    case BodyPressure:
                    case Weakness:
                    case Showdown:
                    case MagicCrash:
                    case DamagedElemAttr:
                    case Dark:
                    case Mystery:
                    case AddDamParty:
                    case HitCriDamR:
                    case Fatality:
                    case Lifting:
                    case DeadlyCharge:
                    case Smite:
                    case AddDamSkill:
                    case Incizing:
                    case DodgeBodyAttack:
                    case DebuffHealing:
                    case AddDamSkill2:
                    case BodyAttack:
                    case TempMoveAbility:
                    case FixDamRBuff:
                    case ElementDarkness:
                    case AreaInstallByHit:
                    case BMageDebuff:
                    case JaguarProvoke:
                    case JaguarBleeding:
                    case DarkLightning:
                    case PinkBeanFlowerPot:
                    case BattlePvPHelenaMark:
                    case PsychicLock:
                    case PsychicLockCoolTime:
                    case PsychicGroundMark:
                    case PowerImmune:
                    case PsychicForce:
                    case MultiPMDR:
                    case ElementResetBySummon:
                    case BahamutLightElemAddDam:
                    case BossPropPlus:
                    case MultiDamSkill:
                    case RWLiftPress:
                    case RWChoppingHammer:
                    case TimeBomb:
                    case Treasure:
                    case AddEffect:
                    case Invincible:
                    case Explosion:
                    case HangOver:
                        oPacket.EncodeInt(getNewOptionsByMobStat(mobStat).nOption);
                        oPacket.EncodeInt(getNewOptionsByMobStat(mobStat).rOption);
                        oPacket.EncodeShort(getNewOptionsByMobStat(mobStat).tOption / 500);
                }
            }
            if (hasNewMobStat(PDR)) {
                oPacket.EncodeInt(getNewOptionsByMobStat(PDR).cOption);
            }
            if (hasNewMobStat(MDR)) {
                oPacket.EncodeInt(getNewOptionsByMobStat(MDR).cOption);
            }
            if (hasNewMobStat(PCounter)) {
                oPacket.EncodeInt(getNewOptionsByMobStat(PCounter).wOption);
            }
            if (hasNewMobStat(MCounter)) {
                oPacket.EncodeInt(getNewOptionsByMobStat(MCounter).wOption);
            }
            if (hasNewMobStat(PCounter)) {
                oPacket.EncodeInt(getNewOptionsByMobStat(PCounter).mOption); // nCounterProb
                oPacket.EncodeInt(getNewOptionsByMobStat(PCounter).bOption); // bCounterDelay
                oPacket.EncodeInt(getNewOptionsByMobStat(PCounter).nReason); // nAggroRank
            } else if (hasNewMobStat(MCounter)) {
                oPacket.EncodeInt(getNewOptionsByMobStat(MCounter).mOption); // nCounterProb
                oPacket.EncodeInt(getNewOptionsByMobStat(MCounter).bOption); // bCounterDelay
                oPacket.EncodeInt(getNewOptionsByMobStat(MCounter).nReason); // nAggroRank
            }
            if (hasNewMobStat(Fatality)) {
                oPacket.EncodeInt(getNewOptionsByMobStat(Fatality).wOption);
                oPacket.EncodeInt(getNewOptionsByMobStat(Fatality).uOption);
                oPacket.EncodeInt(getNewOptionsByMobStat(Fatality).pOption);
                oPacket.EncodeInt(getNewOptionsByMobStat(Fatality).yOption);
                oPacket.EncodeInt(getNewOptionsByMobStat(Fatality).mOption);
            }
            if (hasNewMobStat(Explosion)) {
                oPacket.EncodeInt(getNewOptionsByMobStat(Explosion).wOption);
            }
            if (hasNewMobStat(ExtraBuffStat)) {
                List<Option> values = getNewOptionsByMobStat(ExtraBuffStat).extraOpts;
                oPacket.EncodeBool(values.size() > 0);
                if (values.size() > 0) {
                    oPacket.EncodeInt(getNewOptionsByMobStat(ExtraBuffStat).extraOpts.get(0).nOption); // nPAD
                    oPacket.EncodeInt(getNewOptionsByMobStat(ExtraBuffStat).extraOpts.get(0).mOption); // nMAD
                    oPacket.EncodeInt(getNewOptionsByMobStat(ExtraBuffStat).extraOpts.get(0).xOption); // nPDR
                    oPacket.EncodeInt(getNewOptionsByMobStat(ExtraBuffStat).extraOpts.get(0).yOption); // nMDR
                }
            }
            if (hasNewMobStat(DeadlyCharge)) {
                oPacket.EncodeInt(getNewOptionsByMobStat(DeadlyCharge).pOption);
                oPacket.EncodeInt(getNewOptionsByMobStat(DeadlyCharge).pOption);
            }
            if (hasNewMobStat(Incizing)) {
                oPacket.EncodeInt(getNewOptionsByMobStat(Incizing).wOption);
                oPacket.EncodeInt(getNewOptionsByMobStat(Incizing).uOption);
                oPacket.EncodeInt(getNewOptionsByMobStat(Incizing).pOption);
            }
            if (hasNewMobStat(Speed)) {
                oPacket.EncodeByte(getNewOptionsByMobStat(Speed).mOption);
            }
            if (hasNewMobStat(BMageDebuff)) {
                oPacket.EncodeInt(getNewOptionsByMobStat(BMageDebuff).cOption);
            }
            if (hasNewMobStat(DarkLightning)) {
                oPacket.EncodeInt(getNewOptionsByMobStat(DarkLightning).cOption);
            }
            if (hasNewMobStat(BattlePvPHelenaMark)) {
                oPacket.EncodeInt(getNewOptionsByMobStat(BattlePvPHelenaMark).cOption);
            }
            if (hasNewMobStat(MultiPMDR)) {
                oPacket.EncodeInt(getNewOptionsByMobStat(MultiPMDR).cOption);
            }
            if (hasNewMobStat(Freeze)) {
                oPacket.EncodeInt(getNewOptionsByMobStat(Freeze).cOption);
            }
            if (hasNewMobStat(BurnedInfo)) {
                oPacket.EncodeByte(getBurnedInfos().size());
                for (BurnedInfo bi : getBurnedInfos()) {
                    bi.encode(oPacket);
                }
            }
            if (hasNewMobStat(InvincibleBalog)) {
                oPacket.EncodeByte(getNewOptionsByMobStat(InvincibleBalog).nOption);
                oPacket.EncodeByte(getNewOptionsByMobStat(InvincibleBalog).bOption);
            }
            if (hasNewMobStat(ExchangeAttack)) {
                oPacket.EncodeByte(getNewOptionsByMobStat(ExchangeAttack).bOption);
            }
            if (hasNewMobStat(AddDamParty)) {
                oPacket.EncodeInt(getNewOptionsByMobStat(AddDamParty).wOption);
                oPacket.EncodeInt(getNewOptionsByMobStat(AddDamParty).pOption);
                oPacket.EncodeInt(getNewOptionsByMobStat(AddDamParty).cOption);
            }
            if (hasNewMobStat(LinkTeam)) {
                oPacket.EncodeString(getLinkTeam());
            }
            if (hasNewMobStat(SoulExplosion)) {
                oPacket.EncodeInt(getNewOptionsByMobStat(SoulExplosion).nOption);
                oPacket.EncodeInt(getNewOptionsByMobStat(SoulExplosion).rOption);
                oPacket.EncodeInt(getNewOptionsByMobStat(SoulExplosion).wOption);
            }
            if (hasNewMobStat(SeperateSoulP)) {
                oPacket.EncodeInt(getNewOptionsByMobStat(SeperateSoulP).nOption);
                oPacket.EncodeInt(getNewOptionsByMobStat(SeperateSoulP).rOption);
                oPacket.EncodeShort(getNewOptionsByMobStat(SeperateSoulP).tOption / 500);
                oPacket.EncodeInt(getNewOptionsByMobStat(SeperateSoulP).wOption);
                oPacket.EncodeInt(getNewOptionsByMobStat(SeperateSoulP).uOption);
            }
            if (hasNewMobStat(SeperateSoulC)) {
                oPacket.EncodeInt(getNewOptionsByMobStat(SeperateSoulC).nOption);
                oPacket.EncodeInt(getNewOptionsByMobStat(SeperateSoulC).rOption);
                oPacket.EncodeShort(getNewOptionsByMobStat(SeperateSoulC).tOption / 500);
                oPacket.EncodeInt(getNewOptionsByMobStat(SeperateSoulC).wOption);
            }
            if (hasNewMobStat(Ember)) {
                oPacket.EncodeInt(getNewOptionsByMobStat(Ember).nOption);
                oPacket.EncodeInt(getNewOptionsByMobStat(Ember).rOption);
                oPacket.EncodeInt(getNewOptionsByMobStat(Ember).wOption);
                oPacket.EncodeInt(getNewOptionsByMobStat(Ember).tOption / 500);
                oPacket.EncodeInt(getNewOptionsByMobStat(Ember).uOption);
            }
            if (hasNewMobStat(TrueSight)) {
                oPacket.EncodeInt(getNewOptionsByMobStat(TrueSight).nOption);
                oPacket.EncodeInt(getNewOptionsByMobStat(TrueSight).rOption);
                oPacket.EncodeInt(getNewOptionsByMobStat(TrueSight).tOption / 500);
                oPacket.EncodeInt(getNewOptionsByMobStat(TrueSight).cOption);
                oPacket.EncodeInt(getNewOptionsByMobStat(TrueSight).pOption);
                oPacket.EncodeInt(getNewOptionsByMobStat(TrueSight).uOption);
                oPacket.EncodeInt(getNewOptionsByMobStat(TrueSight).wOption);
            }
            if (hasNewMobStat(MultiDamSkill)) {
                oPacket.EncodeInt(getNewOptionsByMobStat(MultiDamSkill).cOption);
            }
            if (hasNewMobStat(Laser)) {
                oPacket.EncodeInt(getNewOptionsByMobStat(Laser).nOption);
                oPacket.EncodeInt(getNewOptionsByMobStat(Laser).rOption);
                oPacket.EncodeInt(getNewOptionsByMobStat(Laser).tOption / 500);
                oPacket.EncodeInt(getNewOptionsByMobStat(Laser).wOption);
                oPacket.EncodeInt(getNewOptionsByMobStat(Laser).uOption);
            }
            if (hasNewMobStat(ElementResetBySummon)) {
                oPacket.EncodeInt(getNewOptionsByMobStat(ElementResetBySummon).cOption);
                oPacket.EncodeInt(getNewOptionsByMobStat(ElementResetBySummon).pOption);
                oPacket.EncodeInt(getNewOptionsByMobStat(ElementResetBySummon).uOption);
                oPacket.EncodeInt(getNewOptionsByMobStat(ElementResetBySummon).wOption);
            }
            if (hasNewMobStat(BahamutLightElemAddDam)) {
                oPacket.EncodeInt(getNewOptionsByMobStat(BahamutLightElemAddDam).pOption);
                oPacket.EncodeInt(getNewOptionsByMobStat(BahamutLightElemAddDam).cOption);
            }
            getNewStatVals().clear();
        }
    }

    private int[] getMaskByCollection(Map<MobStat, Option> map) {
        int[] res = new int[3];
        for (MobStat mobStat : map.keySet()) {
            res[mobStat.getPosition()] |= mobStat.getVal();
        }
        return res;
    }

    public int[] getNewMask() {
        return getMaskByCollection(getNewStatVals());
    }

    public int[] getCurrentMask() {
        return getMaskByCollection(getCurrentStatVals());
    }

    public int[] getRemovedMask() {
        return getMaskByCollection(getRemovedStatVals());
    }

    public boolean hasNewMobStat(MobStat mobStat) {
        return getNewStatVals().keySet().contains(mobStat);
    }

    public boolean hasCurrentMobStat(MobStat mobStat) {
        return getCurrentStatVals().keySet().contains(mobStat);
    }

    public boolean hasBurnFromSkill(int skillID) {
        return getBurnBySkill(skillID) != null;
    }

    public BurnedInfo getBurnBySkill(int skillID) {
        BurnedInfo res = null;
        for (BurnedInfo bi : getBurnedInfos()) {
            if (bi.getSkillId() == skillID) {
                res = bi;
            }
        }
        return res; // wow no lambda for once
    }

    public boolean hasRemovedMobStat(MobStat mobStat) {
        return getRemovedStatVals().keySet().contains(mobStat);
    }

    public Map<MobStat, Option> getCurrentStatVals() {
        return currentStatVals;
    }

    public TreeMap<MobStat, Option> getNewStatVals() {
        return newStatVals;
    }

    public TreeMap<MobStat, Option> getRemovedStatVals() {
        return removedStatVals;
    }

    public void removeMobStat(MobStat mobStat, Boolean fromSchedule) {
        synchronized (currentStatVals) {
            getRemovedStatVals().put(mobStat, getCurrentStatVals().get(mobStat));
            getCurrentStatVals().remove(mobStat);
            getMob().getMap().broadcastMessage(MobPacket.mobStatReset(getMob(), (byte) 1, false));
            getSchedules().remove(mobStat);
            if (!fromSchedule && getSchedules().containsKey(mobStat)) {
                getSchedules().get(mobStat).cancel(true);
                getSchedules().remove(mobStat);
            } else {
                getSchedules().remove(mobStat);
            }
        }
    }

    public void removeBurnedInfo(Integer charId, Boolean fromSchedule) {
        synchronized (burnedInfos) {
            List<BurnedInfo> biList = getBurnedInfos().stream().filter(bi -> bi.getCharacterId() == charId).collect(Collectors.toList());
            getBurnedInfos().removeAll(biList);
            getRemovedStatVals().put(BurnedInfo, getCurrentOptionsByMobStat(BurnedInfo));
            if (getBurnedInfos().size() == 0) {
                getCurrentStatVals().remove(BurnedInfo);
            }
            getMob().getMap().broadcastMessage(MobPacket.mobStatReset(getMob(), (byte) 1, false, biList));
            if (!fromSchedule) {
                getBurnCancelSchedules().get(charId).cancel(true);
                getBurnCancelSchedules().remove(charId);
                getBurnSchedules().get(charId).cancel(true);
                getBurnSchedules().remove(charId);
            } else {
                getBurnCancelSchedules().remove(charId);
                getBurnSchedules().remove(charId);
            }
        }
    }

    /**
     * Adds a new MobStat to this MobTemporaryStat. Will immediately broadcast the reaction to all clients. Only works for user skills, not
     * mob skills. For the latter, use {@link #addMobSkillOptionsAndBroadCast(MobStat, Option)}.
     *
     * @param mobStat The MobStat to add.
     * @param option The Option that contains the values of the stat.
     */
    public void addStatOptionsAndBroadcast(MobStat mobStat, Option option) {
        addStatOptions(mobStat, option);
        mob.getMap().broadcastMessage(MobPacket.mobStatSet(getMob(), (short) 0));
    }

    /**
     * Adds a new MobStat to this MobTemporary stat. Will immediately broadcast the reaction to all clients. Only works for mob skills, not
     * user skills. For the latter, use {@link #addStatOptionsAndBroadcast(MobStat, Option)}.
     *
     * @param mobStat The MobStat to add.
     * @param o The option that contains the values of the stat.
     */
    public void addMobSkillOptionsAndBroadCast(MobStat mobStat, Option o) {
        o.rOption |= o.slv << 16; // mob skills are encoded differently: not an int, but short (skill ID), then short (slv).
        addStatOptionsAndBroadcast(mobStat, o);
    }

    public void addStatOptions(MobStat mobStat, Option option) {
        option.tTerm *= 1000;
        option.tOption *= 1000;
        int tAct = option.tOption > 0 ? option.tOption : option.tTerm;
        getNewStatVals().put(mobStat, option);
        getCurrentStatVals().put(mobStat, option);
        if (tAct > 0 && mobStat != BurnedInfo) {
            if (getSchedules().containsKey(mobStat)) {
                getSchedules().get(mobStat).cancel(true);
            }
            ScheduledFuture sf = EventManager.addEvent(() -> removeMobStat(mobStat, true), tAct);
            getSchedules().put(mobStat, sf);
        }
    }

    public List<BurnedInfo> getBurnedInfos() {
        return burnedInfos;
    }

    public void setBurnedInfos(List<BurnedInfo> burnedInfos) {
        this.burnedInfos = burnedInfos;
    }

    public Comparator getMobStatComper() {
        return mobStatComper;
    }

    public String getLinkTeam() {
        return linkTeam;
    }

    public void setLinkTeam(String linkTeam) {
        this.linkTeam = linkTeam;
    }

    public boolean hasNewMovementAffectingStat() {
        return getNewStatVals().keySet().stream().anyMatch(MobStat::isMovementAffectingStat);
    }

    public boolean hasCurrentMovementAffectingStat() {
        return getCurrentStatVals().keySet().stream().anyMatch(MobStat::isMovementAffectingStat);
    }

    public boolean hasRemovedMovementAffectingStat() {
        return getRemovedStatVals().keySet().stream().anyMatch(MobStat::isMovementAffectingStat);
    }

    public Map<MobStat, ScheduledFuture> getSchedules() {
        if (schedules == null) {
            schedules = new HashMap<>();
        }
        return schedules;
    }

    public Mob getMob() {
        return mob;
    }

    public void setMob(Mob mob) {
        this.mob = mob;
    }

    public void clear() {
        for (ScheduledFuture t : getBurnSchedules().values()) {
            t.cancel(true);
        }
        getBurnSchedules().clear();
        for (ScheduledFuture t : getBurnCancelSchedules().values()) {
            t.cancel(true);
        }
        getBurnCancelSchedules().clear();
        for (ScheduledFuture t : getSchedules().values()) {
            t.cancel(true);
        }
        getSchedules().clear();
        getCurrentStatVals().forEach((ms, o) -> removeMobStat(ms, false));
    }

    public void createAndAddBurnedInfo(int charId, Skill skill, int max) {
        BurnedInfo bu = getBurnedInfos().stream().
                filter(b -> b.getSkillId() == skill.getId() && b.getCharacterId() == charId)
                .findFirst().orElse(null);
        int slv = Utility.requestCharacter(charId).getSkillLevel(skill.getId());
        MapleStatEffect si = skill.getEffect(slv);
        BurnedInfo bi = new BurnedInfo();
        bi.setCharacterId(charId);
        bi.setSkillId(skill.getId());
        bi.setDamage(si.info.get(dot));
        bi.setInterval(si.info.get(dotInterval) * 1000);
        int time = si.info.get(dotTime) * 1000;
        bi.setEnd((int) (System.currentTimeMillis() + time));
        bi.setDotCount(10 /*time / bi.getInterval()*/); // divide by zero lol
        bi.setSuperPos(si.info.get(dotSuperpos));
        bi.setAttackDelay(0);
        bi.setDotTickIdx(0);
        bi.setDotTickDamR(si.info.get(dot));
        bi.setDotAnimation(bi.getAttackDelay() + bi.getInterval() + time);
        bi.setStartTime((int) System.currentTimeMillis());
        bi.setLastUpdate((int) System.currentTimeMillis());
        if (bu != null) {
            removeBurnedInfo(charId, false);
        }
        getBurnedInfos().add(bi);
        addStatOptionsAndBroadcast(MobStat.BurnedInfo, new Option());
        ScheduledFuture sf = EventManager.addEvent(() -> removeBurnedInfo(charId, true), time);
        ScheduledFuture burn = EventManager.addFixedRateEvent(() -> getMob().damage(Utility.requestCharacter(charId), (long) bi.getDamage(), false), 0, bi.getInterval(), bi.getDotCount());
        // ScheduledFuture burn = EventManager.addFixedRateEvent(() -> getMob().damage((long) bi.getDamage()), 0, bi.getInterval(), bi.getDotCount());
        getBurnCancelSchedules().put(charId, sf);
        getBurnSchedules().put(charId, burn);
    }

    public Map<Integer, ScheduledFuture> getBurnCancelSchedules() {
        return burnCancelSchedules;
    }

    public Map<Integer, ScheduledFuture> getBurnSchedules() {
        return burnSchedules;
    }
}
