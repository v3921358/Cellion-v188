package server.skills.effects;

import server.skills.effects.manager.AbstractEffect;
import client.CharacterTemporaryStat;
import client.MapleJob;
import client.MonsterStatus;
import constants.skills.Beginner;
import server.StatEffect;
import server.StatInfo;
import server.skills.effects.manager.Effect;

/**
 *
 * @author Novak
 *
 */
@Effect
public class BeginnerEffect extends AbstractEffect {

    @Override
    public void SetEffect(StatEffect pEffect, int nSourceID) {
        switch (nSourceID) {
            case Beginner.SIXTH_PARTY_TONIGHT_2:
                break;
            case Beginner.SIXTH_PARTY_TONIGHT_3:
                break;
            case Beginner.ANIS_JUDGMENT_1:
                break;
            case Beginner.ARCHANGELIC_BLESSING_10:
                break;
            case Beginner.ARCHANGELIC_BLESSING_10_1:
                break;
            case Beginner.ARCHANGEL_40_4:
                pEffect.statups.put(CharacterTemporaryStat.IndiePAD, 5);
                pEffect.statups.put(CharacterTemporaryStat.IndieMAD, 5);
                pEffect.statups.put(CharacterTemporaryStat.Speed, 1);
                pEffect.info.put(StatInfo.time, 2100000000);
                break;
            case Beginner.ARCHANGEL_50_5:
                pEffect.statups.put(CharacterTemporaryStat.IndiePAD, 5);
                pEffect.statups.put(CharacterTemporaryStat.IndieMAD, 5);
                pEffect.statups.put(CharacterTemporaryStat.Speed, 1);
                pEffect.info.put(StatInfo.time, 2100000000);
                break;
            case Beginner.BALROGS_HELLFIRE_1:
                break;
            case Beginner.BALROG_10_1:
                break;
            case Beginner.BALROG_7:
                break;
            case Beginner.BAMBOO_RAIN_9:
                break;
            case Beginner.BATTLESHIP_1:
                break;
            case Beginner.BLESSING_OF_THE_FAIRY_7:
                break;
            case Beginner.BLUE_SCOOTER_5:
                break;
            case Beginner.BUFFALO_2:
                break;
            case Beginner.CHICKEN_3:
                break;
            case Beginner.CLOUD_2:
                break;
            case Beginner.CORE_AURA_1:
                break;
            case Beginner.CROKING_1:
                break;
            case Beginner.DARK_ANGELIC_BLESSING_5:
                break;
            case Beginner.DARK_ANGEL_7:
                pEffect.statups.put(CharacterTemporaryStat.IndiePAD, 10);
                pEffect.statups.put(CharacterTemporaryStat.IndieMAD, 10);
                pEffect.statups.put(CharacterTemporaryStat.Speed, 1);
                pEffect.info.put(StatInfo.time, 2100000000);
                break;
            case Beginner.DECENT_ADVANCED_BLESSING_70_7:
                pEffect.statups.put(CharacterTemporaryStat.AdvancedBless, 1);
                break;
            case Beginner.DECENT_COMBAT_ORDERS_70_7:
                pEffect.statups.put(CharacterTemporaryStat.CombatOrders, pEffect.info.get(StatInfo.x));
                break;
            case Beginner.DECENT_HYPER_BODY_70_7:
                pEffect.statups.put(CharacterTemporaryStat.IndieMHP, pEffect.info.get(StatInfo.x));
                pEffect.statups.put(CharacterTemporaryStat.IndieMMP, pEffect.info.get(StatInfo.x));
                break;
            case Beginner.DECENT_MYSTIC_DOOR_70_7:
                pEffect.statups.put(CharacterTemporaryStat.SoulArrow, pEffect.info.get(StatInfo.x));
                break;
            case Beginner.DECENT_SHARP_EYES_70_7:
                pEffect.statups.put(CharacterTemporaryStat.SharpEyes, (pEffect.info.get(StatInfo.x) << 8) + pEffect.info.get(StatInfo.y) + pEffect.info.get(StatInfo.criticaldamageMax));
                break;
            case Beginner.DECENT_SPEED_INFUSION_70_7:
                pEffect.statups.put(CharacterTemporaryStat.Speed, pEffect.info.get(StatInfo.x));
                break;
            case Beginner.DRAGON_2:
                break;
            case Beginner.DRAGON_LEVEL_5:
                break;
            case Beginner.DRAGON_RIDERS_ENERGY_BREATH_1:
                break;
            case Beginner.ECHO_OF_HERO_2:
                pEffect.statups.put(CharacterTemporaryStat.MaxLevelBuff, pEffect.info.get(StatInfo.x));
                break;
            case Beginner.ECKHARTS_DARKNESS:
                break;
            case Beginner.ECKHARTS_VAMPIRE_1:
                break;
            case Beginner.EMPRESSS_BLESSING_6:
                break;
            case Beginner.EMPRESSS_MIGHT:
                break;
            case Beginner.EMPRESSS_MIGHT_1:
                break;
            case Beginner.F1_MACHINE_1:
                break;
            case Beginner.FOLLOW_THE_LEAD_6:
                break;
            case Beginner.FORTUNE_4:
                break;
            case Beginner.FREEZING_AXE_6:
                break;
            case Beginner.FROG_3:
                break;
            case Beginner.GARGOYLE_2:
                break;
            case Beginner.GIANT_POTION_100_10_1:
                break;
            case Beginner.GIANT_POTION_200_20_2:
                break;
            case Beginner.GIANT_POTION_300_30_3:
                break;
            case Beginner.GIANT_RABBIT_3:
                break;
            case Beginner.GODDESS_GUARD_10_1:
                break;
            case Beginner.GODDESS_GUARD_70_7:
                break;
            case Beginner.HAWKEYES_SHARK_WAVE_1:
                break;
            case Beginner.HELICOPTER_3:
                break;
            case Beginner.HELICOPTER_5:
                break;
            case Beginner.HIDDEN_POTENTIAL_EXPLORER:
                pEffect.statups.put(CharacterTemporaryStat.NoDebuff, 1);
                break;
            case Beginner.HIGHWAY_PATROL_CAR_2:
                break;
            case Beginner.HOMEWARD_CANNON:
                break;
            case Beginner.HORNTAILS_FLAME_BREATH_1:
                break;
            case Beginner.HOTAIR_BALLOON_2:
                break;
            case Beginner.ICE_CHOP_5:
                pEffect.monsterStatus.put(MonsterStatus.STUN, 1);
                break;
            case Beginner.ICE_CURSE_5:
                pEffect.monsterStatus.put(MonsterStatus.FREEZE, 1);
                pEffect.info.put(StatInfo.time, pEffect.info.get(StatInfo.time) * 2);
                break;
            case Beginner.ICE_DOUBLE_JUMP_7:
                break;
            case Beginner.ICE_KNIGHT_6:
                break;
            case Beginner.ICE_SMASH_6:
                pEffect.monsterStatus.put(MonsterStatus.FREEZE, 1);
                pEffect.info.put(StatInfo.time, pEffect.info.get(StatInfo.time) * 2);
                break;
            case Beginner.ICE_TEMPEST_5:
                break;
            case Beginner.INVINCIBILITY_10:
                break;
            case Beginner.INVISIBLE_BALROG_2:
                break;
            case Beginner.IRENAS_WIND:
                break;
            case Beginner.IRENAS_WIND_PIERCING_1:
                break;
            case Beginner.JR_TANK_2:
                break;
            case Beginner.KNIGHTS_CHARIOT_2:
                break;
            case Beginner.KURENAI_RUN_AWAY_1:
                break;
            case Beginner.LAW_OFFICER_2:
                break;
            case Beginner.LEGENDARY_SPIRIT_10:
                break;
            case Beginner.LEONARDO_THE_LION_7:
                break;
            case Beginner.LINK_MANAGER_10:
                break;
            case Beginner.LION_2:
                break;
            case Beginner.LOVELY_SCOOTER_1:
                break;
            case Beginner.LOW_RIDER_5:
                break;
            case Beginner.LOW_RIDER_7:
                break;
            case Beginner.MAGICAL_WOODEN_HORSE_1:
                break;
            case Beginner.MAGIC_BROOM_2:
                break;
            case Beginner.MAKER_10:
                break;
            case Beginner.MAPLE_RETURN:
                break;
            case Beginner.MASTER_OF_ORGANIZATION_2:
                break;
            case Beginner.MASTER_OF_ORGANIZATION_3:
                break;
            case Beginner.MASTER_OF_SWIMMING_1:
                break;
            case Beginner.MIHILES_SOUL:
                break;
            case Beginner.MIHILES_SOUL_DRIVER_1:
                break;
            case Beginner.MIST_BALROG_7:
                break;
            case Beginner.MONSTER_RIDER_2:
                break;
            case Beginner.MOTHERSHIP_2:
                break;
            case Beginner.MU_GONGS_ABSOLUTE_DESTRUCTION_1:
                break;
            case Beginner.NADESHIKO_FLY_HIGH_2:
                break;
            case Beginner.NAPOLEAN_MOUNT_2:
                break;
            case Beginner.NIGHTMARE_10_1:
                break;
            case Beginner.NIGHTMARE_7:
                break;
            case Beginner.NIMBLE_FEET_1:
                pEffect.statups.put(CharacterTemporaryStat.Speed, 10 + (pEffect.getLevel() - 1) * 5);
                break;
            case Beginner.NIMBUS_CLOUD_7:
                break;
            case Beginner.NINAS_PENTACLE_2:
                break;
            case Beginner.ORANGE_MUSHROOM_7:
                break;
            case Beginner.OS3A_MACHINE_2:
                break;
            case Beginner.OS4_SHUTTLE_3:
                break;
            case Beginner.OSTRICH_6:
                break;
            case Beginner.OWL_3:
                break;
            case Beginner.OWL__2:
                break;
            case Beginner.OZS_FLAME:
                break;
            case Beginner.OZS_FLAME_GEAR_1:
                break;
            case Beginner.PACHINKO_ROBO_1:
                break;
            case Beginner.PEGASUS_2:
                break;
            case Beginner.PINK_BEANS_ZONE_OF_INCREDIBLE_PAIN_1:
                break;
            case Beginner.PINK_BEAR_HOTAIR_BALLOON_7:
                break;
            case Beginner.PIRATE_BLESSING_2:
                break;
            case Beginner.POWER_EXPLOSION_9:
                break;
            case Beginner.POWER_SUIT_6:
                break;
            case Beginner.RABBIT_RICKSHAW_3:
                break;
            case Beginner.RAGE_OF_PHARAOH_10:
                break;
            case Beginner.RECOVERY_1:
                pEffect.statups.put(CharacterTemporaryStat.Regen, pEffect.info.get(StatInfo.x));
                break;
            case Beginner.RED_DRACO_3:
                break;
            case Beginner.RED_TRUCK_2:
                break;
            case Beginner.RETRO_ROCKETS:
                break;
            case Beginner.RETRO_SCOOTER_1:
                break;
            case Beginner.RETURN:
                break;
            case Beginner.RETURN_TO_SPACESHIP:
                break;
            case Beginner.REXS_CHARGE_1:
                break;
            case Beginner.REXS_HYENA_3:
                break;
            case Beginner.SANTA_SLED_5:
                break;
            case Beginner.SHINJO_10_1:
                break;
            case Beginner.SHINJO_7:
                break;
            case Beginner.SLIMES_WEAKNESS_9:
                break;
            case Beginner.SMALL_RABBIT_2:
                break;
            case Beginner.SOARING_10_1:
                break;
            case Beginner.SOARING_MOUNT_1:
                pEffect.statups.put(CharacterTemporaryStat.Flying, 1);
                pEffect.info.put(StatInfo.time, 2100000000);
                break;
            case Beginner.SPACESHIP_20_2:
                break;
            case Beginner.SPACESHIP_30_3:
                break;
            case Beginner.SPACE_BEAM_10_1:
                break;
            case Beginner.SPACE_BEAM_20_2:
                break;
            case Beginner.SPACE_DASH_10_1:
                break;
            case Beginner.SPACE_DASH_20_2:
                break;
            case Beginner.SPIRIT_OF_ROCKS_DOOM_STRIKE_1:
                break;
            case Beginner.SPIRIT_VIKING_2:
                break;
            case Beginner.STUMPS_WEAKNESS_10:
                break;
            case Beginner.SUPER_TRANSFORMATION:
                pEffect.statups.put(CharacterTemporaryStat.Morph, pEffect.info.get(StatInfo.morph));
                break;
            case Beginner.TEST_4:
                break;
            case Beginner.TIGER_1:
                break;
            case Beginner.TRANSFORMER_1:
                break;
            case Beginner.TURTLE_3:
                break;
            case Beginner.UNICORN_2:
                break;
            case Beginner.UNK_0:
                break;
            case Beginner.UNK_1:
                break;
            case Beginner.UNK_10:
                break;
            case Beginner.UNK_100:
                break;
            case Beginner.UNK_101:
                break;
            case Beginner.UNK_102:
                break;
            case Beginner.UNK_103:
                break;
            case Beginner.UNK_104:
                break;
            case Beginner.UNK_105:
                break;
            case Beginner.UNK_106:
                break;
            case Beginner.UNK_107:
                break;
            case Beginner.UNK_108:
                break;
            case Beginner.UNK_109:
                break;
            case Beginner.UNK_11:
                break;
            case Beginner.UNK_110:
                break;
            case Beginner.UNK_111:
                break;
            case Beginner.UNK_112:
                break;
            case Beginner.UNK_113:
                break;
            case Beginner.UNK_114:
                break;
            case Beginner.UNK_115:
                break;
            case Beginner.UNK_116:
                break;
            case Beginner.UNK_117:
                break;
            case Beginner.UNK_118:
                break;
            case Beginner.UNK_12:
                break;
            case Beginner.UNK_120:
                break;
            case Beginner.UNK_121:
                break;
            case Beginner.UNK_122:
                break;
            case Beginner.UNK_123:
                break;
            case Beginner.UNK_125:
                break;
            case Beginner.UNK_127:
                break;
            case Beginner.UNK_128:
                break;
            case Beginner.UNK_129:
                break;
            case Beginner.UNK_13:
                break;
            case Beginner.UNK_130:
                break;
            case Beginner.UNK_135:
                break;
            case Beginner.UNK_136:
                break;
            case Beginner.UNK_137:
                break;
            case Beginner.UNK_14:
                break;
            case Beginner.UNK_141:
                break;
            case Beginner.UNK_142:
                break;
            case Beginner.UNK_143:
                break;
            case Beginner.UNK_144:
                break;
            case Beginner.UNK_145:
                break;
            case Beginner.UNK_146:
                break;
            case Beginner.UNK_147:
                break;
            case Beginner.UNK_148:
                break;
            case Beginner.UNK_15:
                break;
            case Beginner.UNK_150:
                break;
            case Beginner.UNK_151:
                break;
            case Beginner.UNK_152:
                break;
            case Beginner.UNK_153:
                break;
            case Beginner.UNK_154:
                break;
            case Beginner.UNK_16:
                break;
            case Beginner.UNK_17:
                break;
            case Beginner.UNK_179:
                break;
            case Beginner.UNK_18:
                break;
            case Beginner.UNK_180:
                break;
            case Beginner.UNK_181:
                break;
            case Beginner.UNK_182:
                break;
            case Beginner.UNK_183:
                break;
            case Beginner.UNK_184:
                break;
            case Beginner.UNK_185:
                break;
            case Beginner.UNK_186:
                break;
            case Beginner.UNK_187:
                break;
            case Beginner.UNK_188:
                break;
            case Beginner.UNK_19:
                break;
            case Beginner.UNK_2:
                break;
            case Beginner.UNK_20:
                break;
            case Beginner.UNK_200:
                break;
            case Beginner.UNK_201:
                break;
            case Beginner.UNK_202:
                break;
            case Beginner.UNK_203:
                break;
            case Beginner.UNK_204:
                break;
            case Beginner.UNK_205:
                break;
            case Beginner.UNK_206:
                break;
            case Beginner.UNK_207:
                break;
            case Beginner.UNK_208:
                break;
            case Beginner.UNK_209:
                break;
            case Beginner.UNK_21:
                break;
            case Beginner.UNK_210:
                break;
            case Beginner.UNK_211:
                break;
            case Beginner.UNK_212:
                break;
            case Beginner.UNK_213:
                break;
            case Beginner.UNK_214:
                break;
            case Beginner.UNK_215:
                break;
            case Beginner.UNK_216:
                break;
            case Beginner.UNK_217:
                break;
            case Beginner.UNK_218:
                break;
            case Beginner.UNK_22:
                break;
            case Beginner.UNK_223:
                break;
            case Beginner.UNK_224:
                break;
            case Beginner.UNK_225:
                break;
            case Beginner.UNK_226:
                break;
            case Beginner.UNK_227:
                break;
            case Beginner.UNK_228:
                break;
            case Beginner.UNK_229:
                break;
            case Beginner.UNK_23:
                break;
            case Beginner.UNK_230:
                break;
            case Beginner.UNK_231:
                break;
            case Beginner.UNK_232:
                break;
            case Beginner.UNK_24:
                break;
            case Beginner.UNK_25:
                break;
            case Beginner.UNK_26:
                break;
            case Beginner.UNK_28:
                break;
            case Beginner.UNK_29:
                break;
            case Beginner.UNK_3:
                break;
            case Beginner.UNK_30:
                break;
            case Beginner.UNK_31:
                break;
            case Beginner.UNK_32:
                break;
            case Beginner.UNK_33:
                break;
            case Beginner.UNK_34:
                break;
            case Beginner.UNK_35:
                break;
            case Beginner.UNK_36:
                break;
            case Beginner.UNK_37:
                break;
            case Beginner.UNK_38:
                break;
            case Beginner.UNK_39:
                break;
            case Beginner.UNK_4:
                break;
            case Beginner.UNK_40:
                break;
            case Beginner.UNK_41:
                break;
            case Beginner.UNK_42:
                break;
            case Beginner.UNK_43:
                break;
            case Beginner.UNK_44:
                break;
            case Beginner.UNK_45:
                break;
            case Beginner.UNK_46:
                break;
            case Beginner.UNK_47:
                break;
            case Beginner.UNK_48:
                break;
            case Beginner.UNK_49:
                break;
            case Beginner.UNK_5:
                break;
            case Beginner.UNK_50:
                break;
            case Beginner.UNK_51:
                break;
            case Beginner.UNK_53:
                break;
            case Beginner.UNK_54:
                break;
            case Beginner.UNK_55:
                break;
            case Beginner.UNK_56:
                break;
            case Beginner.UNK_57:
                break;
            case Beginner.UNK_58:
                break;
            case Beginner.UNK_59:
                break;
            case Beginner.UNK_6:
                break;
            case Beginner.UNK_60:
                break;
            case Beginner.UNK_61:
                break;
            case Beginner.UNK_62:
                break;
            case Beginner.UNK_63:
                break;
            case Beginner.UNK_64:
                break;
            case Beginner.UNK_65:
                break;
            case Beginner.UNK_66:
                break;
            case Beginner.UNK_67:
                break;
            case Beginner.UNK_68:
                break;
            case Beginner.UNK_69:
                break;
            case Beginner.UNK_7:
                break;
            case Beginner.UNK_70:
                break;
            case Beginner.UNK_71:
                break;
            case Beginner.UNK_72:
                break;
            case Beginner.UNK_73:
                break;
            case Beginner.UNK_74:
                break;
            case Beginner.UNK_75:
                break;
            case Beginner.UNK_76:
                break;
            case Beginner.UNK_77:
                break;
            case Beginner.UNK_78:
                break;
            case Beginner.UNK_8:
                break;
            case Beginner.UNK_83:
                break;
            case Beginner.UNK_84:
                break;
            case Beginner.UNK_85:
                break;
            case Beginner.UNK_86:
                break;
            case Beginner.UNK_87:
                break;
            case Beginner.UNK_88:
                break;
            case Beginner.UNK_89:
                break;
            case Beginner.UNK_9:
                break;
            case Beginner.UNK_90:
                break;
            case Beginner.UNK_91:
                break;
            case Beginner.UNK_93:
                break;
            case Beginner.UNK_94:
                break;
            case Beginner.UNK_95:
                break;
            case Beginner.UNK_96:
                break;
            case Beginner.UNK_97:
                break;
            case Beginner.UNK_98:
                break;
            case Beginner.UNK_99:
                break;
            case Beginner.VISITOR_MELEE_ATTACK_4:
                break;
            case Beginner.VISITOR_RANGE_ATTACK_4:
                break;
            case Beginner.VON_LEONS_LION_SLASH_1:
                break;
            case Beginner.WHITE_ANGELIC_BLESSING_5:
                break;
            case Beginner.WHITE_ANGEL_7:
                pEffect.statups.put(CharacterTemporaryStat.IndiePAD, 12);
                pEffect.statups.put(CharacterTemporaryStat.IndieMAD, 12);
                pEffect.statups.put(CharacterTemporaryStat.Speed, 1);
                pEffect.info.put(StatInfo.time, 2100000000);
                break;
            case Beginner.WILL_OF_THE_ALLIANCE_5:
                break;
            case Beginner.WITCHS_BROOMSTICK_30_3:
                break;
            case Beginner.WITCHS_BROOMSTICK_50_5:
                break;
            case Beginner.YETI_7:
                break;
            case Beginner.YETI_MOUNT_20_2:
                break;
            case Beginner.YETI_RIDER_1:
                break;
            case Beginner.ZAKUMS_TOWERING_INFERNO_1:
                break;

        }

    }

    @Override
    public boolean IsCorrectClass(int nClass) {
        return nClass == 0;
    }

}
