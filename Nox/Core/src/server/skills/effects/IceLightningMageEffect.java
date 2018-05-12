package server.skills.effects;

import server.skills.effects.manager.AbstractEffect;
import client.CharacterTemporaryStat;
import client.MapleJob;
import client.MonsterStatus;
import constants.skills.IceLightningMage;
import server.StatEffect;
import server.StatInfo;
import server.skills.effects.manager.Effect;

/**
 *
 * @author Novak
 *
 */
@Effect
public class IceLightningMageEffect extends AbstractEffect {

    @Override
    public void SetEffect(StatEffect pEffect, int nSourceID) {
        switch (nSourceID) {
            case IceLightningMage.ARCANE_OVERDRIVE:
                break;
            case IceLightningMage.ELEMENTAL_ADAPTATION_ICE_LIGHTNING:
                pEffect.statups.put(CharacterTemporaryStat.KeyDownAreaMoving, pEffect.info.get(StatInfo.x));
                break;
            case IceLightningMage.ELEMENTAL_DECREASE:
                pEffect.statups.put(CharacterTemporaryStat.ElementalReset, pEffect.info.get(StatInfo.x));
                break;
            case IceLightningMage.ELEMENT_AMPLIFICATION:
                break;
            case IceLightningMage.GLACIER_CHAIN:
                break;
            case IceLightningMage.ICE_DEMON:
                break;
            case IceLightningMage.ICE_STRIKE:
                pEffect.monsterStatus.put(MonsterStatus.FREEZE, 1);
                pEffect.info.put(StatInfo.time, pEffect.info.get(StatInfo.time) * 2);
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
                pEffect.monsterStatus.put(MonsterStatus.STUN, 1);
                pEffect.statups.put(CharacterTemporaryStat.TeleportMasteryOn, pEffect.info.get(StatInfo.x));
                pEffect.info.put(StatInfo.mpCon, pEffect.info.get(StatInfo.y));
                pEffect.info.put(StatInfo.time, 2100000000);
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
