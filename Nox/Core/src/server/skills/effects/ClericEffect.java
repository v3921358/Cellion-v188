package server.skills.effects;

import server.skills.effects.manager.AbstractEffect;
import client.CharacterTemporaryStat;
import client.MapleJob;
import client.MonsterStatus;
import constants.skills.Cleric;
import server.MapleStatEffect;
import server.MapleStatInfo;
import server.skills.effects.manager.Effect;

/**
 *
 * @author Novak
 *
 */
@Effect
public class ClericEffect extends AbstractEffect {

    @Override
    public void SetEffect(MapleStatEffect pEffect, int nSourceID) {
        switch (nSourceID) {
            case Cleric.BLESS:
                break;
            case Cleric.BLESSED_ENSEMBLE:
                break;
            case Cleric.HEAL:
                break;
            case Cleric.HIGH_WISDOM:
                break;
            case Cleric.HOLY_ARROW:
                break;
            case Cleric.INVINCIBLE:
                break;
            case Cleric.INVINCIBLE_1:
                break;
            case Cleric.MAGIC_BOOSTER:
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
