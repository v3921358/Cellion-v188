/*
	NPC Name: 		Gaga
	Description: 		Monkey Dolls Day 5
*/

var status = -1;

function start(mode, type, selection) {
}

function end(mode, type, selection) {
    if (mode == 1) {
	status++;
    } else {
	status--;
    }
    switch (status) {
	case 0:
	    qm.sendSimple("Now select an item you like.#b\r\n#L0# 50 Power Elixir and Scroll for Gloves for ATT#l\r\n#L1# 50 Power Elixir and Scroll for Gloves for M.ATT#l");
	    break;
	case 1:
	    var itemid = -1;
	    if (selection == 0) {
		itemid = 2040804;
	    } else {
		itemid = 2040817;
	    }
	    if (qm.getNumFreeSlot(2) >= 2) {
		qm.forceCompleteQuest();
		qm.gainItem(itemid,1);
		qm.gainItem(2000005, 30);
	    } else {
		qm.sendNext("Please make space in your USE inventory.");
	    }
	    qm.dispose();
	    break;
    }
}