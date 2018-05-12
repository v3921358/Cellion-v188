/*
 * Cellion Development
 */
package client.jobs;

import client.CharacterTemporaryStat;
import client.ClientSocket;
import client.SkillFactory;
import constants.GameConstants;
import constants.skills.DawnWarrior;
import constants.skills.NightWalker;
import constants.skills.ThunderBreaker;
import java.util.Random;
import java.util.concurrent.ScheduledFuture;
import server.StatEffect;
import server.StatInfo;
import server.Timer;
import server.maps.objects.User;
import tools.packet.BuffPacket;
import tools.packet.CField;

/**
 * Cygnus Knights Class Handlers
 *
 * @author Mazen Massoud
 */
public class Cygnus {

    public static class DawnWarriorHandler {

        public static void handleEquinox(User pPlayer) {
            
            if (pPlayer.hasBuff(CharacterTemporaryStat.GlimmeringTime)) {
                
                if (pPlayer.getBuffedValue(CharacterTemporaryStat.PoseType) == 1) { // Falling Moon
                    pPlayer.dispelBuff(DawnWarrior.FALLING_MOON);
                    final StatEffect pEffect = SkillFactory.getSkill(DawnWarrior.RISING_SUN).getEffect(pPlayer.getTotalSkillLevel(DawnWarrior.RISING_SUN));
                    
                    pEffect.statups.put(CharacterTemporaryStat.PoseType, 2); // Rising Sun Stance
                    /*pEffect.statups.put(CharacterTemporaryStat.IndieDamR, pEffect.info.get(MapleStatInfo.indieDamR));
                    pEffect.statups.put(CharacterTemporaryStat.IndieBooster, pEffect.info.get(MapleStatInfo.indieBooster));
                    pEffect.statups.put(CharacterTemporaryStat.IndieCr, pEffect.info.get(MapleStatInfo.indieCr));
                    pEffect.statups.put(CharacterTemporaryStat.BuckShot, 1);*/
                    
                    final StatEffect.CancelEffectAction cancelAction = new StatEffect.CancelEffectAction(pPlayer, pEffect, System.currentTimeMillis(), pEffect.statups);
                    final ScheduledFuture<?> buffSchedule = Timer.BuffTimer.getInstance().schedule(cancelAction, pEffect.info.get(StatInfo.time));
                    pPlayer.registerEffect(pEffect, System.currentTimeMillis(), buffSchedule, pEffect.statups, false, pEffect.info.get(StatInfo.time), pPlayer.getId());
                    pPlayer.getClient().SendPacket(BuffPacket.giveBuff(pPlayer, DawnWarrior.RISING_SUN, pEffect.info.get(StatInfo.time), pEffect.statups, pEffect));
                } else { // Rising Sun
                    pPlayer.dispelBuff(DawnWarrior.RISING_SUN);
                    final StatEffect pEffect = SkillFactory.getSkill(DawnWarrior.FALLING_MOON).getEffect(pPlayer.getTotalSkillLevel(DawnWarrior.FALLING_MOON));
                    
                    pEffect.statups.put(CharacterTemporaryStat.PoseType, 1); // Falling Moon Stance
                    /*pEffect.statups.put(CharacterTemporaryStat.IndieDamR, pEffect.info.get(MapleStatInfo.indieDamR));
                    pEffect.statups.put(CharacterTemporaryStat.IndieBooster, pEffect.info.get(MapleStatInfo.indieBooster));
                    pEffect.statups.put(CharacterTemporaryStat.IndieCr, pEffect.info.get(MapleStatInfo.indieCr));*/
                    pEffect.statups.put(CharacterTemporaryStat.BuckShot, 1);
                    
                    final StatEffect.CancelEffectAction cancelAction = new StatEffect.CancelEffectAction(pPlayer, pEffect, System.currentTimeMillis(), pEffect.statups);
                    final ScheduledFuture<?> buffSchedule = Timer.BuffTimer.getInstance().schedule(cancelAction, pEffect.info.get(StatInfo.time));
                    pPlayer.registerEffect(pEffect, System.currentTimeMillis(), buffSchedule, pEffect.statups, false, pEffect.info.get(StatInfo.time), pPlayer.getId());
                    pPlayer.getClient().SendPacket(BuffPacket.giveBuff(pPlayer, DawnWarrior.FALLING_MOON, pEffect.info.get(StatInfo.time), pEffect.statups, pEffect));
                }
            }
        }
    }

    public static class BlazeWizardHandler {

    }

    public static class WindArcherHandler {

    }

    public static class NightWalkerHandler {

        public static void handleDominionBuff(User pPlayer) {
            if (!GameConstants.isNightWalkerCygnus(pPlayer.getJob())) {
                return;
            }
            final StatEffect buffEffects = SkillFactory.getSkill(NightWalker.DOMINION).getEffect(pPlayer.getTotalSkillLevel(NightWalker.DOMINION));

            buffEffects.statups.put(CharacterTemporaryStat.DamR, 20);
            //buffEffects.statups.put(CharacterTemporaryStat.CriticalBuff, 100);
            //buffEffects.statups.put(CharacterTemporaryStat.Stance, 100);

            final StatEffect.CancelEffectAction cancelAction = new StatEffect.CancelEffectAction(pPlayer, buffEffects, System.currentTimeMillis(), buffEffects.statups);
            final ScheduledFuture<?> buffSchedule = Timer.BuffTimer.getInstance().schedule(cancelAction, 30000);
            pPlayer.registerEffect(buffEffects, System.currentTimeMillis(), buffSchedule, buffEffects.statups, false, 30000, pPlayer.getId());
            pPlayer.getClient().SendPacket(BuffPacket.giveBuff(pPlayer, 0, 30000, buffEffects.statups, buffEffects));
        }

    }

    public static class ThunderBreakerHandler {

        public static void handleLightningBuff(User pPlayer) {
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
                if (pPlayer.getPrimaryStack() < nMaxCount) {
                    pPlayer.setPrimaryStack(pPlayer.getPrimaryStack() + 1);
                } else {
                    pPlayer.setPrimaryStack(nMaxCount);
                }
            }

            final StatEffect buffEffects = SkillFactory.getSkill(ThunderBreaker.LIGHTNING_ELEMENTAL).getEffect(pPlayer.getTotalSkillLevel(ThunderBreaker.LIGHTNING_ELEMENTAL));

            buffEffects.statups.put(CharacterTemporaryStat.IgnoreTargetDEF, pPlayer.getPrimaryStack()); // TODO: Get charges stacking properly.
            buffEffects.statups.put(CharacterTemporaryStat.StrikerHyperElectric, 1); // Hack fix to allow the use of Gale/Typhoon.

            final StatEffect.CancelEffectAction cancelAction = new StatEffect.CancelEffectAction(pPlayer, buffEffects, System.currentTimeMillis(), buffEffects.statups);
            final ScheduledFuture<?> buffSchedule = Timer.BuffTimer.getInstance().schedule(cancelAction, buffEffects.info.get(StatInfo.time));
            pPlayer.registerEffect(buffEffects, System.currentTimeMillis(), buffSchedule, buffEffects.statups, false, buffEffects.info.get(StatInfo.time), pPlayer.getId());
            pPlayer.getClient().SendPacket(BuffPacket.giveBuff(pPlayer, ThunderBreaker.LIGHTNING_ELEMENTAL, buffEffects.info.get(StatInfo.time), buffEffects.statups, buffEffects));
        }
    }

    public static class MihileHandler {

    }
}
