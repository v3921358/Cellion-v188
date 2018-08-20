/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.life.mob;

import enums.MobStat;
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
import server.StatEffect;
import server.life.Mob;
import static enums.StatInfo.dot;
import static enums.StatInfo.dotInterval;
import static enums.StatInfo.dotSuperpos;
import static enums.StatInfo.dotTime;
import static enums.MobStat.*;
import static enums.MobStat.BurnedInfo;
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

            for (Map.Entry<MobStat, Option> entry : getCurrentStatVals().entrySet()) {
                MobStat mobStat = entry.getKey();
                Option option = entry.getValue();
                oPacket.EncodeInt(getCurrentOptionsByMobStat(mobStat).nOption);
                oPacket.EncodeInt(getCurrentOptionsByMobStat(mobStat).rOption);
                oPacket.EncodeShort(getCurrentOptionsByMobStat(mobStat).tOption / 500);
            }
            if (hasCurrentMobStat(PDR)) {
                oPacket.EncodeInt(getCurrentOptionsByMobStat(PDR).cOption);
            }
            if (hasCurrentMobStat(MDR)) {
                oPacket.EncodeInt(getCurrentOptionsByMobStat(MDR).cOption);
            }
            if (hasCurrentMobStat(PCounter)) {
                oPacket.EncodeInt(getCurrentOptionsByMobStat(PCounter).wOption);
            }
            if (hasCurrentMobStat(MCounter)) {
                oPacket.EncodeInt(getCurrentOptionsByMobStat(MCounter).wOption);
            }
            if (hasCurrentMobStat(PCounter)) {
                oPacket.EncodeInt(getCurrentOptionsByMobStat(PCounter).mOption); // nCounterProb
                oPacket.EncodeInt(getCurrentOptionsByMobStat(PCounter).bOption); // bCounterDelay
                oPacket.EncodeInt(getCurrentOptionsByMobStat(PCounter).nReason); // nAggroRank
            } else if (hasCurrentMobStat(MCounter)) {
                oPacket.EncodeInt(getCurrentOptionsByMobStat(MCounter).mOption); // nCounterProb
                oPacket.EncodeInt(getCurrentOptionsByMobStat(MCounter).bOption); // bCounterDelay
                oPacket.EncodeInt(getCurrentOptionsByMobStat(MCounter).nReason); // nAggroRank
            }
            if (hasCurrentMobStat(Fatality)) {
                oPacket.EncodeInt(getCurrentOptionsByMobStat(Fatality).wOption);
                oPacket.EncodeInt(getCurrentOptionsByMobStat(Fatality).uOption);
                oPacket.EncodeInt(getCurrentOptionsByMobStat(Fatality).pOption);
                oPacket.EncodeInt(getCurrentOptionsByMobStat(Fatality).yOption);
                oPacket.EncodeInt(getCurrentOptionsByMobStat(Fatality).mOption);
            }
            if (hasCurrentMobStat(Explosion)) {
                oPacket.EncodeInt(getCurrentOptionsByMobStat(Explosion).wOption);
            }
            if (hasCurrentMobStat(ExtraBuffStat)) {
                List<Option> values = getCurrentOptionsByMobStat(ExtraBuffStat).extraOpts;
                oPacket.EncodeBool(values.size() > 0);
                if (values.size() > 0) {
                    oPacket.EncodeInt(getCurrentOptionsByMobStat(ExtraBuffStat).extraOpts.get(0).nOption); // nPAD
                    oPacket.EncodeInt(getCurrentOptionsByMobStat(ExtraBuffStat).extraOpts.get(0).mOption); // nMAD
                    oPacket.EncodeInt(getCurrentOptionsByMobStat(ExtraBuffStat).extraOpts.get(0).xOption); // nPDR
                    oPacket.EncodeInt(getCurrentOptionsByMobStat(ExtraBuffStat).extraOpts.get(0).yOption); // nMDR
                }
            }
            if (hasCurrentMobStat(DeadlyCharge)) {
                oPacket.EncodeInt(getCurrentOptionsByMobStat(DeadlyCharge).pOption);
                oPacket.EncodeInt(getCurrentOptionsByMobStat(DeadlyCharge).pOption);
            }
            if (hasCurrentMobStat(Incizing)) {
                oPacket.EncodeInt(getCurrentOptionsByMobStat(Incizing).wOption);
                oPacket.EncodeInt(getCurrentOptionsByMobStat(Incizing).uOption);
                oPacket.EncodeInt(getCurrentOptionsByMobStat(Incizing).pOption);
            }
            if (hasCurrentMobStat(Speed)) {
                oPacket.EncodeByte(getCurrentOptionsByMobStat(Speed).mOption);
            }
            if (hasCurrentMobStat(BMageDebuff)) {
                oPacket.EncodeInt(getCurrentOptionsByMobStat(BMageDebuff).cOption);
            }
            if (hasCurrentMobStat(DarkLightning)) {
                oPacket.EncodeInt(getCurrentOptionsByMobStat(DarkLightning).cOption);
            }
            if (hasCurrentMobStat(BattlePvPHelenaMark)) {
                oPacket.EncodeInt(getCurrentOptionsByMobStat(BattlePvPHelenaMark).cOption);
            }
            if (hasCurrentMobStat(MultiPMDR)) {
                oPacket.EncodeInt(getCurrentOptionsByMobStat(MultiPMDR).cOption);
            }
            if (hasCurrentMobStat(Freeze)) {
                oPacket.EncodeInt(getCurrentOptionsByMobStat(Freeze).cOption);
            }
            if (hasCurrentMobStat(BurnedInfo)) {
                oPacket.EncodeByte(getBurnedInfos().size());
                for (BurnedInfo bi : getBurnedInfos()) {
                    bi.encode(oPacket);
                }
            }
            if (hasCurrentMobStat(InvincibleBalog)) {
                oPacket.EncodeByte(getCurrentOptionsByMobStat(InvincibleBalog).nOption);
                oPacket.EncodeByte(getCurrentOptionsByMobStat(InvincibleBalog).bOption);
            }
            if (hasCurrentMobStat(ExchangeAttack)) {
                oPacket.EncodeByte(getCurrentOptionsByMobStat(ExchangeAttack).bOption);
            }
            if (hasCurrentMobStat(AddDamParty)) {
                oPacket.EncodeInt(getCurrentOptionsByMobStat(AddDamParty).wOption);
                oPacket.EncodeInt(getCurrentOptionsByMobStat(AddDamParty).pOption);
                oPacket.EncodeInt(getCurrentOptionsByMobStat(AddDamParty).cOption);
            }
            if (hasCurrentMobStat(LinkTeam)) {
                oPacket.EncodeString(getLinkTeam());
            }
            if (hasCurrentMobStat(SoulExplosion)) {
                oPacket.EncodeInt(getCurrentOptionsByMobStat(SoulExplosion).nOption);
                oPacket.EncodeInt(getCurrentOptionsByMobStat(SoulExplosion).rOption);
                oPacket.EncodeInt(getCurrentOptionsByMobStat(SoulExplosion).wOption);
            }
            if (hasCurrentMobStat(SeperateSoulP)) {
                oPacket.EncodeInt(getCurrentOptionsByMobStat(SeperateSoulP).nOption);
                oPacket.EncodeInt(getCurrentOptionsByMobStat(SeperateSoulP).rOption);
                oPacket.EncodeShort(getCurrentOptionsByMobStat(SeperateSoulP).tOption / 500);
                oPacket.EncodeInt(getCurrentOptionsByMobStat(SeperateSoulP).wOption);
                oPacket.EncodeInt(getCurrentOptionsByMobStat(SeperateSoulP).uOption);
            }
            if (hasCurrentMobStat(SeperateSoulC)) {
                oPacket.EncodeInt(getCurrentOptionsByMobStat(SeperateSoulC).nOption);
                oPacket.EncodeInt(getCurrentOptionsByMobStat(SeperateSoulC).rOption);
                oPacket.EncodeShort(getCurrentOptionsByMobStat(SeperateSoulC).tOption / 500);
                oPacket.EncodeInt(getCurrentOptionsByMobStat(SeperateSoulC).wOption);
            }
            if (hasCurrentMobStat(Ember)) {
                oPacket.EncodeInt(getCurrentOptionsByMobStat(Ember).nOption);
                oPacket.EncodeInt(getCurrentOptionsByMobStat(Ember).rOption);
                oPacket.EncodeInt(getCurrentOptionsByMobStat(Ember).wOption);
                oPacket.EncodeInt(getCurrentOptionsByMobStat(Ember).tOption / 500);
                oPacket.EncodeInt(getCurrentOptionsByMobStat(Ember).uOption);
            }
            if (hasCurrentMobStat(TrueSight)) {
                oPacket.EncodeInt(getCurrentOptionsByMobStat(TrueSight).nOption);
                oPacket.EncodeInt(getCurrentOptionsByMobStat(TrueSight).rOption);
                oPacket.EncodeInt(getCurrentOptionsByMobStat(TrueSight).tOption / 500);
                oPacket.EncodeInt(getCurrentOptionsByMobStat(TrueSight).cOption);
                oPacket.EncodeInt(getCurrentOptionsByMobStat(TrueSight).pOption);
                oPacket.EncodeInt(getCurrentOptionsByMobStat(TrueSight).uOption);
                oPacket.EncodeInt(getCurrentOptionsByMobStat(TrueSight).wOption);
            }
            if (hasCurrentMobStat(MultiDamSkill)) {
                oPacket.EncodeInt(getCurrentOptionsByMobStat(MultiDamSkill).cOption);
            }
            if (hasCurrentMobStat(Laser)) {
                oPacket.EncodeInt(getCurrentOptionsByMobStat(Laser).nOption);
                oPacket.EncodeInt(getCurrentOptionsByMobStat(Laser).rOption);
                oPacket.EncodeInt(getCurrentOptionsByMobStat(Laser).tOption / 500);
                oPacket.EncodeInt(getCurrentOptionsByMobStat(Laser).wOption);
                oPacket.EncodeInt(getCurrentOptionsByMobStat(Laser).uOption);
            }
            if (hasCurrentMobStat(ElementResetBySummon)) {
                oPacket.EncodeInt(getCurrentOptionsByMobStat(ElementResetBySummon).cOption);
                oPacket.EncodeInt(getCurrentOptionsByMobStat(ElementResetBySummon).pOption);
                oPacket.EncodeInt(getCurrentOptionsByMobStat(ElementResetBySummon).uOption);
                oPacket.EncodeInt(getCurrentOptionsByMobStat(ElementResetBySummon).wOption);
            }
            if (hasCurrentMobStat(BahamutLightElemAddDam)) {
                oPacket.EncodeInt(getCurrentOptionsByMobStat(BahamutLightElemAddDam).pOption);
                oPacket.EncodeInt(getCurrentOptionsByMobStat(BahamutLightElemAddDam).cOption);
            }
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
            getMob().getMap().broadcastPacket(MobPacket.mobStatReset(getMob(), (byte) 1, false));
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
            getMob().getMap().broadcastPacket(MobPacket.mobStatReset(getMob(), (byte) 1, false, biList));
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
        mob.getMap().broadcastPacket(MobPacket.mobStatSet(getMob(), (short) 0));
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
        StatEffect si = skill.getEffect(slv);
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
