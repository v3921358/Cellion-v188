package server.skills.effects;

import server.skills.effects.manager.AbstractEffect;
import client.CharacterTemporaryStat;
import client.MapleJob;
import client.MonsterStatus;
import constants.skills.Sniper;
import server.MapleStatEffect;
import server.MapleStatInfo;
import server.skills.effects.manager.Effect;

/**
 *
 * @author Novak
 *
 */
@Effect
public class SniperEffect extends AbstractEffect {

    @Override
    public void SetEffect(MapleStatEffect pEffect, int nSourceID) {
        switch (nSourceID) {
            case Sniper.AGGRESSIVE_RESISTANCE:
                break;
            case Sniper.ARROW_ERUPTION:
                break;
            case Sniper.CONCENTRATE_3:
                break;
            case Sniper.DRAGONS_BREATH:
                break;
            case Sniper.EVASION_BOOST:
                break;
            case Sniper.EXPLOSIVE_BOLT:
                break;
            case Sniper.FREEZER:
                break;
            case Sniper.HOOKSHOT_1:
                break;
            case Sniper.MARKSMANSHIP_1:
                break;
            case Sniper.MORTAL_BLOW:
                break;
            case Sniper.PAIN_KILLER:
                break;
            case Sniper.PUPPET_2:
                break;
            case Sniper.RECKLESS_HUNT_CROSSBOW:
                break;
            case Sniper.SNAPFREEZE_SHOT:
                break;
            case Sniper.STRAFE_2:
                break;

        }

    }

    @Override
    public boolean IsCorrectClass(int nClass) {
        return nClass == 321;
    }

}
