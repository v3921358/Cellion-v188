package client.buddy;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import client.Client;
import database.Database;
import server.maps.objects.User;
import tools.LogHelper;
import tools.packet.CWvsContext;

public class BuddyList {

    private final Map<Integer, BuddylistEntry> buddies = new LinkedHashMap<Integer, BuddylistEntry>();
    private byte capacity;
    private boolean changed = false;

    public BuddyList(byte capacity) {
        this.capacity = capacity;
    }

    public boolean contains(int characterId) {
        return buddies.containsKey(characterId);
    }

    public boolean containsVisible(int characterId) {
        BuddylistEntry ble = buddies.get(characterId);
        if (ble == null) {
            return false;
        }
        return ble.isPending();
    }

    public byte getCapacity() {
        return capacity;
    }

    public void setCapacity(byte capacity) {
        this.capacity = capacity;
    }

    public BuddylistEntry get(int characterId) {
        return buddies.get(characterId);
    }

    public BuddylistEntry get(String characterName) {
        String lowerCaseName = characterName.toLowerCase();
        for (BuddylistEntry ble : buddies.values()) {
            if (ble.getName().toLowerCase().equals(lowerCaseName)) {
                return ble;
            }
        }
        return null;
    }

    public void put(BuddylistEntry entry) {
        buddies.put(entry.getCharacterId(), entry);
        changed = true;
    }

    public void remove(int characterId) {
        buddies.remove(characterId);
        changed = true;
    }

    public Collection<BuddylistEntry> getBuddies() {
        return buddies.values();
    }

    public boolean isFull() {
        return buddies.size() >= capacity;
    }

    public List<Integer> getBuddyIds() {
        List<Integer> buddyIds = new ArrayList<>();
        buddies.values().stream().filter(ble -> !ble.isPending())
                .forEach(ble -> buddyIds.add(ble.getCharacterId()));
        return buddyIds;
    }

    public void loadFromTransfer(final Map<CharacterNameAndId, BuddyTransfer> data) {
        CharacterNameAndId buddyid;
        for (Entry<CharacterNameAndId, BuddyTransfer> qs : data.entrySet()) {
            buddyid = qs.getKey();
            put(new BuddylistEntry(buddyid.getName(), buddyid.getId(), "Default Group", -1, qs.getValue().isPending(), qs.getValue().getMemo(), qs.getValue().isAccountFriend(), qs.getValue().getNickname()));
        }
    }

    public void loadFromDb(int characterId, Connection con) {
        try (PreparedStatement ps = con.prepareStatement("SELECT b.buddyid, b.pending, c.name as buddyname, b.groupname, b.memo, b.friend, b.nickname, b.flag FROM buddies as b, characters as c WHERE c.id = b.buddyid AND c.deletedAt is null AND b.characterid = ?")) {
            ps.setInt(1, characterId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                BuddylistEntry ble = new BuddylistEntry(rs.getString("buddyname"), rs.getInt("buddyid"), rs.getString("groupname"), -1, rs.getBoolean("pending"), rs.getString("memo"), rs.getBoolean("friend"), rs.getString("nickname"));
                ble.setFlag(rs.getByte("flag"));
                put(ble);
            }
            rs.close();
            ps.close();
        } catch (Exception e) {
            LogHelper.SQL.get().info("There was an issue with something from the database the database:\n", e);
        }
    }

    public void addBuddyRequest(Client c, int channelFrom, User chr, boolean accountFriend) {
        Buddy buddy = new Buddy(BuddyResult.INVITE);
        BuddylistEntry ble = new BuddylistEntry(chr.getName(), chr.getId(), "Default Group", channelFrom, true, "", accountFriend, chr.getName());
        if (accountFriend) {
            ble.setFlag(BuddyFlags.AccountFriendRequest.getFlag());
        } else {
            ble.setFlag(BuddyFlags.FriendRequest.getFlag());
        }
        buddy.setEntry(ble);
        buddy.setLevel(chr.getLevel());
        buddy.setJob(chr.getJob());
        buddy.setSubJob(chr.getSubcategory());
        put(buddy.getEntry());
        c.SendPacket(CWvsContext.buddylistMessage(buddy));
    }

    public void setChanged(boolean v) {
        this.changed = v;
    }

    public boolean changed() {
        return changed;
    }
}
