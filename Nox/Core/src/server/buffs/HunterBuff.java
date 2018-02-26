package server.buffs;

import server.buffs.manager.AbstractBuffClass;
import client.CharacterTemporaryStat;
import client.MapleJob;
import client.MonsterStatus;
import constants.skills.Hunter;
import server.MapleStatEffect;
import server.MapleStatInfo;
import server.buffs.manager.BuffEffectManager;

/**
 *
 * @author Novak
 *
 */
@BuffEffectManager
public class HunterBuff extends AbstractBuffClass {

    public HunterBuff() {
        skills = new int[]{
            Hunter.SOUL_ARROW_BOW,
            Hunter.BOW_BOOSTER,
            Hunter.ARROW_BOMB
        };
    }

    @Override
    public boolean containsJob(int job) {
        return job == MapleJob.HUNTER.getId();
    }

    @Override
    public void handleEffect(MapleStatEffect eff, int skill) {
        switch (skill) {
            case Hunter.BOW_BOOSTER: //Bow Booster
                eff.statups.put(CharacterTemporaryStat.Booster, eff.info.get(MapleStatInfo.x));
                break;
            case Hunter.SOUL_ARROW_BOW: //SoulArrow bow
                eff.statups.put(CharacterTemporaryStat.SoulArrow, eff.info.get(MapleStatInfo.x));
                eff.statups.put(CharacterTemporaryStat.EPAD, eff.info.get(MapleStatInfo.epad));
                break;
            case Hunter.ARROW_BOMB:
                eff.monsterStatus.put(MonsterStatus.STUN, 1);
                break;
        }
    }
}
