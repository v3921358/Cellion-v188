var status = -1;

function start() {
    action(1, 0, 0);
}

function action(mode, type, selection) {
var map = cm.getMapId();
    if (mode == 1) {
	status++;
    } else {
	if (status == 0) {
	    cm.dispose();
	    return;
	}
	status--;
    }
    if (status == 0) {
    	if (map == 744000041){//last map
    		cm.sendYesNo("You beat it! Now want to go back and get your reward? ");
    	} else {
			cm.sendYesNo("Giving up already? I knew it.");
    	}
    } else if (status == 1) {
    	if (map == 744000041){//last map
			cm.warp(744000020, 1);
			cm.gainItem(4310075, 4);
    	} else {
			cm.warp(744000020, 1);
		}
	cm.dispose();
    } 
}