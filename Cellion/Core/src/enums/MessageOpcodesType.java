package enums;

/**
 *
 * @author Lloyd Korn
 */
public enum MessageOpcodesType {

    DropPickup(0),
    QuestStatus(1),
    ItemGain(2), // Added on v170~175, shows an info on the bottom right that "you have gained an item ''" >>> 59 00 00 02 3C E0 1E 00
    ExpiredCashItem(3),
    ExpIncrease(4), // updated to 4
    IncreaseSP(5),
    Fame(6),
    Meso(7),
    GuildPoint(8),
    Commitment(9), // Contribution
    GiveBuff(10), // Writes a gray text to the user's chat [Item description]
    GeneralItemExpiration(11),
    System(12),
    QuestInfoEx(14),
    ItemProtectExpire(16),
    ReplaceExpiredItem(17),
    ExpiredItem(18),
    ExpiredSkill(19),
    Trait(20),
    LimitNonCombatExpIncrease(22), // "You cant gain any more Charm EXP today"
    AndroidNotPowered(24), // "The AndroidNotPowered is not powered. Please insert a Mechanical Heart"
    RecoveredFatigue(25), // "You recovered some fatigue by resting"
    PvpPoint(26), // "You received Battle EXP (+xx)", "You received Battle Points (+xx)"
    PvpItem(27),
    WeddingPortal(28),
    HardcoreExp(29),
    AutoLineChanged(30),
    RecordEntryMessage(31),
    EvolvingSystem(32),
    PersonalEvolvingSystemMessage(33),
    CoreInventory(34),
    NxRecordMessage(35), // <Header word> <byte>
    BlockedBehavior(36), // "Zero cannot get mesos in Maple World until Chapter 1 of the Main Quest is complete."
    IncreaseWP(37),
    StylishKill(39),
    SpecialScrollWarningPopup(40), // "Potential Scrolls, Shielding Wards, and other special scrolls have no effect with Star Force Enhancement. Do you still want to enhance it?"
    ExpiredItemPopup(41), //"This is an expired item"
    ;
    private final int type;

    private MessageOpcodesType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }
}
