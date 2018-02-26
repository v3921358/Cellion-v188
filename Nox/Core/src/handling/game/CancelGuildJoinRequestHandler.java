package handling.game;

import client.MapleClient;
import handling.world.World;
import handling.world.MapleGuild;
import net.InPacket;
import server.maps.objects.MapleCharacter;
import tools.packet.CWvsContext.GuildPacket;
import netty.ProcessPacket;

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
        MapleCharacter chr = c.getPlayer();
        int currpending = chr.getPendingGuildId();
        MapleGuild guild = World.Guild.getGuild(currpending);
        if (guild != null) {
            if (guild.getPendingMembers().contains(chr)) {
                guild.removePendingGuildMember(chr);
                c.write(GuildPacket.updateJoinRequestClientInfo(""));
                chr.getClient().write(GuildPacket.findGuild_Done(chr, chr.getPendingGuildId()));
                c.write(GuildPacket.joinGuildRequest(guild, chr, GuildPacket.GuildResult.JoinCancelRequest_Done));
                chr.setPendingGuildId(0);
            }
        } else {
            chr.setPendingGuildId(0);
            c.write(GuildPacket.updateJoinRequestClientInfo(""));
            chr.getClient().write(GuildPacket.joinGuildRequest(guild, chr, GuildPacket.GuildResult.JoinGuild_Unknown));
        }
    }
}
