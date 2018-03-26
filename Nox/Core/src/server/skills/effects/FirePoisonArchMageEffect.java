package server.skills.effects;

import server.skills.effects.manager.AbstractEffect;
import client.CharacterTemporaryStat;
import client.MapleJob;
import client.MonsterStatus;
import constants.skills.FirePoisonArchMage;
import server.MapleStatEffect;
import server.MapleStatInfo;
import server.skills.effects.manager.Effect;

/**
 *
 * @author Novak
 *
 */
@Effect
public class FirePoisonArchMageEffect extends AbstractEffect {

    @Override
    public void SetEffect(MapleStatEffect pEffect, int nSourceID) {
        switch (nSourceID) {
            case FirePoisonArchMage.ARCANE_AIM:
                break;
            case FirePoisonArchMage.BIG_BANG:
                break;
            case FirePoisonArchMage.BUFF_MASTERY:
                break;
            case FirePoisonArchMage.BUFF_MASTERY_1:
                break;
            case FirePoisonArchMage.EPIC_ADVENTURE_3:
                pEffect.statups.put(CharacterTemporaryStat.DamR, pEffect.info.get(MapleStatInfo.indieDamR));
                break;
            case FirePoisonArchMage.FERVENT_DRAIN:
                break;
            case FirePoisonArchMage.FLAME_HAZE:
                break;
            case FirePoisonArchMage.HEROS_WILL_5:
                break;
            case FirePoisonArchMage.HYPER_ACCURACY_5:
                break;
            case FirePoisonArchMage.HYPER_CRITICAL_5:
                break;
            case FirePoisonArchMage.HYPER_DEFENSE_4:
                break;
            case FirePoisonArchMage.HYPER_DEXTERITY_5:
                break;
            case FirePoisonArchMage.HYPER_FURY_5:
                break;
            case FirePoisonArchMage.HYPER_HEALTH_5:
                break;
            case FirePoisonArchMage.HYPER_INTELLIGENCE_5:
                break;
            case FirePoisonArchMage.HYPER_JUMP_5:
                break;
            case FirePoisonArchMage.HYPER_LUCK_5:
                break;
            case FirePoisonArchMage.HYPER_MAGIC_DEFENSE_5:
                break;
            case FirePoisonArchMage.HYPER_MANA_5:
                break;
            case FirePoisonArchMage.HYPER_SPEED_5:
                break;
            case FirePoisonArchMage.HYPER_STRENGTH_5:
                break;
            case FirePoisonArchMage.IFRIT:
                pEffect.statups.put(CharacterTemporaryStat.SUMMON, 1);
                pEffect.monsterStatus.put(MonsterStatus.STUN, 1);
                break;
            case FirePoisonArchMage.INFERNO_AURA:
                break;
            case FirePoisonArchMage.INFINITY:
                pEffect.setHpR(pEffect.info.get(MapleStatInfo.y) / 100.0);
                pEffect.setMpR(pEffect.info.get(MapleStatInfo.y) / 100.0);
                pEffect.statups.put(CharacterTemporaryStat.Infinity, pEffect.info.get(MapleStatInfo.x));
                pEffect.statups.put(CharacterTemporaryStat.Stance, (int) pEffect.info.get(MapleStatInfo.prop));
                break;
            case FirePoisonArchMage.MAPLE_WARRIOR_5:
                pEffect.statups.put(CharacterTemporaryStat.BasicStatUp, pEffect.info.get(MapleStatInfo.x));
                break;
            case FirePoisonArchMage.MEGIDDO_FLAME:
                break;
            case FirePoisonArchMage.MEGIDDO_FLAME_1:
                break;
            case FirePoisonArchMage.METEOR_SHOWER:
                break;
            case FirePoisonArchMage.METEOR_SHOWER_1:
                break;
            case FirePoisonArchMage.MIST_ERUPTION:
                break;
            case FirePoisonArchMage.MIST_ERUPTION_COOLDOWN_CUTTER:
                break;
            case FirePoisonArchMage.MIST_ERUPTION_GUARDBREAKER:
                break;
            case FirePoisonArchMage.MIST_ERUPTION_REINFORCE:
                break;
            case FirePoisonArchMage.PARALYZE:
                pEffect.monsterStatus.put(MonsterStatus.FREEZE, 1);
                pEffect.info.put(MapleStatInfo.time, pEffect.info.get(MapleStatInfo.time) * 2);
                break;
            case FirePoisonArchMage.PARALYZE_CRIPPLE:
                break;
            case FirePoisonArchMage.PARALYZE_EXTRA_STRIKE:
                break;
            case FirePoisonArchMage.PARALYZE_REINFORCE:
                break;
            case FirePoisonArchMage.POISON_MIST_AFTERMATH:
                break;
            case FirePoisonArchMage.POISON_MIST_CRIPPLE:
                break;
            case FirePoisonArchMage.POISON_MIST_REINFORCE:
                break;

        }

    }

    @Override
    public boolean IsCorrectClass(int nClass) {
        return nClass == 212;
    }

}
