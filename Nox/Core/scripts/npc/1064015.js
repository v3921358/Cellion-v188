/*  
 *	Root Abyss
 *	Vellum Pre-Boss Arena NPC
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
			var sendString = "Welcome #h #, to the #bColossal Root#k.\r\n"
							+ "Vellum is the final boss of Root Abyss, working for Damien to restrict the World Tree's power.\r\n"
							+ "\r\n\r\n \t\t\t\t\t\t\t\t\t\t #v03994116# \r\n#r#L0#(Solo Boss Fight) #dFight against Vellum alone!#l#k\r\n"
							+ "#r#L1#(Party Boss Fight) #dDefaut Vellum with your friends!#l#k\r\n"
							+ "\r\n\r\n\r\n \t\t\t\t\t\t\t\t\t\t #v03994442# \r\n#r#L2#(Solo Boss Fight) #dTest your fate against Chaos Vellum!#l#k\r\n"
							+ "#r#L3#(Party Boss Fight) #dConquer Chaos Vellum together!#l#k\r\n\r\n"
							+ "\r\n#r#L4#(Leave) #dRetreat to the Rexion Courtyard!#l#k\r\n";
			
			cm.sendSimple(sendString);
		} else if (status == 1) {
			var nOption;
			if (mode != 0) {
				cm.dispose();
			}
			switch(selection) {
				case 0: // Solo
					if (cm.getPlayerCount(105200410) == 0) {
						cm.resetMap(105200410);
						cm.warp(105200410, 0);
						cm.killAllMonsters(105200410);
						cm.dispose();
					} else {
						cm.sendOk("Sorry, looks like another expedition squad is currently fighting Vellum on this channel. You will be able to enter once they are finished or you can attempt the expedition on another channel.");
						cm.dispose();
					}
					break;
				case 1: // Party Fight
					if (cm.getPlayerCount(105200410) == 0) {
						if (cm.allMembersHere()) {
							if(cm.getParty().getLeader().getId() != cm.getPlayer().getId()) {
								cm.sendOk("The leader of your party must be the one to start the expedition.");
								cm.dispose();
							} else {
								cm.resetMap(105200410);
								cm.warpParty(105200410);
								cm.killAllMonsters(105200410);
								cm.dispose();
							}
						} else {
							cm.sendOk("All party members must be here in order to start the expedition.\r\n#rPlease make sure you are in a party before starting the expedition.");
							cm.dispose();
						}
					} else {
						cm.sendOk("Sorry, looks like another expedition squad is currently fighting Vellum on this channel. You will be able to enter once they are finished.");
						cm.dispose();
					}
					break;
				case 2: // Chaos - Solo
					if (cm.getPlayerCount(105200810) == 0) {
						cm.resetMap(105200810);
						cm.warp(105200810, 0);
						cm.killAllMonsters(105200810);
						cm.spawnMonsterInMap(105200810, 8930000, -50, 443); 
						cm.spawnMonsterInMap(105200810, 8930001, 200, 443);
						cm.spawnMonsterInMap(105200810, 8930001, -250, 443);
						cm.dispose();
					} else {
						cm.sendOk("Sorry, looks like another expedition squad is currently fighting Vellum on this channel. You will be able to enter once they are finished or you can attempt the expedition on another channel.");
						cm.dispose();
					}
					break;
				case 3: // Chaos - Party Fight
					if (cm.getPlayerCount(105200810) == 0) {
						if (cm.allMembersHere()) {
							if(cm.getParty().getLeader().getId() != cm.getPlayer().getId()) {
								cm.sendOk("The leader of your party must be the one to start the expedition.");
								cm.dispose();
							} else {
								cm.resetMap(105200810);
								cm.warpParty(105200810);
								cm.killAllMonsters(105200810);
								cm.spawnMonsterInMap(105200810, 8930000, -50, 443); 
								cm.spawnMonsterInMap(105200810, 8930001, 200, 443);
								cm.spawnMonsterInMap(105200810, 8930001, -250, 443);
								cm.dispose();
							}
						} else {
							cm.sendOk("All party members must be here in order to start the expedition.\r\n#rPlease make sure you are in a party before starting the expedition.");
							cm.dispose();
						}
					} else {
						cm.sendOk("Sorry, looks like another expedition squad is currently fighting Vellum on this channel. You will be able to enter once they are finished.");
						cm.dispose();
					}
					break;
				case 4: // Home
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