/*  
 *	NPC (9270087)
 *  Tutorial: Crashed Ship
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
			cm.sendNextPrevS("That was crazy! The #rSpaceship#k crashed!", 2);
		} else if (status == 1) {
			cm.sendNextPrevS("Huh... I guess #bFiona#k isn't very good at flying.", 2);
			cm.dispose();
		} else {
			cm.dispose();
		}
	}
}