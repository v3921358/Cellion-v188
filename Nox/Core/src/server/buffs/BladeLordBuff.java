package server.buffs;

import server.buffs.manager.AbstractBuffClass;
import client.CharacterTemporaryStat;
import client.MapleJob;
import constants.skills.BladeLord;
import server.MapleStatEffect;
import server.MapleStatInfo;
import server.buffs.manager.BuffEffectManager;

/**
 *
 * @author Novak
 *
 */
@BuffEffectManager
public class BladeLordBuff extends AbstractBuffClass {

    public BladeLordBuff() {
        skills = new int[]{
            BladeLord.ADVANCED_DARK_SIGHT,
            BladeLord.MIRROR_IMAGE
        };
    }

    @Override
    public boolean containsJob(int job) {
        return job == MapleJob.BLADE_LORD.getId();
    }

    @Override
    public void handleEffect(MapleStatEffect eff, int skill) {
        switch (skill) {
            case BladeLord.MIRROR_IMAGE: // Mirror Image
                eff.statups.put(CharacterTemporaryStat.ShadowPartner, eff.info.get(MapleStatInfo.x));
                break;
            case BladeLord.ADVANCED_DARK_SIGHT:
                eff.statups.put(CharacterTemporaryStat.DarkSight, (int) eff.getLevel());
                break;
        }
    }
}
