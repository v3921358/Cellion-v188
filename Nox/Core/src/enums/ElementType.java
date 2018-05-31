package enums;

public enum ElementType {

    NEUTRAL(0),
    PHYSICAL(1),
    FIRE(2, true),
    ICE(3, true),
    LIGHTING(4),
    POISON(5),
    HOLY(6, true),
    DARKNESS(7);

    private final int value;
    private boolean special = false;

    private ElementType(int v) {
        this.value = v;
    }

    private ElementType(int v, boolean special) {
        this.value = v;
        this.special = special;
    }

    public boolean isSpecial() {
        return special;
    }

    public static ElementType getFromChar(char c) {
        switch (Character.toUpperCase(c)) {
            case 'F':
                return FIRE;
            case 'I':
                return ICE;
            case 'L':
                return LIGHTING;
            case 'S':
                return POISON;
            case 'H':
                return HOLY;
            case 'P':
                return PHYSICAL;
            case 'D':
                return DARKNESS;
        }
        throw new IllegalArgumentException("unknown elemnt char " + c);
    }

    public static ElementType getFromId(int c) {
        for (ElementType e : ElementType.values()) {
            if (e.value == c) {
                return e;
            }
        }
        throw new IllegalArgumentException("unknown elemnt id " + c);
    }

    public int getValue() {
        return value;
    }
}
