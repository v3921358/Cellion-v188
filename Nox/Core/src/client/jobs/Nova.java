/*
 * Cellion Development
 */
package client.jobs;

import client.CharacterTemporaryStat;
import client.SkillFactory;
import constants.skills.Kaiser;
import server.maps.objects.User;

/**
 * Nova Class Handlers
 *
 * @author Mazen Massoud
 */
public class Nova {

    public static class KaiserHandler {

        public static int getTempestBladeSkill(User pPlayer) {
            int nSkillID = 0;
            if(pPlayer.hasSkill(Kaiser.TEMPEST_BLADES_1)) {
                nSkillID = Kaiser.TEMPEST_BLADES_1;
            }
            if(pPlayer.hasSkill(Kaiser.TEMPEST_BLADES) && pPlayer.hasBuff(CharacterTemporaryStat.Morph)) {
                nSkillID = Kaiser.TEMPEST_BLADES;
            }
            if(pPlayer.hasSkill(Kaiser.ADVANCED_TEMPEST_BLADES_1)) {
                nSkillID = Kaiser.ADVANCED_TEMPEST_BLADES_1;
            }
            if(pPlayer.hasSkill(Kaiser.ADVANCED_TEMPEST_BLADES) && pPlayer.hasBuff(CharacterTemporaryStat.Morph)) {
                nSkillID = Kaiser.ADVANCED_TEMPEST_BLADES;
            }
            return nSkillID;
        }
        
        public static void handleKaiserCombo(User pPlayer) {
            if (pPlayer.getPrimaryStack() < 1000) {
                pPlayer.setPrimaryStack(pPlayer.getPrimaryStack() + 3);
            }
            SkillFactory.getSkill(61111008).getEffect(1).applyKaiserCombo(pPlayer, (short) pPlayer.getPrimaryStack());
        }

        public static void resetKaiserCombo(User pPlayer) {
            pPlayer.setPrimaryStack(0);
            SkillFactory.getSkill(61111008).getEffect(1).applyKaiserCombo(pPlayer, (short) pPlayer.getPrimaryStack());
        }
    }

}
