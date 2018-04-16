/*
 * Balrog 1 day use coupon
 */

function action(mode, type, selection) {
    if (cm.giveTemporaryCouponSkill(80001008,1, "Balrog", false)) {
	cm.gainItem(2430040, -1);
    }
    cm.dispose();
}