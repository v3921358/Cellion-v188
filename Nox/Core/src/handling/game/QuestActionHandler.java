package handling.game;

import client.Client;
import constants.GameConstants;
import constants.ServerConstants;
import net.ProcessPacket;
import scripting.provider.NPCScriptManager;
import server.maps.objects.User;
import server.quest.Quest;
import net.InPacket;
import tools.LogHelper;
import tools.packet.CField;
import tools.packet.CWvsContext;

/**
 *
 * @author
 */
public class QuestActionHandler implements ProcessPacket<Client> {

    @Override
    public boolean ValidateState(Client c) {
        return true;
    }

    /*
enum QuestRes
{
  QuestReq_LostItem = 0x0,
  QuestReq_AcceptQuest = 0x1,
  QuestReq_CompleteQuest = 0x2,
  QuestReq_ResignQuest = 0x3,
  QuestReq_OpeningScript = 0x4,
  QuestReq_CompleteScript = 0x5,
  QuestReq_LaterStep = 0x6,
  QuestRes_Start_QuestTimer = 0x7,
  QuestRes_End_QuestTimer = 0x8,
  QuestRes_Start_TimeKeepQuestTimer = 0x9,
  QuestRes_End_TimeKeepQuestTimer = 0xA,
  QuestRes_Act_Success = 0xB,
  QuestRes_Act_Failed_Unknown = 0xC,
  QuestRes_Act_Failed_Inventory = 0xD,
  QuestRes_Act_Failed_Meso = 0xE,
  QuestRes_Act_Failed_OverflowMeso = 0xF,
  QuestRes_Act_Failed_Pet = 0x10,
  QuestRes_Act_Failed_Equipped = 0x11,
  QuestRes_Act_Failed_OnlyItem = 0x12,
  QuestRes_Act_Failed_TimeOver = 0x13,
  QuestRes_Act_Failed_State = 0x14,
  QuestRes_Act_Failed_Quest = 0x15,
  QuestRes_Act_Failed_Block = 0x16,
  QuestRes_Act_Failed_Universe = 0x17,
  QuestRes_Act_Reset_QuestTimer = 0x18,
  MakingRes_Success_SoSo = 0x19,
  MakingRes_Success_Good = 0x1A,
  MakingRes_Success_Cool = 0x1B,
  MakingRes_Fail_Unknown = 0x1C,
  MakingRes_Fail_Prob = 0x1D,
  MakingRes_Fail_NoDecomposer = 0x1E,
  MakingRes_Fail_MesoOverflow = 0x1F,
  MakingRes_Fail_TooHighFee = 0x20,
  MakingRes_Fail_NotEnoughMeso = 0x21,
};
     */
    @Override
    public void Process(Client c, InPacket iPacket) {
        final byte action = iPacket.DecodeByte();
        int quest = iPacket.DecodeInt();
        if (quest == 20734) {
            c.SendPacket(CWvsContext.ultimateExplorer());
            return;
        }
        User chr = c.getPlayer();
        if (chr == null) {
            return;
        }

        // Detailed Quest Action Debug
        if (ServerConstants.DEVELOPER_DEBUG_MODE && !ServerConstants.REDUCED_DEBUG_SPAM) {
            String debugAction = "";
            switch (action) {
                case 0:
                    debugAction = "Restore Item";
                    break;
                case 1:
                    debugAction = "Start Quest";
                    break;
                case 2:
                    debugAction = "End Quest";
                    break;
                case 3:
                    debugAction = "Forfeit Quest";
                    break;
                case 4:
                    debugAction = "Start Quest Script";
                    break;
                case 5:
                    debugAction = "End Quest Script";
                    break;
            }
            System.out.println(String.format("[Debug] Requesting Quest ID (%s), Action (%s)", quest, debugAction));
        }

        final Quest q = Quest.getInstance(quest);
        switch (action) {
            case 0: { // Restore lost item
                //chr.updateTick(iPacket.DecodeInt());
                iPacket.DecodeInt();
                final int itemid = iPacket.DecodeInt();
                q.RestoreLostItem(chr, itemid);
                break;
            }
            case 1: { // Start Quest
                final int npc = iPacket.DecodeInt();
                if (npc == 0 && quest > 0) {
                    q.forceStart(chr, npc, null);
                } else if (!q.hasStartScript()) {
                    q.start(chr, npc);
                }
                break;
            }
            case 2: { // Complete Quest
                final int npc = iPacket.DecodeInt();
                //chr.updateTick(iPacket.DecodeInt());
                iPacket.DecodeInt();
                if (q.hasEndScript()) {
                    return;
                }
                if (iPacket.GetRemainder() >= 4) {
                    q.complete(chr, npc, iPacket.DecodeInt());
                } else {
                    q.complete(chr, npc);
                }
                // c.write(CField.completeQuest(c.getPlayer(), quest));
                //c.write(CField.updateQuestInfo(c.getPlayer(), quest, npc, (byte)14));
                // 6 = start quest
                // 7 = unknown error
                // 8 = equip is full
                // 9 = not enough mesos
                // 11 = due to the equipment currently being worn wtf o.o
                // 12 = you may not posess more than one of this item
                break;
            }
            case 3: { // Forfeit Quest
                if (GameConstants.canForfeit(q.getId())) {
                    q.forfeit(chr);
                } else {
                    chr.dropMessage(1, "You may not forfeit this quest.");
                }
                break;
            }
            case 4: { // Scripted Start Quest
                final int npc = iPacket.DecodeInt();
                if (chr.hasBlockedInventory()) {
                    return;
                }

                //c.getPlayer().updateTick(iPacket.DecodeInt());
                NPCScriptManager.getInstance().startQuest(c, npc, quest);
                break;
            }
            case 5: { // Scripted End Quest
                final int npc = iPacket.DecodeInt();
                iPacket.DecodeInt();
                if (chr.hasBlockedInventory()) {
                    return;
                }
                //c.getPlayer().updateTick(iPacket.DecodeInt());
                NPCScriptManager.getInstance().endQuest(c, npc, quest, false);
                c.SendPacket(CField.EffectPacket.showForeignEffect(CField.EffectPacket.UserEffectCodes.QuestComplete)); // Quest completion
                chr.getMap().broadcastMessage(chr, CField.EffectPacket.showForeignEffect(chr.getId(), CField.EffectPacket.UserEffectCodes.QuestComplete), false);
                break;
            }
        }
    }

}
