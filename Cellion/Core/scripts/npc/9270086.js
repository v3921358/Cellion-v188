/*  
 *	NPC (9270087)
 *  Tutorial: Black Bark
 *	REXION
 *
 *	@author Mazen
 */
 
var status = -1;

function action(mode, type, selection) {
	if (mode != 1) {
		cm.dispose();
	} else {
		status++;
		if (status == 0) {
			cm.sendNext("You're alive! That's amazing considering the crash, your friend over there should get some flying lessons. ");
		} else if (status == 1) {
			cm.sendNextPrev("Anyways, she's probably waiting for you. Mind if I have what's left of the #rSpaceship#k by the way?");
			cm.dispose();
		} else {
			cm.dispose();
		}
	}
}