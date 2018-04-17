package handling.game;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import client.MapleClient;
import client.buddy.Buddy;
import client.buddy.BuddyFlags;
import client.buddy.BuddyHandler;
import client.buddy.BuddyList;
import client.buddy.BuddyOperation;
import client.buddy.BuddyRequest;
import client.buddy.BuddyResult;
import client.buddy.BuddylistEntry;
import client.buddy.CharacterIdNameBuddyCapacity;
import database.Database;
import handling.world.World;
import service.ChannelServer;
import net.InPacket;
import server.maps.objects.User;
import tools.LogHelper;
import tools.packet.CWvsContext;
import net.ProcessPacket;

/**
 *
 * @author
 */
public class BuddylistModifyHandler implements ProcessPacket<MapleClient> {

    @Override
    public boolean ValidateState(MapleClient c) {
        return true;
    }

    @Override
    public void Process(MapleClient c, InPacket iPacket) {
        BuddyHandler.handleOperation(c, iPacket);
    }

    private static List<BuddylistEntry> getPendingAccountFriends(List<User> chrs) {
        List<BuddylistEntry> bl = new ArrayList<>();
        try (Connection con = Database.GetConnection()) {
            System.out.println("[" + Thread.currentThread().getStackTrace()[2].getClassName() + "." + Thread.currentThread().getStackTrace()[2].getMethodName() + "] " + Database.GetPoolStats() + " Opening");

            for (User chr : chrs) {
                PreparedStatement ps = con.prepareStatement("SELECT c.name as buddyname, b.characterid, b.buddyid, b.pending, b.groupname, b.memo, b.friend, b.nickname, b.flag FROM buddies as b, characters as c WHERE b.buddyid = c.id AND b.characterid = ? AND b.pending = 1 AND c.deletedAt is null AND b.friend = 1");
                ps.setInt(1, chr.getId());
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    BuddylistEntry ble = new BuddylistEntry(rs.getString("buddyname"), rs.getInt("buddyid"), rs.getString("groupname"), -1, rs.getInt("pending") != 1, rs.getString("memo"), rs.getBoolean("friend"), rs.getString("nickname"));
                    ble.setFlag(rs.getByte("flag"));
                    bl.add(ble);
                }
                rs.close();
                ps.close();
            }
        } catch (Exception e) {
            LogHelper.SQL.get().info("There was an issue with something from the database the database:\n", e);
        }
        System.out.println("[" + Thread.currentThread().getStackTrace()[2].getClassName() + "." + Thread.currentThread().getStackTrace()[2].getMethodName() + "] " + Database.GetPoolStats() + " Closing");

        return bl;
    }

    private static CharacterIdNameBuddyCapacity getCharacterIdAndNameFromDatabase(String name) {

        CharacterIdNameBuddyCapacity ret = null;
        try (Connection con = Database.GetConnection()) {
            System.out.println("[" + Thread.currentThread().getStackTrace()[2].getClassName() + "." + Thread.currentThread().getStackTrace()[2].getMethodName() + "] " + Database.GetPoolStats() + " Opening");

            PreparedStatement ps = con.prepareStatement("SELECT * FROM characters WHERE name LIKE ? AND deletedAt is null");
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    ret = new CharacterIdNameBuddyCapacity(rs.getInt("id"), rs.getString("name"), rs.getInt("buddyCapacity"));
                }
            }
        } catch (Exception e) {
            LogHelper.SQL.get().info(e);
        }
        System.out.println("[" + Thread.currentThread().getStackTrace()[2].getClassName() + "." + Thread.currentThread().getStackTrace()[2].getMethodName() + "] " + Database.GetPoolStats() + " Closing");

        return ret;
    }

    private static void notifyRemoteChannel(MapleClient c, int remoteChannel, int otherCid, BuddyOperation operation, boolean accountFriend, String nickname) {
        User player = c.getPlayer();
        if (remoteChannel > 0) {
            World.WorldBuddy.buddyChanged(otherCid, player.getId(), player.getName(), c.getChannel(), operation, accountFriend, nickname);
        }
    }
}
