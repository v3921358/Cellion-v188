package server.buffs;

import server.buffs.manager.AbstractBuffClass;
import client.CharacterTemporaryStat;
import client.MapleJob;
import constants.skills.Thief;
import server.MapleStatEffect;
import server.MapleStatInfo;
import server.buffs.manager.BuffEffectManager;

/**
 *
 * @author Novak
 */
@BuffEffectManager
public class ThiefBuff extends AbstractBuffClass {

    public ThiefBuff() {
        skills = new int[]{
            Thief.DARK_SIGHT,
            Thief.HASTE
        };
    }

    @Override
    public boolean containsJob(int job) {
        return job == MapleJob.THIEF.getId();
    }

    @Override
    public void handleEffect(MapleStatEffect eff, int skill) {
        switch (skill) {
            case Thief.DARK_SIGHT: //Dark Sight
                eff.statups.put(CharacterTemporaryStat.DarkSight, eff.info.get(MapleStatInfo.x));
                break;
            case Thief.HASTE:
                eff.statups.put(CharacterTemporaryStat.Speed, eff.info.get(MapleStatInfo.speed));
                eff.statups.put(CharacterTemporaryStat.Jump, eff.info.get(MapleStatInfo.jump));
        }
    }
}
