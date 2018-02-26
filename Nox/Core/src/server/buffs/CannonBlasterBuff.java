package server.buffs;

import server.buffs.manager.AbstractBuffClass;
import client.CharacterTemporaryStat;
import client.MapleJob;
import client.MonsterStatus;
import constants.skills.CannonBlaster;
import java.util.EnumMap;
import server.MapleStatEffect;
import server.MapleStatInfo;
import server.Randomizer;
import server.buffs.manager.BuffEffectManager;
import tools.packet.CField;

/**
 *
 * @author Novak
 *
 */
@BuffEffectManager
public class CannonBlasterBuff extends AbstractBuffClass {

    public CannonBlasterBuff() {
        skills = new int[]{
            CannonBlaster.LUCK_OF_THE_DIE,
            CannonBlaster.MONKEY_WAVE,};
    }

    @Override
    public boolean containsJob(int job) {
        return job == MapleJob.CANNON_BLASTER.getId();
    }

    @Override
    public void handleEffect(MapleStatEffect eff, int skill) {
        switch (skill) {
            case CannonBlaster.LUCK_OF_THE_DIE:
                eff.statups.put(CharacterTemporaryStat.Dice, 0);
                break;
            case CannonBlaster.MONKEY_WAVE:
                eff.monsterStatus.put(MonsterStatus.STUN, 1);
                break;
        }
    }
}
