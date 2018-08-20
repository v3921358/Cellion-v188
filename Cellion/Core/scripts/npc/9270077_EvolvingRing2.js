/*
	Naomi - Evolving ring NPC
*/
var status = -1;

function action(mode, type, selection) {
    if (mode == 1)
	status++;
    else
	status--;
		
    if (status == 0) {
	if (!cm.isDateAfter(19,8,2011)) {
	    cm.sendOk("The event has ended.")
	    cm.dispose();
	    return;
	}
	cm.sendSimple("The Evolving ring #2 event has started and will end on #b[19th August 2011]#k The stats of the ring can be upgraded every day, up to 17 times.  Participate in the event and make yourself a super stat ring! \r\n#b#L9# Trade my Evolving Ring 1 for a tradeable version! #l\r\n#L0# What is an evolving ring #2? #l\r\n#L1# I want a ring #2. #l\r\n#L2# I want to upgrade my ring #2. #l");
    } else if (status == 1) {
	switch (selection) {
	    case 9:
		for (var i = 1112499; i <= 1112516; i++) {
		    if (cm.haveItem(i, 1, false, true)) {
			cm.gainItem(i, -1);
			cm.gainItem((1112517 + (i - 1112499) - 1), 1);
			break;
		    }
		}
		cm.dispose();
		break;
	    case 0:
		status = 9;
		cm.sendNext("You can upgrade the stats of the evolving ring #2 once every day, up to 17 times.  Please log in every day, talk to me to receive an item, and play for an hour to receive an item to upgrade your ring.");
		break;
	    case 1:
		status = 19;
		cm.sendYesNo("Would you like to recieve your ring?");
		break;
	    case 2:
		if (cm.haveItem(3994225)) {
		    cm.sendYesNo("You have an Evolving Ring Upgrade Potion! Would you like to upgrade your ring now?");
		    status = 29;
		} else {
		    if (cm.get_player_bosslimit(150020,1)) {
			if (!cm.haveItem(3994224)) { // scheduled potion
			    cm.gainItem(3994224,1,3600000); // one hour
			}
			cm.sendOk("You will have to keep the box for an hour to recieve the mysterious potion that upgrades your ring.  Come back with the mysterious potion.");
		    } else {
			cm.sendOk("You may only trade for this once per day!");
		    }
		    cm.dispose();
		}
		break;
	}
    } else if (status == 10) {
	cm.sendNext("If you log out before one hour, you will need to come back and stay for an hour again to upgrade your ring.");
    } else if (status == 11) {
	cm.sendNextPrev("On the last day of the event, Auguest 19th, you will be able to change the status of your leveled up ring to be tradable. Don't miss it!");
    } else if (status == 12) {
	cm.dispose();
    } else if (status == 20) {
	var have = false;
	for (var i = 1112614; i <= 1112631; i++) {
	    if (cm.haveItem(i, 1, true, true)) {
		have = true;
		break;
	    }
	}
	if (!have) {
	    for (var i = 1112632; i <= 1112648; i++) {
		if (cm.haveItem(i, 1, true, true)) {
		    have = true;
		    break;
		}
	    }
	}
	if (have) {
	    cm.sendOk("Sorry. I can't give you another ring since you have one already.");
	} else {
	    cm.gainItem(1112614,1,86400000); // one Day
	}
	cm.dispose();
    } else if (status == 30) {
	if (cm.get_player_bosslimit(150020,1)) {
	    if (cm.haveItem(3994225)) {
		// 1112631 = lv. 17
		for (var i = 1112614; i <= 1112631; i++) {
		    if (cm.haveItem(i, 1, true, true)) {
			if (!cm.haveItem(i)) {
			    cm.sendOk("Please unequip your ring first before upgrading.");
			    cm.dispose();
			    return;
			}
			var id = i + 1;
			if (id > 1112631) { // Over Lvl 17 untradeable
			    id = 1112648; // Give lvl 17 tradeable
			}
			if (cm.checkAndSet_player_bosslimit(150020,1)) {
			    cm.gainItem(3994225,-1); // Upgrade potion
			    cm.gainItem(i,-1);
			    cm.gainItem(id,1);
			} else {
			    cm.sendOk("You may only trade for this once per day!");
			}
			cm.dispose();
			return;
		    }
		}
		if (cm.haveItem(1112614)) { // Lvl 0
		    cm.gainItem(1112614,-1);
		    cm.gainItem(1112615,1);
		}
		cm.gainItem(3994225,-1); // Upgrade potion
		if (cm.haveItem(3994224)) { // double check! [Potion]
		    cm.gainItem(3994224,-1);
		}
	    } else { // hack.
		cm.sendOk("You do not have the Evolving Ring Upgrade Potion!");
	    }
	} else {
	    cm.sendOk("You may only trade for this once per day!");
	}
	cm.dispose();
    }
}