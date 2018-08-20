package server.skills.effects;

import server.skills.effects.manager.AbstractEffect;
import client.CharacterTemporaryStat;
import client.Jobs;
import client.MonsterStatus;
import constants.skills.Priest;
import server.StatEffect;
import enums.StatInfo;
import server.skills.effects.manager.Effect;

/**
 *
 * @author Novak
 *
 */
@Effect
public class PriestEffect extends AbstractEffect {

    @Override
    public void SetEffect(StatEffect pEffect, int nSourceID) {
        switch (nSourceID) {
            case Priest.ARCANE_OVERDRIVE_1:
                break;
            case Priest.DISPEL:
                break;
            case Priest.DIVINE_PROTECTION:
                pEffect.statups.put(CharacterTemporaryStat.KeyDownAreaMoving, 1);
                pEffect.info.put(StatInfo.time, 2100000000);
                break;
            case Priest.DOOM:
                break;
            case Priest.HOLY_FOCUS:
                break;
            case Priest.HOLY_FOUNTAIN:
                break;
            case Priest.HOLY_MAGIC_SHELL:
                pEffect.statups.put(CharacterTemporaryStat.HolyMagicShell, pEffect.info.get(StatInfo.x));
                pEffect.info.put(StatInfo.cooltime, pEffect.info.get(StatInfo.y));
                pEffect.setHpR(pEffect.info.get(StatInfo.z) / 100.0);
                break;
            case Priest.HOLY_SYMBOL_1:
                pEffect.statups.put(CharacterTemporaryStat.HolySymbol, pEffect.info.get(StatInfo.x));
                break;
            case Priest.MAGIC_BOOSTER_1:
                pEffect.statups.put(CharacterTemporaryStat.Booster, pEffect.info.get(StatInfo.x));
                break;
            case Priest.MYSTIC_DOOR:
                break;
            case Priest.SHINING_RAY:
                pEffect.monsterStatus.put(MonsterStatus.STUN, 1);
                break;
            case Priest.TELEPORT_MASTERY_2:
                pEffect.monsterStatus.put(MonsterStatus.STUN, 1);
                pEffect.statups.put(CharacterTemporaryStat.TeleportMasteryOn, pEffect.info.get(StatInfo.x));
                pEffect.info.put(StatInfo.mpCon, pEffect.info.get(StatInfo.y));
                pEffect.info.put(StatInfo.time, 2100000000);
                break;

        }

    }

    @Override
    public boolean IsCorrectClass(int nClass) {
        return nClass == 231;
    }

}
