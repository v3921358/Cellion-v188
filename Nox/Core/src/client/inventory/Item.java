package client.inventory;

import enums.ItemType;
import enums.ItemFlag;
import server.maps.objects.Pet;

public class Item implements Comparable<Item> {

    private int id;
    private short position;
    private short quantity;
    private short flag;
    private long expiration = -1, inventoryitemid = 0;
    private Pet pet = null;
    private int uniqueid;
    private String owner = "";
    private String gmLog = "";
    private String giftFrom = "";
    private int exp;

    public Item(int id, short position, short quantity, short flag, int uniqueid) {
        super();
        this.id = id;
        this.position = position;
        this.quantity = quantity;
        this.flag = flag;
        this.uniqueid = uniqueid;
    }

    public Item(int id, short position, short quantity, short flag) {
        super();
        this.id = id;
        this.position = position;
        this.quantity = quantity;
        this.flag = flag;
        this.uniqueid = -1;
    }

    public Item(int id, byte position, short quantity) {
        super();
        this.id = id;
        this.position = position;
        this.quantity = quantity;
        this.uniqueid = -1;
    }

    public Item copy() {
        Item ret = new Item(id, position, quantity, flag, uniqueid);
        ret.pet = pet;
        ret.owner = owner;
        ret.gmLog = gmLog;
        ret.expiration = expiration;
        ret.giftFrom = giftFrom;
        ret.exp = exp;
        return ret;
    }

    public Item copyWithQuantity(short qq) {
        Item ret = new Item(id, position, qq, flag, uniqueid);
        ret.pet = pet;
        ret.owner = owner;
        ret.gmLog = gmLog;
        ret.expiration = expiration;
        ret.giftFrom = giftFrom;
        ret.exp = exp;
        return ret;
    }

    public void setPosition(int position) {
        this.position = (short) position;
    }

    public void setQuantity(short quantity) {
        this.quantity = quantity;
    }

    public int getItemId() {
        return id;
    }

    public short getPosition() {
        return position;
    }

    public short getFlag() {
        return flag;
    }

    public short getQuantity() {
        return quantity;
    }

    public ItemType getType() {
        return ItemType.Item; // An Item
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public void setFlag(short flag) {
        this.flag = flag;
    }

    public void modifyFlags(boolean add, ItemFlag flag) {
        if (add) {
            this.flag |= flag.getValue();
        } else {
            this.flag &= ~(flag.getValue());
        }
    }

    public long getExpiration() {
        return expiration;
    }

    public void setExpiration(long expire) {
        this.expiration = expire;
    }

    public String getGMLog() {
        return gmLog;
    }

    public void setGMLog(String GameMaster_log) {
        this.gmLog = GameMaster_log;
    }

    public int getUniqueId() {
        return uniqueid;
    }

    public void setUniqueId(int ui) {
        this.uniqueid = ui;
    }

    public long getInventoryId() { //this doesn't need to be 100% accurate, just different
        return inventoryitemid;
    }

    public void setInventoryId(long ui) {
        this.inventoryitemid = ui;
    }

    public Pet getPet() {
        return pet;
    }

    public void setPet(Pet pet) {
        this.pet = pet;
        if (pet != null) {
            this.uniqueid = pet.getItem().getUniqueId();
        }
    }

    public void setGiftFrom(String gf) {
        this.giftFrom = gf;
    }

    public String getGiftFrom() {
        return giftFrom;
    }

    public int getExp() {
        return exp;
    }

    public void setExp(int exp) {
        this.exp = exp;
    }

    @Override
    public int compareTo(Item other) {
        if (Math.abs(position) < Math.abs(other.getPosition())) {
            return -1;
        } else if (Math.abs(position) == Math.abs(other.getPosition())) {
            return 0;
        } else {
            return 1;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Item)) {
            return false;
        }
        Item ite = (Item) obj;
        return uniqueid == ite.getUniqueId() && id == ite.getItemId() && quantity == ite.getQuantity() && Math.abs(position) == Math.abs(ite.getPosition());
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 53 * hash + this.id;
        hash = 53 * hash + this.position;
        hash = 53 * hash + this.quantity;
        hash = 53 * hash + this.uniqueid;
        return hash;
    }

    @Override
    public String toString() {
        return "Item: " + id + " quantity: " + quantity;
    }
}
