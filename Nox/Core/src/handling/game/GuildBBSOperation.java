package handling.game;

import client.ClientSocket;
import handling.world.World;
import handling.world.MapleBBSThread;
import java.util.List;
import net.InPacket;
import tools.packet.WvsContext;
import net.ProcessPacket;

/**
 *
 * @author
 */
public class GuildBBSOperation implements ProcessPacket<ClientSocket> {

    @Override
    public boolean ValidateState(ClientSocket c) {
        return true;
    }

    @Override
    public void Process(ClientSocket c, InPacket iPacket) {
        if (c.getPlayer().getGuildId() <= 0) {
            return; // expelled while viewing bbs or hax
        }
        int localthreadid = 0;
        final byte action = iPacket.DecodeByte();
        switch (action) {
            case 0: // start a new post
                if (!c.getPlayer().getCheatTracker().canBBS()) {
                    c.getPlayer().dropMessage(1, "You may only start a new thread every 60 seconds.");
                    return;
                }
                final boolean bEdit = iPacket.DecodeByte() > 0;
                if (bEdit) {
                    localthreadid = iPacket.DecodeInt();
                }
                final boolean bNotice = iPacket.DecodeByte() > 0;
                final String title = correctLength(iPacket.DecodeString(), 25);
                String text = correctLength(iPacket.DecodeString(), 600);
                final int icon = iPacket.DecodeInt();
                if (icon >= 0x64 && icon <= 0x6a) {
                    if (!c.getPlayer().haveItem(5290000 + icon - 0x64, 1, false, true)) {
                        return; // hax, using an nx icon that s/he doesn't have
                    }
                } else if (icon < 0 || icon > 2) {
                    return; // hax, using an invalid icon
                }
                if (!bEdit) {
                    newBBSThread(c, title, text, icon, bNotice);
                } else {
                    editBBSThread(c, title, text, icon, localthreadid);
                }
                break;
            case 1: // delete a thread
                localthreadid = iPacket.DecodeInt();
                deleteBBSThread(c, localthreadid);
                break;
            case 2: // list threads
                int start = iPacket.DecodeInt();
                listBBSThreads(c, start * 10);
                break;
            case 3: // list thread + reply, followed by id (int)
                localthreadid = iPacket.DecodeInt();
                displayThread(c, localthreadid);
                break;
            case 4: // reply
                if (!c.getPlayer().getCheatTracker().canBBS()) {
                    c.getPlayer().dropMessage(1, "You may only start a new reply every 60 seconds.");
                    return;
                }
                localthreadid = iPacket.DecodeInt();
                text = correctLength(iPacket.DecodeString(), 25);
                newBBSReply(c, localthreadid, text);
                break;
            case 5: // delete reply
                localthreadid = iPacket.DecodeInt();
                int replyid = iPacket.DecodeInt();
                deleteBBSReply(c, localthreadid, replyid);
                break;
        }
    }

    private static void newBBSReply(final ClientSocket c, final int localthreadid, final String text) {
        if (c.getPlayer().getGuildId() <= 0) {
            return;
        }
        World.Guild.addBBSReply(c.getPlayer().getGuildId(), localthreadid, text, c.getPlayer().getId());
        displayThread(c, localthreadid);
    }

    private static void editBBSThread(final ClientSocket c, final String title, final String text, final int icon, final int localthreadid) {
        if (c.getPlayer().getGuildId() <= 0) {
            return; // expelled while viewing?
        }
        World.Guild.editBBSThread(c.getPlayer().getGuildId(), localthreadid, title, text, icon, c.getPlayer().getId(), c.getPlayer().getGuildRank());
        displayThread(c, localthreadid);
    }

    private static void newBBSThread(final ClientSocket c, final String title, final String text, final int icon, final boolean bNotice) {
        if (c.getPlayer().getGuildId() <= 0) {
            return; // expelled while viewing?
        }
        displayThread(c, World.Guild.addBBSThread(c.getPlayer().getGuildId(), title, text, icon, bNotice, c.getPlayer().getId()));
        listBBSThreads(c, 0);
    }

    private static void deleteBBSThread(final ClientSocket c, final int localthreadid) {
        if (c.getPlayer().getGuildId() <= 0) {
            return;
        }
        World.Guild.deleteBBSThread(c.getPlayer().getGuildId(), localthreadid, c.getPlayer().getId(), (int) c.getPlayer().getGuildRank());
    }

    private static void deleteBBSReply(final ClientSocket c, final int localthreadid, final int replyid) {
        if (c.getPlayer().getGuildId() <= 0) {
            return;
        }

        World.Guild.deleteBBSReply(c.getPlayer().getGuildId(), localthreadid, replyid, c.getPlayer().getId(), (int) c.getPlayer().getGuildRank());
        displayThread(c, localthreadid);
    }

    private static String correctLength(final String in, final int maxSize) {
        if (in.length() > maxSize) {
            return in.substring(0, maxSize);
        }
        return in;
    }

    private static void listBBSThreads(ClientSocket c, int start) {
        if (c.getPlayer().getGuildId() <= 0) {
            return;
        }
        c.SendPacket(WvsContext.GuildPacket.BBSThreadList(World.Guild.getBBS(c.getPlayer().getGuildId()), start));
    }

    private static void displayThread(final ClientSocket c, final int localthreadid) {
        if (c.getPlayer().getGuildId() <= 0) {
            return;
        }
        final List<MapleBBSThread> bbsList = World.Guild.getBBS(c.getPlayer().getGuildId());
        if (bbsList != null) {
            for (MapleBBSThread t : bbsList) {
                if (t != null && t.localthreadID == localthreadid) {
                    c.SendPacket(WvsContext.GuildPacket.showThread(t));
                }
            }
        }
    }
}
