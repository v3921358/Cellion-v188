/*
 * Rexion Development
 */
package handling.jobs;

import client.CharacterTemporaryStat;
import handling.jobs.*;
import client.SkillFactory;
import constants.GameConstants;
import constants.skills.Aran;
import constants.skills.Blaster;
import constants.skills.Hayato;
import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import server.MapleStatEffect;
import server.MapleStatInfo;
import server.Timer;
import server.maps.objects.MapleCharacter;
import tools.packet.BuffPacket;
import tools.packet.JobPacket;
import tools.packet.JobPacket.BlasterPacket;
import tools.packet.JobPacket.HayatoPacket;

/**
 * Sengoku Class Handlers
 *
 * @author Mazen Massoud
 */
public class Sengoku {

    public static class HayatoHandler {
        
        public static void handleBladeStance(MapleCharacter pPlayer) {
            int nStack = pPlayer.getPrimaryStack() + 5;
            if (nStack > 1000) {
                nStack = 1000;
            }
            updateBladeStanceRequest(pPlayer, nStack);
        }
        
        public static void updateBladeStanceRequest(MapleCharacter pPlayer, int nStack) {
            pPlayer.setPrimaryStack(nStack);
            pPlayer.getClient().write(HayatoPacket.SwordEnergy(nStack));
        }
    
    }
}