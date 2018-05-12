package server.skills.effects;

import server.skills.effects.manager.AbstractEffect;
import client.CharacterTemporaryStat;
import client.MapleJob;
import client.MonsterStatus;
import constants.skills.Cannoneer;
import server.StatEffect;
import server.StatInfo;
import server.skills.effects.manager.Effect;

/**
 *
 * @author Novak
 *
 */
@Effect
public class CannoneerEffect extends AbstractEffect {

    @Override
    public void SetEffect(StatEffect pEffect, int nSourceID) {
        switch (nSourceID) {
            case Cannoneer.BARREL_BOMB:
                pEffect.monsterStatus.put(MonsterStatus.STUN, 1);
                break;
            case Cannoneer.BARREL_BOMB_1:
                break;
            case Cannoneer.CANNON_BOOSTER:
                pEffect.statups.put(CharacterTemporaryStat.Booster, pEffect.info.get(StatInfo.x));
                break;
            case Cannoneer.CANNON_MASTERY:
                break;
            case Cannoneer.CRITICAL_FIRE:
                break;
            case Cannoneer.MONKEY_MAGIC:
                pEffect.statups.put(CharacterTemporaryStat.IndieAllStat, pEffect.info.get(StatInfo.indieAllStat));
                pEffect.statups.put(CharacterTemporaryStat.IndieJump, pEffect.info.get(StatInfo.indieJump));
                pEffect.statups.put(CharacterTemporaryStat.IndieMHP, pEffect.info.get(StatInfo.indieMhp));
                pEffect.statups.put(CharacterTemporaryStat.IndieMMP, pEffect.info.get(StatInfo.indieMmp));
                pEffect.statups.put(CharacterTemporaryStat.IndieSpeed, pEffect.info.get(StatInfo.indieSpeed));
                break;
            case Cannoneer.PIRATE_TRAINING:
                break;
            case Cannoneer.SCATTER_SHOT:
                break;
            case Cannoneer.BLAST_BACK:
                pEffect.monsterStatus.put(MonsterStatus.SPEED, pEffect.info.get(StatInfo.z));
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
