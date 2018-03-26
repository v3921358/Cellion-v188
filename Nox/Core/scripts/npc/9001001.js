/*
*	REXION Quick Access
*	Universal NPC
*
*	@author Mazen
*	@co-author Ergoth
*
*/

// Status Definitions
var status = 0;
var selectStatus  = 0;
var mapForm = 0;

// Map Warper Selection
var townMaps = Array(300000000, 680000000, 230000000, 910001000, 260000000, 541000000, 540000000, 211060010, 863100000, 105300000, 310000000, 211000000, 101072000, 101000000, 101050000, 130000000, 820000000, 223000000, 410000000, 141000000, 120040000, 209000000, 682000000, 310070000, 401000000, 100000000, 271010000, 251000000, 744000000, 551000000, 103000000, 222000000, 240000000, 104000000, 220000000, 150000000, 261000000, 807000000, 250000000, 800000000, 600000000, 120000000, 200000000, 800040000, 400000000, 102000000, 914040000, 200100000, 865000000, 801000000, 105000000, 866000000, 693000020, 270000000, 860000000, 273000000, 320000000);
var monsterMaps = Array(240070300,800020110,610040000,270030000,211060000,240040500,551030100,271000300,211061000,211041100,240010501,330002019,270020000,910170000,390009999,610030010,863000100,910180100,272000100,682010200,541000300,241000200,327090040,102040200,240010700,241000210,241000220,350013601,910028600,706041000,706041005,273050000,231040400,401050000,541020400, 224000015, 273040100);
var bossMaps = Array(211070000, 262000000, 105100100, 240050000, 240040700, 105100100, 350060300, 271040000, 211041700, 240050400);

// Map Warper Definitions
var townMapCost = 10000; // Price in Mesos
var monsterMapCost = 4000; // Price in NX
var bossMapCost = 125000; // Price in NX

var monsterMapReqLevel = 60;
var bossMapReqLevel = 120;

var lockedText1 = "";
var lockedText2 = "";

// Exchange System Definitions
var exchangeItemMeso = 4001619; // Golden Maple Leaf
var purchaseCostMeso = 1000000000; // Mesos Needed to Buy
var sellCostMeso = 850000000; // Mesos Gained when Sold

var exchangeItemNX = 4430000; // Maple Leaf Gold
var purchaseCostNX = 1000000; // NX Needed to Buy
var sellCostNX = 850000; // NX Gained when Sold

function start() {
	status = -1;
	action(1, 0, 0);
}

function action(mode, type, selection) {

	if (!cm.isQuestFinished(34330)) { // Auto complete boss pre-quests.
		cm.forceCompleteQuest(2241);
		cm.forceCompleteQuest(33565);
		cm.forceCompleteQuest(31833);
		cm.forceCompleteQuest(30007);
		cm.forceCompleteQuest(3157);
		cm.forceCompleteQuest(7313);
		cm.forceCompleteQuest(31179);
		cm.forceCompleteQuest(3521);
		cm.forceCompleteQuest(31152);
		cm.forceCompleteQuest(33294);
		cm.forceCompleteQuest(34015);
		cm.forceCompleteQuest(17523);
		cm.forceCompleteQuest(58955);
		cm.forceCompleteQuest(34330);
	}
	
	if (mode == -1) {
		cm.dispose();
	} else {
		
		if (status == 0 && mode == 0)  {
			cm.dispose();
			return;
		}
		
		if (mode == 1)  {
			status++;
		} else {
			status--;
		} 
		
		if (status == 0) {
			
			cm.sendNextPrevS("Welcome to the #dREXION Quick Access#k Menu!\r\n" 
						+ "What exactly would you like to do?#r\r\n" 
						+ "#L100#Travel Around the Maple World#l\r\n" 
						+ "#L101#Shop at the General Store#l\r\n"
						+ "#L102#Exchange Mesos and Maple Points (NX)#l\r\n"
						+ "#L103#Access the REXION Vote Rewards#l\r\n"
						+ "#L104#Access the REXION Donor Rewards#l\r\n"
						+ "#L105#Drop CASH and ETC Items#l\r\n"
						+ "#L108#Obtain Fifth Job & V: Matrix\r\n"
						+ "#L106#View Paragon Statistics#l\r\n\r\n"
						+ "#d#L107#View Frequently Asked Questions#l\r\n"
						, 2);
						
		} else if(status == 1) {
			
			switch (selection) {
				case 100:
					if (cm.getLevel() < monsterMapReqLevel) {
						lockedText1 = " (Unlocked at Level " + monsterMapReqLevel + ")";
					} 
					if (cm.getLevel() < bossMapReqLevel) {
						lockedText2 = " (Unlocked at Level " + bossMapReqLevel + ")";
					}
				
					cm.sendNextPrevS("What type of area do you plan on visiting?\r\n"
						+ "#b#L200#10,000 Mesos - Towns#l\r\n"
						+ "#r#L201#5,000 NX - Monster Zones" + lockedText1 + "#l\r\n"
						+ "#L202#300,000 NX - Boss Arenas" + lockedText2 + "#l\r\n\r\n"
						+ "#d#L203#Free - Return to Rexion Hideout", 2);
					break;
				case 101:
					cm.dispose();
					cm.openShop(9090000); // Mu Mu's Shop
					break;
				case 102:
					cm.sendNextPrev("What would you like to exchange?\r\n"
						+ "#d#L300#1,000,000,000 Mesos - Buy a Golden Maple Leaf#l\r\n"
						+ "#r#L301#850,000,000 Mesos - Sell a Golden Maple Leaf#l\r\n\r\n"
						+ "#d#L302#1,000,000 NX - Buy Maple Leaf Gold#l\r\n"
						+ "#r#L303#850,000 NX - Sell Maple Leaf Gold#l");
					break;
				case 103:
					cm.dispose();
					cm.openNpc(9100019); // Vote Rewards NPC
					break;
				case 104:
					cm.dispose();
					cm.openNpc(9100018); // Donor Rewards NPC
					break;
				case 105:
					cm.dispose();
					cm.openNpc(9010017); // Cash Drop NPC
					break;
				case 106:
					if (cm.getLevel() < 250) {
						cm.sendPrevS("Sorry, this feature is unlocked at #rLevel 250#k.", 2);
					} else {
						// Make sure Paragon exp and such matches with the source.
						var rankDisplay = ["I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X", "SS"];
						var neededExp = Array(15000000000000, 20000000000000, 25000000000000, 30000000000000, 35000000000000,
                                            40000000000000, 45000000000000, 50000000000000, 55000000000000, 60000000000000, 100000000000000);
						var percentExp = (cm.getPlayer().getExp() / neededExp[cm.getPlayer().getReborns()]).toFixed(2);
											
						var paragonProfile = "Paragon Statistics (#b#h ##k)\r\n";
						
						if (cm.getPlayer().getReborns() > 0) {
							if(cm.getPlayer().getReborns >= 11) {
								paragonProfile += "Current Rank (#dParagon SS#k) (#d100%#k)\r\nNext Rank (#rNone#k)\r\n";
							}
							paragonProfile += "Current Rank (#dParagon " + rankDisplay[cm.getPlayer().getReborns() - 1] + "#k) (#d" + percentExp + "%#k)\r\nNext Rank (#rParagon " + rankDisplay[cm.getPlayer().getReborns()] + "#k)\r\n";
						} else {
							paragonProfile += "Current Rank (#dNone#k) (#d" + percentExp + "%#k)\r\nNext Rank (#rParagon I#k)\r\n";
						}
										
						if (cm.getPlayer().getReborns() >= 1) {
							paragonProfile += "\r\n\t#d+5% Damage Reduction#k";
						} else {
							paragonProfile += "\r\n\t#r+5% Damage Reduction (Unlocked at Paragon I)#k";
						}
						if (cm.getPlayer().getReborns() >= 2) {
							paragonProfile += "\r\n\t#d+5% Increased Damage#k";
						} else {
							paragonProfile += "\r\n\t#r+5% Increased Damage (Unlocked at Paragon II)#k";
						}
						if (cm.getPlayer().getReborns() >= 3) {
							paragonProfile += "\r\n\t#d+10% Increased All Stats#k";
						} else {
							paragonProfile += "\r\n\t#r+10% Increased All Stats (Unlocked at Paragon III)#k";
						}
						if (cm.getPlayer().getReborns() >= 4) {
							paragonProfile += "\r\n\t#d+10% Increased Meso Gain#k";
						} else {
							paragonProfile += "\r\n\t#r+10% Increased Meso Gain (Unlocked at Paragon IV)#k";
						}
						if (cm.getPlayer().getReborns() >= 5) {
							paragonProfile += "\r\n\t#d+20% Increased NX Chance & Gain#k";
						} else {
							paragonProfile += "\r\n\t#r+20% Increased NX Chance & Gain (Unlocked at Paragon V)#k";
						}
						if (cm.getPlayer().getReborns() >= 6) {
							paragonProfile += "\r\n\t#d+1% Damage Leeched as HP#k";
						} else {
							paragonProfile += "\r\n\t#r+1% Damage Leeched as HP (Unlocked at Paragon VI)#k";
						}
						if (cm.getPlayer().getReborns() >= 7) {
							paragonProfile += "\r\n\t#d+1% Damage Leeched as MP#k";
						} else {
							paragonProfile += "\r\n\t#r+1% Damage Leeched as MP (Unlocked at Paragon VII)#k";
						}
						if (cm.getPlayer().getReborns() >= 8) {
							paragonProfile += "\r\n\t#d+5% Increased Maximum MP#k";
						} else {
							paragonProfile += "\r\n\t#r+5% Increased Maximum MP (Unlocked at Paragon VIII)#k";
						}
						if (cm.getPlayer().getReborns() >= 9) {
							paragonProfile += "\r\n\t#d+5% Increased Maximum HP#k";
						} else {
							paragonProfile += "\r\n\t#r+5% Increased Maximum HP (Unlocked at Paragon IX)#k";
						}
						if (cm.getPlayer().getReborns() >= 10) {
							paragonProfile += "\r\n\t#d+5% Increased Damage#k";
						} else {
							paragonProfile += "\r\n\t#r+5% Increased Damage (Unlocked at Paragon X)#k";
						}
						
						
						cm.sendPrevS(paragonProfile, 2);
					}
					break;
				case 107:
					cm.sendOk("Sorry, this section is not available yet.");
					cm.dispose();
					
					cm.forceCompleteQuest(400050);
					break;
				case 108:
<<<<<<< HEAD
					if (cm.getPlayer().getLevel() < 250 && cm.getPlayer().getReborns() < 1) {
                        cm.sendOk("Sorry, you need to be at least #rlevel 250#k and #rParagon Rank I#k to access the #dV: Matrix#k.");
                        cm.dispose();
                    } else if (cm.isQuestFinished(1460)) {
                        cm.sendOk("You have already finished the #dV: Matrix#k quest.");
                        cm.dispose();
                    } else {
                        cm.sendOk("Congratulations on your #rfifth job#k advancement!\r\nYou now have access to the powerful #dV: Matrix#k!");

                        cm.gainItem(2435902, 250);

                        cm.forceCompleteQuest(1460);
                        cm.forceCompleteQuest(1461);
                        cm.forceCompleteQuest(1462);
                        cm.forceCompleteQuest(1463);
                        cm.forceCompleteQuest(1464);
                        cm.forceCompleteQuest(1465);
                        cm.forceCompleteQuest(1466);
                        cm.forceCompleteQuest(1467);
                        cm.forceCompleteQuest(1468);
                        cm.forceCompleteQuest(1469);
                        cm.forceCompleteQuest(1470);
                        cm.forceCompleteQuest(1471);
                        cm.forceCompleteQuest(1472);
                        cm.forceCompleteQuest(1473);
                        cm.forceCompleteQuest(1474);
                        cm.forceCompleteQuest(1475);
                        cm.forceCompleteQuest(1476);
                        cm.forceCompleteQuest(1477);
                        cm.forceCompleteQuest(1478);
                        cm.forceCompleteQuest(1479);

                        cm.dispose();
                    }
=======
					if (cm.getPlayer().getLevel() < 250) {
						cm.sendOk("Sorry, you need to be atleast level #r250#k to access the #dV: Matrix#k.");
						cm.dispose();
					} else if (cm.isQuestFinished(1460)) {
						cm.sendOk("You have already finished the #dV: Matrix#k quest.");
						cm.dispose();
					} else {
						cm.sendOk("Congratulations on your #rfifth job#k advancement!\r\nYou now have access to the powerful #dV: Matrix#k!");
						
						cm.gainItem(2435902, 250);
						
						cm.forceCompleteQuest(1460);
						cm.forceCompleteQuest(1461);
						cm.forceCompleteQuest(1462);
						cm.forceCompleteQuest(1463);
						cm.forceCompleteQuest(1464);
						cm.forceCompleteQuest(1465);
						cm.forceCompleteQuest(1466);
						cm.forceCompleteQuest(1467);
						cm.forceCompleteQuest(1468);
						cm.forceCompleteQuest(1469);
						cm.forceCompleteQuest(1470);
						cm.forceCompleteQuest(1471);
						cm.forceCompleteQuest(1472);
						cm.forceCompleteQuest(1473);
						cm.forceCompleteQuest(1474);
						cm.forceCompleteQuest(1475);
						cm.forceCompleteQuest(1476);
						cm.forceCompleteQuest(1477);
						cm.forceCompleteQuest(1478);
						cm.forceCompleteQuest(1479);
						
						cm.dispose();
					}
>>>>>>> Effect-Handling-Recode
			}
			
		} else if(status == 2) {
			
			switch (selection) {
				
				// Map Warper
				case 200:
					var selStr = "Where exactly would you like to go?#d";
					for (var i = 0; i < townMaps.length; i++) {
						if (townMaps[i] != cm.getMapId()) {
							selStr += "\r\n#L" + i + "##m" + townMaps[i] + "# #l";
						}
					}
				
					cm.sendNextPrev(selStr);
					selectStatus = 1;
					break;	
				case 201:
					if (cm.getLevel() >= monsterMapReqLevel) {
						var selStr = "Where exactly would you like to go?#d";
						for (var i = 0; i < monsterMaps.length; i++) {
							if (monsterMaps[i] != cm.getMapId()) {
								selStr += "\r\n#L" + i + "##m" + monsterMaps[i] + "# #l";
							}
						}
					
						cm.sendNextPrev(selStr);
						selectStatus = 2;
					} else {
						cm.sendOk("Sorry, you do not have this feature unlocked.");
						cm.dispose();
					}
					break;
				case 202:
					if (cm.getLevel() >= bossMapReqLevel) {
						var selStr = "Where exactly would you like to go?#d";
						for (var i = 0; i < bossMaps.length; i++) {
							if (bossMaps[i] != cm.getMapId()) {
								selStr += "\r\n#L" + i + "##m" + bossMaps[i] + "# #l";
							}
						}
						
						cm.sendNextPrev(selStr);
						selectStatus = 3;
					} else {
						cm.sendOk("Sorry, you do not have this feature unlocked.");
						cm.dispose();
					}
					break;
				case 203:
					cm.warp(866127000, 0);
					cm.dispose();
					break;
				
				// Exchange System
				case 300:
					if (cm.getMeso() >= purchaseCostMeso) {
						cm.sendYesNo("Are you sure you want to exchange #r1,000,000,000 Mesos#k for a #dGolden Maple Leaf#k?");
					} else {
						cm.sendOk("Sorry, you do not have enough #rMesos#k.");
					}
					selectStatus = 4;
					break;
				case 301:
					if (cm.haveItem(exchangeItemMeso)) {
						cm.sendYesNo("Are you sure you want to exchange a #rGolden Maple Leaf#k for #d850,000,000 Mesos#k?");
					} else {
						cm.sendOk("Sorry, you do not have any #rGolden Maple Leaves#k.");
					}
					selectStatus = 5;
					break;
				case 302:
					if (cm.getPlayer().getCSPoints(2) >= purchaseCostNX) {
						cm.sendYesNo("Are you sure you want to exchange #r1,000,000 Maple Points (NX)#k for #dMaple Leaf Gold#k?");
					} else {
						cm.sendOk("Sorry, you do not have enough #rMaple Points (NX)#k.");
					}
					selectStatus = 6;
					break;
				case 303:
					if (cm.haveItem(exchangeItemNX)) {
						cm.sendYesNo("Are you sure you want to exchange #rMaple Leaf Gold#k for #d850,000 Maple Points (NX)#k?");
					} else {
						cm.sendOk("Sorry, you do not have any #rMaple Leaf Gold#k.");
					}
					selectStatus = 7;
					break;
			}
			
		} else if(status == 3) {
			
			switch (selectStatus) {
				
				// Map Warper
				case 1:
					if(cm.getPlayer().getMeso() >= townMapCost) {
						cm.sendYesNo("You don't have anything else to do here, huh?\r\nDo you really want to go to #b#m" + townMaps[selection] + "?");
						selectedMap = selection;
						mapForm = 1;
						break;
					} else {
						cm.sendOk("Sorry, you do not have enough #rMesos#k.");
						break;
					}
				case 2:
					if(cm.getPlayer().getCSPoints(2) >= monsterMapCost) {
						cm.sendYesNo("You don't have anything else to do here, huh?\r\nDo you really want to go to #b#m" + monsterMaps[selection] + "?");
						selectedMap = selection;
						mapForm = 2;
					} else {
						cm.sendOk("Sorry, you do not have enough #rMaple Points (NX)#k.");
					}
					break;
				case 3:
					if(cm.getPlayer().getCSPoints(2) >= bossMapCost) {
						cm.sendYesNo("You don't have anything else to do here, huh?\r\nDo you really want to go to #b#m" + bossMaps[selection] + "?");
						selectedMap = selection;
						mapForm = 3;
					} else {
						cm.sendOk("Sorry, you do not have enough #rMaple Points (NX)#k.");
					}
					break;
				
				// Trade System
				case 4:
					cm.gainItem(exchangeItemMeso, 1);
					cm.gainMeso(-purchaseCostMeso);
					cm.sendOk("Thank you for your transaction.");
					cm.dispose;
					break;
				case 5:
					cm.gainItem(exchangeItemMeso, -1);
					cm.gainMeso(sellCostMeso);
					cm.sendOk("Thank you for your transaction.");
					cm.dispose;
					break;
				case 6:
					cm.gainItem(exchangeItemNX, 1);
					cm.getPlayer().modifyCSPoints(2, -purchaseCostNX, true);
					cm.sendOk("Thank you for your transaction.");
					cm.dispose;
					break;
				case 7:
					cm.gainItem(exchangeItemNX, -1);
					cm.getPlayer().modifyCSPoints(2, sellCostNX, true);
					cm.sendOk("Thank you for your transaction.");
					cm.dispose;
					break;
			}
			
		} else if (status == 4) {
			
			// Map Warper
			switch (mapForm) {
				case 1:
					cm.gainMeso(-townMapCost);
					cm.warp(townMaps[selectedMap]);
					cm.dispose();
					break;
				case 2:
					cm.getPlayer().modifyCSPoints(2, -monsterMapCost, true);
					cm.warp(monsterMaps[selectedMap]);
					cm.dispose();
					break;
				case 3:
					cm.getPlayer().modifyCSPoints(2, -bossMapCost, true);
					cm.warp(bossMaps[selectedMap]);
					cm.dispose();
					break;
			}
			
		}
	}
}
