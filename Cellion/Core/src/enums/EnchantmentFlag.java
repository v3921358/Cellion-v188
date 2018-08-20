package enums;

/**
 * @author Steven
 *
 */
public enum EnchantmentFlag {

    RED_LABEL(0x20), //Function: IsMasterPieceMaterialItem
    BLACK_LABEL(0x40), //Function: IsMasterPieceMaterialItem
    INNOCENT_RUC(0x80),
    HYPER_UPGRADE(0x100),
    HYPER_UPGRADE_BOUND(0x200),//Function: IsVestigeBinded
    HYPER_UPGRADE_TRADE(0x400),//Function: IsVestigePossibleTrading
    HYPER_UPGRADE_ACCOUNT_SHARE(0x800),//Function: IsVestigeAppliedAccountShareTag
    UNKOWN(0x1000);

    private int flag;

    /**
     * This enum handles all the extra item states
     */
    private EnchantmentFlag(int flag) {
        this.flag = flag;
    }

    /**
     * Returns the flag associated with the item state
     *
     * @returns flag
     */
    public int getFlag() {
        return flag;
    }

}
