/*
 * Rexion Development
 */
package client.jobs;

import client.CharacterTemporaryStat;
import client.SkillFactory;
import constants.GameConstants;
import constants.skills.Aran;
import constants.skills.Phantom;
import static java.lang.Integer.max;
import static java.lang.Integer.min;
import java.util.Random;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadLocalRandom;
import server.MapleStatEffect;
import server.MapleStatInfo;
import server.Timer;
import server.maps.objects.User;
import tools.packet.BuffPacket;
import tools.packet.CField;
import tools.packet.JobPacket.LuminousPacket;
import tools.packet.JobPacket.PhantomPacket;

/**
 * Hero Class Handlers
 *
 * @author Mazen Massoud
 */
public class Hero {

    public static class AranHandler {

        public static void handleSwingStudies(User pPlayer) {
            if (pPlayer == null && !GameConstants.isAran(pPlayer.getJob()) && !pPlayer.hasSkill(Aran.SWING_STUDIES_I)) {
                return;
            }

            int nDuration = (pPlayer.getSkillLevel(Aran.SWING_STUDIES_I) > 0) ? 4000 : 2000;
            final MapleStatEffect buffEffects = SkillFactory.getSkill(Aran.SWING_STUDIES_I).getEffect(pPlayer.getTotalSkillLevel(Aran.SWING_STUDIES_I));

            buffEffects.statups.put(CharacterTemporaryStat.NextAttackEnhance, 1);

            final MapleStatEffect.CancelEffectAction cancelAction = new MapleStatEffect.CancelEffectAction(pPlayer, buffEffects, System.currentTimeMillis(), buffEffects.statups);
            final ScheduledFuture<?> buffSchedule = Timer.BuffTimer.getInstance().schedule(cancelAction, nDuration);
            pPlayer.registerEffect(buffEffects, System.currentTimeMillis(), buffSchedule, buffEffects.statups, false, nDuration, pPlayer.getId());
            pPlayer.getClient().write(BuffPacket.giveBuff(pPlayer, Aran.SWING_STUDIES_I, nDuration, buffEffects.statups, buffEffects));
        }

        public static void handleAdrenalineRush(User pPlayer) {
            if (pPlayer == null && !GameConstants.isAran(pPlayer.getJob()) && !pPlayer.hasSkill(Aran.ADRENALINE_RUSH)) {
                return;
            }

            int nDuration = 15000;
            final MapleStatEffect buffEffects = SkillFactory.getSkill(Aran.ADRENALINE_RUSH).getEffect(pPlayer.getTotalSkillLevel(Aran.ADRENALINE_RUSH));

            buffEffects.statups.put(CharacterTemporaryStat.AdrenalinBoost, buffEffects.info.get(MapleStatInfo.x));

            final MapleStatEffect.CancelEffectAction cancelAction = new MapleStatEffect.CancelEffectAction(pPlayer, buffEffects, System.currentTimeMillis(), buffEffects.statups);
            final ScheduledFuture<?> buffSchedule = Timer.BuffTimer.getInstance().schedule(cancelAction, nDuration);
            pPlayer.registerEffect(buffEffects, System.currentTimeMillis(), buffSchedule, buffEffects.statups, false, nDuration, pPlayer.getId());
            pPlayer.getClient().write(BuffPacket.giveBuff(pPlayer, Aran.ADRENALINE_RUSH, nDuration, buffEffects.statups, buffEffects));

            // Set combo down to 500 for when adrenaline rush is completed.
            pPlayer.setLastCombo(System.currentTimeMillis() + nDuration);
            pPlayer.setPrimaryStack(500);
            pPlayer.getClient().write(CField.updateCombo(500));
        }

        public static void handleComboAbility(User pPlayer, int nIncreaseCount) {
            if (pPlayer == null && !GameConstants.isAran(pPlayer.getJob()) && !pPlayer.hasSkill(Aran.COMBO_ABILITY)) {
                return;
            }
            short nCombo = (short) pPlayer.getPrimaryStack();
            long nCurrentTime = System.currentTimeMillis();

            if ((nCombo > 0) && (nCurrentTime - pPlayer.getLastComboTime() > 5000L)) {
                if ((nCombo - 10) > 0) { // Don't let combo go below zero.
                    nCombo -= 10; // Decrease Combo by 10 every 5 seconds.
                } else {
                    nCombo = 0;
                }
            }

            nIncreaseCount = ThreadLocalRandom.current().nextInt(3, 6); // TODO: Increase based off skill attack count.
            nCombo = (short) Math.min(30000, nCombo + nIncreaseCount);
            pPlayer.setLastCombo(nCurrentTime);
            pPlayer.setPrimaryStack(nCombo);

            // Write the combo update packet.
            pPlayer.getClient().write(CField.updateCombo(nCombo));

            // Apply the bonuses provided by the combo.
            final MapleStatEffect buffEffects = SkillFactory.getSkill(Aran.COMBO_ABILITY).getEffect(pPlayer.getTotalSkillLevel(Aran.COMBO_ABILITY));
            buffEffects.statups.put(CharacterTemporaryStat.ComboAbilityBuff, (int) nCombo);
            pPlayer.registerEffect(buffEffects, System.currentTimeMillis(), null, buffEffects.statups, false, buffEffects.info.get(MapleStatInfo.time), pPlayer.getId());
            pPlayer.getClient().write(BuffPacket.giveBuff(pPlayer, Aran.COMBO_ABILITY, 99999, buffEffects.statups, buffEffects));

            if (nCombo >= 998) { // Apply Adrenaline Rush upon reaching max stacks.
                nCombo = (short) 1000;
                pPlayer.setLastCombo(nCurrentTime);
                //pPlayer.setCombo(nCombo);
                pPlayer.getClient().write(CField.updateCombo(nCombo));

                handleAdrenalineRush(pPlayer);
            }
        }
    }

    public static class EvanHandler {

    }

    public static class LuminousHandler {
        
        public static void handleLuminousGauge(User pPlayer, int nSkillID) {
            int nLightGauge = pPlayer.getLarknessRequest(false, false);
            int nDarkGauge = pPlayer.getLarknessRequest(false, true);
            int nLightFeathers = pPlayer.getLarknessRequest(true, false);
            int nDarkFeathers = pPlayer.getLarknessRequest(true, true);
            
            if(GameConstants.isLightSkills(nSkillID)) {
                nLightGauge += 100;
                if (nLightGauge >= 10000) {
                    nLightGauge = 10000;
                    pPlayer.setLuminousState(20040216);
                }
                nLightFeathers += 1;
                if (nLightFeathers >= 5) {
                    nLightFeathers = 5;
                }
            } else if (GameConstants.isDarkSkills(nSkillID)) {
                nDarkGauge += 100;
                if (nDarkGauge >= 10000) {
                    nDarkGauge = 10000;
                    pPlayer.setLuminousState(20040216);
                }
                nDarkFeathers += 1;
                if (nDarkFeathers >= 5) {
                    nDarkFeathers = 5;
                }
            }
            
            if (nLightGauge >= 10000) {
                nLightGauge = 10000;
                nDarkGauge = 0;
                pPlayer.setLuminousState(20040216);
            } else if (nDarkGauge >= 10000) {
                nDarkGauge = 10000;
                nLightGauge = 0;
                pPlayer.setLuminousState(20040216);
            }
            
            pPlayer.setLarknessResult(nLightGauge, false, false);
            pPlayer.setLarknessResult(nDarkGauge, false, true);
            pPlayer.setLarknessResult(nLightFeathers, true, false);
            pPlayer.setLarknessResult(nDarkFeathers, true, true);
            pPlayer.write(LuminousPacket.updateLuminousGauge(nDarkGauge, nLightGauge, nDarkGauge, nLightGauge));
            pPlayer.write(LuminousPacket.setLarknessResult(pPlayer.getLuminousState(), nLightGauge, nDarkGauge, 2100000000));
            
            if (pPlayer.isDeveloper()) {
                pPlayer.dropMessage(5, "[Luminous Debug] Light: " + pPlayer.getLarknessRequest(false, false) + " / Dark: " + pPlayer.getLarknessRequest(false, true));
                pPlayer.dropMessage(5, "[Luminous Debug] Light Feathers: " + pPlayer.getLarknessRequest(true, false) + " / Dark Feather: " + pPlayer.getLarknessRequest(true, true));
            }
        }
    }
    
    public static class PhantomHandler {
        
        public static void handleDeck(User pPlayer) {
            int nCardCount = pPlayer.getPrimaryStack();
            if (nCardCount < 40) {
                nCardCount++;
            }
            updateDeckRequest(pPlayer, nCardCount);
        }
        
        public static void updateDeckRequest(User pPlayer, int nCardCount) {
            pPlayer.setPrimaryStack(nCardCount);
            pPlayer.getMap().broadcastMessage(PhantomPacket.updateCardStack(nCardCount));
        }
        
        public static void judgementDrawRequest(User pPlayer, int nSkillID) {
            Random pRandom = new Random();
            int nMinimumCard;
            int nMaximumCard;
            
            if (nSkillID == Phantom.JUDGMENT_DRAW_4) {
                nMinimumCard = 1;
                nMaximumCard = 4;
            } else {
                nMinimumCard = 1;
                nMaximumCard = 2;
            }
            int nDrawnCard = pRandom.nextInt((nMaximumCard - nMinimumCard) + 1) + nMinimumCard;
            
            final MapleStatEffect pEffect = SkillFactory.getSkill(nSkillID).getEffect(pPlayer.getTotalSkillLevel(nSkillID));

            switch (nDrawnCard) {
                case 1: // Destin Card
                    pPlayer.dropMessage(-1, "Destin Card Drawn");
                    pEffect.statups.put(CharacterTemporaryStat.CriticalBuff, 10);
                    break;
                case 2: // Malheur Card
                    pPlayer.dropMessage(-1, "Malheur Card Drawn");
                    pEffect.statups.put(CharacterTemporaryStat.DropRate, 10);
                    break;
                case 3: // Endurance Card
                    pPlayer.dropMessage(-1, "Endurance Card Drawn");
                    pEffect.statups.put(CharacterTemporaryStat.AsrR, 20);
                    pEffect.statups.put(CharacterTemporaryStat.TerR, 20);
                    break;
                case 4: // Drain Card
                    pPlayer.dropMessage(-1, "Drain Card Drawn");
                    pEffect.statups.put(CharacterTemporaryStat.IndieDrainHP, 1);
                    break;
            }
            
            final MapleStatEffect.CancelEffectAction cancelAction = new MapleStatEffect.CancelEffectAction(pPlayer, pEffect, System.currentTimeMillis(), pEffect.statups);
            final ScheduledFuture<?> buffSchedule = Timer.BuffTimer.getInstance().schedule(cancelAction, pEffect.info.get(MapleStatInfo.time));
            pPlayer.registerEffect(pEffect, System.currentTimeMillis(), buffSchedule, pEffect.statups, false, pEffect.info.get(MapleStatInfo.time), pPlayer.getId());
            pPlayer.getClient().write(BuffPacket.giveBuff(pPlayer, nSkillID, pEffect.info.get(MapleStatInfo.time), pEffect.statups, pEffect));
            
            updateDeckRequest(pPlayer, 0); // Reset the deck back to zero.
        }
    }
    
}
