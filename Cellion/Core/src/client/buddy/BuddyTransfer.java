package client.buddy;

/**
 * @author Steven
 *
 */
public class BuddyTransfer {

    private boolean pending;
    private String nickname;
    private boolean accountFriend;
    private String memo;

    public BuddyTransfer(boolean pending, String nickname, boolean accountFriend, String memo) {
        this.pending = pending;
        this.nickname = nickname;
        this.accountFriend = accountFriend;
        this.memo = memo;
    }

    /**
     * @return pending
     */
    public boolean isPending() {
        return pending;
    }

    /**
     * If the request is pending
     *
     * @param pending
     */
    public void setPending(boolean pending) {
        this.pending = pending;
    }

    /**
     * @return nickname
     */
    public String getNickname() {
        return nickname;
    }

    /**
     * Sets the nickname
     *
     * @param nickname
     */
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    /**
     * @return accountFriend
     */
    public boolean isAccountFriend() {
        return accountFriend;
    }

    /**
     * Whether this account is a account friend
     *
     * @param accountFriend
     */
    public void setAccountFriend(boolean accountFriend) {
        this.accountFriend = accountFriend;
    }

    /**
     * @return memo
     */
    public String getMemo() {
        return memo;
    }

    /**
     * Sets the memo for the buddy
     *
     * @param memo
     */
    public void setMemo(String memo) {
        this.memo = memo;
    }

}
