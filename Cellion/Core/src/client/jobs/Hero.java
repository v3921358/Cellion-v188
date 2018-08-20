/*
 * Cellion Development
 */
package client.jobs;

import client.CharacterTemporaryStat;
import client.SkillFactory;
import constants.GameConstants;
import constants.skills.Aran;
import constants.skills.Phantom;
import constants.skills.Shade;
import enums.ForceAtomType;
import static java.lang.Integer.max;
import static java.lang.Integer.min;
import java.util.Random;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadLocalRandom;
import server.StatEffect;
import enums.StatInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Arrays;
import server.Timer;
import server.life.Mob;
import server.maps.MapleMapObject;
import server.maps.MapleMapObjectType;
import server.maps.objects.ForceAtom;
import server.maps.objects.User;
import tools.Utility;
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
            final StatEffect buffEffects = SkillFactory.getSkill(Aran.SWING_STUDIES_I).getEffect(pPlayer.getTotalSkillLevel(Aran.SWING_STUDIES_I));

            buffEffects.statups.put(CharacterTemporaryStat.NextAttackEnhance, 1);

            final StatEffect.CancelEffectAction cancelAction = new StatEffect.CancelEffectAction(pPlayer, buffEffects, System.currentTimeMillis(), buffEffects.statups);
            final ScheduledFuture<?> buffSchedule = Timer.BuffTimer.getInstance().schedule(cancelAction, nDuration);
            pPlayer.registerEffect(buffEffects, System.currentTimeMillis(), buffSchedule, buffEffects.statups, false, nDuration, pPlayer.getId());
            pPlayer.getClient().SendPacket(BuffPacket.giveBuff(pPlayer, Aran.SWING_STUDIES_I, nDuration, buffEffects.statups, buffEffects));
        }

        public static void handleAdrenalineRush(User pPlayer) {
            if (pPlayer == null && !GameConstants.isAran(pPlayer.getJob()) && !pPlayer.hasSkill(Aran.ADRENALINE_RUSH)) {
                return;
            }

            int nDuration = 15000;
            final StatEffect buffEffects = SkillFactory.getSkill(Aran.ADRENALINE_RUSH).getEffect(pPlayer.getTotalSkillLevel(Aran.ADRENALINE_RUSH));

            buffEffects.statups.put(CharacterTemporaryStat.AdrenalinBoost, buffEffects.info.get(StatInfo.x));

            final StatEffect.CancelEffectAction cancelAction = new StatEffect.CancelEffectAction(pPlayer, buffEffects, System.currentTimeMillis(), buffEffects.statups);
            final ScheduledFuture<?> buffSchedule = Timer.BuffTimer.getInstance().schedule(cancelAction, nDuration);
            pPlayer.registerEffect(buffEffects, System.currentTimeMillis(), buffSchedule, buffEffects.statups, false, nDuration, pPlayer.getId());
            pPlayer.getClient().SendPacket(BuffPacket.giveBuff(pPlayer, Aran.ADRENALINE_RUSH, nDuration, buffEffects.statups, buffEffects));

            // Set combo down to 500 for when adrenaline rush is completed.
            pPlayer.setLastCombo(System.currentTimeMillis() + nDuration);
            pPlayer.setPrimaryStack(500);
            pPlayer.getClient().SendPacket(CField.updateCombo(500));
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
            pPlayer.getClient().SendPacket(CField.updateCombo(nCombo));

            // Apply the bonuses provided by the combo.
            final StatEffect buffEffects = SkillFactory.getSkill(Aran.COMBO_ABILITY).getEffect(pPlayer.getTotalSkillLevel(Aran.COMBO_ABILITY));
            buffEffects.statups.put(CharacterTemporaryStat.ComboAbilityBuff, (int) nCombo);
            pPlayer.registerEffect(buffEffects, System.currentTimeMillis(), null, buffEffects.statups, false, buffEffects.info.get(StatInfo.time), pPlayer.getId());
            pPlayer.getClient().SendPacket(BuffPacket.giveBuff(pPlayer, Aran.COMBO_ABILITY, 99999, buffEffects.statups, buffEffects));

            if (nCombo >= 998) { // Apply Adrenaline Rush upon reaching max stacks.
                nCombo = (short) 1000;
                pPlayer.setLastCombo(nCurrentTime);
                //pPlayer.setCombo(nCombo);
                pPlayer.getClient().SendPacket(CField.updateCombo(nCombo));

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

            if (GameConstants.isLightSkills(nSkillID)) {
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
            pPlayer.getMap().broadcastPacket(PhantomPacket.updateCardStack(nCardCount));
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

            final StatEffect pEffect = SkillFactory.getSkill(nSkillID).getEffect(pPlayer.getTotalSkillLevel(nSkillID));

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

            final StatEffect.CancelEffectAction cancelAction = new StatEffect.CancelEffectAction(pPlayer, pEffect, System.currentTimeMillis(), pEffect.statups);
            final ScheduledFuture<?> buffSchedule = Timer.BuffTimer.getInstance().schedule(cancelAction, pEffect.info.get(StatInfo.time));
            pPlayer.registerEffect(pEffect, System.currentTimeMillis(), buffSchedule, pEffect.statups, false, pEffect.info.get(StatInfo.time), pPlayer.getId());
            pPlayer.getClient().SendPacket(BuffPacket.giveBuff(pPlayer, nSkillID, pEffect.info.get(StatInfo.time), pEffect.statups, pEffect));

            updateDeckRequest(pPlayer, 0); // Reset the deck back to zero.
        }
    }
    
    public static class ShadeHandler {
        
        public static void handleFoxSpirits(User pPlayer, int nSkillID) {
            if (pPlayer.hasBuff(CharacterTemporaryStat.HiddenPossession)) {
                if (Utility.resultSuccess(40) && nSkillID != Shade.FOX_SPIRITS_ATOM && nSkillID != Shade.FOX_SPIRITS_ATOM_2) {
                    ArrayList<Mob> pSpiritTargets = new ArrayList<Mob>();
                    for (MapleMapObject pMobs : pPlayer.getMap().getMapObjectsInRange(pPlayer.getPosition(), 100000, Arrays.asList(MapleMapObjectType.MONSTER))) {
                        Mob pSpiritTarget = (Mob) pMobs;
                        pSpiritTargets.add(pSpiritTarget);
                    }
                    
                    if (!pSpiritTargets.isEmpty()) {
                        Mob pTargetMonster = pSpiritTargets.get(Utility.getRandomSelection(pSpiritTargets.size()));
                        int nAngle = 305;
                        int dwMobID = pTargetMonster.getObjectId();

                        int nAtomID = Shade.FOX_SPIRITS_ATOM;
                        int nInc = ForceAtomType.RABBIT_ORB.getInc();
                        int nType = ForceAtomType.RABBIT_ORB.getForceAtomType();
                        if(pPlayer.hasSkill(Shade.FIRE_FOX_SPIRIT_MASTERY)) {
                            nAtomID = Shade.FOX_SPIRITS_ATOM_2;
                            nInc = ForceAtomType.FLAMING_RABBIT_ORB.getInc();
                            nType = ForceAtomType.FLAMING_RABBIT_ORB.getForceAtomType();
                        }

                        ForceAtom forceAtomInfo = new ForceAtom(1, nInc, 15, 7, nAngle, 400, (int) System.currentTimeMillis(), 1, 0, new Point(-50, -50));
                        pPlayer.getMap().broadcastPacket(CField.createForceAtom(false, 0, pPlayer.getId(), nType,true, dwMobID, nAtomID, forceAtomInfo, new Rectangle(), 0, 300, pTargetMonster.getPosition(), nAtomID, pTargetMonster.getPosition()));
                    }
                }
            }
        }
    }

}
