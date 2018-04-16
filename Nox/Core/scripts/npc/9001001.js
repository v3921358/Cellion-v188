/*
*	REXION Quick Access
*	Universal NPC
*
*	@author Mazen
*
*/

// Status Definitions
var status = 0;
var selectStatus  = 0;
var mapForm = 0;

// Map Warper Selection
var townMaps = Array(300000000, 680000000, 230000000, 910001000, 260000000, 541000000, 540000000, 211060010, 863100000, 105300000, 310000000, 211000000, 101072000, 101000000, 101050000, 130000000, 820000000, 223000000, 410000000, 141000000, 120040000, 209000000, 682000000, 310070000, 401000000, 100000000, 271010000, 251000000, 744000000, 551000000, 103000000, 222000000, 240000000, 104000000, 220000000, 150000000, 261000000, 807000000, 250000000, 800000000, 600000000, 120000000, 200000000, 800040000, 400000000, 102000000, 914040000, 200100000, 865000000, 801000000, 105000000, 866000000, 693000020, 270000000, 860000000, 273000000, 320000000);
var monsterMaps = Array(240070300,800020110,610040000,270030000,211060000, 240040500,551030100,271000300,211061000,211041100,240010501,330002019,270020000,910170000,390009999,610030010,863000100,910180100,272000100,682010200,541000300,241000200,327090040,102040200,240010700,241000210,241000220,270010100,910028600,706041000,706041005,273050000,231040400,401050000,541020400, 224000015, 273040100, 272000300, 860000032, 240093100, 211060830, 106030700, 120040300, 551030000, 105200900);
var bossMaps = Array(211070000, 262000000, 105100100, 240050000, 240040700, 105100100, 350060300, 271040000, 211041700, 240050400);

var monsterMapReqLevel = 30;
var bossMapReqLevel = 90;

var lockedText1 = "";
var lockedText2 = "";

var fifthJobText = "";
var eventText = "";

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
			
			if (cm.getPlayer().getLevel() >= 200 && !cm.isQuestFinished(1460)) {
				fifthJobText += "#L99##fs13##dAdvance to Fifth Job & Obtain V:Matrix!#r#fs11##l\r\n\r\n" 
			}
			
			cm.sendNextPrevS("#rALPHA TESTER DEBUG MENU#b\r\n\r\n" 
			
						+ "You've killed " 
						+ cm.getPlayer().getInfoQuest(13337) == "" ? 0 : cm.getPlayer().getInfoQuest(13337) 
						+ "/10000 mobs required to unluck the secret outfit upon release.\r\n\r\n"
						
						+ fifthJobText
						
						+ "#L100#Warp to a location.#l\r\n" 
						+ "#L101#Open general store.#l\r\n"
						+ "#L105#Drop CASH/ETC Items#l\r\n"
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
						+ "#b#L200#Towns#l\r\n"
						+ "#r#L201#Monster Zones" + lockedText1 + "#l\r\n"
						+ "#L202#Boss Arenas" + lockedText2 + "#l\r\n\r\n"
						+ "#d#L203#Free - Return to Henesys", 2);
					break;
				case 101:
					cm.dispose();
					cm.openShop(1500028);
					break;
				case 105:
					cm.dispose();
					cm.openNpc(9010017); // Cash Drop NPC
					break;
				case 99:
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
					break;pc(9000409);
					break;
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
					cm.warp(100000000, 0);
					cm.dispose();
					break;
			}
			
		} else if(status == 3) {
			
			switch (selectStatus) {
				
				// Map Warper
				case 1:
						cm.sendYesNo("You don't have anything else to do here, huh?\r\nDo you really want to go to #b#m" + townMaps[selection] + "?");
						selectedMap = selection;
						mapForm = 1;
						break;
				case 2:
						cm.sendYesNo("You don't have anything else to do here, huh?\r\nDo you really want to go to #b#m" + monsterMaps[selection] + "?");
						selectedMap = selection;
						mapForm = 2;
					break;
				case 3:
						cm.sendYesNo("You don't have anything else to do here, huh?\r\nDo you really want to go to #b#m" + bossMaps[selection] + "?");
						selectedMap = selection;
						mapForm = 3;
					break;
			}
			
		} else if (status == 4) {
			
			// Map Warper
			switch (mapForm) {
				case 1:
					cm.warp(townMaps[selectedMap]);
					cm.dispose();
					break;
				case 2:
					cm.warp(monsterMaps[selectedMap]);
					cm.dispose();
					break;
				case 3:
					cm.warp(bossMaps[selectedMap]);
					cm.dispose();
					break;
			}
			
		}
	}
}
