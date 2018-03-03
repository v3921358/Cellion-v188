package server.skills.effects;

import server.skills.effects.manager.AbstractEffect;
import client.CharacterTemporaryStat;
import client.MapleJob;
import client.MonsterStatus;
import constants.skills.FirePoisonMage;
import server.MapleStatEffect;
import server.MapleStatInfo;
import server.skills.effects.manager.Effect;

/**
 *
 * @author Novak
 *
 */
@Effect
public class FirePoisonMageEffect extends AbstractEffect {

    @Override
    public void SetEffect(MapleStatEffect pEffect, int nSourceID) {
        switch (nSourceID) {
            case FirePoisonMage.ARCANE_OVERDRIVE_2:
                break;
            case FirePoisonMage.BURNING_MAGIC:
                break;
            case FirePoisonMage.ELEMENTAL_ADAPTATION_FIRE_POISON:
                break;
            case FirePoisonMage.ELEMENTAL_DECREASE_2:
                break;
            case FirePoisonMage.ELEMENT_AMPLIFICATION_2:
                break;
            case FirePoisonMage.EXPLOSION:
                break;
            case FirePoisonMage.FIRE_DEMON:
                break;
            case FirePoisonMage.MANA_BURN:
                break;
            case FirePoisonMage.POISON_MIST:
                break;
            case FirePoisonMage.SEAL_2:
                break;
            case FirePoisonMage.SPELL_BOOSTER_2:
                break;
            case FirePoisonMage.TELEPORT_MASTERY_3:
                break;
            case FirePoisonMage.VIRAL_SLIME:
                break;

        }

    }

    @Override
    public boolean IsCorrectClass(int nClass) {
        return nClass == 211;
    }

}
