package enums;

public enum QuestActionType {

    UNDEFINED(-1), 
    exp(0), 
    item(1), 
    nextQuest(2), 
    money(3), 
    quest(4), 
    skill(5), 
    pop(6), 
    buffItemID(7), 
    infoNumber(8), 
    sp(9),
    charismaEXP(10), 
    charmEXP(11), 
    willEXP(12), 
    insightEXP(13), 
    senseEXP(14), 
    craftEXP(15);
    
    final byte type;

    private QuestActionType(int type) {
        this.type = (byte) type;
    }

    public byte getType() {
        return type;
    }

    public static QuestActionType getByType(byte type) {
        for (QuestActionType l : QuestActionType.values()) {
            if (l.getType() == type) {
                return l;
            }
        }
        return null;
    }

    public static QuestActionType getByWZName(String name) {
        try {
            return valueOf(name);
        } catch (IllegalArgumentException ex) {
            return UNDEFINED;
        }
    }
}
