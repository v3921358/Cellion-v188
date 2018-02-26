/*  
	Von Bon Exit NPC
	@author Mazen
*/

load("nashorn:mozilla_compat.js");
importPackage(Packages.tools.packet);

var status = -1;

function action(mode, type, selection) {
	if (mode != 1) {
		cm.dispose();
	} else {
		status++;
		if (status == 0) {
			cm.sendYesNo("Heh. Would you like to leave?");		
		} else if (status == 1) {
			if (mode != 0) {
				cm.dispose();
			}
			cm.warp(105200000, 0);
			cm.dispose();
		}
	}
}