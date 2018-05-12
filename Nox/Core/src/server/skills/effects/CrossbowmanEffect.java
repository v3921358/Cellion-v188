package server.skills.effects;

import server.skills.effects.manager.AbstractEffect;
import client.CharacterTemporaryStat;
import client.MapleJob;
import client.MonsterStatus;
import constants.skills.Crossbowman;
import server.StatEffect;
import server.StatInfo;
import server.skills.effects.manager.Effect;

/**
 *
 * @author Novak
 *
 */
@Effect
public class CrossbowmanEffect extends AbstractEffect {

    @Override
    public void SetEffect(StatEffect pEffect, int nSourceID) {
        switch (nSourceID) {
            case Crossbowman.CROSSBOW_BOOSTER_2:
                pEffect.statups.put(CharacterTemporaryStat.Booster, pEffect.info.get(StatInfo.x));
                break;
            case Crossbowman.CROSSBOW_MASTERY:
                break;
            case Crossbowman.DOUBLE_JUMP_7:
                break;
            case Crossbowman.FINAL_ATTACK_CROSSBOW:
                break;
            case Crossbowman.GOLDEN_EAGLE:
                break;
            case Crossbowman.IRON_ARROW:
                break;
            case Crossbowman.NET_TOSS:
                break;
            case Crossbowman.PHYSICAL_TRAINING_70_7:
                break;
            case Crossbowman.RANGEFINDER:
                break;
            case Crossbowman.SOUL_ARROW_CROSSBOW_1:
                pEffect.statups.put(CharacterTemporaryStat.SoulArrow, pEffect.info.get(StatInfo.x));
                pEffect.statups.put(CharacterTemporaryStat.EPAD, pEffect.info.get(StatInfo.epad));
                pEffect.statups.put(CharacterTemporaryStat.NoBulletConsume, 1);
                break;

        }

    }

    @Override
    public boolean IsCorrectClass(int nClass) {
        return nClass == 320;
    }

}
