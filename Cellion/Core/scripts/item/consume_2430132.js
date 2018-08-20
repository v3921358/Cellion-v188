/*
 * Resistance Secret box
 */

function action(mode, type, selection) {
    if (cm.getJob() > 3000) {
	cm.gainItem(2430132,-1);
	    
	if (cm.getJob() <= 3212) { // Battlemage
	    cm.playerMessage("Password removed. You recieved a Revolution Staff that can be used and equipped by Battle Mage.");
	    cm.gainItem(1382101,1);
	} else if (cm.getJob() <= 3312) {
	    cm.playerMessage("Password removed. You recieved a Revolution Crossbow that can be used and equipped by Wild Hunter.");
	    cm.gainItem(1462093,1);
	} else {
	    cm.playerMessage("Password removed. You recieved a Revolution Gun that can be used and equipped by Mechanic.");
	    cm.gainItem(1492080,1);
	}
    } else {
	cm.playerMessage("This item can only be used by the Resistance member.");
    }
    cm.dispose();
}