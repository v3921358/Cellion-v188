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
                pEffect.statups.put(CharacterTemporaryStat.KeyDownAreaMoving, 1);
                pEffect.info.put(MapleStatInfo.time, 2100000000);
                break;
            case FirePoisonMage.ELEMENTAL_DECREASE_2:
                pEffect.statups.put(CharacterTemporaryStat.ElementalReset, pEffect.info.get(MapleStatInfo.x));
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
                pEffect.monsterStatus.put(MonsterStatus.STUN, 1);
                pEffect.statups.put(CharacterTemporaryStat.TeleportMasteryOn, pEffect.info.get(MapleStatInfo.x));
                pEffect.info.put(MapleStatInfo.mpCon, pEffect.info.get(MapleStatInfo.y));
                pEffect.info.put(MapleStatInfo.time, 2100000000);
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
