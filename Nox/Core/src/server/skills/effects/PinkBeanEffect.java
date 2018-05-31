package server.skills.effects;

import server.skills.effects.manager.AbstractEffect;
import client.CharacterTemporaryStat;
import client.Jobs;
import client.MonsterStatus;
import constants.skills.PinkBean;
import server.StatEffect;
import enums.StatInfo;
import server.skills.effects.manager.Effect;

/**
 *
 * @author Novak
 *
 */
@Effect
public class PinkBeanEffect extends AbstractEffect {

    @Override
    public void SetEffect(StatEffect pEffect, int nSourceID) {
        switch (nSourceID) {
            case PinkBean.ALLIANCE_INSPIRATION:
                break;
            case PinkBean.ARCHANGEL:
                break;
            case PinkBean.ARCHANGELIC_BLESSING_6:
                break;
            case PinkBean.ARCHANGELIC_BLESSING_7:
                break;
            case PinkBean.ARCHANGEL_1:
                break;
            case PinkBean.BAMBOO_RAIN:
                break;
            case PinkBean.BLESSING_OF_THE_FAIRY_6:
                break;
            case PinkBean.CURSE_OF_BLACK_BEAN:
                break;
            case PinkBean.DARK_ANGEL:
                break;
            case PinkBean.DARK_ANGELIC_BLESSING_3:
                break;
            case PinkBean.DECENT_ADVANCED_BLESSING_6:
                break;
            case PinkBean.DECENT_COMBAT_ORDERS_6:
                break;
            case PinkBean.DECENT_HASTE_5:
                break;
            case PinkBean.DECENT_HYPER_BODY_6:
                break;
            case PinkBean.DECENT_MYSTIC_DOOR_6:
                break;
            case PinkBean.DECENT_SHARP_EYES_6:
                break;
            case PinkBean.DECENT_SPEED_INFUSION_6:
                break;
            case PinkBean.EMPRESSS_BLESSING_4:
                break;
            case PinkBean.FREEZING_AXE_4:
                break;
            case PinkBean.GIANT_POTION:
                break;
            case PinkBean.GIANT_POTION_1:
                break;
            case PinkBean.GIANT_POTION_2:
                break;
            case PinkBean.HIDDEN_POTENTIAL_HERO_3:
                break;
            case PinkBean.ICE_CHOP_30_3:
                break;
            case PinkBean.ICE_CURSE_30_3:
                break;
            case PinkBean.ICE_DOUBLE_JUMP:
                break;
            case PinkBean.ICE_KNIGHT:
                break;
            case PinkBean.ICE_SMASH_4:
                break;
            case PinkBean.ICE_TEMPEST_30_3:
                break;
            case PinkBean.INVINCIBILITY:
                break;
            case PinkBean.LEGENDARY_SPIRIT:
                break;
            case PinkBean.LINK_MANAGER_5:
                break;
            case PinkBean.MAKER:
                break;
            case PinkBean.MASTER_OF_ORGANIZATION_6:
                break;
            case PinkBean.MASTER_OF_SWIMMING_3:
                break;
            case PinkBean.PIGS_WEAKNESS_6:
                break;
            case PinkBean.PIRATE_BLESSING_4:
                break;
            case PinkBean.POWER_EXPLOSION:
                break;
            case PinkBean.RAGE_OF_PHARAOH:
                break;
            case PinkBean.SLIMES_WEAKNESS_5:
                break;
            case PinkBean.SOARING:
                break;
            case PinkBean.SPACESHIP:
                break;
            case PinkBean.SPACE_BEAM:
                break;
            case PinkBean.SPACE_DASH:
                break;
            case PinkBean.STUMPS_WEAKNESS_6:
                break;
            case PinkBean.WHITE_ANGELIC_BLESSING_40_4:
                break;
            case PinkBean.WHITE_ANGEL_2:
                break;
            case PinkBean.BLAZING_YOYO:
                break;
            case PinkBean.BLAZING_YOYO_1:
                break;
            case PinkBean.BLAZING_YOYO_2:
                break;
            case PinkBean.BREEZY:
                break;
            case PinkBean.CHILL_OUT:
                break;
            case PinkBean.ELECTRIC_GUITAR:
                break;
            case PinkBean.EVERYBODY_HAPPY:
                break;
            case PinkBean.GO_MINI_BEANS:
                break;
            case PinkBean.GO_PINK_BEANS:
                break;
            case PinkBean.INSTANT_GARDEN:
                break;
            case PinkBean.LETS_ROCK:
                break;
            case PinkBean.LETS_ROLL:
                break;
            case PinkBean.LETS_ROLL_1:
                break;
            case PinkBean.LETS_ROLL_2:
                break;
            case PinkBean.MEGAPHONE:
                break;
            case PinkBean.MIDAIR_SKY_JUMP:
                break;
            case PinkBean.MINI_BEAN:
                break;
            case PinkBean.MYSTERIOUS_COCKTAIL:
                break;
            case PinkBean.NOM_NOM_MEAT:
                break;
            case PinkBean.PINK_BEANS_DIGNITY:
                break;
            case PinkBean.PINK_BEANS_DIGNITY_DAMAGE:
                break;
            case PinkBean.PINK_BEANS_HEADSET:
                break;
            case PinkBean.PINK_BEAN_POWER:
                break;
            case PinkBean.PINK_BEAN_POWER_DAMAGE:
                break;
            case PinkBean.PINK_DROP:
                break;
            case PinkBean.PINK_POWERHOUSE:
                break;
            case PinkBean.PINK_POWERHOUSE_1:
                break;
            case PinkBean.PINK_POWERHOUSE_2:
                break;
            case PinkBean.PINK_POWERHOUSE_3:
                break;
            case PinkBean.PINK_POWERHOUSE_4:
                break;
            case PinkBean.PINK_POWERHOUSE_5:
                break;
            case PinkBean.PINK_POWERHOUSE_6:
                break;
            case PinkBean.PINK_POWERHOUSE_7:
                break;
            case PinkBean.PINK_PULVERIZER:
                break;
            case PinkBean.PINK_PULVERIZER_DAMAGE:
                break;
            case PinkBean.PINK_SHADOW:
                break;
            case PinkBean.PINK_SHADOW_1:
                break;
            case PinkBean.PINK_SHADOW_2:
                break;
            case PinkBean.PINK_WARRIOR:
                break;
            case PinkBean.PINK_WARRIOR_1:
                break;
            case PinkBean.POSIE:
                break;
            case PinkBean.PRETTY:
                break;
            case PinkBean.SKY_JUMP:
                break;
            case PinkBean.SKY_JUMP_GROUNDER:
                break;
            case PinkBean.TONGUE_OUT:
                break;
            case PinkBean.UMBRELLA:
                break;
            case PinkBean.WHISTLE:
                break;
            case PinkBean.ZZZ:
                break;

        }

    }

    @Override
    public boolean IsCorrectClass(int nClass) {
        return nClass == 13000 || nClass == 13100;
    }

}
