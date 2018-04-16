/* 
 * NPC : Gion (Evan mount quest, Lv 80)
 */

function action(mode, type, selection) {
    if (cm.getMonsterCount(925110000) == 0) {

	if (!cm.canHold(4032497)) {
	    cm.sendNext("You're going to have to drop some of those things you're holding in your hands if you want to rescue me. You look like you've got too much baggage.");
	} else {
	    if (!cm.haveItem(4032497)) {
		cm.gainItem(4032497, 1);
	    }
	    cm.sendNext("Thank you for rescuing me. Lets hurry and get back to the team.");
	}
    } else {
	cm.sendOk("Please defeat all monsters!");
    }
    cm.dispose();
}