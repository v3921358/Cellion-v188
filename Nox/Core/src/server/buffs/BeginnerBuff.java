package server.buffs;

import server.buffs.manager.AbstractBuffClass;
import client.CharacterTemporaryStat;
import client.MapleJob;
import client.MonsterStatus;
import constants.skills.Beginner;
import server.MapleStatEffect;
import server.MapleStatInfo;
import server.buffs.manager.BuffEffectManager;

/**
 *
 * @author Novak
 *
 */
@BuffEffectManager
public class BeginnerBuff extends AbstractBuffClass {

    /**
     * Initializes the BeginnerBuff constructor by setting the buffs array with all the buffs that are to be handled.
     */
    @BuffEffectManager
    public BeginnerBuff() {
        skills = new int[]{
            Beginner.NIMBLE_FEET,
            Beginner.RECOVERY,
            Beginner.SUPER_TRANSFORMATION,
            Beginner.DARK_ANGEL,
            Beginner.ARCHANGEL,
            Beginner.ARCHANGEL_1,
            Beginner.WHITE_ANGEL,
            Beginner.HIDDEN_POTENTIAL_EXPLORER,
            Beginner.DECENT_MYSTIC_DOOR,
            Beginner.ECHO_OF_HERO,
            Beginner.DECENT_HYPER_BODY,
            Beginner.DECENT_COMBAT_ORDERS,
            Beginner.DECENT_ADVANCED_BLESSING,
            Beginner.DECENT_SPEED_INFUSION,
            Beginner.ICE_CHOP,
            Beginner.ICE_SMASH,
            Beginner.ICE_CURSE,
            Beginner.DECENT_SHARP_EYES,
            Beginner.SOARING,
            Beginner.SOARING_MOUNT
        };
    }

    @Override
    public boolean containsJob(int job) {
        return job == MapleJob.BEGINNER.getId();
    }

    @Override
    public void handleEffect(MapleStatEffect eff, int skill) {
        switch (skill) {
            case Beginner.NIMBLE_FEET:
                eff.statups.put(CharacterTemporaryStat.Speed, 10 + (eff.getLevel() - 1) * 5);
                break;
            case Beginner.RECOVERY:
                eff.statups.put(CharacterTemporaryStat.Regen, eff.info.get(MapleStatInfo.x));
                break;
            case Beginner.SUPER_TRANSFORMATION:
                eff.statups.put(CharacterTemporaryStat.Morph, eff.info.get(MapleStatInfo.morph));
                break;
            case Beginner.DARK_ANGEL:
                eff.info.put(MapleStatInfo.time, 2100000000);
                eff.statups.put(CharacterTemporaryStat.IndiePAD, 10);
                eff.statups.put(CharacterTemporaryStat.IndieMAD, 10);
                eff.statups.put(CharacterTemporaryStat.Speed, 1);
                break;
            case Beginner.ARCHANGEL:
            case Beginner.ARCHANGEL_1:
                eff.info.put(MapleStatInfo.time, 2100000000);
                eff.statups.put(CharacterTemporaryStat.IndiePAD, 5);
                eff.statups.put(CharacterTemporaryStat.IndieMAD, 5);
                eff.statups.put(CharacterTemporaryStat.Speed, 1);
                break;
            case Beginner.WHITE_ANGEL:
                eff.info.put(MapleStatInfo.time, 2100000000);
                eff.statups.put(CharacterTemporaryStat.IndiePAD, 12);
                eff.statups.put(CharacterTemporaryStat.IndieMAD, 12);
                eff.statups.put(CharacterTemporaryStat.Speed, 1);
                break;
            case Beginner.HIDDEN_POTENTIAL_EXPLORER:
                eff.statups.put(CharacterTemporaryStat.NoDebuff, 1);
                break;
            case Beginner.DECENT_MYSTIC_DOOR:
                eff.statups.put(CharacterTemporaryStat.SoulArrow, eff.info.get(MapleStatInfo.x));
                break;
            case Beginner.ECHO_OF_HERO: // Echo of Hero
                eff.statups.put(CharacterTemporaryStat.MaxLevelBuff, eff.info.get(MapleStatInfo.x));
                break;
            case Beginner.DECENT_HYPER_BODY:
                eff.statups.put(CharacterTemporaryStat.IndieMHP, eff.info.get(MapleStatInfo.x));
                eff.statups.put(CharacterTemporaryStat.IndieMMP, eff.info.get(MapleStatInfo.x));
                break;
            case Beginner.DECENT_COMBAT_ORDERS:
                eff.statups.put(CharacterTemporaryStat.CombatOrders, eff.info.get(MapleStatInfo.x));
                break;
            case Beginner.DECENT_ADVANCED_BLESSING:
                eff.statups.put(CharacterTemporaryStat.AdvancedBless, 1);
                break;
            case Beginner.DECENT_SPEED_INFUSION:
                eff.statups.put(CharacterTemporaryStat.Speed, eff.info.get(MapleStatInfo.x));
                break;
            case Beginner.ICE_CHOP:
                eff.monsterStatus.put(MonsterStatus.STUN, 1);
                break;
            case Beginner.ICE_SMASH:
            case Beginner.ICE_CURSE:
                eff.monsterStatus.put(MonsterStatus.FREEZE, 1);
                eff.info.put(MapleStatInfo.time, eff.info.get(MapleStatInfo.time) * 2); // freezing skills are a little strange
                break;
            case Beginner.DECENT_SHARP_EYES:
                eff.statups.put(CharacterTemporaryStat.SharpEyes, (eff.info.get(MapleStatInfo.x) << 8) + eff.info.get(MapleStatInfo.y) + eff.info.get(MapleStatInfo.criticaldamageMax));
                break;
            case Beginner.SOARING: // Soaring
            case Beginner.SOARING_MOUNT: // Soaring
                eff.info.put(MapleStatInfo.time, 2100000000);
                eff.statups.put(CharacterTemporaryStat.Flying, 1);
                break;
        }
    }
}
