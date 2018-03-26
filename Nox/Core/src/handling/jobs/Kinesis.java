/*
 * Rexion Development
 */
package handling.jobs;

import client.CharacterTemporaryStat;
import client.Skill;
import handling.jobs.*;
import client.SkillFactory;
import server.MapleStatEffect;
import server.MapleStatInfo;
import server.maps.objects.MapleCharacter;
import tools.packet.BuffPacket;

/**
 * Kinesis Class Handlers
 *
 * @author Mazen
 */
public class Kinesis {

    public static class KinesisHandler {

        public static void handlePsychicPoint(MapleCharacter pPlayer, int nSkill) {
            MapleStatEffect oEffect = SkillFactory.getSkill(nSkill).getEffect(pPlayer.getTotalSkillLevel(nSkill));
            int nPointConsume = oEffect.info.get(MapleStatInfo.ppCon);
            int nPointRecover = oEffect.info.get(MapleStatInfo.ppRecovery);

            switch (nSkill) {
                case constants.skills.Kinesis.KINETIC_PILEDRIVER:
                case constants.skills.Kinesis.PSYCHIC_BLAST:
                case constants.skills.Kinesis.PSYCHIC_DRAIN:
                case constants.skills.Kinesis.PSYCHIC_GRAB:
                case constants.skills.Kinesis.PSYCHIC_ASSAULT:
                case constants.skills.Kinesis.MIND_TREMOR:
                case constants.skills.Kinesis.PSYCHIC_CLUTCH:
                case constants.skills.Kinesis.MIND_QUAKE:
                case constants.skills.Kinesis.MIND_BREAK:
                    nPointRecover = 1;
                    break;
                case constants.skills.Kinesis.ULTIMATE_BPM:
                    nPointConsume = 1;
                    break;
                case constants.skills.Kinesis.KINETIC_JAUNT:
                    nPointConsume = 2;
                    break;
                case constants.skills.Kinesis.ULTIMATE_DEEP_IMPACT:
                    nPointConsume = 5;
                    break;
                case constants.skills.Kinesis.ULTIMATE_PSYCHIC_SHOT:
                    nPointConsume = 5;
                    break;
                case constants.skills.Kinesis.ULTIMATE_TRAINWRECK:
                    nPointConsume = 25;
                    break;
                default:
                    nPointConsume = 1; // Why not.
                    break;
            }

            int nPsychicPoint = pPlayer.getAdditionalStack() + nPointRecover - nPointConsume;
            if (nPsychicPoint > 35) { // Max PP
                nPsychicPoint = 35;
            }

            pPlayer.setAdditionalStack(nPsychicPoint);
            oEffect.statups.put(CharacterTemporaryStat.KinesisPsychicPoint, nPsychicPoint);
            pPlayer.registerEffect(oEffect, System.currentTimeMillis(), null, oEffect.statups, false, oEffect.info.get(MapleStatInfo.time), pPlayer.getId());
            pPlayer.getClient().write(BuffPacket.giveBuff(pPlayer, 0, oEffect.info.get(MapleStatInfo.time), oEffect.statups, oEffect));
            pPlayer.yellowMessage("nConsume = " + nPointConsume + " / nPointRecover = " + nPointRecover);
        }

    }

}
