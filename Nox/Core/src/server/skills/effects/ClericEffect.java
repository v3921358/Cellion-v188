package server.skills.effects;

import server.skills.effects.manager.AbstractEffect;
import client.CharacterTemporaryStat;
import client.MapleJob;
import client.MonsterStatus;
import constants.skills.Cleric;
import server.StatEffect;
import server.StatInfo;
import server.skills.effects.manager.Effect;

/**
 *
 * @author Novak
 *
 */
@Effect
public class ClericEffect extends AbstractEffect {

    @Override
    public void SetEffect(StatEffect pEffect, int nSourceID) {
        switch (nSourceID) {
            case Cleric.BLESS:
                pEffect.statups.put(CharacterTemporaryStat.Bless, pEffect.info.get(StatInfo.x));
                break;
            case Cleric.BLESSED_ENSEMBLE:
                pEffect.statups.put(CharacterTemporaryStat.BlessEnsenble, pEffect.info.get(StatInfo.x));
                break;
            case Cleric.HEAL:
                break;
            case Cleric.HIGH_WISDOM:
                break;
            case Cleric.HOLY_ARROW:
                break;
            case Cleric.INVINCIBLE:
                pEffect.statups.put(CharacterTemporaryStat.Invincible, pEffect.info.get(StatInfo.x));
                break;
            case Cleric.INVINCIBLE_1:
                break;
            case Cleric.MAGIC_BOOSTER:
                pEffect.statups.put(CharacterTemporaryStat.Booster, pEffect.info.get(StatInfo.x));
                break;
            case Cleric.MP_EATER:
                break;
            case Cleric.SPELL_MASTERY:
                break;
            case Cleric.TELEPORT_3:
                break;

        }

    }

    @Override
    public boolean IsCorrectClass(int nClass) {
        return nClass == 230;
    }

}
