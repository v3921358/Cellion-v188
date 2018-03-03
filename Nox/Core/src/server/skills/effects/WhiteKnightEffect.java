package server.skills.effects;

import server.skills.effects.manager.AbstractEffect;
import client.CharacterTemporaryStat;
import client.MapleJob;
import client.MonsterStatus;
import constants.skills.WhiteKnight;
import server.MapleStatEffect;
import server.MapleStatInfo;
import server.skills.effects.manager.Effect;

/**
 *
 * @author Novak
 *
 */
@Effect
public class WhiteKnightEffect extends AbstractEffect {

    @Override
    public void SetEffect(MapleStatEffect pEffect, int nSourceID) {
        switch (nSourceID) {
            case WhiteKnight.ACHILLES:
                break;
            case WhiteKnight.BLIZZARD_CHARGE:
                break;
            case WhiteKnight.CHARGED_BLOW:
                break;
            case WhiteKnight.COMBAT_ORDERS:
                break;
            case WhiteKnight.DIVINE_SHIELD:
                break;
            case WhiteKnight.FLAME_CHARGE:
                break;
            case WhiteKnight.HP_RECOVERY:
                break;
            case WhiteKnight.LIGHTNING_CHARGE_1:
                break;
            case WhiteKnight.MAGIC_CRASH_3:
                break;
            case WhiteKnight.PARASHOCK_GUARD:
                break;
            case WhiteKnight.RUSH_3:
                break;
            case WhiteKnight.SHIELD_MASTERY:
                break;
            case WhiteKnight.THREATEN:
                break;

        }

    }

    @Override
    public boolean IsCorrectClass(int nClass) {
        return nClass == 121;
    }

}
