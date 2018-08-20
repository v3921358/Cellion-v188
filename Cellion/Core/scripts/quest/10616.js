/*
 * Reach Dual blade Lv. 80!
 */

var status = -1;

function start(mode, type, selection) {
    if (qm.canHold(2040124)) { // Dual Blade secret scroll 80%
	qm.gainItem(2040124, 1);
	qm.forceCompleteQuest();
    } else {
	qm.sendOk("Please make space for your inventory.");
    }
    qm.dispose();
}

function end(mode, type, selection) {
    qm.dispose();
}