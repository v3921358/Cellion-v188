package server.skills.effects;

import server.skills.effects.manager.AbstractEffect;
import client.CharacterTemporaryStat;
import client.MapleJob;
import client.MonsterStatus;
import constants.skills.Cannoneer;
import server.MapleStatEffect;
import server.MapleStatInfo;
import server.skills.effects.manager.Effect;

/**
 *
 * @author Novak
 *
 */
@Effect
public class CannoneerEffect extends AbstractEffect {

    @Override
    public void SetEffect(MapleStatEffect pEffect, int nSourceID) {
        switch (nSourceID) {
            case Cannoneer.BARREL_BOMB:
                pEffect.monsterStatus.put(MonsterStatus.STUN, 1);
                break;
            case Cannoneer.BARREL_BOMB_1:
                break;
            case Cannoneer.CANNON_BOOSTER:
                pEffect.statups.put(CharacterTemporaryStat.Booster, pEffect.info.get(MapleStatInfo.x));
                break;
            case Cannoneer.CANNON_MASTERY:
                break;
            case Cannoneer.CRITICAL_FIRE:
                break;
            case Cannoneer.MONKEY_MAGIC:
                pEffect.statups.put(CharacterTemporaryStat.IndieAllStat, pEffect.info.get(MapleStatInfo.indieAllStat));
                pEffect.statups.put(CharacterTemporaryStat.IndieJump, pEffect.info.get(MapleStatInfo.indieJump));
                pEffect.statups.put(CharacterTemporaryStat.IndieMHP, pEffect.info.get(MapleStatInfo.indieMhp));
                pEffect.statups.put(CharacterTemporaryStat.IndieMMP, pEffect.info.get(MapleStatInfo.indieMmp));
                pEffect.statups.put(CharacterTemporaryStat.IndieSpeed, pEffect.info.get(MapleStatInfo.indieSpeed));
                break;
            case Cannoneer.PIRATE_TRAINING:
                break;
            case Cannoneer.SCATTER_SHOT:
                break;
            case Cannoneer.BLAST_BACK:
                pEffect.monsterStatus.put(MonsterStatus.SPEED, pEffect.info.get(MapleStatInfo.z));
                break;
            case Cannoneer.CANNON_BLASTER:
                break;
            case Cannoneer.CANNON_BOOST:
                break;
            case Cannoneer.CANNON_STRIKE:
                break;
            case Cannoneer.FORTUNES_FAVOR_3:
                break;
            case Cannoneer.MONKEY_PUSH:
                break;

        }

    }

    @Override
    public boolean IsCorrectClass(int nClass) {
        return nClass == 530 || nClass == 501;
    }

}
