package server.skills.effects;

import server.skills.effects.manager.AbstractEffect;
import client.CharacterTemporaryStat;
import client.Jobs;
import client.MonsterStatus;
import constants.skills.IceLightningArchMage;
import server.StatEffect;
import enums.StatInfo;
import server.skills.effects.manager.Effect;

/**
 *
 * @author Novak
 *
 */
@Effect
public class IceLightningArchMageEffect extends AbstractEffect {

    @Override
    public void SetEffect(StatEffect pEffect, int nSourceID) {
        switch (nSourceID) {
            case IceLightningArchMage.ABSOLUTE_ZERO_AURA:
                pEffect.statups.put(CharacterTemporaryStat.IceAura, 1);
                pEffect.statups.put(CharacterTemporaryStat.AsrR, pEffect.info.get(StatInfo.v));
                pEffect.statups.put(CharacterTemporaryStat.TerR, pEffect.info.get(StatInfo.v));
                pEffect.info.put(StatInfo.time, 2100000000);
                break;
            case IceLightningArchMage.ARCANE_AIM_1:
                break;
            case IceLightningArchMage.BIG_BANG_1:
                break;
            case IceLightningArchMage.BLIZZARD:
                pEffect.monsterStatus.put(MonsterStatus.FREEZE, 1);
                pEffect.info.put(StatInfo.time, pEffect.info.get(StatInfo.time) * 2);
                break;
            case IceLightningArchMage.BLIZZARD_1:
                break;
            case IceLightningArchMage.BUFF_MASTERY_2:
                break;
            case IceLightningArchMage.BUFF_MASTERY_3:
                break;
            case IceLightningArchMage.CHAIN_LIGHTNING:
                pEffect.monsterStatus.put(MonsterStatus.STUN, 1);
                break;
            case IceLightningArchMage.CHAIN_LIGHTNING_EXTRA_STRIKE:
                break;
            case IceLightningArchMage.CHAIN_LIGHTNING_REINFORCE:
                break;
            case IceLightningArchMage.CHAIN_LIGHTNING_SPREAD:
                break;
            case IceLightningArchMage.ELQUINES:
                pEffect.statups.put(CharacterTemporaryStat.SUMMON, 1);
                pEffect.monsterStatus.put(MonsterStatus.FREEZE, 1);
                break;
            case IceLightningArchMage.EPIC_ADVENTURE_5:
                pEffect.statups.put(CharacterTemporaryStat.DamR, pEffect.info.get(StatInfo.indieDamR));
                break;
            case IceLightningArchMage.FREEZING_BREATH:
                pEffect.statups.put(CharacterTemporaryStat.KeyDownAreaMoving, pEffect.info.get(StatInfo.x));
                pEffect.monsterStatus.put(MonsterStatus.FREEZE, 1);
                pEffect.monsterStatus.put(MonsterStatus.MAGIC_ATTACK_UP, -pEffect.info.get(StatInfo.y));
                break;
            case IceLightningArchMage.FROST_CLUTCH:
                break;
            case IceLightningArchMage.FROZEN_ORB:
                break;
            case IceLightningArchMage.FROZEN_ORB_CRITICAL_CHANCE:
                break;
            case IceLightningArchMage.FROZEN_ORB_REINFORCE:
                break;
            case IceLightningArchMage.FROZEN_ORB_SPREAD:
                break;
            case IceLightningArchMage.GLACIER_CHAIN_1:
                break;
            case IceLightningArchMage.HEROS_WILL_10:
                break;
            case IceLightningArchMage.HYPER_ACCURACY_500_50_5:
                break;
            case IceLightningArchMage.HYPER_CRITICAL_500_50_5:
                break;
            case IceLightningArchMage.HYPER_DEFENSE_100_10:
                break;
            case IceLightningArchMage.HYPER_DEXTERITY_500_50_5:
                break;
            case IceLightningArchMage.HYPER_FURY_500_50_5:
                break;
            case IceLightningArchMage.HYPER_HEALTH_500_50_5:
                break;
            case IceLightningArchMage.HYPER_INTELLIGENCE_500_50_5:
                break;
            case IceLightningArchMage.HYPER_JUMP_500_50_5:
                break;
            case IceLightningArchMage.HYPER_LUCK_500_50_5:
                break;
            case IceLightningArchMage.HYPER_MAGIC_DEFENSE_500_50_5:
                break;
            case IceLightningArchMage.HYPER_MANA_500_50_5:
                break;
            case IceLightningArchMage.HYPER_SPEED_500_50_5:
                break;
            case IceLightningArchMage.HYPER_STRENGTH_500_50_5:
                break;
            case IceLightningArchMage.INFINITY_1:
                pEffect.setHpR(pEffect.info.get(StatInfo.y) / 100.0);
                pEffect.setMpR(pEffect.info.get(StatInfo.y) / 100.0);
                pEffect.statups.put(CharacterTemporaryStat.Infinity, pEffect.info.get(StatInfo.x));
                pEffect.statups.put(CharacterTemporaryStat.Stance, (int) pEffect.info.get(StatInfo.prop));
                break;
            case IceLightningArchMage.LIGHTNING_ORB:
                break;
            case IceLightningArchMage.MAPLE_WARRIOR_10_1:
                pEffect.statups.put(CharacterTemporaryStat.BasicStatUp, pEffect.info.get(StatInfo.x));
                break;
            case IceLightningArchMage.TELEPORT_MASTERY_RANGE_1:
                break;
            case IceLightningArchMage.TELEPORT_MASTERY_RANGE_2:
                break;
            case IceLightningArchMage.TELEPORT_MASTERY_REINFORCE_1:
                break;
            case IceLightningArchMage.TELEPORT_MASTERY_SPREAD:
                break;

        }

    }

    @Override
    public boolean IsCorrectClass(int nClass) {
        return nClass == 222;
    }

}
