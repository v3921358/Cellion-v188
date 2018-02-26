var PQ = 'CaptainDarkgoo';
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
        if (cm.getPQLog(PQ) >= 5) {
                cm.sendSimple("You can only fight this boss 5 times a day.");
                cm.dispose();
            }else{
	 	         cm.sendYesNo("This is Captain Darkgoo's hideout at Gold Beach: Shady Beach.\r\nIf you haven't gotten permission, you shouldn't be here.\r\nEnter anyway?");
    }
    } else if (status == 1) {
	//if (cm.getPlayerCount(120041900) < 5) {
    	cm.resetMap(120041900);
		cm.warpParty(120041900, 1);
    	cm.mapChangeTimer(120041900, 120041800, 900, false);
        cm.setPQLog(PQ);
    	//cm.spawnMob(5250007, 1, 218, 150);
		cm.dispose();
    }
}