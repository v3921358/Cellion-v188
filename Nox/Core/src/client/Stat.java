package client;

/**
 * Character Stats
 * @author Mazen Massoud
 */
public enum Stat {

    Skin(0x1),                  // Byte
    Face(0x2),                  // Integer
    Hair(0x4),                  // Integer
    Level(0x10),                // Byte
    Job(0x20),                  // Short
    STR(0x40),                  // Short
    DEX(0x80),                  // Short
    INT(0x100),                 // Short
    LUK(0x200),                 // Short
    HP(0x400),                  // Integer
    MaxHP(0x800),               // Integer
    MP(0x1000),                 // Integer
    MaxMP(0x2000),              // Integer
    AP(0x4000),                 // Short
    SP(0x8000),                 // Short (Depending on Case)
    EXP(0x10000),               // Long
    Fame(0x20000),              // Integer
    Meso(0x40000),              // Long
    Fatigue(0x80000),           // Byte
    CharismaEXP(0x100000),      // Integer
    InsightEXP(0x200000),       // Integer
    WillEXP(0x400000),          // Integer
    CraftEXP(0x800000),         // Integer
    SenseEXP(0x1000000),        // Integer
    CharmEXP(0x2000000),        // Integer
    DayLimit(0x4000000),        // 6 Shorts, Byte, Long
    AlbaActivity(0x8000000),    // Unknown
    CharacterCard(0x10000000),  // Character Card Data
    PvpEXP(0x20000000),         // Integer, Byte, Integer
    PvpRank(0x40000000),        // 2 Bytes
    PvpPoints(0x80000000),      // Integer
    IceGuage(0x200000000L);     // Unknown
    
    private final long nIndex;

    private Stat(long i) {
        this.nIndex = i;
    }

    public int getValue() {
        return (int) nIndex;
    }

    public static Stat getByValue(final long nValue) {
        for (final Stat stat : Stat.values()) {
            if (stat.nIndex == nValue) {
                return stat;
            }
        }
        return null;
    }

    public static enum Temp {

        STR(0x1),
        DEX(0x2),
        INT(0x4),
        LUK(0x8),
        WATK(0x10),
        PDD(0x20),
        MATK(0x40),
        MDD(0x80),
        ACC(0x100),
        EVA(0x200),
        SPEED(0x400),       // Byte
        JUMP(0x800),        // Byte
        UNKNOWN(0x1000);    // Byte
        
        private final int nIndex;

        private Temp(int i) {
            this.nIndex = i;
        }

        public int getValue() {
            return nIndex;
        }
    }
}
