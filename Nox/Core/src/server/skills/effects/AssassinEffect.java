package server.skills.effects;

import server.skills.effects.manager.AbstractEffect;
import client.CharacterTemporaryStat;
import client.MapleJob;
import client.MonsterStatus;
import constants.skills.Assassin;
import server.MapleStatEffect;
import server.MapleStatInfo;
import server.skills.effects.manager.Effect;

/**
 *
 * @author Novak
 *
 */
@Effect
public class AssassinEffect extends AbstractEffect {

    @Override
    public void SetEffect(MapleStatEffect pEffect, int nSourceID) {
        switch (nSourceID) {
            case Assassin.ASSASSINS_MARK:
                break;
            case Assassin.ASSASSINS_MARK_1:
                break;
            case Assassin.ASSASSINS_MARK_2:
                break;
            case Assassin.CLAW_BOOSTER_1:
                pEffect.statups.put(CharacterTemporaryStat.Booster, pEffect.info.get(MapleStatInfo.x));
                break;
            case Assassin.CLAW_MASTERY:
                break;
            case Assassin.CRITICAL_THROW:
                break;
            case Assassin.DRAIN_1:
                break;
            case Assassin.FLASH_JUMP_6:
                break;
            case Assassin.GUST_CHARM_1:
                break;
            case Assassin.HASTE_5:
                break;
            case Assassin.PHYSICAL_TRAINING_50_5:
                break;
            case Assassin.SHADOW_RESISTANCE_2:
                break;
            case Assassin.SHURIKEN_BURST_1:
                break;

        }

    }

    @Override
    public boolean IsCorrectClass(int nClass) {
        return nClass == 410;
    }

}
