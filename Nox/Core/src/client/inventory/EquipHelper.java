package client.inventory;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Lloyd Korn
 */
public class EquipHelper {

    public static List<EquipSpecialStat> calculateEquipSpecialStatsForEncoding(Equip eq) {
        List<EquipSpecialStat> stats = new ArrayList<>();

        //SPECIAL STATS:
        if (eq.getTotalDamage() > 0) {
            stats.add(EquipSpecialStat.TOTAL_DAMAGE);
        }
        if (eq.getAllStat() > 0) {
            stats.add(EquipSpecialStat.ALL_STAT);
        }
        stats.add(EquipSpecialStat.KARMA_COUNT); //no count = -1
        //if (0 != 0) {
        //    eq.getSpecialStats().add(EquipSpecialStat.UNK10);
        //}
        stats.add(EquipSpecialStat.GRADE_OPTION); // test
        stats.add(EquipSpecialStat.ITEM_STATE); // star_flag

        return stats;
    }

    public static List<EquipStat> calculateEquipStatsForEncoding(Equip eq) {
        List<EquipStat> stats = new ArrayList<>();

        if (eq.getUpgradeSlots() > 0) {
            stats.add(EquipStat.SLOTS);
        }
        if (eq.getLevel() > 0) {
            stats.add(EquipStat.LEVEL);
        }
        if (eq.getStr() > 0) {
            stats.add(EquipStat.STR);
        }
        if (eq.getDex() > 0) {
            stats.add(EquipStat.DEX);
        }
        if (eq.getInt() > 0) {
            stats.add(EquipStat.INT);
        }
        if (eq.getLuk() > 0) {
            stats.add(EquipStat.LUK);
        }
        if (eq.getHp() > 0) {
            stats.add(EquipStat.MHP);
        }
        if (eq.getMp() > 0) {
            stats.add(EquipStat.MMP);
        }
        if (eq.getWatk() > 0) {
            stats.add(EquipStat.WATK);
        }
        if (eq.getMatk() > 0) {
            stats.add(EquipStat.MATK);
        }
        if (eq.getWdef() > 0) {
            stats.add(EquipStat.PDD);
        }
        if (eq.getHands() > 0) {
            stats.add(EquipStat.HANDS);
        }
        if (eq.getSpeed() > 0) {
            stats.add(EquipStat.SPEED);
        }
        if (eq.getJump() > 0) {
            stats.add(EquipStat.JUMP);
        }
        if (eq.getFlag() > 0) {
            stats.add(EquipStat.FLAG);
        }
        if (eq.getIncSkill() > 0) {
            stats.add(EquipStat.INC_SKILL);
        }
        if (eq.getEquipLevel() > 0) {
            stats.add(EquipStat.ITEM_LEVEL);
        }
        if (eq.getItemEXP() > 0) {
            stats.add(EquipStat.ITEM_EXP);
        }
        if (eq.getDurability() > -1) {
            stats.add(EquipStat.DURABILITY);
        }
        if (eq.getViciousHammer() > 0) {
            stats.add(EquipStat.VICIOUS_HAMMER);
        }
        if (eq.getPVPDamage() > 0) {
            stats.add(EquipStat.PVP_DAMAGE);
        }
        if (eq.getSpellTrace() > 0) {
            stats.add(EquipStat.SPELL_TRACE);
        }
        if (eq.getReqLevel() > 0) {
            stats.add(EquipStat.REQUIRED_LEVEL);
        }
        if (eq.getYggdrasilWisdom() > 0) {
            stats.add(EquipStat.YGGDRASIL_WISDOM);
        }
        if (eq.getFinalStrike()) {
            stats.add(EquipStat.FINAL_STRIKE);
        }
        if (eq.getBossDamage() > 0) {
            stats.add(EquipStat.BOSS_DAMAGE);
        }
        if (eq.getIgnorePDR() > 0) {
            stats.add(EquipStat.IGNORE_PDR);
        }

        return stats;
    }
}
