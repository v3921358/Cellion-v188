package client;

import constants.GameConstants;
import constants.JobConstants;

public enum MapleJob {

    BEGINNER(0, -1, JobConstants._1stJobAdvancementLv),
    ///////////////
    WARRIOR(100, 0, JobConstants._2ndJobAdvancementLv),
    FIGHTER(110, 1, JobConstants._3rdJobAdvancementLv),
    CRUSADER(111, 2, JobConstants._4thJobAdvancementLv, false, true, false),
    HERO(112, 3, JobConstants._5thJobAdvancementLv, true, false, true),
    HERO_1(113, 4, GameConstants.maxLevel, false, false, true),
    //
    PAGE(120, 1, JobConstants._3rdJobAdvancementLv),
    WHITEKNIGHT(121, 2, JobConstants._4thJobAdvancementLv, false, true, false),
    PALADIN(122, 3, JobConstants._5thJobAdvancementLv, true, false, true),
    PALADIN_1(123, 4, GameConstants.maxLevel, false, false, true),
    //
    SPEARMAN(130, 1, JobConstants._3rdJobAdvancementLv),
    DRAGONKNIGHT(131, 2, JobConstants._4thJobAdvancementLv, false, true, false),
    DARKKNIGHT(132, 3, JobConstants._5thJobAdvancementLv, true, false, true),
    DARKKNIGHT_1(133, 4, GameConstants.maxLevel, false, false, true),
    ///////////////
    MAGICIAN(200, 0, JobConstants._2ndJobAdvancementLv),
    FP_WIZARD(210, 1, JobConstants._3rdJobAdvancementLv),
    FP_MAGE(211, 2, JobConstants._4thJobAdvancementLv, false, true, false),
    FP_ARCHMAGE(212, 3, JobConstants._5thJobAdvancementLv, true, false, true),
    FP_ARCHMAGE_1(213, 4, GameConstants.maxLevel, false, false, true),
    //
    IL_WIZARD(220, 1, JobConstants._3rdJobAdvancementLv),
    IL_MAGE(221, 2, JobConstants._4thJobAdvancementLv, false, true, false),
    IL_ARCHMAGE(222, 3, JobConstants._5thJobAdvancementLv, true, false, true),
    IL_ARCHMAGE_1(223, 4, GameConstants.maxLevel, false, false, true),
    //
    CLERIC(230, 1, JobConstants._3rdJobAdvancementLv),
    PRIEST(231, 2, JobConstants._4thJobAdvancementLv, false, true, false),
    BISHOP(232, 3, JobConstants._5thJobAdvancementLv, true, false, true),
    BISHOP_1(233, 4, GameConstants.maxLevel, false, false, true),
    ///////////////
    BOWMAN(300, 0, JobConstants._2ndJobAdvancementLv),
    HUNTER(310, 1, JobConstants._3rdJobAdvancementLv),
    RANGER(311, 2, JobConstants._4thJobAdvancementLv, false, true, false),
    BOWMASTER(312, 3, JobConstants._5thJobAdvancementLv, true, false, true),
    BOWMASTER_1(313, 4, GameConstants.maxLevel, false, false, true),
    //
    CROSSBOWMAN(320, 1, JobConstants._3rdJobAdvancementLv),
    SNIPER(321, 2, JobConstants._4thJobAdvancementLv, false, true, false),
    MARKSMAN(322, 3, JobConstants._5thJobAdvancementLv, true, false, true),
    MARKSMAN_1(323, 4, GameConstants.maxLevel, false, false, true),
    ///////////////
    THIEF(400, 0, JobConstants._2ndJobAdvancementLv),
    ASSASSIN(410, 1, JobConstants._3rdJobAdvancementLv),
    HERMIT(411, 2, JobConstants._4thJobAdvancementLv, false, true, false),
    NIGHTLORD(412, 3, JobConstants._5thJobAdvancementLv, true, false, true),
    NIGHTLORD_1(413, 4, GameConstants.maxLevel, false, false, true),
    //
    BANDIT(420, 1, JobConstants._3rdJobAdvancementLv),
    CHIEFBANDIT(421, 2, JobConstants._4thJobAdvancementLv, false, true, false),
    SHADOWER(422, 3, JobConstants._5thJobAdvancementLv, true, false, true),
    SHADOWER_1(423, 4, GameConstants.maxLevel, false, false, true),
    //
    BLADE_RECRUIT(430, 1, JobConstants.DualBlade_1stJobPlusAdvancementLv),
    BLADE_ACOLYTE(431, 2, JobConstants._2ndJobAdvancementLv),
    BLADE_SPECIALIST(432, 3, JobConstants.DualBlade_2ndJobPlusAdvancementLv),
    BLADE_LORD(433, 4, JobConstants._3rdJobAdvancementLv, false, true, false),
    BLADE_MASTER(434, 5, JobConstants._4thJobAdvancementLv, true, false, true),
    BLADE_MASTER_1(435, 6, GameConstants.maxLevel, false, false, true),
    ///////////////
    PIRATE(500, 0, JobConstants._2ndJobAdvancementLv),
    PIRATE_CS(501, 0, JobConstants._2ndJobAdvancementLv),
    JETT1(508, 0, JobConstants._2ndJobAdvancementLv),
    BRAWLER(510, 1, JobConstants._3rdJobAdvancementLv),
    MARAUDER(511, 2, JobConstants._4thJobAdvancementLv, false, true, false),
    BUCCANEER(512, 3, JobConstants._5thJobAdvancementLv, true, false, true),
    BUCCANEER_1(513, 4, GameConstants.maxLevel, false, false, true),
    //
    GUNSLINGER(520, 1, JobConstants._3rdJobAdvancementLv),
    OUTLAW(521, 2, JobConstants._4thJobAdvancementLv, false, true, false),
    CORSAIR(522, 3, JobConstants._5thJobAdvancementLv, true, false, true),
    CORSAIR_1(523, 4, GameConstants.maxLevel, false, false, true),
    //
    CANNONEER(530, 1, JobConstants._3rdJobAdvancementLv),
    CANNON_BLASTER(531, 2, JobConstants._4thJobAdvancementLv, false, true, false),
    CANNON_MASTER(532, 3, JobConstants._5thJobAdvancementLv, true, false, true),
    CANNON_MASTER_1(533, 4, GameConstants.maxLevel, false, false, true),
    //
    JETT2(570, 1, JobConstants._3rdJobAdvancementLv),
    JETT3(571, 2, JobConstants._4thJobAdvancementLv, false, true, false),
    JETT4(572, 3, JobConstants._5thJobAdvancementLv, true, false, true),
    JETT5(573, 4, GameConstants.maxLevel, false, false, true),
    //
    MANAGER(800, 0, GameConstants.maxLevel),
    ///////////////
    GM(900, 0, GameConstants.maxLevel),
    SUPERGM(910, 0, GameConstants.maxLevel),
    ///////////////
    NOBLESSE(1000, -1, JobConstants._1stJobAdvancementLv),
    DAWNWARRIOR1(1100, 0, JobConstants._2ndJobAdvancementLv),
    DAWNWARRIOR2(1110, 1, JobConstants._3rdJobAdvancementLv),
    DAWNWARRIOR3(1111, 2, JobConstants._4thJobAdvancementLv, false, true, false),
    DAWNWARRIOR4(1112, 3, JobConstants._5thJobAdvancementLv, true, false, true),
    DAWNWARRIOR5(1113, 4, GameConstants.maxLevel, false, false, true),
    //
    BLAZEWIZARD1(1200, 0, JobConstants._2ndJobAdvancementLv),
    BLAZEWIZARD2(1210, 1, JobConstants._3rdJobAdvancementLv),
    BLAZEWIZARD3(1211, 2, JobConstants._4thJobAdvancementLv, false, true, false),
    BLAZEWIZARD4(1212, 3, JobConstants._5thJobAdvancementLv, true, false, true),
    BLAZEWIZARD5(1213, 4, GameConstants.maxLevel, false, false, true),
    ///////////////
    WINDARCHER1(1300, 0, JobConstants._2ndJobAdvancementLv),
    WINDARCHER2(1310, 1, JobConstants._3rdJobAdvancementLv),
    WINDARCHER3(1311, 2, JobConstants._4thJobAdvancementLv, false, true, false),
    WINDARCHER4(1312, 3, JobConstants._5thJobAdvancementLv, true, false, true),
    WINDARCHER5(1313, 4, GameConstants.maxLevel, false, false, true),
    ///////////////
    NIGHTWALKER1(1400, 0, JobConstants._2ndJobAdvancementLv),
    NIGHTWALKER2(1410, 1, JobConstants._3rdJobAdvancementLv),
    NIGHTWALKER3(1411, 2, JobConstants._4thJobAdvancementLv, false, true, false),
    NIGHTWALKER4(1412, 3, JobConstants._5thJobAdvancementLv, true, false, true),
    NIGHTWALKER5(1413, 4, GameConstants.maxLevel, false, false, true),
    ///////////////
    THUNDERBREAKER1(1500, 0, JobConstants._2ndJobAdvancementLv),
    THUNDERBREAKER2(1510, 1, JobConstants._3rdJobAdvancementLv),
    THUNDERBREAKER3(1511, 2, JobConstants._4thJobAdvancementLv, false, true, false),
    THUNDERBREAKER4(1512, 3, JobConstants._5thJobAdvancementLv, true, false, true),
    THUNDERBREAKER5(1513, 4, GameConstants.maxLevel, false, false, true),
    ///////////////
    LEGEND(2000, -1, JobConstants._1stJobAdvancementLv),
    ARAN1(2100, 0, JobConstants._2ndJobAdvancementLv),
    ARAN2(2110, 1, JobConstants._3rdJobAdvancementLv),
    ARAN3(2111, 2, JobConstants._4thJobAdvancementLv, false, true, false),
    ARAN4(2112, 3, JobConstants._5thJobAdvancementLv, true, false, true),
    ARAN5(2113, 4, GameConstants.maxLevel, false, false, true),
    //
    EVAN_NOOB(2001, -1, JobConstants._1stJobAdvancementLv),
    EVAN1(2210, 0, JobConstants._2ndJobAdvancementLv),
    EVAN2(2212, 1, JobConstants._3rdJobAdvancementLv),
    EVAN3(2214, 2, JobConstants._4thJobAdvancementLv, false, true, false),
    EVAN4(2218, 3, JobConstants._5thJobAdvancementLv, true, false, true),
    EVAN5(2219, 4, GameConstants.maxLevel, false, false, true),
    //EVAN6(2214, 5, JobConstants.Evan_7thJobAdvancementLv),
    //EVAN7(2215, 6, JobConstants.Evan_8thJobAdvancementLv),
    //EVAN8(2216, 7, JobConstants.Evan_9thJobAdvancementLv),
    //EVAN9(2217, 8, JobConstants.Evan_10thJobAdvancementLv, false, true, false),
    //EVAN10(2218, 9, JobConstants.Evan_11thJobAdvancementLv, false, false, true),
    //EVAN11(2219, 10, GameConstants.maxLevel, false, false, true),
    //
    MERCEDES(2002, -1, JobConstants._1stJobAdvancementLv),
    MERCEDES1(2300, 0, JobConstants._2ndJobAdvancementLv),
    MERCEDES2(2310, 1, JobConstants._3rdJobAdvancementLv),
    MERCEDES3(2311, 2, JobConstants._4thJobAdvancementLv, false, true, false),
    MERCEDES4(2312, 3, JobConstants._5thJobAdvancementLv, true, false, true),
    MERCEDES5(2313, 4, GameConstants.maxLevel, false, false, true),
    //
    PHANTOM(2003, -1, JobConstants._1stJobAdvancementLv),
    PHANTOM1(2400, 0, JobConstants._2ndJobAdvancementLv),
    PHANTOM2(2410, 1, JobConstants._3rdJobAdvancementLv),
    PHANTOM3(2411, 2, JobConstants._4thJobAdvancementLv, false, true, false),
    PHANTOM4(2412, 3, JobConstants._5thJobAdvancementLv, true, false, true),
    PHANTOM5(2413, 4, GameConstants.maxLevel, false, false, true),
    //
    LUMINOUS(2004, -1, JobConstants._1stJobAdvancementLv),
    LUMINOUS1(2700, 0, JobConstants._2ndJobAdvancementLv),
    LUMINOUS2(2710, 1, JobConstants._3rdJobAdvancementLv),
    LUMINOUS3(2711, 2, JobConstants._4thJobAdvancementLv, false, true, false),
    LUMINOUS4(2712, 3, JobConstants._5thJobAdvancementLv, true, false, true),
    LUMINOUS5(2713, 4, GameConstants.maxLevel, false, false, true),
    //
    SHADE(2005, -1, JobConstants._1stJobAdvancementLv),
    SHADE1(2500, 0, JobConstants._2ndJobAdvancementLv),
    SHADE2(2510, 1, JobConstants._3rdJobAdvancementLv),
    SHADE3(2511, 2, JobConstants._4thJobAdvancementLv, false, true, false),
    SHADE4(2512, 3, JobConstants._5thJobAdvancementLv, true, false, true),
    SHADE5(2513, 4, GameConstants.maxLevel, false, false, true),
    ///////////////
    CITIZEN(3000, -1, JobConstants._1stJobAdvancementLv),
    DEMON_SLAYER(3001, -1, JobConstants._1stJobAdvancementLv),
    XENON(3002, -1, JobConstants._1stJobAdvancementLv),
    DEMON_SLAYER1(3100, 0, JobConstants._2ndJobAdvancementLv),
    DEMON_SLAYER2(3110, 1, JobConstants._3rdJobAdvancementLv),
    DEMON_SLAYER3(3111, 2, JobConstants._4thJobAdvancementLv, false, true, false),
    DEMON_SLAYER4(3112, 3, JobConstants._5thJobAdvancementLv, true, false, true),
    DEMON_SLAYER5(3113, 4, GameConstants.maxLevel, false, false, true),
    //
    DEMON_AVENGER1(3101, 0, JobConstants._2ndJobAdvancementLv),
    DEMON_AVENGER2(3120, 1, JobConstants._3rdJobAdvancementLv),
    DEMON_AVENGER3(3121, 2, JobConstants._4thJobAdvancementLv, false, true, false),
    DEMON_AVENGER4(3122, 3, JobConstants._5thJobAdvancementLv, true, false, true),
    DEMON_AVENGER5(3123, 4, GameConstants.maxLevel, false, false, true),
    //
    BATTLE_MAGE_1(3200, 0, JobConstants._2ndJobAdvancementLv),
    BATTLE_MAGE_2(3210, 1, JobConstants._3rdJobAdvancementLv),
    BATTLE_MAGE_3(3211, 2, JobConstants._4thJobAdvancementLv, false, true, false),
    BATTLE_MAGE_4(3212, 3, JobConstants._5thJobAdvancementLv, true, false, true),
    BATTLE_MAGE_5(3313, 4, GameConstants.maxLevel, false, false, true),
    //
    WILD_HUNTER_1(3300, 0, JobConstants._2ndJobAdvancementLv),
    WILD_HUNTER_2(3310, 1, JobConstants._3rdJobAdvancementLv),
    WILD_HUNTER_3(3311, 2, JobConstants._4thJobAdvancementLv, false, true, false),
    WILD_HUNTER_4(3312, 3, JobConstants._5thJobAdvancementLv, true, false, true),
    WILD_HUNTER_5(3313, 4, GameConstants.maxLevel, false, false, true),
    //
    MECHANIC_1(3500, 0, JobConstants._2ndJobAdvancementLv),
    MECHANIC_2(3510, 1, JobConstants._3rdJobAdvancementLv),
    MECHANIC_3(3511, 2, JobConstants._4thJobAdvancementLv, false, true, false),
    MECHANIC_4(3512, 3, JobConstants._5thJobAdvancementLv, true, false, true),
    MECHANIC_5(3513, 4, GameConstants.maxLevel, false, false, true),
    //
    XENON1(3600, 0, JobConstants._2ndJobAdvancementLv),
    XENON2(3610, 1, JobConstants._3rdJobAdvancementLv),
    XENON3(3611, 2, JobConstants._4thJobAdvancementLv, false, true, false),
    XENON4(3612, 3, JobConstants._5thJobAdvancementLv, true, false, true),
    XENON5(3613, 4, GameConstants.maxLevel, false, false, true),
    //
    BLASTER1(3700, 0, JobConstants._2ndJobAdvancementLv),
    BLASTER2(3710, 1, JobConstants._3rdJobAdvancementLv),
    BLASTER3(3711, 2, JobConstants._4thJobAdvancementLv, false, true, false),
    BLASTER4(3712, 3, JobConstants._5thJobAdvancementLv, true, false, true),
    ///////////////
    HAYATO(4001, -1, JobConstants._1stJobAdvancementLv),
    KANNA(4002, -1, JobConstants._1stJobAdvancementLv),
    HAYATO1(4100, 0, JobConstants._2ndJobAdvancementLv),
    HAYATO2(4110, 1, JobConstants._3rdJobAdvancementLv),
    HAYATO3(4111, 2, JobConstants._4thJobAdvancementLv, false, true, false),
    HAYATO4(4112, 3, JobConstants._5thJobAdvancementLv, true, false, true),
    HAYATO5(4113, 4, GameConstants.maxLevel, false, false, true),
    //
    KANNA1(4200, 0, JobConstants._2ndJobAdvancementLv),
    KANNA2(4210, 1, JobConstants._3rdJobAdvancementLv),
    KANNA3(4211, 2, JobConstants._4thJobAdvancementLv),
    KANNA4(4212, 3, JobConstants._5thJobAdvancementLv, true, false, true),
    KANNA5(4213, 4, GameConstants.maxLevel, false, false, true),
    ///////////////
    NAMELESS_WARDEN(5000, -1, JobConstants._1stJobAdvancementLv),
    MIHILE1(5100, 0, JobConstants._2ndJobAdvancementLv),
    MIHILE2(5110, 1, JobConstants._3rdJobAdvancementLv),
    MIHILE3(5111, 2, JobConstants._4thJobAdvancementLv, false, true, false),
    MIHILE4(5112, 3, JobConstants._5thJobAdvancementLv, true, false, true),
    MIHILE5(5113, 4, GameConstants.maxLevel, false, false, true),
    ///////////////
    KAISER(6000, -1, JobConstants._1stJobAdvancementLv),
    KAISER1(6100, 0, JobConstants._2ndJobAdvancementLv),
    KAISER2(6110, 1, JobConstants._3rdJobAdvancementLv),
    KAISER3(6111, 2, JobConstants._4thJobAdvancementLv, false, true, false),
    KAISER4(6112, 3, JobConstants._5thJobAdvancementLv, true, false, true),
    KAISER5(6113, 4, GameConstants.maxLevel, false, false, true),
    //
    ANGELIC_BUSTER(6001, -1, JobConstants._1stJobAdvancementLv),
    ANGELIC_BUSTER1(6500, 0, JobConstants._2ndJobAdvancementLv),
    ANGELIC_BUSTER2(6510, 1, JobConstants._3rdJobAdvancementLv),
    ANGELIC_BUSTER3(6511, 2, JobConstants._4thJobAdvancementLv, false, true, false),
    ANGELIC_BUSTER4(6512, 3, JobConstants._5thJobAdvancementLv, true, false, true),
    ANGELIC_BUSTER5(6513, 4, GameConstants.maxLevel, false, false, true),
    ///////////////
    ADDITIONAL_SKILLS(9000, -2, 0),
    ADDITIONAL_SKILLS1(9100, -2, 0),
    ADDITIONAL_SKILLS2(9200, -2, 0),
    ADDITIONAL_SKILLS3(9201, -2, 0),
    ADDITIONAL_SKILLS4(9202, -2, 0),
    ADDITIONAL_SKILLS5(9203, -2, 0),
    ADDITIONAL_SKILLS6(9204, -2, 0),
    ADDITIONAL_SKILLS7(9500, -2, 0),
    ///////////////
    ZERO(10000, -1, JobConstants._1stJobAdvancementLv), // This job is not used, for client reference only, zero starts at 10112
    ZERO1(10100, 0, GameConstants.maxLevel), // This job is not used, for client reference only, zero starts at 10112
    ZERO2(10110, 0, GameConstants.maxLevel), // This job is not used, for client reference only, zero starts at 10112
    ZERO3(10111, 0, GameConstants.maxLevel), // This job is not used, for client reference only, zero starts at 10112
    ZERO4(10112, 0, GameConstants.maxLevel, false, false, true),
    ZERO5(10113, 0, GameConstants.maxLevel, false, false, true),
    ///////////////
    BEASTTAMER(11200, 0, JobConstants._2ndJobAdvancementLv),
    BEASTTAMER2(11210, 1, JobConstants._3rdJobAdvancementLv),
    BEASTTAMER3(11211, 2, JobConstants._4thJobAdvancementLv, false, true, false),
    BEASTTAMER4(11212, 3, JobConstants._5thJobAdvancementLv, true, false, true),
    BEASTTAMER5(11213, 4, GameConstants.maxLevel, false, false, true),
    ///////////////
    PINKBEAN(13000, -1, JobConstants._1stJobAdvancementLv),
    PINKBEAN1(13100, 0, GameConstants.maxLevel, false, false, true), // to be verified... does PB gain every level? 
    ///////////////
    KINESIS(14000, -1, JobConstants._1stJobAdvancementLv),
    KINESIS1(14200, 0, JobConstants._2ndJobAdvancementLv),
    KINESIS2(14210, 1, JobConstants._3rdJobAdvancementLv),
    KINESIS3(14211, 2, JobConstants._4thJobAdvancementLv, false, true, false),
    KINESIS4(14212, 3, JobConstants._5thJobAdvancementLv, true, false, true),
    KINESIS5(14213, 4, GameConstants.maxLevel, false, false, true);

    private final int jobid;
    private final int jobSkillBook;
    private final boolean isFinalJob, isQuickSPGainJob, isQuickSPGainForNextJob;
    private final int maxLevelForNextAdvancement;

    private MapleJob(int id, int jobSkillBook, int maxLevelForNextAdvancement) {
        this.jobid = id;
        this.jobSkillBook = jobSkillBook;
        this.maxLevelForNextAdvancement = maxLevelForNextAdvancement;
        this.isQuickSPGainJob = false;
        this.isQuickSPGainForNextJob = false;
        this.isFinalJob = false;
    }

    private MapleJob(int id, int jobSkillBook, int maxLevelForNextAdvancement, boolean isQuickSPGainJob, boolean isQuickSPGainForNextJob, boolean isFinalJob) {
        this.jobid = id;
        this.jobSkillBook = jobSkillBook;
        this.maxLevelForNextAdvancement = maxLevelForNextAdvancement;
        this.isQuickSPGainJob = isQuickSPGainJob;
        this.isQuickSPGainForNextJob = isQuickSPGainForNextJob;
        this.isFinalJob = isFinalJob;
    }

    public int getId() {
        return this.jobid;
    }

    /**
     * Gets the next possible job that the character will be advancing to. If this is beginner, you may leave it as 'Warrior', it is fine..
     * just need this for reference for 'isQuickSPGainJob' because if the player over levels at 100, while being a third job... the
     * additional SP might be lost.
     *
     * @return
     */
    /* public MapleJob getNextPossibleJob() {
        return nextPossibleJob;
    }*/
    /**
     * How SP are defined in the MySQL: '0-0-1-21-5-21-3-1' jobSkillBook: -1 = noob/beginner; 0 = first job, 1 = second job; 2 = third job;
     * 3 = fourth job; -2 = additional skills
     *
     * @return int The skillbook tab
     */
    public int getJobSkillBook() {
        return jobSkillBook;
    }

    /**
     * Defines if this is the final available job for the class Note: Once fifth job is released, this should be updated to reflect that. :)
     *
     * @return boolean
     */
    public boolean isFinalJob() {
        return isFinalJob;
    }

    /**
     * The job that gains double the amount of SP at levels ending with: 0, 3, 6, 9.
     *
     * @return boolean
     */
    public boolean isQuickSPGainJob() {
        return isQuickSPGainJob;
    }

    public boolean isQuickSPGainForNextJob() {
        return isQuickSPGainForNextJob;
    }

    /**
     * The max level for this job before requiring a job advancement. Default = 0, if unavailable.
     *
     * @return
     */
    public int getMaxLevelForNextAdvancement() {
        return maxLevelForNextAdvancement;
    }

    public static String getName(MapleJob mjob) {
        return mjob.name();
    }

    public static MapleJob getById(int id) {
        for (MapleJob l : values()) {
            if (l.getId() == id) {
                return l;
            }
        }
        return null;
    }

    public static boolean isExist(int id) {
        for (MapleJob job : values()) {
            if (job.getId() == id) {
                return true;
            }
        }
        return false;
    }
}
