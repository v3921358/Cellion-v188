package enums;

public enum BuddyResult {
    UNKNOWN(-1),
    LOAD_FRIENDS(23),
    //LOAD_ACCOUNT_ID_FRIEND_DONE(0x19),
    NOTIFY_CHANGE_FRIEND_INFO(24),
    INVITE(25),
    SET_FRIEND_DONE(26),
    SET_FRIEND_FULL_ME(27),
    SET_FRIEND_FULL_OTHER(28),
    SET_FRIEND_ALREADY_SET(29),
    SET_FRIEND_ALREADY_REQUESTED(30),
    SET_FRIEND_READY(31),
    SET_FRIEND_CANT_SELF(32),
    SET_FRIEND_MASTER(33),
    SET_FRIEND_UNKNOWN_USER(34),
    SET_FRIEND_UNKNOWN(35),
    SET_FRIEND_REMAIN_CHARACTER_FRIEND(36),
    SET_MESSENGER_MODE(37),
    SEND_SINGLE_FRIEND_INFO(38),
    ACCEPT_FRIEND_UNKNOWN(39),
    DELETE_FRIEND_DONE(40),
    DELETE_FRIEND_UNKNOWN(41),
    NOTIFY(42),
    NOTIFY_NEW_FRIEND(43),
    CAPACITY(44),
    CAPACITY_UNKNOWN(45),
    SET_FRIEND_DONE_FOR_FARM(46),
    INVITE_FOR_FARM(47),
    ACCEPT_FOR_FARM(48),
    SET_FRIEND_BLOCKED_BEHAVIOR(49),
    NOTICE_DELETED(50),
    INVITE_EVENT_BESTFRIEND(51),
    REFUSE_EVENT_BESTFRIEND(52);

    private int result;

    private BuddyResult(int result) {
        this.result = result;
    }

    public int getId() {
        return result;
    }

    public BuddyResult getOperation(int result) {
        for (BuddyResult op : BuddyResult.values()) {
            if (op.getId() == result) {
                return op;
            }
        }
        return BuddyResult.UNKNOWN;
    }

}
