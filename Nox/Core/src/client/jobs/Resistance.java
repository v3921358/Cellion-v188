/*
 * Rexion Development
 */
package client.jobs;

import client.CharacterTemporaryStat;
import client.MapleStat;
import client.Skill;
import client.SkillFactory;
import constants.GameConstants;
import constants.skills.Aran;
import constants.skills.Blaster;
import java.util.concurrent.ScheduledFuture;
import server.MapleStatEffect;
import server.Timer;
import server.maps.objects.User;
import tools.packet.BuffPacket;
import tools.packet.JobPacket;
import tools.packet.JobPacket.BlasterPacket;

/**
 * Resistance Class Handlers
 *
 * @author Mazen Massoud
 */
public class Resistance {

    public static class BlasterHandler {

        public static void enterCylinderState(User pPlayer) {
            short nAmmo = (short) getMaxAmmo(pPlayer);
            int nGauge = 0;
            updateCylinderRequest(pPlayer, nAmmo, nGauge);
        }

        public static void handleCylinderReload(User pPlayer) {
            short nAmmo = (short) getMaxAmmo(pPlayer);
            int nGauge = pPlayer.getAdditionalStack();
            updateCylinderRequest(pPlayer, nAmmo, nGauge);
        }

        public static void handleAmmoCost(User pPlayer) {
            short nAmmo = (short) (pPlayer.getPrimaryStack() - 1);
            int nGauge = pPlayer.getAdditionalStack();
            updateCylinderRequest(pPlayer, nAmmo, nGauge);
        }

        public static void handleGaugeIncrease(User pPlayer) {
            short nAmmo = (short) (pPlayer.getPrimaryStack());
            int nGauge = pPlayer.getAdditionalStack() + 1;
            if (nGauge > getMaxAmmo(pPlayer)) {
                nGauge = getMaxAmmo(pPlayer);
            }
            updateCylinderRequest(pPlayer, nAmmo, nGauge);
        }

        public static void updateCylinderRequest(User pPlayer, int nAmmo, int nGauge) {
            pPlayer.setPrimaryStack(nAmmo);
            pPlayer.setAdditionalStack(nGauge);
            
            final MapleStatEffect buffEffects = SkillFactory.getSkill(Blaster.REVOLVING_CANNON).getEffect(pPlayer.getTotalSkillLevel(Blaster.REVOLVING_CANNON));
            buffEffects.statups.put(CharacterTemporaryStat.RWCylinder, 1);
            pPlayer.registerEffect(buffEffects, System.currentTimeMillis(), null, buffEffects.statups, false, 2100000000, pPlayer.getId());
            pPlayer.getClient().write(BuffPacket.giveBuff(pPlayer, Blaster.REVOLVING_CANNON, 2100000000, buffEffects.statups, buffEffects));
        }
        
        public static void handleOverheat(User pPlayer) {
            int nDuration = 7000;
            final MapleStatEffect buffEffects = SkillFactory.getSkill(Blaster.BUNKER_BUSTER_EXPLOSION).getEffect(pPlayer.getTotalSkillLevel(Blaster.BUNKER_BUSTER_EXPLOSION));
            
            buffEffects.statups.put(CharacterTemporaryStat.RWOverHeat, 1);
            
            final MapleStatEffect.CancelEffectAction cancelAction = new MapleStatEffect.CancelEffectAction(pPlayer, buffEffects, System.currentTimeMillis(), buffEffects.statups);
            final ScheduledFuture<?> buffSchedule = Timer.BuffTimer.getInstance().schedule(cancelAction, nDuration);
            pPlayer.registerEffect(buffEffects, System.currentTimeMillis(), buffSchedule, buffEffects.statups, false, nDuration, pPlayer.getId());
            pPlayer.getClient().write(BuffPacket.giveBuff(pPlayer, Blaster.BUNKER_BUSTER_EXPLOSION, nDuration, buffEffects.statups, buffEffects));
            
            updateCylinderRequest(pPlayer, pPlayer.getPrimaryStack(), 0);
        }
        
        public static int getMaxAmmo(User pPlayer) {
            int nMaxAmmo = 3;
            if(pPlayer.hasSkill(Blaster.REVOLVING_CANNON_PLUS)) {
                nMaxAmmo = 4;
            }
            if(pPlayer.hasSkill(Blaster.REVOLVING_CANNON_PLUS_II)) {
                nMaxAmmo = 5;
            }
            if(pPlayer.hasSkill(Blaster.REVOLVING_CANNON_PLUS_III)) {
                nMaxAmmo = 6;
            }
            return nMaxAmmo;
        }
        
        public static void requestBlastShield(User pPlayer) {
            Skill pSkill = SkillFactory.getSkill(constants.skills.Blaster.BLAST_SHIELD);
            MapleStatEffect pEffect = pSkill.getEffect(pPlayer.getTotalSkillLevel(pSkill));
            
            pEffect.statups.put(CharacterTemporaryStat.RWBarrier, 1);
            pPlayer.registerEffect(pEffect, System.currentTimeMillis(), null, pEffect.statups, false, 3000, pPlayer.getId());
            pPlayer.getClient().write(BuffPacket.giveBuff(pPlayer, pSkill.getId(), 3000, pEffect.statups, pEffect));
        }
        
        public static void requestVitalityShield(User pPlayer) {
            if (!pPlayer.hasBuff(CharacterTemporaryStat.RWBarrier)) {
                return;
            }
            Skill pSkill = SkillFactory.getSkill(constants.skills.Blaster.VITALITY_SHIELD);
            MapleStatEffect pEffect = pSkill.getEffect(pPlayer.getTotalSkillLevel(pSkill));
            int nDuration = 15000;
            
            pEffect.statups.put(CharacterTemporaryStat.RWBarrierHeal, 1);
            
            final MapleStatEffect.CancelEffectAction cancelAction = new MapleStatEffect.CancelEffectAction(pPlayer, pEffect, System.currentTimeMillis(), pEffect.statups);
            final ScheduledFuture<?> buffSchedule = Timer.BuffTimer.getInstance().schedule(cancelAction, nDuration);
            
            pPlayer.registerEffect(pEffect, System.currentTimeMillis(), buffSchedule, pEffect.statups, false, nDuration, pPlayer.getId());
            pPlayer.getClient().write(BuffPacket.giveBuff(pPlayer, pSkill.getId(), nDuration, pEffect.statups, pEffect));
            
            int nHpRecovered = pPlayer.getStat().getHp() + (pPlayer.getStat().getMaxHp() / 2);
            pPlayer.getStat().setHp(nHpRecovered, pPlayer);
            pPlayer.updateSingleStat(MapleStat.HP, nHpRecovered);
            
            pPlayer.cancelEffectFromTemporaryStat(CharacterTemporaryStat.RWBarrier); // Consume Blast Shield
        }
    }

}
