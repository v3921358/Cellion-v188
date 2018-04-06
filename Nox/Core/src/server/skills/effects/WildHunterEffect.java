package server.skills.effects;

import server.skills.effects.manager.AbstractEffect;
import client.CharacterTemporaryStat;
import client.MapleJob;
import client.MonsterStatus;
import constants.skills.WildHunter;
import server.MapleStatEffect;
import server.MapleStatInfo;
import server.skills.effects.manager.Effect;

/**
 *
 * @author Novak
 *
 */
@Effect
public class WildHunterEffect extends AbstractEffect {

    @Override
    public void SetEffect(MapleStatEffect pEffect, int nSourceID) {
        switch (nSourceID) {
            case WildHunter.ANOTHER_BITE:
                break;
            case WildHunter.CROSSBOW_BOOSTER_1:
                break;
            case WildHunter.DOUBLE_JUMP_6:
                break;
            case WildHunter.DOUBLE_SHOT_3:
                break;
            case WildHunter.DOUBLE_SHOT_4:
                break;
            case WildHunter.GODDESS_GUARD_100_10:
                break;
            case WildHunter.GODDESS_GUARD_8:
                break;
            case WildHunter.GRAVIBOOTS:
                break;
            case WildHunter.GRAVIBOOTS_1:
                break;
            case WildHunter.JAGUAR_MANAGEMENT:
                break;
            case WildHunter.JAGUAR_RIDER:
                pEffect.statups.put(CharacterTemporaryStat.RideVehicle, 1932015); // ps: u dont always use this jag lulz
                pEffect.statups.put(CharacterTemporaryStat.JaguarCount, 1);
                pEffect.statups.put(CharacterTemporaryStat.JaguarSummoned, 1);
                pEffect.info.put(MapleStatInfo.time, 2100000000);
                break;
            case WildHunter.NATURES_BALANCE:
                break;
            case WildHunter.NATURES_WRATH:
                break;
            case WildHunter.RESISTANCE_AUTO_CRANK:
                break;
            case WildHunter.SUMMON_JAGUAR:
            case WildHunter.SUMMON_JAGUAR_1:
            case WildHunter.SUMMON_JAGUAR_2:
            case WildHunter.SUMMON_JAGUAR_3:
            case WildHunter.SUMMON_JAGUAR_4:
            case WildHunter.SUMMON_JAGUAR_5:
            case WildHunter.SUMMON_JAGUAR_6:
            case WildHunter.SUMMON_JAGUAR_7:
            case WildHunter.SUMMON_JAGUAR_8:
            case WildHunter.SUMMON_JAGUAR_9:
            case WildHunter.SUMMON_JAGUAR_10:
                pEffect.statups.put(CharacterTemporaryStat.SUMMON, 3870); //Fake flag for summons
                pEffect.statups.put(CharacterTemporaryStat.JaguarSummoned, pEffect.info.get(MapleStatInfo.x));
                pEffect.info.put(MapleStatInfo.time, 2100000000);
                break;
            case WildHunter.SWIPE:
                break;
            case WildHunter.SWIPE_1:
                break;
            case WildHunter.TRIPLE_SHOT:
                break;
            case WildHunter.WILD_LURE:
                break;
            case WildHunter.CALL_OF_THE_WILD:
                pEffect.statups.put(CharacterTemporaryStat.HowlingDefence, pEffect.info.get(MapleStatInfo.x));
                pEffect.statups.put(CharacterTemporaryStat.HowlingEvasion, pEffect.info.get(MapleStatInfo.x));
                pEffect.statups.put(CharacterTemporaryStat.HowlingMaxMP, pEffect.info.get(MapleStatInfo.x));
                pEffect.statups.put(CharacterTemporaryStat.HowlingCritical, pEffect.info.get(MapleStatInfo.z));
                pEffect.statups.put(CharacterTemporaryStat.HowlingAttackDamage, pEffect.info.get(MapleStatInfo.z));
                break;
            case WildHunter.CROSSBOW_BOOSTER:
                pEffect.statups.put(CharacterTemporaryStat.Booster, pEffect.info.get(MapleStatInfo.x) * 2);
                break;
            case WildHunter.CROSSBOW_MASTERY_1:
                break;
            case WildHunter.DASH_N_SLASH_1:
                break;
            case WildHunter.DASH_N_SLASH_2:
                break;
            case WildHunter.DASH_N_SLASH_3:
                break;
            case WildHunter.FINAL_ATTACK_7:
                break;
            case WildHunter.ITS_RAINING_MINES:
                break;
            case WildHunter.ITS_RAINING_MINES_1:
                break;
            case WildHunter.JAGUAROSHI:
                break;
            case WildHunter.JAGUAROSHI_1:
                break;
            case WildHunter.JAGUAR_MASTERY:
                break;
            case WildHunter.JAGUAR_RAWR:
                break;
            case WildHunter.PHYSICAL_TRAINING_100_10_1:
                break;
            case WildHunter.RICOCHET:
                break;
            case WildHunter.SILVER_HAWK_1:
                break;
            case WildHunter.SOUL_ARROW_CROSSBOW:
                pEffect.statups.put(CharacterTemporaryStat.SoulArrow, pEffect.info.get(MapleStatInfo.x));
                pEffect.statups.put(CharacterTemporaryStat.PAD, pEffect.info.get(MapleStatInfo.pad));
                pEffect.statups.put(CharacterTemporaryStat.EPAD, pEffect.info.get(MapleStatInfo.epad));
                pEffect.statups.put(CharacterTemporaryStat.NoBulletConsume, 1);
                break;
            case WildHunter.TRIPLE_SHOT_1:
                break;
            case WildHunter.TRIPLE_SHOT_2:
                break;
            case WildHunter.BACKSTEP:
                break;
            case WildHunter.BLIND:
                break;
            case WildHunter.CONCENTRATE_2:
                break;
            case WildHunter.DASH_N_SLASH:
                break;
            case WildHunter.ENDURING_FIRE:
                break;
            case WildHunter.ENDURING_FIRE_1:
                break;
            case WildHunter.ENDURING_FIRE_2:
                break;
            case WildHunter.FELINE_BERSERK_1:
                break;
            case WildHunter.FLURRY:
                break;
            case WildHunter.HUNTING_ASSISTANT_UNIT_1:
                break;
            case WildHunter.JAGUAR_BOOST:
                break;
            case WildHunter.JAGUAR_LINK:
                break;
            case WildHunter.SILVER_HAWK_2:
                break;
            case WildHunter.SONIC_ROAR:
                break;
            case WildHunter.SONIC_ROAR_2:
                break;
            case WildHunter.SWIPE_2:
                break;
            case WildHunter.WHITE_HEAT_RUSH:
                break;
            case WildHunter.WILD_TRAP:
                break;
            case WildHunter.ADVANCED_FINAL_ATTACK_4:
                break;
            case WildHunter.CROSSBOW_EXPERT:
                break;
            case WildHunter.DRILL_SALVO:
                break;
            case WildHunter.EXPLODING_ARROWS:
                break;
            case WildHunter.EXPLODING_ARROWS_1:
                break;
            case WildHunter.EXTENDED_MAGAZINE:
                break;
            case WildHunter.EXTENDED_MAGAZINE_1:
                break;
            case WildHunter.FELINE_BERSERK:
                pEffect.statups.put(CharacterTemporaryStat.Speed, pEffect.info.get(MapleStatInfo.x));
                pEffect.statups.put(CharacterTemporaryStat.BeastFormDamageUp, pEffect.info.get(MapleStatInfo.z));
                pEffect.statups.put(CharacterTemporaryStat.IndieBooster, pEffect.info.get(MapleStatInfo.indieBooster));
                break;
            case WildHunter.FELINE_BERSERK_RAPID_ATTACK:
                break;
            case WildHunter.FELINE_BERSERK_REINFORCE:
                break;
            case WildHunter.FELINE_BERSERK_VITALITY:
                break;
            case WildHunter.FOR_LIBERTY_1:
                pEffect.statups.put(CharacterTemporaryStat.DamR, pEffect.info.get(MapleStatInfo.indieDamR));
                break;
            case WildHunter.HEROS_WILL_40_4:
                break;
            case WildHunter.HUNTING_ASSISTANT_UNIT:
                break;
            case WildHunter.HYPER_ACCURACY_9:
                break;
            case WildHunter.HYPER_CRITICAL_9:
                break;
            case WildHunter.HYPER_DEFENSE_7:
                break;
            case WildHunter.HYPER_DEXTERITY_9:
                break;
            case WildHunter.HYPER_FURY_9:
                break;
            case WildHunter.HYPER_HEALTH_9:
                break;
            case WildHunter.HYPER_INTELLIGENCE_9:
                break;
            case WildHunter.HYPER_JUMP_9:
                break;
            case WildHunter.HYPER_LUCK_9:
                break;
            case WildHunter.HYPER_MAGIC_DEFENSE_9:
                break;
            case WildHunter.HYPER_MANA_9:
                break;
            case WildHunter.HYPER_SPEED_9:
                break;
            case WildHunter.HYPER_STRENGTH_9:
                break;
            case WildHunter.JAGUAR_RAMPAGE:
                break;
            case WildHunter.JAGUAR_RAMPAGE_1:
                break;
            case WildHunter.JAGUAR_RAMPAGE_2:
                break;
            case WildHunter.JAGUAR_SOUL:
                break;
            case WildHunter.JAGUAR_SOUL_1:
                break;
            case WildHunter.MAPLE_WARRIOR_50_5:
                pEffect.statups.put(CharacterTemporaryStat.BasicStatUp, pEffect.info.get(MapleStatInfo.x));
                break;
            case WildHunter.NATURAL_FORCE:
                break;
            case WildHunter.SHARP_EYES_1:
                pEffect.statups.put(CharacterTemporaryStat.SharpEyes, pEffect.info.get(MapleStatInfo.y));
                pEffect.statups.put(CharacterTemporaryStat.CriticalBuff, pEffect.info.get(MapleStatInfo.x));
                break;
            case WildHunter.SILENT_RAMPAGE:
                break;
            case WildHunter.SONIC_ROAR_1:
                break;
            case WildHunter.STINK_BOMB_SHOT:
                break;
            case WildHunter.SUMMON_JAGUAR_COOLDOWN_CUTTER:
                break;
            case WildHunter.SUMMON_JAGUAR_ENHANCE:
                break;
            case WildHunter.SUMMON_JAGUAR_REINFORCE:
                break;
            case WildHunter.WILD_ARROW_BLAST:
                break;
            case WildHunter.WILD_ARROW_BLAST_1:
                break;
            case WildHunter.WILD_ARROW_BLAST_2:
                break;
            case WildHunter.WILD_ARROW_BLAST_BOSS_RUSH:
                break;
            case WildHunter.WILD_ARROW_BLAST_GUARDBREAK:
                break;
            case WildHunter.WILD_ARROW_BLAST_REINFORCE:
                break;
            case WildHunter.WILD_INSTINCT:
                break;

        }

    }

    @Override
    public boolean IsCorrectClass(int nClass) {
        return nClass == 3300 || nClass == 3310 || nClass == 3311 || nClass == 3312;
    }

}
