/*  
 *	Arkarium
 *	Arkarium Pre-Boss Arena NPC
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
			var sendString = "Welcome #h #, to the #bDimensional Schism#k.\r\n"
							+ "Before you is Arkarium, the right hand commander of the Black Mage. Will you challenge him?\r\n"
							+ "#r#L0#(Solo Boss Fight) #dTake on Arkarium by yourself!#l#k\r\n"
							+ "#r#L1#(Party Boss Fight) #dBring down Arkarium with friends!#l#k\r\n"
							+ "#r#L2#(Leave) #dRetreat to Free Market!#l#k\r\n";
			
			cm.sendNext(sendString);
		} else if (status == 1) {
			var nOption;
			if (mode != 0) {
				cm.dispose();
			}
			switch(selection) {
				case 0: // Solo
					if (cm.getPlayer().canAttemptBoss("ARKARIUM")) {
						if (cm.getPlayerCount(272030400) == 0) {
							cm.getPlayer().setBossAttempt("ARKARIUM");
							cm.resetMap(272030400);
							cm.warp(272030400, 0);
							//cm.spawnMonsterInMap(272030400, 8860000, -20, -180); 
							cm.dispose();
						} else {
							cm.sendOk("Sorry, looks like another expedition squad is currently fighting Lotus on this channel. You will be able to enter once they are finished or you can attempt the expedition on another channel.");
							cm.dispose();
						}
					} else {
						cm.sendOk("\tSorry, looks like you have fought Arkarium recently.\r\n\t#bPlease try again later.");
						cm.dispose();
					}
					break;
				case 1: // Party Fight
					if (cm.getPlayerCount(272030400) == 0) {
						if (cm.allMembersHere()) {
							if(cm.getParty().getLeader().getId() != cm.getPlayer().getId()) {
								cm.sendOk("The leader of your party must be the one to start the expedition.");
								cm.dispose();
							} else {
								if (cm.getPlayer().canPartyAttemptBoss("ARKARIUM")) {
									cm.getPlayer().setPartyBossAttempt("ARKARIUM");
									cm.resetMap(272030400);
									cm.warpParty(272030400); 
									//cm.spawnMonsterInMap(272030400, 8860000, -20, -180); 
									cm.dispose();
								} else {
									cm.sendOk("\tSorry, looks like you have fought Arkarium recently.\r\n\t#bPlease try again later.");
									cm.dispose();
								}
							}
						} else {
							cm.sendOk("All party members must be here in order to start the expedition.\r\n#rPlease make sure you are in a party before starting the expedition.");
							cm.dispose();
						}
					} else {
						cm.sendOk("Sorry, looks like another expedition squad is currently fighting Hilla on this channel. You will be able to enter once they are finished.");
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