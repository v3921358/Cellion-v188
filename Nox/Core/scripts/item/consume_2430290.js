/*
 * Old Envelope
 */
function action(mode, type, selection) {
    if (cm.canHold(4350000)) {
	cm.gainItem(2430290,-1);
	if (Math.round((Math.random() * 100)) < 30) {
	    cm.sendNext("Spiegelman told you not to open it! uh oh no.");
	    cm.gainItem(4350000 + Math.round(Math.random() * 2),1);
	} else {
	    cm.sendNext("Nothing is inside this old envelope.");
	}
    } else{
	cm.playerMessage("Make space in your ETC inventory.")
    }
    cm.dispose();
}