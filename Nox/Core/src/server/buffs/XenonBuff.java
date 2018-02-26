package server.buffs;

import server.buffs.manager.AbstractBuffClass;
import client.CharacterTemporaryStat;
import client.MapleJob;
import constants.GameConstants;
import static constants.GameConstants.isXenon;
import constants.skills.Xenon;
import server.MapleStatEffect;
import server.MapleStatInfo;
import server.buffs.manager.BuffEffectManager;

/**
 *
 * @author Maple
 */
@BuffEffectManager
public class XenonBuff extends AbstractBuffClass {

    public XenonBuff() {
        skills = new int[]{
            Xenon.CIRCUIT_SURGE,
            Xenon.PERSPECTIVE_SHIFT,
            Xenon.EFFICIENCY_STREAMLINE,
            Xenon.XENON_BOOSTER,
            Xenon.MANIFEST_PROJECTOR,
            Xenon.EMERGENCY_RESUPPLY,
            Xenon.AMARANTH_GENERATOR,
            Xenon.HYBRID_DEFENSES,
            Xenon.OOPARTS_CODE,
            Xenon.OFFENSIVE_MATRIX,
            Xenon.MAPLE_WARRIOR,
            Xenon.HYPOGRAM_FIELD_FORCE_FIELD,
            Xenon.HYPOGRAM_FIELD_PENETRATE,
            Xenon.HYPOGRAM_FIELD_PERSIST,
            Xenon.HYPOGRAM_FIELD_REINFORCE,
            Xenon.HYPOGRAM_FIELD_SPEED,
            Xenon.HYPOGRAM_FIELD_SUPPORT,
            Xenon.AEGIS_SYSTEM,
            Xenon.AEGIS_SYSTEM_1,
            Xenon.MULTILATERAL_2,
            Xenon.MULTILATERAL_3,
            Xenon.MULTILATERAL_4,
            Xenon.MULTILATERAL_5,
            Xenon.MULTILATERAL_4
        };
    }

    @Override
    public boolean containsJob(int job) {
        return isXenon(job);
    }

    @Override
    public void handleEffect(MapleStatEffect eff, int skill) {
        switch (skill) {
            case Xenon.CIRCUIT_SURGE: // Circuit Surge
                eff.statups.put(CharacterTemporaryStat.IndiePAD, eff.info.get(MapleStatInfo.indiePad));
                break;
            case Xenon.PERSPECTIVE_SHIFT: // Perspective Shift - dc temp to remove dc
                eff.info.put(MapleStatInfo.powerCon, 6);
                eff.statups.put(CharacterTemporaryStat.CriticalBuff, eff.info.get(MapleStatInfo.x));
                break;
            case Xenon.EFFICIENCY_STREAMLINE: // Efficiency Streamline
                eff.statups.put(CharacterTemporaryStat.IndieMMPR, eff.info.get(MapleStatInfo.indieMmpR));
                eff.statups.put(CharacterTemporaryStat.IndieMHPR, eff.info.get(MapleStatInfo.indieMhpR));
                break;
            case Xenon.XENON_BOOSTER: // Xenon Booster
                eff.statups.put(CharacterTemporaryStat.Booster, eff.info.get(MapleStatInfo.x));
                break;
            case Xenon.MANIFEST_PROJECTOR: // Manifest Projector
                eff.statups.put(CharacterTemporaryStat.ShadowPartner, eff.info.get(MapleStatInfo.y));
                break;
            case Xenon.EMERGENCY_RESUPPLY: // Emergency Resupply
                eff.statups.put(CharacterTemporaryStat.SurplusSupply, eff.info.get(MapleStatInfo.x));
                break;
            case Xenon.AMARANTH_GENERATOR:
                eff.statups.put(CharacterTemporaryStat.SurplusSupply, eff.info.get(MapleStatInfo.x));
                eff.statups.put(CharacterTemporaryStat.AmaranthGenerator, eff.info.get(MapleStatInfo.y));
                eff.info.put(MapleStatInfo.time, 10000);

                //eff.statups.put(CharacterTemporaryStat.SurplusSupply, 16);
                break;
            case Xenon.HYBRID_DEFENSES: // Hybrid Defenses
                eff.statups.put(CharacterTemporaryStat.EVAR, eff.info.get(MapleStatInfo.prop));
                eff.statups.put(CharacterTemporaryStat.DamAbsorbShield, eff.info.get(MapleStatInfo.z));
                break;
            case Xenon.OOPARTS_CODE: // OOPArts Code IndieDamR
                eff.statups.put(CharacterTemporaryStat.BdR, eff.info.get(MapleStatInfo.x));
                eff.statups.put(CharacterTemporaryStat.IndieDamR, eff.info.get(MapleStatInfo.indieDamR));
                break;
            case Xenon.OFFENSIVE_MATRIX: // Offensive Matrix
                eff.statups.put(CharacterTemporaryStat.Stance, eff.info.get(MapleStatInfo.x));
                eff.statups.put(CharacterTemporaryStat.IgnoreTargetDEF, eff.info.get(MapleStatInfo.y));
                break;
            case Xenon.MAPLE_WARRIOR: // Maple Warrior
                eff.statups.put(CharacterTemporaryStat.IndieStatR, eff.info.get(MapleStatInfo.x));
                break;
            case Xenon.HYPOGRAM_FIELD_FORCE_FIELD:
            case Xenon.HYPOGRAM_FIELD_PENETRATE:
            case Xenon.HYPOGRAM_FIELD_PERSIST:
            case Xenon.HYPOGRAM_FIELD_REINFORCE:
            case Xenon.HYPOGRAM_FIELD_SPEED:
            case Xenon.HYPOGRAM_FIELD_SUPPORT:
                eff.statups.put(CharacterTemporaryStat.SUMMON, 1);
                break;
            case Xenon.AEGIS_SYSTEM:
                eff.statups.put(CharacterTemporaryStat.XenonAegisSystem, 1);
                break;
            case Xenon.AEGIS_SYSTEM_1:
            case Xenon.MULTILATERAL_2:
            case Xenon.MULTILATERAL_3:
            case Xenon.MULTILATERAL_4:
            case Xenon.MULTILATERAL_5:
                eff.statups.put(CharacterTemporaryStat.Stance, eff.info.get(MapleStatInfo.w));
                eff.statups.put(CharacterTemporaryStat.IndieDamR, eff.info.get(MapleStatInfo.z));
                break;
        }
    }
}
