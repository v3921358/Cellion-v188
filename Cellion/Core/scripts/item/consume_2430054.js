/*
 * Black Scooter 30 day use coupon
 */

function action(mode, type, selection) {
    if (cm.giveTemporaryCouponSkill(80001005,30, "Black Scooter", false)) {
	cm.gainItem(2430054, -1);
    }
    cm.dispose();
}