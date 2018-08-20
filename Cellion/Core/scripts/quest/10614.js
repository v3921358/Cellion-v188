/*
 * Reach Dual blade Lv. 60!
 */

var status = -1;

function start(mode, type, selection) {
    if (qm.canHold(2040123)) { // Dual Blade Secret scroll 60%
	qm.gainItem(2040123, 1);
	qm.forceCompleteQuest();
    } else {
	qm.sendOk("Please make space for your inventory.");
    }
    qm.dispose();
}

function end(mode, type, selection) {
    qm.dispose();
}