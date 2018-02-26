package client.buddy;

public final class CharacterIdNameBuddyCapacity extends CharacterNameAndId {

    private final int buddyCapacity;

    public CharacterIdNameBuddyCapacity(int id, String name, int buddyCapacity) {
        super(id, name);
        this.buddyCapacity = buddyCapacity;
    }

    public int getBuddyCapacity() {
        return buddyCapacity;
    }
}
