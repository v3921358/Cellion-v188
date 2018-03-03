package server.skills.effects;

import server.skills.effects.manager.AbstractEffect;
import client.CharacterTemporaryStat;
import client.MapleJob;
import client.MonsterStatus;
import constants.skills.ChiefBandit;
import server.MapleStatEffect;
import server.MapleStatInfo;
import server.skills.effects.manager.Effect;

/**
 *
 * @author Novak
 *
 */
@Effect
public class ChiefBanditEffect extends AbstractEffect {

    @Override
    public void SetEffect(MapleStatEffect pEffect, int nSourceID) {
        switch (nSourceID) {
            case ChiefBandit.ADVANCED_DARK_SIGHT:
                break;
            case ChiefBandit.BAND_OF_THIEVES:
                break;
            case ChiefBandit.CHAKRA:
                break;
            case ChiefBandit.DARK_FLARE:
                break;
            case ChiefBandit.ENVELOPING_DARKNESS_3:
                break;
            case ChiefBandit.INTO_DARKNESS:
                break;
            case ChiefBandit.MESO_EXPLOSION:
                break;
            case ChiefBandit.MESO_EXPLOSION_1:
                break;
            case ChiefBandit.MESO_GUARD:
                break;
            case ChiefBandit.MESO_MASTERY_1:
                break;
            case ChiefBandit.MIDNIGHT_CARNIVAL:
                break;
            case ChiefBandit.PHASE_DASH:
                break;
            case ChiefBandit.PICK_POCKET:
                break;
            case ChiefBandit.SHADOW_PARTNER:
                break;
            case ChiefBandit.SHIELD_MASTERY_2:
                break;
            case ChiefBandit.VENOM_4:
                break;

        }

    }

    @Override
    public boolean IsCorrectClass(int nClass) {
        return nClass == 421;
    }

}
