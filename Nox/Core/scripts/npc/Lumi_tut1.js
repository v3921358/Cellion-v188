var status = -1;

function action(mode, type, selection) {
    if (mode == 1) {
	status++;
    } else {
	if (status == 0) {
	    cm.dispose();
	}
	status--;
    }
    if (status == 0) {
		cm.sendPlayerToNpc("The heavens have set the perfect stage for our final confrontation....");
    } else if (status == 1) {
		cm.LumiMove927020000();
    }  else if (status == 2) {
		cm.sendDirectionStatus(3, 1);
		//cm.sendDirectionStatus(4, 0);
	//	cm.warp(924020010,0);
		cm.dispose();
	}
}