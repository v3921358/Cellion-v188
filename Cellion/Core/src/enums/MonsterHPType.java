package enums;

/**
 *
 * @author Lloyd Korn
 */
public enum MonsterHPType {

    BossHP((byte) 1),
    FriendlyMonsterHP((byte) 2),
    MulungDojoMonsterHP((byte) 3),
    RegularMonsterHP((byte) 4),
    BossHPWithRegularHPBar((byte) 5),;

    private final byte hpdisplaytype;

    private MonsterHPType(byte hpdisplaytype) {
        this.hpdisplaytype = hpdisplaytype;
    }

    public static MonsterHPType getFromInt(byte hpdisplaytype) {
        for (MonsterHPType val : values()) {
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
