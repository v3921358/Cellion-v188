/*
 * Black Scooter 1 day use coupon
 */

function action(mode, type, selection) {
    if (cm.giveTemporaryCouponSkill(80001005, 1, "Black Scooter", false)) {
	cm.gainItem(2430037, -1);
    }
    cm.dispose();
}