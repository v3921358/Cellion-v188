package server.life;

/**
 *
 * @author Lloyd Korn
 */
public enum MapleMonsterCategory {
    Normal(0),
    WorldTour(6),
    Event(8),
    Unknown(-1),;

    private int val;

    private MapleMonsterCategory(int val) {
        this.val = val;
    }

    public int getVal() {
        return val;
    }

    public static MapleMonsterCategory getFromInt(int val) {
        for (MapleMonsterCategory cat : MapleMonsterCategory.values()) {
            if (cat.getVal() == val) {
                return cat;
            }
        }
        return MapleMonsterCategory.Unknown;
    }
}
