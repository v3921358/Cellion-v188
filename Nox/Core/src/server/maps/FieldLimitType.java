package server.maps;

public enum FieldLimitType {

    VipRock(0x40),
    Event(0x2000),
    Event2(0x10000),
    UnableToUseAntiMacroItem(0), // CField::IsUnableToUseAntiMacroItem(CField *this)
    UnableToJump(0), // CField::IsUnableToJump(CField *this)
    UnableToUseSkill(1), // CField::IsUnableToUseSkill(CField *this)
    UnableToUseSummonItem(2), // CField::IsUnableToUseSummonItem(CField *this)
    UnableToUseMysticDoor(3), // CField::IsUnableToUseMysticDoor(CField *this)
    UnableToMigrate(4), // CField::IsUnableToMigrate(CField *this)
    UnableToUsePortalScroll(5), // CField::IsUnableToUsePortalScroll(CField *this)
    UnableToUseTeleportItem(6), // CField::IsUnableToUseTeleportItem(CField *this)
    UnableToOpenMiniGame(7), // CField::IsUnableToOpenMiniGame(CField *this)
    UnableToUseSpecificPortalScroll(8), // CField::IsUnableToUseSpecificPortalScroll(CField *this)
    UnableToUseTamingMob(9), //  CField::IsUnableToUseTamingMob(CField *this)
    UnableToConsumeStatChangeItem(10), // CField::IsUnableToConsumeStatChangeItem(CField *this)
    UnableToChangePartyBoss(11), // CField::IsUnableToChangePartyBoss(CField *this)
    NoMonsterCapacityLimit(12), // NOMOBCAPACITYLIMIT
    UnableToUseWeddingInvitationItem(13), // CField::IsUnableToUseWeddingInvitationItem(CField *this)
    UnableToUseCashWeather(14), // CField::IsUnableToUseCashWeatherItem(CField *this)
    UnableToUsePet(15), // CField::IsUnableToUsePet(CField *this)
    AntiMacroLimit(16), // ANTIMACROLIMIT
    UnableToFallDown(17), // CField::IsUnableToFallDown(CField *this)
    UnableToSummonNPC(18), // CField::IsUnaUnableToUseAntiMacroItembleToSummonNPC(CField *this)
    NoExpDecrease(19), // NOEXPDECREASE
    NoDamageOnFalling(20), // CField::IsNoDamageOnFalling(CField *this)
    ParcelOpenLimit(21), // PARCELOPENLIMIT
    DropLimit(22), // CField::IsDropLimit(CField *this)
    UnableToUseRocketBoost(23), // CField::IsUnableToUseRocketBoost(CField *this)
    ItemOptionLimit(24), // ITEMOPTIONLIMIT
    NoQuestAlert(25), // CField::IsNoQuestAlert(CField *this)
    NoAndroid(26), // NOANDROID
    AutoExpandMinimap(27), // CField::IsAutoExpandMinimap(CField *this)
    MoveSkillOnly(28), // CField::IsMoveSkillOnly(CField *this)
    OnlyStarPlanet(29), // ONLYSTARPLANETPET
    ;

    /*[Flags]
    enum FIELDOPT : int 
    {
        MOVELIMIT = 1 << 0,
        SKILLLIMIT = 1 << 1,
        SUMMONLIMIT = 1 << 2,
        MYSTICDOORLIMIT = 1 << 3,
        MIGRATELIMIT = 1 << 4,
        PORTALSCROLLLIMIT = 1 << 5,
        TELEPORTITEMLIMIT = 1 << 6,
        MINIGAMELIMIT = 1 << 7,
        SPECIFICPORTALSCROLLLIMIT = 1 << 8,
        TAMINGMOBLIMIT = 1 << 9,
        STATCHANGEITEMCONSUMELIMIT = 1 << 10,
        PARTYBOSSCHANGELIMIT = 1 << 11,
        NOMOBCAPACITYLIMIT = 1 << 12,
        WEDDINGINVITATIONLIMIT = 1 << 13,
        CASHWEATHERCONSUMELIMIT = 1 << 14,
        NOPET = 1 << 15,
        ANTIMACROLIMIT = 1 << 16,
        FALLDOWNLIMIT = 1 << 17,
        SUMMONNPCLIMIT = 1 << 18,
        NOEXPDECREASE = 1 << 19,
        NODAMAGEONFALLING = 1 << 20,
        PARCELOPENLIMIT = 1 << 21,
        DROPLIMIT = 1 << 22,
        ROCKETBOOSTER_LIMIT = 1 << 23,
        ITEMOPTIONLIMIT = 1 << 24,
        NOQUESTALERTLIMIT = 1 << 25,
        NOANDROID = 1 << 26,
        AUTO_EXPAND_MINIMAP = 1 << 27,
        MOVESKILLONLY = 1 << 28,
        ONLYSTARPLANETPET = 1 << 29,
    }*/
    private final int numShifts;

    private FieldLimitType(int numShifts) {
        this.numShifts = numShifts;
    }

    public final boolean check(int fieldlimit) {
        //return (fieldlimit & i) == i;
        return (fieldlimit & 1) != 0;
    }

    public final boolean check(MapleMap map) {
        //return (map.getSharedMapResources().fieldLimit & i) == i;
        return ((map.getSharedMapResources().fieldLimit >> numShifts) & 1) != 0;
    }

    public final boolean checkFlag(int fieldlimit) {
        // support legacy crap which by now is still a guess work and is wrong...
        // until we're ready to remove it
        return (fieldlimit & numShifts) == numShifts;
    }

    public final boolean checkFlag(MapleMap map) {
        return (map.getSharedMapResources().fieldLimit & numShifts) == numShifts;
    }
}
