package server.potentials;

/**
 *
 * @author Lloyd Korn
 */
public enum ItemPotentialTierType {
    Rare((byte) 17),
    Rare_Hidden((byte) 1, true),
    Epic((byte) 18),
    Epic_Hidden((byte) 2, true),
    Unique((byte) 19),
    Unique_Hidden((byte) 3, true),
    Legendary((byte) 20),
    Legendary_Hidden((byte) 4, true),
    Hidden((byte) 14, true), // Hidden with no indication of rank

    None((byte) 0);

    private final byte value;
    private final boolean isHiddenType;

    private ItemPotentialTierType(byte value) {
        this.value = value;
        this.isHiddenType = false;
    }

    private ItemPotentialTierType(byte value, boolean isHiddenType) {
        this.value = value;
        this.isHiddenType = isHiddenType;
    }

    public byte getValue() {
        return value;
    }

    public boolean isHiddenType() {
        return isHiddenType;
    }

    public static ItemPotentialTierType getHiddenPotentialTier(ItemPotentialTierType current) {
        if (current.isHiddenType()) {
            return None;
        }
        switch (current) {
            case Rare:
                return Rare_Hidden;
            case Epic:
                return Epic_Hidden;
            case Unique:
                return Unique_Hidden;
            case Legendary:
                return Legendary_Hidden;
        }
        return None;
    }

    public static ItemPotentialTierType getItemPotentialTierTypeFromInt(int val) {
        for (ItemPotentialTierType t : values()) {
            if (t.getValue() == val) {
                return t;
            }
        }
        return None;
    }
}
