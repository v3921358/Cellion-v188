/*
 * Latest tech 2 : Evolving belt quest
 */
var status = -1;

function start(mode, type, selection) {
    if (mode == 1) {
	status++;
    } else {
	if (status == 0) {
	    qm.sendNext("What?");
	    qm.dispose();
	    return;
	}
	status--;
    }
    switch (status) {
	case 0:
	    qm.sendNext("What?! The item changed into a belt after 30 minutes? How would ANYONE have EVER guessed that?! But that's not very advanced tech, now it is? Let me see the belt?");
	    break;
	case 1:
	    qm.sendNextPrev("Hmph, Ability Belts like these don't sell for much. Useless! I must have a word with Dr. Kim about this. You can keep the darned thing.");
	    break;
	case 2:
	    qm.forceCompleteQuest();
	    qm.dispose();
	    break;
    }
}

function end(mode, type, selection) {
    qm.dispose();
}