package server.skills.effects;

import server.skills.effects.manager.AbstractEffect;
import client.CharacterTemporaryStat;
import client.MapleJob;
import client.MonsterStatus;
import constants.skills.Ranger;
import server.StatEffect;
import server.StatInfo;
import server.skills.effects.manager.Effect;

/**
 *
 * @author Novak
 *
 */
@Effect
public class RangerEffect extends AbstractEffect {

    @Override
    public void SetEffect(StatEffect pEffect, int nSourceID) {
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
                pEffect.statups.put(CharacterTemporaryStat.SUMMON, 1);
                pEffect.monsterStatus.put(MonsterStatus.STUN, 1);
                break;
            case Ranger.PUPPET_1:
                break;
            case Ranger.RECKLESS_HUNT_BOW:
                pEffect.statups.put(CharacterTemporaryStat.DamR, pEffect.info.get(StatInfo.indieDamR));
                pEffect.statups.put(CharacterTemporaryStat.PAD, pEffect.info.get(StatInfo.padX));
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
