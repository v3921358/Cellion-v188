/*  
 *	Root Abyss
 *	Crimson Queen Pre-Boss Arena NPC
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
							+ "The Crimson Queen is one of the four main bosses of Root Abyss, working for Damien to restrict the World Tree's power.\r\n"
							+ "\r\n\r\n \t\t\t\t\t\t\t\t\t\t #v03994116# \r\n#r#L0#(Solo Boss Fight) #dFace off against the Crimson Queen alone!#l#k\r\n"
							+ "#r#L1#(Party Boss Fight) #dFight the Crimson Queen with your squad!#l#k\r\n"
							+ "\r\n\r\n\r\n \t\t\t\t\t\t\t\t\t\t #v03994442# \r\n#r#L2#(Solo Boss Fight) #dFace the Chaos Crimson Queen alone!#l#k\r\n"
							+ "#r#L3#(Party Boss Fight) #dFight the Chaos Crimson Queen together!#l#k\r\n\r\n"
							+ "\r\n#r#L4#(Leave) #dRetreat to the Rexion Courtyard!#l#k\r\n";
			
			cm.sendSimple(sendString);
		} else if (status == 1) {
			var nOption;
			if (mode != 0) {
				cm.dispose();
			}
			switch(selection) {
				case 0: // Solo
					if (cm.getPlayerCount(105200310) == 0) {
						cm.resetMap(105200310);
						cm.warp(105200310, 0);
						cm.dispose();
					} else {
						cm.sendOk("Sorry, looks like another expedition squad is currently fighting the Crimson Queen on this channel. You will be able to enter once they are finished or you can attempt the expedition on another channel.");
						cm.dispose();
					}
					break;
				case 1: // Party Fight
					if (cm.getPlayerCount(105200310) == 0) {
						if (cm.allMembersHere()) {
							if(cm.getParty().getLeader().getId() != cm.getPlayer().getId()) {
								cm.sendOk("The leader of your party must be the one to start the expedition.");
								cm.dispose();
							} else {
								cm.resetMap(105200310);
								cm.warpParty(105200310);
								cm.dispose();
							}
						} else {
							cm.sendOk("All party members must be here in order to start the expedition.\r\n#rPlease make sure you are in a party before starting the expedition.");
							cm.dispose();
						}
					} else {
						cm.sendOk("Sorry, looks like another expedition squad is currently fighting the Crimson Queen on this channel. You will be able to enter once they are finished.");
						cm.dispose();
					}
					break;
				case 2: // Chaos - Solo
					if (cm.getPlayerCount(105200710) == 0) {
						cm.resetMap(105200710);
						cm.warp(105200710, 0);
						cm.dispose();
					} else {
						cm.sendOk("Sorry, looks like another expedition squad is currently fighting the Crimson Queen on this channel. You will be able to enter once they are finished or you can attempt the expedition on another channel.");
						cm.dispose();
					}
					break;
				case 3: // Chaos - Party Fight
					if (cm.getPlayerCount(105200710) == 0) {
						if (cm.allMembersHere()) {
							if(cm.getParty().getLeader().getId() != cm.getPlayer().getId()) {
								cm.sendOk("The leader of your party must be the one to start the expedition.");
								cm.dispose();
							} else {
								cm.resetMap(105200710);
								cm.warpParty(105200710);
								cm.dispose();
							}
						} else {
							cm.sendOk("All party members must be here in order to start the expedition.\r\n#rPlease make sure you are in a party before starting the expedition.");
							cm.dispose();
						}
					} else {
						cm.sendOk("Sorry, looks like another expedition squad is currently fighting the Crimson Queen on this channel. You will be able to enter once they are finished.");
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