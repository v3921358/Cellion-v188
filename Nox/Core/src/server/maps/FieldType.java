package server.maps;

/**
 *
 * @author Lloyd Korn
 */
public enum FieldType {
    Snowball("1"),
    MapleCoconut("4"),
    Regular("0"),;
    private String val;

    private FieldType(String typeVal) {
        this.val = typeVal;
    }

    public String getValue() {
        return val;
    }

    public static final FieldType getFromVal(String data) {
        for (FieldType type : FieldType.values()) {
            if (type.val.equals(data)) {
                return type;
            }
        }
        return FieldType.Regular;
    }
}
