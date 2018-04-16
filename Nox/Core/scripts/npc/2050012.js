/*
 * Agent Macro 
 */

var status = -1;

function action(mode, type, selection) {
    if (mode == 1)
	status++;
    else {
	if (status == 1) {
	    cm.sendNext("I knew you are too weak for this, forget about it.");
	    cm.dispose();
	    return;
	}
	status--;
    }
    if (status == 0) {
	if (cm.getMapId() == 280030001) {
	    var eim = cm.getEventInstance();
	    
	    if (eim != null && eim.getPropertyEx("SuperZakumDead") == 1) {
		status = 109;
		cm.sendSimple("Wow I see that the Super Zakum is dead, excellent job! I've prepared a gift here for you #i3010313:#.\r\n\r\nAlso did it drop any of this? #i1003361:# It is quite rare.. so.. #b\r\n\r\n#L0# Wow, give me that now!#l\r\n#L1# No thanks, I do not deserve this.#l");
	    } else {
		status = 99;
		cm.sendNext("Shoot shoot and shoot!! with all your might! \r\nWHAT!??! Giving up already? I knew you are too weak for this, forget about it.");
	    }
	} else {
	    cm.sendNext("Shhh... I advise you not to act like you know me here. The Omega Sector is the safest place in this world, as well as the most threatened. Unless you need me for something important, please excuse me right this minute.");
	
	    var em = cm.getEventManager("MockZakum");
	    if (em == null) {
		cm.dispose();
	    } else {
		if (cm.getMapId() != 280030001) {
		    if (enterbat()) {
			return;
		    }
		}
	    }
	}
    } else if (status == 1) {
	cm.sendYesNo("As I said, you need to be strong! How great it would be if I can train you to protect #bOmega Sector#k?. Would you like to join me in a secret training mission against the Black Mage? \r\n\r\nWho knows, you can even get something cool like this #i1003361:# or this #i3010313:#.\r\n\r\n#b(Please do not tell anyone about this training to avoid widespread panic! Omega Sector is still one of the safest place in this world)");
    } else if (status == 2) {
	var level = cm.getPlayerStat("LVL");
	if (level < 160) {
	    cm.sendNext("Who are you? I can only accept strong adventurers of Level 160 and above!");
	    cm.dispose();
	} else {
	    if (cm.getMapId() != 221000000) {
		cm.sendSimple("So what will you do?\r\n#L0# Retrieve the secret #rZakum treasure#k for the mission#k#l\r\n#L1# Go to Omega Sector now!#k#l");
	    } else {
		cm.sendSimple("So what will you do?\r\n#L0# Retrieve the secret #rZakum treasure#k for the mission#k#l\r\n#L2# Form the squad and take down the #btraining Zakum!#k#k#l");
	    }
	}
    } else if (status == 100) {
	cm.warp(221000000,1);
	cm.dispose();
    } else if (status == 110) {
	if (cm.canHold(3010313)) {
	    if (selection == 1) {
		cm.playerMessage("Agent Macro gave that to me anyway..")
	    }
	    var eim = cm.getEventInstance();
	    if (eim != null) {
		if (eim.getPropertyEx("PlayerGainNX_" + cm.getId()) == 0) { // prevent some map teleport hack or glitch
		    cm.gainItem(3010313,1);
		    eim.setPropertyEx("PlayerGainNX_" + cm.getId(), 1);
		    //		    cm.gainNX(7000); // gained via Event script.
		    cm.increaseWillpowerTrait(40);
		    cm.increaseAmbitionTrait(10);
		}
	    }
	    cm.warp(221000000,1);
	} else {
	    cm.sendNext("Please make space in your SETUP inventory.");
	}
	cm.dispose();
    } else if (status == 3) {
	if (selection == 0) {
	    if (cm.getParty() != null) {
		if (cm.isLeader()) {
		    var q = cm.getEventManager("mob_wipeout");
		    if (q == null) {
			cm.sendOk("Unknown error occured");
		    } else {
			if (cm.allMembersHereWithinLevel(160,200) && cm.isPartyMemberLevelRangeWithin(20)) {
			    q.startInstance(cm.getParty(), cm.getMap());
			} else {
			    cm.sendNext("Some party members may not be in the desired level for this PQ mode or a member of your party's Level is 20 below/ above you.")
			}
		    }
		} else {
		    cm.sendOk("You are not the leader of the party, please ask your leader to talk to me.");
		}
	    } else {
		cm.sendNext("Hey you, you need a party for this secret mission.");
	    }
	    cm.dispose();
	} else if (selection == 1) {
	    if (cm.getMapId() != 221000000) {
		if (cm.haveItem(3800031)) {
		    cm.warp(221000000,1);
		} else {
		    cm.sendNext("You need 5 #i3800031:#! Join me in the secret mission and retrieve the item!");
		}
	    }
	    cm.dispose();
	} else if (selection == 2) {
	    var em = cm.getEventManager("MockZakum");
	    if (em == null) {
		cm.dispose();
		return;
	    }
	    var prop = em.getProperty("state");
	    if (prop == null || prop.equals("0")) {
		var squadAvailability = cm.getSquadAvailability("MockZakum");
		if (squadAvailability == -1) {
		    if (!cm.isLeader()) {
			cm.sendOk("The party leader may apply for the expedition squad leader.");
			cm.dispose();
		    } else {
			status = 9;
			cm.sendYesNo("Are you interested in becoming the leader of the expedition Squad?");
		    }

		} else if (squadAvailability == 1) {
		    // -1 = Cancelled, 0 = not, 1 = true
		    var type = cm.isSquadLeader("MockZakum");
		    if (type == -1) {
			cm.sendOk("The squad has ended, please re-register.");
			cm.dispose();
		    } else if (type == 2) {
			cm.sendOk("The squad have already entered. Please try again later");
			cm.dispose();
		    } else if (type == 0) {
			var memberType = cm.isSquadMember("MockZakum");
			if (memberType == 2) {
			    cm.sendOk("You been banned from the squad.");
			    cm.dispose();
			} else if (memberType == 1) {
			    status = 29;
			    cm.sendSimple("What do you want to do? \r\n#b#L0#Check out members#l \r\n#b#L1#Join the squad#l \r\n#b#L2#Withdraw from squad#l");
			} else if (memberType == -1) {
			    cm.sendOk("The squad has ended, please re-register.");
			    cm.dispose();
			} else {
			    status = 29;
			    cm.sendSimple("What do you want to do? \r\n#b#L0#Check out members#l \r\n#b#L1#Join the squad#l \r\n#b#L2#Withdraw from squad#l");
			}
		    } else { // Is leader
			status = 19;
			cm.sendSimple("What do you want to do? \r\n#b#L0#Check out members#l \r\n#b#L1#Remove member#l \r\n#b#L2#Edit restricted list#l \r\n#r#L3#Enter map#l");
		    // TODO viewing!
		    }
		} else {
		    cm.sendOk("The battle against the boss has already begun.");
		    cm.dispose();
		}
	    } else {
		cm.sendOk("The battle against the boss has already begun.");
		cm.dispose();
		return;
	    }
	}
    } else if (status == 30) {
	if (selection == 0) {
	    if (!cm.getSquadList("MockZakum", 0)) {
		cm.sendOk("Due to an unknown error, the request for squad has been denied.");
		cm.dispose();
	    } else {
		cm.dispose();
	    }
	} else if (selection == 1) { // join
	    if (!cm.get_player_bosslimit(150049,2)) {
		if (cm.getPlayerStat("GM") == 0) {
		    cm.sendOk("You may only face the boss twice per day.");
		    cm.dispose();
		    return;
		}
	    }
	    var ba = cm.addMember("MockZakum", true);
	    if (ba == 2) {
		cm.sendOk("The squad is currently full, please try again later.");
		cm.dispose();
	    } else if (ba == 1) {
		cm.sendOk("You have joined the squad successfully");
		cm.dispose();
	    } else {
		cm.sendOk("You are already part of the squad.");
		cm.dispose();
	    }
	} else {// withdraw
	    var baa = cm.addMember("MockZakum", false);
	    if (baa == 1) {
		cm.sendOk("You have withdrawed from the squad successfully");
		cm.dispose();
	    } else {
		cm.sendOk("You are not part of the squad.");
		cm.dispose();
	    }
	}
    } else if (status == 22) {
	cm.banMember("MockZakum", selection);
	cm.dispose();
    } else if (status == 23) {
	if (selection != -1) {
	    cm.acceptMember("MockZakum", selection);
	}
	cm.dispose();
    } else if (status == 20) {
	if (selection == 0) {
	    if (!cm.getSquadList("MockZakum", 0)) {
		cm.sendOk("Due to an unknown error, the request for squad has been denied.");
		cm.dispose();
	    } else {
		cm.dispose();
	    }
	} else if (selection == 1) {
	    status = 21; // Ban member
	    if (!cm.getSquadList("MockZakum", 1)) {
		cm.sendOk("Due to an unknown error, the request for squad has been denied.");
		cm.dispose();
	    } else {
		cm.dispose();
	    }
	} else if (selection == 2) {
	    status = 22;
	    if (!cm.getSquadList("MockZakum", 2)) {
		cm.sendOk("Due to an unknown error, the request for squad has been denied.");
		cm.dispose();
	    } else {
		cm.dispose();
	    }
	} else if (selection == 3 || selection == 4) { // get insode
	    if (cm.getSquad("MockZakum") != null) {
		var dd = cm.getEventManager("MockZakum");
		if (cm.haveItem(3800031, 5)) {
		    if (!cm.checkAndSet_player_bosslimit(150049,2)) {
			if (cm.getPlayerStat("GM") == 0) {
			    cm.dispose();
			    return;
			}
		    }
		    cm.gainItem(3800031,-5);
		    dd.startInstance(cm.getSquad("MockZakum"), cm.getPlayer());
		} else {
		    cm.sendNext("You need 5 #i3800031:#! Join me in the secret mission and retrieve the item!");
		}
		cm.dispose();
	    } else {
		cm.sendOk("Due to an unknown error, the request for squad has been denied.");
		cm.dispose();
	    }
	} else {
	    cm.dispose();
	}
    } else if (status == 10) {
	if (mode == 1) {
	    if (cm.getPartySize() < 1 || !cm.allMembersHere()) {
		cm.sendOk("Only the leader of the party that consists of 3 or more members is eligible to become the Zakum Squad Leader.");
	    } else {
		if (!cm.get_player_bosslimit(150049,2)) {
		    if (cm.getPlayerStat("GM") == 0) {
			cm.sendOk("You may only face the boss twice per day.");
			cm.dispose();
			return;
		    }
		}
		if (cm.haveItem(3800031, 5)) {
		    cm.registerSquad("MockZakum", 5, " has been registered as the leader of the squad. If you would you like to join please register for the Expedition Squad within the time period.");
		    cm.sendOk("You have been registered as the leader of the Squad. For the next 5 minutes, you can add the members of the Expedition Squad.");
		} else {
		    cm.sendNext("You need 5 #i3800031:#! Join me in the secret mission and retrieve the item!");
		}
	    }
	} else {
	    cm.sendOk("Talk to me if you want to become the leader of the Expedition squad.")
	}
	cm.dispose();
    }
}

function enterbat() {
    var eim = cm.enterSquad_instance("MockZakum");
    if (eim != null) {
	if (!cm.checkAndSet_player_bosslimit(150049,2)) { // hack perhaps
	    if (cm.getPlayerStat("GM") == 0) {
		cm.dispose();
		cm.playerMessage("You have exceeded the daily limitation of the boss.");
		return true;
	    }
	}
	eim.registerPlayer(cm.getPlayer());
	cm.dispose();
	return true;
    }
    return false;
}