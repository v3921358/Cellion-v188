/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package handling.game;

import client.ClientSocket;
import client.Stat;
import client.Skill;
import client.SkillFactory;
import constants.GameConstants;
import server.AutobanManager;
import server.maps.objects.User;
import tools.Pair;
import net.InPacket;
import tools.packet.WvsContext;
import net.ProcessPacket;

/**
 *
 * @author
 */
public class DistributeSPHandler implements ProcessPacket<ClientSocket> {

    @Override
    public boolean ValidateState(ClientSocket c) {
        return true;
    }

    @Override
    public void Process(ClientSocket c, InPacket iPacket) {
        User chr = c.getPlayer();

        c.getPlayer().updateTick(iPacket.DecodeInt());
        final int skillid = iPacket.DecodeInt();
        final int amount = iPacket.DecodeInt();
        boolean isBeginnerSkill = false;
        final int remainingSp;
        if (!GameConstants.isBeastTamer(chr.getJob()) && GameConstants.isBeginnerJob(skillid / 10000) && (skillid % 10000 == 1000 || skillid % 10000 == 1001 || skillid % 10000 == 1002 || skillid % 10000 == 2)) {
            final boolean resistance = skillid / 10000 == 3000 || skillid / 10000 == 3001;
            final int snailsLevel = chr.getSkillLevel(SkillFactory.getSkill(((skillid / 10000) * 10000) + 1000));
            final int recoveryLevel = chr.getSkillLevel(SkillFactory.getSkill(((skillid / 10000) * 10000) + 1001));
            final int nimbleFeetLevel = chr.getSkillLevel(SkillFactory.getSkill(((skillid / 10000) * 10000) + (resistance ? 2 : 1002)));
            remainingSp = Math.min((chr.getLevel() - 1), resistance ? 9 : 6) - snailsLevel - recoveryLevel - nimbleFeetLevel;
            isBeginnerSkill = true;
        } else {
            remainingSp = chr.getRemainingSp(GameConstants.getSkillBookForSkill(skillid));
            if (chr.isDeveloper()) {
                chr.dropMessage(5, "[DistributeSP Debug] Skill Book : " + GameConstants.getSkillBookForSkill(skillid));
            }
        }
        Skill skill = SkillFactory.getSkill(skillid);
        for (Pair<String, Integer> ski : skill.getRequiredSkills()) {
            if (ski.left.equals("level")) {
                if (chr.getLevel() < ski.right) {
                    return;
                }
            } else {
                if (!GameConstants.isBeastTamer(chr.getJob())) {
                    int left = Integer.parseInt(ski.left);
                    if (chr.getSkillLevel(SkillFactory.getSkill(left)) < ski.right && !chr.isGM()) {
                        AutobanManager.getInstance().addPoints(c, 1000, 0, "Trying to learn a skill without the required skill (" + skillid + ")");
                        return;
                    }
                }
            }
        }
        final int maxlevel = skill.isFourthJob() ? chr.getMasterLevel(skill) : skill.getMaxLevel();
        final int curLevel = chr.getSkillLevel(skill);

        if (skill.isInvisible() && chr.getSkillLevel(skill) == 0 && !chr.isGM()) {
            if ((skill.isFourthJob() && chr.getMasterLevel(skill) == 0) || (!skill.isFourthJob() && maxlevel < 10 && !GameConstants.isDualBlade(chr.getJob()) && !isBeginnerSkill && chr.getMasterLevel(skill) <= 0)) {
                c.SendPacket(WvsContext.enableActions());
                AutobanManager.getInstance().addPoints(c, 1000, 0, "Illegal distribution of SP to invisible skills (" + skillid + ")");
                return;
            }
        }
        for (int i : GameConstants.blockedSkills) {
            if (skill.getId() == i) {
                c.SendPacket(WvsContext.enableActions());
                chr.dropMessage(1, "This skill has been blocked and may not be added.");
                return;
            }
        }
        if ((remainingSp >= amount && curLevel + amount <= maxlevel) && skill.canBeLearnedBy(chr.getJob()) || chr.isGM()) {
            final int skillbook = GameConstants.getSkillBookForSkill(skillid);
            if (!isBeginnerSkill) {
                chr.setRemainingSp(chr.getRemainingSp(skillbook) - amount, skillbook);
            }
            /*if (GameConstants.isBeastTamer(chr.getJob())) {
                chr.setRemainingSp(chr.getRemainingSp(skillbook) - amount, skillbook);
            }*/
            chr.updateSingleStat(Stat.SP, chr.getRemainingSp(skillbook));
            chr.changeSingleSkillLevel(skill, (byte) (curLevel + amount), chr.getMasterLevel(skill));
        } else if (!skill.canBeLearnedBy(chr.getJob()) && !chr.isGM()) {
            AutobanManager.getInstance().addPoints(c, 1000, 0, "Trying to learn a skill for a different job (" + skillid + ")");
        }
        c.SendPacket(WvsContext.enableActions());
    }

}
