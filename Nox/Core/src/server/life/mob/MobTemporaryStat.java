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
                oPacket.EncodeInteger(mask[i]);
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
                        oPacket.EncodeInteger(getNewOptionsByMobStat(mobStat).nOption);
                        oPacket.EncodeInteger(getNewOptionsByMobStat(mobStat).rOption);
                        oPacket.EncodeShort(getNewOptionsByMobStat(mobStat).tOption / 500);
                }
            }
            if (hasNewMobStat(PDR)) {
                oPacket.EncodeInteger(getNewOptionsByMobStat(PDR).cOption);
            }
            if (hasNewMobStat(MDR)) {
                oPacket.EncodeInteger(getNewOptionsByMobStat(MDR).cOption);
            }
            if (hasNewMobStat(PCounter)) {
                oPacket.EncodeInteger(getNewOptionsByMobStat(PCounter).wOption);
            }
            if (hasNewMobStat(MCounter)) {
                oPacket.EncodeInteger(getNewOptionsByMobStat(MCounter).wOption);
            }
            if (hasNewMobStat(PCounter)) {
                oPacket.EncodeInteger(getNewOptionsByMobStat(PCounter).mOption); // nCounterProb
                oPacket.EncodeInteger(getNewOptionsByMobStat(PCounter).bOption); // bCounterDelay
                oPacket.EncodeInteger(getNewOptionsByMobStat(PCounter).nReason); // nAggroRank
            } else if (hasNewMobStat(MCounter)) {
                oPacket.EncodeInteger(getNewOptionsByMobStat(MCounter).mOption); // nCounterProb
                oPacket.EncodeInteger(getNewOptionsByMobStat(MCounter).bOption); // bCounterDelay
                oPacket.EncodeInteger(getNewOptionsByMobStat(MCounter).nReason); // nAggroRank
            }
            if (hasNewMobStat(Fatality)) {
                oPacket.EncodeInteger(getNewOptionsByMobStat(Fatality).wOption);
                oPacket.EncodeInteger(getNewOptionsByMobStat(Fatality).uOption);
                oPacket.EncodeInteger(getNewOptionsByMobStat(Fatality).pOption);
                oPacket.EncodeInteger(getNewOptionsByMobStat(Fatality).yOption);
                oPacket.EncodeInteger(getNewOptionsByMobStat(Fatality).mOption);
            }
            if (hasNewMobStat(Explosion)) {
                oPacket.EncodeInteger(getNewOptionsByMobStat(Explosion).wOption);
            }
            if (hasNewMobStat(ExtraBuffStat)) {
                List<Option> values = getNewOptionsByMobStat(ExtraBuffStat).extraOpts;
                oPacket.Encode(values.size() > 0);
                if (values.size() > 0) {
                    oPacket.EncodeInteger(getNewOptionsByMobStat(ExtraBuffStat).extraOpts.get(0).nOption); // nPAD
                    oPacket.EncodeInteger(getNewOptionsByMobStat(ExtraBuffStat).extraOpts.get(0).mOption); // nMAD
                    oPacket.EncodeInteger(getNewOptionsByMobStat(ExtraBuffStat).extraOpts.get(0).xOption); // nPDR
                    oPacket.EncodeInteger(getNewOptionsByMobStat(ExtraBuffStat).extraOpts.get(0).yOption); // nMDR
                }
            }
            if (hasNewMobStat(DeadlyCharge)) {
                oPacket.EncodeInteger(getNewOptionsByMobStat(DeadlyCharge).pOption);
                oPacket.EncodeInteger(getNewOptionsByMobStat(DeadlyCharge).pOption);
            }
            if (hasNewMobStat(Incizing)) {
                oPacket.EncodeInteger(getNewOptionsByMobStat(Incizing).wOption);
                oPacket.EncodeInteger(getNewOptionsByMobStat(Incizing).uOption);
                oPacket.EncodeInteger(getNewOptionsByMobStat(Incizing).pOption);
            }
            if (hasNewMobStat(Speed)) {
                oPacket.Encode(getNewOptionsByMobStat(Speed).mOption);
            }
            if (hasNewMobStat(BMageDebuff)) {
                oPacket.EncodeInteger(getNewOptionsByMobStat(BMageDebuff).cOption);
            }
            if (hasNewMobStat(DarkLightning)) {
                oPacket.EncodeInteger(getNewOptionsByMobStat(DarkLightning).cOption);
            }
            if (hasNewMobStat(BattlePvPHelenaMark)) {
                oPacket.EncodeInteger(getNewOptionsByMobStat(BattlePvPHelenaMark).cOption);
            }
            if (hasNewMobStat(MultiPMDR)) {
                oPacket.EncodeInteger(getNewOptionsByMobStat(MultiPMDR).cOption);
            }
            if (hasNewMobStat(Freeze)) {
                oPacket.EncodeInteger(getNewOptionsByMobStat(Freeze).cOption);
            }
            if (hasNewMobStat(BurnedInfo)) {
                oPacket.Encode(getBurnedInfos().size());
                for (BurnedInfo bi : getBurnedInfos()) {
                    bi.encode(oPacket);
                }
            }
            if (hasNewMobStat(InvincibleBalog)) {
                oPacket.Encode(getNewOptionsByMobStat(InvincibleBalog).nOption);
                oPacket.Encode(getNewOptionsByMobStat(InvincibleBalog).bOption);
            }
            if (hasNewMobStat(ExchangeAttack)) {
                oPacket.Encode(getNewOptionsByMobStat(ExchangeAttack).bOption);
            }
            if (hasNewMobStat(AddDamParty)) {
                oPacket.EncodeInteger(getNewOptionsByMobStat(AddDamParty).wOption);
                oPacket.EncodeInteger(getNewOptionsByMobStat(AddDamParty).pOption);
                oPacket.EncodeInteger(getNewOptionsByMobStat(AddDamParty).cOption);
            }
            if (hasNewMobStat(LinkTeam)) {
                oPacket.EncodeString(getLinkTeam());
            }
            if (hasNewMobStat(SoulExplosion)) {
                oPacket.EncodeInteger(getNewOptionsByMobStat(SoulExplosion).nOption);
                oPacket.EncodeInteger(getNewOptionsByMobStat(SoulExplosion).rOption);
                oPacket.EncodeInteger(getNewOptionsByMobStat(SoulExplosion).wOption);
            }
            if (hasNewMobStat(SeperateSoulP)) {
                oPacket.EncodeInteger(getNewOptionsByMobStat(SeperateSoulP).nOption);
                oPacket.EncodeInteger(getNewOptionsByMobStat(SeperateSoulP).rOption);
                oPacket.EncodeShort(getNewOptionsByMobStat(SeperateSoulP).tOption / 500);
                oPacket.EncodeInteger(getNewOptionsByMobStat(SeperateSoulP).wOption);
                oPacket.EncodeInteger(getNewOptionsByMobStat(SeperateSoulP).uOption);
            }
            if (hasNewMobStat(SeperateSoulC)) {
                oPacket.EncodeInteger(getNewOptionsByMobStat(SeperateSoulC).nOption);
                oPacket.EncodeInteger(getNewOptionsByMobStat(SeperateSoulC).rOption);
                oPacket.EncodeShort(getNewOptionsByMobStat(SeperateSoulC).tOption / 500);
                oPacket.EncodeInteger(getNewOptionsByMobStat(SeperateSoulC).wOption);
            }
            if (hasNewMobStat(Ember)) {
                oPacket.EncodeInteger(getNewOptionsByMobStat(Ember).nOption);
                oPacket.EncodeInteger(getNewOptionsByMobStat(Ember).rOption);
                oPacket.EncodeInteger(getNewOptionsByMobStat(Ember).wOption);
                oPacket.EncodeInteger(getNewOptionsByMobStat(Ember).tOption / 500);
                oPacket.EncodeInteger(getNewOptionsByMobStat(Ember).uOption);
            }
            if (hasNewMobStat(TrueSight)) {
                oPacket.EncodeInteger(getNewOptionsByMobStat(TrueSight).nOption);
                oPacket.EncodeInteger(getNewOptionsByMobStat(TrueSight).rOption);
                oPacket.EncodeInteger(getNewOptionsByMobStat(TrueSight).tOption / 500);
                oPacket.EncodeInteger(getNewOptionsByMobStat(TrueSight).cOption);
                oPacket.EncodeInteger(getNewOptionsByMobStat(TrueSight).pOption);
                oPacket.EncodeInteger(getNewOptionsByMobStat(TrueSight).uOption);
                oPacket.EncodeInteger(getNewOptionsByMobStat(TrueSight).wOption);
            }
            if (hasNewMobStat(MultiDamSkill)) {
                oPacket.EncodeInteger(getNewOptionsByMobStat(MultiDamSkill).cOption);
            }
            if (hasNewMobStat(Laser)) {
                oPacket.EncodeInteger(getNewOptionsByMobStat(Laser).nOption);
                oPacket.EncodeInteger(getNewOptionsByMobStat(Laser).rOption);
                oPacket.EncodeInteger(getNewOptionsByMobStat(Laser).tOption / 500);
                oPacket.EncodeInteger(getNewOptionsByMobStat(Laser).wOption);
                oPacket.EncodeInteger(getNewOptionsByMobStat(Laser).uOption);
            }
            if (hasNewMobStat(ElementResetBySummon)) {
                oPacket.EncodeInteger(getNewOptionsByMobStat(ElementResetBySummon).cOption);
                oPacket.EncodeInteger(getNewOptionsByMobStat(ElementResetBySummon).pOption);
                oPacket.EncodeInteger(getNewOptionsByMobStat(ElementResetBySummon).uOption);
                oPacket.EncodeInteger(getNewOptionsByMobStat(ElementResetBySummon).wOption);
            }
            if (hasNewMobStat(BahamutLightElemAddDam)) {
                oPacket.EncodeInteger(getNewOptionsByMobStat(BahamutLightElemAddDam).pOption);
                oPacket.EncodeInteger(getNewOptionsByMobStat(BahamutLightElemAddDam).cOption);
            }
            getNewStatVals().clear();
        }
    }

    private int[] getMaskByCollection(Map<MobStat, Option> map) {
        int[] res = new int[3];
        for (MobStat mobStat : map.keySet()) {
            res[mobStat.getPosition()] |= mobStat.getVal();
        //System.out.println(mobStat);
        }
        //System.out.println(String.format("Mob stat mask is %d %d %d, in String format:", res[0], res[1], res[2]));
        OutPacket outPacket = new OutPacket(80);
        for (int i = 0; i < res.length; i++) {
            outPacket.EncodeInteger(res[i]);
        }
        //System.out.println(Util.readableByteArray(outPacket.getData()));
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
     * Adds a new MobStat to this MobTemporaryStat. Will immediately broadcast the reaction to all clients.
     * Only works for user skills, not mob skills. For the latter, use {@link #addMobSkillOptionsAndBroadCast(MobStat, Option)}.
     * @param mobStat The MobStat to add.
     * @param option The Option that contains the values of the stat.
     */
    public void addStatOptionsAndBroadcast(MobStat mobStat, Option option) {
        addStatOptions(mobStat, option);
        mob.getMap().broadcastMessage(MobPacket.mobStatSet(getMob(), (short) 0));
    }

    /**
     * Adds a new MobStat to this MobTemporary stat. Will immediately broadcast the reaction to all clients.
     * Only works for mob skills, not user skills. For the latter, use {@link #addStatOptionsAndBroadcast(MobStat, Option)}.
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
