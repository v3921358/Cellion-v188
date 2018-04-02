var easterChairs = [3015446, 3010120, 3010862];
var easterRingsWeap = [1702722, 1702305, 1112173, 1112285, 1115111, 1115022];
var easterShoes = [1073037, 1073181, 1073182];
var easterOveralls = [1053115, 1053109, 1053110];
var easterCaps = [1003050, 1000062, 1001089, 1003204, 1004876, 1012603];

var currency = 4001689;
status = -1;
var nChoice;

function start() {
	text = "#i4001689##fs13#\tEnjoy #dEaster with the Rexion community#k!#fs11#\r\n"
		+ "During this time you can collect Easter Eggs that drop around the Maple World, and trade them in for exclusive event rewards! Bring me any eggs you find!\r\n"
		+ "#L100##fs12##dView the Easter Egg Shop!#k#fs11##l";
	cm.sendSimple(text);
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
		text = "Happy Easter, #r#h ##k!\r\nWhat would you like to trade in your Easter Eggs for?#r#fs12#\r\n";
		
		text += "\r\n#d#fs13#Caps & Accessories#fs11# - 1000 Eggs#r";
		for (j = 0; j < easterCaps.length; j++)
			text += "\r\n#L10" + j + "#" +  "#v" + easterCaps[j] + "# #t" + easterCaps[j] + "##l";
		
		text += "\r\n\r\n\r\n#d#fs13#Overalls#fs11# - 1000 Eggs#r";
		for (j = 0; j < easterOveralls.length; j++)
			text += "\r\n#L20" + j + "#" +  "#v" + easterOveralls[j] + "# #t" + easterOveralls[j] + "##l";
		
		text += "\r\n\r\n\r\n#d#fs13#Shoes#fs11# - 500 Eggs#r";
		for (j = 0; j < easterShoes.length; j++)
			text += "\r\n#L30" + j + "#" +  "#v" + easterShoes[j] + "# #t" + easterShoes[j] + "##l";
		
		text += "\r\n\r\n\r\n#d#fs13#Rings & Weapons#fs11# - 1500 Eggs#r";
		for (j = 0; j < easterRingsWeap.length; j++)
			text += "\r\n#L40" + j + "#" +  "#v" + easterRingsWeap[j] + "# #t" + easterRingsWeap[j] + "##l";
		
		text += "\r\n\r\n\r\n#d#fs13#Chairs#fs11# - 2500 Eggs#r";
		
		for (j = 0; j < easterChairs.length; j++)
			text += "\r\n#L50" + j + "#" +  "#v" + easterChairs[j] + "# #t" + easterChairs[j] + "##l";
		
		cm.sendSimple(text);
	} else if (status == 1) {
		if (selection >= 500) { // CHAIRS
			nChoice = (selection - 500);
			if (cm.haveItem(currency, 2500) && !cm.isInventoryFull(cm.getPlayer(), 4)) {
				cm.gainItem(currency, -2500);
				cm.gainItem(easterChairs[nChoice], 1);
				cm.sendOk("Thank you for your purchase.\r\nEnjoy your new #i"+easterChairs[nChoice]+"# !");
				cm.dispose();
			} else {
				cm.sendOk("Sorry, it looks like you do not have enough #i" + currency + "# !\r\nGo hunt me down some more!");
				cm.dispose();
			}
		} else if (selection >= 400) { // RINGS/WEPS
			nChoice = (selection - 400);
			if (cm.haveItem(currency, 1500) && !cm.isInventoryFull(cm.getPlayer(), 1)) {
				cm.gainItem(currency, -1500);
				cm.gainItem(easterRingsWeap[nChoice], 1);
				cm.sendOk("Thank you for your purchase.\r\nEnjoy your new #i"+easterRingsWeap[nChoice]+"# !");
				cm.dispose();
			} else {
				cm.sendOk("Sorry, it looks like you do not have enough #i" + currency + "# !\r\nGo hunt me down some more!");
				cm.dispose();
			}
		} else if (selection >= 300) { // SHOES
			nChoice = (selection - 300);
			if (cm.haveItem(currency, 500) && !cm.isInventoryFull(cm.getPlayer(), 1)) {
				cm.gainItem(currency, -500);
				cm.gainItem(easterShoes[nChoice], 1);
				cm.sendOk("Thank you for your purchase.\r\nEnjoy your new #i"+easterShoes[nChoice]+"# !");
				cm.dispose();
			} else {
				cm.sendOk("Sorry, it looks like you do not have enough #i" + currency + "# !\r\nGo hunt me down some more!");
				cm.dispose();
			}
		} else if (selection >= 200) { // OVERALLS
			nChoice = (selection - 200);
			if (cm.haveItem(currency, 1000) && !cm.isInventoryFull(cm.getPlayer(), 1)) {
				cm.gainItem(currency, -1000);
				cm.gainItem(easterOveralls[nChoice], 1);
				cm.sendOk("Thank you for your purchase.\r\nEnjoy your new #i"+easterOveralls[nChoice]+"# !");
				cm.dispose();
			} else {
				cm.sendOk("Sorry, it looks like you do not have enough #i" + currency + "# !\r\nGo hunt me down some more!");
				cm.dispose();
			}
		} else { // CAPS/FACE
			nChoice = (selection - 100);
			if (cm.haveItem(currency, 1000) && !cm.isInventoryFull(cm.getPlayer(), 1)) {
				cm.gainItem(currency, -1000);
				cm.gainItem(easterCaps[nChoice], 1);
				cm.sendOk("Thank you for your purchase.\r\nEnjoy your new #i"+easterCaps[nChoice]+"# !");
				cm.dispose();
			} else {
				cm.sendOk("Sorry, it looks like you do not have enough #i" + currency + "# !\r\nGo hunt me down some more!");
				cm.dispose();
			}
		} 
	}
}