/*
 * NPC : Ridley
 * Masteria
 */

var status = -1;

function action(mode, type, selection) {
    if (mode == 1) {
	status++;
    } else {
	if (status == 0) {
	    cm.sendNext("Okay, then continue your tour. Have fun!");
	    cm.dispose();
	    return;
	}
	status--;
    }

    switch (status) {
	case 0:
	    cm.sendYesNo("Do you wish to leave Crimson Wood and return to your original town?");
	    break;
	case 1:
	    cm.sendNext("Okay, visit Crimson Wood again soon!");
	    break;
	case 2:
	    cm.warp(cm.getSavedLocation("MULUNG_TC"), 0);
	    cm.clearSavedLocation("MULUNG_TC");
	    cm.dispose();
	    break;
    }
}