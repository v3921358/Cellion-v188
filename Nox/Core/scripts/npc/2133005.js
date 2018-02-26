var PQ = 'Chao';
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
    //switch(cm.getPlayer().getMapId()) {
	//case 300010410:
    if (status == 0) {
        if (cm.getPQLog(PQ) >= 1) {
                cm.sendSimple("You can only fight this boss once a day.");
                cm.dispose();
            }else{
	 	         cm.sendYesNo("Do you want to enter and fight Chao.\r\nIf you haven't gotten permission, you shouldn't be here.\r\nEnter anyway?");
    }
    } else if (status == 1) {
    	cm.resetMap(300010410);
		cm.warpParty(300010420, 1);
    	cm.mapChangeTimer(300010420, 300010410, 420, false);
        cm.setPQLog(PQ);
    	cm.spawnMob(5250004, 1, 269, 93);
		cm.dispose();
    }
}