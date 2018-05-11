package server.life;

/**
 *
 * @author Lloyd Korn
 */
public enum MonsterCategory {
    Normal(0),
    WorldTour(6),
    Event(8),
    Unknown(-1),;

    private int val;

    private MonsterCategory(int val) {
        this.val = val;
    }

    public int getVal() {
        return val;
    }

    public static MonsterCategory getFromInt(int val) {
        for (MonsterCategory cat : MonsterCategory.values()) {
            if (cat.getVal() == val) {
                return cat;
            }
        }
        return MonsterCategory.Unknown;
    }
}
