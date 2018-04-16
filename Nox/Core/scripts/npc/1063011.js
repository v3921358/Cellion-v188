/*
 * NPC : Cave Wall of Evil Eye
 * Map : 105070300
 */

var status = -1;

function action(mode, type, selection) {
    if (mode == 1) {
	status++;
    } else {
	status--;
    }
    if (status == 0) {
	cm.sendGetText("I hear a strange voice. #bIf you wish to enter, blurt out the password#k!");
    } else if (status == 1) {
	if (cm.getText().equals("Francis is a genius Puppeteer!")) {
	    if (cm.getPlayerCount(910510000) == 0) { //	pi.forceCompleteQuest(21731); // hack :(
		cm.resetMap(910510000);

		cm.warp(910510000, 1);
		cm.spawnNpc(1204002, 421, 257);
		cm.startSelfTimer(600, 105070300);
	    } else {
		cm.playerMessage("The cave is already being searched by someone else. Better come back later.");
	    }
	} else {
	    cm.sendOk("Invalid password! Intruders are not allowed.")
	}
	cm.dispose();
    }
}