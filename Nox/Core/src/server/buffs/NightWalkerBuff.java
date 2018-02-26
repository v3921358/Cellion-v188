package server.buffs;

import client.CharacterTemporaryStat;
import client.MapleJob;
import client.MonsterStatus;
import static constants.GameConstants.isNightWalkerCygnus;
import constants.skills.NightWalker;
import server.MapleStatEffect;
import server.MapleStatInfo;
import server.buffs.manager.AbstractBuffClass;
import server.buffs.manager.BuffEffectManager;

/**
 *
 * @author Novak
 *
 */
@BuffEffectManager
public class NightWalkerBuff extends AbstractBuffClass {

    public NightWalkerBuff() {
        skills = new int[]{
            NightWalker.HASTE,
            NightWalker.HASTE_1,
            NightWalker.SHADOW_BAT,
            NightWalker.SHADOW_BAT_2,
            NightWalker.SHADOW_BAT_3,
            NightWalker.DARK_ELEMENTAL,
            NightWalker.THROWING_BOOSTER,
            NightWalker.DARK_SIGHT,
            NightWalker.SPIRIT_PROJECTION,
            NightWalker.DARKNESS_ASCENDING,
            NightWalker.DARK_SERVANT,
            NightWalker.SHADOW_ILLUSION,
            NightWalker.DOMINION,
            NightWalker.DARK_OMEN,
            NightWalker.VITALITY_SIPHON,
            NightWalker.GLORY_OF_THE_GUARDIANS
        };
    }

    @Override
    public boolean containsJob(int job) {
        return isNightWalkerCygnus(job);
    }

    @Override
    public void handleEffect(MapleStatEffect eff, int skill) {
        switch (skill) {
            case NightWalker.SHADOW_BAT:
                eff.statups.put(CharacterTemporaryStat.NightWalkerBat, 1);
                break;
            case NightWalker.HASTE:
            case NightWalker.HASTE_1:
                eff.statups.put(CharacterTemporaryStat.Speed, eff.info.get(MapleStatInfo.speed));
                eff.statups.put(CharacterTemporaryStat.Jump, eff.info.get(MapleStatInfo.jump));
                eff.statups.put(CharacterTemporaryStat.EVAR, eff.info.get(MapleStatInfo.er));
                break;
            case NightWalker.DARK_ELEMENTAL:
                eff.statups.put(CharacterTemporaryStat.ElementDarkness, eff.info.get(MapleStatInfo.x));
                break;
            case NightWalker.THROWING_BOOSTER:
                eff.statups.put(CharacterTemporaryStat.Booster, eff.info.get(MapleStatInfo.x));
                break;
            case NightWalker.SPIRIT_PROJECTION:
                eff.statups.put(CharacterTemporaryStat.NoBulletConsume, 0);
                break;
            case NightWalker.DARKNESS_ASCENDING:
                eff.statups.put(CharacterTemporaryStat.DarknessAscension, eff.info.get(MapleStatInfo.x));
                break;
            case NightWalker.DOMINION:
                eff.statups.put(CharacterTemporaryStat.IndieStance, eff.info.get(MapleStatInfo.indieStance));
                eff.statups.put(CharacterTemporaryStat.IndieCr, eff.info.get(MapleStatInfo.indieCr));
                eff.statups.put(CharacterTemporaryStat.IndieDamR, eff.info.get(MapleStatInfo.indieDamR));
                break;
            case NightWalker.DARK_SERVANT:
                eff.statups.put(CharacterTemporaryStat.ShadowServant, eff.info.get(MapleStatInfo.x));
                break;
            case NightWalker.SHADOW_ILLUSION:
                eff.statups.put(CharacterTemporaryStat.ShadowIllusion, eff.info.get(MapleStatInfo.x));
                break;
            case NightWalker.DARK_OMEN:
                //TODO
                break;
            case NightWalker.VITALITY_SIPHON:
                //TODO
                break;
            case NightWalker.GLORY_OF_THE_GUARDIANS:
                eff.statups.put(CharacterTemporaryStat.DamR, eff.info.get(MapleStatInfo.indieDamR));
                break;
            case NightWalker.DARK_SIGHT:
                eff.statups.put(CharacterTemporaryStat.DarkSight, eff.info.get(MapleStatInfo.x));
                break;
        }
    }
}
