package handling.world;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import client.CardData;
import client.MapleMarriage;
import client.QuestStatus;
import client.MapleTrait.MapleTraitType;
import client.Skill;
import client.SkillEntry;
import client.anticheat.ReportType;
import client.buddy.BuddyTransfer;
import client.buddy.BuddylistEntry;
import client.buddy.CharacterNameAndId;
import client.inventory.MapleImp;
import client.inventory.MapleMount;
import client.inventory.ShopRepurchase;
import constants.GameConstants;
import java.util.HashMap;
import server.maps.objects.User;
import server.maps.objects.Pet;
import server.maps.objects.MonsterFamiliar;
import server.quest.Quest;
import server.skills.VMatrixRecord;
import tools.Pair;

public class CharacterTransfer {

    public boolean isZeroBeta, isAngelicDressup, isBurning;
    public int characterid, accountid, fame, pvpExp, pvpPoints, hair, face,
            faceMarking, ears, tail, elf, mapid, honourexp, honourlevel, guildid,
            partyid, messengerid, ACash, nxCredit, MaplePoints,
            mount_itemid, mount_exp, points, vpoints, dpoints, epoints, marriageId, maxhp, maxmp, hp, mp,
            familyid, seniorid, junior1, junior2, currentrep, totalrep, battleshipHP, gachexp, guildContribution, totalWins, totalLosses,
            zeroBetaHair, zeroBetaFace, angelicDressupHair, angelicDressupFace, angelicDressupSuit;
    public byte channel, gender, gmLevel, guildrank, alliancerank, clonez,
            fairyExp, cardStack, buddysize, world, initialSpawnPoint, skinColor, mount_level, mount_Fatigue, subcategory;
    public long lastfametime, TranferTime, exp, meso;
    public String name, accountname, BlessOfFairy, BlessOfEmpress, chalkboard, tempIP;
    public short level, hpApUsed, job, fatigue;
    public Object inventorys, skillmacro, storage, cs, anticheat, innerSkills, azwanShopList;
    public int[] savedlocation, wishlist, rocks, remainingSp, remainingHSp, regrocks, hyperrocks;
    public byte[] petStore;
    public MapleImp[] imps;
    public Map<Integer, Integer> mbook;
    public Map<Byte, Integer> reports = new LinkedHashMap<>();
    //public List<Pair<Integer, Boolean>> stolenSkills;
    public int[][] aStealMemory = new int[6][4];
    public Map<Integer, Integer> mStealSkillInfo = new HashMap<>();
    public Map<Integer, Pair<Byte, Integer>> keymap;
    public Map<Integer, MonsterFamiliar> familiars;
    public List<Integer> finishedAchievements = null, famedcharacters = null, battledaccs = null, extendedSlots = null;
    public List<ShopRepurchase> rebuy = null;
    public final Map<MapleTraitType, Integer> traits = new EnumMap<>(MapleTraitType.class);
    public final Map<CharacterNameAndId, BuddyTransfer> buddies = new LinkedHashMap<>();
    public final Map<Integer, Object> Quest = new LinkedHashMap<>(); // Questid instead of MapleQuest, as it's huge. Cant be transporting MapleQuest.java
    public Map<Integer, String> InfoQuest;
    public final Map<Integer, SkillEntry> Skills = new LinkedHashMap<>(); // Skillid instead of Skill.java, as it's huge. Cant be transporting Skill.java and MapleStatEffect.java
    public final Map<Integer, CardData> cardsInfo = new LinkedHashMap<>();
    public MapleMarriage marriage;
    public List<VMatrixRecord> aVMatrixRecord = new ArrayList<>();

    /*Start of Custom Feature*/
 /*All custom shit declare here*/
    public int reborns, apstorage, str, dex, int_, luk, remainingAp;
    public boolean bigBrother;

    /*Boss Timed Variables*/
    public long tHila, tZakum, tHorntail, tRanmaru, tCrimsonQueen, tPierre, tVonBon, tVellum, tLotus, tUrsus, tArkarium, tCygnus, tMagnus, tLucid;

    /*End of Custom Feature*/
    public CharacterTransfer() {
        finishedAchievements = new ArrayList<>();
        famedcharacters = new ArrayList<>();
        battledaccs = new ArrayList<>();
        extendedSlots = new ArrayList<>();
        rebuy = new ArrayList<>();
        InfoQuest = new LinkedHashMap<>();
        keymap = new LinkedHashMap<>();
        familiars = new LinkedHashMap<>();
        mbook = new LinkedHashMap<>();
    }

    public CharacterTransfer(final User pPlayer) {
        this.characterid = pPlayer.getId();
        this.accountid = pPlayer.getAccountID();
        this.accountname = pPlayer.getClient().getAccountName();
        this.channel = (byte) pPlayer.getClient().getChannel();
        this.nxCredit = pPlayer.getCSPoints(1);
        this.ACash = pPlayer.getCSPoints(4);
        this.MaplePoints = pPlayer.getCSPoints(2);
        // this.stolenSkills = chr.getStolenSkills();
        for (int i = 1; i <= 5; i++) {
            for (int j = 0; j < GameConstants.getNumSteal(i); j++) {
                this.aStealMemory[i][j] = pPlayer.aStealMemory[i][j];
            }
        }
        for (Map.Entry<Integer, Integer> mStealSkill : pPlayer.mStealSkillInfo.entrySet()) {
            this.mStealSkillInfo.put(mStealSkill.getKey(), mStealSkill.getValue());
        }
        this.vpoints = pPlayer.getVPoints();
        this.name = pPlayer.getName();
        this.fame = pPlayer.getFame();
        this.gender = (byte) pPlayer.getGender();
        this.level = pPlayer.getLevel();
        this.str = pPlayer.getStat().getStr();
        this.dex = pPlayer.getStat().getDex();
        this.int_ = pPlayer.getStat().getInt();
        this.luk = pPlayer.getStat().getLuk();
        this.hp = pPlayer.getStat().getHp();
        this.mp = pPlayer.getStat().getMp();
        this.maxhp = pPlayer.getStat().getMaxHp();
        this.maxmp = pPlayer.getStat().getMaxMp();
        this.exp = pPlayer.getExp();
        this.hpApUsed = pPlayer.getHpApUsed();
        this.remainingAp = pPlayer.getRemainingAp();
        this.remainingSp = pPlayer.getRemainingSps();
        this.remainingHSp = pPlayer.getRemainingHSps();
        this.meso = pPlayer.getMeso();
        this.pvpExp = pPlayer.getTotalBattleExp();
        this.pvpPoints = pPlayer.getBattlePoints();
        /*
         * Start of Custom Feature
         */
        this.reborns = pPlayer.getReborns();
        this.apstorage = pPlayer.getAPS();
        /*
         * End of Custom Feature
         */
        this.skinColor = pPlayer.getSkinColor();
        this.job = pPlayer.getJob();
        this.hair = pPlayer.getHair();
        this.face = pPlayer.getFace();
        this.zeroBetaHair = pPlayer.getZeroBetaHair();
        this.zeroBetaFace = pPlayer.getZeroBetaFace();
        this.angelicDressupHair = pPlayer.getAngelicDressupHair();
        this.angelicDressupFace = pPlayer.getAngelicDressupFace();
        this.angelicDressupSuit = pPlayer.getAngelicDressupSuit();
        this.faceMarking = pPlayer.getFaceMarking();
        this.ears = pPlayer.getEars();
        this.tail = pPlayer.getTail();
        this.elf = pPlayer.getElf();
        this.mapid = pPlayer.getMapId();
        this.initialSpawnPoint = pPlayer.getInitialSpawnpoint();
        this.marriageId = pPlayer.getMarriageId();
        this.marriage = pPlayer.getMarriage();
        this.world = pPlayer.getWorld();
        this.guildid = pPlayer.getGuildId();
        this.guildrank = (byte) pPlayer.getGuildRank();
        this.guildContribution = pPlayer.getGuildContribution();
        this.alliancerank = (byte) pPlayer.getAllianceRank();
        this.gmLevel = (byte) pPlayer.getGMLevel();
        this.points = pPlayer.getPoints();
        this.dpoints = pPlayer.getDPoints();
        this.epoints = pPlayer.getEPoints();
        this.fairyExp = pPlayer.getFairyExp();
        this.cardStack = pPlayer.getCardStack();
        this.petStore = pPlayer.getPetStores();
        this.subcategory = pPlayer.getSubcategory();
        this.imps = pPlayer.getImps();
        this.fatigue = (short) pPlayer.getFatigue();
        this.currentrep = pPlayer.getCurrentRep();
        this.totalrep = pPlayer.getTotalRep();
        this.familyid = pPlayer.getFamilyId();
        this.totalWins = pPlayer.getTotalWins();
        this.totalLosses = pPlayer.getTotalLosses();
        this.seniorid = pPlayer.getSeniorId();
        this.junior1 = pPlayer.getJunior1();
        this.junior2 = pPlayer.getJunior2();
        this.battleshipHP = pPlayer.currentBattleshipHP();
        this.gachexp = pPlayer.getGachExp();
        this.familiars = pPlayer.getFamiliars();
        pPlayer.getCheatTracker().dispose();
        this.anticheat = pPlayer.getCheatTracker();
        this.tempIP = pPlayer.getClient().getTempIP();
        this.rebuy = pPlayer.getShopRepurchases();
        boolean uneq = false;
        for (int i = 0; i < this.petStore.length; i++) {
            final Pet pet = pPlayer.getPet(i);
            if (this.petStore[i] == 0) {
                this.petStore[i] = (byte) -1;
            }
            if (pet != null) {
                uneq = true;
                this.petStore[i] = (byte) Math.max(this.petStore[i], pet.getItem().getPosition());
            }

        }
        if (uneq) {
            pPlayer.unequipAllPets();
        }

        for (MapleTraitType t : MapleTraitType.values()) {
            this.traits.put(t, pPlayer.getTrait(t).getTotalExp());
        }
        for (final BuddylistEntry qs : pPlayer.getBuddylist().getBuddies()) {
            this.buddies.put(new CharacterNameAndId(qs.getCharacterId(), qs.getName()), new BuddyTransfer(qs.isPending(), qs.getNickname(), qs.isAccountFriend(), qs.getMemo()));
        }
        for (final Entry<ReportType, Integer> ss : pPlayer.getReports().entrySet()) {
            this.reports.put(ss.getKey().i, ss.getValue());
        }
        this.buddysize = pPlayer.getBuddyCapacity();

        this.partyid = pPlayer.getParty() == null ? -1 : pPlayer.getParty().getId();

        if (pPlayer.getMessenger() != null) {
            this.messengerid = pPlayer.getMessenger().getId();
        } else {
            this.messengerid = 0;
        }

        this.finishedAchievements = pPlayer.getFinishedAchievements();
        this.InfoQuest = pPlayer.getInfoQuestMap();

        for (Entry<Quest, QuestStatus> qs : pPlayer.getQuestMap().entrySet()) {
            this.Quest.put(qs.getKey().getId(), qs.getValue());
        }

        this.mbook = pPlayer.getMonsterBook().getCards();
        this.inventorys = pPlayer.getInventorys();

        for (Entry<Skill, SkillEntry> qs : pPlayer.getSkills().entrySet()) {
            this.Skills.put(qs.getKey().getId(), qs.getValue());
        }
        for (Entry<Integer, CardData> ii : pPlayer.getCharacterCard().getCards().entrySet()) {
            this.cardsInfo.put(ii.getKey(), ii.getValue());
        }

        this.BlessOfFairy = pPlayer.getBlessOfFairyOrigin();
        this.BlessOfEmpress = pPlayer.getBlessOfEmpressOrigin();
        this.chalkboard = pPlayer.getChalkboard();
        this.skillmacro = pPlayer.getMacros();
        this.keymap = pPlayer.getKeyLayout().getLayout();
        this.savedlocation = pPlayer.getSavedLocations();
        this.wishlist = pPlayer.getWishlist();
        this.rocks = pPlayer.getRocks();
        this.regrocks = pPlayer.getRegRocks();
        this.hyperrocks = pPlayer.getHyperRocks();
        this.famedcharacters = pPlayer.getFamedCharacters();
        this.battledaccs = pPlayer.getBattledCharacters();
        this.lastfametime = pPlayer.getLastFameTime();
        this.storage = pPlayer.getStorage();
        this.cs = pPlayer.getCashInventory();
        this.extendedSlots = pPlayer.getExtendedSlots();
        this.honourexp = pPlayer.getHonourExp();
        this.honourlevel = pPlayer.getHonorLevel();
        this.innerSkills = pPlayer.getInnerSkills();
        this.azwanShopList = pPlayer.getAzwanShop();
        final MapleMount mount = pPlayer.getMount();
        this.mount_itemid = mount.getItemId();
        this.mount_Fatigue = mount.getFatigue();
        this.mount_level = mount.getLevel();
        this.mount_exp = mount.getExp();
        this.isZeroBeta = pPlayer.isZeroBetaState();
        this.isAngelicDressup = pPlayer.isAngelicDressupState();
        this.isBurning = pPlayer.isBurning();
        this.aVMatrixRecord = pPlayer.aVMatrixRecord;
        
        TranferTime = System.currentTimeMillis();
        
        /*Boss Timed Variables*/
        tHila = pPlayer.tHila;
        tZakum = pPlayer.tZakum;
        tHorntail = pPlayer.tHorntail;
        tRanmaru = pPlayer.tRanmaru;
        tCrimsonQueen = pPlayer.tCrimsonQueen;
        tPierre = pPlayer.tPierre;
        tVonBon = pPlayer.tVonBon;
        tVellum = pPlayer.tVellum;
        tLotus = pPlayer.tLotus;
        tUrsus = pPlayer.tUrsus;
        tArkarium = pPlayer.tArkarium;
        tCygnus = pPlayer.tCygnus;
        tMagnus = pPlayer.tMagnus;
        tLucid = pPlayer.tLucid;
    }
}
