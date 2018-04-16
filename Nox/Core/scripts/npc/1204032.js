/* Aran quest : ???
*/
var status = -1;

function action(mode, type, selection) {
    if (mode == 0) {
	cm.dispose();
	return;
    } else {
	status++;
    }
    switch (status) {
	case 0:
	    if (!cm.haveItem(4032328)) {
		cm.sendOk("Aran! Please get the letter!");
		cm.dispose();
	    } else {
		cm.sendNext("Aran, have you retrieved the letter? Ah, what a relief. I knew you'd pull through.");
	    }
	    break;
	case 1:
	    cm.warp(100000201,0);
	    cm.dispose();
	    break;
    }
}
