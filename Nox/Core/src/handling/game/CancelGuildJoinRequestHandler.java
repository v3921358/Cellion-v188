package handling.game;

import client.MapleClient;
import handling.world.World;
import handling.world.MapleGuild;
import net.InPacket;
import server.maps.objects.User;
import tools.packet.CWvsContext.GuildPacket;
import net.ProcessPacket;

public final class CancelGuildJoinRequestHandler implements ProcessPacket<MapleClient> {

    @Override
    public boolean ValidateState(MapleClient c) {
        return true;
    }

    @Override
    public void Process(MapleClient c, InPacket iPacket) {
        if (c.getPlayer() == null) {
            return;
        }
        User chr = c.getPlayer();
        int currpending = chr.getPendingGuildId();
        MapleGuild guild = World.Guild.getGuild(currpending);
        if (guild != null) {
            if (guild.getPendingMembers().contains(chr)) {
                guild.removePendingGuildMember(chr);
                c.SendPacket(GuildPacket.updateJoinRequestClientInfo(""));
                chr.getClient().SendPacket(GuildPacket.findGuild_Done(chr, chr.getPendingGuildId()));
                c.SendPacket(GuildPacket.joinGuildRequest(guild, chr, GuildPacket.GuildResult.JoinCancelRequest_Done));
                chr.setPendingGuildId(0);
            }
        } else {
            chr.setPendingGuildId(0);
            c.SendPacket(GuildPacket.updateJoinRequestClientInfo(""));
            chr.getClient().SendPacket(GuildPacket.joinGuildRequest(guild, chr, GuildPacket.GuildResult.JoinGuild_Unknown));
        }
    }
}
