var status = -1;
var PQ = 'MinibossTreglow';

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
	 	cm.sendYesNo("Would you like to enter Treglow's Laboratory (3 times per day)?\r\n#b#L0# Enter #l\r\n#L1# Do Not Enter #l");
    } else if (status == 1) {
        if(selection == 0){
            if (cm.getParty() == null) { // No Party
                cm.sendOk("You must be in a party to enter.");
                cm.dispose();
            } else if (!cm.isLeader()) { // Not Party Leader
                cm.sendOk("Tell your leader to go first.");
                cm.dispose();
            } else if (cm.getPQLogAll(PQ) >= 3){
                cm.sendOk("Once a day only.");
                cm.dispose();
            }  else {
                switch(cm.getPlayer().getMapId()) {
                    case 401052104:
                        cm.resetMap(401052200);
                        cm.warpParty(401052200, 0);
                        cm.setPQLogAll(PQ);
                        cm.mapChangeTimer(401052200, 401052104, 1200, false);
                        cm.spawnMob(8880008, 1, 1498, -1347);
                        cm.dispose();
                        break;
                    case 401051104:
                        cm.resetMap(401051200);
                        cm.warpParty(401051200, 0);
                        cm.setPQLogAll(PQ);
                        cm.mapChangeTimer(401051200, 401051104, 1200, false);
                        cm.spawnMob(8880006, 1, 1440, -1347);
                        cm.dispose();
                }
                
            }
        }else if(selection == 1){
            cm.sendOk("Come back when your ready.");
            cm.dispose();
        }
    }
}