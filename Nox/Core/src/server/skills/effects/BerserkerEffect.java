package server.skills.effects;

import server.skills.effects.manager.AbstractEffect;
import client.CharacterTemporaryStat;
import client.MapleJob;
import client.MonsterStatus;
import constants.skills.Berserker;
import server.MapleStatEffect;
import server.MapleStatInfo;
import server.skills.effects.manager.Effect;

/**
 *
 * @author Novak
 *
 */
@Effect
public class BerserkerEffect extends AbstractEffect {

    @Override
    public void SetEffect(MapleStatEffect pEffect, int nSourceID) {
        switch (nSourceID) {
            case Berserker.CROSS_SURGE:
                break;
            case Berserker.DRAGON_BUSTER:
                break;
            case Berserker.DRAGON_FURY:
                break;
            case Berserker.DRAGON_ROAR:
                break;
            case Berserker.DRAGON_STRENGTH:
                break;
            case Berserker.ELEMENTAL_RESISTANCE:
                break;
            case Berserker.ENDURE_1:
                break;
            case Berserker.EVIL_EYE_OF_DOMINATION:
                break;
            case Berserker.EVIL_EYE_SHOCK:
                break;
            case Berserker.HEX_OF_THE_EVIL_EYE_1:
                break;
            case Berserker.LA_MANCHA_SPEAR:
                break;
            case Berserker.LORD_OF_DARKNESS:
                break;
            case Berserker.MAGIC_CRASH_1:
                break;
            case Berserker.RUSH_1:
                break;
            case Berserker.SACRIFICE:
                break;

        }

    }

    @Override
    public boolean IsCorrectClass(int nClass) {
        return nClass == 131;
    }

}
