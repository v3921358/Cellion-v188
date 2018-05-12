package server.skills.effects;

import server.skills.effects.manager.AbstractEffect;
import client.CharacterTemporaryStat;
import client.MapleJob;
import client.MonsterStatus;
import constants.skills.Paladin;
import server.StatEffect;
import server.StatInfo;
import server.skills.effects.manager.Effect;

/**
 *
 * @author Novak
 *
 */
@Effect
public class PaladinEffect extends AbstractEffect {

    @Override
    public void SetEffect(StatEffect pEffect, int nSourceID) {
        switch (nSourceID) {
            case Paladin.ACHILLES_1:
                break;
            case Paladin.ADVANCED_CHARGE:
                break;
            case Paladin.BLAST:
                break;
            case Paladin.BLAST_CRITICAL_CHANCE:
                break;
            case Paladin.BLAST_EXTRA_STRIKE:
                break;
            case Paladin.BLAST_REINFORCE:
                break;
            case Paladin.DIVINE_CHARGE:
                pEffect.statups.put(CharacterTemporaryStat.WeaponCharge, pEffect.info.get(StatInfo.x));
                pEffect.statups.put(CharacterTemporaryStat.DamR, pEffect.info.get(StatInfo.z));
                break;
            case Paladin.DIVINE_SHIELD_2:
                break;
            case Paladin.ELEMENTAL_FORCE:
                pEffect.statups.put(CharacterTemporaryStat.IndieDamR, pEffect.info.get(StatInfo.indieDamR));
                pEffect.statups.put(CharacterTemporaryStat.ElementalReset, pEffect.info.get(StatInfo.x));
                break;
            case Paladin.EPIC_ADVENTURE_1:
                pEffect.statups.put(CharacterTemporaryStat.DamR, pEffect.info.get(StatInfo.indieDamR));
                break;
            case Paladin.GUARDIAN:
                break;
            case Paladin.GUARDIAN_1:
                break;
            case Paladin.HEAVENS_HAMMER:
                break;
            case Paladin.HEAVENS_HAMMER_COOLDOWN_CUTTER:
                break;
            case Paladin.HEAVENS_HAMMER_EXTRA_STRIKE:
                break;
            case Paladin.HEAVENS_HAMMER_REINFORCE:
                break;
            case Paladin.HEROS_WILL_3:
                break;
            case Paladin.HIGH_PALADIN:
                break;
            case Paladin.HYPER_ACCURACY_900_90_9:
                break;
            case Paladin.HYPER_CRITICAL_900_90_9:
                break;
            case Paladin.HYPER_DEFENSE_400_40_4:
                break;
            case Paladin.HYPER_DEXTERITY_900_90_9:
                break;
            case Paladin.HYPER_FURY_900_90_9:
                break;
            case Paladin.HYPER_HEALTH_900_90_9:
                break;
            case Paladin.HYPER_INTELLIGENCE_900_90_9:
                break;
            case Paladin.HYPER_JUMP_1000_100_10:
                break;
            case Paladin.HYPER_LUCK_900_90_9:
                break;
            case Paladin.HYPER_MAGIC_DEFENSE_1000_100_10:
                break;
            case Paladin.HYPER_MANA_900_90_9:
                break;
            case Paladin.HYPER_SPEED_1000_100_10:
                break;
            case Paladin.HYPER_STRENGTH_900_90_9:
                break;
            case Paladin.MAGIC_CRASH:
                break;
            case Paladin.MAPLE_WARRIOR_4:
                pEffect.statups.put(CharacterTemporaryStat.BasicStatUp, pEffect.info.get(StatInfo.x));
                break;
            case Paladin.MONSTER_MAGNET:
                break;
            case Paladin.POWER_STANCE_1:
                break;
            case Paladin.POWER_STANCE_7:
                break;
            case Paladin.RUSH:
                break;
            case Paladin.SACROSANCTITY:
                pEffect.statups.put(CharacterTemporaryStat.NotDamaged, pEffect.info.get(StatInfo.x));
                break;
            case Paladin.SMITE_SHIELD:
                break;
            case Paladin.THREATEN_ENHANCE:
                break;
            case Paladin.THREATEN_OPPORTUNITY:
                break;
            case Paladin.THREATEN_PERSIST:
                break;

        }

    }

    @Override
    public boolean IsCorrectClass(int nClass) {
        return nClass == 122;
    }

}
