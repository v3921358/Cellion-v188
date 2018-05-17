/*
 * CELLION Development
 * Event Reward Shop NPC
 * @author Mazen Massoud
 */

// Category
var aCategory = ["300 EP - Face Accessories",
				   "300 EP - Eye Accessories"];

// Category Pricing	   
var aPrices = [300, 300];

// Category Items	
var aChairs = [3010029, 3010030, 3010031, 3010032, 3010033, 3010057, 3010058, 3010180, 3010181, 
              3010256, 3010301, 3010302, 3010416, 3010417, 3010412, 3010524, 3015000, 3014014,
              3015051, 3015050, 3015049, 3015048, 3015342];

var aTitles = [3700001, 3700006, 3700009, 3700011, 3700012, 3700013, 3700014, 3700016, 3700017,
              3700018, 3700025, 3700026, 3700029, 3700034, 3700039, 3700040, 3700041, 3700044,
              3700051, 3700050, 3700099, 3700100, 3700101, 3700102, 3700104, 3700106, 3700119,
              3700120, 3700136, 3700143, 3700144, 3700158, 3700164, 3700214, 3700216, 3700217, 
              3700247, 3700268, 3700270, 3700281, 3700336, 3700337];		
			  
// Variable Storage
var nEP;
var nCategorySelection;
var shopString;
var aItemList;
var status = 0;

function start() {
	nEP = cm.getPlayer().getEPoints();
    cm.sendNextPrevS("Welcome to the #dCELLION#k Event Shop.\r\nEvent Points can be earned by participating in server events!\r\n\r\n\t#dJoin us on Discord to stay up to date with all our latest events.\r\n\tStay Connected (#rhttps://discord.gg/pxT9qxb#d)", 2);
	status = -1;   
}

function action(mode, type, selection) {
	
    if (mode == -1) {
        cm.dispose();
    } else {
        if (mode == 0) cm.dispose();
		if (mode == 1) status++;
		else status--;
		
		if (status == 0) {
			
		   introText = "You currently have #b" + nEP + "#k Event Points.\r\n"
					+ "What would you like to exchange them for?#fs13##b";
					
		   for(var i = 0; i < aCategory.length; i++) introText += "\r\n#L" + i + "#" + aCategory[i] + "#l";  
		   cm.sendSimple(introText);
		  
		} else if (status == 1) { 
			
			nCategorySelection = selection;  // Save Category for Status #2
			shopString = "You currently have #b" + nEP + "#k Event Points.\r\n What would you like to buy for " + aPrices[nCategorySelection] +" Event Points?"; 
			
			switch (nCategorySelection) {
				case 0:
					aItemList = aChairs;
					break;
				case 1:
					aItemList = aTitles;
					break;
			}
			
			for (var j = 0; j < aItemList.length; j++) shopString += "\r\n#L" + j + "#" + "#v" + aItemList[j] + "##l";
			cm.sendSimple(shopString);
			
		} else if (status == 2) {
			
			if (nEP >= aPrices[nCategorySelection]){
				cm.getPlayer().setEPoints(cm.getPlayer().getEPoints() - aPrices[nCategorySelection]);
				cm.gainItem(aItemList[selection], 1);
				cm.sendOk("Thank you for supporting #dCELLION#k.\r\nEnjoy your new #i"+ aItemList[selection]+"# !");
			} else {
				cm.sendOk ("Sorry, you do not have enough Event Points!");
			}
			cm.dispose();
		} 
	}
}