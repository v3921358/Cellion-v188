/*
 * Croco 1 day use coupon
 */

function action(mode, type, selection) {
    if (cm.giveTemporaryCouponSkill(80001004,1, "Croco", false)) {
	cm.gainItem(2430036, -1);
    }
    cm.dispose();
}