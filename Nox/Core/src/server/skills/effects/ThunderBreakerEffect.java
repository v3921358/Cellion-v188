package server.skills.effects;

import server.skills.effects.manager.AbstractEffect;
import client.CharacterTemporaryStat;
import client.MapleJob;
import client.MonsterStatus;
import constants.skills.ThunderBreaker;
import server.MapleStatEffect;
import server.MapleStatInfo;
import server.skills.effects.manager.Effect;

/**
 *
 * @author Novak
 *
 */
@Effect
public class ThunderBreakerEffect extends AbstractEffect {

    @Override
    public void SetEffect(MapleStatEffect pEffect, int nSourceID) {
        switch (nSourceID) {
            case ThunderBreaker.DASH_1:
                break;
            case ThunderBreaker.ELECTRIFIED:
                break;
            case ThunderBreaker.FLASH:
                break;
            case ThunderBreaker.FORTUNES_FAVOR:
                break;
            case ThunderBreaker.HP_BOOST_1:
                break;
            case ThunderBreaker.LIGHTNING:
                break;
            case ThunderBreaker.LIGHTNING_ELEMENTAL:
                pEffect.statups.put(CharacterTemporaryStat.CygnusElementSkill, 1);
                pEffect.statups.put(CharacterTemporaryStat.IgnoreMobpdpR, pEffect.info.get(MapleStatInfo.x));
                break;
            case ThunderBreaker.LIGHTNING_PUNCH:
                break;
            case ThunderBreaker.QUICK_MOTION:
                break;
            case ThunderBreaker.SHADOW_HEART:
                break;
            case ThunderBreaker.SOMERSAULT_KICK:
                break;
            case ThunderBreaker.STRAIGHT:
                break;
            case ThunderBreaker.CORKSCREW_BLOW:
                break;
            case ThunderBreaker.DARK_CLARITY:
                break;
            case ThunderBreaker.ENERGY_BLAST:
                break;
            case ThunderBreaker.ENERGY_CHARGE_3:
                break;
            case ThunderBreaker.GAINS:
                break;
            case ThunderBreaker.HP_BOOST_6:
                break;
            case ThunderBreaker.KNUCKLE_BOOSTER:
                pEffect.statups.put(CharacterTemporaryStat.Booster, pEffect.info.get(MapleStatInfo.x));
                break;
            case ThunderBreaker.KNUCKLE_BOOSTER_1:
                pEffect.statups.put(CharacterTemporaryStat.Booster, pEffect.info.get(MapleStatInfo.x));
                break;
            case ThunderBreaker.KNUCKLE_MASTERY_3:
                break;
            case ThunderBreaker.KNUCKLE_MASTERY_4:
                break;
            case ThunderBreaker.LIGHTNING_BOOST:
                break;
            case ThunderBreaker.LIGHTNING_CHARGE:
                break;
            case ThunderBreaker.PHYSICAL_TRAINING_60_6:
                break;
            case ThunderBreaker.SHARK_SWEEP:
                break;
            case ThunderBreaker.TIDAL_CRASH:
                break;
            case ThunderBreaker.TORNADO_UPPERCUT_1:
                break;
            case ThunderBreaker.ASCENSION:
                break;
            case ThunderBreaker.BUCCANEER_BLAST_1:
                break;
            case ThunderBreaker.CRITICAL_PUNCH_2:
                break;
            case ThunderBreaker.ENERGY_DRAIN_2:
                break;
            case ThunderBreaker.GALE:
                break;
            case ThunderBreaker.IRONCLAD:
                pEffect.statups.put(CharacterTemporaryStat.DamAbsorbShield, pEffect.info.get(MapleStatInfo.y));
                break;
            case ThunderBreaker.IRONCLAD_1:
                break;
            case ThunderBreaker.LANDLUBBER_BLAST:
                break;
            case ThunderBreaker.LIGHTNING_LORD:
                break;
            case ThunderBreaker.LINK_MASTERY:
                pEffect.statups.put(CharacterTemporaryStat.DamR, pEffect.info.get(MapleStatInfo.x));
                break;
            case ThunderBreaker.OCTOPUNCH_2:
                break;
            case ThunderBreaker.OPPORTUNISTIC_FIGHTER_1:
                break;
            case ThunderBreaker.PRECISION_STRIKES_1:
                break;
            case ThunderBreaker.ROLL_OF_THE_DICE_7:
                break;
            case ThunderBreaker.SEAWALL:
                pEffect.statups.put(CharacterTemporaryStat.AsrR, pEffect.info.get(MapleStatInfo.asrR));
                pEffect.statups.put(CharacterTemporaryStat.TerR, pEffect.info.get(MapleStatInfo.terR));
                break;
            case ThunderBreaker.SEAWALL_1:
                break;
            case ThunderBreaker.SHARK_WAVE:
                break;
            case ThunderBreaker.SHOCKWAVE_2:
                break;
            case ThunderBreaker.SPARK:
                break;
            case ThunderBreaker.SPEED_INFUSION_3:
                break;
            case ThunderBreaker.THUNDER:
                break;
            case ThunderBreaker.TRANSFORMATION_2:
                break;
            case ThunderBreaker.ANNIHILATE:
                break;
            case ThunderBreaker.ANNIHILATE_BOSS_RUSH:
                break;
            case ThunderBreaker.ANNIHILATE_GUARDBREAK:
                break;
            case ThunderBreaker.ANNIHILATE_REINFORCE:
                break;
            case ThunderBreaker.ARC_CHARGER:
                pEffect.statups.put(CharacterTemporaryStat.ShadowPartner, pEffect.info.get(MapleStatInfo.x));
                break;
            case ThunderBreaker.CALL_OF_CYGNUS:
                pEffect.statups.put(CharacterTemporaryStat.IndieStatR, pEffect.info.get(MapleStatInfo.x));
                break;
            case ThunderBreaker.DEEP_RISING:
                break;
            case ThunderBreaker.ELECTRIFY:
                break;
            case ThunderBreaker.GALE_EXTRA_STRIKE:
                break;
            case ThunderBreaker.GALE_REINFORCE:
                break;
            case ThunderBreaker.GALE_SPREAD:
                break;
            case ThunderBreaker.GLORY_OF_THE_GUARDIANS:
                pEffect.statups.put(CharacterTemporaryStat.PAD, pEffect.info.get(MapleStatInfo.indieDamR));
                break;
            case ThunderBreaker.HYPER_ACCURACY_70_7:
                break;
            case ThunderBreaker.HYPER_CRITICAL_70_7:
                break;
            case ThunderBreaker.HYPER_DEFENSE_40_4:
                break;
            case ThunderBreaker.HYPER_DEXTERITY_70_7:
                break;
            case ThunderBreaker.HYPER_FURY_70_7:
                break;
            case ThunderBreaker.HYPER_HEALTH_70_7:
                break;
            case ThunderBreaker.HYPER_INTELLIGENCE_70_7:
                break;
            case ThunderBreaker.HYPER_JUMP_70_7:
                break;
            case ThunderBreaker.HYPER_LUCK_70_7:
                break;
            case ThunderBreaker.HYPER_MAGIC_DEFENSE_70_7:
                break;
            case ThunderBreaker.HYPER_MANA_70_7:
                break;
            case ThunderBreaker.HYPER_SPEED_70_7:
                break;
            case ThunderBreaker.HYPER_STRENGTH_70_7:
                break;
            case ThunderBreaker.KNUCKLE_EXPERT:
                break;
            case ThunderBreaker.PRIMAL_BOLT:
                pEffect.statups.put(CharacterTemporaryStat.StrikerHyperElectric, pEffect.info.get(MapleStatInfo.x));
                pEffect.statups.put(CharacterTemporaryStat.IndieDamR, pEffect.info.get(MapleStatInfo.indieDamR));
                break;
            case ThunderBreaker.SPEED_INFUSION:
                pEffect.statups.put(CharacterTemporaryStat.IndieBooster, pEffect.info.get(MapleStatInfo.x));
                break;
            case ThunderBreaker.THUNDERBOLT:
                break;
            case ThunderBreaker.THUNDERBOLT_EXTRA_STRIKE:
                break;
            case ThunderBreaker.THUNDERBOLT_REINFORCE:
                break;
            case ThunderBreaker.THUNDERBOLT_SPREAD:
                break;
            case ThunderBreaker.THUNDER_GOD:
                break;
            case ThunderBreaker.TYPHOON:
                break;

        }

    }

    @Override
    public boolean IsCorrectClass(int nClass) {
        return nClass == 1500 || nClass == 1510 || nClass == 1511 || nClass == 1512;
    }

}
