package server.maps.objects;

public enum MapleForceAtomTypes {
    DEMONFORCE_LOCAL(0x0),
    BLANCCARTE_BOTH(0x1),
    FLYINGSWORD_BOTH(0x2),
    SOULSEEKER_BOTH(0x3),
    SOULSEEKER_RECREATE(0x4),
    AEGIS_BOTH(0x5),
    AEGISACTIVE_BOTH(0x6),
    TRIFLINGWHIM_BOTH(0x7),
    STORMBRINGER_BOTH(0x8),
    ZEROFORCE_LOCAL(0x9),
    QUIVERCATRIDGE_BOTH(0xA),
    MARKOFASSASSIN_BOTH(0xB),
    MESOEXPLOSION_BOTH(0xC),
    POSSESSION_BOTH(0xD),
    EVENTPOINT_LOCAL(0xE),
    SHADOW_BAT_BOTH(0xF),
    SHADOW_BAT_BOUND_BOTH(0x10),
    NONTARGET_BOTH(0x11),
    TYPINGGAME_BOTH(0x12),
    SSFSHOOTING_BOTH(0x13),
    HORMING(0x14),
    NONTARGET_SIKSIN_BOTH(0x15),
    TELEKINESIS_BOTH(0x16),
    MAGIC_WRECKAGE(0x17),
    ADV_MAGIC_WRECKAGE(0x18),
    AUTO_SOULSEEKER_BOTH(0x19),
    AUTO_SOULSEEKER_RECREATE(0x1A),
    NUM(0x1B);
    private final int type;

    private MapleForceAtomTypes(int type) {
        this.type = type;
    }

    public byte getType() {
        return (byte) type;
    }
}
