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
import client.MapleQuestStatus;
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
import server.maps.objects.MapleCharacter;
import server.maps.objects.MaplePet;
import server.maps.objects.MonsterFamiliar;
import server.quest.MapleQuest;
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
    public long magnusTime;

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

    public CharacterTransfer(final MapleCharacter chr) {
        this.characterid = chr.getId();
        this.accountid = chr.getAccountID();
        this.accountname = chr.getClient().getAccountName();
        this.channel = (byte) chr.getClient().getChannel();
        this.nxCredit = chr.getCSPoints(1);
        this.ACash = chr.getCSPoints(4);
        this.MaplePoints = chr.getCSPoints(2);
        // this.stolenSkills = chr.getStolenSkills();
        for(int i = 1; i <= 5; i++) {
            for(int j = 0; j < GameConstants.getNumSteal(i); j++) {
                this.aStealMemory[i][j] = chr.aStealMemory[i][j];
            }
        }
        for (Map.Entry<Integer, Integer> mStealSkill : chr.mStealSkillInfo.entrySet()) {
            this.mStealSkillInfo.put(mStealSkill.getKey(), mStealSkill.getValue());
        }
        this.vpoints = chr.getVPoints();
        this.name = chr.getName();
        this.fame = chr.getFame();
        this.gender = (byte) chr.getGender();
        this.level = chr.getLevel();
        this.str = chr.getStat().getStr();
        this.dex = chr.getStat().getDex();
        this.int_ = chr.getStat().getInt();
        this.luk = chr.getStat().getLuk();
        this.hp = chr.getStat().getHp();
        this.mp = chr.getStat().getMp();
        this.maxhp = chr.getStat().getMaxHp();
        this.maxmp = chr.getStat().getMaxMp();
        this.exp = chr.getExp();
        this.hpApUsed = chr.getHpApUsed();
        this.remainingAp = chr.getRemainingAp();
        this.remainingSp = chr.getRemainingSps();
        this.remainingHSp = chr.getRemainingHSps();
        this.meso = chr.getMeso();
        this.pvpExp = chr.getTotalBattleExp();
        this.pvpPoints = chr.getBattlePoints();
        /*
         * Start of Custom Feature
         */
        this.reborns = chr.getReborns();
        this.apstorage = chr.getAPS();
        /*
         * End of Custom Feature
         */
        this.skinColor = chr.getSkinColor();
        this.job = chr.getJob();
        this.hair = chr.getHair();
        this.face = chr.getFace();
        this.zeroBetaHair = chr.getZeroBetaHair();
        this.zeroBetaFace = chr.getZeroBetaFace();
        this.angelicDressupHair = chr.getAngelicDressupHair();
        this.angelicDressupFace = chr.getAngelicDressupFace();
        this.angelicDressupSuit = chr.getAngelicDressupSuit();
        this.faceMarking = chr.getFaceMarking();
        this.ears = chr.getEars();
        this.tail = chr.getTail();
        this.elf = chr.getElf();
        this.mapid = chr.getMapId();
        this.initialSpawnPoint = chr.getInitialSpawnpoint();
        this.marriageId = chr.getMarriageId();
        this.marriage = chr.getMarriage();
        this.world = chr.getWorld();
        this.guildid = chr.getGuildId();
        this.guildrank = (byte) chr.getGuildRank();
        this.guildContribution = chr.getGuildContribution();
        this.alliancerank = (byte) chr.getAllianceRank();
        this.gmLevel = (byte) chr.getGMLevel();
        this.points = chr.getPoints();
        this.dpoints = chr.getDPoints();
        this.epoints = chr.getEPoints();
        this.fairyExp = chr.getFairyExp();
        this.cardStack = chr.getCardStack();
        this.petStore = chr.getPetStores();
        this.subcategory = chr.getSubcategory();
        this.imps = chr.getImps();
        this.fatigue = (short) chr.getFatigue();
        this.currentrep = chr.getCurrentRep();
        this.totalrep = chr.getTotalRep();
        this.familyid = chr.getFamilyId();
        this.totalWins = chr.getTotalWins();
        this.totalLosses = chr.getTotalLosses();
        this.seniorid = chr.getSeniorId();
        this.junior1 = chr.getJunior1();
        this.junior2 = chr.getJunior2();
        this.battleshipHP = chr.currentBattleshipHP();
        this.gachexp = chr.getGachExp();
        this.familiars = chr.getFamiliars();
        chr.getCheatTracker().dispose();
        this.anticheat = chr.getCheatTracker();
        this.tempIP = chr.getClient().getTempIP();
        this.rebuy = chr.getShopRepurchases();
        boolean uneq = false;
        for (int i = 0; i < this.petStore.length; i++) {
            final MaplePet pet = chr.getPet(i);
            if (this.petStore[i] == 0) {
                this.petStore[i] = (byte) -1;
            }
            if (pet != null) {
                uneq = true;
                this.petStore[i] = (byte) Math.max(this.petStore[i], pet.getItem().getPosition());
            }

        }
        if (uneq) {
            chr.unequipAllPets();
        }

        for (MapleTraitType t : MapleTraitType.values()) {
            this.traits.put(t, chr.getTrait(t).getTotalExp());
        }
        for (final BuddylistEntry qs : chr.getBuddylist().getBuddies()) {
            this.buddies.put(new CharacterNameAndId(qs.getCharacterId(), qs.getName()), new BuddyTransfer(qs.isPending(), qs.getNickname(), qs.isAccountFriend(), qs.getMemo()));
        }
        for (final Entry<ReportType, Integer> ss : chr.getReports().entrySet()) {
            this.reports.put(ss.getKey().i, ss.getValue());
        }
        this.buddysize = chr.getBuddyCapacity();

        this.partyid = chr.getParty() == null ? -1 : chr.getParty().getId();

        if (chr.getMessenger() != null) {
            this.messengerid = chr.getMessenger().getId();
        } else {
            this.messengerid = 0;
        }

        this.finishedAchievements = chr.getFinishedAchievements();
        this.InfoQuest = chr.getInfoQuestMap();

        for (Entry<MapleQuest, MapleQuestStatus> qs : chr.getQuestMap().entrySet()) {
            this.Quest.put(qs.getKey().getId(), qs.getValue());
        }

        this.mbook = chr.getMonsterBook().getCards();
        this.inventorys = chr.getInventorys();

        for (Entry<Skill, SkillEntry> qs : chr.getSkills().entrySet()) {
            this.Skills.put(qs.getKey().getId(), qs.getValue());
        }
        for (Entry<Integer, CardData> ii : chr.getCharacterCard().getCards().entrySet()) {
            this.cardsInfo.put(ii.getKey(), ii.getValue());
        }

        this.BlessOfFairy = chr.getBlessOfFairyOrigin();
        this.BlessOfEmpress = chr.getBlessOfEmpressOrigin();
        this.chalkboard = chr.getChalkboard();
        this.skillmacro = chr.getMacros();
        this.keymap = chr.getKeyLayout().getLayout();
        this.savedlocation = chr.getSavedLocations();
        this.wishlist = chr.getWishlist();
        this.rocks = chr.getRocks();
        this.regrocks = chr.getRegRocks();
        this.hyperrocks = chr.getHyperRocks();
        this.famedcharacters = chr.getFamedCharacters();
        this.battledaccs = chr.getBattledCharacters();
        this.lastfametime = chr.getLastFameTime();
        this.storage = chr.getStorage();
        this.cs = chr.getCashInventory();
        this.extendedSlots = chr.getExtendedSlots();
        this.honourexp = chr.getHonourExp();
        this.honourlevel = chr.getHonorLevel();
        this.innerSkills = chr.getInnerSkills();
        this.azwanShopList = chr.getAzwanShop();
        final MapleMount mount = chr.getMount();
        this.mount_itemid = mount.getItemId();
        this.mount_Fatigue = mount.getFatigue();
        this.mount_level = mount.getLevel();
        this.mount_exp = mount.getExp();
        this.isZeroBeta = chr.isZeroBetaState();
        this.isAngelicDressup = chr.isAngelicDressupState();
        this.isBurning = chr.isBurning();
        this.aVMatrixRecord = chr.aVMatrixRecord;
        /*Boss Timed Variables*/
        this.magnusTime = chr.getLastMagnusTime();
        TranferTime = System.currentTimeMillis();
    }
}
