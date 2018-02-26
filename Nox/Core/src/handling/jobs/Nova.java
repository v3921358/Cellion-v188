/*
 * Rexion Development
 */
package handling.jobs;

import handling.jobs.*;
import client.SkillFactory;
import server.maps.objects.MapleCharacter;

/**
 * Nova Class Handlers
 *
 * @author Mazen
 */
public class Nova {

    public static class KaiserHandler {

        public static void handleKaiserCombo(MapleCharacter pPlayer) {
            if (pPlayer.getComboStack() < 1000) {
                pPlayer.setComboStack(pPlayer.getComboStack() + 3);
            }
            SkillFactory.getSkill(61111008).getEffect(1).applyKaiserCombo(pPlayer, (short) pPlayer.getComboStack());
        }

        public static void resetKaiserCombo(MapleCharacter pPlayer) {
            pPlayer.setComboStack(0);
            SkillFactory.getSkill(61111008).getEffect(1).applyKaiserCombo(pPlayer, (short) pPlayer.getComboStack());
        }
    }

}
