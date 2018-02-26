package server.buffs;

import server.buffs.manager.AbstractBuffClass;
import client.CharacterTemporaryStat;
import client.MapleJob;
import client.MonsterStatus;
import static constants.GameConstants.isEvan;
import constants.skills.Evan;
import server.MapleStatEffect;
import server.MapleStatInfo;
import server.buffs.manager.BuffEffectManager;

/**
 *
 * @author Novak
 */
@BuffEffectManager
public class EvanBuff extends AbstractBuffClass {

    public EvanBuff() {
        skills = new int[]{
            Evan.MAGIC_GUARD,
            Evan.MAGIC_BOOSTER,
            Evan.MAPLE_WARRIOR,
            Evan.HEROS_WILL,
            Evan.BLESSING_OF_THE_ONYX,
            Evan.ELEMENTAL_DECREASE,
            Evan.MAGIC_RESISTANCE,
            Evan.HEROIC_MEMORIES,
            Evan.SUMMON_ONYX_DRAGON
        };
    }

    @Override
    public boolean containsJob(int job) {
        return isEvan(job);
    }

    @Override
    public void handleEffect(MapleStatEffect eff, int skill) {
        switch (skill) {
            case Evan.MAGIC_GUARD:
                eff.statups.put(CharacterTemporaryStat.MagicGuard, eff.info.get(MapleStatInfo.x));
                eff.info.put(MapleStatInfo.time, 2100000000);
                break;
            case Evan.ELEMENTAL_DECREASE: // Elemental Decrease
                eff.statups.put(CharacterTemporaryStat.ElementalReset, eff.info.get(MapleStatInfo.x));
            case Evan.MAGIC_BOOSTER: // Magic Booster
                eff.statups.put(CharacterTemporaryStat.Booster, eff.info.get(MapleStatInfo.x));
                break;
            case Evan.MAGIC_RESISTANCE: // Magic Resistance
                eff.statups.put(CharacterTemporaryStat.MagicResistance, eff.info.get(MapleStatInfo.x));
                break;
            case Evan.MAPLE_WARRIOR: // Maple Warrior
                eff.statups.put(CharacterTemporaryStat.IndieStatR, eff.info.get(MapleStatInfo.x));
                break;
            case Evan.ONYX_WILL: // Onyx Will
                eff.statups.put(CharacterTemporaryStat.IndieDamR, eff.info.get(MapleStatInfo.damage));
                eff.statups.put(CharacterTemporaryStat.Stance, eff.info.get(MapleStatInfo.prop));
                break;
            case Evan.BLESSING_OF_THE_ONYX: // Blessing of the Onyx
                eff.statups.put(CharacterTemporaryStat.EMAD, eff.info.get(MapleStatInfo.emad));
                eff.statups.put(CharacterTemporaryStat.EPDD, eff.info.get(MapleStatInfo.epdd));
                break;
            case Evan.HEROIC_MEMORIES: // Heroic Memories
                eff.statups.put(CharacterTemporaryStat.DamR, eff.info.get(MapleStatInfo.indieDamR));
                //eff.statups.put(CharacterTemporaryStat.IncMaxDamage, eff.info.get(MapleStatInfo.indieMaxDamageOver));
                break;
            case Evan.SUMMON_ONYX_DRAGON:
                eff.statups.put(CharacterTemporaryStat.SUMMON, 1);
                break;
        }
    }
}
