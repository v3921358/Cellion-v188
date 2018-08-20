/*
 * Air bubble for Kenta PQ
 */

function action(mode, type, selection) {
    var eim = cm.getEventInstance();
    if (eim != null) {
	var bubble = eim.getPropertyEx("AirBubble");
	if (bubble < 20) {
	    bubble ++;
	    eim.setPropertyEx("AirBubble", bubble);
	    
	    if (bubble < 20) {
		cm.broadcastEarnTitleMsg("Air bubble "+bubble+" has been obtained.");
	    } else {
		cm.broadcastEarnTitleMsg("Air bubble 20 has been obtained.");
		eim.broadcastPlayerMsg(5, "All Air Bubbles have been found! Proceed to the next stage.");
	    }
	}
    }
    if (cm.haveItem(2430364)) { // Just in case it is used in the inv
	cm.gainItem(2430364, -1); 
    }
    cm.dispose();
}