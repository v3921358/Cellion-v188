/*
 * Reach Dual blade Lv. 100!
 */

var status = -1;

function start(mode, type, selection) {
    if (qm.canHold(1012191)) { // Dual blade mask
	qm.gainItem(1012191, 1);
	qm.forceCompleteQuest();
    } else {
	qm.sendOk("Please make space for your inventory.");
    }
    qm.dispose();
}

function end(mode, type, selection) {
    qm.dispose();
}