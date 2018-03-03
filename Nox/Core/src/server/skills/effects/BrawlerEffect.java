package server.skills.effects;

import server.skills.effects.manager.AbstractEffect;
import client.CharacterTemporaryStat;
import client.MapleJob;
import client.MonsterStatus;
import constants.skills.Brawler;
import server.MapleStatEffect;
import server.MapleStatInfo;
import server.skills.effects.manager.Effect;

/**
 *
 * @author Novak
 *
 */
@Effect
public class BrawlerEffect extends AbstractEffect {

    @Override
    public void SetEffect(MapleStatEffect pEffect, int nSourceID) {
        switch (nSourceID) {
            case Brawler.BACKSPIN_BLOW_1:
                break;
            case Brawler.CORKSCREW_BLOW_2:
                break;
            case Brawler.CRITICAL_PUNCH_1:
                break;
            case Brawler.DARK_CLARITY_2:
                pEffect.statups.put(CharacterTemporaryStat.IndiePAD, pEffect.info.get(MapleStatInfo.indiePad));
                break;
            case Brawler.DARK_CLARITY_3:
                break;
            case Brawler.DOUBLE_UPPERCUT_1:
                break;
            case Brawler.ENERGY_CHARGE_2:
                break;
            case Brawler.ENERGY_VORTEX:
                break;
            case Brawler.HP_BOOST_5:
                break;
            case Brawler.KNUCKLE_BOOSTER_3:
                pEffect.statups.put(CharacterTemporaryStat.Booster, pEffect.info.get(MapleStatInfo.x));
                break;
            case Brawler.KNUCKLE_MASTERY_1:
                break;
            case Brawler.MP_RECOVERY_1:
                break;
            case Brawler.OAK_BARREL_1:
                break;
            case Brawler.PERSEVERANCE_1:
                break;
            case Brawler.PHYSICAL_TRAINING_40_4:
                break;
            case Brawler.TORNADO_UPPERCUT_2:
                break;

        }

    }

    @Override
    public boolean IsCorrectClass(int nClass) {
        return nClass == 510;
    }

}
