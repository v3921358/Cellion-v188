package client.buddy;

public enum BuddyRequest {
    UNKNOWN(-1),
    LOAD_FRIEND(0x0),
    SET_FRIEND(0x1),
    ACCEPT_FRIEND(0x2),
    ACCEPT_ACCOUNT_FRIEND(0x3),
    DELETE_FRIEND(0x4),
    DELETE_ACCOUNT_FRIEND(0x5),
    REFUSE_FRIEND(0x6),
    REFUSE_ACCOUNT_FRIEND(0x7),
    NOTIFY_LOGIN(0x8),
    NOTIFY_LOGOUT(0x9),
    CAPACITY(0xA),
    CONVERT_ACCOUNT_FRIEND(0xB),
    MODIFY_FRIEND(0xC),
    MODIFY_FRIEND_GROUP(0xD),
    MODIFY_ACCOUNT_FRIEND_GROUP(0xE),
    SET_OFFLINE(0xF),
    SET_ONLINE(0x10),
    SET_BLACKLIST(0x11),
    DELETE_BLACKLIST(0x12),
    LOAD_FRIEND_POINT_INFO(0x13),
    LOAD_FRIEND_CHATN(0x14),
    INVITE_EVENT_BESTFRIEND(0x15),
    ACCEPT_EVENT_BESTFRIEND(0x16),
    REFUSE_EVENT_BESTFRIEND(0x17);

    private int request;

    private BuddyRequest(int request) {
        this.request = request;
    }

    public int getId() {
        return request;
    }

    public BuddyRequest getRequest(int operation) {
        for (BuddyRequest op : BuddyRequest.values()) {
            if (op.getId() == operation) {
                return op;
            }
        }
        return BuddyRequest.UNKNOWN;
    }
}
