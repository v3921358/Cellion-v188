/*
 * Reach Dual blade Lv. 40!
 */

var status = -1;

function start(mode, type, selection) {
    if (qm.canHold(2040122)) { // Dual Blade Secret scroll 40%
	qm.gainItem(2040122, 1);
	qm.forceCompleteQuest();
    } else {
	qm.sendOk("Please make space for your inventory.");
    }
    qm.dispose();
}

function end(mode, type, selection) {
    qm.dispose();
}