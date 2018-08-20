package enums;

public enum ItemFlag {

    LOCK(0x01),
    SPIKES(0x02), // who's wrong? spikes or karma? lol
    KARMA_USE(0x02),// who's wrong? spikes or karma? lol
    COLD(0x04),
    UNTRADABLE(0x08),
    //
    CRAFTED_USE(0x10), // who's wrong? crafted use or lol
    KARMA_EQ(0x10), // send this to cs items and become tradable?
    CHARM_EQUIPPED(0x20),
    ANDROID_ACTIVATED(0x40),
    CRAFTED(0x80),
    //
    SHIELD_WARD(0x100), //shield icon
    LUCKY_DAY(0x200), //this has some clover leaf thing at bottomleft
    KARMA_ACC_USE(0x400),
    //
    KARMA_ACC(0x1000),
    SLOTS_PROTECT(0x2000), // Upgrade count protection
    SCROLL_PROTECT(0x4000), // scroll protection
    RETURN_SCROLL(0x8000);
    private final int value;

    private ItemFlag(int value) {
        this.value = value;
    }

    public final int getValue() {
        return value;
    }

    public final boolean check(int flag) {
        return (flag & value) == value;
    }
}
