package enums;

/**
 *
 * @author
 */
public enum ShopOperationType {
    Buy(0),
    Sell(8),
    Recharge(14),
    UnknownError(0x20),;

    private int i;

    private ShopOperationType(int i) {
        this.i = i;
    }

    public int getOp() {
        return i;
    }
}
