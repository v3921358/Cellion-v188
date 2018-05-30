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
        User pPlayer = c.getPlayer();
        pPlayer.updateTick(iPacket.DecodeInt());
        final int nSkillID = iPacket.DecodeInt();
        final int nAmount = iPacket.DecodeInt();
        boolean bBeginnerSkill = false;
        final int nRemainingSP;
        if (!GameConstants.isBeastTamer(pPlayer.getJob()) && GameConstants.isBeginnerJob(nSkillID / 10000) && (nSkillID % 10000 == 1000 || nSkillID % 10000 == 1001 || nSkillID % 10000 == 1002 || nSkillID % 10000 == 2)) {
            final boolean bResistance = nSkillID / 10000 == 3000 || nSkillID / 10000 == 3001;
            final int nSnailsLevel = pPlayer.getSkillLevel(SkillFactory.getSkill(((nSkillID / 10000) * 10000) + 1000));
            final int bRecoveryLevel = pPlayer.getSkillLevel(SkillFactory.getSkill(((nSkillID / 10000) * 10000) + 1001));
            final int nNimbleFeetLevel = pPlayer.getSkillLevel(SkillFactory.getSkill(((nSkillID / 10000) * 10000) + (bResistance ? 2 : 1002)));
            nRemainingSP = Math.min((pPlayer.getLevel() - 1), bResistance ? 9 : 6) - nSnailsLevel - bRecoveryLevel - nNimbleFeetLevel;
            bBeginnerSkill = true;
        } else {
            nRemainingSP = pPlayer.getRemainingSp(GameConstants.getSkillBookForSkill(nSkillID));
            if (pPlayer.isDeveloper()) pPlayer.dropMessage(5, "[DistributeSP Debug] Skill Book : " + GameConstants.getSkillBookForSkill(nSkillID));
        }
        Skill pSkill = SkillFactory.getSkill(nSkillID);
        for (Pair<String, Integer> ski : pSkill.getRequiredSkills()) {
            if (ski.left.equals("level")) {
                if (pPlayer.getLevel() < ski.right) {
                    return;
                }
            } else {
                if (!GameConstants.isBeastTamer(pPlayer.getJob())) {
                    int left = Integer.parseInt(ski.left);
                    if (pPlayer.getSkillLevel(SkillFactory.getSkill(left)) < ski.right && !pPlayer.isGM()) {
                        AutobanManager.getInstance().addPoints(c, 1000, 0, "Trying to learn a skill without the required skill (" + nSkillID + ")");
                        return;
                    }
                }
            }
        }
        final int nMaxSLV = pSkill.isFourthJob() ? pPlayer.getMasterLevel(pSkill) : pSkill.getMaxLevel();
        final int nCurrentSLV = pPlayer.getSkillLevel(pSkill);

        if (pSkill.isInvisible() && pPlayer.getSkillLevel(pSkill) == 0 && !pPlayer.isGM()) {
            if ((pSkill.isFourthJob() && pPlayer.getMasterLevel(pSkill) == 0) || (!pSkill.isFourthJob() && nMaxSLV < 10 && !GameConstants.isDualBlade(pPlayer.getJob()) && !bBeginnerSkill && pPlayer.getMasterLevel(pSkill) <= 0)) {
                c.SendPacket(WvsContext.enableActions());
                AutobanManager.getInstance().addPoints(c, 1000, 0, "Illegal distribution of SP to invisible skills (" + nSkillID + ")");
                return;
            }
        }
        for (int i : GameConstants.blockedSkills) {
            if (pSkill.getId() == i) {
                c.SendPacket(WvsContext.enableActions());
                pPlayer.dropMessage(1, "This skill has been blocked and may not be added.");
                return;
            }
        }
        if ((nRemainingSP >= nAmount && nCurrentSLV + nAmount <= nMaxSLV) && pSkill.canBeLearnedBy(pPlayer.getJob()) || pPlayer.isGM()) {
            final int skillbook = GameConstants.getSkillBookForSkill(nSkillID);
            if (!bBeginnerSkill) {
                pPlayer.setRemainingSp(pPlayer.getRemainingSp(skillbook) - nAmount, skillbook);
            }
            /*if (GameConstants.isBeastTamer(chr.getJob())) {
                chr.setRemainingSp(chr.getRemainingSp(skillbook) - amount, skillbook);
            }*/
            pPlayer.updateSingleStat(Stat.SP, pPlayer.getRemainingSp(skillbook));
            pPlayer.changeSingleSkillLevel(pSkill, (byte) (nCurrentSLV + nAmount), pPlayer.getMasterLevel(pSkill));
            pPlayer.OnUpdateSkillData(nSkillID, (nCurrentSLV + nAmount), nMaxSLV); // Save skill info to database.
        } else if (!pSkill.canBeLearnedBy(pPlayer.getJob()) && !pPlayer.isGM()) {
            AutobanManager.getInstance().addPoints(c, 1000, 0, "Trying to learn a skill for a different job (" + nSkillID + ")");
        }
        c.SendPacket(WvsContext.enableActions());
    }

}
