package handling.game;

import client.ClientSocket;
import handling.world.World;
import handling.world.MapleGuild;
import server.maps.objects.User;
import net.InPacket;
import net.OutPacket;

import tools.packet.WvsContext;
import net.ProcessPacket;
import tools.LogHelper;

/**
 *
 * @author
 */
public class AllianceOperationHandler implements ProcessPacket<ClientSocket> {

    @Override
    public boolean ValidateState(ClientSocket c) {
        return true;
    }

    private static void DenyInvite(ClientSocket c, MapleGuild gs) {
        final int inviteid = World.Guild.getInvitedId(c.getPlayer().getGuildId());
        if (inviteid > 0) {
            final int newAlliance = World.Alliance.getAllianceLeader(inviteid);
            if (newAlliance > 0) {
                final User chr = c.getChannelServer().getPlayerStorage().getCharacterById(newAlliance);
                if (chr != null) {
                    chr.dropMessage(5, gs.getName() + " Guild has rejected the Guild Union invitation.");
                }
                World.Guild.setInvitedId(c.getPlayer().getGuildId(), 0);
            }
        }
        //c.write(CWvsContext.enableActions());
    }

    public static void handleAllianceRequest(InPacket iPacket, ClientSocket c, boolean denied) {
        if (c.getPlayer().getGuildId() <= 0) {
            c.SendPacket(WvsContext.enableActions());
            return;
        }
        final MapleGuild gs = World.Guild.getGuild(c.getPlayer().getGuildId());
        if (gs == null) {
            c.SendPacket(WvsContext.enableActions());
            return;
        }
        byte op = iPacket.DecodeByte();
        if (c.getPlayer().getGuildRank() != 1 && op != 1) { //only updating doesn't need guild leader
            return;
        }
        if (op == 22) {
            denied = true;
        }
        int leaderid = 0;
        if (gs.getAllianceId() > 0) {
            leaderid = World.Alliance.getAllianceLeader(gs.getAllianceId());
        }
        //accept invite, and deny invite don't need allianceid.
        if (op != 4 && !denied) {
            if (gs.getAllianceId() <= 0 || leaderid <= 0) {
                return;
            }
        } else if (leaderid > 0 || gs.getAllianceId() > 0) { //infact, if they have allianceid it's suspicious
            return;
        }
        if (denied) {
            DenyInvite(c, gs);
            return;
        }
        User chr;
        int inviteid;
        switch (op) {
            case 1: //load... must be in world op

                for (OutPacket pack : World.Alliance.getAllianceInfo(gs.getAllianceId(), false)) {
                    if (pack != null) {
                        c.SendPacket(pack);
                    }
                }
                break;
            case 3: //invite
                final int newGuild = World.Guild.getGuildLeader(iPacket.DecodeString());
                if (newGuild > 0 && c.getPlayer().getAllianceRank() == 1 && leaderid == c.getPlayer().getId()) {
                    chr = c.getChannelServer().getPlayerStorage().getCharacterById(newGuild);
                    if (chr != null && chr.getGuildId() > 0 && World.Alliance.canInvite(gs.getAllianceId())) {
                        chr.getClient().SendPacket(WvsContext.AlliancePacket.sendAllianceInvite(World.Alliance.getAlliance(gs.getAllianceId()).getName(), c.getPlayer()));
                        World.Guild.setInvitedId(chr.getGuildId(), gs.getAllianceId());
                    } else {
                        c.getPlayer().dropMessage(1, "Make sure the leader of the guild is online and in your channel.");
                    }
                } else {
                    c.getPlayer().dropMessage(1, "That Guild was not found. Please enter the correct Guild Name. (Not the player name)");
                }
                break;
            case 4: //accept invite... guildid that invited(int, a/b check) -> guildname that was invited? but we dont care about that
                inviteid = World.Guild.getInvitedId(c.getPlayer().getGuildId());
                if (inviteid > 0) {
                    if (!World.Alliance.addGuildToAlliance(inviteid, c.getPlayer().getGuildId())) {
                        c.getPlayer().dropMessage(5, "An error occured when adding guild.");
                    }
                    World.Guild.setInvitedId(c.getPlayer().getGuildId(), 0);
                }
                break;
            case 2: //leave; nothing
            case 6: //expel, guildid(int) -> allianceid(don't care, a/b check)
                final int gid;
                if (op == 6 && iPacket.GetRemainder() >= 4) {
                    gid = iPacket.DecodeInt();
                    if (iPacket.GetRemainder() >= 4 && gs.getAllianceId() != iPacket.DecodeInt()) {
                        break;
                    }
                } else {
                    gid = c.getPlayer().getGuildId();
                }
                if (c.getPlayer().getAllianceRank() <= 2 && (c.getPlayer().getAllianceRank() == 1 || c.getPlayer().getGuildId() == gid)) {
                    if (!World.Alliance.removeGuildFromAlliance(gs.getAllianceId(), gid, c.getPlayer().getGuildId() != gid)) {
                        c.getPlayer().dropMessage(5, "An error occured when removing guild.");
                    }
                }
                break;
            case 7: //change leader
                if (c.getPlayer().getAllianceRank() == 1 && leaderid == c.getPlayer().getId()) {
                    if (!World.Alliance.changeAllianceLeader(gs.getAllianceId(), iPacket.DecodeInt())) {
                        c.getPlayer().dropMessage(5, "An error occured when changing leader.");
                    }
                }
                break;
            case 8: //title update
                if (c.getPlayer().getAllianceRank() == 1 && leaderid == c.getPlayer().getId()) {
                    String[] ranks = new String[5];
                    for (int i = 0; i < 5; i++) {
                        ranks[i] = iPacket.DecodeString();
                    }
                    World.Alliance.updateAllianceRanks(gs.getAllianceId(), ranks);
                }
                break;
            case 9:
                if (c.getPlayer().getAllianceRank() <= 2) {
                    if (!World.Alliance.changeAllianceRank(gs.getAllianceId(), iPacket.DecodeInt(), iPacket.DecodeByte())) {
                        c.getPlayer().dropMessage(5, "An error occured when changing rank.");
                    }
                }
                break;
            case 10: //notice update
                if (c.getPlayer().getAllianceRank() <= 2) {
                    final String notice = iPacket.DecodeString();
                    if (notice.length() > 100) {
                        break;
                    }
                    World.Alliance.updateAllianceNotice(gs.getAllianceId(), notice);
                }
                break;
            default:
                LogHelper.GENERAL_EXCEPTION.get().info("[AllianceOperationHandler] Unknown alliance action:" + op);
                break;
        }
        //c.write(CWvsContext.enableActions());
    }

    @Override
    public void Process(ClientSocket c, InPacket iPacket) {
        handleAllianceRequest(iPacket, c, false);
    }

}
