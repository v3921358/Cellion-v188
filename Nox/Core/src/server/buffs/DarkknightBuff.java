package server.buffs;

import server.buffs.manager.AbstractBuffClass;
import client.CharacterTemporaryStat;
import client.MapleJob;
import constants.skills.Darkknight;
import server.MapleStatEffect;
import server.MapleStatInfo;
import server.buffs.manager.BuffEffectManager;

/**
 *
 * @author Novak
 *
 */
@BuffEffectManager
public class DarkknightBuff extends AbstractBuffClass {

    public DarkknightBuff() {
        skills = new int[]{
            Darkknight.SACRIFICE,
            Darkknight.MAPLE_WARRIOR,
            Darkknight.EPIC_ADVENTURE,
            Darkknight.DARK_THIRST
        };
    }

    @Override
    public boolean containsJob(int job) {
        return job == MapleJob.DARKKNIGHT.getId()
                || job == MapleJob.DARKKNIGHT_1.getId();
    }

    @Override
    public void handleEffect(MapleStatEffect eff, int skill) {
        switch (skill) {
            case Darkknight.SACRIFICE:
                eff.statups.put(CharacterTemporaryStat.IgnoreTargetDEF, eff.info.get(MapleStatInfo.ignoreMobpdpR));
                eff.statups.put(CharacterTemporaryStat.BdR, eff.info.get(MapleStatInfo.indieBDR));
                break;
            case Darkknight.MAPLE_WARRIOR:
                eff.statups.put(CharacterTemporaryStat.BasicStatUp, eff.info.get(MapleStatInfo.x));
                break;
            case Darkknight.EPIC_ADVENTURE:
                eff.statups.put(CharacterTemporaryStat.DamR, eff.info.get(MapleStatInfo.indieDamR));
                //eff.statups.put(CharacterTemporaryStat.IndieMaxDamageOver, eff.info.get(MapleStatInfo.indieMaxDamageOver));
                break;
            case Darkknight.DARK_THIRST:
                eff.statups.put(CharacterTemporaryStat.IndiePAD, eff.info.get(MapleStatInfo.indiePad));
                eff.statups.put(CharacterTemporaryStat.Regen, eff.info.get(MapleStatInfo.x));
                break;
        }
    }
}
