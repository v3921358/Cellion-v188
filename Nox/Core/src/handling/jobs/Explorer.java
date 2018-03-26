/*
 * Rexion Development
 */
package handling.jobs;

import client.CharacterTemporaryStat;
import client.MapleClient;
import client.SkillFactory;
import constants.GameConstants;
import constants.skills.*;
import java.util.EnumMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ScheduledFuture;
import server.MapleStatEffect;
import server.MapleStatInfo;
import server.Randomizer;
import server.Timer;
import server.maps.objects.MapleCharacter;
import tools.packet.BuffPacket;
import tools.packet.CField;
import tools.packet.JobPacket;
import tools.packet.JobPacket.ShadowerPacket;

/**
 * Explorer Class Handlers
 *
 * @author Mazen Massoud
 */
public class Explorer {
    
    public static class HeroHandler {
        
        public static void handleComboOrbs(MapleCharacter pPlayer, int nSkillID) {
            if (pPlayer == null && !GameConstants.isWarriorHero(pPlayer.getJob()) && !pPlayer.hasSkill(Fighter.COMBO_ATTACK)) {
                return;
            }
            
            int nCombo = pPlayer.getPrimaryStack();
            int nMaxOrbs = getMaxOrbs(pPlayer);
            
            switch (nSkillID) {
                case Crusader.PANIC:
                    nCombo -= 1;
                    break;
                case Crusader.SHOUT:
                    nCombo -= 2;
                    break;
                case constants.skills.Hero.PUNCTURE:
                    nCombo -= 2;
                    break;
                case constants.skills.Hero.ENRAGE:
                    nCombo -= 1;
                    break;
            }
            
            if (nCombo > nMaxOrbs) {
                nCombo = nMaxOrbs;
            } else if (nCombo < 0) {
                nCombo = 0;
            }
            
            pPlayer.setPrimaryStack(nCombo);
            setComboAttack(pPlayer, nCombo);
        }
        
        public static void handleComboAttack(MapleCharacter pPlayer) {
            if (pPlayer == null && !GameConstants.isWarriorHero(pPlayer.getJob()) && !pPlayer.hasSkill(Fighter.COMBO_ATTACK)) {
                return;
            }
            
            int nCombo = pPlayer.getPrimaryStack();
            int nGainChance = 0;
            int nMaxOrbs = getMaxOrbs(pPlayer);
            boolean bDoubleChance = false;
            
            if (pPlayer.hasSkill(Fighter.COMBO_ATTACK)) {
                nGainChance = 40; // 40%
            }
            if (pPlayer.hasSkill(Crusader.COMBO_SYNERGY)) {
                nGainChance = 80; // 80%
            }
            if (pPlayer.hasSkill(constants.skills.Hero.ADVANCED_COMBO)) {
                bDoubleChance = true;
            }
            
            if (Randomizer.nextInt(100) < nGainChance) {
                if (pPlayer.getPrimaryStack() + 1 > nMaxOrbs) {
                    nCombo = nMaxOrbs;
                } else {
                    nCombo++;
                }
            } else {
                if (bDoubleChance) { // Re-roll chance if player has Advanced Combo.
                    if (pPlayer.getPrimaryStack() + 1> nMaxOrbs) {
                        nCombo = nMaxOrbs;
                    } else {
                        nCombo++;
                    }
                }
            }
            
            pPlayer.setPrimaryStack(nCombo);
            setComboAttack(pPlayer, nCombo);
        }
        
        public static void setComboAttack(MapleCharacter pPlayer, int nAmount) {
            if (pPlayer == null && !GameConstants.isWarriorHero(pPlayer.getJob()) && !pPlayer.hasSkill(Fighter.COMBO_ATTACK)) {
                return;
            }
            
            int nDuration = 210000000;
            final MapleStatEffect buffEffects = SkillFactory.getSkill(Fighter.COMBO_ATTACK).getEffect(pPlayer.getTotalSkillLevel(Fighter.COMBO_ATTACK));

            buffEffects.statups.put(CharacterTemporaryStat.ComboCounter, nAmount);

            final MapleStatEffect.CancelEffectAction cancelAction = new MapleStatEffect.CancelEffectAction(pPlayer, buffEffects, System.currentTimeMillis(), buffEffects.statups);
            final ScheduledFuture<?> buffSchedule = Timer.BuffTimer.getInstance().schedule(cancelAction, nDuration);
            pPlayer.registerEffect(buffEffects, System.currentTimeMillis(), buffSchedule, buffEffects.statups, false, nDuration, pPlayer.getId());
            pPlayer.getClient().write(BuffPacket.giveBuff(pPlayer, Fighter.COMBO_ATTACK, nDuration, buffEffects.statups, buffEffects));
        }
        
        public static int getMaxOrbs(MapleCharacter pPlayer) {
            int nMaxOrbs = 0;
            
            if (pPlayer.hasSkill(Fighter.COMBO_ATTACK)) {
                nMaxOrbs = 4;
            }
            if (pPlayer.hasSkill(constants.skills.Hero.ADVANCED_COMBO)) {
                nMaxOrbs = 9;
            }
            
            return nMaxOrbs;
        }
    }   
    
    public static class ShadowerHandler {
        
        public static void handleBodyCount(MapleCharacter pPlayer) {
            if (pPlayer == null && !GameConstants.isThiefShadower(pPlayer.getJob()) && !pPlayer.hasSkill(Shadower.SHADOWER_INSTINCT)) {
                return;
            }
            
            int nBodyCount = pPlayer.getPrimaryStack();
            
            if (nBodyCount < 5) {
                nBodyCount++;
                pPlayer.setPrimaryStack(nBodyCount);
                
                if (nBodyCount == 5) {
                    pPlayer.dropMessage(-1, "Maximum Body Count Reached");
                }
            }
            
            ShadowerPacket.setKillingPoint(nBodyCount);
        }
        
        public static void handleShadowerInstinct(MapleCharacter pPlayer) {
            int nBodyCount = pPlayer.getPrimaryStack();
            
            // Apply the respected buff bonuses to the player.
            final MapleStatEffect pEffect = SkillFactory.getSkill(Shadower.SHADOWER_INSTINCT).getEffect(pPlayer.getTotalSkillLevel(Shadower.SHADOWER_INSTINCT));

            pEffect.statups.put(CharacterTemporaryStat.IgnoreMobpdpR, pPlayer.getSkillLevel(Shadower.SHADOWER_INSTINCT));
            pEffect.statups.put(CharacterTemporaryStat.PAD, (1 + nBodyCount) * pEffect.info.get(MapleStatInfo.x));
            if (nBodyCount > 0) {
                pEffect.statups.put(CharacterTemporaryStat.IndiePAD, (nBodyCount) * pEffect.info.get(MapleStatInfo.x));
            }
            
            final MapleStatEffect.CancelEffectAction cancelAction = new MapleStatEffect.CancelEffectAction(pPlayer, pEffect, System.currentTimeMillis(), pEffect.statups);
            final ScheduledFuture<?> buffSchedule = Timer.BuffTimer.getInstance().schedule(cancelAction, pEffect.info.get(MapleStatInfo.time));
            pPlayer.registerEffect(pEffect, System.currentTimeMillis(), buffSchedule, pEffect.statups, false, pEffect.info.get(MapleStatInfo.time), pPlayer.getId());
            pPlayer.getClient().write(BuffPacket.giveBuff(pPlayer, Shadower.SHADOWER_INSTINCT, pEffect.info.get(MapleStatInfo.time), pEffect.statups, pEffect));
            
            pPlayer.setPrimaryStack(0); // Set body count back to zero.
            pPlayer.dropMessage(-1, "Body Count Reset");
        }
        
        public static void handleFlipTheCoin(MapleCharacter pPlayer) {
            int nAmount = pPlayer.getAdditionalStack();
            
            if (pPlayer.hasBuff(CharacterTemporaryStat.FlipTheCoin)) {
                if (nAmount < 5) {
                    nAmount++;
                    pPlayer.setAdditionalStack(nAmount);
                }
            } else {
                nAmount = 1;
                pPlayer.setAdditionalStack(1);
            }
            
            // Apply the respected buff bonuses to the player.
            final MapleStatEffect pEffect = SkillFactory.getSkill(Shadower.FLIP_OF_THE_COIN).getEffect(pPlayer.getTotalSkillLevel(Shadower.FLIP_OF_THE_COIN));

            pEffect.statups.put(CharacterTemporaryStat.FlipTheCoin, nAmount);
            pEffect.statups.put(CharacterTemporaryStat.CriticalBuff, nAmount * 10);
            pEffect.statups.put(CharacterTemporaryStat.IndieDamR, nAmount * pEffect.info.get(MapleStatInfo.indieDamR));

            final MapleStatEffect.CancelEffectAction cancelAction = new MapleStatEffect.CancelEffectAction(pPlayer, pEffect, System.currentTimeMillis(), pEffect.statups);
            final ScheduledFuture<?> buffSchedule = Timer.BuffTimer.getInstance().schedule(cancelAction, pEffect.info.get(MapleStatInfo.time));
            pPlayer.registerEffect(pEffect, System.currentTimeMillis(), buffSchedule, pEffect.statups, false, pEffect.info.get(MapleStatInfo.time), pPlayer.getId());
            pPlayer.getClient().write(BuffPacket.giveBuff(pPlayer, Shadower.FLIP_OF_THE_COIN, pEffect.info.get(MapleStatInfo.time), pEffect.statups, pEffect));
            
            // Turn off Flip The Coin in order for the player to require another critical strike for next use.
            pPlayer.getMap().broadcastMessage(ShadowerPacket.toggleFlipTheCoin(false));
            pPlayer.dropMessage(-1, "Flip of the Coin (" + nAmount + "/5)");
        }
    }
}
