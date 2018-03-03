package server.skills.effects;

import server.skills.effects.manager.AbstractEffect;
import client.CharacterTemporaryStat;
import client.MapleJob;
import client.MonsterStatus;
import constants.skills.Pirate;
import server.MapleStatEffect;
import server.MapleStatInfo;
import server.skills.effects.manager.Effect;

/**
 *
 * @author Novak
 *
 */
@Effect
public class PirateEffect extends AbstractEffect {

    @Override
    public void SetEffect(MapleStatEffect pEffect, int nSourceID) {
        switch (nSourceID) {
            case Pirate.BULLET_TIME:
                break;
            case Pirate.DASH:
                pEffect.statups.put(CharacterTemporaryStat.DashSpeed, pEffect.info.get(MapleStatInfo.x));
                pEffect.statups.put(CharacterTemporaryStat.DashJump, pEffect.info.get(MapleStatInfo.y));
                break;
            case Pirate.DOUBLE_SHOT:
                break;
            case Pirate.FLASH_FIST:
                break;
            case Pirate.FORTUNES_FAVOR_1:
                break;
            case Pirate.OCTOPUSH:
                break;
            case Pirate.SHADOW_HEART_2:
                break;
            case Pirate.SOMMERSAULT_KICK:
                break;
            case Pirate.TRIPLE_FIRE:
                break;

        }

    }

    @Override
    public boolean IsCorrectClass(int nClass) {
        return nClass == 500;
    }

}
