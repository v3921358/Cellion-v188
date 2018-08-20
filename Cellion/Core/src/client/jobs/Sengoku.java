/*
 * Cellion Development
 */
package client.jobs;

import client.CharacterTemporaryStat;
import client.SkillFactory;
import constants.GameConstants;
import constants.skills.Aran;
import constants.skills.Blaster;
import constants.skills.Hayato;
import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import server.StatEffect;
import enums.StatInfo;
import server.Timer;
import server.maps.objects.User;
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

        public static void handleBladeStance(User pPlayer) {
            int nStack;
            
            if (pPlayer.hasBuff(CharacterTemporaryStat.HayatoStance)) nStack = pPlayer.getPrimaryStack() + 2;   // Quick Draw Stance Gain
            else nStack = pPlayer.getPrimaryStack() + 5;                                                        // Normal Stance Gain
            
            if (nStack > 1000) nStack = 1000;
            updateBladeStanceRequest(pPlayer, nStack);
        }

        public static void updateBladeStanceRequest(User pPlayer, int nStack) {
            pPlayer.setPrimaryStack(nStack);
            pPlayer.getClient().SendPacket(HayatoPacket.SwordEnergy(nStack));
        }

    }
}
