/*
 * Wooden Airplane 3 day coupon
 */

function action(mode, type, selection) {
    if (cm.giveTemporaryCouponSkill(80001027,1, "Wooden Airplane", false)) {
	cm.gainItem(2430249, -1);
    }
    cm.dispose();
}