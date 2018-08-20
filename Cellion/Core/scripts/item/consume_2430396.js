/*
 * Mimir's Well water for Cannon shooters
 */

function action(mode, type, selection) {
    if (cm.isDateBefore(17, 7, 2012)) {
	var job = cm.getJob();
	if (job >= 530 && job <= 532) {
	    var level = cm.getPlayerStat("LVL");
	    if (level < 100) {
		var rate = 5;
		if (level < 50) {
		    rate = 10;
		}
		cm.gainItem(2430396, -1);
		cm.gainExp_Percentage(rate);
	    } else {
		cm.playerMessage("You can't use this on a character of level above 100.");
	    }
	} else {
	    cm.playerMessage("This can only be consumed by a Cannoner character.");
	}
    } else {
	cm.playerMessage("It is past 17 July 2012, and you are unable to use it.");
    }
    cm.dispose();
}