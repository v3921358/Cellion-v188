/*
 * Cellion Cubing NPC
 * @author Mazen Massoud
 */
 
var slot = Array();

var aCubeID = [2710002, 5062009, 5062006, 5062500];
var aCubeCosts = [2000, 10000, 30000, 30000];
var nSelectedCube;

function start() {
	text = "\r\nWelcome to the #dCELLION Quick Cube System#k!\r\n#rHere you can quickly reroll potential lines to your equipment.\r\n\r\n\t";
	for (var i = 0; i < aCubeID.length; i++) {
		text += " #i" + aCubeID[i] + "# ";
	}
	
    cm.sendNext(text);
	status = -1;
}

function action(mode, type, selection){
	if (mode != 1) {
        cm.dispose();
        return;
    }
	status++;
	
	if (status == 0) {
		text = "Which potential cube do you wish to use?\r\n#d";
		for (var i = 0; i < aCubeID.length; i++) {
			text += "\t#L" + i + "##i" + aCubeID[i] + "# #t" + aCubeID[i] + "##l\r\n";
		}
		cm.sendSimple(text);
	} else if (status == 1) {
		nSelectedCube = selection;
		inv = cm.getInventory(1);
		var bbb = false;
		var selStr = "Which item would you like to cube?\r\n#b";
		for (var i = 0; i <= inv.getSlotLimit(); i++) {
			slot.push(i);
			var it = inv.getItem(i);
			if (aCubeID[nSelectedCube] != 5062500) { // Not Bonus Potential
				if (it == null || it.getPotential1() == 0) {
					continue;
				}
			} else {
				if (it == null || it.getBonusPotential1() == 0) {
					continue;
				}
			}
			itemid = it.getItemId();
			bbb = true;
			selStr += "#L" + i + "##v" + itemid + "# #t" + itemid + "##l\r\n";
		}
		if (!bbb) {
			cm.sendOk("Looks like you don't have any equips with potential lines on them.\r\n#dTry using a Potential Scroll on your equipment first!");
			cm.dispose();
			return;
		}
		cm.sendSimple(selStr + "#k");
	} else if (status == 2) {
		cube: 
		OnCubeResult(selection, aCubeID[nSelectedCube]);
	} else if (status == 3) {
		continue cube;
	}
}

function OnCubeResult(nEquipSlot, nSelectedCube) {
	inv = cm.getInventory(1);
	var it = inv.getItem(nEquipSlot);
	
	cm.getPlayer().yellowMessage("selection: " + nEquipSlot + " / cube: " + aCubeID[nSelectedCube]);
	if (cm.haveItem(aCubeID[nSelectedCube])) {
		cm.OnCubeRequest(nEquipSlot, aCubeID[nSelectedCube]);
		cm.gainItem(aCubeID[nSelectedCube], -1);
	} else if (cm.getPlayer().getCSPoints(2) > aCubeCosts[nSelectedCube]) {
		cm.OnCubeRequest(nEquipSlot, aCubeID[nSelectedCube]);
		cm.getPlayer().gainNX(-(aCubeCosts[nSelectedCube]), true);
	}
	if (aCubeID[nSelectedCube] != 5062500) { // Not Bonus Potential
		sResults = "Potential Cube Results#r\r\n\r\n\t" 
					+ it.getPotential1() + "\r\n\t" 
					+ it.getPotential2() + "\r\n\t" 
					+ it.getPotential3();
	} else {
		sResults = "Potential Cube Results#d\r\n\r\n\t" 
					+ it.getBonusPotential1() + "\r\n\t" 
					+ it.getBonusPotential2() + "\r\n\t" 
					+ it.getBonusPotential3();
	}
	cm.sendYesNo(sResults);
}
