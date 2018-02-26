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

        public static void handleSwingStudies(MapleCharacter oPlayer) {
            if (oPlayer == null && !GameConstants.isAran(oPlayer.getJob()) && !oPlayer.hasSkill(Aran.SWING_STUDIES_1)) {
                return;
            }
            
            int nDuration = (oPlayer.getSkillLevel(Aran.SWING_STUDIES_2) > 0) ? 4000 : 2000;
            final MapleStatEffect buffEffects = SkillFactory.getSkill(Aran.SWING_STUDIES_1).getEffect(oPlayer.getTotalSkillLevel(Aran.SWING_STUDIES_1));

            buffEffects.statups.put(CharacterTemporaryStat.NextAttackEnhance, 1);

            final MapleStatEffect.CancelEffectAction cancelAction = new MapleStatEffect.CancelEffectAction(oPlayer, buffEffects, System.currentTimeMillis(), buffEffects.statups);
            final ScheduledFuture<?> buffSchedule = Timer.BuffTimer.getInstance().schedule(cancelAction, nDuration);
            oPlayer.registerEffect(buffEffects, System.currentTimeMillis(), buffSchedule, buffEffects.statups, false, nDuration, oPlayer.getId());
            oPlayer.getClient().write(BuffPacket.giveBuff(oPlayer, Aran.SWING_STUDIES_1, nDuration, buffEffects.statups, buffEffects));
        }

        public static void handleAdrenalineRush(MapleCharacter oPlayer) {
            if (oPlayer == null && !GameConstants.isAran(oPlayer.getJob()) && !oPlayer.hasSkill(Aran.ADRENALINE_RUSH)) {
                return;
            }
            
            int nDuration = 15000;
            final MapleStatEffect buffEffects = SkillFactory.getSkill(Aran.ADRENALINE_RUSH).getEffect(oPlayer.getTotalSkillLevel(Aran.ADRENALINE_RUSH));

            buffEffects.statups.put(CharacterTemporaryStat.AdrenalinBoost, buffEffects.info.get(MapleStatInfo.x));

            final MapleStatEffect.CancelEffectAction cancelAction = new MapleStatEffect.CancelEffectAction(oPlayer, buffEffects, System.currentTimeMillis(), buffEffects.statups);
            final ScheduledFuture<?> buffSchedule = Timer.BuffTimer.getInstance().schedule(cancelAction, nDuration);
            oPlayer.registerEffect(buffEffects, System.currentTimeMillis(), buffSchedule, buffEffects.statups, false, nDuration, oPlayer.getId());
            oPlayer.getClient().write(BuffPacket.giveBuff(oPlayer, Aran.ADRENALINE_RUSH, nDuration, buffEffects.statups, buffEffects));
            
            // Set combo down to 500 for when adrenaline rush is completed.
            oPlayer.setLastCombo(System.currentTimeMillis() + nDuration);
            oPlayer.setComboStack(500);
            oPlayer.getClient().write(CField.updateCombo(500));
        }
        
        public static void handleComboAbility(MapleCharacter oPlayer, int nIncreaseCount) {
            if (oPlayer == null && !GameConstants.isAran(oPlayer.getJob()) && !oPlayer.hasSkill(Aran.COMBO_ABILITY)) {
                return;
            }
            
            short nCombo = (short) oPlayer.getComboStack();
            long nCurrentTime = System.currentTimeMillis();

            if ((nCombo > 0) && (nCurrentTime - oPlayer.getLastComboTime() > 5000L)) {
                if ((nCombo - 10) > 0) { // Don't let combo go below zero.
                    nCombo -= 10; // Decrease Combo by 10 every 5 seconds.
                } else {
                    nCombo = 0;
                }
            }

            nIncreaseCount = ThreadLocalRandom.current().nextInt(3, 6); // TODO: Increase based off skill attack count.
            nCombo = (short) Math.min(30000, nCombo + nIncreaseCount);
            oPlayer.setLastCombo(nCurrentTime);
            oPlayer.setComboStack(nCombo);

            // Write the combo update packet.
            oPlayer.getClient().write(CField.updateCombo(nCombo));

            // Apply the bonuses provided by the combo.
            final MapleStatEffect buffEffects = SkillFactory.getSkill(Aran.COMBO_ABILITY).getEffect(oPlayer.getTotalSkillLevel(Aran.COMBO_ABILITY));
            buffEffects.statups.put(CharacterTemporaryStat.ComboAbilityBuff, (int) nCombo);
            oPlayer.registerEffect(buffEffects, System.currentTimeMillis(), null, buffEffects.statups, false, buffEffects.info.get(MapleStatInfo.time), oPlayer.getId());
            oPlayer.getClient().write(BuffPacket.giveBuff(oPlayer, Aran.COMBO_ABILITY, 99999, buffEffects.statups, buffEffects));
            
            if (nCombo >= 998) { // Apply Adrenaline Rush upon reaching max stacks.
                nCombo = (short) 1000;
                oPlayer.setLastCombo(nCurrentTime);
                //oPlayer.setCombo(nCombo);
                oPlayer.getClient().write(CField.updateCombo(nCombo));
                
                handleAdrenalineRush(oPlayer);
            }
        }
    }

    public static class EvanHandler {

    }

}
