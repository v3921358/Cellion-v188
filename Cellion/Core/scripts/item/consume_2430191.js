/*
 * Cute Newbie 
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
	    cm.dispose();
	    break;
    }
}