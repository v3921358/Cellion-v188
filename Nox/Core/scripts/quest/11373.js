/*
	NPC Name: 		Gaga
	Description: 		Monkey Dolls Day 3
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
	    qm.sendSimple("Now select an item you like.#b\r\n#L0# 30 Power Elixir and Scroll for Armor for ATT#l\r\n#L1# 30 Power Elixir and Scroll for Armor for M.AT#l");
	    break;
	case 1:
	    var itemid = -1;
	    if (selection == 0) {
		itemid = 2046227;
	    } else {
		itemid = 2046228;
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