package handling.world;

import java.awt.Point;
import java.util.List;

import client.Skill;
import client.SkillFactory;
import constants.GameConstants;
import provider.data.HexTool;
import server.StatEffect;
import server.maps.objects.User;

public class AttackInfo {

    public int skill, skillLevel, charge, lastAttackTickCount;
    public List<AttackMonster> allDamage;
    public Point position = new Point();
    public int display;
    public byte speed, csstar, AOE, slot, attackFlag;
    public int tbyte, mobCount, numberOfHits;
    public boolean real = true;

    // To track multi kill
    public byte after_NumMobsKilled = 0;

    public void cleanupMemory() {
        allDamage.clear(); // technically there's no leak here, but it'd be faster if we remove MapleMonster reference manually
        position = null;
    }

    public final StatEffect getAttackEffect(User pPlayer, int nSLV, Skill pSkill) {
        if (GameConstants.isMulungSkill(skill) || GameConstants.isPyramidSkill(skill) || GameConstants.isInflationSkill(skill)) {
            nSLV = 1;
        } else if (nSLV <= 0) {
            return null;
        }
        int dd = ((display & 0x8000) != 0 ? (display - 0x8000) : display);
        if (GameConstants.isLinkedAttackSkill(skill)) {
            final Skill skillLink = SkillFactory.getSkill(skill);
            if (1 == 1) { //is bugged after red
                return skillLink.getEffect(nSLV);
            }

            if (dd > SkillFactory.Delay.magic6.i && dd != SkillFactory.Delay.shot.i && dd != SkillFactory.Delay.fist.i) {
                if (skillLink.getAnimation() == -1 || Math.abs(skillLink.getAnimation() - dd) > 0x10) {
                    pPlayer.dropMessage(-1, "Animation: " + skillLink.getAnimation() + " | " + HexTool.getOpcodeToString(skillLink.getAnimation()));
                    if (skillLink.getAnimation() == -1) {
                        pPlayer.dropMessage(5, "Please report this: animation for skill " + skillLink.getId() + " doesn't exist");
                    } else {
                        //AutobanManager.getInstance().autoban(chr.getClient(), "No delay hack, SkillID : " + skillLink.getId() + ", animation: " + dd + ", expected: " + skillLink.getAnimation());
                    }
                    if (pSkill.getId() == 24121003) {
                        return skillLink.getEffect(nSLV);
                    }
                    if (GameConstants.isZero(pSkill.getId() / 10000)) {
                        return skillLink.getEffect(nSLV); //idk wat 2 do w/ dis
                    }
                    if (GameConstants.isBeastTamer(pSkill.getId() / 11000)) {
                        return skillLink.getEffect(nSLV);
                    }
                    return null;
                }
            }
            return skillLink.getEffect(nSLV);
        } // i'm too lazy to calculate the new skill types =.=
        /*
         * if (dd > SkillFactory.Delay.magic6.i && dd !=
         * SkillFactory.Delay.shot.i && dd != SkillFactory.Delay.fist.i) { if
         * (skill_.getAnimation() == -1 || Math.abs(skill_.getAnimation() - dd)
         * > 0x10) { if (skill_.getAnimation() == -1) { chr.dropMessage(5,
         * "Please report this: animation for skill " + skill_.getId() + "
         * doesn't exist"); } else {
         * AutobanManager.getInstance().autoban(chr.getClient(), "No delay hack,
         * SkillID : " + skill_.getId() + ", animation: " + dd + ", expected: "
         * + skill_.getAnimation()); } return null; }
         }
         */
        return pSkill.getEffect(nSLV);
    }

}
