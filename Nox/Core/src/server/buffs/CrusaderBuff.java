package server.buffs;

import server.buffs.manager.AbstractBuffClass;
import client.MapleJob;
import client.MonsterStatus;
import constants.skills.Crusader;
import server.MapleStatEffect;
import server.MapleStatInfo;
import server.buffs.manager.BuffEffectManager;

/**
 *
 * @author Novak
 *
 */
@BuffEffectManager
public class CrusaderBuff extends AbstractBuffClass {

    public CrusaderBuff() {
        skills = new int[]{
            Crusader.SHOUT,
            Crusader.PANIC
        };
    }

    @Override
    public boolean containsJob(int job) {
        return job == MapleJob.CRUSADER.getId();
    }

    @Override
    public void handleEffect(MapleStatEffect eff, int skill) {
        switch (skill) {
            case Crusader.SHOUT:
                eff.monsterStatus.put(MonsterStatus.STUN, 1);
                break;
            case Crusader.PANIC:
                eff.monsterStatus.put(MonsterStatus.DARKNESS, eff.info.get(MapleStatInfo.x));
                break;
        }
    }
}
