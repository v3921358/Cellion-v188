var status = -1;

function start(mode, type, selection) {
	if (mode == 1){
	    status++;
	 }else{
	    status--;
	qm.dispose();
	}
	if (status == 0) {
		qm.forceStartQuest();
		qm.dispose();
	} 
}

function end(mode, type, selection) {
	if (mode == 1){
	    status++;
	 }else{
	    status--;
	qm.dispose();
	}
	if (status == 0) {
		qm.sendNext("Did you stop 10 #o9306000# monsters?");
	} if (status == 1){
		qm.sendNextPrev("Warm-up program completed. Follow similar protocol for all future Links.");
	} if(status == 2){
		qm.giveEvoCore(3601201);
		qm.sendNextPrev("You received core that allows you to affect monster appearance. Exit the Evolution System, equip the Core, and then reenter the Virtual World.");
	} if(status == 3){
		qm.sendNextPrev("#bTerminate your connection with the Evolution System#k, #eequip the Core#n, and try again.");
	} if(status == 4){
		qm.sendNextPrev("Connection to Evolution System closing.");
	} if(status == 5){
		qm.forceCompleteQuest();
		qm.warp(957000000);
		qm.dispose();
	}
}       
  
  