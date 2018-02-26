/*

REXION - Equipment Shop 
@Author: Mazen

*/
var status = 0;


//Setup the data you would like your shop to contain below 
var itemOptions = ["Lv. 120 Timeless Gear", 
					"Lv. 150 Fafnir Gear",
					"Lv. 160 Sweetwater Gear\r\n",
					"Lv. 120 Timeless Weapons", 
					"Lv. 150 Fafnir Weapons",
					"Lv. 160 Sweetwater Weapons"];
					
var leafId = 4001126;
var extraCostId_1 = 4001126; // Golden Maple Leaf
var extraCostId_2 = 4001126;  // Maple Leaf Gold
					
var leafCost = [1000, 1500, 5000, 2500, 3000, 10000];  //Format for leafCost [caps, tops, bottoms, gloves, boots, longcoats, cloaks, weapons] 
var extraCost_1 = [1000, 1000, 1000, 1000, 1000, 1000];
var extraCost_2 = [1000, 1000, 1000, 1000, 1000, 1000];

var lv120_Gear = [1003976];
var lv150_Gear = [1003976];
var lv160_Gear = [1003976];

var lv120_Weapon = [1003976];
var lv150_Weapon = [1003976];
var lv160_Weapon = [1003976];

var itemCategory; 
var shopText;


function start() {
	ml = 4001126;
    cm.sendNext("Welcome to the #dREXION#k Equip.\r\nHere you can find all the rarest Maple Items.\r\n\r\n#rYou'll need Maple Leaves that you collect around REXION!");
	status = -1;   
}

function action(mode, type, selection) {
	
   if (mode == -1) {
        cm.dispose();
    } else {
        if (mode == 0) {
            cm.sendNext("Thank you for stopping by!");
            cm.dispose();
            return;
        }
   
   if (mode == 1)
            status++;
        else
            status--;
   
   //First NPC message to user, lists types and leafCost. User chooses category
   if(status == 0){
	   introText = "Welcome to the #dREXION#k Equipment Forge!\r\n"
				+ "What would you like to forge?#r";
	   for(var i = 0; i < itemOptions.length; i++){
		introText += "\r\n#L" + i + "#" + itemOptions[i] + "#l";  
	   }
	   cm.sendSimple(introText);
	  
	//Lists the available items in each category, user chooses which item to recieve
   } else if(status == 1) { 
	   itemCategory = selection;  //save user-chosen category for status #2
	   
			if (selection == 0) {
                shopText = "If you're interested in forging #d" + itemOptions[itemCategory] + "#k, you're going to need #r" + leafCost[itemCategory] + " Maple Leaves#k and #r" + extraCost_1[itemCategory] + " Golden Maple Leaves#k to cover the cost of materials. What would you like to forge?";  
                for (var j = 0; j < lv120_Gear.length; j++)
                  shopText += "\r\n#L" + j + "#" + "#v" + lv120_Gear[j] + "# - #t" + lv120_Gear[j] + "# #l";
                    cm.sendSimple(shopText);
            } else if (selection == 1) { 
                shopText = "If you're interested in forging #d" + itemOptions[itemCategory] + "#k, you're going to need #r" + leafCost[itemCategory] + " Maple Leaves#k and #r" + extraCost_1[itemCategory] + " Golden Maple Leaves#k to cover the cost of materials. What would you like to forge?";
                for (var j = 0; j < lv150_Gear.length; j++)
                  shopText += "\r\n#L" + j + "#" + "#v" + lv150_Gear[j] + "# - #t" + lv150_Gear[j] + "# #l";
                    cm.sendSimple(shopText);
            } else if (selection == 2) {
                shopText = "If you're interested in forging #d" + itemOptions[itemCategory] + "#k, you're going to need #r" + leafCost[itemCategory] + " Maple Leaves#k and #r" + extraCost_1[itemCategory] + " Golden Maple Leaves#k and #r" + extraCost_2[itemCategory] + " Maple Leaf Gold#k to cover the cost of materials. What would you like to forge?";
                for (var j = 0; j < lv160_Gear.length; j++)
                  shopText += "\r\n#L" + j + "#" + "#v" + lv160_Gear[j] + "# - #t" + lv160_Gear[j] + "# #l";
                    cm.sendSimple(shopText);
            } else if (selection == 3) {
                shopText = "If you're interested in forging #d" + itemOptions[itemCategory] + "#k, you're going to need #r" + leafCost[itemCategory] + " Maple Leaves#k and #r" + extraCost_1[itemCategory] + " Golden Maple Leaves#k to cover the cost of materials. What would you like to forge?";
                for (var j = 0; j < lv120_Weapon.length; j++)
                  shopText += "\r\n#L" + j + "#" + "#v" + lv120_Weapon[j] + "# - #t" + lv120_Weapon[j] + "# #l";
                    cm.sendSimple(shopText);
            } else if (selection == 4) {
                shopText = "If you're interested in forging #d" + itemOptions[itemCategory] + "#k, you're going to need #r" + leafCost[itemCategory] + " Maple Leaves#k and #r" + extraCost_1[itemCategory] + " Golden Maple Leaves#k to cover the cost of materials. What would you like to forge?"; 
                for (var j = 0; j < lv150_Weapon.length; j++)
                  shopText += "\r\n#L" + j + "#" + "#v" + lv150_Weapon[j] + "# - #t" + lv150_Weapon[j] + "# #l";
                    cm.sendSimple(shopText);
            } else if (selection == 5) {
                shopText = "If you're interested in forging #d" + itemOptions[itemCategory] + "#k, you're going to need #r" + leafCost[itemCategory] + " Maple Leaves#k, #r" + extraCost_1[itemCategory] + " Golden Maple Leaves#k and #r" + extraCost_2[itemCategory] + " Maple Leaf Gold#k to cover the cost of materials. What would you like to forge?"; 
                for (var j = 0; j < lv160_Weapon.length; j++)
                  shopText += "\r\n#L" + j + "#" + "#v" + lv160_Weapon[j] + "# - #t" + lv160_Weapon[j] + "# #l";
                    cm.sendSimple(shopText);
			} 
	   
	   
   } else if(status == 2) {
			
			if (itemCategory == 0){
				if (cm.haveItem(leafId, leafCost[itemCategory])){
                    cm.gainItem(leafId, -(leafCost[itemCategory]));
                    cm.gainItem(lv120_Gear[selection], 1);
                    cm.sendOk("Thank you for your purchase.\r\nEnjoy your new #i" + lv120_Gear[selection] + "# !");
                } else {
                    cm.sendYesNo("Sorry, you do not have enough #bmesos#k or #bmaple leaves#k! Would you like to forge another item?");
                }
            } else if (itemCategory == 1) {
                if (cm.haveItem(ml, leafCost[1]) && (cm.getMeso() >= mesoPrices[1])){
                    cm.gainItem(ml, -(leafCost[1]));
					cm.gainMeso(-(mesoPrices[1]));
                    cm.gainItem(pearlWep[selection], 1);
                    cm.sendOk("Thank you for your purchase.\r\nEnjoy your new #i"+pearlWep[selection]+"# !");
                } else {
                    cm.sendOk("Sorry, you do not have enough #bmesos#k or #bmaple leaves#k!");
                }
            } else if (itemCategory == 2){
                if (cm.haveItem(ml, leafCost[2]) && (cm.getMeso() >= mesoPrices[2])){
                    cm.getPlayer().gainItem(ml, -(leafCost[2]));
					cm.gainMeso(-(mesoPrices[2]));
                    cm.gainItem(pearlPend[selection], 1);
                    cm.sendOk("Thank you for your purchase.\r\nEnjoy your new #i"+pearlPend[selection]+"# !");
                } else {
                    cm.sendOk("Sorry, you do not have enough #bmesos#k or #bmaple leaves#k!");
                }
            } else if (itemCategory == 3) {
                if (cm.haveItem(ml, leafCost[3]) && (cm.getMeso() >= mesoPrices[3]) && cm.haveItem(pearlEqp[selection], 1)){
                    cm.gainItem(ml, -(leafCost[3]));
					cm.gainMeso(-(mesoPrices[3]));
                    cm.gainItem(onyxEqp[selection], 1);
					cm.gainItem(pearlEqp[selection], -1);
                    cm.sendOk("Thank you for your purchase.r\nEnjoy your new #i"+onyxEqp[selection]+"# !");
                } else {
                    cm.sendOk("Sorry, you do not have enough #bmesos#k or #bmaple leaves#k!\r\nRemember that the #rPearl Maple#k version of the item is required.");
                }
            } else if (itemCategory == 4) {
                if (cm.haveItem(ml, leafCost[4]) && (cm.getMeso() >= mesoPrices[4]) && cm.haveItem(pearlWep[selection], 1)){
                    cm.gainItem(ml, -(leafCost[4]));
					cm.gainMeso(-(mesoPrices[4]));
                    cm.gainItem(onyxWep[selection], 1);
					cm.gainItem(pearlWep[selection], -1);
                    cm.sendOk("Thank you for your purchase.\r\nEnjoy your new #i"+onyxWep[selection]+"# !");
                } else {
                    cm.sendOk("Sorry, you do not have enough #bmesos#k or #bmaple leaves#k!\r\nRemember that the #rPearl Maple#k version of the item is required.");
                }
            } else if (itemCategory == 5) {
                if (cm.haveItem(ml, leafCost[5]) && (cm.getMeso() >= mesoPrices[5]) && cm.haveItem(pearlPend[selection], 1)){
                    cm.gainItem(ml, -(leafCost[5]));
					cm.gainMeso(-(mesoPrices[5]));
                    cm.gainItem(onyxPend[selection], 1);
					cm.gainItem(pearlPend[selection], -1);
                    cm.sendOk("Thank you for your purchase.\r\nEnjoy your new #i"+onyxPend[selection]+"# !");
                } else {
                    cm.sendOk("Sorry, you do not have enough #bmesos#k or #bmaple leaves#k!\r\nRemember that the #rPearl Maple#k version of the item is required.");
                }
			} else if (itemCategory == 6) {
                if (cm.haveItem(ml, leafCost[6]) && (cm.getMeso() >= mesoPrices[6])){
                    cm.gainItem(ml, -(leafCost[6]));
					cm.gainMeso(-(mesoPrices[6]));
                    cm.gainItem(onyxRing[selection], 1);
                    cm.sendOk("Thank you for your purchase.\r\nEnjoy your new #i"+onyxRing[selection]+"# !");
                } else {
                    cm.sendOk("Sorry, you do not have enough #bmesos#k or #bmaple leaves#k!");
                }
            } else if (itemCategory == 7) {
                if (dp >= leafCost[7]){
                    cm.getPlayer().setDPoints(cm.getPlayer().getDPoints() - leafCost[7]);
                    cm.gainItem(weapons[selection], 1);
                    cm.sendOk("Thank you for supporting #dREXION#k.\r\nEnjoy your new #i"+weapons[selection]+"# !");
                } else {
                    cm.sendOk ("Sorry, you do not have enough donor points!");
                }
            } 
			
            cm.dispose();
			
        } else if (status == 3) {
			cm.dispose();
			cm.openNpc(9201082);
		}
		
	}
	 
}