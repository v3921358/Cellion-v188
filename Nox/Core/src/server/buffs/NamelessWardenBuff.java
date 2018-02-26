package server.buffs;

import server.buffs.manager.AbstractBuffClass;
import client.CharacterTemporaryStat;
import client.MapleJob;
import constants.skills.NamelessWarden;
import server.MapleStatEffect;
import server.MapleStatInfo;
import server.buffs.manager.BuffEffectManager;

/**
 *
 * @author Novak
 *
 */
@BuffEffectManager
public class NamelessWardenBuff extends AbstractBuffClass {

    public NamelessWardenBuff() {
        skills = new int[]{
            NamelessWarden.KNIGHTS_WATCH,
            NamelessWarden.EMPRESSS_PRAYER,
            NamelessWarden.NIMBLE_FEET
        };
    }

    @Override
    public boolean containsJob(int job) {
        return job == MapleJob.NAMELESS_WARDEN.getId();
    }

    @Override
    public void handleEffect(MapleStatEffect eff, int skill) {
        switch (skill) {
            case NamelessWarden.KNIGHTS_WATCH:
                eff.statups.put(CharacterTemporaryStat.Stance, (int) eff.info.get(MapleStatInfo.prop));
                break;
            case NamelessWarden.EMPRESSS_PRAYER:
                eff.statups.put(CharacterTemporaryStat.MaxLevelBuff, (int) eff.info.get(MapleStatInfo.x));
                break;
            case NamelessWarden.NIMBLE_FEET:
                eff.statups.put(CharacterTemporaryStat.Speed, 10 + (eff.getLevel() - 1) * 5);
                break;
        }
    }
}
