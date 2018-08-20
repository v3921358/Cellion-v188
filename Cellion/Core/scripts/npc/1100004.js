/*
	NPC Name: 		Kiru
	Map(s): 		Empress's Road - Sky Ferry
	Description: 		Station Info
*/
var status = -1;

function action(mode, type, selection) {
    if(mode == 0) {
	cm.sendNext("You don't want to go? Then whatever.");
	cm.dispose();
	return;
    } else {
	status++;
    }
    if (status == 0) {
	cm.sendSimple("Do you have an airplane? Airplanes can take you to other continents, without needing to wait for a ship. Of course, it costs 5,000 mesos, but you're paying for convenience!#b\r\n\r\n#L0# Use the airplane. #r(5000 Mesos)#b#l\r\n#L1# Board a ship.#l");
    } else if(status == 1) {
	if (selection == 0) {
	    if (cm.isBuffedMount_Airplane()) {
		if (cm.getMeso() >= 5000) {
		    cm.gainMeso(-5000);
		    cm.warp(200110060,0);
		} else {
		    cm.sendOk("You do not have 5000 meso to use the airplane.")
		}
	    } else {
		cm.sendNext("You need to ride on an Airplane first or the mount you are currently using cannot be used as an Airplane.");
	    }
	    cm.dispose();
	} else if (selection == 1) {
	    cm.sendYesNo("Hmmm... I like the current for today. Are you planning on leaving #m130000000# to go somewhere else? This boat heads to Orbis of Ossyria.");
	}
    } else if (status == 2) {
	cm.sendYesNo("Have you taken care of everything you needed to in #m130000000#? If you happen to be headed toward #b#m200000000##k I can take you there. What do you say? Are you going to go to #m200000000#? \r\n\r\n\r\n You'll have to pay a fee of #b1000#k Mesos.");
    } else if (status == 3) {
	if (cm.getMeso() >= 1000) {
	    cm.gainMeso(-1000);
	    cm.warp(200090023,0);
	    cm.startSelfTimer(240, 200000161);
	// 4 min
	} else {
	    cm.sendNext("You will need 800 meso to board this ship that heads to Edelstein.");
	}
	cm.dispose();
    }
}