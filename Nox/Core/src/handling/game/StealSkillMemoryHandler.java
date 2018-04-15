package handling.game;

import client.MapleClient;
import client.SkillFactory;
import constants.GameConstants;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import server.maps.objects.User;
import net.InPacket;
import tools.packet.CWvsContext;
import net.ProcessPacket;

/**
 *
 * @author Five
 */
public class StealSkillMemoryHandler implements ProcessPacket<MapleClient> {

    @Override
    public boolean ValidateState(MapleClient c) {
        return true;
    }

    @Override
    public void Process(MapleClient c, InPacket iPacket) {
        int nStealSkillID = iPacket.DecodeInt();
        int dwCharacterID = iPacket.DecodeInt();
        boolean bRemove = iPacket.DecodeBool();
        int nResult;
        int nSkillRoot = nStealSkillID / 10000;
        int nSlotID = GameConstants.getJobNumber(nSkillRoot);
        if (GameConstants.isHyperSkill(SkillFactory.getSkill(nStealSkillID))) {
            nSlotID = 5;
        }
        int nPOS = 0;
        int nStealSLV = 0;
        int nStealSMLV = 0;
        if (bRemove) {
            nResult = 3; // Remove
            for (nPOS = 0; nPOS < 4; nPOS++) {
                if (c.getPlayer().aStealMemory[nSlotID][nPOS] == nStealSkillID) {
                    c.getPlayer().aStealMemory[nSlotID][nPOS] = 0;
                    break;
                }
            }
            for (Map.Entry<Integer, Integer> mStealSkill : c.getPlayer().mStealSkillInfo.entrySet()) {
                int nEquipped = mStealSkill.getValue();
                if (nEquipped == nStealSkillID) {
                    c.getPlayer().mStealSkillInfo.put(mStealSkill.getKey(), 0);
                }
            }
            c.getPlayer().changed_skills = true;
            c.getPlayer().changeSkillLevelSkip(SkillFactory.getSkill(nStealSkillID), 0, (byte) 0);
        } else {
            User pUser = c.getPlayer().getMap().getCharacterById(dwCharacterID);
            nResult = 0; // Steal
            if (pUser != null) {
                List<Integer> aPOS = new ArrayList<>();
                nPOS = 0;
                for (int nSkillID : c.getPlayer().aStealMemory[nSlotID]) {
                    if (nPOS < GameConstants.getNumSteal(nSlotID)) {
                        System.err.println(String.format("aStealMemory[%d][%d] = %d", nSlotID, nPOS, nSkillID));
                        if (nSkillID == 0 || nSkillID == nStealSkillID) {
                            if (nSkillID == nStealSkillID) {
                                c.getPlayer().aStealMemory[nSlotID][nPOS] = nStealSkillID;
                                aPOS.clear();
                                break;
                            }
                            aPOS.add(nPOS);
                        }
                        nPOS++;
                    } else {
                        break;
                    }
                }
                if (!aPOS.isEmpty()) {
                    nPOS = aPOS.get(0);
                    c.getPlayer().aStealMemory[nSlotID][nPOS] = nStealSkillID;
                    aPOS.clear();
                }
                nStealSLV = pUser.getTotalSkillLevel(nStealSkillID);
                nStealSMLV = pUser.getMasterLevel(nStealSkillID);
                c.getPlayer().changed_skills = true;
                c.getPlayer().changeSkillLevelSkip(SkillFactory.getSkill(nStealSkillID), nStealSLV, (byte) nStealSMLV);
            } else {
                nResult = 1; // NoTarget
            }
        }
        c.SendPacket(CWvsContext.OnChangeStealMemoryResult(1, nResult, nSlotID, nPOS, nStealSkillID, nStealSLV, nStealSMLV));
    }
}
