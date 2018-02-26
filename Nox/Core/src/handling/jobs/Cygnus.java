/*
 * Rexion Development
 */
package handling.jobs;

import client.CharacterTemporaryStat;
import client.MapleClient;
import client.SkillFactory;
import constants.GameConstants;
import constants.skills.Aran;
import constants.skills.ThunderBreaker;
import java.util.Random;
import java.util.concurrent.ScheduledFuture;
import server.MapleStatEffect;
import server.MapleStatInfo;
import server.Timer;
import server.maps.objects.MapleCharacter;
import tools.packet.BuffPacket;
import tools.packet.CField;

/**
 * Cygnus Knights Class Handlers
 *
 * @author Mazen Massoud
 */
public class Cygnus {

    public static class DawnWarriorHandler {

    }

    public static class BlazeWizardHandler {

    }

    public static class WindArcherHandler {

    }

    public static class NightWalkerHandler {

    }

    public static class ThunderBreakerHandler {

        public static void handleLightningBuff(MapleCharacter pPlayer) {
            if (pPlayer == null && !GameConstants.isThunderBreakerCygnus(pPlayer.getJob()) && !pPlayer.hasSkill(ThunderBreaker.LIGHTNING_ELEMENTAL)) {
                return;
            }
            
            int nMaxCount = 1;
            double nChargeProp = 0.10;
            
            // Determining Maximum Amount of Lightning Buffs
            if (pPlayer.hasSkill(ThunderBreaker.ELECTRIFIED)) {
                nMaxCount++; // 2 Total Charges
                nChargeProp += 0.20; // 30% Total
            }
            if (pPlayer.hasSkill(ThunderBreaker.LIGHTNING_BOOST)) {
                nMaxCount++; // 3 Total Charges
                nChargeProp += 0.20; // 50% Total
            }
            if (pPlayer.hasSkill(ThunderBreaker.LIGHTNING_LORD)) {
                nMaxCount++; // 4 Total Charges
                nChargeProp += 0.30; // 80% Total
            }
            if (pPlayer.hasSkill(ThunderBreaker.THUNDER_GOD)) {
                nMaxCount++; // 5 Total Charges
                nChargeProp += 0.20; // 100% Total
            }
            
            // Handle Chance to Gain Lightning Buff
            if (new Random().nextDouble() <= nChargeProp) {
              
                // Update Lightning Buff Count
                if (pPlayer.getComboStack() < nMaxCount) {
                    pPlayer.setComboStack(pPlayer.getComboStack() + 1);
                } else {
                    pPlayer.setComboStack(nMaxCount);
                }
            }
            
            final MapleStatEffect buffEffects = SkillFactory.getSkill(ThunderBreaker.LIGHTNING_ELEMENTAL).getEffect(pPlayer.getTotalSkillLevel(ThunderBreaker.LIGHTNING_ELEMENTAL));

            buffEffects.statups.put(CharacterTemporaryStat.IgnoreTargetDEF, pPlayer.getComboStack()); // TODO: Get charges stacking properly.
            buffEffects.statups.put(CharacterTemporaryStat.StrikerHyperElectric, 1); // Hack fix to allow the use of Gale/Typhoon.

            final MapleStatEffect.CancelEffectAction cancelAction = new MapleStatEffect.CancelEffectAction(pPlayer, buffEffects, System.currentTimeMillis(), buffEffects.statups);
            final ScheduledFuture<?> buffSchedule = Timer.BuffTimer.getInstance().schedule(cancelAction, buffEffects.info.get(MapleStatInfo.time));
            pPlayer.registerEffect(buffEffects, System.currentTimeMillis(), buffSchedule, buffEffects.statups, false, buffEffects.info.get(MapleStatInfo.time), pPlayer.getId());
            pPlayer.getClient().write(BuffPacket.giveBuff(pPlayer, ThunderBreaker.LIGHTNING_ELEMENTAL, buffEffects.info.get(MapleStatInfo.time), buffEffects.statups, buffEffects));
        }
    }

    public static class MihileHandler {

    }
}
