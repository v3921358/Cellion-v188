/*
 * Rexion Development
 */
package handling.jobs;

import client.CharacterTemporaryStat;
import client.MapleClient;
import client.SkillFactory;
import constants.GameConstants;
import constants.skills.*;
import java.util.Random;
import java.util.concurrent.ScheduledFuture;
import server.MapleStatEffect;
import server.MapleStatInfo;
import server.Randomizer;
import server.Timer;
import server.maps.objects.MapleCharacter;
import tools.packet.BuffPacket;
import tools.packet.CField;

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
            
            int nCombo = pPlayer.getComboStack();
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
            
            pPlayer.setComboStack(nCombo);
            setComboAttack(pPlayer, nCombo);
        }
        
        public static void handleComboAttack(MapleCharacter pPlayer) {
            if (pPlayer == null && !GameConstants.isWarriorHero(pPlayer.getJob()) && !pPlayer.hasSkill(Fighter.COMBO_ATTACK)) {
                return;
            }
            
            int nCombo = pPlayer.getComboStack();
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
                if (pPlayer.getComboStack() + 1 > nMaxOrbs) {
                    nCombo = nMaxOrbs;
                } else {
                    nCombo++;
                }
            } else {
                if (bDoubleChance) { // Re-roll chance if player has Advanced Combo.
                    if (pPlayer.getComboStack() + 1> nMaxOrbs) {
                        nCombo = nMaxOrbs;
                    } else {
                        nCombo++;
                    }
                }
            }
            
            pPlayer.setComboStack(nCombo);
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
}
