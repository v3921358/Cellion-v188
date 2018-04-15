package handling.game;

import client.MapleClient;
import handling.world.World;
import handling.world.MapleGuild;
import net.InPacket;
import server.maps.objects.User;
import tools.packet.CWvsContext.GuildPacket;
import net.ProcessPacket;

public final class GuildJoinRequestHandler implements ProcessPacket<MapleClient> {

    @Override
    public boolean ValidateState(MapleClient c) {
        return true;
    }

    @Override
    public void Process(MapleClient c, InPacket iPacket) {
        if (c.getPlayer() == null) {
            return;
        }
        int guildId = iPacket.DecodeInt();
        User chr = c.getPlayer();
        MapleGuild guild = World.Guild.getGuild(guildId);

        if (guild != null) {
            if (guild.getPendingMembers().contains(chr)) {
                guild.removePendingGuildMember(chr);
                c.SendPacket(GuildPacket.updateJoinRequestClientInfo(""));
                chr.getClient().SendPacket(GuildPacket.joinGuildRequest(guild, chr, GuildPacket.GuildResult.JoinGuild_Unknown));
                chr.getClient().SendPacket(GuildPacket.findGuild_Done(chr, guildId));
                chr.setPendingGuildId(0);
            } else {
                try {
                    guild.addPendingGuildMember(chr);
                    chr.setPendingGuildId(guildId);
                    //c.write(GuildPacket.updateJoinRequestClientInfo(""));
                    c.SendPacket(GuildPacket.updateJoinRequestClientInfo(guild.getName()));
                } catch (Exception Ex) {
                    chr.setPendingGuildId(0);
                    c.SendPacket(GuildPacket.updateJoinRequestClientInfo(""));
                    chr.getClient().SendPacket(GuildPacket.joinGuildRequest(guild, chr, GuildPacket.GuildResult.JoinRequest_AlreadyFull));
                }
            }
        } else {
            chr.setPendingGuildId(0);
            c.SendPacket(GuildPacket.updateJoinRequestClientInfo(""));
            chr.getClient().SendPacket(GuildPacket.joinGuildRequest(guild, chr, GuildPacket.GuildResult.JoinGuild_Unknown));
        }
    }
}
