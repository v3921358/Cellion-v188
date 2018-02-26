package server.buffs;

import server.buffs.manager.AbstractBuffClass;
import client.CharacterTemporaryStat;
import client.MapleJob;
import client.MonsterStatus;
import constants.skills.Marauder;
import server.MapleStatEffect;
import server.buffs.manager.BuffEffectManager;

/**
 *
 * @author Novak
 *
 */
@BuffEffectManager
public class MarauderBuff extends AbstractBuffClass {

    public MarauderBuff() {
        skills = new int[]{
            Marauder.ROLL_OF_THE_DICE,
            Marauder.ENERGY_BURST
        };
    }

    @Override
    public boolean containsJob(int job) {
        return job == MapleJob.MARAUDER.getId();
    }

    @Override
    public void handleEffect(MapleStatEffect eff, int skill) {
        switch (skill) {
            case Marauder.ROLL_OF_THE_DICE:
                eff.statups.put(CharacterTemporaryStat.Dice, 0);
                break;
            case Marauder.ENERGY_BURST:
                eff.monsterStatus.put(MonsterStatus.STUN, 1);
                break;
        }
    }
}
