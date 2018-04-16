/* Paris NPC
	Costume borrow
*/

var status = -1;

function action(mode, type, selection) {
    if (mode == 1) {
	status++;
    } else {
	cm.sendOk("Talk to me if you want to borrow a costume!");
	cm.dispose();
	return;
    }
    if (status == 0) {
	cm.askAcceptDecline("Are you here to take part in the wedding? It is the best to dress up yourself before taking part in the wedding ceremony. We specially made some gorgeous suits with low price. Just #b500 Meso#k is enough to borrow the suit for 30 minutes. Would you like to borrow one?");
    } else if (status == 1) {
	if (cm.getPlayerStat("GENDER") == 0) {
	    cm.sendSimple("Please choose the suit you want!#b \r\n#L0##i1050131:# #t1050131##l \r\n#L1##i1050132:# #t1050132##l \r\n#L2##i1050133:# #t1050133##l \r\n#L3##i1050134:# #t1050134##l");
	} else {
	    cm.sendSimple("Please choose the dress you want!#b \r\n#L0##i1051150:# #t1051150##l \r\n#L1##i1051151:# #t1051151##l \r\n#L2##i1051152:# #t1051152##l \r\n#L3##i1051153:# #t1051153##l");
	}
    } else {
	var item = -1;

	if (cm.getPlayerStat("GENDER") == 0) {
	    switch (selection) {
		case 0:
		    item = 1050131;
		    break;
		case 1:
		    item = 1050132;
		    break;
		case 2:
		    item = 1050133;
		    break;
		case 3:
		    item = 1050134;
		    break;
	    }
	} else {
	    switch (selection) {
		case 0:
		    item = 1051150;
		    break;
		case 1:
		    item = 1051151;
		    break;
		case 2:
		    item = 1051152;
		    break;
		case 3:
		    item = 1051153;
		    break;
	    }
	}

	if (item != -1) {
	    cm.gainItem(item,1, false, 1000 * 60 * 20);
	}
	cm.sendNext("What a great choice. You look very nice in that dress or suit.");
	cm.dispose();
    }
}