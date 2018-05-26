/*
 *Cellion Crafting NPC
 *@author Arcas
 */

//Setup for the store

//Pearl
var pearlEqp = [1003864, 1052613, 1102563, 1012377, 1132229, 1122253];
var pearlWep = [1302278, 1212067, 1222062, 1232061, 1242066, 1252065, 1312156, 1322206, 1332228, 1362093, 1372180, 1382212, 1402200, 1412138, 1422143, 1432170, 1442226, 1452208, 1462196, 1472217, 1482171, 1492182, 1522097, 1532101, 1542069, 1552069];
//Onyx
var onyxEqp = [1003863, 1052612, 1102562, 1012376, 1132228, 1122252];
var onyxWep = [1302277, 1212066, 1222061, 1232060, 1242065, 1252064, 1312155, 1322205, 1332227, 1362092, 1372179, 1382211, 1402199, 1412137, 1422142, 1432169, 1442225, 1452207, 1462195, 1472216, 1472216, 1492181, 1522096, 1532100, 1542070, 1552070];

var craftOptions = ["Lv. 30 Pearl Equipment and Accessories", "Lv. 30 Pearl Weapon", "Lv. 100 Onyx Equipment and Accessories", "Lv. 100 Onyx Weapon"];

var textBox = "";
var j = 0;

function start() {
    cm.sendNext("Welcome to the #dCELLION#k Maple Leaf workshop.\r\nHere you can craft all the strongest gear!");
	status = -1;
}

function action(mode, type, selection){
	if (mode == 1) {
		status++;
	} else {
		if (status == 0){
			cm.sendNextS("Enjoy your adventure.", 5);
            cm.dispose();
		}
        status -= 1;
	}
	//First NPC message to user, lists types and prices. User chooses category
	if (status == 0){
		if (true) { //!cm.getPlayer().getInventory(1).isFull()
			
			textBox = "Welcome to the #dCELLION#k Maple Leaf service!\r\n"
					+ "Exchange #bMesos#k or #bNX#k and #bCurrencies#k for powerful items!\r\n"
					+ "What would you like to forge today?\r\n#r";
					
			for (j = 0; j < craftOptions.length; j++ )
				textBox += "\r\n#L" + j + "#" + craftOptions[j] + "#l";
			
			cm.sendSimple(textBox);
		} else {
			cm.sendOk("Make sure you have space in your equip tab before trying to craft equipement.");
			cm.dispose();
		}
	//Message that appears when the user chooses an option. List all the items in the category. User chooses the item
	} else if (status == 1){
		switch(selection){
			//Pearl Equipment
			case 0:
				textBox = "Pearl Equipment cost #b2500#k #i4001126##t4001126# and #b2,000,000 mesos#k.\r\nWhat would you like to craft?\r\n";
				for (j = 0; j < pearlEqp.length; j++)
					textBox += "\r\n#L" + j + "#" +  "#v" + pearlEqp[j] + "##t" + pearlEqp[j] + "##l";
				cm.sendSimple(textBox);
				break;
			//Pearl Weapon
			case 1:
				textBox = "Pearl Weapon cost #b2500#k #i4001126##t4001126# and #b2,000,000 mesos#k.\r\nWhat would you like to craft?\r\n";
				for (j = 0; j < pearlWep.length; j++)
					textBox += "\r\n#L" + (100 + j) + "#" +  "#v" + pearlWep[j] + "##t" + pearlWep[j] + "##l";
				cm.sendSimple(textBox);
				break;
			//Onyx Equipment
			case 2:
				textBox = "Onyx Equipment cost #b5000#k #i4001126##t4001126# and #b10,000,000 mesos#k.\r\nWhat would you like to craft?\r\n";
				for (j = 0; j < onyxEqp.length; j++)
					textBox += "\r\n#L" + (200 + j) + "#" +  "#v" + onyxEqp[j] + "##t" + onyxEqp[j] + "##l";
				cm.sendSimple(textBox);
				break;
			//Onyx Weapon
			case 3:
				textBox = "Onyx Weapon cost #b5000#k #i4001126##t4001126# and #b10,000,000 mesos#k.\r\nWhat would you like to craft?\r\n";
				for (j = 0; j < onyxWep.length; j++)
					textBox += "\r\n#L" + (300 + j) + "#" +  "#v" + onyxWep[j] + "##t" + onyxWep[j] + "##l";
				cm.sendSimple(textBox);
				break;
		}
	} else if (status == 2){
		//Pearl Equipment & Weapon
		if (selection >= 0 && selection < 200){
			if (cm.haveItem(4001126, 2500) && (cm.getMeso() >= 2000000)){
				cm.gainItem(4001126, -2500);
				cm.gainMeso(-2000000);
				if (selection < 100) {
					cm.gainItem(pearlEqp[selection], 1);
					cm.sendOk("Thank you for your purchase.\r\nEnjoy your new #i"+pearlEqp[selection]+"# !");
					cm.dispose();
				} else {
					cm.gainItem(pearlWep[selection - 100], 1);
					cm.sendOk("Thank you for your purchase.\r\nEnjoy your new #i"+pearlWep[selection - 100]+"# !");
					cm.dispose();
				}
			} else {
				cm.sendOk("Sorry, you do not have enough #bmesos#k or #i4001126##t4001126##k!");
				cm.dispose();
			}
		//Onyx Equipment & Weapon
		} else if (selection < 400){
			if (cm.haveItem(4001126, 5000) && (cm.getMeso() >= 10000000)){
				if (selection < 300){
					if (cm.haveItem(pearlEqp[selection-200], 1)) {
						cm.gainItem(4001126, -5000);
						cm.gainMeso(-10000000);
						cm.gainItem(pearlEqp[selection-200], -1);
						cm.gainItem(onyxEqp[selection-200], 1);
						cm.sendOk("Thank you for your purchase.r\nEnjoy your new #i"+onyxEqp[selection-200]+"# !");
						cm.dispose();
					} else {
						cm.sendOk("Sorry, you don't have the Maple Pearl version of your item.");
						cm.dispose();
					}
				} else {
					if (cm.haveItem(pearlWep[selection-300], 1)){
						cm.gainItem(4001126, -5000);
						cm.gainMeso(-10000000);
						cm.gainItem(pearlWep[selection-300], -1);
						cm.gainItem(onyxWep[selection-300], 1);
						cm.sendOk("Thank you for your purchase.r\nEnjoy your new #i"+onyxWep[selection-300]+"# !");
						cm.dispose();
					} else {
						cm.sendOk("Sorry, you don't have the Maple Pearl version of your item.");
						cm.dispose();
					}
				}
			} else {
				cm.sendOk("Sorry, you do not have enough #bmesos#k or #i4001126##t4001126##k!");
				cm.dispose();
			}
		}
	}
}