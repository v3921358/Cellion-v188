/*  
	Adobis
	Zakum Pre-Boss Arena NPC
    
	@author Mazen
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
			var sendString = "\r\nWelcome #h #, to #bThe Door to Zakum#k!\r\nAhead, you'll find Zakum, a corrupted spirit sealed in a tree deep in the mines of El Nath. What would you like to do?\r\n"
							+ "#r#L0#(Solo Boss Fight) #dFight the mighty Zakum!#k\r\n"
							//+ "#r#L1#(Party Boss Fight) #dOvercome Papulatus' rule with your squad!#k\r\n" // Zakum can d/c multiple people, keep it as a solo fight for now.
							+ "#r#L2#(Leave) #dRetreat to the Rexion Courtyard!#k\r\n";
			
			cm.sendNextPrevS(sendString, 2);
		} else if (status == 1) {
			var nOption;
			if (mode != 0) {
				cm.dispose();
			}
			switch(selection) {
				case 0: // Solo
					if (cm.getPlayerCount(280030100) == 0) {
						cm.resetMap(280030100);
						cm.warp(280030100, 0);
						cm.dispose();
					} else {
						cm.sendOk("Sorry, looks like another expedition squad is currently fighting Papulatus on this channel. You will be able to enter once they are finished or you can attempt the expedition on another channel.");
						cm.dispose();
					}
					break;
				case 1: // Party Fight
					if (cm.getPlayerCount(280030100) == 0) {
						if (cm.allMembersHere()) {
							if(cm.getParty().getLeader().getId() != cm.getPlayer().getId()) {
								cm.sendOk("The leader of your party must be the one to start the expedition.");
								cm.dispose();
							} else {
								cm.resetMap(280030100);
								cm.warpParty(280030100);
								cm.dispose();
							}
						} else {
							cm.sendOk("All party members must be here in order to start the expedition.\r\n#rPlease make sure you are in a party before starting the expedition.");
							cm.dispose();
						}
					} else {
						cm.sendOk("Sorry, looks like another expedition squad is currently fighting Papulatus on this channel. You will be able to enter once they are finished.");
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