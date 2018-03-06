/*
 *Rexion NX shop
 *@author Mazen
 */


function start() {
    cm.sendNext("Welcome to the #dREXION Quick Access Cash Shop#k!\r\n\r\n#rHere you can buy useful cash items quickly for NX without needing to visit the cash shop.");
	status = -1;
}

var nQuantity;
var nCost;
var nChoice;

function action(mode, type, selection){
	if (mode == 1) {
		status++;
	} else {
		if (status == 0){
			cm.sendNextS("Enjoy your adventure!", 5);
            cm.dispose();
		}
        status -= 1;
	}
	
	if (status == 0) {
		text = "What are you interested in purchasing?\r\n#r"
			 + "#r#i5062009##L5062009#1,200 NX - Red Cube#l\r\n"
			 + "#g#i5062500##L5062500#2,400 NX - Bonus Potential Cube#l\r\n"
			 + "#d#i2048306##L2048306#3,600 NX - Bonus Potential Scroll#l\r\n"
		;
		cm.sendSimple(text);
	} else if (status == 1) {
		nChoice = selection;
		cm.sendGetText("How many #i" + selection + "# would you like to purchase?");
	} else if (status == 2) {
		nQuantity = cm.getText();
		switch(nChoice) {
			case 5062009:
				nCost = 1200 * nQuantity;
				break;
			case 5062500:
				nCost = 2400 * nQuantity;
				break;
			case 2048306:
				nCost = 3600 * nQuantity;
				break;
		}
		if (nQuantity > 0 && nQuantity <= 1000) {
			cm.sendYesNo("So, you would like to purchase #d" + nQuantity + "#k for #r" + nCost + "#k NX?");
		} else {
			cm.sendOk("Please enter a value between #d1#k and #d1000#k.");
			cm.dispose();
		}
	} else if (status == 3) {
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
