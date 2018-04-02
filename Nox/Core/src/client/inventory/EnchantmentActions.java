package client.inventory;

public enum EnchantmentActions {

    SCROLL_UPGRADE(0),
    HYPER_UPGRADE(1),
    TRANSFER_HAMMER(2),
    POTENTIAL_UPGRADE(3),
    ADDITIONAL_POTENTIAL_UPGRADE(4),
    EXTENDED_UPGRADE(5),
    SOUL_WEAPON_UPGRADE(6),
    NO_TYPES(7),
    SCROLLLIST(50),
    FEVER_TIME(51),
    STARFORCE(52),
    MINI_GAME(53),
    SCROLL_RESULT(100),
    STARFORCE_RESULT(101),
    VESTIGE_COMPENSATION_RESULT(102),
    TRANSFER_HAMMER_RESULT(103),
    UNKOWN_FAILURE_RESULT(104),
    DISPLAY_BLOCK(105);

    private int action;

    /**
     * This enum contains all the actions for the enchantment system
     *
     * @param action
     */
    private EnchantmentActions(int action) {
        this.action = action;
    }

    /**
     * @returns the action
     */
    public EnchantmentActions getAction() {
        return this;
    }
    
    public int getValue() {
        return action;
    }

    public EnchantmentActions getAction(byte action) {
        for (EnchantmentActions actions : EnchantmentActions.values()) {
            if (actions.getAction().action == action) {
                return actions;
            }
        }
        return null;
    }

}
