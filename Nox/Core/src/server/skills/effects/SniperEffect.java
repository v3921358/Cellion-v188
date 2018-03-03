package server.skills.effects;

import server.skills.effects.manager.AbstractEffect;
import client.CharacterTemporaryStat;
import client.MapleJob;
import client.MonsterStatus;
import constants.skills.Sniper;
import server.MapleStatEffect;
import server.MapleStatInfo;
import server.skills.effects.manager.Effect;

/**
 *
 * @author Novak
 *
 */
@Effect
public class SniperEffect extends AbstractEffect {

    @Override
    public void SetEffect(MapleStatEffect pEffect, int nSourceID) {
        switch (nSourceID) {
            case Sniper.AGGRESSIVE_RESISTANCE:
                break;
            case Sniper.ARROW_ERUPTION:
                break;
            case Sniper.CONCENTRATE_3:
                break;
            case Sniper.DRAGONS_BREATH:
                break;
            case Sniper.EVASION_BOOST:
                break;
            case Sniper.EXPLOSIVE_BOLT:
                break;
            case Sniper.FREEZER:
                pEffect.statups.put(CharacterTemporaryStat.SUMMON, 1);
                pEffect.monsterStatus.put(MonsterStatus.STUN, 1);
                break;
            case Sniper.HOOKSHOT_1:
                break;
            case Sniper.MARKSMANSHIP_1:
                break;
            case Sniper.MORTAL_BLOW:
                break;
            case Sniper.PAIN_KILLER:
                pEffect.statups.put(CharacterTemporaryStat.KeyDownAreaMoving, pEffect.info.get(MapleStatInfo.asrR));
                pEffect.statups.put(CharacterTemporaryStat.KeyDownAreaMoving, pEffect.info.get(MapleStatInfo.terR));
                break;
            case Sniper.PUPPET_2:
                break;
            case Sniper.RECKLESS_HUNT_CROSSBOW:
                pEffect.statups.put(CharacterTemporaryStat.DamR, pEffect.info.get(MapleStatInfo.indieDamR));
                pEffect.statups.put(CharacterTemporaryStat.PAD, pEffect.info.get(MapleStatInfo.padX));
                break;
            case Sniper.SNAPFREEZE_SHOT:
                break;
            case Sniper.STRAFE_2:
                break;

        }

    }

    @Override
    public boolean IsCorrectClass(int nClass) {
        return nClass == 321;
    }

}
