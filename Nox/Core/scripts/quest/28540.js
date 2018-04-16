/*
 * Evolving belt upgrade : Evolving belt quest
 */
var status = -1;

function start(mode, type, selection) {
    if (mode == 1) {
	status++;
    } else {
	if (status == 1) {
	    qm.sendNext("You don't deserve to have that belt.");
	    qm.dispose();
	    return;
	}
	status--;
    }
    switch (status) {
	case 0:
	    qm.sendNext("I'm not going to ask how you got that belt, because I've got a pretty good guess.");
	    break;
	case 1:
	    qm.sendYesNo("But I WILL ask you if you'd like to know the secret powers contained in that belt. You curious?");
	    break;
	case 2:
	    qm.sendNext("Good. Hold on to this for an hour, and then you'll see the belt's power for yourself.");
	    break;
	case 3:
	    if (qm.canHold(3994427)) {
		qm.forceCompleteQuest();
		if (!qm.haveItem(3994427)) {
		    qm.gainItem(3994427,1,3600000); // 1 hur
		}
		qm.sendNext("Make sure you leave more than 1 slot empty in your EQUIP inventory after 1 hour. Otherwise, you will be in trouble. Bear this in mind.");
	    } else {
		qm.sendNext("Make sure you have an inventory space in your SETUP inventory and talk to me again");
	    }
	    break;
	case 4:
	    qm.sendNext("If you visit me once a day, I will give you a gem to satisfy your curiosity.");
	    qm.dispose();
	    break;
    }
}

function end(mode, type, selection) {
    qm.dispose();
}