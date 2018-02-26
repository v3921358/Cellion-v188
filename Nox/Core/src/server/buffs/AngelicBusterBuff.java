package server.buffs;

import server.buffs.manager.AbstractBuffClass;
import client.CharacterTemporaryStat;
import client.MapleJob;
import constants.GameConstants;
import static constants.GameConstants.isAngelicBuster;
import constants.skills.AngelicBuster;
import server.MapleStatEffect;
import server.MapleStatInfo;
import server.buffs.manager.BuffEffectManager;

/**
 *
 * @author Novak
 */
@BuffEffectManager
public class AngelicBusterBuff extends AbstractBuffClass {

    public AngelicBusterBuff() {
        skills = new int[]{
            AngelicBuster.MELODY_CROSS, //Melody Cross
            AngelicBuster.POWER_TRANSFER, //Power Transfer
            AngelicBuster.IRON_BLOSSOM, //Iron Blossom
            AngelicBuster.STAR_GAZER, //Star Gazer
            AngelicBuster.NOVA_WARRIOR, //Nova Warrior
        };
    }

    @Override
    public boolean containsJob(int job) {
        return isAngelicBuster(job);
    }

    @Override
    public void handleEffect(MapleStatEffect eff, int skill) {
        switch (skill) {
            case AngelicBuster.MELODY_CROSS: // Melody Cross
                eff.statups.put(CharacterTemporaryStat.IndieBooster, eff.info.get(MapleStatInfo.indieBooster));
                eff.statups.put(CharacterTemporaryStat.EMHP, eff.info.get(MapleStatInfo.mhpX));
                break;
            case AngelicBuster.POWER_TRANSFER: // Power Transfer
                eff.statups.put(CharacterTemporaryStat.DamAbsorbShield, eff.info.get(MapleStatInfo.y));
                break;
            case AngelicBuster.IRON_BLOSSOM: // Iron Blossom
                eff.statups.put(CharacterTemporaryStat.Stance, eff.info.get(MapleStatInfo.prop));
                break;
            case AngelicBuster.STAR_GAZER: // Star Gazer 
                eff.statups.put(CharacterTemporaryStat.SoulGazeCriDamR, eff.info.get(MapleStatInfo.x));
                //eff.statups.put(CharacterTemporaryStat.IncCriticalDamMax, eff.info.get(MapleStatInfo.x));
                //eff.statups.put(CharacterTemporaryStat.IncCriticalDamMin, eff.info.get(MapleStatInfo.y));
                break;
            case AngelicBuster.NOVA_WARRIOR: //Nova Warrior
                eff.statups.put(CharacterTemporaryStat.IndieStatR, eff.info.get(MapleStatInfo.x));
                break;
        }
    }
}
