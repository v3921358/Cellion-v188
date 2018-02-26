package server.buffs;

import server.buffs.manager.AbstractBuffClass;
import client.CharacterTemporaryStat;
import client.MapleJob;
import client.MonsterStatus;
import static constants.GameConstants.isDawnWarriorCygnus;
import constants.skills.DawnWarrior;
import server.MapleStatEffect;
import server.MapleStatInfo;
import server.buffs.manager.BuffEffectManager;

@BuffEffectManager
public class DawnWarriorBuff extends AbstractBuffClass {

    public DawnWarriorBuff() {
        skills = new int[]{
            DawnWarrior.HAND_OF_LIGHT,
            DawnWarrior.SOUL_ELEMENT,
            DawnWarrior.DIVINE_HAND,
            DawnWarrior.FALLING_MOON,
            DawnWarrior.RISING_SUN,
            DawnWarrior.SOUL_OF_THE_GUARDIAN,
            DawnWarrior.EQUINOX_CYCLE,
            DawnWarrior.SOUL_PLEDGE,
            DawnWarrior.GLORY_OF_THE_GUARDIANS,
            DawnWarrior.CALL_OF_CYGNUS,
            DawnWarrior.SOUL_PLEDGE
        };
    }

    @Override
    public boolean containsJob(int job) {
        return isDawnWarriorCygnus(job);
    }

    @Override
    public void handleEffect(MapleStatEffect eff, int skill) {
        switch (skill) {
            case DawnWarrior.HAND_OF_LIGHT: //Hand of Light
                eff.statups.put(CharacterTemporaryStat.ACCR, eff.info.get(MapleStatInfo.x));
                eff.statups.put(CharacterTemporaryStat.IndiePAD, eff.info.get(MapleStatInfo.indiePad));
                break;
            case DawnWarrior.SOUL_ELEMENT: // Soul Element
                eff.monsterStatus.put(MonsterStatus.SUMMON, 1);
                eff.monsterStatus.put(MonsterStatus.STUN, 1);
                break;
            case DawnWarrior.DIVINE_HAND: // Divine Hand
                eff.statups.put(CharacterTemporaryStat.IndiePAD, eff.info.get(MapleStatInfo.indiePad));
                break;
            case DawnWarrior.FALLING_MOON: //Falling Moon
                eff.statups.put(CharacterTemporaryStat.PoseType, 1);
                eff.statups.put(CharacterTemporaryStat.BuckShot, eff.info.get(MapleStatInfo.x));
                eff.statups.put(CharacterTemporaryStat.IndieCr, eff.info.get(MapleStatInfo.indieCr));
                eff.info.put(MapleStatInfo.time, 2100000000);
                break;
            case DawnWarrior.RISING_SUN: // Rising Sun //
                eff.statups.put(CharacterTemporaryStat.PoseType, 2);
                eff.statups.put(CharacterTemporaryStat.IndieDamR, eff.info.get(MapleStatInfo.indieDamR));
                eff.statups.put(CharacterTemporaryStat.Booster, eff.info.get(MapleStatInfo.indieBooster));
                eff.info.put(MapleStatInfo.time, 2100000000);
                break;
            case DawnWarrior.SOUL_OF_THE_GUARDIAN: // soul of guardian
                eff.statups.put(CharacterTemporaryStat.IncMaxHP, eff.info.get(MapleStatInfo.indieMhp));
                eff.statups.put(CharacterTemporaryStat.IndieMAD, eff.info.get(MapleStatInfo.indiePdd));
                eff.statups.put(CharacterTemporaryStat.IndiePAD, eff.info.get(MapleStatInfo.indiePdd));
                break;
            case DawnWarrior.EQUINOX_CYCLE: // Equinox Cycle
                eff.statups.put(CharacterTemporaryStat.GlimmeringTime, 1);
                break;
            case DawnWarrior.SOUL_PLEDGE: // soul pledge
                eff.statups.put(CharacterTemporaryStat.IndieCr, eff.info.get(MapleStatInfo.indieCr));
                eff.statups.put(CharacterTemporaryStat.IndieAllStat, eff.info.get(MapleStatInfo.indieAllStat));
                eff.statups.put(CharacterTemporaryStat.Stance, eff.info.get(MapleStatInfo.prop));
                break;
            case DawnWarrior.GLORY_OF_THE_GUARDIANS: //Glory of the guardians
                eff.statups.put(CharacterTemporaryStat.DamR, eff.info.get(MapleStatInfo.indieDamR));
                //eff.statups.put(CharacterTemporaryStat.IncMaxDamage, eff.info.get(MapleStatInfo.indieMaxDamageOver));
                break;
            case DawnWarrior.CALL_OF_CYGNUS:
                eff.statups.put(CharacterTemporaryStat.IndieStatR, eff.info.get(MapleStatInfo.x));
                break;
        }
    }
}
