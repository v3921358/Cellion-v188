/* 
 *	NPC (9010017) Dev Doll
 *	Drop Cash Items
 */

var status = 0;
var invs = Array(1, 5);
var invv;
var selected;
var slot_1 = Array();
var slot_2 = Array();
var statsSel;

function start() {
	action(1,0,0);
}

function action(mode, type, selection) {
	if (mode != 1) {
		cm.dispose();
		return;
	}
	status++;
	if (status == 1) {
		var bbb = false;
		var selStr = "#dCash#k items can't be dropped? Let's break the rules #b;)#k.\r\n#rWhich item would you like to drop?#k\r\n#d";
		for (var x = 0; x < invs.length; x++) {
			var inv = cm.getInventory(invs[x]);
			for (var i = 0; i <= inv.getSlotLimit(); i++) {
				if (x == 0) {
					slot_1.push(i);
				} else {
					slot_2.push(i);
				}
				var it = inv.getItem(i);
				if (it == null) {
					continue;
				}
				var itemid = it.getItemId();
				if (!cm.isCash(itemid)) {
					continue;
				}
				if (itemid == 5211111 || itemid == 5211122) { // Block Exp Coupons
					continue;
				}
				bbb = true;
				selStr += "#L" + ((invs[x] * 1000) + i) + "##v" + itemid + "#  #t" + itemid + "##l\r\n";
			}
		}
		if (!bbb) {
			cm.sendOk("Doesn't look like you have any #dCash#k items.");
			cm.dispose();
			return;
		}
		cm.sendNextPrevS(selStr + "#k", 2);
		//cm.sendSimple(selStr + "#k");
	} else if (status == 2) {
		invv = (selection / 1000) | 0;
		selected = (selection % 1000) | 0;
		var inzz = cm.getInventory(invv);
		if (selected >= inzz.getSlotLimit()) { 
			cm.sendOk("Oops, something went wrong.");
			cm.dispose();
			return;
		}
		if (invv == invs[0]) {
			statsSel = inzz.getItem(slot_1[selected]);
		} else if (invv == invs[1]) {
			statsSel = inzz.getItem(slot_2[selected]);
		}
		if (statsSel == null) {
			cm.sendOk("Oops, something went wrong.");
			cm.dispose();
			return;
		}
		cm.sendGetNumber("#v" + statsSel.getItemId() + "#  #d#t" + statsSel.getItemId() + "##k\r\nHow many would you like to drop?\r\n ", 1, 1, statsSel.getQuantity());
	} else if (status == 3) {
		if (!cm.dropItem(selected, invv, selection)) {
			cm.sendOk("Oops, something went wrong.");
			cm.dispose();
		} else {
			status = 0;
			action(1, 0, 0);
		}
	}
}