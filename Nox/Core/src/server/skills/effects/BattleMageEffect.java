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
                pEffect.statups.put(CharacterTemporaryStat.BMageDeath, 1);
                pEffect.info.put(MapleStatInfo.time, 2100000000);
                break;
            case BattleMage.DARK_AURA_1:
                pEffect.statups.put(CharacterTemporaryStat.BMageAura, (int) pEffect.getLevel());
                pEffect.statups.put(CharacterTemporaryStat.IndieDamR, pEffect.info.get(MapleStatInfo.indieDamR));
                pEffect.info.put(MapleStatInfo.time, 2100000000);
                break;
            case BattleMage.ELEMENTAL_WEAKNESS_1:
                break;
            case BattleMage.GODDESS_GUARD_10:
                break;
            case BattleMage.GODDESS_GUARD_5:
                break;
            case BattleMage.HASTY_AURA:
                pEffect.statups.put(CharacterTemporaryStat.BMageAura, (int) pEffect.getLevel());
                pEffect.statups.put(CharacterTemporaryStat.Speed, pEffect.info.get(MapleStatInfo.indieSpeed));
                pEffect.statups.put(CharacterTemporaryStat.IndieBooster, pEffect.info.get(MapleStatInfo.x));
                pEffect.info.put(MapleStatInfo.time, 2100000000);
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
                pEffect.statups.put(CharacterTemporaryStat.BMageAura, (int) pEffect.getLevel());
                pEffect.statups.put(CharacterTemporaryStat.ComboDrain, 2);
                pEffect.statups.put(CharacterTemporaryStat.Regen, 2);
                pEffect.info.put(MapleStatInfo.time, 2100000000);
                break;
            case BattleMage.GRIM_CONTRACT:
                pEffect.statups.put(CharacterTemporaryStat.BMageDeath, 1);
                pEffect.info.put(MapleStatInfo.time, 2100000000);
                break;
            case BattleMage.HIGH_WISDOM_6:
                break;
            case BattleMage.ORDINARY_CONVERSION:
                break;
            case BattleMage.QUAD_BLOW:
                break;
            case BattleMage.STAFF_BOOST:
                pEffect.statups.put(CharacterTemporaryStat.Booster, pEffect.info.get(MapleStatInfo.x));
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
                pEffect.statups.put(CharacterTemporaryStat.BMageAura, (int) pEffect.getLevel());
                pEffect.statups.put(CharacterTemporaryStat.AsrR, pEffect.info.get(MapleStatInfo.asrR));
                pEffect.statups.put(CharacterTemporaryStat.TerR, pEffect.info.get(MapleStatInfo.terR));
                pEffect.statups.put(CharacterTemporaryStat.IgnoreMobDamR, pEffect.info.get(MapleStatInfo.y));
                pEffect.info.put(MapleStatInfo.time, 2100000000);
                break;
            case BattleMage.BODY_BOOST:
                break;
            case BattleMage.CONVERSION:
                break;
            case BattleMage.DARK_CONDITIONING:
                break;
            case BattleMage.DARK_SHOCK:
                pEffect.statups.put(CharacterTemporaryStat.TeleportMasteryOn, 1);
                break;
            case BattleMage.DARK_SHOCK_1:
                break;
            case BattleMage.DARK_SHOCK_2:
                break;
            case BattleMage.GRIM_CONTRACT_II:
                pEffect.statups.put(CharacterTemporaryStat.BMageDeath, 1);
                pEffect.info.put(MapleStatInfo.time, 2100000000);
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
                pEffect.statups.put(CharacterTemporaryStat.Enrage, 1);
                pEffect.statups.put(CharacterTemporaryStat.DamR, pEffect.info.get(MapleStatInfo.x));
                pEffect.statups.put(CharacterTemporaryStat.CriticalBuff, pEffect.info.get(MapleStatInfo.z));
                pEffect.info.put(MapleStatInfo.time, 2100000000);
                break;
            case BattleMage.BLUE_AURA_DISPEL_MAGIC:
                break;
            case BattleMage.DARK_AURA:
                pEffect.statups.put(CharacterTemporaryStat.BMageAura, (int) pEffect.getLevel());
                pEffect.statups.put(CharacterTemporaryStat.IndieDamR, pEffect.info.get(MapleStatInfo.indieDamR));
                pEffect.info.put(MapleStatInfo.time, 2100000000);
                break;
            case BattleMage.DARK_AURA_BOSS_RUSH:
                break;
            case BattleMage.DARK_GENESIS:
                pEffect.monsterStatus.put(MonsterStatus.STUN, 1);
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
                pEffect.statups.put(CharacterTemporaryStat.DamR, pEffect.info.get(MapleStatInfo.indieDamR));
                break;
            case BattleMage.GRAND_LIGHT_AURA:
                break;
            case BattleMage.GRIM_CONTRACT_III:
                pEffect.statups.put(CharacterTemporaryStat.BMageDeath, 1);
                pEffect.info.put(MapleStatInfo.time, 2100000000);
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
                pEffect.statups.put(CharacterTemporaryStat.IndieStatR, pEffect.info.get(MapleStatInfo.x));
                break;
            case BattleMage.MASTER_OF_DEATH:
                pEffect.statups.put(CharacterTemporaryStat.AttackCountX, 2);
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
                pEffect.statups.put(CharacterTemporaryStat.BMageAura, pEffect.info.get(MapleStatInfo.x));
                break;
            case BattleMage.WEAKENING_AURA:
                pEffect.statups.put(CharacterTemporaryStat.BMageAura, (int) pEffect.getLevel());
                pEffect.info.put(MapleStatInfo.time, 2100000000);
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
