/*
	Cannoneer Intro Setup
	@Author Mazen
*/

 status = -1;

function start(mode, type, selection) {
	if (mode == 1) {
		status++;
	} else {
		qm.dispose();
		return;
	}
	if (status == 0) {
		qm.sendNextNoESC("Greetings! Isn't this just the perfect weather for a journey? I'm Skipper, the captain of this fine ship. You must be a new Explorer, eh? Nice to meet you.");
	} else if (status == 1) {
		qm.sendNextNoESC("We're on our way to Maple Island!");
	} else if (status == 2) {
		qm.sendPlayerToNpc("I'm so excited! I can't wait to choose my job path!")
	} else if (status == 3) {
		qm.sendNextNoESC("Haha, that's the spirit! We'll be there soon.")
	} else if (status == 4) {
		qm.sendNextNoESC(".......");
	} else if (status == 5) {
		qm.sendPlayerToNpc(".......");
	} else if (status == 6) {
		qm.sendNextNoESC("We're being attacked by a Jr. Balrog! Get to safety!");
	} else if (status == 7) {
		qm.warp(931050310, 0); //Warp to dark room.
		qm.sendPlayerToNpc("(Where am I?... What Happened...)")
	} else if (status == 8) {
		qm.warp(3000100, 1); //Warp to Coco Island, but second spawn point to avoid disconnect.
		qm.sendPlayerToNpc("Huh? What's that monkey doing there on the left?");
	} else if (status == 9) {
		qm.forceCompleteQuest();
		qm.dispose();
	}
}
function end(mode, type, selection) {
	qm.dispose();
}
