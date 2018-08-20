/*
 * Cellion Cubing NPC - Part 1
 * @author Mazen Massoud
 */
 
var nSlot = Array();
var nSelectedCube;

var aCubeID = [2710002, 5062009, 5062006, 5062500];
var aCubeCosts = [2000, 10000, 30000, 30000];

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
		pInventory = cm.getInventory(1);
		var bCanCube = false;
		var selStr = "Which item would you like to cube?\r\n#b";
		for (var i = 0; i <= pInventory.getSlotLimit(); i++) {
			nSlot.push(i);
			var pItem = pInventory.getItem(i);
			
			if (aCubeID[nSelectedCube] == 5062500) { 					// Bonus Potential
				if (pItem == null || pItem.getBonusPotential1() == 0) {
					continue;
				}
			} else { 													// Normal Potential
				if (pItem == null || pItem.getPotential1() == 0) {
					continue;
				}
				/*if (aCubeID[nSelectedCube] == 2710002) { 				// No legendary equips for Craftsman's Cubes.
					if (pItem == null || pItem.hasLegendaryPotential) {
						continue;
					}
				}*/
			}
			
			nItemID = pItem.getItemId();
			bCanCube = true;
			selStr += "#L" + i + "##v" + nItemID + "# #t" + nItemID + "##l\r\n";
		}
		if (!bCanCube) {
			cm.sendOk("Looks like you don't have any equips with potential lines on them.\r\n#dTry using a Potential Scroll on your equipment first!");
			cm.dispose();
			return;
		}
		cm.sendSimple(selStr + "#k");
	} else if (status == 2) {
		cm.getPlayer().OnSetCubeInfo(selection, aCubeID[nSelectedCube]);
		cm.dispose();
		cm.openNpc(9000020);
	} 
}
