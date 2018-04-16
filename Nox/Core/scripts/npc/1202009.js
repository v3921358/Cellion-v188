/*
 * Wolf guard
 */

var status = -1;

function action(mode, type, selection) {
    if (mode == 1) {
	status++;
    } else {
	status--;
    }
    switch (status) {
	case 0:
	    if (cm.getQuestStatus(21612) > 0) {
		cm.sendNext("What is it? If you are here to waste my time, get lost.");
	    } else {
		cm.sendNext("What is it? If you are here to waste my time, get lost.");
		cm.dispose();
	    }
	    break;
	case 1:
	    cm.sendNextPrev("Oh you are here to see the captain that was talking about? In that case, come right in.");
	    break;
	case 2:
	    cm.warp(140010210, 0);
	    cm.dispose();
	    break;
    }
}