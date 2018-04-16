/*
 * Lion King Medal
 */

var status = -1;

function action(mode, type, selection) {
    if (mode == 1) {
	status++;
    } else {
	status--;
    }
    switch (status) {
	case 0:
	    if (cm.haveItem(4000630, 50)) {
		var str = "";
		str += "\r\n#b#L0# Trade 50 #i4000630:# #t4000630# for \r\n#i4310009:# #t4310009#";

		if (cm.haveItem(4000630, 100)) {
		    str += "\r\n#b#L1# Trade 100 #i4000630:# #t4000630# for \r\n#i4310010:# #t4310010#";
		}
		cm.sendSimple(str);
	    } else {
		cm.sendOk("You do not have at least 50 #t4000630# to make Lion King Medal.");
		cm.dispose();
	    }
	    break;
	case 1:
	    switch (selection) {
		case 0:
		    if (cm.canHold(4310009)) {
			if (cm.haveItem(4000630, 50)) {
			    cm.gainItem(2430158,-1);
			    cm.gainItem(4000630, -50);
			    cm.gainItem(4310009, 1);
			}
		    } else {
			cm.sendNext("Please make space in your ETC inventory.");
		    }
		    break;
		case 1:
		    if (cm.canHold(4310010)) {
			if (cm.haveItem(4000630, 100)) {
			    cm.gainItem(2430158,-1);
			    cm.gainItem(4000630, -100);
			    cm.gainItem(4310010, 1);
			}
		    } else {
			cm.sendNext("Please make space in your ETC inventory.");
		    }
		    break;
	    }
	    cm.dispose();
	    break;
    }
}