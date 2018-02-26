package server.buffs;

import server.buffs.manager.AbstractBuffClass;
import client.CharacterTemporaryStat;
import client.MapleJob;
import constants.skills.Pirate;
import server.MapleStatEffect;
import server.MapleStatInfo;
import server.buffs.manager.BuffEffectManager;

/**
 *
 * @author Maple
 */
@BuffEffectManager
public class PirateBuff extends AbstractBuffClass {

    public PirateBuff() {
        skills = new int[]{
            Pirate.DASH
        };
    }

    @Override
    public boolean containsJob(int job) {
        return job == MapleJob.PIRATE.getId();
    }

    @Override
    public void handleEffect(MapleStatEffect eff, int skill) {
        switch (skill) {
            case Pirate.DASH:
                eff.statups.put(CharacterTemporaryStat.DashSpeed, eff.info.get(MapleStatInfo.x));
                eff.statups.put(CharacterTemporaryStat.DashJump, eff.info.get(MapleStatInfo.y));
                break;
        }
    }
}
