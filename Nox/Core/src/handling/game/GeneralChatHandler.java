package handling.game;

import client.ClientSocket;
import constants.ServerConstants;
import handling.PacketThrottleLimits;
import net.InPacket;

import server.commands.CommandProcessor;
import server.maps.objects.User;
import tools.LogHelper;
import tools.packet.CField;
import tools.packet.WvsContext;
import net.ProcessPacket;

/**
 * UserChat
 * @author Mazen Massoud
 */
@PacketThrottleLimits(
        FlagCount = 20,
        ResetTimeMillis = 20000,
        MinTimeMillisBetweenPackets = 100,
        FunctionName = "GeneralChatHandler",
        BanType = PacketThrottleLimits.PacketThrottleBanType.Disconnect)
public class GeneralChatHandler implements ProcessPacket<ClientSocket> {

    @Override
    public boolean ValidateState(ClientSocket c) {
        return true;
    }

    @Override
    public void Process(ClientSocket c, InPacket iPacket) {
        final User pPlayer = c.getPlayer();
        if (pPlayer == null) {
            return;
        }
        
        pPlayer.updateTick(iPacket.DecodeInt());
        final String sText = iPacket.DecodeString();
        final int nAppend = iPacket.DecodeByte();

        if (sText.length() > 0 && pPlayer.getMap() != null && !CommandProcessor.processCommand(c, sText, ServerConstants.CommandType.NORMAL)) {
            if (!pPlayer.isGM() && sText.length() >= 80) {
                LogHelper.PACKET_EDIT_HACK.get().info(
                        String.format("[GeneralChat] %s [ChrID: %d; AccId %d] has tried to send a general chat of length > 80. [%d]. AppendToChatLog: %s\r\nText:%s",
                                pPlayer.getName(), pPlayer.getId(), c.getAccID(),
                                sText.length(), String.valueOf(nAppend), sText));
                c.Close();
                return;
            }

            Short chatColour = 0;
            String chatPrefix = "";

            /*
            * Chat Colour Index
            * 0 - White Text on Black Backdrop
            * 1 - Lime Green Text on Black Backdrop
            * 2 - Pink Text on Black Backdrop
            * 3 - Orange Text on Black Backdrop
            * 4 - Purple Text on Black Backdrop
            * 5 - Light Green Text on Black Backdrop
            * 6 - Grey Text on Black Backdrop
            * 7 - Yelllow Text on Black Backdrop
            * 8 - Light Yellow Text on Black Backdrop
            * 9 - Blue Text on Black Backdrop
            * 10 - Black Text on White Backdrop
            * 11 - Light Red Text on Black Backdrop
            * 12 - Blue Text on Light Blue Backdrop
            * 13 - Dark Pink Text on 'Super Megaphone' Backdrop
             */
            if (pPlayer.isDeveloper()) {
                chatColour = 12;
                chatPrefix = "DEV ";
            } else if (pPlayer.isGM()) {
                chatColour = 10;
                chatPrefix = "GM ";
            } else {
                if (pPlayer.isIntern()) {
                    chatColour = 9;
                } else if (pPlayer.isDonator()) {
                    chatColour = 4;
                }
            }

            if (pPlayer.getCanTalk() || pPlayer.isGM()) {
                if (pPlayer.isHidden()) { // Broadcast only to Game Masters if character is hidden.
                    pPlayer.getMap().broadcastGMMessage(pPlayer, CField.getGameMessage("(Hidden) " + pPlayer.getName() + " : " + sText, (short) 6), true);
                    pPlayer.getMap().broadcastGMMessage(pPlayer, CField.getChatText(pPlayer.getId(), sText, false, nAppend), true);
                } else {
                    if ((!pPlayer.isIntern()) || (pPlayer.isIntern() && pPlayer.usingStaffChat())) { // If player is staff, only use special text if they have it enabled.
                        if (chatColour > 0 || !chatPrefix.equals("")) {
                            pPlayer.getMap().broadcastPacket(CField.getGameMessage(chatPrefix + pPlayer.getName() + " : " + sText, chatColour));
                            pPlayer.getMap().broadcastPacket(CField.getChatText(pPlayer.getId(), sText, false, 0));
                        } else {
                            pPlayer.getMap().broadcastPacket(CField.getChatText(pPlayer.getId(), sText, pPlayer.isGM(), nAppend));
                        }
                    } else {
                        pPlayer.getMap().broadcastPacket(CField.getChatText(pPlayer.getId(), sText, (pPlayer.usingStaffChat() && pPlayer.isGM()), nAppend));
                    }
                }
            } else {
                c.SendPacket(WvsContext.broadcastMsg(6, "You have been muted and are unable to talk."));
            }
        }
    }
}
