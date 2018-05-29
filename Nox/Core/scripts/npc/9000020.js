/*
 * Cellion Cubing NPC Part 2
 * @author Mazen Massoud
 */

status = -1;

var bFreeMarket = true; // If true, can only be used in the Free Market.

var sLine1 = "";
var sLine2 = "";
var sLine3 = "";
var sBonusLine1 = "";
var sBonusLine2 = "";
var sBonusLine3 = "";

function action(mode, type, selection) {
    if (mode != 1) {
		cm.getPlayer().OnSetCubeInfo(0, 0);
        cm.dispose();
        return;
    }
    if (!(cm.getPlayer().nEquipSlotForCube > 0) && !(cm.getPlayer().nSelectedCubeID > 0)) {
        cm.dispose();
        return;
    }
	
	if (bFreeMarket && cm.getPlayer().getMap().getId() != 910000000) {
		cm.getPlayer().OnSetCubeInfo(0, 0);
		cm.dispose();
        return;
	}
	
    status++;
	
    if (status == 0) {
		
		pInventory = cm.getInventory(1);
		var pItem = pInventory.getItem(cm.getPlayer().nEquipSlotForCube);
		
		switch (cm.getPlayer().nSelectedCubeID) {
			case 2710002:
				nNXCost = 2000;
				break;
			case 5062009:
				nNXCost = 10000;
				break;
			case 5062006:
				nNXCost = 30000;
				break;
			case 5062500:
				nNXCost = 30000;
				break;
			default:
				nNXCost = 0;
				break;
		}
		
		if (cm.haveItem(cm.getPlayer().nSelectedCubeID)) {
			cm.OnCubeRequest(cm.getPlayer().nEquipSlotForCube, cm.getPlayer().nSelectedCubeID);
			cm.gainItem(cm.getPlayer().nSelectedCubeID, -1);
		} else if (cm.getPlayer().getCSPoints(2) > nNXCost) {
			cm.OnCubeRequest(cm.getPlayer().nEquipSlotForCube, cm.getPlayer().nSelectedCubeID);
			cm.getPlayer().gainNX(-nNXCost, true);
		} else {
			cm.sendOk("Sorry, looks like you don't have enough NX or any cubes left.");
			cm.getPlayer().OnSetCubeInfo(0, 0);
			cm.dispose();
		}
		
		sLine1 = cm.OnReadPotential(pItem.getPotential1());
		sLine2 = cm.OnReadPotential(pItem.getPotential2());
		sLine3 = cm.OnReadPotential(pItem.getPotential3());
		
		sBonusLine1 = cm.OnReadPotential(pItem.getBonusPotential1());
		sBonusLine2 = cm.OnReadPotential(pItem.getBonusPotential2());
		sBonusLine3 = cm.OnReadPotential(pItem.getBonusPotential3());
		
		if (cm.getPlayer().nSelectedCubeID != 5062500) { // Not Bonus Potential
			sResults = "Potential Results#d\r\n\r\n\t#e#fs12#" 
						+ sLine1 + "\r\n\t" 
						+ sLine2 + "\r\n\t" 
						+ sLine3 + "#fs11#";
		} else {
			sResults = "Bonus Potential Results#g\r\n\r\n\t#e#fs12#" 
						+ sBonusLine1 + "\r\n\t" 
						+ sBonusLine2 + "\r\n\t" 
						+ sBonusLine3 + "#fs11#";
		}
		
		sResults += "\r\n\r\n\r\n\t #i" + pItem.getItemId() + "#\t\t\t\t\t\t\t#k#nWould you like to use another cube?";
		cm.sendYesNoS(sResults, 2);
		
    } else if (status == 1) {
		
        cm.dispose();
		cm.openNpc(9000020);
		
    } 
}