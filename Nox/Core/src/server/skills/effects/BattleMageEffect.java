package server.skills.effects;

import server.skills.effects.manager.AbstractEffect;
import client.CharacterTemporaryStat;
import client.MapleJob;
import client.MonsterStatus;
import constants.skills.BattleMage;
import server.MapleStatEffect;
import server.MapleStatInfo;
import server.skills.effects.manager.Effect;

/**
 *
 * @author Novak
 *
 */
@Effect
public class BattleMageEffect extends AbstractEffect {

    @Override
    public void SetEffect(MapleStatEffect pEffect, int nSourceID) {
        switch (nSourceID) {
            case BattleMage.COMBAT_TELEPORT:
                break;
            case BattleMage.CONDEMNATION:
                break;
            case BattleMage.DARK_AURA_1:
                break;
            case BattleMage.ELEMENTAL_WEAKNESS_1:
                break;
            case BattleMage.GODDESS_GUARD_10:
                break;
            case BattleMage.GODDESS_GUARD_5:
                break;
            case BattleMage.HASTY_AURA:
                break;
            case BattleMage.STAFF_ARTIST:
                break;
            case BattleMage.THE_FINISHER:
                break;
            case BattleMage.THE_FINISHER_1:
                break;
            case BattleMage.THE_FINISHER_2:
                break;
            case BattleMage.THE_FINISHER_3:
                break;
            case BattleMage.THE_FINISHER_4:
                break;
            case BattleMage.THE_FINISHER_5:
                break;
            case BattleMage.TRIPLE_BLOW:
                break;
            case BattleMage.BLOOD_DRAIN:
                break;
            case BattleMage.BLUE_AURA_1:
                break;
            case BattleMage.DARK_CHAIN:
                break;
            case BattleMage.DRAINING_AURA:
                break;
            case BattleMage.GRIM_CONTRACT:
                break;
            case BattleMage.HIGH_WISDOM_6:
                break;
            case BattleMage.ORDINARY_CONVERSION:
                break;
            case BattleMage.QUAD_BLOW:
                break;
            case BattleMage.STAFF_BOOST:
                break;
            case BattleMage.STAFF_MASTERY:
                break;
            case BattleMage.YELLOW_AURA:
                break;
            case BattleMage.ADVANCED_BLUE_AURA_1:
                break;
            case BattleMage.ADVANCED_DARK_CHAIN:
                break;
            case BattleMage.BATTLE_BURST:
                break;
            case BattleMage.BATTLE_MASTERY:
                break;
            case BattleMage.BLUE_AURA:
                break;
            case BattleMage.BODY_BOOST:
                break;
            case BattleMage.CONVERSION:
                break;
            case BattleMage.DARK_CONDITIONING:
                break;
            case BattleMage.DARK_SHOCK:
                break;
            case BattleMage.DARK_SHOCK_1:
                break;
            case BattleMage.DARK_SHOCK_2:
                break;
            case BattleMage.GRIM_CONTRACT_II:
                break;
            case BattleMage.POWER_STANCE_4:
                break;
            case BattleMage.QUINTUPLE_BLOW:
                break;
            case BattleMage.STANCE_1:
                break;
            case BattleMage.SUMMON_REAPER_BUFF:
                break;
            case BattleMage.TELEPORT_MASTERY_4:
                break;
            case BattleMage.ADVANCED_BLUE_AURA:
                break;
            case BattleMage.ADVANCED_DARK_AURA:
                break;
            case BattleMage.ADVANCED_DARK_AURA_1:
                break;
            case BattleMage.ADVANCED_YELLOW_AURA:
                break;
            case BattleMage.ADVANCED_YELLOW_AURA_1:
                break;
            case BattleMage.BATTLE_RAGE:
                break;
            case BattleMage.BLUE_AURA_DISPEL_MAGIC:
                break;
            case BattleMage.DARK_AURA:
                break;
            case BattleMage.DARK_AURA_BOSS_RUSH:
                break;
            case BattleMage.DARK_GENESIS:
                break;
            case BattleMage.DARK_GENESIS_1:
                break;
            case BattleMage.DARK_GENESIS_ADDITIONAL_REINFORCE:
                break;
            case BattleMage.DARK_GENESIS_COOLDOWN_CUTTER:
                break;
            case BattleMage.DARK_GENESIS_REINFORCE:
                break;
            case BattleMage.DARK_SHOCK_COOLDOWN_CUTTER:
                break;
            case BattleMage.DARK_SHOCK_REINFORCE:
                break;
            case BattleMage.DARK_SHOCK_SPLITTER:
                break;
            case BattleMage.ENERGIZE:
                break;
            case BattleMage.FINISHING_BLOW:
                break;
            case BattleMage.FOR_LIBERTY:
                break;
            case BattleMage.GRAND_LIGHT_AURA:
                break;
            case BattleMage.GRIM_CONTRACT_III:
                break;
            case BattleMage.HEROS_WILL_6:
                break;
            case BattleMage.HYPER_ACCURACY:
                break;
            case BattleMage.HYPER_CRITICAL:
                break;
            case BattleMage.HYPER_DEFENSE:
                break;
            case BattleMage.HYPER_DEXTERITY:
                break;
            case BattleMage.HYPER_FURY:
                break;
            case BattleMage.HYPER_HEALTH:
                break;
            case BattleMage.HYPER_INTELLIGENCE:
                break;
            case BattleMage.HYPER_JUMP:
                break;
            case BattleMage.HYPER_LUCK:
                break;
            case BattleMage.HYPER_MAGIC_DEFENSE:
                break;
            case BattleMage.HYPER_MANA:
                break;
            case BattleMage.HYPER_SPEED:
                break;
            case BattleMage.HYPER_STRENGTH:
                break;
            case BattleMage.MAPLE_WARRIOR_6:
                break;
            case BattleMage.MASTER_OF_DEATH:
                break;
            case BattleMage.PARTY_SHIELD:
                break;
            case BattleMage.PARTY_SHIELD_COOLDOWN_CUTTER:
                break;
            case BattleMage.PARTY_SHIELD_ENHANCE:
                break;
            case BattleMage.PARTY_SHIELD_PERSIST:
                break;
            case BattleMage.SPELL_BOOST:
                break;
            case BattleMage.STAFF_EXPERT:
                break;
            case BattleMage.SWEEPING_STAFF:
                break;
            case BattleMage.SWEEPING_STAFF_1:
                break;
            case BattleMage.SWEEPING_STAFF_2:
                break;
            case BattleMage.TELEPORT_MASTERY_ENHANCE:
                break;
            case BattleMage.TELEPORT_MASTERY_RANGE:
                break;
            case BattleMage.TELEPORT_MASTERY_REINFORCE:
                break;
            case BattleMage.TWISTER_SPIN:
                break;
            case BattleMage.TWISTER_SPIN_COOLDOWN_CUTTER:
                break;
            case BattleMage.TWISTER_SPIN_REINFORCE:
                break;
            case BattleMage.TWISTER_SPIN_SPREAD:
                break;
            case BattleMage.UNIFICATION_AURA:
                break;
            case BattleMage.WEAKENING_AURA:
                break;
            case BattleMage.WEAKENING_AURA_ELEMENTAL_DECREASE:
                break;

        }

    }

    @Override
    public boolean IsCorrectClass(int nClass) {
        return nClass == 3200 || nClass == 3210 || nClass == 3211 || nClass == 3212;
    }

}
