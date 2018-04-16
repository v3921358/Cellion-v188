package server.maps.objects;

import client.*;
import client.MapleQuestStatus.MapleQuestState;
import client.MapleSpecialStats.MapleSpecialStatUpdateType;
import client.MapleTrait.MapleTraitType;
import client.anticheat.CheatTracker;
import client.anticheat.ReportType;
import client.buddy.Buddy;
import client.buddy.BuddyList;
import client.buddy.BuddyResult;
import client.buddy.BuddylistEntry;
import server.MapleStatEffect;
import client.inventory.*;
import client.inventory.MapleImp.ImpFlag;
import constants.GameConstants;
import constants.InventoryConstants;
import constants.MapConstants;
import constants.ServerConstants;
import constants.skills.Aran;
import constants.skills.Bowmaster;
import constants.skills.Crossbowman;
import constants.skills.Fighter;
import constants.skills.Hayato;
import constants.skills.Hero;
import constants.skills.Hunter;
import constants.skills.Kinesis;
import constants.skills.NightWalker;
import constants.skills.Mercedes;
import constants.skills.Mihile;
import constants.skills.Page;
import constants.skills.Phantom;
import constants.skills.Spearman;
import constants.skills.WildHunter;
import constants.skills.Zero;
import database.Database;
import handling.game.AndroidEmotionChanger;
import client.jobs.Explorer.ShadowerHandler;
import constants.ItemConstants;
import handling.login.LoginInformationProvider.JobType;
import handling.world.*;
import net.OutPacket;

import scripting.EventInstanceManager;
import scripting.provider.NPCScriptManager;
import server.*;
import server.MapleStatEffect.CancelEffectAction;
import server.Timer;
import server.Timer.BuffTimer;
import server.Timer.MapTimer;
import server.Timer.WorldTimer;
import server.life.*;
import server.maps.*;
import server.messages.*;
import server.movement.LifeMovementFragment;
import server.potentials.ItemPotentialProvider;
import server.potentials.ItemPotentialTierType;
import server.quest.MapleQuest;
import server.shops.MapleShop;
import server.shops.MapleShopFactory;
import server.shops.MapleShopItem;
import server.stores.IMaplePlayerShop;
import service.ChannelServer;
import service.LoginServer;
import tools.*;
import tools.packet.*;
import tools.packet.CField.EffectPacket;
import tools.packet.CField.EffectPacket.UserEffectCodes;
import tools.packet.CField.NPCPacket;
import tools.packet.CField.SummonPacket;
import tools.packet.CUserLocal.DeadUIStats;
import tools.packet.CWvsContext.InfoPacket;
import tools.packet.CWvsContext.Reward;
import tools.packet.JobPacket.AvengerPacket;
import tools.packet.JobPacket.LuminousPacket;
import tools.packet.JobPacket.PhantomPacket;
import tools.packet.JobPacket.XenonPacket;

import java.awt.*;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.sql.*;
import static java.sql.Statement.RETURN_GENERATED_KEYS;
import java.time.LocalDateTime;
import java.util.*;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import server.skills.VMatrixRecord;

public class User extends AnimatedMapleMapObject implements Serializable, MapleCharacterLook {

    public int acaneAim = 0, dualBrid = 0;
    private int[] stopForceAtoms;
    private static final long serialVersionUID = 845748950829L;
    private String name, chalktext, blessingOfFairy, blessingOfEmpress, teleportname;
    private long lastComboTime, lastfametime, keydown_skill, nextConsume, pqStartTime, lastDragonBloodTime,
            lastBerserkTime, lastRecoveryTime, lastSummonTime, mapChangeTime, lastFishingTime, lastFairyTime,
            lastHPTime, lastMPTime, lastFamiliarEffectTime, lastExceedTime, lastDOTTime, exp, meso;
    private byte gmLevel, gender, initialSpawnPoint, skinColor, guildrank = 5, allianceRank = 5,
            world, fairyExp, subcategory, cardStack, runningStack, runningBless = 0, numClones;
    private short level, job, mulung_energy, combo, force, availableCP, fatigue, totalCP, hpApUsed, scrolledPosition,
            kaiserCombo, xenonSurplus, exceed, kinesisPsychicPoint, exceedAttack = 0;
    public int accountid, id, hair, face, zeroBetaHair, zeroBetaFace, angelicDressupHair, angelicDressupFace, angelicDressupSuit, faceMarking, ears, tail, elf, mapid, fame, pvpExp, pvpPoints, totalWins, totalLosses,
            guildid = 0, fallcounter, maplepoints, acash, nxcredit, chair, itemEffect, points, vpoints, dpoints, epoints,
            rank = 1, rankMove = 0, jobRank = 1, jobRankMove = 0, marriageId, marriageItemId, dotHP,
            currentrep, totalrep, coconutteam, followid, battleshipHP, gachexp, challenge, guildContribution = 0, maplerewards = 0,
            remainingAp, honourExp, honorLevel, runningLight, runningLightSlot, runningDark, runningDarkSlot, luminousState, starterquest, starterquestid, evoentry, touchedrune;
    private Point oldPos;
    private MonsterFamiliar summonedFamiliar;
    private int[] wishlist, rocks, savedLocations, regrocks, hyperrocks, remainingSp = new int[10], remainingHSp = new int[3];
    private transient AtomicInteger insd;
    private transient MapleCharacterConversationType conversationType; // // 1 = NPC/ Quest, 2 = Donald, 3 = Hired Merch store, 4 = Storage
    private List<Integer> lastmonthfameids, lastmonthbattleids, extendedSlots;
    private List<MapleDoor> doors;
    private List<MechDoor> mechDoors;
    private List<Pet> pets;
    private List<ShopRepurchase> shopRepurchases;
    private MapleShop azwanShopList;
    private MapleImp[] imps;
    private List<Pair<Integer, Boolean>> stolenSkills = new ArrayList<>();
    private transient WeakReference<User>[] clones;
    private transient Set<Mob> controlled;
    private transient Set<MapleMapObject> visibleMapObjects;
    private transient ReentrantReadWriteLock visibleMapObjectsLock;
    private transient ReentrantReadWriteLock summonsLock;
    private transient ReentrantReadWriteLock controlledLock;
    private transient MapleAndroid android;
    private final Map<MapleQuest, MapleQuestStatus> quests;
    private Map<Integer, String> questinfo;
    private final Map<Skill, SkillEntry> skills;
    private transient Map<CharacterTemporaryStat, CharacterTemporaryStatValueHolder> effects;
    private final Map<String, String> CustomValues = new HashMap<>();
    private transient List<Summon> summons;
    private transient Map<Integer, Summon> summonss;
    private transient Map<Integer, MapleCoolDownValueHolder> coolDowns;
    private transient Map<MapleDisease, MapleDiseaseValueHolder> diseases;
    private Map<ReportType, Integer> reports;
    private CashShop cs;
    private transient Deque<MapleCarnivalChallenge> pendingCarnivalRequests;
    private transient MapleCarnivalParty carnivalParty;
    private BuddyList buddylist;
    private MonsterBook monsterbook;
    private transient CheatTracker anticheat;
    private MapleClient client;
    private transient MapleParty party;
    private PlayerStats stats;
    private final MapleCharacterCards characterCard;
    private transient MapleMap map;
    private transient MapleShop shop;
    private transient MapleDragon dragon;
    private transient MapleHaku haku;
    private transient MapleExtractor extractor;
    private transient RockPaperScissors rps;
    private Map<Integer, MonsterFamiliar> familiars;
    private MapleStorage storage;
    private transient MapleTrade trade;
    private MapleMount mount;
    private List<Integer> finishedAchievements;
    private MapleMessenger messenger;
    private byte[] petStore;
    private transient IMaplePlayerShop playerShop;
    private boolean invincible, canTalk, followinitiator, followon, smega, hasSummon, clone;
    private MapleGuildCharacter mgc;
    private MapleFamilyCharacter mfc;
    private transient EventInstanceManager eventInstance;
    private MapleInventory[] inventory;
    private SkillMacro[] skillMacros = new SkillMacro[5];
    private final EnumMap<MapleTraitType, MapleTrait> traits;
    private MapleKeyLayout keylayout;
    private transient ScheduledFuture<?> mapTimeLimitTask;
    private transient Event_PyramidSubway pyramidSubway = null;
    private transient List<Integer> pendingExpiration = null;
    private transient Map<Skill, SkillEntry> pendingSkills = null;
    private transient Map<Integer, Integer> linkMobs;
    private List<InnerSkillValueHolder> innerSkills;
    public boolean keyvalue_changed = false, innerskill_changed = true, changed_skills;
    private boolean changed_wishlist, changed_trocklocations, changed_regrocklocations, changed_hyperrocklocations, changed_skillmacros, changed_achievements,
            changed_savedlocations, changedQuestInfo, changed_reports, changed_extendedSlots, update_skillswipe;

    private transient CharacterTemporaryValues temporaryValues;
    /*
     * Start of Custom Feature
     */
    private int reborns, apstorage, batCount, darkElementalCombo;
    private List<LifeMovementFragment> lastGMMovement;
    private boolean bigBrother = false;
    /*
     * End of Custom Feature
     */
    private int str;
    private int luk;
    private int int_;
    private int dex;
    private int[] friendshippoints = new int[4];
    private int friendshiptoadd;
    private int wheelItem = 0;
    public transient static ScheduledFuture<?> XenonSupplyTask = null;
    private MapleCoreAura coreAura;
    private List<MaplePotionPot> potionPots;
    private int deathCount = 0;
    private MapleMarriage marriage;
    private int charListPosition;
    private boolean isZeroBeta = false;
    private boolean isAngelicDressup = false;
    private boolean isBurning = false;
    private int pendingGuildId = 0;

    public int[][] aStealMemory = new int[6][4];
    public Map<Integer, Integer> mStealSkillInfo = new HashMap<>();

    /*
     *  Luminous Gauge & Feather Handling
     *  Larkness System
     */
    private int lightGauge, darkGauge, lightFeathers, darkFeathers;

    public int getLarknessRequest(boolean bFeathers, boolean bDark) {

        if (!bFeathers) { // Get Gauge
            if (!bDark) {
                return this.lightGauge;
            } else {
                return this.darkGauge;
            }
        } else { // Get Feathers
            if (!bDark) {
                return this.lightFeathers;
            } else {
                return this.darkFeathers;
            }
        }
    }

    public void setLarknessResult(int nAmount, boolean bFeathers, boolean bDark) {

        if (!bFeathers) { // Set Gauge
            if (!bDark) {
                this.lightGauge = nAmount;
            } else {
                this.darkGauge = nAmount;
            }
        } else { // Set Feathers
            if (!bDark) {
                this.lightFeathers = nAmount;
            } else {
                this.darkFeathers = nAmount;
            }
        }
    }

    /*
     *  V: Matrix
     */
    public List<VMatrixRecord> aVMatrixRecord = new ArrayList<>();

    /**
     * Pet Features
     */
    public boolean hasPetVacuum() {
        if (haveItem(ItemConstants.PET_VAC)) {
            return true;
        }
        return false;
    }

    /*
     *  Staff Variables
     */
    private boolean bDisableStaffChat; // Disables special coloured chat for staff members.
    private boolean bGodMode;

    public boolean usingStaffChat() {
        if (bDisableStaffChat) {
            return false;
        }
        return true;
    }

    public void toggleStaffChat(boolean bDisable) {
        bDisableStaffChat = bDisable;
    }

    public boolean hasGodMode() {
        if (bGodMode) {
            return true;
        }
        return false;
    }

    public void toggleGodMode(boolean bEnabled) {
        if (bEnabled) {
            bGodMode = true;
        } else {
            bGodMode = false;
        }
    }

    /*
     *   Boss Variables
     */
    private long nMagnusTime; // Last Magnus boss attempt.

    public long getLastMagnusTime() {
        return nMagnusTime;
    }

    public void setLastMagnusTime() {
        if (getParty() == null) { // If player is in a party, sets the entry time for all players in the party.
            for (MaplePartyCharacter z : getParty().getMembers()) {
                User pPlayer = getMap().getCharacterById(z.getId());
                pPlayer.nMagnusTime = System.currentTimeMillis();
            }
            return;
        }
        nMagnusTime = System.currentTimeMillis();
        saveToDB(false, false);
    }

    public boolean canFightMagnus() {
        if (System.currentTimeMillis() > nMagnusTime + 86400000) {
            return true;
        }
        return false;
    }

    public boolean canPartyFightMagnus() {
        for (MaplePartyCharacter z : getParty().getMembers()) {
            User pPlayer = getMap().getCharacterById(z.getId());
            if (!pPlayer.canFightMagnus()) {
                return false;
            }
        }
        return true;
    }

    /*
     *   Job Handler Variables
     */
    private int nPrimaryStack; // General variable to store a combo count.
    private int nAdditionalStack; // Secondary variable to store other stack counts.
    private long nLastTimeReference; // General time reference that can be used for timers.
    private long nAdditionalLastTimeReference; // Secondary time reference that can be used for timers.
    private boolean bSwingStudies; // Check if Aran's Swing Studies is active.

    public int getPrimaryStack() {
        return nPrimaryStack;
    }

    public int getAdditionalStack() {
        return nAdditionalStack;
    }

    public void setPrimaryStack(int nAmount) {
        nPrimaryStack = nAmount;
    }

    public void setAdditionalStack(int nAmount) {
        nAdditionalStack = nAmount;
    }

    public void setTimeReference(long nTime) {
        nLastTimeReference = nTime;
    }

    public void setAdditionalTimeReference(long nTime) {
        nAdditionalLastTimeReference = nTime;
    }

    public boolean checkTimeout() {
        long nDuration = 0;
        if (getBuffedValue(CharacterTemporaryStat.NextAttackEnhance) != null) { // Aran: Swing Studies
            nDuration = 4000;
            if (nLastTimeReference + nDuration < System.currentTimeMillis()) {
                dispelBuff(Aran.SWING_STUDIES_I);

                setSwingStudies(false);
                return true;
            }
        }
        if (getBuffedValue(CharacterTemporaryStat.AdrenalinBoost) != null) { // Aran: Adrenaline Rush
            nDuration = 15000;
            if (nAdditionalLastTimeReference + nDuration < System.currentTimeMillis()) {
                dispelBuff(Aran.ADRENALINE_RUSH);

                nPrimaryStack = 500; // Sets Aran Combo back to 500 after completing Adrenaline Rush.
                setCombo(combo);
                write(CField.updateCombo(combo));
                return true;
            }
        }
        return false;
    }

    public void setSwingStudies(boolean bEnabled) {
        bSwingStudies = bEnabled;
    }

    public boolean getSwingStudies() {
        return bSwingStudies;
    }

    /**
     * Channel ID
     *
     * @return The channel ID that the MapleCharacter object is currently on.
     */
    public static int getChannel(int nCharacterID) {
        int nChannelID = 0;
        for (int i = 1; i <= ChannelServer.getChannelCount(); i++) {
            User pPlayer = ChannelServer.getInstance(i).getPlayerStorage().getCharacterById(nCharacterID);
            if (pPlayer != null) {
                nChannelID = i;
                break;
            }
        }
        return nChannelID;
    }

    private User(final boolean ChannelServer) {
        setStance(0);
        setPosition(new Point(0, 0));

        temporaryValues = new CharacterTemporaryValues();
        inventory = new MapleInventory[MapleInventoryType.values().length];
        for (MapleInventoryType type : MapleInventoryType.values()) {
            inventory[type.ordinal()] = new MapleInventory(type);
        }
        quests = new LinkedHashMap<>(); // Stupid erev quest.
        skills = new LinkedHashMap<>(); //Stupid UAs.

        stats = new PlayerStats();
        innerSkills = new LinkedList<>();
        azwanShopList = null;
        characterCard = new MapleCharacterCards();
        for (int i = 0; i < remainingSp.length; i++) {
            remainingSp[i] = 0;
        }
        for (int i = 0; i < remainingHSp.length; i++) {
            remainingHSp[i] = 0;
        }
        traits = new EnumMap<>(MapleTraitType.class);
        for (MapleTraitType t : MapleTraitType.values()) {
            traits.put(t, new MapleTrait(t));
        }
        charListPosition = 0;
        if (ChannelServer) {
            changed_reports = false;
            changed_skills = false;
            changed_achievements = false;
            changed_wishlist = false;
            changed_trocklocations = false;
            changed_regrocklocations = false;
            changed_hyperrocklocations = false;
            changed_skillmacros = false;
            changed_savedlocations = false;
            changed_extendedSlots = false;
            changedQuestInfo = false;
            update_skillswipe = false;
            scrolledPosition = 0;
            lastComboTime = 0;
            mulung_energy = 0;
            combo = 0;
            force = 0;
            keydown_skill = 0;
            nextConsume = 0;
            pqStartTime = 0;
            fairyExp = 0;
            cardStack = 0;
            runningStack = 1;
            mapChangeTime = 0;
            lastRecoveryTime = 0;
            lastDragonBloodTime = 0;
            lastBerserkTime = 0;
            lastFishingTime = 0;
            lastFairyTime = 0;
            lastHPTime = 0;
            lastMPTime = 0;
            lastFamiliarEffectTime = 0;
            oldPos = new Point(0, 0);
            coconutteam = 0;
            followid = 0;
            battleshipHP = 0;
            marriageItemId = 0;
            marriage = null;
            fallcounter = 0;
            challenge = 0;
            dotHP = 0;
            lastSummonTime = 0;
            hasSummon = false;
            invincible = false;
            canTalk = true;
            clone = false;
            followinitiator = false;
            followon = false;
            shopRepurchases = new ArrayList<>();
            linkMobs = new HashMap<>();
            finishedAchievements = new ArrayList<>();
            reports = new EnumMap<>(ReportType.class);
            teleportname = "";
            smega = true;
            petStore = new byte[3];
            for (int i = 0; i < petStore.length; i++) {
                petStore[i] = (byte) -1;
            }
            wishlist = new int[30];
            rocks = new int[10];
            regrocks = new int[5];
            hyperrocks = new int[13];
            imps = new MapleImp[3];
            clones = new WeakReference[5]; //for now
            for (int i = 0; i < clones.length; i++) {
                clones[i] = new WeakReference<>(null);
            }
            familiars = new LinkedHashMap<>();
            extendedSlots = new ArrayList<>();
            effects = new EnumMap<>(CharacterTemporaryStat.class);
            diseases = new EnumMap<>(MapleDisease.class);
            coolDowns = new LinkedHashMap<>();
            conversationType = MapleCharacterConversationType.None;
            insd = new AtomicInteger(-1);
            keylayout = new MapleKeyLayout();
            doors = new ArrayList<>();
            mechDoors = new ArrayList<>();
            controlled = new LinkedHashSet<>();
            controlledLock = new ReentrantReadWriteLock();
            summons = new LinkedList<>();
            summonsLock = new ReentrantReadWriteLock();
            visibleMapObjects = new LinkedHashSet<>();
            visibleMapObjectsLock = new ReentrantReadWriteLock();
            pendingCarnivalRequests = new LinkedList<>();

            savedLocations = new int[SavedLocationType.values().length];
            for (int i = 0; i < SavedLocationType.values().length; i++) {
                savedLocations[i] = -1;
            }
            questinfo = new LinkedHashMap<>();
            pets = new ArrayList<>();
            friendshippoints = new int[4];
            coreAura = new MapleCoreAura(id, 24 * 60);
            potionPots = new ArrayList<>();
            this.mStealSkillInfo.put(24001001, 0);
            this.mStealSkillInfo.put(24101001, 0);
            this.mStealSkillInfo.put(24111001, 0);
            this.mStealSkillInfo.put(24121001, 0);
            this.mStealSkillInfo.put(24121054, 0);
        }
    }

    public static User getDefault(final MapleClient client, final JobType type) {
        User ret = new User(false);
        ret.client = client;
        ret.map = null;
        ret.exp = 0;
        if (!ServerConstants.GLOBAL_GM_ACC) {
            ret.gmLevel = 0;
        }
        ret.job = (short) type.getJobId();
        ret.meso = 100000;
        ret.level = 1;
        ret.remainingAp = 0;
        ret.fame = 0;
        ret.accountid = client.getAccID();
        ret.buddylist = new BuddyList((byte) 20);

        ret.stats.str = 12;
        ret.stats.dex = 5;
        ret.stats.int_ = 4;
        ret.stats.luk = 4;
        ret.stats.maxhp = 50;
        ret.stats.hp = 50;
        ret.stats.maxmp = 5;
        ret.stats.mp = 5;
        ret.gachexp = 0;
        ret.friendshippoints = new int[]{0, 0, 0, 0};
        ret.friendshiptoadd = 0;
        ret.starterquest = 0;
        ret.starterquestid = 0;
        ret.evoentry = 5;
        ret.charListPosition = 0;
        try (Connection con = Database.GetConnection()) {
            System.out.println(Thread.currentThread().getStackTrace()[2].getClassName() + "." + Thread.currentThread().getStackTrace()[2].getMethodName());
            PreparedStatement ps;
            ps = con.prepareStatement("SELECT * FROM accounts WHERE id = ?");
            ps.setInt(1, ret.accountid);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    if (ServerConstants.GLOBAL_GM_ACC) {
                        ret.gmLevel = rs.getByte("gm");
                    }
                    ret.client.setAccountName(rs.getString("name"));
                    ret.nxcredit = rs.getInt("nxCredit");
                    ret.acash = rs.getInt("ACash");
                    ret.maplepoints = rs.getInt("mPoints");
                    ret.points = rs.getInt("points");
                    ret.vpoints = rs.getInt("vpoints");
                    ret.epoints = rs.getInt("epoints");
                    ret.dpoints = rs.getInt("dpoints");
                }
            }
            ps.close();
        } catch (SQLException e) {
            LogHelper.SQL.get().info("[SQL] There was an issue with something from the database:\n", e);
        }
        return ret;
    }

    public static User reconstructCharacter(final CharacterTransfer ct, final MapleClient client, final boolean isChannel) {
        final User ret = new User(true); // Always true, it's change channel
        ret.client = client;
        if (!isChannel) {
            ret.client.setChannel(ct.channel);
        }
        ret.id = ct.characterid;
        ret.name = ct.name;
        ret.level = ct.level;
        ret.fame = ct.fame;

        ret.CRand = new PlayerRandomStream();

        ret.stats.str = ct.str;
        ret.stats.dex = ct.dex;
        ret.stats.int_ = ct.int_;
        ret.stats.luk = ct.luk;
        ret.stats.maxhp = ct.maxhp;
        ret.stats.maxmp = ct.maxmp;
        ret.stats.hp = ct.hp;
        ret.stats.mp = ct.mp;

        ret.characterCard.setCards(ct.cardsInfo);

        ret.chalktext = ct.chalkboard;
        ret.gmLevel = ct.gmLevel;
        ret.exp = ret.level >= GameConstants.maxLevel ? 0 : ct.exp;
        ret.hpApUsed = ct.hpApUsed;
        ret.remainingSp = ct.remainingSp;
        ret.remainingHSp = ct.remainingHSp;
        ret.remainingAp = ct.remainingAp;
        ret.meso = ct.meso;
        //ret.stolenSkills = ct.stolenSkills;
        ret.aStealMemory = ct.aStealMemory;
        ret.mStealSkillInfo = ct.mStealSkillInfo;
        ret.skinColor = ct.skinColor;
        ret.gender = ct.gender;
        ret.job = ct.job;
        ret.hair = ct.hair;
        ret.face = ct.face;
        ret.zeroBetaHair = ct.zeroBetaHair;
        ret.zeroBetaFace = ct.zeroBetaFace;
        ret.angelicDressupHair = ct.angelicDressupHair;
        ret.angelicDressupFace = ct.angelicDressupFace;
        ret.angelicDressupSuit = ct.angelicDressupSuit;
        ret.faceMarking = ct.faceMarking;
        ret.ears = ct.ears;
        ret.tail = ct.tail;
        ret.elf = ct.elf;
        ret.accountid = ct.accountid;
        ret.totalWins = ct.totalWins;
        ret.totalLosses = ct.totalLosses;
        client.setAccID(ct.accountid);
        ret.mapid = ct.mapid;
        ret.initialSpawnPoint = ct.initialSpawnPoint;
        ret.world = ct.world;
        ret.guildid = ct.guildid;
        ret.guildrank = ct.guildrank;
        ret.guildContribution = ct.guildContribution;
        ret.allianceRank = ct.alliancerank;
        ret.points = ct.points;
        ret.vpoints = ct.vpoints;
        ret.epoints = ct.epoints;
        ret.dpoints = ct.dpoints;
        ret.fairyExp = ct.fairyExp;
        ret.cardStack = ct.cardStack;
        ret.marriageId = ct.marriageId;
        ret.marriage = ct.marriage;
        ret.currentrep = ct.currentrep;
        ret.totalrep = ct.totalrep;
        ret.gachexp = ct.gachexp;
        ret.honourExp = ct.honourexp;
        ret.honorLevel = ct.honourlevel;
        ret.innerSkills = (LinkedList<InnerSkillValueHolder>) ct.innerSkills;
        ret.azwanShopList = (MapleShop) ct.azwanShopList;
        ret.pvpExp = ct.pvpExp;
        ret.pvpPoints = ct.pvpPoints;
        /*
         * Start of Custom Feature
         */
        ret.reborns = ct.reborns;
        ret.apstorage = ct.apstorage;
        ret.bigBrother = ct.bigBrother;
        /*
         * End of Custom Feature
         */

 /*Start of Boss Features*/
        ret.nMagnusTime = ct.magnusTime;
        /*End of Boss Features*/

        ret.makeMFC(ct.familyid, ct.seniorid, ct.junior1, ct.junior2);
        if (ret.guildid > 0) {
            ret.mgc = new MapleGuildCharacter(ret);
        }
        ret.fatigue = ct.fatigue;
        ret.buddylist = new BuddyList(ct.buddysize);
        ret.subcategory = ct.subcategory;

        if (isChannel) {
            final MapleMapFactory mapFactory = ChannelServer.getInstance(client.getChannel()).getMapFactory();
            ret.map = mapFactory.getMap(ret.mapid);
            if (ret.map == null) { //char is on a map that doesn't exist warp it to spinel forest
                ret.map = mapFactory.getMap(MapConstants.SPINEL_FOREST);
            } else {
                MapleMap forcedReturnMap = ret.map.getForcedReturnMap();

                if (forcedReturnMap != null) {
                    ret.map = forcedReturnMap;

                    if (forcedReturnMap.getId() == 4000000) {
                        ret.initialSpawnPoint = 0;
                    }
                }
            }
            MaplePortal portal = ret.map.getPortal(ret.initialSpawnPoint);
            if (portal == null) {
                portal = ret.map.getPortal(0); // char is on a spawnpoint that doesn't exist - select the first spawnpoint instead
                ret.initialSpawnPoint = 0;
            }
            ret.setPosition(portal.getPosition());

            final int messengerid = ct.messengerid;
            if (messengerid > 0) {
                ret.messenger = World.Messenger.getMessenger(messengerid);
            }
        } else {

            ret.messenger = null;
        }
        int partyid = ct.partyid;
        if (partyid >= 0) {
            MapleParty party = World.Party.getParty(partyid);
            if (party != null && party.getMemberById(ret.id) != null) {
                ret.party = party;
            }
        }

        MapleQuestStatus queststatus_from;
        for (final Map.Entry<Integer, Object> qs : ct.Quest.entrySet()) {
            queststatus_from = (MapleQuestStatus) qs.getValue();
            queststatus_from.setQuest(qs.getKey());
            ret.quests.put(queststatus_from.getQuest(), queststatus_from);
        }
        for (final Map.Entry<Integer, SkillEntry> qs : ct.Skills.entrySet()) {
            ret.skills.put(SkillFactory.getSkill(qs.getKey()), qs.getValue());
        }

        for (final Integer zz : ct.finishedAchievements) {
            ret.finishedAchievements.add(zz);
        }
        for (Entry<MapleTraitType, Integer> t : ct.traits.entrySet()) {
            ret.traits.get(t.getKey()).setExp(t.getValue());
        }
        for (final Map.Entry<Byte, Integer> qs : ct.reports.entrySet()) {
            ret.reports.put(ReportType.getById(qs.getKey()), qs.getValue());
        }
        ret.monsterbook = new MonsterBook(ct.mbook, ret);
        ret.inventory = (MapleInventory[]) ct.inventorys;
        ret.blessingOfFairy = ct.BlessOfFairy;
        ret.blessingOfEmpress = ct.BlessOfEmpress;
        ret.skillMacros = (SkillMacro[]) ct.skillmacro;
        ret.petStore = ct.petStore;
        ret.keylayout = new MapleKeyLayout(ct.keymap);
        ret.questinfo = ct.InfoQuest;
        ret.familiars = ct.familiars;
        ret.savedLocations = ct.savedlocation;
        ret.wishlist = ct.wishlist;
        ret.rocks = ct.rocks;
        ret.regrocks = ct.regrocks;
        ret.hyperrocks = ct.hyperrocks;
        ret.buddylist.loadFromTransfer(ct.buddies);
        ret.keydown_skill = 0; // Keydown skill can't be brought over
        ret.lastfametime = ct.lastfametime;
        ret.lastmonthfameids = ct.famedcharacters;
        ret.lastmonthbattleids = ct.battledaccs;
        ret.extendedSlots = ct.extendedSlots;
        ret.storage = (MapleStorage) ct.storage;
        ret.cs = (CashShop) ct.cs;
        client.setAccountName(ct.accountname);
        ret.nxcredit = ct.nxCredit;
        ret.acash = ct.ACash;
        ret.maplepoints = ct.MaplePoints;
        ret.numClones = ct.clonez;
        ret.imps = ct.imps;
        ret.anticheat = (CheatTracker) ct.anticheat;
        ret.anticheat.start(ret);
        ret.shopRepurchases = ct.rebuy;
        ret.mount = new MapleMount(ret, ct.mount_itemid, PlayerStats.getSkillByJob(1004, ret.job), ct.mount_Fatigue, ct.mount_level, ct.mount_exp);
        ret.expirationTask(false, false);
        ret.stats.recalcLocalStats(true, ret);
        client.setTempIP(ct.tempIP);
        ret.isZeroBeta = ct.isZeroBeta;
        ret.isAngelicDressup = ct.isAngelicDressup;
        ret.isBurning = ct.isBurning;
        ret.aVMatrixRecord = ct.aVMatrixRecord;
        return ret;
    }

    public static User loadCharFromDB(int charid, MapleClient client, boolean channelserver) {
        return loadCharFromDB(charid, client, channelserver, null);
    }

    public static User loadCharFromDB(int charid, MapleClient client, boolean channelserver, final Map<Integer, CardData> cads) {
        final User ret = new User(channelserver);
        ret.client = client;
        ret.id = charid;

        try (Connection con = Database.GetConnection()) {
            System.out.println(Thread.currentThread().getStackTrace()[2].getClassName() + "." + Thread.currentThread().getStackTrace()[2].getMethodName());

            //LOAD BASIC STUFF
            try (PreparedStatement ps = con.prepareStatement("SELECT * FROM characters WHERE id = ? AND deletedAt is null")) {
                ps.setInt(1, charid);
                ResultSet rs = ps.executeQuery();
                if (!rs.next()) {
                    rs.close();
                    ps.close();
                    throw new RuntimeException("Loading the Char Failed (char not found)");
                }
                ret.name = rs.getString("name");
                ret.level = rs.getShort("level");
                ret.fame = rs.getInt("fame");

                ret.stats.str = rs.getShort("str");
                ret.stats.dex = rs.getShort("dex");
                ret.stats.int_ = rs.getShort("int");
                ret.stats.luk = rs.getShort("luk");
                ret.stats.maxhp = rs.getInt("maxhp");
                ret.stats.maxmp = rs.getInt("maxmp");
                ret.stats.hp = rs.getInt("hp");
                ret.stats.mp = rs.getInt("mp");
                ret.job = rs.getShort("job");
                ret.gmLevel = rs.getByte("gm");
                ret.exp = ret.level >= GameConstants.maxLevel ? 0 : rs.getLong("exp");
                ret.hpApUsed = rs.getShort("hpApUsed");
                String[] sp = rs.getString("sp").split(",");
                for (int i = 0; i < ret.remainingSp.length; i++) {
                    ret.remainingSp[i] = Integer.parseInt(sp[i]);
                }
                String[] hsp = rs.getString("hsp").split(",");
                for (int i = 0; i < ret.remainingHSp.length; i++) {
                    ret.remainingHSp[i] = Integer.parseInt(hsp[i]);
                }
                ret.remainingAp = rs.getShort("ap");
                ret.meso = rs.getLong("meso");
                ret.skinColor = rs.getByte("skincolor");
                ret.gender = rs.getByte("gender");

                ret.hair = rs.getInt("hair");
                ret.face = rs.getInt("face");
                ret.zeroBetaHair = rs.getInt("zeroBetaHair");
                ret.zeroBetaFace = rs.getInt("zeroBetaFace");
                ret.angelicDressupHair = rs.getInt("angelicDressupHair");
                ret.angelicDressupFace = rs.getInt("angelicDressupFace");
                ret.angelicDressupSuit = rs.getInt("angelicDressupSuit");
                ret.faceMarking = rs.getInt("faceMarking");
                ret.ears = rs.getInt("ears");
                ret.tail = rs.getInt("tail");
                ret.elf = rs.getInt("elf");
                ret.accountid = rs.getInt("accountid");
                client.setAccID(ret.accountid);
                ret.mapid = rs.getInt("map");
                ret.initialSpawnPoint = rs.getByte("spawnpoint");
                ret.world = rs.getByte("world");
                ret.guildid = rs.getInt("guildid");
                ret.guildrank = rs.getByte("guildrank");
                ret.allianceRank = rs.getByte("allianceRank");
                ret.guildContribution = rs.getInt("guildContribution");
                ret.totalWins = rs.getInt("totalWins");
                ret.totalLosses = rs.getInt("totalLosses");
                ret.currentrep = rs.getInt("currentrep");
                ret.totalrep = rs.getInt("totalrep");
                ret.makeMFC(rs.getInt("familyid"), rs.getInt("seniorid"), rs.getInt("junior1"), rs.getInt("junior2"));
                if (ret.guildid > 0) {
                    ret.mgc = new MapleGuildCharacter(ret);
                }
                ret.gachexp = rs.getInt("gachexp");
                ret.buddylist = new BuddyList(rs.getByte("buddyCapacity"));
                ret.honourExp = rs.getInt("honourExp");
                ret.honorLevel = rs.getInt("honourLevel");
                ret.subcategory = rs.getByte("subcategory");
                ret.mount = new MapleMount(ret, 0, PlayerStats.getSkillByJob(1004, ret.job), (byte) 0, (byte) 1, 0);
                ret.rank = rs.getInt("rank");
                ret.rankMove = rs.getInt("rankMove");
                ret.jobRank = rs.getInt("jobRank");
                ret.jobRankMove = rs.getInt("jobRankMove");
                ret.marriageId = rs.getInt("marriageId");
                ret.fatigue = rs.getShort("fatigue");
                ret.pvpExp = rs.getInt("pvpExp");
                ret.pvpPoints = rs.getInt("pvpPoints");
                ret.friendshiptoadd = rs.getInt("friendshiptoadd");
                ret.starterquest = rs.getInt("starterquest");
                ret.starterquestid = rs.getInt("starterquestid");
                ret.evoentry = rs.getInt("evoentry");
                ret.reborns = rs.getInt("reborns");
                ret.apstorage = rs.getInt("apstorage");
                ret.nMagnusTime = rs.getLong("magnusTime");
                ret.charListPosition = rs.getInt("position");
                ret.isBurning = rs.getBoolean("isBurning");
                for (MapleTrait t : ret.traits.values()) {
                    t.setExp(rs.getInt(t.getType().name()));
                }

                if (channelserver) {
                    ret.CRand = new PlayerRandomStream();
                    ret.anticheat = new CheatTracker(ret);
                    MapleMapFactory mapFactory = ChannelServer.getInstance(client.getChannel()).getMapFactory();
                    ret.map = mapFactory.getMap(ret.mapid);
                    if (ret.map == null) { //char is on a map that doesn't exist warp it to spinel forest
                        ret.map = mapFactory.getMap(950000100);
                    }
                    MaplePortal portal = ret.map.getPortal(ret.initialSpawnPoint);
                    if (portal == null) {
                        portal = ret.map.getPortal(0); // char is on a spawnpoint that doesn't exist - select the first spawnpoint instead
                        ret.initialSpawnPoint = 0;
                    }
                    ret.setPosition(portal.getPosition());

                    int partyid = rs.getInt("party");
                    if (partyid >= 0) {
                        MapleParty party = World.Party.getParty(partyid);
                        if (party != null && party.getMemberById(ret.id) != null) {
                            ret.party = party;
                        }
                    }
                    String[] petsParse = rs.getString("pets").split(",");
                    for (int i = 0; i < ret.petStore.length; i++) {
                        ret.petStore[i] = Byte.parseByte(petsParse[i]);
                    }
                    String[] friendshippoints = rs.getString("friendshippoints").split(",");
                    for (int i = 0; i < 4; i++) {
                        ret.friendshippoints[i] = Integer.parseInt(friendshippoints[i]);
                    }

                    try (PreparedStatement pse = con.prepareStatement("SELECT * FROM achievements WHERE accountid = ?")) {
                        pse.setInt(1, ret.accountid);
                        ResultSet rse = pse.executeQuery();
                        while (rse.next()) {
                            ret.finishedAchievements.add(rse.getInt("achievementid"));
                        }
                        rse.close();
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }

                    try (PreparedStatement pse = con.prepareStatement("SELECT * FROM reports WHERE characterid = ?")) {
                        pse.setInt(1, charid);
                        ResultSet rse = pse.executeQuery();
                        while (rse.next()) {
                            if (ReportType.getById(rse.getByte("type")) != null) {
                                ret.reports.put(ReportType.getById(rse.getByte("type")), rse.getInt("count"));
                            }
                        }
                        rse.close();
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                    rs.close();
                }
            }

            if (ret.marriageId > 0) {
                try (PreparedStatement ps = con.prepareStatement("SELECT * FROM characters WHERE id = ? AND deletedAt is null")) {
                    ps.setInt(1, ret.marriageId);
                    ResultSet rs = ps.executeQuery();

                    int partnerId = rs.getInt("id");

                    ret.marriage = new MapleMarriage(partnerId, ret.marriageItemId);
                    ret.marriage.setHusbandId(ret.gender == 0 ? ret.id : partnerId);
                    ret.marriage.setWifeId(ret.gender == 1 ? ret.id : partnerId);
                    String partnerName = rs.getString("name");
                    ret.marriage.setHusbandName(ret.gender == 0 ? ret.name : partnerName);
                    ret.marriage.setWifeName(ret.gender == 1 ? ret.name : partnerName);

                    rs.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }

            if (cads != null) { // so that we load only once.
                ret.characterCard.setCards(cads);
            } else { // load
                ret.characterCard.loadCards(client, channelserver, con);
            }

            // Load VMatrix
            try (PreparedStatement ps = con.prepareStatement("SELECT * FROM vmatrix WHERE characterid = ?")) {
                ps.setInt(1, charid);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    boolean nState = rs.getBoolean("state");
                    int nCoreID = rs.getInt("coreid");
                    int nSkillID = rs.getInt("skillid");
                    int nSkillID2 = rs.getInt("skillid2");
                    int nSkillID3 = rs.getInt("skillid3");
                    int nSLV = rs.getInt("level");
                    int nMasterLev = rs.getInt("masterlevel");
                    int nExp = rs.getInt("experience");
                    VMatrixRecord pRecord = new VMatrixRecord();
                    pRecord.nState = nState ? VMatrixRecord.Active : VMatrixRecord.Inactive;
                    pRecord.nCoreID = nCoreID;
                    pRecord.nSkillID = nSkillID;
                    pRecord.nSkillID2 = nSkillID2;
                    pRecord.nSkillID3 = nSkillID3;
                    pRecord.nSLV = nSLV;
                    pRecord.nMasterLev = nMasterLev;
                    pRecord.nExp = nExp;
                    ret.aVMatrixRecord.add(pRecord);
                }
                rs.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }

            // Load Phantom Stolen Skills
            try (PreparedStatement ps = con.prepareStatement("SELECT * from stealskill WHERE dwCharacterID = ?")) {
                ps.setInt(1, charid);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {

                    int nSlotSkillID = rs.getInt("nSlotSkillID");
                    int nStealSkillID = rs.getInt("nStealSkillID");
                    int nSlotID = rs.getInt("nSlotID");
                    int nPOS = rs.getInt("nPOS");
                    boolean bSet = rs.getInt("bSet") > 0;

                    if (bSet) {
                        ret.mStealSkillInfo.put(nSlotSkillID, nStealSkillID);
                    }

                    ret.aStealMemory[nSlotID][nPOS] = nStealSkillID;
                }
                rs.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }

            try (PreparedStatement ps = con.prepareStatement("SELECT * FROM queststatus WHERE characterid = ?"); PreparedStatement pse = con.prepareStatement("SELECT * FROM queststatusmobs WHERE queststatusid = ?")) {
                ps.setInt(1, charid);
                ResultSet rs = ps.executeQuery();

                while (rs.next()) {
                    final int id = rs.getInt("quest");
                    final MapleQuest q = MapleQuest.getInstance(id);
                    final byte stat = rs.getByte("status");
                    if ((stat == 1 || stat == 2) && channelserver && (q == null || q.isBlocked())) { //bigbang
                        continue;
                    }
                    if (stat == 1 && channelserver && !q.canStart(ret, null)) { //bigbang
                        continue;
                    }
                    final MapleQuestStatus status = new MapleQuestStatus(q, MapleQuestState.getFromValue(stat));
                    final long cTime = rs.getLong("time");
                    if (cTime > -1) {
                        status.setCompletionTime(cTime * 1000);
                    }
                    status.setForfeited(rs.getInt("forfeited"));
                    status.setCustomData(rs.getString("customData"));
                    ret.quests.put(q, status);
                    pse.setInt(1, rs.getInt("queststatusid"));
                    try (ResultSet rsMobs = pse.executeQuery()) {
                        while (rsMobs.next()) {
                            status.setMobKills(rsMobs.getInt("mob"), rsMobs.getInt("count"));
                        }
                    }
                }
                rs.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }

            if (channelserver) {
                ret.monsterbook = MonsterBook.loadCards(ret.accountid, ret, con);

                try (PreparedStatement ps = con.prepareStatement("SELECT * FROM inventoryslot where characterid = ?")) {
                    ps.setInt(1, charid);
                    ResultSet rs = ps.executeQuery();
                    if (!rs.next()) {
                        rs.close();
                        ps.close();
                        throw new RuntimeException("No Inventory slot column found in SQL. [inventoryslot]");
                    } else {
                        ret.getInventory(MapleInventoryType.EQUIP).setSlotLimit(rs.getByte("equip"));
                        ret.getInventory(MapleInventoryType.USE).setSlotLimit(rs.getByte("use"));
                        ret.getInventory(MapleInventoryType.SETUP).setSlotLimit(rs.getByte("setup"));
                        ret.getInventory(MapleInventoryType.ETC).setSlotLimit(rs.getByte("etc"));
                        ret.getInventory(MapleInventoryType.CASH).setSlotLimit(rs.getByte("cash"));
                    }
                    ps.close();
                    rs.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }

                for (Pair<Item, MapleInventoryType> mit : ItemLoader.INVENTORY.loadItems(false, charid, con).values()) {
                    ret.getInventory(mit.getRight()).addFromDB(mit.getLeft());
                    if (mit.getLeft().getPet() != null) {
                        ret.pets.add(mit.getLeft().getPet());
                    }
                }

                try (PreparedStatement ps = con.prepareStatement("SELECT * FROM accounts WHERE id = ?")) {
                    ps.setInt(1, ret.accountid);
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()) {
                        ret.getClient().setAccountName(rs.getString("name"));
                        ret.nxcredit = rs.getInt("nxCredit");
                        ret.acash = rs.getInt("ACash");
                        ret.maplepoints = rs.getInt("mPoints");
                        ret.points = rs.getInt("points");
                        ret.vpoints = rs.getInt("vpoints");
                        ret.epoints = rs.getInt("epoints");
                        ret.dpoints = rs.getInt("dpoints");

                        if (rs.getTimestamp("lastlogon") != null) {
                            final Calendar cal = Calendar.getInstance();
                            cal.setTimeInMillis(rs.getTimestamp("lastlogon").getTime());
                        }
                        if (rs.getInt("banned") > 0) {
                            rs.close();
                            ps.close();
                            ret.getClient().Close();
                            throw new RuntimeException("Loading a banned character");
                        }
                        rs.close();
                        ps.close();

                        try (PreparedStatement pse = con.prepareStatement("UPDATE accounts SET lastlogon = CURRENT_TIMESTAMP() WHERE id = ?")) {
                            pse.setInt(1, ret.accountid);
                            pse.executeUpdate();
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                        }
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }

                try (PreparedStatement ps = con.prepareStatement("SELECT * FROM questinfo WHERE characterid = ?")) {
                    ps.setInt(1, charid);
                    ResultSet rs = ps.executeQuery();

                    while (rs.next()) {
                        ret.questinfo.put(rs.getInt("quest"), rs.getString("customData"));
                    }

                    rs.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }

                try (PreparedStatement ps = con.prepareStatement("SELECT skillid, skilllevel, masterlevel, expiration FROM skills WHERE characterid = ?")) {
                    ps.setInt(1, charid);
                    ResultSet rs = ps.executeQuery();
                    Skill skil;
                    while (rs.next()) {
                        final int skid = rs.getInt("skillid");
                        skil = SkillFactory.getSkill(skid);
                        int skl = rs.getInt("skilllevel");
                        byte msl = rs.getByte("masterlevel");
                        if (skil != null && GameConstants.isApplicableSkill(skid)) {
                            if (skl > skil.getMaxLevel() && (skid < 92000000 || skid > 99999999)) {
                                if (!skil.isBeginnerSkill() && skil.canBeLearnedBy(ret.job) && !skil.isSpecialSkill()) {
                                    ret.remainingSp[GameConstants.getSkillBookForSkill(skid)] += (skl - skil.getMaxLevel());
                                }
                                skl = (byte) skil.getMaxLevel();
                            }
                            if (msl > skil.getMaxLevel()) {
                                msl = (byte) skil.getMaxLevel();
                            }
                            ret.skills.put(skil, new SkillEntry(skl, msl, rs.getLong("expiration")));
                        } else if (skil == null) { //doesnt. exist. e.g. bb
                            if (!GameConstants.isBeginnerJob(skid / 10000) && skid / 10000 != 900 && skid / 10000 != 800 && skid / 10000 != 9000) {
                                ret.remainingSp[GameConstants.getSkillBookForSkill(skid)] += skl;
                            }
                        }
                    }
                    rs.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }

                ret.expirationTask(false, true);

                try (PreparedStatement ps = con.prepareStatement("SELECT * FROM coreauras WHERE cid = ?")) {
                    ps.setInt(1, ret.id);
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()) {
                        ret.coreAura = new MapleCoreAura(ret.id, rs.getInt("expire"));
                        ret.coreAura.setStr(rs.getInt("str"));
                        ret.coreAura.setDex(rs.getInt("dex"));
                        ret.coreAura.setInt(rs.getInt("int"));
                        ret.coreAura.setLuk(rs.getInt("luk"));
                        ret.coreAura.setAtt(rs.getInt("att"));
                        ret.coreAura.setMagic(rs.getInt("magic"));
                        ret.coreAura.setTotal(rs.getInt("total"));
                    } else {
                        ret.coreAura = new MapleCoreAura(ret.id, 24 * 60);
                    }
                    rs.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }

                // Bless of Fairy handling
                try (PreparedStatement ps = con.prepareStatement("SELECT * FROM characters WHERE accountid = ? AND deletedAt is null ORDER BY level DESC")) {
                    ps.setInt(1, ret.accountid);
                    ResultSet rs = ps.executeQuery();
                    int maxlevel_ = 0, maxlevel_2 = 0;
                    while (rs.next()) {
                        if (rs.getInt("id") != charid) { // Not this character
                            if (GameConstants.isCygnusKnight(rs.getShort("job"))) {
                                int maxlevel = (rs.getShort("level") / 5);

                                if (maxlevel > 24) {
                                    maxlevel = 24;
                                }
                                if (maxlevel > maxlevel_2 || maxlevel_2 == 0) {
                                    maxlevel_2 = maxlevel;
                                    ret.blessingOfEmpress = rs.getString("name");
                                }
                            }
                            int maxlevel = (rs.getShort("level") / 10);

                            if (maxlevel > 20) {
                                maxlevel = 20;
                            }
                            if (maxlevel > maxlevel_ || maxlevel_ == 0) {
                                maxlevel_ = maxlevel;
                                ret.blessingOfFairy = rs.getString("name");
                            }

                        }
                    }
                    if (ret.blessingOfFairy == null) {
                        ret.blessingOfFairy = ret.name;
                    }
                    ret.skills.put(SkillFactory.getSkill(GameConstants.getBOF_ForJob(ret.job)), new SkillEntry(maxlevel_, (byte) 0, -1));
                    if (SkillFactory.getSkill(GameConstants.getEmpress_ForJob(ret.job)) != null) {
                        if (ret.blessingOfEmpress == null) {
                            ret.blessingOfEmpress = ret.blessingOfFairy;
                        }
                        ret.skills.put(SkillFactory.getSkill(GameConstants.getEmpress_ForJob(ret.job)), new SkillEntry(maxlevel_2, (byte) 0, -1));
                    }
                    rs.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }

                try (PreparedStatement ps = con.prepareStatement("SELECT skill_id, skill_level, max_level, rank, locked FROM inner_ability_skills WHERE player_id = ?")) {
                    ps.setInt(1, charid);
                    ResultSet rs = ps.executeQuery();
                    while (rs.next()) {
                        ret.innerSkills.add(new InnerSkillValueHolder(rs.getInt("skill_id"), rs.getByte("skill_level"), rs.getByte("max_level"), rs.getByte("rank"), rs.getBoolean("locked")));
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }

                try (PreparedStatement ps = con.prepareStatement("SELECT * FROM skillmacros WHERE characterid = ?")) {
                    ps.setInt(1, charid);
                    ResultSet rs = ps.executeQuery();
                    int position;
                    while (rs.next()) {
                        position = rs.getInt("position");
                        SkillMacro macro = new SkillMacro(rs.getInt("skill1"), rs.getInt("skill2"), rs.getInt("skill3"), rs.getString("name"), rs.getInt("shout"), position);
                        ret.skillMacros[position] = macro;
                    }
                    rs.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }

                try (PreparedStatement ps = con.prepareStatement("SELECT * FROM familiars WHERE characterid = ?")) {
                    ps.setInt(1, charid);
                    ResultSet rs = ps.executeQuery();
                    while (rs.next()) {
                        if (rs.getLong("expiry") <= System.currentTimeMillis()) {
                            continue;
                        }
                        ret.familiars.put(rs.getInt("familiar"), new MonsterFamiliar(charid, rs.getInt("id"), rs.getInt("familiar"), rs.getLong("expiry"), rs.getString("name"), rs.getInt("fatigue"), rs.getByte("vitality")));
                    }
                    rs.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }

                try (PreparedStatement ps = con.prepareStatement("SELECT `key`,`type`,`action` FROM keymap WHERE characterid = ?")) {
                    ps.setInt(1, charid);
                    ResultSet rs = ps.executeQuery();

                    while (rs.next()) {
                        ret.keylayout.addKeyLayout(rs.getInt("key"), rs.getByte("type"), rs.getInt("action"), false);
                    }
                    rs.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }

                try (PreparedStatement ps = con.prepareStatement("SELECT `locationtype`,`map` FROM savedlocations WHERE characterid = ?")) {
                    ps.setInt(1, charid);
                    ResultSet rs = ps.executeQuery();
                    while (rs.next()) {
                        ret.savedLocations[rs.getInt("locationtype")] = rs.getInt("map");
                    }
                    rs.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }

                try (PreparedStatement ps = con.prepareStatement("SELECT `characterid_to`,`when` FROM famelog WHERE characterid = ? AND DATEDIFF(NOW(),`when`) < 30")) {
                    ps.setInt(1, charid);
                    ResultSet rs = ps.executeQuery();
                    ret.lastfametime = 0;
                    ret.lastmonthfameids = new ArrayList<>(31);
                    while (rs.next()) {
                        ret.lastfametime = Math.max(ret.lastfametime, rs.getTimestamp("when").getTime());
                        ret.lastmonthfameids.add(Integer.valueOf(rs.getInt("characterid_to")));
                    }
                    rs.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }

                try (PreparedStatement ps = con.prepareStatement("SELECT `accid_to`,`when` FROM battlelog WHERE accid = ? AND DATEDIFF(NOW(),`when`) < 30")) {
                    ps.setInt(1, ret.accountid);
                    ResultSet rs = ps.executeQuery();
                    ret.lastmonthbattleids = new ArrayList<>();
                    while (rs.next()) {
                        ret.lastmonthbattleids.add(Integer.valueOf(rs.getInt("accid_to")));
                    }
                    rs.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }

                try (PreparedStatement ps = con.prepareStatement("SELECT `itemId` FROM extendedslots WHERE characterid = ?")) {
                    ps.setInt(1, charid);
                    ResultSet rs = ps.executeQuery();
                    while (rs.next()) {
                        ret.extendedSlots.add(Integer.valueOf(rs.getInt("itemId")));
                    }
                    rs.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }

                ret.buddylist.loadFromDb(charid, con);
                ret.storage = MapleStorage.loadStorage(ret.accountid);
                ret.cs = new CashShop(ret.accountid, charid, ret.getJob(), con);

                try (PreparedStatement ps = con.prepareStatement("SELECT sn FROM wishlist WHERE characterid = ?")) {
                    ps.setInt(1, charid);
                    ResultSet rs = ps.executeQuery();
                    int i = 0;
                    while (rs.next()) {
                        ret.wishlist[i] = rs.getInt("sn");
                        i++;
                    }
                    while (i < 30) {
                        ret.wishlist[i] = 0;
                        i++;
                    }
                    rs.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }

                try (PreparedStatement ps = con.prepareStatement("SELECT mapid FROM trocklocations WHERE characterid = ?")) {
                    ps.setInt(1, charid);
                    ResultSet rs = ps.executeQuery();
                    int r = 0;
                    while (rs.next()) {
                        ret.rocks[r] = rs.getInt("mapid");
                        r++;
                    }
                    while (r < 10) {
                        ret.rocks[r] = 999999999;
                        r++;
                    }
                    rs.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }

                try (PreparedStatement ps = con.prepareStatement("SELECT mapid FROM regrocklocations WHERE characterid = ?")) {
                    ps.setInt(1, charid);
                    ResultSet rs = ps.executeQuery();
                    int r = 0;
                    while (rs.next()) {
                        ret.regrocks[r] = rs.getInt("mapid");
                        r++;
                    }
                    while (r < 5) {
                        ret.regrocks[r] = 999999999;
                        r++;
                    }
                    rs.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }

                try (PreparedStatement ps = con.prepareStatement("SELECT mapid FROM hyperrocklocations WHERE characterid = ?")) {
                    ps.setInt(1, charid);
                    ResultSet rs = ps.executeQuery();
                    int r = 0;
                    while (rs.next()) {
                        ret.hyperrocks[r] = rs.getInt("mapid");
                        r++;
                    }
                    while (r < 13) {
                        ret.hyperrocks[r] = 999999999;
                        r++;
                    }
                    rs.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }

                try (PreparedStatement ps = con.prepareStatement("SELECT * from stolen WHERE characterid = ?")) {
                    ps.setInt(1, charid);
                    ResultSet rs = ps.executeQuery();
                    while (rs.next()) {
                        ret.stolenSkills.add(new Pair<>(rs.getInt("skillid"), rs.getInt("chosen") > 0));
                    }
                    rs.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }

                try (PreparedStatement ps = con.prepareStatement("SELECT * FROM imps WHERE characterid = ?")) {
                    ps.setInt(1, charid);
                    ResultSet rs = ps.executeQuery();
                    int r = 0;
                    while (rs.next()) {
                        ret.imps[r] = new MapleImp(rs.getInt("itemid"));
                        ret.imps[r].setLevel(rs.getByte("level"));
                        ret.imps[r].setState(rs.getByte("state"));
                        ret.imps[r].setCloseness(rs.getShort("closeness"));
                        ret.imps[r].setFullness(rs.getShort("fullness"));
                        r++;
                    }
                    rs.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }

                try (PreparedStatement ps = con.prepareStatement("SELECT * FROM mountdata WHERE characterid = ?")) {
                    ps.setInt(1, charid);
                    ResultSet rs = ps.executeQuery();
                    if (!rs.next()) {
                        throw new RuntimeException("No mount data found on SQL column");
                    }
                    final Item mount = ret.getInventory(MapleInventoryType.EQUIPPED).getItem((byte) -18);
                    ret.mount = new MapleMount(ret, mount != null ? mount.getItemId() : 0, 80001000, rs.getByte("Fatigue"), rs.getByte("Level"), rs.getInt("Exp"));
                    rs.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }

                ret.stats.recalcLocalStats(true, ret);
            } else { // Not channel server
                for (Pair<Item, MapleInventoryType> mit : ItemLoader.INVENTORY.loadItems(true, charid, con).values()) {
                    ret.getInventory(mit.getRight()).addFromDB(mit.getLeft());
                }
                ret.stats.recalcPVPRank(ret);
            }
        } catch (SQLException ex) {
            LogHelper.SQL.get().info("Failed to load character:\n{}", ex);
        }
        return ret;
    }

    public static int getQuestKillCount(User chr, final int mobid) {
        try (PreparedStatement ps = Database.GetConnection().prepareStatement("SELECT queststatusid FROM queststatus WHERE characterid = ?")) {
            ResultSet rse;
            try (ResultSet rs = ps.executeQuery()) {
                try (PreparedStatement pse = Database.GetConnection().prepareStatement("SELECT count FROM queststatusmobs WHERE queststatusid = ?")) {
                    rse = pse.executeQuery();
                    while (rs.next()) {
                        pse.close();
                        return rse.getInt("count");
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static void saveNewCharToDB(final User chr, final JobType type, short db) {
        PreparedStatement ps = null;
        PreparedStatement pse = null;
        ResultSet rs = null;
        try (Connection con = Database.GetConnection()) {
            System.out.println(Thread.currentThread().getStackTrace()[2].getClassName() + "." + Thread.currentThread().getStackTrace()[2].getMethodName());
            con.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
            con.setAutoCommit(false);
            ps = con.prepareStatement("INSERT INTO characters (level, str, dex, luk, `int`, hp, mp, maxhp, maxmp, sp, hsp, ap, skincolor, gender, job, hair, face, zeroBetaHair, zeroBetaFace, angelicDressupHair, angelicDressupFace, angelicDressupSuit, faceMarking, ears, tail, map, meso, party, buddyCapacity, pets, subcategory, elf, friendshippoints, gm, accountid, name, world, starterquest, starterquestid, evoentry, position, magnusTime)"
                    + "                                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", RETURN_GENERATED_KEYS);
            int index = 0;
            ps.setInt(++index, chr.level); // Level
            final PlayerStats stat = chr.stats;
            ps.setInt(++index, stat.getStr()); // Str
            ps.setInt(++index, stat.getDex()); // Dex
            ps.setInt(++index, stat.getLuk()); // Luk
            ps.setInt(++index, stat.getInt()); // Int
            ps.setInt(++index, stat.getHp()); // HP
            ps.setInt(++index, stat.getMp());
            ps.setInt(++index, stat.getMaxHp()); // MP
            ps.setInt(++index, stat.getMaxMp());
            final StringBuilder sps = new StringBuilder();
            for (int i = 0; i < chr.remainingSp.length; i++) {
                sps.append(chr.remainingSp[i]);
                sps.append(",");
            }
            final String sp = sps.toString();
            ps.setString(++index, sp.substring(0, sp.length() - 1));
            final StringBuilder hsps = new StringBuilder();
            for (int i = 0; i < chr.remainingHSp.length; i++) {
                hsps.append(chr.remainingHSp[i]);
                hsps.append(",");
            }
            final String hsp = hsps.toString();
            ps.setString(++index, hsp.substring(0, hsp.length() - 1));
            if (chr.remainingAp > (999 + 16) - (chr.str + chr.dex + chr.int_ + chr.luk)) {
                chr.remainingAp = (999 + 16) - (chr.str + chr.dex + chr.int_ + chr.luk);
            }
            ps.setShort(++index, (short) chr.remainingAp); // Remaining AP
            ps.setByte(++index, chr.skinColor);
            ps.setByte(++index, chr.gender);
            ps.setInt(++index, db == 2 ? 501 : chr.job); // Sets Cannoneer job (db=2 = cannoneer?). Default: ps.setInt(++index, chr.job);
            ps.setInt(++index, chr.hair);
            ps.setInt(++index, chr.face);
            ps.setInt(++index, chr.zeroBetaHair);
            ps.setInt(++index, chr.zeroBetaFace);
            ps.setInt(++index, chr.angelicDressupHair);
            ps.setInt(++index, chr.angelicDressupFace);
            ps.setInt(++index, chr.angelicDressupSuit);
            ps.setInt(++index, chr.faceMarking);
            if (db < 0 || db > 10) {
                db = 0;
            }
            ps.setInt(++index, chr.ears);
            ps.setInt(++index, chr.tail);
            ps.setInt(++index, type.getStartingMapId()); //ps.setInt(++index, db == 2 ? 3000600 : type.getStartingMapId());
            ps.setLong(++index, chr.meso); // Meso
            ps.setInt(++index, -1); // Party
            ps.setByte(++index, chr.buddylist.getCapacity()); // Buddylist
            ps.setString(++index, "-1,-1,-1");
            ps.setInt(++index, db); //for now
            ps.setInt(++index, chr.elf);
            ps.setString(++index, chr.friendshippoints[0] + "," + chr.friendshippoints[1] + "," + chr.friendshippoints[2] + "," + chr.friendshippoints[3]);
            if (chr.isDeveloper() && ServerConstants.GLOBAL_GM_ACC) {
                ps.setByte(++index, (byte) 5);
            } else if (chr.isAdmin() && ServerConstants.GLOBAL_GM_ACC) {
                ps.setByte(++index, (byte) 4);
            } else if (chr.isGM() && ServerConstants.GLOBAL_GM_ACC) {
                ps.setByte(++index, (byte) 3);
            } else if (chr.isIntern() && ServerConstants.GLOBAL_GM_ACC) {
                ps.setByte(++index, (byte) 2);
            } else if (chr.isDonator()) {
                ps.setByte(++index, (byte) 1);
            } else {
                ps.setByte(++index, (byte) 0);
            }
            ps.setInt(++index, chr.getAccountID());
            ps.setString(++index, chr.name);
            ps.setByte(++index, chr.world);
            ps.setInt(++index, chr.starterquest);
            ps.setInt(++index, chr.starterquestid);
            ps.setInt(++index, chr.evoentry);
            ps.setInt(++index, chr.charListPosition);
            ps.setInt(++index, 0);
            ps.executeUpdate();

            rs = ps.getGeneratedKeys();
            if (rs.next()) {
                chr.id = rs.getInt(1);
            } else {
                ps.close();
                rs.close();
                System.out.println("[Debug] Inserting character database info failed.");
                throw new SQLException("Inserting char failed.");
            }
            ps.close();
            rs.close();
            ps = con.prepareStatement("INSERT INTO queststatus (`queststatusid`, `characterid`, `quest`, `status`, `time`, `forfeited`, `customData`) VALUES (DEFAULT, ?, ?, ?, ?, ?, ?)", RETURN_GENERATED_KEYS);
            pse = con.prepareStatement("INSERT INTO queststatusmobs VALUES (DEFAULT, ?, ?, ?)");
            ps.setInt(1, chr.id);
            for (final MapleQuestStatus q : chr.quests.values()) {
                ps.setInt(2, q.getQuest().getId());
                ps.setInt(3, q.getStatus().getValue());
                ps.setInt(4, (int) (q.getCompletionTime() / 1000));
                ps.setInt(5, q.getForfeited());
                ps.setString(6, q.getCustomData());
                ps.execute();
                rs = ps.getGeneratedKeys();
                if (q.hasMobKills()) {
                    rs.next();
                    for (int mob : q.getMobKills().keySet()) {
                        pse.setInt(1, rs.getInt(1));
                        pse.setInt(2, mob);
                        pse.setInt(3, q.getMobKills(mob));
                        pse.execute();
                    }
                }
                rs.close();
            }
            ps.close();
            pse.close();

            ps = con.prepareStatement("INSERT INTO skills (characterid, skillid, skilllevel, masterlevel, expiration) VALUES (?, ?, ?, ?, ?)");
            ps.setInt(1, chr.id);

            for (final Entry<Skill, SkillEntry> skill : chr.skills.entrySet()) {
                if (GameConstants.isApplicableSkill(skill.getKey().getId())) { //do not save additional skills
                    ps.setInt(2, skill.getKey().getId());
                    ps.setInt(3, skill.getValue().skillevel);
                    ps.setByte(4, skill.getValue().masterlevel);
                    ps.setLong(5, skill.getValue().expiration);
                    ps.execute();
                }
            }
            ps.close();

            //TODO {TEST} SAVE NEW JETT CORE AURA
            ps = con.prepareStatement("INSERT INTO coreauras (cid, str, dex, `int`, luk, att, magic, total, expire) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");
            ps.setInt(1, chr.id);
            if (GameConstants.isJett(chr.job)) {
                ps.setInt(2, 3);
                ps.setInt(3, 3);
                ps.setInt(4, 3);
                ps.setInt(5, 3);
                ps.setInt(6, 3);
                ps.setInt(7, 3);
                ps.setInt(8, 24 * 60);
            }

            ps = con.prepareStatement("INSERT INTO inventoryslot (characterid, `equip`, `use`, `setup`, `etc`, `cash`) VALUES (?, ?, ?, ?, ?, ?)");
            ps.setInt(1, chr.id);
            ps.setByte(2, (byte) 32); // Eq
            ps.setByte(3, (byte) 32); // Use
            ps.setByte(4, (byte) 32); // Setup
            ps.setByte(5, (byte) 32); // ETC
            ps.setByte(6, (byte) 60); // Cash
            ps.execute();
            ps.close();

            ps = con.prepareStatement("INSERT INTO mountdata (characterid, `Level`, `Exp`, `Fatigue`) VALUES (?, ?, ?, ?)");
            ps.setInt(1, chr.id);
            ps.setByte(2, (byte) 1);
            ps.setInt(3, 0);
            ps.setByte(4, (byte) 0);
            ps.execute();
            ps.close();
            // old
            //final int[] array1 = {2, 3, 4, 5, 6, 7, 16, 17, 18, 19, 23, 25, 26, 27, 31, 34, 37, 38, 41, 44, 45, 46, 50, 57, 59, 60, 61, 62, 63, 64, 65, 8, 9, 24, 30};
            //final int[] array2 = {4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 5, 5, 5, 4, 5, 6, 6, 6, 6, 6, 6, 6, 4, 4, 4, 4};
            //final int[] array3 = {10, 12, 13, 18, 6, 11, 8, 5, 0, 4, 1, 19, 14, 15, 3, 17, 9, 20, 22, 50, 51, 52, 7, 53, 100, 101, 102, 103, 104, 105, 106, 16, 23, 24, 2};
            //int[] array1 = {18, 65, 2, 23, 3, 4, 5, 6, 16, 17, 19, 25, 26, 27, 31, 34, 35, 37, 38, 40, 43, 44, 45, 46, 50, 56, 59, 60, 61, 62, 63, 64, 57, 48, 29, 7, 24, 33, 41, 39, 8, 20, 21, 49};
            int[] array1 = {2, 3, 64, 4, 65, 5, 6, 7, 8, 13, 17, 16, 19, 18, 21, 20, 23, 22, 25, 24, 27, 26, 29, 31, 34, 35, 33, 38, 39, 37, 43, 40, 41, 46, 47, 44, 45, 51, 50, 49, 48, 59, 57, 56, 63, 62, 61, 60};
            //int[] array2 = {4, 6, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 5, 5, 4, 4, 5, 6, 6, 6, 6, 6, 6, 5, 4, 5, 4, 4, 4, 4, 4, 4, 4, 4, 4};
            int[] array2 = {4, 4, 6, 4, 6, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 5, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 5, 5, 4, 4, 4, 4, 6, 5, 5, 6, 6, 6, 6};
            //int[] array3 = {0, 106, 10, 1, 12, 13, 18, 24, 8, 5, 4, 19, 14, 15, 2, 17, 11, 3, 20, 16, 9, 50, 51, 6, 7, 53, 100, 101, 102, 103, 104, 105, 54, 30, 52, 21, 25, 26, 23, 27, 29, 28, 31, 22};
            int[] array3 = {10, 12, 105, 13, 106, 18, 24, 21, 29, 33, 5, 8, 4, 0, 31, 28, 1, 34, 19, 25, 15, 14, 52, 2, 17, 11, 26, 20, 27, 3, 9, 16, 23, 6, 32, 50, 51, 35, 7, 22, 30, 100, 54, 53, 104, 103, 102, 101};
            /*0 0 0 0 0 4 104 124 134 184 244 214 290 0 0 0 0 0 0 0 4 330 0 0 0 4 84 54 04 44
             284 314 344 14 254 194 144 150 0 5 520 0 4 20 0 4 264 174 110 0 4 34 204 274 164
             230 0 4 95 505 514 64 324 304 224 74 350 0 0 0 0 0 0 0 5 535 540 0 6 1006 1016
             1026 1036 1046 1056 1060 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
             0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0*/
            ps = con.prepareStatement("INSERT INTO keymap (characterid, `key`, `type`, `action`) VALUES (?, ?, ?, ?)");
            ps.setInt(1, chr.id);
            for (int i = 0; i < array1.length; i++) {
                ps.setInt(2, array1[i]);
                ps.setInt(3, array2[i]);
                ps.setInt(4, array3[i]);
                ps.execute();
            }
            ps.close();

            List<Pair<Item, MapleInventoryType>> listing = new ArrayList<>();
            for (final MapleInventory iv : chr.inventory) {
                for (final Item item : iv.list()) {
                    listing.add(new Pair<>(item, iv.getType()));
                }
            }
            ItemLoader.INVENTORY.saveItems(listing, chr.id, con);

            con.commit();
        } catch (SQLException e) {
            LogHelper.SQL.get().info("Could not save character:\n{}", e);
            try {
                Database.GetConnection().rollback();
            } catch (SQLException ex) {
                LogHelper.SQL.get().info("Requred to roll back:\n{}", ex);
            }
        } finally {
            try {
                if (pse != null) {
                    pse.close();
                }
                if (ps != null) {
                    ps.close();
                }
                if (rs != null) {
                    rs.close();
                }
                Database.GetConnection().setAutoCommit(true);
                Database.GetConnection().setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
            } catch (SQLException e) {
                LogHelper.SQL.get().info("Could not close the thread:\n{}", e);
            }
        }
    }

    public synchronized void updateCharlistPosition(int charPos) {

        try (PreparedStatement ps = Database.GetConnection().prepareStatement("UPDATE characters SET position = ? WHERE id = ?", RETURN_GENERATED_KEYS)) {
            ps.setInt(1, charPos);
            ps.setInt(2, id);
            ps.executeUpdate();
        } catch (Exception e) {
            System.out.println("Issues with updating the database with the new position of the character in the character list.");
        }
    }

    /**
     * Save Inventory and Equips of a Character
     *
     * @author Mazen Massoud
     * @purpose To save the item information of a character in order to prevent duplication and rollback.
     */
    public void saveItemData() {
        if (isClone()) {
            return;
        }

        try (Connection con = Database.GetConnection()) {
            System.out.println(Thread.currentThread().getStackTrace()[2].getClassName() + "." + Thread.currentThread().getStackTrace()[2].getMethodName());
            deleteWhereCharacterId(con, "DELETE FROM inventoryslot WHERE characterid = ?");
            try (PreparedStatement ps = con.prepareStatement("INSERT INTO inventoryslot (characterid, `equip`, `use`, `setup`, `etc`, `cash`) VALUES (?, ?, ?, ?, ?, ?)")) {
                ps.setInt(1, id);
                ps.setByte(2, getInventory(MapleInventoryType.EQUIP).getSlotLimit());
                ps.setByte(3, getInventory(MapleInventoryType.USE).getSlotLimit());
                ps.setByte(4, getInventory(MapleInventoryType.SETUP).getSlotLimit());
                ps.setByte(5, getInventory(MapleInventoryType.ETC).getSlotLimit());
                ps.setByte(6, getInventory(MapleInventoryType.CASH).getSlotLimit());
                ps.execute();
                ps.close();
            } catch (SQLException e) {
                LogHelper.SQL.get().info("Could not save character information:\n{}", e);
            }

            saveInventory(con);
        } catch (SQLException e) {
            LogHelper.SQL.get().info("Could not save character information:\n{}", e);
        }

        if (isDeveloper()) {
            dropMessage(5, "[SQL Debug] Item Data has been saved successfully.");
        }
    }

    public void saveToDB(boolean dc, boolean fromcs) {
        if (isClone()) {
            return;
        }

        try (Connection con = Database.GetConnection()) {
            System.out.println(Thread.currentThread().getStackTrace()[2].getClassName() + "." + Thread.currentThread().getStackTrace()[2].getMethodName());

            try (PreparedStatement ps = con.prepareStatement("UPDATE characters SET level = ?, fame = ?, str = ?, dex = ?, luk = ?, `int` = ?, exp = ?, hp = ?, mp = ?, maxhp = ?, maxmp = ?, sp = ?, hsp = ?, ap = ?, gm = ?, skincolor = ?, gender = ?, job = ?, hair = ?, face = ?, zeroBetaHair = ?, zeroBetaFace = ?, angelicDressupHair = ?, angelicDressupFace = ?, angelicDressupSuit = ?, faceMarking = ?, ears = ?, tail = ?, map = ?, meso = ?, hpApUsed = ?, spawnpoint = ?, party = ?, buddyCapacity = ?, pets = ?, subcategory = ?, currentrep = ?, totalrep = ?, gachexp = ?, fatigue = ?, charm = ?, charisma = ?, craft = ?, insight = ?, sense = ?, will = ?, totalwins = ?, totallosses = ?, pvpExp = ?, pvpPoints = ?, reborns = ?, apstorage = ?, magnusTime = ?, elf = ?, honourExp = ?, honourLevel = ?, friendshippoints = ?, friendshiptoadd = ?, name = ?, starterquest = ?, starterquestid = ?, evoentry = ?, position = ?, isBurning = ? WHERE id = ?", RETURN_GENERATED_KEYS)) {
                int index = 0;
                ps.setInt(++index, level);
                ps.setInt(++index, fame);
                ps.setInt(++index, stats.getStr());
                ps.setInt(++index, stats.getDex());
                ps.setInt(++index, stats.getLuk());
                ps.setInt(++index, stats.getInt());
                ps.setLong(++index, level >= GameConstants.maxLevel ? 0 : exp);
                ps.setInt(++index, stats.getHp() < 1 ? 50 : stats.getHp());
                ps.setInt(++index, stats.getMp());
                ps.setInt(++index, stats.getMaxHp());
                ps.setInt(++index, stats.getMaxMp());
                final StringBuilder sps = new StringBuilder();
                for (int i = 0; i < remainingSp.length; i++) {
                    sps.append(remainingSp[i]);
                    sps.append(",");
                }
                final String skillpoints = sps.toString();
                ps.setString(++index, skillpoints.substring(0, skillpoints.length() - 1));

                final StringBuilder hsps = new StringBuilder();
                for (int i = 0; i < remainingHSp.length; i++) {
                    hsps.append(remainingHSp[i]);
                    hsps.append(",");
                }
                final String hskillpoints = hsps.toString();
                ps.setString(++index, hskillpoints.substring(0, hskillpoints.length() - 1));
                ps.setInt(++index, remainingAp);
                ps.setByte(++index, gmLevel);
                ps.setByte(++index, skinColor);
                ps.setByte(++index, gender);
                ps.setInt(++index, job);
                ps.setInt(++index, hair);
                ps.setInt(++index, face);
                ps.setInt(++index, zeroBetaHair);
                ps.setInt(++index, zeroBetaFace);
                ps.setInt(++index, angelicDressupHair);
                ps.setInt(++index, angelicDressupFace);
                ps.setInt(++index, angelicDressupSuit);
                ps.setInt(++index, faceMarking);
                ps.setInt(++index, ears);
                ps.setInt(++index, tail);
                if (!fromcs && map != null) {
                    MapleMap forcedReturnMap = map.getForcedReturnMap();
                    if (forcedReturnMap != null && forcedReturnMap.getId() != 999999999) {
                        ps.setInt(++index, forcedReturnMap.getId());
                    } else {
                        ps.setInt(++index, stats.getHp() < 1 ? map.getReturnMap().getId() : map.getId());
                    }
                } else {
                    ps.setInt(++index, mapid);
                }
                ps.setLong(++index, meso);
                ps.setShort(++index, hpApUsed);
                if (map == null) {
                    ps.setByte(++index, (byte) 0);
                } else {
                    final MaplePortal closest = map.findClosestSpawnpoint(getTruePosition());
                    ps.setByte(++index, (byte) (closest != null ? closest.getId() : 0));
                }
                ps.setInt(++index, party == null ? -1 : party.getId());
                ps.setShort(++index, buddylist.getCapacity());
                final StringBuilder petz = new StringBuilder();
                int petLength = 0;
                for (final Pet pet : pets) { // its correct, hmm
                    if (pet.getSummoned()) {
                        pet.saveToDb();
                        petz.append(pet.getItem().getPosition());
                        petz.append(",");
                        petLength++;
                    }
                }
                while (petLength < 3) {
                    petz.append("-1,");
                    petLength++;
                }
                final String petstring = petz.toString();
                ps.setString(++index, petstring.substring(0, petstring.length() - 1));
                ps.setByte(++index, subcategory);
                //ps.setInt(++index, marriageId);
                ps.setInt(++index, currentrep);
                ps.setInt(++index, totalrep);
                ps.setInt(++index, gachexp);
                ps.setShort(++index, fatigue);
                ps.setInt(++index, traits.get(MapleTraitType.charm).getTotalExp());
                ps.setInt(++index, traits.get(MapleTraitType.charisma).getTotalExp());
                ps.setInt(++index, traits.get(MapleTraitType.craft).getTotalExp());
                ps.setInt(++index, traits.get(MapleTraitType.insight).getTotalExp());
                ps.setInt(++index, traits.get(MapleTraitType.sense).getTotalExp());
                ps.setInt(++index, traits.get(MapleTraitType.will).getTotalExp());
                ps.setInt(++index, totalWins);
                ps.setInt(++index, totalLosses);
                ps.setInt(++index, pvpExp);
                ps.setInt(++index, pvpPoints);
                /*
             * Start of Custom Features
                 */
                ps.setInt(++index, reborns);
                ps.setInt(++index, apstorage);
                /*
             * End of Custom Features
                 */

 /*Start of Boss Features*/
                ps.setLong(++index, nMagnusTime);
                /*End of Boss Features*/

                ps.setInt(++index, elf);
                ps.setInt(++index, honourExp);
                ps.setInt(++index, honorLevel);
                ps.setString(++index, friendshippoints[0] + "," + friendshippoints[1] + "," + friendshippoints[2] + "," + friendshippoints[3]);
                ps.setInt(++index, friendshiptoadd);
                ps.setString(++index, name);
                ps.setInt(++index, starterquest);
                ps.setInt(++index, starterquestid);
                ps.setInt(++index, evoentry);
                ps.setInt(++index, charListPosition);
                ps.setBoolean(++index, isBurning);
                ps.setInt(++index, id);

                if (ps.executeUpdate() < 1) {
                    ps.close();
                    throw new SQLException("Character not in database (" + id + ")");
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }

            deleteWhereCharacterId(con, "DELETE FROM stolen WHERE characterid = ?");

            for (Pair<Integer, Boolean> st : stolenSkills) {
                try (PreparedStatement ps = con.prepareStatement("INSERT INTO stolen (characterid, skillid, chosen) VALUES (?, ?, ?)")) {
                    ps.setInt(1, id);
                    ps.setInt(2, st.left);
                    ps.setInt(3, st.right ? 1 : 0);
                    ps.execute();
                    ps.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }

            if (changed_skillmacros) {
                deleteWhereCharacterId(con, "DELETE FROM skillmacros WHERE characterid = ?");
                for (int i = 0; i < 5; i++) {
                    final SkillMacro macro = skillMacros[i];
                    if (macro != null) {
                        try (PreparedStatement ps = con.prepareStatement("INSERT INTO skillmacros (characterid, skill1, skill2, skill3, name, shout, position) VALUES (?, ?, ?, ?, ?, ?, ?)")) {
                            ps.setInt(1, id);
                            ps.setInt(2, macro.getSkill1());
                            ps.setInt(3, macro.getSkill2());
                            ps.setInt(4, macro.getSkill3());
                            ps.setString(5, macro.getName());
                            ps.setInt(6, macro.getShout());
                            ps.setInt(7, i);
                            ps.execute();
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            }

            deleteWhereCharacterId(con, "DELETE FROM inventoryslot WHERE characterid = ?");

            try (PreparedStatement ps = con.prepareStatement("INSERT INTO inventoryslot (characterid, `equip`, `use`, `setup`, `etc`, `cash`) VALUES (?, ?, ?, ?, ?, ?)")) {
                ps.setInt(1, id);
                ps.setByte(2, getInventory(MapleInventoryType.EQUIP).getSlotLimit());
                ps.setByte(3, getInventory(MapleInventoryType.USE).getSlotLimit());
                ps.setByte(4, getInventory(MapleInventoryType.SETUP).getSlotLimit());
                ps.setByte(5, getInventory(MapleInventoryType.ETC).getSlotLimit());
                ps.setByte(6, getInventory(MapleInventoryType.CASH).getSlotLimit());
                ps.execute();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }

            saveInventory(con);//CHECK

            if (changedQuestInfo) {
                deleteWhereCharacterId(con, "DELETE FROM questinfo WHERE characterid = ?");
                try (PreparedStatement ps = con.prepareStatement("INSERT INTO questinfo (`characterid`, `quest`, `customData`) VALUES (?, ?, ?)")) {
                    ps.setInt(1, id);
                    for (final Entry<Integer, String> q : questinfo.entrySet()) {
                        ps.setInt(2, q.getKey());
                        ps.setString(3, q.getValue());
                        ps.execute();
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }

            deleteWhereCharacterId(con, "DELETE FROM queststatus WHERE characterid = ?");

            try (PreparedStatement ps = con.prepareStatement("INSERT INTO queststatus (`queststatusid`, `characterid`, `quest`, `status`, `time`, `forfeited`, `customData`) VALUES (DEFAULT, ?, ?, ?, ?, ?, ?)", RETURN_GENERATED_KEYS)) {
                try (PreparedStatement pse = con.prepareStatement("INSERT INTO queststatusmobs VALUES (DEFAULT, ?, ?, ?)")) {
                    ps.setInt(1, id);
                    for (final MapleQuestStatus q : quests.values()) {
                        ps.setInt(2, q.getQuest().getId());
                        ps.setInt(3, q.getStatus().getValue());
                        ps.setInt(4, (int) (q.getCompletionTime() / 1000));
                        ps.setInt(5, q.getForfeited());
                        ps.setString(6, q.getCustomData());
                        ps.execute();
                        ResultSet rs = ps.getGeneratedKeys();
                        if (q.hasMobKills()) {
                            rs.next();
                            for (int mob : q.getMobKills().keySet()) {
                                pse.setInt(1, rs.getInt(1));
                                pse.setInt(2, mob);
                                pse.setInt(3, q.getMobKills(mob));
                                pse.execute();
                            }
                        }
                        rs.close();
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }

            if (changed_skills) {
                deleteWhereCharacterId(con, "DELETE FROM skills WHERE characterid = ?");
                try (PreparedStatement ps = con.prepareStatement("INSERT INTO skills (characterid, skillid, skilllevel, masterlevel, expiration) VALUES (?, ?, ?, ?, ?)")) {
                    ps.setInt(1, id);

                    for (final Entry<Skill, SkillEntry> skill : skills.entrySet()) {
                        if (GameConstants.isApplicableSkill(skill.getKey().getId())) { //do not save additional skills
                            ps.setInt(2, skill.getKey().getId());
                            ps.setInt(3, skill.getValue().skillevel);
                            ps.setByte(4, skill.getValue().masterlevel);
                            ps.setLong(5, skill.getValue().expiration);
                            ps.execute();
                        }
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }

            // Save VMatrix (KAZ SAYS THIS DOESNT LOOP RIGHT NIGGA)
            deleteWhereCharacterId(con, "DELETE FROM vmatrix WHERE characterid = ?");
            try (PreparedStatement ps = con.prepareStatement("INSERT INTO vmatrix (characterid, state, coreid, skillid, skillid2, skillid3, level, masterlevel, experience) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
                ps.setInt(1, id);
                for (VMatrixRecord pRecord : aVMatrixRecord) {
                    ps.setBoolean(2, pRecord.nState == VMatrixRecord.Active);
                    ps.setInt(3, pRecord.nCoreID);
                    ps.setInt(4, pRecord.nSkillID);
                    ps.setInt(5, pRecord.nSkillID2);
                    ps.setInt(6, pRecord.nSkillID3);
                    ps.setInt(7, pRecord.nSLV);
                    ps.setInt(8, pRecord.nMasterLev);
                    ps.setInt(9, pRecord.nExp);
                    ps.execute();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }

            // Save Phantom Stolen Skills
            deleteWhereCharacterId(con, "DELETE FROM stealskill WHERE dwCharacterID = ?");
            try (PreparedStatement ps = con.prepareStatement("INSERT INTO stealskill (dwCharacterID, nSlotSkillID, nStealSkillID, nSlotID, nPOS, bSet) " + "VALUES (?, ?, ?, ?, ?, ?)")) {
                ps.setInt(1, id);
                for (int i = 1; i <= 5; i++) {
                    for (int j = 0; j < GameConstants.getNumSteal(i); j++) {
                        int nSlotSkillID = 0;
                        switch (i) {
                            case 1:
                                nSlotSkillID = 24001001;
                                break;
                            case 2:
                                nSlotSkillID = 24101001;
                                break;
                            case 3:
                                nSlotSkillID = 24111001;
                                break;
                            case 4:
                                nSlotSkillID = 24121001;
                                break;
                            case 5:
                                nSlotSkillID = 24121054;
                                break;
                        }
                        int nStealSkillID = aStealMemory[i][j];
                        int nSlotID = i;
                        int nPOS = j;
                        boolean bSet = false;
                        if (mStealSkillInfo.get(nSlotSkillID) == nStealSkillID) {
                            bSet = true;
                        }

                        //System.out.printf("[Phantom Save Debug] nSlotSkillID: %d / nStealSkillID: %d / nSlotID: %d / nPOS: %d", nSlotSkillID, nStealSkillID, nSlotID, nPOS);
                        ps.setInt(2, nSlotSkillID);
                        ps.setInt(3, nStealSkillID);
                        ps.setInt(4, nSlotID);
                        ps.setInt(5, nPOS);
                        ps.setInt(6, bSet ? 1 : 0);
                        ps.execute();
                    }
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }

            //TODO {TEST} SAVE NEW JETT CORE AURA
            deleteWhereCharacterId(con, "DELETE FROM coreauras WHERE cid = ?");
            try (PreparedStatement ps = con.prepareStatement("INSERT INTO coreauras (cid, str, dex, `int`, luk, att, magic, total, expire) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
                ps.setInt(1, id);
                ps.setInt(2, getCoreAura().getStr());
                ps.setInt(3, getCoreAura().getDex());
                ps.setInt(4, getCoreAura().getInt());
                ps.setInt(5, getCoreAura().getLuk());
                ps.setInt(6, getCoreAura().getAtt());
                ps.setInt(7, getCoreAura().getMagic());
                ps.setInt(8, getCoreAura().getTotal());
                ps.setInt(9, getCoreAura().getExpire());
                ps.execute();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }

            if (innerskill_changed) {
                if (innerSkills != null) {
                    deleteWhereCharacterId(con, "DELETE FROM inner_ability_skills WHERE player_id = ?");
                    try (PreparedStatement ps = con.prepareStatement("INSERT INTO inner_ability_skills (player_id, skill_id, skill_level, max_level, rank, locked) VALUES (?, ?, ?, ?, ?, ?)")) {
                        ps.setInt(1, id);

                        for (int i = 0; i < innerSkills.size(); ++i) {
                            ps.setInt(2, innerSkills.get(i).getSkillId());
                            ps.setInt(3, innerSkills.get(i).getSkillLevel());
                            ps.setInt(4, innerSkills.get(i).getMaxLevel());
                            ps.setInt(5, innerSkills.get(i).getRank());
                            ps.setBoolean(6, innerSkills.get(i).isLocked());
                            ps.executeUpdate();
                        }
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                }
            }

            List<MapleCoolDownValueHolder> cd = getCooldowns();
            if (dc && cd.size() > 0) {
                try (PreparedStatement ps = con.prepareStatement("INSERT INTO skills_cooldowns (charid, SkillID, StartTime, length) VALUES (?, ?, ?, ?)")) {
                    ps.setInt(1, getId());
                    for (final MapleCoolDownValueHolder cooling : cd) {
                        ps.setInt(2, cooling.skillId);
                        ps.setLong(3, cooling.startTime);
                        ps.setLong(4, cooling.length);
                        ps.execute();
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }

            if (changed_savedlocations) {
                deleteWhereCharacterId(con, "DELETE FROM savedlocations WHERE characterid = ?");
                try (PreparedStatement ps = con.prepareStatement("INSERT INTO savedlocations (characterid, `locationtype`, `map`) VALUES (?, ?, ?)")) {
                    ps.setInt(1, id);
                    for (final SavedLocationType savedLocationType : SavedLocationType.values()) {
                        if (savedLocations[savedLocationType.getValue()] != -1) {
                            ps.setInt(2, savedLocationType.getValue());
                            ps.setInt(3, savedLocations[savedLocationType.getValue()]);
                            ps.execute();
                        }
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }

            if (changed_achievements) {
                try (PreparedStatement ps = con.prepareStatement("DELETE FROM achievements WHERE accountid = ?")) {
                    ps.setInt(1, accountid);
                    ps.executeUpdate();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }

                try (PreparedStatement ps = con.prepareStatement("INSERT INTO achievements(charid, achievementid, accountid) VALUES(?, ?, ?)")) {
                    for (Integer achid : finishedAchievements) {
                        ps.setInt(1, id);
                        ps.setInt(2, achid);
                        ps.setInt(3, accountid);
                        ps.execute();
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }

            if (changed_reports) {
                deleteWhereCharacterId(con, "DELETE FROM reports WHERE characterid = ?");
                try (PreparedStatement ps = con.prepareStatement("INSERT INTO reports VALUES(DEFAULT, ?, ?, ?)")) {
                    for (Entry<ReportType, Integer> achid : reports.entrySet()) {
                        ps.setInt(1, id);
                        ps.setByte(2, achid.getKey().i);
                        ps.setInt(3, achid.getValue());
                        ps.execute();
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }

            if (buddylist.changed()) {
                deleteWhereCharacterId(con, "DELETE FROM buddies WHERE characterid = ?");
                try (PreparedStatement ps = con.prepareStatement("INSERT INTO buddies (characterid, `buddyid`, `pending`, `groupname`, `memo`, `flag`, `friend`) VALUES (?, ?, ?, ?, ?, ?, ?)")) {
                    ps.setInt(1, id);
                    for (BuddylistEntry entry : buddylist.getBuddies()) {
                        ps.setInt(2, entry.getCharacterId());
                        ps.setBoolean(3, entry.isPending());
                        ps.setString(4, entry.getGroup());
                        ps.setString(5, entry.getMemo());
                        ps.setInt(6, entry.getFlag());
                        ps.setBoolean(7, entry.isAccountFriend());
                        ps.execute();
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
                buddylist.setChanged(false);
            }

            try (PreparedStatement ps = con.prepareStatement("UPDATE accounts SET `nxCredit` = ?, `ACash` = ?, `mPoints` = ?, `points` = ?, `vpoints` = ?, `dpoints` = ?, `epoints` = ? WHERE id = ?")) {
                ps.setInt(1, nxcredit);
                ps.setInt(2, acash);
                ps.setInt(3, maplepoints);
                ps.setInt(4, points);
                ps.setInt(5, vpoints);
                ps.setInt(6, dpoints);
                ps.setInt(7, epoints);
                ps.setInt(8, client.getAccID());
                ps.executeUpdate();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }

            if (storage != null) {
                storage.saveToDB(con);
            }
            if (cs != null) {
                cs.save(con);
            }
            if (PlayerNPC.Auto_Update) {
                PlayerNPC.updateByCharId(this);
            }
            keylayout.saveKeys(id); //CHECK
            mount.saveMount(id); //CHECK
            monsterbook.saveCards(accountid); //CHECK

            deleteWhereCharacterId(con, "DELETE FROM familiars WHERE characterid = ?");
            try (PreparedStatement ps = con.prepareStatement("INSERT INTO familiars (characterid, expiry, name, fatigue, vitality, familiar) VALUES (?, ?, ?, ?, ?, ?)")) {
                ps.setInt(1, id);
                for (MonsterFamiliar f : familiars.values()) {
                    ps.setLong(2, f.getExpiry());
                    ps.setString(3, f.getName());
                    ps.setInt(4, f.getFatigue());
                    ps.setByte(5, f.getVitality());
                    ps.setInt(6, f.getFamiliar());
                    ps.executeUpdate();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }

            deleteWhereCharacterId(con, "DELETE FROM imps WHERE characterid = ?");
            try (PreparedStatement ps = con.prepareStatement("INSERT INTO imps (characterid, itemid, closeness, fullness, state, level) VALUES (?, ?, ?, ?, ?, ?)")) {
                ps.setInt(1, id);
                for (MapleImp imp : imps) {
                    if (imp != null) {
                        ps.setInt(2, imp.getItemId());
                        ps.setShort(3, imp.getCloseness());
                        ps.setShort(4, imp.getFullness());
                        ps.setByte(5, imp.getState());
                        ps.setByte(6, imp.getLevel());
                        ps.executeUpdate();
                    }
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }

            if (changed_wishlist) {
                deleteWhereCharacterId(con, "DELETE FROM wishlist WHERE characterid = ?");
                for (int i = 0; i < getWishlistSize(); i++) {
                    try (PreparedStatement ps = con.prepareStatement("INSERT INTO wishlist(characterid, sn) VALUES(?, ?) ")) {
                        ps.setInt(1, getId());
                        ps.setInt(2, wishlist[i]);
                        ps.execute();
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                }
            }
            if (changed_trocklocations) {
                deleteWhereCharacterId(con, "DELETE FROM trocklocations WHERE characterid = ?");
                for (int i = 0; i < rocks.length; i++) {
                    if (rocks[i] != 999999999) {
                        try (PreparedStatement ps = con.prepareStatement("INSERT INTO trocklocations(characterid, mapid) VALUES(?, ?) ")) {
                            ps.setInt(1, getId());
                            ps.setInt(2, rocks[i]);
                            ps.execute();
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            }

            if (changed_regrocklocations) {
                deleteWhereCharacterId(con, "DELETE FROM regrocklocations WHERE characterid = ?");
                for (int i = 0; i < regrocks.length; i++) {
                    if (regrocks[i] != 999999999) {
                        try (PreparedStatement ps = con.prepareStatement("INSERT INTO regrocklocations(characterid, mapid) VALUES(?, ?) ")) {
                            ps.setInt(1, getId());
                            ps.setInt(2, regrocks[i]);
                            ps.execute();
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            }
            if (changed_hyperrocklocations) {
                deleteWhereCharacterId(con, "DELETE FROM hyperrocklocations WHERE characterid = ?");
                for (int i = 0; i < hyperrocks.length; i++) {
                    if (hyperrocks[i] != 999999999) {
                        try (PreparedStatement ps = con.prepareStatement("INSERT INTO hyperrocklocations(characterid, mapid) VALUES(?, ?) ")) {
                            ps.setInt(1, getId());
                            ps.setInt(2, hyperrocks[i]);
                            ps.execute();
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            }
            if (changed_extendedSlots) {
                deleteWhereCharacterId(con, "DELETE FROM extendedSlots WHERE characterid = ?");
                for (int i : extendedSlots) {
                    if (getInventory(MapleInventoryType.ETC).findById(i) != null) { //just in case
                        try (PreparedStatement ps = con.prepareStatement("INSERT INTO extendedSlots(characterid, itemId) VALUES(?, ?) ")) {
                            ps.setInt(1, getId());
                            ps.setInt(2, i);
                            ps.execute();
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            }

            changed_wishlist = false;
            changed_trocklocations = false;
            changed_regrocklocations = false;
            changed_hyperrocklocations = false;
            changed_skillmacros = false;
            changed_savedlocations = false;
            changedQuestInfo = false;
            changed_achievements = false;
            changed_extendedSlots = false;
            changed_skills = false;
            changed_reports = false;
        } catch (SQLException e) {
            LogHelper.SQL.get().info("Could not save character information:\n{}", e);
        }
    }

    private void deleteWhereCharacterId(Connection con, String sql) {
        deleteWhereCharacterId(con, sql, id);
    }

    public static void deleteWhereCharacterId(Connection con, String sql, int id) {
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public static void deleteWhereCharacterId_NoLock(Connection con, String sql, int id) {
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.execute();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void saveInventory(Connection con) {
        List<Pair<Item, MapleInventoryType>> listing = new ArrayList<>();
        for (final MapleInventory iv : inventory) {
            for (final Item item : iv.list()) {
                listing.add(new Pair<>(item, iv.getType()));
            }
        }
        ItemLoader.INVENTORY.saveItems(listing, id, con);
    }

    public final PlayerStats getStat() {
        return stats;
    }

    public final void addQuestInfoExPacket(OutPacket oPacket) {
        oPacket.EncodeShort(questinfo.size()); // // Party Quest data (quest needs to be added in the quests list)

        for (final Entry<Integer, String> q : questinfo.entrySet()) {
            oPacket.EncodeInt(q.getKey());
            oPacket.EncodeString(q.getValue() == null ? "" : q.getValue());
        }
    }

    public final void updateInfoQuest(int questId, String data) {
        questinfo.put(questId, data);
        changedQuestInfo = true;
        UpdateQuestMessage quest = new UpdateQuestMessage(questId, data);
        write(CWvsContext.messagePacket(quest));
    }

    public final String getInfoQuest(final int questid) {
        if (questinfo.containsKey(questid)) {
            return questinfo.get(questid);
        }
        return "";
    }

    public final int getNumQuest() {
        int i = 0;
        i = quests.values().stream().filter((q) -> (q.getStatus() == MapleQuestState.Completed)).map((_item) -> 1).reduce(i, Integer::sum);

        return i;
    }

    public final MapleQuestState getQuestStatus(final int quest) {
        final MapleQuest qq = MapleQuest.getInstance(quest);
        if (getQuestNoAdd(qq) == null) {
            return MapleQuestState.NotStarted;
        }
        return getQuestNoAdd(qq).getStatus();
    }

    public final MapleQuestStatus getQuest(final MapleQuest quest) {
        if (!quests.containsKey(quest)) {
            return new MapleQuestStatus(quest, MapleQuestState.NotStarted);
        }
        return quests.get(quest);
    }

    public final void setQuestAdd(final MapleQuest quest, final MapleQuestState status, final String customData) {
        if (!quests.containsKey(quest)) {
            final MapleQuestStatus stat = new MapleQuestStatus(quest, status);
            stat.setCustomData(customData);
            quests.put(quest, stat);
        }
    }

    public final MapleQuestStatus getQuestNAdd(final MapleQuest quest) {
        if (!quests.containsKey(quest)) {
            final MapleQuestStatus status = new MapleQuestStatus(quest, MapleQuestState.NotStarted);
            quests.put(quest, status);
            return status;
        }
        return quests.get(quest);
    }

    public final MapleQuestStatus getQuestNoAdd(final MapleQuest quest) {
        return quests.get(quest);
    }

    public final MapleQuestStatus getQuestRemove(final MapleQuest quest) {
        return quests.remove(quest);
    }

    public final void updateQuest(final MapleQuestStatus quest) {
        updateQuest(quest, false);
    }

    public final void updateQuest(final MapleQuestStatus quest, final boolean update) {
        quests.put(quest.getQuest(), quest);
        QuestStatusMessage questMessage = new QuestStatusMessage(quest);

        client.SendPacket(CWvsContext.messagePacket(questMessage));
        if (quest.getStatus() == MapleQuestState.Started && !update) {
            client.SendPacket(CField.updateQuestInfo(this, quest.getQuest().getId(), quest.getNpc(), (byte) 11));//was10
        }
    }

    public final Map<Integer, String> getInfoQuestMap() {
        return questinfo;
    }

    public final Map<MapleQuest, MapleQuestStatus> getQuestMap() {
        return quests;
    }

    //region Buffs
    /**
     * Gets an unmodifiable list of buffed stat value applied to the character.
     *
     * @return Set<Map.Entry<CharacterTemporaryStat, CharacterTemporaryStatValueHolder>>
     */
    public List<Map.Entry<CharacterTemporaryStat, CharacterTemporaryStatValueHolder>> getBuffedValuesPlayerStats() {
        return effects.entrySet().stream().collect(Collectors.toList());
    }

    public boolean hasBuff(CharacterTemporaryStat effect) {
        final CharacterTemporaryStatValueHolder buffStat = effects.get(effect);
        if (buffStat != null) {
            return true;
        }
        return false;
    }

    public Integer getBuffedValue(CharacterTemporaryStat effect) {
        final CharacterTemporaryStatValueHolder mbsvh = effects.get(effect);
        if (mbsvh == null) {
            return null;
        }
        return mbsvh.value;
    }

    public CharacterTemporaryStatValueHolder getAppliedTemporaryStat(CharacterTemporaryStat effect) {
        final CharacterTemporaryStatValueHolder mbsvh = effects.get(effect);
        if (mbsvh == null) {
            return null;
        }
        return mbsvh;
    }

    public final Integer getBuffedSkillX(final CharacterTemporaryStat effect) {
        final CharacterTemporaryStatValueHolder mbsvh = effects.get(effect);
        if (mbsvh == null) {
            return null;
        }
        return mbsvh.effect.getX();
    }

    public final Integer getBuffedSkillY(final CharacterTemporaryStat effect) {
        final CharacterTemporaryStatValueHolder mbsvh = effects.get(effect);
        if (mbsvh == null) {
            return null;
        }
        return mbsvh.effect.getY();
    }

    public boolean isBuffFrom(CharacterTemporaryStat stat, Skill skill) {
        final CharacterTemporaryStatValueHolder mbsvh = effects.get(stat);
        if (mbsvh == null || mbsvh.effect == null) {
            return false;
        }
        return mbsvh.effect.getSourceId() == skill.getId();
    }

    public int getBuffSource(CharacterTemporaryStat stat) {
        final CharacterTemporaryStatValueHolder mbsvh = effects.get(stat);
        return mbsvh == null ? -1 : mbsvh.effect.getSourceId();
    }

    public int getTrueBuffSource(CharacterTemporaryStat stat) {
        final CharacterTemporaryStatValueHolder mbsvh = effects.get(stat);
        return mbsvh == null ? -1 : mbsvh.effect.getSourceId();
    }

    public int getItemQuantity(int itemid, boolean checkEquipped) {
        int possesed = inventory[GameConstants.getInventoryType(itemid).ordinal()].countById(itemid);
        if (checkEquipped) {
            possesed += inventory[MapleInventoryType.EQUIPPED.ordinal()].countById(itemid);
        }
        return possesed;
    }

    //Overwrites the value of an already registered TemporaryStat
    public void setBuffedValue(CharacterTemporaryStat effect, int value) {
        final CharacterTemporaryStatValueHolder mbsvh = effects.get(effect);
        if (mbsvh == null) {
            return;
        }
        mbsvh.value = value;
    }

    public void setSchedule(CharacterTemporaryStat effect, ScheduledFuture<?> sched) {
        final CharacterTemporaryStatValueHolder mbsvh = effects.get(effect);
        if (mbsvh == null) {
            return;
        }
        mbsvh.schedule.cancel(false);
        mbsvh.schedule = sched;
    }

    public Long getBuffedStarttime(CharacterTemporaryStat effect) {
        final CharacterTemporaryStatValueHolder mbsvh = effects.get(effect);
        return mbsvh == null ? null : mbsvh.startTime;
    }

    public int getBuffDuration(CharacterTemporaryStat effect) {
        final CharacterTemporaryStatValueHolder mbsvh = effects.get(effect);
        return mbsvh == null ? null : mbsvh.localDuration;
    }

    public MapleStatEffect getStatForBuff(CharacterTemporaryStat effect) {
        final CharacterTemporaryStatValueHolder mbsvh = effects.get(effect);
        return mbsvh == null ? null : mbsvh.effect;
    }

    public void registerEffect(MapleStatEffect effect, long starttime, ScheduledFuture<?> schedule, int from) {
        registerEffect(effect, starttime, schedule, effect.getStatups(), false, effect.getDuration(), from);
    }

    public void registerEffect(MapleStatEffect effect, long starttime, ScheduledFuture<?> schedule, Map<CharacterTemporaryStat, Integer> statups, boolean silent, final int localDuration, final int cid) {
        if (statups.isEmpty() && (!GameConstants.isNotBuffSkill(effect.getSourceId()))) { //
            if (ServerConstants.DEVELOPER_DEBUG_MODE) {
                LogHelper.GENERAL_EXCEPTION.get().fatal("[WARNING] The buff " + effect.getSourceId() + " has no effect values! Please define the statups in the BuffClass. (Server.buffs.XXX, create if not exists.)");
            }
        }
        if (effect.isHide()) {
            map.broadcastNONGMMessage(this, CField.removePlayerFromMap(getId()), false);
        } else if (effect.isDragonBlood()) {
            prepareDragonBlood();
        } else if (effect.isRecovery()) {
            prepareRecovery();
        } else if (effect.isBerserk()) {
            checkBerserk();
        } else if (effect.statups.containsKey(CharacterTemporaryStat.RideVehicle)) {
            getMount().startSchedule();
        }

        for (Entry<CharacterTemporaryStat, Integer> statup : statups.entrySet()) {
            int value = statup.getValue();
            if (statup.getKey() == CharacterTemporaryStat.RideVehicle) {
                removeFamiliar();
            }
            try {
                effects.put(statup.getKey(), new CharacterTemporaryStatValueHolder(effect, starttime, schedule, value, localDuration, cid));
            } catch (Exception ex) {
                if (ServerConstants.DEVELOPER_DEBUG_MODE) {
                    LogHelper.GENERAL_EXCEPTION.get().fatal("[WARNING] Unable to apply the buff effect: \r\n" + ex.toString() + "\r\n");
                }
            }
        }
        if (!silent) {
            stats.recalcLocalStats(this);
        }
    }

    public List<CharacterTemporaryStat> getTemporaryStats(final MapleStatEffect effect, final long startTime) {
        List<CharacterTemporaryStat> liStats = new ArrayList<>();

        effects.entrySet().stream()
                .filter(stat_Effect -> stat_Effect.getValue().effect.sameSource(effect)
                && (startTime == -1 || startTime == stat_Effect.getValue().startTime))
                .forEach(stat_Effect -> liStats.add(stat_Effect.getKey()));

        return liStats;
    }

    private boolean deregisterTemporaryStats(List<CharacterTemporaryStat> stats) {
        boolean clonez = false;
        List<CharacterTemporaryStatValueHolder> effectsToCancel = new ArrayList<>(stats.size());
        for (CharacterTemporaryStat stat : stats) {
            final CharacterTemporaryStatValueHolder mbsvh = effects.remove(stat);
            if (mbsvh != null) {
                boolean addMbsvh = true;
                for (CharacterTemporaryStatValueHolder contained : effectsToCancel) {
                    if (mbsvh.startTime == contained.startTime && contained.effect == mbsvh.effect) {
                        addMbsvh = false;
                    }
                }
                if (addMbsvh) {
                    effectsToCancel.add(mbsvh);
                }
                if (stat == CharacterTemporaryStat.SUMMON || stat == CharacterTemporaryStat.PUPPET || stat == CharacterTemporaryStat.REAPER || stat == CharacterTemporaryStat.Beholder || stat == CharacterTemporaryStat.DamR || stat == CharacterTemporaryStat.IndiePAD) {
                    final int summonId = mbsvh.effect.getSourceId();
                    final List<Summon> toRemove = new ArrayList<>();
                    visibleMapObjectsLock.writeLock().lock(); //We need to lock this later on anyway so do it now to prevent deadlocks.
                    summonsLock.writeLock().lock();
                    try {
                        for (Summon summon : summons) {
                            if (summon.getSkill() == summonId || (summonId == 35121009 && summon.getSkill() == 35121011) || ((summonId == 86 || summonId == 88 || summonId == 91 || summonId == 180 || summonId == 96) && summon.getSkill() == summonId + 999) || ((summonId == 1085 || summonId == 1087 || summonId == 1090 || summonId == 1179 || summonId == 1154) && summon.getSkill() == summonId - 999)) { //removes bots n tots
                                map.broadcastMessage(SummonPacket.removeSummon(summon, true));
                                map.removeMapObject(summon);
                                visibleMapObjects.remove(summon);
                                toRemove.add(summon);
                            }
                        }
                        for (Summon s : toRemove) {
                            summons.remove(s);
                        }
                    } finally {
                        summonsLock.writeLock().unlock();
                        visibleMapObjectsLock.writeLock().unlock(); //lolwut
                    }
                    if (summonId == 3111005 || summonId == 3211005) {
                        cancelEffectFromTemporaryStat(CharacterTemporaryStat.SpiritLink);
                    }
                    //} else if (stat == CharacterTemporaryStat.DRAGONBLOOD) {
                    //  lastDragonBloodTime = 0;
                } else if (stat == CharacterTemporaryStat.Regen || mbsvh.effect.getSourceId() == 35121005) {
                    lastRecoveryTime = 0;
                    //} else if (stat == CharacterTemporaryStat.HOMING_BEACON || stat == CharacterTemporaryStat.MANY_USES) {
                    //  linkMobs.clear();
                } else if (stat == CharacterTemporaryStat.IllusionStep) {
                    disposeClones();
                    clonez = true;
                }
            }
        }
        effectsToCancel.stream()
                .filter((cancelEffectCancelTasks) -> getTemporaryStats(cancelEffectCancelTasks.effect, cancelEffectCancelTasks.startTime).isEmpty() && cancelEffectCancelTasks.schedule != null)
                .forEach((cancelEffectCancelTasks) -> {
                    cancelEffectCancelTasks.schedule.cancel(false);
                });
        return clonez;
    }

    /**
     * @param effect
     * @param overwrite when overwrite is set no data is sent and all the Buffstats in the StatEffect are deregistered
     * @param startTime
     */
    public void cancelEffect(final MapleStatEffect effect, final boolean overwrite, final long startTime) {
        if (effect == null) {
            return;
        }
        cancelEffect(effect, overwrite, startTime, effect.getStatups());
    }

    public void cancelEffect(final MapleStatEffect effect, final boolean overwrite, final long startTime, Map<CharacterTemporaryStat, Integer> statups) {
        if (effect == null) {
            return;
        }

        List<CharacterTemporaryStat> buffstats;
        if (!overwrite) {
            buffstats = getTemporaryStats(effect, startTime);
        } else {
            buffstats = new ArrayList<>(statups.keySet());
        }
        if (buffstats.size() <= 0) {
            //System.out.println("\n\n\nRIPRIPRIP\n\n\n");
            return;
        }
        if (effect.isInfinity() && getBuffedValue(CharacterTemporaryStat.Infinity) != null) { //before
            int duration = Math.max(effect.getDuration(), effect.alchemistModifyVal(this, effect.getDuration(), false));
            final long start = getBuffedStarttime(CharacterTemporaryStat.Infinity);
            duration += (int) ((start - System.currentTimeMillis()));
            if (duration > 0) {
                final int neworbcount = getBuffedValue(CharacterTemporaryStat.Infinity) + effect.getDamage();
                final Map<CharacterTemporaryStat, Integer> stat = new EnumMap<>(CharacterTemporaryStat.class
                );
                stat.put(CharacterTemporaryStat.Infinity, neworbcount);
                setBuffedValue(CharacterTemporaryStat.Infinity, neworbcount);
                client.SendPacket(BuffPacket.giveBuff(this, effect.getSourceId(), duration, stat, effect));
                addHP((int) (effect.getHpR() * this.stats.getCurrentMaxHp()));
                addMP((int) (effect.getMpR() * this.stats.getCurrentMaxMp(this.getJob())));
                setSchedule(CharacterTemporaryStat.Infinity, BuffTimer.getInstance().schedule(new CancelEffectAction(this, effect, start, stat), effect.alchemistModifyVal(this, 4000, false)));
                return;
            } else if ((effect.getSourceId() == 15001022 || effect.getSourceId() == 27121005) && !overwrite) {
                acaneAim = 0;
            }
        }

        final boolean clonez = deregisterTemporaryStats(buffstats);

        if (effect.isMagicDoor()) {
            // remove for all on maps
            if (!getDoors().isEmpty()) {
                removeDoor();
                silentPartyUpdate();
            }
        } else if (effect.isMechDoor()) {
            if (!getMechDoors().isEmpty()) {
                removeMechDoor();
            }
        } else if (effect.statups.containsKey(CharacterTemporaryStat.RideVehicle)) {
            getMount().cancelSchedule();
        } else if (effect.isAranCombo()) {
            combo = 0;
        }

        this.summons.forEach((summon) -> {
            if (summon.getSkill() == effect.getSourceId()) {
                summon.sendDestroyData(client);
                this.removeSummon(summon);
            }
        });

        // check if we are still logged in o.o
        cancelPlayerBuffs(buffstats, overwrite);
        if (!overwrite) {
            if (effect.isHide() && client.getChannelServer().getPlayerStorage().getCharacterById(this.getId()) != null) { //Wow this is so fking hacky...
                map.broadcastMessage(this, CField.spawnPlayerMapObject(this), false);

                for (final Pet pet : pets) {
                    if (pet.getSummoned()) {
                        map.broadcastMessage(this, PetPacket.OnActivated(this.id, getPetIndex(pet), true, pet, 0), true);
                    }
                }

                for (final WeakReference<User> chr : clones) {
                    if (chr.get() != null) {
                        map.broadcastMessage(chr.get(), CField.spawnPlayerMapObject(chr.get()), false);
                    }
                }
            }
        }
        if (effect.getSourceId() == 35121013 && !overwrite) { //when siege 2 deactivates, missile re-activates
            SkillFactory.getSkill(35121005).getEffect(getTotalSkillLevel(35121005)).applyTo(this);
        }
        if (!clonez) {
            for (WeakReference<User> chr : clones) {
                if (chr.get() != null) {
                    chr.get().cancelEffect(effect, overwrite, startTime);
                }
            }
        }
    }

    public void cancelTemporaryStats(CharacterTemporaryStat... stat) {
        List<CharacterTemporaryStat> buffStatList = Arrays.asList(stat);
        deregisterTemporaryStats(buffStatList);
        cancelPlayerBuffs(buffStatList, false);
    }

    public void cancelEffectFromTemporaryStat(CharacterTemporaryStat stat) {
        if (effects.containsKey(stat)) {
            final CharacterTemporaryStatValueHolder bValue = effects.get(stat);
            if (bValue != null) // double check once again, avoid racing condition... dont want to lock here
            {
                cancelEffect(bValue.effect, false, -1);
            }
        }
    }

    public void cancelEffectFromTemporaryStat(CharacterTemporaryStat stat, int from) {
        if (effects.containsKey(stat)) {
            final CharacterTemporaryStatValueHolder bValue = effects.get(stat);
            if (bValue != null) { // double check once again, avoid racing condition... dont want to lock here
                if (bValue.cid == from) {
                    cancelEffect(bValue.effect, false, -1);
                }
            }
        }
    }

    public boolean containsBuffstatEffect(CharacterTemporaryStat stat) {
        return effects.containsKey(stat);
    }

    private void cancelPlayerBuffs(List<CharacterTemporaryStat> buffstats, boolean overwrite) {
        try {
            boolean write = client != null && client.getChannelServer() != null && client.getChannelServer().getPlayerStorage().getCharacterById(getId()) != null;
            if (overwrite) {
                List<CharacterTemporaryStat> z = new ArrayList<>();
                for (CharacterTemporaryStat s : buffstats) {
                    if (s.isIndie()) {
                        z.add(s);
                    }
                }
                if (z.size() > 0) {
                    System.out.println("Size : " + z.size() + " / Buffs" + z.toString());
                    buffstats = z;
                } else {
                    return; //don't write anything
                }
            } else if (write) {
                stats.recalcLocalStats(this);
            }
            client.SendPacket(BuffPacket.cancelBuff(buffstats));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void dispel() {
        if (!isHidden()) {
            effects.values().stream()
                    .filter(mbsvh -> mbsvh.schedule != null
                    && !mbsvh.effect.statups.containsKey(CharacterTemporaryStat.Morph)
                    && !mbsvh.effect.isGmBuff()
                    && !mbsvh.effect.statups.containsKey(CharacterTemporaryStat.RideVehicle)
                    && !mbsvh.effect.isMechChange()
                    && !mbsvh.effect.isEnergyCharged()
                    && !mbsvh.effect.isAranCombo())
                    .forEach(mbsvh -> cancelEffect(mbsvh.effect, false, mbsvh.startTime));
        }
    }

    public void dispelSkill(int skillid) {
        final Optional<CharacterTemporaryStatValueHolder> firstDispelledSkill = effects.values().stream()
                .filter(mbsvh -> mbsvh.effect.getSourceId() == skillid)
                .findFirst();

        if (firstDispelledSkill.isPresent()) {
            CharacterTemporaryStatValueHolder mbsvh = firstDispelledSkill.get();
            cancelEffect(mbsvh.effect, false, mbsvh.startTime);
        }
        if (skillid == 4221054) {
            acaneAim = 0;
            dualBrid = 0;
        }
    }

    public void dispelSummons() {
        effects.values().stream()
                .filter(mbsvh -> mbsvh.effect.getSummonMovementType() != null)
                .forEach(mbsvh -> cancelEffect(mbsvh.effect, false, mbsvh.startTime));
    }

    public void dispelBuff(int skillid) {
        final Optional<CharacterTemporaryStatValueHolder> firstDispelledBuff = effects.values().stream()
                .filter(mbsvh -> mbsvh.effect.getSourceId() == skillid)
                .findFirst();

        if (firstDispelledBuff.isPresent()) {
            CharacterTemporaryStatValueHolder mbsvh = firstDispelledBuff.get();
            cancelEffect(mbsvh.effect, false, mbsvh.startTime);
        }
    }

    public void cancelBuffs() {
        effects.clear();
    }

    public void cancelAllBuffs() {
        effects.values().stream().forEach((mbsvh) -> {
            cancelEffect(mbsvh.effect, false, mbsvh.startTime);
        });
    }

    public void cancelMorphs() {
        List<CharacterTemporaryStatValueHolder> morphsToCancel = effects.values().stream()
                .filter(mbsvh -> GameConstants.isDispellableMorph(mbsvh.effect.getSourceId())
                && mbsvh.effect.statups.containsKey(CharacterTemporaryStat.Morph))
                .collect(Collectors.toList());
        for (CharacterTemporaryStatValueHolder mbsvh : morphsToCancel) {
            disposeClones();
            cancelEffect(mbsvh.effect, false, mbsvh.startTime);
        }
    }

    public int getMorphState() {
        final Optional<CharacterTemporaryStatValueHolder> morph
                = effects.values().stream().filter(buff
                        -> buff.effect.statups.containsKey(CharacterTemporaryStat.Morph)
                ).findFirst();
        if (morph.isPresent()) {
            return morph.get().effect.getSourceId();
        }
        return -1;
    }

    public void silentGiveBuffs(List<PlayerBuffValueHolder> buffs) {
        if (buffs == null) {
            return;
        }
        buffs.stream().forEach((mbsvh) -> {
            mbsvh.effect.silentApplyBuff(this, mbsvh.startTime, mbsvh.localDuration, mbsvh.statup, mbsvh.cid);
        });
    }

    public List<PlayerBuffValueHolder> getAllBuffs() {
        final List<PlayerBuffValueHolder> ret = new ArrayList<>();
        final Map<Pair<Integer, Byte>, Integer> alreadyDone = new HashMap<>();

        effects.entrySet().stream().forEach((mbsvh) -> {
            final Pair<Integer, Byte> key = new Pair<>(mbsvh.getValue().effect.getSourceId(), mbsvh.getValue().effect.getLevel());
            if (alreadyDone.containsKey(key)) {
                ret.get(alreadyDone.get(key)).statup.put(mbsvh.getKey(), mbsvh.getValue().value);
            } else {
                alreadyDone.put(key, ret.size());
                final EnumMap<CharacterTemporaryStat, Integer> list = new EnumMap<>(CharacterTemporaryStat.class
                );
                list.put(mbsvh.getKey(), mbsvh.getValue().value);
                ret.add(new PlayerBuffValueHolder(mbsvh.getValue().startTime, mbsvh.getValue().effect, list, mbsvh.getValue().localDuration, mbsvh.getValue().cid));
            }
        });
        return ret;
    }

    public void cancelMagicDoor() {
        effects.values().stream()
                .filter(mbsvh -> mbsvh.effect.isMagicDoor())
                .forEach(mbsvh -> cancelEffect(mbsvh.effect, false, mbsvh.startTime));
    }
    //endregion

    public void doDragonBlood() {
        /*
        final MapleStatEffect bloodEffect = getStatForBuff(CharacterTemporaryStat.DRAGONBLOOD);
        if (bloodEffect == null) {
            lastDragonBloodTime = 0;
            return;
        }
        prepareDragonBlood();
        if (stats.getHp() - bloodEffect.getX() <= 1) {
            cancelTemporaryStats(CharacterTemporaryStat.DRAGONBLOOD);

        } else {
            addHP(-bloodEffect.getX());
            client.write(EffectPacket.showOwnBuffEffect(bloodEffect.getSourceId(), UserEffectCodes.SkillSpecial, getLevel(), bloodEffect.getLevel()));
            map.broadcastMessage(MapleCharacter.this, EffectPacket.showBuffeffect(getId(), bloodEffect.getSourceId(), UserEffectCodes.SkillSpecial, getLevel(), bloodEffect.getLevel()), false);
        }
         */
    }

    public final boolean canBlood(long now) {
        return lastDragonBloodTime > 0 && lastDragonBloodTime + 4000 < now;
    }

    private void prepareDragonBlood() {
        lastDragonBloodTime = System.currentTimeMillis();
    }

    public void doRecovery() {
        MapleStatEffect bloodEffect = getStatForBuff(CharacterTemporaryStat.Regen);
        if (bloodEffect == null) {
            bloodEffect = getStatForBuff(CharacterTemporaryStat.Mechanic);
            if (bloodEffect == null) {
                lastRecoveryTime = 0;
            } else if (bloodEffect.getSourceId() == 35121005) {
                prepareRecovery();
                if (stats.getMp() < bloodEffect.getU()) {
                    cancelEffectFromTemporaryStat(CharacterTemporaryStat.RideVehicle);
                    cancelEffectFromTemporaryStat(CharacterTemporaryStat.Mechanic);
                } else {
                    addMP(-bloodEffect.getU());
                }
            }
        } else {
            prepareRecovery();
            if (stats.getHp() >= stats.getCurrentMaxHp()) {
                cancelEffectFromTemporaryStat(CharacterTemporaryStat.Regen);
            } else {
                healHP(bloodEffect.getX());
            }
        }
    }

    public final boolean canRecover(long now) {
        return lastRecoveryTime > 0 && lastRecoveryTime + 5000 < now;
    }

    private void prepareRecovery() {
        lastRecoveryTime = System.currentTimeMillis();
    }

    public void startMapTimeLimitTask(int time, final MapleMap to) {
        if (time <= 0) { //jail
            time = 1;
        }
        client.SendPacket(CField.getClock(time));
        final MapleMap ourMap = getMap();
        time *= 1000;
        mapTimeLimitTask = MapTimer.getInstance().register(new Runnable() {
            @Override
            public void run() {
                if (ourMap.getId() == GameConstants.JAIL) {
                    getQuestNAdd(MapleQuest.getInstance(GameConstants.JAIL_TIME)).setCustomData(String.valueOf(System.currentTimeMillis()));
                    getQuestNAdd(MapleQuest.getInstance(GameConstants.JAIL_QUEST)).setCustomData("0"); //release them!
                }
                changeMap(to, to.getPortal(0));
            }
        }, time, time);
    }

    public boolean canDOT(long now) {
        return lastDOTTime > 0 && lastDOTTime + 8000 < now;
    }

    public boolean hasDOT() {
        return dotHP > 0;
    }

    public void doDOT() {
        addHP(-(dotHP * 4));
        dotHP = 0;
        lastDOTTime = 0;
    }

    public void setDOT(int d, int source, int sourceLevel) {
        this.dotHP = d;
        addHP(-(dotHP * 4));
        map.broadcastMessage(CField.getPVPMist(id, source, sourceLevel, d));
        lastDOTTime = System.currentTimeMillis();
    }

    public void startFishingTask() {
        cancelFishingTask();
        lastFishingTime = System.currentTimeMillis();
    }

    public boolean canFish(long now) {
        return lastFishingTime > 0 && lastFishingTime + GameConstants.getFishingTime(false, isGM()) < now;
    }

    public void doFish(long now) {
        lastFishingTime = now;
        if (client == null || client.getPlayer() == null || !client.isReceiving() || (!haveItem(2270008, 1, false, true)) || !GameConstants.isFishingMap(getMapId()) || chair <= 0) {
            cancelFishingTask();
            return;
        }
        MapleInventoryManipulator.removeById(client, MapleInventoryType.USE, 2270008, 1, false, false);
        boolean passed = false;
        while (!passed) {
            int randval = RandomRewards.getFishingReward();
            switch (randval) {
                case 0: // Meso
                    final int money = Randomizer.rand(10, 50000);
                    gainMeso(money, true);
                    passed = true;
                    break;
                case 1: // EXP
                    final long experi = Randomizer.nextInt((int) Math.min((Math.abs(getNeededExp() / 200) + 1), 500000));
                    gainExp(experi, true, false, true);
                    passed = true;
                    break;
                default:
                    if (MapleItemInformationProvider.getInstance().itemExists(randval)) {
                        MapleInventoryManipulator.addById(client, randval, (short) 1, "Fishing" + " on " + LocalDateTime.now());
                        passed = true;
                    }
                    break;
            }
        }
    }

    public void cancelMapTimeLimitTask() {
        if (mapTimeLimitTask != null) {
            mapTimeLimitTask.cancel(false);
            mapTimeLimitTask = null;
        }
    }

    public long getNeededExp() {
        return GameConstants.getExpNeededForLevel(level);
    }

    public void cancelFishingTask() {
        lastFishingTime = 0;
    }

    public boolean hasSkill(int nSkillId) {
        if (getSkillLevel(nSkillId) > 0) {
            return true;
        }
        return false;
    }

    public int getSkillLevel(int skillid) {
        return getSkillLevel(SkillFactory.getSkill(skillid));
    }

    public int getTotalSkillLevel(int skillid) {
        if (GameConstants.iskaiser_Transfiguration_Skill(skillid)) {
            return SkillFactory.getSkill(skillid).getMaxLevel();
        }
        return getTotalSkillLevel(SkillFactory.getSkill(skillid));
    }

    private final void handleEnergyChargedFinal(Skill echskill, int targets, int skillLevel) {
        if (targets > 0) {
            Integer energyLevel = getBuffedValue(CharacterTemporaryStat.EnergyCharged);
            MapleStatEffect skillEffect = echskill.getEffect(skillLevel);
            if (energyLevel == null || energyLevel < 0) {
                skillEffect.applyEnergyBuff(this, echskill.getId(), targets);
            } else {
                energyLevel = Math.min(energyLevel + skillEffect.getX() * targets, 10000);
                //System.out.println("CHARGE APPLIED: " + energyLevel);
                this.client.SendPacket(CField.EffectPacket.showOwnBuffEffect(echskill.getId(), UserEffectCodes.SkillUse, getLevel(), skillLevel));
                //this.map.broadcastMessage(this, CField.EffectPacket.showBuffeffect(this.id, skillid, UserEffectCodes.SkillUseBySummoned, getLevel(), skilllevel), false);

                this.client.SendPacket(BuffPacket.giveEnergyCharged(this, energyLevel, echskill.getId(), -1));
                setBuffedValue(CharacterTemporaryStat.EnergyCharged, energyLevel);
            }
        }
    }

    public final void handleEnergyCharged(int targets, boolean isKoC) {
        if (!isKoC) {
            int firstSkillLevel = getTotalSkillLevel(SkillFactory.getSkill(constants.skills.Brawler.ENERGY_CHARGE_2));
            int secondSkillLevel = getTotalSkillLevel(SkillFactory.getSkill(constants.skills.Marauder.SUPERCHARGE));
            int thirdSkillLevel = getTotalSkillLevel(SkillFactory.getSkill(constants.skills.Buccaneer.ULTRA_CHARGE));
            if (thirdSkillLevel > 0) {
                Skill echskill = SkillFactory.getSkill(constants.skills.Buccaneer.ULTRA_CHARGE);
                handleEnergyChargedFinal(echskill, targets, thirdSkillLevel);
            } else if (secondSkillLevel > 0) {
                Skill echskill = SkillFactory.getSkill(constants.skills.Marauder.SUPERCHARGE);
                handleEnergyChargedFinal(echskill, targets, secondSkillLevel);
            } else if (firstSkillLevel > 0) {
                Skill echskill = SkillFactory.getSkill(constants.skills.Brawler.ENERGY_CHARGE_2);
                handleEnergyChargedFinal(echskill, targets, firstSkillLevel);
            }
        } else if (isKoC) {
            /*  int SkillLevel = getTotalSkillLevel(SkillFactory.getSkill(constants.skills.Thunderbreaker.ENERGY_CHARGE));
            if (SkillLevel > 0) {
                Skill echskill = SkillFactory.getSkill(constants.skills.Thunderbreaker.ENERGY_CHARGE);
                handleEnergyChargedFinal(echskill, targets, SkillLevel);
            } */
        }
    }

    public final void handleBattleshipHP(int damage) {
        if (damage < 0) {
            final MapleStatEffect effect = getStatForBuff(CharacterTemporaryStat.RideVehicle);
            if (effect != null && effect.getSourceId() == 5221006) {
                battleshipHP += damage;
                client.SendPacket(CField.skillCooldown(5221999, battleshipHP / 10));
                if (battleshipHP <= 0) {
                    battleshipHP = 0;

                    addCooldown(5221006, System.currentTimeMillis(), effect.getCooldown(this));

                    cancelEffectFromTemporaryStat(CharacterTemporaryStat.RideVehicle);
                }
            }
        }
    }

    public final void handleOrbgain() {
        int orbcount = getBuffedValue(CharacterTemporaryStat.ComboCounter);
        Skill normalcombo;
        Skill advcombo;

        switch (getJob()) {
            case 1110:
            case 1111:
            case 1112:
                normalcombo = SkillFactory.getSkill(11111001);
                advcombo = SkillFactory.getSkill(11110005);
                break;
            default:
                normalcombo = SkillFactory.getSkill(1101013);
                advcombo = SkillFactory.getSkill(1120003);
                break;
        }

        MapleStatEffect ceffect;
        int advComboSkillLevel = getTotalSkillLevel(advcombo);
        if (advComboSkillLevel > 0) {
            ceffect = advcombo.getEffect(advComboSkillLevel);
        } else if (getSkillLevel(normalcombo) > 0) {
            ceffect = normalcombo.getEffect(getTotalSkillLevel(normalcombo));
        } else {
            return;
        }

        if (orbcount < ceffect.getX() + 1) {
            int neworbcount = orbcount + 1;
            if (advComboSkillLevel > 0 && ceffect.makeChanceResult()) {
                if (neworbcount < ceffect.getX() + 1) {
                    neworbcount++;

                }
            }
            EnumMap<CharacterTemporaryStat, Integer> stat = new EnumMap<>(CharacterTemporaryStat.class
            );
            stat.put(CharacterTemporaryStat.ComboCounter, neworbcount);
            setBuffedValue(CharacterTemporaryStat.ComboCounter, neworbcount);
            int duration = ceffect.getDuration();
            duration += (int) ((getBuffedStarttime(CharacterTemporaryStat.ComboCounter) - System.currentTimeMillis()));

            client.SendPacket(BuffPacket.giveBuff(this, normalcombo.getId(), duration, stat, ceffect));
            map.broadcastMessage(this, BuffPacket.giveForeignBuff(getId(), stat, ceffect), false);
            //map.broadcastMessage(this, BuffPacket.giveForeignBuff(this, getId(), stat, ceffect), false);
        }
    }

    public void handleOrbconsume(int howmany) {
        Skill normalcombo;

        switch (getJob()) {
            case 1110:
            case 1111:
            case 1112:
                normalcombo = SkillFactory.getSkill(11111001);
                break;
            default:
                normalcombo = SkillFactory.getSkill(1111002);
                break;
        }
        if (getSkillLevel(normalcombo) <= 0) {
            return;
        }
        MapleStatEffect ceffect = getStatForBuff(CharacterTemporaryStat.ComboCounter);
        if (ceffect == null) {
            return;

        }
        EnumMap<CharacterTemporaryStat, Integer> stat = new EnumMap<>(CharacterTemporaryStat.class
        );
        stat.put(CharacterTemporaryStat.ComboCounter, Math.max(1, getBuffedValue(CharacterTemporaryStat.ComboCounter) - howmany));
        setBuffedValue(CharacterTemporaryStat.ComboCounter, Math.max(1, getBuffedValue(CharacterTemporaryStat.ComboCounter) - howmany));
        int duration = ceffect.getDuration();
        duration += (int) ((getBuffedStarttime(CharacterTemporaryStat.ComboCounter) - System.currentTimeMillis()));

        client.SendPacket(BuffPacket.giveBuff(this, normalcombo.getId(), duration, stat, ceffect));
        map.broadcastMessage(this, BuffPacket.giveForeignBuff(getId(), stat, ceffect), false);
        //map.broadcastMessage(this, BuffPacket.giveForeignBuff(this, getId(), stat, ceffect), false);
    }

    public void silentEnforceMaxHpMp() {
        stats.setMp(stats.getMp(), this);
        stats.setHp(stats.getHp(), true, this);
    }

    public void enforceMaxHpMp() {
        Map<MapleStat, Long> statups = new EnumMap<>(MapleStat.class
        );
        if (stats.getMp() > stats.getCurrentMaxMp(this.getJob())) {
            stats.setMp(stats.getMp(), this);
            statups.put(MapleStat.MP, Long.valueOf(stats.getMp()));
        }
        if (stats.getHp() > stats.getCurrentMaxHp()) {
            stats.setHp(stats.getHp(), this);
            statups.put(MapleStat.HP, Long.valueOf(stats.getHp()));
        }
        if (statups.size() > 0) {
            client.SendPacket(CWvsContext.updatePlayerStats(statups, this));
        }
    }

    public void setTouchedRune(int type) {
        touchedrune = type;
    }

    public int getTouchedRune() {
        return touchedrune;
    }

    public long getRuneTimeStamp() {
        return Long.parseLong(getKeyValue("LastTouchedRune"));
    }

    public void setRuneTimeStamp(long time) {
        setKeyValue("LastTouchedRune", String.valueOf(time));
    }

    public MapleMap getMap() {
        return map;
    }

    public MonsterBook getMonsterBook() {
        return monsterbook;
    }

    public void setMap(MapleMap newmap) {
        this.map = newmap;
    }

    public void setMap(int PmapId) {
        this.mapid = PmapId;
    }

    public int getMapId() {
        if (map != null) {
            return map.getId();
        }
        return mapid;
    }

    public byte getInitialSpawnpoint() {
        return initialSpawnPoint;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public final String getBlessOfFairyOrigin() {
        return blessingOfFairy;
    }

    public final String getBlessOfEmpressOrigin() {
        return blessingOfEmpress;
    }

    public final short getLevel() {
        return level;
    }

    public short setChangeLevel() {
        return getLevel();
    }

    public final int getFame() {
        return fame;
    }

    public final int getFallCounter() {
        return fallcounter;
    }

    public final MapleClient getClient() {
        return client;
    }

    public final void setClient(final MapleClient client) {
        this.client = client;
    }

    public long getExp() {
        return exp;
    }

    public int getRemainingAp() {
        return remainingAp;
    }

    public int getRemainingSp() {
        return remainingSp[GameConstants.getSkillBook(job, 0)]; //default
    }

    public int getRemainingSp(final int skillbook) {
        return remainingSp[skillbook];
    }

    public int[] getRemainingSps() {
        return remainingSp;
    }

    public int getRemainingSpSize() {
        int ret = 0;
        for (int i = 0; i < remainingSp.length; i++) {
            if (remainingSp[i] > 0) {
                ret++;
            }
        }
        return ret;
    }

    public int getRemainingHSp(int mode) {
        if (mode >= 0 && remainingHSp.length < mode) {
            return remainingHSp[mode];
        }
        return 0;
    }

    public int[] getRemainingHSps() {
        return remainingHSp;
    }

    public int getRemainingHSpSize() {
        int ret = 0;
        for (int i = 0; i < remainingHSp.length; i++) {
            if (remainingHSp[i] > 0) {
                ret++;
            }
        }
        return ret;
    }

    public short getHpApUsed() {
        return hpApUsed;
    }

    public boolean isHidden() {
        return getBuffSource(CharacterTemporaryStat.DarkSight) / 1000000 == 9;
    }

    public void setHpApUsed(short hpApUsed) {
        this.hpApUsed = hpApUsed;
    }

    @Override
    public byte getSkinColor() {
        return skinColor;
    }

    public void setSkinColor(byte skinColor) {
        this.skinColor = skinColor;
    }

    @Override
    public short getJob() {
        return job;
    }

    public String getJobName(short id) {
        return MapleJob.getName(MapleJob.getById(id));
    }

    @Override
    public byte getGender() {
        return gender;
    }

    @Override
    public byte getSecondaryGender() {
        if (GameConstants.isZero(getJob())) {
            return 1;
        }
        return gender;
    }

    @Override
    public int getHair() {
        return hair;
    }

    @Override
    public int getAngelicDressupHair() {
        return angelicDressupHair;
    }

    @Override
    public int getZeroBetaHair() {
        return zeroBetaHair;
    }

    @Override
    public int getFace() {
        return face;
    }

    @Override
    public int getAngelicDressupFace() {
        return angelicDressupFace;
    }

    @Override
    public int getAngelicDressupSuit() {
        return angelicDressupSuit;
    }

    @Override
    public int getZeroBetaFace() {
        return zeroBetaFace;
    }

    @Override
    public int getFaceMarking() {
        return faceMarking;
    }

    public void setFaceMarking(int mark) {
        this.faceMarking = mark;
    }

    @Override
    public int getEars() {
        return ears;
    }

    public void setEars(int ears) {
        this.ears = ears;
        equipChanged();
    }

    @Override
    public int getTail() {
        return tail;
    }

    public void setTail(int tail) {
        this.tail = tail;
        equipChanged();
    }

    @Override
    public int getElf() {
        return elf;
    }

    public void setElf(int elf) {
        this.elf = elf;
        equipChanged();
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setExp(long exp) {
        this.exp = exp;
    }

    public void setHair(int hair) {
        this.hair = hair;
    }

    public void setFace(int face) {
        this.face = face;
    }

    public void setZeroBetaHair(int hair) {
        this.zeroBetaHair = hair;
    }

    public void setZeroBetaFace(int face) {
        this.zeroBetaFace = face;
    }

    public void setAngelicDressupHair(int hair) {
        this.angelicDressupHair = hair;
    }

    public void setAngelicDressupFace(int face) {
        this.angelicDressupFace = face;
    }

    public void setAngelicDressupSuit(int suit) {
        this.angelicDressupSuit = suit;
    }

    public void setFame(int fame) {
        this.fame = fame;
    }

    public void setFallCounter(int fallcounter) {
        this.fallcounter = fallcounter;
    }

    public Point getOldPosition() {
        return oldPos;
    }

    public void setOldPosition(Point x) {
        this.oldPos = x;
    }

    public void setRemainingAp(int remainingAp) {
        this.remainingAp = remainingAp;
    }

    public void setRemainingSp(int remainingSp) {
        this.remainingSp[GameConstants.getSkillBook(job, 0)] = remainingSp; //default
    }

    public void setRemainingSp(int remainingSp, final int skillbook) {
        this.remainingSp[skillbook] = remainingSp;
    }

    public void setRemainingHSp(int mode, int amount) {
        this.remainingHSp[mode] = amount;
    }

    public void setGender(byte gender) {
        this.gender = gender;
    }

    public void setInvincible(boolean invinc) {
        invincible = invinc;
    }

    public boolean isInvincible() {
        return invincible;
    }

    public CheatTracker getCheatTracker() {
        return anticheat;
    }

    public BuddyList getBuddylist() {
        return buddylist;
    }

    public void addFame(int famechange) {
        this.fame += famechange;
        getTrait(MapleTraitType.charm).addLocalExp(famechange);
        if (this.fame >= 50) {
            finishAchievement(7);
        }
    }

    public void updateFame() {
        updateSingleStat(MapleStat.FAME, this.fame);
    }

    public boolean changeMap(final MapleMap to, final Point pos) {
        return changeMapInternal(to, pos, CField.getWarpToMap(this, to, 0x80, false), null);
    }

    public boolean changeMap(final MapleMap to) {
        return changeMapInternal(to, to.getPortal(0).getPosition(), CField.getWarpToMap(this, to, 0, false), to.getPortal(0));
    }

    public boolean changeMap(final MapleMap to, final MaplePortal pto) {
        return changeMapInternal(to, pto.getPosition(), CField.getWarpToMap(this, to, pto.getId(), false), null);
    }

    public boolean changeMapPortal(final MapleMap to, final MaplePortal pto) {
        return changeMapInternal(to, pto.getPosition(), CField.getWarpToMap(this, to, pto.getId(), false), pto);
    }

    public boolean changeEvolvingMap(final MapleMap to, final MaplePortal pto, String bgm, int applymap) {
        return changeMapInternal(to, pto.getPosition(), CField.getWarpToMap(this, to, pto.getId(), false), pto);
    }

    /**
     * Changes the current field of the character to the target field
     *
     * @param to
     * @param pos
     * @param warpPacket
     * @param pto
     * @return
     */
    private boolean changeMapInternal(final MapleMap to, final Point pos, OutPacket warpPacket, final MaplePortal pto) {
        if (to == null) {
            return false;
        } else if (to.getSharedMapResources().lvLimit > getLevel()) {
            this.dropMessage(5, "You cannot go any closer to it, as the force of the earth is hindering your movement.");
            return false;
        }
        //if (getAntiMacro().inProgress()) {
        //    dropMessage(5, "You cannot use it in the middle of the Lie Detector Test.");
        //    return;
        //}
        final int nowmapid = map.getId();
        if (eventInstance != null) {
            eventInstance.changedMap(this, to.getId());
        }
        final boolean pyramid = pyramidSubway != null;
        if (map.getId() == nowmapid) {
            client.SendPacket(warpPacket);
            final boolean shouldChange = !isClone() && client.getChannelServer().getPlayerStorage().getCharacterById(getId()) != null;
            final boolean shouldState = map.getId() == to.getId();
            if (shouldChange && shouldState) {
                to.setCheckStates(false);
            }
            map.removePlayer(this);

            if (shouldChange) {
                map = to;
                setPosition(pos);
                to.addPlayer(this, nowmapid);
                stats.relocHeal(this);
                if (to.getSharedMapResources().starForceBarrier > 0) {
                    stats.recalcLocalStats(this);
                }

                if (shouldState) {
                    to.setCheckStates(true);
                }
            }
        }
        if (pyramid && pyramidSubway != null) { //checks if they had pyramid before AND after changing
            pyramidSubway.onChangeMap(this, to.getId());
        }
        // updateDeathCount();
        return true;
    }

    public void cancelChallenge() {
        if (challenge != 0 && client.getChannelServer() != null) {
            final User chr = client.getChannelServer().getPlayerStorage().getCharacterById(challenge);
            if (chr != null) {
                chr.dropMessage(6, getName() + " has denied your request.");
                chr.setChallenge(0);
            }
            dropMessage(6, "Denied the challenge.");
            challenge = 0;
        }
    }

    public void leaveMap(MapleMap map) {
        controlledLock.writeLock().lock();
        visibleMapObjectsLock.writeLock().lock();
        try {
            for (Mob mons : controlled) {
                if (mons != null) {
                    mons.setController(null);
                    mons.setControllerHasAggro(false);
                    map.updateMonsterController(mons);
                }
            }
            controlled.clear();
            visibleMapObjects.clear();
        } finally {
            controlledLock.writeLock().unlock();
            visibleMapObjectsLock.writeLock().unlock();
        }
        if (chair != 0) {
            chair = 0;
        }
        clearLinkMid();
        cancelFishingTask();
        cancelChallenge();
        if (!getMechDoors().isEmpty()) {
            removeMechDoor();
        }
        cancelMapTimeLimitTask();
        if (getTrade() != null) {
            MapleTrade.cancelTrade(getTrade(), client, this);
        }
        //antiMacro.reset(); // reset lie detector
    }

    public void spMessage(int sp, int jobId) {
        IncreaseSpMessage spm = new IncreaseSpMessage(sp, jobId);
        client.SendPacket(CWvsContext.messagePacket(spm));
    }

    public void changeJob(short newJob) {
        Map<MapleStat, Long> statup = new EnumMap<>(MapleStat.class
        );

        try {
            this.job = newJob;
            statup.put(MapleStat.JOB, Long.valueOf(newJob));

            // Handle SP gain here
            if (!GameConstants.isExtendedSpJob(job)) {
                boolean isFourthJob = job % 10 >= 2;

                remainingSp[GameConstants.getSkillBook(job, 0)] += isFourthJob ? 3 : 1;
            }
            // Handle AP gain here
            if (newJob % 10 >= 1 && level >= 70) { //3rd job or higher. lucky for evans who get 80, 100, 120, 160 ap...
                remainingAp += 5;
                if (!isGM()) {
                    resetStatsByJob(true);
                }

                statup.put(MapleStat.AVAILABLEAP, Long.valueOf(remainingAp));
            }

            // Handle HP and MP gain here
            int maxhp = stats.getMaxHp(), maxmp = stats.getMaxMp();

            switch (job) {
                case 100: // Warrior
                case 1100: // Soul Master
                case 2100: // Aran
                case 3100:
                case 3200:
                case 4100:
                case 5100:
                case 6100:
                    maxhp += Randomizer.rand(200, 250);
                    break;
                case 3110:
                    maxhp += Randomizer.rand(300, 350);
                    break;
                case 200: // Magician
                case 2200: //evan
                case 2210: //evan
                case 2700:
                case 4200:
                    maxmp += Randomizer.rand(100, 150);
                    break;
                case 300: // Bowman
                case 400: // Thief
                case 500: // Pirate
                case 2300:
                case 3300:
                case 3500:
                case 3600:
                case 6500:
                    maxhp += Randomizer.rand(100, 150);
                    maxmp += Randomizer.rand(25, 50);
                    break;
                case 110: // Fighter
                case 120: // Page
                case 130: // Spearman
                case 1110: // Soul Master
                case 2110: // Aran
                case 3210:
                case 4110:
                case 5110:
                case 6110:
                    maxhp += Randomizer.rand(300, 350);
                    break;
                case 210: // FP
                case 220: // IL
                case 230: // Cleric
                    maxmp += Randomizer.rand(400, 450);
                    break;
                case 310: // Bowman
                case 320: // Crossbowman
                case 410: // Assasin
                case 420: // Bandit
                case 430: // Semi Dualer
                case 510:
                case 520:
                case 530:
                case 2310:
                case 1310: // Wind Breaker
                case 1410: // Night Walker
                case 3310:
                case 3510:
                    maxhp += Randomizer.rand(200, 250);
                    maxhp += Randomizer.rand(150, 200);
                    break;
                case 800: // Manager
                case 900: // GM
                case 910: // SuperGM
                    break; //No additions for now
            }
            if (maxhp >= GameConstants.maxHP) {
                maxhp = GameConstants.maxHP;
            }
            if (maxmp >= GameConstants.maxMP) {
                maxmp = GameConstants.maxMP;
            }
            if (GameConstants.isDemonSlayer(job)) {
                maxmp = GameConstants.getMPByJob(job);
            } else if (GameConstants.isZero(job)) {
                maxmp = 100;
            }
            stats.setInfo(maxhp, maxmp, maxhp, maxmp);

            statup.put(MapleStat.MAXHP, Long.valueOf(maxhp));
            statup.put(MapleStat.IndieMMP, Long.valueOf(maxmp));
            statup.put(MapleStat.HP, Long.valueOf(maxhp));
            statup.put(MapleStat.MP, Long.valueOf(maxmp));

            characterCard.recalcLocalStats(this);
            stats.recalcLocalStats(this);
            client.SendPacket(CWvsContext.updatePlayerStats(statup, this));
            //map.broadcastMessage(this, EffectPacket.showForeignEffect(getId(), UserEffectCodes.JobChanged), false); // Bugged, displays for all players.
            client.SendPacket(EffectPacket.showForeignEffect(getId(), UserEffectCodes.JobChanged));
            silentPartyUpdate();
            guildUpdate();
            familyUpdate();

            if (dragon != null) {
                map.broadcastMessage(CField.removeDragon(this.id));
                dragon = null;
            }
            if (haku != null) {
                haku = null;
            }
            baseSkills();
            if (newJob >= 2200 && newJob <= 2218) { //make new
                if (getBuffedValue(CharacterTemporaryStat.RideVehicle) != null) {
                    cancelTemporaryStats(CharacterTemporaryStat.RideVehicle);
                }
                makeDragon();
            }
            if (((newJob >= 4200) && (newJob <= 4212)) || (newJob == 4002)) {
                if (getBuffedValue(CharacterTemporaryStat.RideVehicle) != null) {
                    cancelTemporaryStats(new CharacterTemporaryStat[]{CharacterTemporaryStat.RideVehicle});
                }
                makeHaku();
            }
            checkForceShield();
            cancelEffectFromTemporaryStat(CharacterTemporaryStat.ShadowPartner);

            if (GameConstants.isPhantom(job)) {
                client.SendPacket(PhantomPacket.updateCardStack(0));
                resetRunningStack();
            }
            if (newJob == 2200) {
                MapleQuest.getInstance(22100).forceStart(this, 0, null);
                MapleQuest.getInstance(22100).forceComplete(this, 0);
                expandInventory((byte) 1, 4);
                expandInventory((byte) 2, 4);
                expandInventory((byte) 3, 4);
                expandInventory((byte) 4, 4);
                client.SendPacket(NPCPacket.getEvanTutorial("UI/tutorial/evan/14/0"));
                dropMessage(5, "The baby Dragon hatched and appears to have something to tell you. Click the baby Dragon to start a conversation.");

            }
        } catch (Exception e) {
            LogHelper.GENERAL_EXCEPTION.get().info("Error trying to change jobs:\n{}", e);
        }
    }

    public void handleDemonJob(int selection) {
        if (selection == 0) {
            changeJob((short) 3101);
        } else if (selection == 1) {
            changeJob((short) 3100);
        }
    }

    public void addJobSkills() {
        if (GameConstants.isPhantom(job)) {
            changeSkillLevel(SkillFactory.getSkill(job == 2412 ? 20031210 : 20031209), (byte) 1, (byte) 1);
            changeSkillLevel(SkillFactory.getSkill(job == 2412 ? 20031209 : 20031210), (byte) -1, (byte) 0);
        } else if (GameConstants.isXenon(job)) {
            int[] _skills = new int[]{job >= 3610 ? 30021235 : 0,
                job != 3002 ? 30021236 : 0, job != 3002 ? 30021237 : 0, 36000004,
                36100007, 36110007, 36120010};
            for (int skill : _skills) {
                Skill _skill = SkillFactory.getSkill(skill);
                if (_skill != null && _skill.canBeLearnedBy(job)) {
                    changeSkillLevel(_skill, (byte) 1, (byte) 1);
                }
            }
        }
    }

    private void addLevelSkills() {
        if (job == 3612 && level >= 200) {
            Skill skill = SkillFactory.getSkill(36120016);
            if (getSkillLevel(skill) < 1) {
                if (skill != null && skill.canBeLearnedBy(job)) {
                    changeSkillLevel(skill, (byte) 1, (byte) 1);
                }
            }
        }

        if (GameConstants.isPhantom(job)) {
            if (level >= 30 && (!hasSkill(Phantom.JUDGMENT_DRAW_5) || !hasSkill(Phantom.JUDGMENT_DRAW_AUTOMANUAL))) { // Judgment Draw (Carte Blanche) 
                changeSkillLevel(SkillFactory.getSkill(Phantom.JUDGMENT_DRAW_5), (byte) 1, (byte) 1);
                changeSkillLevel(SkillFactory.getSkill(Phantom.JUDGMENT_DRAW_AUTOMANUAL), (byte) 1, (byte) 1);
            }
            if (level >= 100 && !hasSkill(Phantom.JUDGMENT_DRAW_4)) { // Judgment Draw (Carte Noire)
                changeSkillLevel(SkillFactory.getSkill(Phantom.JUDGMENT_DRAW_4), (byte) 1, (byte) 1);
            }
        }
        if (GameConstants.isKinesis(job)) {
            if (level >= 10 && !hasSkill(Kinesis.ESP)) { // ESP
                changeSkillLevel(SkillFactory.getSkill(Kinesis.ESP), (byte) 6, (byte) 6);
            }
        }
    }

    public void baseSkills() {
        Map<Skill, SkillEntry> list = new HashMap<>();
        if (GameConstants.getJobNumber(job) >= 3) { //third job.
            List<Integer> base_skills = SkillFactory.getSkillsByJob(job);
            if (base_skills != null) {
                for (int i : base_skills) {
                    final Skill skil = SkillFactory.getSkill(i);
                    Tuple<String, String, String> skillName_ = MapleStringInformationProvider.getSkillStringCache().get(i);

                    if (skil != null && !skil.isInvisible() && skil.isFourthJob() && getSkillLevel(skil) <= 0 && getMasterLevel(skil) <= 0 && skil.getMasterLevel() > 0) {
                        list.put(skil, new SkillEntry((byte) 0, (byte) skil.getMasterLevel(), SkillFactory.getDefaultSExpiry(skil))); //usually 10 master
                    } else if (skil != null && skillName_ != null && (skillName_.get_2().contains("Maple Warrior") || skillName_.get_2().contains("Nova Warrior")) && getSkillLevel(skil) <= 0 && getMasterLevel(skil) <= 0) {
                        list.put(skil, new SkillEntry((byte) 0, (byte) 10, SkillFactory.getDefaultSExpiry(skil))); //hackish
                    }
                }

            }
        }
        Skill skil;

        int start = (job / 100) * 1000000;
        int end = job * 10000 + 3000;
        for (int i = start; i < end; i++) {
            skil = SkillFactory.getSkill(i);
            if (skil != null) {
                if (getSkillLevel(skil) <= 0 && !skil.isInvisible()) {
                    boolean fourth = skil.isFourthJob();
                    int master = skil.getMasterLevel();
                    list.put(skil, new SkillEntry((byte) 0, (byte) (fourth ? master > 0 ? master : skil.getMaxLevel() : skil.getMaxLevel()), -1));
                }
            }
        }
        if (!list.isEmpty()) {
            changeSkillsLevel(list);
        }
    }

    public void makeDragon() {
        dragon = new MapleDragon(this);
        map.broadcastMessage(CField.spawnDragon(dragon));
    }

    public MapleDragon getDragon() {
        return dragon;
    }

    public void makeHaku() {
        haku = new MapleHaku(this);
        map.broadcastMessage(CField.spawnHaku(haku, false));
    }

    public MapleHaku getHaku() {
        return haku;
    }

    public void gainAp(short ap) {
        this.remainingAp += ap;
        updateSingleStat(MapleStat.AVAILABLEAP, this.remainingAp);
    }

    public void gainSP(int sp) {
        this.remainingSp[GameConstants.getSkillBook(job, 0)] += sp; //default
        updateSingleStat(MapleStat.AVAILABLESP, 0); // we don't care the value here
        spMessage(sp, job);
    }

    public void gainSP(int sp, final int skillbook) {
        this.remainingSp[skillbook] += sp;
        updateSingleStat(MapleStat.AVAILABLESP, 0);
        spMessage(sp, job);
    }

    public void gainHyperSP(int mode, int hsp) {
        this.remainingHSp[mode] += hsp;

        client.SendPacket(CWvsContext.updateSpecialStat(
                MapleSpecialStatUpdateType.UpdateHyperSkills, 0x1C,
                mode, hsp));
    }

    public void updateHyperSPAmount() {
        for (int i = 0; i < 3; i++) {
            client.SendPacket(CWvsContext.updateSpecialStat(
                    MapleSpecialStatUpdateType.UpdateHyperSkills, 0x1C,
                    i, remainingHSp[i]));
        }
    }

    public void resetSP(int sp) {
        for (int i = 0; i < remainingSp.length; i++) {
            this.remainingSp[i] = sp;
        }
        updateSingleStat(MapleStat.AVAILABLESP, 0);
    }

    public void resetAPSP() {
        resetSP(0);
        gainAp((short) -this.remainingAp);
    }

    public void resetSp(int jobid) {
        int skillpoint = 0;
        for (Skill skil : SkillFactory.getAllSkills()) {
            if (GameConstants.isApplicableSkill(skil.getId()) && skil.canBeLearnedBy(getJob()) && skil.getId() >= jobid * 1000000 && skil.getId() < (jobid + 1) * 1000000 && !skil.isInvisible()) {
                skillpoint += getSkillLevel(skil);
            }
        }
        gainSP(skillpoint, GameConstants.getSkillBook(jobid, 0));
        final Map<Skill, SkillEntry> skillmap = new HashMap<>(getSkills());
        final Map<Skill, SkillEntry> newList = new HashMap<>();
        for (Entry<Skill, SkillEntry> skill : skillmap.entrySet()) {
            newList.put(skill.getKey(), new SkillEntry((byte) 0, (byte) 0, -1));
        }
        changeSkillsLevel(newList);
        newList.clear();
        skillmap.clear();
    }

    public List<Integer> getProfessions() {
        List<Integer> prof = new ArrayList<>();
        for (int i = 9200; i <= 9204; i++) {
            if (getProfessionLevel(id * 10000) > 0) {
                prof.add(i);
            }
        }
        return prof;
    }

    public byte getProfessionLevel(int id) {
        int ret = getSkillLevel(id);
        if (ret <= 0) {
            return 0;
        }
        return (byte) ((ret >>> 24) & 0xFF); //the last byte
    }

    public short getProfessionExp(int id) {
        int ret = getSkillLevel(id);
        if (ret <= 0) {
            return 0;
        }
        return (short) (ret & 0xFFFF); //the first two byte
    }

    public boolean addProfessionExp(int id, int expGain) {
        int ret = getProfessionLevel(id);
        if (ret <= 0 || ret >= 10) {
            return false;
        }
        int newExp = getProfessionExp(id) + expGain;
        if (newExp >= GameConstants.getProfessionEXP(ret)) {
            //gain level
            changeProfessionLevelExp(id, ret + 1, newExp - GameConstants.getProfessionEXP(ret));
            int traitGain = (int) Math.pow(2, ret + 1);
            switch (id) {
                case 92000000:
                    traits.get(MapleTraitType.sense).addExp(traitGain, this);
                    break;
                case 92010000:
                    traits.get(MapleTraitType.will).addExp(traitGain, this);
                    break;
                case 92020000:
                case 92030000:
                case 92040000:
                    traits.get(MapleTraitType.craft).addExp(traitGain, this);
                    break;
            }
            return true;
        } else {
            changeProfessionLevelExp(id, ret, newExp);
            return false;
        }
    }

    public void changeProfessionLevelExp(int id, int level, int exp) {
        changeSingleSkillLevel(SkillFactory.getSkill(id), ((level & 0xFF) << 24) + (exp & 0xFFFF), (byte) 10);
    }

    public void changeSingleSkillLevel(final Skill skill, int newLevel, byte newMasterlevel) { //1 month
        if (skill == null) {
            return;
        }
        changeSingleSkillLevel(skill, newLevel, newMasterlevel, SkillFactory.getDefaultSExpiry(skill));
    }

    public void changeSingleSkillLevel(final Skill skill, int newLevel, byte newMasterlevel, long expiration) {
        final Map<Skill, SkillEntry> list = new HashMap<>();
        boolean hasRecovery = false, recalculate = false;
        if (changeSkillData(skill, newLevel, newMasterlevel, expiration)) { // no loop, only 1
            list.put(skill, new SkillEntry(newLevel, newMasterlevel, expiration));
            if (GameConstants.isRecoveryIncSkill(skill.getId())) {
                hasRecovery = true;
            }
            if (skill.getId() < 80000000) {
                recalculate = true;
            }
        }
        if (list.isEmpty()) { // nothing is changed
            return;
        }
        client.SendPacket(CWvsContext.updateSkills(list, false));
        reUpdateStat(hasRecovery, recalculate);
    }

    public void changeSingleSkillLevel(Skill skill, int newLevel, byte newMasterlevel, long expiration, boolean hyper) {
        final Map<Skill, SkillEntry> list = new HashMap<>();
        boolean hasRecovery = false, recalculate = false;
        if (changeSkillData(skill, newLevel, newMasterlevel, expiration)) { // no loop, only 1
            list.put(skill, new SkillEntry(newLevel, newMasterlevel, expiration));
            if (GameConstants.isRecoveryIncSkill(skill.getId())) {
                hasRecovery = true;
            }
            if (skill.getId() < 80000000) {
                recalculate = true;
            }
        }
        if (list.isEmpty()) { // nothing is changed
            return;
        }
        client.SendPacket(CWvsContext.updateSkills(list, hyper));
        reUpdateStat(hasRecovery, recalculate);
    }

    public void changeSkillsLevel(Map<Skill, SkillEntry> ss) {
        changeSkillsLevel(ss, false);
    }

    public void changeSkillsLevel(Map<Skill, SkillEntry> ss, boolean hyper) {
        if (ss.isEmpty()) {
            return;
        }
        final Map<Skill, SkillEntry> list = new HashMap<>();
        boolean hasRecovery = false, recalculate = false;
        for (final Entry<Skill, SkillEntry> data : ss.entrySet()) {
            if (changeSkillData(data.getKey(), data.getValue().skillevel, data.getValue().masterlevel, data.getValue().expiration)) {
                list.put(data.getKey(), data.getValue());
                if (GameConstants.isRecoveryIncSkill(data.getKey().getId())) {
                    hasRecovery = true;
                }
                if (data.getKey().getId() < 80000000) {
                    recalculate = true;
                }
            }
        }
        if (list.isEmpty()) { // nothing is changed
            return;
        }
        client.SendPacket(CWvsContext.updateSkills(list, hyper));
        reUpdateStat(hasRecovery, recalculate);
    }

    public void reUpdateStat(boolean hasRecovery, boolean recalculate) {
        changed_skills = true;
        if (hasRecovery) {
            stats.relocHeal(this);
        }
        if (recalculate) {
            stats.recalcLocalStats(this);
        }
    }

    public void changeSkillLevel(int nSkillID, byte nNewLevel, byte nNewMasterlevel) {
        changeSkillLevelSkip(SkillFactory.getSkill(nSkillID), nNewLevel, nNewMasterlevel);
    }

    public void changeSkillLevel(Skill skill, byte newLevel, byte newMasterlevel) {
        changeSkillLevelSkip(skill, newLevel, newMasterlevel);
    }

    public void changeSkillLevelSkip(Skill skil, int skilLevel, byte masterLevel) {
        final Map<Skill, SkillEntry> enry = new HashMap<>(1);
        enry.put(skil, new SkillEntry(skilLevel, masterLevel, -1L));
        changeSkillLevelSkip(enry, true);
    }

    public boolean changeSkillData(final Skill skill, int newLevel, byte newMasterlevel, long expiration) {
        if (skill == null || (!GameConstants.isApplicableSkill(skill.getId()) && !GameConstants.isApplicableSkill_(skill.getId()))) {
            return false;
        }
        if (newLevel == 0 && newMasterlevel == 0) {
            if (skills.containsKey(skill)) {
                skills.remove(skill);
            } else {
                return false; //nothing happen
            }
        } else {
            skills.put(skill, new SkillEntry(newLevel, newMasterlevel, expiration));
        }
        return true;
    }

    public void changeSkillLevelSkip(final Map<Skill, SkillEntry> skill, final boolean write) { // only used for temporary skills (not saved into db)
        if (skill.isEmpty()) {
            return;
        }
        final Map<Skill, SkillEntry> newL = new HashMap<>();
        for (final Entry<Skill, SkillEntry> z : skill.entrySet()) {
            if (z.getKey() == null) {
                continue;
            }
            newL.put(z.getKey(), z.getValue());
            if (z.getValue().skillevel == 0 && z.getValue().masterlevel == 0) {
                if (skills.containsKey(z.getKey())) {
                    skills.remove(z.getKey());
                } else {
                    continue;
                }
            } else {
                skills.put(z.getKey(), z.getValue());
            }
        }
        if (write && !newL.isEmpty()) {
            client.SendPacket(CWvsContext.updateSkills(newL, false));
        }
    }

    public void playerDead() {
        final List<DeadUIStats> deadstats = new ArrayList<>();
        deadstats.add(DeadUIStats.bOnDeadRevive);

        if (deathCount == 1) {
            switch (getMapId()) {
                case 105200610: // Root Abyss Death Count
                case 105200210:
                case 105200710:
                case 105200310:
                case 105200510:
                case 105200110:
                case 105200410:
                case 105200810:
                    setDeathCount(0);

                    changeMap(105200000, 0);
                    fakeRelog2();
                    dropMessage(5, "Ran out of deaths, Better luck next time..");
                    break;
                case 401060200: // Magnus Death Count
                case 401060100:
                    setDeathCount(0);

                    changeMap(400000000, 0);
                    fakeRelog2();
                    dropMessage(5, "Ran out of deaths, Better luck next time..");
                    break;
                case 350060600: // Lotus Death Count
                case 350060400:

                    setDeathCount(0);

                    changeMap(350060300, 0);
                    fakeRelog2();
                    dropMessage(5, "Ran out of deaths, Better luck next time..");
                    break;
            }
        }
        if (deathCount > 0) {
            deathCount--;
            updateDeathCount();
        }
        if (getEventInstance() != null) {
            getEventInstance().playerKilled(this);
        }
        cancelEffectFromTemporaryStat(CharacterTemporaryStat.ShadowPartner);
        cancelEffectFromTemporaryStat(CharacterTemporaryStat.Morph);
        cancelEffectFromTemporaryStat(CharacterTemporaryStat.Flying);
        cancelEffectFromTemporaryStat(CharacterTemporaryStat.RideVehicle);
        cancelEffectFromTemporaryStat(CharacterTemporaryStat.Mechanic);
        cancelEffectFromTemporaryStat(CharacterTemporaryStat.Regen);
        cancelEffectFromTemporaryStat(CharacterTemporaryStat.IndieMHPR);
        cancelEffectFromTemporaryStat(CharacterTemporaryStat.IndieMMPR);
        cancelEffectFromTemporaryStat(CharacterTemporaryStat.IncMaxHP);
        cancelEffectFromTemporaryStat(CharacterTemporaryStat.IncMaxMP);
        cancelEffectFromTemporaryStat(CharacterTemporaryStat.EMHP);
        cancelEffectFromTemporaryStat(CharacterTemporaryStat.EMMP);
        cancelEffectFromTemporaryStat(CharacterTemporaryStat.MaxHP);
        cancelEffectFromTemporaryStat(CharacterTemporaryStat.IndieMMP);
        dispelSummons();
        checkFollow();
        dotHP = 0;
        lastDOTTime = 0;
        if (GameConstants.isAzwanMap(getMapId())) {
            //client.write(CWvsContext.showAzwanKilled());
        }
        if (!GameConstants.isBeginnerJob(job) && !inPVP() && !GameConstants.isAzwanMap(getMapId())) {
            int charms = getItemQuantity(5130000, false);
            if (charms > 0) {
                MapleInventoryManipulator.removeById(client, MapleInventoryType.CASH, 5130000, 1, true, false);
                charms--;
                if (charms > 0xFF) {
                    charms = 0xFF;
                }
                client.SendPacket(EffectPacket.useCharm((byte) charms, (byte) 0, true));
                deadstats.add(DeadUIStats.bOnDeadProtectExpMaplePoint);
            } else {
                float diepercentage;
                long expforlevel = getNeededExp();

                if (FieldLimitType.NoExpDecrease.check(map)) {
                    diepercentage = 0.0f;
                } else if (map.getSharedMapResources().town /*|| FieldLimitType.RegularExpLoss.check(map)*/) {
                    diepercentage = 0.01f;
                } else {
                    diepercentage = (float) (0.1f - ((traits.get(MapleTraitType.charisma).getLevel() / 20) / 100f) - (stats.expLossReduceR / 100f));
                }
                long expAfterDeath = (exp - (long) ((double) expforlevel * diepercentage));
                if (expAfterDeath < 0) {
                    expAfterDeath = 0;
                }
                this.exp = expAfterDeath;
            }
            this.updateSingleStat(MapleStat.EXP, this.exp);
        }
        if (pyramidSubway != null) {
            stats.setHp((short) 50, this);
            setXenonSurplus((short) 0);
            pyramidSubway.fail(this);
        }

        // Sent dead response
        //this.dropMessage(5, "Dead UI " + HexTool.toString(CUserLocal.openUIOnDead(deadstats)));
        Timer.BuffTimer.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
                client.SendPacket(CUserLocal.openUIOnDead(deadstats));
            }
        }, 2000); // ugly I know.. but given the way odin implements death detection, this is necessary. Client needs to receive the update HP data first
    }

    public void updatePartyMemberHP() {
        if (party != null && client.getChannelServer() != null) {
            final int channel = client.getChannel();
            for (MaplePartyCharacter partychar : party.getMembers()) {
                if (partychar != null && partychar.getMapid() == getMapId() && partychar.getChannel() == channel) {
                    final User other = client.getChannelServer().getPlayerStorage().getCharacterByName(partychar.getName());
                    if (other != null) {
                        other.getClient().SendPacket(CField.updatePartyMemberHP(getId(), stats.getHp(), stats.getCurrentMaxHp()));
                    }
                }
            }
        }
    }

    public void receivePartyMemberHP() {
        if (party == null) {
            return;
        }
        int channel = client.getChannel();
        for (MaplePartyCharacter partychar : party.getMembers()) {
            if (partychar != null && partychar.getMapid() == getMapId() && partychar.getChannel() == channel) {
                User other = client.getChannelServer().getPlayerStorage().getCharacterByName(partychar.getName());
                if (other != null) {
                    client.SendPacket(CField.updatePartyMemberHP(other.getId(), other.getStat().getHp(), other.getStat().getCurrentMaxHp()));
                }
            }
        }
    }

    public void healHP(int delta) {
        addHP(delta);
        client.SendPacket(EffectPacket.showOwnHpHealed(delta));
        getMap().broadcastMessage(this, EffectPacket.showHpHealed(getId(), delta), false);
    }

    public void healMP(int delta) {
        addMP(delta);
        client.SendPacket(EffectPacket.showOwnHpHealed(delta));
        getMap().broadcastMessage(this, EffectPacket.showHpHealed(getId(), delta), false);
    }

    public int getCurrentHP() {
        return stats.getHp();
    }

    public int getMaxHP() {
        return stats.getCurrentMaxHp();
    }

    /**
     * Convenience function which adds the supplied parameter to the current hp then directly does a updateSingleStat.
     *
     * @see User#setHp(int)
     * @param delta
     */
    public void addHP(int delta) {
        if (stats.setHp(stats.getHp() + delta, this)) {
            updateSingleStat(MapleStat.HP, stats.getHp());
        }
    }

    /**
     * Convenience function which adds the supplied parameter to the current mp then directly does a updateSingleStat.
     *
     * @see User#setMp(int)
     * @param delta
     */
    public void addMP(int delta) {
        addMP(delta, false);
    }

    public void addMP(int delta, boolean ignore) {
        if ((delta < 0 && GameConstants.isDemonSlayer(getJob())) || !GameConstants.isDemonSlayer(getJob()) || ignore) {
            if (stats.setMp(stats.getMp() + delta, this)) {
                updateSingleStat(MapleStat.MP, stats.getMp());
            }
        }
    }

    public void addMPHP(int hpDiff, int mpDiff) {
        Map<MapleStat, Long> statups = new EnumMap<>(MapleStat.class
        );

        if (stats.setHp(stats.getHp() + hpDiff, this)) {
            statups.put(MapleStat.HP, Long.valueOf(stats.getHp()));
        }
        if ((mpDiff < 0 && GameConstants.isDemonSlayer(getJob())) || !GameConstants.isDemonSlayer(getJob())) {
            if (stats.setMp(stats.getMp() + mpDiff, this)) {
                statups.put(MapleStat.MP, Long.valueOf(stats.getMp()));
            }
        }
        if (statups.size() > 0) {
            client.SendPacket(CWvsContext.updatePlayerStats(statups, this));
        }
    }

    public void updateSingleStat(MapleStat stat, long newval) {
        updateSingleStat(stat, newval, false);
    }

    /**
     * Updates a single stat of this MapleCharacter for the client. This method only creates and sends an update packet, it does not update
     * the stat stored in this MapleCharacter instance.
     *
     * @param stat
     * @param newval
     * @param itemReaction
     */
    public void updateSingleStat(MapleStat stat, long newval, boolean itemReaction) {
        Map<MapleStat, Long> statup = new EnumMap<>(MapleStat.class
        );
        statup.put(stat, newval);
        client.SendPacket(CWvsContext.updatePlayerStats(statup, itemReaction, this));
    }

    public void setGmLevel(byte level) {
        this.gmLevel = level;
    }

    public void familyRep(long prevexp, long needed, boolean leveled) {
        if (mfc != null) {
            long onepercent = needed / 100;
            if (onepercent <= 0) {
                return;
            }
            int percentrep = (int) (getExp() / onepercent - prevexp / onepercent);
            if (leveled) {
                percentrep = 100 - percentrep + (level / 2);
            }
            if (percentrep > 0) {
                int sensen = World.Family.setRep(mfc.getFamilyId(), mfc.getSeniorId(), percentrep * 10, level, name);
                if (sensen > 0) {
                    World.Family.setRep(mfc.getFamilyId(), sensen, percentrep * 5, level, name); //and we stop here
                }
            }
        }
    }

    /**
     * Proxy method for obtaining EXP -- quests, etc
     *
     * @param totalEXPGained - The exp that the player should obtain from quest
     * @param show - Should the EXP gain be shown to the player?
     * @param bOnQuest - Shows the EXP obtained in chat
     * @param highestDamage
     */
    public void gainExp(final long totalEXPGained, boolean show, boolean bOnQuest, boolean highestDamage) {
        EnumMap<ExpGainTypes, Integer> expIncreaseStats = new EnumMap<>(ExpGainTypes.class
        );

        gainExp(totalEXPGained, 0, show, highestDamage, bOnQuest, 0, expIncreaseStats);
    }

    /**
     * Obtains experience points after killing a monster
     *
     * @param totalEXPGained - The exp that the player should obtain from quest
     * @param bonusEXPGained - The calculated bonus EXP such as 2x card, fairy pendant, etc
     * @param show - Should the EXP gain be shown to the player?
     * @param highestDamage - Shows a white EXP gain text if the highest damage is dealt by this player
     * @param bOnQuest - Shows the EXP obtained in chat
     * @param burningFieldBonusEXPRate - The amount of EXP rate from burning field
     * @param expIncreaseStats
     */
    public void gainExp(final long totalEXPGained, final long bonusEXPGained, boolean show, boolean highestDamage, boolean bOnQuest,
            int burningFieldBonusEXPRate, EnumMap<ExpGainTypes, Integer> expIncreaseStats) {
        if (!isAlive() || totalEXPGained <= 0 || bonusEXPGained < 0) { // dead or long overflow.
            return;
        }

        long needed = getNeededExp();
        if (level >= GameConstants.maxLevel) {
            setExp(0);
        } else {
            boolean leveled = false;

            long toGiveEXP = totalEXPGained + bonusEXPGained;
            while (toGiveEXP > 0) {
                long newRemainingExpAmount = Math.min(getNeededExp(), toGiveEXP); // whatever that's lower..

                toGiveEXP -= newRemainingExpAmount; // decrease remaining EXP first
                exp += newRemainingExpAmount; // add to character's exp

                while (exp >= getNeededExp()) {
                    if (level >= GameConstants.maxLevel) {
                        setExp(0);
                        break;
                    } else {
                        int currLevel = level;
                        setExp(exp - getNeededExp());

                        if (this.isBurning && currLevel >= 10 && currLevel <= 150) {
                            levelUp();
                            levelUp();
                        }
                    }
                }
            }
            familyRep(getNeededExp(), needed, leveled);
        }
        if (totalEXPGained != 0) {
            updateSingleStat(MapleStat.EXP, getExp());

            if (show) {
                int truncatedEXP = (int) totalEXPGained;
                if (truncatedEXP <= 0 || truncatedEXP >= Integer.MAX_VALUE) {
                    truncatedEXP = Integer.MAX_VALUE;
                }

                client.SendPacket(InfoPacket.gainExp(truncatedEXP, highestDamage, bOnQuest, (byte) 0, burningFieldBonusEXPRate, expIncreaseStats));
            }

            // Others
            if (totalEXPGained > 0) {
                stats.checkEquipLevels(this, totalEXPGained); //gms like
            }
        }
    }

    public void forceReAddItemNoUpdate(Item item, MapleInventoryType type) {
        getInventory(type).removeSlot(item.getPosition());
        getInventory(type).addFromDB(item);
    }

    public void forceReAddItem(Item item, MapleInventoryType type) { //used for stuff like durability, item exp/level, probably owner?
        forceReAddItemNoUpdate(item, type);
        type = MapleInventoryType.EQUIP;
        if (type != MapleInventoryType.UNDEFINED) {

            List<ModifyInventory> mod = new ArrayList<>();
            mod.add(new ModifyInventory(ModifyInventoryOperation.Remove, item));
            mod.add(new ModifyInventory(ModifyInventoryOperation.AddItem, item));
            client.SendPacket(CWvsContext.inventoryOperation(true, mod));
        }
    }

    public void forceReAddItemFlag(Item item, MapleInventoryType type) { //used for flags
        forceReAddItemNoUpdate(item, type);
        if (type != MapleInventoryType.UNDEFINED) {

            List<ModifyInventory> mod = new ArrayList<>();
            mod.add(new ModifyInventory(ModifyInventoryOperation.Remove, item));
            mod.add(new ModifyInventory(ModifyInventoryOperation.AddItem, item));
            client.SendPacket(CWvsContext.inventoryOperation(true, mod));
        }
    }

    public void forceReAddItemBook(Item item, MapleInventoryType type) { //used for mbook
        forceReAddItemNoUpdate(item, type);
        if (type != MapleInventoryType.UNDEFINED) {
            client.SendPacket(CWvsContext.upgradeBook(item, this));
        }
    }

    public void silentPartyUpdate() {
        if (party != null) {
            World.Party.updateParty(party.getId(), PartyOperation.SILENT_UPDATE, new MaplePartyCharacter(this));
        }
    }

    public boolean isDonator() {
        return gmLevel >= 1;
    }

    public boolean isIntern() {
        return gmLevel >= 2;
    }

    public boolean isGM() {
        return gmLevel >= 3;
    }

    public boolean isAdmin() {
        return gmLevel >= 4;
    }

    public boolean isDeveloper() {
        return gmLevel >= 5;
    }

    public boolean hasGmLevel(int level) {
        return gmLevel >= level;
    }

    public final void expandInventoryRequest(MapleInventoryType nType, int nAmount) {

    }

    public final MapleInventory getInventory(MapleInventoryType type) {
        return inventory[type.ordinal()];
    }

    public final MapleInventory[] getInventorys() {
        return inventory;
    }

    public final void expirationTask(boolean pending, boolean firstLoad) {
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        if (pending) {
            if (pendingExpiration != null) {
                for (Integer z : pendingExpiration) {
                    List<Integer> items = new ArrayList<>();
                    items.add(z);
                    MessageInterface expire;
                    if (!ii.isCash(z)) {
                        expire = new GeneralItemExpirationMessage(items);
                    } else {
                        expire = new ExpiredCashItemMessage(z);
                    }
                    client.SendPacket(CWvsContext.messagePacket(expire));
                    if (!firstLoad) {
                        final Pair<Integer, String> replace = ii.replaceItemInfo(z);
                        if (replace != null && replace.left > 0 && replace.right.length() > 0) {
                            dropMessage(5, replace.right);
                        }
                    }
                }
            }
            pendingExpiration = null;
            if (pendingSkills != null) {
                client.SendPacket(CWvsContext.updateSkills(pendingSkills, false));
                for (Skill z : pendingSkills.keySet()) {
                    client.SendPacket(CWvsContext.broadcastMsg(5, "[" + SkillFactory.getSkillName(z.getId()) + "] skill has expired and will not be available for use."));
                }
            } //not real msg
            pendingSkills = null;
            return;
        }
        final MapleQuestStatus stat = getQuestNoAdd(MapleQuest.getInstance(GameConstants.PENDANT_SLOT));
        long expiration;
        final List<Integer> ret = new ArrayList<>();
        final long currenttime = System.currentTimeMillis();
        final List<Triple<MapleInventoryType, Item, Boolean>> toberemove = new ArrayList<>(); // This is here to prevent deadlock.
        final List<Item> tobeunlock = new ArrayList<>(); // This is here to prevent deadlock.

        for (final MapleInventoryType inv : MapleInventoryType.values()) {
            for (final Item item : getInventory(inv)) {
                expiration = item.getExpiration();

                if ((expiration != -1 && !InventoryConstants.isPet(item.getItemId()) && currenttime > expiration) || (firstLoad && ii.isLogoutExpire(item.getItemId()))) {
                    if (ItemFlag.LOCK.check(item.getFlag())) {
                        tobeunlock.add(item);
                    } else if (currenttime > expiration) {
                        toberemove.add(new Triple<>(inv, item, false));
                    }
                } else if (item.getItemId() == 5000054 && item.getPet() != null && item.getPet().getSecondsLeft() <= 0) {
                    toberemove.add(new Triple<>(inv, item, false));
                } else if (item.getPosition() == -59) {
                    if (stat == null || stat.getCustomData() == null || Long.parseLong(stat.getCustomData()) < currenttime) {
                        toberemove.add(new Triple<>(inv, item, true));
                    }
                }
            }
        }
        Item item;
        for (final Triple<MapleInventoryType, Item, Boolean> itemz : toberemove) {
            item = itemz.getMid();
            getInventory(itemz.getLeft()).removeItem(item.getPosition(), item.getQuantity(), false);
            if (itemz.getRight() && getInventory(GameConstants.getInventoryType(item.getItemId())).getNextFreeSlot() > -1) {
                item.setPosition(getInventory(GameConstants.getInventoryType(item.getItemId())).getNextFreeSlot());
                getInventory(GameConstants.getInventoryType(item.getItemId())).addFromDB(item);
            } else {
                ret.add(item.getItemId());
            }
            if (!firstLoad) {
                final Pair<Integer, String> replace = ii.replaceItemInfo(item.getItemId());
                if (replace != null && replace.left > 0) {
                    Item theNewItem;
                    if (GameConstants.getInventoryType(replace.left) == MapleInventoryType.EQUIP) {
                        theNewItem = ii.getEquipById(replace.left);
                        theNewItem.setPosition(item.getPosition());
                    } else {
                        theNewItem = new Item(replace.left, item.getPosition(), (short) 1, (byte) 0);
                    }
                    getInventory(itemz.getLeft()).addFromDB(theNewItem);
                }
            }
        }
        for (final Item itemz : tobeunlock) {
            itemz.setExpiration(-1);
            itemz.setFlag((byte) (itemz.getFlag() - ItemFlag.LOCK.getValue()));
        }
        this.pendingExpiration = ret;

        final Map<Skill, SkillEntry> skilz = new HashMap<>();
        final List<Skill> toberem = new ArrayList<>();
        for (Entry<Skill, SkillEntry> skil : skills.entrySet()) {
            if (skil.getValue().expiration != -1 && currenttime > skil.getValue().expiration) {
                toberem.add(skil.getKey());
            }
        }
        for (Skill skil : toberem) {
            skilz.put(skil, new SkillEntry(0, (byte) 0, -1));
            this.skills.remove(skil);
            changed_skills = true;
        }
        this.pendingSkills = skilz;
        if (stat != null && stat.getCustomData() != null && Long.parseLong(stat.getCustomData()) < currenttime) { //expired bro
            quests.remove(MapleQuest.getInstance(7830));
            quests.remove(MapleQuest.getInstance(GameConstants.PENDANT_SLOT));
        }
    }

    public void refreshBattleshipHP() {
        if (getJob() == 592) {
            client.SendPacket(CWvsContext.giveKilling(currentBattleshipHP()));
        }
    }

    public MapleShop getShop() {
        return shop;
    }

    public void setShop(MapleShop shop) {
        this.shop = shop;
    }

    public long getMeso() {
        return meso;
    }

    public final int[] getSavedLocations() {
        return savedLocations;
    }

    public int getSavedLocation(SavedLocationType type) {
        return savedLocations[type.getValue()];
    }

    public void saveLocation(SavedLocationType type) {
        savedLocations[type.getValue()] = getMapId();
        changed_savedlocations = true;
    }

    public void saveLocation(SavedLocationType type, int mapz) {
        savedLocations[type.getValue()] = mapz;
        changed_savedlocations = true;
    }

    public void clearSavedLocation(SavedLocationType type) {
        savedLocations[type.getValue()] = -1;
        changed_savedlocations = true;
    }

    public void gainMeso(long gain, boolean show) {
        gainMeso(gain, show, false);
    }

    public void gainMeso(long gain, boolean show, boolean inChat) {
        if (meso + gain < 0 || meso + gain > ServerConstants.MAX_MESOS) {
            client.SendPacket(CWvsContext.enableActions());
            return;
        }
        meso += gain;
        updateSingleStat(MapleStat.MESO, meso, false);
        client.SendPacket(CWvsContext.enableActions());
        if (show) {
            client.SendPacket(InfoPacket.showMesoGain(gain, inChat));
        }
    }

    public void controlMonster(Mob monster, boolean aggro) {
        if (clone || monster == null) {
            return;
        }
        monster.setController(this);
        controlledLock.writeLock().lock();
        try {
            controlled.add(monster);
        } finally {
            controlledLock.writeLock().unlock();
        }
        client.SendPacket(MobPacket.controlMonster(monster, false, aggro, GameConstants.isAzwanMap(getMapId())));

        monster.sendStatus(client);
    }

    public void stopControllingMonster(Mob monster) {
        if (clone || monster == null) {
            return;
        }
        controlledLock.writeLock().lock();
        try {
            if (controlled.contains(monster)) {
                controlled.remove(monster);
            }
        } finally {
            controlledLock.writeLock().unlock();
        }
    }

    public void checkMonsterAggro(Mob monster) {
        if (clone || monster == null) {
            return;
        }
        if (monster.getController() == this) {
            monster.setControllerHasAggro(true);
        } else {
            monster.switchController(this, true);
        }
    }

    public int getControlledSize() {
        return controlled.size();
    }

    public int getAccountID() {
        return accountid;
    }

    // <editor-fold defaultstate="visible" desc="Quests"> 
    public void mobKilled(String name, int id, int lastSkillIdUsed) {

        for (MapleQuestStatus q : quests.values()) {
            if (q.getStatus() != MapleQuestState.Started || !q.hasMobKills()) {
                continue;
            }

            if (q.mobKilled(id, lastSkillIdUsed)) {
                client.SendPacket(InfoPacket.updateQuestMobKills(q));
                dropMessage(-7, String.format("%s %s / %s", name, q.getMobKills(id), q.getQuest().getRelevantMobs().get(id)));
                if (q.getQuest().canComplete(this, null)) {
                    client.SendPacket(CWvsContext.showQuestCompletion(q.getQuest().getId()));
                }
            }
        }
    }

    public final List<MapleQuestStatus> getStartedQuests() {
        List<MapleQuestStatus> ret = new LinkedList<>();
        quests.values().stream().filter((q) -> (q.getStatus() == MapleQuestState.Started && !q.getQuest().isBlocked())).forEach((q) -> {
            ret.add(q);
        });
        return ret;
    }

    public final List<MapleQuestStatus> getCompletedQuests() {
        List<MapleQuestStatus> ret = new LinkedList<>();
        quests.values().stream().filter((q) -> (q.getStatus() == MapleQuestState.Completed && !q.getQuest().isBlocked())).forEach((q) -> {
            ret.add(q);
        });
        return ret;
    }

    public final List<Pair<Integer, Long>> getCompletedMedals() {
        List<Pair<Integer, Long>> ret = new ArrayList<>();
        quests.values().stream().filter((q) -> (q.getStatus() == MapleQuestState.Completed && !q.getQuest().isBlocked() && q.getQuest().getMedalItem() > 0 && GameConstants.getInventoryType(q.getQuest().getMedalItem()) == MapleInventoryType.EQUIP)).forEach((q) -> {
            ret.add(new Pair<>(q.getQuest().getId(), q.getCompletionTime()));
        });
        return ret;
    }
    // </editor-fold> 

    // <editor-fold defaultstate="visible" desc="Skills"> 
    public Map<Skill, SkillEntry> getSkills() {
        return Collections.unmodifiableMap(skills);
    }

    public int getTotalSkillLevel(final Skill skill) {
        if (skill == null) {
            return 0;
        }
        final SkillEntry ret = skills.get(skill);
        if (ret == null || ret.skillevel <= 0) {
            return 0;
        }

        return Math.min(skill.getTrueMax(), ret.skillevel + (skill.isBeginnerSkill() ? 0 : (stats.combatOrders + (skill.getMaxLevel() > 10 ? stats.incAllskill : 0) + stats.getSkillIncrement(skill.getId()))));
    }

    public int getAllSkillLevels() {
        int rett = 0;
        for (Entry<Skill, SkillEntry> ret : skills.entrySet()) {
            if (!ret.getKey().isBeginnerSkill() && !ret.getKey().isSpecialSkill() && ret.getValue().skillevel > 0) {
                rett += ret.getValue().skillevel;
            }
        }
        return rett;
    }

    public long getSkillExpiry(final Skill skill) {
        if (skill == null) {
            return 0;
        }
        final SkillEntry ret = skills.get(skill);
        if (ret == null || ret.skillevel <= 0) {
            return 0;
        }
        return ret.expiration;
    }

    public int getSkillLevel(final Skill skill) {
        if (skill == null) {
            return 0;
        }
        final SkillEntry ret = skills.get(skill);
        if (ret == null || ret.skillevel <= 0) {
            return 0;
        }
        return ret.skillevel;
    }

    public byte getMasterLevel(final int skill) {
        return getMasterLevel(SkillFactory.getSkill(skill));
    }

    public byte getMasterLevel(final Skill skill) {
        final SkillEntry ret = skills.get(skill);
        if (ret == null) {
            return 0;
        }
        return ret.masterlevel;
    }

    public void resetSpToZero() {
        resetSP(0);
    }
    // </editor-fold> 

    public void reloadSkills() {
        HashMap<Skill, SkillEntry> sa = new HashMap<>();
        for (Skill cSkills : SkillFactory.getAllSkills()) {
            if (GameConstants.isApplicableSkill(cSkills.getId()) && cSkills.canBeLearnedBy(getJob()) && !cSkills.isInvisible()) { //no db/additionals/resistance skills
                sa.put(cSkills, new SkillEntry((byte) getSkillLevel(cSkills), (byte) getMasterLevel(cSkills), SkillFactory.getDefaultSExpiry(cSkills)));
            }
        }
        changeSkillsLevel(sa);
    }

    public void resetAp() {
        int usedAp = (stats.getStr() + stats.getDex() + stats.getInt() + stats.getLuk()) - 16;
        remainingAp += usedAp;
        stats.str = 4;
        stats.dex = 4;
        stats.int_ = 4;
        stats.luk = 4;
        updateSingleStat(MapleStat.AVAILABLEAP, remainingAp);
        updateSingleStat(MapleStat.STR, 4);
        updateSingleStat(MapleStat.DEX, 4);
        updateSingleStat(MapleStat.INT, 4);
        updateSingleStat(MapleStat.LUK, 4);
    }

    public void levelUp() {
        if (getLevel() >= GameConstants.maxLevel) {
            return;
        }

        final MapleJob currentJob = MapleJob.getById(job);

        // Max HP and MP allocation
        int maxhp = stats.getMaxHp();
        int maxmp = stats.getMaxMp();

        // Bonus HP Gain
        if (ServerConstants.BUFFED_HP_GAIN) {
            maxhp += Randomizer.rand(40, 65);
        }

        if (GameConstants.isBeginnerJob(job)) { // Beginner
            maxhp += Randomizer.rand(12, 16);
            maxmp += Randomizer.rand(10, 12);
        } else if (job >= 3100 && job <= 3112) { // Warrior
            maxhp += Randomizer.rand(48, 52);
        } else if ((job >= 100 && job <= 132) || (job >= 1100 && job <= 1111)) { // Warrior
            maxhp += Randomizer.rand(48, 52);
            maxmp += Randomizer.rand(4, 6);
        } else if ((job >= 200 && job <= 232) || (job >= 1200 && job <= 1211)) { // Magician
            maxhp += Randomizer.rand(10, 14);
            maxmp += Randomizer.rand(48, 52);
        } else if (job >= 3200 && job <= 3212) { //battle mages get their own little neat thing
            maxhp += Randomizer.rand(20, 24);
            maxmp += Randomizer.rand(42, 44);
        } else if ((job >= 300 && job <= 322) || (job >= 400 && job <= 434) || (job >= 1300 && job <= 1311) || (job >= 1400 && job <= 1411) || (job >= 3300 && job <= 3312) || (job >= 2300 && job <= 2312)) { // Bowman, Thief, Wind Breaker and Night Walker
            maxhp += Randomizer.rand(20, 24);
            maxmp += Randomizer.rand(14, 16);
        } else if ((job >= 510 && job <= 512) || (job >= 1510 && job <= 1512)) { // Pirate
            maxhp += Randomizer.rand(37, 41);
            maxmp += Randomizer.rand(18, 22);
        } else if ((job >= 500 && job <= 532) || (job >= 3500 && job <= 3512) || job == 1500) { // Pirate
            maxhp += Randomizer.rand(20, 24);
            maxmp += Randomizer.rand(18, 22);
        } else if (job >= 2100 && job <= 2112) { // Aran
            maxhp += Randomizer.rand(50, 52);
            maxmp += Randomizer.rand(4, 6);
        } else if (job >= 2200 && job <= 2218) { // Evan
            maxhp += Randomizer.rand(12, 16);
            maxmp += Randomizer.rand(50, 52);
        } else if (job >= 5100 && job <= 5112) { // Evan
            maxhp += Randomizer.rand(48, 52);
            maxmp += Randomizer.rand(4, 6);
        } else { // GameMaster
            maxhp += Randomizer.rand(50, 100);
            maxmp += Randomizer.rand(50, 100);
        }
        maxmp += stats.getTotalInt() / 10;

        maxhp = Math.min(GameConstants.maxHP, Math.abs(maxhp));
        maxmp = Math.min(GameConstants.maxMP, Math.abs(maxmp));
        if (GameConstants.isDemonSlayer(job)) { //TODO: use shield instead of df per job
            maxmp = GameConstants.getMPByJob(job);
        }
        if (GameConstants.isZero(job) || GameConstants.isKanna(job)) {
            maxmp = 100;
        }
        level++;

        // Update stats related stuff
        final Map<MapleStat, Long> statup = new EnumMap<>(MapleStat.class
        );

        statup.put(MapleStat.MAXHP, (long) maxhp);
        statup.put(MapleStat.IndieMMP, (long) maxmp);
        statup.put(MapleStat.HP, (long) maxhp);
        statup.put(MapleStat.MP, (long) maxmp);
        statup.put(MapleStat.EXP, exp);
        statup.put(MapleStat.LEVEL, (long) level);

        // AP allocation
        if (level < 10) { //Auto STR up until 10.
            stats.str += 5;
            remainingAp = 0;

            statup.put(MapleStat.STR, (long) stats.getStr());
        } else if (level == 10) {//Auto AP Reset when hitting level 10.
            remainingAp += 5;
            resetAp();
        } else {
            remainingAp += 5;
        }

        // Gives the player SP for the current job 
        // NOTE: At this point, the player's level have already +1! Keep that in mind.
        addLevelSkills();

        if (level >= 10) { // At least Lv. 10 to gain 3 SPs, upon job advancement 5 SPs will be given.
            if (GameConstants.isExtendedSpJob(job)) {
                if (GameConstants.isZero(job) && level >= 100) {
                    remainingSp[0] += 3; //alpha gets 3sp
                    remainingSp[1] += 3; //beta gets 3sp
                } else {
                    final boolean isCurrentLevelforNextJobAdvancement = level == currentJob.getMaxLevelForNextAdvancement() && !currentJob.isFinalJob();
                    final boolean haveCurrentLevelExceedNextJobAdvancement = level > currentJob.getMaxLevelForNextAdvancement();

                    final int basicSPGain;
                    if (level <= 110) {
                        basicSPGain = 3;
                    } else if (level <= 120) {
                        basicSPGain = 4;
                    } else if (level <= 130) {
                        basicSPGain = 5;
                    } else if (level <= 140) {
                        basicSPGain = 6;
                    } else {
                        basicSPGain = 0;
                    }

                    int spGain_thisJob = haveCurrentLevelExceedNextJobAdvancement ? 0 : basicSPGain;
                    int spGain_NextJob = haveCurrentLevelExceedNextJobAdvancement ? basicSPGain : 0;

                    // SPs for job advancement later..
                    if (isCurrentLevelforNextJobAdvancement) {
                        // Dual blade, zero, and beast tamer dont gain on job advancement.
                        if (!GameConstants.isDualBladeNoSP(job) && !GameConstants.isZero(job) && !GameConstants.isBeastTamer(job)) {
                            spGain_NextJob += 5;
                        }
                    }

                    // Handle additional SP gain at levels ending with 0,3,6,9 during fourth job
                    if ((currentJob.isQuickSPGainJob() && !haveCurrentLevelExceedNextJobAdvancement)
                            || (currentJob.isQuickSPGainForNextJob() && (isCurrentLevelforNextJobAdvancement || haveCurrentLevelExceedNextJobAdvancement))) {
                        String lvString = String.valueOf(level);
                        String lastDigitOfLevel = lvString.substring(lvString.length() - 1, lvString.length());

                        switch (lastDigitOfLevel) {
                            case "0":
                            case "3":
                            case "6":
                            case "9":
                                if (isCurrentLevelforNextJobAdvancement) {
                                    spGain_NextJob *= 2;
                                } else {
                                    spGain_thisJob *= 2;
                                }
                                break;
                        }
                    }

                    if (currentJob.getJobSkillBook() != -1) { // if not beginner.
                        remainingSp[currentJob.getJobSkillBook()] += spGain_thisJob;
                        // System.out.println("Gaining "+spGain_thisJob+" SP for this job");
                    }
                    if (currentJob.getJobSkillBook() + 1 < remainingSp.length) { // prevent out of range, in case someone is drunk
                        remainingSp[currentJob.getJobSkillBook() + 1] += spGain_NextJob;
                        // System.out.println("Gaining "+spGain_NextJob+" SP for next job");
                    }
                }
            } else {
                // if it is not an extended SP job, we would want to give only during advancement...
            }

            if (GameConstants.isDualBlade(job)) {
                if (level < 100) {
                    gainSP(5, GameConstants.getSkillBook(job, 0));
                } else if (level == 100) {
                    gainSP(255, GameConstants.getSkillBook(job, 0));
                }
            }

            if (GameConstants.isBeastTamer(job)) {
                gainSP(3);
            }
        }

        // Hyper SP
        if (!GameConstants.isZero(job)) {  //no hypers for zero
            switch (level) {
                case 140:
                case 160:
                case 190:
                    gainHyperSP(0, 1);
                    gainHyperSP(1, 1);
                    break;
                case 150:
                case 170:
                    gainHyperSP(0, 1);
                    gainHyperSP(2, 1);
                    break;
                case 180:
                    gainHyperSP(1, 1);
                    break;
                case 200:
                    gainHyperSP(0, 1);
                    gainHyperSP(1, 1);
                    gainHyperSP(2, 1);
                    break;
            }
        }

        // Handle Resolution Time (Skill Level Up)
        if (GameConstants.isZero(job)) {
            switch (level) {
                case 101:
                    //for (int i = 40000; i <= 40055; i++) {
                    MapleQuestStatus pZeroTutorial = getQuestNoAdd(MapleQuest.getInstance(40054));
                    pZeroTutorial.setCompletionTime(System.currentTimeMillis());
                    pZeroTutorial.getQuest().complete(this, 0);
                //}
                case 120:
                    changeSkillLevel(SkillFactory.getSkill(Zero.RESOLUTION_TIME), (byte) 1, (byte) 5);
                    break;
                case 140:
                    changeSkillLevel(SkillFactory.getSkill(Zero.RESOLUTION_TIME), (byte) 2, (byte) 5);
                    break;
                case 160:
                    changeSkillLevel(SkillFactory.getSkill(Zero.RESOLUTION_TIME), (byte) 3, (byte) 5);
                    break;
                case 180:
                    changeSkillLevel(SkillFactory.getSkill(Zero.RESOLUTION_TIME), (byte) 4, (byte) 5);
                    break;
                case 200:
                    changeSkillLevel(SkillFactory.getSkill(Zero.RESOLUTION_TIME), (byte) 5, (byte) 5);
                    break;
            }
        }

        // Hyper Skill Distribution
        hyperSkillRequest();
        /*if (level == 200) {
            giveHyperSkills();
        }*/

        statup.put(MapleStat.AVAILABLEAP, (long) remainingAp);
        statup.put(MapleStat.AVAILABLESP, (long) remainingSp[GameConstants.getSkillBook(job, level)]);

        // Packet + stats updates
        //     client.write(CField.getAndroidTalkStyle(2008, "Yay", 2));
        stats.setInfo(maxhp, maxmp, maxhp, maxmp);
        client.SendPacket(CWvsContext.updatePlayerStats(statup, this));
        //map.broadcastMessage(this, EffectPacket.showForeignEffect(getId(), UserEffectCodes.LevelUp), true); // lol this is buggy.
        client.SendPacket(EffectPacket.showForeignEffect(getId(), UserEffectCodes.LevelUp));
        characterCard.recalcLocalStats(this);
        stats.recalcLocalStats(this);

        // Etc!
        silentPartyUpdate();
        guildUpdate();
        familyUpdate();
        autoJobAdvance();
        AndroidEmotionChanger.changeEmotion(this, 10);

        if (GameConstants.isZero(job)) {
            checkZeroWeapon();
            checkZeroTranscendent();
        }
        if (level == GameConstants.maxLevel) {
            setExp(0);
            StringBuilder sb = new StringBuilder();
            sb.append("[" + ServerConstants.SERVER_NAME + "] ");
            addMedalString(client.getPlayer(), sb);
            sb.append(getName()).append(" has reached Level ").append(level).append("!");
            World.Broadcast.broadcastMessage(CField.getGameMessage(sb.toString(), (short) 7));
        }
        userRewardRequest(level);
        //checkCustomReward(level);
    }

    /*
     *  Hyper Skill Distribution Hander
     *  @author Mazen
     *  @author Arca
     *
     *  @purpose Gives the player hyper skills if they have reached the required levels for each skill.
     */
    public void hyperSkillRequest() {

        HashMap<Skill, SkillEntry> skillMap = new HashMap<>();
        for (Skill selectSkill : SkillFactory.getAllSkills()) {
            if (GameConstants.isApplicableSkill(selectSkill.getId()) && selectSkill.canBeLearnedBy(getJob()) && !selectSkill.isInvisible()) {

                boolean bReqLevel = level >= GameConstants.getHyperSkillRequiredLevel(selectSkill.getId()) && GameConstants.getHyperSkillRequiredLevel(selectSkill.getId()) != 0;

                if (selectSkill.isHyper() && bReqLevel) {
                    skillMap.put(selectSkill, new SkillEntry((byte) selectSkill.getMaxLevel(), (byte) selectSkill.getMaxLevel(), SkillFactory.getDefaultSExpiry(selectSkill)));
                }
            }
        }
        changeSkillsLevel(skillMap);
    }

    public void giveHyperSkills() {
        HashMap<Skill, SkillEntry> skillMap = new HashMap<>();
        for (Skill selectSkill : SkillFactory.getAllSkills()) {
            if (GameConstants.isApplicableSkill(selectSkill.getId()) && selectSkill.canBeLearnedBy(getJob()) && !selectSkill.isInvisible()) {
                if (selectSkill.isHyper()) {
                    skillMap.put(selectSkill, new SkillEntry((byte) selectSkill.getMaxLevel(), (byte) selectSkill.getMaxLevel(), SkillFactory.getDefaultSExpiry(selectSkill)));
                }
            }
        }
        changeSkillsLevel(skillMap);
    }

    /*
     *  Mastery Book Handling
     *  @author Mazen Massoud
     * 
     *  @purpose Provide a list of all skills currently available for the use of a Mastery Book to increase Skill Master Level.
     *  @param bLv30Book - If true, provides the skills available for the Lv. 30 Mastery Books. Otherwise defaults to the Lv. 20 Mastery Books.
     **/
    public List<Integer> masteryBookRequest(boolean bLv30Book) {
        final List<Integer> nSkill = new ArrayList<>();
        for (Skill selectSkill : SkillFactory.getAllSkills()) {
            if (GameConstants.isApplicableSkill(selectSkill.getId()) && selectSkill.canBeLearnedBy(getJob()) && !selectSkill.isInvisible()) {

                if (bLv30Book) {
                    boolean bMasteryBookResult = (getSkillLevel(selectSkill) < 30)
                            && (getSkillLevel(selectSkill) >= 15)
                            && (selectSkill.getMasterLevel() < 30)
                            && ((selectSkill.getMasterLevel() < selectSkill.getTrueMax()))
                            && ((selectSkill.getMasterLevel() + 10 <= selectSkill.getTrueMax()))
                            && ((selectSkill.getMasterLevel() > 0));

                    if (bMasteryBookResult) {
                        nSkill.add(selectSkill.getId());
                    }
                } else {
                    boolean bMasteryBookResult = (getSkillLevel(selectSkill) < 20)
                            && (getSkillLevel(selectSkill) >= 10)
                            && (selectSkill.getMasterLevel() < 20)
                            && ((selectSkill.getMasterLevel() < selectSkill.getTrueMax()))
                            && ((selectSkill.getMasterLevel() + 10 <= selectSkill.getTrueMax()))
                            && ((selectSkill.getMasterLevel() > 0));

                    if (bMasteryBookResult) {
                        nSkill.add(selectSkill.getId());
                    }
                }
            }
        }
        return nSkill;
    }

    /*
     *  Mastery Book Handling: Skill Mastery Level
     *  @purpose Change the player's skill mastery level and display an effect.
     **/
    public void setSkillMasterLevel(int nSkill, int nMasterLevel) {
        Skill pSkill = SkillFactory.getSkill(nSkill);
        if (pSkill == null) {
            return;
        }
        write(CWvsContext.useSkillBook(this, nSkill, nMasterLevel, true, true));
        changeSingleSkillLevel(pSkill, getSkillLevel(pSkill), (byte) (int) nMasterLevel);
        pSkill.setMasterLevel(nMasterLevel);

        if (isDeveloper()) {
            dropMessage(5, "[Mastery Book Debug] Current Master Level (" + pSkill.getMasterLevel() + ") / Maximum Level (" + pSkill.getTrueMax() + ")");
        }
    }

    public void JobAdvanceSp(int i) {
        remainingSp[i] += 5;
        stats.recalcLocalStats(this);
    }

    public void autoJobAdvance() {
        if (GameConstants.isWarriorHero(job)) {
            switch (getLevel()) {
                case 60:
                    changeJob((short) 111);
                    JobAdvanceSp(3);
                    break;
                case 100:
                    changeJob((short) 112);
                    JobAdvanceSp(4);
                    break;
            }
        } else if (GameConstants.isWarriorPaladin(job)) {
            switch (getLevel()) {
                case 60:
                    changeJob((short) 121);
                    JobAdvanceSp(3);
                    break;
                case 100:
                    changeJob((short) 122);
                    JobAdvanceSp(4);
                    break;
            }
        } else if (GameConstants.isWarriorDarkKnight(job)) {
            switch (getLevel()) {
                case 60:
                    changeJob((short) 131);
                    JobAdvanceSp(3);
                    break;
                case 100:
                    changeJob((short) 132);
                    JobAdvanceSp(4);
                    break;
            }
        } else if (GameConstants.isMagicianFirePoison(job)) {
            switch (getLevel()) {
                case 60:
                    changeJob((short) 211);
                    JobAdvanceSp(3);
                    break;
                case 100:
                    changeJob((short) 212);
                    JobAdvanceSp(4);
                    break;
            }
        } else if (GameConstants.isMagicianIceLightning(job)) {
            switch (getLevel()) {
                case 60:
                    changeJob((short) 221);
                    JobAdvanceSp(3);
                    break;
                case 100:
                    changeJob((short) 222);
                    JobAdvanceSp(4);
                    break;
            }
        } else if (GameConstants.isMagicianBishop(job)) {
            switch (getLevel()) {
                case 60:
                    changeJob((short) 231);
                    JobAdvanceSp(3);
                    break;
                case 100:
                    changeJob((short) 232);
                    JobAdvanceSp(4);
                    break;
            }
        } else if (GameConstants.isArcherBowmaster(job)) {
            switch (getLevel()) {
                case 60:
                    changeJob((short) 311);
                    JobAdvanceSp(3);
                    break;
                case 100:
                    changeJob((short) 312);
                    JobAdvanceSp(4);
                    break;
            }
        } else if (GameConstants.isArcherMarksman(job)) {
            switch (getLevel()) {
                case 60:
                    changeJob((short) 321);
                    JobAdvanceSp(3);
                    break;
                case 100:
                    changeJob((short) 322);
                    JobAdvanceSp(4);
                    break;
            }
        } else if (GameConstants.isThiefNightLord(job)) {
            switch (getLevel()) {
                case 60:
                    changeJob((short) 411);
                    JobAdvanceSp(3);
                    break;
                case 100:
                    changeJob((short) 412);
                    JobAdvanceSp(4);
                    break;
            }
        } else if (GameConstants.isThiefShadower(job)) {
            switch (getLevel()) {
                case 60:
                    changeJob((short) 421);
                    JobAdvanceSp(3);
                    break;
                case 100:
                    changeJob((short) 422);
                    JobAdvanceSp(4);
                    break;
            }
        } else if (GameConstants.isPirateBuccaneer(job)) {
            switch (getLevel()) {
                case 60:
                    changeJob((short) 511);
                    JobAdvanceSp(3);
                    break;
                case 100:
                    changeJob((short) 512);
                    JobAdvanceSp(4);
                    break;
            }
        } else if (GameConstants.isPirateCorsair(job)) {
            switch (getLevel()) {
                case 60:
                    changeJob((short) 521);
                    JobAdvanceSp(3);
                    break;
                case 100:
                    changeJob((short) 522);
                    JobAdvanceSp(4);
                    break;
            }
        } else if (GameConstants.isDawnWarriorCygnus(job)) {
            switch (getLevel()) {
                case 30:
                    changeJob((short) 1110);
                    JobAdvanceSp(2);
                    break;
                case 60:
                    changeJob((short) 1111);
                    JobAdvanceSp(3);
                    break;
                case 100:
                    changeJob((short) 1112);
                    JobAdvanceSp(4);
                    break;
            }
        } else if (GameConstants.isBlazeWizardCygnus(job)) {
            switch (getLevel()) {
                case 30:
                    changeJob((short) 1210);
                    JobAdvanceSp(2);
                    break;
                case 60:
                    changeJob((short) 1211);
                    JobAdvanceSp(3);
                    break;
                case 100:
                    changeJob((short) 1212);
                    JobAdvanceSp(4);
                    break;
            }
        } else if (GameConstants.isWindArcherCygnus(job)) {
            switch (getLevel()) {
                case 30:
                    changeJob((short) 1310);
                    JobAdvanceSp(2);
                    break;
                case 60:
                    changeJob((short) 1311);
                    JobAdvanceSp(3);
                    break;
                case 100:
                    changeJob((short) 1312);
                    JobAdvanceSp(4);
                    break;
            }
        } else if (GameConstants.isNightWalkerCygnus(job)) {
            switch (getLevel()) {
                case 30:
                    changeJob((short) 1410);
                    JobAdvanceSp(2);
                    break;
                case 60:
                    changeJob((short) 1411);
                    JobAdvanceSp(3);
                    break;
                case 100:
                    changeJob((short) 1412);
                    JobAdvanceSp(4);
                    break;
            }
        } else if (GameConstants.isThunderBreakerCygnus(job)) {
            switch (getLevel()) {
                case 30:
                    changeJob((short) 1510);
                    JobAdvanceSp(2);
                    break;
                case 60:
                    changeJob((short) 1511);
                    JobAdvanceSp(3);
                    break;
                case 100:
                    changeJob((short) 1512);
                    JobAdvanceSp(4);
                    break;
            }
        } else if (GameConstants.isAranJob(job)) {
            switch (getLevel()) {
                case 10:
                case 11:
                    changeJob((short) 2100);
                    JobAdvanceSp(1);
                    break;
                case 30:
                    changeJob((short) 2110);
                    JobAdvanceSp(2);
                    break;
                case 60:
                    changeJob((short) 2111);
                    JobAdvanceSp(3);
                    break;
                case 100:
                    changeJob((short) 2112);
                    JobAdvanceSp(4);
                    break;
            }
        } else if (GameConstants.isBattleMage(job)) {
            switch (getLevel()) {
                case 30:
                    changeJob((short) 3210);
                    JobAdvanceSp(2);
                    break;
                case 60:
                    changeJob((short) 3211);
                    JobAdvanceSp(3);
                    break;
                case 100:
                    changeJob((short) 3212);
                    JobAdvanceSp(4);
                    break;
            }
        } else if (GameConstants.isWildHunter(job)) {
            switch (getLevel()) {
                case 30:
                    changeJob((short) 3310);
                    JobAdvanceSp(2);
                    break;
                case 60:
                    changeJob((short) 3311);
                    JobAdvanceSp(3);
                    break;
                case 100:
                    changeJob((short) 3312);
                    JobAdvanceSp(4);
                    break;
            }
        } else if (GameConstants.isMechanic(job)) {
            switch (getLevel()) {
                case 30:
                    changeJob((short) 3510);
                    JobAdvanceSp(2);
                    break;
                case 60:
                    changeJob((short) 3511);
                    JobAdvanceSp(3);
                    break;
                case 100:
                    changeJob((short) 3512);
                    JobAdvanceSp(4);
                    break;
            }
        } else if (GameConstants.isBlaster(job)) {
            switch (getLevel()) {
                case 30:
                    changeJob((short) 3710);
                    JobAdvanceSp(2);
                    break;
                case 60:
                    changeJob((short) 3711);
                    JobAdvanceSp(3);
                    break;
                case 100:
                    changeJob((short) 3712);
                    JobAdvanceSp(4);
                    break;
            }
        } else if (GameConstants.isCannoneer(job)) {
            switch (getLevel()) {
                case 30:
                    changeJob((short) 530);
                    JobAdvanceSp(2);
                    break;
                case 60:
                    changeJob((short) 531);
                    JobAdvanceSp(3);
                    break;
                case 100:
                    changeJob((short) 532);
                    JobAdvanceSp(4);
                    break;
            }
        } else if (GameConstants.isDemonSlayer(job)) {
            switch (getLevel()) {
                case 30:
                    changeJob((short) 3110);
                    JobAdvanceSp(2);
                    break;
                case 60:
                    changeJob((short) 3111);
                    JobAdvanceSp(3);
                    break;
                case 100:
                    changeJob((short) 3112);
                    JobAdvanceSp(4);
                    break;
            }
        } else if (GameConstants.isDemonAvenger(job)) {
            switch (getLevel()) {
                case 30:
                    changeJob((short) 3120);
                    JobAdvanceSp(2);
                    break;
                case 60:
                    changeJob((short) 3121);
                    JobAdvanceSp(3);
                    break;
                case 100:
                    changeJob((short) 3122);
                    JobAdvanceSp(4);
                    break;
            }
        } else if (GameConstants.isEvan(job)) {
            switch (getLevel()) {
                case 10:
                case 11:
                    changeJob((short) 2210);
                    JobAdvanceSp(1);
                    break;
                case 30:
                    changeJob((short) 2212);
                    JobAdvanceSp(2);
                    break;
                case 60:
                    changeJob((short) 2214);
                    JobAdvanceSp(3);
                    break;
                case 100:
                    changeJob((short) 2218);
                    JobAdvanceSp(4);
                    break;
            }
        } else if (GameConstants.isMihile(job)) {
            switch (getLevel()) {
                case 10:
                case 11:
                    changeJob((short) 5100);
                    JobAdvanceSp(1);
                    break;
                case 30:
                    changeJob((short) 5110);
                    JobAdvanceSp(2);
                    break;
                case 60:
                    changeJob((short) 5111);
                    JobAdvanceSp(3);
                    break;
                case 100:
                    changeJob((short) 5112);
                    JobAdvanceSp(4);
                    break;
            }
        } else if (GameConstants.isMercedes(job)) {
            switch (getLevel()) {
                case 10:
                case 11:
                    changeJob((short) 2300);
                    JobAdvanceSp(1);
                    break;
                case 30:
                    changeJob((short) 2310);
                    JobAdvanceSp(2);
                    break;
                case 60:
                    changeJob((short) 2311);
                    JobAdvanceSp(3);
                    break;
                case 100:
                    changeJob((short) 2312);
                    JobAdvanceSp(4);
                    break;
            }
        } else if (GameConstants.isHayato(job)) {
            switch (getLevel()) {
                case 10:
                case 11:
                    changeJob((short) 4100);
                    JobAdvanceSp(1);
                    changeSingleSkillLevel(SkillFactory.getSkill(Hayato.BATTOUJUTSU_STANCE), (byte) 1, (byte) 20);
                    break;
                case 30:
                    changeJob((short) 4110);
                    JobAdvanceSp(2);
                    break;
                case 60:
                    changeJob((short) 4111);
                    changeSingleSkillLevel(SkillFactory.getSkill(Hayato.BATTOUJUTSU_SOUL), (byte) 1, (byte) 20);
                    JobAdvanceSp(3);
                    break;
                case 100:
                    changeJob((short) 4112);
                    JobAdvanceSp(4);
                    break;
            }
        } else if (GameConstants.isKanna(job)) {
            switch (getLevel()) {
                case 10:
                case 11:
                    changeJob((short) 4200);
                    JobAdvanceSp(1);
                    break;
                case 30:
                    changeJob((short) 4210);
                    JobAdvanceSp(2);
                    break;
                case 60:
                    changeJob((short) 4211);
                    JobAdvanceSp(3);
                    break;
                case 100:
                    changeJob((short) 4212);
                    JobAdvanceSp(4);
                    break;
            }
        } else if (GameConstants.isAngelicBuster(job)) {
            switch (getLevel()) {
                case 10:
                case 11:
                    //changeJob((short) 6500);
                    JobAdvanceSp(1);
                    break;
                case 30:
                    changeJob((short) 6510);
                    JobAdvanceSp(2);
                    break;
                case 60:
                    changeJob((short) 6511);
                    JobAdvanceSp(3);
                    break;
                case 100:
                    changeJob((short) 6512);
                    JobAdvanceSp(4);
                    break;
            }
        } else if (GameConstants.isLuminous(job)) {
            switch (getLevel()) {
                case 10:
                case 11:
                    changeJob((short) 2700);
                    JobAdvanceSp(1);
                    break;
                case 30:
                    changeJob((short) 2710);
                    JobAdvanceSp(2);
                    break;
                case 60:
                    changeJob((short) 2711);
                    JobAdvanceSp(3);
                    break;
                case 100:
                    changeJob((short) 2712);
                    JobAdvanceSp(4);
                    break;
            }
        } else if (GameConstants.isKaiser(job)) {
            switch (getLevel()) {
                case 10:
                case 11:
                    changeJob((short) 6100);
                    JobAdvanceSp(1);
                    break;
                case 30:
                    changeJob((short) 6110);
                    JobAdvanceSp(2);
                    break;
                case 60:
                    changeJob((short) 6111);
                    JobAdvanceSp(3);
                    break;
                case 100:
                    changeJob((short) 6112);
                    JobAdvanceSp(4);
                    break;
            }
        } else if (GameConstants.isXenon(job)) {
            switch (getLevel()) {
                case 10:
                case 11:
                    changeJob((short) 3600);
                    JobAdvanceSp(1);
                    break;
                case 30:
                    changeJob((short) 3610);
                    JobAdvanceSp(2);
                    break;
                case 60:
                    changeJob((short) 3611);
                    JobAdvanceSp(3);
                    break;
                case 100:
                    changeJob((short) 3612);
                    JobAdvanceSp(4);
                    break;
            }
        } else if (GameConstants.isDualBlade(job)) {
            switch (getLevel()) {
                case 10:
                case 11:
                    //changeJob((short) 400);
                    JobAdvanceSp(1);
                    break;
                case 20:
                    changeJob((short) 430);
                    JobAdvanceSp(2);
                    break;
                case 30:
                    changeJob((short) 431);
                    JobAdvanceSp(3);
                    break;
                case 45:
                    changeJob((short) 432);
                    JobAdvanceSp(4);
                    break;
                case 60:
                    changeJob((short) 433);
                    JobAdvanceSp(5);
                    break;
                case 100:
                    changeJob((short) 434);
                    JobAdvanceSp(6);
                    break;
            }
        } else if (GameConstants.isShade(job)) {
            switch (getLevel()) {
                case 10:
                case 11:
                    changeJob((short) 2500);
                    JobAdvanceSp(1);
                    changeSingleSkillLevel(SkillFactory.getSkill(25001002), (byte) 1, (byte) 25); // Flash Fist
                    changeSingleSkillLevel(SkillFactory.getSkill(25001000), (byte) 1, (byte) 25); // Swift Strike
                    break;
                case 30:
                    changeJob((short) 2510);
                    JobAdvanceSp(2);
                    break;
                case 60:
                    changeJob((short) 2511);
                    JobAdvanceSp(3);
                    break;
                case 100:
                    changeJob((short) 2512);
                    JobAdvanceSp(4);
                    break;
            }
        } else if (GameConstants.isKinesis(job)) {
            switch (getLevel()) {
                case 10:
                    changeJob((short) 14200);
                    JobAdvanceSp(1);
                    break;
                case 30:
                    changeJob((short) 14210);
                    JobAdvanceSp(2);
                    break;
                case 60:
                    changeJob((short) 14211);
                    JobAdvanceSp(3);
                    break;
                case 100:
                    changeJob((short) 14212);
                    JobAdvanceSp(4);
                    break;
            }
        } else if (GameConstants.isPhantom(job)) {
            switch (getLevel()) {
                case 10:
                case 11:
                    changeJob((short) 2400);
                    JobAdvanceSp(1);
                    break;
                case 30:
                    changeJob((short) 2410);
                    JobAdvanceSp(2);
                    break;
                case 60:
                    changeJob((short) 2411);
                    JobAdvanceSp(3);
                    break;
                case 100:
                    changeJob((short) 2412);
                    JobAdvanceSp(4);
                    break;
            }
        } else if (GameConstants.isJett(job)) {
            switch (getLevel()) {
                case 10:
                case 11:
                    changeJob((short) 508);
                    JobAdvanceSp(1);
                    break;
                case 30:
                    changeJob((short) 570);
                    JobAdvanceSp(2);
                    break;
                case 60:
                    changeJob((short) 571);
                    JobAdvanceSp(3);
                    break;
                case 100:
                    changeJob((short) 572);
                    JobAdvanceSp(4);
                    break;
            }
        }
    }

    /*
     *  Inventory Sort/Consolidate Items
     *  @author Mazen
     *
     *  @purpose Sort the characters inventory to fully reload items, this can be used to fix issues related to items not showing up.
     *  @method Emulate the functions that occur when a player sorts their inventory, this includes both sortInventory and sortItems functions.
     */
    public void sortInventory(byte nType) { // nType : 1 = Equipment, 2 = Use, 3 = Setup, 4 = Etc, 5 = Cash.
        // Gather Handler
        final MapleInventoryType invType = MapleInventoryType.getByType((nType));
        MapleInventory Inv = getInventory(invType);

        final List<Item> itemMap = new LinkedList<>();
        for (Item item : Inv.list()) {
            itemMap.add(item.copy()); // Clone all items in the list.
        }
        for (Item itemStats : itemMap) {
            MapleInventoryManipulator.removeFromSlot(client, invType, itemStats.getPosition(), itemStats.getQuantity(), true, false);
        }

        final List<Item> sortedItems = sortItemList(itemMap);
        for (Item item : sortedItems) {
            MapleInventoryManipulator.addFromDrop(client, item, false);
        }
        client.SendPacket(CWvsContext.finishedGather(nType));
        itemMap.clear();
        sortedItems.clear();

        // Sort Handler
        setScrolledPosition((short) 0);
        final MapleInventoryType pInvType = MapleInventoryType.getByType((byte) nType);

        final MapleInventory pInv = getInventory(pInvType); // MapleInventoryType
        boolean sorted = false;

        while (!sorted) {
            final byte freeSlot = (byte) pInv.getNextFreeSlot();
            if (freeSlot != -1) {
                byte itemSlot = -1;
                for (byte i = (byte) (freeSlot + 1); i <= pInv.getSlotLimit(); i++) {
                    if (pInv.getItem(i) != null) {
                        itemSlot = i;
                        break;
                    }
                }
                if (itemSlot > 0) {
                    MapleInventoryManipulator.move(client, pInvType, itemSlot, freeSlot);
                } else {
                    sorted = true;
                }
            } else {
                sorted = true;
            }
        }
        client.SendPacket(CWvsContext.finishedSort(pInvType.getType()));
        client.SendPacket(CWvsContext.enableActions());
    }

    private static List<Item> sortItemList(final List<Item> passedMap) {
        final List<Integer> itemIds = new ArrayList<>(); // empty list.
        for (Item item : passedMap) {
            itemIds.add(item.getItemId()); // adds all item ids to the empty list to be sorted.
        }
        Collections.sort(itemIds); // sorts item ids

        final List<Item> sortedList = new LinkedList<>(); // ordered list pl0x <3.

        for (Integer val : itemIds) {
            for (Item item : passedMap) {
                if (val == item.getItemId()) { // Goes through every index and finds the first value that matches
                    sortedList.add(item);
                    passedMap.remove(item);
                    break;
                }
            }
        }
        return sortedList;
    }

    public void sendPolice() {
        client.SendPacket(CWvsContext.broadcastMsg("You have been banned by a #b" + ServerConstants.SERVER_NAME + " GM#k for hacking."));

        WorldTimer.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
                client.disconnect(false, false); //FAGGOTS
            }
        }, 6000);
    }

    public void changeKeybinding(int key, byte type, int action) {
        if (type != 0) {
            keylayout.addKeyLayout(key, type, action, true);
        } else {
            keylayout.removeKeyLayout(key, true);
        }
    }

    public void changeKeybinding(String key_name, byte type, int action) { //not finished yet TODO: finish it
        int key;
        switch (key_name.toUpperCase()) {
            case "F1":
            case "F2":
            case "F3":
            case "F4":
            case "F5":
            case "F6":
            case "F7":
            case "F8":
            case "F9":
            case "F10":
            case "F11":
            case "F12":
                key = 58 + Integer.parseInt(key_name.replace("F", ""));
                break;
            case "1":
            case "!":
            case "2":
            case "@":
            case "3":
            case "#":
            case "4":
            case "$":
            case "5":
            case "%":
            case "6":
            case "^":
            case "7":
            case "&":
            case "8":
            case "*":
            case "9":
            case "(":
                key = 1 + Integer.parseInt(key_name);
                break;
            case "0":
            case ")":
                key = 11;
                break;
            case "-":
            case "_":
                key = 12;
                break;
            case "=":
            case "+":
                key = 13;
                break;
            default:
                key = -1;
                break;
        }
        if (key != -1) {
            if (type != 0) {
                keylayout.addKeyLayout(key, type, action, true);
            } else {
                keylayout.removeKeyLayout(key, true);
            }
        }
    }

    public void sendMacros() {
        for (int i = 0; i < 5; i++) {
            if (skillMacros[i] != null) {
                client.SendPacket(CField.getMacros(skillMacros));
                break;
            }
        }
    }

    public void updateMacros(int position, SkillMacro updateMacro) {
        skillMacros[position] = updateMacro;
        changed_skillmacros = true;
    }

    public final SkillMacro[] getMacros() {
        return skillMacros;
    }

    public void tempban(String reason, Calendar duration, int greason, boolean IPMac) {
        if (IPMac) {
            client.banMacs();
        }
        client.SendPacket(CWvsContext.GMPoliceMessage(true));
        try (Connection con = Database.GetConnection()) {
            System.out.println(Thread.currentThread().getStackTrace()[2].getClassName() + "." + Thread.currentThread().getStackTrace()[2].getMethodName());
            PreparedStatement ps;
            if (IPMac) {
                ps = con.prepareStatement("INSERT INTO ipbans VALUES (DEFAULT, ?)");
                ps.setString(1, client.GetIP());
                ps.execute();
                ps.close();
            }

            client.Close();

            ps = con.prepareStatement("UPDATE accounts SET tempban = ?, banreason = ?, greason = ? WHERE id = ?");
            Timestamp TS = new Timestamp(duration.getTimeInMillis());
            ps.setTimestamp(1, TS);
            ps.setString(2, reason);
            ps.setInt(3, greason);
            ps.setInt(4, accountid);
            ps.execute();
            ps.close();
        } catch (SQLException ex) {
            LogHelper.SQL.get().info("[SQL] There was an issue with something from the database:\n", ex);
        }

    }

    public int getMaxHp() {
        return getStat().getMaxHp();
    }

    public int getMaxMp() {
        return getStat().getMaxMp();
    }

    public void setHp(int amount) {
        getStat().setHp(amount, this);
    }

    public void setMp(int amount) {
        getStat().setMp(amount, this);
    }

    public final boolean ban(String reason, boolean IPMac, boolean autoban, boolean hellban) {
        if (lastmonthfameids == null) {
            throw new RuntimeException("Trying to ban a non-loaded character (testhack)");
        }
        client.SendPacket(CWvsContext.GMPoliceMessage(true));
        try (Connection con = Database.GetConnection()) {
            System.out.println(Thread.currentThread().getStackTrace()[2].getClassName() + "." + Thread.currentThread().getStackTrace()[2].getMethodName());
            PreparedStatement ps = con.prepareStatement("UPDATE accounts SET banned = ?, banreason = ? WHERE id = ?");
            ps.setInt(1, autoban ? 2 : 1);
            ps.setString(2, reason);
            ps.setInt(3, accountid);
            ps.execute();
            ps.close();

            if (IPMac) {
                client.banMacs();
                ps = con.prepareStatement("INSERT INTO ipbans VALUES (DEFAULT, ?)");
                ps.setString(1, client.getSessionIPAddress());
                ps.execute();
                ps.close();
            }
        } catch (SQLException ex) {
            LogHelper.SQL.get().info("[SQL] There was an issue with something from the database:\n", ex);
            return false;
        }
        client.Close();
        return true;
    }

    public static boolean ban(String id, String reason, boolean accountId, int gmlevel, boolean hellban) {
        try (Connection con = Database.GetConnection()) {
            System.out.println(Thread.currentThread().getStackTrace()[2].getClassName() + "." + Thread.currentThread().getStackTrace()[2].getMethodName());
            PreparedStatement ps;
            if (id.matches("/[0-9]{1,3}\\..*")) {
                ps = con.prepareStatement("INSERT INTO ipbans VALUES (DEFAULT, ?)");
                ps.setString(1, id);
                ps.execute();
                ps.close();
                return true;
            }
            if (accountId) {
                ps = con.prepareStatement("SELECT id FROM accounts WHERE name = ?");
            } else {
                ps = con.prepareStatement("SELECT accountid FROM characters WHERE name = ? AND deletedAt is null");
            }
            boolean ret = false;
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int z = rs.getInt(1);
                    try (PreparedStatement psb = con.prepareStatement("UPDATE accounts SET banned = 1, banreason = ? WHERE id = ? AND gm < ?")) {
                        psb.setString(1, reason);
                        psb.setInt(2, z);
                        psb.setInt(3, gmlevel);
                        psb.execute();
                    }

                    if (gmlevel > 100) { //admin ban
                        try (PreparedStatement psa = con.prepareStatement("SELECT * FROM accounts WHERE id = ?")) {
                            psa.setInt(1, z);
                            try (ResultSet rsa = psa.executeQuery()) {
                                if (rsa.next()) {
                                    String sessionIP = rsa.getString("sessionIP");
                                    if (sessionIP != null && sessionIP.matches("/[0-9]{1,3}\\..*")) {
                                        try (PreparedStatement psz = con.prepareStatement("INSERT INTO ipbans VALUES (DEFAULT, ?)")) {
                                            psz.setString(1, sessionIP);
                                            psz.execute();
                                        }
                                    }
                                    if (rsa.getString("macs") != null) {
                                        String[] macData = rsa.getString("macs").split(", ");
                                        if (macData.length > 0) {
                                            MapleClient.banMacs(macData);
                                        }
                                    }
                                }
                            }
                        }
                    }
                    ret = true;
                }
            }
            ps.close();
            return ret;
        } catch (SQLException ex) {
            LogHelper.SQL.get().info("[SQL] There was an issue with something from the database:\n", ex);
        }
        return false;
    }

    /**
     * Oid of players is always = the cid
     *
     * @return
     */
    @Override
    public int getObjectId() {
        return getId();
    }

    /**
     * Throws unsupported operation exception, oid of players is read only
     *
     * @param id
     */
    @Override
    public void setObjectId(int id) {
        throw new UnsupportedOperationException();
    }

    public MapleStorage getStorage() {
        return storage;
    }

    public void addVisibleMapObject(MapleMapObject mo) {
        if (clone) {
            return;
        }

        visibleMapObjectsLock.writeLock().lock();
        try {
            visibleMapObjects.add(mo);
        } finally {
            visibleMapObjectsLock.writeLock().unlock();
        }
    }

    public void removeVisibleMapObject(MapleMapObject mo) {
        if (clone) {
            return;
        }

        visibleMapObjectsLock.writeLock().lock();
        try {
            visibleMapObjects.remove(mo);
        } finally {
            visibleMapObjectsLock.writeLock().unlock();
        }
    }

    public boolean isMapObjectVisible(MapleMapObject mo) {
        visibleMapObjectsLock.readLock().lock();
        try {
            return !clone && visibleMapObjects.contains(mo);
        } finally {
            visibleMapObjectsLock.readLock().unlock();
        }
    }

    public Collection<MapleMapObject> getAndWriteLockVisibleMapObjects() {
        visibleMapObjectsLock.writeLock().lock();
        return visibleMapObjects;
    }

    public void unlockWriteVisibleMapObjects() {
        visibleMapObjectsLock.writeLock().unlock();
    }

    public boolean isAlive() {
        return stats.getHp() > 0;
    }

    @Override
    public void sendDestroyData(MapleClient client) {
        client.SendPacket(CField.removePlayerFromMap(this.getObjectId()));
        for (final WeakReference<User> chr : clones) {
            if (chr.get() != null) {
                chr.get().sendDestroyData(client);
            }
        }
        //don't need this, client takes care of it
        /*
         * if (dragon != null) {
         * client.write(CField.removeDragon(this.getId())); } if
         * (android != null) {
         * client.write(CField.deactivateAndroid(this.getId())); }
         * if (summonedFamiliar != null) {
         * client.write(CField.removeFamiliar(this.getId())); }
         */
        if (GameConstants.isXenon(job)) {
            XenonSupplyTask.cancel(true);
            XenonSupplyTask = null;
        }
    }

    @Override
    public void sendSpawnData(MapleClient client) {
        if (client.getPlayer().allowedToTarget(this)) {
            //if (client.getPlayer() != this)
            client.SendPacket(CField.spawnPlayerMapObject(this));

            for (final Pet pet : pets) {
                if (pet.getSummoned()) {
                    map.broadcastMessage(this, PetPacket.OnActivated(this.id, getPetIndex(pet), true, pet, 0), true);
                }
            }

            for (final WeakReference<User> chr : clones) {
                if (chr.get() != null) {
                    chr.get().sendSpawnData(client);
                }
            }
            if (dragon != null) {
                client.SendPacket(CField.spawnDragon(dragon));
            }
            if (haku != null) {
                client.SendPacket(CField.spawnHaku(haku, false));
            }
            if (android != null) {
                client.SendPacket(CField.spawnAndroid(this, android));
            }
            if (summonedFamiliar != null) {
                client.SendPacket(CField.spawnFamiliar(summonedFamiliar, true, true));
            }
            if (summons != null && summons.size() > 0) {
                summonsLock.readLock().lock();
                try {
                    for (final Summon summon : summons) {
                        client.SendPacket(SummonPacket.spawnSummon(summon, false));
                    }
                } finally {
                    summonsLock.readLock().unlock();
                }
            }
            if (followid > 0 && followon) {
                client.SendPacket(CField.followEffect(followinitiator ? followid : id, followinitiator ? id : followid, null));
            }
        }
    }

    public final void equipChanged() {
        if (map == null) {
            return;
        }
        map.broadcastMessage(this, CField.updateCharLook(this, false), false);
        stats.recalcLocalStats(this);
        if (getMessenger() != null) {
            World.Messenger.updateMessenger(getMessenger().getId(), getName(), client.getChannel());
        }
    }

    public final Pet getPet(final int index) {
        byte count = 0;
        for (final Pet pet : pets) {
            if (pet.getSummoned()) {
                if (count == index) {
                    return pet;
                }
                count++;
            }
        }
        return null;
    }

    public void removePetCS(Pet pet) {
        pets.remove(pet);
    }

    public void addPet(final Pet pet) {
        if (pets.contains(pet)) {
            pets.remove(pet);
        }
        pets.add(pet);
        // So that the pet will be at the last
        // Pet index logic :(
    }

    public void removePet(Pet pet, boolean shiftLeft) {
        pet.setSummoned(0);
        /*
         * int slot = -1; for (int i = 0; i < 3; i++) { if (pets[i] != null) {
         * if (pets[i].getUniqueId() == pet.getUniqueId()) { pets[i] = null;
         * slot = i; break; } } } if (shiftLeft) { if (slot > -1) { for (int i =
         * slot; i < 3; i++) { if (i != 2) { pets[i] = pets[i + 1]; } else {
         * pets[i] = null; } } } }
         */
    }

    public final byte getPetIndex(final Pet petz) {
        byte count = 0;
        for (final Pet pet : pets) {
            if (pet.getSummoned()) {
                if (pet.getItem().getUniqueId() == petz.getItem().getUniqueId()) {
                    return count;
                }
                count++;
            }
        }
        return -1;
    }

    public final byte getPetIndex(final int petId) {
        byte count = 0;
        for (final Pet pet : pets) {
            if (pet.getSummoned()) {
                if (pet.getItem().getUniqueId() == petId) {
                    return count;
                }
                count++;
            }
        }
        return -1;
    }

    public final List<Pet> getSummonedPets() {
        List<Pet> ret = new ArrayList<>();
        for (final Pet pet : pets) {
            if (pet.getSummoned()) {
                ret.add(pet);
            }
        }
        return ret;
    }

    public final byte getPetById(final int petId) {
        byte count = 0;
        for (final Pet pet : pets) {
            if (pet.getSummoned()) {
                if (pet.getItem().getItemId() == petId) {
                    return count;
                }
                count++;
            }
        }
        return -1;
    }

    public final List<Pet> getPets() {
        return pets;
    }

    public final void unequipAllPets() {
        for (final Pet pet : pets) {
            if (pet != null) {
                unequipPet(pet, true, false);
            }
        }
    }

    public void unequipPet(Pet pet, boolean shiftLeft, boolean hunger) {
        if (pet.getSummoned()) {
            pet.saveToDb();

            if (map != null) {
                client.SendPacket(PetPacket.updatePet(pet, null, true)); // Remove pet from equip window.
                map.broadcastMessage(this, PetPacket.OnDeactivated(this.id, getPetIndex(pet), pet), true); //Remove the pet from map.
                //map.broadcastMessage(this, PetPacket.OnActivated(this.id, getPetIndex(pet), true, pet, 1), true);
            }

            removePet(pet, shiftLeft);
            client.SendPacket(CWvsContext.enableActions());
        }
    }

    /*
     * public void shiftPetsRight() { if (pets[2] == null) { pets[2] = pets[1];
     * pets[1] = pets[0]; pets[0] = null; } }
     */
    public final long getLastFameTime() {
        return lastfametime;
    }

    public final List<Integer> getFamedCharacters() {
        return lastmonthfameids;
    }

    public final List<Integer> getBattledCharacters() {
        return lastmonthbattleids;
    }

    public FameStatus canGiveFame(User from) {
        if (lastfametime >= System.currentTimeMillis() - 60 * 60 * 24 * 1000) {
            return FameStatus.NOT_TODAY;
        } else if (from == null || lastmonthfameids == null || lastmonthfameids.contains(Integer.valueOf(from.getId()))) {
            return FameStatus.NOT_THIS_MONTH;
        }
        return FameStatus.OK;
    }

    public void hasGivenFame(User to) {
        lastfametime = System.currentTimeMillis();
        lastmonthfameids.add(Integer.valueOf(to.getId()));
        try (Connection con = Database.GetConnection()) {
            System.out.println(Thread.currentThread().getStackTrace()[2].getClassName() + "." + Thread.currentThread().getStackTrace()[2].getMethodName());
            try (PreparedStatement ps = con.prepareStatement("INSERT INTO famelog (characterid, characterid_to) VALUES (?, ?)")) {
                ps.setInt(1, getId());
                ps.setInt(2, to.getId());
                ps.execute();
            }
        } catch (SQLException e) {
            System.err.println("ERROR writing famelog for char " + getName() + " to " + to.getName() + e);
        }
    }

    public boolean canBattle(User to) {
        return to != null && lastmonthbattleids != null && !lastmonthbattleids.contains(Integer.valueOf(to.getAccountID()));
    }

    public void hasBattled(User to) {
        lastmonthbattleids.add(Integer.valueOf(to.getAccountID()));
        try (Connection con = Database.GetConnection()) {
            System.out.println(Thread.currentThread().getStackTrace()[2].getClassName() + "." + Thread.currentThread().getStackTrace()[2].getMethodName());
            try (PreparedStatement ps = con.prepareStatement("INSERT INTO battlelog (accid, accid_to) VALUES (?, ?)")) {
                ps.setInt(1, getAccountID());
                ps.setInt(2, to.getAccountID());
                ps.execute();
            }
        } catch (SQLException e) {
            System.err.println("ERROR writing battlelog for char " + getName() + " to " + to.getName() + e);
        }
    }

    public final MapleKeyLayout getKeyLayout() {
        return this.keylayout;
    }

    public MapleParty getParty() {
        if (party == null) {
            return null;
        } else if (party.isDisbanded()) {
            party = null;
        }
        return party;
    }

    public byte getWorld() {
        return world;
    }

    public void setWorld(byte world) {
        this.world = world;
    }

    public void setParty(MapleParty party) {
        this.party = party;
    }

    public MapleTrade getTrade() {
        return trade;
    }

    public void setTrade(MapleTrade trade) {
        this.trade = trade;
    }

    public EventInstanceManager getEventInstance() {
        return eventInstance;
    }

    public void setEventInstance(EventInstanceManager eventInstance) {
        this.eventInstance = eventInstance;
    }

    public void addDoor(MapleDoor door) {
        doors.add(door);
    }

    public void clearDoors() {
        doors.clear();
    }

    public List<MapleDoor> getDoors() {
        return new ArrayList<>(doors);
    }

    public void addMechDoor(MechDoor door) {
        mechDoors.add(door);
    }

    public void clearMechDoors() {
        mechDoors.clear();
    }

    public List<MechDoor> getMechDoors() {
        return new ArrayList<>(mechDoors);
    }

    public void setSmega() {
        if (smega) {
            smega = false;
            dropMessage(6, "You have disabled Super Megaphones.");
        } else {
            smega = true;
            dropMessage(6, "You have enabled Super Megaphones.");
        }
    }

    public boolean getSmega() {
        return smega;
    }

    public List<Summon> getSummonsReadLock() {
        summonsLock.readLock().lock();
        return summons;
    }

    public Map<Integer, Summon> getSummons() {
        return summonss;
    }

    public int getSummonsSize() {
        return summons.size();
    }

    public void unlockSummonsReadLock() {
        summonsLock.readLock().unlock();
    }

    public void addSummon(Summon s) {
        summonsLock.writeLock().lock();
        try {
            summons.add(s);
        } finally {
            summonsLock.writeLock().unlock();
        }
    }

    public void removeSummon(Summon s) {
        summonsLock.writeLock().lock();
        try {
            summons.remove(s);
        } finally {
            summonsLock.writeLock().unlock();
        }
    }

    public int getChair() {
        return chair;
    }

    public int getItemEffect() {
        return itemEffect;
    }

    public void setChair(int chair) {
        this.chair = chair;
        stats.relocHeal(this);
    }

    public void setItemEffect(int itemEffect) {
        this.itemEffect = itemEffect;
    }

    @Override
    public MapleMapObjectType getType() {
        return MapleMapObjectType.PLAYER;
    }

    public int getFamilyId() {
        if (mfc == null) {
            return 0;
        }
        return mfc.getFamilyId();
    }

    public int getSeniorId() {
        if (mfc == null) {
            return 0;
        }
        return mfc.getSeniorId();
    }

    public int getJunior1() {
        if (mfc == null) {
            return 0;
        }
        return mfc.getJunior1();
    }

    public int getJunior2() {
        if (mfc == null) {
            return 0;
        }
        return mfc.getJunior2();
    }

    public int getCurrentRep() {
        return currentrep;
    }

    public int getTotalRep() {
        return totalrep;
    }

    public void setCurrentRep(int _rank) {
        currentrep = _rank;
        if (mfc != null) {
            mfc.setCurrentRep(_rank);
        }
    }

    public void setTotalRep(int _rank) {
        totalrep = _rank;
        if (mfc != null) {
            mfc.setTotalRep(_rank);
        }
    }

    public int getTotalWins() {
        return totalWins;
    }

    public int getTotalLosses() {
        return totalLosses;
    }

    public void increaseTotalWins() {
        totalWins++;
    }

    public void increaseTotalLosses() {
        totalLosses++;
    }

    public int getGuildId() {
        return guildid;
    }

    public byte getGuildRank() {
        return guildrank;
    }

    public int getGuildContribution() {
        return guildContribution;
    }

    public void setGuildId(int _id) {
        guildid = _id;
        if (guildid > 0) {
            if (mgc == null) {
                mgc = new MapleGuildCharacter(this);
            } else {
                mgc.setGuildId(guildid);
            }
        } else {
            mgc = null;
            guildContribution = 0;
        }
    }

    public void setGuildRank(byte _rank) {
        guildrank = _rank;
        if (mgc != null) {
            mgc.setGuildRank(_rank);
        }
    }

    public void setGuildContribution(int _c) {
        this.guildContribution = _c;
        if (mgc != null) {
            mgc.setGuildContribution(_c);
        }
    }

    public MapleGuildCharacter getMGC() {
        return mgc;
    }

    public void setAllianceRank(byte rank) {
        allianceRank = rank;
        if (mgc != null) {
            mgc.setAllianceRank(rank);
        }
    }

    public byte getAllianceRank() {
        return allianceRank;
    }

    public MapleGuild getGuild() {
        if (getGuildId() <= 0) {
            return null;
        }
        return World.Guild.getGuild(getGuildId());
    }

    public void setJob(int j) {
        this.job = (short) j;
    }

    public void guildUpdate() {
        if (guildid <= 0) {
            return;
        }
        mgc.setLevel((short) level);
        mgc.setJobId(job);
        World.Guild.memberLevelJobUpdate(mgc);
    }

    public void saveGuildStatus() {
        MapleGuild.setOfflineGuildStatus(guildid, guildrank, guildContribution, allianceRank, id);
    }

    public void familyUpdate() {
        if (mfc == null) {
            return;
        }
        World.Family.memberFamilyUpdate(mfc, this);
    }

    public void saveFamilyStatus() {
        try (Connection con = Database.GetConnection()) {
            System.out.println(Thread.currentThread().getStackTrace()[2].getClassName() + "." + Thread.currentThread().getStackTrace()[2].getMethodName());
            try (PreparedStatement ps = con.prepareStatement("UPDATE characters SET familyid = ?, seniorid = ?, junior1 = ?, junior2 = ? WHERE id = ?")) {
                if (mfc == null) {
                    ps.setInt(1, 0);
                    ps.setInt(2, 0);
                    ps.setInt(3, 0);
                    ps.setInt(4, 0);
                } else {
                    ps.setInt(1, mfc.getFamilyId());
                    ps.setInt(2, mfc.getSeniorId());
                    ps.setInt(3, mfc.getJunior1());
                    ps.setInt(4, mfc.getJunior2());
                }
                ps.setInt(5, id);
                ps.executeUpdate();
            }
        } catch (SQLException se) {
            LogHelper.SQL.get().info("Error saving family status ", se);
        }
        //MapleFamily.setOfflineFamilyStatus(familyid, seniorid, junior1, junior2, currentrep, totalrep, id);
    }

    public void modifyCSPoints(int type, int quantity) {
        modifyCSPoints(type, quantity, false);
    }

    public void modifyCSPoints(int type, int quantity, boolean show) {
        switch (type) {
            case 1:
                if (nxcredit + quantity < 0) {
                    if (show) {
                        dropMessage(-1, "You have the maximum amount of NX Cash.");
                    }
                    return;
                }
                ///if (quantity > 0) {
                //    quantity = (quantity / 2); //stuff is cheaper lol
                //}
                nxcredit += quantity;
                break;
            case 2:
                if (maplepoints + quantity < 0) {
                    if (show) {
                        dropMessage(-1, "You have the maximum amount of Maple Points.");
                    }
                    return;
                }
                maplepoints += quantity;
                client.SendPacket(CWvsContext.updateMaplePoint(maplepoints));
                break;
            case 3:
                if (maplerewards + quantity < 0) {
                    if (show) {
                        dropMessage(-1, "You have the maximum amount of Maple Reward points.");
                    }
                    return;
                }
                ///if (quantity > 0) {
                //    quantity = (quantity / 2); //stuff is cheaper lol
                //}
                maplerewards += quantity;
                break;
            case 4:
                if (acash + quantity < 0) {
                    if (show) {
                        dropMessage(-1, "You have the maximum amount of NX Cash.");
                    }
                    return;
                }
                //if (quantity > 0) {
                //    quantity = (quantity / 2); //stuff is cheaper lol
                //}
                acash += quantity;
                break;
            default:
                break;
        }
        if (show && quantity != 0) {
            dropMessage(-1, "You have " + (quantity > 0 ? "gained " : "lost ") + quantity + (type == 1 ? " NX Cash!" : " Maple Points!"));
        }
    }

    public int getCSPoints(int type) {
        switch (type) {
            case 1:
                return nxcredit;
            case 2:
                return maplepoints;
            case 3:
                return maplerewards;
            case 4:
                return acash;
            default:
                return 0;
        }
    }

    public void setMapleRewards(int nRewards) {
        this.maplerewards = nRewards;
    }

    public final boolean hasEquipped(int itemid) {
        return inventory[MapleInventoryType.EQUIPPED.ordinal()].countById(itemid) >= 1;
    }

    public final boolean haveItem(int itemid, int quantity, boolean checkEquipped, boolean greaterOrEquals) {
        final MapleInventoryType type = GameConstants.getInventoryType(itemid);
        int possesed = inventory[type.ordinal()].countById(itemid);
        if (checkEquipped && type == MapleInventoryType.EQUIP) {
            possesed += inventory[MapleInventoryType.EQUIPPED.ordinal()].countById(itemid);
        }
        if (greaterOrEquals) {
            return possesed >= quantity;
        } else {
            return possesed == quantity;
        }
    }

    public final boolean haveItem(int itemid, int quantity) {
        return haveItem(itemid, quantity, true, true);
    }

    public final boolean haveItem(int itemid) {
        return haveItem(itemid, 1, true, true);
    }

    public static boolean tempban(String reason, Calendar duration, int greason, int accountid) {
        try (Connection con = Database.GetConnection()) {
            System.out.println(Thread.currentThread().getStackTrace()[2].getClassName() + "." + Thread.currentThread().getStackTrace()[2].getMethodName());
            try (PreparedStatement ps = con.prepareStatement("UPDATE accounts SET tempban = ?, banreason = ?, greason = ? WHERE id = ?")) {
                Timestamp TS = new Timestamp(duration.getTimeInMillis());
                ps.setTimestamp(1, TS);
                ps.setString(2, reason);
                ps.setInt(3, greason);
                ps.setInt(4, accountid);
                ps.executeUpdate();
            }
            return true;
        } catch (SQLException ex) {
            LogHelper.SQL.get().info("[SQL] There was an issue with something from the database:\n", ex);
        }
        return false;
    }

    public void setGM(byte level) {
        this.gmLevel = level;
    }

    /**
     * Gets the last movement made by GM character, so as to capture the movement for !vacmob command
     *
     * @return
     */
    public List<LifeMovementFragment> getLastGMMovement() {
        return lastGMMovement;
    }

    public void setLastGMMovement(List<LifeMovementFragment> lastGMMovement) {
        this.lastGMMovement = lastGMMovement;
    }

    /**
     * The list of shop repurchases the character is allowed to rebuy
     *
     * @return Unmodifiable List
     */
    public List<ShopRepurchase> getShopRepurchases() {
        return Collections.unmodifiableList(this.shopRepurchases.stream().collect(Collectors.toList()));
    }

    /**
     * Add a shop repurchase item to the list
     *
     * @param purchase
     */
    public void addShopRepurchase(ShopRepurchase purchase) {
        if (shopRepurchases.size() > 10) {
            shopRepurchases.remove(0);
        }
        shopRepurchases.add(purchase);
    }

    /**
     * Remove a shop repurchase from the list
     *
     * @param purchase
     * @return boolean -- if removed, or not. This is used to double-check to ensure that everything is in order
     */
    public boolean removeShopRepurchase(ShopRepurchase purchase) {
        if (shopRepurchases.contains(purchase)) {
            shopRepurchases.remove(purchase);
            return true;
        }
        return false;

    }

    public int getFinalAttackSkill() {
        int nSkill = 0;
        if (GameConstants.isAran(job)) {
            if (hasSkill(Aran.ADVANCED_FINAL_ATTACK_5)) {
                nSkill = Aran.ADVANCED_FINAL_ATTACK_5;
            } else if (hasSkill(Aran.FINAL_ATTACK_1)) {
                nSkill = Aran.FINAL_ATTACK_1;
            }
        } else if (GameConstants.isMihile(job)) {
            if (hasSkill(Mihile.ADVANCED_FINAL_ATTACK_1)) {
                nSkill = Mihile.ADVANCED_FINAL_ATTACK_1;
            } else if (hasSkill(Mihile.FINAL_ATTACK_5)) {
                nSkill = Mihile.FINAL_ATTACK_5;
            }
        } else if (GameConstants.isWildHunter(job)) {
            if (hasSkill(WildHunter.ADVANCED_FINAL_ATTACK_4)) {
                nSkill = WildHunter.ADVANCED_FINAL_ATTACK_4;
            } else if (hasSkill(WildHunter.FINAL_ATTACK_7)) {
                nSkill = WildHunter.FINAL_ATTACK_7;
            }
        } else if (GameConstants.isMercedes(job)) {
            if (hasSkill(Mercedes.ADVANCED_FINAL_ATTACK)) {
                nSkill = Mercedes.ADVANCED_FINAL_ATTACK;
            } else if (hasSkill(Mercedes.FINAL_ATTACK_DUAL_BOWGUNS)) {
                nSkill = Mercedes.FINAL_ATTACK_DUAL_BOWGUNS;
            }
        } else if (GameConstants.isWarriorHero(job)) {
            if (hasSkill(Hero.ADVANCED_FINAL_ATTACK_2)) {
                nSkill = Hero.ADVANCED_FINAL_ATTACK_2;
            } else if (hasSkill(Fighter.FINAL_ATTACK_6)) {
                nSkill = Fighter.FINAL_ATTACK_6;
            }
        } else if (GameConstants.isWarriorPaladin(job)) {
            if (hasSkill(Page.FINAL_ATTACK_4)) {
                nSkill = Page.FINAL_ATTACK_4;
            }
        } else if (GameConstants.isWarriorDarkKnight(job)) {
            if (hasSkill(Spearman.FINAL_ATTACK_2)) {
                nSkill = Spearman.FINAL_ATTACK_2;
            }
        } else if (GameConstants.isArcherBowmaster(job)) {
            if (hasSkill(Bowmaster.ADVANCED_FINAL_ATTACK_3)) {
                nSkill = Bowmaster.ADVANCED_FINAL_ATTACK_3;
            } else if (hasSkill(Hunter.FINAL_ATTACK_BOW)) {
                nSkill = Hunter.FINAL_ATTACK_BOW;
            }
        } else if (GameConstants.isArcherMarksman(job)) {
            if (hasSkill(Crossbowman.FINAL_ATTACK_CROSSBOW)) {
                nSkill = Crossbowman.FINAL_ATTACK_CROSSBOW;
            }
        }
        return nSkill;

    }

    public void incrementKillQuestAlpha() {
        String quest = getInfoQuest(13337);
        if (quest.isEmpty()) {
            quest = "0";
        }

        int questkillnum = Integer.parseInt(quest);
        questkillnum++;
        
        updateInfoQuest(13337, Integer.toString(questkillnum));
    }

    public static enum FameStatus {

        OK,
        NOT_TODAY,
        NOT_THIS_MONTH
    }

    public byte getBuddyCapacity() {
        return buddylist.getCapacity();
    }

    public void setBuddyCapacity(byte capacity) {
        buddylist.setCapacity(capacity);
        Buddy buddy = new Buddy(BuddyResult.CAPACITY);
        buddy.setCapacity(capacity);
        client.SendPacket(CWvsContext.buddylistMessage(buddy));
    }

    public MapleMessenger getMessenger() {
        return messenger;
    }

    public void setMessenger(MapleMessenger messenger) {
        this.messenger = messenger;
    }

    public void clearAllCooldowns() {
        List<Pair<Integer, Integer>> cooldowns = new ArrayList<>();

        for (MapleCoolDownValueHolder m : getCooldowns()) {
            final int skil = m.skillId;
            removeCooldown(skil);

            cooldowns.add(new Pair<>(skil, 0));
        }
        if (!cooldowns.isEmpty()) {
            client.SendPacket(CField.skillCooldown(cooldowns));
        }
    }

    // <editor-fold defaultstate="collapsed" desc="Cooldowns">
    private boolean noCooldown = false;

    /**
     * Toggles enable/disable cooldown when using skills for debugging purpose.
     *
     * @return
     */
    public boolean toggleCooldown() {
        noCooldown = !noCooldown;
        return noCooldown;
    }

    /**
     * Adds skill cooldown for the specified skill
     *
     * @param skillId
     * @param startTime
     * @param length
     */
    public void addCooldown(int skillId, long startTime, int length) {
        if (noCooldown) {
            return;
        }
        client.SendPacket(CField.skillCooldown(skillId, length));

        coolDowns.put(skillId, new MapleCoolDownValueHolder(skillId, startTime, length * 1000));
    }

    public void removeCooldown(int skillId) {
        if (coolDowns.containsKey(skillId)) {
            coolDowns.remove(skillId);
        }
    }

    public boolean skillisCooling(int skillId) {
        return coolDowns.containsKey(skillId);
    }

    /**
     * Setup skill cooldown for character, during login.
     *
     * @param cooldowns
     */
    public void giveCoolDowns(final List<MapleCoolDownValueHolder> cooldowns) {
        if (cooldowns != null) {
            for (MapleCoolDownValueHolder cooldown : cooldowns) {
                coolDowns.put(cooldown.skillId, cooldown);
            }
        } else {
            final List<Pair<Integer, Integer>> cooldowns_packet = new ArrayList<>();

            try (Connection con = Database.GetConnection()) {
                System.out.println(Thread.currentThread().getStackTrace()[2].getClassName() + "." + Thread.currentThread().getStackTrace()[2].getMethodName());
                ResultSet rs;
                try (PreparedStatement ps = con.prepareStatement("SELECT SkillID,StartTime,length FROM skills_cooldowns WHERE charid = ?")) {
                    ps.setInt(1, getId());
                    rs = ps.executeQuery();
                    while (rs.next()) {
                        if (rs.getLong("length") + rs.getLong("StartTime") - System.currentTimeMillis() <= 0) {
                            continue;
                        }

                        int skillId = rs.getInt("SkillID");
                        long startTime = rs.getLong("StartTime");
                        long length = rs.getLong("length");

                        coolDowns.put(skillId, new MapleCoolDownValueHolder(skillId, startTime, length));

                        cooldowns_packet.add(new Pair<>(skillId, (int) (length / 1000)));
                    }
                }
                rs.close();
                deleteWhereCharacterId(con, "DELETE FROM skills_cooldowns WHERE charid = ?");

            } catch (SQLException e) {
                LogHelper.SQL.get().info("[SQL] There was an issue with something from the database:\n", e);
            }

            // Sent packet
            if (!cooldowns_packet.isEmpty()) {
                getClient().SendPacket(CField.skillCooldown(cooldowns_packet));
            }
        }
    }

    public int getCooldownSize() {
        return coolDowns.size();
    }

    public int getDiseaseSize() {
        return diseases.size();
    }

    public List<MapleCoolDownValueHolder> getCooldowns() {
        List<MapleCoolDownValueHolder> ret = new ArrayList<>();
        for (MapleCoolDownValueHolder mc : coolDowns.values()) {
            if (mc != null) {
                ret.add(mc);
            }
        }
        return ret;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Diseases">
    public final List<MapleDiseaseValueHolder> getAllDiseases() {
        return diseases.values().stream().collect(Collectors.toList());
    }

    public final boolean hasDisease(final MapleDisease dis) {
        return diseases.containsKey(dis);
    }

    public void giveDebuff(MapleDisease disease, MobSkill skill) {
        if ((this.map != null) && (!hasDisease(disease))) {
            if ((disease != MapleDisease.SEDUCE) && (disease != MapleDisease.STUN) && (disease != MapleDisease.FLAG)
                    && (getBuffedValue(CharacterTemporaryStat.AdvancedBless) != null)) {
                return;
            }

            int mC = getBuffSource(CharacterTemporaryStat.Mechanic);
            if ((mC > 0) && (mC != 35121005)) {
                return;
            }
            if ((this.stats.asrR > 0) && (Randomizer.nextInt(100) < this.stats.asrR)) {
                return;
            }

            this.diseases.put(disease, new MapleDiseaseValueHolder(disease, System.currentTimeMillis(), skill.getDuration() - this.stats.decreaseDebuff));
            this.client.SendPacket(BuffPacket.giveDebuff(disease, skill));
            this.map.broadcastMessage(this, BuffPacket.giveForeignDebuff(this.id, disease, skill), false);

            if ((skill.getX() > 0) && (disease == MapleDisease.POISON)) {
                addHP((int) (-(skill.getX() * ((skill.getDuration() - this.stats.decreaseDebuff) / 1000L))));
            }
        }
    }

    public final void giveSilentDebuff(final List<MapleDiseaseValueHolder> ld) {
        if (ld != null) {
            ld.stream().forEach((disease) -> {
                diseases.put(disease.disease, disease);
            });
        }
    }

    public void dispelDebuff(MapleDisease debuff) {
        if (hasDisease(debuff)) {
            client.SendPacket(BuffPacket.cancelDebuff(debuff));
            map.broadcastMessage(this, BuffPacket.cancelForeignDebuff(id, debuff), false);

            diseases.remove(debuff);
        }
    }

    public void dispelDebuffs() {
        diseases.keySet().stream().forEach((d) -> {
            dispelDebuff(d);
        });
    }

    public void cancelAllDebuffs() {
        diseases.clear();
    }
    // </editor-fold>

    public void setLevel(final short level) {
        this.level = level;
    }

    public void sendNote(String to, String msg) {
        sendNote(to, msg, 0);
    }

    public void sendNote(String to, String msg, int fame) {
        MapleCharacterUtil.sendNote(to, getName(), msg, fame);
    }

    public void sendMapleGMNote(String to, String msg, int fame) {
        MapleCharacterUtil.sendNote(to, "MapleGM", msg, fame);
    }

    public void showNote() {
        try (Connection con = Database.GetConnection()) {
            System.out.println(Thread.currentThread().getStackTrace()[2].getClassName() + "." + Thread.currentThread().getStackTrace()[2].getMethodName());
            try (PreparedStatement ps = con.prepareStatement("SELECT * FROM notes WHERE `to`=?", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
                ps.setString(1, getName());
                try (ResultSet rs = ps.executeQuery()) {
                    rs.last();
                    int count = rs.getRow();
                    rs.first();
                    client.SendPacket(CSPacket.showNotes(rs, count));
                }
            }
        } catch (SQLException e) {
            LogHelper.SQL.get().info("[SQL] There was an issue with something from the database:\n", e);
        }
    }

    public void deleteNote(int id, int fame) {
        try (Connection con = Database.GetConnection()) {
            System.out.println(Thread.currentThread().getStackTrace()[2].getClassName() + "." + Thread.currentThread().getStackTrace()[2].getMethodName());
            PreparedStatement ps = con.prepareStatement("SELECT gift FROM notes WHERE `id`=?");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                if (rs.getInt("gift") == fame && fame > 0) { //not exploited! hurray
                    addFame(fame);
                    updateSingleStat(MapleStat.FAME, getFame());
                    FameMessage fameMessage = new FameMessage(fame);
                    write(CWvsContext.messagePacket(fameMessage));
                }
            }
            rs.close();
            ps.close();
            ps = con.prepareStatement("DELETE FROM notes WHERE `id`=?");
            ps.setInt(1, id);
            ps.execute();
            ps.close();
        } catch (SQLException e) {
            LogHelper.SQL.get().info("[SQL] There was an issue with something from the database:\n", e);
        }
    }

    public int getMulungEnergy() {
        return mulung_energy;
    }

    public void mulungEnergyModifier(boolean inc) {
        if (inc) {
            if (mulung_energy + 100 > 10000) {
                mulung_energy = 10000;
            } else {
                mulung_energy += 100;
            }
        } else {
            mulung_energy = 0;
        }
        client.SendPacket(CWvsContext.MulungEnergy(mulung_energy));
    }

    public void writeMulungEnergy() {
        client.SendPacket(CWvsContext.MulungEnergy(mulung_energy));
    }

    public void writeEnergy(String type, String inc) {
        client.SendPacket(CWvsContext.sendPyramidEnergy(type, inc));
    }

    public void writeStatus(String type, String inc) {
        client.SendPacket(CWvsContext.sendGhostStatus(type, inc));
    }

    public void writePoint(String type, String inc) {
        client.SendPacket(CWvsContext.sendGhostPoint(type, inc));
    }

    // <editor-fold defaultstate="visible" desc="Attack combos"> 
    public short getCombo() {
        return combo;
    }

    public void setCombo(short combo) {
        this.combo = combo;
    }

    public final long getLastComboTime() {
        return lastComboTime;
    }

    public void setLastCombo(final long combo) {
        this.lastComboTime = combo;
    }
    // </editor-fold>

    public final long getKeyDownSkill_Time() {
        return keydown_skill;
    }

    public void setKeyDownSkillTime(final long keydown_skill) {
        this.keydown_skill = keydown_skill;
    }

    public void checkBerserk() { //berserk is special in that it doesn't use worldtimer :)
        if (job != 132 || lastBerserkTime < 0 || lastBerserkTime + 10000 > System.currentTimeMillis()) {
            return;
        }
        final Skill BerserkX = SkillFactory.getSkill(1320006);
        final int skilllevel = getTotalSkillLevel(BerserkX);
        if (skilllevel >= 1 && map != null) {
            lastBerserkTime = System.currentTimeMillis();
            final MapleStatEffect ampStat = BerserkX.getEffect(skilllevel);
            stats.Berserk = stats.getHp() * 100 / stats.getCurrentMaxHp() >= ampStat.getX();
            client.SendPacket(EffectPacket.showOwnBuffEffect(1320006, UserEffectCodes.SkillUse, getLevel(), skilllevel, (byte) (stats.Berserk ? 1 : 0)));
            map.broadcastMessage(this, EffectPacket.showBuffeffect(getId(), 1320006, UserEffectCodes.SkillUse, getLevel(), skilllevel, (byte) (stats.Berserk ? 1 : 0)), false);
        } else {
            lastBerserkTime = -1; // somebody thre? O_O
        }
    }

    public void setChalkboard(String text) {
        this.chalktext = text;
        if (map != null) {
            map.broadcastMessage(CSPacket.useChalkboard(getId(), text));
        }
    }

    public String getChalkboard() {
        return chalktext;
    }

    public MapleMount getMount() {
        return mount;
    }

    public int[] getWishlist() {
        return wishlist;
    }

    public void clearWishlist() {
        for (int i = 0; i < 10; i++) {
            wishlist[i] = 0;
        }
        changed_wishlist = true;
    }

    public int getWishlistSize() {
        int ret = 0;
        for (int i = 0; i < 10; i++) {
            if (wishlist[i] > 0) {
                ret++;
            }
        }
        return ret;
    }

    public void setWishlist(int[] wl) {
        this.wishlist = wl;
        changed_wishlist = true;
    }

    public int[] getRocks() {
        return rocks;
    }

    public int getRockSize() {
        int ret = 0;
        for (int i = 0; i < 10; i++) {
            if (rocks[i] != 999999999) {
                ret++;
            }
        }
        return ret;
    }

    public void deleteFromRocks(int map) {
        for (int i = 0; i < 10; i++) {
            if (rocks[i] == map) {
                rocks[i] = 999999999;
                changed_trocklocations = true;
                break;
            }
        }
    }

    public void addRockMap() {
        if (getRockSize() >= 10) {
            return;
        }
        rocks[getRockSize()] = getMapId();
        changed_trocklocations = true;
    }

    public boolean isRockMap(int id) {
        for (int i = 0; i < 10; i++) {
            if (rocks[i] == id) {
                return true;
            }
        }
        return false;
    }

    public int[] getRegRocks() {
        return regrocks;
    }

    public int getRegRockSize() {
        int ret = 0;
        for (int i = 0; i < 5; i++) {
            if (regrocks[i] != 999999999) {
                ret++;
            }
        }
        return ret;
    }

    public void deleteFromRegRocks(int map) {
        for (int i = 0; i < 5; i++) {
            if (regrocks[i] == map) {
                regrocks[i] = 999999999;
                changed_regrocklocations = true;
                break;
            }
        }
    }

    public void addRegRockMap() {
        if (getRegRockSize() >= 5) {
            return;
        }
        regrocks[getRegRockSize()] = getMapId();
        changed_regrocklocations = true;
    }

    public boolean isRegRockMap(int id) {
        for (int i = 0; i < 5; i++) {
            if (regrocks[i] == id) {
                return true;
            }
        }
        return false;
    }

    public int[] getHyperRocks() {
        return hyperrocks;
    }

    public int getHyperRockSize() {
        int ret = 0;
        for (int i = 0; i < 13; i++) {
            if (hyperrocks[i] != 999999999) {
                ret++;
            }
        }
        return ret;
    }

    public void deleteFromHyperRocks(int map) {
        for (int i = 0; i < 13; i++) {
            if (hyperrocks[i] == map) {
                hyperrocks[i] = 999999999;
                changed_hyperrocklocations = true;
                break;
            }
        }
    }

    public void addHyperRockMap() {
        if (getRegRockSize() >= 13) {
            return;
        }
        hyperrocks[getHyperRockSize()] = getMapId();
        changed_hyperrocklocations = true;
    }

    public boolean isHyperRockMap(int id) {
        for (int i = 0; i < 13; i++) {
            if (hyperrocks[i] == id) {
                return true;
            }
        }
        return false;
    }

    public void dropMessage(int type, String message) {
        switch (type) {
            case -1:
                client.SendPacket(CWvsContext.getTopMsg(message));
                break;
            case -2:
                client.SendPacket(PlayerShopPacket.shopChat(message, 0)); //0 or what
                break;
            case -3:
                client.SendPacket(CField.getChatText(getId(), message, isGM(), false)); //1 = hide
                break;
            case -4:
                client.SendPacket(CField.getChatText(getId(), message, isGM(), true)); //1 = hide
                break;
            case -5:
                client.SendPacket(CField.getGameMessage(message, (short) 6)); //pink
                break;
            case -6:
                client.SendPacket(CField.getGameMessage(message, (short) 11)); //white bg
                break;
            case -7:
                client.SendPacket(CWvsContext.getMidMsg(message, false, 0));
                break;
            case -8:
                client.SendPacket(CWvsContext.getMidMsg(message, true, 0));
            default:
                client.SendPacket(CWvsContext.broadcastMsg(type, message));
        }
    }

    public void showMessage(int type, String msg) {
        client.SendPacket(CField.getGameMessage(msg, (short) type));
    }

    public void showInfo(String caption, boolean pink, String msg) {
        short type = (short) (pink ? 6 : 7);
        if (caption == null || caption.isEmpty()) {
            client.SendPacket(CField.getGameMessage(msg, type));
        } else {
            client.SendPacket(CField.getGameMessage("[" + caption + "] " + msg, type));
        }
    }

    public void yellowMessage(String text) {
        client.SendPacket(CField.getGameMessage(text, (short) 7));
    }

    public IMaplePlayerShop getPlayerShop() {
        return playerShop;
    }

    public void setPlayerShop(IMaplePlayerShop playerShop) {
        this.playerShop = playerShop;
    }

    public int getDirection() {
        return insd.get();
    }

    public void setDirection(int inst) {
        this.insd.set(inst);
    }

    public MapleCarnivalParty getCarnivalParty() {
        return carnivalParty;
    }

    public void setCarnivalParty(MapleCarnivalParty party) {
        carnivalParty = party;
    }

    public void addCP(int ammount) {
        totalCP += ammount;
        availableCP += ammount;
    }

    public void useCP(int ammount) {
        availableCP -= ammount;
    }

    public int getAvailableCP() {
        return availableCP;
    }

    public int getTotalCP() {
        return totalCP;
    }

    public void resetCP() {
        totalCP = 0;
        availableCP = 0;
    }

    public void addCarnivalRequest(MapleCarnivalChallenge request) {
        pendingCarnivalRequests.add(request);
    }

    public final MapleCarnivalChallenge getNextCarnivalRequest() {
        return pendingCarnivalRequests.pollLast();
    }

    public void clearCarnivalRequests() {
        pendingCarnivalRequests = new LinkedList<>();
    }

    public void startMonsterCarnival(final int enemyavailable, final int enemytotal) {
        client.SendPacket(MonsterCarnivalPacket.startMonsterCarnival(this, enemyavailable, enemytotal));
    }

    public void CPUpdate(final boolean party, final int available, final int total, final int team) {
        client.SendPacket(MonsterCarnivalPacket.CPUpdate(party, available, total, team));
    }

    public void playerDiedCPQ(final String name, final int lostCP, final int team) {
        client.SendPacket(MonsterCarnivalPacket.playerDiedMessage(name, lostCP, team));
    }

    public void setAchievementFinished(int id) {
        if (!finishedAchievements.contains(id)) {
            finishedAchievements.add(id);
            changed_achievements = true;
        }
    }

    public boolean achievementFinished(int achievementid) {
        return finishedAchievements.contains(achievementid);
    }

    public void finishAchievement(int id) {
        if (!achievementFinished(id)) {
            if (isAlive() && !isClone()) {
                MapleAchievements.getInstance().getById(id).finishAchievement(this);
            }
        }
    }

    public List<Integer> getFinishedAchievements() {
        return finishedAchievements;
    }

    public boolean getCanTalk() {
        return this.canTalk;
    }

    public void canTalk(boolean talk) {
        this.canTalk = talk;
    }

    public double getEXPMod() {
        return stats.expMod;
    }

    public double getDropMod() {
        return stats.dropMod;
    }

    public int getCashMod() {
        return stats.cashMod;
    }

    public void setPoints(int p) {
        this.points = p;
        if (this.points >= 1) {
            finishAchievement(1);
        }
    }

    public int getPoints() {
        return points;
    }

    public void setVPoints(int p) {
        this.vpoints = p;
    }

    public int getVPoints() {
        return vpoints;
    }

    public int getStarterQuestID() {
        return starterquestid;
    }

    public int getEvoEntry() {
        return evoentry;
    }

    public void setEvoEntry(int p) {
        this.evoentry -= p;
    }

    int time = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);

    public void refillEvo(int p) {
        if (Calendar.HOUR_OF_DAY != 12) {
            this.setEvoEntry(5);
            try {
                try (PreparedStatement ps = Database.GetConnection().prepareStatement("UPDATE characters SET evoentry = 5")) {
                    ps.executeUpdate();
                }
            } catch (SQLException ex) {
                throw new RuntimeException("Runtime Exception - Could not connect to MySql Server.");
            }
        }
        if (Calendar.HOUR_OF_DAY != 24) {
            this.setEvoEntry(5);
            try {
                try (PreparedStatement ps = Database.GetConnection().prepareStatement("UPDATE characters SET evoentry = 5")) {
                    ps.executeUpdate();
                }
            } catch (SQLException ex) {
                throw new RuntimeException("Runtime Exception - Could not connect to MySql Server.");
            }
        }
    }

    public int getStarterQuest() {
        return starterquest;
    }

    public void setStarterQuest(int p) {
        this.starterquest = p;
    }

    public void setStarterQuestID(int p) {
        this.starterquestid = p;
    }

    public void setEPoints(int p) {
        this.epoints = p;
    }

    public int getEPoints() {
        return epoints;
    }

    public void setDPoints(int p) {
        this.dpoints = p;
    }

    public int getDPoints() {
        return dpoints;
    }

    public int getGMLevel() {
        return gmLevel;
    }

    public CashShop getCashInventory() {
        return cs;
    }

    public void removeItem(int id, int quantity) {
        MapleInventoryManipulator.removeById(client, GameConstants.getInventoryType(id), id, quantity, true, false);
        client.SendPacket(InfoPacket.getShowItemGain(id, (short) quantity, true));
    }

    public void removeAll(int id) {
        removeAll(id, true);
    }

    public void removeAll(int id, boolean show) {
        MapleInventoryType type = GameConstants.getInventoryType(id);
        int possessed = getInventory(type).countById(id);

        if (possessed > 0) {
            MapleInventoryManipulator.removeById(getClient(), type, id, possessed, true, false);
            if (show) {
                getClient().SendPacket(InfoPacket.getShowItemGain(id, (short) -possessed, true));
            }
        }
        /*
         * if (type == MapleInventoryType.EQUIP) { //check equipped type =
         * MapleInventoryType.EQUIPPED; possessed =
         * getInventory(type).countById(id);
         *
         * if (possessed > 0) {
         * MapleInventoryManipulator.removeById(getClient(), type, id,
         * possessed, true, false);
         * getClient().write(CField.getShowItemGain(id,
         * (short)-possessed, true)); } }
         */
    }

    public Triple<List<MapleRing>, List<MapleRing>, List<MapleRing>> getRings(boolean equip) {
        MapleInventory iv = getInventory(MapleInventoryType.EQUIPPED);
        List<Item> equipped = iv.newList();
        Collections.sort(equipped);
        List<MapleRing> crings = new ArrayList<>(), frings = new ArrayList<>(), mrings = new ArrayList<>();
        MapleRing ring;
        for (Item ite : equipped) {
            Equip item = (Equip) ite;
            if (item.getRing() != null) {
                ring = item.getRing();
                ring.setEquipped(true);
                if (GameConstants.isEffectRing(item.getItemId())) {
                    if (equip) {
                        if (GameConstants.isCrushRing(item.getItemId())) {
                            crings.add(ring);
                        } else if (GameConstants.isFriendshipRing(item.getItemId())) {
                            frings.add(ring);
                        } else if (GameConstants.isMarriageRing(item.getItemId())) {
                            mrings.add(ring);
                        }
                    } else if (crings.isEmpty() && GameConstants.isCrushRing(item.getItemId())) {
                        crings.add(ring);
                    } else if (frings.isEmpty() && GameConstants.isFriendshipRing(item.getItemId())) {
                        frings.add(ring);
                    } else if (mrings.isEmpty() && GameConstants.isMarriageRing(item.getItemId())) {
                        mrings.add(ring);
                    } //for 3rd person the actual slot doesnt matter, so we'll use this to have both shirt/ring same?
                    //however there seems to be something else behind this, will have to sniff someone with shirt and ring, or more conveniently 3-4 of those
                }
            }
        }
        if (equip) {
            iv = getInventory(MapleInventoryType.EQUIP);
            for (Item ite : iv.list()) {
                Equip item = (Equip) ite;
                if (item.getRing() != null && GameConstants.isCrushRing(item.getItemId())) {
                    ring = item.getRing();
                    ring.setEquipped(false);
                    if (GameConstants.isFriendshipRing(item.getItemId())) {
                        frings.add(ring);
                    } else if (GameConstants.isCrushRing(item.getItemId())) {
                        crings.add(ring);
                    } else if (GameConstants.isMarriageRing(item.getItemId())) {
                        mrings.add(ring);
                    }
                }
            }
        }
        Collections.sort(frings, new MapleRing.RingComparator());
        Collections.sort(crings, new MapleRing.RingComparator());
        Collections.sort(mrings, new MapleRing.RingComparator());
        return new Triple<>(crings, frings, mrings);
    }

    public int getFh() {
        MapleFoothold fh = getMap().getSharedMapResources().footholds.findBelow(getTruePosition());
        if (fh != null) {
            return fh.getId();
        }
        return 0;
    }

    public void startFairySchedule(boolean exp) {
        startFairySchedule(exp, false);
    }

    public void startFairySchedule(boolean exp, boolean equipped) {
        cancelFairySchedule(exp || stats.equippedFairy == 0);
        if (fairyExp <= 0) {
            fairyExp = (byte) stats.equippedFairy;
        }
        if (equipped && fairyExp < stats.equippedFairy * 3 && stats.equippedFairy > 0) {
            dropMessage(5, "The Fairy Pendant's experience points will increase to " + (fairyExp + stats.equippedFairy) + "% after one hour.");
        }
        lastFairyTime = System.currentTimeMillis();
    }

    public final boolean canFairy(long now) {
        return lastFairyTime > 0 && lastFairyTime + (60 * 60 * 1000) < now;
    }

    public final boolean canHP(long now) {
        if (lastHPTime + 5000 < now) {
            lastHPTime = now;
            return true;
        }
        return false;
    }

    public final boolean canMP(long now) {
        if (lastMPTime + 5000 < now) {
            lastMPTime = now;
            return true;
        }
        return false;
    }

    public final boolean canHPRecover(long now) {
        if (stats.hpRecoverTime > 0 && lastHPTime + stats.hpRecoverTime < now) {
            lastHPTime = now;
            return true;
        }
        return false;
    }

    public final boolean canMPRecover(long now) {
        if (stats.mpRecoverTime > 0 && lastMPTime + stats.mpRecoverTime < now) {
            lastMPTime = now;
            return true;
        }
        return false;
    }

    public void cancelFairySchedule(boolean exp) {
        lastFairyTime = 0;
        if (exp) {
            this.fairyExp = 0;
        }
    }

    public void doFairy() {
        if (fairyExp < stats.equippedFairy * 3 && stats.equippedFairy > 0) {
            fairyExp += stats.equippedFairy;
            dropMessage(5, "The Fairy Pendant's EXP was boosted to " + fairyExp + "%.");
        }
        if (getGuildId() > 0) {
            World.Guild.gainGP(getGuildId(), 20, id);
            GuildPointMessage gp = new GuildPointMessage(20);
            client.SendPacket(CWvsContext.messagePacket(gp));
        }
        traits.get(MapleTraitType.will).addExp(5, this); //willpower every hour
        startFairySchedule(false, true);
    }

    public byte getFairyExp() {
        return fairyExp;
    }

    public int getTeam() {
        return coconutteam;
    }

    public void setTeam(int v) {
        this.coconutteam = v;
    }

    public void spawnPet(byte slot) {
        spawnPet(slot, false, true);
    }

    public void spawnPet(byte slot, boolean lead) {
        spawnPet(slot, lead, true);
    }

    public void spawnPet(byte slot, boolean lead, boolean broadcast) {
        final Item item = getInventory(MapleInventoryType.CASH).getItem(slot);
        if (item == null || item.getItemId() >= 5010000 || item.getItemId() < 5000000) {
            return;
        }
        switch (item.getItemId()) {
            case 5000047:
            case 5000028: {
                /*final MaplePet pet = MaplePet.createPet(item.getItemId() + 1, MapleInventoryIdentifier.getInstance());
                if (pet != null) {
                    MapleInventoryManipulator.addById(client, item.getItemId() + 1, (short) 1, item.getOwner(), pet, 45, false, "Evolved from pet " + item.getItemId() + " on " + LocalDateTime.now());
                    MapleInventoryManipulator.removeFromSlot(client, MapleInventoryType.CASH, slot, (short) 1, false);
                }*/
                getMap().spawnItemDrop(this, this, new client.inventory.Item(item.getItemId() + 1, (byte) 0, (short) 1, (byte) 0), this.getPosition(), true, true, false);
                MapleInventoryManipulator.removeFromSlot(client, MapleInventoryType.CASH, slot, (short) 1, false);
                break;
            }
            default: {
                final Pet pet = item.getPet();
                if (pet != null && (item.getItemId() != 5000054 || pet.getSecondsLeft() > 0) && (item.getExpiration() == -1 || item.getExpiration() > System.currentTimeMillis())) {
                    if (pet.getSummoned()) { // Already summoned, let's keep it
                        unequipPet(pet, true, false);
                        pet.setSummoned(0);
                        dropMessage(5, "Your pet has been put away.");
                    } else {
                        int leadid = 8;
                        if (GameConstants.isCygnusKnight(getJob())) {
                            leadid = 10000018;
                        } else if (GameConstants.isAran(getJob())) {
                            leadid = 20000024;
                        } else if (GameConstants.isEvan(getJob())) {
                            leadid = 20011024;
                        } else if (GameConstants.isMercedes(getJob())) {
                            leadid = 20021024;
                        } else if (GameConstants.isPhantom(getJob())) {
                            leadid = 20031024;
                        } else if (GameConstants.isDemonSlayer(getJob())) {
                            leadid = 30011024;
                        } else if (GameConstants.isResistance(getJob())) {
                            leadid = 30001024;
                        } else if (GameConstants.isMihile(getJob())) {
                            leadid = 50001018;
                        } else if (GameConstants.isCannoneer(getJob())) {
                            leadid = 10008;
                        }
                        if (getSkillLevel(SkillFactory.getSkill(leadid)) == 0 && getPet(0) != null) {
                            unequipPet(getPet(0), false, false);
                        } else if (lead && getSkillLevel(SkillFactory.getSkill(leadid)) > 0) { // Follow the Lead
                            //shiftPetsRight();
                        }

                        pet.setPos(getPosition());
                        try {
                            pet.setFh(getMap().getSharedMapResources().footholds.findBelow(getPosition()).getId());
                        } catch (NullPointerException e) {
                            pet.setFh(0); // Moving fixes this Foodhold.
                        }
                        pet.setStance(0);
                        pet.setSummoned(1); // Let Summoned be True
                        addPet(pet);
                        pet.setSummoned(getPetIndex(pet) + 1); // Get the Pet's Index
                        if (broadcast && getMap() != null) {
                            client.SendPacket(PetPacket.updatePet(pet, getInventory(MapleInventoryType.CASH).getItem(slot), false));
                            //List<ModifyInventory> aMod = new ArrayList<>();
                            //Item pItem = pet.getItem();
                            //aMod.add(new ModifyInventory(ModifyInventoryOperation.Remove, pItem));
                            //aMod.add(new ModifyInventory(ModifyInventoryOperation.AddItem, pItem));
                            //client.write(CWvsContext.inventoryOperation(true, aMod));
                            map.broadcastMessage(this, PetPacket.OnActivated(this.id, getPetIndex(pet), true, pet, 0), true);
                            client.SendPacket(PetPacket.showPetUpdate(this, pet.getItem().getUniqueId(), (byte) (pet.getSummonedValue() - 1)));
                        }
                        dropMessage(5, "Your pet has been summoned, you may only have one pet active at a time.");

                        if (ServerConstants.DEVELOPER_DEBUG_MODE) {
                            System.out.println("[Debug] Summoned Pet (" + pet.getItem().getItemId() + ")");
                        }
                    }
                }
                break;
            }
        }
        client.SendPacket(CWvsContext.enableActions());
    }

    public void clearLinkMid() {
        linkMobs.clear();
        //cancelEffectFromTemporaryStat(CharacterTemporaryStat.HOMING_BEACON);
        //cancelEffectFromTemporaryStat(CharacterTemporaryStat.MANY_USES);
    }

    public int getFirstLinkMid() {
        for (Integer lm : linkMobs.keySet()) {
            return lm.intValue();
        }
        return 0;
    }

    public Map<Integer, Integer> getAllLinkMid() {
        return linkMobs;
    }

    public void setLinkMid(int lm, int x) {
        linkMobs.put(lm, x);
    }

    public int getDamageIncrease(int lm) {
        if (linkMobs.containsKey(lm)) {
            return linkMobs.get(lm);
        }
        return 0;
    }

    public void setDragon(MapleDragon d) {
        this.dragon = d;
    }

    public void setHaku(MapleHaku h) {
        this.haku = h;
    }

    public MapleExtractor getExtractor() {
        return extractor;
    }

    public void setExtractor(MapleExtractor me) {
        removeExtractor();
        this.extractor = me;
    }

    public void removeExtractor() {
        if (extractor != null) {
            map.broadcastMessage(CField.removeExtractor(this.id));
            map.removeMapObject(extractor);
            extractor = null;
        }
    }

    public final void spawnSavedPets() {
        for (int i = 0; i < petStore.length; i++) {
            if (petStore[i] > -1) {
                spawnPet(petStore[i], true, true);
                //spawnPet(petStore[i], false, false);
            }
        }
        petStore = new byte[]{-1, -1, -1};
    }

    public final byte[] getPetStores() {
        return petStore;
    }

    public void resetStats(final int str, final int dex, final int int_, final int luk) {
        Map<MapleStat, Long> stat = new EnumMap<>(MapleStat.class
        );
        int total = stats.getStr() + stats.getDex() + stats.getLuk() + stats.getInt() + getRemainingAp();

        total -= str;
        stats.str = (int) str;

        total -= dex;
        stats.dex = (int) dex;

        total -= int_;
        stats.int_ = (int) int_;

        total -= luk;
        stats.luk = (int) luk;

        setRemainingAp((int) total);
        stats.recalcLocalStats(this);
        stat.put(MapleStat.STR, (long) str);
        stat.put(MapleStat.DEX, (long) dex);
        stat.put(MapleStat.INT, (long) int_);
        stat.put(MapleStat.LUK, (long) luk);
        stat.put(MapleStat.AVAILABLEAP, (long) total);
        client.SendPacket(CWvsContext.updatePlayerStats(stat, false, this));
    }

    public Event_PyramidSubway getPyramidSubway() {
        return pyramidSubway;
    }

    public void setPyramidSubway(Event_PyramidSubway ps) {
        this.pyramidSubway = ps;
    }

    public byte getSubcategory() {
        if (GameConstants.isJett(job)) {
            return 10;
        }
        if (job >= 430 && job <= 434) {
            return 1;
        }
        if (GameConstants.isCannoneer(job) || job == 1) {
            return 2;
        }
        if (job != 0 && job != 400) {
            return 0;
        }
        return subcategory;
    }

    public void setSubcategory(int z) {
        this.subcategory = (byte) z;
    }

    public int itemQuantity(final int itemid) {
        return getInventory(GameConstants.getInventoryType(itemid)).countById(itemid);
    }

    public void setRPS(RockPaperScissors rps) {
        this.rps = rps;
    }

    public RockPaperScissors getRPS() {
        return rps;
    }

    public long getNextConsume() {
        return nextConsume;
    }

    public void setNextConsume(long nc) {
        this.nextConsume = nc;
    }

    public int getRank() {
        return rank;
    }

    public int getRankMove() {
        return rankMove;
    }

    public int getJobRank() {
        return jobRank;
    }

    public int getJobRankMove() {
        return jobRankMove;
    }

    public void getAllModes() {
        if (GameConstants.isBeastTamer(job)) {
            final Map<Skill, SkillEntry> ss = new HashMap<>();
            ss.put(SkillFactory.getSkill(110001501), new SkillEntry((byte) 1, (byte) 1, -1));
            ss.put(SkillFactory.getSkill(110001502), new SkillEntry((byte) 1, (byte) 1, -1));
            ss.put(SkillFactory.getSkill(110001503), new SkillEntry((byte) 1, (byte) 1, -1));
            ss.put(SkillFactory.getSkill(110001504), new SkillEntry((byte) 1, (byte) 1, -1));
            changeSkillsLevel(ss);
            NPCScriptManager.getInstance().dispose(client);
        } else {
            dropMessage(6, "You are not beast tamer!");
        }
    }

    public void changeChannel(final int channel) {
        final ChannelServer toch = ChannelServer.getInstance(channel);

        if (channel == client.getChannel() || toch == null || toch.isShutdown()) {
            client.SendPacket(CWvsContext.broadcastMsg("Channel unavailable."));
            return;
        }
        final ChannelServer ch = ChannelServer.getInstance(client.getChannel());
        if (getMessenger() != null) {
            World.Messenger.silentLeaveMessenger(getMessenger().getId(), new MapleMessengerCharacter(this));
        }
        PlayerBuffStorage.addBuffsToStorage(getId(), getAllBuffs());
        PlayerBuffStorage.addCooldownsToStorage(getId(), getCooldowns());
        PlayerBuffStorage.addDiseaseToStorage(getId(), getAllDiseases());

        World.changeChannelData(new CharacterTransfer(this), getId(), channel);
        ch.removePlayer(this);
        String s = client.getSessionIPAddress();
        client.updateLoginState(MapleClient.MapleClientLoginState.CHANGE_CHANNEL, s);
        LoginServer.addIPAuth(s.substring(s.indexOf('/') + 1, s.length()));
        client.SendPacket(CField.getChannelChange(client, Integer.parseInt(toch.getIP().split(":")[1])));
        saveToDB(false, false);
        getMap().removePlayer(this);

        client.setPlayer(null);
        client.setReceiving(false);
    }

    public void forceChangeChannel(final int channel) {
        final ChannelServer toch = ChannelServer.getInstance(channel);
        final ChannelServer ch = ChannelServer.getInstance(client.getChannel());
        if (getMessenger() != null) {
            World.Messenger.silentLeaveMessenger(getMessenger().getId(), new MapleMessengerCharacter(this));
        }
        PlayerBuffStorage.addBuffsToStorage(getId(), getAllBuffs());
        PlayerBuffStorage.addCooldownsToStorage(getId(), getCooldowns());
        PlayerBuffStorage.addDiseaseToStorage(getId(), getAllDiseases());

        World.changeChannelData(new CharacterTransfer(this), getId(), channel);
        ch.removePlayer(this);
        client.updateLoginState(MapleClient.MapleClientLoginState.CHANGE_CHANNEL, client.getSessionIPAddress());

        final String s = client.getSessionIPAddress();
        LoginServer.addIPAuth(s.substring(s.indexOf('/') + 1, s.length()));
        client.SendPacket(CField.getChannelChange(client, Integer.parseInt(toch.getIP().split(":")[1])));
        saveToDB(false, false);
        getMap().removePlayer(this);

        client.setPlayer(null);
        client.setReceiving(false);
    }

    public void expandInventory(byte type, int amount) {
        final MapleInventory inv = getInventory(MapleInventoryType.getByType(type));
        inv.addSlot((byte) amount);
        client.SendPacket(CWvsContext.getSlotUpdate(type, (byte) inv.getSlotLimit()));
    }

    public boolean allowedToTarget(User other) {
        return other != null && (!other.isHidden() || getGMLevel() >= other.getGMLevel());
    }

    public int getFollowId() {
        return followid;
    }

    public void setFollowId(int fi) {
        this.followid = fi;
        if (fi == 0) {
            this.followinitiator = false;
            this.followon = false;
        }
    }

    public void setFollowInitiator(boolean fi) {
        this.followinitiator = fi;
    }

    public void setFollowOn(boolean fi) {
        this.followon = fi;
    }

    public boolean isFollowOn() {
        return followon;
    }

    public boolean isFollowInitiator() {
        return followinitiator;
    }

    public void checkFollow() {
        if (followid <= 0) {
            return;
        }
        if (followon) {
            map.broadcastMessage(CField.followEffect(id, 0, null));
            map.broadcastMessage(CField.followEffect(followid, 0, null));
        }
        User tt = map.getCharacterById(followid);
        client.SendPacket(CField.getFollowMessage("Follow canceled."));
        if (tt != null) {
            tt.setFollowId(0);
            tt.getClient().SendPacket(CField.getFollowMessage("Follow canceled."));
        }
        setFollowId(0);
    }

    public int getDamageSkin() {
        final List<MapleQuestStatus> started = getStartedQuests();
        String customdata = "0";
        for (final MapleQuestStatus q : started) {
            if (q.getQuest().getId() == 7291 && q.getCustomData() != null) {
                customdata = q.getCustomData();
            }
        }
        return Integer.parseInt(customdata);
    }

    public int getMarriageId() {
        return marriageId;
    }

    public void setMarriageId(final int mi) {
        this.marriageId = mi;
    }

    public int getMarriageItemId() {
        return marriageItemId;
    }

    public void setMarriageItemId(final int mi) {
        this.marriageItemId = mi;
    }

    public MapleMarriage getMarriage() {
        return marriage;
    }

    public void setMarriage(MapleMarriage marriage) {
        this.marriage = marriage;
    }

    // TODO: gvup, vic, lose, draw, VR
    public boolean startPartyQuest(final int questid) {
        boolean ret = false;
        MapleQuest q = MapleQuest.getInstance(questid);
        if (q == null || !q.isPartyQuest()) {
            return false;
        }
        if (!quests.containsKey(q) || !questinfo.containsKey(questid)) {
            final MapleQuestStatus status = getQuestNAdd(q);
            status.setStatus(MapleQuestState.Started);
            updateQuest(status);

            switch (questid) {
                case 1300:
                case 1301:
                case 1302: //carnival, ariants.
                    updateInfoQuest(questid, "min=0;sec=0;date=0000-00-00;have=0;rank=F;try=0;cmp=0;CR=0;VR=0;gvup=0;vic=0;lose=0;draw=0");
                    break;
                case 1303: //ghost pq
                    updateInfoQuest(questid, "min=0;sec=0;date=0000-00-00;have=0;have1=0;rank=F;try=0;cmp=0;CR=0;VR=0;vic=0;lose=0");
                    break;
                case 1204: //herb town pq
                    updateInfoQuest(questid, "min=0;sec=0;date=0000-00-00;have0=0;have1=0;have2=0;have3=0;rank=F;try=0;cmp=0;CR=0;VR=0");
                    break;
                case 1206: //ellin pq
                    updateInfoQuest(questid, "min=0;sec=0;date=0000-00-00;have0=0;have1=0;rank=F;try=0;cmp=0;CR=0;VR=0");
                    break;
                default:
                    updateInfoQuest(questid, "min=0;sec=0;date=0000-00-00;have=0;rank=F;try=0;cmp=0;CR=0;VR=0");
                    break;
            }
            ret = true;
        } //started the quest.
        return ret;
    }

    public String getOneInfo(final int questid, final String key) {
        if (!questinfo.containsKey(questid) || key == null || MapleQuest.getInstance(questid) == null || !MapleQuest.getInstance(questid).isPartyQuest()) {
            return null;
        }
        final String[] split = questinfo.get(questid).split(";");
        for (String x : split) {
            final String[] split2 = x.split("="); //should be only 2
            if (split2.length == 2 && split2[0].equals(key)) {
                return split2[1];
            }
        }
        return null;
    }

    public void updateOneInfo(final int questid, final String key, final String value) {
        if (!questinfo.containsKey(questid) || key == null || value == null || MapleQuest.getInstance(questid) == null || !MapleQuest.getInstance(questid).isPartyQuest()) {
            return;
        }
        final String[] split = questinfo.get(questid).split(";");
        boolean changed = false;
        final StringBuilder newQuest = new StringBuilder();
        for (String x : split) {
            final String[] split2 = x.split("="); //should be only 2
            if (split2.length != 2) {
                continue;
            }
            if (split2[0].equals(key)) {
                newQuest.append(key).append("=").append(value);
            } else {
                newQuest.append(x);
            }
            newQuest.append(";");
            changed = true;
        }

        updateInfoQuest(questid, changed ? newQuest.toString().substring(0, newQuest.toString().length() - 1) : newQuest.toString());
    }

    public void recalcPartyQuestRank(final int questid) {
        if (MapleQuest.getInstance(questid) == null || !MapleQuest.getInstance(questid).isPartyQuest()) {
            return;
        }
        if (!startPartyQuest(questid)) {
            final String oldRank = getOneInfo(questid, "rank");
            if (oldRank == null || oldRank.equals("S")) {
                return;
            }
            String newRank;
            switch (oldRank) {
                case "A":
                    newRank = "S";
                    break;
                case "B":
                    newRank = "A";
                    break;
                case "C":
                    newRank = "B";
                    break;
                case "D":
                    newRank = "C";
                    break;
                case "F":
                    newRank = "D";
                    break;
                default:
                    return;
            }
            final List<Pair<String, Pair<String, Integer>>> questInfo = MapleQuest.getInstance(questid).getInfoByRank(newRank);
            if (questInfo == null) {
                return;
            }
            for (Pair<String, Pair<String, Integer>> q : questInfo) {
                boolean found = false;
                final String val = getOneInfo(questid, q.right.left);
                if (val == null) {
                    return;
                }
                int vall;
                try {
                    vall = Integer.parseInt(val);
                } catch (NumberFormatException e) {
                    return;
                }
                switch (q.left) {
                    case "less":
                        found = vall < q.right.right;
                        break;
                    case "more":
                        found = vall > q.right.right;
                        break;
                    case "equal":
                        found = vall == q.right.right;
                        break;
                }
                if (!found) {
                    return;
                }
            }
            //perfectly safe
            updateOneInfo(questid, "rank", newRank);
        }
    }

    public void tryPartyQuest(final int questid) {
        if (MapleQuest.getInstance(questid) == null || !MapleQuest.getInstance(questid).isPartyQuest()) {
            return;
        }
        try {
            startPartyQuest(questid);
            pqStartTime = System.currentTimeMillis();
            updateOneInfo(questid, "try", String.valueOf(Integer.parseInt(getOneInfo(questid, "try")) + 1));
        } catch (NumberFormatException e) {
            System.out.println("tryPartyQuest error");
        }
    }

    public void endPartyQuest(final int questid) {
        if (MapleQuest.getInstance(questid) == null || !MapleQuest.getInstance(questid).isPartyQuest()) {
            return;
        }
        try {
            startPartyQuest(questid);
            if (pqStartTime > 0) {
                final long changeTime = System.currentTimeMillis() - pqStartTime;
                final int mins = (int) (changeTime / 1000 / 60), secs = (int) (changeTime / 1000 % 60);
                final int mins2 = Integer.parseInt(getOneInfo(questid, "min"));
                if (mins2 <= 0 || mins < mins2) {
                    updateOneInfo(questid, "min", String.valueOf(mins));
                    updateOneInfo(questid, "sec", String.valueOf(secs));
                    updateOneInfo(questid, "date", LocalDateTime.now().toString());
                }
                final int newCmp = Integer.parseInt(getOneInfo(questid, "cmp")) + 1;
                updateOneInfo(questid, "cmp", String.valueOf(newCmp));
                updateOneInfo(questid, "CR", String.valueOf((int) Math.ceil((newCmp * 100.0) / Integer.parseInt(getOneInfo(questid, "try")))));
                recalcPartyQuestRank(questid);
                pqStartTime = 0;
            }
        } catch (Exception e) {
            System.out.println("endPartyQuest error");
        }

    }

    public void havePartyQuest(final int itemId) {
        int questid, index = -1;
        switch (itemId) {
            case 1002798:
                questid = 1200; //henesys
                break;
            case 1072369:
                questid = 1201; //kerning
                break;
            case 1022073:
                questid = 1202; //ludi
                break;
            case 1082232:
                questid = 1203; //orbis
                break;
            case 1002571:
            case 1002572:
            case 1002573:
            case 1002574:
                questid = 1204; //herbtown
                index = itemId - 1002571;
                break;
            case 1102226:
                questid = 1303; //ghost
                break;
            case 1102227:
                questid = 1303; //ghost
                index = 0;
                break;
            case 1122010:
                questid = 1205; //magatia
                break;
            case 1032061:
            case 1032060:
                questid = 1206; //ellin
                index = itemId - 1032060;
                break;
            case 3010018:
                questid = 1300; //ariant
                break;
            case 1122007:
                questid = 1301; //carnival
                break;
            case 1122058:
                questid = 1302; //carnival2
                break;
            default:
                return;
        }
        if (MapleQuest.getInstance(questid) == null || !MapleQuest.getInstance(questid).isPartyQuest()) {
            return;
        }
        startPartyQuest(questid);
        updateOneInfo(questid, "have" + (index == -1 ? "" : index), "1");
    }

    public void resetStatsByJob(boolean beginnerJob) {
        int baseJob = (beginnerJob ? (job % 1000) : (((job % 1000) / 100) * 100)); //1112 -> 112 -> 1 -> 100
        boolean UA = getQuestNoAdd(MapleQuest.getInstance(GameConstants.ULT_EXPLORER)) != null;
        switch (baseJob) {
            case 100:
                //first job = warrior
                resetStats(UA ? 4 : 35, 4, 4, 4);
                break;
            case 200:
                resetStats(4, 4, UA ? 4 : 20, 4);
                break;
            case 300:
            case 400:
                resetStats(4, UA ? 4 : 25, 4, 4);
                break;
            case 500:
                resetStats(4, UA ? 4 : 20, 4, 4);
                break;
            case 0:
                resetStats(4, 4, 4, 4);
                break;
            default:
                break;
        }
    }

    public boolean hasSummon() {
        return hasSummon;
    }

    public void setHasSummon(boolean summ) {
        this.hasSummon = summ;
    }

    public void removeDoor() {
        final MapleDoor door = getDoors().iterator().next();
        for (final User chr : door.getTarget().getCharacters()) {
            door.sendDestroyData(chr.getClient());
        }
        for (final User chr : door.getTown().getCharacters()) {
            door.sendDestroyData(chr.getClient());
        }
        for (final MapleDoor destroyDoor : getDoors()) {
            door.getTarget().removeMapObject(destroyDoor);
            door.getTown().removeMapObject(destroyDoor);
        }
        clearDoors();
    }

    public void removeMechDoor() {
        for (final MechDoor destroyDoor : getMechDoors()) {
            for (final User chr : getMap().getCharacters()) {
                destroyDoor.sendDestroyData(chr.getClient());
            }
            getMap().removeMapObject(destroyDoor);
        }
        clearMechDoors();
    }

    public void changeRemoval() {
        changeRemoval(false);
    }

    public void changeRemoval(boolean dc) {
        if (getCheatTracker() != null && dc) {
            getCheatTracker().dispose();
        }
        removeFamiliar();
        dispelSummons();
        if (!dc) {
            cancelEffectFromTemporaryStat(CharacterTemporaryStat.Flying);
            cancelEffectFromTemporaryStat(CharacterTemporaryStat.RideVehicle);
            cancelEffectFromTemporaryStat(CharacterTemporaryStat.Mechanic);
            cancelEffectFromTemporaryStat(CharacterTemporaryStat.Regen);
        }
        if (getPyramidSubway() != null) {
            getPyramidSubway().dispose(this);
        }
        if (playerShop != null && !dc) {
            playerShop.removeVisitor(this);
            if (playerShop.isOwner(this)) {
                playerShop.setOpen(true);
            }
        }
        if (!getDoors().isEmpty()) {
            removeDoor();
        }
        if (!getMechDoors().isEmpty()) {
            removeMechDoor();
        }
        NPCScriptManager.getInstance().dispose(client);
        cancelFairySchedule(false);
    }

    public void updateTick(int newTick) {
        anticheat.updateTick(newTick);
    }

    public boolean canUseFamilyBuff(MapleFamilyBuff buff) {
        final MapleQuestStatus stat = getQuestNoAdd(MapleQuest.getInstance(buff.questID));
        if (stat == null) {
            return true;
        }
        if (stat.getCustomData() == null) {
            stat.setCustomData("0");
        }
        return Long.parseLong(stat.getCustomData()) + (24 * 3600000) < System.currentTimeMillis();
    }

    public void useFamilyBuff(MapleFamilyBuff buff) {
        final MapleQuestStatus stat = getQuestNAdd(MapleQuest.getInstance(buff.questID));
        stat.setCustomData(String.valueOf(System.currentTimeMillis()));
    }

    public List<Integer> usedBuffs() {
        //assume count = 1
        List<Integer> used = new ArrayList<>();
        MapleFamilyBuff[] z = MapleFamilyBuff.values();
        for (int i = 0; i < z.length; i++) {
            if (!canUseFamilyBuff(z[i])) {
                used.add(i);
            }
        }
        return used;
    }

    public String getTeleportName() {
        return teleportname;
    }

    public void setTeleportName(final String tname) {
        teleportname = tname;
    }

    public int getNoJuniors() {
        if (mfc == null) {
            return 0;
        }
        return mfc.getNoJuniors();
    }

    public MapleFamilyCharacter getMFC() {
        return mfc;
    }

    public void makeMFC(final int familyid, final int seniorid, final int junior1, final int junior2) {
        if (familyid > 0) {
            MapleFamily f = World.Family.getFamily(familyid);
            if (f == null) {
                mfc = null;
            } else {
                mfc = f.getMFC(id);
                if (mfc == null) {
                    mfc = f.addFamilyMemberInfo(this, seniorid, junior1, junior2);
                }
                if (mfc.getSeniorId() != seniorid) {
                    mfc.setSeniorId(seniorid);
                }
                if (mfc.getJunior1() != junior1) {
                    mfc.setJunior1(junior1);
                }
                if (mfc.getJunior2() != junior2) {
                    mfc.setJunior2(junior2);
                }
            }
        } else {
            mfc = null;
        }
    }

    public void setFamily(final int newf, final int news, final int newj1, final int newj2) {
        if (mfc == null || newf != mfc.getFamilyId() || news != mfc.getSeniorId() || newj1 != mfc.getJunior1() || newj2 != mfc.getJunior2()) {
            makeMFC(newf, news, newj1, newj2);
        }
    }

    public int maxBattleshipHP(int skillid) {
        return (getTotalSkillLevel(skillid) * 5000) + ((getLevel() - 120) * 3000);
    }

    public int currentBattleshipHP() {
        return battleshipHP;
    }

    public void setBattleshipHP(int v) {
        this.battleshipHP = v;
    }

    public void decreaseBattleshipHP() {
        this.battleshipHP--;
    }

    public int getGachExp() {
        return gachexp;
    }

    public void setGachExp(int ge) {
        this.gachexp = ge;
    }

    public boolean isInBlockedMap() {
        if (!isAlive() || getPyramidSubway() != null || getMap().getSquadByMap() != null || getEventInstance() != null || getMap().getEMByMap() != null) {
            return true;
        }
        if ((getMapId() >= 680000210 && getMapId() <= 680000502) || (getMapId() / 10000 == 92502 && getMapId() >= 925020100) || (getMapId() / 10000 == 92503) || getMapId() == GameConstants.JAIL) {
            return true;
        }
        for (int i : GameConstants.blockedMaps) {
            if (getMapId() == i) {
                return true;
            }
        }
        if (getMapId() >= 689010000 && getMapId() < 689014000) { //Pink Zakum
            return true;
        }
        return false;
    }

    public boolean isInTownMap() {
        if (hasBlockedInventory() || !getMap().getSharedMapResources().town || FieldLimitType.VipRock.check(getMap()) || getEventInstance() != null) {
            return false;
        }
        for (int i : GameConstants.blockedMaps) {
            if (getMapId() == i) {
                return false;
            }
        }
        return true;
    }

    public boolean hasBlockedInventory() {
        return !isAlive() || getTrade() != null || getConversation() != MapleCharacterConversationType.None || getDirection() >= 0 || getPlayerShop() != null || map == null;
    }

    public void startPartySearch(final List<Integer> jobs, final int maxLevel, final int minLevel, final int membersNeeded) {
        for (User chr : map.getCharacters()) {
            if (chr.getId() != id && chr.getParty() == null && chr.getLevel() >= minLevel && chr.getLevel() <= maxLevel && (jobs.isEmpty() || jobs.contains(Integer.valueOf(chr.getJob()))) && (isGM() || !chr.isGM())) {
                if (party != null && party.getMembers().size() < 6 && party.getMembers().size() < membersNeeded) {
                    chr.setParty(party);
                    World.Party.updateParty(party.getId(), PartyOperation.JOIN, new MaplePartyCharacter(chr));
                    chr.receivePartyMemberHP();
                    chr.updatePartyMemberHP();
                } else {
                    break;
                }
            }
        }
    }

    public int getChallenge() {
        return challenge;
    }

    public void setChallenge(int c) {
        this.challenge = c;
    }

    public short getFatigue() {
        return fatigue;
    }

    public void setFatigue(int j) {
        this.fatigue = (short) Math.max(0, j);
        updateSingleStat(MapleStat.FATIGUE, this.fatigue);
    }

    public void reloadCharacter() {
        final int currentChannel = client.getChannel();
        forceChangeChannel(currentChannel);
    }

    public void fakeRelog() {
        final int chan = client.getChannel();
        final MapleMap mapp = getMap();
        mapp.setCheckStates(false);
        saveToDB(false, false);
        client.SendPacket(CWvsContext.getFamiliarInfo(this));
        client.SendPacket(CField.getCharInfo(this));
        mapp.removePlayer(this);
        mapp.addPlayer(this);
        forceChangeChannel(chan);
    }

    public void fakeRelog2() {
        final int chan = client.getChannel();
        final MapleMap mapp = getMap();
        mapp.setCheckStates(false);
        saveToDB(false, false);
        mapp.removePlayer(this);
        mapp.addPlayer(this);
        forceChangeChannel(chan);
    }

    public boolean canSummon() {
        return canSummon(5000);
    }

    public boolean canSummon(int g) {
        if (lastSummonTime + g < System.currentTimeMillis()) {
            lastSummonTime = System.currentTimeMillis();
            return true;
        }
        return false;
    }

    public int getIntNoRecord(int questID) {
        final MapleQuestStatus stat = getQuestNoAdd(MapleQuest.getInstance(questID));
        if (stat == null || stat.getCustomData() == null) {
            return 0;
        }
        return Integer.parseInt(stat.getCustomData());
    }

    public long getLongNoRecord(int questID) {
        final MapleQuestStatus stat = getQuestNoAdd(MapleQuest.getInstance(questID));
        if (stat == null || stat.getCustomData() == null) {
            return 0;
        }
        return Long.parseLong(stat.getCustomData());
    }

    public int getIntRecord(int questID) {
        final MapleQuestStatus stat = getQuestNAdd(MapleQuest.getInstance(questID));
        if (stat.getCustomData() == null) {
            stat.setCustomData("0");
        }
        return Integer.parseInt(stat.getCustomData());
    }

    public long getLongRecord(int questID) {
        final MapleQuestStatus stat = getQuestNAdd(MapleQuest.getInstance(questID));
        if (stat.getCustomData() == null) {
            stat.setCustomData("0");
        }
        return Long.parseLong(stat.getCustomData());
    }

    public void updatePetAuto() {
        if (getIntNoRecord(GameConstants.HP_ITEM) > 0) {
            client.SendPacket(CField.petAutoHP(getIntRecord(GameConstants.HP_ITEM)));
        }
        if (getIntNoRecord(GameConstants.MP_ITEM) > 0) {
            client.SendPacket(CField.petAutoMP(getIntRecord(GameConstants.MP_ITEM)));
        }
        if (getIntNoRecord(GameConstants.BUFF_ITEM) > 0) {
            //client.SendPacket(CField.petAutoBuff(getIntRecord(GameConstants.BUFF_ITEM)));
        }
    }

    public void sendEnglishQuiz(String msg) {
        client.SendPacket(NPCPacket.getEnglishQuiz(9010000, (byte) 0, 9010000, msg, "00 00"));
    }

    public void setChangeTime() {
        mapChangeTime = System.currentTimeMillis();
    }

    public long getChangeTime() {
        return mapChangeTime;
    }

    public Map<ReportType, Integer> getReports() {
        return reports;
    }

    public void addReport(ReportType type) {
        Integer value = reports.get(type);
        reports.put(type, value == null ? 1 : (value + 1));
        changed_reports = true;
    }

    public void clearReports(ReportType type) {
        reports.remove(type);
        changed_reports = true;
    }

    public void clearReports() {
        reports.clear();
        changed_reports = true;
    }

    public final int getReportPoints() {
        int ret = 0;
        for (Integer entry : reports.values()) {
            ret += entry;
        }
        return ret;
    }

    public final String getReportSummary() {
        final StringBuilder ret = new StringBuilder();
        final List<Pair<ReportType, Integer>> offenseList = new ArrayList<>();
        for (final Entry<ReportType, Integer> entry : reports.entrySet()) {
            offenseList.add(new Pair<>(entry.getKey(), entry.getValue()));
        }
        Collections.sort(offenseList, new Comparator<Pair<ReportType, Integer>>() {
            @Override
            public final int compare(final Pair<ReportType, Integer> o1, final Pair<ReportType, Integer> o2) {
                final int thisVal = o1.getRight();
                final int anotherVal = o2.getRight();
                return (thisVal < anotherVal ? 1 : (thisVal == anotherVal ? 0 : -1));
            }
        });
        for (int x = 0; x < offenseList.size(); x++) {
            ret.append(StringUtil.makeEnumHumanReadable(offenseList.get(x).left.name()));
            ret.append(": ");
            ret.append(offenseList.get(x).right);
            ret.append(" ");
        }
        return ret.toString();
    }

    public short getScrolledPosition() {
        return scrolledPosition;
    }

    public void setScrolledPosition(short s) {
        this.scrolledPosition = s;
    }

    public MapleTrait getTrait(MapleTraitType t) {
        return traits.get(t);
    }

    public void forceCompleteQuest(int id) {
        MapleQuest.getInstance(id).forceComplete(this, 9270035); //troll
    }

    public void fCompleteQuest(int nQuestId) {
        MapleQuest.getInstance(nQuestId).forceComplete(this, 0);
    }

    public List<Integer> getExtendedSlots() {
        return extendedSlots;
    }

    public int getExtendedSlot(int index) {
        if (extendedSlots.size() <= index || index < 0) {
            return -1;
        }
        return extendedSlots.get(index);
    }

    public void changedExtended() {
        changed_extendedSlots = true;
    }

    public MapleAndroid getAndroid() {
        return android;
    }

    public void removeAndroid() {
        if (map != null) {
            map.broadcastMessage(CField.deactivateAndroid(this.id));
        }
        android = null;
    }

    public void setAndroid(MapleAndroid a) {
        this.android = a;
        if ((this.map != null) && (a != null)) {
            MapleFoothold foot = getMap().getSharedMapResources().footholds.findBelow(getPosition());
            if (foot == null) {
                a.setFh(0);
            } else {
                a.setFh(foot.getId());
            }
            this.map.broadcastMessage(CField.spawnAndroid(this, a));
            this.map.broadcastMessage(CField.showAndroidEmotion(getId(), (byte) (Randomizer.nextInt(17) + 1)));
        } else if (map != null && a == null) { //Remove
            map.broadcastMessage(this, CField.deactivateAndroid(this.getId()), true);
        }
    }

    public Map<Integer, MonsterFamiliar> getFamiliars() {
        return familiars;
    }

    public MonsterFamiliar getSummonedFamiliar() {
        return summonedFamiliar;
    }

    public void removeFamiliar() {
        if (summonedFamiliar != null && map != null) {
            removeVisibleFamiliar();
        }
        summonedFamiliar = null;
    }

    public void removeVisibleFamiliar() {
        getMap().removeMapObject(summonedFamiliar);
        removeVisibleMapObject(summonedFamiliar);
        getMap().broadcastMessage(CField.removeFamiliar(this.getId()));
        anticheat.resetFamiliarAttack();
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        cancelEffect(ii.getItemEffect(ii.getFamiliar(summonedFamiliar.getFamiliar()).getPassive()), false, System.currentTimeMillis());
    }

    public void spawnFamiliar(MonsterFamiliar mf, boolean respawn) {
        summonedFamiliar = mf;

        mf.setStance(0);
        Point p = getPosition();
        p.y = p.y - 30;
        mf.setPosition(p);
        mf.setFh(getFh());
        addVisibleMapObject(mf);
        getMap().spawnFamiliar(mf, respawn);

        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        final MapleStatEffect eff = ii.getItemEffect(ii.getFamiliar(summonedFamiliar.getFamiliar()).getPassive());
        if (eff != null && eff.getInterval() <= 0 && eff.makeChanceResult()) { //i think this is actually done through a recv, which is ATTACK_FAMILIAR +1
            eff.applyTo(this);
        }
        lastFamiliarEffectTime = System.currentTimeMillis();
    }

    public final boolean canFamiliarEffect(long now, MapleStatEffect eff) {
        return lastFamiliarEffectTime > 0 && lastFamiliarEffectTime + eff.getInterval() < now;
    }

    public void doFamiliarSchedule(long now) {
        if (familiars == null) {
            return;
        }
        for (MonsterFamiliar mf : familiars.values()) {
            if (summonedFamiliar != null && summonedFamiliar.getId() == mf.getId()) {
                mf.addFatigue(this, 5);
                final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
                final MapleStatEffect eff = ii.getItemEffect(ii.getFamiliar(summonedFamiliar.getFamiliar()).getPassive());
                if (eff != null && eff.getInterval() > 0 && canFamiliarEffect(now, eff) && eff.makeChanceResult()) {
                    eff.applyTo(this);
                }
            } else if (mf.getFatigue() > 0) {
                mf.setFatigue(Math.max(0, mf.getFatigue() - 5));
            }
        }
    }

    public MapleImp[] getImps() {
        return imps;
    }

    public void sendImp() {
        for (int i = 0; i < imps.length; i++) {
            if (imps[i] != null) {
                client.SendPacket(CWvsContext.updateImp(imps[i], ImpFlag.SUMMONED.getValue(), i, true));
            }
        }
    }

    public int getBattlePoints() {
        return pvpPoints;
    }

    public int getTotalBattleExp() {
        return pvpExp;
    }

    public void setBattlePoints(int p) {
        if (p != pvpPoints) {
            PvpPointMessage pvp = new PvpPointMessage(0, p - pvpPoints);
            write(CWvsContext.messagePacket(pvp));
            updateSingleStat(MapleStat.BATTLE_POINTS, p);
        }
        this.pvpPoints = p;
    }

    public void setTotalBattleExp(int p) {
        final int previous = pvpExp;
        this.pvpExp = p;
        if (p != previous) {
            stats.recalcPVPRank(this);

            updateSingleStat(MapleStat.BATTLE_EXP, stats.pvpExp);
            updateSingleStat(MapleStat.BATTLE_RANK, stats.pvpRank);
        }
    }

    public void changeTeam(int newTeam) {
        this.coconutteam = newTeam;

        if (inPVP()) {
            //client.write(CField.getPVPTransform(newTeam + 1));
            map.broadcastMessage(CField.changeTeam(id, newTeam + 1));
        } else {
            client.SendPacket(CField.showEquipEffect(newTeam));
        }
    }

    public void disease(int type, int level) {
        if (MapleDisease.getBySkill(type) == null) {
            return;
        }
        chair = 0;
        client.SendPacket(CField.cancelChair(id, -1));
        map.broadcastMessage(this, CField.showChair(id, 0), false);
        giveDebuff(MapleDisease.getBySkill(type), MobSkillFactory.getMobSkill(type, level));
    }

    public boolean inPVP() {
        return eventInstance != null && eventInstance.getName().startsWith("PVP");
    }

    public boolean inAzwan() {
        return mapid >= 262020000 && mapid < 262023000;
    }

    public Pair<Double, Boolean> modifyDamageTaken(double damage, MapleMapObject attacke) {
        if (hasGodMode()) {
            damage = 0; // Godmode
        }

        Pair<Double, Boolean> ret = new Pair<>(damage, false);
        if (damage <= 0) {
            return ret;
        }

        if (stats.ignoreTakenDAMr > 0 && Randomizer.nextInt(100) < stats.ignoreTakenDAMr_rate) {
            damage -= Math.floor((stats.ignoreTakenDAMr * damage) / 100.0f);
        }
        if (stats.ignoreTakenDAM > 0 && Randomizer.nextInt(100) < stats.ignoreTakenDAM_rate) {
            damage -= stats.ignoreTakenDAM;
        }
        final Integer div = getBuffedValue(CharacterTemporaryStat.BlessingArmor);
        final Integer div2 = getBuffedValue(CharacterTemporaryStat.HolyMagicShell);
        if (div2 != null) {
            if (div2 <= 0) {
                cancelEffectFromTemporaryStat(CharacterTemporaryStat.HolyMagicShell);
            } else {
                setBuffedValue(CharacterTemporaryStat.HolyMagicShell, div2 - 1);
                damage = 0;
            }
        } else if (div != null) {
            if (div <= 0) {
                cancelEffectFromTemporaryStat(CharacterTemporaryStat.BlessingArmor);
            } else {
                setBuffedValue(CharacterTemporaryStat.BlessingArmor, div - 1);
                damage = 0;
            }
        }
        MapleStatEffect barrier = getStatForBuff(CharacterTemporaryStat.ComboBarrier);
        if (barrier != null) {
            damage = ((barrier.getX() / 1000.0) * damage);
        }
        barrier = getStatForBuff(CharacterTemporaryStat.MagicShield);
        if (barrier != null) {
            damage = ((barrier.getX() / 1000.0) * damage);
        }
        barrier = getStatForBuff(CharacterTemporaryStat.DamAbsorbShield);
        if (barrier != null) {
            damage = ((barrier.getX() / 1000.0) * damage);
        }
        List<Integer> attack = attacke instanceof Mob || attacke == null ? null : (new ArrayList<>());
        if (damage > 0) {
            if (getJob() == 122 && !skillisCooling(1220013)) {
                final Skill divine = SkillFactory.getSkill(1220013);
                if (getTotalSkillLevel(divine) > 0) {
                    final MapleStatEffect divineShield = divine.getEffect(getTotalSkillLevel(divine));
                    if (divineShield.makeChanceResult()) {
                        divineShield.applyTo(this);

                        addCooldown(1220013, System.currentTimeMillis(), divineShield.getCooldown(this));
                    }
                }
                /*
            } else if (getBuffedValue(CharacterTemporaryStat.SATELLITESAFE_PROC) != null && getBuffedValue(CharacterTemporaryStat.SATELLITESAFE_ABSORB) != null && getBuffedValue(CharacterTemporaryStat.PUPPET) != null) {
                double buff = getBuffedValue(CharacterTemporaryStat.SATELLITESAFE_PROC).doubleValue();
                double buffz = getBuffedValue(CharacterTemporaryStat.SATELLITESAFE_ABSORB).doubleValue();
                if ((int) ((buff / 100.0) * getStat().getMaxHp()) <= damage) {
                    damage -= ((buffz / 100.0) * damage);
                    cancelEffectFromTemporaryStat(CharacterTemporaryStat.PUPPET);
                }
                 */
            } else if (getJob() == 433 || getJob() == 434) {
                final Skill divine = SkillFactory.getSkill(4330001);
                if (getTotalSkillLevel(divine) > 0 && getBuffedValue(CharacterTemporaryStat.DarkSight) == null && !skillisCooling(divine.getId())) {
                    final MapleStatEffect divineShield = divine.getEffect(getTotalSkillLevel(divine));
                    if (Randomizer.nextInt(100) < divineShield.getX()) {
                        divineShield.applyTo(this);
                    }
                }
            } else if ((getJob() == 512 || getJob() == 522) && getBuffedValue(CharacterTemporaryStat.DamR) == null) {
                final Skill divine = SkillFactory.getSkill(getJob() == 512 ? 5120011 : 5220012);
                if (getTotalSkillLevel(divine) > 0 && !skillisCooling(divine.getId())) {
                    final MapleStatEffect divineShield = divine.getEffect(getTotalSkillLevel(divine));
                    if (divineShield.makeChanceResult()) {
                        divineShield.applyTo(this);

                        addCooldown(divine.getId(), System.currentTimeMillis(), divineShield.getCooldown(this));
                    }
                }
            } else if (getJob() == 312 && attacke != null) {
                final Skill divine = SkillFactory.getSkill(3120010);
                if (getTotalSkillLevel(divine) > 0) {
                    final MapleStatEffect divineShield = divine.getEffect(getTotalSkillLevel(divine));
                    if (divineShield.makeChanceResult()) {
                        if (attacke instanceof Mob) {
                            final Rectangle bounds = divineShield.calculateBoundingBox(getTruePosition(), isFacingLeft());
                            final List<MapleMapObject> affected = getMap().getMapObjectsInRect(bounds, Arrays.asList(attacke.getType()));
                            int i = 0;

                            for (final MapleMapObject mo : affected) {
                                Mob mons = (Mob) mo;
                                if (mons.getStats().isFriendly() || mons.isFake()) {
                                    continue;
                                }
                                mons.applyStatus(this, new MonsterStatusEffect(MonsterStatus.STUN, 1, divineShield.getSourceId(), null, false), false, divineShield.getDuration(), true, divineShield);
                                final int theDmg = (int) (divineShield.getDamage() * getStat().getCurrentMaxBaseDamage() / 100.0);
                                mons.damage(this, theDmg, true);
                                getMap().broadcastMessage(MobPacket.damageMonster(mons.getObjectId(), theDmg));
                                i++;
                                if (i >= divineShield.getMobCount()) {
                                    break;
                                }
                            }
                        } else {
                            User chr = (User) attacke;
                            chr.addHP(-divineShield.getDamage());
                            attack.add((int) divineShield.getDamage());
                        }
                    }
                }
            } else if ((getJob() == 531 || getJob() == 532) && attacke != null) {
                final Skill divine = SkillFactory.getSkill(5310009); //iPacket.DecodeInt() = 5310009, then iPacket.DecodeInt() = damage. (175000)
                if (getTotalSkillLevel(divine) > 0) {
                    final MapleStatEffect divineShield = divine.getEffect(getTotalSkillLevel(divine));
                    if (divineShield.makeChanceResult()) {
                        if (attacke instanceof Mob) {
                            final Mob attacker = (Mob) attacke;
                            final int theDmg = (int) (divineShield.getDamage() * getStat().getCurrentMaxBaseDamage() / 100.0);
                            attacker.damage(this, theDmg, true);
                            getMap().broadcastMessage(MobPacket.damageMonster(attacker.getObjectId(), theDmg));
                        } else {
                            final User attacker = (User) attacke;
                            attacker.addHP(-divineShield.getDamage());
                            attack.add((int) divineShield.getDamage());
                        }
                    }
                }
            } else if (getJob() == 132 && attacke != null) {
                final Skill divine = SkillFactory.getSkill(1421013);
                if (getTotalSkillLevel(divine) > 0 && !skillisCooling(divine.getId()) && getBuffSource(CharacterTemporaryStat.Beholder) == 1421015) {
                    World.Broadcast.broadcastMessage(CField.getGameMessage("Sacrifice.", (short) 7));
                    final MapleStatEffect divineShield = divine.getEffect(getTotalSkillLevel(divine));
                    if (divineShield.makeChanceResult()) {
                        addCooldown(divine.getId(), System.currentTimeMillis(), divineShield.getCooldown(this) * 0);

                        if (attacke instanceof Mob) {
                            final Mob attacker = (Mob) attacke;
                            final int theDmg = (int) (divineShield.getDamage() * getStat().getCurrentMaxBaseDamage() / 100.0);
                            attacker.damage(this, theDmg, true);
                            getMap().broadcastMessage(MobPacket.damageMonster(attacker.getObjectId(), theDmg));
                        } else {
                            final User attacker = (User) attacke;
                            attacker.addHP(-divineShield.getDamage());
                            attack.add((int) divineShield.getDamage());
                        }
                    }
                }
            }
            if (attacke != null) {
                final int damr = (Randomizer.nextInt(100) < getStat().DAMreflect_rate ? getStat().DAMreflect : 0) + (getBuffedValue(CharacterTemporaryStat.PowerGuard) != null ? getBuffedValue(CharacterTemporaryStat.PowerGuard) : 0);
                final int bouncedam_ = damr + (getBuffedValue(CharacterTemporaryStat.Guard) != null ? getBuffedValue(CharacterTemporaryStat.Guard) : 0);
                if (bouncedam_ > 0) {
                    long bouncedamage = (long) (damage * bouncedam_ / 100);
                    long bouncer = (long) (damage * damr / 100);
                    damage -= bouncer;
                    if (attacke instanceof Mob) {
                        final Mob attacker = (Mob) attacke;
                        bouncedamage = Math.min(bouncedamage, attacker.getMobMaxHp() / 10);
                        attacker.damage(this, bouncedamage, true);
                        getMap().broadcastMessage(this, MobPacket.damageMonster(attacker.getObjectId(), bouncedamage), getTruePosition());
                        if (getBuffSource(CharacterTemporaryStat.Guard) == 31101003) {
                            MapleStatEffect eff = this.getStatForBuff(CharacterTemporaryStat.Guard);
                            if (eff.makeChanceResult()) {
                                attacker.applyStatus(this, new MonsterStatusEffect(MonsterStatus.STUN, 1, eff.getSourceId(), null, false), false, eff.getSubTime(), true, eff);
                            }
                        }
                    } else {
                        final User attacker = (User) attacke;
                        bouncedamage = Math.min(bouncedamage, attacker.getStat().getCurrentMaxHp() / 10);
                        attacker.addHP(-((int) bouncedamage));
                        attack.add((int) bouncedamage);
                        if (getBuffSource(CharacterTemporaryStat.Guard) == 31101003) {
                            MapleStatEffect eff = this.getStatForBuff(CharacterTemporaryStat.Guard);
                            if (eff.makeChanceResult()) {
                                attacker.disease(MapleDisease.STUN.getDisease(), 1);
                            }
                        }
                    }
                    ret.right = true;
                }
                if ((getJob() == 411 || getJob() == 412 || getJob() == 421 || getJob() == 422) && getBuffedValue(CharacterTemporaryStat.SUMMON) != null && attacke != null) {
                    final List<Summon> ss = getSummonsReadLock();
                    try {
                        for (Summon sum : ss) {
                            if (sum.getTruePosition().distanceSq(getTruePosition()) < 400000.0 && (sum.getSkill() == 4111007 || sum.getSkill() == 4211007 || sum.getSkill() == 14111010)) {
                                final List<Pair<Integer, Long>> allDamage = new ArrayList<>();
                                if (attacke instanceof Mob) {
                                    final Mob attacker = (Mob) attacke;
                                    final long theDmg = (long) (SkillFactory.getSkill(sum.getSkill()).getEffect(sum.getSkillLevel()).getX() * damage / 100.0);
                                    allDamage.add(new Pair<>(attacker.getObjectId(), theDmg));
                                    getMap().broadcastMessage(SummonPacket.summonAttack(sum.getOwnerId(), sum.getObjectId(), (byte) 0x84, allDamage, getLevel(), true));
                                    attacker.damage(this, theDmg, true);
                                    checkMonsterAggro(attacker);
                                    if (!attacker.isAlive()) {
                                        getClient().SendPacket(MobPacket.killMonster(attacker.getObjectId(), 1, false));
                                    }
                                } else {
                                    final User chr = (User) attacke;
                                    final int dmg = SkillFactory.getSkill(sum.getSkill()).getEffect(sum.getSkillLevel()).getX();
                                    chr.addHP(-dmg);
                                    attack.add(dmg);
                                }
                            }
                        }
                    } finally {
                        unlockSummonsReadLock();
                    }
                }
            }
        }
        if (attack != null && attack.size() > 0 && attacke != null) {
            getMap().broadcastMessage(CField.pvpCool(attacke.getObjectId(), attack));
        }
        ret.left = damage;
        return ret;
    }

    public void onAttack(long maxhp, long maxmp, int skillid, int oid, int totDamage, int critCount) {
        if (stats.hpRecoverProp > 0) {
            if (Randomizer.nextInt(100) <= stats.hpRecoverProp) {//i think its out of 100, anyway
                if (stats.hpRecover > 0) {
                    healHP(stats.hpRecover);
                }
                if (stats.hpRecoverPercent > 0) {
                    addHP(((int) Math.min(maxhp, Math.min(((int) ((double) totDamage * (double) stats.hpRecoverPercent / 100.0)), stats.getMaxHp() / 2))));
                }
            }
        }
        if (stats.mpRecoverProp > 0 && !GameConstants.isDemonSlayer(getJob())) {
            if (Randomizer.nextInt(100) <= stats.mpRecoverProp) {//i think its out of 100, anyway
                healMP(stats.mpRecover);
            }
        }
        if (getBuffedValue(CharacterTemporaryStat.ComboDrain) != null) {
            addHP(((int) Math.min(maxhp, Math.min(((int) ((double) totDamage * (double) getStatForBuff(CharacterTemporaryStat.ComboDrain).getX() / 100.0)), stats.getMaxHp() / 2))));
        }
        if (getBuffSource(CharacterTemporaryStat.ComboDrain) == 23101003) {
            addMP(((int) Math.min(maxmp, Math.min(((int) ((double) totDamage * (double) getStatForBuff(CharacterTemporaryStat.ComboDrain).getX() / 100.0)), stats.getMaxMp() / 2))));
        }
        if (getBuffedValue(CharacterTemporaryStat.REAPER) != null && getBuffedValue(CharacterTemporaryStat.SUMMON) == null && getSummonsSize() < 4 && canSummon()) {
            final MapleStatEffect eff = getStatForBuff(CharacterTemporaryStat.REAPER);
            if (eff.makeChanceResult()) {
                eff.applyTo(this, this, false, null, eff.getDuration());
            }
        }
        if (getJob() == 212 || getJob() == 222 || getJob() == 232) {
            int[] venomskills = {2120010, 2220010, 2320011};
            for (int i : venomskills) {
                final Skill skill = SkillFactory.getSkill(i);
                if (getTotalSkillLevel(skill) > 0) {
                    final MapleStatEffect venomEffect = skill.getEffect(getTotalSkillLevel(skill));
                    if (venomEffect.makeChanceResult() && getAllLinkMid().size() < venomEffect.getY()) {
                        setLinkMid(oid, venomEffect.getX());
                        venomEffect.applyTo(this);
                    }
                    break;
                }
            }
        }
        int[] venomskills = {4110011, 4120005, 4210010, 4220005, 4320005, 4340001, 14110004};
        for (int i : venomskills) {
            if (i == 4110011) {
                if (getTotalSkillLevel(4120011) > 0) {
                    i = 4120011;
                }
            } else if (i == 4210010) {
                if (getTotalSkillLevel(4220011) > 0) {
                    i = 4220011;
                }
            }
            final Skill skill = SkillFactory.getSkill(i);
            if (getTotalSkillLevel(skill) > 0) {
                final MapleStatEffect venomEffect = skill.getEffect(getTotalSkillLevel(skill));
                final Mob monster = map.getMonsterByOid(oid);
                if (venomEffect.makeChanceResult() && monster != null) {
                    monster.applyStatus(this, new MonsterStatusEffect(MonsterStatus.POISON, 1, i, null, false), true, venomEffect.getDuration(), true, venomEffect);
                }
                break;
            }
        }
        if (getJob() == 2410 || getJob() == 2411 || getJob() == 2412) {
            final Skill skil = SkillFactory.getSkill(getJob() == 2412 ? 24120002 : 24100003);
            if (getTotalSkillLevel(skil) > 0 && critCount > 0 && skillid != 24120002 && skillid != 24100003) {
                final MapleStatEffect eff = skil.getEffect(getTotalSkillLevel(skil));
                if (eff.makeChanceResult()) {
                    setBattleshipHP(Math.min(getJob() == 2412 ? 40 : 20, currentBattleshipHP() + 1));
                    attackCarte(eff, oid, 1);
                }
            }
        }

        /*if (GameConstants.isKinesis(getJob())) {
            KinesisHandler.handlePsychicPoint(this, skillid);
            //gainPsychicPoint((short) -1);
            //dropMessage(-1, "Psychic Points left: " + getPsychicPoint());
        }*/
        // effects
        if (skillid > 0) {
            final Skill skil = SkillFactory.getSkill(skillid);
            if (skil != null) {
                final MapleStatEffect effect = skil.getEffect(getTotalSkillLevel(skil));
                switch (skillid) {
                    case 15111001:
                    case 3111008:
                    case 1078:
                    case 31111003:
                    case 11078:
                    case 14101006:
                    case 33111006: //swipe
                    case 4101005: //drain
                    case 5111004: { // Energy Drain
                        addHP(((int) Math.min(maxhp, Math.min(((int) ((double) totDamage * (double) effect.getX() / 100.0)), stats.getMaxHp() / 2))));
                        break;
                    }
                    case 5211006:
                    case 22151002: //killer wing
                    case 5220011: {//homing
                        setLinkMid(oid, effect.getX());
                        break;
                    }
                    case 33101007: { //jaguar
                        clearLinkMid();
                        break;
                    }
                }
            }
        }
    }

    public void attackCarte(final MapleStatEffect eff, final int oid, final int x) {
        if (x > 0) {
            lastBerserkTime += x; //lol unused variable.
            map.broadcastMessage(PhantomPacket.getCarteAnimation(id, oid, job, (int) lastBerserkTime, x));
            client.SendPacket(PhantomPacket.updateCardStack(currentBattleshipHP()));
        }
    }

    public void handleForceGain(int oid, int skillid) {
        handleForceGain(oid, skillid, 0);
    }

    public void handleForceGain(int oid, int skillid, int extraForce) {
        if (!GameConstants.isForceIncrease(skillid) && extraForce <= 0) {
            return;
        }
        int forceGain = 1;
        if (getLevel() >= 30 && getLevel() < 70) {
            forceGain = 2;
        } else if (getLevel() >= 70 && getLevel() < 120) {
            forceGain = 3;
        } else if (getLevel() >= 120) {
            forceGain = 4;
        }
        this.force = (short) (this.force + 1);
        if (GameConstants.isDemonSlayer(getJob())) {
            addMP(extraForce > 0 ? extraForce : forceGain, true);
        }
        getClient().SendPacket(CField.gainForce(oid, this.force, forceGain));
        if (GameConstants.isDemonSlayer(getJob())
                && this.stats.mpRecoverProp > 0 && extraForce <= 0
                && Randomizer.nextInt(100) <= this.stats.mpRecoverProp) {
            this.force = (short) (this.force + 1);
            addMP(this.stats.mpRecover, true);
            getClient().SendPacket(CField.gainForce(oid, this.force, this.stats.mpRecover));
        }
    }

    public void afterAttack(int mobCount, int attackCount, int skillid) {
        switch (getJob()) {
            case 510:
            case 511:
            case 512: {
                handleEnergyCharged(mobCount * attackCount, false);
                break;
            }
            case 1510:
            case 1511:
            case 1512: {
                handleEnergyCharged(mobCount * attackCount, true);
                break;
            }
            case 592:
                if (skillid == 4221001) {
                    setBattleshipHP(0);
                } else {
                    setBattleshipHP(Math.min(5, currentBattleshipHP() + 1)); //max 5
                }
                refreshBattleshipHP();
                break;
            case 111:
            case 112:
            case 1111:
            case 1112:
            case 2411:
            case 2412:
                if (skillid != 1111008 & getBuffedValue(CharacterTemporaryStat.ComboCounter) != null) { // shout should not give orbs
                    handleOrbgain();
                }
                break;
        }

        /*
        if (getBuffedValue(CharacterTemporaryStat.OWL_SPIRIT) != null) {
            if (currentBattleshipHP() > 0) {
                decreaseBattleshipHP();
            }
            if (currentBattleshipHP() <= 0) {
                cancelEffectFromTemporaryStat(CharacterTemporaryStat.OWL_SPIRIT);
            }
        }
         */
        if (!isIntern()) {
            //cancelEffectFromTemporaryStat(CharacterTemporaryStat.WIND_WALK);
            //cancelEffectFromTemporaryStat(CharacterTemporaryStat.Speed);
            final MapleStatEffect ds = getStatForBuff(CharacterTemporaryStat.DarkSight);
            if (ds != null) {
                if (ds.getSourceId() != 4330001 || !ds.makeChanceResult()) {
                    cancelEffectFromTemporaryStat(CharacterTemporaryStat.DarkSight);
                }
            }
        }
    }

    public void applyIceGage(int x) {
        updateSingleStat(MapleStat.ICE_GAUGE, x);
    }

    public Rectangle getBounds() {
        return new Rectangle(getTruePosition().x - 25, getTruePosition().y - 75, 50, 75);
    }

    @Override
    public Map<Short, Integer> getEquips(boolean fusionAnvil) {
        final Map<Short, Integer> eq = new HashMap<>();
        for (final Item item : inventory[MapleInventoryType.EQUIPPED.ordinal()].newList()) {
            int itemId = item.getItemId();
            if (item instanceof Equip && fusionAnvil) {
                if (((Equip) item).getFusionAnvil() != 0) {
                    itemId = ((Equip) item).getFusionAnvil();
                }
            }
            eq.put((short) item.getPosition(), itemId);
        }
        return eq;
    }

    @Override
    public Map<Short, Integer> getSecondaryEquips(boolean fusionAnvil) {
        final Map<Short, Integer> eq = new HashMap<>();
        for (final Item item : inventory[MapleInventoryType.EQUIPPED.ordinal()].newList()) {
            int itemId = item.getItemId();
            if (item instanceof Equip) {
                if (fusionAnvil) {
                    if (((Equip) item).getFusionAnvil() != 0) {
                        itemId = ((Equip) item).getFusionAnvil();
                    }
                }
                if (GameConstants.isAngelicBuster(getJob()) && InventoryConstants.isOverall(itemId)) {
                    itemId = 1051291; //ab def overall
                }
            }
            if (GameConstants.isAngelicBuster(getJob())) {
                if (!InventoryConstants.isOverall(itemId) && !InventoryConstants.isSecondaryWeapon(itemId)
                        && !InventoryConstants.isWeapon(itemId) && !InventoryConstants.isMedal(itemId)) {
                    continue;
                }
            }
            eq.put((short) item.getPosition(), itemId);
        }
        return eq;
    }

    @Override
    public Map<Short, Integer> getTotems() {
        final Map<Short, Integer> eq = new HashMap<>();
        for (final Item item : inventory[MapleInventoryType.EQUIPPED.ordinal()].newList()) {
            eq.put((short) (item.getPosition() + 5000), item.getItemId());
        }
        return eq;
    }
    private transient PlayerRandomStream CRand;

    public final PlayerRandomStream CRand() {
        return CRand;
    }

    public void handleCardStack() {
        Skill noir = SkillFactory.getSkill(24120002);
        Skill blanc = SkillFactory.getSkill(24100003);
        MapleStatEffect ceffect = null;
        int advSkillLevel = getTotalSkillLevel(noir);
        boolean isAdv = false;
        if (advSkillLevel > 0) {
            ceffect = noir.getEffect(advSkillLevel);
            isAdv = true;
        } else if (getSkillLevel(blanc) > 0) {
            ceffect = blanc.getEffect(getTotalSkillLevel(blanc));
        } else {
            return;
        }
        if (getJob() == 2412 && getCardStack() == 40) {
            this.runningStack = 0;
            this.cardStack = (byte) (40);
        }
        if (getJob() == 2400 && getCardStack() == 20 || getJob() == 2410 && getCardStack() == 20 || getJob() == 2411 && getCardStack() == 20) {
            this.runningStack = 0;
            this.cardStack = (byte) (20);
        }
        if (ceffect.makeChanceResult()) {
            if (this.cardStack < (getJob() == 2412 ? 40 : 20)) {
                this.cardStack = (byte) (this.cardStack + 1);
            }
            this.runningStack += 1;
            this.client.SendPacket(PhantomPacket.gainCardStack(getId(), this.runningStack, isAdv ? 2 : 1, ceffect.getSourceId(), Randomizer.rand(100000, 500000), 1));
            this.client.SendPacket(PhantomPacket.updateCardStack(this.cardStack));
        }
    }

    /*
     public void resetRunningStack() {
     this.runningStack = 0;
     }

     public int getRunningStack() {
     return this.runningStack;
     }

     public void addRunningStack(int s) {
     this.runningStack += s;
     }

     public void setCardStack(byte amount) {
     this.cardStack = amount;
     }

     public byte getCardStack() {
     return cardStack;
     } // running id plox
     */
    public final MapleCharacterCards getCharacterCard() {
        return characterCard;
    }

    /*
     * Start of Custom Feature
     */
    public int getReborns() {
        return reborns;
    }

    public void setReborns(int amount) {
        reborns = amount;
    }

    public int getAPS() {
        return apstorage;
    }

    public void gainAPS(int aps) {
        apstorage += aps;
    }

    public final boolean canHold(final int itemid) {
        return getInventory(GameConstants.getInventoryType(itemid)).getNextFreeSlot() > -1;
    }

    public int getStr() {
        return str;
    }

    public void setStr(int str) {
        this.str = str;
        stats.recalcLocalStats(this);
    }

    public int getInt() {
        return int_;
    }

    public void setInt(int int_) {
        this.int_ = int_;
        stats.recalcLocalStats(this);
    }

    public int getLuk() {
        return luk;
    }

    public int getDex() {
        return dex;
    }

    public void setLuk(int luk) {
        this.luk = luk;
        stats.recalcLocalStats(this);
    }

    public void setDex(int dex) {
        this.dex = dex;
        stats.recalcLocalStats(this);
    }

    public void gainVPoints(int amount) {
        this.vpoints += amount;
    }

    public void gainEPoints(int amount) {
        this.epoints += amount;
    }

    public void gainDPoints(int amount) {
        this.dpoints += amount;
    }

    public static String makeMapleReadable(String in) {
        String i = in.replace('I', 'i');
        i = i.replace('l', 'L');
        i = i.replace("rn", "Rn");
        i = i.replace("vv", "Vv");
        i = i.replace("VV", "Vv");
        return i;
    }

    public void changeMap(int map, int portal) {
        MapleMap warpMap = client.getChannelServer().getMapFactory().getMap(map);
        changeMap(warpMap, warpMap.getPortal(portal));
    }

    public void unchooseStolenSkill(int skillID) { //base skill
        if (skillisCooling(20031208) || stolenSkills == null) {
            dropMessage(-6, "[Loadout] The skill is under cooldown. Please wait.");
            return;
        }
        final int stolenjob = GameConstants.getJobNumber(skillID / 10000);
        boolean changed = false;
        for (Pair<Integer, Boolean> sk : stolenSkills) {
            if (sk.right && GameConstants.getJobNumber(sk.left / 10000) == stolenjob) {
                cancelStolenSkill(sk.left);
                sk.right = false;
                changed = true;
            }
        }
        if (changed) {
            final Skill skil = SkillFactory.getSkill(skillID);
            changeSkillLevelSkip(skil, getSkillLevel(skil), (byte) 0);
            client.SendPacket(PhantomPacket.replaceStolenSkill(GameConstants.getStealSkill(stolenjob), 0));
        }
    }

    public void cancelStolenSkill(int skillID) {
        final Skill skk = SkillFactory.getSkill(skillID);
        final MapleStatEffect eff = skk.getEffect(getTotalSkillLevel(skk));

        if (eff.isMonsterBuff() || (eff.getStatups().isEmpty() && !eff.getMonsterStati().isEmpty())) {
            for (MapleMapObject o : map.getAllMapObjects(MapleMapObjectType.MONSTER)) {
                final Mob mons = (Mob) o;

                for (MonsterStatus b : eff.getMonsterStati().keySet()) {
                    if (mons.isBuffed(b) && mons.getBuff(b).getFromID() == this.id) {
                        mons.cancelStatus(b);
                    }
                }
            }
        } else if (eff.getDuration() > 0 && !eff.getStatups().isEmpty()) {
            for (User chr : map.getCharacters()) {
                chr.cancelEffect(eff, false, -1, eff.getStatups());

            }
        }
    }

    public void chooseStolenSkill(int skillID) {
        if (skillisCooling(20031208) || stolenSkills == null) {
            dropMessage(-6, "[Loadout] The skill is under cooldown. Please wait.");
            return;
        }
        final Pair<Integer, Boolean> dummy = new Pair<>(skillID, false);
        if (stolenSkills.contains(dummy)) {
            unchooseStolenSkill(skillID);
            stolenSkills.get(stolenSkills.indexOf(dummy)).right = true;

            client.SendPacket(PhantomPacket.replaceStolenSkill(GameConstants.getStealSkill(GameConstants.getJobNumber(skillID / 10000)), skillID));
            //if (ServerConstants.CUSTOM_SKILL) {
            //    client.write(MaplePacketCreator.skillCooldown(20031208, 5));
            //    addCooldown(20031208, System.currentTimeMillis(), 5000);
            //}
        }
    }

    public void addStolenSkill(int skillID, int skillLevel) {
        System.err.println(String.format("Adding Skill: [%d:%d]", skillID, skillLevel));
        if (skillisCooling(20031208) || stolenSkills == null) {
            dropMessage(-6, "[Loadout] The skill is under cooldown. Please wait.");
            return;
        }
        final Pair<Integer, Boolean> dummy = new Pair<>(skillID, true);
        final Skill skil = SkillFactory.getSkill(skillID);
        System.err.println(String.format("Stolen: %s, Stealable: %s", stolenSkills.contains(dummy) ? "True" : "False", GameConstants.canSteal(skil) ? "True" : "False"));
        if (!stolenSkills.contains(dummy) && GameConstants.canSteal(skil)) {
            dummy.right = false;
            skillLevel = Math.min(skil.getMaxLevel(), skillLevel);
            final int jobid = GameConstants.getJobNumber(skillID / 10000);
            System.err.println(String.format("idk wtf this even means: %s", getSkillLevel(GameConstants.getStealSkill(jobid)) > 0 ? "True" : "False"));
            if (getSkillLevel(GameConstants.getStealSkill(jobid)) > 0) {
                int count = 0;
                skillLevel = Math.min(getSkillLevel(GameConstants.getStealSkill(jobid)), skillLevel);
                for (Pair<Integer, Boolean> sk : stolenSkills) {
                    if (GameConstants.getJobNumber(sk.left / 10000) == jobid) {
                        count++;
                    }
                }
                System.err.println(String.format("Stolen Count: %d, Max Allow: %d", count, GameConstants.getNumSteal(jobid)));
                if (count < GameConstants.getNumSteal(jobid)) {
                    stolenSkills.add(dummy);
                    changed_skills = true;
                    changeSkillLevelSkip(skil, skillLevel, (byte) skillLevel);
                    client.SendPacket(PhantomPacket.addStolenSkill(jobid, count, skillID, skillLevel));
                    //client.write(MaplePacketCreator.updateStolenSkills(this, jobid));
                    System.err.println(String.format("Stolen %d [%s]", skillID, stolenSkills.contains(new Pair(skillID, false)) ? "True" : "False"));
                }
            }
        }
        for (Pair<Integer, Boolean> sk : stolenSkills) {
            System.err.println(String.format("%d [%s]", sk.left, sk.right ? "Equipped" : "Unequipped"));
        }
    }

    public void removeStolenSkill(int skillID) {
        if (skillisCooling(20031208) || stolenSkills == null) {
            dropMessage(-6, "[Loadout] The skill is under cooldown. Please wait.");
            return;
        }
        final int jobid = GameConstants.getJobNumber(skillID / 10000);
        final Pair<Integer, Boolean> dummy = new Pair<>(skillID, false);
        int count = -1, cc = 0;
        for (int i = 0; i < stolenSkills.size(); i++) {
            if (stolenSkills.get(i).left == skillID) {
                if (stolenSkills.get(i).right) {
                    unchooseStolenSkill(skillID);
                }
                count = cc;
                break;
            } else if (GameConstants.getJobNumber(stolenSkills.get(i).left / 10000) == jobid) {
                cc++;
            }
        }
        if (count >= 0) {
            cancelStolenSkill(skillID);
            stolenSkills.remove(dummy);
            dummy.right = true;
            stolenSkills.remove(dummy);
            changed_skills = true;
            changeSkillLevelSkip(SkillFactory.getSkill(skillID), 0, (byte) 0);
            //hacky process begins here
            client.SendPacket(PhantomPacket.replaceStolenSkill(GameConstants.getStealSkill(jobid), 0));
            for (int i = 0; i < GameConstants.getNumSteal(jobid); i++) {
                client.SendPacket(PhantomPacket.removeStolenSkill(jobid, i));
            }
            count = 0;
            for (Pair<Integer, Boolean> sk : stolenSkills) {
                if (GameConstants.getJobNumber(sk.left / 10000) == jobid) {
                    client.SendPacket(PhantomPacket.addStolenSkill(jobid, count, sk.left, getSkillLevel(sk.left)));
                    if (sk.right) {
                        client.SendPacket(PhantomPacket.replaceStolenSkill(GameConstants.getStealSkill(jobid), sk.left));
                    }
                    count++;
                }
            }
            client.SendPacket(PhantomPacket.removeStolenSkill(jobid, count));
            //client.write(MaplePacketCreator.updateStolenSkills(this, jobid));
        }
    }

    public List<Pair<Integer, Boolean>> getStolenSkills() {
        return stolenSkills;
    }

    public int getOriginSkillLevel(int skill) {
        SkillEntry ret = skills.get(SkillFactory.getSkill(skill));
        if (ret == null) {
            return 0;
        }
        if (ret.skillevel == 0) {
            return 0;
        }
        if (SkillFactory.getSkill(skill).getMaxLevel() == 1) {
            return 1;
        }
        return ret.skillevel;
    }

    public void setHonourExp(int exp) {
        this.honourExp = exp;
    }

    public int getHonourExp() {
        return honourExp;
    }

    public void setHonorLevel(int level) {
        this.honorLevel = level;
    }

    public int getHonorLevel() {
        if (honorLevel == 0) {
            honorLevel++;
        }
        return honorLevel;
    }

    public List<InnerSkillValueHolder> getInnerSkills() {
        return innerSkills;
    }

    public void addHonorExp(int amount, boolean show) {
        if (getHonorLevel() < 1) {
            setHonorLevel(1);
        }
        if (getHonourExp() + amount >= getHonorLevel() * 500) {
            //     honourLevelUp();
            int leftamount = (getHonourExp() + amount) - ((getHonorLevel() - 1) * 500);
            leftamount = Math.min(leftamount, ((getHonorLevel()) * 500) - 1);
            setHonourExp(leftamount);
            return;
        }
        setHonourExp(getHonourExp() + amount);
        client.SendPacket(CWvsContext.updateAzwanFame(getHonorLevel(), getHonourExp(), true));
        client.SendPacket(CWvsContext.updateSpecialStat(MapleSpecialStatUpdateType.UpdateHonor, 0, getHonorLevel(), getHonourNextExp()));
        if (show) {
            dropMessage(5, "You obtained " + amount + " Honor EXP.");
        }
    }

    public int getHonourNextExp() {
        if (getHonorLevel() == 0) {
            return 0;
        }
        return (getHonorLevel() + 1) * 500;
    }

    public void HonorUnlock() {
        InnerSkillValueHolder inner = null;
        byte ability = 0;
        if (getLevel() == 30);/* || getLevel() == 50 || getLevel() == 100 && inner != null && ability >= 1 && ability <= 3)*/ {
            inner = new InnerSkillValueHolder(70000015, (byte) 1, (byte) 1, (byte) 1, true);
            innerSkills.add(inner);
            changeSkillLevel(SkillFactory.getSkill(inner.getSkillId()), inner.getSkillLevel(), inner.getSkillLevel());
            client.SendPacket(CField.getCharInfo(this));
            client.SendPacket(CField.updateInnerPotential(ability, inner.getSkillId(), inner.getSkillLevel(), inner.getRank()));
            fakeRelog2();
        }
    }

    public void HonorUnlock2() {
        InnerSkillValueHolder inner = null;
        byte ability = 0;
        if (getLevel() == 60);
        {
            inner = new InnerSkillValueHolder(70000016, (byte) 1, (byte) 1, (byte) 1, true);
            innerSkills.add(inner);
            changeSkillLevel(SkillFactory.getSkill(inner.getSkillId()), inner.getSkillLevel(), inner.getSkillLevel());
            client.SendPacket(CField.getCharInfo(this));
            client.SendPacket(CField.updateInnerPotential(ability, inner.getSkillId(), inner.getSkillLevel(), inner.getRank()));
            fakeRelog2();
        }
    }

    public void HonorUnlock3() {
        InnerSkillValueHolder inner = null;
        byte ability = 0;
        if (getLevel() == 100);
        {
            inner = new InnerSkillValueHolder(70000017, (byte) 1, (byte) 1, (byte) 1, true);
            innerSkills.add(inner);
            changeSkillLevel(SkillFactory.getSkill(inner.getSkillId()), inner.getSkillLevel(), inner.getSkillLevel());
            client.SendPacket(CField.getCharInfo(this));
            client.SendPacket(CField.updateInnerPotential(ability, inner.getSkillId(), inner.getSkillLevel(), inner.getRank()));
            fakeRelog2();
        }
    }

    public void gainHonor(int honor, boolean show) {
        addHonorExp(honor, false);
        if (show) {
            SystemMessage message = new SystemMessage(honor + " Honor EXP obtained.");
            write(CWvsContext.messagePacket(message));
        }
    }

    public void azwanReward(final int map, final int portal) {
        client.SendPacket(CField.UIPacket.sendAzwanResult());
        client.SendPacket(CWvsContext.enableActions());
        MapTimer.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
                changeMap(map, portal);
            }
        }, 5 * 1000);
    }

    public int getPQLog(String pqid) {
        try (Connection con1 = Database.GetConnection()) {
            System.out.println(Thread.currentThread().getStackTrace()[2].getClassName() + "." + Thread.currentThread().getStackTrace()[2].getMethodName());
            int ret_count = 0;
            PreparedStatement ps;
            ps = con1.prepareStatement("SELECT COUNT(*) FROM pqlog WHERE charid = ? and pqid = ? and lastattempt >= subtime(current_timestamp, '1 0:0:0.0')");
            ps.setInt(1, id);
            ps.setString(2, pqid);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                ret_count = rs.getInt(1);
            } else {
                ret_count = -1;
            }
            rs.close();
            ps.close();
            return ret_count;
        } catch (Exception Ex) {
            return -1;
        }
    }

    //setPQLog module
    public void setPQLog(String pqid) {
        try (Connection con1 = Database.GetConnection()) {
            System.out.println(Thread.currentThread().getStackTrace()[2].getClassName() + "." + Thread.currentThread().getStackTrace()[2].getMethodName());
            PreparedStatement ps;
            ps = con1.prepareStatement("INSERT INTO pqlog (accid, charid, pqid) values (?,?,?)");
            ps.setInt(1, accountid);
            ps.setInt(2, id);
            ps.setString(3, pqid);
            ps.executeUpdate();
            ps.close();
        } catch (Exception Ex) {
        }
    }

    public void setKeyValue(String key, String values) {
        if (CustomValues.containsKey(key)) {
            CustomValues.remove(key);
        }
        CustomValues.put(key, values);
        keyvalue_changed = true;
    }

    public String getKeyValue(String key) {
        if (CustomValues.containsKey(key)) {
            return CustomValues.get(key);
        }
        return null;
    }

    public void gainItem(int code, int amount) {
        MapleInventoryManipulator.addById(client, code, (short) amount, null);
    }

    public void gainItem(int code, int amount, String gmLog) {
        MapleInventoryManipulator.addById(client, code, (short) amount, gmLog);
    }

    public final int[] getFriendShipPoints() {
        return friendshippoints;
    }

    public final void setFriendShipPoints(int joejoe, int hermoninny, int littledragon, int ika) {
        this.friendshippoints[0] = joejoe;
        this.friendshippoints[1] = hermoninny;
        this.friendshippoints[2] = littledragon;
        this.friendshippoints[3] = ika;
    }

    public final int getFriendShipToAdd() {
        return friendshiptoadd;
    }

    public final void setFriendShipToAdd(int points) {
        this.friendshiptoadd = points;
    }

    public final void addFriendShipToAdd(int points) {
        this.friendshiptoadd += points;
    }

    public void makeNewAzwanShop() {
        /*
         * Azwan Etc Scrolls 60% - 30 conqueror coins
         * Azwan Weapon Scrolls 60% - 50 conqueror coins
         * 9 Conqueror's Coin - 1 emperor coin
         * Circulator - rank * 1 emperor coins
         * Azwan Scrolls 50% - 3 emperor coins
         * Azwan Scrolls 40% - 5 emperor coins
         * Azwan Scrolls 30% - 7 emperor coins
         * Azwan Scrolls 20% - 10 emperor coins
         * Emperor Armor Recipes - 70 emperor coins
         * Emperor Accessory & Weapon Recipes - 75 emperor coins
         * Emperor Weapon - 300 emperor coins
         * Emperor Armor - 250 emperor coins
         * Duke Accessory - 50 emperor coins
         * Duke Accessory - 50 emperor coins
         * 10 Sunrise Dew - 3 conqueror coins
         * 10 Simset Dew - 4 conqueror coins
         * 10 Reindeer Milk - 2 conqueror coins
         * 100% Scrolls - 3 conqueror coins
         */
        azwanShopList = new MapleShop(100000000 + getId(), 2182002);
        int itemid = GameConstants.getAzwanRecipes()[(int) Math.floor(Math.random() * GameConstants.getAzwanRecipes().length)];
        azwanShopList.addItem(new MapleShopItem((short) 1, (short) 1, itemid, 0, (short) 0, 4310038, 75, (byte) 0, 0, 0, 0, 0));
        itemid = GameConstants.getAzwanScrolls()[(int) Math.floor(Math.random() * GameConstants.getAzwanScrolls().length)];
        azwanShopList.addItem(new MapleShopItem((short) 1, (short) 1, itemid, 0, (short) 0, 4310036, 15, (byte) 0, 0, 0, 0, 0));
        itemid = (Integer) GameConstants.getUseItems()[(int) Math.floor(Math.random() * GameConstants.getUseItems().length)].getLeft();
        int price = (Integer) GameConstants.getUseItems()[(int) Math.floor(Math.random() * GameConstants.getUseItems().length)].getRight();
        azwanShopList.addItem(new MapleShopItem((short) 1, (short) 1, itemid, price, (short) 0, 0, 0, (byte) 0, 0, 0, 0, 0));
        itemid = GameConstants.getCirculators()[(int) Math.floor(Math.random() * GameConstants.getCirculators().length)];
        price = InnerAbillity.getInstance().getCirculatorRank(itemid);
        if (price > 10) {
            price = 10;
        }
        azwanShopList.addItem(new MapleShopItem((short) 1, (short) 1, itemid, 0, (short) 0, 4310038, price, (byte) 0, 0, 0, 0, 0));
        //client.write(CField.getWhisper("Jean Pierre", client.getChannel(), "Psst! I got some new items in stock! Come take a look! Oh, but if your Honor Level increased, why not wait until you get a Circulator?"));
    }

    public MapleShop getAzwanShop() {
        return azwanShopList;
    }

    public void openAzwanShop() {
        if (azwanShopList == null) {
            MapleShopFactory.getInstance().getShop(2182002).sendShop(client);
        } else {
            getAzwanShop().sendShop(client);
        }
    }

    public void sendWelcome() {
        getClient().SendPacket(CField.startMapEffect("Welcome to " + getClient().getChannelServer().getServerName() + "!", 5122000, true));
        dropMessage(1, "Welcome to " + getClient().getChannelServer().getServerName() + ", " + getName() + " ! \r\nUse @npc to collect your Item Of Appreciation once you're level 10! \r\nUse @help for commands. \r\nGood luck and have fun!");
        dropMessage(5, "Your EXP Rate will be set to 1x until you finish the tutorial.");
        dropMessage(5, "Use @npc to collect your Item Of Appreciation once you're level 10! Use @help for commands. Good luck and have fun!");
        dropMessage(1, "Use @help for additional commands.");
    }

    private static void addMedalString(final User c, final StringBuilder sb) {
        final Item medal = c.getInventory(MapleInventoryType.EQUIPPED).getItem((byte) -46);
        if (medal != null) { // Medal
            sb.append("<");
            if (medal.getItemId() == 1142257 && GameConstants.isExplorer(c.getJob())) {
                MapleQuestStatus stat = c.getQuestNoAdd(MapleQuest.getInstance(GameConstants.ULT_EXPLORER));
                if (stat != null && stat.getCustomData() != null) {
                    sb.append(stat.getCustomData());
                    sb.append("'s Successor");
                } else {
                    sb.append(MapleItemInformationProvider.getInstance().getName(medal.getItemId()));
                }
            } else {
                sb.append(MapleItemInformationProvider.getInstance().getName(medal.getItemId()));
            }
            sb.append("> ");
        }
    }

    public void updateReward() {
        List<MapleReward> rewards = new LinkedList<>();
        try (Connection con = Database.GetConnection()) {
            System.out.println(Thread.currentThread().getStackTrace()[2].getClassName() + "." + Thread.currentThread().getStackTrace()[2].getMethodName());
            try (PreparedStatement ps = con.prepareStatement("SELECT * FROM rewards WHERE `cid`=?")) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    //rewards.last();
                    //int size = rewards.getRow();
                    //rewards.first();
                    //client.write(Reward.updateReward(rewards.getInt("id"), (byte) 9, rewards, size, 9));
                    while (rs.next()) {
                        rewards.add(new MapleReward(
                                rs.getInt("id"),
                                rs.getLong("start"),
                                rs.getLong("end"),
                                rs.getInt("type"),
                                rs.getInt("itemId"),
                                rs.getInt("mp"),
                                rs.getInt("meso"),
                                rs.getInt("exp"),
                                rs.getString("desc")));
                    }
                }
            }
        } catch (SQLException e) {
            LogHelper.SQL.get().info("[SQL] There was an issue with something from the database:\n", e);
        }
        client.SendPacket(Reward.updateReward(0, (byte) 9, rewards, 9));
    }

    public MapleReward getReward(int id) {
        MapleReward reward = null;
        try (Connection con = Database.GetConnection()) {
            System.out.println(Thread.currentThread().getStackTrace()[2].getClassName() + "." + Thread.currentThread().getStackTrace()[2].getMethodName());
            try (PreparedStatement ps = con.prepareStatement("SELECT * FROM rewards WHERE `id` = ?")) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        reward = new MapleReward(rs.getInt("id"), rs.getLong("start"), rs.getLong("end"), rs.getInt("type"), rs.getInt("itemId"), rs.getInt("mp"), rs.getInt("meso"), rs.getInt("exp"), rs.getString("desc"));
                    }
                }
            }
        } catch (SQLException e) {
            LogHelper.SQL.get().info("[SQL] There was an issue with something from the database:\n", e);
        }
        return reward;
    }

    public void addReward(int type, int item, int mp, int meso, int exp, String desc) {
        addReward(0L, 0L, type, item, mp, meso, exp, desc);
    }

    public void addReward(long start, long end, int type, int item, int mp, int meso, int exp, String desc) {
        try (Connection con = Database.GetConnection()) {
            System.out.println(Thread.currentThread().getStackTrace()[2].getClassName() + "." + Thread.currentThread().getStackTrace()[2].getMethodName());
            try (PreparedStatement ps = con.prepareStatement("INSERT INTO rewards (`cid`, `start`, `end`, `type`, `itemId`, `mp`, `meso`, `exp`, `desc`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
                ps.setInt(1, id);
                ps.setLong(2, start);
                ps.setLong(3, end);
                ps.setInt(4, type);
                ps.setInt(5, item);
                ps.setInt(6, mp);
                ps.setInt(7, meso);
                ps.setInt(8, exp);
                ps.setString(9, desc);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            LogHelper.SQL.get().info("[SQL] There was an issue with something from the database:\n", e);
        }
    }

    public void deleteReward(int id) {
        try (Connection con = Database.GetConnection()) {
            System.out.println(Thread.currentThread().getStackTrace()[2].getClassName() + "." + Thread.currentThread().getStackTrace()[2].getMethodName());
            try (PreparedStatement ps = con.prepareStatement("DELETE FROM rewards WHERE `id` = ?")) {
                ps.setInt(1, id);
                ps.execute();
            }
        } catch (SQLException e) {
            LogHelper.SQL.get().info("[SQL] There was an issue with something from the database:\n", e);
        }
        updateReward();
    }

    public void userRewardRequest(int nLevel) {
        if (GameConstants.isExplorer(job)) {
            switch (nLevel) {
                case 15:
                    addReward(300, 2000019, 0, 0, 0, "You have been rewarded with some power elixir for reaching Lv. " + level + "!");
                    break;
                case 30:
                    addReward(1, 1142747, 0, 0, 0, "You have been rewarded with a new medal for reaching Lv. " + level + "!");
                    if (GameConstants.isWarriorHero(job)) {
                        addReward(1, 1352200, 0, 0, 0, "You have been rewarded with a new secondary weapon for reaching Lv. " + level + "!");
                    } else if (GameConstants.isWarriorPaladin(job)) {
                        addReward(1, 1352210, 0, 0, 0, "You have been rewarded with a new secondary weapon for reaching Lv. " + level + "!");
                    } else if (GameConstants.isWarriorDarkKnight(job)) {
                        addReward(1, 1352220, 0, 0, 0, "You have been rewarded with a new secondary weapon for reaching Lv. " + level + "!");
                    } else if (GameConstants.isMagicianIceLightning(job)) {
                        addReward(1, 1352230, 0, 0, 0, "You have been rewarded with a new secondary weapon for reaching Lv. " + level + "!");
                    } else if (GameConstants.isMagicianFirePoison(job)) {
                        addReward(1, 1352240, 0, 0, 0, "You have been rewarded with a new secondary weapon for reaching Lv. " + level + "!");
                    } else if (GameConstants.isMagicianBishop(job)) {
                        addReward(1, 1352250, 0, 0, 0, "You have been rewarded with a new secondary weapon for reaching Lv. " + level + "!");
                    } else if (GameConstants.isArcherBowmaster(job)) {
                        addReward(1, 1352260, 0, 0, 0, "You have been rewarded with a new secondary weapon for reaching Lv. " + level + "!");
                    } else if (GameConstants.isArcherMarksman(job)) {
                        addReward(1, 1352270, 0, 0, 0, "You have been rewarded with a new secondary weapon for reaching Lv. " + level + "!");
                    } else if (GameConstants.isThiefShadower(job)) {
                        addReward(1, 1352280, 0, 0, 0, "You have been rewarded with a new secondary weapon for reaching Lv. " + level + "!");
                    } else if (GameConstants.isThiefNightLord(job)) {
                        addReward(1, 1352290, 0, 0, 0, "You have been rewarded with a new secondary weapon for reaching Lv. " + level + "!");
                    } else if (GameConstants.isPirateBuccaneer(job)) {
                        addReward(1, 1352900, 0, 0, 0, "You have been rewarded with a new secondary weapon for reaching Lv. " + level + "!");
                    } else if (GameConstants.isPirateCorsair(job)) {
                        addReward(1, 1352910, 0, 0, 0, "You have been rewarded with a new secondary weapon for reaching Lv. " + level + "!");
                    } else if (GameConstants.isCannoneer(job)) {
                        addReward(1, 1352920, 0, 0, 0, "You have been rewarded with a new secondary weapon for reaching Lv. " + level + "!");
                    }
                    break;
                case 60:
                    addReward(1, 1142748, 0, 0, 0, "You have been rewarded with a new medal for reaching Lv. " + level + "!");
                    addReward(1, 1190300, 0, 0, 0, "You have been rewarded with a Silver Maple Leaf Emblem for reaching Lv. " + level + "!");
                    if (GameConstants.isWarriorHero(job)) {
                        addReward(1, 1352201, 0, 0, 0, "You have been rewarded with a new secondary weapon for reaching Lv. " + level + "!");
                    } else if (GameConstants.isWarriorPaladin(job)) {
                        addReward(1, 1352211, 0, 0, 0, "You have been rewarded with a new secondary weapon for reaching Lv. " + level + "!");
                    } else if (GameConstants.isWarriorDarkKnight(job)) {
                        addReward(1, 1352221, 0, 0, 0, "You have been rewarded with a new secondary weapon for reaching Lv. " + level + "!");
                    } else if (GameConstants.isMagicianIceLightning(job)) {
                        addReward(1, 1352231, 0, 0, 0, "You have been rewarded with a new secondary weapon for reaching Lv. " + level + "!");
                    } else if (GameConstants.isMagicianFirePoison(job)) {
                        addReward(1, 1352241, 0, 0, 0, "You have been rewarded with a new secondary weapon for reaching Lv. " + level + "!");
                    } else if (GameConstants.isMagicianBishop(job)) {
                        addReward(1, 1352251, 0, 0, 0, "You have been rewarded with a new secondary weapon for reaching Lv. " + level + "!");
                    } else if (GameConstants.isArcherBowmaster(job)) {
                        addReward(1, 1352261, 0, 0, 0, "You have been rewarded with a new secondary weapon for reaching Lv. " + level + "!");
                    } else if (GameConstants.isArcherMarksman(job)) {
                        addReward(1, 1352271, 0, 0, 0, "You have been rewarded with a new secondary weapon for reaching Lv. " + level + "!");
                    } else if (GameConstants.isThiefShadower(job)) {
                        addReward(1, 1352281, 0, 0, 0, "You have been rewarded with a new secondary weapon for reaching Lv. " + level + "!");
                    } else if (GameConstants.isThiefNightLord(job)) {
                        addReward(1, 1352291, 0, 0, 0, "You have been rewarded with a new secondary weapon for reaching Lv. " + level + "!");
                    } else if (GameConstants.isPirateBuccaneer(job)) {
                        addReward(1, 1352901, 0, 0, 0, "You have been rewarded with a new secondary weapon for reaching Lv. " + level + "!");
                    } else if (GameConstants.isPirateCorsair(job)) {
                        addReward(1, 1352911, 0, 0, 0, "You have been rewarded with a new secondary weapon for reaching Lv. " + level + "!");
                    } else if (GameConstants.isCannoneer(job)) {
                        addReward(1, 1352921, 0, 0, 0, "You have been rewarded with a new secondary weapon for reaching Lv. " + level + "!");
                    }
                    break;
                case 100:
                    givePinnacleGear();
                    addReward(1, 1142749, 0, 0, 0, "You have been rewarded with a new medal for reaching Lv. " + level + "!");
                    addReward(1, 1190301, 0, 0, 0, "You have been rewarded with a Gold Maple Leaf Emblem for reaching Lv. " + level + "!");
                    if (GameConstants.isWarriorHero(job)) {
                        addReward(1, 1352201, 0, 0, 0, "You have been rewarded with a new secondary weapon for reaching Lv. " + level + "!");
                    } else if (GameConstants.isWarriorPaladin(job)) {
                        addReward(1, 1352211, 0, 0, 0, "You have been rewarded with a new secondary weapon for reaching Lv. " + level + "!");
                    } else if (GameConstants.isWarriorDarkKnight(job)) {
                        addReward(1, 1352221, 0, 0, 0, "You have been rewarded with a new secondary weapon for reaching Lv. " + level + "!");
                    } else if (GameConstants.isMagicianIceLightning(job)) {
                        addReward(1, 1352231, 0, 0, 0, "You have been rewarded with a new secondary weapon for reaching Lv. " + level + "!");
                    } else if (GameConstants.isMagicianFirePoison(job)) {
                        addReward(1, 1352241, 0, 0, 0, "You have been rewarded with a new secondary weapon for reaching Lv. " + level + "!");
                    } else if (GameConstants.isMagicianBishop(job)) {
                        addReward(1, 1352251, 0, 0, 0, "You have been rewarded with a new secondary weapon for reaching Lv. " + level + "!");
                    } else if (GameConstants.isArcherBowmaster(job)) {
                        addReward(1, 1352261, 0, 0, 0, "You have been rewarded with a new secondary weapon for reaching Lv. " + level + "!");
                    } else if (GameConstants.isArcherMarksman(job)) {
                        addReward(1, 1352271, 0, 0, 0, "You have been rewarded with a new secondary weapon for reaching Lv. " + level + "!");
                    } else if (GameConstants.isThiefShadower(job)) {
                        addReward(1, 1352281, 0, 0, 0, "You have been rewarded with a new secondary weapon for reaching Lv. " + level + "!");
                    } else if (GameConstants.isThiefNightLord(job)) {
                        addReward(1, 1352291, 0, 0, 0, "You have been rewarded with a new secondary weapon for reaching Lv. " + level + "!");
                    } else if (GameConstants.isPirateBuccaneer(job)) {
                        addReward(1, 1352901, 0, 0, 0, "You have been rewarded with a new secondary weapon for reaching Lv. " + level + "!");
                    } else if (GameConstants.isPirateCorsair(job)) {
                        addReward(1, 1352911, 0, 0, 0, "You have been rewarded with a new secondary weapon for reaching Lv. " + level + "!");
                    } else if (GameConstants.isCannoneer(job)) {
                        addReward(1, 1352921, 0, 0, 0, "You have been rewarded with a new secondary weapon for reaching Lv. " + level + "!");
                    }
                    break;
                case 150:
                    addReward(1, 1142750, 0, 0, 0, "You have been rewarded with a new medal for reaching Lv. " + level + "!");
                    break;
                case 200:
                    addReward(1, 1142751, 0, 0, 0, "You have been rewarded with a new medal for reaching Lv. " + level + "!");
                    break;
            }
        } else if (GameConstants.isCygnusKnight(job)) {
            switch (nLevel) {
                case 15:
                    addReward(300, 2000019, 0, 0, 0, "You have been rewarded with some power elixir for reaching Lv. " + level + "!");
                    break;
                case 30:
                    addReward(1, 1142747, 0, 0, 0, "You have been rewarded with a new medal for reaching Lv. " + level + "!");
                    if (!GameConstants.isMihile(job)) {
                        addReward(1, 1352970, 0, 0, 0, "You have been rewarded with a new secondary weapon for reaching Lv. " + level + "!");
                    }
                    break;
                case 60:
                    addReward(1, 1142748, 0, 0, 0, "You have been rewarded with a new medal for reaching Lv. " + level + "!");
                    if (!GameConstants.isMihile(job)) {
                        addReward(1, 1352971, 0, 0, 0, "You have been rewarded with a new secondary weapon for reaching Lv. " + level + "!");
                    }
                    addReward(1, 1190800, 0, 0, 0, "You have been rewarded with a Silver Cygnus Emblem for reaching Lv. " + level + "!");
                    break;
                case 100:
                    givePinnacleGear();
                    addReward(1, 1142749, 0, 0, 0, "You have been rewarded with a new medal for reaching Lv. " + level + "!");
                    if (!GameConstants.isMihile(job)) {
                        addReward(1, 1352972, 0, 0, 0, "You have been rewarded with a new secondary weapon for reaching Lv. " + level + "!");
                    }
                    addReward(1, 1190801, 0, 0, 0, "You have been rewarded with a Gold Cygnus Emblem for reaching Lv. " + level + "!");
                    break;
                case 150:
                    addReward(1, 1142750, 0, 0, 0, "You have been rewarded with a new medal for reaching Lv. " + level + "!");
                    break;
                case 200:
                    addReward(1, 1142751, 0, 0, 0, "You have been rewarded with a new medal for reaching Lv. " + level + "!");
                    break;
            }
        } else if (GameConstants.isDemon(job)) {
            switch (nLevel) {
                case 15:
                    addReward(300, 2000019, 0, 0, 0, "You have been rewarded with some power elixir for reaching Lv. " + level + "!");
                    break;
                case 30:
                    addReward(1, 1142747, 0, 0, 0, "You have been rewarded with a new medal for reaching Lv. " + level + "!");
                case 60:
                    addReward(1, 1142748, 0, 0, 0, "You have been rewarded with a new medal for reaching Lv. " + level + "!");
                    addReward(1, 1190700, 0, 0, 0, "You have been rewarded with a Silver Demon Emblem for reaching Lv. " + level + "!");
                    break;
                case 100:
                    givePinnacleGear();
                    addReward(1, 1142749, 0, 0, 0, "You have been rewarded with a new medal for reaching Lv. " + level + "!");
                    addReward(1, 1190701, 0, 0, 0, "You have been rewarded with a Gold Demon Emblem for reaching Lv. " + level + "!");
                    break;
                case 150:
                    addReward(1, 1142750, 0, 0, 0, "You have been rewarded with a new medal for reaching Lv. " + level + "!");
                    break;
                case 200:
                    addReward(1, 1142751, 0, 0, 0, "You have been rewarded with a new medal for reaching Lv. " + level + "!");
                    break;
            }
        } else if (GameConstants.isXenon(job)) {
            switch (nLevel) {
                case 15:
                    addReward(1, 1353000, 0, 0, 0, "You have been rewarded with a new secondary weapon for reaching Lv. " + level + "!");
                    addReward(300, 2000019, 0, 0, 0, "You have been rewarded with some power elixir for reaching Lv. " + level + "!");
                    break;
                case 30:
                    addReward(1, 1353001, 0, 0, 0, "You have been rewarded with a new secondary weapon for reaching Lv. " + level + "!");
                    addReward(1, 1142747, 0, 0, 0, "You have been rewarded with a new medal for reaching Lv. " + level + "!");
                    break;
                case 60:
                    addReward(1, 1142748, 0, 0, 0, "You have been rewarded with a new medal for reaching Lv. " + level + "!");
                    addReward(1, 1353002, 0, 0, 0, "You have been rewarded with a new secondary weapon for reaching Lv. " + level + "!");
                    addReward(1, 1190200, 0, 0, 0, "You have been rewarded with a Silver Hybrid Heart for reaching Lv. " + level + "!");
                    break;
                case 100:
                    givePinnacleGear();
                    addReward(1, 1142749, 0, 0, 0, "You have been rewarded with a new medal for reaching Lv. " + level + "!");
                    addReward(1, 1353003, 0, 0, 0, "You have been rewarded with a new secondary weapon for reaching Lv. " + level + "!");
                    addReward(1, 1190201, 0, 0, 0, "You have been rewarded with a Gold Hybrid Heart for reaching Lv. " + level + "!");
                    break;
                case 150:
                    addReward(1, 1142750, 0, 0, 0, "You have been rewarded with a new medal for reaching Lv. " + level + "!");
                    break;
                case 200:
                    addReward(1, 1142751, 0, 0, 0, "You have been rewarded with a new medal for reaching Lv. " + level + "!");
                    break;
            }
        } else if (GameConstants.isResistance(job)) {
            switch (nLevel) {
                case 15:
                    addReward(300, 2000019, 0, 0, 0, "You have been rewarded with some power elixir for reaching Lv. " + level + "!");
                    if (GameConstants.isMechanic(job)) {
                        addReward(1, 1352700, 0, 0, 0, "You have been rewarded with a new secondary weapon for reaching Lv. " + level + "!");
                    } else if (GameConstants.isBattleMage(job)) {
                        addReward(1, 1352950, 0, 0, 0, "You have been rewarded with a new secondary weapon for reaching Lv. " + level + "!");
                    } else if (GameConstants.isWildHunter(job)) {
                        addReward(1, 1352960, 0, 0, 0, "You have been rewarded with a new secondary weapon for reaching Lv. " + level + "!");
                    } else if (GameConstants.isBlaster(job)) {
                        addReward(1, 1353400, 0, 0, 0, "You have been rewarded with a new secondary weapon for reaching Lv. " + level + "!");
                    }
                    break;
                case 30:
                    addReward(1, 1142747, 0, 0, 0, "You have been rewarded with a new medal for reaching Lv. " + level + "!");
                    if (GameConstants.isMechanic(job)) {
                        addReward(1, 1352701, 0, 0, 0, "You have been rewarded with a new secondary weapon for reaching Lv. " + level + "!");
                    } else if (GameConstants.isBattleMage(job)) {
                        addReward(1, 1352951, 0, 0, 0, "You have been rewarded with a new secondary weapon for reaching Lv. " + level + "!");
                    } else if (GameConstants.isWildHunter(job)) {
                        addReward(1, 1352961, 0, 0, 0, "You have been rewarded with a new secondary weapon for reaching Lv. " + level + "!");
                    } else if (GameConstants.isBlaster(job)) {
                        addReward(1, 1353401, 0, 0, 0, "You have been rewarded with a new secondary weapon for reaching Lv. " + level + "!");
                    }
                    break;
                case 60:
                    addReward(1, 1142748, 0, 0, 0, "You have been rewarded with a new medal for reaching Lv. " + level + "!");
                    addReward(1, 1190600, 0, 0, 0, "You have been rewarded with a Silver Resistance Emblem for reaching Lv. " + level + "!");
                    if (GameConstants.isMechanic(job)) {
                        addReward(1, 1352702, 0, 0, 0, "You have been rewarded with a new secondary weapon for reaching Lv. " + level + "!");
                    } else if (GameConstants.isBattleMage(job)) {
                        addReward(1, 1352952, 0, 0, 0, "You have been rewarded with a new secondary weapon for reaching Lv. " + level + "!");
                    } else if (GameConstants.isWildHunter(job)) {
                        addReward(1, 1352962, 0, 0, 0, "You have been rewarded with a new secondary weapon for reaching Lv. " + level + "!");
                    } else if (GameConstants.isBlaster(job)) {
                        addReward(1, 1353402, 0, 0, 0, "You have been rewarded with a new secondary weapon for reaching Lv. " + level + "!");
                    }
                    break;
                case 100:
                    givePinnacleGear();
                    addReward(1, 1142749, 0, 0, 0, "You have been rewarded with a new medal for reaching Lv. " + level + "!");
                    addReward(1, 1190601, 0, 0, 0, "You have been rewarded with a Gold Resistance Emblem for reaching Lv. " + level + "!");
                    if (GameConstants.isMechanic(job)) {
                        addReward(1, 1352703, 0, 0, 0, "You have been rewarded with a new secondary weapon for reaching Lv. " + level + "!");
                    } else if (GameConstants.isBattleMage(job)) {
                        addReward(1, 1352953, 0, 0, 0, "You have been rewarded with a new secondary weapon for reaching Lv. " + level + "!");
                    } else if (GameConstants.isWildHunter(job)) {
                        addReward(1, 1352963, 0, 0, 0, "You have been rewarded with a new secondary weapon for reaching Lv. " + level + "!");
                    } else if (GameConstants.isBlaster(job)) {
                        addReward(1, 1353403, 0, 0, 0, "You have been rewarded with a new secondary weapon for reaching Lv. " + level + "!");
                    }
                    break;
                case 150:
                    addReward(1, 1142750, 0, 0, 0, "You have been rewarded with a new medal for reaching Lv. " + level + "!");
                    break;
                case 200:
                    addReward(1, 1142751, 0, 0, 0, "You have been rewarded with a new medal for reaching Lv. " + level + "!");
                    break;
            }
        } else if (GameConstants.isLegend(job)) {
            switch (nLevel) {
                case 15:
                    addReward(300, 2000019, 0, 0, 0, "You have been rewarded with some power elixir for reaching Lv. " + level + "!");
                    if (GameConstants.isAran(job)) {
                        addReward(1, 1352930, 0, 0, 0, "You have been rewarded with a new secondary weapon for reaching Lv. " + level + "!");
                    } else if (GameConstants.isEvan(job)) {
                        addReward(1, 1352940, 0, 0, 0, "You have been rewarded with a new secondary weapon for reaching Lv. " + level + "!");
                    } else if (GameConstants.isMercedes(job)) {
                        addReward(1, 1352000, 0, 0, 0, "You have been rewarded with a new secondary weapon for reaching Lv. " + level + "!");
                    } else if (GameConstants.isPhantom(job)) {
                        addReward(1, 1352100, 0, 0, 0, "You have been rewarded with a new secondary weapon for reaching Lv. " + level + "!");
                    } else if (GameConstants.isShade(job)) {
                        addReward(1, 1353100, 0, 0, 0, "You have been rewarded with a new secondary weapon for reaching Lv. " + level + "!");
                    } else if (GameConstants.isLuminous(job)) {
                        addReward(1, 1352400, 0, 0, 0, "You have been rewarded with a new secondary weapon for reaching Lv. " + level + "!");
                    }
                    break;
                case 30:
                    addReward(1, 1142747, 0, 0, 0, "You have been rewarded with a new medal for reaching Lv. " + level + "!");
                    if (GameConstants.isAran(job)) {
                        addReward(1, 1352931, 0, 0, 0, "You have been rewarded with a new secondary weapon for reaching Lv. " + level + "!");
                    } else if (GameConstants.isEvan(job)) {
                        addReward(1, 1352941, 0, 0, 0, "You have been rewarded with a new secondary weapon for reaching Lv. " + level + "!");
                    } else if (GameConstants.isMercedes(job)) {
                        addReward(1, 1352001, 0, 0, 0, "You have been rewarded with a new secondary weapon for reaching Lv. " + level + "!");
                    } else if (GameConstants.isPhantom(job)) {
                        addReward(1, 1352101, 0, 0, 0, "You have been rewarded with a new secondary weapon for reaching Lv. " + level + "!");
                    } else if (GameConstants.isShade(job)) {
                        addReward(1, 1353101, 0, 0, 0, "You have been rewarded with a new secondary weapon for reaching Lv. " + level + "!");
                    } else if (GameConstants.isLuminous(job)) {
                        addReward(1, 1352401, 0, 0, 0, "You have been rewarded with a new secondary weapon for reaching Lv. " + level + "!");
                    }
                    break;
                case 60:
                    addReward(1, 1142748, 0, 0, 0, "You have been rewarded with a new medal for reaching Lv. " + level + "!");
                    if (GameConstants.isAran(job)) {
                        addReward(1, 1190512, 0, 0, 0, "You have been rewarded with a Silver Heroes Emblem for reaching Lv. " + level + "!");
                        addReward(1, 1352932, 0, 0, 0, "You have been rewarded with a new secondary weapon for reaching Lv. " + level + "!");
                    } else if (GameConstants.isEvan(job)) {
                        addReward(1, 1190518, 0, 0, 0, "You have been rewarded with a Silver Heroes Emblem for reaching Lv. " + level + "!");
                        addReward(1, 1352942, 0, 0, 0, "You have been rewarded with a new secondary weapon for reaching Lv. " + level + "!");
                    } else if (GameConstants.isMercedes(job)) {
                        addReward(1, 1190510, 0, 0, 0, "You have been rewarded with a Silver Heroes Emblem for reaching Lv. " + level + "!");
                        addReward(1, 1352002, 0, 0, 0, "You have been rewarded with a new secondary weapon for reaching Lv. " + level + "!");
                    } else if (GameConstants.isPhantom(job)) {
                        addReward(1, 1190514, 0, 0, 0, "You have been rewarded with a Silver Heroes Emblem for reaching Lv. " + level + "!");
                        addReward(1, 1352102, 0, 0, 0, "You have been rewarded with a new secondary weapon for reaching Lv. " + level + "!");
                    } else if (GameConstants.isShade(job)) {
                        addReward(1, 1190520, 0, 0, 0, "You have been rewarded with a Silver Heroes Emblem for reaching Lv. " + level + "!");
                        addReward(1, 1353102, 0, 0, 0, "You have been rewarded with a new secondary weapon for reaching Lv. " + level + "!");
                    } else if (GameConstants.isLuminous(job)) {
                        addReward(1, 1190516, 0, 0, 0, "You have been rewarded with a Silver Heroes Emblem for reaching Lv. " + level + "!");
                        addReward(1, 1352402, 0, 0, 0, "You have been rewarded with a new secondary weapon for reaching Lv. " + level + "!");
                    }
                    break;
                case 100:
                    givePinnacleGear();
                    addReward(1, 1142749, 0, 0, 0, "You have been rewarded with a new medal for reaching Lv. " + level + "!");
                    if (GameConstants.isAran(job)) {
                        addReward(1, 1190513, 0, 0, 0, "You have been rewarded with a Silver Heroes Emblem for reaching Lv. " + level + "!");
                        addReward(1, 1352933, 0, 0, 0, "You have been rewarded with a new secondary weapon for reaching Lv. " + level + "!");
                    } else if (GameConstants.isEvan(job)) {
                        addReward(1, 1190519, 0, 0, 0, "You have been rewarded with a Silver Heroes Emblem for reaching Lv. " + level + "!");
                        addReward(1, 1352943, 0, 0, 0, "You have been rewarded with a new secondary weapon for reaching Lv. " + level + "!");
                    } else if (GameConstants.isMercedes(job)) {
                        addReward(1, 1190511, 0, 0, 0, "You have been rewarded with a Silver Heroes Emblem for reaching Lv. " + level + "!");
                        addReward(1, 1352003, 0, 0, 0, "You have been rewarded with a new secondary weapon for reaching Lv. " + level + "!");
                    } else if (GameConstants.isPhantom(job)) {
                        addReward(1, 1190515, 0, 0, 0, "You have been rewarded with a Silver Heroes Emblem for reaching Lv. " + level + "!");
                        addReward(1, 1352103, 0, 0, 0, "You have been rewarded with a new secondary weapon for reaching Lv. " + level + "!");
                    } else if (GameConstants.isShade(job)) {
                        addReward(1, 1190521, 0, 0, 0, "You have been rewarded with a Silver Heroes Emblem for reaching Lv. " + level + "!");
                        addReward(1, 1353103, 0, 0, 0, "You have been rewarded with a new secondary weapon for reaching Lv. " + level + "!");
                    } else if (GameConstants.isLuminous(job)) {
                        addReward(1, 1190517, 0, 0, 0, "You have been rewarded with a Silver Heroes Emblem for reaching Lv. " + level + "!");
                        addReward(1, 1352403, 0, 0, 0, "You have been rewarded with a new secondary weapon for reaching Lv. " + level + "!");
                    }
                    break;
                case 150:
                    addReward(1, 1142750, 0, 0, 0, "You have been rewarded with a new medal for reaching Lv. " + level + "!");
                    break;
                case 200:
                    addReward(1, 1142751, 0, 0, 0, "You have been rewarded with a new medal for reaching Lv. " + level + "!");
                    break;
            }
        } else if (GameConstants.isJett(job)) {
            switch (nLevel) {
                case 15:
                    addReward(300, 2000019, 0, 0, 0, "You have been rewarded with some power elixir for reaching Lv. " + level + "!");
                    addReward(1, 1352820, 0, 0, 0, "You have been rewarded with a new secondary weapon for reaching Lv. " + level + "!");
                    break;
                case 30:
                    addReward(1, 1142747, 0, 0, 0, "You have been rewarded with a new medal for reaching Lv. " + level + "!");
                    addReward(1, 1352821, 0, 0, 0, "You have been rewarded with a new secondary weapon for reaching Lv. " + level + "!");
                    break;
                case 60:
                    addReward(1, 1142748, 0, 0, 0, "You have been rewarded with a new medal for reaching Lv. " + level + "!");
                    addReward(1, 1352822, 0, 0, 0, "You have been rewarded with a new secondary weapon for reaching Lv. " + level + "!");
                    addReward(1, 1191106, 0, 0, 0, "You have been rewarded with a Silver Astro Emblem for reaching Lv. " + level + "!");
                    break;
                case 100:
                    givePinnacleGear();
                    addReward(1, 1142749, 0, 0, 0, "You have been rewarded with a new medal for reaching Lv. " + level + "!");
                    addReward(1, 1352823, 0, 0, 0, "You have been rewarded with a new secondary weapon for reaching Lv. " + level + "!");
                    addReward(1, 1191107, 0, 0, 0, "You have been rewarded with a Gold Astro Emblem for reaching Lv. " + level + "!");
                    break;
                case 150:
                    addReward(1, 1142750, 0, 0, 0, "You have been rewarded with a new medal for reaching Lv. " + level + "!");
                    break;
                case 200:
                    addReward(1, 1142751, 0, 0, 0, "You have been rewarded with a new medal for reaching Lv. " + level + "!");
                    break;
            }
        } else if (GameConstants.isKaiser(job)) {
            switch (nLevel) {
                case 15:
                    addReward(300, 2000019, 0, 0, 0, "You have been rewarded with some power elixir for reaching Lv. " + level + "!");
                    break;
                case 30:
                    addReward(1, 1142747, 0, 0, 0, "You have been rewarded with a new medal for reaching Lv. " + level + "!");
                    break;
                case 60:
                    addReward(1, 1142748, 0, 0, 0, "You have been rewarded with a new medal for reaching Lv. " + level + "!");
                    addReward(1, 1190000, 0, 0, 0, "You have been rewarded with a Lesser Dragon Emblem for reaching Lv. " + level + "!");
                    break;
                case 100:
                    givePinnacleGear();
                    addReward(1, 1142749, 0, 0, 0, "You have been rewarded with a new medal for reaching Lv. " + level + "!");
                    addReward(1, 1190001, 0, 0, 0, "You have been rewarded with a Dragon Emblem for reaching Lv. " + level + "!");
                    break;
                case 150:
                    addReward(1, 1142750, 0, 0, 0, "You have been rewarded with a new medal for reaching Lv. " + level + "!");
                    break;
                case 200:
                    addReward(1, 1142751, 0, 0, 0, "You have been rewarded with a new medal for reaching Lv. " + level + "!");
                    break;
            }
        } else if (GameConstants.isAngelicBuster(job)) {
            switch (nLevel) {
                case 15:
                    addReward(300, 2000019, 0, 0, 0, "You have been rewarded with some power elixir for reaching Lv. " + level + "!");
                    break;
                case 30:
                    addReward(1, 1142747, 0, 0, 0, "You have been rewarded with a new medal for reaching Lv. " + level + "!");
                    break;
                case 60:
                    addReward(1, 1142748, 0, 0, 0, "You have been rewarded with a new medal for reaching Lv. " + level + "!");
                    addReward(1, 1190100, 0, 0, 0, "You have been rewarded with a Lesser Angel Emblem for reaching Lv. " + level + "!");
                    break;
                case 100:
                    givePinnacleGear();
                    addReward(1, 1142749, 0, 0, 0, "You have been rewarded with a new medal for reaching Lv. " + level + "!");
                    addReward(1, 1190101, 0, 0, 0, "You have been rewarded with an Angel Emblem for reaching Lv. " + level + "!");
                    break;
                case 150:
                    addReward(1, 1142750, 0, 0, 0, "You have been rewarded with a new medal for reaching Lv. " + level + "!");
                    break;
                case 200:
                    addReward(1, 1142751, 0, 0, 0, "You have been rewarded with a new medal for reaching Lv. " + level + "!");
                    break;
            }
        } else if (GameConstants.isHayato(job)) {
            switch (nLevel) {
                case 15:
                    addReward(300, 2000019, 0, 0, 0, "You have been rewarded with some power elixir for reaching Lv. " + level + "!");
                    addReward(1, 1352800, 0, 0, 0, "You have been rewarded with a new secondary weapon for reaching Lv. " + level + "!");
                    break;
                case 30:
                    addReward(1, 1142747, 0, 0, 0, "You have been rewarded with a new medal for reaching Lv. " + level + "!");
                    addReward(1, 1352801, 0, 0, 0, "You have been rewarded with a new secondary weapon for reaching Lv. " + level + "!");
                    break;
                case 60:
                    addReward(1, 1142748, 0, 0, 0, "You have been rewarded with a new medal for reaching Lv. " + level + "!");
                    addReward(1, 1352802, 0, 0, 0, "You have been rewarded with a new secondary weapon for reaching Lv. " + level + "!");
                    addReward(1, 1191100, 0, 0, 0, "You have been rewarded with a Silver Crescent Emblem for reaching Lv. " + level + "!");
                    break;
                case 100:
                    givePinnacleGear();
                    addReward(1, 1142749, 0, 0, 0, "You have been rewarded with a new medal for reaching Lv. " + level + "!");
                    addReward(1, 1352803, 0, 0, 0, "You have been rewarded with a new secondary weapon for reaching Lv. " + level + "!");
                    addReward(1, 1191101, 0, 0, 0, "You have been rewarded with a Gold Crescent Emblem for reaching Lv. " + level + "!");
                    break;
                case 150:
                    addReward(1, 1142750, 0, 0, 0, "You have been rewarded with a new medal for reaching Lv. " + level + "!");
                    break;
                case 200:
                    addReward(1, 1142751, 0, 0, 0, "You have been rewarded with a new medal for reaching Lv. " + level + "!");
                    break;
            }
        } else if (GameConstants.isKanna(job)) {
            switch (nLevel) {
                case 15:
                    addReward(300, 2000019, 0, 0, 0, "You have been rewarded with some power elixir for reaching Lv. " + level + "!");
                    break;
                case 30:
                    addReward(1, 1142747, 0, 0, 0, "You have been rewarded with a new medal for reaching Lv. " + level + "!");
                    addReward(1, 1142747, 0, 0, 0, "You have been rewarded with a new medal for reaching Lv. " + level + "!");
                    break;
                case 60:
                    addReward(1, 1142748, 0, 0, 0, "You have been rewarded with a new medal for reaching Lv. " + level + "!");
                    addReward(1, 1191102, 0, 0, 0, "You have been rewarded with a Silver Blossom Emblem for reaching Lv. " + level + "!");
                    break;
                case 100:
                    givePinnacleGear();
                    addReward(1, 1142749, 0, 0, 0, "You have been rewarded with a new medal for reaching Lv. " + level + "!");
                    addReward(1, 1191103, 0, 0, 0, "You have been rewarded with a Gold Blossom Emblem for reaching Lv. " + level + "!");
                    break;
                case 150:
                    addReward(1, 1142750, 0, 0, 0, "You have been rewarded with a new medal for reaching Lv. " + level + "!");
                    break;
                case 200:
                    addReward(1, 1142751, 0, 0, 0, "You have been rewarded with a new medal for reaching Lv. " + level + "!");
                    break;
            }
        } else if (GameConstants.isBeastTamer(job)) {
            switch (nLevel) {
                case 15:
                    addReward(300, 2000019, 0, 0, 0, "You have been rewarded with some power elixir for reaching Lv. " + level + "!");
                    addReward(1, 1352810, 0, 0, 0, "You have been rewarded with a new secondary weapon for reaching Lv. " + level + "!");
                    break;
                case 30:
                    addReward(1, 1142747, 0, 0, 0, "You have been rewarded with a new medal for reaching Lv. " + level + "!");
                    addReward(1, 1352811, 0, 0, 0, "You have been rewarded with a new secondary weapon for reaching Lv. " + level + "!");
                    break;
                case 60:
                    addReward(1, 1142748, 0, 0, 0, "You have been rewarded with a new medal for reaching Lv. " + level + "!");
                    addReward(1, 1352812, 0, 0, 0, "You have been rewarded with a new secondary weapon for reaching Lv. " + level + "!");
                    addReward(1, 1191108, 0, 0, 0, "You have been rewarded with a Silver Forest Emblem for reaching Lv. " + level + "!");
                    break;
                case 100:
                    givePinnacleGear();
                    addReward(1, 1142749, 0, 0, 0, "You have been rewarded with a new medal for reaching Lv. " + level + "!");
                    addReward(1, 1352813, 0, 0, 0, "You have been rewarded with a new secondary weapon for reaching Lv. " + level + "!");
                    addReward(1, 1191109, 0, 0, 0, "You have been rewarded with a Gold Forest Emblem for reaching Lv. " + level + "!");
                    break;
                case 150:
                    addReward(1, 1142750, 0, 0, 0, "You have been rewarded with a new medal for reaching Lv. " + level + "!");
                    break;
                case 200:
                    addReward(1, 1142751, 0, 0, 0, "You have been rewarded with a new medal for reaching Lv. " + level + "!");
                    break;
            }
        } else if (GameConstants.isKinesis(job)) {
            switch (nLevel) {
                case 15:
                    addReward(300, 2000019, 0, 0, 0, "You have been rewarded with some power elixir for reaching Lv. " + level + "!");
                    addReward(1, 1353200, 0, 0, 0, "You have been rewarded with a new secondary weapon for reaching Lv. " + level + "!");
                    break;
                case 30:
                    addReward(1, 1142747, 0, 0, 0, "You have been rewarded with a new medal for reaching Lv. " + level + "!");
                    addReward(1, 1353201, 0, 0, 0, "You have been rewarded with a new secondary weapon for reaching Lv. " + level + "!");
                    break;
                case 60:
                    addReward(1, 1142748, 0, 0, 0, "You have been rewarded with a new medal for reaching Lv. " + level + "!");
                    addReward(1, 1353202, 0, 0, 0, "You have been rewarded with a new secondary weapon for reaching Lv. " + level + "!");
                    addReward(1, 1191000, 0, 0, 0, "You have been rewarded with a Silver Kinesis Emblem for reaching Lv. " + level + "!");
                    break;
                case 100:
                    givePinnacleGear();
                    addReward(1, 1142749, 0, 0, 0, "You have been rewarded with a new medal for reaching Lv. " + level + "!");
                    addReward(1, 1353203, 0, 0, 0, "You have been rewarded with a new secondary weapon for reaching Lv. " + level + "!");
                    addReward(1, 1191001, 0, 0, 0, "You have been rewarded with a Gold Kinesis Emblem for reaching Lv. " + level + "!");
                    break;
                case 150:
                    addReward(1, 1142750, 0, 0, 0, "You have been rewarded with a new medal for reaching Lv. " + level + "!");
                    break;
                case 200:
                    addReward(1, 1142751, 0, 0, 0, "You have been rewarded with a new medal for reaching Lv. " + level + "!");
                    break;
            }
        } else if (GameConstants.isZero(job)) {
            switch (nLevel) {
                case 105:
                    givePinnacleGear();
                    addReward(1, 1142749, 0, 0, 0, "You have been rewarded with a new medal for reaching Lv. " + level + "!");
                    addReward(300, 2000019, 0, 0, 0, "You have been rewarded with some power elixir for reaching Lv. " + level + "!");
                    addReward(1, 1190530, 0, 0, 0, "You have been rewarded with an Eternal Time Emblem for reaching Lv. " + level + "!");
                    break;
                case 150:
                    addReward(1, 1142750, 0, 0, 0, "You have been rewarded with a new medal for reaching Lv. " + level + "!");
                    break;
                case 200:
                    addReward(1, 1142751, 0, 0, 0, "You have been rewarded with a new medal for reaching Lv. " + level + "!");
                    break;
            }
        }
        updateReward();
    }

    public void givePinnacleGear() {
        // Pinnacle Set
        addReward(1, 1003740, 0, 0, 0, "You have been rewarded with a new set for reaching Lv.100!");
        addReward(1, 1052569, 0, 0, 0, "You have been rewarded with a new set for reaching Lv.100!");
        addReward(1, 1072768, 0, 0, 0, "You have been rewarded with a new set for reaching Lv.100!");
        addReward(1, 1082498, 0, 0, 0, "You have been rewarded with a new set for reaching Lv.100!");
        addReward(1, 1102506, 0, 0, 0, "You have been rewarded with a new set for reaching Lv.100!");
        addReward(1, 1112794, 0, 0, 0, "You have been rewarded with a new set for reaching Lv.100!");
        addReward(1, 1122220, 0, 0, 0, "You have been rewarded with a new set for reaching Lv.100!");
        addReward(1, 1132209, 0, 0, 0, "You have been rewarded with a new set for reaching Lv.100!");
        addReward(1, 1152119, 0, 0, 0, "You have been rewarded with a new set for reaching Lv.100!");

        // Weapons
        if (GameConstants.isDualBlade(job)) {
            addReward(1, 1342008, 0, 0, 0, "You have been rewarded with a new set for reaching Lv.100!"); // Meteor Katara
        }
        if (GameConstants.isLuminous(job)) {
            addReward(1, 1212055, 0, 0, 0, "You have been rewarded with a new set for reaching Lv.100!"); // Pinnacle Shining Rod
        }
        if (GameConstants.isAngelicBuster(job)) {
            addReward(1, 1222050, 0, 0, 0, "You have been rewarded with a new set for reaching Lv.100!"); // Pinnacle Soul Shooter
        }
        if (GameConstants.isDemonAvenger(job)) {
            addReward(1, 1232050, 0, 0, 0, "You have been rewarded with a new set for reaching Lv.100!"); // Pinnacle Grim Seeker
        }
        if (GameConstants.isXenon(job)) {
            addReward(1, 1242049, 0, 0, 0, "You have been rewarded with a new set for reaching Lv.100!"); // Pinnacle Hefty Head
        }
        if (GameConstants.isBeastTamer(job)) {
            addReward(1, 1252061, 0, 0, 0, "You have been rewarded with a new set for reaching Lv.100!"); // Pinnacle Kitty Soul Scepter
        }
        if (GameConstants.isWarriorPaladin(job) || GameConstants.isWarriorHero(job) || GameConstants.isMihile(job) || GameConstants.isDawnWarriorCygnus(job)) {
            addReward(1, 1302258, 0, 0, 0, "You have been rewarded with a new set for reaching Lv.100!"); // Pinnacle Gladius
        }
        if (GameConstants.isDualBlade(job) || GameConstants.isThiefShadower(job)) {
            addReward(1, 1332216, 0, 0, 0, "You have been rewarded with a new set for reaching Lv.100!"); // Pinnacle Kanzir
        }
        if (GameConstants.isPhantom(job)) {
            addReward(1, 1362083, 0, 0, 0, "You have been rewarded with a new set for reaching Lv.100!"); // Pinnacle Arc-en-ciel
        }
        if (GameConstants.isEvan(job) || GameConstants.isMagicianIceLightning(job) || GameConstants.isMagicianFirePoison(job) || GameConstants.isMagicianBishop(job) || GameConstants.isBlazeWizardCygnus(job)) {
            addReward(1, 1372170, 0, 0, 0, "You have been rewarded with a new set for reaching Lv.100!"); // Pinnacle Twin Angels
        }
        if (GameConstants.isBattleMage(job) || GameConstants.isMagicianIceLightning(job) || GameConstants.isMagicianFirePoison(job) || GameConstants.isMagicianBishop(job) || GameConstants.isBlazeWizardCygnus(job)) {
            addReward(1, 1382202, 0, 0, 0, "You have been rewarded with a new set for reaching Lv.100!"); // Pinnacle Pain Killer
        }
        if (GameConstants.isWarriorPaladin(job) || GameConstants.isWarriorHero(job) || GameConstants.isKaiser(job) || GameConstants.isDawnWarriorCygnus(job)) {
            addReward(1, 1402187, 0, 0, 0, "You have been rewarded with a new set for reaching Lv.100!"); // Pinnacle Claymore
        }
        if (GameConstants.isAran(job) || GameConstants.isWarriorDarkKnight(job)) {
            addReward(1, 1442211, 0, 0, 0, "You have been rewarded with a new set for reaching Lv.100!"); // Pinnacle Halfmoon
        }
        if (GameConstants.isWindArcherCygnus(job) || GameConstants.isArcherBowmaster(job)) {
            addReward(1, 1452198, 0, 0, 0, "You have been rewarded with a new set for reaching Lv.100!"); // Pinnacle Ash Lord
        }
        if (GameConstants.isShade(job) || GameConstants.isPirateBuccaneer(job) || GameConstants.isThunderBreakerCygnus(job)) {
            addReward(1, 1482161, 0, 0, 0, "You have been rewarded with a new set for reaching Lv.100!"); // Pinnacle Bloody Claw
        }
        if (GameConstants.isJett(job) || GameConstants.isPirateCorsair(job)) {
            addReward(1, 1492172, 0, 0, 0, "You have been rewarded with a new set for reaching Lv.100!"); // Pinnacle Queen's Finger
        }
        if (GameConstants.isMercedes(job)) {
            addReward(1, 1522087, 0, 0, 0, "You have been rewarded with a new set for reaching Lv.100!"); // Pinnacle Argents
        }
        if (GameConstants.isCannoneer(job)) {
            addReward(1, 1532091, 0, 0, 0, "You have been rewarded with a new set for reaching Lv.100!"); // Pinnacle Crash
        }
        if (GameConstants.isHayato(job)) {
            addReward(1, 1542065, 0, 0, 0, "You have been rewarded with a new set for reaching Lv.100!"); // Pinnacle Katana 
        }
        if (GameConstants.isKanna(job)) {
            addReward(1, 1552065, 0, 0, 0, "You have been rewarded with a new set for reaching Lv.100!"); // Pinnacle Fan 
        }
        if (GameConstants.isWarriorHero(job) || GameConstants.isDemonSlayer(job)) {
            addReward(1, 1312145, 0, 0, 0, "You have been rewarded with a new set for reaching Lv.100!"); // Pinnacle Counter
        }
        if (GameConstants.isWarriorHero(job)) {
            addReward(1, 1412128, 0, 0, 0, "You have been rewarded with a new set for reaching Lv.100!"); // Pinnacle Butterfly
        }
        if (GameConstants.isWarriorPaladin(job)) {
            addReward(1, 1422131, 0, 0, 0, "You have been rewarded with a new set for reaching Lv.100!"); // Big Pinnacle Maul
        }
        if (GameConstants.isWarriorDarkKnight(job)) {
            addReward(1, 1432160, 0, 0, 0, "You have been rewarded with a new set for reaching Lv.100!"); // Pinnacle Omni Pierce
        }
        if (GameConstants.isArcherMarksman(job) || GameConstants.isWildHunter(job)) {
            addReward(1, 1462186, 0, 0, 0, "You have been rewarded with a new set for reaching Lv.100!"); // Pinnacle Lock
        }
        if (GameConstants.isNightWalkerCygnus(job) || GameConstants.isThiefNightLord(job)) {
            addReward(1, 1472207, 0, 0, 0, "You have been rewarded with a new set for reaching Lv.100!"); // Pinnacle Black Event 
        }
        if (GameConstants.isBlaster(job)) {
            addReward(1, 1582007, 0, 0, 0, "You have been rewarded with a new set for reaching Lv.100!"); // Pinnacle Black Event 
        }
        if (GameConstants.isKinesis(job)) {
            addReward(1, 1262007, 0, 0, 0, "You have been rewarded with a new set for reaching Lv.100!"); // Daemon Psy-limiter   
        }
    }

    public void checkCustomReward(int level) {
        List<Integer> rewards = new LinkedList<>();
        int mesoReward = 0;
        switch (level) {
            case 10:
                //  rewards.add(2450000);
                //   rewards.add(2022918);
                mesoReward = 1000;
                break;
            case 20:
                // rewards.add(2022918);
                rewards.add(1032099);
                mesoReward = 3000;
                break;
            case 30:
                rewards.add(2450000);
                rewards.add(1112659);
                mesoReward = 5000;
                break;
            case 50:
                rewards.add(2022918);
                rewards.add(1003361);
                rewards.add(1082399);
                rewards.add(1102337);
                mesoReward = 7500;
                break;
            case 70:
                rewards.add(2450000);
                rewards.add(2022918);
                rewards.add(1003016);
                mesoReward = 10000;
                break;
            case 100:
                addReward(4, 0, 0, 10000000, 0, "Here is a special reward for the experts! Alpha only tho.");//just for beta
                rewards.add(2450000);
                rewards.add(2022918);
                rewards.add(1122195);
                rewards.add(1132043);
                rewards.add(1152084);
                mesoReward = 12500;
                break;
            case 120:
                rewards.add(2450000);
                rewards.add(1182007);
                mesoReward = 15000;
                break;
            case 150:
                rewards.add(1142349);
                mesoReward = 17500;
                break;
            case 170:
                rewards.add(1142295);
                mesoReward = 20000;
                break;
            case 200:
                rewards.add(1142456);
                mesoReward = 25000;
                break;
        }

        for (int reward : rewards) {
            addReward(1, reward, 0, 0, 0, "Here is a special reward for reaching Lv. " + level + "!");
        }
        if (mesoReward != 0) {
            addReward(3, 0, mesoReward, 0, 0, "Here is a special reward for reaching Lv. " + level + "!");
        }
        updateReward();
    }

    public void newCharRewards() {
        List<Integer> rewards = new LinkedList<>();
        // rewards.add(2022680);
        //rewards.add(2450031);
        rewards.add(1142358);
        // addReward(4, 0, 0, 5000000, 0, "Here is a special reward for beginners to help you start your journey!");//was 1000000
        for (int reward : rewards) {
            addReward(1, reward, 0, 0, 0, "Here is a special reward for beginners to help you start your journey!");
        }
        updateReward();
    }

    public boolean canVote() {
        try (Connection con = Database.GetConnection()) {
            System.out.println(Thread.currentThread().getStackTrace()[2].getClassName() + "." + Thread.currentThread().getStackTrace()[2].getMethodName());
            try (PreparedStatement ps = con.prepareStatement("SELECT * FROM ipvotes WHERE `accid` = ?")) {
                ps.setInt(1, getAccountID());
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    if ((rs.getLong("lastvote") - (System.currentTimeMillis() / 1000)) > 6 * 60 * 60) {
                        return true;
                    }
                } else {
                    return true;
                }
            }
        } catch (SQLException e) {
            LogHelper.SQL.get().info("[SQL] There was an issue with something from the database:\n", e);
        }
        return false;
    }

    public void setVote() {
        try (Connection con = Database.GetConnection()) {
            System.out.println(Thread.currentThread().getStackTrace()[2].getClassName() + "." + Thread.currentThread().getStackTrace()[2].getMethodName());
            try (PreparedStatement ps = con.prepareStatement("DELETE FROM ipvotes WHERE `accid` = ?")) {
                ps.setInt(1, getAccountID());
                ps.execute();
            }
        } catch (SQLException e) {
            LogHelper.SQL.get().info("[SQL] There was an issue with something from the database:\n", e);
        }
        try (Connection con = Database.GetConnection()) {
            System.out.println(Thread.currentThread().getStackTrace()[2].getClassName() + "." + Thread.currentThread().getStackTrace()[2].getMethodName());
            try (PreparedStatement ps = con.prepareStatement("INSERT INTO ipvotes (`ip`, `accid`, `lastvote`) VALUES (?, ?, ?)")) {
                ps.setString(1, getClient().GetIP().split(":")[0].replaceAll("/", ""));
                ps.setInt(2, getAccountID());
                ps.setLong(3, System.currentTimeMillis() / 1000);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            LogHelper.SQL.get().info("[SQL] There was an issue with something from the database:\n", e);
        }
    }

    public void removeBGLayers() {
        for (byte i = 0; i < 127; i++) {
            client.SendPacket(CField.removeBGLayer(true, 0, i, 0));
            //duration 0 = forever map 0 = current map
        }
    }

    public void showBGLayers() {
        for (byte i = 0; i < 127; i++) {
            client.SendPacket(CField.removeBGLayer(false, 0, i, 0));
            //duration 0 = forever map 0 = current map
        }
    }
    public static final int CUSTOM_BG = 61000000;

    public int getCustomBGState() {
        return getIntNoRecord(CUSTOM_BG);
    }

    public void toggleCustomBGState() {
        getQuestNAdd(MapleQuest.getInstance(CUSTOM_BG)).setCustomData(String.valueOf(getCustomBGState() == 1 ? 0 : 1));
        //  for (byte i = 0; i < 127; i++) {
        //WriteFuture write = client.write(CField.removeBGLayer((getCustomBGState() == 1), 0, i, 0));
        //duration 0 = forever map 0 = current map
        // }
    }

    public void giveLinkSkill(User chr, int linkSkill, int accID, int charID, int sourceSkillLevel, int sourceSkillMaxLevel) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try (Connection con = Database.GetConnection()) {
            System.out.println(Thread.currentThread().getStackTrace()[2].getClassName() + "." + Thread.currentThread().getStackTrace()[2].getMethodName());
            try {
                ps = con.prepareStatement("SELECT accountid FROM characters where id = ?  AND deletedAt is null");
                ps.setInt(1, charID);

                rs = ps.executeQuery();

                if (rs.next()) {
                    accID = rs.getInt(1);
                }
                ps.close();
                rs.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            try {
                ps = con.prepareStatement("DELETE FROM skills WHERE skillid = ? AND characterid IN (SELECT id FROM characters WHERE accountid = ? AND deletedAt is null )");
                ps.setInt(1, linkSkill);
                ps.setInt(2, accID);
                ps.execute();
                ps.close();
                rs.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            try {
                ps = con.prepareStatement("INSERT INTO skills(skillid,characterid,skilllevel,masterlevel,expiration,victimid) "
                        + "VALUES(?,?,?,?,?,?)");
                ps.setInt(1, linkSkill);
                ps.setInt(2, charID);
                ps.setInt(3, sourceSkillLevel);
                ps.setInt(4, sourceSkillMaxLevel);
                ps.setInt(5, -1);
                ps.setInt(6, charID);
                ps.executeUpdate();
                ps.close();
                rs.close();
                con.commit();
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                try {
                    con.setAutoCommit(true);
                } catch (SQLException ex) {
                    LogHelper.SQL.get().info("[SQL] There was an issue with something from the database:\n", ex);
                }
            }
            chr.dropMessage(1, "Link skill updated!");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void addLinkSkill(int cid, int skill) {
        try (Connection con = Database.GetConnection()) {
            System.out.println(Thread.currentThread().getStackTrace()[2].getClassName() + "." + Thread.currentThread().getStackTrace()[2].getMethodName());
            try (PreparedStatement ps = con.prepareStatement("INSERT INTO skills (characterid, skillid, skilllevel, masterlevel, expiration) VALUES (?, ?, ?, ?, ?)")) {
                ps.setInt(1, cid);
                if (GameConstants.isApplicableSkill(skill)) { //do not save additional skills
                    ps.setInt(2, skill);
                    ps.setInt(3, 1);
                    ps.setByte(4, (byte) 1);
                    ps.setLong(5, -1);
                    ps.execute();
                }
            }
        } catch (SQLException ex) {
            LogHelper.SQL.get().info("[SQL] There was an issue with something from the database:\n", ex);
        }
    }

    public int getWheelItem() {
        return wheelItem;
    }

    public void setWheelItem(int wheelItem) {
        this.wheelItem = wheelItem;
    }

    public static void removePartTime(int cid) {
        try (Connection con = Database.GetConnection()) {
            System.out.println(Thread.currentThread().getStackTrace()[2].getClassName() + "." + Thread.currentThread().getStackTrace()[2].getMethodName());
            try (PreparedStatement ps = con.prepareStatement("DELETE FROM parttime where cid = ?")) {
                ps.setInt(1, cid);
                ps.executeUpdate();
            }
        } catch (SQLException ex) {
            LogHelper.SQL.get().info("[SQL] There was an issue with something from the database:\n", ex);
        }
    }

    public static void addPartTime(PartTimeJob partTime) {
        if (partTime.getCharacterId() < 1) {
            return;
        }
        addPartTime(partTime.getCharacterId(), partTime.getJob(), partTime.getTime(), partTime.getReward());
    }

    public static void addPartTime(int cid, byte job, long time, int reward) {
        try (Connection con = Database.GetConnection()) {
            System.out.println(Thread.currentThread().getStackTrace()[2].getClassName() + "." + Thread.currentThread().getStackTrace()[2].getMethodName());
            try (PreparedStatement ps = con.prepareStatement("INSERT INTO parttime (cid, job, time, reward) VALUES (?, ?, ?, ?)")) {
                ps.setInt(1, cid);
                ps.setByte(2, job);
                ps.setLong(3, time);
                ps.setInt(4, reward);
                ps.execute();
            }
        } catch (SQLException ex) {
            LogHelper.SQL.get().info("[SQL] There was an issue with something from the database:\n", ex);
        }
    }

    public static PartTimeJob getPartTime(int cid) {
        PartTimeJob partTime = new PartTimeJob(cid);
        try (Connection con = Database.GetConnection()) {
            System.out.println(Thread.currentThread().getStackTrace()[2].getClassName() + "." + Thread.currentThread().getStackTrace()[2].getMethodName());
            try (PreparedStatement ps = con.prepareStatement("SELECT * FROM parttime WHERE cid = ?")) {
                ps.setInt(1, cid);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        partTime.setJob(rs.getByte("job"));
                        partTime.setTime(rs.getLong("time"));
                        partTime.setReward(rs.getInt("reward"));
                    }
                }
            }
        } catch (Exception ex) {
            System.out.println("Failed to retrieve part time job: " + ex);
        }
        return partTime;
    }

    /**
     * Kinesis: Psychic Point Handlers
     *
     * @author Mazen
     *
     * @purpose Handle gaining Psychic Points.
     */
    public short getPsychicPoint() {
        return kinesisPsychicPoint;
    }

    public void setPsychicPoint(short amount) {
        int maxPP = 30;
        if (kinesisPsychicPoint + amount > maxPP) {
            this.kinesisPsychicPoint = (short) maxPP;
            updatePsychicPoint(kinesisPsychicPoint);
            return;
        }
        this.kinesisPsychicPoint = amount;
        updatePsychicPoint(kinesisPsychicPoint);
    }

    public void gainPsychicPoint(short amount) {
        int maxPP = 30;
        if (kinesisPsychicPoint + amount > maxPP) {
            this.kinesisPsychicPoint = (short) maxPP;
            updatePsychicPoint(kinesisPsychicPoint);
            return;
        }
        this.kinesisPsychicPoint += amount;
        updatePsychicPoint(kinesisPsychicPoint);
    }

    public void updatePsychicPoint(short amount) {
        int maxPP = 30;
        if (amount > maxPP) {
            amount = (short) maxPP;
        }
        //client.write(KinesisPacket.givePsychicPoint(getJob(), kinesisPsychicPoint));
        //client.write(KinesisPacket.givePsychicPoint(amount));
    }

    public void handlePsychicPoint(int skillid) {
        int maxPP = 0;
        MapleStatEffect effects = SkillFactory.getSkill(skillid).getEffect(getSkillLevel(skillid));
        dropMessage(5, "PPCon: " + effects.getPPCon() + " | PPRecovery: " + effects.getPPRecovery());

        switch (getJob()) {
            case 14200:
                maxPP = 10;
                break;
            case 14210:
                maxPP = 15;
                break;
            case 14211:
                maxPP = 20;
                break;
            case 14212:
                maxPP = 30;
                break;
        }

        // Kinesis PP Consume Skills
        if (effects.getPPCon() > 0 && effects.getPPRecovery() < 1) {
            kinesisPsychicPoint -= effects.getPPCon();
            kinesisPsychicPoint -= 1;
        }

        kinesisPsychicPoint += 1;

        /*if (effects.getPPRecovery() > 0) {
            kinesisPsychicPoint += effects.getPPRecovery(); 
        } else if (effects.getPPCon() > 0) {  
            kinesisPsychicPoint -= effects.getPPCon();
        } else if (skillid == 142121008) {
            kinesisPsychicPoint += 15;
        }*/
        //updatePsychicPoint(kinesisPsychicPoint);
        //client.write(KinesisPacket.givePsychicPoint(getJob(), kinesisPsychicPoint));
    }

    /**
     * Night Walker: Shadow Bat Spawn Handler
     *
     * @author Mazen
     *
     * @purpose Spawn up to two Shadow Bats, one bat is spawned after getting a combo of three.
     * @method Handled Shadow Bat as a passive rather than toggle, used kaiserCombo variable to keep track of combo.
     */
    public void handleShadowBat(int mobid, int skill) {
        Skill batSkill = SkillFactory.getSkill(NightWalker.SHADOW_BAT_3);

        if (getSkillLevel(NightWalker.SHADOW_BAT) > 0 || getSkillLevel(NightWalker.SHADOW_BAT_1) > 0 || getSkillLevel(NightWalker.SHADOW_BAT_2) > 0 || getSkillLevel(NightWalker.SHADOW_BAT_3) > 0) {
            if (kaiserCombo >= 3) {
                if (batCount < 2) {
                    MapleStatEffect batEffect = batSkill.getEffect(getSkillLevel(NightWalker.SHADOW_BAT_3));
                    Point batPos = getTruePosition();
                    Summon batSummon = new Summon(this, batEffect, batPos, SummonMovementType.CIRCLE_FOLLOW, 60000);

                    batSummon.setPosition(batPos);
                    getMap().spawnSummon(batSummon);
                    batEffect.applyTo(this, null);
                    batCount++;
                }
                kaiserCombo = 0;
            } else {
                kaiserCombo++;
            }
        }
    }

    public void setBatCount(int amount) {
        batCount = amount;
    }

    public int getBatCount() {
        return batCount;
    }

    /**
     * Night Walker: Dark Elemental Handler
     *
     * @author Mazen
     *
     * @purpose Handle Mark of Darkness provided from Dark Elemental.
     * @method
     */
    public void handleDarkElemental() {
        if (getBuffedValue(CharacterTemporaryStat.ElementDarkness) != null) {
            int stackChance = Randomizer.rand(1, 5); // 1 in 5 Chance (20%)
            if (stackChance == 1) {
                if (darkElementalCombo < 2) { // Limit of 2.
                    darkElementalCombo++;
                    lastExceedTime = System.currentTimeMillis();
                }
                dropMessage(-1, "Mark of Darkness (" + darkElementalCombo + ")");
            }
        } else {
            darkElementalCombo = 0;
        }
    }

    public void setDarkElementalCombo(int amount) {
        darkElementalCombo = amount;
    }

    public int getDarkElementalCombo() {
        return darkElementalCombo;
    }

    public boolean darkElementalTimeout(long now) {
        if (lastExceedTime + 6000 < now) {
            return true;
        }
        return false;
    }

    /**
     * Amaranth Generator Simulator
     *
     * @author Mazen
     *
     * @purpose Imitate the Generator Hyper Skill's effect.
     * @method Sets Xenon's Surplus to 20 every second, for 10 seconds.
     */
    public void handleXenonGenerator() throws Exception {
        dropMessage(5, "The Amaranth Generator is providing you with infinite power!");

        final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleWithFixedDelay(new Runnable() {
            int runCount = 0;

            @Override
            public void run() {
                ++runCount;
                setXenonSurplus((short) 20); // Gives Xenon Max Surplus.
                if (runCount > 39) { // Run this event 40 times.
                    executor.shutdown();
                    dropMessage(5, "The effects of the Amaranth Generator have worn off.");
                }
            }

        }, 0, 250, TimeUnit.MILLISECONDS); // Once every 250ms until the effect wears off (10s).
        executor.awaitTermination(250, TimeUnit.MILLISECONDS);
    }

    /**
     * Fighter: Combo Attack
     *
     * @author Mazen
     *
     * @purpose Handle Combo Attack ability.
     * @method Uses the kaiserCombo variable (again) to apply the combo buff to the player.
     */
    public void handleComboAttack() {
        int comboChance = Randomizer.rand(1, 10); // 1 in 3 Chance (33%)
        if (comboChance == 1) {
            if (kaiserCombo < 6) { // Max Combo Count is 5.
                kaiserCombo += 1;
            } else {
                kaiserCombo = 0;
            }
        }
        SkillFactory.getSkill(1101013).getEffect(1).applyComboAttack(this, kaiserCombo);
    }

    public void handleKaiserCombo() {
        if (kaiserCombo < 7000) {
            kaiserCombo += 3;
        }
        SkillFactory.getSkill(61111008).getEffect(1).applyKaiserCombo(this, kaiserCombo);
    }

    public void resetKaiserCombo() {
        kaiserCombo = 0;
        SkillFactory.getSkill(61111008).getEffect(1).applyKaiserCombo(this, kaiserCombo);
    }

    public void changeLuminousMode() {
        final User chr = this;
        chr.getClient().SendPacket(JobPacket.LuminousPacket.giveLuminousState(20040220, chr.getLightGauge(), chr.getDarkGauge(), 10000));
        Timer.WorldTimer.getInstance().schedule(() -> {
            chr.dispelBuff(20040220);
            switch (chr.getLuminousState()) {
                case 20040217:
                    chr.getClient().SendPacket(JobPacket.LuminousPacket.giveLuminousState(20040216, chr.getLightGauge(), chr.getDarkGauge(), 2000000000));
                    chr.runningLight--;
                    chr.setLuminousState(20040216);
                    break;
                case 20040216:
                    chr.getClient().SendPacket(JobPacket.LuminousPacket.giveLuminousState(20040217, chr.getLightGauge(), chr.getDarkGauge(), 2000000000));
                    chr.runningDark--;
                    chr.setLuminousState(20040217);
                    break;
                default:
                    //In case used when not in any stance to avoid perma Equilibrium
                    chr.getClient().SendPacket(JobPacket.LuminousPacket.giveLuminousState(20040216, chr.getLightGauge(), chr.getDarkGauge(), 2000000000));
                    chr.setLuminousState(20040216);
                    break;
            }
        }, 10000);
    }

    public void handleLuminous(int skillid) {
        switch (skillid) {
            case 27001100:
            case 27101100:
            case 27101101:
            case 27111100:
            case 27111101:
            case 27121100:
                runningLightSlot += Randomizer.nextInt(200) + 200;
                if (runningLightSlot > 10000) {
                    runningLightSlot = 0;
                    runningLight += 1;
                    if (runningLight > 5) {
                        runningLight = 5;
                    }
                }
                break;
            case 27001201:
            case 27101202:
            case 27111202:
            case 27121201:
            case 27121202:
            case 27120211:
                runningDarkSlot += Randomizer.nextInt(200) + 100;
                if (runningDarkSlot > 10000) {
                    runningDarkSlot = 0;
                    runningDark += 1;
                    if (runningDark > 5) {
                        runningDark = 5;
                    }
                }
                break;
        }
        client.SendPacket(LuminousPacket.updateLuminousGauge(runningDarkSlot, runningLightSlot, runningDark, runningLight));
    }

    public void resetRunningStack() {
        this.runningStack = 0;
        this.runningDark = 0;
        this.runningDarkSlot = 0;
        this.runningLight = 0;
        this.runningLightSlot = 0;
    }

    public int getRunningStack() {
        return this.runningStack;
    }

    public void addRunningStack(int s) {
        runningStack += s;
    }

    public int getLightGauge() {
        return runningLightSlot;
    }

    public int getDarkGauge() {
        return runningDarkSlot;
    }

    public int getLuminousState() {
        return luminousState;
    }

    public void setLuminousState(int luminousState) {
        this.luminousState = luminousState;
    }

    public void setCardStack(byte amount) {
        this.cardStack = amount;
    }

    public byte getCardStack() {
        return this.cardStack;
    }

    public void applyBlackBlessingBuff(int combos) {
        if ((combos == -1) && (this.runningBless == 0)) {
            //combos = 0;
            return;
        }
        Skill skill = SkillFactory.getSkill(27100003);
        int lvl = getTotalSkillLevel(27100003);
        if (lvl > 0) {
            runningBless = ((byte) (runningBless + combos));
            if (runningBless > 3) {
                runningBless = 3;
            }
            if (runningBless == 0) {
                if (getBuffedValue(CharacterTemporaryStat.BlessOfDarkness) != null) {
                    cancelTemporaryStats(new CharacterTemporaryStat[]{CharacterTemporaryStat.BlessOfDarkness});
                }
            } else {
                skill.getEffect(lvl).applyBlackBlessingBuff(this, runningBless);
            }
        }
    }

    public final void applyLifeTidal() {
        final Skill lunar = SkillFactory.getSkill(27110007);
        int critical = this.getSkillLevel(lunar);
        if ((this.getStat().getHp() / this.getStat().getMaxHp()) * 100 < (this.getStat().getMp() / this.getStat().getMaxMp()) * 100) {
            this.getClient().SendPacket(JobPacket.LuminousPacket.giveLifeTidal(false, lunar.getEffect(critical).getX()));
        } else if ((this.getStat().getHp() / this.getStat().getMaxHp()) * 100 > (this.getStat().getMp() / this.getStat().getMaxHp()) * 100) {
            if (critical > 0) {
                this.getStat().crit_rate += lunar.getEffect(critical).getProb();
                this.getStat().passive_sharpeye_min_percent += lunar.getEffect(critical).getCriticalMin();
                this.getClient().SendPacket(JobPacket.LuminousPacket.giveLifeTidal(true, lunar.getEffect(critical).getProb()));
            }
        }
    }

    public short getXenonSurplus() {
        return xenonSurplus;
    }

    public void setXenonSurplus(short amount) {
        int maxSupply = level >= 100 ? 20 : level >= 60 ? 15 : level >= 30 ? 10 : 5;
        if (xenonSurplus + amount > maxSupply) {
            this.xenonSurplus = (short) maxSupply;
            updateXenonSurplus(xenonSurplus);
            return;
        }
        this.xenonSurplus = amount;
        updateXenonSurplus(xenonSurplus);
    }

    public void gainXenonSurplus(short amount) {
        int maxSupply = level >= 100 ? 20 : level >= 60 ? 15 : level >= 30 ? 10 : 5;
        if (xenonSurplus + amount > maxSupply) {
            this.xenonSurplus = (short) maxSupply;
            updateXenonSurplus(xenonSurplus);
            return;
        }
        this.xenonSurplus += amount;
        updateXenonSurplus(xenonSurplus);
    }

    public void updateXenonSurplus(short amount) {
        int maxSupply = level >= 100 ? 20 : level >= 60 ? 15 : level >= 30 ? 10 : 5;
        if (amount > maxSupply) {
            amount = (short) maxSupply;
        }
        client.SendPacket(XenonPacket.giveXenonSupply(amount));
    }

    public final void startXenonSupply() {
        BuffTimer tMan = BuffTimer.getInstance();
        Runnable r = new Runnable() {
            @Override
            public void run() {
                int maxSupply = level >= 100 ? 20 : level >= 60 ? 15 : level >= 30 ? 10 : 5;
                if (maxSupply > getXenonSurplus()) {
                    gainXenonSurplus((short) 1);
                }
            }
        };
        if (client.isLoggedIn()) {
            XenonSupplyTask = tMan.register(r, 4000);
        }
    }

    public short getExceed() {
        return exceed;
    }

    public void setExceed(short amount) {
        this.exceed = amount;
    }

    public void gainExceed(short amount) {
        this.exceed += amount;
        updateExceed(exceed);
    }

    public void updateExceed(short amount) {
        client.SendPacket(AvengerPacket.giveExceed(amount));
    }

    public void handleExceedAttack(int skill) {
        long now = System.currentTimeMillis();
        if (lastExceedTime + 15000 < now) { //Cool time is 15000 ms. 
            exceedAttack = 0;
            lastExceedTime = now;
        }
        client.SendPacket(AvengerPacket.giveExceedAttack(skill, ++exceedAttack));
    }

    public boolean exceedTimeout(long now) {
        if (lastExceedTime + 15000 < now) {
            client.SendPacket(AvengerPacket.cancelExceed());
            return true;
        }
        return false;
    }

    public MapleCoreAura getCoreAura() {
        return coreAura;
    }

    public void changeWarriorStance(final int skillid) {
        if (skillid == 11101022) {
            //   dispelBuff(11111022); 
            List<CharacterTemporaryStat> statups = new LinkedList<CharacterTemporaryStat>();
            statups.add(CharacterTemporaryStat.CriticalBuff);
            statups.add(CharacterTemporaryStat.Stance);
            //   client.write(BuffPacket.cancelBuff(statups));*/ 
            //   client.write(JobPacket.DawnWarriorPacket.giveMoonfallStance(getSkillLevel(skillid))); 
            SkillFactory.getSkill(skillid).getEffect(1).applyTo(this);
        } else if (skillid == 11111022) {
            //  dispelBuff(11101022); 
            //   System.out.println("Start of buff"); 
            List<CharacterTemporaryStat> statups = new LinkedList<CharacterTemporaryStat>();
            statups.add(CharacterTemporaryStat.Booster);
            //       System.out.println("ATT Speed"); 
            statups.add(CharacterTemporaryStat.IndieDamR);
            //       System.out.println("DMG Perc"); 
            statups.add(CharacterTemporaryStat.Stance);
            //    statups.put(MapleStatInfo.time, Integer.MAX_VALUE);
            //       System.out.println("WAR Stance"); 
            //client.write(BuffPacket.cancelBuff(statups));*/ 
            //   client.write(JobPacket.DawnWarriorPacket.giveSunriseStance(getSkillLevel(skillid))); 
            SkillFactory.getSkill(skillid).getEffect(1).applyTo(this);
        } else if (skillid == 11121005) {
            //equinox 
        } else if (skillid == 11121011) {
            //      dispelBuff(11101022); 
            //    client.write(JobPacket.DawnWarriorPacket.giveEquinox_Moon(getSkillLevel(skillid), Integer.MAX_VALUE)); 
            SkillFactory.getSkill(skillid).getEffect(1).applyTo(this);
        } else if (skillid == 11121012) {
            //Doesnothingatm
            //    dispelBuff(11101022); 
            //   client.write(JobPacket.DawnWarriorPacket.giveEquinox_Sun(getSkillLevel(skillid), Integer.MAX_VALUE)); 
            SkillFactory.getSkill(skillid).getEffect(1).applyTo(this);
        }
    }

    public List<MaplePotionPot> getPotionPots() {
        return potionPots;
    }

    public int getDeathCount() {
        return deathCount;
    }

    public void setDeathCount(int deathCount) {
        this.deathCount = deathCount;
    }

    public void updateDeathCount() {
        if (deathCount < 0) {
            deathCount = 0;

        }
        if (deathCount > 99) {
            deathCount = 99;
        }
        if (deathCount > 0) {
            client.SendPacket(CUserLocal.updateDeathCount(deathCount));
        }
    }

    /*public void updateDeathCount() {
        if (deathCount < 0) {
            deathCount = 0;
        }
        if (deathCount > 99) {
            deathCount = 99;
        }
        client.write(CUserLocal.updateDeathCount(deathCount));
    }*/
    public void giveMiracleBlessing() {
        for (User chr : getMap().getCharacters()) {
            MapleItemInformationProvider.getInstance().getItemEffect(2023055).applyTo(chr);
        }
        getMap().broadcastMessage(CField.startMapEffect(name + " has received Double Miracle Time's Mysterious Blessing. Congratulations!", 2023055, true));
        World.Broadcast.broadcastMessage(CWvsContext.broadcastMsg(0x19, 0, name + " has received [Double Miracle Time's Miraculous Blessing]. Congratulations!", false));
    }

    /* 
    *   Credits to Malissy for cleaning up the FoceShield method.
    *   Classes get proper shields now.
     */
    public void checkForceShield() {
        final MapleItemInformationProvider li = MapleItemInformationProvider.getInstance();
        Equip equip;
        boolean potential = false;
        switch (job) {
            case 508:
                equip = (Equip) li.getEquipById(1352300);
                break;
            case 572:
                potential = true;
            case 570:
            case 571:
                equip = (Equip) li.getEquipById(1352301 + job % 10);
                break;
            case 3001:
                equip = (Equip) li.getEquipById(1099000);
                break;
            case 3112:
                potential = true;
            case 3100:
            case 3110:
            case 3111:
                equip = (Equip) li.getEquipById(1099001 + job % 10 + ((job % 100) / 10));
                break;
            case 3122:
                potential = true;
            case 3101:
            case 3120:
            case 3121:
                equip = (Equip) li.getEquipById(1099000 + job % 10 + ((job % 100) / 10));
                break;
            case 5112:
                potential = true;
            case 5100:
            case 5110:
            case 5111:
                equip = (Equip) li.getEquipById(1098000 + job % 10 + ((job % 100) / 10));
                break;
            case 6001:
                equip = (Equip) li.getEquipById(1352600);
                break;
            case 6512:
                potential = true;
            case 6500:
            case 6510:
            case 6511:
                equip = (Equip) li.getEquipById(1352601 + job % 10 + ((job % 100) / 10));
                break;
            case 6000:
                equip = (Equip) li.getEquipById(1352500);
                break;
            case 6112:
                potential = true;
            case 6100:
            case 6110:
            case 6111:
                equip = (Equip) li.getEquipById(1352500 + job % 10 + ((job % 100) / 10));
                break;
            case 3002:
                equip = (Equip) li.getEquipById(1353000);
                break;
            case 3612:
                potential = true;
            case 3600:
            case 3610:
            case 3611:
                equip = (Equip) li.getEquipById(1353001 + job % 10 + ((job % 100) / 10));
                break;
            default:
                equip = null;
        }
        if (equip != null) {
            if (potential) {
                ItemPotentialProvider.addPotentialtoMonsterItemDrop(equip, false, false, ItemPotentialTierType.Rare_Hidden);
            }
            equip.setPosition((short) -10);
            equip.setQuantity((short) 1);
            equip.setGMLog("Job Advance");
            forceReAddItemNoUpdate(equip, MapleInventoryType.EQUIPPED);

            List<ModifyInventory> mod = new ArrayList<>();
            mod.add(new ModifyInventory(ModifyInventoryOperation.AddItem, equip));
            client.SendPacket(CWvsContext.inventoryOperation(true, mod));
            equipChanged();
        }
    }

    //Yeah we should probably just figure out the real update system using kmiPacketk
    public void checkZeroWeapon() {
        if (level < 100) {
            return;
        }
        int lazuli = getInventory(MapleInventoryType.EQUIPPED).getItem((short) -11).getItemId();
        int lapis = getInventory(MapleInventoryType.EQUIPPED).getItem((short) -10).getItemId();
        if (lazuli == getZeroWeapon(false) && lapis == getZeroWeapon(true)) {
            return;
        }
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        for (int i = 0; i < 2; i++) {
            int itemId = i == 0 ? getZeroWeapon(false) : getZeroWeapon(true);
            Equip equip = (Equip) ii.getEquipById(itemId);
            equip.setPosition((short) (i == 0 ? -11 : -10));
            equip.setQuantity((short) 1);
            equip.setGMLog("Zero Weapon Advance");
            forceReAddItemNoUpdate(equip, MapleInventoryType.EQUIPPED);

            List<ModifyInventory> mod = new ArrayList<>();
            mod.add(new ModifyInventory(ModifyInventoryOperation.AddItem, equip));
            client.SendPacket(CWvsContext.inventoryOperation(true, mod));
        }
        equipChanged();
    }

    //Yeah we should probably just figure out the real update system using kmiPacketk
    public int getZeroWeapon(boolean lapis) {
        if (level < 100) {
            return lapis ? 1562000 : 1572000;
        }
        int weapon = lapis ? 1562001 : 1572001;
        if (level >= 100 && level < 160) {
            weapon += (level % 100) / 10;
        } else if (level >= 160 && level < 170) {
            weapon += 5;
        } else if (level >= 170) {
            weapon += 6;
        }
        return weapon;
    }

    public void zeroChange(boolean beta) {
        setZeroBetaState(beta);
        getMap().broadcastMessage(this, CUserLocal.zeroTag(this), getPosition());
    }

    public void checkZeroTranscendent() {
        int skill = -1;
        switch (level / 10) {
            case 10:
                skill = 100000267;
                break;
            case 11:
                skill = 100001261;
                break;
            case 12:
                skill = 100001274;
                break;
            case 14:
                skill = 100001272;
                break;
            case 17:
                skill = 100001283;
                break;
            case 20:
                skill = 100001005;
                break;
            default:
                return;
        }
        Skill skil = SkillFactory.getSkill(skill);
        if (skil != null) {
            changeSkillLevelSkip(skil, 1, (byte) 1);
        }
    }

    public void femaleMihile() {
        setGender((byte) 1);
        setFace(21158);
        setHair(34773);
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        Equip equip;
        int[][] items = new int[][]{{1051355, -5}, {1072833, -6}};
        for (int[] item : items) {
            equip = (Equip) ii.getEquipById(item[0]);
            equip.setPosition((short) item[1]);
            equip.setQuantity((short) 1);
            equip.setGMLog("Female Mihile");
            forceReAddItemNoUpdate(equip, MapleInventoryType.EQUIPPED);

            List<ModifyInventory> mod = new ArrayList<>();
            mod.add(new ModifyInventory(ModifyInventoryOperation.AddItem, equip));
            client.SendPacket(CWvsContext.inventoryOperation(true, mod));
        }
        updateSingleStat(MapleStat.FACE, this.face);
        updateSingleStat(MapleStat.HAIR, this.hair);
        equipChanged();
    }

    public void forceUpdateItem(Item item) {
        final List<ModifyInventory> mods = new LinkedList<>();
        mods.add(new ModifyInventory(ModifyInventoryOperation.Remove, item));
        mods.add(new ModifyInventory(ModifyInventoryOperation.AddItem, item));
        client.SendPacket(CWvsContext.inventoryOperation(true, mods));
    }

    public int getCharListPosition() {
        return charListPosition;
    }

    public void setCharListPosition(int charListPosition) {
        this.charListPosition = charListPosition;
    }

    public void setZeroBetaState(boolean state) {
        isZeroBeta = state;
    }

    public void setAngelicDressupState(boolean state) {
        isAngelicDressup = state;
    }

    public boolean isZeroBetaState() {
        return isZeroBeta;
    }

    public boolean isAngelicDressupState() {
        return isAngelicDressup;
    }

    public void write(OutPacket packet) {
        client.SendPacket(packet);
    }

    public void setStopForceAtoms(int idx, int count, int objectid) {
        this.stopForceAtoms = new int[3];
        stopForceAtoms[0] = idx;
        stopForceAtoms[1] = count;
        stopForceAtoms[2] = objectid;
    }

    public int[] getStopForAtoms() {
        return this.stopForceAtoms;
    }

    public boolean isBurning() {
        return this.isBurning;
    }

    public boolean updateBurning(int characterId, boolean burning) {

        try (Connection con = Database.GetConnection()) {
            System.out.println(Thread.currentThread().getStackTrace()[2].getClassName() + "." + Thread.currentThread().getStackTrace()[2].getMethodName());
            try (PreparedStatement ps = con.prepareStatement("UPDATE characters SET isBurning = ? WHERE id = ?", RETURN_GENERATED_KEYS)) {
                ps.setBoolean(1, burning);
                ps.setInt(2, characterId);
                ps.executeUpdate();

            }
        } catch (SQLException ex) {
            LogHelper.SQL.get().info("[SQL] There was an issue with something from the database:\n", ex);
            return false;
        }
        this.isBurning = burning;
        return true;
    }

    public boolean bigBrother() {
        return bigBrother;
    }

    public void setBigBrother(boolean bigBrother) {
        this.bigBrother = bigBrother;
    }

    public int getPendingGuildId() {
        return this.pendingGuildId;
    }

    public void setPendingGuildId(int id) {
        this.pendingGuildId = id;
    }

    public boolean isClone() {
        return clone;
    }

    public void setClone(boolean c) {
        this.clone = c;
    }

    public WeakReference<User>[] getClones() {
        return clones;
    }

    public User cloneLooks() {
        MapleClient cloneclient = client; //needs to create a new MapleClient for this to work properly. Can't figure that out yet.

        final int minus = (getId() + Randomizer.nextInt(Integer.MAX_VALUE - getId())); // really randomize it, dont want it to fail

        User ret = new User(true);
        ret.id = minus;
        ret.client = cloneclient;
        ret.exp = 0;
        ret.meso = 0;
        ret.remainingAp = 0;
        ret.fame = 0;
        ret.accountid = client.getAccID();
        ret.anticheat = anticheat;
        ret.name = name;
        ret.level = level;
        ret.fame = fame;
        ret.job = job;
        ret.hair = hair;
        ret.face = face;
        ret.zeroBetaHair = zeroBetaHair;
        ret.zeroBetaFace = zeroBetaFace;
        ret.angelicDressupHair = angelicDressupHair;
        ret.angelicDressupFace = angelicDressupFace;
        ret.angelicDressupSuit = angelicDressupSuit;
        ret.faceMarking = faceMarking;
        ret.ears = ears;
        ret.tail = tail;
        ret.elf = elf;
        ret.skinColor = skinColor;
        ret.monsterbook = monsterbook;
        ret.mount = mount;
        ret.CRand = new PlayerRandomStream();
        ret.gmLevel = gmLevel;
        ret.gender = gender;
        ret.mapid = map.getId();
        ret.map = map;
        ret.setStance(getStance());
        ret.chair = chair;
        ret.itemEffect = itemEffect;
        ret.guildid = guildid;
        ret.currentrep = currentrep;
        ret.totalrep = totalrep;
        ret.stats = stats;
        ret.effects.putAll(effects);
        ret.dispelSummons();
        ret.guildrank = guildrank;
        ret.guildContribution = guildContribution;
        ret.allianceRank = allianceRank;
        ret.setPosition(getTruePosition());
        for (Item equip : getInventory(MapleInventoryType.EQUIPPED).newList()) {
            ret.getInventory(MapleInventoryType.EQUIPPED).addFromDB(equip.copy());
        }
        ret.skillMacros = skillMacros;
        ret.keylayout = keylayout;
        ret.questinfo = questinfo;
        ret.savedLocations = savedLocations;
        ret.wishlist = wishlist;
        ret.buddylist = buddylist;
        ret.keydown_skill = 0;
        ret.lastmonthfameids = lastmonthfameids;
        ret.lastfametime = lastfametime;
        ret.storage = storage;
        ret.cs = this.cs;
        ret.client.setAccountName(client.getAccountName());
        ret.nxcredit = nxcredit;
        ret.acash = acash;
        ret.maplepoints = maplepoints;
        ret.clone = true;
        ret.client.setChannel(this.client.getChannel());
        while (map.getCharacterById(ret.id) != null || client.getChannelServer().getPlayerStorage().getCharacterById(ret.id) != null) {
            ret.id++;
        }
        ret.client.setPlayer(ret);
        return ret;
    }

    public final void cloneLook() {
        if (clone || inPVP()) {
            return;
        }
        for (int i = 0; i < clones.length; i++) {
            if (clones[i].get() == null) {
                final User newp = cloneLooks();
                map.addPlayer(newp);
                map.broadcastMessage(CField.updateCharLook(newp, false));
                map.movePlayer(newp, getTruePosition());
                clones[i] = new WeakReference<>(newp);
                return;
            }
        }
    }

    public final void disposeClones() {
        numClones = 0;
        for (int i = 0; i < clones.length; i++) {
            if (clones[i].get() != null) {
                map.removePlayer(clones[i].get());
                if (clones[i].get().getClient() != null) {
                    clones[i].get().getClient().setPlayer(null);
                    clones[i].get().client = null;
                }
                clones[i] = new WeakReference<>(null);
                numClones++;
            }
        }
    }

    public final int getCloneSize() {
        int z = 0;
        for (int i = 0; i < clones.length; i++) {
            if (clones[i].get() != null) {
                z++;
            }
        }
        return z;
    }

    public void spawnClones() {
        if (!isGM()) { //removed tetris piece likely, expired or whatever
            numClones = (byte) (stats.hasClone ? 1 : 0);
        }
        for (int i = 0; i < numClones; i++) {
            cloneLook();
        }
        numClones = 0;
    }

    public byte getNumClones() {
        return numClones;
    }

    // <editor-fold defaultstate="collapsed" desc="Player conversation state"> 
    public MapleCharacterConversationType getConversation() {
        return conversationType;
    }

    public void setConversation(MapleCharacterConversationType conversationType) {
        this.conversationType = conversationType;

    }

    // 1 = NPC/ Quest, 2 = Donald, 3 = Hired Merch store, 4 = Storage
    public enum MapleCharacterConversationType {
        NPC_Or_Quest((byte) 1),
        Donald((byte) 2),
        HiredMerchant((byte) 3),
        Storage((byte) 4),
        None((byte) 0);
        private final byte type;

        private MapleCharacterConversationType(byte type) {
            this.type = type;
        }

        public byte getType() {
            return type;
        }

        public MapleCharacterConversationType getFromInt(byte type) {
            for (MapleCharacterConversationType t : values()) {
                if (type == t.getType()) {
                    return t;
                }
            }
            return None;
        }
    }
// </editor-fold> 

// <editor-fold defaultstate="collapsed" desc="Temporary values"> 
    /**
     * Gets the object to stores temporary data that does not necessarily require saving. This would be destoryed when the player logs off,
     * or change channel.
     *
     * @return
     */
    public CharacterTemporaryValues getTemporaryValues() {
        return this.temporaryValues;

    }

    /**
     * Stores temporary data that does not necessarily require saving. This would be destoryed when the player logs off, or change channel.
     */
    public static class CharacterTemporaryValues {

        private final Map<Long, Object> temporaryObjects = new HashMap<>();

        // can be anything, but I dont want to randomize to prevent people from storing multiple stats of items, and being able to go back at any time
        public static final long KEY_MEMORIAL_CUBE = 92024819L,
                KEY_BLACK_CUBE = 1215189L;

        /**
         * Gets the temporary object stored by key
         *
         * @param key
         * @return
         */
        public Object getTemporaryObject(long key) {
            Object obj = temporaryObjects.get(key);
            if (obj != null) {
                temporaryObjects.remove(key);
            }
            return obj;
        }

        /**
         *
         * @param randomKey
         * @param obj
         */
        public void setTemporaryObject(long randomKey, Object obj) {
            temporaryObjects.put(randomKey, obj);
        }

        /**
         * Stores a temporary value to the map.
         *
         * @param obj
         * @return long the key
         */
        public long setTemporaryObject(Object obj) {
            long randomKey = Randomizer.nextLong();

            temporaryObjects.put(randomKey, obj);

            return randomKey;
        }

    }
// </editor-fold> 

    public void SendPacket(OutPacket oPacket) {
        this.getClient().SendPacket(oPacket);
    }
}
