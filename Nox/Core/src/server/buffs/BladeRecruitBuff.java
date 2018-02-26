package server.buffs;

import server.buffs.manager.AbstractBuffClass;
import client.CharacterTemporaryStat;
import client.MapleJob;
import constants.GameConstants;
import constants.skills.BladeRecruit;
import server.MapleStatEffect;
import server.MapleStatInfo;
import server.buffs.manager.BuffEffectManager;

/**
 *
 * @author Novak
 *
 */
@BuffEffectManager
public class BladeRecruitBuff extends AbstractBuffClass {

    public BladeRecruitBuff() {
        skills = new int[]{
            BladeRecruit.SELF_HASTE
        };
    }

    @Override
    public boolean containsJob(int job) {
        return GameConstants.isDualBlade(job);
    }

    @Override
    public void handleEffect(MapleStatEffect eff, int skill) {
        switch (skill) {
            case BladeRecruit.SELF_HASTE: // Self Haste
                eff.statups.put(CharacterTemporaryStat.Jump, eff.info.get(MapleStatInfo.jump));
                eff.statups.put(CharacterTemporaryStat.Speed, eff.info.get(MapleStatInfo.speed));
                break;
        }
    }
}
