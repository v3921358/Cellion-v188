// Juliet

var status = -1;

function action(mode, type, selection) {
    if (mode == 1) {
	status++;
    } else {
	status--;
    }
    switch (status) {
	case 0:
	    cm.warp(926110600,0);
	    cm.gainExp(70000);
	    break;
    }
    cm.dispose();
}