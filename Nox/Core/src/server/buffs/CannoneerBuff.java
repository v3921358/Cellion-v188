package server.buffs;

import server.buffs.manager.AbstractBuffClass;
import client.CharacterTemporaryStat;
import client.MapleJob;
import client.MonsterStatus;
import constants.skills.Cannoneer;
import java.util.Random;
import server.MapleStatEffect;
import server.MapleStatInfo;
import server.buffs.manager.BuffEffectManager;

/**
 * @author Novak
 *
 */
@BuffEffectManager
public class CannoneerBuff extends AbstractBuffClass {

    public CannoneerBuff() {
        skills = new int[]{
            Cannoneer.CANNON_BOOSTER,
            Cannoneer.BARREL_BOMB,
            Cannoneer.MONKEY_MAGIC,};
    }

    @Override
    public boolean containsJob(int job) {
        return job == MapleJob.CANNONEER.getId();
    }

    @Override
    public void handleEffect(MapleStatEffect eff, int skill) {
        switch (skill) {
            case Cannoneer.CANNON_BOOSTER:
                eff.statups.put(CharacterTemporaryStat.Booster, eff.info.get(MapleStatInfo.x)); 
                break;
            case Cannoneer.BARREL_BOMB:
                eff.monsterStatus.put(MonsterStatus.STUN, 1); 
                break;
            case Cannoneer.MONKEY_MAGIC: // Monkey Magic
                eff.statups.put(CharacterTemporaryStat.IndieAllStat, eff.info.get(MapleStatInfo.indieAllStat));
                eff.statups.put(CharacterTemporaryStat.IndieJump, eff.info.get(MapleStatInfo.indieJump));
                eff.statups.put(CharacterTemporaryStat.IndieMHP, eff.info.get(MapleStatInfo.indieMhp));
                eff.statups.put(CharacterTemporaryStat.IndieMMP, eff.info.get(MapleStatInfo.indieMmp));
                eff.statups.put(CharacterTemporaryStat.IndieSpeed, eff.info.get(MapleStatInfo.indieSpeed));
                break;
        }
    }
}
