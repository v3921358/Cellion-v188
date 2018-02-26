package server.buffs;

import server.buffs.manager.AbstractBuffClass;
import client.CharacterTemporaryStat;
import client.MapleJob;
import static constants.GameConstants.isShade;
import constants.skills.Shade;
import server.MapleStatEffect;
import server.MapleStatInfo;
import server.buffs.manager.BuffEffectManager;

/**
 *
 * @author Novak
 *
 */
@BuffEffectManager
public class ShadeBuff extends AbstractBuffClass {

    @BuffEffectManager
    public ShadeBuff() {
        skills = new int[]{
            Shade.MAPLE_WARRIOR,
            Shade.SPIRIT_WARD,
            Shade.FOX_SPIRITS,
            Shade.HEROIC_MEMORIES,
            Shade.SPIRIT_BOND_MAX
        };
    }

    @Override
    public boolean containsJob(int job) {
        return isShade(job);
    }

    @Override
    public void handleEffect(MapleStatEffect eff, int skill) {
        switch (skill) {
            case Shade.MAPLE_WARRIOR:
                eff.statups.put(CharacterTemporaryStat.IndieStatR, eff.info.get(MapleStatInfo.x));
                break;
            case Shade.SPIRIT_WARD:
                eff.statups.put(CharacterTemporaryStat.SpiritGuard, eff.info.get(MapleStatInfo.x));
                break;
            case Shade.FOX_SPIRITS:
                // Handled in SpecialAttackMove.
                eff.statups.put(CharacterTemporaryStat.ChangeFoxMan, 1);
                eff.info.put(MapleStatInfo.time, 2100000000);
                break;
            case Shade.HEROIC_MEMORIES:
                eff.statups.put(CharacterTemporaryStat.IndieDamR, eff.info.get(MapleStatInfo.indieDamR));
                break;
            case Shade.SPIRIT_BOND_MAX:
                eff.statups.put(CharacterTemporaryStat.PAD, eff.info.get(MapleStatInfo.indiePad));
                eff.statups.put(CharacterTemporaryStat.DamR, eff.info.get(MapleStatInfo.indieDamR));
                eff.statups.put(CharacterTemporaryStat.IndieBooster, eff.info.get(MapleStatInfo.indieBooster));
                eff.statups.put(CharacterTemporaryStat.IgnoreTargetDEF, eff.info.get(MapleStatInfo.x));
                break;
        }
    }
}
