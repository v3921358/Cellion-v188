package client.buddy;

public class BuddylistEntry {

    private String name, group;
    private int cid, channel;
    private boolean pending;
    private String memo;
    private boolean accountFriend;
    private String nickname;
    private int accountId;
    private byte flag;

    /**
     * @param name
     * @param characterId
     * @param group
     * @param channel should be -1 if the buddy is offline
     * @param pending
     */
    public BuddylistEntry(String name, int characterId, String group, int channel, boolean pending, String memo, boolean accountFriend, String nickname) {
        super();
        this.name = name;
        this.cid = characterId;
        this.group = group;
        this.channel = channel;
        this.pending = pending;
        this.memo = memo;
        this.accountFriend = accountFriend;
        this.nickname = nickname;
    }

    /**
     * @return the channel the character is on. If the character is offline returns -1.
     */
    public int getChannel() {
        return channel;
    }

    /**
     * Sets the channel
     *
     * @param channel
     */
    public void setChannel(int channel) {
        this.channel = channel;
    }

    /**
     * If the user is online
     *
     * @return boolean
     */
    public boolean isOnline() {
        return channel >= 0;
    }

    /**
     * Sets the user offline
     */
    public void setOffline() {
        channel = -1;
    }

    /**
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * @return id
     */
    public int getCharacterId() {
        return cid;
    }

    /**
     * Sets the status of the request
     *
     * @param boolean
     */
    public void setPending(boolean prending) {
        this.pending = prending;
    }

    /**
     * If the user has a pending request
     *
     * @return boolean
     */
    public boolean isPending() {
        return pending;
    }

    /**
     * @return group
     */
    public String getGroup() {
        return group;
    }

    /**
     * Sets the group
     *
     * @param String
     */
    public void setGroup(String group) {
        this.group = group;
    }

    /**
     * @return memo
     */
    public String getMemo() {
        return memo;
    }

    /**
     * Sets the memo
     *
     * @param String
     */
    public void setMemo(String memo) {
        this.memo = memo;
    }

    /**
     * @returns accountFriend
     */
    public boolean isAccountFriend() {
        return accountFriend;
    }

    /**
     * Sets the boolean if the specific buddy is an account friend
     *
     * @param accountFriend
     */
    public void setAccountFriend(boolean accountFriend) {
        this.accountFriend = accountFriend;
    }

    /**
     * @return account nickname
     */
    public String getNickname() {
        return nickname;
    }

    /**
     * Sets the account nickname
     *
     * @param nickname
     */
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    /**
     * @return the accountId
     */
    public int getAccountId() {
        return accountId;
    }

    /**
     * @param accountId the accountId to set
     */
    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + cid;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final BuddylistEntry other = (BuddylistEntry) obj;
        return cid == other.cid;
    }

    /**
     * @return the flag
     */
    public byte getFlag() {
        return flag;
    }

    /**
     * This method sets the flag for the entry
     *
     * @param flag
     */
    public void setFlag(byte flag) {
        this.flag = flag;
    }

}
