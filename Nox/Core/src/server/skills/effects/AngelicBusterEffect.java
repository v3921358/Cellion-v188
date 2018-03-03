package server.skills.effects;

import server.skills.effects.manager.AbstractEffect;
import client.CharacterTemporaryStat;
import client.MapleJob;
import client.MonsterStatus;
import constants.skills.AngelicBuster;
import server.MapleStatEffect;
import server.MapleStatInfo;
import server.skills.effects.manager.Effect;

/**
 *
 * @author Novak
 *
 */
@Effect
public class AngelicBusterEffect extends AbstractEffect {

    @Override
    public void SetEffect(MapleStatEffect pEffect, int nSourceID) {
        switch (nSourceID) {
            case AngelicBuster.AFFINITY_HEART_I:
                break;
            case AngelicBuster.FEATHER_HOP:
                break;
            case AngelicBuster.MELODY_CROSS:
                break;
            case AngelicBuster.STAR_BUBBLE:
                break;
            case AngelicBuster.AFFINITY_HEART_II:
                break;
            case AngelicBuster.BEAUTIFUL_SOUL:
                break;
            case AngelicBuster.LOVELY_STING:
                break;
            case AngelicBuster.LOVELY_STING_1:
                break;
            case AngelicBuster.LOVELY_STING_2:
                break;
            case AngelicBuster.PINK_PUMMEL:
                break;
            case AngelicBuster.POWER_TRANSFER:
                break;
            case AngelicBuster.SOUL_SHOOTER_MASTERY:
                break;
            case AngelicBuster.AFFINITY_HEART_III:
                break;
            case AngelicBuster.DRAGON_WHISTLE:
                break;
            case AngelicBuster.DRAGON_WHISTLE_1:
                break;
            case AngelicBuster.HEAVENLY_CRASH:
                break;
            case AngelicBuster.IRON_BLOSSOM:
                break;
            case AngelicBuster.LOVE_ME_HEART:
                break;
            case AngelicBuster.SHINING_STAR_BURST:
                break;
            case AngelicBuster.SOUL_SEEKER:
                break;
            case AngelicBuster.SOUL_SEEKER_1:
                break;
            case AngelicBuster.AFFINITY_HEART_IV:
                break;
            case AngelicBuster.CELESTIAL_ROAR:
                break;
            case AngelicBuster.FINALE_RIBBON:
                break;
            case AngelicBuster.FINALE_RIBBON_COOLDOWN_CUTTER:
                break;
            case AngelicBuster.FINALE_RIBBON_ENHANCE:
                break;
            case AngelicBuster.FINALE_RIBBON_REINFORCE:
                break;
            case AngelicBuster.FINAL_CONTRACT:
                break;
            case AngelicBuster.HYPER_ACCURACY_300_30_3:
                break;
            case AngelicBuster.HYPER_CRITICAL_300_30_3:
                break;
            case AngelicBuster.HYPER_DEXTERITY_400_40_4:
                break;
            case AngelicBuster.HYPER_FURY_300_30_3:
                break;
            case AngelicBuster.HYPER_HEALTH_300_30_3:
                break;
            case AngelicBuster.HYPER_INTELLIGENCE_400_40_4:
                break;
            case AngelicBuster.HYPER_JUMP_300_30_3:
                break;
            case AngelicBuster.HYPER_LUCK_300_30_3:
                break;
            case AngelicBuster.HYPER_MAGIC_DEFENSE_300_30_3:
                break;
            case AngelicBuster.HYPER_MANA_300_30_3:
                break;
            case AngelicBuster.HYPER_SPEED_300_30_3:
                break;
            case AngelicBuster.HYPER_STRENGTH_400_40_4:
                break;
            case AngelicBuster.HYPER_WEAPON_DEFENSE_4:
                break;
            case AngelicBuster.NOVA_TEMPERANCE_1:
                break;
            case AngelicBuster.NOVA_WARRIOR:
                break;
            case AngelicBuster.PIERCING_SOUL_SEEKER:
                break;
            case AngelicBuster.PIERCING_TRINITY:
                break;
            case AngelicBuster.PRETTY_EXALTATION:
                break;
            case AngelicBuster.SOUL_RESONANCE:
                break;
            case AngelicBuster.SOUL_SEEKER_EXPERT:
                break;
            case AngelicBuster.SOUL_SEEKER_EXPERT_1:
                break;
            case AngelicBuster.SOUL_SEEKER_MAKE_UP:
                break;
            case AngelicBuster.SOUL_SEEKER_REINFORCE:
                break;
            case AngelicBuster.SOUL_SHOOTER_EXPERT:
                break;
            case AngelicBuster.STAR_GAZER:
                break;
            case AngelicBuster.SUPREME_SUPERNOVA:
                break;
            case AngelicBuster.TRINITY:
                break;
            case AngelicBuster.TRINITYEXTRA_STRIKE:
                break;
            case AngelicBuster.TRINITYREINFORCE:
                break;
            case AngelicBuster.TRINITY_1:
                break;
            case AngelicBuster.TRINITY_2:
                break;

        }

    }

    @Override
    public boolean IsCorrectClass(int nClass) {
        return nClass == 6500 || nClass == 6510 || nClass == 6511 || nClass == 6512;
    }

}
