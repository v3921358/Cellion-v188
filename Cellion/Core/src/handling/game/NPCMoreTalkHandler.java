package handling.game;

import client.ClientSocket;
import scripting.NPCConversationManager;
import enums.NPCChatType;
import scripting.provider.NPCScriptManager;
import server.AutobanManager;
import server.maps.MapScriptMethods;
import net.InPacket;
import server.maps.objects.User.MapleCharacterConversationType;
import tools.packet.WvsContext;
import net.ProcessPacket;

/**
 *
 * @author
 */
public class NPCMoreTalkHandler implements ProcessPacket<ClientSocket> {

    @Override
    public boolean ValidateState(ClientSocket c) {
        return true;
    }

    @Override
    public void Process(ClientSocket c, InPacket iPacket) {
        final NPCChatType lastMsgType = NPCChatType.fromInt(iPacket.DecodeByte()); // 00 (last msg type I think)
        if (lastMsgType == NPCChatType.OnAskAvater && iPacket.GetRemainder() >= 4) {
            iPacket.DecodeShort();
        }
        final byte action = iPacket.DecodeByte(); // 00 = end chat, 01 == follow

        if (((lastMsgType == NPCChatType.OnAskSlideMenu && c.getPlayer().getDirection() >= 0)
                || (lastMsgType == NPCChatType.OnAskSlideMenu && c.getPlayer().getDirection() == -1)) && action == 1) {
            byte lastbyte = iPacket.DecodeByte(); // 00 = end chat, 01 == follow
            if (lastbyte == 0) {
                c.SendPacket(WvsContext.enableActions());
            } else {
                MapScriptMethods.startDirectionInfo(c.getPlayer(), lastMsgType == NPCChatType.OnAskUserDirection);
                c.SendPacket(WvsContext.enableActions());
            }
            return;
        }

        final NPCConversationManager cm = NPCScriptManager.getInstance().getCM(c);
        if (cm == null || c.getPlayer().getConversation() != MapleCharacterConversationType.NPC_Or_Quest) {
            return;
        }
        final NPCChatType lastMsgType_server = cm.getLastChatType(); // 00 (last msg type I think)

        /*if (cm != null && lastMsg == 0x17) {
            c.getPlayer().handleDemonJob(iPacket.DecodeInt());
            return;
        }*/
        int selection = -1;
        switch (lastMsgType) {
            case OnAskText:
                if (action == 1) {
                    cm.setGetText(iPacket.DecodeString());
                }
                break;
            case OnAskNumber: // Get number
            case OnAskMenu: // Simple
            case OnAskAvater: // Style
            case OnAskSlideMenu: // Mirror Dimension
                if (iPacket.GetRemainder() > 0) {
                    if (iPacket.GetRemainder() >= 4) {
                        selection = iPacket.DecodeInt();
                    } else {
                        selection = iPacket.DecodeByte();
                    }
                    if (selection < cm.min_range || selection > cm.max_range) {
                        AutobanManager.getInstance().autoban(c, "Sending an invalid NPC selection range [possible meso dupe?] : " + selection);
                        return;
                    }
                } else {
                    cm.dispose();
                    return;
                }
                break;
            case OnAskAngelicBuster:
            case OnAskSelectMenu:
                NPCScriptManager.getInstance().action(c, (byte) 1, lastMsgType.getType(), action);
                break;
        }

        if (action != -1) {
            // NOTE: We can only rely on server values for now
            // because IMAGE, NEXT, PREV, NEXTPREV, OK are essentially operation 0
            // due to the way odin coded them... until this is fixed
            if (!cm.LastChatType.check(lastMsgType_server, action)) {
                cm.dispose();
                // System.out.println(lastMsgType.toString() + " " + action);
                return;
            }
            // Reset
            cm.setLastChatType(NPCChatType.NULL);

            switch (cm.getType()) {
                case StartQuest:
                    NPCScriptManager.getInstance().startQuest(c, action, lastMsgType.getType(), selection);
                    break;
                case EndQuest:
                    NPCScriptManager.getInstance().endQuest(c, action, lastMsgType.getType(), selection);
                    break;
                case Npc:
                    NPCScriptManager.getInstance().action(c, action, lastMsgType.getType(), selection);
                    break;
            }
        } else {
            cm.dispose();
        }
    }
}
