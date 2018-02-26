package server.buffs;

import server.buffs.manager.AbstractBuffClass;
import client.CharacterTemporaryStat;
import client.MapleJob;
import client.MonsterStatus;
import constants.skills.Marksman;
import server.MapleStatEffect;
import server.MapleStatInfo;
import server.buffs.manager.BuffEffectManager;

/**
 *
 * @author Novak
 *
 */
@BuffEffectManager
public class MarksmanBuff extends AbstractBuffClass {

    public MarksmanBuff() {
        skills = new int[]{
            Marksman.MAPLE_WARRIOR,
            Marksman.SHARP_EYES,
            Marksman.ILLUSION_STEP,
            Marksman.EPIC_ADVENTURE,
            Marksman.BULLSEYE_SHOT
        };
    }

    @Override
    public boolean containsJob(int job) {
        return job == MapleJob.MARKSMAN.getId()
                || job == MapleJob.MARKSMAN_1.getId();
    }

    @Override
    public void handleEffect(MapleStatEffect eff, int skill) {
        switch (skill) {
            case Marksman.MAPLE_WARRIOR:
                eff.statups.put(CharacterTemporaryStat.BasicStatUp, eff.info.get(MapleStatInfo.x));
                break;
            case Marksman.SHARP_EYES:
                eff.statups.put(CharacterTemporaryStat.SharpEyes, eff.info.get(MapleStatInfo.y));
                eff.statups.put(CharacterTemporaryStat.CriticalGrowing, eff.info.get(MapleStatInfo.x));
                break;
            case Marksman.BULLSEYE_SHOT:
                eff.statups.put(CharacterTemporaryStat.IgnoreMobpdpR, eff.info.get(MapleStatInfo.ignoreMobpdpR));
                eff.statups.put(CharacterTemporaryStat.IndieDamR, eff.info.get(MapleStatInfo.indieDamR));
                eff.statups.put(CharacterTemporaryStat.BullsEye, 1);
                break;
            case Marksman.ILLUSION_STEP:
                eff.statups.put(CharacterTemporaryStat.Blind, eff.info.get(MapleStatInfo.x));
                eff.monsterStatus.put(MonsterStatus.ACC, eff.info.get(MapleStatInfo.x));
                break;
            case Marksman.EPIC_ADVENTURE:
                eff.statups.put(CharacterTemporaryStat.DamR, eff.info.get(MapleStatInfo.indieDamR));
                //eff.statups.put(CharacterTemporaryStat.IndieMaxDamageOver, eff.info.get(MapleStatInfo.indieMaxDamageOver));
                break;
        }
    }
}
