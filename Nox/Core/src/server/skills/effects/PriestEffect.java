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
                pEffect.statups.put(CharacterTemporaryStat.KeyDownAreaMoving, 1);
                pEffect.info.put(MapleStatInfo.time, 2100000000);
                break;
            case Priest.DOOM:
                break;
            case Priest.HOLY_FOCUS:
                break;
            case Priest.HOLY_FOUNTAIN:
                break;
            case Priest.HOLY_MAGIC_SHELL:
                pEffect.statups.put(CharacterTemporaryStat.HolyMagicShell, pEffect.info.get(MapleStatInfo.x));
                pEffect.info.put(MapleStatInfo.cooltime, pEffect.info.get(MapleStatInfo.y));
                pEffect.setHpR(pEffect.info.get(MapleStatInfo.z) / 100.0);
                break;
            case Priest.HOLY_SYMBOL_1:
                pEffect.statups.put(CharacterTemporaryStat.HolySymbol, pEffect.info.get(MapleStatInfo.x));
                break;
            case Priest.MAGIC_BOOSTER_1:
                break;
            case Priest.MYSTIC_DOOR:
                break;
            case Priest.SHINING_RAY:
                pEffect.monsterStatus.put(MonsterStatus.STUN, 1);
                break;
            case Priest.TELEPORT_MASTERY_2:
                pEffect.monsterStatus.put(MonsterStatus.STUN, 1);
                pEffect.statups.put(CharacterTemporaryStat.TeleportMasteryOn, pEffect.info.get(MapleStatInfo.x));
                pEffect.info.put(MapleStatInfo.mpCon, pEffect.info.get(MapleStatInfo.y));
                pEffect.info.put(MapleStatInfo.time, 2100000000);
                break;

        }

    }

    @Override
    public boolean IsCorrectClass(int nClass) {
        return nClass == 231;
    }

}
