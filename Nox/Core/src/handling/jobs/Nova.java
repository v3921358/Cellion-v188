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

        public static void handleKaiserCombo(MapleCharacter oPlayer) {
            if (oPlayer.getComboStack() < 1000) {
                oPlayer.setComboStack(oPlayer.getComboStack() + 3);
            }
            SkillFactory.getSkill(61111008).getEffect(1).applyKaiserCombo(oPlayer, (short) oPlayer.getComboStack());
        }

        public static void resetKaiserCombo(MapleCharacter oPlayer) {
            oPlayer.setComboStack(0);
            SkillFactory.getSkill(61111008).getEffect(1).applyKaiserCombo(oPlayer, (short) oPlayer.getComboStack());
        }
    }

}
