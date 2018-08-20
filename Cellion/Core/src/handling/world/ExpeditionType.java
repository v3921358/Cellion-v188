package handling.world;

public enum ExpeditionType {

    Balrog(6, 2001, 45, 250),
    Zakum(30, 2002, 50, 250),
    Horntail(30, 2003, 80, 250),
    Gollux(30, 2003, 80, 250),
    Pink_Bean(30, 2004, 120, 250),
    Chaos_Zakum(30, 2005, 100, 250),
    ChaosHT(30, 2006, 110, 250),
    CWKPQ(30, 2007, 90, 250),
    Von_Leon(18, 2008, 120, 250),
    Cygnus(18, 2009, 170, 250),
    Arkarium(18, 2010, 120, 250),
    Hilla(6, 2011, 70, 120),
    Chaos_Pink_Bean(30, 2012, 170, 250),
    Easy_Queen(2, 2013, 120, 250),
    Chaos_Queen(3, 2014, 190, 250),
    Easy_Pierre(2, 2015, 120, 250),
    Chaos_Pierre(3, 2016, 190, 250),
    Easy_VonBon(2, 2017, 120, 250),
    Chaos_VonBon(3, 2018, 190, 250),
    Easy_Vellum(2, 2019, 120, 250),
    Chaos_Vellum(3, 2020, 190, 250),
    Golden_Ravana(1, 2021, 215, 240),
    Magnus(1, 2022, 190, 255),
    ELotus(1, 2023, 190, 255),
    MLotus(1, 2024, 190, 255),
    CLotus(1, 2025, 190, 255),
    Ranmaru(3, 2026, 200, 250),
    MagnusN(1, 2027, 140, 255);

    public int maxMembers, maxParty, exped, minLevel, maxLevel;

    private ExpeditionType(int maxMembers, int exped, int minLevel, int maxLevel) {
        this.maxMembers = maxMembers;
        this.exped = exped;
        this.maxParty = (maxMembers / 2) + (maxMembers % 2 > 0 ? 1 : 0);
        this.minLevel = minLevel;
        this.maxLevel = maxLevel;
    }

    public static ExpeditionType getById(int id) {
        for (ExpeditionType pst : ExpeditionType.values()) {
            if (pst.exped == id) {
                return pst;
            }
        }
        return null;
    }
}
