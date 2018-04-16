/* Grant - Professions
*/
var status = -1;

function action(mode, type, selection) {
    if (mode == 1)
	status++;
    else {
	cm.sendOk("Go ahead and enter.");
	cm.dispose();
	return;
    }
    
    if (status == 0) {
	if (cm.getQuestStatus(3195) == 1 || cm.getQuestStatus(3196) == 1) {
	    cm.sendYesNo("Do you want to go to Saffron's Herb Patch? You must learn about Herbalism from Saffron before you can do so.");
	} else if (cm.getQuestStatus(3197) == 1 || cm.getQuestStatus(3198) == 1) {
	    cm.sendYesNo("Do you want to go to Cole's Mine? You must learn about Mining from Cole before you can do so.");
	    status = 9;
	} else {
	    if (cm.getSkillLevel(92000000) > 0) {
		status = 99;
		cm.sendSimple("Where do you want to go?\r\n\r\n#L0##bNovice Secret Herb Patch#k (Silver Herb, Magenta Herb)#b#l\r\n#L1#Intermediate Secret Herb Patch #k(Blue Herb, Brown Herb)#l#k");
	    } else if (cm.getSkillLevel(92010000) > 0) {
		status = 89;
		cm.sendSimple("Where do you want to go?\r\n\r\n#L0##bNovice Secret Mine#k (Silver Vien, Magenta Vien)#b#l\r\n#L1#Intermediate Secret Mine #k(Blue Vien, Brown Vien)#l#k");
	    } else {
		cm.playerMessage("There is nothing else you can do here...");
		cm.dispose();
	    }
	}
    } else if (status == 1) {
	cm.warp(910001001,1);
	cm.dispose();
    } else if (status == 10) {
	cm.warp(910001002,1);
	cm.dispose();
    } else if (status == 90) {
	var mapid, itemid, msg;
	if (selection == 0) {
	    itemid = 4001480;
	    mapid = 910001005;
	    msg = "You will now enter the Novice Secret Mine. You will not be able to re-enter if you come out.";
	} else {
	    itemid = 4001481;
	    mapid = 910001006;
	    msg = "You will now enter the Intermediate Secret Mine. You will not be able to re-enter if you come out.";
	}
	if (cm.haveItem(itemid)) {
	    if (cm.checkAndSet_player_bosslimit(150035,1)) {
		cm.gainItem(itemid, -1);
		cm.warp(mapid,1);
		cm.playerMessage(msg);
	    } else {
		cm.playerMessage("You can only enter the Secret Mine once per day.");
	    }
	} else {
	    cm.playerMessage("You need the ticket to enter this place.");
	}
	cm.dispose();
    } else if (status == 100) {
	var mapid, itemid, msg;
	if (selection == 0) {
	    itemid = 4001482;
	    mapid = 910001003;
	    msg = "You will now enter the Novice Secret Herb Patch. You will not be able to re-enter if you come out.";
	} else {
	    itemid = 4001483;
	    mapid = 910001004;
	    msg = "You will now enter the Intermediate Secret Herb Patch. You will not be able to re-enter if you come out.";
	}
	if (cm.haveItem(itemid)) {
	    if (cm.checkAndSet_player_bosslimit(150035,1)) {
		cm.gainItem(itemid, -1);
		cm.playerMessage(msg);
		cm.warp(mapid,1);
	    } else {
		cm.playerMessage("You can only enter the Secret Herb Patch once per day.");
	    }
	} else {
	    cm.playerMessage("You need the ticket to enter this place.");
	}
	cm.dispose();
    }
}