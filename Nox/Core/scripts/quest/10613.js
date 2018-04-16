/*
 * Reach Dual blade Lv. 50!
 */

var status = -1;

function start(mode, type, selection) {
    if (qm.canHold(1012189)) { // Blue Mask
	qm.gainItem(1012189, 1);
	qm.forceCompleteQuest();
    } else {
	qm.sendOk("Please make space for your inventory.");
    }
    qm.dispose();
}

function end(mode, type, selection) {
    qm.dispose();
}