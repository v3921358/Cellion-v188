/*
 *Rexion Crafting NPC
 *@author Arcas
 */

//Setup for the store
//Tyrant
var tyrantType = [1072743, 1102481, 1132174, 1082543];
var tyrantBoots = [1072743, 1072744, 1072745, 1072746, 1072747]; //70 coins
var tyrantCapes = [1102481, 1102482, 1102483, 1102484, 1102485]; //70 coins
var tyrantBelts = [1132174, 1132175, 1132176, 1132177, 1132178]; //100 coins
var tyrantGloves = [1082543, 1082544, 1082545, 1082546, 1082547]; //500 coins
//Pearl
var pearlEqp = [1003864, 1052613, 1102563, 1012377, 1132229, 1122253];
var pearlWep = [1302278, 1212067, 1222062, 1232061, 1242066, 1252065, 1312156, 1322206, 1332228, 1472030, 1362093, 1372180, 1382212, 1402200, 1412138, 1422143, 1432170, 1442226, 1452208, 1462196, 1472217, 1482171, 1492182, 1522097, 1532101, 1542069, 1552069];
//Onyx
var onyxRing = [1113034];
var onyxEqp = [1003863, 1052612, 1102562, 1012376, 1132228, 1122252];
var onyxWep = [1302277, 1212066, 1222061, 1232060, 1242065, 1252064, 1312155, 1322205, 1332227, 1352825, 1362092, 1372179, 1382211, 1402199, 1412137, 1422142, 1432169, 1442225, 1452207, 1462195, 1472216, 1472216, 1492181, 1522096, 1532100, 1542070, 1552070];
//Fafnir
var fafnirEqp = [1042254, 1062165, 1003797, 1042255, 1062166, 1003798, 1042256, 1062167, 1003799, 1042257, 1062168, 1003800, 1042258, 1062169, 1003801];
var fafnirWeapons = [1212063, 1222058, 1232057, 1242060, 1242061, 1252015, 1262016, 1302275, 1312153, 1322203, 1332225, 1342082, 1362090, 1372177, 1382208, 1402196, 1412135, 1422140, 1432167, 1442223, 1452205, 1462193, 1472214, 1482168, 1492179, 1522094, 1532098, 1542063, 1552063, 1582016];
//Misc
var miscEqp = [1190302, 1113185];

var craftOptions = ["Lv. 30 Pearl Equipment and Accessories", "Lv. 30 Pearl Weapon", "Lv. 100 Onyx Equipment and Accessories", "Lv. 100 Onyx Weapon", "Lv. 100 Onyx Ring", "Lv. 150 Fafnir Equipment", "Lv. 150 Fafnir Weapon", "Lv. 150 Tyrant Equipment\r\n", "Miscellanious Items"];

var textBox = "";
var j = 0;

function start() {
    cm.sendNext("Welcome to the #dREXION#k Blacksmith Workshop!\r\n#tWhere you can craft all of the strongest gear.");
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
		textBox = "Welcome to the #dREXION#k Blacksmith Service!\r\n"
				+ "Exchange #bMesos#k or #bNX#k and #bCurrencies#k for powerful items!\r\n"
				+ "What would you like to forge today?#r";
	   for (j = 0; j < craftOptions.length; j++ )
		textBox += "\r\n#L" + j + "#" + craftOptions[j] + "#l";
	   cm.sendSimple(textBox);
	//Message that appears when the user chooses an option. List all the items in the category. User chooses the item
	} else if (status == 1){
		switch(selection){
			//Pearl Equipment
			case 0:
				textBox = "Pearl Equipment cost #b1000#k #i4001126##t4001126# and #b2,000,000 mesos#k.\r\nWhat would you like to craft?\r\n";
				for (j = 0; j < pearlEqp.length; j++)
					textBox += "\r\n#L" + j + "#" +  "#v" + pearlEqp[j] + "##t" + pearlEqp[j] + "##l";
				cm.sendSimple(textBox);
				break;
			//Pearl Weapon
			case 1:
				textBox = "Pearl Weapon cost #b1000#k #i4001126##t4001126# and #b2,000,000 mesos#k.\r\nWhat would you like to craft?\r\n";
				for (j = 0; j < pearlWep.length; j++)
					textBox += "\r\n#L" + (100 + j) + "#" +  "#v" + pearlWep[j] + "##t" + pearlWep[j] + "##l";
				cm.sendSimple(textBox);
				break;
			//Onyx Equipment
			case 2:
				textBox = "Onyx Equipment cost #b2500#k #i4001126##t4001126# and #b10,000,000 mesos#k.\r\nWhat would you like to craft?\r\n";
				for (j = 0; j < onyxEqp.length; j++)
					textBox += "\r\n#L" + (200 + j) + "#" +  "#v" + onyxEqp[j] + "##t" + onyxEqp[j] + "##l";
				cm.sendSimple(textBox);
				break;
			//Onyx Weapon
			case 3:
				textBox = "Onyx Weapon cost #b2500#k #i4001126##t4001126# and #b10,000,000 mesos#k.\r\nWhat would you like to craft?\r\n";
				for (j = 0; j < onyxWep.length; j++)
					textBox += "\r\n#L" + (300 + j) + "#" +  "#v" + onyxWep[j] + "##t" + onyxWep[j] + "##l";
				cm.sendSimple(textBox);
				break;
			//Onyx Ring
			case 4:
				textBox = "Onyx Ring cost #10,000#k #i4001126##t4001126# and #b15,000,000 mesos#k.\r\nWould you like to craft it?\r\n";
				for (j = 0; j < onyxRing.length; j++)
					textBox += "\r\n#L" + (400 + j) + "#" +  "#v" + onyxRing[j] + "##t" + onyxRing[j] + "##l";
				cm.sendSimple(textBox);
				break;
			//Fafnir Equipment
			case 5: 
				textBox = "Fafnir Equipment cost #b10#k #i4310064##t4310064#, #b10#k #i4310065##t4310065# and #b4#k #i4430000##t4430000##k.\r\nWhat would you like to craft?\r\n";
				for (j = 0; j < fafnirEqp.length; j++)
					textBox += "\r\n#L" + (500 + j) + "#" +  "#v" + fafnirEqp[j] + "##t" + fafnirEqp[j] + "##l";
				cm.sendSimple(textBox);
				break;
			//Fafnir Weapon
			case 6:
				textBox = "Fafnir Weapon cost #b20#k #i4310064##t4310064#, #b20#k #i4310065##t4310065# and #b6#k #i4430000##t4430000##k.\r\nWhat would you like to craft?\r\n";
				for (j = 0; j < fafnirWeapons.length; j++){
					if (fafnirWeapons[j] != 1242060 && fafnirWeapons[j] != 1242061){
						textBox += "\r\n#L" + (600 + j) + "#" +  "#v" + fafnirWeapons[j] + "##t" + fafnirWeapons[j] + "##l";
					} else if (fafnirWeapons[j] == 1242060) {
						textBox += "\r\n#L" + (600 + j) + "#" +  "#v" + fafnirWeapons[j] + "##t" + fafnirWeapons[j] + "(Thief)##l";
					} else {
						textBox += "\r\n#L" + (600 + j) + "#" +  "#v" + fafnirWeapons[j] + "##t" + fafnirWeapons[j] + "#(Pirate)#l";
					}
				}
				cm.sendSimple(textBox);
				break;
			//Tyrant Equipment
			case 7:
				textBox = "Please choose what type of Tyrant you would like to craft.\r\n";
				textBox += "\r\n#L" + (700 + 0) + "#" +  "#v" + tyrantType[0] + "##bShoes#k#l";
				textBox += "\r\n#L" + (700 + 1) + "#" +  "#v" + tyrantType[1] + "##bCape#k#l";
				textBox += "\r\n#L" + (700 + 2) + "#" +  "#v" + tyrantType[2] + "##bBelt#k#l";
				textBox += "\r\n#L" + (700 + 3) + "#" +  "#v" + tyrantType[3] + "##bGloves#k#l";
				cm.sendSimple(textBox);
				break;
			//Misc items 
			case 8: 
				textBox = "Please choose what Item you would like to craft.\r\n";
				//Crystal Emblem
				textBox += "\r\nCost :#b10#i4430000##t4430000##k.";
				textBox += "\r\n#L" + (800 + 0) + "#" +  "#v" + miscEqp[0] + "##b#k#l";
				//Blackgate Ring
				textBox += "\r\nCost :#b10,000#i4001126##t4001126##k #b2#i4001619##t4001619##k."; 
				textBox += "\r\n#L" + (800 + 1) + "#" +  "#v" + miscEqp[1] + "##b#k#l";
				cm.sendSimple(textBox);
				break;
		}
	} else if (status == 2){
		if (cm.isInventoryFull(cm.getPlayer(), 1)) {
			cm.sendOk("Looks like you don't have enough inventory space.");
			cm.dispose();
			return;
		}
		
		//Pearl Equipment & Weapon
		if (selection >= 0 && selection < 200){
			if (cm.haveItem(4001126, 1000) && (cm.getMeso() >= 2000000)){
				cm.gainItem(4001126, -1000);
				cm.gainMeso(-2000000);
				if (selection < 100)
					cm.gainItem(pearlEqp[selection], 1);
				else
					cm.gainItem(pearlWep[selection - 100], 1);
				cm.sendOk("Thank you for your purchase.\r\nEnjoy your new #i"+pearlEqp[selection]+"# !");
				cm.dispose();
			} else {
				cm.sendOk("Sorry, you do not have enough #bmesos#k or #i4001126##t4001126##k!");
				cm.dispose();
			}
		//Onyx Equipment & Weapon
		} else if (selection < 400){
			if (cm.haveItem(4001126, 2500) && (cm.getMeso() >= 10000000)){
				if (selection < 300){
					if (cm.haveItem(pearlEqp[selection-200], 1)) {
						cm.gainItem(4001126, -2500);
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
						cm.gainItem(4001126, -2500);
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
		//Onyx Ring
		} else if (selection < 500){
			if (cm.haveItem(4001126, 10000) && (cm.getMeso() >= 15000000)){
				cm.gainItem(4001126, -10000);
				cm.gainMeso(-15000000);
				cm.gainItem(onyxRing[selection-400], 1);
				cm.dispose();
			} else {
				cm.sendOk("Sorry, you do not have enough #bmesos#k or #i4001126##t4001126##k!");
				cm.dispose();
			}
		//Fafnir Equipment
		} else if (selection < 600){
			if (cm.haveItem(4310064, 10) && cm.haveItem(4310065, 10) && cm.haveItem(4430000, 4)){
				cm.gainItem(4310064, -10);
				cm.gainItem(4310065, -10);
				cm.gainItem(4430000, -4);
				cm.gainItem(fafnirEqp[selection-500], 1);
				cm.dispose();
			} else {
				cm.sendOk("Remember, you need #b10#i4310064##t4310064##k,#b10#i4310065##t4310065##k and #b4#i4430000##t4430000##k to craft a Fafnir equipment!");
				cm.dispose();
			}
		//Fafnir Weapon
		} else if (selection < 700){
			if (cm.haveItem(4310064, 20) && cm.haveItem(4310065, 20) && cm.haveItem(4430000, 6)){
				cm.gainItem(4310064, -20);
				cm.gainItem(4310065, -20);
				cm.gainItem(4430000, -6);
				cm.gainItem(fafnirWeapons[selection-600], 1);
				cm.dispose();
			} else {
				cm.sendOk("Remember, you need #b20#i4310064##t4310064##k,#b20#i4310065##t4310065##k and #b6#i4430000##t4430000##k to craft a Fafnir weapon!");
				cm.dispose();
			}
		//Tyrant Equipment
		} else if (selection < 800){
			switch(selection){
				//Boot
				case 700:
					textBox = "Tyrant Shoes cost #b70#k #i4310058##t4310058# and #b2#k #i4001619##t4001619##k.\r\nWhat would you like to craft?\r\n";
					for (j = 0; j < tyrantBoots.length; j++)
						textBox += "\r\n#L" + j + "#" +  "#v" + tyrantBoots[j] + "##t" + tyrantBoots[j] + "##l";
					cm.sendSimple(textBox);
					break;
				//Cape
				case 701:
					textBox = "Tyrant Cape cost #b70#k #i4310058##t4310058# and #b2#k #i4001619##t4001619##k.\r\nWhat would you like to craft?\r\n";
					for (j = 0; j < tyrantCapes.length; j++)
						textBox += "\r\n#L" + (10 + j) + "#" +  "#v" + tyrantCapes[j] + "##t" + tyrantCapes[j] + "##l";
					cm.sendSimple(textBox);
					break;
				//Belt
				case 702:
					textBox = "Tyrant Belt cost #b70#k #i4310058##t4310058# and #b2#k #i4001619##t4001619##k.\r\nWhat would you like to craft?\r\n";
					for (j = 0; j < tyrantBoots.length; j++)
						textBox += "\r\n#L" + (20 + j) + "#" +  "#v" + tyrantBelts[j] + "##t" + tyrantBelts[j] + "##l";
					cm.sendSimple(textBox);
					break;
				//Gloves
				case 703:
					textBox = "Tyrant Gloves cost #b500#k #i4310058##t4310058# and #b10#k #i4001619##t4001619##k.\r\nWhat would you like to craft?\r\n";
					for (j = 0; j < tyrantGloves.length; j++)
						textBox += "\r\n#L" + (30 + j) + "#" +  "#v" + tyrantGloves[j] + "##t" + tyrantGloves[j] + "##l";
					cm.sendSimple(textBox);
					break;
			}
		} else if (selection < 900) {
			switch(selection){
				//Crystal Maple Emblem
				case 800:
					if (cm.haveItem(4430000, 10)){
						cm.gainItem(4430000, -10);
						cm.gainItem(miscEqp[0], 1);
						cm.dispose();
					} else {
						cm.sendOk("Sorry, you do not have enough #i4430000##t4430000##k!");
						cm.dispose();
					}
					break;
				//Blackgate Ring
				case 801:
					if (cm.haveItem(4001126, 10000) && cm.haveItem(4001619, 2)){
						cm.gainItem(4001126, -10000);
						cm.gainItem(4001619, -2);
						cm.gainItem(miscEqp[1], 1);
						cm.dispose();
					} else {
						cm.sendOk("Sorry, you do not have enough #i4001619##t4001619# or #i4001126##t4001126##k!");
						cm.dispose();
					}
					break;
			}
		}
	} else if (status == 3){
		if (selection < 20){
			if (cm.haveItem(4310058, 70) && cm.haveItem(4001619, 2)){
				cm.gainItem(4310058, -70);
				cm.gainItem(4001619, -2);
				if (selection < 10)
					cm.gainItem(tyrantBoots[selection], 1);
				else
					cm.gainItem(tyrantCapes[selection - 10], 1);
				cm.dispose();
			} else {
				cm.sendOk("Remember, you need #b70#i4310058##t4310058##k and #b2#i4001619##t4001619##k!");
				cm.dispose();
			}
		} else if (selection < 30){
			if (cm.haveItem(4310058, 100) && cm.haveItem(4001619, 3)){
				cm.gainItem(4310058, -100);
				cm.gainItem(4001619, -3);
				cm.gainItem(tyrantBelts[selection - 20], 1);
				cm.dispose();
			} else {
				cm.sendOk("Remember, you need #b100#i4310058##t4310058##k and #b3#i4001619##t4001619##k!");
				cm.dispose();
			}
		} else if (selection < 40){
			if (cm.haveItem(4310058, 500) && cm.haveItem(4001619, 10)){
				cm.gainItem(4310058, -500);
				cm.gainItem(4001619, -10);
				cm.gainItem(tyrantGloves[selection - 30], 1);
				cm.dispose();
				
			} else {
				cm.sendOk("Remember, you need #b500#i4310058##t4310058##k and #b10#i4001619##t4001619##k!");
				cm.dispose();
			}
		}
	}
}
