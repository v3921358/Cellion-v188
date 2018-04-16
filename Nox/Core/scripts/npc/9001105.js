// Grandpa moon bunny

var status = -1;

function action(mode, type, selection) {
    if (mode == 1)
	status++;
    else {
	cm.sendNext("If too much time passes, his safety can't be guaranteed.");
	cm.dispose();
	return;
    }
    if (status == 0) {
	if (cm.getMapId() != 922240200) {
	    status = 9;
	    cm.sendYesNo("Don't worry if you fail. You'll have 3 chances. Do you still want to give up?");
	} else 
	    cm.sendSimple("Did you have something to say...?\r\n\r\n#b#L0#I want to rescue Gaga.#l");
    } else if (status == 1) {
	cm.sendNext("Welcome! I heard what happened from Baby Moon Bunny. I'm glad you came since I was planning on requesting some help. Gaga is a friend of mine who has helped me before and often stops by to say hello. Unfortunately, he was on his way back from visiting me when he was kidnapped by aliens.");
    } else if (status == 2) {
	cm.sendYesNo("If we just leave Gaga with the aliens, something terrible will happen to him! I'll let you borrow a spaceship that the Moon Bunnies use for traveling so that you can rescue Gaga. #bAlthough he might appear a bit indecisive, slow, and immature at times,#k he's really a nice young man. Do you want to go rescue him now?");
    } else if (status == 3) {
	// 922240002
	// give itemid 2360002
	// 3 min
	// weather 5120027, Please rescue Gaga within the time limit.
	if (cm.getParty() != null) {
	    if (cm.isLeader()) {
		var q = cm.getEventManager("RescueGaga");
		if (q == null) {
		    cm.sendOk("Unknown error occured");
		} else {
		    q.startInstance(cm.getParty(), cm.getMap());
		}
	    } else {
		cm.sendOk("You are not the leader of the party, please ask your leader to talk to me.");
	    }
	} else {
	    cm.sendOk("You will need a party in order to get in there.");
	}
	cm.dispose();
    } else if (status == 9) {
	cm.warp(922240200,0);
	cm.dispose();
    }
}