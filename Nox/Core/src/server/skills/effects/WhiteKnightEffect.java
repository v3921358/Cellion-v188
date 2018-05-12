package server.skills.effects;

import server.skills.effects.manager.AbstractEffect;
import client.CharacterTemporaryStat;
import client.MapleJob;
import client.MonsterStatus;
import constants.skills.WhiteKnight;
import server.StatEffect;
import server.StatInfo;
import server.skills.effects.manager.Effect;

/**
 *
 * @author Novak
 *
 */
@Effect
public class WhiteKnightEffect extends AbstractEffect {

    @Override
    public void SetEffect(StatEffect pEffect, int nSourceID) {
        switch (nSourceID) {
            case WhiteKnight.ACHILLES:
                break;
            case WhiteKnight.BLIZZARD_CHARGE:
                break;
            case WhiteKnight.CHARGED_BLOW:
                break;
            case WhiteKnight.COMBAT_ORDERS:
                pEffect.statups.put(CharacterTemporaryStat.CombatOrders, pEffect.info.get(StatInfo.x));
                break;
            case WhiteKnight.DIVINE_SHIELD:
                break;
            case WhiteKnight.FLAME_CHARGE:
                break;
            case WhiteKnight.HP_RECOVERY:
                pEffect.statups.put(CharacterTemporaryStat.Regen, 1);
                break;
            case WhiteKnight.LIGHTNING_CHARGE_1:
                pEffect.statups.put(CharacterTemporaryStat.WeaponCharge, pEffect.info.get(StatInfo.x));
                pEffect.statups.put(CharacterTemporaryStat.DamR, pEffect.info.get(StatInfo.z));
                break;
            case WhiteKnight.MAGIC_CRASH_3:
                break;
            case WhiteKnight.PARASHOCK_GUARD:
                pEffect.statups.put(CharacterTemporaryStat.KnightsAura, pEffect.info.get(StatInfo.x) / 2);
                pEffect.statups.put(CharacterTemporaryStat.IndiePAD, pEffect.info.get(StatInfo.indiePad));
                break;
            case WhiteKnight.RUSH_3:
                break;
            case WhiteKnight.SHIELD_MASTERY:
                break;
            case WhiteKnight.THREATEN:
                pEffect.monsterStatus.put(MonsterStatus.WATK, pEffect.info.get(StatInfo.x));
                pEffect.monsterStatus.put(MonsterStatus.PDD, pEffect.info.get(StatInfo.x));
                pEffect.monsterStatus.put(MonsterStatus.MATK, pEffect.info.get(StatInfo.x));
                pEffect.monsterStatus.put(MonsterStatus.MDD, pEffect.info.get(StatInfo.x));
                pEffect.monsterStatus.put(MonsterStatus.EVA, pEffect.info.get(StatInfo.z));
                break;

        }

    }

    @Override
    public boolean IsCorrectClass(int nClass) {
        return nClass == 121;
    }

}
