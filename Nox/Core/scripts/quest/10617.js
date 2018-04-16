/*
 * Reach Dual blade Lv. 90!
 */

var status = -1;

function start(mode, type, selection) {
    if (qm.canHold(2040125)) { // Dual Blade secret scroll 90%
	qm.gainItem(2040125, 1);
	qm.forceCompleteQuest();
    } else {
	qm.sendOk("Please make space for your inventory.");
    }
    qm.dispose();
}

function end(mode, type, selection) {
    qm.dispose();
}