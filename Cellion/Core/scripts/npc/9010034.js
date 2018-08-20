/*
 * Cellion NX shop
 * @author Mazen Massoud
 */


function start() {
    cm.sendNext("Welcome to the #dCELLION Quick Access Cash Shop#k!\r\n\r\n#rHere you can buy useful cash items quickly for NX without needing to visit the cash shop.");
	status = -1;
}

var nQuantity;
var nCost;
var nChoice;

function action(mode, type, selection){
	if (mode != 1) {
        cm.dispose();
        return;
    }
	status++;
	
	if (status == 0) {
		text = "What are you interested in purchasing?\r\n\r\n#r"
			 + "#d#i2710002##L2710002# 2,000 NX - Master Craftsman's Cube#l\r\n#k"
			 + "#r#i5062009##L5062009# 10,000 NX - Red Cube#l\r\n#k"
			 + "#b#i5062006##L5062006# 30,000 NX - Platinum Miracle Cube#l\r\n#k"
			 + "#g#i5062500##L5062500# 30,000 NX - Bonus Potential Cube#l\r\n#k"
			 
			 + "\r\n________________________________________________\r\n\r\n"
			 
			 + "#d#i5062300# #L5062300# 10,000 NX - Potential Stamp#l\r\n#k"
			 + " #d #i2049419 ##L2049419# 10,000 NX - Potential Scroll#l\r\n#k"
			 + "#d#i2048306##L2048306# 200,000 NX - Bonus Potential Scroll#l\r\n#k"
			 
			 + "\r\n________________________________________________\r\n\r\n"
			 
			 + "#d #i5520000# #L5520000# 2,000 NX - Scissors of Karma#l\r\n#k"
			 + "#d#i5520001# #L5520001# 5,000 NX - Platinum Scissors of Karma#l\r\n#k"
			 
			 + "\r\n________________________________________________\r\n\r\n"
			 
			 + "#d #i5076000# #L5076000# 2,000 NX - Item Megaphone#l\r\n#k"
			 + "#d#i5040004##L5040004# 30,000 NX - Hyper Teleport Rock (3 Days)#l\r\n#k"
		;
		cm.sendSimple(text);
	} else if (status == 1) {
		nChoice = selection;
		cm.sendGetText("How many #i" + selection + "# would you like to purchase?");
	} else if (status == 2) {
		nQuantity = cm.getText();
		switch(nChoice) { 
			case 2710002: // Craftsman Cube
				nCost = 2000 * nQuantity;
				break;
			case 5062009: // Red Cube
				nCost = 10000 * nQuantity;
				break;
			case 5062006: // Platinum Miracle Cube
				nCost = 30000 * nQuantity;
				break;
			case 5062500: // Bonus Cube
				nCost = 30000 * nQuantity;
				break;
			case 5062300: // Potential Stamp
				nCost = 10000 * nQuantity;
				break;
			case 2049419: // Potential Scroll
				nCost = 10000 * nQuantity;
				break;
			case 2048306: // Bonus Potential Scroll
				nCost = 200000 * nQuantity;
				break;
			case 5520000: // Scissors of Karma
				nCost = 25000 * nQuantity;
				break;
			case 5520001: // Platinum Scissors of Karma
				nCost = 50000 * nQuantity;
				break;
			case 5076000: // Item Megaphone
				nCost = 2000 * nQuantity;
				break;
			case 5040004: // Hyper Teleport Rock
				nCost = 30000 * nQuantity;
				break;
		}
		if (nQuantity > 0 && nQuantity <= 1000) {
			cm.sendYesNo("So, you would like to purchase #d" + nQuantity + "#k for #r" + nCost + "#k NX?");
		} else {
			cm.sendOk("Please enter a value between #d1#k and #d1000#k.");
			cm.dispose();
		}
	} else if (status == 3) {
		if (nChoice == 5040004) {
			
			if (cm.getPlayer().getCSPoints(2) >= nCost) {
				cm.getPlayer().modifyCSPoints(2, -nCost, true);
				cm.gainItemPeriod(nChoice, nQuantity, 72, true); //item id, amount, time, is hours?
				cm.sendOk("You have successfully purchased #b" + nQuantity + "#k #i" + nChoice + "# .");
				cm.dispose();
			} else {
				cm.sendOk("Sorry, looks like you don't have enough #rNX#k to make that purchase.");
				cm.dispose();
			}
			
		} else {
			
			if (cm.getPlayer().getCSPoints(2) >= nCost) {
				cm.getPlayer().modifyCSPoints(2, -nCost, true);
				cm.gainItem(nChoice, nQuantity);
				cm.sendOk("You have successfully purchased #b" + nQuantity + "#k #i" + nChoice + "# .");
				cm.dispose();
			} else {
				cm.sendOk("Sorry, looks like you don't have enough #rNX#k to make that purchase.");
				cm.dispose();
			}
		}
	}
}
