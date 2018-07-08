/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package handling.game;

import client.ClientSocket;
import client.QuestStatus;
import client.Skill;
import client.SkillEntry;
import client.SkillFactory;
import constants.GameConstants;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.InPacket;
import net.ProcessPacket;
import server.messages.UpdateQuestMessage;
import server.quest.Quest;
import server.skills.VCore;
import server.skills.VCore.EnforceOption;
import server.skills.VMatrixRecord;
import tools.packet.WvsContext;

/**
 *
 * @author Five
 */
public final class UpdateMatrixHandler implements ProcessPacket<ClientSocket> {

    @Override
    public boolean ValidateState(ClientSocket c) {
        return true;
    }

    @Override
    public void Process(ClientSocket c, InPacket iPacket) {
        int nUpdateType = iPacket.DecodeInt(); // 0 = Enable, 1 = Disable, 3 = Enhance, 4 = DisassembleSingle, 5 = DisassembleMultiple, 6 = CraftNode, 8 = CraftNodestone
        if (nUpdateType == VMatrixRecord.Enable || nUpdateType == VMatrixRecord.Disable) {
            int nSlot = iPacket.DecodeInt(); // VMatrix Record
            iPacket.DecodeInt(); // Always -1
            VMatrixRecord pRecord = c.getPlayer().aVMatrixRecord.get(nSlot);
            if (pRecord != null) {
                if (nUpdateType == VMatrixRecord.Enable) {
                    pRecord.nState = VMatrixRecord.Active;
                } else if (nUpdateType == VMatrixRecord.Disable) {
                    pRecord.nState = VMatrixRecord.Inactive;
                }
            }
            UpdateMatrixRecord(c);
            c.SendPacket(WvsContext.OnVMatrixUpdate(c.getPlayer().aVMatrixRecord, true, nUpdateType, nSlot));
        } else if (nUpdateType == VMatrixRecord.DisassembleSingle) {
            int nSlot = iPacket.DecodeInt();
            iPacket.DecodeInt(); // Always -1
            VMatrixRecord pRecord = c.getPlayer().aVMatrixRecord.get(nSlot);
            if (pRecord != null && pRecord.nState != VMatrixRecord.Active) {
                int nShard = 0;
                if (VCore.IsSkillNode(pRecord.nCoreID)) {
                    nShard = VCore.mSkillEnforce.get(pRecord.nSLV).nExtract;
                } else if (VCore.IsBoostNode(pRecord.nCoreID)) {
                    nShard = VCore.mBoostEnforce.get(pRecord.nSLV).nExtract;
                } else if (VCore.IsSpecialNode(pRecord.nCoreID)) {
                    nShard = VCore.mSpecialEnforce.get(pRecord.nSLV).nExtract;
                }
                if (c.getPlayer().aVMatrixRecord.remove(nSlot) != null) {
                    final QuestStatus pQuest = c.getPlayer().getQuestNAdd(Quest.getInstance(1477));
                    String sVal = pQuest.getCustomData();
                    if (sVal != null && !sVal.isEmpty()) {
                        String sNum = sVal.substring(6);
                        int nTotalShardCount = Integer.valueOf(sNum) + nShard;
                        pQuest.setCustomData("count=" + Integer.toString(nTotalShardCount));
                    }

                    //SimpleStrMap pStr = c.getPlayer().mQuestRecordEx.get(QuestRecordEx.NodeShard);
                    //int nTotalShard = Integer.valueOf(pStr.GetValue("count")) + nShard;
                    //pStr.SetValue("count", Integer.toString(nTotalShard));
                    //SendPacket(CWvsContext.OnMessage(new Message(MessageResult.QuestRecordExMessage, QuestRecordEx.NodeShard, pStr.GetRawString())));
                    c.SendPacket(WvsContext.messagePacket(new UpdateQuestMessage(1477, pQuest.getCustomData())));
                    UpdateMatrixRecord(c);
                    c.SendPacket(WvsContext.OnVMatrixUpdate(c.getPlayer().aVMatrixRecord, true, nUpdateType, nSlot));
                    c.SendPacket(WvsContext.OnNodeShardResult(nShard));
                    //usCharacterDataModFlag |= DBChar.QuestRecordEx.Get();
                }
            }
        } else if (nUpdateType == VMatrixRecord.DisassembleMultiple) {
            int nCount = iPacket.DecodeInt();
            final QuestStatus pQuest = c.getPlayer().getQuestNAdd(Quest.getInstance(1477));
            String sVal = pQuest.getCustomData();
            int nOldShard = 0;
            int nTotalShard = 0;
            if (sVal != null && !sVal.isEmpty()) {
                String sNum = sVal.substring(6);
                nOldShard = Integer.valueOf(sNum);
                nTotalShard = Integer.valueOf(sNum);
            }
            //SimpleStrMap pStr = c.getPlayer().mQuestRecordEx.get(QuestRecordEx.NodeShard);
            //int nTotalShard = Integer.valueOf(pStr.GetValue("count"));
            int nGainShard = 0;
            List<VMatrixRecord> aChangeLog = new ArrayList<>();
            for (int i = 0; i < nCount; i++) {
                int nSlot = iPacket.DecodeInt();
                VMatrixRecord pRecord = c.getPlayer().aVMatrixRecord.get(nSlot);
                if (pRecord != null && pRecord.nState != VMatrixRecord.Active) {
                    int nShard = 0;
                    if (VCore.IsSkillNode(pRecord.nCoreID)) {
                        nShard = VCore.mSkillEnforce.get(pRecord.nSLV).nExtract;
                    } else if (VCore.IsBoostNode(pRecord.nCoreID)) {
                        nShard = VCore.mBoostEnforce.get(pRecord.nSLV).nExtract;
                    } else if (VCore.IsSpecialNode(pRecord.nCoreID)) {
                        nShard = VCore.mSpecialEnforce.get(pRecord.nSLV).nExtract;
                    }
                    aChangeLog.add(pRecord);
                    nTotalShard += nShard;
                    nGainShard += nShard;
                }
            }
            for (VMatrixRecord pRecord : aChangeLog) {
                c.getPlayer().aVMatrixRecord.remove(pRecord);
            }
            aChangeLog.clear();
            //if (nTotalShard != Integer.valueOf(pStr.GetValue("count"))) {
            if (nTotalShard != nOldShard) {
                //pStr.SetValue("count", Integer.toString(nTotalShard));
                pQuest.setCustomData("count=" + Integer.toString(nTotalShard));

                //SendPacket(CWvsContext.OnMessage(new Message(MessageResult.QuestRecordExMessage, QuestRecordEx.NodeShard, pStr.GetRawString())));
                c.SendPacket(WvsContext.messagePacket(new UpdateQuestMessage(1477, pQuest.getCustomData())));

                UpdateMatrixRecord(c);
                c.SendPacket(WvsContext.OnVMatrixUpdate(c.getPlayer().aVMatrixRecord, true, nUpdateType, 0));
                c.SendPacket(WvsContext.OnNodeShardResult(nGainShard));
                //usCharacterDataModFlag |= DBChar.QuestRecordEx.Get();
            }
        } else if (nUpdateType == VMatrixRecord.Enhance) {
            int nSlot = iPacket.DecodeInt();
            VMatrixRecord pRecord = c.getPlayer().aVMatrixRecord.get(nSlot);
            if (pRecord != null && pRecord.nSLV < VCore.GetMaxLevel(VCore.GetCore(pRecord.nCoreID).nType)) {
                int nGainExp = 0;
                int nCount = iPacket.DecodeInt();
                List<VMatrixRecord> aChangeLog = new ArrayList<>();
                for (int i = 0; i < nCount; i++) {
                    int nEnhanceSlot = iPacket.DecodeInt();
                    VMatrixRecord pEnhanceRecord = c.getPlayer().aVMatrixRecord.get(nEnhanceSlot);
                    if (pEnhanceRecord != null && pRecord.nSkillID == pEnhanceRecord.nSkillID && !VCore.IsSpecialNode(pRecord.nCoreID) && !VCore.IsSpecialNode(pEnhanceRecord.nCoreID)) {
                        int nExp = 0;
                        if (VCore.IsSkillNode(pEnhanceRecord.nCoreID)) {
                            nExp = VCore.mSkillEnforce.get(pEnhanceRecord.nSLV).nEnforceExp;
                        } else if (VCore.IsBoostNode(pEnhanceRecord.nCoreID)) {
                            nExp = VCore.mBoostEnforce.get(pEnhanceRecord.nSLV).nEnforceExp;
                        }
                        nGainExp += nExp;
                        aChangeLog.add(pEnhanceRecord);
                    }
                }
                for (VMatrixRecord pEnhanceRecord : aChangeLog) {
                    c.getPlayer().aVMatrixRecord.remove(pEnhanceRecord);
                }
                aChangeLog.clear();
                int nCurSLV = pRecord.nSLV;
                Map<Integer, EnforceOption> mEnforce = VCore.GetEnforceOption(VCore.GetCore(pRecord.nCoreID).nType);
                int nGainExpBackup = nGainExp;
                while (nGainExp > 0) {
                    int nNextExp = mEnforce.get(pRecord.nSLV).nNextExp - pRecord.nExp;
                    if (nGainExp > nNextExp) {
                        pRecord.nSLV = Math.min(pRecord.nSLV + 1, 25);
                        pRecord.nExp = 0;
                        nGainExp -= nNextExp;
                    } else {
                        pRecord.nExp += nGainExp;
                        nGainExp = 0;
                    }
                }
                UpdateMatrixRecord(c);
                c.SendPacket(WvsContext.OnVMatrixUpdate(c.getPlayer().aVMatrixRecord, true, nUpdateType, 0));
                c.SendPacket(WvsContext.OnNodeEnhanceResult(c.getPlayer().aVMatrixRecord.indexOf(pRecord), nGainExpBackup, nCurSLV, pRecord.nSLV));
            }
        } else if (nUpdateType == VMatrixRecord.CraftNode) {
            int nCoreID = iPacket.DecodeInt();
            VCore pCore = VCore.GetCore(nCoreID);
            if (pCore != null) {
                int nPrice = 0;
                if (VCore.IsSkillNode(nCoreID)) {
                    nPrice = 140;
                } else if (VCore.IsBoostNode(nCoreID)) {
                    nPrice = 70;
                } else if (VCore.IsSpecialNode(nCoreID)) {
                    nPrice = 250;
                }
                if (nPrice > 0) {
                    final QuestStatus pQuest = c.getPlayer().getQuestNAdd(Quest.getInstance(1477));
                    String sVal = pQuest.getCustomData();
                    int nShardCount = 0;
                    if (sVal != null && !sVal.isEmpty()) {
                        String sNum = sVal.substring(6);
                        nShardCount = Integer.valueOf(sNum);
                    }
                    //SimpleStrMap pStr = c.getPlayer().mQuestRecordEx.get(QuestRecordEx.NodeShard);
                    //int nShardCount = Integer.valueOf(pStr.GetValue("count"));

                    if (nShardCount >= nPrice) {
                        //pStr.SetValue("count", Integer.toString(nShardCount - nPrice));
                        pQuest.setCustomData("count=" + Integer.toString(nShardCount - nPrice));
                        //SendPacket(WvsContext.OnMessage(new Message(MessageResult.QuestRecordExMessage, QuestRecordEx.NodeShard, pStr.GetRawString())));
                        c.SendPacket(WvsContext.messagePacket(new UpdateQuestMessage(1477, pQuest.getCustomData())));
                        //usCharacterDataModFlag |= DBChar.QuestRecordEx.Get();
                        VMatrixRecord pRecord = new VMatrixRecord();
                        pRecord.nCoreID = pCore.nCoreID;
                        if (!VCore.IsSpecialNode(nCoreID)) {
                            pRecord.nSkillID = pCore.aConnectSkill.get(0);
                            pRecord.nSLV = 1;
                            pRecord.nMasterLev = pCore.nMaxLevel;
                        } else {
                            pRecord.nSkillID = 0; // Neckson sets to 0? We have pCore.pOption.nSkillID
                            pRecord.nSLV = 1; // pCore.pOption.nSLV
                            pRecord.nMasterLev = 1; // pCore.nMaxLevel
                            long tCur = System.currentTimeMillis();
                            tCur += 86400000 * pCore.nExpireAfter;
                            //FileTime ftNow = FileTime.GetSystemTime();
                            //ftNow.Add(FileTime.Day, pCore.nExpireAfter);
                            pRecord.ftExpirationDate = tCur;
                        }
                        if (VCore.IsBoostNode(nCoreID)) {
                            List<VCore> aBoostNode = VCore.GetBoostNodes();
                            aBoostNode.remove(pCore);
                            pCore = aBoostNode.get((int) (Math.random() % aBoostNode.size()));
                            while (!pCore.IsJobSkill(c.getPlayer().getJob())) {
                                pCore = aBoostNode.get((int) (Math.random() % aBoostNode.size()));
                            }
                            aBoostNode.remove(pCore);
                            pRecord.nSkillID2 = pCore.aConnectSkill.get(0);
                            pCore = aBoostNode.get((int) (Math.random() % aBoostNode.size()));
                            while (!pCore.IsJobSkill(c.getPlayer().getJob())) {
                                pCore = aBoostNode.get((int) (Math.random() % aBoostNode.size()));
                            }
                            pRecord.nSkillID3 = pCore.aConnectSkill.get(0);
                        }
                        c.getPlayer().aVMatrixRecord.add(pRecord);
                        UpdateMatrixRecord(c);
                        c.SendPacket(WvsContext.OnVMatrixUpdate(c.getPlayer().aVMatrixRecord, true, nUpdateType, 0));
                        c.SendPacket(WvsContext.OnNodeCraftResult(nCoreID, pRecord.nSkillID, pRecord.nSkillID2, pRecord.nSkillID3));
                    }
                }
            }
        }
    }

    public void UpdateMatrixRecord(ClientSocket c) {
        // Neckson seriously removes all then re-adds and sends two skillrecord packets
        //List<SkillRecord> aChange = new ArrayList<>();
        //SkillRecord pSkillRecord;
        Map<Skill, SkillEntry> mChange = new HashMap<>();
        Skill pSkill;
        SkillEntry pEntry;
        for (VMatrixRecord pMatrixRecord : c.getPlayer().aVMatrixRecord) {
            if (pMatrixRecord.nSkillID != 0) {
                /*
                pSkillRecord = new SkillRecord();
                pSkillRecord.nInfo = 0;
                pSkillRecord.nSkillID = pMatrixRecord.nSkillID;
                pSkillRecord.nMasterLevel = pMatrixRecord.nMasterLev;
                pSkillRecord.ftExpiration = pMatrixRecord.ftExpirationDate;
                aChange.add(pSkillRecord);
                //c.getPlayer().mSkillRecord.remove(pMatrixRecord.nSkillID);
                //c.getPlayer().mSkillExpired.remove(pMatrixRecord.nSkillID);
                //c.getPlayer().mSkillMasterLev.remove(pMatrixRecord.nSkillID);
                 */
                pSkill = SkillFactory.getSkill(pMatrixRecord.nSkillID);
                pEntry = new SkillEntry(0, (byte) pMatrixRecord.nMasterLev, pMatrixRecord.ftExpirationDate);
                c.getPlayer().changeSingleSkillLevel(pSkill, pEntry.skillevel, pEntry.masterlevel, pEntry.expiration);
                mChange.put(pSkill, pEntry);
            }
            if (pMatrixRecord.nSkillID2 != 0) {
                /*
                pSkillRecord = new SkillRecord();
                pSkillRecord.nInfo = 0;
                pSkillRecord.nSkillID = pMatrixRecord.nSkillID2;
                pSkillRecord.nMasterLevel = pMatrixRecord.nMasterLev;
                pSkillRecord.ftExpiration = pMatrixRecord.ftExpirationDate;
                aChange.add(pSkillRecord);
                c.getPlayer().mSkillRecord.remove(pMatrixRecord.nSkillID2);
                c.getPlayer().mSkillExpired.remove(pMatrixRecord.nSkillID2);
                c.getPlayer().mSkillMasterLev.remove(pMatrixRecord.nSkillID2);
                 */
                pSkill = SkillFactory.getSkill(pMatrixRecord.nSkillID);
                pEntry = new SkillEntry(0, (byte) pMatrixRecord.nMasterLev, pMatrixRecord.ftExpirationDate);
                c.getPlayer().changeSingleSkillLevel(pSkill, pEntry.skillevel, pEntry.masterlevel, pEntry.expiration);
                mChange.put(pSkill, pEntry);
            }
            if (pMatrixRecord.nSkillID3 != 0) {
                /*
                pSkillRecord = new SkillRecord();
                pSkillRecord.nInfo = 0;
                pSkillRecord.nSkillID = pMatrixRecord.nSkillID3;
                pSkillRecord.nMasterLevel = pMatrixRecord.nMasterLev;
                pSkillRecord.ftExpiration = pMatrixRecord.ftExpirationDate;
                aChange.add(pSkillRecord);
                c.getPlayer().mSkillRecord.remove(pMatrixRecord.nSkillID3);
                c.getPlayer().mSkillExpired.remove(pMatrixRecord.nSkillID3);
                c.getPlayer().mSkillMasterLev.remove(pMatrixRecord.nSkillID3);
                 */
                pSkill = SkillFactory.getSkill(pMatrixRecord.nSkillID);
                pEntry = new SkillEntry(0, (byte) pMatrixRecord.nMasterLev, pMatrixRecord.ftExpirationDate);
                c.getPlayer().changeSingleSkillLevel(pSkill, pEntry.skillevel, pEntry.masterlevel, pEntry.expiration);
                mChange.put(pSkill, pEntry);
            }
        }
        //pUserSkillRecord.SendCharacterSkillRecord(Request.Excl, aChange);
        //aChange.clear();
        c.SendPacket(WvsContext.updateSkills(mChange, false));
        mChange.clear();

        for (VMatrixRecord pMatrixRecord : c.getPlayer().aVMatrixRecord) {
            if (pMatrixRecord.nState == VMatrixRecord.Active) {
                //int nSLV = pMatrixRecord.nSLV;
                int nSLV = pMatrixRecord.nSLV + c.getPlayer().getSkillLevel(pMatrixRecord.nSkillID);
                //if (c.getPlayer().mSkillRecord.containsKey(pMatrixRecord.nSkillID)) {
                //    nSLV += c.getPlayer().mSkillRecord.get(pMatrixRecord.nSkillID);
                //}
                if (pMatrixRecord.nSkillID != 0) {
                nSLV = Math.min(nSLV, pMatrixRecord.nMasterLev);
                /*
                pSkillRecord = new SkillRecord();
                pSkillRecord.nInfo = nSLV;
                pSkillRecord.nSkillID = pMatrixRecord.nSkillID;
                pSkillRecord.nMasterLevel = pMatrixRecord.nMasterLev;
                pSkillRecord.ftExpiration = pMatrixRecord.ftExpirationDate;
                aChange.add(pSkillRecord);
                c.getPlayer().mSkillRecord.put(pMatrixRecord.nSkillID, nSLV);
                c.getPlayer().mSkillExpired.put(pMatrixRecord.nSkillID, pMatrixRecord.ftExpirationDate);
                c.getPlayer().mSkillMasterLev.put(pMatrixRecord.nSkillID, pMatrixRecord.nMasterLev);
                 */

                pSkill = SkillFactory.getSkill(pMatrixRecord.nSkillID);
                pEntry = new SkillEntry(nSLV, (byte) pMatrixRecord.nMasterLev, pMatrixRecord.ftExpirationDate);
                c.getPlayer().changeSingleSkillLevel(pSkill, pEntry.skillevel, pEntry.masterlevel, pEntry.expiration);
                mChange.put(pSkill, pEntry);
                }
                if (pMatrixRecord.nSkillID2 != 0) {
                    nSLV = pMatrixRecord.nSLV + c.getPlayer().getSkillLevel(pMatrixRecord.nSkillID);
                    //nSLV = pMatrixRecord.nSLV;
                    //if (c.getPlayer().mSkillRecord.containsKey(pMatrixRecord.nSkillID2)) {
                    //    nSLV += c.getPlayer().mSkillRecord.get(pMatrixRecord.nSkillID2);
                    //}
                    nSLV = Math.min(nSLV, pMatrixRecord.nMasterLev);
                    /*
                    pSkillRecord = new SkillRecord();
                    pSkillRecord.nInfo = nSLV;
                    pSkillRecord.nSkillID = pMatrixRecord.nSkillID2;
                    pSkillRecord.nMasterLevel = pMatrixRecord.nMasterLev;
                    pSkillRecord.ftExpiration = pMatrixRecord.ftExpirationDate;
                    aChange.add(pSkillRecord);
                    c.getPlayer().mSkillRecord.put(pMatrixRecord.nSkillID2, nSLV);
                    c.getPlayer().mSkillExpired.put(pMatrixRecord.nSkillID2, pMatrixRecord.ftExpirationDate);
                    c.getPlayer().mSkillMasterLev.put(pMatrixRecord.nSkillID2, pMatrixRecord.nMasterLev);
                     */

                    pSkill = SkillFactory.getSkill(pMatrixRecord.nSkillID);
                    pEntry = new SkillEntry(nSLV, (byte) pMatrixRecord.nMasterLev, pMatrixRecord.ftExpirationDate);
                    c.getPlayer().changeSingleSkillLevel(pSkill, pEntry.skillevel, pEntry.masterlevel, pEntry.expiration);
                    mChange.put(pSkill, pEntry);
                }
                if (pMatrixRecord.nSkillID3 != 0) {
                    nSLV = pMatrixRecord.nSLV + c.getPlayer().getSkillLevel(pMatrixRecord.nSkillID);
                    //nSLV = pMatrixRecord.nSLV;
                    //if (c.getPlayer().mSkillRecord.containsKey(pMatrixRecord.nSkillID3)) {
                    //    nSLV += c.getPlayer().mSkillRecord.get(pMatrixRecord.nSkillID3);
                    //}
                    nSLV = Math.min(nSLV, pMatrixRecord.nMasterLev);
                    /*
                    pSkillRecord = new SkillRecord();
                    pSkillRecord.nInfo = nSLV;
                    pSkillRecord.nSkillID = pMatrixRecord.nSkillID3;
                    pSkillRecord.nMasterLevel = pMatrixRecord.nMasterLev;
                    pSkillRecord.ftExpiration = pMatrixRecord.ftExpirationDate;
                    aChange.add(pSkillRecord);
                    c.getPlayer().mSkillRecord.put(pMatrixRecord.nSkillID3, nSLV);
                    c.getPlayer().mSkillExpired.put(pMatrixRecord.nSkillID3, pMatrixRecord.ftExpirationDate);
                    c.getPlayer().mSkillMasterLev.put(pMatrixRecord.nSkillID3, pMatrixRecord.nMasterLev);
                     */

                    pSkill = SkillFactory.getSkill(pMatrixRecord.nSkillID);
                    pEntry = new SkillEntry(nSLV, (byte) pMatrixRecord.nMasterLev, pMatrixRecord.ftExpirationDate);
                    c.getPlayer().changeSingleSkillLevel(pSkill, pEntry.skillevel, pEntry.masterlevel, pEntry.expiration);
                    mChange.put(pSkill, pEntry);
                }
            }
        }
        //pUserSkillRecord.SendCharacterSkillRecord(Request.Excl, aChange);
        //aChange.clear();
        c.SendPacket(WvsContext.updateSkills(mChange, false));
        mChange.clear();

        //usCharacterDataModFlag |= DBChar.SkillRecord.Get() | DBChar.WildHunterInfo.Get();
    }
}
