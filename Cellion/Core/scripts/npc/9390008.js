var status = -1;

function action(mode, type, selection) {
    if (cm.getPlayer().getMapId() == 861000100 || cm.getPlayer().getMapId() == 861000200 || cm.getPlayer().getMapId() == 861000300 || cm.getPlayer().getMapId() == 861000400 || cm.getPlayer().getMapId() == 861000500) {
cm.warp(861000000);
cm.dispose();
    }
    if (mode == 1) {
	status++;
    } else {
	if (status == 0) {
	    cm.dispose();
	}
	status--;
    }

    if (status == 0) {
	cm.sendSimple("A strange magnetic field is emanating from inside the spaceship I need someone strong to carry out some experiments.\r\n\r\n#b#L2##eEnter the spaceship.#l#k");
    } else if (status == 1) {
	if (selection == 2) {
	    if (cm.getPlayer().getParty() == null || !cm.isLeader()) {
		cm.sendOk("The leader of the party must be here.");
	    } else {
		var party = cm.getPlayer().getParty().getMembers();
		var mapId = cm.getPlayer().getMapId();
		var next = true;
		var size = 0;
		var it = party.iterator();
		while (it.hasNext()) {
			var cPlayer = it.next();
			var ccPlayer = cm.getPlayer().getMap().getCharacterById(cPlayer.getId());
			if (!cm.getPlayer().havePartyItem(4001432,1)) {
			next = false;
			break;
			}
			if (ccPlayer == null || ccPlayer.getLevel() < 235) {
				next = false;
				break;
			}

			size += (ccPlayer.isGM() ? 4 : 1);
		}	
		if (next && size >= 2) {
			var em = cm.getEventManager("2095_tokyo");
			if (em == null) {
				cm.sendOk("2095_tokyo is fine at the moment. Please try again later.");
			} else {
		    var prop = em.getProperty("state");
		    if (prop.equals("0") || prop == null) {
			cm.givePartyItems(4001432, -1);
			em.startInstance(cm.getPlayer().getParty(), cm.getPlayer().getMap(), 200);
			
		    } else {
			cm.sendOk("Another party quest has already entered this channel.");
		    }
			}
		} else {
			cm.sendOk("All 2+ members of your party must be here and level 235 or greater, And posses #i4001432#");
		}
	    }
	} else if (selection == 3) {
		if (!cm.canHold(1022123,1)) {
			cm.sendOk("Make room in Equip.");
		} else if (cm.haveItem(4001535,50)) { //TODO JUMP
			cm.gainItem(1022123, 1);
			cm.gainItem(4001535, -50);
		} else {
			cm.sendOk("Come back with 50 Pianus Scale.");
		}
	} else if (selection == 4) {
		if (!cm.canHold(2048010,1) || !cm.canHold(2048011,1) || !cm.canHold(2048012,1) || !cm.canHold(2048013,1)) {
			cm.sendOk("Make room in Use.");
		} else if (cm.haveItem(4001535,5)) { //TODO JUMP
			cm.gainItem(2048010 + java.lang.Math.floor(java.lang.Math.random() * 4) | 0, 1);
			cm.gainItem(4001535, -5);
		} else {
			cm.sendOk("Come back with 5 Pianus Scale.");
		}
	}
	cm.dispose();
    }

}