package handling.game;

import client.MapleCharacterUtil;
import client.MapleClient;
import handling.PacketThrottleLimits;
import handling.world.MapleMessenger;
import handling.world.MapleMessengerCharacter;
import handling.world.World;
import net.InPacket;
import server.maps.objects.User;
import tools.packet.CField;
import tools.packet.CWvsContext;
import net.ProcessPacket;

/**
 *
 * @author Lloyd Korn
 */
@PacketThrottleLimits(
        FlagCount = 20,
        ResetTimeMillis = 20000,
        MinTimeMillisBetweenPackets = 500,
        FunctionName = "MessengerHandler",
        BanType = PacketThrottleLimits.PacketThrottleBanType.Disconnect)
public class MessengerHandler implements ProcessPacket<MapleClient> {

    @Override
    public boolean ValidateState(MapleClient c) {
        return true;
    }

    @Override
    public void Process(MapleClient c, InPacket iPacket) {
        String input;
        MapleMessenger messenger = c.getPlayer().getMessenger();

        switch (iPacket.DecodeByte()) {
            case 0x00: // open
                if (messenger == null) {
                    iPacket.DecodeByte();
                    byte mode = iPacket.DecodeByte();
                    int messengerid = iPacket.DecodeInt();
                    if (messengerid == 0) { // create
                        c.getPlayer().setMessenger(World.Messenger.createMessenger(new MapleMessengerCharacter(c.getPlayer())));
                    } else { // join
                        messenger = World.Messenger.getMessenger(messengerid);
                        if (messenger != null) {
                            final int position = messenger.getLowestPosition();
                            if (position > -1 && position < 7) {
                                c.getPlayer().setMessenger(messenger);
                                World.Messenger.joinMessenger(messenger.getId(), new MapleMessengerCharacter(c.getPlayer()), c.getPlayer().getName(), c.getChannel());
                            }
                        }
                    }
                }
                break;
            case 0x02: // exit
                if (messenger != null) {
                    final MapleMessengerCharacter messengerplayer = new MapleMessengerCharacter(c.getPlayer());
                    World.Messenger.leaveMessenger(messenger.getId(), messengerplayer);
                    c.getPlayer().setMessenger(null);
                }
                break;
            case 0x03: // invite
                if (messenger != null) {
                    final int position = messenger.getLowestPosition();
                    if (position <= -1 || position >= 7) {
                        return;
                    }
                    input = iPacket.DecodeString();
                    final User target = c.getChannelServer().getPlayerStorage().getCharacterByName(input);

                    if (target != null) {
                        if (target.getMessenger() == null) {
                            if (!target.isIntern() || c.getPlayer().isIntern()) {
                                c.SendPacket(CField.messengerNote(input, 4, 1));
                                target.getClient().SendPacket(CField.messengerInvite(c.getPlayer().getName(), messenger.getId()));
                            } else {
                                c.SendPacket(CField.messengerNote(input, 4, 0));
                            }
                        } else {
                            c.SendPacket(CField.messengerChat(c.getPlayer().getName(), " : " + target.getName() + " is already using Maple Messenger."));
                        }
                    } else if (World.isConnected(input)) {
                        World.Messenger.messengerInvite(c.getPlayer().getName(), messenger.getId(), input, c.getChannel(), c.getPlayer().isIntern());
                    } else {
                        c.SendPacket(CField.messengerNote(input, 4, 0));
                    }
                }
                break;
            case 0x05: // decline
                final String targeted = iPacket.DecodeString();
                final User target = c.getChannelServer().getPlayerStorage().getCharacterByName(targeted);
                if (target != null) { // This channel
                    if (target.getMessenger() != null) {
                        target.getClient().SendPacket(CField.messengerNote(c.getPlayer().getName(), 5, 0));
                    }
                } else // Other channel
                {
                    if (!c.getPlayer().isIntern()) {
                        World.Messenger.declineChat(targeted, c.getPlayer().getName());
                    }
                }
                break;
            case 0x06: // message
                if (messenger != null) {
                    final String charname = iPacket.DecodeString();
                    final String text = iPacket.DecodeString();
                    if (!c.getPlayer().isIntern() && text.length() >= 1000) {
                        return;
                    }
                    final String chattext = charname + "" + text;
                    World.Messenger.messengerChat(messenger.getId(), charname, text, c.getPlayer().getName());
                    if (messenger.isMonitored() && chattext.length() > c.getPlayer().getName().length() + 3) { //name : NOT name0 or name1
                        World.Broadcast.broadcastGMMessage(
                                CWvsContext.broadcastMsg(
                                        6, "[GM Message] " + MapleCharacterUtil.makeMapleReadable(c.getPlayer().getName()) + "(Messenger: "
                                        + messenger.getMemberNamesDEBUG() + ") said: " + chattext));
                    }
                }
                break;
            case 0x09: //like
                if (messenger != null) {
                    String charname = iPacket.DecodeString();
                    //todo send like packet here
                }
                break;
            case 0x0A: //guidance
                if (messenger != null) {
                    iPacket.DecodeByte();
                    String charname = iPacket.DecodeString();
                    String targetname = iPacket.DecodeString();
                    //todo send guide packet here
                }
                break;
            case 0x0B: //char info
                if (messenger != null) {
                    String charname = iPacket.DecodeString();
                    User character = c.getChannelServer().getPlayerStorage().getCharacterByName(charname);
                    c.SendPacket(CField.messengerCharInfo(character));
                }
                break;
            case 0x0E: //whisper
                if (messenger != null) {
                    String charname = iPacket.DecodeString();
                    //todo send whisper packet here
                }
                break;
        }
    }
}
