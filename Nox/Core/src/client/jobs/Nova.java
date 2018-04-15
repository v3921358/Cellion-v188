/*
 * Cellion Development
 */
package client.jobs;

import client.SkillFactory;
import server.maps.objects.User;

/**
 * Nova Class Handlers
 *
 * @author Mazen
 */
public class Nova {

    public static class KaiserHandler {

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
