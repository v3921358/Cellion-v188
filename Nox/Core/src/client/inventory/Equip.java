package client.inventory;

import constants.GameConstants;
import constants.InventoryConstants;
import server.MapleItemInformationProvider;
import server.maps.objects.MapleAndroid;
import server.potentials.ItemPotentialTierType;

public class Equip extends Item {

    public static enum ScrollResult {

        SUCCESS,
        FAIL,
        CURSE
    }

    private static final long ARMOR_RATIO = 350000L;
    private static final long WEAPON_RATIO = 700000L;
    private byte enhance = 0;
    private short reqLevel = 0, spellTrace = 0;

    //charm: -1 = has not been initialized yet, 0 = already been worn, >0 = has teh charm exp
    private byte upgradeSlots = 0, level = 0, vicioushammer = 0, yggdrasilWisdom = 0, bossDamage = 0, ignorePDR = 0, totalDamage = 0, allStat = 0, karmaCount = -1;
    private short hpR = 0, mpR = 0; // HP and MP rate is handled differently. It only exist on the item, and cannot be increased. Thus it is not on SQL database
    private short str = 0, dex = 0, _int = 0, luk = 0, hp = 0, mp = 0, watk = 0, matk = 0, wdef = 0, mdef = 0,
            acc = 0, avoid = 0, hands = 0, speed = 0, jump = 0, charmExp = 0, pvpDamage = 0, soulOptionId = 0, soulSocketId = 0, soulOption = 0;

    // Potentials
    private ItemPotentialTierType potentialState = ItemPotentialTierType.None;
    private ItemPotentialTierType potentialBonusState = ItemPotentialTierType.None;
    private int potential1 = 0, potential2 = 0, potential3 = 0,
            bonuspotential1 = 0, bonuspotential2 = 0, bonuspotential3 = 0, fusionAnvil = 0, socket1 = -1, socket2 = -1, socket3 = -1, expBonus = -1,
            cashGrade = 0;
    // Others
    private int durability = -1, incSkill = -1;
    private int[] cashStats = new int[3];
    private long itemEXP = 0;
    private boolean finalStrike = false;
    private MapleRing ring = null;
    private MapleAndroid android = null;

    private boolean betaShare;
    private byte enchantFail = 0;
    private int starFlag = 0;
    private short nArcane, nArcaneMaxLevel;
    private int nArcaneExp;

    public Equip(int id, short position, byte flag) {
        super(id, position, (short) 1, flag);
    }

    public Equip(int id, short position, int uniqueid, short flag) {
        super(id, position, (short) 1, flag, uniqueid);
    }

    @Override
    public Item copy() {
        Equip ret = new Equip(getItemId(), getPosition(), getUniqueId(), getFlag());
        ret.str = str;
        ret.dex = dex;
        ret._int = _int;
        ret.luk = luk;
        ret.hp = hp;
        ret.mp = mp;
        ret.matk = matk;
        ret.mdef = mdef;
        ret.watk = watk;
        ret.wdef = wdef;
        ret.acc = acc;
        ret.avoid = avoid;
        ret.hands = hands;
        ret.speed = speed;
        ret.jump = jump;
        ret.enhance = enhance;
        ret.upgradeSlots = upgradeSlots;
        ret.level = level;
        ret.itemEXP = itemEXP;
        ret.durability = durability;
        ret.hpR = hpR;
        ret.mpR = mpR;

        // Potentials
        ret.potentialState = potentialState;
        ret.potentialBonusState = potentialBonusState;

        ret.enhance = enhance;
        ret.potential1 = potential1;
        ret.potential2 = potential2;
        ret.potential3 = potential3;
        ret.bonuspotential1 = bonuspotential1;
        ret.bonuspotential2 = bonuspotential2;
        ret.bonuspotential3 = bonuspotential3;

        ret.fusionAnvil = fusionAnvil;
        ret.socket1 = socket1;
        ret.socket2 = socket2;
        ret.socket3 = socket3;
        ret.charmExp = charmExp;
        ret.pvpDamage = pvpDamage;
        ret.incSkill = incSkill;
        ret.spellTrace = spellTrace;
        ret.reqLevel = reqLevel;
        ret.yggdrasilWisdom = yggdrasilWisdom;
        ret.finalStrike = finalStrike;
        ret.bossDamage = bossDamage;
        ret.ignorePDR = ignorePDR;
        ret.totalDamage = totalDamage;
        ret.allStat = allStat;
        ret.karmaCount = karmaCount;
        ret.setGiftFrom(getGiftFrom());
        ret.setOwner(getOwner());
        ret.setQuantity(getQuantity());
        ret.setExpiration(getExpiration());
        ret.soulOptionId = soulOptionId;
        ret.soulSocketId = soulSocketId;
        ret.soulOption = soulOption;
        ret.expBonus = expBonus;
        ret.cashGrade = cashGrade;
        ret.cashStats = cashStats;
        ret.betaShare = betaShare;
        ret.enchantFail = enchantFail;
        ret.starFlag = starFlag;
        ret.nArcane = nArcane;
        ret.nArcaneExp = nArcaneExp;
        ret.nArcaneMaxLevel = nArcaneMaxLevel;
        return ret;
    }

    @Override
    public ItemType getType() {
        return ItemType.Equipment;
    }

    public byte getUpgradeSlots() {
        return upgradeSlots;
    }

    public short getStr() {
        return str;
    }

    public short getDex() {
        return dex;
    }

    public short getInt() {
        return _int;
    }

    public short getLuk() {
        return luk;
    }

    public short getHp() {
        return hp;
    }

    public short getMp() {
        return mp;
    }

    public short getWatk() {
        return watk;
    }

    public short getMatk() {
        return matk;
    }

    public short getWdef() {
        return wdef;
    }

    public short getMdef() {
        return mdef;
    }

    public short getAcc() {
        return acc;
    }

    public short getAvoid() {
        return avoid;
    }

    public short getHands() {
        return hands;
    }

    public short getSpeed() {
        return speed;
    }

    public short getJump() {
        return jump;
    }

    public void setStr(short str) {
        if (str < 0) {
            str = 0;
        }
        this.str = str;
    }

    public void setDex(short dex) {
        if (dex < 0) {
            dex = 0;
        }
        this.dex = dex;
    }

    public void setInt(short _int) {
        if (_int < 0) {
            _int = 0;
        }
        this._int = _int;
    }

    public void setLuk(short luk) {
        if (luk < 0) {
            luk = 0;
        }
        this.luk = luk;
    }

    public void setHp(short hp) {
        if (hp < 0) {
            hp = 0;
        }
        this.hp = hp;
    }

    public void setMp(short mp) {
        if (mp < 0) {
            mp = 0;
        }
        this.mp = mp;
    }

    public void setWatk(short watk) {
        if (watk < 0) {
            watk = 0;
        }
        this.watk = watk;
    }

    public void setMatk(short matk) {
        if (matk < 0) {
            matk = 0;
        }
        this.matk = matk;
    }

    public void setWdef(short wdef) {
        if (wdef < 0) {
            wdef = 0;
        }
        this.wdef = wdef;
    }

    public void setMdef(short mdef) {
        if (mdef < 0) {
            mdef = 0;
        }
        this.mdef = mdef;
    }

    public void setAcc(short acc) {
        if (acc < 0) {
            acc = 0;
        }
        this.acc = acc;
    }

    public void setAvoid(short avoid) {
        if (avoid < 0) {
            avoid = 0;
        }
        this.avoid = avoid;
    }

    public void setHands(short hands) {
        if (hands < 0) {
            hands = 0;
        }
        this.hands = hands;
    }

    public void setSpeed(short speed) {
        if (speed < 0) {
            speed = 0;
        }
        this.speed = speed;
    }

    public void setJump(short jump) {
        if (jump < 0) {
            jump = 0;
        }
        this.jump = jump;
    }

    public void setUpgradeSlots(byte upgradeSlots) {
        this.upgradeSlots = upgradeSlots;
    }

    public byte getLevel() {
        return level;
    }

    public void setLevel(byte level) {
        this.level = level;
    }

    public byte getViciousHammer() {
        return vicioushammer;
    }

    public void setViciousHammer(byte ham) {
        vicioushammer = ham;
    }

    public long getItemEXP() {
        return itemEXP;
    }

    public void setItemEXP(long itemEXP) {
        if (itemEXP < 0) {
            itemEXP = 0;
        }
        this.itemEXP = itemEXP;
    }

    public long getEquipExp() {
        if (itemEXP <= 0) {
            return 0;
        }
        if (InventoryConstants.isWeapon(getItemId())) {
            return getItemEXP() / WEAPON_RATIO;
        } else {
            return itemEXP / ARMOR_RATIO;
        }
    }

    public long getEquipExpForLevel() {
        if (getEquipExp() <= 0) {
            return 0;
        }
        long expz = getEquipExp();
        for (int i = getBaseLevel(); i <= GameConstants.getMaxLevel(getItemId()); i++) {
            if (expz >= GameConstants.getExpForLevel(i, getItemId())) {
                expz -= GameConstants.getExpForLevel(i, getItemId());
            } else { //for 0, dont continue;
                break;
            }
        }
        return expz;
    }

    public long getExpPercentage() {
        if (getEquipLevel() < getBaseLevel() || getEquipLevel() > GameConstants.getMaxLevel(getItemId()) || GameConstants.getExpForLevel(getEquipLevel(), getItemId()) <= 0) {
            return 0;
        }
        return getEquipExpForLevel() * 100 / GameConstants.getExpForLevel(getEquipLevel(), getItemId());
    }

    public int getEquipLevel() {
        if (GameConstants.getMaxLevel(getItemId()) <= 0) {
            return 0;
        } else if (getEquipExp() <= 0) {
            return getBaseLevel();
        }
        int levelz = getBaseLevel();
        long expz = getEquipExp();
        for (int i = levelz; (GameConstants.getStatFromWeapon(getItemId()) == null ? (i <= GameConstants.getMaxLevel(getItemId())) : (i < GameConstants.getMaxLevel(getItemId()))); i++) {
            if (expz >= GameConstants.getExpForLevel(i, getItemId())) {
                levelz++;
                expz -= GameConstants.getExpForLevel(i, getItemId());
            } else { //for 0, dont continue;
                break;
            }
        }
        return levelz;
    }

    public int getBaseLevel() {
        return (GameConstants.getStatFromWeapon(getItemId()) == null ? 1 : 0);
    }

    @Override
    public void setQuantity(short quantity) {
        if (quantity < 0 || quantity > 1) {
            throw new RuntimeException("Setting the quantity to " + quantity + " on an equip (itemid: " + getItemId() + ")");
        }
        super.setQuantity(quantity);
    }

    public int getDurability() {
        return durability;
    }

    public void setDurability(final int dur) {
        durability = dur;
    }

    /**
     * Sets the HP rate of this equipment during server startup initialization. HP rate cannot be increased via scrolls, it only exist on
     * the equipment. Thus this isn;t on the SQL database entry
     *
     * @param hpR
     */
    public void setMHPr(short hpR) {
        this.hpR = hpR;
    }

    /**
     * Sets the MP rate of this equipment during server startup initialization MP rate cannot be increased via scrolls, it only exist on the
     * equipment. Thus this isn;t on the SQL database entry
     *
     * @param mpR
     */
    public void setMMPr(short mpR) {
        this.mpR = mpR;
    }

    /**
     * Gets the HP rate increase of this equipment [eg: Chaos horntail necklace]
     *
     * @return
     */
    public short getMHPr() {
        return hpR;
    }

    /**
     * Gets the MP rate increase of this equipment [eg: Chaos horntail necklace]
     *
     * @return
     */
    public short getMMPr() {
        return mpR;
    }

    public short getSpellTrace() {
        return spellTrace;
    }

    public void setSpellTrace(short spellTrace) {
        this.spellTrace = spellTrace;
    }

    public byte getReqLevel() {
        // Hack fix for required level since it's apparently very broken.
        int nRequiredLevel = (int) (Math.floor((reqLevel / 100) * 0.8633) * 100);
        if (nRequiredLevel > 250) {
            nRequiredLevel = 200;
        }

        return (byte) nRequiredLevel;
    }

    public void setReqLevel(short reqLevel) {
        short nRequiredLevel = (short) (reqLevel & 0xFF);
        if (nRequiredLevel > 250) {
            nRequiredLevel = 200;
        }
        this.reqLevel = nRequiredLevel;
    }

    public byte getYggdrasilWisdom() {
        return yggdrasilWisdom;
    }

    public void setYggdrasilWisdom(byte yggdrasilWisdom) {
        this.yggdrasilWisdom = yggdrasilWisdom;
    }

    public boolean getFinalStrike() {
        return finalStrike;
    }

    public void setFinalStrike(boolean finalStrike) {
        this.finalStrike = finalStrike;
    }

    public byte getBossDamage() {
        return bossDamage;
    }

    public void setBossDamage(byte bossDamage) {
        this.bossDamage = bossDamage;
    }

    public byte getIgnorePDR() {
        return ignorePDR;
    }

    public void setIgnorePDR(byte ignorePDR) {
        this.ignorePDR = ignorePDR;
    }

    public byte getTotalDamage() {
        return totalDamage;
    }

    public void setTotalDamage(byte totalDamage) {
        this.totalDamage = totalDamage;
    }

    public byte getAllStat() {
        return allStat;
    }

    public void setAllStat(byte allStat) {
        this.allStat = allStat;
    }

    public byte getKarmaCount() {
        return karmaCount;
    }

    public void setKarmaCount(byte karmaCount) {
        this.karmaCount = karmaCount;
    }

    public byte getEnhance() {
        return enhance;
    }

    public void setEnhance(final byte en) {
        enhance = en;
    }

    // <editor-fold defaultstate="visible" desc="Item potentials"> 
    /**
     * Gets the potential tier state of this equipment (None, rare, epic, unique, legendary)
     *
     * @return
     */
    public ItemPotentialTierType getPotentialTier() {
        return potentialState;
    }

    public void setPotentialTier(ItemPotentialTierType potentialState) {
        this.potentialState = potentialState;
    }

    /**
     * Gets the potential bonus tier state of this equipment (None, rare, epic, unique, legendary)
     *
     * @return
     */
    public ItemPotentialTierType getPotentialBonusTier() {
        return potentialBonusState;
    }

    public void setPotentialBonusTier(ItemPotentialTierType potentialBonusState) {
        this.potentialBonusState = potentialBonusState;
    }

    public int getPotential1() {
        return potential1;
    }

    public void setPotential1(final int en) {
        potential1 = en;
    }

    public int getPotential2() {
        return potential2;
    }

    public void setPotential2(final int en) {
        potential2 = en;
    }

    public int getPotential3() {
        return potential3;
    }

    public void setPotential3(final int en) {
        potential3 = en;
    }

    public int getBonusPotential1() {
        return bonuspotential1;
    }

    public void setBonusPotential1(final int en) {
        bonuspotential1 = en;
    }

    public int getBonusPotential2() {
        return bonuspotential2;
    }

    public void setBonusPotential2(final int en) {
        bonuspotential2 = en;
    }

    public int getBonusPotential3() {
        return bonuspotential3;
    }

    public void setBonusPotential3(final int en) {
        bonuspotential3 = en;
    }
    // </editor-fold>

    public int getFusionAnvil() {
        return fusionAnvil;
    }

    public void setFusionAnvil(final int en) {
        fusionAnvil = en;
    }

    public int getIncSkill() {
        return incSkill;
    }

    public void setIncSkill(int inc) {
        incSkill = inc;
    }

    public short getCharmEXP() {
        return charmExp;
    }

    public short getPVPDamage() {
        return pvpDamage;
    }

    public void setCharmEXP(short s) {
        charmExp = s;
        short flags = getFlag();
        setFlag(flags |= ItemFlag.CHARM_EQUIPPED.getValue());
    }

    public void setPVPDamage(short p) {
        pvpDamage = p;
    }

    public Short getPvpDamage() {
        return pvpDamage;
    }

    public MapleRing getRing() {
        if (!GameConstants.isEffectRing(getItemId()) || getUniqueId() <= 0) {
            return null;
        }
        if (ring == null) {
            ring = MapleRing.loadFromDb(getUniqueId(), getPosition() < 0);
        }
        return ring;
    }

    public void setRing(MapleRing ring) {
        this.ring = ring;
    }

    public MapleAndroid getAndroid() {
        if (getItemId() / 10000 != 166 || getUniqueId() <= 0) {
            return null;
        }
        if (android == null) {
            android = MapleAndroid.loadFromDb(getItemId(), getUniqueId());
        }
        return android;
    }

    public void setAndroid(MapleAndroid ring) {
        android = ring;
    }

    public short getSocketState() {
        int flag = 0;
        if (!MapleItemInformationProvider.getInstance().isCash(getItemId())) {
            if (socket1 == 0) {
                flag |= SocketFlag.DEFAULT.getValue();
                flag |= SocketFlag.SOCKET_BOX_1.getValue();
            }
            if (socket1 > 0) {
                flag |= SocketFlag.DEFAULT.getValue();
                flag |= SocketFlag.SOCKET_BOX_1.getValue();
                flag |= SocketFlag.USED_SOCKET_1.getValue();
            }
        }
        return (short) flag;
    }

    public int getSocket1() {
        return socket1;
    }

    public void setSocket1(int socket1) {
        this.socket1 = socket1;
    }

    public int getSocket2() {
        return socket2;
    }

    public void setSocket2(int socket2) {
        this.socket2 = socket2;
    }

    public int getSocket3() {
        return socket3;
    }

    public void setSocket3(int socket3) {
        this.socket3 = socket3;
    }

    /**
     * @return the soulOptionId
     */
    public short getSoulOptionId() {
        return soulOptionId;
    }

    /**
     * @param soulOptionId the soulOptionId to set
     */
    public void setSoulOptionId(short soulOptionId) {
        this.soulOptionId = soulOptionId;
    }

    /**
     * @return the soulSocketId
     */
    public short getSoulSocketId() {
        return soulSocketId;
    }

    /**
     * @param soulSocketId the soulSocketId to set
     */
    public void setSoulSocketId(short soulSocketId) {
        this.soulSocketId = soulSocketId;
    }

    /**
     * @return the soulOption
     */
    public short getSoulOption() {
        return soulOption;
    }

    /**
     * @param soulOption the soulOption to set
     */
    public void setSoulOption(short soulOption) {
        this.soulOption = soulOption;
    }

    /**
     * @return the expBonus
     */
    public int getExpBonus() {
        return expBonus;
    }

    /**
     * @param expBonus the expBonus to set
     */
    public void setExpBonus(int expBonus) {
        this.expBonus = expBonus;
    }

    /**
     * @return the cashGrade
     */
    public int getCashGrade() {
        return cashGrade;
    }

    /**
     * @param cashGrade the cashGrade to set
     */
    public void setCashGrade(int cashGrade) {
        this.cashGrade = cashGrade;
    }

    /**
     * @return the cashStats
     */
    public int[] getCashStats() {
        return cashStats;
    }

    /**
     *
     * @param cashStats cashStat1 the cashStat1 to set
     */
    public void setCashStats(int[] cashStats) {
        this.cashStats = cashStats;
    }

    /**
     * This method returns back a boolean representing if the item is shared between alpha or beta
     *
     * @return betaShare
     */
    public boolean isBetaShare() {
        return betaShare;
    }

    /**
     * This method is set if a cash item is shared between alpha and beta
     *
     * @param betaShare
     */
    public void setBetaShare(boolean betaShare) {
        this.betaShare = betaShare;
    }

    /**
     * This is a special flag that came with the star force system All the flags are located in {@link EnchantmentFlag}
     *
     * @param mask
     */
    public void setStarFlag(int starFlag) {
        this.starFlag = starFlag;
    }

    /**
     * This gives the flag for the star force
     *
     * @return starFlag
     */
    public int getStarFlag() {
        return starFlag;
    }

    /**
     * How many times the star force rank fell
     *
     * @return
     */
    public byte getEnchantFail() {
        return enchantFail;
    }

    /**
     * Sets the star force fail rate
     *
     * @param enchantFail
     */
    public void setEnchantFail(int enchantFail) {
        this.enchantFail = (byte) enchantFail;
    }

    public short getArcane() {
        return this.nArcane;
    }

    public void setArcane(int nArcane) {
        this.nArcane = (short) nArcane;
    }

    public short getArcaneMaxLevel() {
        return this.nArcaneMaxLevel;
    }

    public void setArcaneMaxLevel(int nMaxLevel) {
        this.nArcaneMaxLevel = (short) nMaxLevel;
    }

    public int getArcaneExp() {
        return this.nArcaneExp;
    }

    public void setArcaneExp(int nExp) {
        this.nArcaneExp = (int) nExp;
    }
}
