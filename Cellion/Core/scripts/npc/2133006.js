var PQ = 'FaryBoss';
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
	 	         cm.sendYesNo("This is Ephenia's hideout.\r\nIf you haven't gotten permission, you shouldn't be here.\r\nEnter anyway?");
    }
    } else if (status == 1) {
    	cm.resetMap(300030310);
		cm.warpParty(300030310, 1);
    	cm.mapChangeTimer(300030310, 300030300, 900, false);
        cm.setPQLog(PQ);
    	//cm.spawnMob(5250007, 1, 218, 150);
		cm.dispose();
    }
}