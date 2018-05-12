package server.skills.effects;

import server.skills.effects.manager.AbstractEffect;
import client.CharacterTemporaryStat;
import client.MapleJob;
import client.MonsterStatus;
import constants.skills.Hermit;
import server.StatEffect;
import server.StatInfo;
import server.skills.effects.manager.Effect;

/**
 *
 * @author Novak
 *
 */
@Effect
public class HermitEffect extends AbstractEffect {

    @Override
    public void SetEffect(StatEffect pEffect, int nSourceID) {
        switch (nSourceID) {
            case Hermit.ALCHEMIC_ADRENALINE_2:
                break;
            case Hermit.ALCHEMIST_1:
                break;
            case Hermit.AVENGER_1:
                break;
            case Hermit.DARK_FLARE_2:
                pEffect.statups.put(CharacterTemporaryStat.SUMMON, 1);
                break;
            case Hermit.ENVELOPING_DARKNESS_2:
                break;
            case Hermit.EXPERT_THROWING_STAR_HANDLING:
                break;
            case Hermit.MESO_UP:
                break;
            case Hermit.SHADE_SPLITTER_1:
                break;
            case Hermit.SHADE_SPLITTER_2:
                break;
            case Hermit.SHADOW_MESO:
                break;
            case Hermit.SHADOW_PARTNER_2:
                pEffect.statups.put(CharacterTemporaryStat.ShadowPartner, pEffect.info.get(StatInfo.x));
                break;
            case Hermit.SHADOW_STARS_1:
                pEffect.statups.put(CharacterTemporaryStat.NoBulletConsume, 0);
                break;
            case Hermit.SHADOW_WEB_1:
                pEffect.monsterStatus.put(MonsterStatus.SHADOW_WEB, 1);
                break;
            case Hermit.TRIPLE_THROW_3:
                break;
            case Hermit.VENOM_3:
                break;

        }

    }

    @Override
    public boolean IsCorrectClass(int nClass) {
        return nClass == 411;
    }

}
