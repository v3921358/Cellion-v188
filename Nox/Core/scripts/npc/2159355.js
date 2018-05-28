/*  
	Magnus
	Magnus Pre-Boss Arena NPC
    
	@author Mazen Massoud
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
			var sendString = "Welcome #h #, to the #bTyrant's Castle#k!\r\nMagnus is a former commander of the Black Mage, exiled from Pantheon for abusing his powers. What would you like to do?\r\n"
							+ "#r#L0#(Solo Boss Fight) #dTest your fate against Magnus (Hard) alone!#k\r\n"
							+ "#r#L1#(Party Boss Fight) #dFace Magnus (Hard) with your squad!#k\r\n"
							+ "#r#L2#(Leave) #dRetreat to Free Market!#k\r\n";
			
			cm.sendSimple(sendString);
		} else if (status == 1) {
			var nOption;
			if (mode != 0) {
				cm.dispose();
			}
			switch(selection) {
				case 0: // Solo
					if (cm.getPlayerCount(401060200) == 0) {
						if (cm.getPlayer().canAttemptBoss("MAGNUS")) {
							cm.getPlayer().setBossAttempt("MAGNUS");
							cm.resetMap(401060200);
							cm.warp(401060200, 0);
							cm.spawnMonsterInMap(401060200, 8880000, 2009, -1347); // Map, Mob, Pos X, Pos Y.
							cm.dispose();
						} else {
							cm.sendOk("\tSorry, looks like you have fought Magnus recently.\r\n\t#bPlease try again later.");
							cm.dispose();
						}
					} else {
						cm.sendOk("Sorry, looks like another expedition squad is currently fighting Magnus on this channel. You will be able to enter once they are finished.");
						cm.dispose();
					}
					break;
				case 1: // Party Fight
					if (cm.getPlayerCount(401060200) == 0) {
						if (cm.allMembersHere()) {
							if(cm.getParty().getLeader().getId() != cm.getPlayer().getId()) {
								cm.sendOk("The leader of your party must be the one to start the expedition.");
								cm.dispose();
							} else {
								if (cm.getPlayer().canPartyAttemptBoss("MAGNUS")) {
									cm.getPlayer().setPartyBossAttempt("MAGNUS");
									cm.resetMap(401060200);
									cm.warpParty(401060200);
									cm.spawnMonsterInMap(401060200, 8880000, 2009, -1347); // Map, Mob, Pos X, Pos Y.
									cm.dispose();
								} else {
									cm.sendOk("\tSorry, looks like you have fought Magnus recently.\r\n\t#bPlease try again later.");
									cm.dispose();
								}
							}
						} else {
							cm.sendOk("All party members must be here in order to start the expedition.\r\n#rPlease make sure you are in a party before starting the expedition.");
							cm.dispose();
						}
					} else {
						cm.sendOk("Sorry, looks like another expedition squad is currently fighting Magnus on this channel. You will be able to enter once they are finished.");
						cm.dispose();
					}
					break;
				case 2: // Home
					cm.warp(910000000, 0);
					cm.dispose();
					break;
				default:
					cm.dispose();
					break;
			}
		}
	}
}