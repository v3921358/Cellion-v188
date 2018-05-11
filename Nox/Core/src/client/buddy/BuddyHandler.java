/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client.buddy;

import client.Client;
import net.InPacket;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import client.Client;
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
import tools.Utility;
import static tools.packet.CWvsContext.OnLoadAccountIDOfCharacterFriendResult;

/**
 *
 * @author Mazen Massoud
 */
public class BuddyHandler {

    public static void handleOperation(Client c, InPacket iPacket) {
        BuddyRequest mode = BuddyRequest.UNKNOWN.getRequest(iPacket.DecodeByte());
        BuddyList buddylist = c.getPlayer().getBuddylist();
        Buddy buddy = new Buddy();
        BuddylistEntry ble;
        switch (mode) {
            case SET_FRIEND: {
                String name = iPacket.DecodeString();
                String group = iPacket.DecodeString();
                String memo = iPacket.DecodeString();
                boolean accountFriend = iPacket.DecodeByte() > 0;
                byte flag = 0;
                String nick = iPacket.DecodeString();
                ble = buddylist.get(name);
                if (name.length() < 4 || name.length() > 13 || group.length() > 16) {
                    return;
                }
                if (ble != null && (ble.getGroup().equals(group) || !ble.isPending())) {
                    buddy.setResult(BuddyResult.SET_FRIEND_DONE);
                    c.SendPacket(CWvsContext.buddylistMessage(buddy));
                } else if (buddylist.isFull()) {
                    buddy.setResult(BuddyResult.SET_FRIEND_FULL_ME);
                    c.SendPacket(CWvsContext.buddylistMessage(buddy));
                } else if (ble == null) {
                    try {
                        CharacterIdNameBuddyCapacity charWithId = null;
                        int channel = Utility.requestChannel(name);
                        User otherChar = null;
                        if (channel > 0) {
                            otherChar = ChannelServer.getInstance(channel).getPlayerStorage().getCharacterByName(name);
                            if (otherChar == null) {
                                charWithId = getCharacterIdAndNameFromDatabase(name);
                            } else {
                                charWithId = new CharacterIdNameBuddyCapacity(otherChar.getId(), otherChar.getName(), otherChar.getBuddylist().getCapacity());
                            }
                        } else {
                            charWithId = getCharacterIdAndNameFromDatabase(name);
                        }

                        if (charWithId != null) {
                            BuddyOperation buddyAddResult = null;
                            if (channel > 0) {
                                buddyAddResult = World.WorldBuddy.requestBuddyAdd(c.getPlayer(), accountFriend, otherChar);
                            } else {
                                try (Connection con = Database.GetConnection()) {

                                    PreparedStatement ps = con.prepareStatement("SELECT COUNT(*) as buddyCount FROM buddies WHERE characterid = ? AND pending = 0");
                                    ps.setInt(1, charWithId.getId());
                                    ResultSet rs = ps.executeQuery();

                                    if (!rs.next()) {
                                        ps.close();
                                        rs.close();
                                        throw new RuntimeException("Result set expected");
                                    } else {
                                        int count = rs.getInt("buddyCount");
                                        if (count >= charWithId.getBuddyCapacity()) {
                                            buddyAddResult = BuddyOperation.BUDDYLIST_FULL;
                                        }
                                    }
                                    rs.close();
                                    ps.close();

                                    ps = con.prepareStatement("SELECT pending FROM buddies WHERE characterid = ? AND buddyid = ?");
                                    ps.setInt(1, charWithId.getId());
                                    ps.setInt(2, c.getPlayer().getId());
                                    rs = ps.executeQuery();
                                    if (rs.next()) {
                                        buddyAddResult = BuddyOperation.ALREADY_ON_LIST;
                                    }
                                    rs.close();
                                    ps.close();
                                }

                            }
                            if (buddyAddResult == BuddyOperation.BUDDYLIST_FULL) {
                                c.SendPacket(CWvsContext.buddylistMessage(new Buddy(BuddyResult.SET_FRIEND_FULL_OTHER)));
                            } else {
                                int displayChannel = -1;
                                int otherCid = charWithId.getId();
                                if (buddyAddResult == BuddyOperation.ALREADY_ON_LIST && channel > 0) {
                                    displayChannel = channel;
                                    notifyRemoteChannel(c, channel, otherCid, BuddyOperation.ADD, accountFriend, "");
                                } else if (buddyAddResult != BuddyOperation.ALREADY_ON_LIST) {
                                    if (nick.equals("")) {
                                        nick = charWithId.getName();
                                    }
                                    try (Connection con = Database.GetConnection()) {
                                        try (PreparedStatement ps = con.prepareStatement("INSERT INTO buddies (`characterid`, `buddyid`, `pending`, `friend`, `nickname`, `memo`, `flag`) VALUES (?, ?, 1, ?, ?, ?, ?)")) {
                                            ps.setInt(1, charWithId.getId());
                                            ps.setInt(2, c.getPlayer().getId());
                                            ps.setBoolean(3, accountFriend);
                                            ps.setString(4, nick);
                                            ps.setString(5, memo);
                                            ps.setInt(6, flag);
                                            ps.executeUpdate();
                                        }
                                    }

                                }
                                ble = new BuddylistEntry(charWithId.getName(), otherCid, group, displayChannel, true, memo, accountFriend, nick);
                                ble.setFlag(flag);
                                buddylist.put(ble);
                                buddy.setResult(BuddyResult.LOAD_FRIENDS);
                                buddy.setEntries(new ArrayList<>(buddylist.getBuddies()));
                                c.SendPacket(CWvsContext.buddylistMessage(buddy));
                            }
                        } else {
                            c.SendPacket(CWvsContext.buddylistMessage(new Buddy(BuddyResult.SET_FRIEND_FULL_OTHER)));
                        }
                    } catch (SQLException e) {
                        LogHelper.SQL.get().info("There was an issue with adding buddies:\r\n", e);
                    }
                } else {
                    ble.setGroup(group);
                    ble.setFlag(flag);
                    buddy.setResult(BuddyResult.LOAD_FRIENDS);
                    buddy.setEntries(new ArrayList<>(buddylist.getBuddies()));
                    c.SendPacket(CWvsContext.buddylistMessage(buddy));
                }
                break;
            }
            case ACCEPT_FRIEND: {
                int otherCid = iPacket.DecodeInt();
                ble = buddylist.get(otherCid);
                if (!buddylist.isFull() && ble != null) {
                    int channel = Utility.requestChannel(otherCid);
                    BuddylistEntry entry = new BuddylistEntry(ble.getName(), otherCid, "Default Group", channel, false, "", false, "");
                    byte flag = 0;
                    if (ble.isOnline()) {
                        flag = BuddyFlags.FriendOnline.getFlag();
                    } else {
                        flag = BuddyFlags.FriendOffline.getFlag();
                    }
                    entry.setFlag(flag);
                    buddylist.put(entry);
                    buddy.setResult(BuddyResult.LOAD_FRIENDS);
                    buddy.setEntries(new ArrayList<>(buddylist.getBuddies()));
                    c.SendPacket(CWvsContext.buddylistMessage(buddy));
                    notifyRemoteChannel(c, channel, otherCid, BuddyOperation.ADD, false, "");
                } else {
                    c.SendPacket(CWvsContext.buddylistMessage(new Buddy(BuddyResult.SET_FRIEND_FULL_ME)));
                }
                c.getPlayer().OnlineBuddyListRequest(); //c.getPlayer().fakeRelog2();
                break;
            }
            case DELETE_FRIEND:
                int otherCid = iPacket.DecodeInt();
                ble = buddylist.get(otherCid);
                if (ble != null) {
                    notifyRemoteChannel(c, Utility.requestChannel(otherCid), otherCid, BuddyOperation.DELETE, false, "");
                }
                buddylist.remove(otherCid);
                buddy.setResult(BuddyResult.DELETE_FRIEND_DONE);
                buddy.setEntry(ble);
                c.SendPacket(CWvsContext.buddylistMessage(buddy));
                break;
            case ACCEPT_ACCOUNT_FRIEND: {
                int index = iPacket.DecodeInt();
                List<BuddylistEntry> entries = getPendingAccountFriends(c.loadCharacters(c.getPlayer().getWorld()));
                if (!entries.isEmpty()) {
                    ble = entries.get(index);
                    if (!buddylist.isFull()) {
                        int channel = Utility.requestChannel(ble.getCharacterId());
                        BuddylistEntry entry = new BuddylistEntry(ble.getName(), ble.getCharacterId(), "Default Group", channel, false, "", true, ble.getName());
                        byte flag = 0;
                        if (ble.isOnline()) {
                            flag = BuddyFlags.AccountFriendOnline.getFlag();
                        } else {
                            flag = BuddyFlags.AccountFriendOffline.getFlag();
                        }
                        entry.setFlag(flag);
                        buddylist.put(entry);
                        buddy.setResult(BuddyResult.LOAD_FRIENDS);
                        buddy.setEntries(new ArrayList<>(buddylist.getBuddies()));
                        c.SendPacket(CWvsContext.buddylistMessage(buddy));
                        notifyRemoteChannel(c, channel, ble.getCharacterId(), BuddyOperation.ADD, true, ble.getNickname());
                    } else {
                        c.SendPacket(CWvsContext.buddylistMessage(new Buddy(BuddyResult.SET_FRIEND_FULL_ME)));
                    }
                    c.getPlayer().OnlineBuddyListRequest(); //c.getPlayer().fakeRelog2();
                }
                break;
            }
            case MODIFY_FRIEND: {

                break;
            }
            case DELETE_ACCOUNT_FRIEND: {

                break;
            }
            default:
                break;
        }
    }

    private static List<BuddylistEntry> getPendingAccountFriends(List<User> chrs) {
        List<BuddylistEntry> bl = new ArrayList<>();
        try (Connection con = Database.GetConnection()) {

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

        return bl;
    }

    private static CharacterIdNameBuddyCapacity getCharacterIdAndNameFromDatabase(String name) {
        CharacterIdNameBuddyCapacity ret = null;
        try (Connection con = Database.GetConnection()) {

            PreparedStatement ps = con.prepareStatement("SELECT * FROM characters WHERE name LIKE ? AND deletedAt is null");
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    ret = new CharacterIdNameBuddyCapacity(rs.getInt("id"), rs.getString("name"), rs.getInt("buddyCapacity"));
                }
            }
            ps.close();
        } catch (Exception e) {
            LogHelper.SQL.get().info(e);
        }

        return ret;
    }

    private static void notifyRemoteChannel(Client c, int remoteChannel, int otherCid, BuddyOperation operation, boolean accountFriend, String nickname) {
        User player = c.getPlayer();
        if (remoteChannel > 0) {
            World.WorldBuddy.buddyChanged(otherCid, player.getId(), player.getName(), c.getChannel(), operation, accountFriend, nickname);
        }
    }
}
