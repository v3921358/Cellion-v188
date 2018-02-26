package server.messages;

/**
 *
 * @author Lloyd Korn
 */
public enum _MessageOpcodesType {

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
    GiveBuff(0xA), // Writes a gray text to the user's chat [Item description]
    GeneralItemExpiration(0xB),
    System(0xC),
    QuestInfoEx(0xD),
    ItemProtectExpire(0xF),
    ReplaceExpiredItem(0x10),
    ExpiredItem(0x11),
    ExpiredSkill(0x12),
    Trait(0x13),
    LimitNonCombatExpIncrease(0x14), // "You cant gain any more Charm EXP today"
    AndroidNotPowered(0x16), // "The AndroidNotPowered is not powered. Please insert a Mechanical Heart"
    RecoveredFatigue(0x17), // "You recovered some fatigue by resting"
    PvpPoint(0x18), // "You received Battle EXP (+xx)", "You received Battle Points (+xx)"
    PvpItem(0x19),
    WeddingPortal(0x1A),
    HardcoreExp(0x1B),
    AutoLineChanged(0x1C),
    RecordEntryMessage(0x1D),
    EvolvingSystem(0x1E),
    PersonalEvolvingSystemMessage(0x1F),
    CoreInventory(0x20),
    Unknown(0x21), // <Header word> <byte>
    BlockedBehavior(0x22), // "Zero cannot get mesos in Maple World until Chapter 1 of the Main Quest is complete."
    IncreaseWP(0x23),
    StylishKill(0x25),
    SpecialScrollWarningPopup(0x26), // "Potential Scrolls, Shielding Wards, and other special scrolls have no effect with Star Force Enhancement. Do you still want to enhance it?"
    ExpiredItemPopup(0x27), //"This is an expired item"
    ;
    private final int type;

    private _MessageOpcodesType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }
}
