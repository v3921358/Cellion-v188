/* 
 * Kenta - Exit Kenta PQ
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
	    cm.sendYesNo("Exit the party quest? You may not get back once you have exit.");
	    break;
	case 1:
	    cm.warp(923040000,0);
	    cm.dispose();
	    break;
    }
}