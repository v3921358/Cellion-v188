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
            if (pPlayer.getPrimaryStack() < 1000) {
                pPlayer.setPrimaryStack(pPlayer.getPrimaryStack() + 3);
            }
            SkillFactory.getSkill(61111008).getEffect(1).applyKaiserCombo(pPlayer, (short) pPlayer.getPrimaryStack());
        }

        public static void resetKaiserCombo(MapleCharacter pPlayer) {
            pPlayer.setPrimaryStack(0);
            SkillFactory.getSkill(61111008).getEffect(1).applyKaiserCombo(pPlayer, (short) pPlayer.getPrimaryStack());
        }
    }

}
