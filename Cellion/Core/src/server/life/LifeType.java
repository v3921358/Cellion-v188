package server.life;

/**
 * An object to represent the "life" type present on Map.wz data
 *
 * @author Lloyd Korn
 */
public enum LifeType {
    Monster("m"),
    Npcs("n"),
    Unknown(""),;
    private final String data;

    private LifeType(String data) {
        this.data = data;
    }

    public String getData() {
        return data;
    }

    public static LifeType getLifeByData(String data) {
        for (LifeType type : LifeType.values()) {
            if (type.getData().contains(data)) {
                return type;
            }
        }
        return LifeType.Unknown;
    }
}
