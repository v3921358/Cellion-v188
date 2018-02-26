package server.life;

/**
 *
 * @author Lloyd Korn
 */
public enum MapleMonsterHpDisplayType {

    BossHP((byte) 1),
    FriendlyMonsterHP((byte) 2),
    MulungDojoMonsterHP((byte) 3),
    RegularMonsterHP((byte) 4),
    BossHPWithRegularHPBar((byte) 5),;

    private final byte hpdisplaytype;

    private MapleMonsterHpDisplayType(byte hpdisplaytype) {
        this.hpdisplaytype = hpdisplaytype;
    }

    public static MapleMonsterHpDisplayType getFromInt(byte hpdisplaytype) {
        for (MapleMonsterHpDisplayType val : values()) {
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
