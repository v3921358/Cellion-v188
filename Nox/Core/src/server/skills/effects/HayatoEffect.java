package server.skills.effects;

import server.skills.effects.manager.AbstractEffect;
import client.CharacterTemporaryStat;
import client.MapleJob;
import client.MonsterStatus;
import constants.skills.Hayato;
import server.MapleStatEffect;
import server.MapleStatInfo;
import server.skills.effects.manager.Effect;

/**
 *
 * @author Novak
 *
 */
@Effect
public class HayatoEffect extends AbstractEffect {

    @Override
    public void SetEffect(MapleStatEffect pEffect, int nSourceID) {
        switch (nSourceID) {
            case Hayato.BATTOUJUTSU_ADVANCE:
                break;
            case Hayato.BATTOUJUTSU_ADVANCE_1:
                break;
            case Hayato.BATTOUJUTSU_DASH:
                break;
            case Hayato.BATTOUJUTSU_LEAP:
                break;
            case Hayato.BATTOUJUTSU_STANCE:
                pEffect.info.put(MapleStatInfo.time, 2100000000);
                pEffect.statups.put(CharacterTemporaryStat.CriticalBuff, pEffect.info.get(MapleStatInfo.y));
                pEffect.statups.put(CharacterTemporaryStat.Battoujutsu, 1);
                pEffect.statups.put(CharacterTemporaryStat.Booster, pEffect.info.get(MapleStatInfo.actionSpeed));
                pEffect.statups.put(CharacterTemporaryStat.HayatoStance, pEffect.info.get(MapleStatInfo.prop));
                break;
            case Hayato.BLADE_FLASH:
                break;
            case Hayato.CENTER_KI_1:
                break;
            case Hayato.SANRENZAN:
                break;
            case Hayato.SHOURYUUSEN_3:
                break;
            case Hayato.SHOURYUUSEN_4:
                break;
            case Hayato.SURGING_BLADE_2:
                break;
            case Hayato.DANKUUSEN:
                break;
            case Hayato.FALCON_DIVE:
                break;
            case Hayato.FALCON_DIVE_1:
                break;
            case Hayato.JIN_BLADE_FLASH:
                break;
            case Hayato.JIN_SANRENZAN:
                break;
            case Hayato.KATANA_BOOSTER:
                pEffect.statups.put(CharacterTemporaryStat.Booster, pEffect.info.get(MapleStatInfo.x));
                break;
            case Hayato.MILITARY_MIGHT:
                pEffect.statups.put(CharacterTemporaryStat.EMHP, pEffect.info.get(MapleStatInfo.x));
                pEffect.statups.put(CharacterTemporaryStat.EMMP, pEffect.info.get(MapleStatInfo.y));
                pEffect.statups.put(CharacterTemporaryStat.EPAD, pEffect.info.get(MapleStatInfo.padX));
                pEffect.statups.put(CharacterTemporaryStat.Speed, pEffect.info.get(MapleStatInfo.speed));
                pEffect.statups.put(CharacterTemporaryStat.Jump, pEffect.info.get(MapleStatInfo.jump));
                break;
            case Hayato.RISING_SLASH:
                break;
            case Hayato.RISING_SLASH_1:
                break;
            case Hayato.SHOURYUUSEN:
                break;
            case Hayato.UNFALTERING_BLADE:
                break;
            case Hayato.VAPOR_BLADE:
                break;
            case Hayato.BATTOUJUTSU_SOUL:
                pEffect.info.put(MapleStatInfo.time, 2100000000);
                pEffect.statups.put(CharacterTemporaryStat.CriticalBuff, pEffect.info.get(MapleStatInfo.y));
                pEffect.statups.put(CharacterTemporaryStat.Battoujutsu, 2);
                pEffect.statups.put(CharacterTemporaryStat.Booster, pEffect.info.get(MapleStatInfo.actionSpeed));
                pEffect.statups.put(CharacterTemporaryStat.HayatoStance, pEffect.info.get(MapleStatInfo.prop));
                break;
            case Hayato.DANKUUSEN_1:
                break;
            case Hayato.DANKUUSEN_2:
                break;
            case Hayato.FUU_BLADE_FLASH:
                break;
            case Hayato.FUU_SANRENZAN:
                break;
            case Hayato.MERCILESS_BLADE:
                break;
            case Hayato.SWEEPING_SWORD:
                break;
            case Hayato.SWEEPING_SWORD_1:
                break;
            case Hayato.TORNADO_BLADE_4:
                break;
            case Hayato.WARRIORS_HEART:
                pEffect.statups.put(CharacterTemporaryStat.Regen, pEffect.info.get(MapleStatInfo.damage));
                break;
            case Hayato.WHIRLWIND_CUT_3:
                break;
            case Hayato.WILLOW_DODGE:
                break;
            case Hayato.AKATSUKI_BLOSSOMS:
                break;
            case Hayato.AKATSUKI_HERO:
                pEffect.statups.put(CharacterTemporaryStat.BasicStatUp, pEffect.info.get(MapleStatInfo.x));
                break;
            case Hayato.BLOODLETTER:
                break;
            case Hayato.CLEAVER:
                break;
            case Hayato.COUNTERATTACK:
                break;
            case Hayato.EYE_FOR_AN_EYE:
                break;
            case Hayato.FALCONS_HONOR:
                break;
            case Hayato.FALCON_DIVE_2:
                break;
            case Hayato.GOD_OF_BLADES:
                pEffect.statups.put(CharacterTemporaryStat.IndiePAD, pEffect.info.get(MapleStatInfo.indiePad));
                pEffect.statups.put(CharacterTemporaryStat.AsrR, pEffect.info.get(MapleStatInfo.x));
                pEffect.statups.put(CharacterTemporaryStat.TerR, pEffect.info.get(MapleStatInfo.x));
                break;
            case Hayato.HITOKIRI_STRIKE:
                break;
            case Hayato.HITOKIRI_STRIKE_COOLDOWN_CUTTER:
                break;
            case Hayato.HITOKIRI_STRIKE_EXTRA_STRIKE:
                break;
            case Hayato.HITOKIRI_STRIKE_SPREAD:
                break;
            case Hayato.HYPER_ACCURACY_100_10:
                break;
            case Hayato.HYPER_CRITICAL_100_10:
                break;
            case Hayato.HYPER_DEFENSE_70_7:
                break;
            case Hayato.HYPER_DEXTERITY_100_10:
                break;
            case Hayato.HYPER_FURY_100_10:
                break;
            case Hayato.HYPER_HEALTH_100_10:
                break;
            case Hayato.HYPER_INTELLIGENCE_100_10:
                break;
            case Hayato.HYPER_JUMP_100_10:
                break;
            case Hayato.HYPER_LUCK_100_10:
                break;
            case Hayato.HYPER_MAGIC_DEFENSE_100_10:
                break;
            case Hayato.HYPER_MANA_100_10:
                break;
            case Hayato.HYPER_SPEED_100_10:
                break;
            case Hayato.HYPER_STRENGTH_100_10:
                break;
            case Hayato.IRON_SKIN:
                pEffect.statups.put(CharacterTemporaryStat.AsrR, pEffect.info.get(MapleStatInfo.asrR));
                pEffect.statups.put(CharacterTemporaryStat.TerR, pEffect.info.get(MapleStatInfo.terR));
                break;
            case Hayato.JINSOKU:
                break;
            case Hayato.PRINCESSS_VOW:
                pEffect.statups.put(CharacterTemporaryStat.DamR, pEffect.info.get(MapleStatInfo.indieDamR));
                break;
            case Hayato.RAI_BLADE_FLASH:
                break;
            case Hayato.RAI_SANRENZAN:
                break;
            case Hayato.SHINSOKU:
                break;
            case Hayato.SHINSOKU_BOSS_RUSH:
                break;
            case Hayato.SHINSOKU_EXTRA_STRIKE:
                break;
            case Hayato.SHINSOKU_REINFORCE:
                break;
            case Hayato.SUDDEN_STRIKE:
                break;
            case Hayato.SUDDEN_STRIKE_1:
                break;
            case Hayato.SUDDEN_STRIKE_EXTRA_STRIKE:
                break;
            case Hayato.SUDDEN_STRIKE_REINFORCE:
                break;
            case Hayato.SUDDEN_STRIKE_SPREAD:
                break;
            case Hayato.SUMMER_RAIN_2:
                break;
            case Hayato.TORNADO_BLADE:
                break;
            case Hayato.TORNADO_BLADE_1:
                break;
            case Hayato.ANIS_JUDGMENT:
                break;
            case Hayato.AUTO_FOLLOW:
                break;
            case Hayato.BALROGS_HELLFIRE:
                break;
            case Hayato.BALROG_3:
                break;
            case Hayato.BAMBOO_RAIN_3:
                break;
            case Hayato.BLESSING_OF_THE_FAIRY_80_8:
                break;
            case Hayato.DECENT_ADVANCED_BLESSING_10:
                break;
            case Hayato.DECENT_COMBAT_ORDERS_10:
                break;
            case Hayato.DECENT_HASTE_9:
                break;
            case Hayato.DECENT_HYPER_BODY_10:
                break;
            case Hayato.DECENT_MYSTIC_DOOR_10:
                break;
            case Hayato.DECENT_SHARP_EYES_10:
                break;
            case Hayato.DECENT_SPEED_INFUSION_10:
                break;
            case Hayato.DRAGON_RIDERS_ENERGY_BREATH:
                break;
            case Hayato.ECHO_OF_HERO:
                break;
            case Hayato.ECKHARTS_VAMPIRE:
                break;
            case Hayato.EMPRESSS_BLESSING_70_7:
                break;
            case Hayato.EMPRESSS_MIGHT_4:
                break;
            case Hayato.EMPRESSS_MIGHT_5:
                break;
            case Hayato.FOLLOW_THE_LEAD_2:
                break;
            case Hayato.HAWKEYES_SHARK_WAVE:
                break;
            case Hayato.HITOKIRI_HUNDRED_STRIKE:
                break;
            case Hayato.HOP_DOWN:
                break;
            case Hayato.HORNTAILS_FLAME_BREATH:
                break;
            case Hayato.INVINCIBILITY_4:
                break;
            case Hayato.IRENAS_WIND_PIERCING:
                break;
            case Hayato.KEEN_EDGE_1:
                break;
            case Hayato.LEGENDARY_SPIRIT_3:
                break;
            case Hayato.LEONARDO_THE_LION:
                break;
            case Hayato.LILIN:
                break;
            case Hayato.LINK_MANAGER_3:
                break;
            case Hayato.LOW_RIDER_3:
                break;
            case Hayato.MAKER_3:
                break;
            case Hayato.MASTER_OF_BLADES:
                break;
            case Hayato.MIHILES_SOUL_DRIVER:
                break;
            case Hayato.MIST_BALROG_1:
                break;
            case Hayato.MONSTER_RIDER:
                break;
            case Hayato.MONSTER_TRAP:
                break;
            case Hayato.MU_GONGS_ABSOLUTE_DESTRUCTION:
                break;
            case Hayato.NIGHTMARE_3:
                break;
            case Hayato.NIMBUS_CLOUD_1:
                break;
            case Hayato.NORMAL_STANCE_BONUS_:
                break;
            case Hayato.ORANGE_MUSHROOM_1:
                break;
            case Hayato.OS4_SHUTTLE:
                break;
            case Hayato.OZS_FLAME_GEAR:
                break;
            case Hayato.PIGS_WEAKNESS_5:
                break;
            case Hayato.PINK_BEANS_ZONE_OF_INCREDIBLE_PAIN:
                break;
            case Hayato.PINK_BEAR_HOTAIR_BALLOON_1:
                break;
            case Hayato.POWER_EXPLOSION_3:
                break;
            case Hayato.QUICK_DRAW:
                pEffect.statups.put(CharacterTemporaryStat.BladeStance, 1);
                //pEffect.statups.put(CharacterTemporaryStat.HayatoStance, 2);
                //pEffect.statups.put(CharacterTemporaryStat.HayatoStanceBonus, 2);
                pEffect.info.put(MapleStatInfo.time, 2100000000);
                break;
            case Hayato.QUICK_DRAW_STANCE_BONUS_:
                break;
            case Hayato.RAGE_OF_PHARAOH_2:
                break;
            case Hayato.REXS_CHARGE:
                break;
            case Hayato.SHIMADA_HEART:
                break;
            case Hayato.SHINSOO:
                break;
            case Hayato.SLIMES_WEAKNESS_4:
                break;
            case Hayato.SOARING_3:
                break;
            case Hayato.SPIRIT_OF_ROCKS_DOOM_STRIKE:
                break;
            case Hayato.STUMPS_WEAKNESS_5:
                break;
            case Hayato.SUMMER_RAIN:
                pEffect.statups.put(CharacterTemporaryStat.IndieDamR, 10);
                pEffect.info.put(MapleStatInfo.time, 20000);
                break;
            case Hayato.SUMMER_RAIN_1:
                break;
            case Hayato.VISITOR_MELEE_ATTACK:
                break;
            case Hayato.VISITOR_RANGE_ATTACK:
                break;
            case Hayato.VON_LEONS_LION_SLASH:
                break;
            case Hayato.WHITE_ANGEL:
                break;
            case Hayato.WHITE_ANGELIC_BLESSING_90_9:
                break;
            case Hayato.WITCHS_BROOMSTICK:
                break;
            case Hayato.WITCHS_BROOMSTICK_3:
                break;
            case Hayato.WITCHS_BROOMSTICK_4:
                break;
            case Hayato.YETI_1:
                break;
            case Hayato.YETI_MOUNT_2:
                break;
            case Hayato.ZAKUMS_TOWERING_INFERNO:
                break;

        }

    }

    @Override
    public boolean IsCorrectClass(int nClass) {
        return nClass == 4100 || nClass == 4110 || nClass == 4111 || nClass == 4112 || nClass == 4001;
    }

}
