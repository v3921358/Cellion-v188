/*
 * Reach Dual blade Lv. 20!
 */

var status = -1;

function start(mode, type, selection) {
    if (qm.canHold(1012187)) { // Shabby Mask
	qm.gainItem(1012187, 1);
	qm.forceCompleteQuest();
    } else {
	qm.sendOk("Please make space for your inventory.");
    }
    qm.dispose();
}

function end(mode, type, selection) {
    qm.dispose();
}