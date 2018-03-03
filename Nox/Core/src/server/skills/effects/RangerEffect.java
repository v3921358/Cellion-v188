package server.skills.effects;

import server.skills.effects.manager.AbstractEffect;
import client.CharacterTemporaryStat;
import client.MapleJob;
import client.MonsterStatus;
import constants.skills.Ranger;
import server.MapleStatEffect;
import server.MapleStatInfo;
import server.skills.effects.manager.Effect;

/**
 *
 * @author Novak
 *
 */
@Effect
public class RangerEffect extends AbstractEffect {

    @Override
    public void SetEffect(MapleStatEffect pEffect, int nSourceID) {
        switch (nSourceID) {
            case Ranger.ARROW_BLASTER_1:
                break;
            case Ranger.ARROW_RAIN_1:
                break;
            case Ranger.CONCENTRATE_1:
                break;
            case Ranger.DRAIN_ARROW:
                break;
            case Ranger.EVASION_BOOST_2:
                break;
            case Ranger.FLAME_SURGE_2:
                break;
            case Ranger.FOCUSED_FURY_1:
                break;
            case Ranger.HOOKSHOT:
                break;
            case Ranger.HURRICANE_2:
                break;
            case Ranger.MARKSMANSHIP_3:
                break;
            case Ranger.MORTAL_BLOW_2:
                break;
            case Ranger.PHOENIX:
                break;
            case Ranger.PUPPET_1:
                break;
            case Ranger.RECKLESS_HUNT_BOW:
                break;
            case Ranger.STRAFE_1:
                break;

        }

    }

    @Override
    public boolean IsCorrectClass(int nClass) {
        return nClass == 311;
    }

}
