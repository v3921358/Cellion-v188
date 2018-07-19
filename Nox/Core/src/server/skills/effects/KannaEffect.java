package server.skills.effects;

import server.skills.effects.manager.AbstractEffect;
import client.CharacterTemporaryStat;
import client.Jobs;
import client.MonsterStatus;
import constants.skills.Kanna;
import server.StatEffect;
import enums.StatInfo;
import server.skills.effects.manager.Effect;

/**
 *
 * @author Novak
 *
 */
@Effect
public class KannaEffect extends AbstractEffect {

    @Override
    public void SetEffect(StatEffect pEffect, int nSourceID) {
        switch (nSourceID) {
            case Kanna.CENTER_KI:
                break;
            case Kanna.CLAY_YAKSHA:
                break;
            case Kanna.ETHER_PULSE_2:
                break;
            case Kanna.HAKU:
                break;
            case Kanna.HAKUS_GIFT:
                pEffect.statups.put(CharacterTemporaryStat.Regen, pEffect.info.get(StatInfo.hp));
                break;
            case Kanna.SHIKIGAMI_HAUNTING_2:
                break;
            case Kanna.SHIKIGAMI_HAUNTING_3:
                break;
            case Kanna.SHIKIGAMI_HAUNTING_4:
                break;
            case Kanna.BREATH_OF_THE_UNSEEN:
                pEffect.statups.put(CharacterTemporaryStat.IgnoreTargetDEF, pEffect.info.get(StatInfo.x));
                pEffect.statups.put(CharacterTemporaryStat.Stance, pEffect.info.get(StatInfo.prop));
                break;
            case Kanna.BURNING_SHIKIGAMI_HAUNTING:
                break;
            case Kanna.BURNING_SHIKIGAMI_HAUNTING_1:
                break;
            case Kanna.FOXFIRE:
                pEffect.statups.put(CharacterTemporaryStat.SUMMON, 1);
                break;
            case Kanna.HAKUS_BLESSING:
                break;
            case Kanna.HAKUS_GIFT_1:
                break;
            case Kanna.HAKU_REBORN:
                break;
            case Kanna.NIMBUS_CURSE:
                break;
            case Kanna.RADIANT_PEACOCK:
                pEffect.statups.put(CharacterTemporaryStat.Booster, pEffect.info.get(StatInfo.x));
                break;
            case Kanna.ROCK_YAKSHA:
                break;
            case Kanna.SHIKIGAMI_CHARM:
                break;
            case Kanna.SOUL_BOMB:
                break;
            case Kanna.BLOSSOM_BARRIER:
                break;
            case Kanna.DEMONS_FURY:
                break;
            case Kanna.FROZEN_SHIKIGAMI_HAUNTING:
                break;
            case Kanna.FROZEN_SHIKIGAMI_HAUNTING_1:
                break;
            case Kanna.HAKU_REBORN_3:
                break;
            case Kanna.HAKU_REBORN_4:
                break;
            case Kanna.KISHIN_SHOUKAN:
                pEffect.statups.put(CharacterTemporaryStat.SUMMON, 1);
                pEffect.statups.put(CharacterTemporaryStat.IncMobRateDummy, 1);
                break;
            case Kanna.LIFEBLOOD_RITUAL:
                break;
            case Kanna.MANA_BALANCE:
                break;
            case Kanna.SOUL_SHEAR:
                break;
            case Kanna.SPIRIT_CORRAL:
                break;
            case Kanna.SPIRIT_PATH:
                break;
            case Kanna.TENGU_STRIKE:
                break;
            case Kanna.BELLFLOWER_BARRIER_2:
                break;
            case Kanna.BELLFLOWER_BARRIER_BOSS_RUSH:
                break;
            case Kanna.BELLFLOWER_BARRIER_COOLDOWN_CUTTER:
                break;
            case Kanna.BELLFLOWER_BARRIER_PERSIST:
                break;
            case Kanna.BINDING_TEMPEST:
                break;
            case Kanna.BLACKHEARTED_CURSE:
                break;
            case Kanna.BLOSSOMING_DAWN_HEROS_WILL:
                break;
            case Kanna.BREATH_OF_THE_UNSEEN_3:
                break;
            case Kanna.DAWNS_WARRIOR_MAPLE_WARRIOR:
                pEffect.statups.put(CharacterTemporaryStat.BasicStatUp, pEffect.info.get(StatInfo.x));
                break;
            case Kanna.DEMONS_FURY_1:
                break;
            case Kanna.FALLING_SAKURA_2:
                break;
            case Kanna.FALLING_SAKURA_REINFORCE:
                break;
            case Kanna.FALLING_SAKURA_SPREAD:
                break;
            case Kanna.FALLING_SAKURA_VITALITY:
                break;
            case Kanna.FOXFIRE_3:
                break;
            case Kanna.HAKUS_BLESSING__1:
                break;
            case Kanna.HAKUS_GIFT_4:
                break;
            case Kanna.HAKU_PERFECTED:
                break;
            case Kanna.HYPER_ACCURACY_20_2:
                break;
            case Kanna.HYPER_CRITICAL_20_2:
                break;
            case Kanna.HYPER_DEFENSE_10:
                break;
            case Kanna.HYPER_DEXTERITY_10:
                break;
            case Kanna.HYPER_FURY_20_2:
                break;
            case Kanna.HYPER_HEALTH_20_2:
                break;
            case Kanna.HYPER_INTELLIGENCE_10:
                break;
            case Kanna.HYPER_JUMP_20_2:
                break;
            case Kanna.HYPER_LUCK_10:
                break;
            case Kanna.HYPER_MAGIC_DEFENSE_20_2:
                break;
            case Kanna.HYPER_MANA_20_2:
                break;
            case Kanna.HYPER_MAX_MANA:
                break;
            case Kanna.HYPER_SPEED_20_2:
                break;
            case Kanna.HYPER_STRENGTH_10:
                break;
            case Kanna.KASEN:
                break;
            case Kanna.MIGHTY_SHIKIGAMI_HAUNTING_2:
                break;
            case Kanna.MONKEY_SPIRITS:
                break;
            case Kanna.NINETAILED_FURY_1:
                break;
            case Kanna.OROCHI_2:
                break;
            case Kanna.PRINCESSS_VOW_1:
                pEffect.statups.put(CharacterTemporaryStat.DamR, pEffect.info.get(StatInfo.indieDamR));
                break;
            case Kanna.SHADOWBOUND_HAKU:
                break;
            case Kanna.SHOCKING_SHIKIGAMI_HAUNTING:
                break;
            case Kanna.VANQUISHERS_CHARM_2:
                break;
            case Kanna.VANQUISHERS_CHARM_EXTRA_STRIKE:
                break;
            case Kanna.VANQUISHERS_CHARM_REINFORCE:
                break;
            case Kanna.VANQUISHERS_CHARM_SPREAD:
                break;
            case Kanna.VERITABLE_PANDEMONIUM:
                break;
            case Kanna.ANIS_JUDGMENT_6:
                break;
            case Kanna.AUTO_FOLLOW_1:
                break;
            case Kanna.BALROGS_HELLFIRE_6:
                break;
            case Kanna.BALROG_100_10_1:
                break;
            case Kanna.BAMBOO_RAIN_50_5:
                break;
            case Kanna.BLESSING_OF_THE_FAIRY_9:
                break;
            case Kanna.DECENT_ADVANCED_BLESSING_2:
                break;
            case Kanna.DECENT_COMBAT_ORDERS_2:
                break;
            case Kanna.DECENT_HASTE_2:
                break;
            case Kanna.DECENT_HYPER_BODY_2:
                break;
            case Kanna.DECENT_MYSTIC_DOOR_2:
                break;
            case Kanna.DECENT_SHARP_EYES_2:
                break;
            case Kanna.DECENT_SPEED_INFUSION_2:
                break;
            case Kanna.DRAGON_RIDERS_ENERGY_BREATH_6:
                break;
            case Kanna.ECHO_OF_HERO_4:
                break;
            case Kanna.ECKHARTS_VAMPIRE_2:
                break;
            case Kanna.ELEMENTALISM_2:
                break;
            case Kanna.ELEMENTALISM_LINK_SKILL:
                break;
            case Kanna.ELEMENTAL_BLESSING:
                break;
            case Kanna.EMPRESSS_BLESSING_10:
                break;
            case Kanna.EMPRESSS_MIGHT_2:
                break;
            case Kanna.EMPRESSS_MIGHT_3:
                break;
            case Kanna.FOLLOW_THE_LEAD_70_7:
                break;
            case Kanna.HAKU_1:
                break;
            case Kanna.HAWKEYES_SHARK_WAVE_2:
                break;
            case Kanna.HOP_DOWN_1:
                break;
            case Kanna.HORNTAILS_FLAME_BREATH_6:
                break;
            case Kanna.INVINCIBILITY_70_7:
                break;
            case Kanna.IRENAS_WIND_PIERCING_2:
                break;
            case Kanna.LEGENDARY_SPIRIT_70_7:
                break;
            case Kanna.LEONARDO_THE_LION_40_4:
                break;
            case Kanna.LILIN_1:
                break;
            case Kanna.LINK_MANAGER_50_5:
                break;
            case Kanna.LOW_RIDER_20_2:
                break;
            case Kanna.MAKER_80_8:
                break;
            case Kanna.MANA_FONT:
                break;
            case Kanna.MIHILES_SOUL_DRIVER_2:
                break;
            case Kanna.MONSTER_RIDER_5:
                break;
            case Kanna.MONSTER_TRAP_2:
                break;
            case Kanna.MU_GONGS_ABSOLUTE_DESTRUCTION_6:
                break;
            case Kanna.NIGHTMARE_100_10:
                break;
            case Kanna.NIMBUS_CLOUD_70_7:
                break;
            case Kanna.NINETAILED_FURY:
                break;
            case Kanna.ORANGE_MUSHROOM_40_4:
                break;
            case Kanna.OS4_SHUTTLE_6:
                break;
            case Kanna.OZS_FLAME_GEAR_2:
                break;
            case Kanna.PIGS_WEAKNESS_1:
                break;
            case Kanna.PINK_BEANS_ZONE_OF_INCREDIBLE_PAIN_6:
                break;
            case Kanna.PINK_BEAR_HOTAIR_BALLOON_40_4:
                break;
            case Kanna.POWER_EXPLOSION_50_5:
                break;
            case Kanna.RAGE_OF_PHARAOH_80_8:
                break;
            case Kanna.REXS_CHARGE_6:
                break;
            case Kanna.SHINSOO_1:
                break;
            case Kanna.SLIMES_WEAKNESS_1:
                break;
            case Kanna.SOARING_90_9:
                break;
            case Kanna.SPIRIT_OF_ROCKS_DOOM_STRIKE_6:
                break;
            case Kanna.STUMPS_WEAKNESS_1:
                break;
            case Kanna.VIRTUES_BLESSING_3:
                break;
            case Kanna.VISITOR_MELEE_ATTACK_7:
                break;
            case Kanna.VISITOR_RANGE_ATTACK_7:
                break;
            case Kanna.VON_LEONS_LION_SLASH_6:
                break;
            case Kanna.WHITE_ANGELIC_BLESSING_10:
                break;
            case Kanna.WHITE_ANGEL_30_3:
                break;
            case Kanna.WITCHS_BROOMSTICK_2000_200_20_2:
                break;
            case Kanna.WITCHS_BROOMSTICK_3000_300_30_3:
                break;
            case Kanna.YETI_40_4:
                break;
            case Kanna.YETI_MOUNT_400_40_4:
                break;
            case Kanna.ZAKUMS_TOWERING_INFERNO_6:
                break;

        }

    }

    @Override
    public boolean IsCorrectClass(int nClass) {
        return nClass == 4200 || nClass == 4210 || nClass == 4211 || nClass == 4212 || nClass == 4002;
    }

}
