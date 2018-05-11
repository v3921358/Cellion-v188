package client.buddy;

/**
 * @author Steven
 *
 */
public enum BuddyFlags {

    Friend(0),
    FriendRequest(1),
    FriendOffline(2),
    FriendOnline(3),
    MobileOnline(4),
    MobileOffline(5),
    AccountFriendRequest(6),
    AccountFriendOnline(7),
    AccountFriendOffline(8);

    private int flag;

    private BuddyFlags(int flag) {
        this.flag = flag;
    }

    public byte getFlag() {
        return (byte) flag;
    }

    public byte getFlag(BuddylistEntry entry) {
        byte flag = 0;
        if (entry.isAccountFriend()) {
            if (entry.isOnline() && !entry.isPending()) {
                flag = AccountFriendOnline.getFlag();
            } else {
                flag = MobileOffline.getFlag();
            }
        } else {
            if (entry.isOnline() && !entry.isPending()) {
                flag = FriendOnline.getFlag();
            } else {
                flag = FriendOffline.getFlag();
            }
        }
        return flag;
    }

}
