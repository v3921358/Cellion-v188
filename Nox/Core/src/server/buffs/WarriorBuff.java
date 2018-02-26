package server.buffs;

import server.buffs.manager.AbstractBuffClass;
import client.CharacterTemporaryStat;
import client.MapleJob;
import constants.GameConstants;
import constants.skills.Warrior;
import server.MapleStatEffect;
import server.MapleStatInfo;
import server.buffs.manager.BuffEffectManager;

/**
 *
 * @author Novak
 */
@BuffEffectManager
public class WarriorBuff extends AbstractBuffClass {

    public WarriorBuff() {
        skills = new int[]{
            Warrior.IRON_BODY
        };
    }

    @Override
    public boolean containsJob(int job) {
        return job == MapleJob.WARRIOR.getId();
    }

    @Override
    public void handleEffect(MapleStatEffect eff, int skill) {
        switch (skill) {
            case Warrior.IRON_BODY:
                eff.statups.put(CharacterTemporaryStat.IndiePDD, eff.info.get(MapleStatInfo.indiePdd));
                break;
        }
    }
}
