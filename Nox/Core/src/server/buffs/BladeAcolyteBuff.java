package server.buffs;

import server.buffs.manager.AbstractBuffClass;
import client.CharacterTemporaryStat;
import client.MapleJob;
import constants.skills.BladeAcolyte;
import server.MapleStatEffect;
import server.MapleStatInfo;
import server.buffs.manager.BuffEffectManager;

/**
 *
 * @author Novak
 *
 */
@BuffEffectManager
public class BladeAcolyteBuff extends AbstractBuffClass {

    public BladeAcolyteBuff() {
        skills = new int[]{
            BladeAcolyte.KATARA_BOOSTER
        };
    }

    @Override
    public boolean containsJob(int job) {
        return job == MapleJob.BLADE_ACOLYTE.getId();
    }

    @Override
    public void handleEffect(MapleStatEffect eff, int skill) {
        switch (skill) {
            case BladeAcolyte.KATARA_BOOSTER: // Katara Booster
                eff.statups.put(CharacterTemporaryStat.Booster, eff.info.get(MapleStatInfo.x));
                break;
            case BladeAcolyte.CHANNEL_KARMA: // Channel Karma
                eff.statups.put(CharacterTemporaryStat.PAD, eff.info.get(MapleStatInfo.pad));
                break;
        }
    }
}
