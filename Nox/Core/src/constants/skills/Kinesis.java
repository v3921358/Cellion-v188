package constants.skills;

public class Kinesis {
    
    public static boolean is_kinesis_psychiclock_skill(int nSkillID) {
        if (nSkillID > 142111002) {
            if (nSkillID < 142120000 || nSkillID > 142120002 && nSkillID != 142120014) {
                return false;
            }
        } else if (nSkillID != 142111002 && nSkillID != 142100010 && nSkillID != 142110003 && nSkillID != 142110015) {
            return true;
        }
        return true;
    }

    public static final int ALLIANCE_INSPIRATION_1 = 140000190;
    public static final int ARCHANGELIC_BLESSING_600_60_6 = 140000086;
    public static final int ARCHANGELIC_BLESSING_700_70_7 = 140000091;
    public static final int ARCHANGEL_3000_300_30_3 = 140001090;
    public static final int ARCHANGEL_5000_500_50_5 = 140001085;
    public static final int BAMBOO_RAIN_1 = 140001009;
    public static final int BLESSING_OF_THE_FAIRY_40_4 = 140000012;
    public static final int DARK_ANGELIC_BLESSING_30_3 = 140000088;
    public static final int DARK_ANGEL_70_7 = 140001087;
    public static final int DECENT_ADVANCED_BLESSING_8 = 140008005;
    public static final int DECENT_COMBAT_ORDERS_8 = 140008004;
    public static final int DECENT_HASTE_7 = 140008000;
    public static final int DECENT_HYPER_BODY_8 = 140008003;
    public static final int DECENT_MYSTIC_DOOR_8 = 140008001;
    public static final int DECENT_SHARP_EYES_8 = 140008002;
    public static final int DECENT_SPEED_INFUSION_8 = 140008006;
    public static final int EMPRESSS_BLESSING_50_5 = 140000073;
    public static final int ESP = 140000291;
    public static final int FOLLOW_THE_LEAD = 140001024;
    public static final int FREEZING_AXE_40_4 = 140000097;
    public static final int GIANT_POTION_100000_10000_1000_100_10 = 140001095;
    public static final int GIANT_POTION_80000_8000_800_80_8 = 140001092;
    public static final int GIANT_POTION_90000_9000_900_90_9 = 140001094;
    public static final int HEROS_ECHO = 140001005;
    public static final int HIDDEN_POTENTIAL_HERO_10 = 140000093;
    public static final int ICE_CHOP_40_4 = 140000103;
    public static final int ICE_CURSE_40_4 = 140000104;
    public static final int ICE_DOUBLE_JUMP_60_6 = 140001098;
    public static final int ICE_KNIGHT_50_5 = 140001105;
    public static final int ICE_SMASH_40_4 = 140000099;
    public static final int ICE_TEMPEST_40_4 = 140000100;
    public static final int INVINCIBILITY_2 = 140001010;
    public static final int JUDGMENT_1 = 140000292;
    public static final int LEGENDARY_SPIRIT_1 = 140001003;
    public static final int LINK_MANAGER_90_9 = 140001251;
    public static final int MAKER_1 = 140001007;
    public static final int MASTER_OF_ORGANIZATION_7 = 140000112;
    public static final int MASTER_OF_ORGANIZATION_8 = 140000111;
    public static final int MASTER_OF_SWIMMING_4 = 140000109;
    public static final int PIGS_WEAKNESS_4 = 140009000;
    public static final int PIRATE_BLESSING_5 = 140000110;
    public static final int POWER_EXPLOSION_1 = 140001011;
    public static final int PSYCHIC_ATTACK = 140001289;
    public static final int RAGE_OF_PHARAOH_1 = 140001020;
    public static final int RETURN_1 = 140001290;
    public static final int SOARING_1 = 140001026;
    public static final int SPACESHIP_1 = 140001013;
    public static final int SPACE_BEAM_1 = 140001015;
    public static final int SPACE_DASH_1 = 140001014;
    public static final int STUMPS_WEAKNESS_4 = 140009001;
    public static final int VISITOR_MELEE_ATTACK_1 = 140001066;
    public static final int VISITOR_RANGE_ATTACK_1 = 140001067;
    public static final int WHITE_ANGELIC_BLESSING_60_6 = 140000180;
    public static final int WHITE_ANGEL_90_9 = 140001179;
    public static final int ESP_MASTERY = 142100006;
    public static final int KINETIC_PILEDRIVER = 142101002;
    public static final int KINETIC_PILEDRIVER_1 = 142100008;
    public static final int MENTAL_STRENGTH = 142100007;
    public static final int PSYCHIC_ARMOR = 142101004;
    public static final int PSYCHIC_BLAST = 142100000;
    public static final int PSYCHIC_BLAST_VORTEX = 142100001;
    public static final int PSYCHIC_DRAIN = 142101009;
    public static final int PSYCHIC_DRAIN_1 = 142100010;
    public static final int PURE_POWER = 142101005;
    public static final int ULTIMATE_DEEP_IMPACT = 142101003;
    public static final int KINETIC_COMBO = 142110011;
    public static final int KINETIC_JAUNT = 142111010;
    public static final int MIND_TREMOR = 142111006;
    public static final int MITIGATION = 142110014;
    public static final int PSYCHIC_ANNIHILATION = 142110015;
    public static final int PSYCHIC_ASSAULT = 142110000;
    public static final int PSYCHIC_ASSAULT_VORTEX = 142110001;
    public static final int PSYCHIC_BULWARK = 142110009;
    public static final int PSYCHIC_GRAB = 142111002;
    public static final int PSYCHIC_REINFORCEMENT = 142111008;
    public static final int PSYCHIC_SMASH = 142110003;
    public static final int THIRD_EYE = 142110012;
    public static final int TRANSCENDENCE = 142110013;
    public static final int ULTIMATE_TRAINWRECK = 142111007;
    public static final int AWAKENING = 142120010;
    public static final int CLEAR_MIND = 142121007;
    public static final int CRITICAL_RUSH = 142120011;
    public static final int MASTERY = 142120013;
    public static final int MENTAL_OVERDRIVE = 142121032;
    public static final int MENTAL_SHOCK = 142121031;
    public static final int MENTAL_TEMPEST = 142121030;
    public static final int MENTAL_TEMPEST_1 = 142120030;
    public static final int MIND_BREAK = 142121004;
    public static final int MIND_BREAK_COOLDOWN_CUTTER = 142120040;
    public static final int MIND_BREAK_ENHANCE = 142120041;
    public static final int MIND_BREAK_REINFORCE = 142120039;
    public static final int MIND_QUAKE = 142120003;
    public static final int MIND_SCRAMBLER = 142120012;
    public static final int MIND_TREMOR_OVERWHELM = 142120036;
    public static final int MIND_TREMOR_PERSIST = 142120038;
    public static final int MIND_TREMOR_REINFORCE = 142120037;
    public static final int PRESIDENTS_ORDERS = 142121016;
    public static final int PSYCHIC_ANNIHILATION_1 = 142120001;
    public static final int PSYCHIC_ANNIHILATION_2 = 142120014;
    public static final int PSYCHIC_CHARGER = 142121008;
    public static final int PSYCHIC_CHARGER_1 = 142120015;
    public static final int PSYCHIC_CLUTCH = 142120000;
    public static final int PSYCHIC_GRAB_BOSS_POINT = 142120033;
    public static final int PSYCHIC_GRAB_REINFORCE = 142120034;
    public static final int PSYCHIC_GRAB_STEEL_SKIN = 142120035;
    public static final int SUPREME_CONCENTRATION = 142120009;
    public static final int TELEPATH_TACTICS = 142121006;
    public static final int ULTIMATE_BPM = 142121005;
    public static final int ULTIMATE_PSYCHIC_SHOT = 142120002;
}
