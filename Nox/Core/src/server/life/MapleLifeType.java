package server.life;

/**
 * An object to represent the "life" type present on Map.wz data
 *
 * @author Lloyd Korn
 */
public enum MapleLifeType {
    Monster("m"),
    Npcs("n"),
    Unknown(""),;
    private final String data;

    private MapleLifeType(String data) {
        this.data = data;
    }

    public String getData() {
        return data;
    }

    public static MapleLifeType getLifeByData(String data) {
        for (MapleLifeType type : MapleLifeType.values()) {
            if (type.getData().contains(data)) {
                return type;
            }
        }
        return MapleLifeType.Unknown;
    }
}
