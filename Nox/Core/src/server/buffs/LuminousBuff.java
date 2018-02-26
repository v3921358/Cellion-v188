package server.buffs;

import server.buffs.manager.AbstractBuffClass;
import client.CharacterTemporaryStat;
import client.MapleJob;
import client.MonsterStatus;
import static constants.GameConstants.isLuminous;
import constants.skills.Luminous;
import server.MapleStatEffect;
import server.MapleStatInfo;
import server.buffs.manager.BuffEffectManager;

/**
 *
 * @author Maple
 */
@BuffEffectManager
public class LuminousBuff extends AbstractBuffClass {

    public LuminousBuff() {
        skills = new int[]{
            Luminous.STANDARD_MAGIC_GUARD,
            Luminous.MAGIC_BOOSTER,
            Luminous.MAPLE_WARRIOR,
            Luminous.DARK_CRESCENDO,
            Luminous.ARMAGEDDON,
            Luminous.SHADOW_SHELL,
            Luminous.PRESSURE_VOID,
            Luminous.PHOTIC_MEDITATION, //Photic Meditation
            Luminous.ARCANE_PITCH,
            Luminous.HEROIC_MEMORIES
        };
    }

    @Override
    public boolean containsJob(int job) {
        return isLuminous(job);
    }

    @Override
    public void handleEffect(MapleStatEffect eff, int skill) {
        switch (skill) {
            case Luminous.DUSK_GUARD:
                eff.statups.put(CharacterTemporaryStat.IndiePDD, eff.info.get(MapleStatInfo.indiePdd));
                break;
            case Luminous.STANDARD_MAGIC_GUARD:
                eff.statups.put(CharacterTemporaryStat.MagicGuard, eff.info.get(MapleStatInfo.x));
                break;
            case Luminous.MAGIC_BOOSTER:
                eff.statups.put(CharacterTemporaryStat.Booster, eff.info.get(MapleStatInfo.x));
                break;
            case Luminous.MAPLE_WARRIOR:
                eff.statups.put(CharacterTemporaryStat.IndieStatR, eff.info.get(MapleStatInfo.x));
                break;
            case Luminous.DARK_CRESCENDO:
                eff.info.put(MapleStatInfo.time, 180000);
                eff.statups.put(CharacterTemporaryStat.StackBuff, eff.info.get(MapleStatInfo.x));
                break;
            case Luminous.ARMAGEDDON:
                eff.monsterStatus.put(MonsterStatus.STUN, 1);
                break;
            case Luminous.PHOTIC_MEDITATION: // Photic Meditation
                eff.statups.put(CharacterTemporaryStat.EMAD, eff.info.get(MapleStatInfo.emad));
                break;
            case Luminous.ARCANE_PITCH: // Arcane Pitch
                eff.statups.put(CharacterTemporaryStat.TerR, eff.info.get(MapleStatInfo.y));
                eff.statups.put(CharacterTemporaryStat.IgnoreTargetDEF, eff.info.get(MapleStatInfo.x));
                eff.statups.put(CharacterTemporaryStat.ElementalReset, eff.info.get(MapleStatInfo.y));
                break;
            case Luminous.SHADOW_SHELL:
            case Luminous.PRESSURE_VOID:
                eff.info.put(MapleStatInfo.time, 2100000000);
                eff.statups.put(CharacterTemporaryStat.KeyDownAreaMoving, eff.info.get(MapleStatInfo.x));
                break;
            case Luminous.HEROIC_MEMORIES:
                eff.statups.put(CharacterTemporaryStat.DamR, eff.info.get(MapleStatInfo.indieDamR));
                //eff.statups.put(CharacterTemporaryStat.IncMaxDamage, eff.info.get(MapleStatInfo.indieMaxDamageOver));
                break;
        }
    }
}
