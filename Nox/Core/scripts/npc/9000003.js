/*
 * Vikan NPC, Event map
 */

var status = -1;

function action(mode, type, selection) {
    if (mode == 1) {
	status++;
    } else {
	status--;
    }
    // 4031018
    switch (status) {
	case 0:
	    if (!cm.haveItem(4031018)) {
		cm.sendNext("Hey hey!!! Find the Treasure Scroll! I lost the scroll somewhere and I can't leave without it!");
		cm.dispose();
	    } else {
		cm.sendYesNo("Hey, I see you have #bTreasure Scroll#k. Would you mind returning me back the lost scroll?");
	    }
	    break;
	case 1:
	    var item;
	    if (Math.random() * 100 <= 5) {
		item = cm.gainGachaponItem(3010025, 1);
	    } else {
		var itemlist = new Array(4170023, 2020032, 2020020, 2020031);
		item = cm.gainGachaponItem(itemlist[Math.floor(Math.random() * itemlist.length)], 1);
	    }
	    if (item != -1) {
		cm.gainItem(4031018, -1);
		cm.sendOk("You have obtained #b#t" + item + "##k.");
	    } else {
		cm.sendOk("Please check your item inventory and see if you have the Treasure Scroll, or if the inventory is full.");
	    }
	    cm.dispose();
	    break;
    }
}
