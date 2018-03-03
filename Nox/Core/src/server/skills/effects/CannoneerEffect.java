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
                break;
            case Cannoneer.BARREL_BOMB_1:
                break;
            case Cannoneer.CANNON_BOOSTER:
                break;
            case Cannoneer.CANNON_MASTERY:
                break;
            case Cannoneer.CRITICAL_FIRE:
                break;
            case Cannoneer.MONKEY_MAGIC:
                break;
            case Cannoneer.PIRATE_TRAINING:
                break;
            case Cannoneer.SCATTER_SHOT:
                break;
            case Cannoneer.BLAST_BACK:
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
