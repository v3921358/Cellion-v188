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
import java.util.concurrent.ScheduledFuture;
import server.MapleStatEffect;
import server.Timer;
import server.maps.objects.MapleCharacter;
import tools.packet.BuffPacket;
import tools.packet.JobPacket;
import tools.packet.JobPacket.BlasterPacket;

/**
 * Resistance Class Handlers
 *
 * @author Mazen
 */
public class Resistance {

    public static class BlasterHandler {

        public static void enterCylinderState(MapleCharacter pPlayer) {
            short nAmmo = (short) getMaxAmmo(pPlayer);
            int nGauge = 0;
            pPlayer.setComboStack(nAmmo);
            pPlayer.setAdditionalStack(nGauge);
            updateCylinderRequest(pPlayer);
        }

        public static void handleCylinderReload(MapleCharacter pPlayer) {
            short nAmmo = (short) getMaxAmmo(pPlayer);
            int nGauge = pPlayer.getAdditionalStack();
            
            pPlayer.setComboStack(nAmmo);
            pPlayer.setAdditionalStack(nGauge);
            updateCylinderRequest(pPlayer);
            //pPlayer.getClient().write(BlasterPacket.setCylinderState(nAmmo, nGauge));
        }

        public static void handleAmmoCost(MapleCharacter pPlayer) {
            short nAmmo = (short) (pPlayer.getComboStack() - 1);
            int nGauge = pPlayer.getAdditionalStack();
            
            pPlayer.setComboStack(nAmmo);
            pPlayer.setAdditionalStack(nGauge);
            updateCylinderRequest(pPlayer);
        }

        public static void handleGaugeIncrease(MapleCharacter pPlayer) {
            short nAmmo = (short) (pPlayer.getComboStack());
            int nGauge = pPlayer.getAdditionalStack() + 1;
            
            if (nGauge > getMaxAmmo(pPlayer)) {
                nGauge = getMaxAmmo(pPlayer);
            }
            
            pPlayer.setComboStack(nAmmo);
            pPlayer.setAdditionalStack(nGauge);
            updateCylinderRequest(pPlayer);
        }

        public static void updateCylinderRequest(MapleCharacter pPlayer) {
            final MapleStatEffect buffEffects = SkillFactory.getSkill(Blaster.REVOLVING_CANNON).getEffect(pPlayer.getTotalSkillLevel(Blaster.REVOLVING_CANNON));
            buffEffects.statups.put(CharacterTemporaryStat.RWCylinder, 1);
            pPlayer.registerEffect(buffEffects, System.currentTimeMillis(), null, buffEffects.statups, false, 2100000000, pPlayer.getId());
            pPlayer.getClient().write(BuffPacket.giveBuff(pPlayer, Blaster.REVOLVING_CANNON, 2100000000, buffEffects.statups, buffEffects));
        }
        
        public static void handleOverheat(MapleCharacter pPlayer) {
            int nDuration = 7000;
            final MapleStatEffect buffEffects = SkillFactory.getSkill(Blaster.BUNKER_BUSTER_EXPLOSION).getEffect(pPlayer.getTotalSkillLevel(Blaster.BUNKER_BUSTER_EXPLOSION));
            
            buffEffects.statups.put(CharacterTemporaryStat.RWOverHeat, 1);
            
            final MapleStatEffect.CancelEffectAction cancelAction = new MapleStatEffect.CancelEffectAction(pPlayer, buffEffects, System.currentTimeMillis(), buffEffects.statups);
            final ScheduledFuture<?> buffSchedule = Timer.BuffTimer.getInstance().schedule(cancelAction, nDuration);
            pPlayer.registerEffect(buffEffects, System.currentTimeMillis(), buffSchedule, buffEffects.statups, false, nDuration, pPlayer.getId());
            pPlayer.getClient().write(BuffPacket.giveBuff(pPlayer, Blaster.BUNKER_BUSTER_EXPLOSION, nDuration, buffEffects.statups, buffEffects));
            
            pPlayer.setAdditionalStack(0);
            updateCylinderRequest(pPlayer);
        }
        
        public static int getMaxAmmo(MapleCharacter pPlayer) {
            int nMaxAmmo = 3;
            if(pPlayer.hasSkill(Blaster.REVOLVING_CANNON_PLUS)) {
                nMaxAmmo = 4;
            }
            if(pPlayer.hasSkill(Blaster.REVOLVING_CANNON_PLUS_2)) {
                nMaxAmmo = 5;
            }
            if(pPlayer.hasSkill(Blaster.REVOLVING_CANNON_PLUS_3)) {
                nMaxAmmo = 6;
            }
            return nMaxAmmo;
        }
    }

}
