package handling.game;

import client.MapleCharacterUtil;
import client.ClientSocket;
import constants.ServerConstants;
import handling.PacketThrottleLimits;
import handling.world.World;
import net.InPacket;
import server.commands.CommandProcessor;
import server.maps.objects.User;
import tools.LogHelper;
import tools.packet.WvsContext;
import net.ProcessPacket;

/**
 *
 * @author Lloyd Korn
 */
@PacketThrottleLimits(
        FlagCount = 20,
        ResetTimeMillis = 20000,
        MinTimeMillisBetweenPackets = 500,
        FunctionName = "PartyChatHandler",
        BanType = PacketThrottleLimits.PacketThrottleBanType.Disconnect)
public class PartyChatHandler implements ProcessPacket<ClientSocket> {

    @Override
    public boolean ValidateState(ClientSocket c) {
        return true;
    }

    @Override
    public void Process(ClientSocket c, InPacket iPacket) {
        User chr = c.getPlayer();

        chr.updateTick(iPacket.DecodeInt());
        final int type = iPacket.DecodeByte();
        final byte numRecipients = iPacket.DecodeByte();
        if (numRecipients <= 0) {
            return;
        }
        int recipients[] = new int[numRecipients];

        for (byte i = 0; i < numRecipients; i++) {
            recipients[i] = iPacket.DecodeInt();
        }
        final String chattext = iPacket.DecodeString();
        if (chr == null || !chr.getCanTalk()) {
            c.SendPacket(WvsContext.broadcastMsg(6, "You have been muted and are therefore unable to talk."));
            return;
        }

        if (c.isMonitored()) {
            String chattype = "Unknown";
            switch (type) {
                case 0:
                    chattype = "Buddy";
                    break;
                case 1:
                    chattype = "Party";
                    break;
                case 2:
                    chattype = "Guild";
                    break;
                case 3:
                    chattype = "Alliance";
                    break;
                case 4:
                    chattype = "Expedition";
                    break;
                default:
                    LogHelper.PACKET_EDIT_HACK.get().info(
                            String.format("[OtherChat] %s [ChrID: %d; AccId %d] has tried to sent a chat of type %d.\r\nText:%s",
                                    chr.getName(), chr.getId(), c.getAccID(),
                                    type, chattext)
                    );
                    c.Close();
                    break;
            }
            World.Broadcast.broadcastGMMessage(WvsContext.broadcastMsg(6, "[GM Message] " + MapleCharacterUtil.makeMapleReadable(chr.getName())
                            + " said (" + chattype + "): " + chattext));

        }
        if (chattext.length() <= 0 || CommandProcessor.processCommand(c, chattext, ServerConstants.CommandType.NORMAL)) {
            return;
        }
        // chr.getCheatTracker().checkMsg();
        switch (type) {
            case 0:
                World.WorldBuddy.buddyChat(recipients, chr.getId(), chr.getName(), chattext);
                break;
            case 1:
                if (chr.getParty() == null) {
                    break;
                }
                World.Party.partyChat(chr.getParty().getId(), chattext, chr.getName());
                break;
            case 2:
                if (chr.getGuildId() <= 0) {
                    break;
                }
                World.Guild.guildChat(chr.getGuildId(), chr.getName(), chr.getId(), chattext);
                break;
            case 3:
                if (chr.getGuildId() <= 0) {
                    break;
                }
                World.Alliance.allianceChat(chr.getGuildId(), chr.getName(), chr.getId(), chattext);
                break;
            case 4:
                if (chr.getParty().getExpeditionId() <= 0) {
                    break;
                }
                World.Party.expedChat(chr.getParty().getExpeditionId(), chattext, chr.getName());
                break;
        }
    }

}
