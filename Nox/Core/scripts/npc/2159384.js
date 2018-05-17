/*
 * EZ Jail
 * @author Mazen Massoud
 */

status = -1;

function action(mode, type, selection) {
	if (mode == 1) {
		status++;
	} else {
		if (status == 0) {
			cm.dispose();
		}
		status -= 1;
	}
	
	if (status == 0) {
		cm.sendOk("Heh, you're currently in jail, you should probably spend your time in here thinking about what you did wrong.\r\n\r\b\t#dRemember to vote every day to earn rewards!");
		cm.dispose();
	}
}