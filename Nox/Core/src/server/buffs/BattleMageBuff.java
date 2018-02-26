package server.buffs;

import server.buffs.manager.AbstractBuffClass;
import client.CharacterTemporaryStat;
import client.MapleJob;
import client.MonsterStatus;
import static constants.GameConstants.isBattleMage;
import constants.skills.BattleMage;
import server.MapleStatEffect;
import server.MapleStatInfo;
import server.buffs.manager.BuffEffectManager;

/**
 *
 * @author Novak
 *
 */
@BuffEffectManager
public class BattleMageBuff extends AbstractBuffClass {

    public BattleMageBuff() {
        skills = new int[]{
            BattleMage.STAFF_BOOST,
            BattleMage.MAPLE_WARRIOR,
            BattleMage.CONDEMNATION,
            BattleMage.DARK_CHAIN,
            BattleMage.DARK_GENESIS,
            BattleMage.DARK_AURA,
            BattleMage.DARK_SHOCK,
            BattleMage.DRAINING_AURA,
            BattleMage.HASTY_AURA,
            BattleMage.WEAKENING_AURA,
            BattleMage.BLUE_AURA,
            BattleMage.BATTLE_RAGE,
            BattleMage.FOR_LIBERTY,
            BattleMage.UNIFICATION_AURA,
            BattleMage.MASTER_OF_DEATH
        };
    }

    @Override
    public boolean containsJob(int job) {
        return isBattleMage(job);
    }

    @Override
    public void handleEffect(MapleStatEffect eff, int skill) {
        switch (skill) {
            case BattleMage.STAFF_BOOST:
                eff.statups.put(CharacterTemporaryStat.Booster, eff.info.get(MapleStatInfo.x));
                break;
            case BattleMage.MAPLE_WARRIOR:
                eff.statups.put(CharacterTemporaryStat.IndieStatR, eff.info.get(MapleStatInfo.x));
                break;
            case BattleMage.CONDEMNATION:
                // Handled in SpecialAttackMove.
                eff.statups.put(CharacterTemporaryStat.BMageDeath, 1);
                eff.info.put(MapleStatInfo.time, 2100000000);
                break;
            case BattleMage.DARK_CHAIN:
            case BattleMage.DARK_GENESIS:
                eff.monsterStatus.put(MonsterStatus.STUN, 1);
                break;
            case BattleMage.DARK_SHOCK:
                eff.statups.put(CharacterTemporaryStat.TeleportMasteryOn, 1);
            case BattleMage.DARK_AURA:
                //eff.statups.put(CharacterTemporaryStat.BMageAura, (int) eff.getLevel());
                eff.statups.put(CharacterTemporaryStat.IndieDamR, eff.info.get(MapleStatInfo.indieDamR));
                eff.info.put(MapleStatInfo.time, 2100000000);
                break;
            case BattleMage.DRAINING_AURA:
                //eff.statups.put(CharacterTemporaryStat.BMageAura, (int) eff.getLevel());
                eff.statups.put(CharacterTemporaryStat.ComboDrain, 2);
                eff.statups.put(CharacterTemporaryStat.Regen, 2);
                eff.info.put(MapleStatInfo.time, 2100000000);
                break;
            case BattleMage.HASTY_AURA:
                //eff.statups.put(CharacterTemporaryStat.BMageAura, (int) eff.getLevel());
                eff.statups.put(CharacterTemporaryStat.Speed, eff.info.get(MapleStatInfo.indieSpeed));
                eff.statups.put(CharacterTemporaryStat.IndieBooster, eff.info.get(MapleStatInfo.x));
                eff.info.put(MapleStatInfo.time, 2100000000);
                break;
            case BattleMage.WEAKENING_AURA:
                //eff.statups.put(CharacterTemporaryStat.BMageAura, (int) eff.getLevel());
                eff.info.put(MapleStatInfo.time, 2100000000);
                break;
            case BattleMage.BLUE_AURA:
                //eff.statups.put(CharacterTemporaryStat.BMageAura, (int) eff.getLevel());
                eff.statups.put(CharacterTemporaryStat.AsrR, eff.info.get(MapleStatInfo.asrR));
                eff.statups.put(CharacterTemporaryStat.TerR, eff.info.get(MapleStatInfo.terR));
                eff.statups.put(CharacterTemporaryStat.IgnoreMobDamR, eff.info.get(MapleStatInfo.y));
                eff.info.put(MapleStatInfo.time, 2100000000);
                break;
            case BattleMage.BATTLE_RAGE:
                eff.statups.put(CharacterTemporaryStat.Enrage, 1);
                eff.statups.put(CharacterTemporaryStat.DamR, eff.info.get(MapleStatInfo.x));
                eff.statups.put(CharacterTemporaryStat.CriticalBuff, eff.info.get(MapleStatInfo.z));
                eff.info.put(MapleStatInfo.time, 2100000000);
                break;
            case BattleMage.FOR_LIBERTY:
                eff.statups.put(CharacterTemporaryStat.DamR, eff.info.get(MapleStatInfo.indieDamR));
                //eff.statups.put(CharacterTemporaryStat.IndieMaxDamageOver, eff.info.get(MapleStatInfo.indieMaxDamageOver));
                break;
            case BattleMage.UNIFICATION_AURA:
                eff.statups.put(CharacterTemporaryStat.BMageAura, eff.info.get(MapleStatInfo.x));
            case BattleMage.MASTER_OF_DEATH:
                eff.statups.put(CharacterTemporaryStat.AttackCountX, 2);
                break;
        }
    }
}
