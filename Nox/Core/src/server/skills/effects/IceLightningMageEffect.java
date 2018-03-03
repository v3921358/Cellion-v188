package server.skills.effects;

import server.skills.effects.manager.AbstractEffect;
import client.CharacterTemporaryStat;
import client.MapleJob;
import client.MonsterStatus;
import constants.skills.IceLightningMage;
import server.MapleStatEffect;
import server.MapleStatInfo;
import server.skills.effects.manager.Effect;

/**
 *
 * @author Novak
 *
 */
@Effect
public class IceLightningMageEffect extends AbstractEffect {

    @Override
    public void SetEffect(MapleStatEffect pEffect, int nSourceID) {
        switch (nSourceID) {
            case IceLightningMage.ARCANE_OVERDRIVE:
                break;
            case IceLightningMage.ELEMENTAL_ADAPTATION_ICE_LIGHTNING:
                break;
            case IceLightningMage.ELEMENTAL_DECREASE:
                break;
            case IceLightningMage.ELEMENT_AMPLIFICATION:
                break;
            case IceLightningMage.GLACIER_CHAIN:
                break;
            case IceLightningMage.ICE_DEMON:
                break;
            case IceLightningMage.ICE_STRIKE:
                break;
            case IceLightningMage.SEAL:
                break;
            case IceLightningMage.SHATTER:
                break;
            case IceLightningMage.SPELL_BOOSTER_1:
                break;
            case IceLightningMage.STORM_MAGIC:
                break;
            case IceLightningMage.TELEPORT_MASTERY:
                break;
            case IceLightningMage.THUNDERSTORM:
                break;
            case IceLightningMage.THUNDER_SPEAR:
                break;

        }

    }

    @Override
    public boolean IsCorrectClass(int nClass) {
        return nClass == 221;
    }

}
