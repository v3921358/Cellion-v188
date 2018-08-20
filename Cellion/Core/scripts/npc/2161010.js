/*  Luden - Ani
 */

var status = -1;

function action(mode, type, selection) {
    if (mode == 1) {
	status++;
    } else {
	status--;
    }
    if (status == 0) {
	if (cm.getEventInstance() == null) {
	    cm.dispose();
	    return;
	}
	if (cm.haveMonster(8210013)) {
	    cm.sendNext("Save the residents!!! Are you sure you want to get out of here?");
	} else {
	    cm.sendNext("Would you like to go to receive your rewards? ");
	    status = 9;
	}
    } else if (status == 1) {
	cm.warp(cm.getSavedLocation("MULUNG_TC"), 0);
	cm.clearSavedLocation("MULUNG_TC");
	cm.dispose();
    } else if (status == 10) {
	cm.warp(921132000,0);
	cm.dispose();
    }
}