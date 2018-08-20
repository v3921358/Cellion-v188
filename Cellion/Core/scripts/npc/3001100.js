/*
	NPC Name: 		Kyrin
	Map(s): 		Maple Road : Spilt road of choice
	Description: 		Job tutorial, movie clip
*/

var status = -1;

function start() {
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == 1) {
	status++;
    } else {
	if (status == 1) {
	    cm.sendNext("Do you want to try out our Magnus Simulation before invading the real deal?");
	    cm.dispose();
	    return;
	}
	status--;
    }
    if (status == 0) {
	cm.sendNext("Magnus is a tyrant which has overtaken Heliseum. We would like your help to defeat him. First do you wish to try our Magnus Simulation?");
    } else if (status == 1) {
	cm.sendYesNo("Do you want me to warp you to our Simulation Ground?");
    } else if (status == 2) {
	cm.warp(401060399, 0); // Effect/Direction3.img/pirate/Scene00
	cm.dispose();
    }
}