package server.potentials;

/**
 *
 * @author Lloyd Korn
 */
public enum ItemPotentialSkill {

    Decent_HyperBody(8003),
    Decent_Haste(8000),
    Decent_SharpEye(8002),
    Decent_MysticDoor(8001),
    Decent_CombatOrders(8004),
    Decent_AdvancedBlessing(8005),
    Decent_SpeedInfusion(8006),
    None(-1),;

    private final int basicSkillId;

    private ItemPotentialSkill(int basicSkillId) {
        this.basicSkillId = basicSkillId;
    }

    public int getBasicSkillId() {
        return basicSkillId;
    }
}
