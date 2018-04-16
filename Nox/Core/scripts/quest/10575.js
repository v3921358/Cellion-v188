/*
 * Cassandra - Return of the explorers
 */
var status = -1;

function start(mode, type, selection) {
    if (mode == 1) {
	status++;
    } else {
	status--;
    }
    if (status == 0) {
	qm.sendNext("Wow! You've already gained 30 levels? Let's see...");
    } else if (status == 1) {
	var record = qm.getQuestRecord(150012);
	var data = record.getInfoData();
	if (data == null) {
	    qm.sendOk("What? You havn't gained 30 levels yet, come back when you do. You must create a new Adventurer character and get it to level 30 to receive a reward.");
	    qm.dispose();
	} else {
	    if (qm.getPlayerStat("LVL") >= 30) {
		if (data.equals("0")) {
		    qm.sendSimple("Please select one of them : \n\r #L0##i1112427:# #t1112427##l \n\r #L1##i1112428:# #t1112428##l \n\r #L2##i1112429:# #t1112429##l");
		} else {
		    qm.sendOk("You have already received a reward.");
		    qm.dispose();
		}
	    } else {
		qm.sendOk("What? You havn't gained 30 levels yet, come back when you do. You must create a new Adventurer character and get it to level 30 to receive a reward.");
		qm.dispose();
	    }
	}
    } else if (status == 2) {
	if (!qm.canHold(1112427)) {
	    qm.sendOk("Please make space in your inventory slot.");
	    qm.dispose();
	} else {
	    switch (selection) {
		case 0:
		    qm.gainItem(1112427, 1);
		    break;
		case 1:
		    qm.gainItem(1112428, 1);
		    break;
		case 2:
		    qm.gainItem(1112429, 1);
		    break;
	    }
	    qm.dispose();
	}
    }
}