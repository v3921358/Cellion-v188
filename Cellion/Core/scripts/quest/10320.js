/*
 *  Gaga Relic , Acquire 5000 points
 */

var status = -1;

function start(mode, type, selection) {
    if (mode == 1) {
	status++;
    } else {
	status--;
    }
    if (status == 0) {
	qm.sendNext("Wow did you hit 5,000 points already? That's fast! #b#h ##k, I will give you #i1142305:# and cast a magic spell and also improve your speed as an gift.");
    } else if (status == 1) {
	qm.sendNextPrev("You will be awarded with special gift if you hit over 10000pts.Why don't you target for top 10?");
    } else {
	if (qm.canHold(1142305)) {
	    qm.gainItem(1142305, 1);
	    qm.useItem(2022563);
	    qm.forceCompleteQuest();
	} else {
	    qm.sendOk("Why don't you make a space in your equip inventory first before talking to me?");
	}
	qm.dispose();
    }
}

function end(mode, type, selection) {
    qm.dispose();
}