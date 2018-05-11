package server.life;

/**
 *
 * @author Lloyd Korn
 */
public enum MonsterHpDisplayType {

    BossHP((byte) 1),
    FriendlyMonsterHP((byte) 2),
    MulungDojoMonsterHP((byte) 3),
    RegularMonsterHP((byte) 4),
    BossHPWithRegularHPBar((byte) 5),;

    private final byte hpdisplaytype;

    private MonsterHpDisplayType(byte hpdisplaytype) {
        this.hpdisplaytype = hpdisplaytype;
    }

    public static MonsterHpDisplayType getFromInt(byte hpdisplaytype) {
        for (MonsterHpDisplayType val : values()) {
            if (val.getHPDisplayType() == hpdisplaytype) {
                return val;
            }
        }
        return RegularMonsterHP;
    }

    public byte getHPDisplayType() {
        return hpdisplaytype;
    }

}
