/*
 * Miracle cube fragment
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
	    if (cm.haveItem(2430112, 10)) {
		var str = "";
		str += "\r\n#b#L0# Trade 10 #i2430112:# #t2430112# for \r\n#i2049401:# #t2049401#";

		if (cm.haveItem(2430112, 20)) {
		    str += "\r\n#b#L1# Trade 20 #i2430112:# #t2430112# for \r\n#i2049400:# #t2049400#";
		}
		cm.sendSimple(str);
	    } else {
		cm.sendOk("You do not have at least 10 #t2430112#, to make potential scrolls.");
		cm.dispose();
	    }
	    break;
	case 1:
	    switch (selection) {
		case 0:
		    if (cm.haveItem(2430112, 10)) {
			cm.gainItem(2430112, -10);
			cm.gainItem(2049401, 1);
		    }
		    break;
		case 1:
		    if (cm.haveItem(2430112, 20)) {
			cm.gainItem(2430112, -20);
			cm.gainItem(2049400, 1);
		    }
		    break;
	    }
	    cm.dispose();
	    break;
    }
}