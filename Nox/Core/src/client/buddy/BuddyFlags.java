package client.buddy;

/**
 * @author Steven
 *
 */
public enum BuddyFlags {

    REGULAR_FRIEND(0x0),
    REGULAR_INVITE(0x1),
    OFFLINE(0x2),
    ONLINE(0x3),
    MOBILE_SYMBOL(0x4),
    ACCOUNT_FRIEND_OFFLINE(0x5),
    ACOUNT_FRIEND_INVITE(0x6),
    ACCOUNT_FRIEND(0x7);

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
                flag = ACCOUNT_FRIEND.getFlag();
            } else {
                flag = ACCOUNT_FRIEND_OFFLINE.getFlag();
            }
        } else {
            if (entry.isOnline() && !entry.isPending()) {
                flag = ONLINE.getFlag();
            } else {
                flag = OFFLINE.getFlag();
            }
        }
        return flag;
    }

}
