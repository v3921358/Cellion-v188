package client.inventory;

import java.io.Serializable;

/**
 * A class that holds the list of items that the user may re-purchase back from NPC stores
 *
 * @author
 */
public class ShopRepurchase implements Serializable {

    private final Item Item;
    private final int PreviousSalePrice;

    public ShopRepurchase(Item item, int PreviousSalePrice) {
        this.Item = item;
        this.PreviousSalePrice = PreviousSalePrice;
    }

    public Item getItem() {
        return Item;
    }

    public int getPreviousSalePrice() {
        return PreviousSalePrice;
    }
}
