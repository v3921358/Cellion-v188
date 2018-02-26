package server.buffs;

import server.buffs.manager.AbstractBuffClass;
import client.CharacterTemporaryStat;
import client.MapleJob;
import client.MonsterStatus;
import constants.skills.ILArchmage;
import server.MapleStatEffect;
import server.MapleStatInfo;
import server.buffs.manager.BuffEffectManager;

/**
 *
 * @author Novak
 *
 */
@BuffEffectManager
public class ILArchmageBuff extends AbstractBuffClass {

    public ILArchmageBuff() {
        skills = new int[]{
            ILArchmage.MAPLE_WARRIOR,
            ILArchmage.CHAIN_LIGHTNING,
            ILArchmage.BLIZZARD,
            ILArchmage.ELQUINES,
            ILArchmage.INFINITY,
            ILArchmage.EPIC_ADVENTURE,
            ILArchmage.FREEZING_BREATH,
            ILArchmage.ABSOLUTE_ZERO_AURA
        };
    }

    @Override
    public boolean containsJob(int job) {
        return job == MapleJob.IL_ARCHMAGE.getId()
                || job == MapleJob.IL_ARCHMAGE_1.getId();
    }

    @Override
    public void handleEffect(MapleStatEffect eff, int skill) {
        switch (skill) {
            case ILArchmage.MAPLE_WARRIOR:
                eff.statups.put(CharacterTemporaryStat.BasicStatUp, eff.info.get(MapleStatInfo.x));
                break;
            case ILArchmage.ABSOLUTE_ZERO_AURA:
                eff.info.put(MapleStatInfo.time, 2100000000);
                eff.statups.put(CharacterTemporaryStat.IceAura, 1);
                eff.statups.put(CharacterTemporaryStat.AsrR, eff.info.get(MapleStatInfo.v));
                eff.statups.put(CharacterTemporaryStat.TerR, eff.info.get(MapleStatInfo.v));
                break;
            case ILArchmage.CHAIN_LIGHTNING:
                eff.monsterStatus.put(MonsterStatus.STUN, 1);
                break;
            case ILArchmage.BLIZZARD:
                eff.monsterStatus.put(MonsterStatus.FREEZE, 1);
                eff.info.put(MapleStatInfo.time, eff.info.get(MapleStatInfo.time) * 2);
                break;
            case ILArchmage.ELQUINES:
                eff.statups.put(CharacterTemporaryStat.SUMMON, 1);
                eff.monsterStatus.put(MonsterStatus.FREEZE, 1);
                break;
            case ILArchmage.INFINITY:
                eff.setHpR(eff.info.get(MapleStatInfo.y) / 100.0);
                eff.setMpR(eff.info.get(MapleStatInfo.y) / 100.0);
                eff.statups.put(CharacterTemporaryStat.Infinity, eff.info.get(MapleStatInfo.x));
                eff.statups.put(CharacterTemporaryStat.Stance, (int) eff.info.get(MapleStatInfo.prop));
                break;
            case ILArchmage.EPIC_ADVENTURE:
                eff.statups.put(CharacterTemporaryStat.DamR, eff.info.get(MapleStatInfo.indieDamR));
                //eff.statups.put(CharacterTemporaryStat.IndieMaxDamageOver, eff.info.get(MapleStatInfo.indieMaxDamageOver));
                break;
            case ILArchmage.FREEZING_BREATH:
                eff.statups.put(CharacterTemporaryStat.KeyDownAreaMoving, eff.info.get(MapleStatInfo.x));
                eff.monsterStatus.put(MonsterStatus.FREEZE, 1);
                eff.monsterStatus.put(MonsterStatus.MAGIC_ATTACK_UP, -eff.info.get(MapleStatInfo.y));//Make sure this doesn't make em stronger tho

        }
    }
}
