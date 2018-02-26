/*
 * Rexion Development
 */
package handling.jobs;

import client.CharacterTemporaryStat;
import client.MapleClient;
import client.SkillFactory;
import constants.GameConstants;
import constants.skills.Aran;
import handling.world.AttackInfo;
import static java.lang.Integer.max;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadLocalRandom;
import server.MapleStatEffect;
import server.MapleStatInfo;
import server.Timer;
import server.life.MobAttackInfo;
import server.maps.objects.MapleCharacter;
import tools.packet.BuffPacket;
import tools.packet.CField;

/**
 * Hero Class Handlers
 *
 * @author Mazen Massoud
 */
public class Hero {

    public static class AranHandler {

        public static void handleSwingStudies(MapleCharacter pPlayer) {
            if (pPlayer == null && !GameConstants.isAran(pPlayer.getJob()) && !pPlayer.hasSkill(Aran.SWING_STUDIES_1)) {
                return;
            }
            
            int nDuration = (pPlayer.getSkillLevel(Aran.SWING_STUDIES_2) > 0) ? 4000 : 2000;
            final MapleStatEffect buffEffects = SkillFactory.getSkill(Aran.SWING_STUDIES_1).getEffect(pPlayer.getTotalSkillLevel(Aran.SWING_STUDIES_1));

            buffEffects.statups.put(CharacterTemporaryStat.NextAttackEnhance, 1);

            final MapleStatEffect.CancelEffectAction cancelAction = new MapleStatEffect.CancelEffectAction(pPlayer, buffEffects, System.currentTimeMillis(), buffEffects.statups);
            final ScheduledFuture<?> buffSchedule = Timer.BuffTimer.getInstance().schedule(cancelAction, nDuration);
            pPlayer.registerEffect(buffEffects, System.currentTimeMillis(), buffSchedule, buffEffects.statups, false, nDuration, pPlayer.getId());
            pPlayer.getClient().write(BuffPacket.giveBuff(pPlayer, Aran.SWING_STUDIES_1, nDuration, buffEffects.statups, buffEffects));
        }

        public static void handleAdrenalineRush(MapleCharacter pPlayer) {
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
            pPlayer.setComboStack(500);
            pPlayer.getClient().write(CField.updateCombo(500));
        }
        
        public static void handleComboAbility(MapleCharacter pPlayer, int nIncreaseCount) {
            if (pPlayer == null && !GameConstants.isAran(pPlayer.getJob()) && !pPlayer.hasSkill(Aran.COMBO_ABILITY)) {
                return;
            }
            
            short nCombo = (short) pPlayer.getComboStack();
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
            pPlayer.setComboStack(nCombo);

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

}
