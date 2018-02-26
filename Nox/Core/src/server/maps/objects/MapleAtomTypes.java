package server.maps.objects;

public enum MapleAtomTypes {
    FORCE(0),
    JUDGEMENT(1),
    SOUL_SEEKER(3),
    EZIAS_SYSTEM(5),
    ROCKET(6),
    TRIFILE_WIND(7),
    THIEF_MARK(11),
    MESO_EXPLOSION(12);

    private int type;

    private MapleAtomTypes(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

}
