package server.buffs;

import server.buffs.manager.AbstractBuffClass;
import client.MapleJob;
import client.MonsterStatus;
import constants.skills.BladeSpecialist;
import server.MapleStatEffect;
import server.MapleStatInfo;
import server.buffs.manager.BuffEffectManager;

/**
 *
 * @author Novak
 *
 */
@BuffEffectManager
public class BladeSpecialistBuff extends AbstractBuffClass {

    public BladeSpecialistBuff() {
        skills = new int[]{
            BladeSpecialist.FLASHBANG
        };
    }

    @Override
    public boolean containsJob(int job) {
        return job == MapleJob.BLADE_SPECIALIST.getId();
    }

    @Override
    public void handleEffect(MapleStatEffect eff, int skill) {
        switch (skill) {
            case BladeSpecialist.FLASHBANG:
                eff.monsterStatus.put(MonsterStatus.STUN, 1);
                //eff.monsterStatus.put(MonsterStatus.DARKNESS, eff.info.get(MapleStatInfo.x));
                break;
        }
    }
}
