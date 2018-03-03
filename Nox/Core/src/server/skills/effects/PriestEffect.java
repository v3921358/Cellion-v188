package server.skills.effects;

import server.skills.effects.manager.AbstractEffect;
import client.CharacterTemporaryStat;
import client.MapleJob;
import client.MonsterStatus;
import constants.skills.Priest;
import server.MapleStatEffect;
import server.MapleStatInfo;
import server.skills.effects.manager.Effect;

/**
 *
 * @author Novak
 *
 */
@Effect
public class PriestEffect extends AbstractEffect {

    @Override
    public void SetEffect(MapleStatEffect pEffect, int nSourceID) {
        switch (nSourceID) {
            case Priest.ARCANE_OVERDRIVE_1:
                break;
            case Priest.DISPEL:
                break;
            case Priest.DIVINE_PROTECTION:
                break;
            case Priest.DOOM:
                break;
            case Priest.HOLY_FOCUS:
                break;
            case Priest.HOLY_FOUNTAIN:
                break;
            case Priest.HOLY_MAGIC_SHELL:
                break;
            case Priest.HOLY_SYMBOL_1:
                break;
            case Priest.MAGIC_BOOSTER_1:
                break;
            case Priest.MYSTIC_DOOR:
                break;
            case Priest.SHINING_RAY:
                break;
            case Priest.TELEPORT_MASTERY_2:
                break;

        }

    }

    @Override
    public boolean IsCorrectClass(int nClass) {
        return nClass == 231;
    }

}
