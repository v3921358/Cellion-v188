/*
 * Cellion Development
 */
package client.jobs;

import client.CharacterTemporaryStat;
import client.Skill;
import client.SkillFactory;
import server.MapleStatEffect;
import server.maps.objects.User;
import tools.packet.BuffPacket;
import tools.packet.JobPacket.KinesisPacket;

/**
 * Kinesis Class Handlers
 *
 * @author Mazen
 */
public class Kinesis {

    public static class KinesisHandler {

        public static void handlePsychicPoint(User pPlayer, int nSkill) {
            MapleStatEffect pEffect = SkillFactory.getSkill(nSkill).getEffect(pPlayer.getTotalSkillLevel(nSkill));

            int nPsychicPointChange = pEffect.calcPsychicPowerChange(pPlayer);

            if (pPlayer.hasBuff(CharacterTemporaryStat.KinesisPsychicOver)) {
                nPsychicPointChange /= 2;
            }

            int nPsychicPoint = pPlayer.getPrimaryStack() + nPsychicPointChange;
            if (nPsychicPoint > 30) { // Max PP
                nPsychicPoint = 30;
            }
            if (nPsychicPoint < 0) {
                nPsychicPoint = 0;
            }

            psychicPointResult(pPlayer, nPsychicPoint);
        }

        public static void psychicPointResult(User pPlayer, int nAmount) {
            pPlayer.setPrimaryStack(nAmount);
            pPlayer.write(KinesisPacket.updatePsychicPoint(nAmount));
        }

        public static void requestMentalShield(User pPlayer) {
            Skill pSkill = SkillFactory.getSkill(constants.skills.Kinesis.MENTAL_SHIELD);
            MapleStatEffect pEffect = pSkill.getEffect(pPlayer.getTotalSkillLevel(pSkill));

            pEffect.statups.put(CharacterTemporaryStat.KinesisPsychicEnergeShield, 1);
            pPlayer.registerEffect(pEffect, System.currentTimeMillis(), null, pEffect.statups, false, 2100000000, pPlayer.getId());
            pPlayer.getClient().SendPacket(BuffPacket.giveBuff(pPlayer, pSkill.getId(), 2100000000, pEffect.statups, pEffect));
        }
    }
}
