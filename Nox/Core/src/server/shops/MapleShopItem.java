package server.shops;

public class MapleShopItem {

    private short buyable;
    private short quantity;
    private int itemId;
    private int price;
    private short slot;
    private int reqItem;
    private int reqItemQ;
    private int category;
    private int minLevel;
    private int expiration;
    private byte rank;
    private int potential;
    private int pointQuestId;
    private int pointQuestPrice;
    private int starCoin;
    private int questExId;
    private String questExKey;
    private int questExValue;
    private int maxLevel;
    private int questId;
    private int saleLimit;
    private int levelLimited;

    public MapleShopItem(int itemId, int price, short slot, short buyable) {
        this(buyable, (short) 1, itemId, price, slot, 0, 0, (byte) 0, 0, 0, 0, 0);
    }

    public MapleShopItem(int itemId, int price, short slot, short buyable, short quantity) {
        this(buyable, quantity, itemId, price, slot, 0, 0, (byte) 0, 0, 0, 0, 0);
    }

    public MapleShopItem(short buyable, short quantity, int itemId, int price, short slot, int reqItem, int reqItemQ, byte rank, int category, int minLevel, int expiration, int potential) {
        this.buyable = buyable;
        this.quantity = quantity;
        this.itemId = itemId;
        this.price = price;
        this.slot = slot;
        this.reqItem = reqItem;
        this.reqItemQ = reqItemQ;
        this.rank = rank;
        this.category = category;
        this.minLevel = minLevel;
        this.expiration = expiration;
        this.potential = potential;
    }

    public short getBuyable() {
        return buyable;
    }

    public short getQuantity() {
        return quantity;
    }

    public int getItemId() {
        return itemId;
    }

    public int getPrice() {
        return price;
    }

    public short getSlot() {
        return slot;
    }

    public int getReqItem() {
        return reqItem;
    }

    public int getReqItemQ() {
        return reqItemQ;
    }

    public byte getRank() {
        return rank;
    }

    public int getCategory() {
        return category;
    }

    public int getMinLevel() {
        return minLevel;
    }

    public int getExpiration() {
        return expiration;
    }

    public int getPotentialGrade() {
        return potential;
    }

    /**
     * @return the pointQuestId
     */
    public int getPointQuestId() {
        return pointQuestId;
    }

    /**
     * @param pointQuestId the pointQuestId to set
     */
    public void setPointQuestId(int pointQuestId) {
        this.pointQuestId = pointQuestId;
    }

    /**
     * @return the pointQuestPrice
     */
    public int getPointQuestPrice() {
        return pointQuestPrice;
    }

    /**
     * @param pointQuestPrice the pointQuestPrice to set
     */
    public void setPointQuestPrice(int pointQuestPrice) {
        this.pointQuestPrice = pointQuestPrice;
    }

    /**
     * @return the starCoin
     */
    public int getStarCoin() {
        return starCoin;
    }

    /**
     * @param starCoin the starCoin to set
     */
    public void setStarCoin(int starCoin) {
        this.starCoin = starCoin;
    }

    /**
     * @return the questExId
     */
    public int getQuestExId() {
        return questExId;
    }

    /**
     * @param questExId the questExId to set
     */
    public void setQuestExId(int questExId) {
        this.questExId = questExId;
    }

    /**
     * @return the questExKey
     */
    public String getQuestExKey() {
        return questExKey;
    }

    /**
     * @param questExKey the questExKey to set
     */
    public void setQuestExKey(String questExKey) {
        this.questExKey = questExKey;
    }

    public int getQuestExValue() {
        return questExValue;
    }

    public void setQuestExValue(int questExValue) {
        this.questExValue = questExValue;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public void setMaxLevel(int maxLevel) {
        this.maxLevel = maxLevel;
    }

    public int getQuestId() {
        return questId;
    }

    public void setQuestId(int questId) {
        this.questId = questId;
    }

    public int getSaleLimit() {
        return saleLimit;
    }

    public void setSaleLimit(int saleLimit) {
        this.saleLimit = saleLimit;
    }

    /**
     * @return the levelLimited
     */
    public int getLevelLimited() {
        return levelLimited;
    }

    /**
     * @param levelLimited the levelLimited to set
     */
    public void setLevelLimited(int levelLimited) {
        this.levelLimited = levelLimited;
    }
}
