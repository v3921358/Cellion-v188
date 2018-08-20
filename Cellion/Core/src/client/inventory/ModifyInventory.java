package client.inventory;

import enums.ModifyInventoryOperation;

/**
 *
 * @author kevin
 */
public class ModifyInventory {

    private final ModifyInventoryOperation mode;
    private Item item;
    private short oldPos;

    public ModifyInventory(ModifyInventoryOperation mode, Item item) {
        this.mode = mode;
        this.item = item.copy();
    }

    public ModifyInventory(ModifyInventoryOperation mode, Item item, short oldPos) {
        this.mode = mode;
        this.item = item.copy();
        this.oldPos = oldPos;
    }

    public ModifyInventoryOperation getMode() {
        return mode;
    }

    public int getInventoryType() {
        return item.getItemId() / 1000000;
    }

    public short getPosition() {
        return item.getPosition();
    }

    public short getOldPosition() {
        return oldPos;
    }

    public short getQuantity() {
        return item.getQuantity();
    }

    public Item getItem() {
        return item;
    }

    public void clear() {
        this.item = null;
    }
}
