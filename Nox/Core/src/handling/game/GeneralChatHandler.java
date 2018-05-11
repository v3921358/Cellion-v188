package handling.game;

import client.Client;
import constants.ServerConstants;
import handling.PacketThrottleLimits;
import net.InPacket;

import server.commands.CommandProcessor;
import server.maps.objects.User;
import tools.LogHelper;
import tools.packet.CField;
import tools.packet.CWvsContext;
import net.ProcessPacket;

/**
 *
 * @author Lloyd Korn
 * @author Mazen Massoud
 */
@PacketThrottleLimits(
        FlagCount = 20,
        ResetTimeMillis = 20000,
        MinTimeMillisBetweenPackets = 100,
        FunctionName = "GeneralChatHandler",
        BanType = PacketThrottleLimits.PacketThrottleBanType.Disconnect)
public class GeneralChatHandler implements ProcessPacket<Client> {

    @Override
    public boolean ValidateState(Client c) {
        return true;
    }

    @Override
    public void Process(Client c, InPacket iPacket) {
        final User chr = c.getPlayer();

        if (chr == null) {
            return;
        }
        chr.updateTick(iPacket.DecodeInt());

        final String text = iPacket.DecodeString();
        final boolean appendToChatLogList = iPacket.DecodeByte() == 0;

        if (text.length() > 0 && chr.getMap() != null && !CommandProcessor.processCommand(c, text, ServerConstants.CommandType.NORMAL)) {
            if (!chr.isGM() && text.length() >= 80) {
                LogHelper.PACKET_EDIT_HACK.get().info(
                        String.format("[GeneralChat] %s [ChrID: %d; AccId %d] has tried to send a general chat of length > 80. [%d]. AppendToChatLog: %s\r\nText:%s",
                                chr.getName(), chr.getId(), c.getAccID(),
                                text.length(), String.valueOf(appendToChatLogList), text));
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
            if (chr.isDeveloper()) {
                chatColour = 12;
                chatPrefix = "DEV ";
            } else if (chr.isGM()) {
                chatColour = 10;
                chatPrefix = "GM ";
            } else {
                if (chr.isIntern()) {
                    chatColour = 9;
                } else if (chr.isDonator()) {
                    chatColour = 4;
                }
            }

            if (chr.getCanTalk() || chr.isGM()) {
                if (chr.isHidden()) { // Broadcast only to Game Masters if character is hidden.
                    chr.getMap().broadcastGMMessage(chr, CField.getGameMessage("(Hidden) " + chr.getName() + " : " + text, (short) 6), true);
                    chr.getMap().broadcastGMMessage(chr, CField.getChatText(chr.getId(), text, false, !appendToChatLogList), true);
                } else {
                    if ((!chr.isIntern()) || (chr.isIntern() && chr.usingStaffChat())) { // If player is staff, only use special text if they have it enabled.
                        if (chatColour > 0 || !chatPrefix.equals("")) {
                            chr.getMap().broadcastMessage(CField.getGameMessage(chatPrefix + chr.getName() + " : " + text, chatColour));
                            chr.getMap().broadcastMessage(CField.getChatText(chr.getId(), text, false, !appendToChatLogList));
                        } else {
                            chr.getMap().broadcastMessage(CField.getChatText(chr.getId(), text, chr.isGM(), appendToChatLogList));
                        }
                    } else {
                        chr.getMap().broadcastMessage(CField.getChatText(chr.getId(), text, (chr.usingStaffChat() && chr.isGM()), appendToChatLogList));
                    }
                }
            } else {
                c.SendPacket(CWvsContext.broadcastMsg(6, "You have been muted and are unable to talk."));
            }
        }
    }
}
