package handling.game;

import client.ClientSocket;
import client.Skill;
import client.SkillFactory;
import handling.world.World;
import handling.world.MapleGuild;
import handling.world.MapleGuildResponse;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import server.StatEffect;
import server.maps.objects.User;
import tools.Pair;
import net.InPacket;
import net.OutPacket;

import tools.LogHelper;
import tools.packet.WvsContext;
import tools.packet.WvsContext.GuildPacket;
import net.ProcessPacket;

/**
 *
 * @author
 */
public class GuildOperationHandler implements ProcessPacket<ClientSocket> {

    @Override
    public boolean ValidateState(ClientSocket c) {
        return true;
    }

    private static final Map<String, Pair<Integer, Long>> invited = new HashMap<>();
    private static long nextPruneTime = System.currentTimeMillis() + 5 * 60 * 1000;

    public static class GuildOperations {

        public static final int joinGuild = 0x01;
        public static final int joinGuildDataRequest = 0x02;
        public static final int createGuild = 0x04;
        public static final int playerInvite = 0x07;
        public static final int leaveGuild = 0x0B;
        public static final int expelMember = 0x0C;
        public static final int changeRankTitles = 0x12;
        public static final int changePlayerRank = 0x13;
        public static final int changeGuildMark = 0x14;
        public static final int guildNotice = 0x15;
        public static final int buyGuildSkill = 0x23;
        public static final int activateGuildSkill = 0x24;
        public static final int changeGuildLeader = 0x28;
        public static final int guildSearch = 0x2D;
    }

    public static class GuildSearchTypes {

        public static final int OveralSearch = 1;
        public static final int GuildNameSearch = 2;
        public static final int LeaderNameSearch = 3;
    }

    public static Map<String, Pair<Integer, Long>> getGuildInvitationList() {
        return invited;
    }

    @Override
    public void Process(ClientSocket c, InPacket iPacket) {
        final long currentTime = System.currentTimeMillis();
        if (currentTime >= nextPruneTime) {
            Iterator<Map.Entry<String, Pair<Integer, Long>>> itr = invited.entrySet().iterator();
            Map.Entry<String, Pair<Integer, Long>> inv;
            while (itr.hasNext()) {
                inv = itr.next();
                if (currentTime >= inv.getValue().right) {
                    itr.remove();
                }
            }
            nextPruneTime += 5 * 60 * 1000;
        }
        short GuildOperationCode = iPacket.DecodeByte();
        switch (GuildOperationCode) {
            case GuildOperations.joinGuildDataRequest:// open a guild from search results
                int guildId = iPacket.DecodeInt();
                MapleGuild g = World.Guild.getGuild(guildId);
                if (g == null) {
                    break;
                }
                c.SendPacket(GuildPacket.findGuild_Done(c.getPlayer(), guildId));
                break;
            case GuildOperations.createGuild: // Create guild
                if (c.getPlayer().getGuildId() > 0 || c.getPlayer().getMapId() != 200000301) {
                    c.getPlayer().dropMessage(1, "You cannot create a new Guild while in one.");
                    return;
                } else if (c.getPlayer().getMeso() < 500000) {
                    c.getPlayer().dropMessage(1, "You do not have enough mesos to create a Guild.");
                    return;
                }
                final String guildName = iPacket.DecodeString();

                if (!isGuildNameAcceptable(guildName)) {
                    c.getPlayer().dropMessage(1, "The Guild name you have chosen is not accepted.");
                    return;
                }
                guildId = World.Guild.createGuild(c.getPlayer().getId(), guildName);
                if (guildId == 0) {
                    c.getPlayer().dropMessage(1, "Please try again.");
                    return;
                }
                c.getPlayer().gainMeso(-500000, true, true);
                c.getPlayer().setGuildId(guildId);
                c.getPlayer().setGuildRank((byte) 1);
                c.getPlayer().saveGuildStatus();
                c.getPlayer().finishAchievement(35);
                World.Guild.setGuildMemberOnline(c.getPlayer().getMGC(), true, c.getChannel());
                c.SendPacket(WvsContext.GuildPacket.createNewGuild(c.getPlayer()));
                World.Guild.gainGP(c.getPlayer().getGuildId(), 500, c.getPlayer().getId());
                c.getPlayer().dropMessage(1, "You have successfully created a Guild.");
                break;
            case GuildOperations.playerInvite: // invitation
                //07 FF F9 86 00 0B 00 78 53 74 61 72 4C 69 67 68 74 73 D0 00 00 00 98 0A 00 00 00 00 00 00 ->receive invite
                if (c.getPlayer().getGuildId() <= 0 || c.getPlayer().getGuildRank() > 2) { // 1 == guild master, 2 == jr
                    return;
                }
                String name = iPacket.DecodeString().toLowerCase();
                if (invited.containsKey(name)) {
                    c.getPlayer().dropMessage(5, "The player is currently handling an invitation.");
                    return;
                }
                final MapleGuildResponse mgr = MapleGuild.sendInvite(c, name);

                if (mgr != null) {
                    c.SendPacket(mgr.createPacket());
                } else {
                    Pair<Integer, Long> put = invited.put(name, new Pair<>(c.getPlayer().getGuildId(), currentTime + (1 * 60000))); //20 mins expire
                }
                break;
            case GuildOperations.joinGuild: // accepted guild invitation
                //32 D3 A6 01 00 D3 A6 01 00 09 00 4B 61 7A 49 73 41 4E 75 62 06 00 4C 65 61 64 65 72 07 00 4F 66 66 69 63 65 72 06 00 4D 65 6D 62 65 72 00 00 00 00 01 00 FF F9 86 00 78 53 74 61 72 4C 69 67 68 74 73 00 00 98 0A 00 00 D0 00 00 00 01 00 00 00 01 00 00 00 03 00 00 00 F4 01 00 00 F4 01 00 00 5E 01 00 00 D0 8D BD 63 89 0A D2 01 01 00 C1 9E A6 00 4C 6F 6C 69 6D 61 6C 69 74 61 00 00 00 FE 01 00 00 1F 00 00 00 00 00 00 00 01 00 00 00 03 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 40 E0 FD 3B 37 4F 01 14 00 00 00 00 00 00 00 00 00 00 00 F4 01 00 00 F4 01 00 00 00 00 00 00 01 00 00 96 00 00 00 00 00 00
                //accepted invite
                name = c.getPlayer().getName().toLowerCase();
                if (c.getPlayer().getGuildId() > 0) {
                    invited.remove(name);
                    return;
                }
                guildId = iPacket.DecodeInt();
                Pair<Integer, Long> gid = invited.remove(name);
                if (gid != null && guildId == gid.left) {
                    c.getPlayer().setGuildId(guildId);
                    c.getPlayer().setGuildRank((byte) 5);
                    int s = World.Guild.addGuildMember(c.getPlayer().getMGC());
                    if (s == 0) {
                        c.getPlayer().dropMessage(1, "The Guild you are trying to join is already full.");
                        c.getPlayer().setGuildId(0);
                        return;
                    }
                    c.SendPacket(WvsContext.GuildPacket.loadGuild_Done(c.getPlayer()));
                    final MapleGuild gs = World.Guild.getGuild(guildId);
                    for (OutPacket pack : World.Alliance.getAllianceInfo(gs.getAllianceId(), true)) {
                        if (pack != null) {
                            c.SendPacket(pack);
                        }
                    }
                    c.getPlayer().saveGuildStatus();
                    respawnPlayer(c.getPlayer());
                }
                break;
            case GuildOperations.leaveGuild: // leaving
                int cid = iPacket.DecodeInt();
                name = iPacket.DecodeString();
                if (cid != c.getPlayer().getId() || !name.equals(c.getPlayer().getName()) || c.getPlayer().getGuildId() <= 0) {
                    return;
                }
                World.Guild.leaveGuild(c.getPlayer().getMGC());
                c.SendPacket(GuildPacket.genericGuildMessage((byte) GuildPacket.GuildResult.WithdrawGuild_Done));
                break;
            case GuildOperations.expelMember: // Expel
                cid = iPacket.DecodeInt();
                name = iPacket.DecodeString();

                if (c.getPlayer().getGuildRank() > 2 || c.getPlayer().getGuildId() <= 0) {
                    return;
                }
                World.Guild.expelMember(c.getPlayer().getMGC(), name, cid);
                break;
            case GuildOperations.changeRankTitles: // Guild rank titles change
                if (c.getPlayer().getGuildId() <= 0 || c.getPlayer().getGuildRank() != 1) {
                    return;
                }
                String ranks[] = new String[5];
                for (int i = 0; i < 5; i++) {
                    ranks[i] = iPacket.DecodeString();
                }

                World.Guild.changeRankTitle(c.getPlayer().getGuildId(), ranks);
                break;
            case GuildOperations.changePlayerRank: // Rank change
                cid = iPacket.DecodeInt();
                byte newRank = iPacket.DecodeByte();

                if ((newRank <= 1 || newRank > 5) || c.getPlayer().getGuildRank() > 2 || (newRank <= 2 && c.getPlayer().getGuildRank() != 1) || c.getPlayer().getGuildId() <= 0) {
                    return;
                }

                World.Guild.changeRank(c.getPlayer().getGuildId(), cid, newRank);
                break;
            case GuildOperations.changeGuildMark: // guild emblem change
                if (c.getPlayer().getGuildId() <= 0 || c.getPlayer().getGuildRank() != 1/* || c.getPlayer().getMapId() != 200000301*/) {
                    return;
                }
                if (c.getPlayer().getMeso() < 1500000) {
                    c.getPlayer().dropMessage(1, "You do not have enough mesos to create an emblem.");
                    return;
                }
                final short bg = iPacket.DecodeShort();
                final byte bgcolor = iPacket.DecodeByte();
                final short logo = iPacket.DecodeShort();
                final byte logocolor = iPacket.DecodeByte();
                World.Guild.setGuildEmblem(c.getPlayer().getGuildId(), bg, bgcolor, logo, logocolor);
                c.getPlayer().gainMeso(-1500000, true, true);
                respawnPlayer(c.getPlayer());
                break;
            case GuildOperations.guildNotice: // guild notice change (unsure)
                final String notice = iPacket.DecodeString();
                if (notice.length() > 100 || c.getPlayer().getGuildId() <= 0 || c.getPlayer().getGuildRank() > 2) {
                    return;
                }
                World.Guild.setGuildNotice(c.getPlayer().getGuildId(), notice);
                break;
            case GuildOperations.buyGuildSkill: //guild skill purchase
                Skill skilli = SkillFactory.getSkill(iPacket.DecodeInt());
                int guild = c.getPlayer().getGuildId();
                if (guild <= 0 || skilli == null || skilli.getId() < 91000000) {
                    return;
                }
                int SP = iPacket.DecodeByte();
                int eff = World.Guild.getGuildSkillLevel(guild, skilli.getId()) + SP;
                if (eff > skilli.getMaxLevel()) {
                    return;
                }
                final StatEffect skillid = skilli.getEffect(eff);
                int reqGuildLevel = skillid.getReqGuildLevel();
                if (reqGuildLevel <= 0 || reqGuildLevel > World.Guild.getGuildLevel(guild)) {
                    return;
                }
                if (World.Guild.purchaseGuildSkill(guild, skillid.getSourceId(), c.getPlayer().getName(), c.getPlayer().getId())) {

                }
                break;
            case GuildOperations.activateGuildSkill: //guild skill activation
                skilli = SkillFactory.getSkill(iPacket.DecodeInt());
                if (c.getPlayer().getGuildId() <= 0 || skilli == null) {
                    return;
                }
                eff = World.Guild.getGuildSkillLevel(c.getPlayer().getGuildId(), skilli.getId());
                if (eff <= 0) {
                    return;
                }
                final StatEffect skillii = skilli.getEffect(eff);
                if (skillii.getReqGuildLevel() < 0 || c.getPlayer().getMeso() < skillii.getExtendPrice()) {
                    return;
                }
                if (World.Guild.activateSkill(c.getPlayer().getGuildId(), skillii.getSourceId(), c.getPlayer().getName())) {
                    c.getPlayer().gainMeso(-skillii.getExtendPrice(), true);
                }
                break;
            case GuildOperations.changeGuildLeader: //guild leader change
                cid = iPacket.DecodeInt();
                if (c.getPlayer().getGuildId() <= 0 || c.getPlayer().getGuildRank() > 1) {
                    return;
                }
                World.Guild.setGuildLeader(c.getPlayer().getGuildId(), cid);
                break;
            case GuildOperations.guildSearch:
                if (c == null || c.getPlayer() == null || c.getPlayer().getGuildId() > 0) {
                    break;
                }
                boolean isNummericalSearch = (iPacket.DecodeByte() == 1);
                if (isNummericalSearch) {
                    byte minGuildLevel = iPacket.DecodeByte();
                    byte maxGuildLevel = iPacket.DecodeByte();
                    byte minGuildSize = iPacket.DecodeByte();
                    byte maxGuildSize = iPacket.DecodeByte();
                    byte minAvgMemberLevel = iPacket.DecodeByte();
                    byte maxAvgMemberLevel = iPacket.DecodeByte();
                    Map<Integer, MapleGuild> guildSearchResult = World.Guild.getGuildByNummericalSearch(minGuildLevel, maxGuildLevel, minGuildSize, maxGuildSize, minAvgMemberLevel, maxAvgMemberLevel);
                    c.SendPacket(GuildPacket.guildSearchResult(c, guildSearchResult));
                } else {
                    byte type = iPacket.DecodeByte();
                    boolean exactWord = (iPacket.DecodeShort() != 0);
                    switch (type) {
                        case GuildSearchTypes.OveralSearch:
                            String query = iPacket.DecodeString();
                            Map<Integer, MapleGuild> matchingGuilds = World.Guild.getGuildByNameSearch(c, query, exactWord);
                            Map<Integer, MapleGuild> matchingOwners = World.Guild.getGuildByOwnerSearch(c, query, exactWord);
                            Map<Integer, MapleGuild> matchingOverall = new LinkedHashMap<>();
                            matchingOverall.putAll(matchingGuilds);
                            matchingOverall.putAll(matchingOwners);
                            c.SendPacket(GuildPacket.guildSearchResult(c, matchingOverall));
                            break;
                        case GuildSearchTypes.GuildNameSearch:
                            Map<Integer, MapleGuild> guildNameSearchResult = World.Guild.getGuildByNameSearch(c, iPacket.DecodeString(), exactWord);
                            c.SendPacket(GuildPacket.guildSearchResult(c, guildNameSearchResult));
                            break;
                        case GuildSearchTypes.LeaderNameSearch:
                            Map<Integer, MapleGuild> ownerNameSearchResult = World.Guild.getGuildByOwnerSearch(c, iPacket.DecodeString(), exactWord);
                            c.SendPacket(GuildPacket.guildSearchResult(c, ownerNameSearchResult));
                            break;
                        default:
                            break;
                    }
                }
                break;
            default:
                LogHelper.PACKET_HANDLER.get().error("Handler: GuildOperationHandler. Unknown Guild Operation code: " + GuildOperationCode);
                break;
        }
    }

    private static boolean isGuildNameAcceptable(final String name) {
        if (name.length() < 3 || name.length() > 12) {
            return false;
        }
        for (int i = 0; i < name.length(); i++) {
            if (!Character.isLowerCase(name.charAt(i)) && !Character.isUpperCase(name.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    private static void respawnPlayer(final User mc) {
        if (mc.getMap() == null) {
            return;
        }
        mc.getMap().broadcastMessage(WvsContext.GuildPacket.sendSetGuildNameMsg(mc));
        mc.getMap().broadcastMessage(WvsContext.GuildPacket.sendSetGuildMarkMsg(mc));
    }
}
