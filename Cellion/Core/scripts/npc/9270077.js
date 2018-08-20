/*
 * Naomi NPC [Event map]
 */

var status = -1;

function action(mode, type, selection) {
    if (mode == 1) {
	status++;
    } else {
	cm.sendOk("You must have something else to do!");
	cm.dispose();
	return;
    }
    switch (status) {
	case 0:
	    cm.sendSimple("Hello there, what would you like to do? \r\n#L0#I would like to have the rewards for #bStone of Virtue#k event#l\r\n#L1#I would like to have #bStone of Virtue#k#l");
	    break;
	case 1:
	    if (selection == 0) {
		if (cm.getNumFreeSlot(1) < 2 || cm.getNumFreeSlot(2) < 2 || cm.getNumFreeSlot(3) < 2 || cm.getNumFreeSlot(4) < 2) {
		    cm.sendNext("You need 2 slot from Equip, Use, Etc, Setup to proceed.");
		    cm.dispose();
		    return;
		}
		var record = cm.getQuestRecord(11300);
		var data = record.getInfoData();
		if (data == null) {
		    data = "1";
		}
		var intdata = parseInt(data);
		
		if (cm.getQuestStatus(11303) == 0 && intdata >= 3) {
		    cm.gainItem(1122171,1,1000 * 60 * 60 * 24 * 1)
		    cm.forceCompleteQuest(11303);
		} else {
		    if (cm.getQuestStatus(11304) == 0 && intdata >= 4) {
			cm.gainItem(2001505,200)
			cm.forceCompleteQuest(11304);
		    } else {
			if (cm.getQuestStatus(11305) == 0 && intdata >= 5) {
			    cm.gainItem(1122172,1,1000 * 60 * 60 * 24 * 1)
			    cm.forceCompleteQuest(11305);
			} else {
			    if (cm.getQuestStatus(11306) == 0 && intdata >= 6) {
				cm.gainItem(2430370,1)
				cm.forceCompleteQuest(11306);
			    } else {
				if (cm.getQuestStatus(11307) == 0 && intdata >= 7) {
				    cm.gainItem(2430369,1)
				    cm.gainItem(3700003,1)
				    cm.forceCompleteQuest(11307);
				} else {
				    if (cm.getQuestStatus(11308) == 0 && intdata >= 8) {
					cm.gainItem(2049303,1)
					cm.forceCompleteQuest(11308);
				    } else {
					if (cm.getQuestStatus(11309) == 0 && intdata >= 9) {
					    cm.gainItem(2049402,1,false, 1000 * 60 * 60 * 24 * 5)
					    cm.forceCompleteQuest(11309);
					} else {
					    if (cm.getQuestStatus(11310) == 0 && intdata >= 10) {
						cm.gainItem(1032105,1)
						cm.gainItem(3700004,1)
						cm.forceCompleteQuest(11310);
					    } else {
						cm.sendNext("You have already acquired all the rewards.");
					    }
					}
				    }
				}
			    }
			}
		    }
		}
		cm.playerMessage("You have currently participated the event for "+data+" days.");
	    } else if (selection == 1) {
		cm.sendNext("The event has ended.");
		cm.removeAll(4001527);
		cm.removeAll(4162001);
		
/*		if (cm.getNumFreeSlot(4) > 2) {
		    cm.gainItem(4001527,1,false,1000 * 60 * 60 * 24 * 5);
		    cm.gainItem(4162001,1,false,1000 * 60 * 60 * 24 * 5);
		} else {
		    cm.sendNext("Make 2 empty space in your ETC inventory.");
		}*/
	    }
	    cm.dispose();
	    break;
    }
}
