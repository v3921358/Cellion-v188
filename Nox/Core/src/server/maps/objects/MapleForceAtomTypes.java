package server.maps.objects;

public enum MapleForceAtomTypes {
    DemonForce(0),
    Carte(1),
    NetherShield(2),
    SoulSeeker(3),
    SoulSeekerRecreate(4),
    Aegis(5),
    AegisActive(6),
    TriflingWind(7),
    StormBringer(8),
    ZeroForce(9),
    QuiverCartridge(10),
    MarkOfAssassin(11),
    MesoExplosion(12),
    Possession(13),
    EventPoint(14),
    ShadowBat(15),
    ShadowBatBound(16),
    NonTarget(17),
    TypingGame(18),
    SSFShooting(19),
    HomingBeacon(20),
    NonTargetSikSin(21),
    TeleKinesis(22),
    MagicWreckage(23),
    AdvancedMagicWreckage(24),
    AutoSoulSeeker(25),
    AutoSoulSeekerRecreate(26),
    AfterImage(27),
    DoTPunisher(28),
    SparkleBurst(29),
    
    // Unknown 30-33
    Unknown(30),
    Unknown_1(31),
    Unknown_2(32),
    Unknown_3(33),
    
    IdleWhim(34),
    SpiritStone(35),
    Num(36);
    private final int type;

    private MapleForceAtomTypes(int type) {
        this.type = type;
    }

    public byte getType() {
        return (byte) type;
    }
}
