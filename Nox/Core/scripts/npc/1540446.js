/*  
 *	Lotus
 *	Lotus Pre-Boss Arena NPC
 *	Rexion Development
 *   
 *	@author Mazen Massoud
 */

load("nashorn:mozilla_compat.js");
importPackage(Packages.tools.packet);

var status = -1;

function action(mode, type, selection) {
	if (mode != 1) {
		cm.dispose();
	} else {
		status++;
		if (status == 0) {
			var sendString = "Welcome #h #, to #bBlack Heaven#k.\r\n"
							+ "Lotus is one of the commanders of the Black Mage and is Orchid's twin-brother. Ahead lies Lotus and his mirrored images.\r\n"
							+ "#r#L0#(Solo Boss Fight) #dAttempt the Mirrored Lotus fight alone!#l#k\r\n"
							+ "#r#L1#(Party Boss Fight) #dTake down the Mirrored Lotus together!#l#k\r\n"
							+ "#r#L2#(Leave) #dRetreat to the Rexion Courtyard!#l#k\r\n";
			
			cm.sendNextPrevS(sendString, 2);
		} else if (status == 1) {
			var nOption;
			if (mode != 0) {
				cm.dispose();
			}
			switch(selection) {
				case 0: // Solo
					if (cm.getPlayerCount(350060279) == 0) {
						cm.resetMap(350060279);
						cm.warp(350060279, 0);
						cm.spawnModifiedMonsterInMap(350060279, 8240099, 115, -16, 15000000000); 
						cm.spawnModifiedMonsterInMap(350060279, 8240099, 115, -16, 5000000000); 
						//cm.spawnMonsterInMap(350060279, 8240099, 100, -16); 
						//cm.spawnMonsterInMap(350060279, 8240099, 506, -16);
						cm.dispose();
					} else {
						cm.sendOk("Sorry, looks like another expedition squad is currently fighting Lotus on this channel. You will be able to enter once they are finished or you can attempt the expedition on another channel.");
						cm.dispose();
					}
					break;
				case 1: // Party Fight
					if (cm.getPlayerCount(350060279) == 0) {
						if (cm.allMembersHere()) {
							if(cm.getParty().getLeader().getId() != cm.getPlayer().getId()) {
								cm.sendOk("The leader of your party must be the one to start the expedition.");
								cm.dispose();
							} else {
								cm.resetMap(350060279);
								cm.warpParty(350060279); 35000000000000
								cm.spawnModifiedMonsterInMap(350060279, 8240099, 115, -16, 15000000000); 
								cm.spawnModifiedMonsterInMap(350060279, 8240099, 115, -16, 5000000000); 
								//cm.spawnMonsterInMap(350060279, 8240099, 115, -16); 
								//cm.spawnMonsterInMap(350060279, 8240099, 506, -16);
								if (cm.getPlayerCount(350060279) >= 3) { // Spawn a third Lotus if 3+ party members.
									cm.spawnMonsterInMap(350060279, 8240099, 306, -16);
								}
								cm.dispose();
							}
						} else {
							cm.sendOk("All party members must be here in order to start the expedition.\r\n#rPlease make sure you are in a party before starting the expedition.");
							cm.dispose();
						}
					} else {
						cm.sendOk("Sorry, looks like another expedition squad is currently fighting Lotus on this channel. You will be able to enter once they are finished.");
						cm.dispose();
					}
					break;
				case 2: // Home
					cm.warp(101071300, 0);
					cm.dispose();
					break;
				default:
					cm.dispose();
					break;
			}
		}
	}
}