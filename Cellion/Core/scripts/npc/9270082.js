/*  
 *	NPC (9270082)
 *  Tutorial: Broken Wooden Barrel
 *	REXION
 *
 *	@author Mazen
 */
 
var status = -1;

// Item given to check which state of the tutorial the player is on.
var tutorialItemCheck = 4310203;

function giveStarterPack() {
	cm.gainItem(2000004, 100); // Elixers
	cm.gainItem(2000005, 50); // Power Elixers
	cm.gainItemPeriod(5211111, 1, 1, true); // 1.5x EXP Coupon (item id, amount, time, is hours?)
}

function action(mode, type, selection) {
	if (mode != 1) {
		cm.dispose();
	} else {
		status++;
		
		if (cm.haveItem(tutorialItemCheck)) {
			if (status == 0) {
				cm.sendNextPrevS("Hey neat! Looks like there's some #bSupplies#k in here.", 2);
			} else if (status == 1) {
				cm.sendNextPrevS("Wow, look at that. Found #b100 Elixers#k, #b50 Power Elixers#k, and even a #bOne Hour 150% Experience Boost#k!", 2);
				cm.gainItem(tutorialItemCheck, -1);
				giveStarterPack();
				cm.dispose();
			}
		} else {
			cm.sendOk("It appears to be empty.");
			cm.dispose();
		}
		
	}
}