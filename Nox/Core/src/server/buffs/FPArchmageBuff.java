package server.buffs;

import server.buffs.manager.AbstractBuffClass;
import client.CharacterTemporaryStat;
import client.MapleJob;
import client.MonsterStatus;
import constants.skills.FPArchmage;
import server.MapleStatEffect;
import server.MapleStatInfo;
import server.buffs.manager.BuffEffectManager;

/**
 *
 * @author Novak
 *
 */
@BuffEffectManager
public class FPArchmageBuff extends AbstractBuffClass {

    public FPArchmageBuff() {
        skills = new int[]{
            FPArchmage.MAPLE_WARRIOR,
            FPArchmage.PARALYZE,
            FPArchmage.IFRIT,
            FPArchmage.INFINITY,
            FPArchmage.EPIC_ADVENTURE,
            FPArchmage.INFERNO_AURA
        };
    }

    @Override
    public boolean containsJob(int job) {
        return job == MapleJob.FP_ARCHMAGE.getId()
                || job == MapleJob.FP_ARCHMAGE_1.getId();
    }

    @Override
    public void handleEffect(MapleStatEffect eff, int skill) {
        switch (skill) {
            case FPArchmage.MAPLE_WARRIOR:
                eff.statups.put(CharacterTemporaryStat.BasicStatUp, eff.info.get(MapleStatInfo.x));
                break;
            case FPArchmage.PARALYZE:
                eff.monsterStatus.put(MonsterStatus.FREEZE, 1);
                eff.info.put(MapleStatInfo.time, eff.info.get(MapleStatInfo.time) * 2);
                break;
            case FPArchmage.IFRIT:
                eff.statups.put(CharacterTemporaryStat.SUMMON, 1);
                eff.monsterStatus.put(MonsterStatus.STUN, 1);
                break;
            case FPArchmage.INFINITY:
                eff.setHpR(eff.info.get(MapleStatInfo.y) / 100.0);
                eff.setMpR(eff.info.get(MapleStatInfo.y) / 100.0);
                eff.statups.put(CharacterTemporaryStat.Infinity, eff.info.get(MapleStatInfo.x));
                eff.statups.put(CharacterTemporaryStat.Stance, (int) eff.info.get(MapleStatInfo.prop));
                break;
            case FPArchmage.EPIC_ADVENTURE:
                eff.statups.put(CharacterTemporaryStat.DamR, eff.info.get(MapleStatInfo.indieDamR));
                //eff.statups.put(CharacterTemporaryStat.IndieMaxDamageOver, eff.info.get(MapleStatInfo.indieMaxDamageOver));
                break;
        }
    }
}
