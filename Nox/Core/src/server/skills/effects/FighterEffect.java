package server.skills.effects;

import server.skills.effects.manager.AbstractEffect;
import client.CharacterTemporaryStat;
import client.MapleJob;
import client.MonsterStatus;
import constants.skills.Fighter;
import server.MapleStatEffect;
import server.MapleStatInfo;
import server.skills.effects.manager.Effect;

/**
 *
 * @author Novak
 *
 */
@Effect
public class FighterEffect extends AbstractEffect {

    @Override
    public void SetEffect(MapleStatEffect pEffect, int nSourceID) {
        switch (nSourceID) {
            case Fighter.BRANDISH:
                break;
            case Fighter.COMBO_ATTACK:
                pEffect.statups.put(CharacterTemporaryStat.ComboCounter, 0);
                pEffect.info.put(MapleStatInfo.time, 2100000000);
                break;
            case Fighter.COMBO_FURY:
                break;
            case Fighter.COMBO_FURY_1:
                break;
            case Fighter.FINAL_ATTACK_6:
                break;
            case Fighter.GROUND_SMASH:
                break;
            case Fighter.PHYSICAL_TRAINING_100_10:
                break;
            case Fighter.POWER_REFLECTION:
                break;
            case Fighter.RAGE:
                pEffect.statups.put(CharacterTemporaryStat.PAD, pEffect.info.get(MapleStatInfo.indiePad));
                pEffect.statups.put(CharacterTemporaryStat.PowerGuard, pEffect.info.get(MapleStatInfo.x));
                break;
            case Fighter.SLIPSTREAM:
                break;
            case Fighter.WEAPON_BOOSTER:
                pEffect.statups.put(CharacterTemporaryStat.IndieBooster, pEffect.info.get(MapleStatInfo.indieBooster));
                pEffect.statups.put(CharacterTemporaryStat.Booster, pEffect.info.get(MapleStatInfo.x) * 2);
                break;
            case Fighter.WEAPON_MASTERY_3:
                break;

        }

    }

    @Override
    public boolean IsCorrectClass(int nClass) {
        return nClass == 110;
    }

}
