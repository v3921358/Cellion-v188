/*
 * Testing NPC
 */

status = -1;

var nQuantity;
var nCost;
var nChoice;

function action(mode, type, selection){
	if (mode == 1) {
		status++;
	} else {
		if (status == 0){
			cm.sendNextS("Enjoy your adventure!", 5);
            cm.dispose();
		}
        status -= 1;
	}
	
	if (!cm.getPlayer().isGM()) {
		cm.dispose();
		return;
	}
	
	if (status == 0) {
		text = " : \r\n"
		;
		cm.sendSimple(text + cm.getInventoryItems("EQUIP"));
	} 
}
