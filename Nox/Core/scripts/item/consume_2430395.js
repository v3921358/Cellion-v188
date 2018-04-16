/*
 * Mighty Banana
 */

function action(mode, type, selection) {
    if (cm.isDateBefore(15, 5, 2012)) {
	var level = cm.getPlayerStat("LVL");
	var rate = 1;
	if (level < 50) {
	    rate = 10;
	} else if (level < 70) {
	    rate = 7;
	} else if (level < 100) {
	    rate = 5;
	} else if (level < 150) {
	    rate = 2;
	}
	cm.gainItem(2430395, -1);
	cm.gainExp_Percentage(rate);
    } else {
	cm.playerMessage("It is past 15 May 2012, and you are unable to use it.");
    }
    cm.dispose();
}